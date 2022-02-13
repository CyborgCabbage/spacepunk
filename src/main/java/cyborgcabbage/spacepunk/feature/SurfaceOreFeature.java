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
        float angle = random.nextFloat() * (float)Math.PI;
        float xzScale = (float)oreFeatureConfig.size / 8.0f;
        int magnitude = MathHelper.ceil(((float)oreFeatureConfig.size / 16.0f * 2.0f + 1.0f) / 2.0f);
        double xEnd = (double)blockPos.getX() + Math.sin(angle) * (double)xzScale;
        double xStart = (double)blockPos.getX() - Math.sin(angle) * (double)xzScale;
        double zEnd = (double)blockPos.getZ() + Math.cos(angle) * (double)xzScale;
        double zStart = (double)blockPos.getZ() - Math.cos(angle) * (double)xzScale;
        int yScale = 2;
        double yRand1 = blockPos.getY() + random.nextInt(3) - yScale;
        double yRand2 = blockPos.getY() + random.nextInt(3) - yScale;
        int n = blockPos.getX() - MathHelper.ceil(xzScale) - magnitude;
        int o = blockPos.getY() - yScale - magnitude;
        int p = blockPos.getZ() - MathHelper.ceil(xzScale) - magnitude;
        int q = 2 * (MathHelper.ceil(xzScale) + magnitude);
        int r = 2 * (2 + magnitude);
        for (int s = n; s <= n + q; ++s) {
            for (int t = p; t <= p + q; ++t) {
                if (o > structureWorldAccess.getTopY(Heightmap.Type.OCEAN_FLOOR_WG, s, t)) continue;
                return this.generateVeinPart(structureWorldAccess, random, oreFeatureConfig, xEnd, xStart, zEnd, zStart, yRand1, yRand2, n, o, p, q, r);
            }
        }
        return false;
    }
}
