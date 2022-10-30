package cyborgcabbage.spacepunk.client.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import cyborgcabbage.spacepunk.Spacepunk;
import cyborgcabbage.spacepunk.entity.RocketEntity;
import cyborgcabbage.spacepunk.inventory.RocketScreenHandler;
import cyborgcabbage.spacepunk.util.PlanetProperties;
import cyborgcabbage.spacepunk.util.RocketNavigation;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Optional;

public class RocketScreen extends HandledScreen<RocketScreenHandler> {
    //A path to the gui texture. In this example we use the texture from the dispenser
    private static final Identifier BACKGROUND = new Identifier(Spacepunk.MOD_ID, "textures/gui/rocket.png");

    int centreY = 0;

    RocketScreenHandler screenHandler;
    public RocketScreen(RocketScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        screenHandler = handler;
        backgroundHeight += 16;
        playerInventoryTitleY += 10000;
    }

    private ButtonWidget buttonChangeTarget;
    private ButtonWidget buttonRotate;
    private ButtonWidget buttonLaunch;
    private ButtonWidget buttonDisassemble;

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, BACKGROUND);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        drawTexture(matrices, x, y, 0, 0, backgroundWidth, backgroundHeight);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        updateCentreY();
        if(client == null) return;
        if(client.world == null) return;
        Optional<RocketNavigation.Body> body = Spacepunk.ROCKET_NAVIGATION.find(client.world.getRegistryKey());
        if(body.isEmpty()) return;
        ArrayList<RocketNavigation.Body> targetList = body.get().parent.getChildren();
        Identifier targetDim = targetList.get(screenHandler.getTargetDimensionIndex()).getWorld().getValue();
        if(buttonChangeTarget != null)
            buttonChangeTarget.setMessage(Text.translatable("dimension."+targetDim));
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        drawMouseoverTooltip(matrices, mouseX, mouseY);
        RenderSystem.setShaderTexture(0, PlanetProperties.getIcon(targetDim));
        int size = 64;
        DrawableHelper.drawTexture(matrices, x+(backgroundWidth-size)/2, y+62-size/2, size, size, 0, 0, 32, 32, 32, 32);
        {
            Text text = Text.translatable("gui.spacepunk.rocket.construction").formatted(Formatting.UNDERLINE);
            int textWidth = textRenderer.getWidth(text);
            int textHeight = textRenderer.fontHeight;
            textRenderer.draw(matrices, text, x + (backgroundWidth - textWidth) / 2.f, centreY + 94 - textHeight / 2.f, 0x404040);
        }
        {
            Text text = Text.translatable("gui.spacepunk.rocket.navigation").formatted(Formatting.UNDERLINE);
            int textWidth = textRenderer.getWidth(text);
            int textHeight = textRenderer.fontHeight;
            textRenderer.draw(matrices, text, x + (backgroundWidth - textWidth) / 2.f, y + 22 - textHeight / 2.f, 0x404040);
        }
    }

    @Override
    protected void init() {
        super.init();
        updateCentreY();
        // Center the title
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
        int buttonWidth = 70;
        int buttonHeight = 20;
        int leftWidth = backgroundWidth/2;
        buttonChangeTarget = new ButtonWidget(x+backgroundWidth/2-buttonWidth/2, centreY+22-buttonHeight/2, buttonWidth, buttonHeight, Text.literal(""), b -> buttonAction(RocketEntity.ACTION_CHANGE_TARGET, false));
        buttonLaunch = new ButtonWidget(x+backgroundWidth/2-buttonWidth/2, centreY+74-buttonHeight/2, buttonWidth, buttonHeight, Text.translatable("gui.spacepunk.rocket.launch"), b -> buttonAction(RocketEntity.ACTION_LAUNCH, true));
        buttonDisassemble = new ButtonWidget(x+backgroundWidth/2-buttonWidth/2-36, centreY+112-buttonHeight/2, buttonWidth, buttonHeight, Text.translatable("gui.spacepunk.rocket.disassemble"), b -> buttonAction(RocketEntity.ACTION_DISASSEMBLE, true));
        buttonRotate = new ButtonWidget( x+backgroundWidth/2-buttonWidth/2+36, centreY+112-buttonHeight/2, buttonWidth, buttonHeight, Text.translatable("gui.spacepunk.rocket.rotate"), b -> buttonAction(RocketEntity.ACTION_ROTATE, false));
        this.addDrawableChild(buttonLaunch);
        this.addDrawableChild(buttonDisassemble);
        this.addDrawableChild(buttonChangeTarget);
        this.addDrawableChild(buttonRotate);
    }

    private void buttonAction(int actionId, boolean closeScreen){
        client.interactionManager.clickButton(handler.syncId, actionId);
        //Exit Menu
        if(closeScreen) client.setScreen(null);
    }

    private void updateCentreY(){
        centreY = y+54;
    }
}


