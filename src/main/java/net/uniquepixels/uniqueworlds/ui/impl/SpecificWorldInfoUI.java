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
import net.uniquepixels.uniqueworlds.ui.UIStyle;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

import java.util.List;

public class SpecificWorldInfoUI extends ChestUI {
  private final World world;

  public SpecificWorldInfoUI(World world) {
    super(Component.translatable("ui.specific.title").color(UIStyle.DEFAULT_COLOR).arguments(Component.text(world.getName()).color(NamedTextColor.GRAY)), UIRow.CHEST_ROW_3);
    this.world = world;
  }

  @Override
  protected void initItems(Player player) throws OutOfInventoryException {

    // items
    // delete,  world info

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
