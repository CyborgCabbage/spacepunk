package cyborgcabbage.spacepunk.client;

import cyborgcabbage.spacepunk.Spacepunk;
import cyborgcabbage.spacepunk.client.inventory.RocketScreen;
import cyborgcabbage.spacepunk.client.render.entity.RocketEntityRenderer;
import cyborgcabbage.spacepunk.client.render.entity.SulfurCreeperEntityRenderer;
import cyborgcabbage.spacepunk.client.render.entity.SulfurTntEntityRenderer;
import cyborgcabbage.spacepunk.client.render.entity.model.RocketEntityModel;
import cyborgcabbage.spacepunk.client.render.entity.model.SulfurCreeperEntityModel;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class SpacepunkClient implements ClientModInitializer {
    public static final EntityModelLayer MODEL_ROCKET_LAYER = new EntityModelLayer(new Identifier(Spacepunk.MOD_ID, "rocket"), "main");
    public static final EntityModelLayer MODEL_SULFUR_CREEPER_LAYER = new EntityModelLayer(new Identifier(Spacepunk.MOD_ID, "sulfur_creeper"), "main");
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(Spacepunk.ROCKET_ENTITY_TYPE, RocketEntityRenderer::new);
        EntityRendererRegistry.register(Spacepunk.SULFUR_TNT_ENTITY_TYPE, SulfurTntEntityRenderer::new);
        EntityRendererRegistry.register(Spacepunk.SULFUR_CREEPER_ENTITY_TYPE, SulfurCreeperEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(MODEL_ROCKET_LAYER, RocketEntityModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(MODEL_SULFUR_CREEPER_LAYER, SulfurCreeperEntityModel::getTexturedModelData);
        ScreenRegistry.register(Spacepunk.ROCKET_SCREEN_HANDLER, RocketScreen::new);
        BlockRenderLayerMap.INSTANCE.putBlock(Spacepunk.VENUS_DOOR, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(Spacepunk.VENUS_TRAPDOOR, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(Spacepunk.VENUS_SAPLING, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(Spacepunk.EXTRA_TALL_GRASS, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(Spacepunk.VENUS_LEAVES, RenderLayer.getCutoutMipped());
        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> {
            if (world == null || pos == null) {
                return -1;
            }
            return BiomeColors.getGrassColor(world, pos);
        }, Spacepunk.EXTRA_TALL_GRASS);
    }
}
