package cyborgcabbage.spacepunk.gen.beta;

import cyborgcabbage.spacepunk.gen.beta.biome.BiomeGenBase;
import cyborgcabbage.spacepunk.gen.beta.map.MapGenBase;
import cyborgcabbage.spacepunk.gen.beta.map.MapGenCaves;
import cyborgcabbage.spacepunk.gen.beta.noise.MoonPerlin;
import cyborgcabbage.spacepunk.gen.beta.worldgen.WorldGenMinable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.chunk.Chunk;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;

public class ChunkProviderMoon extends BetaChunkProvider {
	private final BlockState fill = Blocks.STONE.getDefaultState();
	private final BlockState top = Blocks.GRAVEL.getDefaultState();
	private int chunkX;
	private int chunkZ;
	final protected MapGenBase caveGen = new MapGenCaves();

	public ChunkProviderMoon(long seed) {
		super(seed);
		MoonPerlin.initGenerator(seed);
	}

	public void fillChunk(Chunk chunk) {
		var pos = chunk.getPos();
		this.rand.setSeed((long)pos.x * 341873128712L + (long)pos.z * 132897987541L);
		this.generateTerrain(chunk);
		var craters = this.getNearbyCraters(chunk);
		this.carveCraters(chunk, craters);
		this.caveGen.generate(chunk, worldSeed);
	}

	public void populate(StructureWorldAccess world, Chunk chunk) {
		int i = chunk.getPos().x;
		int j = chunk.getPos().z;
		int chunkStartX = i * 16;
		int chunkStartZ = j * 16;

		int tmp;
		int x;
		int y;
		int z;
		for(tmp = 0; tmp < 5; ++tmp) {
			x = chunkStartX + this.rand.nextInt(16);
			y = this.rand.nextInt(30);
			z = chunkStartZ + this.rand.nextInt(16);
			(new WorldGenMinable(Blocks.IRON_ORE.getDefaultState(), 4)).generate(world, this.rand, x, y, z);
		}

		for(tmp = 0; tmp < 2; ++tmp) {
			x = chunkStartX + this.rand.nextInt(16);
			y = this.rand.nextInt(30);
			z = chunkStartZ + this.rand.nextInt(16);
			(new WorldGenMinable(Blocks.GOLD_ORE.getDefaultState(), 4)).generate(world, this.rand, x, y, z);
		}
		/* Cheese
		for(tmp = 0; tmp < 12; ++tmp) {
			x = chunkStartX + this.rand.nextInt(16);
			y = this.rand.nextInt(128);
			z = chunkStartZ + this.rand.nextInt(16);
			(new WorldGenMinable(113, 8)).generate(this.worldObj, this.rand, x, y, z);
		}
		*/
	}

	@Override
	public BiomeGenBase getBiome(int x, int z) {
		return BiomeGenBase.sky;
	}

	public void generateTerrain(Chunk chunk) {
		ChunkPos pos = chunk.getPos();
		int i = pos.x;
		int j = pos.z;
		BlockPos.Mutable blockPos = new BlockPos.Mutable();
		for(int x = 0; x < 16; ++x) {
			blockPos.setX(x);
			for(int z = 0; z < 16; ++z) {
				blockPos.setZ(z);
				int xx = x + 16 * i;
				int zz = z + 16 * j;
				int surface = getSurfaceHeight(xx, zz);
				int surfaceDust = surface - (6 + this.rand.nextInt(5));

				for(int y = 127; y >= 0; --y) {
					blockPos.setY(y);
					BlockState state;
					if(y <= this.rand.nextInt(3)) {
						state = Blocks.BEDROCK.getDefaultState();
					} else if(y <= surfaceDust) {
						state = this.fill;
					} else if(y <= surface) {
						state = this.top;
					} else {
						state = Blocks.AIR.getDefaultState();
					}
					chunk.setBlockState(blockPos, state, false);
				}
			}
		}
	}

	private int getSurfaceHeight(int x, int z){
		/*final float param = 512;
		final float f = ;
		return 60 +
				(int)Math.abs(MoonPerlin.Noise(3.0F * (float)x /  param, 3.0F * (float)z /  param, 0.0F) * 15.0F) +
				(int)(MoonPerlin.Noise(f * (float)x /  param, f * (float)z /  param, 0.0F) * 15.0F);*/
		float h = 60;
		float f = 1.0F/512;
		float m = 32.f;
		for (int i = 0; i < 5; i++) {
			h += Math.abs(MoonPerlin.Noise(f * (float)x , f * (float)z , 0.0F) * m);
			f *= 2;
			m /= 2;
		}
		return (int)h;
	}

	private ArrayList<Crater> getNearbyCraters(Chunk chunk) {
		int x = chunk.getPos().x;
		int z = chunk.getPos().z;
		ArrayList<Crater> craters = new ArrayList<>();
		for (int xc = x-3; xc <= x+3; xc++){
			for (int zc = z-3; zc <= z+3; zc++){
				getCrater(xc, zc).ifPresent(craters::add);
			}
		}
		return craters;
	}

	private void carveCraters(Chunk chunk, ArrayList<Crater> craters) {
		if(craters.isEmpty()) return;
		int xChunk = chunk.getPos().x*16;
		int zChunk = chunk.getPos().z*16;
		BlockPos.Mutable blockPos = new BlockPos.Mutable();
		for(int z = zChunk; z < zChunk+16; ++z) {
			blockPos.setZ(z);
			for(int x = xChunk; x < xChunk+16; ++x) {
				blockPos.setX(x);
				int surface = chunk.sampleHeightmap(Heightmap.Type.MOTION_BLOCKING, x, z);
				boolean inSphere = false;
				for(int y = Math.max(surface-20, 0); y <= surface; y++){
					blockPos.setY(y);
					int ro = 0;
					int depth = surface - 8 - y;
					if(depth > 0) ro = depth;
					for(Crater c : craters){
						if(blockPos.isWithinDistance(c.pos, c.radius-ro)){
							chunk.setBlockState(blockPos, Blocks.AIR.getDefaultState(), false);
							if(!inSphere){
								blockPos.setY(y-1);
								if(chunk.getBlockState(blockPos).isOf(Blocks.STONE)){
									if (this.rand.nextDouble() < 0.8D) chunk.setBlockState(blockPos, this.top, false);
								}
								inSphere = true;
							}
							break;
						}
					}
				}

			}
		}
	}

	private Optional<Crater> getCrater(int x, int z) {
		Random random = new Random((long)x * 341873128712L + (long)z * 132897987541L + 666);
		if(random.nextInt(40) == 0){
			int xCrater = random.nextInt(16)+16*x;
			int zCrater = random.nextInt(16)+16*z;
			int radius = random.nextInt(5, 40);
			int yCrater = getSurfaceHeight(xCrater, zCrater)+radius/2;
			return Optional.of(new Crater(new BlockPos(xCrater,yCrater,zCrater), radius));
		}
		return Optional.empty();
	}

	record Crater(BlockPos pos, float radius){}
	/*
	private void createCrater(int chunkX, int chunkZ, Chunk chunk) {
		double centerRand = this.randFromPoint(chunkX, chunkZ);
		byte maxCenterDelta = 6;
		int centerX = chunkX * 16 + 8 + (int)((double)maxCenterDelta * centerRand);
		int centerZ = chunkZ * 16 + 8 + (int)((double)maxCenterDelta * centerRand);
		int radius = (int)((centerRand + 1.0D) * 8.0D) + 8;
		boolean sphereY = false;
		BlockPos.Mutable blockPos = new BlockPos.Mutable();
		for(int z = 0; z < 16; ++z) {
			blockPos.setZ(z);
			for(int x = 0; x < 16; ++x) {
				blockPos.setX(x);
				int distance = -1 * centerX * centerX + 2 * (x + this.chunkX) * centerX - centerZ * centerZ + 2 * centerZ * (z + this.chunkZ) + radius * radius - (x + this.chunkX) * (x + this.chunkX) - (z + this.chunkZ) * (z + this.chunkZ);
				if(distance > 0) {
					int i17 = (int)(Math.sqrt(distance) / 2.5D);
					boolean inSphere = false;

					for(int y = 90; y > 0; --y) {
						blockPos.setY(y);
						if(i17 == 0) {
							break;
						}

						if(inSphere) {
							chunk.setBlockState(blockPos, Blocks.AIR.getDefaultState(), false);
							--i17;
						} else if(!chunk.getBlockState(blockPos).isAir()) {
							++y;
							inSphere = true;
						}
					}

					if(this.rand.nextDouble() < 0.8D && inSphere) {
						chunk.setBlockState(blockPos, this.top, false);
					}
				}
			}
		}

	}

	private double randFromPoint(int x, int z) {
		int n = x + z * 57;
		n ^= n << 13;
		return 1.0D - (double)(n * (n * n * 15731 + 789221) + 1376312589 & Integer.MAX_VALUE) / 1.073741824E9D;
	}
	*/
}
