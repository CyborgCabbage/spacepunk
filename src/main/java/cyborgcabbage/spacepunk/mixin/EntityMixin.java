package cyborgcabbage.spacepunk.mixin;

import cyborgcabbage.spacepunk.Spacepunk;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.BlockLocating;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.dimension.AreaHelper;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow public World world;

    @Shadow protected abstract Optional<BlockLocating.Rectangle> getPortalRect(ServerWorld destWorld, BlockPos destPos, boolean destIsNether, WorldBorder worldBorder);

    @Shadow protected BlockPos lastNetherPortalPosition;

    @Shadow protected abstract Vec3d positionInPortal(Direction.Axis portalAxis, BlockLocating.Rectangle portalRect);

    @Shadow public abstract EntityDimensions getDimensions(EntityPose pose);

    @Shadow public abstract EntityPose getPose();

    @Shadow public abstract Vec3d getVelocity();

    @Shadow public abstract float getYaw();

    @Shadow public abstract double getX();

    @Shadow public abstract double getY();

    @Shadow public abstract double getZ();

    @Shadow public abstract float getPitch();

    @Inject(method="getTeleportTarget",at=@At("HEAD"), cancellable = true)
    void inject(ServerWorld destination, CallbackInfoReturnable<TeleportTarget> cir) {
        boolean destIsNether = (destination.getRegistryKey() == Spacepunk.BETA_NETHER && this.world.getRegistryKey() == Spacepunk.BETA_OVERWORLD);
        boolean originIsNether = (this.world.getRegistryKey() == Spacepunk.BETA_NETHER && destination.getRegistryKey() == Spacepunk.BETA_OVERWORLD);
        if(originIsNether || destIsNether){
            WorldBorder worldBorder = destination.getWorldBorder();
            double d = DimensionType.getCoordinateScaleFactor(this.world.getDimension(), destination.getDimension());
            BlockPos blockPos2 = worldBorder.clamp(this.getX() * d, this.getY(), this.getZ() * d);
            cir.setReturnValue(this.getPortalRect(destination, blockPos2, destIsNether, worldBorder).map(rect -> {
                Vec3d vec3d;
                Direction.Axis axis;
                BlockState blockState = this.world.getBlockState(this.lastNetherPortalPosition);
                if (blockState.contains(Properties.HORIZONTAL_AXIS)) {
                    axis = blockState.get(Properties.HORIZONTAL_AXIS);
                    BlockLocating.Rectangle rectangle = BlockLocating.getLargestRectangle(this.lastNetherPortalPosition, axis, 21, Direction.Axis.Y, 21, pos -> this.world.getBlockState((BlockPos)pos) == blockState);
                    vec3d = this.positionInPortal(axis, rectangle);
                } else {
                    axis = Direction.Axis.X;
                    vec3d = new Vec3d(0.5, 0.0, 0.0);
                }
                return AreaHelper.getNetherTeleportTarget(destination, rect, axis, vec3d, this.getDimensions(this.getPose()), this.getVelocity(), this.getYaw(), this.getPitch());
            }).orElse(null));
        }
    }
    @ModifyArg(method="tickPortal", at=@At(value="INVOKE",target="Lnet/minecraft/server/MinecraftServer;getWorld(Lnet/minecraft/util/registry/RegistryKey;)Lnet/minecraft/server/world/ServerWorld;"))
    private RegistryKey<World> inject2(RegistryKey<World> key){
        if(this.world.getRegistryKey() == Spacepunk.BETA_NETHER){
            return Spacepunk.BETA_OVERWORLD;
        }else if(this.world.getRegistryKey() == Spacepunk.BETA_OVERWORLD){
            return Spacepunk.BETA_NETHER;
        }
        return key;
    }
}
