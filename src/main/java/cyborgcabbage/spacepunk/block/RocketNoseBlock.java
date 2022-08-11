package cyborgcabbage.spacepunk.block;

import cyborgcabbage.spacepunk.Spacepunk;
import cyborgcabbage.spacepunk.entity.RocketEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.Wearable;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;

public class RocketNoseBlock extends Block implements Wearable {
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
                        rocketEntity.refreshPositionAndAngles(d3.getX() + 0.5, d3.getY(), d3.getZ() + 0.5, 0.0f, 0.0f);
                        world.spawnEntity(rocketEntity);
                        if (!world.isClient) world.playSound(null, d2, SoundEvents.BLOCK_ANVIL_USE, SoundCategory.BLOCKS, 1f, 1);
                    }else{
                        Spacepunk.LOGGER.error("RocketEntity spawning failed at "+d3+", RocketEntity was null");
                    }
                }
            }
        }
    }
}
