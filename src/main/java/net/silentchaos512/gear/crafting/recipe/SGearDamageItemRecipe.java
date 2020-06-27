package net.silentchaos512.gear.crafting.recipe;

import com.google.gson.JsonObject;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.ShapelessRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.lib.crafting.recipe.DamageItemRecipe;

import javax.annotation.Nullable;

public class SGearDamageItemRecipe extends DamageItemRecipe {
    public static final ResourceLocation NAME = SilentGear.getId("damage_item");
    public static final IRecipeSerializer<SGearDamageItemRecipe> SERIALIZER = new Serializer();

    private final int minGearTear;

    public SGearDamageItemRecipe(ShapelessRecipe recipe, int minGearTear) {
        super(recipe);
        this.minGearTear = minGearTear;
    }

    @Override
    public boolean matches(CraftingInventory inv, World worldIn) {
        return super.matches(inv, worldIn) && gearItemsMatchForCrafting(inv);
    }

    private boolean gearItemsMatchForCrafting(IInventory inv) {
        for (int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack stack = inv.getStackInSlot(i);
            if (GearHelper.isGear(stack) && GearHelper.isBroken(stack) && GearData.getTier(stack) >= this.minGearTear) {
                return false;
            }
        }
        return true;
    }

    private static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<SGearDamageItemRecipe> {
        @Override
        public SGearDamageItemRecipe read(ResourceLocation recipeId, JsonObject json) {
            int tier = JSONUtils.getInt(json, "minGearTier", 0);
            return new SGearDamageItemRecipe(DamageItemRecipe.SERIALIZER.read(recipeId, json), tier);
        }

        @Nullable
        @Override
        public SGearDamageItemRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
            DamageItemRecipe read = DamageItemRecipe.SERIALIZER.read(recipeId, buffer);
            int tier = buffer.readVarInt();
            return read != null ? new SGearDamageItemRecipe(read, tier) : null;
        }

        @Override
        public void write(PacketBuffer buffer, SGearDamageItemRecipe recipe) {
            DamageItemRecipe.SERIALIZER.write(buffer, recipe);
            buffer.writeVarInt(recipe.minGearTear);
        }
    }
}
