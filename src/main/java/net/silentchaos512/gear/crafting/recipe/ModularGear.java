/*
 * Silent Gear -- ModularGear
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
import com.google.gson.JsonSyntaxException;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.parts.PartType;
import net.silentchaos512.gear.parts.PartData;
import net.silentchaos512.gear.parts.PartManager;
import net.silentchaos512.lib.collection.StackList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

// FIXME: ModRecipes has a map these must exist in. How to get the recipes?
public final class ModularGear implements IRecipe {
    private final ResourceLocation recipeId;
    private final ICoreItem item;

    private ModularGear(ResourceLocation recipeId, ICoreItem item) {
        this.recipeId = recipeId;
        this.item = item;
    }

    @Override
    public ItemStack getCraftingResult(IInventory inv) {
        Collection<ItemStack> parts = getComponents(inv);
        return getCraftingResult(parts);
    }

    @Override
    public boolean canFit(int width, int height) {
        return false;
    }

    public ItemStack getCraftingResult(Collection<ItemStack> parts) {
        List<PartData> data = new ArrayList<>();
        Map<PartType, PartData> partsByType = new HashMap<>();

        for (ItemStack stack : parts) {
            PartData part = PartData.from(stack);
            /*
            if (stack.getItem() instanceof ToolHead) {
                if (!ToolHead.getToolClass(stack).equals(this.item.getGearClass()))
                    return ItemStack.EMPTY;
                data.addAll(ToolHead.getAllParts(stack));
            } else if (part != null) {
                PartType type = part.getPart().getType();
                if (partsByType.containsKey(type) && partsByType.get(type).getPart() != part.getPart())
                    return ItemStack.EMPTY;
                partsByType.put(part.getPart().getType(), part);
            }
            */
        }

        data.addAll(partsByType.values());
        return this.item.construct(data);
    }

    @Override
    public boolean matches(IInventory inv, World world) {
        Collection<ItemStack> parts = getComponents(inv);
        return item.matchesRecipe(parts) && parts.size() == StackList.from(inv).size();
    }

    @Nonnull
    @Override
    public ItemStack getRecipeOutput() {
        return new ItemStack((Item) this.item);
    }

    @Override
    public ResourceLocation getId() {
        return recipeId;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    private static Collection<ItemStack> getComponents(IInventory inv) {
        Collection<ItemStack> parts = new ArrayList<>();
//        parts.addAll(getComponents(inv, s -> s.getItem() instanceof ToolHead));
        parts.addAll(getComponents(inv, s -> PartManager.from(s) != null));
        return parts;
    }

    private static Collection<ItemStack> getComponents(IInventory inv, Predicate<ItemStack> predicate) {
        List<ItemStack> parts = new ArrayList<>();
        for (int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack stack = inv.getStackInSlot(i);
            if (!stack.isEmpty() && predicate.test(stack))
                parts.add(stack);
        }
        return parts;
    }

    public static class Serializer implements IRecipeSerializer<ModularGear> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public ModularGear read(ResourceLocation recipeId, JsonObject json) {
            JsonObject resultObj = json.getAsJsonObject("result");
            ResourceLocation itemId = new ResourceLocation(JsonUtils.getString(resultObj, "item", ""));
            Item item = ForgeRegistries.ITEMS.getValue(itemId);
            return makeRecipe(recipeId, item);
        }

        @Override
        public ModularGear read(ResourceLocation recipeId, PacketBuffer buffer) {
            String itemId = buffer.readString(255);
            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemId));
            return makeRecipe(recipeId, item);
        }

        private static ModularGear makeRecipe(ResourceLocation recipeId, @Nullable Item item) {
            if (item == null)
                throw new JsonSyntaxException("item does not exist");
            if (!(item instanceof ICoreItem))
                throw new JsonSyntaxException("item is not a gear item");

            return new ModularGear(recipeId, (ICoreItem) item);
        }

        @Override
        public void write(PacketBuffer buffer, ModularGear recipe) {
            buffer.writeString(Objects.requireNonNull(recipe.item.asItem().getRegistryName()).toString());
        }

        @Override
        public ResourceLocation getName() {
            return new ResourceLocation(SilentGear.MOD_ID, "gear_crafting");
        }
    }
}
