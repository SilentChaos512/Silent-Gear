package net.silentchaos512.gear.crafting.recipe.smithing;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.neoforged.neoforge.common.CommonHooks;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.gear.part.PartData;
import net.silentchaos512.gear.setup.SgRecipes;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;

public class UpgradeSmithingRecipe extends GearSmithingRecipe {
    public UpgradeSmithingRecipe(ResourceLocation recipeId, ItemStack gearItem, Ingredient template, Ingredient addition) {
        super(recipeId, gearItem, template, addition);
    }

    @Override
    protected ItemStack applyUpgrade(ItemStack gear, ItemStack upgradeItem) {
        PartData part = PartData.from(upgradeItem);
        if (part != null) {
            GearType gearType = GearHelper.getType(gear);
            if (gearType.isGear() && part.get().canAddToGear(gear, part) && !GearData.hasPart(gear, part.get())) {
                ItemStack result = gear.copy();
                GearData.addPart(result, part);
                GearData.recalculateStats(result, CommonHooks.getCraftingPlayer());
                return result;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SgRecipes.SMITHING_UPGRADE.get();
    }

    public static class Serializer implements RecipeSerializer<UpgradeSmithingRecipe> {
        @Override
        public UpgradeSmithingRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            ItemStack gearItem = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "gear"));
            Ingredient template = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "template"));
            Ingredient upgradeItem = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "addition"));
            return new UpgradeSmithingRecipe(recipeId, gearItem, template, upgradeItem);
        }

        @Override
        public UpgradeSmithingRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            ItemStack gearItem = buffer.readItem();
            Ingredient template = Ingredient.fromNetwork(buffer);
            Ingredient addition = Ingredient.fromNetwork(buffer);
            return new UpgradeSmithingRecipe(recipeId, gearItem, template, addition);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, UpgradeSmithingRecipe recipe) {
            buffer.writeItem(recipe.gearItem);
            recipe.template.toNetwork(buffer);
            recipe.addition.toNetwork(buffer);
        }
    }
}
