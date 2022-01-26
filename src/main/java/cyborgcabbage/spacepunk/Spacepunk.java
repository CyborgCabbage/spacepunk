package cyborgcabbage.spacepunk;

import cyborgcabbage.spacepunk.block.RocketNoseBlock;
import cyborgcabbage.spacepunk.entity.RocketEntity;
import cyborgcabbage.spacepunk.inventory.RocketScreenHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Spacepunk implements ModInitializer {
	public static final String MODID = "spacepunk";
	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);
	public static final EntityType<RocketEntity> ROCKET_ENTITY_TYPE = Registry.register(
			Registry.ENTITY_TYPE,
			new Identifier(MODID, "rocket"),
			FabricEntityTypeBuilder.create(SpawnGroup.MISC, RocketEntity::new).dimensions(EntityDimensions.fixed(1.0f, 3.0f)).build()
	);

	public static final Block ROCKET_NOSE_BLOCK = new RocketNoseBlock(FabricBlockSettings.of(Material.METAL, MapColor.ORANGE).requiresTool().strength(3.0f, 6.0f).sounds(BlockSoundGroup.COPPER));

	public static final ScreenHandlerType<RocketScreenHandler> BOX_SCREEN_HANDLER;

	public static final Identifier ROCKET_ACTION_PACKET_ID = new Identifier(MODID, "rocket_action");


	static {
		//We use registerSimple here because our Entity is not an ExtendedScreenHandlerFactory
		//but a NamedScreenHandlerFactory.
		//In a later Tutorial you will see what ExtendedScreenHandlerFactory can do!
		BOX_SCREEN_HANDLER = ScreenHandlerRegistry.registerExtended(new Identifier(MODID, "rocket"), RocketScreenHandler::new);
	}

	@Override
	public void onInitialize() {
		Registry.register(Registry.BLOCK, new Identifier(MODID, "rocket_nose"), ROCKET_NOSE_BLOCK);
		Registry.register(Registry.ITEM, new Identifier(MODID, "rocket_nose"), new BlockItem(ROCKET_NOSE_BLOCK, new FabricItemSettings().group(ItemGroup.MISC)));
		ServerPlayNetworking.registerGlobalReceiver(ROCKET_ACTION_PACKET_ID, (server, player, handler, buf, responseSender) -> {
			// Read packet data on the event loop
			int rocketEntityId = buf.readInt();
			int actionId = buf.readInt();
			server.execute(() -> {
				// Everything in this lambda is run on the render thread
				RocketEntity rocketEntity = (RocketEntity)player.world.getEntityById(rocketEntityId);
				LOGGER.info("Action Entity is "+rocketEntity.getEntityName());
				switch (actionId) {
					case RocketEntity.ACTION_DISASSEMBLE -> rocketEntity.discard();
					case RocketEntity.ACTION_LAUNCH -> rocketEntity.launch();
				}
			});
		});
	}
}
