package net.uniquepixels.uniqueworlds.ui.impl.createworld.generator;

import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

public class FlatChunkGenerator extends ChunkGenerator {

  private final int dirtSpace;

  public FlatChunkGenerator(int dirtSpace) {
    this.dirtSpace = dirtSpace;
  }


  @Override
  public void generateNoise(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull ChunkGenerator.ChunkData chunkData) {
    // empty
  }

  @Override
  public @Nullable BiomeProvider getDefaultBiomeProvider(@NotNull WorldInfo worldInfo) {
    return new BiomeProvider() {
      @Override
      public @NotNull Biome getBiome(@NotNull WorldInfo worldInfo, int x, int y, int z) {
        return Biome.PLAINS;
      }

      @Override
      public @NotNull List<Biome> getBiomes(@NotNull WorldInfo worldInfo) {
        return List.of(Biome.PLAINS);
      }
    };
  }

  @Override
  public void generateSurface(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull ChunkGenerator.ChunkData chunkData) {
    for (int x = 0; x < 16; x++) {
      for (int z = 0; z < 16; z++) {
        for (int y = 0; y < this.dirtSpace; y++) {
          chunkData.setBlock(x, y, z, Material.DIRT);
        }
      }
    }

    for (int x = 0; x < 16; x++) {
      for (int z = 0; z < 16; z++) {
        chunkData.setBlock(x, this.dirtSpace + 1, z, Material.GRASS_BLOCK);
      }
    }
  }

  @Override
  public void generateBedrock(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull ChunkGenerator.ChunkData chunkData) {
    for (int x = 0; x < 16; x++) {
      for (int z = 0; z < 16; z++) {
        chunkData.setBlock(x, 0, z, Material.BEDROCK);
      }
    }
  }

  @Override
  public void generateCaves(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull ChunkGenerator.ChunkData chunkData) {
    // empty
  }
}
