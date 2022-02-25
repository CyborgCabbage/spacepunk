package cyborgcabbage.spacepunk.block;

import cyborgcabbage.spacepunk.Spacepunk;
import net.minecraft.block.sapling.SaplingGenerator;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.*;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.ConfiguredFeatures;
import net.minecraft.world.gen.feature.TreeConfiguredFeatures;

import java.util.Random;

public class VenusSaplingGenerator extends SaplingGenerator {
    @Override
    protected ConfiguredFeature<?, ?> getTreeFeature(Random random, boolean bees) {
        /*RegistryKey<ConfiguredFeature<?, ?>> key = RegistryKey.of(Registry.CONFIGURED_FEATURE_KEY,new Identifier(Spacepunk.MOD_ID,"venus_tree"));
        DefaultedRegistry
        Spacepunk.LOGGER.info(key.toString());*/
        return TreeConfiguredFeatures.OAK;
    }
}
