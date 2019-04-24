package net.silentchaos512.gear.api.parts;

import net.minecraft.item.Item;
import net.minecraft.item.crafting.Ingredient;
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

    /**
     * Gets an {@link Ingredient} to represent this part. Useful for the JEI plugin.
     *
     * @return An {@link Ingredient} which prioritizes the tag over the item, or null if neither is
     * present
     */
    @Nullable
    default Ingredient getIngredient() {
        // Prioritize tag
        Tag<Item> tag = getTag();
        if (tag != null) {
            return Ingredient.fromTag(tag);
        }
        // Then item
        IItemProvider item = getItem();
        if (item != null) {
            return Ingredient.fromItems(item);
        }
        // Part has neither?
        return null;
    }
}
