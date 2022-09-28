package cyborgcabbage.spacepunk.client.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import cyborgcabbage.spacepunk.Spacepunk;
import cyborgcabbage.spacepunk.entity.RocketEntity;
import cyborgcabbage.spacepunk.inventory.RocketScreenHandler;
import cyborgcabbage.spacepunk.util.PlanetProperties;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

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
        var targetDim = Spacepunk.TARGET_DIMENSION_LIST.get(screenHandler.getTargetDimensionIndex()).getValue();
        if(buttonChangeTarget != null)
            buttonChangeTarget.setMessage(Text.translatable("dimension."+targetDim));
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        drawMouseoverTooltip(matrices, mouseX, mouseY);
        RenderSystem.setShaderTexture(0, PlanetProperties.getIcon(targetDim));
        int size = 64;
        int rightX = x+backgroundWidth/2;
        int rightWidth = backgroundWidth/2;
        DrawableHelper.drawTexture(matrices, rightX+rightWidth/2-size/2, centreY-size/2, size, size, 0, 0, 32, 32, 32, 32);
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
        buttonLaunch = new ButtonWidget(x+(leftWidth-buttonWidth)/2, centreY-22-buttonHeight/2 , buttonWidth, buttonHeight, Text.translatable("gui.spacepunk.rocket.launch"), b -> buttonAction(RocketEntity.ACTION_LAUNCH, true));
        buttonDisassemble = new ButtonWidget(x+(leftWidth-buttonWidth)/2, centreY-buttonHeight/2 , buttonWidth, buttonHeight, Text.translatable("gui.spacepunk.rocket.disassemble"), b -> buttonAction(RocketEntity.ACTION_DISASSEMBLE, true));
        buttonChangeTarget = new ButtonWidget(x+(leftWidth-buttonWidth)/2, centreY+22-buttonHeight/2 , buttonWidth, buttonHeight, Text.literal(""), b -> buttonAction(RocketEntity.ACTION_CHANGE_TARGET, false));
        buttonRotate = new ButtonWidget(x+(leftWidth-buttonWidth)/2, centreY+44-buttonHeight/2 , buttonWidth, buttonHeight, Text.translatable("gui.spacepunk.rocket.rotate"), b -> buttonAction(RocketEntity.ACTION_ROTATE, false));
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


