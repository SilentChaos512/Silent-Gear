/*
 * Silent Gear -- RecipeModularItem
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

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.parts.ItemPartData;
import net.silentchaos512.gear.api.parts.PartRegistry;
import net.silentchaos512.gear.api.parts.PartType;
import net.silentchaos512.gear.item.ToolHead;
import net.silentchaos512.lib.recipe.RecipeBaseSL;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Predicate;

public class RecipeModularItem extends RecipeBaseSL {
    private final ICoreItem item;

    public RecipeModularItem(ICoreItem item) {
        this.item = item;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        Collection<ItemStack> parts = getComponents(inv);
        return getCraftingResult(parts);
    }

    public ItemStack getCraftingResult(Collection<ItemStack> parts) {
        List<ItemPartData> data = new ArrayList<>();
        Map<PartType, ItemPartData> partsByType = new HashMap<>();

        for (ItemStack stack : parts) {
            ItemPartData part = ItemPartData.fromStack(stack);
            if (stack.getItem() instanceof ToolHead) {
                if (!ToolHead.getToolClass(stack).equals(this.item.getGearClass()))
                    return ItemStack.EMPTY;
                data.addAll(ToolHead.getAllParts(stack));
            }
            else if (part != null) {
                PartType type = part.getPart().getType();
                if (partsByType.containsKey(type) && partsByType.get(type).getPart() != part.getPart())
                    return ItemStack.EMPTY;
                partsByType.put(part.getPart().getType(), part);
            }
        }

        data.addAll(partsByType.values());
        return this.item.construct((Item) this.item, data);
    }

    @Override
    public boolean matches(InventoryCrafting inv, World world) {
        Collection<ItemStack> parts = getComponents(inv);
        return item.matchesRecipe(parts) && parts.size() == getNonEmptyStacks(inv).size();
    }

    @Nonnull
    @Override
    public ItemStack getRecipeOutput() {
        return new ItemStack((Item) this.item);
    }

    private Collection<ItemStack> getComponents(InventoryCrafting inv) {
        List<ItemStack> parts = new ArrayList<>();
        parts.addAll(getComponents(inv, s -> s.getItem() instanceof ToolHead));
        parts.addAll(getComponents(inv, s -> PartRegistry.get(s) != null));
        return parts;
    }

    private Collection<ItemStack> getComponents(InventoryCrafting inv, Predicate<ItemStack> predicate) {
        List<ItemStack> parts = new ArrayList<>();
        for (int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack stack = inv.getStackInSlot(i);
            if (!stack.isEmpty() && predicate.test(stack))
                parts.add(stack);
        }
        return parts;
    }
}
