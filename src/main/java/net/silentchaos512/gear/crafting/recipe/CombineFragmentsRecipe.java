package net.silentchaos512.gear.crafting.recipe;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.silentchaos512.gear.api.material.IMaterial;
import net.silentchaos512.gear.crafting.ingredient.ExclusionIngredient;
import net.silentchaos512.gear.init.ModItems;
import net.silentchaos512.gear.init.ModRecipes;
import net.silentchaos512.gear.item.FragmentItem;
import net.silentchaos512.lib.collection.StackList;

import java.util.HashSet;
import java.util.Set;

public class CombineFragmentsRecipe extends SpecialRecipe {
    public CombineFragmentsRecipe(ResourceLocation idIn) {
        super(idIn);
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ModRecipes.COMBINE_FRAGMENTS.get();
    }

    @Override
    public boolean matches(CraftingInventory craftingInventory, World world) {
        // First, count the fragments. We want to fail fast.
        int fragmentCount = 0;
        for (int i = 0; i < craftingInventory.getSizeInventory(); ++i) {
            ItemStack stack = craftingInventory.getStackInSlot(i);
            if (!stack.isEmpty()) {
                if (stack.getItem() == ModItems.FRAGMENT.get()) {
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
        Set<ResourceLocation> uniques = new HashSet<>();
        for (ItemStack stack : StackList.from(craftingInventory)) {
            IMaterial material = FragmentItem.getMaterial(stack);
            if (material == null) {
                return false;
            }
            uniques.add(material.getId());
        }
        return uniques.size() == 1;
    }

    @Override
    public ItemStack getCraftingResult(CraftingInventory craftingInventory) {
        StackList list = StackList.from(craftingInventory);
        ItemStack stack = list.firstOfType(FragmentItem.class);
        if (stack.isEmpty()) return ItemStack.EMPTY;

        IMaterial material = FragmentItem.getMaterial(stack);
        if (material == null) return ItemStack.EMPTY;

        ItemStack[] matchingStacks = material.getIngredient().getMatchingStacks();
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
    public boolean canFit(int width, int height) {
        return width * height >= 8;
    }
}
