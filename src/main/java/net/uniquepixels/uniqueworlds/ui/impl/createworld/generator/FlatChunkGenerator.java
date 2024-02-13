package net.uniquepixels.uniqueworlds.ui.impl.createworld.generator;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;
import org.jetbrains.annotations.NotNull;

public class FlatChunkGenerator extends ChunkGenerator {

  private final int dirtSpace;

  public FlatChunkGenerator(int dirtSpace) {
    this.dirtSpace = dirtSpace;
  }

  @NotNull
  @Override
  public ChunkData createVanillaChunkData(@NotNull World world, int chunkX, int chunkZ) {
    ChunkData chunkData = Bukkit.getServer().createChunkData(world);

    for (int x = 0; x < 16; x++) {
      for (int z = 0; z < 16; z++) {
        chunkData.setBlock(x, 0, z, Material.BEDROCK);
      }
    }

    for (int x = 0; x < 16; x++) {
      for (int z = 0; z < 16; z++) {
        for (int y = 1; y < this.dirtSpace; y++) {
          chunkData.setBlock(x, 1, z, Material.DIRT);
        }
      }
    }

    for (int x = 0; x < 16; x++) {
      for (int z = 0; z < 16; z++) {
        chunkData.setBlock(x, this.dirtSpace + 1, z, Material.GRASS_BLOCK);
      }
    }

    return chunkData;
  }
}
