package cyborgcabbage.spacepunk;

import cyborgcabbage.spacepunk.armor.MyArmorMaterials;
import cyborgcabbage.spacepunk.block.*;
import cyborgcabbage.spacepunk.entity.RocketEntity;
import cyborgcabbage.spacepunk.entity.SulfurCreeperEntity;
import cyborgcabbage.spacepunk.entity.SulfurTntEntity;
import cyborgcabbage.spacepunk.feature.BoulderFeature;
import cyborgcabbage.spacepunk.feature.BoulderFeatureConfig;
import cyborgcabbage.spacepunk.feature.FractalStarFeature;
import cyborgcabbage.spacepunk.feature.FractalStarFeatureConfig;
import cyborgcabbage.spacepunk.gen.beta.BetaChunkGenerator;
import cyborgcabbage.spacepunk.inventory.RocketScreenHandler;
import cyborgcabbage.spacepunk.item.BottledAirItem;
import cyborgcabbage.spacepunk.item.ExtraTallGrassBlockItem;
import cyborgcabbage.spacepunk.item.GuideBookItem;
import cyborgcabbage.spacepunk.util.BuildRocketCriterion;
import cyborgcabbage.spacepunk.util.PlanetProperties;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.*;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vazkii.patchouli.api.IMultiblock;
import vazkii.patchouli.api.PatchouliAPI;

import java.util.ArrayList;

public class Spacepunk implements ModInitializer {
	public static final String MOD_ID = "spacepunk";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final DamageSource VACUUM = new DamageSource("vacuum").setBypassesArmor();

	public static ArrayList<RegistryKey<World>> TARGET_DIMENSION_LIST = new ArrayList<>();

	public static final RegistryKey<World> MOON = RegistryKey.of(Registry.WORLD_KEY, id("moon"));
	public static final RegistryKey<World> VENUS = RegistryKey.of(Registry.WORLD_KEY, id("venus"));

	public static EntityType<RocketEntity> ROCKET_ENTITY;
	public static EntityType<SulfurTntEntity> SULFUR_TNT_ENTITY;
	public static EntityType<SulfurCreeperEntity> SULFUR_CREEPER_ENTITY;

	public static final Block OXYGEN = new OxygenBlock(FabricBlockSettings.of(Material.AIR).noCollision().dropsNothing().air().ticksRandomly());

	public static final Block ROCKET_NOSE = new RocketNoseBlock(FabricBlockSettings.of(Material.METAL, MapColor.ORANGE).requiresTool().strength(3.0f, 6.0f).sounds(BlockSoundGroup.COPPER));
	public static final ItemGroup MY_ITEM_GROUP = FabricItemGroupBuilder.build(id("spacepunk"), () -> new ItemStack(ROCKET_NOSE));
	//Moon
	public static final Block LUNAR_SOIL = new FallingBlock(FabricBlockSettings.of(Material.SOIL, MapColor.LIGHT_GRAY).strength(0.5f).sounds(BlockSoundGroup.GRAVEL));
	public static final Block LUNAR_ROCK = new Block(FabricBlockSettings.of(Material.STONE, MapColor.STONE_GRAY).requiresTool().strength(1.5f, 6.0f));
	public static final Block LUNAR_ROCK_STAIRS = new StairsBlock(LUNAR_ROCK.getDefaultState(), FabricBlockSettings.copy(LUNAR_ROCK));
	public static final Block LUNAR_ROCK_SLAB = new SlabBlock(FabricBlockSettings.of(Material.STONE, MapColor.STONE_GRAY).requiresTool().strength(2.0f, 6.0f));
	public static final Block LUNAR_BRICKS = new Block(FabricBlockSettings.of(Material.STONE).requiresTool().strength(1.5f, 6.0f));
	public static final Block LUNAR_BRICK_STAIRS = new StairsBlock(LUNAR_BRICKS.getDefaultState(), FabricBlockSettings.copy(LUNAR_BRICKS));
	public static final Block LUNAR_BRICK_SLAB = new SlabBlock(FabricBlockSettings.of(Material.STONE, MapColor.STONE_GRAY).requiresTool().strength(2.0f, 6.0f));
	public static final Block LUNAR_BRICK_WALL =  new WallBlock(FabricBlockSettings.copy(LUNAR_BRICKS));
	public static final Block CHISELED_LUNAR_BRICKS =  new Block(FabricBlockSettings.of(Material.STONE).requiresTool().strength(1.5f, 6.0f));
	public static final Block CRACKED_LUNAR_BRICKS =  new Block(FabricBlockSettings.of(Material.STONE).requiresTool().strength(1.5f, 6.0f));
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
	public static final Block EXTRA_TALL_GRASS = new ExtraTallGrassBlock(FabricBlockSettings.of(Material.REPLACEABLE_PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS));

	public static final Block VENUS_DOOR = new DoorBlock(FabricBlockSettings.of(Material.WOOD, VENUS_PLANKS.getDefaultMapColor()).strength(3.0f).sounds(BlockSoundGroup.WOOD).nonOpaque());
	public static final Block VENUS_SAPLING = new SaplingBlock(new VenusSaplingGenerator(), FabricBlockSettings.of(Material.PLANT).noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.GRASS));

	public static final Item VENUS_DOOR_ITEM = new TallBlockItem(VENUS_DOOR, new FabricItemSettings().group(MY_ITEM_GROUP));
	public static final Item EXTRA_TALL_GRASS_ITEM = new ExtraTallGrassBlockItem(EXTRA_TALL_GRASS, new Item.Settings().group(MY_ITEM_GROUP));

	public static final Item SPACESUIT_HELMET = new ArmorItem(MyArmorMaterials.SPACESUIT, EquipmentSlot.HEAD, new FabricItemSettings().group(MY_ITEM_GROUP));
	public static final Item BOTTLED_AIR = new BottledAirItem(new FabricItemSettings().maxDamage(180).group(MY_ITEM_GROUP));
	public static final Item PRESSURE_GAUGE = new Item(new FabricItemSettings().group(MY_ITEM_GROUP));

	public static final Item ROCKETRY_GUIDE = new GuideBookItem(id("rocketry_guide"), new FabricItemSettings().group(MY_ITEM_GROUP));
	//public static final Item ROCKETRY_GUIDE = new Item(new FabricItemSettings().group(MY_ITEM_GROUP));

	public static final ExtendedScreenHandlerType<RocketScreenHandler> ROCKET_SCREEN_HANDLER = new ExtendedScreenHandlerType<>(RocketScreenHandler::new);

	public static final IMultiblock ROCKET_MULTIBLOCK = PatchouliAPI.get().makeMultiblock(new String[][]{
			{"N"},
			{"C"},
			{"C"},
			{"0"},
	}, 'N', ROCKET_NOSE, 'C', Blocks.COPPER_BLOCK, '0', Blocks.BLAST_FURNACE).setSymmetrical(true);

	public static final BuildRocketCriterion BUILD_ROCKET = Criteria.register(new BuildRocketCriterion());

	public static final Identifier ROCKET_LAUNCH_SOUND_ID = id("rocket_launch");
	public static SoundEvent ROCKET_LAUNCH_SOUND_EVENT = new SoundEvent(ROCKET_LAUNCH_SOUND_ID);

	@Override
	public void onInitialize() {
		EntitySleepEvents.ALLOW_SLEEPING.register((player, sleepingPos) -> {
			if(PlanetProperties.getTimeDivisor(player.world.getRegistryKey().getValue()) != 1){
				if(!player.world.isClient)
					player.sendMessage(Text.translatable("bed.spacepunk.cant_sleep"), true);
				return PlayerEntity.SleepFailureReason.NOT_POSSIBLE_HERE;
			}else {
				return null;
			}
		});

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

		//Book
		Registry.register(Registry.ITEM, id("rocketry_guide"), ROCKETRY_GUIDE);

		//Tech
		registerBlockAndItem("rocket_nose", ROCKET_NOSE);
		Registry.register(Registry.ITEM, id("spacesuit_helmet"), SPACESUIT_HELMET);
		Registry.register(Registry.ITEM, id("bottled_air"), BOTTLED_AIR);
		Registry.register(Registry.ITEM, id("pressure_gauge"), PRESSURE_GAUGE);

		//Moon
		registerBlockAndItem("lunar_soil", LUNAR_SOIL);

		registerBlockAndItem("lunar_rock", LUNAR_ROCK);
		registerBlockAndItem("lunar_rock_stairs", LUNAR_ROCK_STAIRS);
		registerBlockAndItem("lunar_rock_slab", LUNAR_ROCK_SLAB);

		registerBlockAndItem("lunar_bricks", LUNAR_BRICKS);
		registerBlockAndItem("lunar_brick_stairs", LUNAR_BRICK_STAIRS);
		registerBlockAndItem("lunar_brick_slab", LUNAR_BRICK_SLAB);
		registerBlockAndItem("cracked_lunar_bricks", CRACKED_LUNAR_BRICKS);
		registerBlockAndItem("chiseled_lunar_bricks", CHISELED_LUNAR_BRICKS);
		registerBlockAndItem("lunar_brick_wall", LUNAR_BRICK_WALL);
		//Venus
		registerBlockAndItem("venus_wood", VENUS_WOOD);
		registerBlockAndItem("venus_leaves", VENUS_LEAVES);
		registerBlockAndItem("venus_planks", VENUS_PLANKS);
		registerBlockAndItem("venus_slab", VENUS_SLAB);
		registerBlockAndItem("venus_stairs", VENUS_STAIRS);
		registerBlockAndItem("venus_log", VENUS_LOG);
		registerBlockAndItem("venus_pressure_plate", VENUS_PRESSURE_PLATE);
		registerBlockAndItem("venus_trapdoor", VENUS_TRAPDOOR);
		Registry.register(Registry.ITEM, id("venus_door"), VENUS_DOOR_ITEM);
		registerBlockAndItem("venus_button", VENUS_BUTTON);
		registerBlockAndItem("venus_fence_gate", VENUS_FENCE_GATE);
		registerBlockAndItem("venus_fence", VENUS_FENCE);
		registerBlockAndItem("stripped_venus_log", STRIPPED_VENUS_LOG);
		registerBlockAndItem("stripped_venus_wood", STRIPPED_VENUS_WOOD);
		registerBlockAndItem("venus_sapling", VENUS_SAPLING);
		Registry.register(Registry.ITEM, id("extra_tall_grass"), EXTRA_TALL_GRASS_ITEM);

		registerBlockAndItem("sulfur", SULFUR);
		registerBlockAndItem("sulfur_tnt", SULFUR_TNT);

		Registry.register(Registry.BLOCK, id("oxygen"), OXYGEN);
		Registry.register(Registry.BLOCK, id("venus_door"), VENUS_DOOR);
		Registry.register(Registry.BLOCK, id("extra_tall_grass"), EXTRA_TALL_GRASS);

		Registry.register(Registry.FEATURE, id("fractal_star"), new FractalStarFeature(FractalStarFeatureConfig.CODEC));
		Registry.register(Registry.FEATURE, id("boulder"), new BoulderFeature(BoulderFeatureConfig.CODEC));

		TARGET_DIMENSION_LIST.add(World.OVERWORLD);
		TARGET_DIMENSION_LIST.add(MOON);

		PatchouliAPI.get().registerMultiblock(id("rocket_mk1"), ROCKET_MULTIBLOCK);

		Registry.register(Registry.SOUND_EVENT, ROCKET_LAUNCH_SOUND_ID, ROCKET_LAUNCH_SOUND_EVENT);

		Registry.register(Registry.CHUNK_GENERATOR, id("beta"), BetaChunkGenerator.CODEC);
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
		if (OxygenBlock.getPressure(entity.world, new BlockPos(entity.getEyePos())) >= 4) return false;
		return true;
	}
}
