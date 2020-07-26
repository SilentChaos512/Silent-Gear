package net.silentchaos512.gear.util;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.silentchaos512.utils.Color;

public final class TextUtil {
    private TextUtil() {throw new IllegalAccessError("Utility class");}

    public static IFormattableTextComponent translate(String prefix, String suffix) {
        return new TranslationTextComponent(prefix + ".silentgear." + suffix);
    }

    public static IFormattableTextComponent translate(String prefix, String suffix, Object... args) {
        return new TranslationTextComponent(prefix + ".silentgear." + suffix, args);
    }

    public static IFormattableTextComponent misc(String key, Object... args) {
        return translate("misc", key, args);
    }

    public static IFormattableTextComponent keyBinding(KeyBinding keyBinding) {
        return misc("key", keyBinding.func_238171_j_());
    }

    public static IFormattableTextComponent withColor(IFormattableTextComponent text, Color color) {
        return text.func_230530_a_(text.getStyle().setColor(net.minecraft.util.text.Color.func_240743_a_(color.getColor())));
    }
}
