package cyborgcabbage.spacepunk.client.render.dimension;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.DimensionRenderingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.DimensionEffects;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;

public class VenusRenderer implements DimensionRenderingRegistry.SkyRenderer {
    @Override
    public void render(WorldRenderContext context) {
        if (context.world() == null || context.matrixStack() == null) return;
        ClientWorld world = context.world();
        //Sky Color
        Vec3d skyColor = world.getSkyColor(context.camera().getPos(), context.tickDelta());
        float skyR = (float) skyColor.x;
        float skyG = (float) skyColor.y;
        float skyB = (float) skyColor.z;
        //Fog
        BackgroundRenderer.setFogBlack();

        RenderSystem.depthMask(false);

        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(skyR, skyG, skyB, 1.0f);
        DimensionRenderUtil.drawLightSky(context);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        DimensionRenderUtil.drawSunriseAndSunset(context);

        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
        float rg = 1.0f - world.getRainGradient(context.tickDelta());
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, rg);
        DimensionRenderUtil.drawBody(context, 0.f, 100.f, 60.f, DimensionRenderUtil.SUN);

        float starBrightness = world.method_23787(context.tickDelta()) * rg * 0.7f;
        if(starBrightness > 0.f) {
            RenderSystem.setShaderColor(starBrightness, starBrightness, starBrightness, starBrightness);
            DimensionRenderUtil.drawStars(context);
        }

        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, 1.0f);
        DimensionRenderUtil.drawDarkSky(context);

        RenderSystem.disableBlend();
        if (world.getDimensionEffects().isAlternateSkyColor()) {
            RenderSystem.setShaderColor(skyR * 0.2f + 0.04f, skyG * 0.2f + 0.04f, skyB * 0.6f + 0.1f, 1.0f);
        } else {
            RenderSystem.setShaderColor(skyR, skyG, skyB, 1.0f);
        }
        RenderSystem.enableTexture();

        RenderSystem.depthMask(true);
    }
}
