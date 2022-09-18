package cyborgcabbage.spacepunk.inventory;

import cyborgcabbage.spacepunk.Spacepunk;
import cyborgcabbage.spacepunk.entity.RocketEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;

public class RocketScreenHandler extends ScreenHandler {
    private final RocketEntity rocketEntity;
    PropertyDelegate propertyDelegate;

    public RocketScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId, new ArrayPropertyDelegate(1), null);
    }

    public RocketScreenHandler(int syncId, PropertyDelegate propertyDelegate, RocketEntity _rocketEntity) {
        super(Spacepunk.ROCKET_SCREEN_HANDLER, syncId);
        this.propertyDelegate = propertyDelegate;
        this.addProperties(propertyDelegate);
        rocketEntity = _rocketEntity;
    }

    public int getTargetDimensionIndex(){
        return propertyDelegate.get(0);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        if(rocketEntity == null) return true;
        return player.distanceTo(rocketEntity) <= 8.0;
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int invSlot) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean onButtonClick(PlayerEntity player, int id) {
        if(rocketEntity != null) {
            switch (id) {
                case RocketEntity.ACTION_DISASSEMBLE -> rocketEntity.disassemble(true);
                case RocketEntity.ACTION_LAUNCH -> rocketEntity.launch(player);
                case RocketEntity.ACTION_CHANGE_TARGET -> rocketEntity.changeTarget();
                case RocketEntity.ACTION_ROTATE -> rocketEntity.rotate();
                default -> {
                    Spacepunk.LOGGER.error("Rocket Action Packet: Unexpected value " + id);
                    return false;
                }
            }
            return true;
        }else{
            return false;
        }
    }
}


