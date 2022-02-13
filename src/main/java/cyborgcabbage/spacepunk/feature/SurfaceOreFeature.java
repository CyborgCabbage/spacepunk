package cyborgcabbage.spacepunk.feature;

import com.mojang.serialization.Codec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.OreFeature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.util.FeatureContext;

import java.util.Random;

public class SurfaceOreFeature extends OreFeature {
    public SurfaceOreFeature(Codec<OreFeatureConfig> codec) {
        super(codec);
    }

    /*@Override
    public boolean generate(FeatureContext<OreFeatureConfig> c) {
        FeatureContext<OreFeatureConfig> c2 = new FeatureContext<>(
                c.getFeature(),
                c.getWorld(),
                c.getGenerator(),
                c.getRandom(),
                c.getWorld().getTopPosition(Heightmap.Type.OCEAN_FLOOR_WG, c.getOrigin()).up(1),
                c.getConfig()
        );
        return super.generate(c2);
    }*/
    @Override
    public boolean generate(FeatureContext<OreFeatureConfig> context) {
        Random random = context.getRandom();
        BlockPos blockPos = context.getOrigin();//context.getWorld().getTopPosition(Heightmap.Type.OCEAN_FLOOR_WG, context.getOrigin());
        StructureWorldAccess structureWorldAccess = context.getWorld();
        OreFeatureConfig oreFeatureConfig = context.getConfig();
        structureWorldAccess.setBlockState(blockPos, oreFeatureConfig.targets.get(0).state, 3);
        float f = random.nextFloat() * (float)Math.PI;
        float g = (float)oreFeatureConfig.size / 8.0f;
        int i = MathHelper.ceil(((float)oreFeatureConfig.size / 16.0f * 2.0f + 1.0f) / 2.0f);
        double d = (double)blockPos.getX() + Math.sin(f) * (double)g;
        double e = (double)blockPos.getX() - Math.sin(f) * (double)g;
        double h = (double)blockPos.getZ() + Math.cos(f) * (double)g;
        double j = (double)blockPos.getZ() - Math.cos(f) * (double)g;
        int k = 2;
        double l = blockPos.getY() + random.nextInt(3) - 2;
        double m = blockPos.getY() + random.nextInt(3) - 2;
        int n = blockPos.getX() - MathHelper.ceil(g) - i;
        int o = blockPos.getY() - 2 - i;
        int p = blockPos.getZ() - MathHelper.ceil(g) - i;
        int q = 2 * (MathHelper.ceil(g) + i);
        int r = 2 * (2 + i);
        for (int s = n; s <= n + q; ++s) {
            for (int t = p; t <= p + q; ++t) {
                //if (o > structureWorldAccess.getTopY(Heightmap.Type.OCEAN_FLOOR_WG, s, t)) continue;
                return this.generateVeinPart(structureWorldAccess, random, oreFeatureConfig, d, e, h, j, l, m, n, o, p, q, r);
            }
        }
        return false;
    }
}
