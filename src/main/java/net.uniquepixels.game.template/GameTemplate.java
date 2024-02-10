package net.uniquepixels.game.template;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import net.uniquepixels.core.paper.gui.backend.UIHolder;
import net.uniquepixels.core.paper.chat.chatinput.ChatInputManager;

public class GameTemplate extends JavaPlugin {

  @Override
  public void onEnable() {

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
    ChatInputManager chatInputManager = chatProvider.getProvider();

  }

  @Override
  public void onDisable() {

  }
}
