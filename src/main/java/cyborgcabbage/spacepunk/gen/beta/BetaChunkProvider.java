package cyborgcabbage.spacepunk.gen.beta;

import cyborgcabbage.spacepunk.gen.beta.map.MapGenBase;
import cyborgcabbage.spacepunk.gen.beta.map.MapGenCaves;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.chunk.Chunk;

import java.util.Random;

public abstract class BetaChunkProvider {
    final protected MapGenBase caveGen = new MapGenCaves();
    final protected Random rand;
    final long worldSeed;
    public BetaChunkProvider(long seed) {
        worldSeed = seed;
        rand = new Random(seed);
    }

    public abstract void fillChunk(Chunk chunk);
    public abstract void populate(StructureWorldAccess world, Chunk chunk);
}
