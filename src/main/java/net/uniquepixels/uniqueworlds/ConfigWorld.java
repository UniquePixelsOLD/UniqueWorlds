package net.uniquepixels.uniqueworlds;

import net.uniquepixels.uniqueworlds.ui.impl.createworld.CreateWorldOptions;
import net.uniquepixels.uniqueworlds.ui.impl.createworld.generator.FlatChunkGenerator;
import net.uniquepixels.uniqueworlds.ui.impl.createworld.generator.IslandChunkGenerator;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ConfigWorld {

  private final FileConfiguration config;

  public ConfigWorld(FileConfiguration config) {
    this.config = config;
  }

  public void saveWorld(World world, CreateWorldOptions options) {

    config.set("worlds." + world.getUID() + ".name", world.getName());
    config.set("worlds." + world.getUID() + ".environment", world.getEnvironment().name());
    config.set("worlds." + world.getUID() + ".generator", options.name());

    UniqueWorlds.getPlugin(UniqueWorlds.class).saveConfig();
  }

  public void removeWorld(World world) {

    config.set("worlds." + world.getUID(), null);
    UniqueWorlds.getPlugin(UniqueWorlds.class).saveConfig();

  }

  public List<UnloadedWorldData> loadWorlds() {

    List<UnloadedWorldData> worldData = new ArrayList<>();

    ConfigurationSection section = config.getConfigurationSection("worlds");

    if (section == null)
      return worldData;

    Set<String> worlds = section.getKeys(false);

    for (String world : worlds) {

      UUID uuid = UUID.fromString(world);
      String worldName = config.getString("worlds." + uuid + ".name");
      World.Environment worldEnvironment = World.Environment.valueOf(config.getString("worlds." + uuid + ".environment"));
      CreateWorldOptions worldGen = CreateWorldOptions.valueOf(config.getString("worlds." + uuid + ".generator"));

      worldData.add(new UnloadedWorldData(worldName, uuid, worldEnvironment, worldGen));

    }

    return worldData;
  }

  public record UnloadedWorldData(String name, UUID uid, World.Environment environment, CreateWorldOptions options) {

    public void loadWorld() {

      WorldCreator worldCreator = this.loadWorldInWorldCreator();
      if (worldCreator == null)
        return;

      worldCreator.createWorld();
    }

    private WorldCreator loadWorldInWorldCreator() {

      if (this.checkIfWorldIsLoaded())
        return null;


      WorldCreator worldCreator = WorldCreator.name(name).environment(environment);

      switch (options) {
        case FLAT -> {
          return worldCreator.generator(new FlatChunkGenerator(1));
        }
        case BUILDERS_READY -> {
          return worldCreator.generator(new FlatChunkGenerator(30));
        }
        case ISLAND -> {
          return worldCreator.generator(new IslandChunkGenerator());
        }
        default -> {
          return worldCreator;
        }
      }

    }

    private boolean checkIfWorldIsLoaded() {
      return !Bukkit.getWorlds().stream().filter(world -> world.getUID().equals(uid)).toList().isEmpty();
    }

  }
}
