package cyborgcabbage.spacepunk.block;

import cyborgcabbage.spacepunk.Spacepunk;
import cyborgcabbage.spacepunk.feature.VenusFoliagePlacer;
import net.minecraft.block.Blocks;
import net.minecraft.block.sapling.LargeTreeSaplingGenerator;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.feature.size.TwoLayersFeatureSize;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.trunk.GiantTrunkPlacer;
import net.minecraft.world.gen.trunk.StraightTrunkPlacer;
import org.jetbrains.annotations.Nullable;

public class VenusSaplingGenerator extends LargeTreeSaplingGenerator {
    public static final RegistryEntry<ConfiguredFeature<TreeFeatureConfig, Feature<TreeFeatureConfig>>> VENUS = RegistryEntry.of(Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, Spacepunk.id("venus_tree"), new ConfiguredFeature<>(Feature.TREE, new TreeFeatureConfig.Builder(
            BlockStateProvider.of(Spacepunk.VENUS_LOG),
            new StraightTrunkPlacer(6, 4, 0),
            BlockStateProvider.of(Spacepunk.VENUS_LEAVES),
            new VenusFoliagePlacer(ConstantIntProvider.create(2), ConstantIntProvider.create(0)),
            new TwoLayersFeatureSize(1, 0, 1)
    ).ignoreVines().build())));

    public static final RegistryEntry<ConfiguredFeature<TreeFeatureConfig, Feature<TreeFeatureConfig>>> MEGA_VENUS = RegistryEntry.of(Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, Spacepunk.id("mega_venus_tree"), new ConfiguredFeature<>(Feature.TREE, new TreeFeatureConfig.Builder(
            BlockStateProvider.of(Spacepunk.VENUS_LOG),
            new GiantTrunkPlacer(19, 2, 2),
            BlockStateProvider.of(Spacepunk.VENUS_LEAVES),
            new VenusFoliagePlacer(ConstantIntProvider.create(2), ConstantIntProvider.create(0)),
            new TwoLayersFeatureSize(1, 1, 2)
    ).ignoreVines().build())));

    public static final RegistryEntry<ConfiguredFeature<TreeFeatureConfig, Feature<TreeFeatureConfig>>> FLAT_VENUS = RegistryEntry.of(Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, Spacepunk.id("flat_venus_tree"), new ConfiguredFeature<>(Feature.TREE, new TreeFeatureConfig.Builder(
            BlockStateProvider.of(Spacepunk.VENUS_LOG),
            new StraightTrunkPlacer(4, 1, 0),
            BlockStateProvider.of(Spacepunk.VENUS_LEAVES),
            new VenusFoliagePlacer(ConstantIntProvider.create(3), ConstantIntProvider.create(0)),
            new TwoLayersFeatureSize(1, 0, 1)
    ).ignoreVines().build())));
    /*
    public static final RegistryEntry<PlacedFeature> VENUS_CHECKED = PlacedFeatures.register("venus_tree_checked", VENUS, PlacedFeatures.wouldSurvive(Spacepunk.VENUS_SAPLING));

    public static final RegistryEntry<PlacedFeature> MEGA_VENUS_CHECKED = PlacedFeatures.register("mega_venus_tree_checked", MEGA_VENUS, PlacedFeatures.wouldSurvive(Spacepunk.VENUS_SAPLING));
    */
    @Nullable
    @Override
    protected RegistryEntry<? extends ConfiguredFeature<?, ?>> getTreeFeature(net.minecraft.util.math.random.Random random, boolean bees) {
        if (random.nextInt(10) == 0) return FLAT_VENUS;
        return VENUS;
    }

    @Nullable
    @Override
    protected RegistryEntry<? extends ConfiguredFeature<?, ?>> getLargeTreeFeature(Random random) {
        return MEGA_VENUS;
    }
}
