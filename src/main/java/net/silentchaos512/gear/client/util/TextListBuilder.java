package net.silentchaos512.gear.client.util;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.ArrayList;
import java.util.List;

public class TextListBuilder {
    static final String[] BULLETS = {"\u2022", "\u25e6", "\u25aa"}; // •◦▪

    private final List<ITextComponent> list = new ArrayList<>();
    private int indent = 0;

    public TextListBuilder indent() {
        ++indent;
        return this;
    }

    public TextListBuilder unindent() {
        if (indent > 0)
            --indent;
        return this;
    }

    public TextListBuilder add(ITextComponent text) {
        this.list.add(indentWithBullet().func_230529_a_(text));
        return this;
    }

    private IFormattableTextComponent indentWithBullet() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < this.indent; ++i) {
            builder.append("  ");
        }

        String bullet = BULLETS[MathHelper.clamp(this.indent, 0, BULLETS.length - 1)];
        builder.append(bullet).append(" ");
        return new StringTextComponent(builder.toString());
    }

    @SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType") // list not used after build
    public List<ITextComponent> build() {
        return list;
    }
}
