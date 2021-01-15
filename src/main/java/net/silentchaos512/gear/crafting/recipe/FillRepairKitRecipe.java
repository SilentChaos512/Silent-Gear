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
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.init.ModRecipes;
import net.silentchaos512.gear.item.RepairKitItem;
import net.silentchaos512.gear.util.Const;
import net.silentchaos512.lib.collection.StackList;

public class FillRepairKitRecipe extends SpecialRecipe {
    public FillRepairKitRecipe(ResourceLocation idIn) {
        super(idIn);
    }

    @Override
    public boolean matches(CraftingInventory inv, World worldIn) {
        // Need 1 repair kit and 1+ mats
        boolean kitFound = false;
        int matsFound = 0;

        for (int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack stack = inv.getStackInSlot(i);
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
    public ItemStack getCraftingResult(CraftingInventory inv) {
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
        MaterialInstance material = MaterialInstance.from(stack);
        return material != null && isRepairMaterial(material);
    }

    private static boolean isRepairMaterial(MaterialInstance material) {
        float durability = material.getStat(PartType.MAIN, ItemStats.DURABILITY);
        float armorDurability = material.getStat(PartType.MAIN, ItemStats.ARMOR_DURABILITY);
        return material.get().allowedInPart(material, PartType.MAIN) && (durability > 0 || armorDurability > 0);
    }

    @Override
    public boolean canFit(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public ResourceLocation getId() {
        return Const.FILL_REPAIR_KIT;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ModRecipes.FILL_REPAIR_KIT.get();
    }

    public static final class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<FillRepairKitRecipe> {
        @Override
        public FillRepairKitRecipe read(ResourceLocation recipeId, JsonObject json) {
            return new FillRepairKitRecipe(recipeId);
        }

        @Override
        public FillRepairKitRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
            return new FillRepairKitRecipe(recipeId);
        }

        @Override
        public void write(PacketBuffer buffer, FillRepairKitRecipe recipe) {}
    }
}
