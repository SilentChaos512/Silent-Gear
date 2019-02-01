package net.silentchaos512.gear.api.parts;

import net.minecraft.item.Item;
import net.minecraft.tags.Tag;
import net.minecraft.util.IItemProvider;

import javax.annotation.Nullable;

public interface IPartMaterial {
    boolean matches(IItemProvider input);

    @Nullable
    IItemProvider getItem();

    @Nullable
    IItemProvider getSmallItem();

    @Nullable
    Tag<Item> getTag();

    @Nullable
    Tag<Item> getSmallTag();
}
