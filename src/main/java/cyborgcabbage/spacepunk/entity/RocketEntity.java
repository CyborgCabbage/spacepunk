package cyborgcabbage.spacepunk.entity;

import cyborgcabbage.spacepunk.Spacepunk;
import cyborgcabbage.spacepunk.inventory.ImplementedInventory;
import cyborgcabbage.spacepunk.inventory.RocketScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class RocketEntity extends Entity implements ExtendedScreenHandlerFactory, ImplementedInventory {
    public static final int ACTION_DISASSEMBLE = 0;
    public static final int ACTION_LAUNCH = 1;

    private static final TrackedData<Boolean> ENGINE_ON = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public RocketEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(ENGINE_ON, false);
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        Inventories.readNbt(nbt, items);
        this.dataTracker.set(ENGINE_ON, nbt.getBoolean("EngineOn"));
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        Inventories.writeNbt(nbt, items);
        nbt.putBoolean("EngineOn", this.dataTracker.get(ENGINE_ON));
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
        if (source.getAttacker() instanceof PlayerEntity player) {
            if(player.getAbilities().creativeMode && !player.equals(getFirstPassenger())) disassemble(false);
        }
        return true;
    }
    /*
    Rocket Interaction
    */
    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        if (player.shouldCancelInteraction() || Objects.equals(getFirstPassenger(), player)) {
            return menuInteraction(player, hand);
        }
        return ridingInteraction(player, hand);
    }

    /*
    Rocket Menu
    */
    private ActionResult menuInteraction(PlayerEntity player, Hand hand){
        player.openHandledScreen(this);
        if (!player.world.isClient) {
            this.emitGameEvent(GameEvent.CONTAINER_OPEN, player);
            return ActionResult.CONSUME;
        }
        return ActionResult.SUCCESS;
    }
    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new RocketScreenHandler(syncId, inv, this);
    }
    @Override
    public Text getDisplayName() {
        return new TranslatableText(getType().getTranslationKey());
    }

    /*
    Inventory
    */
    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(9, ItemStack.EMPTY);

    @Override
    public DefaultedList<ItemStack> getItems() {
        return items;
    }

    /*
    Ride Rocket
    */
    private ActionResult ridingInteraction(PlayerEntity player, Hand hand){
        if (!this.world.isClient) {
            return player.startRiding(this) ? ActionResult.CONSUME : ActionResult.PASS;
        }
        return ActionResult.SUCCESS;
    }
    public double getMountedHeightOffset() {
        return 1.0;
    }

    /*
    Tick
    */
    @Override
    public void baseTick() {
        //Setup
        boolean engineOn = dataTracker.get(ENGINE_ON);
        //Movement
        double gravity = -0.08;
        if(engineOn) gravity = 0.01;
        addVelocity(0.0, gravity, 0.0);
        move(MovementType.SELF,getVelocity());
        //Particles
        if(world.isClient() && engineOn){
            Vec3d vel = getVelocity();
            world.addParticle(ParticleTypes.SMOKE, getX(), getY(), getZ(), vel.x, vel.y-1.0, vel.z);
        }
        super.baseTick();
    }
    /*
    Launch
    */
    public void launch(){
        dataTracker.set(ENGINE_ON, true);
    }

    /*
    Disassemble
    */
    public void disassemble(boolean drop){
        if(drop){
            world.spawnEntity(new ItemEntity(world,getX(),getY()+3.5,getZ(),new ItemStack(Spacepunk.ROCKET_NOSE),0,0,0));
            world.spawnEntity(new ItemEntity(world,getX(),getY()+2.5,getZ(),new ItemStack(Blocks.COPPER_BLOCK),0,0,0));
            world.spawnEntity(new ItemEntity(world,getX(),getY()+1.5,getZ(),new ItemStack(Blocks.COPPER_BLOCK),0,0,0));
            world.spawnEntity(new ItemEntity(world,getX(),getY()+0.5,getZ(),new ItemStack(Blocks.BLAST_FURNACE),0,0,0));
        }
        discard();
    }


    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeInt(getId());
    }
}
