/*
 * Silent Gear -- QuickRepairRecipe
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
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.parts.PartData;
import net.silentchaos512.gear.parts.PartManager;
import net.silentchaos512.gear.parts.RepairContext;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.lib.collection.StackList;

import java.util.Collection;

public class QuickRepairRecipe extends SpecialRecipe {
    public static final ResourceLocation NAME = SilentGear.getId("quick_repair");
    public static final Serializer SERIALIZER = new Serializer();

    public QuickRepairRecipe(ResourceLocation idIn) {
        super(idIn);
    }

    @Override
    public boolean matches(CraftingInventory inv, World worldIn) {
        // Need 1 gear and 1+ parts
        StackList list = StackList.from(inv);

        final ItemStack gear = list.uniqueOfType(ICoreItem.class);
        if (gear.isEmpty()) return false;

        int partsCount = 0;
        for (ItemStack stack : list) {
            if (!(stack.getItem() instanceof ICoreItem)) {
                PartData part = PartData.from(stack);
                if (part == null || part.getRepairAmount(gear, RepairContext.Type.QUICK) <= 0) {
                    return false;
                }
                ++partsCount;
            }
        }
        return partsCount > 0;
    }

    @Override
    public ItemStack getCraftingResult(CraftingInventory inv) {
        StackList list = StackList.from(inv);
        ItemStack gear = list.uniqueOfType(ICoreItem.class).copy();
        Collection<ItemStack> parts = list.allMatches(s -> PartManager.from(s) != null);

        if (gear.isEmpty() || parts.isEmpty()) return ItemStack.EMPTY;

        float repairValue = 0f;
        int materialCount = 0;
        for (ItemStack stack : parts) {
            PartData data = PartData.from(stack);
            if (data != null) {
                repairValue += data.getRepairAmount(gear, RepairContext.Type.QUICK);
                ++materialCount;
            }
        }

        // Makes odd repair values line up better
        repairValue += 1;

        // Repair efficiency instance tool class
        if (gear.getItem() instanceof ICoreItem) {
            float repairEfficiency = GearData.getStat(gear, ItemStats.REPAIR_EFFICIENCY);
            // FIXME: temp fix for missing equipment modifiers
            if (repairEfficiency > 0) {
                repairValue *= repairEfficiency;
            }
        }

        gear.attemptDamageItem(-Math.round(repairValue), SilentGear.random, null);
//            GearStatistics.incrementStat(gear, "silentgear.repair_count", materialCount);
        GearData.incrementRepairCount(gear, materialCount);
        GearData.recalculateStats(gear, null);
        return gear;
    }

    @Override
    public boolean canFit(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public ResourceLocation getId() {
        return NAME;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    public static final class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<QuickRepairRecipe> {
        @Override
        public QuickRepairRecipe read(ResourceLocation recipeId, JsonObject json) {
            return new QuickRepairRecipe(recipeId);
        }

        @Override
        public QuickRepairRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
            return new QuickRepairRecipe(recipeId);
        }

        @Override
        public void write(PacketBuffer buffer, QuickRepairRecipe recipe) {}
    }
}
