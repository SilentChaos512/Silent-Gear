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
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.gear.part.PartData;
import net.silentchaos512.gear.init.ModRecipes;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;

public class UpgradeSmithingRecipe extends GearSmithingRecipe {
    public UpgradeSmithingRecipe(ResourceLocation recipeIdIn, ItemStack gearItem, Ingredient additionIn) {
        super(recipeIdIn, gearItem, additionIn);
    }

    @Override
    protected ItemStack applyUpgrade(ItemStack gear, ItemStack upgradeItem) {
        PartData part = PartData.from(upgradeItem);
        if (part != null) {
            GearType gearType = GearHelper.getType(gear);
            if (gearType.isGear() && part.get().canAddToGear(gear, part) && !GearData.hasPart(gear, part.get())) {
                ItemStack result = gear.copy();
                GearData.addPart(result, part);
                GearData.recalculateStats(result, ForgeHooks.getCraftingPlayer());
                return result;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.SMITHING_UPGRADE.get();
    }

    public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<UpgradeSmithingRecipe> {
        @Override
        public UpgradeSmithingRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            ItemStack gearItem = ShapedRecipe.itemFromJson(GsonHelper.getAsJsonObject(json, "gear"));
            Ingredient upgradeItem = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "addition"));
            return new UpgradeSmithingRecipe(recipeId, gearItem, upgradeItem);
        }

        @Override
        public UpgradeSmithingRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            ItemStack itemstack = buffer.readItem();
            Ingredient ingredient1 = Ingredient.fromNetwork(buffer);
            return new UpgradeSmithingRecipe(recipeId, itemstack, ingredient1);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, UpgradeSmithingRecipe recipe) {
            buffer.writeItem(recipe.gearItem);
            recipe.addition.toNetwork(buffer);
        }
    }
}
