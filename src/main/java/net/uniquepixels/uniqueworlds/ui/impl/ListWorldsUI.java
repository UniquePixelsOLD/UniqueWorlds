package net.uniquepixels.uniqueworlds.ui.impl;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.uniquepixels.core.paper.gui.UIRow;
import net.uniquepixels.core.paper.gui.UISlot;
import net.uniquepixels.core.paper.gui.background.UIBackground;
import net.uniquepixels.core.paper.gui.exception.OutOfInventoryException;
import net.uniquepixels.core.paper.gui.item.UIItem;
import net.uniquepixels.core.paper.gui.types.chest.ChestUI;
import net.uniquepixels.core.paper.item.DefaultItemStackBuilder;
import net.uniquepixels.uniqueworlds.UniqueWorlds;
import net.uniquepixels.uniqueworlds.ui.UIHeads;
import net.uniquepixels.uniqueworlds.ui.UIStyle;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class ListWorldsUI extends ChestUI {

  private final NamespacedKey worldId = new NamespacedKey(JavaPlugin.getPlugin(UniqueWorlds.class), "world-id");
  private boolean removeWorlds = false;

  public ListWorldsUI() {
    super(Component.translatable("ui.world.title").color(UIStyle.DEFAULT_COLOR), UIRow.CHEST_ROW_6);
  }

  private ItemStack getItemForWorld(World world, Component title) {

    DefaultItemStackBuilder<?> itemStack = new DefaultItemStackBuilder<>(Material.PAPER);

    switch (world.getEnvironment()) {
      case NORMAL -> itemStack = new DefaultItemStackBuilder<>(Material.GRASS_BLOCK);
      case NETHER -> itemStack = new DefaultItemStackBuilder<>(Material.NETHERRACK);
      case THE_END -> itemStack = new DefaultItemStackBuilder<>(Material.END_STONE);
      case CUSTOM -> itemStack = new DefaultItemStackBuilder<>(Material.DIRT_PATH);
    }

    return itemStack
      .displayName(title)
      .addData(this.worldId, PersistentDataType.STRING, world.getUID().toString())
      .applyItemMeta()
      .buildItem();
  }

  @Override
  protected void initItems(Player player) throws OutOfInventoryException {

    Locale locale = player.locale();

    for (int slot = 0; slot < Bukkit.getWorlds().size(); slot++) {

      World world = Bukkit.getWorlds().get(slot);

      item(new UIItem(this.getItemForWorld(world, Component.text(world.getName())), UISlot.fromSlotId(slot).orElse(UISlot.SLOT_0)),
        (player1, uiItem, clickType, inventoryClickEvent) -> {

          String rawWorldId = new DefaultItemStackBuilder<>(uiItem.getItemStack()).getData(this.worldId, PersistentDataType.STRING);

          World clickedWorld = Bukkit.getWorld(UUID.fromString(rawWorldId));

          switch (clickType) {
            case LEFT -> {
              player1.closeInventory();
              Location loc = new Location(clickedWorld, player1.getX(), player1.getY(), player1.getZ());
              player1.teleportAsync(loc);
              player1.playSound(loc, Sound.ENTITY_ENDER_PEARL_THROW, 30f, 1f);
              player1.sendMessage(UIStyle.PREFIX.append(Component.text("teleported!")));
            }
            case RIGHT -> {

            }
          }

          return true;
        });

    }

    item(new UIItem(
        new DefaultItemStackBuilder<>(UIHeads.PLUS_HEAD.clone())
          .displayName(UIStyle.UI_ARROW.append(
            UIStyle.translate(locale, "ui.world.add.title").color(NamedTextColor.GREEN)
          ))
          .addLoreLine(
            UIStyle.leftClick(locale)
              .append(UIStyle.MINUS.append(UIStyle.translate(locale, "ui.world.add.lore").color(NamedTextColor.GRAY)))
          )
          .applyItemMeta()
          .buildItem(), UISlot.SLOT_45),
      (player1, uiItem, clickType, inventoryClickEvent) -> {


        return true;
      });

    Component removeLore = UIStyle.leftClick(locale)
      .append(UIStyle.MINUS.append(UIStyle.translate(locale, "ui.world.remove.lore", () -> UIStyle.translate(locale, "action.disabled").color(NamedTextColor.RED)).color(NamedTextColor.GRAY)));

    if (this.removeWorlds)
      removeLore = UIStyle.leftClick(locale)
        .append(UIStyle.MINUS.append(UIStyle.translate(locale, "ui.world.remove.lore", () -> UIStyle.translate(locale, "action.enabled").color(NamedTextColor.GREEN)).color(NamedTextColor.GRAY)));

    item(new UIItem(
        new DefaultItemStackBuilder<>(UIHeads.REMOVE_HEAD.clone())
          .displayName(UIStyle.UI_ARROW.append(
            UIStyle.translate(locale, "ui.world.remove.title").color(NamedTextColor.RED)
          ))
          .addLoreLine(removeLore)
          .applyItemMeta()
          .buildItem(), UISlot.SLOT_46),
      (player1, uiItem, clickType, inventoryClickEvent) -> {

        player1.sendMessage("not impl yet!");

        return true;
      });

    setBackground(new UIBackground(UIBackground.BackgroundType.BOTTOM_LINE, List.of(
      new UIItem(
        new DefaultItemStackBuilder<>(Material.GRAY_STAINED_GLASS_PANE)
          .displayName(Component.empty())
          .addFlags(ItemFlag.values())
          .applyItemMeta().buildItem(), UISlot.SLOT_0
      )
    )));

  }

  @Override
  public void onClose(Player player) {

  }

  @Override
  public boolean allowItemMovementInOtherInventories() {
    return false;
  }
}
