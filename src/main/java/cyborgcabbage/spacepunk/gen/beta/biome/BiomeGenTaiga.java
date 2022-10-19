package cyborgcabbage.spacepunk.gen.beta.biome;

import cyborgcabbage.spacepunk.gen.beta.worldgen.WorldGenTaiga1;
import cyborgcabbage.spacepunk.gen.beta.worldgen.WorldGenTaiga2;
import cyborgcabbage.spacepunk.gen.beta.worldgen.WorldGenerator;

import java.util.Random;

public class BiomeGenTaiga extends BiomeGenBase {
	public BiomeGenTaiga() {
		//this.spawnableCreatureList.add(new SpawnListEntry(EntityWolf.class, 2));
	}

	public WorldGenerator getRandomWorldGenForTrees(Random random1) {
		return random1.nextInt(3) == 0 ? new WorldGenTaiga1() : new WorldGenTaiga2();
	}
}
