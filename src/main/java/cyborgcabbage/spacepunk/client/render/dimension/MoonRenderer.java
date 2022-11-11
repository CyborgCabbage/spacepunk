package cyborgcabbage.spacepunk.client.render.dimension;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.DimensionRenderingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.DimensionEffects;
import net.minecraft.util.math.Vec3d;

@Environment(value= EnvType.CLIENT)
public class MoonRenderer implements DimensionRenderingRegistry.SkyRenderer {
    @Override
    public void render(WorldRenderContext context) {
        if (context.world() == null || context.matrixStack() == null) return;
        context.world().calculateAmbientDarkness();
        BackgroundRenderer.setFogBlack();
        RenderSystem.depthMask(false);

        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(0, 0, 0, 1);
        DimensionRenderUtil.drawLightSky(context);

        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        DimensionRenderUtil.drawStars(context);
        DimensionRenderUtil.drawBody(context, 0.f, 100.f, 15.f, DimensionRenderUtil.SUN_NO_GLOW);
        RenderSystem.setShaderColor(.5f,.5f,.5f,1);
        DimensionRenderUtil.drawBody(context, 180.f, 100.f, 20.f, DimensionRenderUtil.EARTH);


        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(0, 0, 0, 1);
        DimensionRenderUtil.drawDarkSky(context);

        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(0, 0, 0, 1);
        RenderSystem.enableTexture();

        RenderSystem.depthMask(true);
    }

    public static class MoonEffects extends DimensionEffects {
        public MoonEffects() {
            super(Float.NaN, false, SkyType.NORMAL, false, false);
        }

        @Override
        public Vec3d adjustFogColor(Vec3d color, float sunHeight) {
            return new Vec3d(0,0,0);
        }

        @Override
        public boolean useThickFog(int camX, int camY) {
            return false;
        }

        @Override
        public float[] getFogColorOverride(float skyAngle, float tickDelta) {
            return null;
        }
    }
}