package cyborgcabbage.spacepunk.block;

import cyborgcabbage.spacepunk.Spacepunk;
import cyborgcabbage.spacepunk.util.PlanetProperties;
import net.minecraft.block.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class OxygenBlock extends Block {
    public static final IntProperty PRESSURE = IntProperty.of("pressure", 1, 8);
    private static final int TICKRATE = 5;
    private static final int DECAY_CHANCE = 10;

    public OxygenBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(PRESSURE, 8));
    }

    public static int getPressure(World world, BlockPos pos) {
        if(PlanetProperties.hasAtmosphere(world.getRegistryKey().getValue())) return 8;
        BlockState s = world.getBlockState(pos);
        if(s.isAir()){
            return getPressure(s);
        }else{
            //Check feet
            BlockState s2 = world.getBlockState(pos.offset(Direction.DOWN));
            if(s2.isAir()){
                return getPressure(s2);
            }
        }
        return 0;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.empty();
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(PRESSURE);
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if(random.nextBoolean()) {
            decay(state, world, pos, random);
        }else {
            spread(state, world, pos);
        }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        spread(state, world, pos);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if(!world.isClient)
            world.createAndScheduleBlockTick(pos, this, TICKRATE);
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if(!world.isClient)
            world.createAndScheduleBlockTick(pos, this, TICKRATE);
    }

    private void spread(BlockState state, ServerWorld world, BlockPos pos){
        ArrayList<Gas> list = new ArrayList<>();
        Gas thisGas = new Gas(pos, getPressure(state));
        if(world.getTopY(Heightmap.Type.MOTION_BLOCKING, pos.getX(), pos.getZ()) <= pos.getY()){
            setPressure(world, pos, 0);
            return;
        }
        for(Direction d : Direction.values()){
            BlockPos p = pos.offset(d);
            BlockState s = world.getBlockState(p);
            if(s.isAir()){
                list.add(new Gas(p, getPressure(s)));
            }
        }
        Collections.shuffle(list);
        list.add(thisGas);
        int total = list.stream().mapToInt(Gas::pressure).sum();
        int divided = total/list.size();
        if(divided < thisGas.pressure){
            int diff = total-divided*list.size();
            for (Gas gas : list) {
                int extra = diff > 0 ? 1 : 0;
                if(divided+extra != gas.pressure)
                    setPressure(world, gas.pos, divided+extra);
                diff--;
            }
        }
    }

    private void decay(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        int pressure = getPressure(state);
        if(pressure > 0) {
            setPressure(world, pos, pressure - 1);
        }
    }

    private static void setPressure(ServerWorld world, BlockPos pos, int pressure) {
        if(pressure == 0){
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
        }else{
            world.setBlockState(pos, Spacepunk.OXYGEN.getDefaultState().with(PRESSURE, pressure));
        }
    }

    private static int getPressure(BlockState state) {
        if(state.isOf(Spacepunk.OXYGEN)){
            return state.get(PRESSURE);
        }else{
            return 0;
        }
    }

    public static void depositOxygen(ServerWorld world, BlockPos origin, int amount){
        HashSet<BlockPos> blocks = new HashSet<>();
        blocks.add(origin);
        int volume = 0;
        for (int i = 0; i < 8; i++) {
            HashSet<BlockPos> newBlocks = new HashSet<>();
            for (BlockPos blockPos : blocks) {
                for (Direction value : Direction.values()) {
                    var offset = blockPos.offset(value);
                    var state = world.getBlockState(offset);
                    if(state.isAir()) {
                        newBlocks.add(offset);
                        volume += 8-getPressure(state);
                    }
                }
            }
            var sizeBefore = blocks.size();
            blocks.addAll(newBlocks);
            if(sizeBefore == blocks.size()) break;
            if(volume > amount) break;
        }
        for (BlockPos block : blocks) {
            var state = world.getBlockState(block);
            if(state.isAir()){
                int currentPressure = getPressure(state);
                int room = 8-currentPressure;
                if(room > amount){
                    setPressure(world, block, currentPressure+amount);
                    amount = 0;
                }else{
                    setPressure(world, block, 8);
                    amount -= room;
                }
            }
            if(amount <= 0) break;
        }
    }
    record Gas(BlockPos pos, int pressure){}
}
