package cyborgcabbage.spacepunk.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public record FractalStarFeatureConfig(IntProvider height, BlockStateProvider block) implements FeatureConfig {
    public static final Codec<FractalStarFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            IntProvider.VALUE_CODEC.fieldOf("height").forGetter(FractalStarFeatureConfig::height),
            BlockStateProvider.TYPE_CODEC.fieldOf("block").forGetter(FractalStarFeatureConfig::block)
    ).apply(instance, instance.stable(FractalStarFeatureConfig::new)));
}
