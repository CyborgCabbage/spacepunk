package cyborgcabbage.spacepunk.feature;

import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

import java.util.HashSet;
import java.util.concurrent.atomic.AtomicBoolean;

public class FractalStarFeature extends Feature<FractalStarFeatureConfig> {
    public FractalStarFeature(Codec<FractalStarFeatureConfig> configCodec) {
        super(configCodec);
    }

    @Override
    public boolean generate(FeatureContext<FractalStarFeatureConfig> context) {
        FractalStarFeatureConfig config = context.getConfig();
        BlockPos pos = context.getWorld().getTopPosition(Heightmap.Type.OCEAN_FLOOR_WG, context.getOrigin());
        HashSet<BlockPos> blockSet = Sets.newHashSet();
        int height = config.height().get(context.getRandom());
        if(createBranch(blockSet, height, Direction.UP, pos.down(1), context, 3)){
            return false;
        }else{
            for(BlockPos blockPos: blockSet){
                context.getWorld().setBlockState(blockPos, config.block().getBlockState(context.getRandom(), blockPos), 3);
            }
            return true;
        }
    }

    private boolean createBranch(HashSet<BlockPos> blockSet, int length, Direction dir, BlockPos origin, FeatureContext<FractalStarFeatureConfig> context, int depth){
        if(depth < 0) return false;
        //Add blocks to set
        for (int y = 0; y < length; y++) {
            blockSet.add(origin.offset(dir,y+1));
        }
        //Check for obstruction
        if(spikeObstructed(length,dir,origin,context)){
            return true;
        }
        //Create child spikes/branches
        int maxBranchLength = (length-2)*2/3;
        if(maxBranchLength > 0) {
            int branchLength = Math.max(1, context.getRandom().nextBetween(maxBranchLength-1,maxBranchLength+1));
            boolean result = createBranch(blockSet, branchLength, Direction.NORTH, origin.offset(dir, length), context, depth - 1);
            result |= createBranch(blockSet, branchLength, Direction.SOUTH, origin.offset(dir, length), context, depth - 1);
            result |= createBranch(blockSet, branchLength, Direction.EAST, origin.offset(dir, length), context, depth - 1);
            result |= createBranch(blockSet, branchLength, Direction.WEST, origin.offset(dir, length), context, depth - 1);
            result |= createBranch(blockSet, branchLength, Direction.UP, origin.offset(dir, length), context, depth - 1);
            result |= createBranch(blockSet, branchLength, Direction.DOWN, origin.offset(dir, length), context, depth - 1);
            return result;
        }
        return false;
    }

    private boolean checkBranchObstructed(int length, Direction dir, BlockPos origin, FeatureContext<FractalStarFeatureConfig> context, int depth){
        if(depth < 0) return false;
        FractalStarFeatureConfig config = context.getConfig();
        BlockPos o = origin.offset(dir,2);
        BlockPos e = origin.offset(dir,length);
        BlockBox blockBox = new BlockBox(o.getX(),o.getY(),o.getZ(),e.getX(),e.getY(),e.getZ());
        blockBox.expand(1);
        AtomicBoolean obstructed = new AtomicBoolean(false);
        blockBox.forEachVertex((blockPos) -> {
            if(!context.getWorld().getBlockState(blockPos).isAir()){
                obstructed.set(true);
            }
        });
        if(obstructed.get()){
            return true;
        }
        int maxBranchLength = (length-2)*2/3;
        if(maxBranchLength > 0) {
            int branchLength = Math.max(1, context.getRandom().nextBetween(maxBranchLength-1,maxBranchLength+1));
            boolean result;
            result = checkBranchObstructed(branchLength, Direction.NORTH, origin.offset(dir, length), context, depth - 1);
            result |= checkBranchObstructed(branchLength, Direction.SOUTH, origin.offset(dir, length), context, depth - 1);
            result |= checkBranchObstructed(branchLength, Direction.EAST, origin.offset(dir, length), context, depth - 1);
            result |= checkBranchObstructed(branchLength, Direction.WEST, origin.offset(dir, length), context, depth - 1);
            result |= checkBranchObstructed(branchLength, Direction.UP, origin.offset(dir, length), context, depth - 1);
            result |= checkBranchObstructed(branchLength, Direction.DOWN, origin.offset(dir, length), context, depth - 1);
            return result;
        }else{
            return false;
        }
    }

    private boolean spikeObstructed(int length, Direction dir, BlockPos origin, FeatureContext<FractalStarFeatureConfig> context){
        //Search for obstructions in area
        BlockPos o = origin.offset(dir,1);
        BlockPos e = origin.offset(dir,length);
        int x = Math.min(o.getX(),e.getX())-1;
        int y = Math.min(o.getY(),e.getY())-1;
        int z = Math.min(o.getZ(),e.getZ())-1;
        int X = Math.max(o.getX(),e.getX())+1;
        int Y = Math.max(o.getY(),e.getY())+1;
        int Z = Math.max(o.getZ(),e.getZ())+1;
        switch(dir){
            case DOWN -> Y-=1;
            case UP -> y+=1;
            case NORTH -> Z-=1;
            case SOUTH -> z+=1;
            case WEST -> X-=1;
            case EAST -> x+=1;
        }
        boolean obstructed = false;
        for(int i = x; i <= X; i++){
            for(int j = y; j <= Y; j++){
                for(int k = z; k <= Z; k++){
                    if(!context.getWorld().getBlockState(new BlockPos(i,j,k)).isAir()){
                        obstructed = true;
                    }
                }
            }
        }
        return obstructed;
    }
}
