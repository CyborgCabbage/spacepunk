package cyborgcabbage.spacepunk.block;

import cyborgcabbage.spacepunk.Spacepunk;
import cyborgcabbage.spacepunk.entity.RocketEntity;
import net.minecraft.block.*;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.Wearable;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import vazkii.patchouli.api.PatchouliAPI;

public class RocketNoseBlock extends Block implements Wearable {
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;

    private static final VoxelShape NOSE_SHAPE = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 12.0, 14.0);
    public RocketNoseBlock(Settings settings) {
        super(settings);
        setDefaultState(stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
        return NOSE_SHAPE;
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        BlockPos d0 = pos.add(0,0,0);
        BlockPos d1 = pos.add(0,-1,0);
        BlockPos d2 = pos.add(0,-2,0);
        BlockPos d3 = pos.add(0,-3,0);
        if(world.getBlockState(d1).isOf(Blocks.COPPER_BLOCK)){
            if(world.getBlockState(d2).isOf(Blocks.COPPER_BLOCK)){
                if(world.getBlockState(d3).isOf(Blocks.BLAST_FURNACE)){
                    RocketEntity rocketEntity = Spacepunk.ROCKET_ENTITY.create(world);
                    if(rocketEntity != null) {
                        world.setBlockState(d0, Blocks.AIR.getDefaultState(), Block.NOTIFY_LISTENERS);
                        world.setBlockState(d1, Blocks.AIR.getDefaultState(), Block.NOTIFY_LISTENERS);
                        world.setBlockState(d2, Blocks.AIR.getDefaultState(), Block.NOTIFY_LISTENERS);
                        world.setBlockState(d3, Blocks.AIR.getDefaultState(), Block.NOTIFY_LISTENERS);
                        world.syncWorldEvent(WorldEvents.BLOCK_BROKEN, d0, Block.getRawIdFromState(world.getBlockState(d0)));
                        world.syncWorldEvent(WorldEvents.BLOCK_BROKEN, d1, Block.getRawIdFromState(world.getBlockState(d1)));
                        world.syncWorldEvent(WorldEvents.BLOCK_BROKEN, d2, Block.getRawIdFromState(world.getBlockState(d2)));
                        world.syncWorldEvent(WorldEvents.BLOCK_BROKEN, d3, Block.getRawIdFromState(world.getBlockState(d3)));
                        world.updateNeighbors(d0, Blocks.AIR);
                        world.updateNeighbors(d1, Blocks.AIR);
                        world.updateNeighbors(d2, Blocks.AIR);
                        world.updateNeighbors(d3, Blocks.AIR);
                        rocketEntity.refreshPositionAndAngles(d3.getX() + 0.5, d3.getY(), d3.getZ() + 0.5, state.get(FACING).asRotation(), 0.0f);
                        world.spawnEntity(rocketEntity);
                        if (!world.isClient) world.playSound(null, d2, SoundEvents.BLOCK_ANVIL_USE, SoundCategory.BLOCKS, 1f, 1);
                        if (PatchouliAPI.get().getCurrentMultiblock() == Spacepunk.ROCKET_MULTIBLOCK){
                            PatchouliAPI.get().clearMultiblock();
                        }
                    }else{
                        Spacepunk.LOGGER.error("RocketEntity spawning failed at "+d3+", RocketEntity was null");
                    }
                }
            }
        }
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}
