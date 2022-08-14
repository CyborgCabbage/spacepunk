package cyborgcabbage.spacepunk.datagen;

import cyborgcabbage.spacepunk.Spacepunk;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.block.enums.Tilt;
import net.minecraft.data.client.*;
import net.minecraft.data.family.BlockFamilies;
import net.minecraft.data.family.BlockFamily;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

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
        gen.addProvider(blockTag);
        gen.addProvider(itemTag);
        gen.addProvider(model);
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
        }

        @Override
        public void generateItemModels(ItemModelGenerator gen) {
            gen.register(Spacepunk.SPACESUIT_HELMET, Models.GENERATED);
            gen.register(Spacepunk.BOTTLED_AIR, Models.GENERATED);
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
