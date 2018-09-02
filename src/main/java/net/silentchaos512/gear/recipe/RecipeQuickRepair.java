/*
 * Silent Gear -- RecipeQuickRepair
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

package net.silentchaos512.gear.recipe;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.parts.ItemPart;
import net.silentchaos512.gear.api.parts.ItemPartData;
import net.silentchaos512.gear.api.parts.PartRegistry;
import net.silentchaos512.gear.api.stats.CommonItemStats;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.lib.collection.StackList;
import net.silentchaos512.lib.recipe.RecipeBaseSL;
import net.silentchaos512.lib.util.StackHelper;

import java.util.Collection;

public class RecipeQuickRepair extends RecipeBaseSL {
    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        StackList list = StackHelper.getNonEmptyStacks(inv);
        ItemStack gear = list.uniqueOfType(ICoreItem.class).copy();
        Collection<ItemStack> parts = list.allMatches(s -> PartRegistry.get(s) != null);

        if (gear.isEmpty() || parts.isEmpty()) return ItemStack.EMPTY;

        float repairValue = 0f;
        int materialCount = 0;
        for (ItemStack stack : parts) {
            ItemPartData data = ItemPartData.fromStack(stack);
            if (data != null) {
                repairValue += data.getRepairAmount(gear, ItemPart.RepairContext.QUICK);
                ++materialCount;
            }
        }

        // Makes odd repair values line up better
        repairValue += 1;

        // Repair efficiency instance tool class
        if (gear.getItem() instanceof ICoreItem)
            repairValue *= GearData.getStat(gear, CommonItemStats.REPAIR_EFFICIENCY);

        gear.attemptDamageItem(-Math.round(repairValue), SilentGear.random, null);
//        GearStatistics.incrementStat(gear, "silentgear.repair_count", materialCount);
        GearData.incrementRepairCount(gear, materialCount);
        GearData.recalculateStats(gear);
        return gear;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean matches(InventoryCrafting inv, World world) {
        ItemStack gear = ItemStack.EMPTY;
        int partsCount = 0;

        // Need 1 gear and 1+ parts
        for (ItemStack stack : StackHelper.getNonEmptyStacks(inv)) {
            if (stack.getItem() instanceof ICoreItem) {
                if (gear.isEmpty())
                    gear = stack;
                else
                    return false;
            }
            else if (PartRegistry.get(stack) != null) {
                ++partsCount;
                // It needs to be a part with repair value
                ItemPartData data = ItemPartData.fromStack(stack);
                if (data == null || data.getRepairAmount(gear, ItemPart.RepairContext.QUICK) <= 0)
                    return false;
            }
            else {
                return false;
            }
        }

        return !gear.isEmpty() && partsCount > 0;
    }
}
