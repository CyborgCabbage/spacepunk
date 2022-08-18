package cyborgcabbage.spacepunk.datagen;

import cyborgcabbage.spacepunk.Spacepunk;
import cyborgcabbage.spacepunk.block.OxygenBlock;
import cyborgcabbage.spacepunk.block.SulfurTntBlock;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Block;
import net.minecraft.data.client.*;
import net.minecraft.data.family.BlockFamilies;
import net.minecraft.data.family.BlockFamily;
import net.minecraft.data.server.BlockLootTableGenerator;
import net.minecraft.data.server.RecipeProvider;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.Item;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

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
    
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator gen) {
        var blockTag = new BlockTagGenerator(gen);
        var itemTag = new ItemTagGenerator(gen, blockTag);
        var model = new ModelGenerator(gen);
        var loot = new LootGenerator(gen);
        var recipe = new RecipeGenerator(gen);
        gen.addProvider(blockTag);
        gen.addProvider(itemTag);
        gen.addProvider(model);
        gen.addProvider(loot);
        gen.addProvider(recipe);
    }
    
    private static class RecipeGenerator extends FabricRecipeProvider {

        public RecipeGenerator(FabricDataGenerator dataGenerator) {
            super(dataGenerator);
        }

        @Override
        protected void generateRecipes(Consumer<RecipeJsonProvider> exporter) {
            RecipeProvider.generateFamily(exporter, VENUS);
            RecipeProvider.offerPlanksRecipe2(exporter, Spacepunk.VENUS_PLANKS, ItemTagGenerator.VENUS_LOGS);
            RecipeProvider.offerBarkBlockRecipe(exporter, Spacepunk.VENUS_WOOD, Spacepunk.VENUS_LOG);
            RecipeProvider.offerBarkBlockRecipe(exporter, Spacepunk.STRIPPED_VENUS_WOOD, Spacepunk.STRIPPED_VENUS_LOG);
        }
    }
    
    private static class LootGenerator extends FabricBlockLootTableProvider {

        private static final float[] SAPLING_DROP_CHANCE = new float[]{0.05f, 0.0625f, 0.083333336f, 0.1f};

        protected LootGenerator(FabricDataGenerator dataGenerator) {
            super(dataGenerator);
        }

        @Override
        protected void generateBlockLootTables() {
            addDrop(Spacepunk.LUNAR_SOIL);
            addDrop(Spacepunk.LUNAR_ROCK);
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
            addDrop(Spacepunk.VENUS_DOOR, BlockLootTableGenerator::addDoorDrop);
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
            gen.registerSimpleCubeAll(Spacepunk.LUNAR_SOIL);
            gen.registerSimpleCubeAll(Spacepunk.LUNAR_ROCK);
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
            q(VENUS_LOGS, Spacepunk.VENUS_LOG, Spacepunk.STRIPPED_VENUS_LOG, Spacepunk.VENUS_WOOD, Spacepunk.STRIPPED_VENUS_WOOD);

            q(BlockTags.LEAVES, Spacepunk.VENUS_LEAVES);
            q(BlockTags.AXE_MINEABLE);
            q(BlockTags.HOE_MINEABLE, Spacepunk.VENUS_LEAVES);
            q(BlockTags.PICKAXE_MINEABLE, Spacepunk.LUNAR_ROCK, Spacepunk.ROCKET_NOSE);
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
        }
    }
}
