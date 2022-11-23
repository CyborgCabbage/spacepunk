package cyborgcabbage.spacepunk.gen.beta;

import cyborgcabbage.spacepunk.gen.beta.biome.BiomeGenBase;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.chunk.Chunk;

import java.util.Random;

public abstract class BetaChunkProvider {
    final protected Random rand;
    final long worldSeed;
    public BetaChunkProvider(long seed) {
        worldSeed = seed;
        rand = new Random(seed);
    }

    public abstract void fillChunk(Chunk chunk);
    public abstract void populate(StructureWorldAccess world, Chunk chunk);
    public abstract BiomeGenBase getBiome(int x, int z);
}
