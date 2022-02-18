package cyborgcabbage.spacepunk.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public record BoulderFeatureConfig(IntProvider size, IntProvider planes, RuleTest rule, BlockStateProvider block) implements FeatureConfig {
    public static final Codec<BoulderFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            IntProvider.VALUE_CODEC.fieldOf("size").forGetter(BoulderFeatureConfig::size),
            IntProvider.VALUE_CODEC.fieldOf("planes").forGetter(BoulderFeatureConfig::planes),
            RuleTest.TYPE_CODEC.fieldOf("rule").forGetter(BoulderFeatureConfig::rule),
            BlockStateProvider.TYPE_CODEC.fieldOf("block").forGetter(BoulderFeatureConfig::block)
    ).apply(instance, instance.stable(BoulderFeatureConfig::new)));
}
