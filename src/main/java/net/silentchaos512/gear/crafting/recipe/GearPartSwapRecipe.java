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
import net.silentchaos512.gear.api.parts.PartDataList;
import net.silentchaos512.gear.api.parts.PartType;
import net.silentchaos512.gear.parts.PartData;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.lib.collection.StackList;

import java.util.Collection;
import java.util.HashSet;

public class GearPartSwapRecipe implements ICraftingRecipe {
    public static final ResourceLocation NAME = new ResourceLocation(SilentGear.MOD_ID, "swap_gear_part");
    public static final Serializer SERIALIZER = new Serializer();

    @Override
    public boolean matches(CraftingInventory inv, World worldIn) {
        StackList list = StackList.from(inv);
        ItemStack gear = list.uniqueOfType(ICoreItem.class);
        if (gear.isEmpty()) return false;

        ICoreItem item = (ICoreItem) gear.getItem();
        Collection<ItemStack> others = list.allMatches(stack -> !(stack.getItem() instanceof ICoreItem));
        if (others.isEmpty()) return false;

        Collection<PartType> typesFound = new HashSet<>();

        for (ItemStack stack : others) {
            PartData part = PartData.fromStackFast(stack);
            if (part == null) return false;

            // Only required part types (no mains), and no duplicates
            PartType type = part.getType();
            if (type == PartType.MAIN || !item.requiresPartOfType(type) || typesFound.contains(type)) {
                return false;
            }
            typesFound.add(type);
        }
        return true;
    }

    @Override
    public ItemStack getCraftingResult(CraftingInventory inv) {
        StackList list = StackList.from(inv);
        ItemStack gear = list.uniqueOfType(ICoreItem.class);
        if (gear.isEmpty()) return ItemStack.EMPTY;

        Collection<ItemStack> others = list.allMatches(stack -> !(stack.getItem() instanceof ICoreItem));
        if (others.isEmpty()) return ItemStack.EMPTY;

        ItemStack result = gear.copy();
        PartDataList parts = GearData.getConstructionParts(result);

        for (ItemStack stack : others) {
            PartData part = PartData.from(stack);
            if (part == null) return ItemStack.EMPTY;

            // Remove old part of type, replace
            // TODO: Can we return the old part? Probably not reliably with vanilla crafting...
            PartType type = part.getType();
            parts.removeIf(p -> p.getType() == type);
            parts.add(part);
        }

        GearData.writeConstructionParts(result, parts);
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

    public static final class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<GearPartSwapRecipe> {

        @Override
        public GearPartSwapRecipe read(ResourceLocation recipeId, JsonObject json) {
            return new GearPartSwapRecipe();
        }

        @Override
        public GearPartSwapRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
            return new GearPartSwapRecipe();
        }

        @Override
        public void write(PacketBuffer buffer, GearPartSwapRecipe recipe) {}
    }
}
