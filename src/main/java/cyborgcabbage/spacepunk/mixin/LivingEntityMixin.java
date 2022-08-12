package cyborgcabbage.spacepunk.mixin;

import cyborgcabbage.spacepunk.Spacepunk;
import cyborgcabbage.spacepunk.entity.RocketEntity;
import cyborgcabbage.spacepunk.util.MyDamageSource;
import cyborgcabbage.spacepunk.util.PlanetProperties;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    private static final DamageSource VACUUM = new MyDamageSource("vacuum").setBypassesArmor();

    @Inject(method="getPreferredEquipmentSlot",at=@At("HEAD"),cancellable = true)
    private static void rocketNoseHat(ItemStack stack, CallbackInfoReturnable<EquipmentSlot> cir){
        if(stack.isOf(Spacepunk.ROCKET_NOSE.asItem()))
            cir.setReturnValue(EquipmentSlot.HEAD);
    }

    @ModifyVariable(method = "travel", at = @At("STORE"), ordinal = 0)
    private double applyGravityToAcceleration(double x) {
        LivingEntity that = (LivingEntity)(Object)this;
        if(that.world != null)
            return x * PlanetProperties.getGravity(that.world.getRegistryKey().getValue());
        return x;
    }

    @ModifyVariable(method="handleFallDamage", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private float applyGravityToFallDamage(float x){
        LivingEntity that = (LivingEntity)(Object)this;
        if(that.world != null)
            return x * PlanetProperties.getGravity(that.world.getRegistryKey().getValue());
        return x;
    }

    @Inject(method="baseTick", at=@At("HEAD"))
    private void suffocateWithoutAtmosphere(CallbackInfo ci){
        LivingEntity that = (LivingEntity)(Object)this;
        if(that.world != null)
            if(!PlanetProperties.hasAtmosphere(that.world.getRegistryKey().getValue()))
                if(!(that.getVehicle() instanceof RocketEntity))
                    that.damage(VACUUM, 1.0f);
    }
}
