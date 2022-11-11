package cyborgcabbage.spacepunk.client.render.entity;

import cyborgcabbage.spacepunk.client.SpacepunkClient;
import cyborgcabbage.spacepunk.client.render.entity.model.OrderedStoneEntityModel;
import cyborgcabbage.spacepunk.entity.OrderedStoneEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.util.Identifier;

@Environment(value= EnvType.CLIENT)
public class OrderedStoneEntityRenderer extends MobEntityRenderer<OrderedStoneEntity, OrderedStoneEntityModel> {
    private static final Identifier TEXTURE = new Identifier("textures/block/stone.png");

    public OrderedStoneEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new OrderedStoneEntityModel(context.getPart(SpacepunkClient.MODEL_ORDERED_STONE_LAYER)), 0.5f);
    }

    @Override
    public Identifier getTexture(OrderedStoneEntity entity) {
        return TEXTURE;
    }
}
