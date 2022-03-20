package cyborgcabbage.spacepunk.mixin;

import cyborgcabbage.spacepunk.Spacepunk;
import cyborgcabbage.spacepunk.util.PlanetProperties;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Inject(method="getPreferredEquipmentSlot",at=@At("HEAD"),cancellable = true)
    private static void inject(ItemStack stack, CallbackInfoReturnable<EquipmentSlot> cir){
        if(stack.isOf(Spacepunk.ROCKET_NOSE.asItem())){
            cir.setReturnValue(EquipmentSlot.HEAD);
        }
    }

    @ModifyVariable(method = "travel", at = @At("STORE"), ordinal = 0)
    private double injected(double x) {
        LivingEntity that = (LivingEntity)(Object)this;
        World world = that.world;
        if(world != null) {
            return x * PlanetProperties.getGravity(world.getRegistryKey().getValue());
        }
        return x;
    }
}
