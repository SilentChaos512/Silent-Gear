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
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.parts.IUpgradePart;
import net.silentchaos512.gear.config.Config;
import net.silentchaos512.gear.parts.PartData;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.lib.collection.StackList;

import java.util.Collection;

public class UpgradeGearRecipe implements ICraftingRecipe {
    public static final ResourceLocation NAME = new ResourceLocation(SilentGear.MOD_ID, "upgrade_gear");
    public static final Serializer SERIALIZER = new Serializer();

    @Override
    public boolean matches(CraftingInventory inv, World worldIn) {
        if (Config.GENERAL.upgradesInAnvilOnly.get()) return false;

        StackList list = StackList.from(inv);
        // Require 1 and only 1 gear item
        ItemStack gear = list.uniqueOfType(ICoreItem.class);
        if (gear.isEmpty()) return false;

        // Require at least 1 upgrade part
        ItemStack upgrade = list.firstMatch(stack -> {
            PartData part = PartData.fromStackFast(stack);
            return part != null && canApplyUpgrade(gear, part);
        });
        return !gear.isEmpty() && !upgrade.isEmpty();
    }

    private static boolean canApplyUpgrade(ItemStack gear, PartData part) {
        ICoreItem gearItem = (ICoreItem) gear.getItem();
        return part.getPart() instanceof IUpgradePart && ((IUpgradePart) part.getPart()).isValidFor(gearItem);
    }

    @Override
    public ItemStack getCraftingResult(CraftingInventory inv) {
        StackList list = StackList.from(inv);
        ItemStack gear = list.uniqueOfType(ICoreItem.class);
        if (gear.isEmpty()) return ItemStack.EMPTY;

        Collection<ItemStack> upgrades = list.allMatches(stack -> {
            PartData part = PartData.fromStackFast(stack);
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
