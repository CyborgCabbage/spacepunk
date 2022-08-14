package cyborgcabbage.spacepunk.mixin;

import cyborgcabbage.spacepunk.Spacepunk;
import cyborgcabbage.spacepunk.entity.RocketEntity;
import cyborgcabbage.spacepunk.util.PlanetProperties;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Shadow protected abstract int getNextAirUnderwater(int air);

    @Shadow public abstract ItemStack getEquippedStack(EquipmentSlot var1);

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
    private void suffocateInVacuum(CallbackInfo ci){
        LivingEntity that = (LivingEntity)(Object)this;
        if(Spacepunk.inVacuum(that)){
            that.setAir(this.getNextAirUnderwater(that.getAir()));
            if (that.getAir() == -20) {
                that.setAir(0);
                that.damage(Spacepunk.VACUUM, 1.0f);
            }
        }
    }

    @ModifyArg(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;setAir(I)V", ordinal = 2))
    private int restoreAir(int newAir) {
        LivingEntity that = (LivingEntity)(Object)this;
        //If invulnerable
        if (that instanceof PlayerEntity pe && pe.getAbilities().invulnerable) return newAir;
        //If not in a vacuum
        if(!Spacepunk.inVacuum(that)) return newAir;
        //If in a vacuum but not wearing a space helmet
        if(!hasSpaceHelmet()) return that.getAir();
        //If wearing a helmet but is not a player
        if (!(that instanceof PlayerEntity pe)) return newAir;
        //Restore air using bottles
        if (that.getAir() < that.getMaxAir() - 21) {
            DefaultedList<ItemStack> main = pe.getInventory().main;
            for (int i = 0; i < main.size(); i++) {
                ItemStack stack = main.get(i);
                if (stack.isOf(Spacepunk.BOTTLED_AIR)) {
                    stack.damage(1, that, a -> {});
                    if (stack.isEmpty()) {
                        main.set(i, new ItemStack(Items.GLASS_BOTTLE));
                    }
                    return that.getAir() + 20;
                }
            }
        }
        return that.getAir();
    }

    private boolean hasSpaceHelmet(){
        return this.getEquippedStack(EquipmentSlot.HEAD).isOf(Spacepunk.SPACESUIT_HELMET);
    }
}
