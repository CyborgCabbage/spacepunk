package cyborgcabbage.spacepunk.mixin;

import cyborgcabbage.spacepunk.Spacepunk;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractFireBlock.class)
public class AbstractFireBlockMixin {
    @Inject(method="isOverworldOrNether", at=@At("HEAD"), cancellable = true)
    private static void inject(World world, CallbackInfoReturnable<Boolean> cir) {
        if(world.getRegistryKey() == Spacepunk.BETA_OVERWORLD || world.getRegistryKey() == Spacepunk.BETA_NETHER) cir.setReturnValue(true);
    }

}
