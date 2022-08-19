package cyborgcabbage.spacepunk.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(targets = "net.minecraft.world.chunk.ChunkSection$class_6869")
public class ChunkSectionMixin {
    @Shadow public int field_36408;
    @Shadow public int field_36409;
    @Shadow public int field_36410;

    /**
     * @author CyborgCabbage
     * @reason Vanilla assumes air blocks will not have random ticks, this is not true for oxygen.
     */
    @Overwrite
    public void accept(BlockState blockState, int i) {
        FluidState fluidState = blockState.getFluidState();
        if (!blockState.isAir()) {
            this.field_36408 += i;
        }
        if (blockState.hasRandomTicks()) {
            this.field_36409 += i;
        }
        if (!fluidState.isEmpty()) {
            this.field_36408 += i;
            if (fluidState.hasRandomTicks()) {
                this.field_36410 += i;
            }
        }
    }
}
