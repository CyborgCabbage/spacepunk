package cyborgcabbage.spacepunk.client.render.dimension;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import cyborgcabbage.spacepunk.Spacepunk;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

public class DimensionRenderUtil {
    @Nullable private static VertexBuffer lightSkyBuffer;
    @Nullable private static VertexBuffer darkSkyBuffer;
    @Nullable private static VertexBuffer starsBuffer;

    public static final Identifier MOON_PHASES = new Identifier("textures/environment/moon_phases.png");
    public static final Identifier SUN = new Identifier("textures/environment/sun.png");
    public static final Identifier EARTH = Spacepunk.id("textures/environment/earth16.png");
    public static final Identifier SUN_NO_GLOW = Spacepunk.id("textures/environment/sun_no_glow.png");

    public static VertexBuffer createAndFillBuffer(BufferMaker runnable){
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        var buffer = new VertexBuffer();
        BufferBuilder.BuiltBuffer builtBuffer = runnable.build(bufferBuilder);
        buffer.bind();
        buffer.upload(builtBuffer);
        VertexBuffer.unbind();
        return buffer;
    }

    public static BufferBuilder.BuiltBuffer renderSky(BufferBuilder builder, float coneHeight) {
        float flip = Math.signum(coneHeight);
        float xScale = 512.0f;
        float zScale = 512.0f;
        RenderSystem.setShader(GameRenderer::getPositionShader);
        builder.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION);
        //Hub
        builder.vertex(0.0, coneHeight, 0.0).next();
        //Perimeter
        for (int degree = -180; degree <= 180; degree += 45) {
            float radians = (float)degree * ((float)Math.PI / 180.f);
            builder.vertex(
                    MathHelper.cos(radians) * xScale * flip,
                    coneHeight,
                    MathHelper.sin(radians) * zScale
            ).next();
        }
        return builder.end();
    }

    public static BufferBuilder.BuiltBuffer renderStars(BufferBuilder buffer, long seed) {
        Random random = Random.create(seed);
        RenderSystem.setShader(GameRenderer::getPositionShader);
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
        for (int star = 0; star < 1500; ++star) {
            double x = random.nextFloat() * 2.0f - 1.0f;
            double y = random.nextFloat() * 2.0f - 1.0f;
            double z = random.nextFloat() * 2.0f - 1.0f;
            double perturb = 0.15f + random.nextFloat() * 0.1f;
            double lengthSquared = x * x + y * y + z * z;
            if (!(lengthSquared < 1.0) || !(lengthSquared > 0.01)) continue;
            double inverseLength = 1.0 / Math.sqrt(lengthSquared);
            x *= inverseLength;
            y *= inverseLength;
            z *= inverseLength;
            double yaw = Math.atan2(x, z);
            double nx = Math.sin(yaw);
            double ny = Math.cos(yaw);
            double pitch = Math.atan2(Math.sqrt(x * x + z * z), y);
            double px = Math.sin(pitch);
            double py = Math.cos(pitch);
            double rotation = random.nextDouble() * Math.PI * 2.0;
            double rx = Math.sin(rotation);
            double ry = Math.cos(rotation);
            for (int vertex = 0; vertex < 4; ++vertex) {
                double v1 = (double)((vertex & 2) - 1) * perturb;
                double v2 = (double)((vertex + 1 & 2) - 1) * perturb;

                double a1 = v1 * ry - v2 * rx;
                double a2 = v2 * ry + v1 * rx;

                double b1 = a1 * px + 0.0 * py;
                double b2 = 0.0 * px - a1 * py;

                double ox = b2 * nx - a2 * ny;
                double oy = b1;
                double oz = a2 * nx + b2 * ny;
                buffer.vertex(x * 100.0 + ox, y * 100.0 + oy, z * 100.0 + oz).next();
            }
        }
        return buffer.end();
    }

    public static void drawBuffer(WorldRenderContext context, VertexBuffer buffer) {
        buffer.bind();
        buffer.draw(context.matrixStack().peek().getPositionMatrix(), context.projectionMatrix(), RenderSystem.getShader());
        VertexBuffer.unbind();
    }

    public static void drawSunriseAndSunset(WorldRenderContext context) {
        var bufferBuilder = Tessellator.getInstance().getBuffer();
        var matrices = context.matrixStack();
        var world = context.world();
        var tickDelta = context.tickDelta();
        float[] fogColorOverride = world.getDimensionEffects().getFogColorOverride(world.getSkyAngle(tickDelta), tickDelta);
        if (fogColorOverride != null) {
            //Setup
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            RenderSystem.disableTexture();

            matrices.push();
            matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90.0f));
            float flip = MathHelper.sin(world.getSkyAngleRadians(tickDelta)) < 0.0f ? 180.0f : 0.0f;
            matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(flip));
            matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(90.0f));
            float fogR = fogColorOverride[0];
            float fogG = fogColorOverride[1];
            float fogB = fogColorOverride[2];
            float fogA = fogColorOverride[3];
            Matrix4f matrix = matrices.peek().getPositionMatrix();
            bufferBuilder.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
            bufferBuilder.vertex(matrix, 0.0f, 100.0f, 0.0f).color(fogR, fogG, fogB, fogA).next();
            for (int vertex = 0; vertex <= 16; ++vertex) {
                float theta = (float) vertex * ((float) Math.PI * 2) / 16.0f;
                float x = MathHelper.sin(theta);
                float y = MathHelper.cos(theta);
                bufferBuilder.vertex(matrix, x * 120.0f, y * 120.0f, -y * 40.0f * fogA).color(fogR, fogG, fogB, 0.0f).next();
            }
            BufferRenderer.drawWithShader(bufferBuilder.end());
            matrices.pop();
        }
    }

    public static void drawBody(WorldRenderContext context, float angle, float distance, float size, Identifier texture, Vec2f uv1, Vec2f uv2){
        var bufferBuilder = Tessellator.getInstance().getBuffer();
        var matrices = context.matrixStack();
        var world = context.world();
        var tickDelta = context.tickDelta();
        //Setup
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.enableTexture();
        RenderSystem.setShaderTexture(0, texture);
        matrices.push();
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-90.0f));//Rotate to be east-west as opposed to north-south
        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(world.getSkyAngle(tickDelta) * 360.0f + angle));
        Matrix4f positionMatrix = matrices.peek().getPositionMatrix();
        float halfSun = size/2.f;
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(positionMatrix, -halfSun, distance, -halfSun).texture(uv1.x, uv1.y).next();
        bufferBuilder.vertex(positionMatrix, halfSun, distance, -halfSun).texture(uv2.x, uv1.y).next();
        bufferBuilder.vertex(positionMatrix, halfSun, distance, halfSun).texture(uv2.x, uv2.y).next();
        bufferBuilder.vertex(positionMatrix, -halfSun, distance, halfSun).texture(uv1.x, uv2.y).next();
        BufferRenderer.drawWithShader(bufferBuilder.end());
        matrices.pop();
    }

    public static void drawBody(WorldRenderContext context, float angle, float distance, float size, Identifier texture){
        drawBody(context, angle, distance, size, texture, new Vec2f(0,0), new Vec2f(1,1));
    }

    public static void drawPhasedBody(WorldRenderContext context, float angle, float distance, float size, Identifier texture, int textureWidth, int textureHeight, int phase){
        int phaseX = phase % textureWidth;
        int phaseY = phase / textureWidth % textureHeight;
        Vec2f uv1 = new Vec2f(phaseX / (float)textureWidth, phaseY / (float)textureHeight);
        Vec2f uv2 = new Vec2f((phaseX + 1) / (float)textureWidth, (phaseY + 1) / (float)textureHeight);
        drawBody(context,angle,distance,size,texture,uv1,uv2);
    }

    public static void drawLightSky(WorldRenderContext context) {
        RenderSystem.setShader(GameRenderer::getPositionShader);
        RenderSystem.disableTexture();
        if(lightSkyBuffer == null)
            lightSkyBuffer = createAndFillBuffer(bb -> renderSky(bb, 16.0f));
        drawBuffer(context, lightSkyBuffer);
    }

    public static void drawDarkSky(WorldRenderContext context) {
        //Setup
        RenderSystem.setShader(GameRenderer::getPositionShader);
        RenderSystem.disableTexture();
        if(darkSkyBuffer == null)
            darkSkyBuffer = createAndFillBuffer(bb -> renderSky(bb, -16.0f));

        double darkening = context.gameRenderer().getClient().player.getCameraPosVec(context.tickDelta()).y - context.world().getLevelProperties().getSkyDarknessHeight(context.world());
        if (darkening < 0.0) {
            context.matrixStack().push();
            context.matrixStack().translate(0.0, 12.0, 0.0);
            drawBuffer(context, darkSkyBuffer);
            context.matrixStack().pop();
        }
    }

    public static void drawStars(WorldRenderContext context){
        RenderSystem.setShader(GameRenderer::getPositionShader);
        RenderSystem.disableTexture();
        if(starsBuffer == null)
             starsBuffer = createAndFillBuffer(bb -> renderStars(bb,10842L));
        var matrices = context.matrixStack();
        var world = context.world();
        var tickDelta = context.tickDelta();
        matrices.push();
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-90.0f));
        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(world.getSkyAngle(tickDelta) * 360.0f));
        BackgroundRenderer.clearFog();
        drawBuffer(context, starsBuffer);
        boolean thickenFog = world.getDimensionEffects().useThickFog(0, 0) || context.gameRenderer().getClient().inGameHud.getBossBarHud().shouldThickenFog();
        BackgroundRenderer.applyFog(context.camera(), BackgroundRenderer.FogType.FOG_SKY, context.gameRenderer().getViewDistance(), thickenFog, tickDelta);
        matrices.pop();
    }

    private void drawEarthSky(WorldRenderContext context) {
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
        drawLightSky(context);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        drawSunriseAndSunset(context);

        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
        float rg = 1.0f - world.getRainGradient(context.tickDelta());
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, rg);
        drawBody(context, 0.f, 100.f, 60.f, SUN);
        drawPhasedBody(context, 180.f, 100.0f, 40.f, MOON_PHASES, 4, 2, world.getMoonPhase());

        float starBrightness = world.method_23787(context.tickDelta()) * rg;
        if(starBrightness > 0.f) {
            RenderSystem.setShaderColor(starBrightness, starBrightness, starBrightness, starBrightness);
            drawStars(context);
        }

        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, 1.0f);
        drawDarkSky(context);

        RenderSystem.disableBlend();
        if (world.getDimensionEffects().isAlternateSkyColor()) {
            RenderSystem.setShaderColor(skyR * 0.2f + 0.04f, skyG * 0.2f + 0.04f, skyB * 0.6f + 0.1f, 1.0f);
        } else {
            RenderSystem.setShaderColor(skyR, skyG, skyB, 1.0f);
        }
        RenderSystem.enableTexture();

        RenderSystem.depthMask(true);
    }

    @FunctionalInterface
    interface BufferMaker {
        BufferBuilder.BuiltBuffer build(BufferBuilder bufferBuilder);
    }
}
