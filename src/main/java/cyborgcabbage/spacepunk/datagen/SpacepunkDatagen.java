package cyborgcabbage.spacepunk.datagen;

import com.google.common.collect.Lists;
import cyborgcabbage.spacepunk.Spacepunk;
import cyborgcabbage.spacepunk.block.OxygenBlock;
import cyborgcabbage.spacepunk.block.SulfurTntBlock;
import cyborgcabbage.spacepunk.util.BuildRocketCriterion;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.*;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.*;
import net.minecraft.block.Block;
import net.minecraft.data.client.*;
import net.minecraft.data.family.BlockFamilies;
import net.minecraft.data.family.BlockFamily;
import net.minecraft.data.server.BlockLootTableGenerator;
import net.minecraft.data.server.RecipeProvider;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.SetNbtLootFunction;
import net.minecraft.loot.provider.nbt.LootNbtProvider;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtString;
import net.minecraft.predicate.BlockPredicate;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.predicate.entity.EntityEquipmentPredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.common.item.PatchouliItems;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class SpacepunkDatagen implements DataGeneratorEntrypoint {

    public static final BlockFamily VENUS = BlockFamilies.register(Spacepunk.VENUS_PLANKS)
            .button(Spacepunk.VENUS_BUTTON)
            .fence(Spacepunk.VENUS_FENCE)
            .fenceGate(Spacepunk.VENUS_FENCE_GATE)
            .pressurePlate(Spacepunk.VENUS_PRESSURE_PLATE)
            .slab(Spacepunk.VENUS_SLAB)
            .stairs(Spacepunk.VENUS_STAIRS)
            .door(Spacepunk.VENUS_DOOR)
            .trapdoor(Spacepunk.VENUS_TRAPDOOR)
            .group("wooden")
            .unlockCriterionName("has_planks")
            .build();

    public static final BlockFamily LUNAR_ROCK = BlockFamilies.register(Spacepunk.LUNAR_ROCK)
            .slab(Spacepunk.LUNAR_ROCK_SLAB)
            .stairs(Spacepunk.LUNAR_ROCK_STAIRS)
            .build();

    public static final BlockFamily LUNAR_BRICK = BlockFamilies.register(Spacepunk.LUNAR_BRICKS)
            .wall(Spacepunk.LUNAR_BRICK_WALL)
            .stairs(Spacepunk.LUNAR_BRICK_STAIRS)
            .slab(Spacepunk.LUNAR_BRICK_SLAB)
            .chiseled(Spacepunk.CHISELED_LUNAR_BRICKS)
            .cracked(Spacepunk.CRACKED_LUNAR_BRICKS)
            .build();

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator gen) {
        var blockTag = new BlockTagGenerator(gen);
        gen.addProvider(blockTag);
        gen.addProvider(new ItemTagGenerator(gen, blockTag));
        gen.addProvider(new ModelGenerator(gen));
        gen.addProvider(new BlockLootGenerator(gen));
        gen.addProvider(new RecipeGenerator(gen));
        gen.addProvider(new AdvancementGenerator(gen));
        gen.addProvider(new RewardLootGenerator(gen));
    }

    private static class RewardLootGenerator extends SimpleFabricLootTableProvider {

        public RewardLootGenerator(FabricDataGenerator dataGenerator) {
            super(dataGenerator, LootContextTypes.ADVANCEMENT_REWARD);
        }

        @Override
        public void accept(BiConsumer<Identifier, LootTable.Builder> consumer) {
            NbtCompound nbt = new NbtCompound();
            nbt.put("patchouli:book", NbtString.of("spacepunk:rocketry_guide"));
            consumer.accept(Spacepunk.id("get_rocketry_guide"), LootTable.builder().pool(
                    LootPool.builder().with(ItemEntry.builder(PatchouliItems.BOOK).apply(SetNbtLootFunction.builder(nbt)))
            ));
        }
    }

    private static class AdvancementGenerator extends FabricAdvancementProvider {
        private final List<Consumer<Consumer<Advancement>>> list = Util.make(Lists.newArrayList(), list -> {
            list.add(new CustomAdvancementsGenerator());
        });

        protected AdvancementGenerator(FabricDataGenerator dataGenerator) {
            super(dataGenerator);
        }

        @Override
        public void generateAdvancement(Consumer<Advancement> consumer) {
            list.forEach(i -> i.accept(consumer));
        }
    }

    private static class CustomAdvancementsGenerator implements Consumer<Consumer<Advancement>> {

        @Override
        public void accept(Consumer<Advancement> consumer) {
            Identifier tab = Spacepunk.id("rocketry");
            String aPath = "advancement.spacepunk.rocketry";
            Advancement root = Advancement.Builder.create()
                    .display(Items.BOOK, Text.translatable(aPath+".title"),
                            Text.translatable(aPath+".description"),
                            new Identifier("textures/block/copper_block.png"),
                            AdvancementFrame.TASK,
                            false,
                            false,
                            false)
                    .criterion("tick", new TickCriterion.Conditions(Criteria.TICK.getId(), EntityPredicate.Extended.EMPTY))
                    .rewards(AdvancementRewards.Builder.loot(Spacepunk.id("get_rocketry_guide")))
                    .build(consumer, tab+"/root");

            Advancement buildRocket = Advancement.Builder.create()
                    .parent(root)
                    .display(Spacepunk.ROCKET_NOSE, Text.translatable(aPath+".build_rocket.title"),
                            Text.translatable(aPath+".build_rocket.description"),
                            null,
                            AdvancementFrame.TASK,
                            true,
                            true,
                            false)
                    .criterion("build_rocket", new BuildRocketCriterion.Conditions())
                    .build(consumer, tab+"/build_rocket");

            Advancement moon = Advancement.Builder.create()
                    .parent(buildRocket)
                    .display(Spacepunk.LUNAR_SOIL, Text.translatable(aPath+".moon.title"),
                            Text.translatable(aPath+".moon.description"),
                            null,
                            AdvancementFrame.TASK,
                            true,
                            true,
                            false)
                    .criterion("step_on_lunar_soil", TickCriterion.Conditions.createLocation(EntityPredicate.Builder.create().steppingOn(LocationPredicate.Builder.create().block(BlockPredicate.Builder.create().blocks(Spacepunk.LUNAR_SOIL).build()).build()).build()))
                    .build(consumer, tab+"/moon");
        }
    }

    private static class RecipeGenerator extends FabricRecipeProvider {

        public RecipeGenerator(FabricDataGenerator dataGenerator) {
            super(dataGenerator);
        }

        @Override
        protected void generateRecipes(Consumer<RecipeJsonProvider> exporter) {
            RecipeProvider.generateFamily(exporter, VENUS);
            RecipeProvider.generateFamily(exporter, LUNAR_ROCK);
            RecipeProvider.generateFamily(exporter, LUNAR_BRICK);
            RecipeProvider.offerPlanksRecipe2(exporter, Spacepunk.VENUS_PLANKS, ItemTagGenerator.VENUS_LOGS);
            RecipeProvider.offerBarkBlockRecipe(exporter, Spacepunk.VENUS_WOOD, Spacepunk.VENUS_LOG);
            RecipeProvider.offerBarkBlockRecipe(exporter, Spacepunk.STRIPPED_VENUS_WOOD, Spacepunk.STRIPPED_VENUS_LOG);
            ShapedRecipeJsonBuilder.create(Spacepunk.LUNAR_BRICKS, 4).input('#', Spacepunk.LUNAR_ROCK).pattern("##").pattern("##").criterion("has_lunar_rock", RecipeProvider.conditionsFromItem(Spacepunk.LUNAR_ROCK)).offerTo(exporter);
            //RecipeProvider.createStairsRecipe(Spacepunk.LUNAR_BRICK_STAIRS, Ingredient.ofItems(Spacepunk.LUNAR_BRICKS)).criterion("has_lunar_bricks", RecipeProvider.conditionsFromItem(Spacepunk.LUNAR_BRICKS)).offerTo(exporter);
            //RecipeProvider.create
        }
    }
    
    private static class BlockLootGenerator extends FabricBlockLootTableProvider {

        private static final float[] SAPLING_DROP_CHANCE = new float[]{0.05f, 0.0625f, 0.083333336f, 0.1f};

        protected BlockLootGenerator(FabricDataGenerator dataGenerator) {
            super(dataGenerator);
        }

        @Override
        protected void generateBlockLootTables() {
            addDrop(Spacepunk.LUNAR_SOIL);

            addDrop(Spacepunk.LUNAR_ROCK);
            addDrop(Spacepunk.LUNAR_ROCK_SLAB, BlockLootTableGenerator::slabDrops);
            addDrop(Spacepunk.LUNAR_ROCK_STAIRS);

            addDrop(Spacepunk.LUNAR_BRICKS);
            addDrop(Spacepunk.LUNAR_BRICK_WALL);
            addDrop(Spacepunk.CHISELED_LUNAR_BRICKS);
            addDrop(Spacepunk.CRACKED_LUNAR_BRICKS);
            addDrop(Spacepunk.LUNAR_BRICK_SLAB, BlockLootTableGenerator::slabDrops);
            addDrop(Spacepunk.LUNAR_BRICK_STAIRS);

            addDrop(Spacepunk.ROCKET_NOSE);

            addDrop(Spacepunk.VENUS_PLANKS);
            addDrop(Spacepunk.VENUS_SAPLING);
            addDrop(Spacepunk.VENUS_LOG);
            addDrop(Spacepunk.VENUS_WOOD);
            addDrop(Spacepunk.STRIPPED_VENUS_LOG);
            addDrop(Spacepunk.STRIPPED_VENUS_WOOD);
            addDrop(Spacepunk.VENUS_PRESSURE_PLATE);
            addDrop(Spacepunk.VENUS_TRAPDOOR);
            addDrop(Spacepunk.VENUS_BUTTON);
            addDrop(Spacepunk.VENUS_STAIRS);
            addDrop(Spacepunk.VENUS_FENCE_GATE);
            addDrop(Spacepunk.VENUS_FENCE);
            addDrop(Spacepunk.VENUS_SLAB, BlockLootTableGenerator::slabDrops);
            addDrop(Spacepunk.VENUS_DOOR, BlockLootTableGenerator::doorDrops);
            addDrop(Spacepunk.VENUS_LEAVES, (Block block) -> BlockLootTableGenerator.leavesDrop(block, Spacepunk.VENUS_SAPLING, SAPLING_DROP_CHANCE));

            addDrop(Spacepunk.EXTRA_TALL_GRASS, BlockLootTableGenerator::grassDrops);
            addDrop(Spacepunk.SULFUR);
            addDrop(Spacepunk.OXYGEN, dropsNothing());
            addSulfurTntDrop();
        }

        private void addSulfurTntDrop() {
            var dontDropIfUnstable = BlockStatePropertyLootCondition
                    .builder(Spacepunk.SULFUR_TNT)
                    .properties(StatePredicate.Builder.create().exactMatch(SulfurTntBlock.UNSTABLE, false));
            var lootPool = LootPool.builder().with(ItemEntry.builder(Spacepunk.SULFUR_TNT).conditionally(dontDropIfUnstable));
            addDrop(Spacepunk.SULFUR_TNT, LootTable.builder().pool(BlockLootTableGenerator.addSurvivesExplosionCondition(Spacepunk.SULFUR_TNT, lootPool)));
        }
    }

    private static class ModelGenerator extends FabricModelProvider {

        public ModelGenerator(FabricDataGenerator dataGenerator) {
            super(dataGenerator);
        }

        private void registerExtraTallGrass(BlockStateModelGenerator gen) {
            var block = Spacepunk.EXTRA_TALL_GRASS;
            var tint = BlockStateModelGenerator.TintType.TINTED;
            gen.registerItemModel(block, "_top");
            Identifier top = gen.createSubModel(block, "_top", tint.getCrossModel(), TextureMap::cross);
            Identifier middle = gen.createSubModel(block, "_middle", tint.getCrossModel(), TextureMap::cross);
            gen.blockStateCollector
                    .accept(VariantsBlockStateSupplier.create(block)
                            .coordinate(BlockStateModelGenerator.createBooleanModelMap(Properties.UP,top,middle)));
        }

        private void registerDebugOxygen(BlockStateModelGenerator gen) {
            BlockStateVariantMap b = BlockStateVariantMap.create(OxygenBlock.PRESSURE).register(
                    pressure -> BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Spacepunk.OXYGEN, "_" + pressure))
            );
            gen.blockStateCollector.accept(VariantsBlockStateSupplier.create(Spacepunk.OXYGEN).coordinate(b));
        }

        public final void registerPressureGauge(ItemModelGenerator gen) {
            for (int i = 0; i <= 8; ++i) {
                gen.register(Spacepunk.PRESSURE_GAUGE, String.format("_%d", i), Models.GENERATED);
            }
        }

        @Override
        public void generateBlockStateModels(BlockStateModelGenerator gen) {
            gen.registerCubeAllModelTexturePool(VENUS.getBaseBlock()).family(VENUS);
            gen.registerCubeAllModelTexturePool(LUNAR_ROCK.getBaseBlock()).family(LUNAR_ROCK);
            gen.registerCubeAllModelTexturePool(LUNAR_BRICK.getBaseBlock()).family(LUNAR_BRICK);
            gen.registerSimpleCubeAll(Spacepunk.LUNAR_SOIL);
            gen.registerSimpleCubeAll(Spacepunk.SULFUR);
            gen.registerSingleton(Spacepunk.SULFUR_TNT, TexturedModel.CUBE_BOTTOM_TOP);
            gen.registerLog(Spacepunk.VENUS_LOG).log(Spacepunk.VENUS_LOG).wood(Spacepunk.VENUS_WOOD);
            gen.registerLog(Spacepunk.STRIPPED_VENUS_LOG).log(Spacepunk.STRIPPED_VENUS_LOG).wood(Spacepunk.STRIPPED_VENUS_WOOD);
            gen.registerTintableCross(Spacepunk.VENUS_SAPLING, BlockStateModelGenerator.TintType.NOT_TINTED);
            gen.registerSingleton(Spacepunk.VENUS_LEAVES, TexturedModel.LEAVES);
            gen.registerSimpleState(Spacepunk.ROCKET_NOSE);
            registerExtraTallGrass(gen);
            registerDebugOxygen(gen);
        }

        @Override
        public void generateItemModels(ItemModelGenerator gen) {
            gen.register(Spacepunk.SPACESUIT_HELMET, Models.GENERATED);
            gen.register(Spacepunk.BOTTLED_AIR, Models.GENERATED);
            registerPressureGauge(gen);
        }
    }

    private static class BlockTagGenerator extends FabricTagProvider.BlockTagProvider {

        public static final TagKey<Block> VENUS_LOGS = TagKey.of(Registry.BLOCK_KEY, Spacepunk.id("venus_logs"));

        public BlockTagGenerator(FabricDataGenerator dataGenerator) {
            super(dataGenerator);
        }

        @Override
        protected void generateTags() {
            q(VENUS_LOGS,
                    Spacepunk.VENUS_LOG,
                    Spacepunk.STRIPPED_VENUS_LOG,
                    Spacepunk.VENUS_WOOD,
                    Spacepunk.STRIPPED_VENUS_WOOD);
            q(BlockTags.STONE_ORE_REPLACEABLES, Spacepunk.LUNAR_ROCK);
            q(BlockTags.LEAVES, Spacepunk.VENUS_LEAVES);
            q(BlockTags.SLABS,
                    Spacepunk.LUNAR_ROCK_SLAB,
                    Spacepunk.LUNAR_BRICK_SLAB);
            q(BlockTags.STAIRS,
                    Spacepunk.LUNAR_ROCK_STAIRS,
                    Spacepunk.LUNAR_BRICK_STAIRS);
            q(BlockTags.WALLS, Spacepunk.LUNAR_BRICK_WALL);
            q(BlockTags.AXE_MINEABLE);
            q(BlockTags.HOE_MINEABLE, Spacepunk.VENUS_LEAVES);
            q(BlockTags.PICKAXE_MINEABLE,
                    Spacepunk.LUNAR_ROCK,
                    Spacepunk.LUNAR_ROCK_STAIRS,
                    Spacepunk.LUNAR_ROCK_SLAB,
                    Spacepunk.LUNAR_BRICKS,
                    Spacepunk.ROCKET_NOSE,
                    Spacepunk.LUNAR_BRICK_STAIRS,
                    Spacepunk.LUNAR_BRICK_SLAB,
                    Spacepunk.LUNAR_BRICK_WALL,
                    Spacepunk.CHISELED_LUNAR_BRICKS,
                    Spacepunk.CRACKED_LUNAR_BRICKS);
            q(BlockTags.SHOVEL_MINEABLE, Spacepunk.LUNAR_SOIL);
            q(BlockTags.ENDERMAN_HOLDABLE, Spacepunk.LUNAR_SOIL);
            q(BlockTags.FENCE_GATES, Spacepunk.VENUS_FENCE_GATE);
            q(BlockTags.LOGS_THAT_BURN, VENUS_LOGS);
            q(BlockTags.NEEDS_STONE_TOOL, Spacepunk.ROCKET_NOSE);
            q(BlockTags.PLANKS, Spacepunk.VENUS_PLANKS);
            q(BlockTags.SAPLINGS, Spacepunk.VENUS_SAPLING);
            q(BlockTags.WOODEN_BUTTONS, Spacepunk.VENUS_BUTTON);
            q(BlockTags.WOODEN_DOORS, Spacepunk.VENUS_DOOR);
            q(BlockTags.WOODEN_FENCES, Spacepunk.VENUS_FENCE);
            q(BlockTags.WOODEN_SLABS, Spacepunk.VENUS_SLAB);
            q(BlockTags.WOODEN_STAIRS, Spacepunk.VENUS_STAIRS);
            q(BlockTags.WOODEN_PRESSURE_PLATES, Spacepunk.VENUS_PRESSURE_PLATE);
            q(BlockTags.WOODEN_TRAPDOORS, Spacepunk.VENUS_TRAPDOOR);
        }

        private void q(net.minecraft.tag.TagKey<Block> tag, Block... blocks){
            if(blocks.length > 0) {
                getOrCreateTagBuilder(tag).add(blocks);
            }
        }
        private void q(net.minecraft.tag.TagKey<Block> tag, TagKey<Block> blocks){
            getOrCreateTagBuilder(tag).addTag(blocks);
        }
    }

    private static class ItemTagGenerator extends FabricTagProvider.ItemTagProvider {

        public static final TagKey<Item> VENUS_LOGS = TagKey.of(Registry.ITEM_KEY, Spacepunk.id("venus_logs"));

        public ItemTagGenerator(FabricDataGenerator dataGenerator, BlockTagProvider blockTagProvider) {
            super(dataGenerator, blockTagProvider);
        }

        @Override
        protected void generateTags() {
            copy(BlockTagGenerator.VENUS_LOGS, VENUS_LOGS);
            copy(BlockTags.LEAVES, ItemTags.LEAVES);
            copy(BlockTags.LEAVES, ItemTags.LEAVES);
            copy(BlockTags.LOGS_THAT_BURN, ItemTags.LOGS_THAT_BURN);
            copy(BlockTags.PLANKS, ItemTags.PLANKS);
            copy(BlockTags.SAPLINGS, ItemTags.SAPLINGS);
            copy(BlockTags.WOODEN_BUTTONS, ItemTags.WOODEN_BUTTONS);
            copy(BlockTags.WOODEN_DOORS, ItemTags.WOODEN_DOORS);
            copy(BlockTags.WOODEN_FENCES, ItemTags.WOODEN_FENCES);
            copy(BlockTags.WOODEN_PRESSURE_PLATES, ItemTags.WOODEN_PRESSURE_PLATES);
            copy(BlockTags.WOODEN_SLABS, ItemTags.WOODEN_SLABS);
            copy(BlockTags.WOODEN_STAIRS, ItemTags.WOODEN_STAIRS);
            copy(BlockTags.WOODEN_TRAPDOORS, ItemTags.WOODEN_TRAPDOORS);
            copy(BlockTags.SLABS, ItemTags.SLABS);
            copy(BlockTags.STAIRS, ItemTags.STAIRS);
            copy(BlockTags.WALLS, ItemTags.WALLS);
        }
    }
}