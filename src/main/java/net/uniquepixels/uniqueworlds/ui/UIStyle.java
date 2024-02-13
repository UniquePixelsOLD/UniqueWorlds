package net.uniquepixels.uniqueworlds.ui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.translation.GlobalTranslator;

import java.util.Locale;
import java.util.function.Supplier;

public class UIStyle {

  public static TextColor DEFAULT_COLOR = TextColor.fromHexString("#870ac2");
  public static Component PREFIX = Component.text("UniquePerms").color(TextColor.fromHexString("#870ac2")).append(Component.text(" » ").color(NamedTextColor.GRAY));
  public static Component UI_ARROW = Component.text(" » ").color(NamedTextColor.GRAY).style(builder -> builder.decoration(TextDecoration.ITALIC, false).build());
  public static Component MINUS = Component.translatable(" - ").color(NamedTextColor.DARK_GRAY);

  public static Component leftClick(Locale locale) {
    return GlobalTranslator.render(Component.translatable("action.left.click"), locale)
      .color(NamedTextColor.BLUE).style(builder -> builder.decoration(TextDecoration.ITALIC, false).build());
  }

  public static Component rightClick(Locale locale) {
    return GlobalTranslator.render(Component.translatable("action.right.click"), locale)
      .color(NamedTextColor.BLUE).style(builder -> builder.decoration(TextDecoration.ITALIC, false).build());
  }

  public static Component middleClick(Locale locale) {
    return GlobalTranslator.render(Component.translatable("action.middle.click"), locale)
      .color(NamedTextColor.BLUE).style(builder -> builder.decoration(TextDecoration.ITALIC, false).build());
  }

  public static Component translate(Locale locale, String key, Supplier<ComponentLike> args) {
    return GlobalTranslator.render(Component.translatable(key).arguments(args.get()), locale);
  }

  public static Component translate(Locale locale, String key) {
    return GlobalTranslator.render(Component.translatable(key), locale);
  }

}
