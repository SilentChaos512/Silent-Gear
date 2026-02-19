package net.silentchaos512.gear.client.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;

public class TextListBuilder {
    public static final String[] BULLETS = {"\u2022", "\u25e6", "\u25aa"}; // •◦▪
    public static final String VANILLA_BULLET = "\u2666";

    private final List<Component> list = new ArrayList<>();
    private int indent = 0;
    private String bullet = BULLETS[0];

    public TextListBuilder indent() {
        ++indent;
        setDefaultBullet();
        return this;
    }

    public TextListBuilder unindent() {
        if (indent > 0)
            --indent;
        setDefaultBullet();
        return this;
    }

    public TextListBuilder setBullet(String bulletCharacter) {
        this.bullet = bulletCharacter;
        return this;
    }

    public TextListBuilder setDefaultBullet() {
        this.bullet = BULLETS[Mth.clamp(this.indent, 0, BULLETS.length - 1)];
        return this;
    }

    public TextListBuilder add(Component text) {
        this.list.add(indentWithBullet().append(text));
        return this;
    }

    public TextListBuilder add(Component text, ChatFormatting style) {
        this.list.add(indentWithBullet().append(text).withStyle(style));
        return this;
    }

    public TextListBuilder removeLast() {
        this.list.remove(this.list.size() - 1);
        return this;
    }

    private MutableComponent indentWithBullet() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < this.indent; ++i) {
            builder.append("  ");
        }

        builder.append(this.bullet).append(" ");
        return Component.literal(builder.toString());
    }

    @SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType") // list not used after build
    public List<Component> build() {
        return list;
    }
}
