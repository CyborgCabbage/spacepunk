package cyborgcabbage.spacepunk.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cyborgcabbage.spacepunk.Spacepunk;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.foliage.FoliagePlacerType;

import java.util.function.BiConsumer;

public class VenusFoliagePlacer extends FoliagePlacer {

    public static final Codec<VenusFoliagePlacer> CODEC =
            RecordCodecBuilder.create(instance -> VenusFoliagePlacer.fillFoliagePlacerFields(instance).apply(instance, VenusFoliagePlacer::new));

    public static final FoliagePlacerType<VenusFoliagePlacer> VENUS_FOLIAGE_PLACER =
            Registry.register(Registry.FOLIAGE_PLACER_TYPE, Spacepunk.id("venus_foliage_placer"), new FoliagePlacerType<>(CODEC));

    public VenusFoliagePlacer(IntProvider intProvider, IntProvider intProvider2) {
        super(intProvider, intProvider2);
    }

    @Override
    protected FoliagePlacerType<?> getType() {
        return VENUS_FOLIAGE_PLACER;
    }

    @Override
    protected void generate(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, TreeFeatureConfig config, int trunkHeight, FoliagePlacer.TreeNode treeNode, int foliageHeight, int radius, int offset) {
        boolean giantTrunk = treeNode.isGiantTrunk();
        BlockPos blockPos = treeNode.getCenter().up(offset);
        LeafContext leafContext = new LeafContext(world, replacer, random, config, blockPos, giantTrunk);
        if(trunkHeight < 15) {
            cap(leafContext);
            circle(leafContext, Math.sqrt(2)-0.01, Math.sqrt(2)+0.01, 0, 0.5);
            circle(leafContext, radius-0.5, radius+0.8, 0, 1.0);
            circle(leafContext, 0, radius, -1, 1.0);
            if (trunkHeight >= 9) {
                circle(leafContext, 0, 1.5, -3, 0.5);
                circle(leafContext, 0, 1, -3, 1.0);
                circle(leafContext, 0, 1, -5, 0.6);
            } else if (trunkHeight >= 5) {
                circle(leafContext, 0, 1, -3, 0.8);
            }
        }else{
            cap(leafContext);

            circle(leafContext, 0, 1, -1, 0.6);
            circle(leafContext, 3, 3.2, -1, 0.7);
            circle(leafContext, 3.2, 4.2, -1, 1.0);
            circle(leafContext, 0, 4, -2, 1.0);
            circle(leafContext, 0, 1, -3, 0.6);

            circle(leafContext, 0.9, 1.4, -5, 0.5);
            circle(leafContext, 1.8, 2.8, -5, 1.0);
            circle(leafContext, 0, 2, -6, 1.0);

            circle(leafContext, 0, 1.5, -8, 0.5);
            circle(leafContext, 0, 1, -8, 1.0);

            circle(leafContext, 0, 1, -10, 0.6);
        }
    }

    private void cap(LeafContext c) {
        placeFoliageBlock(c,0, 0, 0);
        if(c.giantTrunk()){
            placeFoliageBlock(c,0,-1,1);
            placeFoliageBlock(c,1,-1,0);
            placeFoliageBlock(c,1,-1,1);
        }
    }

    private void placeFoliageBlock(LeafContext c, BlockPos block){
        placeFoliageBlock(c, block.getX(), block.getY(), block.getZ());
    }

    private void placeFoliageBlock(LeafContext c, int x, int y, int z){
        FoliagePlacer.placeFoliageBlock(c.world(), c.replacer(), c.random(), c.config(), c.centerPos().add(x,y,z));
    }

    void circle(LeafContext c, double innerRadius, double outerRadius, int yOffset, double chance) {
        int extra = c.giantTrunk() ? 1 : 0;
        int r = (int)Math.ceil(outerRadius);
        for (int x = -r; x <= r + extra; ++x) {
            for (int z = -r; z <= r + extra; ++z) {
                int ax = Math.abs(x);
                int az = Math.abs(z);
                if (c.giantTrunk()) {
                    ax = Math.min(ax, Math.abs(x - 1));
                    az = Math.min(az, Math.abs(z - 1));
                }
                if(!inCircle(ax,az,outerRadius)) continue;
                if(inCircle(ax,az,innerRadius)) continue;
                if(chance < 1.0)
                    if(c.random().nextDouble() > chance) continue;
                placeFoliageBlock(c, x, yOffset, z);
            }
        }
    }

    public static boolean inCircle(double dx, double dy, double radius){
        return dx*dx+dy*dy <= radius*radius;
    }

    void square(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, TreeFeatureConfig config, BlockPos centerPos, int radius, int y, boolean giantTrunk) {
        int extra = giantTrunk ? 1 : 0;
        BlockPos.Mutable block = new BlockPos.Mutable();
        for (int x = -radius; x <= radius + extra; ++x) {
            for (int z = -radius; z <= radius + extra; ++z) {
                if (this.isPositionInvalid(random, x, y, z, radius, giantTrunk)) continue;
                block.set(centerPos, x, y, z);
                FoliagePlacer.placeFoliageBlock(world, replacer, random, config, block);
            }
        }
    }

    @Override
    public int getRandomHeight(Random random, int trunkHeight, TreeFeatureConfig config) {
        return 0;
    }

    @Override
    protected boolean isInvalidForLeaves(Random random, int dx, int y, int dz, int radius, boolean giantTrunk) {
        return dx == radius && dz == radius;
    }

    private record LeafContext(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, TreeFeatureConfig config, BlockPos centerPos, boolean giantTrunk){};
}
