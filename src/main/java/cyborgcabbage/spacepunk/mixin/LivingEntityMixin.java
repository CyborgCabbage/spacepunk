package cyborgcabbage.spacepunk.mixin;

import cyborgcabbage.spacepunk.Spacepunk;
import cyborgcabbage.spacepunk.util.PlanetProperties;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
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
}
