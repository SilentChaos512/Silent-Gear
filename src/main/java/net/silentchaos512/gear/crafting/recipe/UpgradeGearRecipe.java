/*
 * Silent Gear -- UpgradeGearRecipe
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
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.part.IGearPart;
import net.silentchaos512.gear.api.part.IUpgradePart;
import net.silentchaos512.gear.api.part.PartDataList;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.config.Config;
import net.silentchaos512.gear.gear.part.PartData;
import net.silentchaos512.gear.gear.part.PartManager;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.lib.collection.StackList;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Deprecated
public class UpgradeGearRecipe implements ICraftingRecipe {
    public static final ResourceLocation NAME = new ResourceLocation(SilentGear.MOD_ID, "upgrade_gear");
    public static final Serializer SERIALIZER = new Serializer();

    @Override
    public boolean matches(CraftingInventory inv, World worldIn) {
        if (Config.Common.upgradesInAnvilOnly.get()) return false;

        StackList list = StackList.from(inv);
        // Require 1 and only 1 gear item
        ItemStack gear = list.uniqueOfType(ICoreItem.class);
        if (gear.isEmpty()) return false;

        // Require at least 1 upgrade part
        Collection<ItemStack> upgrades = list.allMatches(stack -> {
            PartData part = PartData.from(stack);
            return part != null && part.getPart() instanceof IUpgradePart;
        });
        if (upgrades.isEmpty()) return false;


        // Test applying the upgrades, make sure there is only one upgrade per position
        Set<PartType> types = new HashSet<>();
        ItemStack test = gear.copy();
        for (ItemStack upgrade : upgrades) {
            PartData part = PartData.from(upgrade);
            if (part == null || types.contains(part.getType()) || !canApplyUpgrade(test, part))
                return false;
            if (part.getType() != PartType.MISC_UPGRADE)
                types.add(part.getType());
            GearData.addUpgradePart(test, part);
        }

        return true;
    }

    private static boolean canApplyUpgrade(ItemStack gear, PartData part) {
        IGearPart gearPart = part.getPart();
        return !GearData.hasPart(gear, gearPart) && part.getPart().canAddToGear(gear, part);
    }

    @Override
    public ItemStack getCraftingResult(CraftingInventory inv) {
        StackList list = StackList.from(inv);
        ItemStack gear = list.uniqueOfType(ICoreItem.class);
        if (gear.isEmpty()) return ItemStack.EMPTY;

        Collection<ItemStack> upgrades = list.allMatches(stack -> {
            PartData part = PartData.from(stack);
            return part != null && part.getPart() instanceof IUpgradePart;
        });

        ItemStack result = gear.copy();

        for (ItemStack upgrade : upgrades) {
            GearData.addUpgradePart(result, upgrade);
        }
        GearData.recalculateStats(result, null);
        return result;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInventory inv) {
        NonNullList<ItemStack> list = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
        StackList stackList = StackList.from(inv);
        ItemStack gear = stackList.uniqueMatch(s -> s.getItem() instanceof ICoreItem);
        PartDataList oldParts = GearData.getConstructionParts(gear);

        for (int i = 0; i < list.size(); ++i) {
            ItemStack stack = inv.getStackInSlot(i);

            if (stack.getItem() instanceof ICoreItem) {
                list.set(i, ItemStack.EMPTY);
            } else {
                IGearPart part = PartManager.from(stack);
                if (part != null) {
                    List<PartData> partsOfType = oldParts.getPartsOfType(part.getType());
                    if (!partsOfType.isEmpty()) {
                        PartData partData = partsOfType.get(0);
                        partData.onRemoveFromGear(gear);
                        list.set(i, partData.getCraftingItem());
                    } else {
                        list.set(i, ItemStack.EMPTY);
                    }
                }
            }
        }

        return list;
    }

    @Override
    public boolean canFit(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getRecipeOutput() {
        // Cannot determine
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isDynamic() {
        return true;
    }

    @Override
    public ResourceLocation getId() {
        return NAME;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    public static final class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<UpgradeGearRecipe> {
        @Override
        public UpgradeGearRecipe read(ResourceLocation recipeId, JsonObject json) {
            return new UpgradeGearRecipe();
        }

        @Override
        public UpgradeGearRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
            return new UpgradeGearRecipe();
        }

        @Override
        public void write(PacketBuffer buffer, UpgradeGearRecipe recipe) {}
    }
}
