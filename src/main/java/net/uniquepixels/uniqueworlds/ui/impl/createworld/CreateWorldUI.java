package net.uniquepixels.uniqueworlds.ui.impl.createworld;

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
import net.uniquepixels.core.paper.item.ItemStackBuilder;
import net.uniquepixels.uniqueworlds.UniqueWorlds;
import net.uniquepixels.uniqueworlds.ui.UIHeads;
import net.uniquepixels.uniqueworlds.ui.UIStyle;
import net.uniquepixels.uniqueworlds.ui.impl.createworld.generator.FlatChunkGenerator;
import net.uniquepixels.uniqueworlds.ui.impl.createworld.generator.IslandChunkGenerator;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class CreateWorldUI extends ChestUI {
  private final String worldName;
  private final UIHolder uiHolder;
  private long seed = -1;
  private CreateWorldOptions worldOptions = CreateWorldOptions.DEFAULT;
  private World.Environment environment = World.Environment.NORMAL;

  public CreateWorldUI(String worldName, UIHolder uiHolder) {
    super(Component.translatable(""), UIRow.CHEST_ROW_3);
    this.worldName = worldName;
    this.uiHolder = uiHolder;
  }

  private CreateWorldUI(String worldName, UIHolder uiHolder, CreateWorldOptions worldOptions, World.Environment environment, long seed) {
    super(Component.translatable(""), UIRow.CHEST_ROW_3);
    this.worldName = worldName;
    this.uiHolder = uiHolder;
    this.worldOptions = worldOptions;
    this.environment = environment;
    this.seed = seed;
  }

  private ItemStackBuilder<?, ?> createEnvironmentItem(Player player) {

    DefaultItemStackBuilder<?> item = new DefaultItemStackBuilder<>(Material.PLAYER_HEAD);

    switch (environment) {
      case NORMAL -> item = new DefaultItemStackBuilder<>(Material.GRASS_BLOCK)
        .addLoreLine(Component.space())
        .addLoreLine(UIStyle.leftClick(player.locale()).append(UIStyle.MINUS.append(Component.text(World.Environment.NORMAL.name()).color(NamedTextColor.GREEN))))
        .addLoreLine(UIStyle.UI_ARROW.append(Component.text(World.Environment.NETHER.name()).color(NamedTextColor.GRAY)))
        .addLoreLine(UIStyle.UI_ARROW.append(Component.text(World.Environment.THE_END.name()).color(NamedTextColor.GRAY)));
      case NETHER -> item = new DefaultItemStackBuilder<>(Material.NETHERRACK)
        .addLoreLine(Component.space())
        .addLoreLine(UIStyle.UI_ARROW.append(Component.text(World.Environment.NORMAL.name()).color(NamedTextColor.GRAY)))
        .addLoreLine(UIStyle.leftClick(player.locale()).append(UIStyle.MINUS.append(Component.text(World.Environment.NETHER.name()).color(NamedTextColor.GREEN))))
        .addLoreLine(UIStyle.UI_ARROW.append(Component.text(World.Environment.THE_END.name()).color(NamedTextColor.GRAY)));
      case THE_END -> item = new DefaultItemStackBuilder<>(Material.END_STONE)
        .addLoreLine(Component.space())
        .addLoreLine(UIStyle.UI_ARROW.append(Component.text(World.Environment.NORMAL.name()).color(NamedTextColor.GRAY)))
        .addLoreLine(UIStyle.UI_ARROW.append(Component.text(World.Environment.NETHER.name()).color(NamedTextColor.GRAY)))
        .addLoreLine(UIStyle.leftClick(player.locale()).append(UIStyle.MINUS.append(Component.text(World.Environment.THE_END.name()).color(NamedTextColor.GREEN))));
    }

    return item
      .displayName(UIStyle.UI_ARROW.append(GlobalTranslator.render(Component.translatable("ui.world.create.environment.title").color(NamedTextColor.GRAY), player.locale())))
      .applyItemMeta();
  }

  private ItemStackBuilder<?, ?> createWorldGeneratorItem(Player player) {

    DefaultItemStackBuilder<?> item = new DefaultItemStackBuilder<>(Material.PLAYER_HEAD);

    switch (worldOptions) {
      case DEFAULT -> item = new DefaultItemStackBuilder<>(Material.STONE_SHOVEL)
        .addLoreLine(Component.space())
        .addLoreLine(UIStyle.leftClick(player.locale()).append(UIStyle.MINUS.append(Component.text(CreateWorldOptions.DEFAULT.name()).color(NamedTextColor.GREEN))))
        .addLoreLine(UIStyle.UI_ARROW.append(Component.text(CreateWorldOptions.FLAT.name()).color(NamedTextColor.GRAY)))
        .addLoreLine(UIStyle.UI_ARROW.append(Component.text(CreateWorldOptions.BUILDERS_READY.name()).color(NamedTextColor.GRAY)))
        .addLoreLine(UIStyle.UI_ARROW.append(Component.text(CreateWorldOptions.ISLAND.name()).color(NamedTextColor.GRAY)));
      case FLAT -> item = new DefaultItemStackBuilder<>(Material.DIRT)
        .addLoreLine(Component.space())
        .addLoreLine(UIStyle.UI_ARROW.append(Component.text(CreateWorldOptions.DEFAULT.name()).color(NamedTextColor.GRAY)))
        .addLoreLine(UIStyle.leftClick(player.locale()).append(UIStyle.MINUS.append(Component.text(CreateWorldOptions.FLAT.name()).color(NamedTextColor.GREEN))))
        .addLoreLine(UIStyle.UI_ARROW.append(Component.text(CreateWorldOptions.BUILDERS_READY.name()).color(NamedTextColor.GRAY)))
        .addLoreLine(UIStyle.UI_ARROW.append(Component.text(CreateWorldOptions.ISLAND.name()).color(NamedTextColor.GRAY)));
      case BUILDERS_READY -> item = new DefaultItemStackBuilder<>(Material.STONE_PICKAXE)
        .addLoreLine(Component.space())
        .addLoreLine(UIStyle.UI_ARROW.append(Component.text(CreateWorldOptions.DEFAULT.name()).color(NamedTextColor.GRAY)))
        .addLoreLine(UIStyle.UI_ARROW.append(Component.text(CreateWorldOptions.FLAT.name()).color(NamedTextColor.GRAY)))
        .addLoreLine(UIStyle.leftClick(player.locale()).append(UIStyle.MINUS.append(Component.text(CreateWorldOptions.BUILDERS_READY.name()).color(NamedTextColor.GREEN))))
        .addLoreLine(UIStyle.UI_ARROW.append(Component.text(CreateWorldOptions.ISLAND.name()).color(NamedTextColor.GRAY)));
      case ISLAND -> item = new DefaultItemStackBuilder<>(Material.STONE)
        .addLoreLine(Component.space())
        .addLoreLine(UIStyle.UI_ARROW.append(Component.text(CreateWorldOptions.DEFAULT.name()).color(NamedTextColor.GRAY)))
        .addLoreLine(UIStyle.UI_ARROW.append(Component.text(CreateWorldOptions.FLAT.name()).color(NamedTextColor.GRAY)))
        .addLoreLine(UIStyle.UI_ARROW.append(Component.text(CreateWorldOptions.BUILDERS_READY.name()).color(NamedTextColor.GRAY)))
        .addLoreLine(UIStyle.leftClick(player.locale()).append(UIStyle.MINUS.append(Component.text(CreateWorldOptions.ISLAND.name()).color(NamedTextColor.GREEN))));
    }

    return item
      .displayName(UIStyle.UI_ARROW.append(GlobalTranslator.render(Component.translatable("ui.world.create.generator.title").color(NamedTextColor.GRAY), player.locale())))
      .applyItemMeta();
  }

  private void openInventorySync(Player player) {
    Bukkit.getScheduler().getMainThreadExecutor(JavaPlugin.getPlugin(UniqueWorlds.class)).execute(() -> {
      this.uiHolder.open(new CreateWorldUI(this.worldName, this.uiHolder, this.worldOptions, this.environment, this.seed), player);
    });
  }


  @Override
  protected void initItems(Player player) throws OutOfInventoryException {

    // Environment
    item(
      new UIItem(createEnvironmentItem(player).buildItem(), UISlot.SLOT_10),
      (player1, uiItem, clickType, inventoryClickEvent) -> {

        switch (this.environment) {
          case NORMAL -> this.environment = World.Environment.NETHER;
          case NETHER -> this.environment = World.Environment.THE_END;
          case THE_END -> this.environment = World.Environment.NORMAL;
        }

        try {
          this.refreshInventory(player1);
        } catch (OutOfInventoryException e) {
          throw new RuntimeException(e);
        }

        return true;
      }
    );

    // World Gen
    item(
      new UIItem(createWorldGeneratorItem(player).buildItem(), UISlot.SLOT_11),
      (player1, uiItem, clickType, inventoryClickEvent) -> {

        switch (this.worldOptions) {
          case DEFAULT -> this.worldOptions = CreateWorldOptions.FLAT;
          case FLAT -> this.worldOptions = CreateWorldOptions.BUILDERS_READY;
          case BUILDERS_READY -> this.worldOptions = CreateWorldOptions.ISLAND;
          case ISLAND -> this.worldOptions = CreateWorldOptions.DEFAULT;
        }

        try {
          this.refreshInventory(player1);
        } catch (OutOfInventoryException e) {
          throw new RuntimeException(e);
        }

        return true;
      }
    );

    // World seed
    item(
      new UIItem(new DefaultItemStackBuilder<>(Material.CAULDRON)
        .displayName(UIStyle.UI_ARROW.append(GlobalTranslator.render(Component.translatable("ui.world.create.seed.title").color(NamedTextColor.GRAY), player.locale())))
        .addLoreLine(UIStyle.leftClick(player.locale()).append(UIStyle.MINUS
          .append(GlobalTranslator.render(Component.translatable("ui.world.create.seed.lore").color(NamedTextColor.GRAY), player.locale()))))
        .applyItemMeta()
        .buildItem(), UISlot.SLOT_12),
      (player1, uiItem, clickType, inventoryClickEvent) -> {

        JavaPlugin.getPlugin(UniqueWorlds.class).getChatInputManager().addChatInput(new ChatInput(player1, component -> {

          String userInput = PlainTextComponentSerializer.plainText().serialize(component);

          try {
            this.seed = Long.parseLong(userInput);
          } catch (NumberFormatException e) {
            player1.sendMessage(UIStyle.PREFIX.append(Component.translatable("ui.world.create.seed.error").color(NamedTextColor.RED)));
            player1.playSound(player1.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 30f, 1f);
          }

          this.openInventorySync(player1);

        }));

        return true;
      }
    );

    // World summarize
    item(
      new UIItem(new DefaultItemStackBuilder<>(Material.SHULKER_BOX)
        .displayName(Component.text(this.worldName).color(NamedTextColor.GRAY))
        .addLoreLine(UIStyle.UI_ARROW.append(Component.text("Environment: " + this.environment.name()).color(NamedTextColor.GRAY)))
        .addLoreLine(UIStyle.UI_ARROW.append(Component.text("Generator: " + this.worldOptions.name()).color(NamedTextColor.GRAY)))
        .addLoreLine(UIStyle.UI_ARROW.append(Component.text("Seed: " + this.seed).color(NamedTextColor.GRAY)))
        .applyItemMeta()
        .buildItem(), UISlot.SLOT_14),
      (player1, uiItem, clickType, inventoryClickEvent) -> {
        return true;
      }
    );

    // Bake
    item(
      new UIItem(new DefaultItemStackBuilder<>(UIHeads.PLUS_HEAD.clone())
        .displayName(UIStyle.UI_ARROW.append(GlobalTranslator.render(Component.translatable("ui.world.create.seed.submit").color(NamedTextColor.GREEN), player.locale())))
        .applyItemMeta()
        .buildItem(), UISlot.SLOT_16),
      (player1, uiItem, clickType, inventoryClickEvent) -> {

        player1.closeInventory();

        WorldCreator worldCreator = WorldCreator.name(this.worldName).environment(this.environment);

        switch (this.worldOptions) {
          case FLAT -> worldCreator = worldCreator.generator(new FlatChunkGenerator(1));
          case BUILDERS_READY -> worldCreator = worldCreator.generator(new FlatChunkGenerator(30));
          case ISLAND -> worldCreator = worldCreator.generator(new IslandChunkGenerator());
        }

        World world = worldCreator.createWorld();

        if (world == null)
          return true;

        world.setSpawnLocation(0, 1, 0);

        JavaPlugin.getPlugin(UniqueWorlds.class).getConfigWorld().saveWorld(world, this.worldOptions);

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
