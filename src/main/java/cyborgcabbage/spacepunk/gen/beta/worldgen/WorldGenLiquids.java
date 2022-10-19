package cyborgcabbage.spacepunk.gen.beta.worldgen;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;

import java.util.Random;

public class WorldGenLiquids extends WorldGenerator {
	private final BlockState liquidBlockId;

	public WorldGenLiquids(BlockState i1) {
		this.liquidBlockId = i1;
	}

	public boolean generate(StructureWorldAccess world, Random random, int i3, int i4, int i5) {
		if(!world.getBlockState(new BlockPos(i3, i4 + 1, i5)).isOf(Blocks.STONE)) {
			return false;
		} else if(!world.getBlockState(new BlockPos(i3, i4 - 1, i5)).isOf(Blocks.STONE)) {
			return false;
		} else if(!world.getBlockState(new BlockPos(i3, i4, i5)).isAir() && !world.getBlockState(new BlockPos(i3, i4, i5)).isOf(Blocks.STONE)) {
			return false;
		} else {
			int i6 = 0;
			if(world.getBlockState(new BlockPos(i3 - 1, i4, i5)).isOf(Blocks.STONE)) {
				++i6;
			}

			if(world.getBlockState(new BlockPos(i3 + 1, i4, i5)).isOf(Blocks.STONE)) {
				++i6;
			}

			if(world.getBlockState(new BlockPos(i3, i4, i5 - 1)).isOf(Blocks.STONE)) {
				++i6;
			}

			if(world.getBlockState(new BlockPos(i3, i4, i5 + 1)).isOf(Blocks.STONE)) {
				++i6;
			}

			int i7 = 0;
			if(world.isAir(new BlockPos(i3 - 1, i4, i5))) {
				++i7;
			}

			if(world.isAir(new BlockPos(i3 + 1, i4, i5))) {
				++i7;
			}

			if(world.isAir(new BlockPos(i3, i4, i5 - 1))) {
				++i7;
			}

			if(world.isAir(new BlockPos(i3, i4, i5 + 1))) {
				++i7;
			}

			if(i6 == 3 && i7 == 1) {
				world.setBlockState(new BlockPos(i3, i4, i5), this.liquidBlockId, Block.NOTIFY_LISTENERS);
				//world.scheduledUpdatesAreImmediate = true;
				//Block.blocksList[this.liquidBlockId].updateTick(world, i3, i4, i5, random);
				//world.scheduledUpdatesAreImmediate = false;
			}

			return true;
		}
	}
}
