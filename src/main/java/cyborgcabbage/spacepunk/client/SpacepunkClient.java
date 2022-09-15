package cyborgcabbage.spacepunk.client;

import cyborgcabbage.spacepunk.Spacepunk;
import cyborgcabbage.spacepunk.block.OxygenBlock;
import cyborgcabbage.spacepunk.client.book.MyPageMultiblock;
import cyborgcabbage.spacepunk.client.inventory.RocketScreen;
import cyborgcabbage.spacepunk.client.render.dimension.MoonRenderer;
import cyborgcabbage.spacepunk.client.render.dimension.VenusRenderer;
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
import net.fabricmc.fabric.api.client.rendering.v1.DimensionRenderingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.color.world.GrassColors;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.entity.Entity;
import net.minecraft.item.BlockItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.client.book.ClientBookRegistry;
import vazkii.patchouli.client.book.page.PageText;

@Environment(EnvType.CLIENT)
public class SpacepunkClient implements ClientModInitializer {
    public static final EntityModelLayer MODEL_ROCKET_LAYER = new EntityModelLayer(new Identifier(Spacepunk.MOD_ID, "rocket"), "main");
    public static final EntityModelLayer MODEL_SULFUR_CREEPER_LAYER = new EntityModelLayer(new Identifier(Spacepunk.MOD_ID, "sulfur_creeper"), "main");
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(Spacepunk.ROCKET_ENTITY, RocketEntityRenderer::new);
        EntityRendererRegistry.register(Spacepunk.SULFUR_TNT_ENTITY, SulfurTntEntityRenderer::new);
        EntityRendererRegistry.register(Spacepunk.SULFUR_CREEPER_ENTITY, SulfurCreeperEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(MODEL_ROCKET_LAYER, RocketEntityModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(MODEL_SULFUR_CREEPER_LAYER, SulfurCreeperEntityModel::getTexturedModelData);
        HandledScreens.register(Spacepunk.ROCKET_SCREEN_HANDLER, RocketScreen::new);
        BlockRenderLayerMap.INSTANCE.putBlock(Spacepunk.VENUS_DOOR, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(Spacepunk.VENUS_TRAPDOOR, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(Spacepunk.VENUS_SAPLING, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(Spacepunk.EXTRA_TALL_GRASS, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(Spacepunk.OXYGEN, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(Spacepunk.VENUS_LEAVES, RenderLayer.getCutoutMipped());
        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> {
            if (world == null || pos == null) {
                return GrassColors.getColor(0.5, 1.0);
            }
            return BiomeColors.getGrassColor(world, pos);
        }, Spacepunk.EXTRA_TALL_GRASS);
        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
            Block block = ((BlockItem)stack.getItem()).getBlock();
            BlockState blockState = block.getDefaultState();
            BlockColorProvider blockColorProvider = ColorProviderRegistry.BLOCK.get(block);
            return blockColorProvider == null ? -1 : blockColorProvider.getColor(blockState, null, null, tintIndex);
        }, Spacepunk.EXTRA_TALL_GRASS);
        ModelPredicateProviderRegistry.register(Spacepunk.PRESSURE_GAUGE, new Identifier("pressure"), (stack, clientWorld, livingEntity, seed) -> {
            Entity entity = (livingEntity != null) ? livingEntity : stack.getHolder();
            if(entity == null) return 0.5f;
            World world = (clientWorld != null) ? clientWorld : entity.world;
            if(world == null) return 0.5f;
            int pressure = OxygenBlock.getPressure(world, new BlockPos(entity.getEyePos()));
            return pressure/8.f;
        });
        DimensionRenderingRegistry.registerSkyRenderer(Spacepunk.MOON, new MoonRenderer());
        DimensionRenderingRegistry.registerDimensionEffects(Spacepunk.MOON.getValue(), new MoonRenderer.MoonEffects());
        DimensionRenderingRegistry.registerSkyRenderer(Spacepunk.VENUS, new VenusRenderer());

        ClientBookRegistry.INSTANCE.pageTypes.put(Spacepunk.id("my_multiblock"), MyPageMultiblock.class);
    }
}
