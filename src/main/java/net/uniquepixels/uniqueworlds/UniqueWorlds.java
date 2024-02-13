package net.uniquepixels.uniqueworlds;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.TranslationRegistry;
import net.uniquepixels.core.paper.chat.chatinput.ChatInputManager;
import net.uniquepixels.core.paper.gui.backend.UIHolder;
import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class UniqueWorlds extends JavaPlugin {

  private ChatInputManager chatInputManager;

  public ChatInputManager getChatInputManager() {
    return chatInputManager;
  }

  @Override
  public void onEnable() {

    ResourceBundle bundle = ResourceBundle.getBundle("translations");
    TranslationRegistry registry = TranslationRegistry.create(Key.key("uniqueworlds"));
    registry.registerAll(Locale.ENGLISH, bundle, true);
    GlobalTranslator.translator().addSource(registry);

    RegisteredServiceProvider<UIHolder> uiProvider = Bukkit.getServicesManager().getRegistration(UIHolder.class);

    if (uiProvider == null)
      return;

    /*
     * UI workflow to open and manage current ui's (extend ChestUI for custom inventories)
     * */
    UIHolder uiHolder = uiProvider.getProvider();

    RegisteredServiceProvider<ChatInputManager> chatProvider = Bukkit.getServicesManager().getRegistration(ChatInputManager.class);

    if (chatProvider == null)
      return;

    /*
     * Use ChatInputManager to get the next chat message from player and add actions after a message has been sent
     * */
    this.chatInputManager = chatProvider.getProvider();

    getCommand("world").setExecutor(new WorldCommand(uiHolder));

    this.loadWorlds();
  }

  private void loadWorlds() {

    List<String> exclude = List.of("cache", "config", "libraries", "logs", "plugins", "versions", "data", ".");

    File worldContainer = Bukkit.getWorldContainer();
    try {
      Files.walk(worldContainer.toPath(), 1, FileVisitOption.FOLLOW_LINKS).filter(path -> path.toFile().isDirectory())
        .filter(path -> !exclude.contains(path.toFile().getName())).forEach(path -> {

          String worldName = path.toFile().getName();

          if (Bukkit.getWorld(worldName) == null) {

            WorldCreator.name(worldName).createWorld();

          }

        });
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

  }

  @Override
  public void onDisable() {

  }
}
