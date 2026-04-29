package net.silentchaos512.gear.crafting.recipe.modkit;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.level.Level;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.item.ModKitItem;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;

public abstract class ModKitRecipe extends CustomRecipe {
    public ModKitRecipe(CraftingBookCategory category) {
        super(category);
    }

    public abstract boolean isActionSupported(PartType partType);

    @Override
    public boolean matches(CraftingInput input, Level level) {
        ItemStack gear = ItemStack.EMPTY;
        boolean foundModKit = false;
        PartType type = PartTypes.NONE.get();

        for (int i = 0; i < input.size(); ++i) {
            ItemStack stack = input.getItem(i);
            if (!stack.isEmpty()) {
                if (gear.isEmpty() && GearHelper.isGear(stack)) {
                    gear = stack;
                } else if (!foundModKit && isModKit(stack)) {
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

        return !gear.isEmpty() && foundModKit && isActionSupported(type) && GearData.hasPartOfType(gear, type);
    }

    public static boolean isModKit(ItemStack stack) {
        return stack.getItem() instanceof ModKitItem;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }
}
