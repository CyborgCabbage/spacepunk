package cyborgcabbage.spacepunk.feature;

import com.mojang.serialization.Codec;
import cyborgcabbage.spacepunk.Spacepunk;
import cyborgcabbage.spacepunk.util.SpaceRandom;
import cyborgcabbage.spacepunk.util.TangentPlane;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

import java.util.ArrayList;
import java.util.List;

public class BoulderFeature extends Feature<BoulderFeatureConfig> {
    public BoulderFeature(Codec<BoulderFeatureConfig> configCodec) {
        super(configCodec);
    }

    @Override
    public boolean generate(FeatureContext<BoulderFeatureConfig> context) {
        Random random = context.getRandom();
        BlockPos blockPos = context.getOrigin();
        StructureWorldAccess world = context.getWorld();
        BoulderFeatureConfig config = context.getConfig();
        int size = config.size().get(random);
        List<TangentPlane> planeList = new ArrayList<>();
        int planeCount = config.planes().get(random);
        for(int i = 0; i < planeCount; i++){
            planeList.add(new TangentPlane(random, size));
        }
        for(int i = -size; i <= size; i++){
            for(int j = -size; j <= size; j++) {
                for (int k = -size; k <= size; k++) {
                    double x = i+SpaceRandom.f(random,-0.5f,0.5f);
                    double y = j+SpaceRandom.f(random,-0.5f,0.5f);
                    double z = k+SpaceRandom.f(random,-0.5f,0.5f);
                    if(x*x+y*y+z*z < size*size+1) {
                        boolean success = true;
                        for(TangentPlane plane: planeList){
                            if(!plane.test(x,y,z)){
                                success = false;
                                break;
                            }
                        }
                        if(success) {
                            BlockPos bp = blockPos.add(i, j, k);
                            world.setBlockState(bp, config.block().getBlockState(random, bp), 3);
                        }
                    }
                }
            }
        }
        return true;
    }
}
