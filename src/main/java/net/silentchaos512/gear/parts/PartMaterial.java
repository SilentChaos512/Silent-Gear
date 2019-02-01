package net.silentchaos512.gear.parts;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.Tag;
import net.minecraft.util.IItemProvider;
import net.silentchaos512.gear.api.parts.IGearPart;
import net.silentchaos512.gear.api.parts.IPartMaterial;

import javax.annotation.Nullable;

/**
 * Represents the items that an {@link IGearPart} can be crafted from.
 */
public class PartMaterial implements IPartMaterial {
    IItemProvider item;
    IItemProvider itemSmall;
    Tag<Item> tag;
    Tag<Item> tagSmall;

    PartMaterial() {}

    @Override
    public boolean matches(IItemProvider input) {
        return item != null && item == input
                || tag != null && tag.contains(input.asItem());
    }

    // TODO: Should getters return optionals instead? Or are the getters needed?

    @Override
    @Nullable
    public IItemProvider getItem() {
        return item;
    }

    @Override
    @Nullable
    public IItemProvider getSmallItem() {
        return itemSmall;
    }

    @Override
    @Nullable
    public Tag<Item> getTag() {
        return tag;
    }

    @Override
    @Nullable
    public Tag<Item> getSmallTag() {
        return tagSmall;
    }

    ItemStack getItemStack() {
        IItemProvider normal = item;
        return normal != null ? new ItemStack(normal) : ItemStack.EMPTY;
    }

    ItemStack getSmallItemStack() {
        IItemProvider small = itemSmall;
        return small != null ? new ItemStack(small) : ItemStack.EMPTY;
    }
}
