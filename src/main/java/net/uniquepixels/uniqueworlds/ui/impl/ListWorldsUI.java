package net.uniquepixels.uniqueworlds.ui.impl;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.translation.GlobalTranslator;
import net.uniquepixels.core.paper.chat.chatinput.ChatInput;
import net.uniquepixels.core.paper.gui.UIRow;
import net.uniquepixels.core.paper.gui.UISlot;
import net.uniquepixels.core.paper.gui.backend.UIHolder;
import net.uniquepixels.core.paper.gui.background.UIBackground;
import net.uniquepixels.core.paper.gui.exception.OutOfInventoryException;
import net.uniquepixels.core.paper.gui.item.UIItem;
import net.uniquepixels.core.paper.gui.types.chest.ChestUI;
import net.uniquepixels.core.paper.item.DefaultItemStackBuilder;
import net.uniquepixels.uniqueworlds.UniqueWorlds;
import net.uniquepixels.uniqueworlds.ui.UIHeads;
import net.uniquepixels.uniqueworlds.ui.UIStyle;
import net.uniquepixels.uniqueworlds.ui.impl.createworld.CreateWorldUI;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class ListWorldsUI extends ChestUI {

  private final NamespacedKey worldId = new NamespacedKey(JavaPlugin.getPlugin(UniqueWorlds.class), "world-id");
  private final UIHolder uiHolder;
  private boolean removeWorlds = false;

  public ListWorldsUI(UIHolder uiHolder) {
    super(Component.translatable("ui.world.title").color(UIStyle.DEFAULT_COLOR), UIRow.CHEST_ROW_6);
    this.uiHolder = uiHolder;
  }

  private ItemStack getItemForWorld(World world, Component title, Locale locale) {

    DefaultItemStackBuilder<?> itemStack = new DefaultItemStackBuilder<>(Material.PAPER);

    switch (world.getEnvironment()) {
      case NORMAL -> itemStack = new DefaultItemStackBuilder<>(Material.GRASS_BLOCK);
      case NETHER -> itemStack = new DefaultItemStackBuilder<>(Material.NETHERRACK);
      case THE_END -> itemStack = new DefaultItemStackBuilder<>(Material.END_STONE);
      case CUSTOM -> itemStack = new DefaultItemStackBuilder<>(Material.DIRT_PATH);
    }

    if (this.removeWorlds)
      return itemStack
        .displayName(title.color(NamedTextColor.GRAY))
        .addData(this.worldId, PersistentDataType.STRING, world.getUID().toString())
        .addLoreLine(
          UIStyle.leftClick(locale).append(UIStyle.MINUS.append(GlobalTranslator.render(Component.translatable("ui.world.entry.delete").color(NamedTextColor.RED), locale)))
        )
        .applyItemMeta()
        .buildItem();

    return itemStack
      .displayName(title.color(NamedTextColor.GRAY))
      .addData(this.worldId, PersistentDataType.STRING, world.getUID().toString())
      .addLoreLine(
        UIStyle.leftClick(locale).append(UIStyle.MINUS.append(GlobalTranslator.render(Component.translatable("ui.world.entry.lore1").color(NamedTextColor.GRAY), locale)))
      )
      .addLoreLine(
        UIStyle.rightClick(locale).append(UIStyle.MINUS.append(GlobalTranslator.render(Component.translatable("ui.world.entry.lore2").color(NamedTextColor.GRAY), locale)))
      )
      .addLoreLine(
        UIStyle.middleClick(locale).append(UIStyle.MINUS.append(GlobalTranslator.render(Component.translatable("ui.world.entry.lore3").color(NamedTextColor.GRAY), locale)))
      )
      .applyItemMeta()
      .buildItem();
  }

  private void openInventorySync(Player player) {
    Bukkit.getScheduler().getMainThreadExecutor(JavaPlugin.getPlugin(UniqueWorlds.class)).execute(() -> {
      this.uiHolder.open(new ListWorldsUI(this.uiHolder), player);
    });
  }

  @Override
  protected void initItems(Player player) throws OutOfInventoryException {

    Locale locale = player.locale();

    for (int slot = 0; slot < Bukkit.getWorlds().size(); slot++) {

      World world = Bukkit.getWorlds().get(slot);

      item(new UIItem(this.getItemForWorld(world, Component.text(world.getName()), player.locale()), UISlot.fromSlotId(slot).orElse(UISlot.SLOT_0)),
        (player1, uiItem, clickType, inventoryClickEvent) -> {

          String rawWorldId = new DefaultItemStackBuilder<>(uiItem.getItemStack()).getData(this.worldId, PersistentDataType.STRING);

          World clickedWorld = Bukkit.getWorld(UUID.fromString(rawWorldId));

          if (clickedWorld == null) {
            player1.sendMessage(UIStyle.PREFIX.append(Component.translatable("ui.world.notfound").color(NamedTextColor.RED)));
            player1.playSound(player1.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 30f, 1f);
            return true;
          }

          if (this.removeWorlds && clickType.isLeftClick()) {

            if (Bukkit.getWorlds().size() == 1) {
              player1.playSound(player1.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 30f, 1f);
              return true;
            }

            player1.closeInventory();
            player1.sendMessage(UIStyle.PREFIX.append(
              Component.translatable("message.delete.world").arguments(Component.text(clickedWorld.getName())).color(NamedTextColor.GRAY)
                .append(Component.space().append(Component.translatable("message.delete.world.plus").color(NamedTextColor.GREEN)))
            ));

            Bukkit.unloadWorld(clickedWorld, true);

            JavaPlugin.getPlugin(UniqueWorlds.class).getConfigWorld().removeWorld(clickedWorld);

            Bukkit.getScheduler().runTaskAsynchronously(JavaPlugin.getPlugin(UniqueWorlds.class), () -> {
              File worldContainer = Bukkit.getWorldContainer();
              new File(worldContainer, world.getName()).delete();


              this.openInventorySync(player1);
            });
            return true;
          }

          switch (clickType) {
            case LEFT -> {
              player1.closeInventory();
              Location loc = new Location(clickedWorld, player1.getX(), player1.getY(), player1.getZ());
              player1.teleportAsync(loc);
              player1.playSound(loc, Sound.ENTITY_ENDER_PEARL_THROW, 30f, 1f);
              player1.sendMessage(UIStyle.PREFIX.append(Component.text("teleported!").color(NamedTextColor.GRAY)));
            }
            case RIGHT -> this.uiHolder.open(new SpecificWorldInfoUI(clickedWorld, this.uiHolder), player1);
            case MIDDLE -> {
              player1.closeInventory();
              Location loc = clickedWorld.getSpawnLocation();
              player1.teleportAsync(loc);
              player1.playSound(loc, Sound.ENTITY_ENDER_PEARL_THROW, 30f, 1f);
              player1.sendMessage(UIStyle.PREFIX.append(Component.text("teleported!").color(NamedTextColor.GRAY)));
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

      player1.closeInventory();

      JavaPlugin.getPlugin(UniqueWorlds.class).getChatInputManager().addChatInput(new ChatInput(player1, component -> {
        String userInput = PlainTextComponentSerializer.plainText().serialize(component);

        Bukkit.getScheduler().getMainThreadExecutor(JavaPlugin.getPlugin(UniqueWorlds.class)).execute(() -> {
          this.uiHolder.open(new CreateWorldUI(userInput, this.uiHolder), player1);
        });

      }));

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
        this.removeWorlds = !removeWorlds;
        player1.playSound(player1.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 30f, 1f);
        try {
          this.refreshInventory(player1);
        } catch (OutOfInventoryException e) {
          throw new RuntimeException(e);
        }
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
