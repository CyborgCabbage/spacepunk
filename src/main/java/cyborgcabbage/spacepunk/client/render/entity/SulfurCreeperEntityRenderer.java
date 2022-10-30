package cyborgcabbage.spacepunk.client.render.entity;

import cyborgcabbage.spacepunk.Spacepunk;
import cyborgcabbage.spacepunk.client.render.entity.model.SulfurCreeperEntityModel;
import cyborgcabbage.spacepunk.entity.SulfurCreeperEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value= EnvType.CLIENT)
public class SulfurCreeperEntityRenderer extends MobEntityRenderer<SulfurCreeperEntity, SulfurCreeperEntityModel> {
    private static final Identifier TEXTURE = new Identifier(Spacepunk.MOD_ID,"textures/entity/sulfur_creeper.png");

    public SulfurCreeperEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new SulfurCreeperEntityModel(context.getPart(EntityModelLayers.CREEPER)), 0.5f);
        //this.addFeature(new CreeperChargeFeatureRenderer(this, context.getModelLoader()));
    }

    @Override
    protected void scale(SulfurCreeperEntity creeperEntity, MatrixStack matrixStack, float f) {
        float g = creeperEntity.getClientFuseTime(f);
        float h = 1.0f + MathHelper.sin(g * 100.0f) * g * 0.01f;
        g = MathHelper.clamp(g, 0.0f, 1.0f);
        g *= g;
        g *= g;
        float i = (1.0f + g * 0.4f) * h;
        float j = (1.0f + g * 0.1f) / h;
        matrixStack.scale(i, j, i);
    }

    @Override
    public Identifier getTexture(SulfurCreeperEntity entity) {
        return TEXTURE;
    }

    @Override
    protected float getAnimationCounter(SulfurCreeperEntity entity, float tickDelta) {
        float g = entity.getClientFuseTime(tickDelta);
        if ((int)(g * 10.0f) % 2 == 0) {
            return 0.0f;
        }
        return MathHelper.clamp(g, 0.5f, 1.0f);
    }
}
