package net.silentchaos512.gear.crafting.recipe;

import com.google.gson.JsonObject;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.ShapelessRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.lib.crafting.recipe.DamageItemRecipe;

import javax.annotation.Nullable;

public class SGearDamageItemRecipe extends DamageItemRecipe {
    public static final ResourceLocation NAME = SilentGear.getId("damage_item");
    public static final IRecipeSerializer<SGearDamageItemRecipe> SERIALIZER = new Serializer();

    public SGearDamageItemRecipe(ShapelessRecipe recipe) {
        super(recipe);
    }

    @Override
    public boolean matches(CraftingInventory inv, World worldIn) {
        return super.matches(inv, worldIn) && hasNoBrokenGear(inv);
    }

    private static boolean hasNoBrokenGear(IInventory inv) {
        for (int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack stack = inv.getStackInSlot(i);
            if (GearHelper.isGear(stack) && GearHelper.isBroken(stack)) {
                return false;
            }
        }
        return true;
    }

    private static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<SGearDamageItemRecipe> {
        @Override
        public SGearDamageItemRecipe read(ResourceLocation recipeId, JsonObject json) {
            return new SGearDamageItemRecipe(DamageItemRecipe.SERIALIZER.read(recipeId, json));
        }

        @Nullable
        @Override
        public SGearDamageItemRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
            DamageItemRecipe read = DamageItemRecipe.SERIALIZER.read(recipeId, buffer);
            return read != null ? new SGearDamageItemRecipe(read) : null;
        }

        @Override
        public void write(PacketBuffer buffer, SGearDamageItemRecipe recipe) {
            DamageItemRecipe.SERIALIZER.write(buffer, recipe);
        }
    }
}
