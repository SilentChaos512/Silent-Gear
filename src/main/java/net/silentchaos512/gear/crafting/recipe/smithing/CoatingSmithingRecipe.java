package net.silentchaos512.gear.crafting.recipe.smithing;

import com.google.gson.JsonObject;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeHooks;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.gear.part.PartData;
import net.silentchaos512.gear.init.ModRecipes;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;

import java.util.Objects;

public class CoatingSmithingRecipe extends GearSmithingRecipe {
    public CoatingSmithingRecipe(ResourceLocation recipeIdIn, ItemStack gearItem, Ingredient additionIn) {
        super(recipeIdIn, gearItem, additionIn);
    }

    @Override
    protected ItemStack applyUpgrade(ItemStack gear, ItemStack upgradeItem) {
        MaterialInstance material = MaterialInstance.from(upgradeItem);
        if (material != null) {
            GearType gearType = GearHelper.getType(gear);
            if (gearType.isGear()) {
                ItemStack result = gear.copy();

                PartType.COATING.getCompoundPartItem(gearType).ifPresent(cpi -> {
                    ItemStack partItem = cpi.create(material, 1);
                    // Unfortunately this deletes the old part; can't get a player here
                    GearData.addOrReplacePart(result, Objects.requireNonNull(PartData.from(partItem)));
                });

                result.setDamageValue(0);
                GearData.removeExcessParts(result, PartType.COATING);
                GearData.recalculateStats(result, ForgeHooks.getCraftingPlayer()); // Crafting player is always null?
                return result;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.SMITHING_COATING.get();
    }

    public static class Serializer implements RecipeSerializer<CoatingSmithingRecipe> {
        @Override
        public CoatingSmithingRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            ItemStack gearItem = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "gear"));
            Ingredient upgradeItem = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "addition"));
            return new CoatingSmithingRecipe(recipeId, gearItem, upgradeItem);
        }

        @Override
        public CoatingSmithingRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            ItemStack itemstack = buffer.readItem();
            Ingredient ingredient1 = Ingredient.fromNetwork(buffer);
            return new CoatingSmithingRecipe(recipeId, itemstack, ingredient1);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, CoatingSmithingRecipe recipe) {
            buffer.writeItem(recipe.gearItem);
            recipe.addition.toNetwork(buffer);
        }
    }
}
