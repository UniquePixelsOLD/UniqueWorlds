package net.uniquepixels.uniqueworlds;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.uniquepixels.core.paper.gui.backend.UIHolder;
import net.uniquepixels.uniqueworlds.ui.UIStyle;
import net.uniquepixels.uniqueworlds.ui.impl.ListWorldsUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class WorldCommand implements CommandExecutor {

  private final UIHolder uiHolder;

  public WorldCommand(UIHolder uiHolder) {
    this.uiHolder = uiHolder;
  }

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

    if (!sender.hasPermission("uniquebuilds.command.world")) {
      sender.sendMessage(UIStyle.PREFIX.append(Component.translatable("no.permission").color(NamedTextColor.RED)));
      return true;
    }

    if (!(sender instanceof Player player))
      return true;

    this.uiHolder.open(new ListWorldsUI(this.uiHolder), player);

    return true;
  }
}
