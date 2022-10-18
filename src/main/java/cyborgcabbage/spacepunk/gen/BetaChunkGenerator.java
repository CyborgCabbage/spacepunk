package cyborgcabbage.spacepunk.gen;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.structure.StructureSet;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.Blender;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import net.minecraft.world.gen.noise.NoiseConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class BetaChunkGenerator extends ChunkGenerator {
    public static final Codec<BetaChunkGenerator> CODEC = RecordCodecBuilder.create(instance -> BetaChunkGenerator.createStructureSetRegistryGetter(instance).and(RegistryOps.createRegistryCodec(Registry.BIOME_KEY).forGetter(BetaChunkGenerator -> BetaChunkGenerator.biomeRegistry)).apply(instance, instance.stable(BetaChunkGenerator::new)));
    private final Registry<Biome> biomeRegistry;

    private List<BlockState> layers = Collections.nCopies(50, Blocks.STONE.getDefaultState());

    public BetaChunkGenerator(Registry<StructureSet> structureSetRegistry, Registry<Biome> biomeRegistry) {
        super(structureSetRegistry, Optional.empty(), new FixedBiomeSource(biomeRegistry.getOrCreateEntry(BiomeKeys.PLAINS)));
        this.biomeRegistry = biomeRegistry;
    }

    public Registry<Biome> getBiomeRegistry() {
        return this.biomeRegistry;
    }

    @Override
    protected Codec<? extends ChunkGenerator> getCodec() {
        return CODEC;
    }

    @Override
    public void carve(ChunkRegion chunkRegion, long seed, NoiseConfig noiseConfig, BiomeAccess biomeAccess, StructureAccessor structureAccessor, Chunk chunk, GenerationStep.Carver carverStep) {

    }

    @Override
    public void buildSurface(ChunkRegion region, StructureAccessor structures, NoiseConfig noiseConfig, Chunk chunk) {

    }

    @Override
    public void populateEntities(ChunkRegion region) {

    }

    @Override
    public int getWorldHeight() {
        return 384;
    }

    @Override
    public CompletableFuture<Chunk> populateNoise(Executor executor, Blender blender, NoiseConfig noiseConfig, StructureAccessor structureAccessor, Chunk chunk) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        Heightmap oceanFloor = chunk.getHeightmap(Heightmap.Type.OCEAN_FLOOR_WG);
        Heightmap worldSurface = chunk.getHeightmap(Heightmap.Type.WORLD_SURFACE_WG);
        for (int i = 0; i < Math.min(chunk.getHeight(), layers.size()); ++i) {
            BlockState blockState = layers.get(i);
            if (blockState == null) continue;
            int j = chunk.getBottomY() + i;
            for (int k = 0; k < 16; ++k) {
                for (int l = 0; l < 16; ++l) {
                    chunk.setBlockState(mutable.set(k, j, l), blockState, false);
                    oceanFloor.trackUpdate(k, j, l, blockState);
                    worldSurface.trackUpdate(k, j, l, blockState);
                }
            }
        }
        return CompletableFuture.completedFuture(chunk);
    }

    @Override
    public int getSeaLevel() {
        return -63;
    }

    @Override
    public int getMinimumY() {
        return 0;
    }

    @Override
    public int getHeight(int x, int z, Heightmap.Type heightmap, HeightLimitView world, NoiseConfig noiseConfig) {
        for (int i = Math.min(layers.size(), world.getTopY()) - 1; i >= 0; --i) {
            BlockState blockState = layers.get(i);
            if (blockState == null || !heightmap.getBlockPredicate().test(blockState)) continue;
            return world.getBottomY() + i + 1;
        }
        return world.getBottomY();
    }

    @Override
    public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView world, NoiseConfig noiseConfig) {
        return new VerticalBlockSample(world.getBottomY(), layers.stream().limit(world.getHeight()).map(state -> state == null ? Blocks.AIR.getDefaultState() : state).toArray(BlockState[]::new));
    }

    @Override
    public void getDebugHudText(List<String> text, NoiseConfig noiseConfig, BlockPos pos) {
    }
}
