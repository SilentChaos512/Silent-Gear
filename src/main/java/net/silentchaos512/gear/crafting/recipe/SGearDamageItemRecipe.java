package net.silentchaos512.gear.crafting.recipe;

import com.google.gson.JsonObject;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.lib.crafting.recipe.DamageItemRecipe;

import javax.annotation.Nullable;

public class SGearDamageItemRecipe extends DamageItemRecipe {
    private final int minGearTear;

    public SGearDamageItemRecipe(ShapelessRecipe recipe, int minGearTear) {
        super(recipe);
        this.minGearTear = minGearTear;
    }

    @Override
    public boolean matches(CraftingContainer inv, Level worldIn) {
        return super.matches(inv, worldIn) && gearItemsMatchForCrafting(inv);
    }

    private boolean gearItemsMatchForCrafting(Container inv) {
        for (int i = 0; i < inv.getContainerSize(); ++i) {
            ItemStack stack = inv.getItem(i);
            if (GearHelper.isGear(stack) && GearHelper.isBroken(stack) && GearData.getTier(stack) >= this.minGearTear) {
                return false;
            }
        }
        return true;
    }

    public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<SGearDamageItemRecipe> {
        @Override
        public SGearDamageItemRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            int tier = GsonHelper.getAsInt(json, "minGearTier", 0);
            return new SGearDamageItemRecipe(DamageItemRecipe.SERIALIZER.fromJson(recipeId, json), tier);
        }

        @Nullable
        @Override
        public SGearDamageItemRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            DamageItemRecipe read = DamageItemRecipe.SERIALIZER.fromNetwork(recipeId, buffer);
            int tier = buffer.readVarInt();
            return read != null ? new SGearDamageItemRecipe(read, tier) : null;
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, SGearDamageItemRecipe recipe) {
            DamageItemRecipe.SERIALIZER.toNetwork(buffer, recipe);
            buffer.writeVarInt(recipe.minGearTear);
        }
    }
}
