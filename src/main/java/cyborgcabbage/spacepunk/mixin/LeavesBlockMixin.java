package cyborgcabbage.spacepunk.mixin;

import cyborgcabbage.spacepunk.block.OxygenBlock;
import cyborgcabbage.spacepunk.util.PlanetProperties;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LeavesBlock.class)
public class LeavesBlockMixin {
    private static final int OXYGEN_AMOUNT = 32;

    @Inject(method="randomTick",at=@At("HEAD"))
    private void produceOxygen(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci){
        if(!PlanetProperties.hasAtmosphere(world.getRegistryKey().getValue()))
            OxygenBlock.depositOxygen(world, pos, OXYGEN_AMOUNT);
    }

    @Inject(method="hasRandomTicks",at=@At("HEAD"),cancellable = true)
    private void alwaysHasRandomTicks(BlockState state, CallbackInfoReturnable<Boolean> cir){
        cir.setReturnValue(true);
    }
}
