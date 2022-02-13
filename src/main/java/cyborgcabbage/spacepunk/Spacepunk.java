package cyborgcabbage.spacepunk;

import cyborgcabbage.spacepunk.block.RocketNoseBlock;
import cyborgcabbage.spacepunk.entity.RocketEntity;
import cyborgcabbage.spacepunk.feature.FractalStarFeature;
import cyborgcabbage.spacepunk.feature.FractalStarFeatureConfig;
import cyborgcabbage.spacepunk.feature.StoneSpiralFeature;
import cyborgcabbage.spacepunk.feature.SurfaceOreFeature;
import cyborgcabbage.spacepunk.inventory.RocketScreenHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Spacepunk implements ModInitializer {
	public static final String MOD_ID = "spacepunk";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final EntityType<RocketEntity> ROCKET_ENTITY_TYPE = Registry.register(
			Registry.ENTITY_TYPE,
			new Identifier(MOD_ID, "rocket"),
			FabricEntityTypeBuilder.create(SpawnGroup.MISC, RocketEntity::new).dimensions(EntityDimensions.fixed(1.0f, 3.0f)).build()
	);

	public static final Block ROCKET_NOSE = new RocketNoseBlock(FabricBlockSettings.of(Material.METAL, MapColor.ORANGE).requiresTool().strength(3.0f, 6.0f).sounds(BlockSoundGroup.COPPER));
	//Moon
	public static final Block LUNAR_SOIL = new Block(AbstractBlock.Settings.of(Material.SOIL, MapColor.LIGHT_GRAY).strength(0.5f).sounds(BlockSoundGroup.GRAVEL));
	public static final Block LUNAR_ROCK = new Block(AbstractBlock.Settings.of(Material.STONE, MapColor.STONE_GRAY).requiresTool().strength(1.5f, 6.0f));

	public static final ScreenHandlerType<RocketScreenHandler> BOX_SCREEN_HANDLER;

	public static final Identifier ROCKET_ACTION_PACKET_ID = new Identifier(MOD_ID, "rocket_action");

	static {
		//We use registerSimple here because our Entity is not an ExtendedScreenHandlerFactory
		//but a NamedScreenHandlerFactory.
		//In a later Tutorial you will see what ExtendedScreenHandlerFactory can do!
		BOX_SCREEN_HANDLER = ScreenHandlerRegistry.registerExtended(new Identifier(MOD_ID, "rocket"), RocketScreenHandler::new);
	}
	
	@Override
	public void onInitialize() {
		Registry.register(Registry.BLOCK, new Identifier(MOD_ID, "rocket_nose"), ROCKET_NOSE);
		Registry.register(Registry.BLOCK, new Identifier(MOD_ID, "lunar_soil"), LUNAR_SOIL);
		Registry.register(Registry.BLOCK, new Identifier(MOD_ID, "lunar_rock"), LUNAR_ROCK);
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, "rocket_nose"), new BlockItem(ROCKET_NOSE, new FabricItemSettings().group(ItemGroup.MISC)));
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, "lunar_soil"), new BlockItem(LUNAR_SOIL, new FabricItemSettings().group(ItemGroup.MISC)));
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, "lunar_rock"), new BlockItem(LUNAR_ROCK, new FabricItemSettings().group(ItemGroup.MISC)));
		Registry.register(Registry.FEATURE, new Identifier(MOD_ID, "stone_spiral"), new StoneSpiralFeature(DefaultFeatureConfig.CODEC));
		Registry.register(Registry.FEATURE, new Identifier(MOD_ID, "fractal_star"), new FractalStarFeature(FractalStarFeatureConfig.CODEC));
		Registry.register(Registry.FEATURE, new Identifier(MOD_ID, "surface_ore"), new SurfaceOreFeature(OreFeatureConfig.CODEC));
		ServerPlayNetworking.registerGlobalReceiver(ROCKET_ACTION_PACKET_ID, (server, player, handler, buf, responseSender) -> {
			// Read packet data on the event loop
			int rocketEntityId = buf.readInt();
			int actionId = buf.readInt();
			server.execute(() -> {
				// Everything in this lambda is run on the render thread
				Entity entity = player.world.getEntityById(rocketEntityId);
				if(entity instanceof RocketEntity rocketEntity) {
					switch (actionId) {
						case RocketEntity.ACTION_DISASSEMBLE -> rocketEntity.disassemble(true);
						case RocketEntity.ACTION_LAUNCH -> rocketEntity.launch();

						default -> LOGGER.error("Rocket Action Packet: Unexpected value " + actionId);
					}
				}else{
					LOGGER.error("Rocket Action Packet: Could not find relevant RocketEntity");
				}
			});
		});
	}
}
