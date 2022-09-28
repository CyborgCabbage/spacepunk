package cyborgcabbage.spacepunk.mixin;

import cyborgcabbage.spacepunk.util.PlanetProperties;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(DimensionType.class)
public abstract class DimensionTypeMixin {

    @ModifyVariable(method = "getSkyAngle", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private long changeDayLength(long value){
        DimensionType that = (DimensionType) (Object) this;
        return value/PlanetProperties.getTimeDivisor(that.effects());
    }
}
