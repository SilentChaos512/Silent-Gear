package net.silentchaos512.gear.util;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public final class TextUtil {
    private TextUtil() {throw new IllegalAccessError("Utility class");}

    public static ITextComponent translate(String prefix, String suffix) {
        return new TranslationTextComponent(prefix + ".silentgear." + suffix);
    }

    public static ITextComponent translate(String prefix, String suffix, Object... args) {
        return new TranslationTextComponent(prefix + ".silentgear." + suffix, args);
    }

    public static ITextComponent misc(String key, Object... args) {
        return translate("misc", key, args);
    }

    public static ITextComponent keyBinding(KeyBinding keyBinding) {
        return misc("key", keyBinding.getLocalizedName());
    }
}
