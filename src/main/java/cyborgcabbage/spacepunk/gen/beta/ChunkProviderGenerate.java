package cyborgcabbage.spacepunk.gen.beta;

import cyborgcabbage.spacepunk.gen.beta.biome.BiomeGenBase;
import cyborgcabbage.spacepunk.gen.beta.map.MapGenBase;
import cyborgcabbage.spacepunk.gen.beta.map.MapGenCaves;
import cyborgcabbage.spacepunk.gen.beta.noise.NoiseGeneratorOctaves;
import cyborgcabbage.spacepunk.gen.beta.noise.NoiseGeneratorOctaves2;
import cyborgcabbage.spacepunk.gen.beta.worldgen.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.chunk.Chunk;

import java.util.Random;

public class ChunkProviderGenerate {
    private final Random rand;
    private final NoiseGeneratorOctaves noise16a;
    private final NoiseGeneratorOctaves noise16b;
    private final NoiseGeneratorOctaves noise8a;
    private final NoiseGeneratorOctaves noise4a;
    private final NoiseGeneratorOctaves noise4b;
    private final NoiseGeneratorOctaves noise10a;
    private final NoiseGeneratorOctaves noise16c;
    private final NoiseGeneratorOctaves treeNoise;
    private double[] terrainNoiseValues;
    private double[] sandNoise = new double[256];
    private double[] gravelNoise = new double[256];
    private double[] stoneNoise = new double[256];
    private final MapGenBase genCaves = new MapGenCaves();
    private BiomeGenBase[] biomesForGeneration;
    double[] highFreq3d8;
    double[] lowFreq3d16a;
    double[] lowFreq3d16b;
    double[] highFreq2d10;
    double[] lowFreq2d16;
    int[][] field_914_i = new int[32][32];
    private double[] generatedTemperatures;
    final long worldSeed;
    public ChunkProviderGenerate(long seed) {
        this.worldSeed = seed;
        this.temperatureGenerator = new NoiseGeneratorOctaves2(new Random(seed * 9871L), 4);
        this.humidityGenrator = new NoiseGeneratorOctaves2(new Random(seed * 39811L), 4);
        this.noise9 = new NoiseGeneratorOctaves2(new Random(seed * 543321L), 2);
        this.rand = new Random(seed);
        this.noise16a = new NoiseGeneratorOctaves(this.rand, 16);
        this.noise16b = new NoiseGeneratorOctaves(this.rand, 16);
        this.noise8a = new NoiseGeneratorOctaves(this.rand, 8);
        this.noise4a = new NoiseGeneratorOctaves(this.rand, 4);
        this.noise4b = new NoiseGeneratorOctaves(this.rand, 4);
        this.noise10a = new NoiseGeneratorOctaves(this.rand, 10);
        this.noise16c = new NoiseGeneratorOctaves(this.rand, 16);
        this.treeNoise = new NoiseGeneratorOctaves(this.rand, 8);
    }

    public void generateTerrain(Chunk chunk) {
        ChunkPos pos = chunk.getPos();
        byte horizontalNoiseSize = 4;
        byte seaLevel = 64;
        int xNoiseSize = horizontalNoiseSize + 1;
        byte yNoiseSize = 17;
        int zNoiseSIze = horizontalNoiseSize + 1;
        this.terrainNoiseValues = this.generateTerrainNoise(this.terrainNoiseValues, pos.x * horizontalNoiseSize, 0, pos.z * horizontalNoiseSize, xNoiseSize, yNoiseSize, zNoiseSIze);
        BlockPos.Mutable blockPos = new BlockPos.Mutable(0, 0, 0);
        for(int xNoiseIndex = 0; xNoiseIndex < horizontalNoiseSize; ++xNoiseIndex) {
            for(int zNoiseIndex = 0; zNoiseIndex < horizontalNoiseSize; ++zNoiseIndex) {
                for(int yNoiseIndex = 0; yNoiseIndex < 16; ++yNoiseIndex) {
                    double yFrac = 0.125D;
                    double v000 = this.terrainNoiseValues[((xNoiseIndex + 0) * zNoiseSIze + zNoiseIndex + 0) * yNoiseSize + yNoiseIndex + 0];
                    double v010 = this.terrainNoiseValues[((xNoiseIndex + 0) * zNoiseSIze + zNoiseIndex + 1) * yNoiseSize + yNoiseIndex + 0];
                    double v100 = this.terrainNoiseValues[((xNoiseIndex + 1) * zNoiseSIze + zNoiseIndex + 0) * yNoiseSize + yNoiseIndex + 0];
                    double v110 = this.terrainNoiseValues[((xNoiseIndex + 1) * zNoiseSIze + zNoiseIndex + 1) * yNoiseSize + yNoiseIndex + 0];
                    double v001 = (this.terrainNoiseValues[((xNoiseIndex + 0) * zNoiseSIze + zNoiseIndex + 0) * yNoiseSize + yNoiseIndex + 1] - v000) * yFrac;
                    double v011 = (this.terrainNoiseValues[((xNoiseIndex + 0) * zNoiseSIze + zNoiseIndex + 1) * yNoiseSize + yNoiseIndex + 1] - v010) * yFrac;
                    double v101 = (this.terrainNoiseValues[((xNoiseIndex + 1) * zNoiseSIze + zNoiseIndex + 0) * yNoiseSize + yNoiseIndex + 1] - v100) * yFrac;
                    double v111 = (this.terrainNoiseValues[((xNoiseIndex + 1) * zNoiseSIze + zNoiseIndex + 1) * yNoiseSize + yNoiseIndex + 1] - v110) * yFrac;

                    for(int ySub = 0; ySub < 8; ++ySub) {
                        blockPos.setY(yNoiseIndex * 8 + ySub);
                        double xFrac = 0.25D;
                        double d35 = v000;
                        double d37 = v010;
                        double d39 = (v100 - v000) * xFrac;
                        double d41 = (v110 - v010) * xFrac;

                        for(int xSub = 0; xSub < 4; ++xSub) {
                            blockPos.setX(xNoiseIndex * 4 + xSub);
                            double zFrac = 0.25D;
                            double density = d35;
                            double zNoiseStep = (d37 - d35) * zFrac;

                            for(int zSub = 0; zSub < 4; ++zSub) {
                                blockPos.setZ(zNoiseIndex * 4 + zSub);
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
                                density += zNoiseStep;
                            }
                            d35 += d39;
                            d37 += d41;
                        }
                        v000 += v001;
                        v010 += v011;
                        v100 += v101;
                        v110 += v111;
                    }
                }
            }
        }

    }

    public void replaceBlocksForBiome(Chunk chunk, BiomeGenBase[] biomeGenBase4) {
        ChunkPos pos = chunk.getPos();
        byte b5 = 64;
        double scale = 8.0D / 256D;
        this.sandNoise = this.noise4a.generateNoiseOctaves(this.sandNoise, pos.x * 16, pos.z * 16, 0.0D, 16, 16, 1, scale, scale, 1.0D);
        this.gravelNoise = this.noise4a.generateNoiseOctaves(this.gravelNoise, pos.x * 16, 109.0134D, pos.z * 16, 16, 1, 16, scale, 1.0D, scale);
        this.stoneNoise = this.noise4b.generateNoiseOctaves(this.stoneNoise, pos.x * 16, pos.z * 16, 0.0D, 16, 16, 1, scale * 2.0D, scale * 2.0D, scale * 2.0D);

        for(int h1 = 0; h1 < 16; ++h1) {
            for(int h2 = 0; h2 < 16; ++h2) {
                BiomeGenBase biomeGenBase10 = biomeGenBase4[h1 + h2 * 16];
                boolean sand = this.sandNoise[h1 + h2 * 16] + this.rand.nextDouble() * 0.2d > 0.0D;
                boolean gravel = this.gravelNoise[h1 + h2 * 16] + this.rand.nextDouble() * 0.2d > 3.0D;
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
        this.biomes = generateBiomes(this.biomes, pos.x * 16, pos.z * 16, 16, 16);
        this.generateTerrain(chunk);
        this.replaceBlocksForBiome(chunk, this.biomes);
        this.genCaves.generate(chunk, worldSeed);
        //chunk4.func_1024_c();
    }

    private double[] generateTerrainNoise(double[] noiseArray, int xOffset, int yOffset, int zOffset, int xNoiseSize, int yNoiseSize, int zNoiseSize) {
        if(noiseArray == null) {
            noiseArray = new double[xNoiseSize * yNoiseSize * zNoiseSize];
        }

        double hScale = 684.412d;
        double vScale = 684.412d;
        double[] temp = this.temperature;
        double[] humidity = this.humidity;
        this.highFreq2d10 = this.noise10a.func_4109_a(this.highFreq2d10, xOffset, zOffset, xNoiseSize, zNoiseSize, 1.121D, 1.121D, 0.5D);
        this.lowFreq2d16 = this.noise16c.func_4109_a(this.lowFreq2d16, xOffset, zOffset, xNoiseSize, zNoiseSize, 200.0D, 200.0D, 0.5D);
        this.highFreq3d8 = this.noise8a.generateNoiseOctaves(this.highFreq3d8, xOffset, yOffset, zOffset, xNoiseSize, yNoiseSize, zNoiseSize, hScale / 80.0D, vScale / 160.0D, hScale / 80.0D);
        this.lowFreq3d16a = this.noise16a.generateNoiseOctaves(this.lowFreq3d16a, xOffset, yOffset, zOffset, xNoiseSize, yNoiseSize, zNoiseSize, hScale, vScale, hScale);
        this.lowFreq3d16b = this.noise16b.generateNoiseOctaves(this.lowFreq3d16b, xOffset, yOffset, zOffset, xNoiseSize, yNoiseSize, zNoiseSize, hScale, vScale, hScale);
        int noiseIndex = 0;
        int noiseIndex2 = 0;
        int samplePeriod = 16 / xNoiseSize;

        for(int xNoiseIndex = 0; xNoiseIndex < xNoiseSize; ++xNoiseIndex) {
            int xSample = xNoiseIndex * samplePeriod + samplePeriod / 2;
            for(int zNoiseIndex = 0; zNoiseIndex < zNoiseSize; ++zNoiseIndex) {
                int zSample = zNoiseIndex * samplePeriod + samplePeriod / 2;
                double tempVal = temp[xSample * 16 + zSample];
                double humidityVal = humidity[xSample * 16 + zSample] * tempVal;
                humidityVal = 1.0D - humidityVal;
                humidityVal *= humidityVal;
                humidityVal *= humidityVal;
                humidityVal = 1.0D - humidityVal;
                double highFreqHumid = (this.highFreq2d10[noiseIndex2] + 256.0D) / 512.0D;
                highFreqHumid *= humidityVal;
                if(highFreqHumid > 1.0D) {
                    highFreqHumid = 1.0D;
                }

                double lowFreq2d3 = this.lowFreq2d16[noiseIndex2] / 8000.0D;
                if(lowFreq2d3 < 0.0D) {
                    lowFreq2d3 = -lowFreq2d3 * 0.3d;
                }

                lowFreq2d3 = lowFreq2d3 * 3.0D - 2.0D;
                if(lowFreq2d3 < 0.0D) {
                    lowFreq2d3 /= 2.0D;
                    if(lowFreq2d3 < -1.0D) {
                        lowFreq2d3 = -1.0D;
                    }

                    lowFreq2d3 /= 1.4D;
                    lowFreq2d3 /= 2.0D;
                    highFreqHumid = 0.0D;
                } else {
                    if(lowFreq2d3 > 1.0D) {
                        lowFreq2d3 = 1.0D;
                    }

                    lowFreq2d3 /= 8.0D;
                }

                if(highFreqHumid < 0.0D) {
                    highFreqHumid = 0.0D;
                }

                highFreqHumid += 0.5D;
                lowFreq2d3 = lowFreq2d3 * (double)yNoiseSize / 16.0D;
                double d31 = (double)yNoiseSize / 2.0D + lowFreq2d3 * 4.0D;
                ++noiseIndex2;

                for(int yNoiseIndex = 0; yNoiseIndex < yNoiseSize; ++yNoiseIndex) {
                    double bias = ((double)yNoiseIndex - d31) * 12.0D / highFreqHumid;
                    if(bias < 0.0D) {
                        bias *= 4.0D;
                    }

                    double a = this.lowFreq3d16a[noiseIndex] / 512.0D;
                    double b = this.lowFreq3d16b[noiseIndex] / 512.0D;
                    double mix = this.highFreq3d8[noiseIndex] / 20.0D + 0.5D;
                    double noiseValue;
                    if(mix < 0.0D) {
                        noiseValue = a;
                    } else if(mix > 1.0D) {
                        noiseValue = b;
                    } else {
                        noiseValue = a + (b - a) * mix;
                    }
                    noiseValue -= bias;
                    //Fall-off
                    if(yNoiseIndex > yNoiseSize - 4) {
                        double d44 = (float)(yNoiseIndex - (yNoiseSize - 4)) / 3.0F;
                        noiseValue = noiseValue * (1.0D - d44) + -10.0D * d44;
                    }

                    noiseArray[noiseIndex] = noiseValue;
                    ++noiseIndex;
                }
            }
        }

        return noiseArray;
    }

    public void populate(StructureWorldAccess world, Chunk chunk) {
        //BlockSand.fallInstantly = true;
        int i2 = chunk.getPos().x;
        int i3 = chunk.getPos().z;
        int i4 = i2 * 16;
        int i5 = i3 * 16;
        BiomeGenBase biomeGenBase6 = getBiomeAtBlock(i4 + 16, i5 + 16);
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
        i13 = (int)((this.treeNoise.func_806_a((double)i4 * d11, (double)i5 * d11) / 8.0D + this.rand.nextDouble() * 4.0D + 4.0D) / 3.0D);
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

        byte dandelionAmount = 0;
        if(biomeGenBase6 == BiomeGenBase.forest) {
            dandelionAmount = 2;
        }

        if(biomeGenBase6 == BiomeGenBase.seasonalForest) {
            dandelionAmount = 4;
        }

        if(biomeGenBase6 == BiomeGenBase.taiga) {
            dandelionAmount = 2;
        }

        if(biomeGenBase6 == BiomeGenBase.plains) {
            dandelionAmount = 3;
        }

        int i19;
        int i25;
        for(i16 = 0; i16 < dandelionAmount; ++i16) {
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
                double d23 = this.generatedTemperatures[i20 * 16 + i21] - (double)(i22 - 64) / 64.0D * 0.3d;
                if(d23 < 0.5D && i22 > 0 && i22 < 128 && world.isAir(new BlockPos(i25, i22, i19)) && world.getBlockState(new BlockPos(i25, i22 - 1, i19)).getMaterial().isSolid() && world.getBlockState(new BlockPos(i25, i22 - 1, i19)).getMaterial() != Material.ICE) {
                    world.setBlockState(new BlockPos(i25, i22, i19), Blocks.SNOW.getDefaultState(), Block.NOTIFY_ALL);
                }
            }
        }

        //BlockSand.fallInstantly = false;
    }

    //World Chunk Manager

    private final NoiseGeneratorOctaves2 temperatureGenerator;
    private final NoiseGeneratorOctaves2 humidityGenrator;
    private final NoiseGeneratorOctaves2 noise9;
    public double[] temperature;
    public double[] humidity;
    public double[] biomeJitter;
    public BiomeGenBase[] biomeRegion;
    public BiomeGenBase[] biomes;

    public BiomeGenBase getBiomeAtBlock(int x, int z) {
        return this.getBiomesInRegion(x, z, 1, 1)[0];
    }

    public double getTemperature(int i1, int i2) {
        this.temperature = this.temperatureGenerator.func_4112_a(this.temperature, i1, i2, 1, 1, 0.02500000037252903d, 0.02500000037252903d, 0.5D);
        return this.temperature[0];
    }

    public BiomeGenBase[] getBiomesInRegion(int xPos, int zPos, int xSize, int zSize) {
        this.biomeRegion = this.generateBiomes(this.biomeRegion, xPos, zPos, xSize, zSize);
        return this.biomeRegion;
    }

    public double[] getTemperatures(double[] d1, int i2, int i3, int i4, int i5) {
        if(d1 == null || d1.length < i4 * i5) {
            d1 = new double[i4 * i5];
        }

        d1 = this.temperatureGenerator.func_4112_a(d1, i2, i3, i4, i5, 0.02500000037252903d, 0.02500000037252903d, 0.25D);
        this.biomeJitter = this.noise9.func_4112_a(this.biomeJitter, i2, i3, i4, i5, 0.25D, 0.25D, 0.5882352941176471D);
        int i6 = 0;

        for(int i7 = 0; i7 < i4; ++i7) {
            for(int i8 = 0; i8 < i5; ++i8) {
                double d9 = this.biomeJitter[i6] * 1.1D + 0.5D;
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

    public BiomeGenBase[] generateBiomes(BiomeGenBase[] biomes, int xChunk, int zChunk, int xSize, int zSize) {
        if(biomes == null || biomes.length < xSize * zSize) {
            biomes = new BiomeGenBase[xSize * zSize];
        }

        this.temperature = this.temperatureGenerator.func_4112_a(this.temperature, xChunk, zChunk, xSize, xSize, 0.02500000037252903d, 0.02500000037252903d, 0.25D);
        this.humidity = this.humidityGenrator.func_4112_a(this.humidity, xChunk, zChunk, xSize, xSize, 0.05F, 0.05F, 0.3333333333333333d);
        this.biomeJitter = this.noise9.func_4112_a(this.biomeJitter, xChunk, zChunk, xSize, xSize, 0.25D, 0.25D, 0.5882352941176471D);
        int i = 0;

        for(int k = 0; k < xSize; ++k) {
            for(int l = 0; l < zSize; ++l) {
                double d9 = this.biomeJitter[i] * 1.1D + 0.5D;
                double weightTemp = 0.01D;
                double tempValue = (this.temperature[i] * 0.15D + 0.7D) * (1.0D - weightTemp) + d9 * weightTemp;
                double weightHumid = 0.002d;
                double humidValue = (this.humidity[i] * 0.15D + 0.5D) * (1.0D - weightHumid) + d9 * weightHumid;
                tempValue = 1.0D - (1.0D - tempValue) * (1.0D - tempValue);
                tempValue = MathHelper.clamp(tempValue,0,1);
                humidValue = MathHelper.clamp(humidValue, 0, 1);
                this.temperature[i] = tempValue;
                this.humidity[i] = humidValue;
                biomes[i++] = BiomeGenBase.getBiomeFromLookup(tempValue, humidValue);
            }
        }
        return biomes;
    }
}
