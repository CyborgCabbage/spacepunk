package cyborgcabbage.spacepunk.block;

import net.minecraft.block.sapling.SaplingGenerator;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.TreeConfiguredFeatures;

import java.util.Random;

public class VenusSaplingGenerator extends SaplingGenerator {
    @Override
    protected RegistryEntry<? extends ConfiguredFeature<?, ?>> getTreeFeature(Random random, boolean bees) {
        /*RegistryKey<ConfiguredFeature<?, ?>> key = RegistryKey.of(Registry.CONFIGURED_FEATURE_KEY,new Identifier(Spacepunk.MOD_ID,"venus_tree"));
        DefaultedRegistry
        Spacepunk.LOGGER.info(key.toString());*/
        return TreeConfiguredFeatures.OAK;
    }
}
