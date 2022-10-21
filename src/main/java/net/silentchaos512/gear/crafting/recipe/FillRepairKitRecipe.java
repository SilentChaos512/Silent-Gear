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
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.silentchaos512.gear.api.material.IMaterial;
import net.silentchaos512.gear.api.material.IMaterialInstance;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.init.SgRecipes;
import net.silentchaos512.gear.item.FragmentItem;
import net.silentchaos512.gear.item.RepairKitItem;
import net.silentchaos512.gear.util.Const;
import net.silentchaos512.lib.collection.StackList;

public class FillRepairKitRecipe extends CustomRecipe {
    public FillRepairKitRecipe(ResourceLocation idIn) {
        super(idIn);
    }

    @Override
    public boolean matches(CraftingContainer inv, Level worldIn) {
        // Need 1 repair kit and 1+ mats
        boolean kitFound = false;
        int matsFound = 0;

        for (int i = 0; i < inv.getContainerSize(); ++i) {
            ItemStack stack = inv.getItem(i);
            if (!stack.isEmpty()) {
                if (stack.getItem() instanceof RepairKitItem) {
                    if (kitFound) {
                        return false;
                    }
                    kitFound = true;
                } else if (isRepairMaterial(stack)) {
                    ++matsFound;
                } else {
                    return false;
                }
            }
        }

        return kitFound && matsFound > 0;
    }

    @Override
    public ItemStack assemble(CraftingContainer inv) {
        StackList list = StackList.from(inv);
        ItemStack repairKit = list.uniqueOfType(RepairKitItem.class).copy();
        repairKit.setCount(1);
        RepairKitItem repairKitItem = (RepairKitItem) repairKit.getItem();

        for (ItemStack mat : list.allMatches(FillRepairKitRecipe::isRepairMaterial)) {
            if (!repairKitItem.addMaterial(repairKit, mat)) {
                // Repair kit is too full to accept more materials
                return ItemStack.EMPTY;
            }
        }

        return repairKit;
    }

    private static boolean isRepairMaterial(ItemStack stack) {
        if (stack.getItem() instanceof FragmentItem) {
            IMaterialInstance material = FragmentItem.getMaterial(stack);
            return material != null && isRepairMaterial(material);
        }

        MaterialInstance material = MaterialInstance.from(stack);
        return material != null && isRepairMaterial(material);
    }

    private static boolean isRepairMaterial(IMaterialInstance material) {
        float durability = material.getStat(PartType.MAIN, ItemStats.DURABILITY);
        float armorDurability = material.getStat(PartType.MAIN, ItemStats.ARMOR_DURABILITY);
        IMaterial mat = material.get();
        return mat != null && mat.allowedInPart(material, PartType.MAIN)
                && (durability > 0 || armorDurability > 0);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public ResourceLocation getId() {
        return Const.FILL_REPAIR_KIT;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SgRecipes.FILL_REPAIR_KIT.get();
    }

    public static final class Serializer implements RecipeSerializer<FillRepairKitRecipe> {
        @Override
        public FillRepairKitRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            return new FillRepairKitRecipe(recipeId);
        }

        @Override
        public FillRepairKitRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            return new FillRepairKitRecipe(recipeId);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, FillRepairKitRecipe recipe) {}
    }
}
