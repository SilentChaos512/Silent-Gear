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
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeHooks;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.config.Config;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.gear.material.MaterialManager;
import net.silentchaos512.gear.gear.part.RepairContext;
import net.silentchaos512.gear.init.SgRecipes;
import net.silentchaos512.gear.item.RepairKitItem;
import net.silentchaos512.gear.util.Const;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.lib.collection.StackList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class QuickRepairRecipe extends CustomRecipe {
    public QuickRepairRecipe(ResourceLocation idIn, CraftingBookCategory bookCategory) {
        super(idIn, bookCategory);
    }

    @Override
    public boolean matches(CraftingContainer inv, Level worldIn) {
        // Need 1 gear, 1 repair kit, and optional materials
        ItemStack gear = ItemStack.EMPTY;
        boolean foundKit = false;
        float repairKitEfficiency = Config.Common.missingRepairKitEfficiency.get().floatValue();
        List<ItemStack> materials = new ArrayList<>();

        for (int i = 0; i < inv.getContainerSize(); ++i) {
            ItemStack stack = inv.getItem(i);
            if (!stack.isEmpty()) {
                //noinspection ChainOfInstanceofChecks
                if (stack.getItem() instanceof ICoreItem) {
                    if (!gear.isEmpty()) {
                        return false;
                    }
                    gear = stack;
                } else if (stack.getItem() instanceof RepairKitItem) {
                    if (foundKit) {
                        return false;
                    }
                    foundKit = true;
                    repairKitEfficiency = getKitEfficiency(stack);
                } else if (MaterialManager.from(stack) != null) {
                    materials.add(stack);
                } else {
                    return false;
                }
            }
        }

        if (gear.isEmpty() || repairKitEfficiency < 0.1E-9) return false;

        for (ItemStack stack : materials) {
            if (!SgRecipes.isRepairMaterial(gear, stack)) {
                return false;
            }
        }

        return true;
    }

    private static float getKitEfficiency(ItemStack stack) {
        if (stack.getItem() instanceof RepairKitItem) {
            return ((RepairKitItem) stack.getItem()).getRepairEfficiency(RepairContext.Type.QUICK);
        }
        return Config.Common.missingRepairKitEfficiency.get().floatValue();
    }

    @Override
    public ItemStack assemble(CraftingContainer inv) {
        StackList list = StackList.from(inv);
        ItemStack gear = list.uniqueOfType(ICoreItem.class).copy();
        ItemStack repairKit = list.uniqueOfType(RepairKitItem.class);
        Collection<ItemStack> mats = list.allMatches(mat -> SgRecipes.isRepairMaterial(gear, mat));

        // Repair with materials first
        repairWithLooseMaterials(gear, repairKit, mats);

        // Then use repair kit, if necessary
        if (gear.getDamageValue() > 0 && repairKit.getItem() instanceof RepairKitItem) {
            RepairKitItem item = (RepairKitItem) repairKit.getItem();
            int value = item.getDamageToRepair(gear, repairKit, RepairContext.Type.QUICK);
            if (value > 0) {
                gear.setDamageValue(gear.getDamageValue() - Math.round(value));
            }
        }

        GearData.incrementRepairCount(gear, 1);
        GearData.recalculateStats(gear, ForgeHooks.getCraftingPlayer());
        return gear;
    }

    private static void repairWithLooseMaterials(ItemStack gear, ItemStack repairKit, Collection<ItemStack> mats) {
        float repairValue = getRepairValueFromMaterials(gear, mats);
        float kitEfficiency = getKitEfficiency(repairKit);
        float gearRepairEfficiency = GearData.getStat(gear, ItemStats.REPAIR_EFFICIENCY);
        gear.setDamageValue(gear.getDamageValue() - Math.round(repairValue * kitEfficiency * gearRepairEfficiency));
    }

    private static float getRepairValueFromMaterials(ItemStack gear, Collection<ItemStack> mats) {
        float repairValue = 0f;
        for (ItemStack stack : mats) {
            MaterialInstance material = MaterialInstance.from(stack);
            if (material != null) {
                repairValue += material.getRepairValue(gear);
            }
        }

        // Repair efficiency instance tool class
        if (gear.getItem() instanceof ICoreItem) {
            float repairEfficiency = GearData.getStat(gear, ItemStats.REPAIR_EFFICIENCY);
            if (repairEfficiency > 0) {
                repairValue *= repairEfficiency;
            }
        }
        return repairValue;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer inv) {
        NonNullList<ItemStack> list = NonNullList.withSize(inv.getContainerSize(), ItemStack.EMPTY);
        StackList stackList = StackList.from(inv);
        ItemStack gear = stackList.uniqueMatch(s -> s.getItem() instanceof ICoreItem);
        ItemStack repairKit = stackList.uniqueMatch(s -> s.getItem() instanceof RepairKitItem);

        for (int i = 0; i < list.size(); ++i) {
            ItemStack stack = inv.getItem(i);

            if (stack.getItem() instanceof RepairKitItem) {
                repairWithLooseMaterials(gear, repairKit, stackList.allMatches(mat -> SgRecipes.isRepairMaterial(gear, mat)));
                RepairKitItem item = (RepairKitItem) stack.getItem();
                ItemStack copy = stack.copy();
                item.removeRepairMaterials(copy, item.getRepairMaterials(gear, copy, RepairContext.Type.QUICK));
                list.set(i, copy);
            } else if (stack.hasCraftingRemainingItem()) {
                list.set(i, stack.getCraftingRemainingItem());
            }
        }

        return list;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public ResourceLocation getId() {
        return Const.QUICK_REPAIR;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SgRecipes.QUICK_REPAIR.get();
    }

    public static final class Serializer implements RecipeSerializer<QuickRepairRecipe> {
        @Override
        public QuickRepairRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            return new QuickRepairRecipe(recipeId, CraftingBookCategory.MISC);
        }

        @Override
        public QuickRepairRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            return new QuickRepairRecipe(recipeId, CraftingBookCategory.MISC);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, QuickRepairRecipe recipe) {}
    }
}
