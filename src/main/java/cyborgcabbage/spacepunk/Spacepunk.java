package cyborgcabbage.spacepunk;

import cyborgcabbage.spacepunk.armor.CustomArmorMaterial;
import cyborgcabbage.spacepunk.block.ExtraTallGrassBlock;
import cyborgcabbage.spacepunk.block.RocketNoseBlock;
import cyborgcabbage.spacepunk.block.SulfurTntBlock;
import cyborgcabbage.spacepunk.block.VenusSaplingGenerator;
import cyborgcabbage.spacepunk.entity.RocketEntity;
import cyborgcabbage.spacepunk.entity.SulfurCreeperEntity;
import cyborgcabbage.spacepunk.entity.SulfurTntEntity;
import cyborgcabbage.spacepunk.feature.BoulderFeature;
import cyborgcabbage.spacepunk.feature.BoulderFeatureConfig;
import cyborgcabbage.spacepunk.feature.FractalStarFeature;
import cyborgcabbage.spacepunk.feature.FractalStarFeatureConfig;
import cyborgcabbage.spacepunk.inventory.RocketScreenHandler;
import cyborgcabbage.spacepunk.item.BottledAirItem;
import cyborgcabbage.spacepunk.item.ExtraTallGrassBlockItem;
import cyborgcabbage.spacepunk.util.MyDamageSource;
import cyborgcabbage.spacepunk.util.PlanetProperties;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.block.*;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.*;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class Spacepunk implements ModInitializer {
	public static final String MOD_ID = "spacepunk";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final DamageSource VACUUM = new MyDamageSource("vacuum").setBypassesArmor();

	public static ArrayList<RegistryKey<World>> TARGET_DIMENSION_LIST = new ArrayList<>();

	public static final RegistryKey<World> MOON = RegistryKey.of(Registry.WORLD_KEY, id("moon"));
	public static final RegistryKey<World> VENUS = RegistryKey.of(Registry.WORLD_KEY, id("venus"));

	public static EntityType<RocketEntity> ROCKET_ENTITY;
	public static EntityType<SulfurTntEntity> SULFUR_TNT_ENTITY;
	public static EntityType<SulfurCreeperEntity> SULFUR_CREEPER_ENTITY;

	public static final Block ROCKET_NOSE = new RocketNoseBlock(FabricBlockSettings.of(Material.METAL, MapColor.ORANGE).requiresTool().strength(3.0f, 6.0f).sounds(BlockSoundGroup.COPPER));
	public static final ItemGroup MY_ITEM_GROUP = FabricItemGroupBuilder.build(id("spacepunk"), () -> new ItemStack(ROCKET_NOSE));
	//Moon
	public static final Block LUNAR_SOIL = new Block(FabricBlockSettings.of(Material.SOIL, MapColor.LIGHT_GRAY).strength(0.5f).sounds(BlockSoundGroup.GRAVEL));
	public static final Block LUNAR_ROCK = new Block(FabricBlockSettings.of(Material.STONE, MapColor.STONE_GRAY).requiresTool().strength(1.5f, 6.0f));
	//Venus
	public static final Block VENUS_WOOD = new PillarBlock(FabricBlockSettings.of(Material.WOOD, MapColor.DIRT_BROWN).strength(2.0f).sounds(BlockSoundGroup.WOOD));
	public static final Block VENUS_LEAVES = new LeavesBlock(FabricBlockSettings.of(Material.LEAVES).strength(0.2f).ticksRandomly().sounds(BlockSoundGroup.GRASS).nonOpaque().allowsSpawning((a,b,c,d)->false).suffocates((a,b,c)->false).blockVision((a,b,c)->false));
	public static final Block VENUS_PLANKS = new Block(FabricBlockSettings.of(Material.WOOD, MapColor.PALE_YELLOW).strength(2.0f, 3.0f).sounds(BlockSoundGroup.WOOD));
	public static final Block VENUS_SLAB = new SlabBlock(FabricBlockSettings.of(Material.WOOD, MapColor.ORANGE).strength(2.0f, 3.0f).sounds(BlockSoundGroup.WOOD));
	public static final Block VENUS_STAIRS = new StairsBlock(VENUS_PLANKS.getDefaultState(), FabricBlockSettings.copy(VENUS_PLANKS));
	public static final Block VENUS_LOG = new PillarBlock(FabricBlockSettings.of(Material.WOOD, state -> state.get(PillarBlock.AXIS) == Direction.Axis.Y ? MapColor.PALE_YELLOW : MapColor.SPRUCE_BROWN).strength(2.0f).sounds(BlockSoundGroup.WOOD));
	public static final Block VENUS_PRESSURE_PLATE = new PressurePlateBlock(PressurePlateBlock.ActivationRule.EVERYTHING, FabricBlockSettings.of(Material.WOOD, VENUS_PLANKS.getDefaultMapColor()).noCollision().strength(0.5f).sounds(BlockSoundGroup.WOOD));
	public static final Block VENUS_TRAPDOOR = new TrapdoorBlock(FabricBlockSettings.of(Material.WOOD, MapColor.PALE_YELLOW).strength(3.0f).sounds(BlockSoundGroup.WOOD).nonOpaque().allowsSpawning((a, b, c, d)->false));
	public static final Block VENUS_BUTTON = new WoodenButtonBlock(FabricBlockSettings.of(Material.DECORATION).noCollision().strength(0.5f).sounds(BlockSoundGroup.WOOD));
	public static final Block VENUS_FENCE_GATE = new FenceGateBlock(FabricBlockSettings.of(Material.WOOD, VENUS_PLANKS.getDefaultMapColor()).strength(2.0f, 3.0f).sounds(BlockSoundGroup.WOOD));
	public static final Block VENUS_FENCE = new FenceBlock(FabricBlockSettings.of(Material.WOOD, VENUS_PLANKS.getDefaultMapColor()).strength(2.0f, 3.0f).sounds(BlockSoundGroup.WOOD));
	public static final Block STRIPPED_VENUS_LOG = new PillarBlock(FabricBlockSettings.of(Material.WOOD, MapColor.PALE_YELLOW).strength(2.0f).sounds(BlockSoundGroup.WOOD));
	public static final Block STRIPPED_VENUS_WOOD = new PillarBlock(FabricBlockSettings.of(Material.WOOD, MapColor.PALE_YELLOW).strength(2.0f).sounds(BlockSoundGroup.WOOD));
	public static final Block SULFUR_TNT = new SulfurTntBlock(FabricBlockSettings.of(Material.TNT).breakInstantly().sounds(BlockSoundGroup.GRASS));
	public static final Block SULFUR = new Block(FabricBlockSettings.of(Material.AGGREGATE, MapColor.YELLOW).strength(0.5f).sounds(BlockSoundGroup.SAND));
	public static final Block EXTRA_TALL_GRASS = new ExtraTallGrassBlock(AbstractBlock.Settings.of(Material.REPLACEABLE_PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS));

	//public static final Block VENUS_SIGN = new SignBlock(FabricBlockSettings.of(Material.WOOD, MapColor.PALE_YELLOW).noCollision().strength(1.0f).sounds(BlockSoundGroup.WOOD), SignType.ACACIA); //TODO: figure out what SignType is
	//public static final Block VENUS_WALL_SIGN = new WallSignBlock(FabricBlockSettings.of(Material.WOOD, MapColor.PALE_YELLOW).noCollision().strength(1.0f).sounds(BlockSoundGroup.WOOD).dropsLike(VENUS_SIGN), SignType.ACACIA);
	public static final Block VENUS_DOOR = new DoorBlock(FabricBlockSettings.of(Material.WOOD, VENUS_PLANKS.getDefaultMapColor()).strength(3.0f).sounds(BlockSoundGroup.WOOD).nonOpaque());
	public static final Block VENUS_SAPLING = new SaplingBlock(new VenusSaplingGenerator(), FabricBlockSettings.of(Material.PLANT).noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.GRASS));

	//public static final Item VENUS_SIGN_ITEM = new SignItem(new FabricItemSettings().maxCount(16).group(ItemGroup.MISC), VENUS_SIGN, VENUS_WALL_SIGN);
	public static final Item VENUS_DOOR_ITEM = new TallBlockItem(VENUS_DOOR, new FabricItemSettings().group(MY_ITEM_GROUP));
	public static final Item EXTRA_TALL_GRASS_ITEM = new ExtraTallGrassBlockItem(EXTRA_TALL_GRASS, new Item.Settings().group(MY_ITEM_GROUP));

	public static final Item SPACESUIT_HELMET = new ArmorItem(CustomArmorMaterial.SPACESUIT, EquipmentSlot.HEAD, new FabricItemSettings().group(MY_ITEM_GROUP));
	public static final Item BOTTLED_AIR = new BottledAirItem(new FabricItemSettings().maxDamage(180).group(MY_ITEM_GROUP));

	//public static final BoatEntity.Type VENUS_BOAT_TYPE = new BoatEntity.Type(VENUS_PLANKS,"venus");

	//public static final Item VENUS_BOAT = new BoatItem(BoatEntity.Type.SPRUCE, new FabricItemSettings().maxCount(1).group(ItemGroup.MISC));

	public static final ExtendedScreenHandlerType<RocketScreenHandler> ROCKET_SCREEN_HANDLER = new ExtendedScreenHandlerType<>(RocketScreenHandler::new);

	@Override
	public void onInitialize() {
		Registry.register(Registry.SCREEN_HANDLER, id("rocket"), ROCKET_SCREEN_HANDLER);
		ROCKET_ENTITY = Registry.register(
				Registry.ENTITY_TYPE,
				id("rocket"),
				FabricEntityTypeBuilder.create()
						.entityFactory(RocketEntity::new)
						.dimensions(EntityDimensions.fixed(1.0f, 3.0f))
						.build()
		);
		SULFUR_TNT_ENTITY = Registry.register(
				Registry.ENTITY_TYPE,
				id("sulfur_tnt"),
				FabricEntityTypeBuilder.create()
						.entityFactory((EntityType.EntityFactory<SulfurTntEntity>)SulfurTntEntity::new)
						.fireImmune()
						.dimensions(EntityDimensions.fixed(0.98f, 0.98f))
						.trackRangeChunks(10)
						.trackedUpdateRate(10)
						.build()
		);
		SULFUR_CREEPER_ENTITY = Registry.register(
				Registry.ENTITY_TYPE,
				id("sulfur_creeper"),
				FabricEntityTypeBuilder.createLiving()
						.spawnGroup(SpawnGroup.MONSTER)
						.entityFactory(SulfurCreeperEntity::new)
						.defaultAttributes(SulfurCreeperEntity::createSulfurCreeperAttributes)
						.dimensions(EntityDimensions.fixed(0.6f, 1.7f))
						.trackRangeChunks(8)
						.build()
		);
		//Tech
		registerBlockAndItem("rocket_nose", ROCKET_NOSE);
		//Moon
		registerBlockAndItem("lunar_soil", LUNAR_SOIL);
		registerBlockAndItem("lunar_rock", LUNAR_ROCK);
		//Venus
		registerBlockAndItem("venus_wood", VENUS_WOOD);
		registerBlockAndItem("venus_leaves", VENUS_LEAVES);
		registerBlockAndItem("venus_planks", VENUS_PLANKS);
		registerBlockAndItem("venus_slab", VENUS_SLAB);
		registerBlockAndItem("venus_stairs", VENUS_STAIRS);
		registerBlockAndItem("venus_log", VENUS_LOG);
		registerBlockAndItem("venus_pressure_plate", VENUS_PRESSURE_PLATE);
		registerBlockAndItem("venus_trapdoor", VENUS_TRAPDOOR);
		registerBlockAndItem("venus_button", VENUS_BUTTON);
		registerBlockAndItem("venus_fence_gate", VENUS_FENCE_GATE);
		registerBlockAndItem("venus_fence", VENUS_FENCE);
		registerBlockAndItem("stripped_venus_log", STRIPPED_VENUS_LOG);
		registerBlockAndItem("stripped_venus_wood", STRIPPED_VENUS_WOOD);

		registerBlockAndItem("venus_sapling", VENUS_SAPLING);

		registerBlockAndItem("sulfur_tnt", SULFUR_TNT);
		registerBlockAndItem("sulfur", SULFUR);

		//Registry.register(Registry.BLOCK, id("venus_sign"), VENUS_SIGN);
		//Registry.register(Registry.BLOCK, id("venus_wall_sign"), VENUS_WALL_SIGN);
		Registry.register(Registry.BLOCK, id("venus_door"), VENUS_DOOR);
		Registry.register(Registry.BLOCK, id("extra_tall_grass"), EXTRA_TALL_GRASS);

		//Registry.register(Registry.ITEM, id("venus_sign"), VENUS_SIGN_ITEM);
		Registry.register(Registry.ITEM, id("venus_door"), VENUS_DOOR_ITEM);
		Registry.register(Registry.ITEM, id("extra_tall_grass"), EXTRA_TALL_GRASS_ITEM);
		Registry.register(Registry.ITEM, id("copper_spacesuit_helmet"), SPACESUIT_HELMET);
		Registry.register(Registry.ITEM, id("bottled_air"), BOTTLED_AIR);

		Registry.register(Registry.FEATURE, id("fractal_star"), new FractalStarFeature(FractalStarFeatureConfig.CODEC));
		Registry.register(Registry.FEATURE, id("boulder"), new BoulderFeature(BoulderFeatureConfig.CODEC));

		TARGET_DIMENSION_LIST.add(World.OVERWORLD);
		TARGET_DIMENSION_LIST.add(MOON);
		TARGET_DIMENSION_LIST.add(VENUS);
	}
	private void registerBlockAndItem(String name, Block block){
		Registry.register(Registry.BLOCK, id(name), block);
		Registry.register(Registry.ITEM, id(name), new BlockItem(block, new FabricItemSettings().group(MY_ITEM_GROUP)));
	}

	public static Identifier id(String s){
		return new Identifier(MOD_ID, s);
	}

	public static boolean inVacuum(Entity entity){
		if (entity.world == null) return false;
		if (PlanetProperties.hasAtmosphere(entity.world.getRegistryKey().getValue())) return false;
		if (entity.getVehicle() instanceof RocketEntity) return false;
		return true;
	}
}
