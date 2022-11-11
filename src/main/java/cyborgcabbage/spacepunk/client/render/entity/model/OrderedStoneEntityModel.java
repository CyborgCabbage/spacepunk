package cyborgcabbage.spacepunk.client.render.entity.model;

import cyborgcabbage.spacepunk.entity.OrderedStoneEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.util.math.MathHelper;

@Environment(value= EnvType.CLIENT)
public class OrderedStoneEntityModel extends SinglePartEntityModel<OrderedStoneEntity> {
    private final ModelPart root;
    //private final ModelPart head;
    private final ModelPart leftHindLeg;
    private final ModelPart rightHindLeg;
    private final ModelPart leftMiddleLeg;
    private final ModelPart rightMiddleLeg;
    private final ModelPart leftFrontLeg;
    private final ModelPart rightFrontLeg;
    private static final int HEAD_AND_BODY_Y_PIVOT = 6;

    private static final String  RIGHT_MIDDLE_LEG = "right_middle_leg";
    private static final String  LEFT_MIDDLE_LEG = "left_middle_leg";

    public OrderedStoneEntityModel(ModelPart root) {
        this.root = root;
        //this.head = root.getChild(EntityModelPartNames.HEAD);
        this.rightHindLeg = root.getChild(EntityModelPartNames.RIGHT_HIND_LEG);
        this.leftHindLeg = root.getChild(EntityModelPartNames.LEFT_HIND_LEG);
        this.rightMiddleLeg = root.getChild(RIGHT_MIDDLE_LEG);
        this.leftMiddleLeg = root.getChild(LEFT_MIDDLE_LEG);
        this.rightFrontLeg = root.getChild(EntityModelPartNames.RIGHT_FRONT_LEG);
        this.leftFrontLeg = root.getChild(EntityModelPartNames.LEFT_FRONT_LEG);
    }

    public static TexturedModelData getTexturedModelData() {
        Dilation dilation = Dilation.NONE;
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        //modelPartData.addChild(EntityModelPartNames.HEAD, ModelPartBuilder.create().uv(0, 0).cuboid(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, dilation), ModelTransform.pivot(0.0f, 6.0f, 0.0f));
        modelPartData.addChild(EntityModelPartNames.BODY, ModelPartBuilder.create().uv(0, 0).cuboid(-15.0f, -14.0f, -15.0f, 30.0f, 30.0f, 30.0f, dilation), ModelTransform.pivot(0.0f, 0.0f, 0.0f));
        ModelPartBuilder leg = ModelPartBuilder.create().uv(0, 0).cuboid(-4.0f, 0.0f, -4.0f, 8.0f, 14.0f, 8.0f, dilation);
        modelPartData.addChild(EntityModelPartNames.RIGHT_HIND_LEG, leg, ModelTransform.pivot(-12.0f, 10.0f, 12.0f));
        modelPartData.addChild(EntityModelPartNames.LEFT_HIND_LEG, leg, ModelTransform.pivot(12.0f, 10.0f, 12.0f));
        modelPartData.addChild(RIGHT_MIDDLE_LEG, leg, ModelTransform.pivot(-12.0f, 10.0f, 0.0f));
        modelPartData.addChild(LEFT_MIDDLE_LEG, leg, ModelTransform.pivot(12.0f, 10.0f, 0.0f));
        modelPartData.addChild(EntityModelPartNames.RIGHT_FRONT_LEG, leg, ModelTransform.pivot(-12.0f, 10.0f, -12.0f));
        modelPartData.addChild(EntityModelPartNames.LEFT_FRONT_LEG, leg, ModelTransform.pivot(12.0f, 10.0f, -12.0f));
        return TexturedModelData.of(modelData, 64, 32);
    }

    @Override
    public ModelPart getPart() {
        return this.root;
    }

    @Override
    public void setAngles(OrderedStoneEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        //this.head.yaw = headYaw * ((float)Math.PI / 180);
        //this.head.pitch = headPitch * ((float)Math.PI / 180);
        this.leftHindLeg.pivotY = 10.f-(MathHelper.cos(limbAngle * 0.6662f)+1.f) * 2.0f * limbDistance;
        this.rightHindLeg.pivotY = 10.f-(MathHelper.cos(limbAngle * 0.6662f + (float)Math.PI)+1.f) * 2.0f * limbDistance;

        this.leftMiddleLeg.pivotY = 10.f-(MathHelper.cos(limbAngle * 0.6662f + (float)Math.PI)+1.f) * 2.0f * limbDistance;
        this.rightMiddleLeg.pivotY = 10.f-(MathHelper.cos(limbAngle * 0.6662f)+1.f) * 2.0f * limbDistance;

        this.leftFrontLeg.pivotY = 10.f-(MathHelper.cos(limbAngle * 0.6662f)+1.f) * 2.0f * limbDistance;
        this.rightFrontLeg.pivotY = 10.f-(MathHelper.cos(limbAngle * 0.6662f + (float)Math.PI)+1.f) * 2.0f * limbDistance;
    }
}
