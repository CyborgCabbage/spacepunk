package cyborgcabbage.spacepunk.client.render.entity.model;

import cyborgcabbage.spacepunk.entity.RocketEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;

public class RocketEntityModel extends EntityModel<RocketEntity> {
    private final ModelPart base;

    public RocketEntityModel(ModelPart modelPart) {
        this.base = modelPart.getChild("base");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("base", ModelPartBuilder.create().uv(0, 0).cuboid(-8.0F, -48.0F, -8.0F, 16.0F, 36.0F, 16.0F, new Dilation(0.0F))
                .uv(64, 46).cuboid(-7.0F, -4.0F, -7.0F, 14.0F, 4.0F, 14.0F, new Dilation(0.0F))
                .uv(64, 65).cuboid(-6.0F, -7.0F, -6.0F, 12.0F, 3.0F, 12.0F, new Dilation(0.0F))
                .uv(64, 115).cuboid(-5.0F, -12.0F, -5.0F, 10.0F, 3.0F, 10.0F, new Dilation(0.0F))
                .uv(64, 103).cuboid(-4.0F, -9.0F, -4.0F, 8.0F, 1.0F, 8.0F, new Dilation(0.0F))
                .uv(0, 64).cuboid(-6.0F, -52.0F, -6.0F, 12.0F, 4.0F, 12.0F, new Dilation(0.0F))
                .uv(0, 84).cuboid(-4.0F, -56.0F, -4.0F, 8.0F, 4.0F, 8.0F, new Dilation(0.0F))
                .uv(0, 104).cuboid(-2.0F, -60.0F, -2.0F, 4.0F, 4.0F, 4.0F, new Dilation(0.0F))
                .uv(64, 85).cuboid(-5.0F, -8.0F, -5.0F, 10.0F, 1.0F, 10.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
        return TexturedModelData.of(modelData, 128, 128);
    }

    @Override
    public void setAngles(RocketEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        base.render(matrices,vertices,light,overlay);
    }
}
