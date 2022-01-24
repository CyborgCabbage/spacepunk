package cyborgcabbage.spacepunk.client.render.entity.model;

import cyborgcabbage.spacepunk.entity.RocketEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;

public class RocketEntityModel extends EntityModel<RocketEntity> {
    private final ModelPart base;
    public static final String ENGINE0 = "engine0";
    public static final String ENGINE1 = "engine1";
    public static final String ENGINE2 = "engine2";
    public static final String ENGINE3 = "engine3";
    public static final String ENGINE4 = "engine4";
    public static final String BODY = "body";
    public static final String NOSE0 = "nose0";
    public static final String NOSE1 = "nose1";
    public static final String NOSE2 = "nose2";

    public RocketEntityModel(ModelPart modelPart) {
        this.base = modelPart.getChild("base");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData base = modelPartData.addChild("base", ModelPartBuilder.create().uv(0, 0).cuboid(-8.0F, -48.0F, -8.0F, 16.0F, 36.0F, 16.0F, new Dilation(0.0F))
                .uv(64, 46).cuboid(-7.0F, -4.0F, -7.0F, 14.0F, 4.0F, 14.0F, new Dilation(0.0F))
                .uv(64, 65).cuboid(-6.0F, -7.0F, -6.0F, 12.0F, 3.0F, 12.0F, new Dilation(0.0F))
                .uv(64, 115).cuboid(-5.0F, -12.0F, -5.0F, 10.0F, 3.0F, 10.0F, new Dilation(0.0F))
                .uv(64, 103).cuboid(-4.0F, -9.0F, -4.0F, 8.0F, 1.0F, 8.0F, new Dilation(0.0F))
                .uv(0, 64).cuboid(-6.0F, -52.0F, -6.0F, 12.0F, 4.0F, 12.0F, new Dilation(0.0F))
                .uv(0, 84).cuboid(-4.0F, -56.0F, -4.0F, 8.0F, 4.0F, 8.0F, new Dilation(0.0F))
                .uv(0, 104).cuboid(-2.0F, -60.0F, -2.0F, 4.0F, 4.0F, 4.0F, new Dilation(0.0F))
                .uv(64, 85).cuboid(-5.0F, -8.0F, -5.0F, 10.0F, 1.0F, 10.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
        /*modelPartData.addChild(ENGINE0, ModelPartBuilder.create().uv(0, 0).cuboid(-7F, 0F, -7F, 14F, 5F, 14F), ModelTransform.pivot(0F, 0F, 0F));
        modelPartData.addChild(ENGINE1, ModelPartBuilder.create().uv(0, 0).cuboid(-6F, 5F, -6F, 12F, 4F, 12F), ModelTransform.pivot(0F, 0F, 0F));
        modelPartData.addChild(ENGINE2, ModelPartBuilder.create().uv(0, 0).cuboid(-5F, 9F, -5F, 10F, 2F, 10F), ModelTransform.pivot(0F, 0F, 0F));
        modelPartData.addChild(ENGINE3, ModelPartBuilder.create().uv(0, 0).cuboid(-4F, 11F, -4F, 8F, 1F, 8F), ModelTransform.pivot(0F, 0F, 0F));
        modelPartData.addChild(ENGINE4, ModelPartBuilder.create().uv(0, 0).cuboid(-6F, 12F, -6F, 12F, 4F, 12F), ModelTransform.pivot(0F, 0F, 0F));
        modelPartData.addChild(BODY, ModelPartBuilder.create().uv(0, 0).cuboid(-8F, 16F, -8F, 16F, 36F, 16F), ModelTransform.pivot(0F, 0F, 0F));
        modelPartData.addChild(NOSE0, ModelPartBuilder.create().uv(0, 0).cuboid(-6F, 52F, -6F, 12F, 4F, 12F), ModelTransform.pivot(0F, 0F, 0F));
        modelPartData.addChild(NOSE1, ModelPartBuilder.create().uv(0, 0).cuboid(-4F, 56F, -4F, 8F, 4F, 8F), ModelTransform.pivot(0F, 0F, 0F));
        modelPartData.addChild(NOSE2, ModelPartBuilder.create().uv(0, 0).cuboid(-2F, 60F, -2F, 4F, 4F, 4F), ModelTransform.pivot(0F, 0F, 0F));*/
        return TexturedModelData.of(modelData, 128, 128);
    }

    @Override
    public void setAngles(RocketEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        /*base.getChild(ENGINE0).render(matrices, vertices, light, overlay, red, green, blue, alpha);
        base.getChild(ENGINE1).render(matrices, vertices, light, overlay, red, green, blue, alpha);
        base.getChild(ENGINE2).render(matrices, vertices, light, overlay, red, green, blue, alpha);
        base.getChild(ENGINE3).render(matrices, vertices, light, overlay, red, green, blue, alpha);
        base.getChild(ENGINE4).render(matrices, vertices, light, overlay, red, green, blue, alpha);
        base.getChild(BODY).render(matrices, vertices, light, overlay, red, green, blue, alpha);
        base.getChild(NOSE0).render(matrices, vertices, light, overlay, red, green, blue, alpha);
        base.getChild(NOSE1).render(matrices, vertices, light, overlay, red, green, blue, alpha);
        base.getChild(NOSE2).render(matrices, vertices, light, overlay, red, green, blue, alpha);*/
        base.render(matrices,vertices,light,overlay);
    }
}
