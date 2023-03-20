package net.silentchaos512.gear.crafting.recipe;

import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.silentchaos512.gear.api.material.IMaterialInstance;
import net.silentchaos512.gear.init.SgItems;
import net.silentchaos512.gear.init.SgRecipes;
import net.silentchaos512.gear.item.FragmentItem;
import net.silentchaos512.lib.collection.StackList;
import net.silentchaos512.lib.crafting.ingredient.ExclusionIngredient;
import net.silentchaos512.lib.util.InventoryUtils;

public class CombineFragmentsRecipe extends CustomRecipe {
    public CombineFragmentsRecipe(ResourceLocation idIn, CraftingBookCategory bookCategory) {
        super(idIn, bookCategory);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SgRecipes.COMBINE_FRAGMENTS.get();
    }

    @Override
    public boolean matches(CraftingContainer craftingInventory, Level world) {
        // First, count the fragments. We want to fail fast.
        int fragmentCount = 0;
        for (int i = 0; i < craftingInventory.getContainerSize(); ++i) {
            ItemStack stack = craftingInventory.getItem(i);
            if (!stack.isEmpty()) {
                if (stack.getItem() == SgItems.FRAGMENT.get()) {
                    ++fragmentCount;
                } else {
                    return false;
                }
            }
        }

        if (fragmentCount != 8) {
            return false;
        }

        // Now, check that the fragments are all the same material.
        IMaterialInstance first = null;
        for (ItemStack stack : StackList.from(craftingInventory)) {
            IMaterialInstance material = FragmentItem.getMaterial(stack);
            if (material == null) {
                return false;
            }

            if (first == null) {
                first = material;
            } else if (!InventoryUtils.canItemsStack(material.getItem(), first.getItem())) {
                return false;
            }
        }
        return first != null;
    }

    @Override
    public ItemStack assemble(CraftingContainer craftingInventory) {
        StackList list = StackList.from(craftingInventory);
        ItemStack stack = list.firstOfType(FragmentItem.class);
        if (stack.isEmpty()) return ItemStack.EMPTY;

        IMaterialInstance material = FragmentItem.getMaterial(stack);
        if (material == null) return ItemStack.EMPTY;

        // Get the actual item the fragment came from (if present)
        if (!material.getItem().isEmpty()) {
            return material.getItem();
        }

        // Try to get an equivalent item from the material's ingredient
        ItemStack[] matchingStacks = material.getIngredient().getItems();
        if (matchingStacks.length < 1) {
            if (material.getIngredient() instanceof ExclusionIngredient) {
                // Get excluded ingredients if no others are available
                ItemStack[] allMatches = ((ExclusionIngredient) material.getIngredient()).getMatchingStacksWithExclusions();
                if (allMatches.length > 0) {
                    return allMatches[0];
                }
            }
            return ItemStack.EMPTY;
        }

        return matchingStacks[0].copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 8;
    }
}
