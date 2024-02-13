package net.uniquepixels.uniqueworlds.ui.impl;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.translation.GlobalTranslator;
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
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.UUID;

public class SpecificWorldInfoUI extends ChestUI {
  private final World world;
  private final UIHolder uiHolder;
  private final NamespacedKey worldId = new NamespacedKey(JavaPlugin.getPlugin(UniqueWorlds.class), "world-id");

  public SpecificWorldInfoUI(World world, UIHolder uiHolder) {
    super(Component.translatable("ui.specific.title").color(UIStyle.DEFAULT_COLOR).arguments(Component.text(world.getName()).color(NamedTextColor.GRAY)), UIRow.CHEST_ROW_3);
    this.world = world;
      this.uiHolder = uiHolder;
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
      .displayName(title.color(NamedTextColor.GRAY))
      .applyItemMeta()
      .buildItem();
  }

  private Component worldLore(int lore, Player player, Component attachment) {
    return GlobalTranslator.render(
      Component.translatable("ui.specific.entry.lore" + lore).style(builder -> builder.color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false))
        .append(UIStyle.MINUS).append(attachment.color(NamedTextColor.GRAY).style(Style.style(TextDecoration.ITALIC))), player.locale()
    );
  }

  @Override
  protected void initItems(Player player) throws OutOfInventoryException {

    item(
      new UIItem(
        new DefaultItemStackBuilder<>(this.getItemForWorld(this.world, Component.text(this.world.getName())))
          .addLoreLine(this.worldLore(1, player, Component.text(this.world.getEnvironment().name())))
          .addLoreLine(this.worldLore(2, player, Component.text(this.world.getSeed()).append(
            Component.space().append(GlobalTranslator.render(Component.translatable("item.right-click").color(NamedTextColor.BLUE), player.locale()))
          )))
          .addData(this.worldId, PersistentDataType.STRING, this.world.getUID().toString())
          .applyItemMeta()
          .buildItem(), UISlot.SLOT_13
      ), (player1, uiItem, clickType, inventoryClickEvent) -> {

        if (!clickType.isRightClick())
          return true;

        String rawWorldId = new DefaultItemStackBuilder<>(uiItem.getItemStack()).getData(this.worldId, PersistentDataType.STRING);
        World clickedWorld = Bukkit.getWorld(UUID.fromString(rawWorldId));

        player1.sendMessage(UIStyle.PREFIX.append(Component.translatable("message.copy.seed").color(NamedTextColor.GREEN)
          .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, String.valueOf(clickedWorld.getSeed())))));

        return true;
      });

    item(
      new UIItem(
        new DefaultItemStackBuilder<>(UIHeads.REMOVE_HEAD.clone())
          .displayName(UIStyle.UI_ARROW.append(GlobalTranslator.render(Component.translatable("item.back").color(NamedTextColor.RED), player.locale())))
          .addLoreLine(this.worldLore(1, player, Component.text(this.world.getEnvironment().name())))
          .addLoreLine(this.worldLore(2, player, Component.text(this.world.getSeed())))
          .applyItemMeta()
          .buildItem(), UISlot.SLOT_18
      ), (player1, uiItem, clickType, inventoryClickEvent) -> {

        this.uiHolder.open(new ListWorldsUI(this.uiHolder), player1);

        return true;
      }
    );

    setBackground(new UIBackground(UIBackground.BackgroundType.FULL, List.of(
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
