/*
 * Silent Gear -- RoughToolRecipe
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

package net.silentchaos512.gear.recipe.gear;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.parts.ItemPartData;
import net.silentchaos512.gear.api.parts.PartRegistry;
import net.silentchaos512.lib.collection.StackList;
import net.silentchaos512.lib.recipe.RecipeBaseSL;

import java.util.Collection;
import java.util.stream.Collectors;

public class RoughToolRecipe extends RecipeBaseSL {
    private final ShapedOreRecipe baseRecipe;

    RoughToolRecipe(ShapedOreRecipe baseRecipe) {
        this.baseRecipe = baseRecipe;
    }

    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        return baseRecipe.matches(inv, worldIn);
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        StackList list = StackList.fromInventory(inv);
        Collection<ItemPartData> parts = list.allMatches(s -> PartRegistry.get(s) != null)
                .stream()
                .map(ItemPartData::fromStack)
                .collect(Collectors.toList());

        ICoreItem item = (ICoreItem) baseRecipe.getRecipeOutput().getItem();
        return item.construct(item.getItem(), parts);
    }

    @Override
    public ItemStack getRecipeOutput() {
        return baseRecipe.getRecipeOutput();
    }
}
