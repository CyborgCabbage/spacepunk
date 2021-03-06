package cyborgcabbage.spacepunk.client.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import cyborgcabbage.spacepunk.Spacepunk;
import cyborgcabbage.spacepunk.entity.RocketEntity;
import cyborgcabbage.spacepunk.inventory.RocketScreenHandler;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class RocketScreen extends HandledScreen<RocketScreenHandler> {
    //A path to the gui texture. In this example we use the texture from the dispenser
    private static final Identifier TEXTURE = new Identifier(Spacepunk.MOD_ID, "textures/gui/rocket.png");
    RocketScreenHandler screenHandler;
    public RocketScreen(RocketScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        screenHandler = handler;
    }

    private ButtonWidget buttonChangeTarget;

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        drawTexture(matrices, x, y, 0, 0, backgroundWidth, backgroundHeight);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if(buttonChangeTarget != null)
            buttonChangeTarget.setMessage(new TranslatableText("dimension."+Spacepunk.TARGET_DIMENSION_LIST.get(screenHandler.getTargetDimensionIndex()).getValue()));
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    @Override
    protected void init() {
        super.init();
        // Center the title
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
        int buttonWidth = 70;
        this.addDrawableChild(new ButtonWidget(x+(backgroundWidth-buttonWidth)/2, y+19 , buttonWidth, 20, new LiteralText("Launch"), button -> {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeInt(handler.getRocketEntityId());
            buf.writeInt(RocketEntity.ACTION_LAUNCH);
            ClientPlayNetworking.send(Spacepunk.ROCKET_ACTION_PACKET_ID, buf);
            //Exit Menu
            this.client.setScreen(null);
            this.client.mouse.lockCursor();
        }));
        this.addDrawableChild(new ButtonWidget(x+(backgroundWidth-buttonWidth)/2, y+19+25 , buttonWidth, 20, new LiteralText("Disassemble"), button -> {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeInt(handler.getRocketEntityId());
            buf.writeInt(RocketEntity.ACTION_DISASSEMBLE);
            ClientPlayNetworking.send(Spacepunk.ROCKET_ACTION_PACKET_ID, buf);
            //Exit Menu
            this.client.setScreen(null);
            this.client.mouse.lockCursor();
        }));
        buttonChangeTarget = new ButtonWidget(x+(backgroundWidth-buttonWidth)/2, y+19+50 , buttonWidth, 20, new LiteralText(""), button -> {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeInt(handler.getRocketEntityId());
            buf.writeInt(RocketEntity.ACTION_CHANGE_TARGET);
            ClientPlayNetworking.send(Spacepunk.ROCKET_ACTION_PACKET_ID, buf);
        });
        this.addDrawableChild(buttonChangeTarget);
    }
}


