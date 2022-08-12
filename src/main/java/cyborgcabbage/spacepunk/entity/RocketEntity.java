package cyborgcabbage.spacepunk.entity;

import cyborgcabbage.spacepunk.Spacepunk;
import cyborgcabbage.spacepunk.inventory.ImplementedInventory;
import cyborgcabbage.spacepunk.inventory.RocketScreenHandler;
import cyborgcabbage.spacepunk.util.PlanetProperties;
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

public class RocketEntity extends Entity implements ExtendedScreenHandlerFactory, ImplementedInventory {
    public static final int STATE_IDLE = 0;
    public static final int STATE_GOING_UP = 1;
    public static final int STATE_WAITING = 2;

    public static final int ACTION_DISASSEMBLE = 0;
    public static final int ACTION_LAUNCH = 1;
    public static final int ACTION_CHANGE_TARGET = 2;

    private static final int FUEL_CAPACITY = 10;

    private static final int TELEPORT_HEIGHT = 0;

    private static final TrackedData<Integer> TRAVEL_STATE = DataTracker.registerData(RocketEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> FUEL = DataTracker.registerData(RocketEntity.class, TrackedDataHandlerRegistry.INTEGER);

    private UUID passengerUuid;

    private int targetDimensionIndex = 0;

    public RocketEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(TRAVEL_STATE, 0);
        this.dataTracker.startTracking(FUEL, 0);
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        Inventories.readNbt(nbt, items);
        dataTracker.set(TRAVEL_STATE, nbt.getInt("TravelState"));
        dataTracker.set(FUEL, nbt.getInt("Fuel"));
        if(nbt.contains("Passenger"))
            passengerUuid = nbt.getUuid("Passenger");
        targetDimensionIndex = nbt.getInt("TargetDimensionIndex");
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        Inventories.writeNbt(nbt, items);
        nbt.putInt("TravelState", dataTracker.get(TRAVEL_STATE));
        nbt.putInt("Fuel", dataTracker.get(FUEL));
        if(passengerUuid != null)
            nbt.putUuid("Passenger", passengerUuid);
        nbt.putInt("TargetDimensionIndex", targetDimensionIndex);
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }


    //Makes it so that it has a solid collision box, just like a boat or shulker.
    @Override
    public boolean isCollidable() {
        return true;
    }

    // Allows the entity to be collided with (like when the player clicks on it or tries to place a block that overlaps it).
    @Override
    public boolean collides() {
        return !isRemoved();
    }
    
    // Allow the rocket to be destroyed by hitting in creative mode.
    @Override
    public boolean damage(DamageSource source, float amount) {
        if (isInvulnerableTo(source)) {
            return false;
        }
        if (world.isClient || isRemoved()) {
            return true;
        }
        emitGameEvent(GameEvent.ENTITY_DAMAGE, source.getAttacker());
        if (source.getAttacker() instanceof PlayerEntity player) {
            if(player.getAbilities().creativeMode && !player.equals(getFirstPassenger())) disassemble(false);
        }
        return true;
    }
    // Rocket Interaction
    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        ItemStack heldStack = player.getStackInHand(hand);
        int fuelLevel = dataTracker.get(FUEL);
        if(heldStack.isOf(Items.LAVA_BUCKET) && fuelLevel < FUEL_CAPACITY){
            if (!world.isClient) {
                player.setStackInHand(hand, ItemUsage.exchangeStack(heldStack, player, new ItemStack(Items.BUCKET)));
                player.incrementStat(Stats.USED.getOrCreateStat(Items.LAVA_BUCKET));
                world.playSound(null, getBlockPos(), SoundEvents.ITEM_BUCKET_EMPTY_LAVA, SoundCategory.BLOCKS, 1.0f, 1.0f);
                dataTracker.set(FUEL,fuelLevel+1);
            }
            return ActionResult.success(world.isClient);
        }else if(heldStack.isOf(Items.BUCKET) && fuelLevel > 0){
            if (!world.isClient) {
                player.setStackInHand(hand, ItemUsage.exchangeStack(heldStack, player, new ItemStack(Items.LAVA_BUCKET)));
                player.incrementStat(Stats.USED.getOrCreateStat(Items.BUCKET));
                world.playSound(null, getBlockPos(), SoundEvents.ITEM_BUCKET_FILL_LAVA, SoundCategory.BLOCKS, 1.0f, 1.0f);
                dataTracker.set(FUEL,fuelLevel-1);
            }
            return ActionResult.success(world.isClient);
        }

        if (player.shouldCancelInteraction() || Objects.equals(getFirstPassenger(), player)) {
            if(dataTracker.get(TRAVEL_STATE) != STATE_GOING_UP) {
                return menuInteraction(player, hand);
            }
        }
        return ridingInteraction(player, hand);
    }

    // Rocket Menu
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
        return new RocketScreenHandler(syncId, inv, this, propertyDelegate);
    }
    @Override
    public Text getDisplayName() {
        return Text.translatable(getType().getTranslationKey());
    }

    // Inventory
    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(9, ItemStack.EMPTY);

    @Override
    public DefaultedList<ItemStack> getItems() {
        return items;
    }

    // Ride Rocket
    private ActionResult ridingInteraction(PlayerEntity player, Hand hand){
        if (!this.world.isClient) {
            return player.startRiding(this) ? ActionResult.CONSUME : ActionResult.PASS;
        }
        return ActionResult.SUCCESS;
    }
    public double getMountedHeightOffset() {
        return 1.0;
    }

    @Override
    public void baseTick() {
        //Movement
        moveRocket();
        //Particles
        if(world.isClient() && dataTracker.get(TRAVEL_STATE) == STATE_GOING_UP){
            Vec3d vel = getVelocity();
            world.addParticle(ParticleTypes.SMOKE, getX(), getY(), getZ(), vel.x, vel.y-1.0, vel.z);
        }
        super.baseTick();
        if(!world.isClient()){
            //Teleport
            if(getY() > world.getTopY() + TELEPORT_HEIGHT && dataTracker.get(TRAVEL_STATE) == STATE_GOING_UP){
                RegistryKey<World> targetDimensionKey = Spacepunk.TARGET_DIMENSION_LIST.get(targetDimensionIndex);
                savePassenger();
                dataTracker.set(TRAVEL_STATE, STATE_WAITING);
                ServerWorld destination = world.getServer().getWorld(targetDimensionKey);
                Vec3d pos = new Vec3d(getPos().x, destination.getTopY() + TELEPORT_HEIGHT, getPos().z);
                for(Entity passenger: getPassengerList()) {
                    FabricDimensions.teleport(passenger, destination, new TeleportTarget(pos, Vec3d.ZERO, passenger.getYaw(), passenger.getPitch()));
                }
                FabricDimensions.teleport(this, destination, new TeleportTarget(pos, Vec3d.ZERO, getYaw(), getPitch()));
            }
            //Land
            if(dataTracker.get(TRAVEL_STATE) == STATE_WAITING){
                //Pickup passenger after teleport
                if(passengerUuid != null){
                    PlayerEntity player = world.getPlayerByUuid(passengerUuid);
                    if(player != null){
                        player.startRiding(this);
                        passengerUuid = null;
                    }
                }else{
                    dataTracker.set(TRAVEL_STATE, STATE_IDLE);
                }
            }
        }
    }

    @Override
    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }

    private void savePassenger(){
        Entity passenger = getFirstPassenger();
        if(passenger instanceof PlayerEntity player)
            passengerUuid = player.getUuid();
    }

    private void moveRocket() {
        setVelocity(0.0, getVelocity().y, 0.0);
        double gravity = -0.08 * PlanetProperties.getGravity(world.getRegistryKey().getValue());
        switch (dataTracker.get(TRAVEL_STATE)) {
            case STATE_IDLE -> {
                addVelocity(0.0, gravity, 0.0);
                move(MovementType.SELF, getVelocity());
            }
            case STATE_GOING_UP -> {
                addVelocity(0.0, 0.01, 0.0);
                move(MovementType.SELF, getVelocity());
            }
            case STATE_WAITING -> {
                setVelocity(0.0, 0.0, 0.0);
            }
        }
    }

    public void launch(PlayerEntity player){
        if(!world.isClient) {
            if (dataTracker.get(FUEL) < FUEL_CAPACITY){
                player.sendMessage(Text.translatable("entity.spacepunk.rocket.fuel"), true);
                return;
            }
            if(!Spacepunk.TARGET_DIMENSION_LIST.contains(world.getRegistryKey())){
                player.sendMessage(Text.translatable("entity.spacepunk.rocket.wrong_dimension"), true);
                return;
            }
            if(Spacepunk.TARGET_DIMENSION_LIST.get(targetDimensionIndex).equals(world.getRegistryKey())){
                player.sendMessage(Text.translatable("entity.spacepunk.rocket.in_dimension"), true);
                return;
            }
            player.sendMessage(Text.translatable("entity.spacepunk.rocket.launch"), true);
            dataTracker.set(FUEL, 0);
            dataTracker.set(TRAVEL_STATE, STATE_GOING_UP);
        }
    }

    public void disassemble(boolean dropItems){
        if(dropItems){
            world.spawnEntity(new ItemEntity(world,getX(),getY()+3.5,getZ(),new ItemStack(Spacepunk.ROCKET_NOSE),0,0,0));
            world.spawnEntity(new ItemEntity(world,getX(),getY()+2.5,getZ(),new ItemStack(Blocks.COPPER_BLOCK),0,0,0));
            world.spawnEntity(new ItemEntity(world,getX(),getY()+1.5,getZ(),new ItemStack(Blocks.COPPER_BLOCK),0,0,0));
            world.spawnEntity(new ItemEntity(world,getX(),getY()+0.5,getZ(),new ItemStack(Blocks.BLAST_FURNACE),0,0,0));
        }
        discard();
    }

    public void changeTarget(){
        targetDimensionIndex++;
        targetDimensionIndex %= Spacepunk.TARGET_DIMENSION_LIST.size();
    }


    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeInt(getId());
    }

    private final PropertyDelegate propertyDelegate = new PropertyDelegate() {
        @Override
        public int get(int index) {
            return targetDimensionIndex;
        }

        @Override
        public void set(int index, int value) {
            targetDimensionIndex = value;
        }

        //this is supposed to return the amount of integers you have in your delegate, in our example only one
        @Override
        public int size() {
            return 1;
        }
    };
}
