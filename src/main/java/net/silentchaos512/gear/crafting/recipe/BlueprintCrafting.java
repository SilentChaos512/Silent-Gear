/*
 * Silent Gear -- BlueprintCrafting
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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.silentchaos512.gear.api.parts.PartRegistry;
import net.silentchaos512.gear.init.ModItems;
import net.silentchaos512.gear.inventory.InventoryCraftingStation;
import net.silentchaos512.gear.item.blueprint.IBlueprint;
import net.silentchaos512.lib.collection.StackList;
import net.silentchaos512.lib.recipe.RecipeBaseSL;
import net.silentchaos512.lib.util.StackHelper;

import javax.annotation.Nonnull;
import java.util.Collection;

public class BlueprintCrafting implements IRecipeFactory {
    @Override
    public IRecipe parse(JsonContext context, JsonObject json) {
        return new Recipe(ModItems.toolHead);
    }

    private static class Recipe extends RecipeBaseSL {
        Item outputType;

        Recipe(Item outputType) {
            this.outputType = outputType;
        }

        @Override
        public ItemStack getCraftingResult(InventoryCrafting inv) {
            StackList list = StackHelper.getNonEmptyStacks(inv);
            ItemStack blueprint = list.firstOfType(IBlueprint.class);
            list.remove(blueprint);
            return ((IBlueprint) blueprint.getItem()).getCraftingResult(blueprint, list);
        }

        @Override
        public boolean matches(InventoryCrafting inv, World world) {
            StackList list = StackHelper.getNonEmptyStacks(inv);
            ItemStack blueprint = list.uniqueOfType(IBlueprint.class);

            // Only one blueprint
            if (blueprint.isEmpty()) {
                return false;
            }

            Collection<ItemStack> materials = list.allMatches(s -> PartRegistry.get(s) != null);
            int materialCount = materials.size();
            IBlueprint blueprintItem = (IBlueprint) blueprint.getItem();

            // Right number of materials and nothing else? FIXME: blueprint book support?
            if (materialCount + 1 != list.size() || materialCount != blueprintItem.getMaterialCost(blueprint)) {
                return false;
            }

            // Inventory allows mixing?
            return inventoryAllowsMixedMaterial(inv) || materials.stream().map(PartRegistry::get).distinct().count() == 1;
        }

        @Nonnull
        @Override
        public ItemStack getRecipeOutput() {
            return new ItemStack(outputType);
        }

        private static boolean inventoryAllowsMixedMaterial(InventoryCrafting inv) {
            return inv instanceof InventoryCraftingStation;
        }
    }
}
