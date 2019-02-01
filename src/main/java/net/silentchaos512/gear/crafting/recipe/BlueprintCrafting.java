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
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.inventory.InventoryCraftingStation;
import net.silentchaos512.gear.item.blueprint.IBlueprint;
import net.silentchaos512.gear.parts.PartManager;
import net.silentchaos512.lib.collection.StackList;

import javax.annotation.Nonnull;
import java.util.Collection;

public final class BlueprintCrafting implements IRecipe {
    private final ResourceLocation recipeId;
    private final IItemProvider outputType;

    private BlueprintCrafting(ResourceLocation id, IItemProvider outputType) {
        this.recipeId = id;
        this.outputType = outputType;
    }

    @Override
    public ItemStack getCraftingResult(IInventory inv) {
        StackList list = StackList.from(inv);
        ItemStack blueprint = list.firstOfType(IBlueprint.class);
        list.remove(blueprint);
        return ((IBlueprint) blueprint.getItem()).getCraftingResult(blueprint, list);
    }

    @Override
    public boolean matches(IInventory inv, World world) {
        StackList list = StackList.from(inv);
        ItemStack blueprint = list.uniqueOfType(IBlueprint.class);

        // Only one blueprint
        if (blueprint.isEmpty()) {
            return false;
        }

        Collection<ItemStack> materials = list.allMatches(s -> PartManager.from(s) != null);
        int materialCount = materials.size();
        IBlueprint blueprintItem = (IBlueprint) blueprint.getItem();

        // Right number of materials and nothing else? FIXME: blueprint book support?
        if (materialCount + 1 != list.size() || materialCount != blueprintItem.getMaterialCost(blueprint)) {
            return false;
        }

        // Inventory allows mixing?
        return inventoryAllowsMixedMaterial(inv) || materials.stream().map(PartManager::from).distinct().count() == 1;
    }

    private static boolean inventoryAllowsMixedMaterial(IInventory inv) {
        return inv instanceof InventoryCraftingStation;
    }

    @Override
    public boolean canFit(int width, int height) {
        return true;
    }

    @Nonnull
    @Override
    public ItemStack getRecipeOutput() {
        return new ItemStack(outputType);
    }

    @Override
    public ResourceLocation getId() {
        return recipeId;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public boolean isDynamic() {
        return true;
    }

    public static class Serializer implements IRecipeSerializer<BlueprintCrafting> {
        public static final Serializer INSTANCE = new Serializer();

        private Serializer() {}

        @Override
        public BlueprintCrafting read(ResourceLocation recipeId, JsonObject json) {
            return new BlueprintCrafting(recipeId, Items.AIR); //FIXME
        }

        @Override
        public BlueprintCrafting read(ResourceLocation recipeId, PacketBuffer buffer) {
            return new BlueprintCrafting(recipeId, Items.AIR); // FIXME
        }

        @Override
        public void write(PacketBuffer buffer, BlueprintCrafting recipe) { }

        @Override
        public ResourceLocation getName() {
            return new ResourceLocation(SilentGear.MOD_ID, "blueprint_crafting");
        }
    }
}
