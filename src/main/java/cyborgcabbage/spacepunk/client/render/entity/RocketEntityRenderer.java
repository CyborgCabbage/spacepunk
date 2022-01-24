package cyborgcabbage.spacepunk.client.render.entity;

import cyborgcabbage.spacepunk.Spacepunk;
import cyborgcabbage.spacepunk.client.SpacepunkClient;
import cyborgcabbage.spacepunk.client.render.entity.model.RocketEntityModel;
import cyborgcabbage.spacepunk.entity.RocketEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;

public class RocketEntityRenderer extends EntityRenderer<RocketEntity> {
    private final RocketEntityModel model;

    public RocketEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        this.model = new RocketEntityModel(ctx.getPart(SpacepunkClient.MODEL_CUBE_LAYER));
    }

    @Override
    public Identifier getTexture(RocketEntity entity) {
        return new Identifier(Spacepunk.MODID, "textures/entity/rocket.png");
    }

    @Override
    public void render(RocketEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();
        matrices.multiply(new Quaternion(Vec3f.POSITIVE_X,180.0f,true));
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(model.getLayer(this.getTexture(entity)));
        model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, 1.0f);
        matrices.pop();
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }
}
