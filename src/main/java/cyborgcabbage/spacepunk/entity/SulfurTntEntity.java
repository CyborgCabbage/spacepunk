package cyborgcabbage.spacepunk.entity;

import cyborgcabbage.spacepunk.Spacepunk;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class SulfurTntEntity extends Entity {
    private static final TrackedData<Integer> FUSE = DataTracker.registerData(SulfurTntEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Optional<BlockState>> STATE = DataTracker.registerData(SulfurTntEntity.class, TrackedDataHandlerRegistry.OPTIONAL_BLOCK_STATE);
    @Nullable
    private LivingEntity causingEntity;
    private float power = 5.0f;
    //private int fuse = 80;
    //private BlockState block = Blocks.AIR.getDefaultState();
    private static final String POWER_KEY = "power";
    private static final String FUSE_KEY = "fuse";
    private static final String BLOCK_KEY = "block";

    public SulfurTntEntity(EntityType<SulfurTntEntity> type, World world) {
        super(type, world);
        this.intersectionChecked = true;
    }

    public SulfurTntEntity(World world, double x, double y, double z, @Nullable LivingEntity igniter, int fuse, float power, BlockState block) {
        this(Spacepunk.SULFUR_TNT_ENTITY, world);
        this.setPosition(x, y, z);
        double d = world.random.nextDouble() * 6.2831854820251465;
        this.setVelocity(-Math.sin(d) * 0.02, 0.2f, -Math.cos(d) * 0.02);
        this.setFuse(fuse);
        this.prevX = x;
        this.prevY = y;
        this.prevZ = z;
        this.causingEntity = igniter;
        this.power = power;
        this.setBlock(block);
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(FUSE, 80);
        this.dataTracker.startTracking(STATE, Optional.of(Blocks.AIR.getDefaultState()));
    }

    @Override
    protected Entity.MoveEffect getMoveEffect() {
        return Entity.MoveEffect.NONE;
    }

    @Override
    public boolean canHit() {
        return !this.isRemoved();
    }

    @Override
    public void tick() {
        if (!this.hasNoGravity()) {
            this.setVelocity(this.getVelocity().add(0.0, -0.04, 0.0));
        }
        this.move(MovementType.SELF, this.getVelocity());
        this.setVelocity(this.getVelocity().multiply(0.98));
        if (this.onGround) {
            this.setVelocity(this.getVelocity().multiply(0.7, -0.5, 0.7));
        }
        int i = this.getFuse() - 1;
        this.setFuse(i);
        if (i <= 0) {
            this.discard();
            if (!this.world.isClient) {
                this.explode();
            }
        } else {
            this.updateWaterState();
            if (this.world.isClient) {
                this.world.addParticle(ParticleTypes.SMOKE, this.getX(), this.getY() + 0.5, this.getZ(), 0.0, 0.0, 0.0);
            }
        }
    }

    private void explode() {

        this.world.createExplosion(this, this.getX(), this.getBodyY(0.0625), this.getZ(), power, this.getBlock().getBlock() == Spacepunk.SULFUR_TNT, Explosion.DestructionType.BREAK);
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putShort(FUSE_KEY, (short)this.getFuse());
        nbt.putFloat(POWER_KEY, this.power);
        nbt.put(BLOCK_KEY, NbtHelper.fromBlockState(getBlock()));
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        this.setFuse(nbt.getShort(FUSE_KEY));
        this.power = nbt.getFloat(POWER_KEY);
        setBlock(NbtHelper.toBlockState(nbt.getCompound(BLOCK_KEY)));
    }

    @Nullable
    public LivingEntity getCausingEntity() {
        return this.causingEntity;
    }

    @Override
    protected float getEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return 0.15f;
    }

    public void setFuse(int fuse) {
        this.dataTracker.set(FUSE, fuse);
    }

    public int getFuse() {
        return this.dataTracker.get(FUSE);
    }

    public void setBlock(BlockState state) {
        this.dataTracker.set(STATE, Optional.of(state));
    }

    public BlockState getBlock() {
        return this.dataTracker.get(STATE).orElse(Blocks.AIR.getDefaultState());
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }
}
