package cyborgcabbage.spacepunk.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(targets = "net.minecraft.world.chunk.ChunkSection$BlockStateCounter")
public class ChunkSectionMixin {
    @Shadow public int nonEmptyBlockCount;
    @Shadow public int randomTickableBlockCount;
    @Shadow public int nonEmptyFluidCount;

    /**
     * @author CyborgCabbage
     * @reason Vanilla assumes air blocks will not have random ticks, this is not true for oxygen.
     */
    @Overwrite
    public void accept(BlockState blockState, int i) {
        FluidState fluidState = blockState.getFluidState();
        if (!blockState.isAir()) {
            this.nonEmptyBlockCount += i;
        }
        if (blockState.hasRandomTicks()) {
            this.randomTickableBlockCount += i;
        }
        if (!fluidState.isEmpty()) {
            this.nonEmptyBlockCount += i;
            if (fluidState.hasRandomTicks()) {
                this.nonEmptyFluidCount += i;
            }
        }
    }
}
