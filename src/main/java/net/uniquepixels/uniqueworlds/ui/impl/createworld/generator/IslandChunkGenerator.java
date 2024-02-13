package net.uniquepixels.uniqueworlds.ui.impl.createworld.generator;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;
import org.jetbrains.annotations.NotNull;

public class IslandChunkGenerator extends ChunkGenerator {

  @NotNull
  @Override
  public ChunkData createVanillaChunkData(@NotNull World world, int chunkX, int chunkZ) {
    ChunkData chunkData = Bukkit.getServer().createChunkData(world);

    for (int x = 0; x < 3; x++) {
      for (int z = 0; z < 3; z++) {
        chunkData.setBlock(x, 0, z, Material.GRASS_BLOCK);
      }
    }

    return chunkData;
  }
}
