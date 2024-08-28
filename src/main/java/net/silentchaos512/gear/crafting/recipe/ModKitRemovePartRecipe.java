package net.silentchaos512.gear.crafting.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.CommonHooks;
import net.silentchaos512.gear.api.item.GearItem;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.gear.part.PartInstance;
import net.silentchaos512.gear.item.ModKitItem;
import net.silentchaos512.gear.setup.SgRecipes;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.lib.collection.StackList;

public class ModKitRemovePartRecipe extends CustomRecipe {
    public ModKitRemovePartRecipe(CraftingBookCategory bookCategory) {
        super(bookCategory);
    }

    @Override
    public boolean matches(CraftingInput inv, Level worldIn) {
        ItemStack gear = ItemStack.EMPTY;
        boolean foundModKit = false;
        PartType type = PartTypes.NONE.get();

        for (int i = 0; i < inv.size(); ++i) {
            ItemStack stack = inv.getItem(i);
            if (!stack.isEmpty()) {
                //noinspection ChainOfInstanceofChecks
                if (gear.isEmpty() && stack.getItem() instanceof GearItem) {
                    gear = stack;
                } else if (!foundModKit && stack.getItem() instanceof ModKitItem) {
                    type = ModKitItem.getSelectedType(stack);
                    if (type == PartTypes.NONE.get()) {
                        return false;
                    }
                    foundModKit = true;
                } else {
                    return false;
                }
            }
        }

        return !gear.isEmpty() && foundModKit && GearData.hasPartOfType(gear, type);
    }

    @Override
    public ItemStack assemble(CraftingInput inv, HolderLookup.Provider registryAccess) {
        StackList list = StackList.from(inv);
        ItemStack gear = list.uniqueOfType(GearItem.class);
        ItemStack modKit = list.uniqueOfType(ModKitItem.class);
        if (gear.isEmpty() || modKit.isEmpty()) return ItemStack.EMPTY;

        ItemStack result = gear.copy();
        PartType type = ModKitItem.getSelectedType(modKit);

        if (GearData.removeFirstPartOfType(result, type)) {
            GearData.recalculateGearData(result, CommonHooks.getCraftingPlayer());
        }
        return result;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput inv) {
        NonNullList<ItemStack> list = NonNullList.withSize(inv.size(), ItemStack.EMPTY);
        ItemStack gear = StackList.from(inv).uniqueOfType(GearItem.class);
        ItemStack modKit = StackList.from(inv).uniqueOfType(ModKitItem.class);
        PartType type = ModKitItem.getSelectedType(modKit);
        PartInstance part = GearData.getConstruction(gear).getPartOfType(type);

        for (int i = 0; i < list.size(); ++i) {
            ItemStack stack = inv.getItem(i);

            if (stack.getItem() instanceof GearItem) {
                list.set(i, part != null ? part.getItem() : ItemStack.EMPTY);
            } else if (stack.hasCraftingRemainingItem()) {
                list.set(i, stack.getCraftingRemainingItem());
            }
        }

        return list;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SgRecipes.MOD_KIT_REMOVE_PART.get();
    }
}
