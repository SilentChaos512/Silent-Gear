package net.silentchaos512.gear.util;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;
import java.util.Optional;

public class IngredientUtils {
    private IngredientUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Returns all items for the ingredient, excluding the barrier item. This is useful for hiding empty tags.
     *
     * @param ingredient The ingredient
     * @return All items in the ingredient mapped to stacks
     */
    public static List<ItemStack> getValidItems(Ingredient ingredient) {
        var listBuilder = ImmutableList.<ItemStack>builder();
        for (Holder<Item> item : ingredient.getValues()) {
            if (!item.is(BuiltInRegistries.ITEM.getKey(Items.BARRIER))) {
                listBuilder.add(new ItemStack(item.value()));
            }
        }
        return listBuilder.build();
    }

    /**
     * Returns all items for the ingredient, excluding the barrier item. This is useful for hiding empty tags.
     *
     * @param ingredient The ingredient
     * @return All items in the ingredient mapped to stacks
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static List<ItemStack> getValidItems(Optional<Ingredient> ingredient) {
        return ingredient.map(IngredientUtils::getValidItems).orElseGet(List::of);
    }

    /**
     * Returns all items for the ingredient, which may include a barrier item for empty tags.
     *
     * @param ingredient The ingredient
     * @return All items in the ingredient mapped to stacks, or possibly a barrier item stack for empty tags
     */
    public static List<ItemStack> getItems(Ingredient ingredient) {
        var listBuilder = ImmutableList.<ItemStack>builder();
        for (Holder<Item> item : ingredient.getValues()) {
            listBuilder.add(new ItemStack(item.value()));
        }
        return listBuilder.build();
    }

    /**
     * Returns all items for the ingredient, which may include a barrier item for empty tags.
     *
     * @param ingredient The ingredient
     * @return All items in the ingredient mapped to stacks, or possibly a barrier item stack for empty tags
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static List<ItemStack> getItems(Optional<Ingredient> ingredient) {
        return ingredient.map(IngredientUtils::getItems).orElseGet(List::of);
    }
}
