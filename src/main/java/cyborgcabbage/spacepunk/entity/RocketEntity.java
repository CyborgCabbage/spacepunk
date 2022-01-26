package cyborgcabbage.spacepunk.entity;

import cyborgcabbage.spacepunk.inventory.RocketScreenHandler;
import cyborgcabbage.spacepunk.inventory.ImplementedInventory;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public class RocketEntity extends Entity implements ExtendedScreenHandlerFactory, ImplementedInventory {
    public static final int ACTION_DISASSEMBLE = 0;
    public static final int ACTION_LAUNCH = 1;

    private boolean engineOn = false;

    public RocketEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    protected void initDataTracker() {

    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        Inventories.readNbt(nbt, items);
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        Inventories.writeNbt(nbt, items);
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
    /*
    Rocket Interaction: Riding/Menu
    */
    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        if (player.shouldCancelInteraction()) {
            player.openHandledScreen(this);
            if (!player.world.isClient) {
                this.emitGameEvent(GameEvent.CONTAINER_OPEN, player);
                return ActionResult.CONSUME;
            }
            return ActionResult.SUCCESS;
        }
        if (!this.world.isClient) {
            return player.startRiding(this) ? ActionResult.CONSUME : ActionResult.PASS;
        }
        return ActionResult.SUCCESS;
    }

    /*
    Rocket Menu
    */
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
    public double getMountedHeightOffset() {
        return 1.0;
    }

    /*
    Movement
    */
    @Override
    public void baseTick() {
        double gravity = -0.08;
        if(engineOn) gravity = 0.01;
        addVelocity(0.0, gravity, 0.0);
        move(MovementType.SELF,getVelocity());
        super.baseTick();
    }
    /*
    Launch
    */
    public void launch(){
        engineOn = true;
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeInt(getId());
    }
}
