package cyborgcabbage.spacepunk.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class RocketEntity extends Entity {

    public RocketEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    protected void initDataTracker() {

    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {

    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {

    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }

    /*
    Makes it so that it has a solid collision box, just like a boat or shulker.
    */
    @Override
    public boolean isCollidable() {
        return true;
    }

    /*
    Allows the entity to be collided with (like when the player clicks on it or tries to place a block that overlaps it).
    */
    @Override
    public boolean collides() {
        return !isRemoved();
    }
    
    /*
    Allow the rocket to be destroyed by hitting in creative mode.
    */
    @Override
    public boolean damage(DamageSource source, float amount) {
        if (isInvulnerableTo(source)) {
            return false;
        }
        if (world.isClient || isRemoved()) {
            return true;
        }
        emitGameEvent(GameEvent.ENTITY_DAMAGED, source.getAttacker());
        if (source.getAttacker() instanceof PlayerEntity && ((PlayerEntity)source.getAttacker()).getAbilities().creativeMode) {
            discard();
        }
        return true;
    }
}
