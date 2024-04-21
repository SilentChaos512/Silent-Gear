package net.silentchaos512.gear.util;

import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.silentchaos512.gear.config.Config;
import net.silentchaos512.lib.util.Color;

import java.util.Collection;

public final class TextUtil {
    private TextUtil() {throw new IllegalAccessError("Utility class");}

    public static MutableComponent translate(String prefix, String suffix) {
        return Component.translatable(prefix + ".silentgear." + suffix);
    }

    public static MutableComponent translate(String prefix, String suffix, Object... args) {
        return Component.translatable(prefix + ".silentgear." + suffix, args);
    }

    public static MutableComponent misc(String key, Object... args) {
        return translate("misc", key, args);
    }

    public static MutableComponent keyBinding(KeyMapping keyBinding) {
        return misc("key", keyBinding.getTranslatedKeyMessage());
    }

    public static MutableComponent withColor(MutableComponent text, int color) {
        return text.withStyle(text.getStyle().withColor(net.minecraft.network.chat.TextColor.fromRgb(color & 0xFFFFFF)));
    }

    public static MutableComponent withColor(MutableComponent text, Color color) {
        return withColor(text, color.getColor());
    }

    public static MutableComponent withColor(MutableComponent text, ChatFormatting color) {
        int colorCode = color.getColor() != null ? color.getColor() : Color.VALUE_WHITE;
        return withColor(text, colorCode);
    }

    public static MutableComponent separatedList(Collection<Component> list) {
        MutableComponent ret = null;
        for (Component c : list) {
            if (ret == null) {
                ret = c.copy();
            } else {
                ret.append(", ").append(c.copy());
            }
        }
        return ret;
    }

    public static void addWipText(Collection<Component> tooltip) {
        if (Config.Common.showWipText.get()) {
            tooltip.add(withColor(misc("wip"), ChatFormatting.RED));
        }
    }
}
