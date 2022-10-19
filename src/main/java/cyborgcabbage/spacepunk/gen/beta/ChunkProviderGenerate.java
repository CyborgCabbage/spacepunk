package cyborgcabbage.spacepunk.gen.beta;

import cyborgcabbage.spacepunk.Spacepunk;
import cyborgcabbage.spacepunk.gen.beta.biome.BiomeGenBase;
import cyborgcabbage.spacepunk.gen.beta.noise.NoiseGeneratorOctaves;
import cyborgcabbage.spacepunk.gen.beta.noise.NoiseGeneratorOctaves2;
import cyborgcabbage.spacepunk.gen.beta.worldgen.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import java.util.Random;

public class ChunkProviderGenerate {
    private Random rand;
    private NoiseGeneratorOctaves noise0;
    private NoiseGeneratorOctaves noise1;
    private NoiseGeneratorOctaves noise2;
    private NoiseGeneratorOctaves noise3;
    private NoiseGeneratorOctaves noise4;
    public NoiseGeneratorOctaves noise5;
    public NoiseGeneratorOctaves noise6;
    public NoiseGeneratorOctaves mobSpawnerNoise;
    private double[] terrainNoiseValues;
    private double[] sandNoise = new double[256];
    private double[] gravelNoise = new double[256];
    private double[] stoneNoise = new double[256];
    //private MapGenBase field_902_u = new MapGenCaves();
    private BiomeGenBase[] biomesForGeneration;
    double[] field_4185_d;
    double[] field_4184_e;
    double[] field_4183_f;
    double[] field_4182_g;
    double[] field_4181_h;
    int[][] field_914_i = new int[32][32];
    private double[] generatedTemperatures;
    final long worldSeed;
    public ChunkProviderGenerate(long seed) {
        this.worldSeed = seed;
        this.temperatureGenerator = new NoiseGeneratorOctaves2(new Random(seed * 9871L), 4);
        this.noise8 = new NoiseGeneratorOctaves2(new Random(seed * 39811L), 4);
        this.noise9 = new NoiseGeneratorOctaves2(new Random(seed * 543321L), 2);
        this.rand = new Random(seed);
        this.noise0 = new NoiseGeneratorOctaves(this.rand, 16);
        this.noise1 = new NoiseGeneratorOctaves(this.rand, 16);
        this.noise2 = new NoiseGeneratorOctaves(this.rand, 8);
        this.noise3 = new NoiseGeneratorOctaves(this.rand, 4);
        this.noise4 = new NoiseGeneratorOctaves(this.rand, 4);
        this.noise5 = new NoiseGeneratorOctaves(this.rand, 10);
        this.noise6 = new NoiseGeneratorOctaves(this.rand, 16);
        this.mobSpawnerNoise = new NoiseGeneratorOctaves(this.rand, 8);
    }

    public void generateTerrain(Chunk chunk) {
        ChunkPos pos = chunk.getPos();
        byte horizontalNoiseSize = 4;
        byte seaLevel = 64;
        int xNoiseSize = horizontalNoiseSize + 1;
        byte yNoiseSize = 17;
        int zNoiseSIze = horizontalNoiseSize + 1;
        this.terrainNoiseValues = this.generateTerrainNoise(this.terrainNoiseValues, pos.x * horizontalNoiseSize, 0, pos.z * horizontalNoiseSize, xNoiseSize, yNoiseSize, zNoiseSIze);
        for(int xNoiseIndex = 0; xNoiseIndex < horizontalNoiseSize; ++xNoiseIndex) {
            for(int zNoiseIndex = 0; zNoiseIndex < horizontalNoiseSize; ++zNoiseIndex) {
                for(int yNoiseIndex = 0; yNoiseIndex < 16; ++yNoiseIndex) {
                    double yFrac = 0.125D;
                    double d16 = this.terrainNoiseValues[((xNoiseIndex + 0) * zNoiseSIze + zNoiseIndex + 0) * yNoiseSize + yNoiseIndex + 0];
                    double d18 = this.terrainNoiseValues[((xNoiseIndex + 0) * zNoiseSIze + zNoiseIndex + 1) * yNoiseSize + yNoiseIndex + 0];
                    double d20 = this.terrainNoiseValues[((xNoiseIndex + 1) * zNoiseSIze + zNoiseIndex + 0) * yNoiseSize + yNoiseIndex + 0];
                    double d22 = this.terrainNoiseValues[((xNoiseIndex + 1) * zNoiseSIze + zNoiseIndex + 1) * yNoiseSize + yNoiseIndex + 0];
                    double d24 = (this.terrainNoiseValues[((xNoiseIndex + 0) * zNoiseSIze + zNoiseIndex + 0) * yNoiseSize + yNoiseIndex + 1] - d16) * yFrac;
                    double d26 = (this.terrainNoiseValues[((xNoiseIndex + 0) * zNoiseSIze + zNoiseIndex + 1) * yNoiseSize + yNoiseIndex + 1] - d18) * yFrac;
                    double d28 = (this.terrainNoiseValues[((xNoiseIndex + 1) * zNoiseSIze + zNoiseIndex + 0) * yNoiseSize + yNoiseIndex + 1] - d20) * yFrac;
                    double d30 = (this.terrainNoiseValues[((xNoiseIndex + 1) * zNoiseSIze + zNoiseIndex + 1) * yNoiseSize + yNoiseIndex + 1] - d22) * yFrac;

                    for(int ySub = 0; ySub < 8; ++ySub) {
                        double xFrac = 0.25D;
                        double d35 = d16;
                        double d37 = d18;
                        double d39 = (d20 - d16) * xFrac;
                        double d41 = (d22 - d18) * xFrac;

                        for(int xSub = 0; xSub < 4; ++xSub) {
                            BlockPos.Mutable blockPos = new BlockPos.Mutable(xSub + xNoiseIndex * 4, yNoiseIndex * 8 + ySub, zNoiseIndex * 4);
                            double zFrac = 0.25D;
                            double density = d35;
                            double zNoiseStep = (d37 - d35) * zFrac;

                            for(int zSub = 0; zSub < 4; ++zSub) {
                                double d53 = temperature[(xNoiseIndex * 4 + xSub) * 16 + zNoiseIndex * 4 + zSub];
                                Block blockState = Blocks.AIR;
                                if(yNoiseIndex * 8 + ySub < seaLevel) {
                                    if(d53 < 0.5D && yNoiseIndex * 8 + ySub >= seaLevel - 1) {
                                        blockState = Blocks.ICE;
                                    } else {
                                        blockState = Blocks.WATER;
                                    }
                                }

                                if(density > 0.0D) {
                                    blockState = Blocks.STONE;
                                }
                                chunk.setBlockState(blockPos, blockState.getDefaultState(), false);
                                blockPos.move(0, 0, 1);
                                density += zNoiseStep;
                            }
                            d35 += d39;
                            d37 += d41;
                        }
                        d16 += d24;
                        d18 += d26;
                        d20 += d28;
                        d22 += d30;
                    }
                }
            }
        }

    }

    public void replaceBlocksForBiome(Chunk chunk, BiomeGenBase[] biomeGenBase4) {
        ChunkPos pos = chunk.getPos();
        byte b5 = 64;
        double d6 = 8.0D / 256D;
        this.sandNoise = this.noise3.generateNoiseOctaves(this.sandNoise, pos.x * 16, pos.z * 16, 0.0D, 16, 16, 1, d6, d6, 1.0D);
        this.gravelNoise = this.noise3.generateNoiseOctaves(this.gravelNoise, pos.x * 16, 109.0134D, pos.z * 16, 16, 1, 16, d6, 1.0D, d6);
        this.stoneNoise = this.noise4.generateNoiseOctaves(this.stoneNoise, pos.x * 16, pos.z * 16, 0.0D, 16, 16, 1, d6 * 2.0D, d6 * 2.0D, d6 * 2.0D);

        for(int h1 = 0; h1 < 16; ++h1) {
            for(int h2 = 0; h2 < 16; ++h2) {
                BiomeGenBase biomeGenBase10 = biomeGenBase4[h1 + h2 * 16];
                boolean sand = this.sandNoise[h1 + h2 * 16] + this.rand.nextDouble() * 0.2D > 0.0D;
                boolean gravel = this.gravelNoise[h1 + h2 * 16] + this.rand.nextDouble() * 0.2D > 3.0D;
                int stone = (int)(this.stoneNoise[h1 + h2 * 16] / 3.0D + 3.0D + this.rand.nextDouble() * 0.25D);
                int i14 = -1;
                BlockState topBlock = biomeGenBase10.topBlock;
                BlockState fillerBlock = biomeGenBase10.fillerBlock;

                for(int yBlock = 127; yBlock >= 0; --yBlock) {
                    BlockPos blockPos = new BlockPos(h2, yBlock, h1);
                    BlockState block = null;
                    if(yBlock <= rand.nextInt(5)) {
                        block = Blocks.BEDROCK.getDefaultState();
                    } else {
                        BlockState state = chunk.getBlockState(blockPos);
                        if(state.isAir()) {
                            i14 = -1;
                        } else if(state.isOf(Blocks.STONE)) {
                            if(i14 == -1) {
                                if(stone <= 0) {
                                    topBlock = Blocks.AIR.getDefaultState();
                                    fillerBlock = Blocks.STONE.getDefaultState();
                                } else if(yBlock >= b5 - 4 && yBlock <= b5 + 1) {
                                    topBlock = biomeGenBase10.topBlock;
                                    fillerBlock = biomeGenBase10.fillerBlock;
                                    if(gravel) {
                                        topBlock = Blocks.AIR.getDefaultState();
                                        fillerBlock = Blocks.GRAVEL.getDefaultState();
                                    }
                                    if(sand) {
                                        topBlock = Blocks.SAND.getDefaultState();
                                        fillerBlock = Blocks.SAND.getDefaultState();
                                    }
                                }

                                if(yBlock < b5 && topBlock == Blocks.AIR.getDefaultState()) {
                                    topBlock = Blocks.WATER.getDefaultState();
                                }

                                i14 = stone;
                                if(yBlock >= b5 - 1) {
                                    block = topBlock;
                                } else {
                                    block = fillerBlock;
                                }
                            } else if(i14 > 0) {
                                --i14;
                                block = fillerBlock;
                                if(i14 == 0 && fillerBlock == Blocks.SAND.getDefaultState()) {
                                    i14 = this.rand.nextInt(4);
                                    fillerBlock = Blocks.SANDSTONE.getDefaultState();
                                }
                            }
                        }
                    }
                    if(block != null) chunk.setBlockState(blockPos, block, false);
                }
            }
        }

    }

    public void fillChunk(Chunk chunk) {
        var pos = chunk.getPos();
        this.rand.setSeed((long)pos.x * 341873128712L + (long)pos.z * 132897987541L);
        this.biomes = loadBlockGeneratorData(this.biomes, pos.x * 16, pos.z * 16, 16, 16);
        this.generateTerrain(chunk);
        this.replaceBlocksForBiome(chunk, this.biomes);
        //this.field_902_u.func_867_a(this, this.worldObj, i1, i2, b3);
        //chunk4.func_1024_c();
    }

    private double[] generateTerrainNoise(double[] noiseArray, int xOffset, int yOffset, int zOffset, int xNoiseSize, int yNoiseSize, int zNoiseSize) {
        if(noiseArray == null) {
            noiseArray = new double[xNoiseSize * yNoiseSize * zNoiseSize];
        }

        double hScale = 684.412D;
        double vScale = 684.412D;
        double[] temp = this.temperature;
        double[] humidity = this.humidity;
        this.field_4182_g = this.noise5.func_4109_a(this.field_4182_g, xOffset, zOffset, xNoiseSize, zNoiseSize, 1.121D, 1.121D, 0.5D);
        this.field_4181_h = this.noise6.func_4109_a(this.field_4181_h, xOffset, zOffset, xNoiseSize, zNoiseSize, 200.0D, 200.0D, 0.5D);
        this.field_4185_d = this.noise2.generateNoiseOctaves(this.field_4185_d, xOffset, yOffset, zOffset, xNoiseSize, yNoiseSize, zNoiseSize, hScale / 80.0D, vScale / 160.0D, hScale / 80.0D);
        this.field_4184_e = this.noise0.generateNoiseOctaves(this.field_4184_e, xOffset, yOffset, zOffset, xNoiseSize, yNoiseSize, zNoiseSize, hScale, vScale, hScale);
        this.field_4183_f = this.noise1.generateNoiseOctaves(this.field_4183_f, xOffset, yOffset, zOffset, xNoiseSize, yNoiseSize, zNoiseSize, hScale, vScale, hScale);
        int noiseIndex = 0;
        int noiseIndex2 = 0;
        int i16 = 16 / xNoiseSize;

        for(int xNoiseIndex = 0; xNoiseIndex < xNoiseSize; ++xNoiseIndex) {
            int i18 = xNoiseIndex * i16 + i16 / 2;

            for(int zNoiseIndex = 0; zNoiseIndex < zNoiseSize; ++zNoiseIndex) {
                int i20 = zNoiseIndex * i16 + i16 / 2;
                double tempVal = temp[i18 * 16 + i20];
                double humidityVal = humidity[i18 * 16 + i20] * tempVal;
                double h = 1.0D - humidityVal;
                h *= h;
                h *= h;
                h = 1.0D - h;
                double d27 = (this.field_4182_g[noiseIndex2] + 256.0D) / 512.0D;
                d27 *= h;
                if(d27 > 1.0D) {
                    d27 = 1.0D;
                }

                double d29 = this.field_4181_h[noiseIndex2] / 8000.0D;
                if(d29 < 0.0D) {
                    d29 = -d29 * 0.3D;
                }

                d29 = d29 * 3.0D - 2.0D;
                if(d29 < 0.0D) {
                    d29 /= 2.0D;
                    if(d29 < -1.0D) {
                        d29 = -1.0D;
                    }

                    d29 /= 1.4D;
                    d29 /= 2.0D;
                    d27 = 0.0D;
                } else {
                    if(d29 > 1.0D) {
                        d29 = 1.0D;
                    }

                    d29 /= 8.0D;
                }

                if(d27 < 0.0D) {
                    d27 = 0.0D;
                }

                d27 += 0.5D;
                d29 = d29 * (double)yNoiseSize / 16.0D;
                double d31 = (double)yNoiseSize / 2.0D + d29 * 4.0D;
                ++noiseIndex2;

                for(int yNoiseIndex = 0; yNoiseIndex < yNoiseSize; ++yNoiseIndex) {
                    double noiseValue = 0.0D;
                    double d36 = ((double)yNoiseIndex - d31) * 12.0D / d27;
                    if(d36 < 0.0D) {
                        d36 *= 4.0D;
                    }

                    double d38 = this.field_4184_e[noiseIndex] / 512.0D;
                    double d40 = this.field_4183_f[noiseIndex] / 512.0D;
                    double d42 = (this.field_4185_d[noiseIndex] / 10.0D + 1.0D) / 2.0D;
                    if(d42 < 0.0D) {
                        noiseValue = d38;
                    } else if(d42 > 1.0D) {
                        noiseValue = d40;
                    } else {
                        noiseValue = d38 + (d40 - d38) * d42;
                    }

                    noiseValue -= d36;
                    if(yNoiseIndex > yNoiseSize - 4) {
                        double d44 = (double)((float)(yNoiseIndex - (yNoiseSize - 4)) / 3.0F);
                        noiseValue = noiseValue * (1.0D - d44) + -10.0D * d44;
                    }

                    noiseArray[noiseIndex] = noiseValue;
                    ++noiseIndex;
                }
            }
        }

        return noiseArray;
    }

    //public boolean chunkExists(int i1, int i2) {return true;}

    public void populate(StructureWorldAccess world, Chunk chunk) {
        //BlockSand.fallInstantly = true;
        int i2 = chunk.getPos().x;
        int i3 = chunk.getPos().z;
        int i4 = i2 * 16;
        int i5 = i3 * 16;
        BiomeGenBase biomeGenBase6 = getBiomeGenAt(i4 + 16, i5 + 16);
        this.rand.setSeed(worldSeed);
        long j7 = this.rand.nextLong() / 2L * 2L + 1L;
        long j9 = this.rand.nextLong() / 2L * 2L + 1L;
        this.rand.setSeed((long)i2 * j7 + (long)i3 * j9 ^ worldSeed);
        double d11 = 0.25D;
        int i13;
        int i14;
        int i15;
        if(this.rand.nextInt(4) == 0) {
            i13 = i4 + this.rand.nextInt(16) + 8;
            i14 = this.rand.nextInt(128);
            i15 = i5 + this.rand.nextInt(16) + 8;
            (new WorldGenLakes(Blocks.WATER.getDefaultState())).generate(world, this.rand, i13, i14, i15);
        }

        if(this.rand.nextInt(8) == 0) {
            i13 = i4 + this.rand.nextInt(16) + 8;
            i14 = this.rand.nextInt(this.rand.nextInt(120) + 8);
            i15 = i5 + this.rand.nextInt(16) + 8;
            if(i14 < 64 || this.rand.nextInt(10) == 0) {
                (new WorldGenLakes(Blocks.LAVA.getDefaultState())).generate(world, this.rand, i13, i14, i15);
            }
        }

        int i16;
        for(i13 = 0; i13 < 8; ++i13) {
            i14 = i4 + this.rand.nextInt(16) + 8;
            i15 = this.rand.nextInt(128);
            i16 = i5 + this.rand.nextInt(16) + 8;
            (new WorldGenDungeons()).generate(world, this.rand, i14, i15, i16);
        }

        for(i13 = 0; i13 < 10; ++i13) {
            i14 = i4 + this.rand.nextInt(16);
            i15 = this.rand.nextInt(128);
            i16 = i5 + this.rand.nextInt(16);
            (new WorldGenClay(32)).generate(world, this.rand, i14, i15, i16);
        }

        for(i13 = 0; i13 < 20; ++i13) {
            i14 = i4 + this.rand.nextInt(16);
            i15 = this.rand.nextInt(128);
            i16 = i5 + this.rand.nextInt(16);
            (new WorldGenMinable(Blocks.DIRT.getDefaultState(), 32)).generate(world, this.rand, i14, i15, i16);
        }

        for(i13 = 0; i13 < 10; ++i13) {
            i14 = i4 + this.rand.nextInt(16);
            i15 = this.rand.nextInt(128);
            i16 = i5 + this.rand.nextInt(16);
            (new WorldGenMinable(Blocks.GRAVEL.getDefaultState(), 32)).generate(world, this.rand, i14, i15, i16);
        }

        for(i13 = 0; i13 < 20; ++i13) {
            i14 = i4 + this.rand.nextInt(16);
            i15 = this.rand.nextInt(128);
            i16 = i5 + this.rand.nextInt(16);
            (new WorldGenMinable(Blocks.COAL_ORE.getDefaultState(), 16)).generate(world, this.rand, i14, i15, i16);
        }

        for(i13 = 0; i13 < 20; ++i13) {
            i14 = i4 + this.rand.nextInt(16);
            i15 = this.rand.nextInt(64);
            i16 = i5 + this.rand.nextInt(16);
            (new WorldGenMinable(Blocks.IRON_ORE.getDefaultState(), 8)).generate(world, this.rand, i14, i15, i16);
        }

        for(i13 = 0; i13 < 2; ++i13) {
            i14 = i4 + this.rand.nextInt(16);
            i15 = this.rand.nextInt(32);
            i16 = i5 + this.rand.nextInt(16);
            (new WorldGenMinable(Blocks.GOLD_ORE.getDefaultState(), 8)).generate(world, this.rand, i14, i15, i16);
        }

        for(i13 = 0; i13 < 8; ++i13) {
            i14 = i4 + this.rand.nextInt(16);
            i15 = this.rand.nextInt(16);
            i16 = i5 + this.rand.nextInt(16);
            (new WorldGenMinable(Blocks.REDSTONE_ORE.getDefaultState(), 7)).generate(world, this.rand, i14, i15, i16);
        }

        for(i13 = 0; i13 < 1; ++i13) {
            i14 = i4 + this.rand.nextInt(16);
            i15 = this.rand.nextInt(16);
            i16 = i5 + this.rand.nextInt(16);
            (new WorldGenMinable(Blocks.DIAMOND_ORE.getDefaultState(), 7)).generate(world, this.rand, i14, i15, i16);
        }

        for(i13 = 0; i13 < 1; ++i13) {
            i14 = i4 + this.rand.nextInt(16);
            i15 = this.rand.nextInt(16) + this.rand.nextInt(16);
            i16 = i5 + this.rand.nextInt(16);
            (new WorldGenMinable(Blocks.LAPIS_ORE.getDefaultState(), 6)).generate(world, this.rand, i14, i15, i16);
        }

        d11 = 0.5D;
        i13 = (int)((this.mobSpawnerNoise.func_806_a((double)i4 * d11, (double)i5 * d11) / 8.0D + this.rand.nextDouble() * 4.0D + 4.0D) / 3.0D);
        i14 = 0;
        if(this.rand.nextInt(10) == 0) {
            ++i14;
        }

        if(biomeGenBase6 == BiomeGenBase.forest) {
            i14 += i13 + 5;
        }

        if(biomeGenBase6 == BiomeGenBase.rainforest) {
            i14 += i13 + 5;
        }

        if(biomeGenBase6 == BiomeGenBase.seasonalForest) {
            i14 += i13 + 2;
        }

        if(biomeGenBase6 == BiomeGenBase.taiga) {
            i14 += i13 + 5;
        }

        if(biomeGenBase6 == BiomeGenBase.desert) {
            i14 -= 20;
        }

        if(biomeGenBase6 == BiomeGenBase.tundra) {
            i14 -= 20;
        }

        if(biomeGenBase6 == BiomeGenBase.plains) {
            i14 -= 20;
        }

        int i17;
        for(i15 = 0; i15 < i14; ++i15) {
            i16 = i4 + this.rand.nextInt(16) + 8;
            i17 = i5 + this.rand.nextInt(16) + 8;
            WorldGenerator worldGenerator18 = biomeGenBase6.getRandomWorldGenForTrees(this.rand);
            worldGenerator18.func_517_a(1.0D, 1.0D, 1.0D);
            worldGenerator18.generate(world, this.rand, i16, world.getTopY(Heightmap.Type.WORLD_SURFACE, i16, i17), i17);
        }

        byte b27 = 0;
        if(biomeGenBase6 == BiomeGenBase.forest) {
            b27 = 2;
        }

        if(biomeGenBase6 == BiomeGenBase.seasonalForest) {
            b27 = 4;
        }

        if(biomeGenBase6 == BiomeGenBase.taiga) {
            b27 = 2;
        }

        if(biomeGenBase6 == BiomeGenBase.plains) {
            b27 = 3;
        }

        int i19;
        int i25;
        for(i16 = 0; i16 < b27; ++i16) {
            i17 = i4 + this.rand.nextInt(16) + 8;
            i25 = this.rand.nextInt(128);
            i19 = i5 + this.rand.nextInt(16) + 8;
            (new WorldGenFlowers(Blocks.DANDELION.getDefaultState())).generate(world, this.rand, i17, i25, i19);
        }

        byte b28 = 0;
        if(biomeGenBase6 == BiomeGenBase.forest) {
            b28 = 2;
        }

        if(biomeGenBase6 == BiomeGenBase.rainforest) {
            b28 = 10;
        }

        if(biomeGenBase6 == BiomeGenBase.seasonalForest) {
            b28 = 2;
        }

        if(biomeGenBase6 == BiomeGenBase.taiga) {
            b28 = 1;
        }

        if(biomeGenBase6 == BiomeGenBase.plains) {
            b28 = 10;
        }

        int i20;
        int i21;
        for(i17 = 0; i17 < b28; ++i17) {
            byte b26 = 1;
            if(biomeGenBase6 == BiomeGenBase.rainforest && this.rand.nextInt(3) != 0) {
                b26 = 2;
            }

            i19 = i4 + this.rand.nextInt(16) + 8;
            i20 = this.rand.nextInt(128);
            i21 = i5 + this.rand.nextInt(16) + 8;

            (new WorldGenTallGrass(b26 == 1 ? Blocks.GRASS.getDefaultState() : Blocks.FERN.getDefaultState())).generate(world, this.rand, i19, i20, i21);
        }

        b28 = 0;
        if(biomeGenBase6 == BiomeGenBase.desert) {
            b28 = 2;
        }

        for(i17 = 0; i17 < b28; ++i17) {
            i25 = i4 + this.rand.nextInt(16) + 8;
            i19 = this.rand.nextInt(128);
            i20 = i5 + this.rand.nextInt(16) + 8;
            (new WorldGenDeadBush(Blocks.DEAD_BUSH.getDefaultState())).generate(world, this.rand, i25, i19, i20);
        }

        if(this.rand.nextInt(2) == 0) {
            i17 = i4 + this.rand.nextInt(16) + 8;
            i25 = this.rand.nextInt(128);
            i19 = i5 + this.rand.nextInt(16) + 8;
            (new WorldGenFlowers(Blocks.POPPY.getDefaultState())).generate(world, this.rand, i17, i25, i19);
        }

        if(this.rand.nextInt(4) == 0) {
            i17 = i4 + this.rand.nextInt(16) + 8;
            i25 = this.rand.nextInt(128);
            i19 = i5 + this.rand.nextInt(16) + 8;
            (new WorldGenFlowers(Blocks.BROWN_MUSHROOM.getDefaultState())).generate(world, this.rand, i17, i25, i19);
        }

        if(this.rand.nextInt(8) == 0) {
            i17 = i4 + this.rand.nextInt(16) + 8;
            i25 = this.rand.nextInt(128);
            i19 = i5 + this.rand.nextInt(16) + 8;
            (new WorldGenFlowers(Blocks.RED_MUSHROOM.getDefaultState())).generate(world, this.rand, i17, i25, i19);
        }

        for(i17 = 0; i17 < 10; ++i17) {
            i25 = i4 + this.rand.nextInt(16) + 8;
            i19 = this.rand.nextInt(128);
            i20 = i5 + this.rand.nextInt(16) + 8;
            (new WorldGenReed()).generate(world, this.rand, i25, i19, i20);
        }

        if(this.rand.nextInt(32) == 0) {
            i17 = i4 + this.rand.nextInt(16) + 8;
            i25 = this.rand.nextInt(128);
            i19 = i5 + this.rand.nextInt(16) + 8;
            (new WorldGenPumpkin()).generate(world, this.rand, i17, i25, i19);
        }

        i17 = 0;
        if(biomeGenBase6 == BiomeGenBase.desert) {
            i17 += 10;
        }

        for(i25 = 0; i25 < i17; ++i25) {
            i19 = i4 + this.rand.nextInt(16) + 8;
            i20 = this.rand.nextInt(128);
            i21 = i5 + this.rand.nextInt(16) + 8;
            (new WorldGenCactus()).generate(world, this.rand, i19, i20, i21);
        }

        for(i25 = 0; i25 < 50; ++i25) {
            i19 = i4 + this.rand.nextInt(16) + 8;
            i20 = this.rand.nextInt(this.rand.nextInt(120) + 8);
            i21 = i5 + this.rand.nextInt(16) + 8;
            (new WorldGenLiquids(Blocks.WATER.getDefaultState())).generate(world, this.rand, i19, i20, i21);
        }

        for(i25 = 0; i25 < 20; ++i25) {
            i19 = i4 + this.rand.nextInt(16) + 8;
            i20 = this.rand.nextInt(this.rand.nextInt(this.rand.nextInt(112) + 8) + 8);
            i21 = i5 + this.rand.nextInt(16) + 8;
            (new WorldGenLiquids(Blocks.LAVA.getDefaultState())).generate(world, this.rand, i19, i20, i21);
        }

        this.generatedTemperatures = getTemperatures(this.generatedTemperatures, i4 + 8, i5 + 8, 16, 16);

        for(i25 = i4 + 8; i25 < i4 + 8 + 16; ++i25) {
            for(i19 = i5 + 8; i19 < i5 + 8 + 16; ++i19) {
                i20 = i25 - (i4 + 8);
                i21 = i19 - (i5 + 8);
                int i22 = world.getTopY(Heightmap.Type.MOTION_BLOCKING, i25, i19);
                double d23 = this.generatedTemperatures[i20 * 16 + i21] - (double)(i22 - 64) / 64.0D * 0.3D;
                if(d23 < 0.5D && i22 > 0 && i22 < 128 && world.isAir(new BlockPos(i25, i22, i19)) && world.getBlockState(new BlockPos(i25, i22 - 1, i19)).getMaterial().isSolid() && world.getBlockState(new BlockPos(i25, i22 - 1, i19)).getMaterial() != Material.ICE) {
                    world.setBlockState(new BlockPos(i25, i22, i19), Blocks.SNOW.getDefaultState(), Block.NOTIFY_ALL);
                }
            }
        }

        //BlockSand.fallInstantly = false;
    }

    /*public boolean saveChunks(boolean z1, IProgressUpdate iProgressUpdate2) {
        return true;
    }

    public boolean unload100OldestChunks() {
        return false;
    }

    public boolean canSave() {
        return true;
    }

    public String makeString() {
        return "RandomLevelSource";
    }*/

    //World Chunk Manager

    private NoiseGeneratorOctaves2 temperatureGenerator;
    private NoiseGeneratorOctaves2 noise8;
    private NoiseGeneratorOctaves2 noise9;
    public double[] temperature;
    public double[] humidity;
    public double[] field_4196_c;
    public BiomeGenBase[] field_4195_d;
    public BiomeGenBase[] biomes;

    public BiomeGenBase getBiomeGenAt(int i1, int i2) {
        return this.func_4069_a(i1, i2, 1, 1)[0];
    }

    public double getTemperature(int i1, int i2) {
        this.temperature = this.temperatureGenerator.func_4112_a(this.temperature, (double)i1, (double)i2, 1, 1, 0.02500000037252903D, 0.02500000037252903D, 0.5D);
        return this.temperature[0];
    }

    public BiomeGenBase[] func_4069_a(int i1, int i2, int i3, int i4) {
        this.field_4195_d = this.loadBlockGeneratorData(this.field_4195_d, i1, i2, i3, i4);
        return this.field_4195_d;
    }

    public double[] getTemperatures(double[] d1, int i2, int i3, int i4, int i5) {
        if(d1 == null || d1.length < i4 * i5) {
            d1 = new double[i4 * i5];
        }

        d1 = this.temperatureGenerator.func_4112_a(d1, (double)i2, (double)i3, i4, i5, 0.02500000037252903D, 0.02500000037252903D, 0.25D);
        this.field_4196_c = this.noise9.func_4112_a(this.field_4196_c, (double)i2, (double)i3, i4, i5, 0.25D, 0.25D, 0.5882352941176471D);
        int i6 = 0;

        for(int i7 = 0; i7 < i4; ++i7) {
            for(int i8 = 0; i8 < i5; ++i8) {
                double d9 = this.field_4196_c[i6] * 1.1D + 0.5D;
                double d11 = 0.01D;
                double d13 = 1.0D - d11;
                double d15 = (d1[i6] * 0.15D + 0.7D) * d13 + d9 * d11;
                d15 = 1.0D - (1.0D - d15) * (1.0D - d15);
                if(d15 < 0.0D) {
                    d15 = 0.0D;
                }

                if(d15 > 1.0D) {
                    d15 = 1.0D;
                }

                d1[i6] = d15;
                ++i6;
            }
        }

        return d1;
    }

    public BiomeGenBase[] loadBlockGeneratorData(BiomeGenBase[] biomes, int i2, int i3, int i4, int i5) {
        if(biomes == null || biomes.length < i4 * i5) {
            biomes = new BiomeGenBase[i4 * i5];
        }

        this.temperature = this.temperatureGenerator.func_4112_a(this.temperature, (double)i2, (double)i3, i4, i4, 0.02500000037252903D, 0.02500000037252903D, 0.25D);
        this.humidity = this.noise8.func_4112_a(this.humidity, (double)i2, (double)i3, i4, i4, (double)0.05F, (double)0.05F, 0.3333333333333333D);
        this.field_4196_c = this.noise9.func_4112_a(this.field_4196_c, (double)i2, (double)i3, i4, i4, 0.25D, 0.25D, 0.5882352941176471D);
        int i6 = 0;

        for(int i7 = 0; i7 < i4; ++i7) {
            for(int i8 = 0; i8 < i5; ++i8) {
                double d9 = this.field_4196_c[i6] * 1.1D + 0.5D;
                double d11 = 0.01D;
                double d13 = 1.0D - d11;
                double d15 = (this.temperature[i6] * 0.15D + 0.7D) * d13 + d9 * d11;
                d11 = 0.002D;
                d13 = 1.0D - d11;
                double d17 = (this.humidity[i6] * 0.15D + 0.5D) * d13 + d9 * d11;
                d15 = 1.0D - (1.0D - d15) * (1.0D - d15);
                if(d15 < 0.0D) {
                    d15 = 0.0D;
                }

                if(d17 < 0.0D) {
                    d17 = 0.0D;
                }

                if(d15 > 1.0D) {
                    d15 = 1.0D;
                }

                if(d17 > 1.0D) {
                    d17 = 1.0D;
                }

                this.temperature[i6] = d15;
                this.humidity[i6] = d17;
                biomes[i6++] = BiomeGenBase.getBiomeFromLookup(d15, d17);
            }
        }
        return biomes;
    }
}
