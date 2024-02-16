package net.uniquepixels.uniqueworlds;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.TranslationRegistry;
import net.uniquepixels.core.paper.chat.chatinput.ChatInputManager;
import net.uniquepixels.core.paper.gui.backend.UIHolder;
import net.uniquepixels.uniqueworlds.ui.impl.createworld.CreateWorldOptions;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Locale;
import java.util.ResourceBundle;

public class UniqueWorlds extends JavaPlugin {

  private ChatInputManager chatInputManager;
  private ConfigWorld configWorld;

  public ConfigWorld getConfigWorld() {
    return configWorld;
  }

  public ChatInputManager getChatInputManager() {
    return chatInputManager;
  }

  @Override
  public void onEnable() {

    ResourceBundle bundle = ResourceBundle.getBundle("translations");
    TranslationRegistry registry = TranslationRegistry.create(Key.key("uniqueworlds"));
    registry.registerAll(Locale.ENGLISH, bundle, true);
    GlobalTranslator.translator().addSource(registry);

    this.saveDefaultConfig();

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

    this.configWorld = new ConfigWorld(this.getConfig());

    getCommand("world").setExecutor(new WorldCommand(uiHolder));

    this.loadWorlds();
  }

  private void loadWorlds() {

    ConfigurationSection worlds = this.getConfig().getConfigurationSection("worlds");

    if (worlds == null)
      Bukkit.getWorlds().forEach(world -> this.configWorld.saveWorld(world, CreateWorldOptions.DEFAULT));

    for (ConfigWorld.UnloadedWorldData worldData : this.configWorld.loadWorlds())
      worldData.loadWorld();


  }

  @Override
  public void onDisable() {

  }
}
