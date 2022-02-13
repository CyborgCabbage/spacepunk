package cyborgcabbage.spacepunk.mixin;

import cyborgcabbage.spacepunk.Spacepunk;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Inject(method="getPreferredEquipmentSlot",at=@At("HEAD"),cancellable = true)
    private static void inject(ItemStack stack, CallbackInfoReturnable<EquipmentSlot> cir){
        if(stack.isOf(Spacepunk.ROCKET_NOSE.asItem())){
            cir.setReturnValue(EquipmentSlot.HEAD);
        }
    }
}
