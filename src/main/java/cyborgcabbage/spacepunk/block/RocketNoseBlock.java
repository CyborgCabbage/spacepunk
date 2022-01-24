package cyborgcabbage.spacepunk.block;

import cyborgcabbage.spacepunk.Spacepunk;
import cyborgcabbage.spacepunk.entity.RocketEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;

public class RocketNoseBlock extends Block {
    private static final VoxelShape NOSE_SHAPE = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 12.0, 14.0);
    public RocketNoseBlock(Settings settings) {
        super(settings);
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
                    world.setBlockState(d0, Blocks.AIR.getDefaultState(), Block.NOTIFY_LISTENERS);
                    world.setBlockState(d1, Blocks.AIR.getDefaultState(), Block.NOTIFY_LISTENERS);
                    world.setBlockState(d2, Blocks.AIR.getDefaultState(), Block.NOTIFY_LISTENERS);
                    world.setBlockState(d3, Blocks.AIR.getDefaultState(), Block.NOTIFY_LISTENERS);
                    world.syncWorldEvent(WorldEvents.BLOCK_BROKEN, d0, Block.getRawIdFromState(world.getBlockState(d0)));
                    world.syncWorldEvent(WorldEvents.BLOCK_BROKEN, d1, Block.getRawIdFromState(world.getBlockState(d0)));
                    world.syncWorldEvent(WorldEvents.BLOCK_BROKEN, d2, Block.getRawIdFromState(world.getBlockState(d0)));
                    world.syncWorldEvent(WorldEvents.BLOCK_BROKEN, d3, Block.getRawIdFromState(world.getBlockState(d0)));
                    world.updateNeighbors(d0, Blocks.AIR);
                    world.updateNeighbors(d1, Blocks.AIR);
                    world.updateNeighbors(d2, Blocks.AIR);
                    world.updateNeighbors(d3, Blocks.AIR);
                    RocketEntity rocketEntity = Spacepunk.ROCKET_ENTITY_TYPE.create(world);
                    rocketEntity.refreshPositionAndAngles(d3.getX() + 0.5, d3.getY(), d3.getZ() + 0.5, 0.0f, 0.0f);
                    world.spawnEntity(rocketEntity);
                }
            }
        }
    }
}
