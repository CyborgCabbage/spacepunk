package cyborgcabbage.spacepunk.gen.beta.biome;

import cyborgcabbage.spacepunk.gen.beta.worldgen.WorldGenBigTree;
import cyborgcabbage.spacepunk.gen.beta.worldgen.WorldGenForest;
import cyborgcabbage.spacepunk.gen.beta.worldgen.WorldGenTrees;
import cyborgcabbage.spacepunk.gen.beta.worldgen.WorldGenerator;

import java.util.Random;

public class BiomeGenForest extends BiomeGenBase {
	public BiomeGenForest() {
		//this.spawnableCreatureList.add(new SpawnListEntry(EntityWolf.class, 2));
	}

	public WorldGenerator getRandomWorldGenForTrees(Random random1) {
		return random1.nextInt(5) == 0 ? new WorldGenForest() : (random1.nextInt(3) == 0 ? new WorldGenBigTree() : new WorldGenTrees());
	}
}
