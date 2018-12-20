/*
 * Silent Gear -- UpgradeGear
 * Copyright (C) 2018 SilentChaos512
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 3
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.silentchaos512.gear.crafting.recipe;

import com.google.gson.JsonObject;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.parts.IUpgradePart;
import net.silentchaos512.gear.api.parts.ItemPartData;
import net.silentchaos512.gear.config.Config;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.lib.recipe.RecipeBaseSL;

import java.util.ArrayList;
import java.util.List;

public class UpgradeGear implements IRecipeFactory {
    @Override
    public IRecipe parse(JsonContext context, JsonObject json) {
        return new Recipe();
    }

    private static class Recipe extends RecipeBaseSL {
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
        public ItemStack getRecipeOutput() {
            return ItemStack.EMPTY;
        }

        @Override
        public boolean matches(InventoryCrafting inv, World world) {
            if (Config.upgradesAnvilOnly) return false;

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
}
