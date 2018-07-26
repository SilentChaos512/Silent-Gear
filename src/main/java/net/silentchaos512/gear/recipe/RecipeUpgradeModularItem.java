package net.silentchaos512.gear.recipe;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.parts.ItemPartData;
import net.silentchaos512.gear.api.parts.IUpgradePart;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.lib.recipe.RecipeBaseSL;

import java.util.ArrayList;
import java.util.List;

public class RecipeUpgradeModularItem extends RecipeBaseSL {

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        ItemStack tool = ItemStack.EMPTY;
        List<ItemStack> upgrades = new ArrayList<>();

        for (ItemStack stack : getNonEmptyStacks(inv)) {
            ItemPartData partData = ItemPartData.fromStack(stack);
            if (stack.getItem() instanceof ICoreItem)
                tool = stack.copy();
            else if (partData != null && partData.getPart() instanceof IUpgradePart)
                upgrades.add(stack);
        }

        if (tool.isEmpty())
            return ItemStack.EMPTY;

        for (ItemStack upgrade : upgrades)
            GearData.addUpgradePart(tool, upgrade);
        GearData.recalculateStats(tool);
        return tool;
    }

    @Override
    public boolean matches(InventoryCrafting inv, World world) {
        boolean foundTool = false;
        boolean foundUpgrade = false;

        for (ItemStack stack : getNonEmptyStacks(inv)) {
            ItemPartData partData = ItemPartData.fromStack(stack);
            if (stack.getItem() instanceof ICoreItem) {
                if (foundTool)
                    return false;
                foundTool = true;
            } else if (partData != null && partData.getPart() instanceof IUpgradePart) {
                foundUpgrade = true;
            } else {
                return false;
            }
        }

        return foundTool && foundUpgrade;
    }
}
