package net.silentchaos512.gear.crafting.recipe.smithing;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
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
            if (gearType.isGear() && part.getPart().canAddToGear(gear, part) && !GearData.hasPart(gear, part.getPart())) {
                ItemStack result = gear.copy();
                GearData.addPart(result, part);
                GearData.recalculateStats(result, ForgeHooks.getCraftingPlayer());
                return result;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ModRecipes.UPGRADE_SMITHING;
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<UpgradeSmithingRecipe> {
        @Override
        public UpgradeSmithingRecipe read(ResourceLocation recipeId, JsonObject json) {
            ItemStack gearItem = ShapedRecipe.deserializeItem(JSONUtils.getJsonObject(json, "gear"));
            Ingredient upgradeItem = Ingredient.deserialize(JSONUtils.getJsonObject(json, "addition"));
            return new UpgradeSmithingRecipe(recipeId, gearItem, upgradeItem);
        }

        @Override
        public UpgradeSmithingRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
            ItemStack itemstack = buffer.readItemStack();
            Ingredient ingredient1 = Ingredient.read(buffer);
            return new UpgradeSmithingRecipe(recipeId, itemstack, ingredient1);
        }

        @Override
        public void write(PacketBuffer buffer, UpgradeSmithingRecipe recipe) {
            buffer.writeItemStack(recipe.gearItem);
            recipe.addition.write(buffer);
        }
    }
}
