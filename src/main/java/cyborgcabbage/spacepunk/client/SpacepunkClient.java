package cyborgcabbage.spacepunk.client;

import cyborgcabbage.spacepunk.Spacepunk;
import cyborgcabbage.spacepunk.client.inventory.RocketScreen;
import cyborgcabbage.spacepunk.client.render.entity.RocketEntityRenderer;
import cyborgcabbage.spacepunk.client.render.entity.model.RocketEntityModel;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class SpacepunkClient implements ClientModInitializer {
    public static final EntityModelLayer MODEL_CUBE_LAYER = new EntityModelLayer(new Identifier(Spacepunk.MOD_ID, "rocket"), "main");
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(Spacepunk.ROCKET_ENTITY_TYPE, RocketEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(MODEL_CUBE_LAYER, RocketEntityModel::getTexturedModelData);
        ScreenRegistry.register(Spacepunk.ROCKET_SCREEN_HANDLER, RocketScreen::new);
    }
}
