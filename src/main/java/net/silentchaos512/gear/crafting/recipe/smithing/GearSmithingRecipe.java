package net.silentchaos512.gear.crafting.recipe.smithing;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.UpgradeRecipe;
import net.minecraft.resources.ResourceLocation;

public abstract class GearSmithingRecipe extends UpgradeRecipe {
    protected final Ingredient addition;
    protected final ItemStack gearItem;

    public GearSmithingRecipe(ResourceLocation recipeIdIn, ItemStack gearItem, Ingredient additionIn) {
        super(recipeIdIn, Ingredient.of(gearItem.getItem()), additionIn, gearItem);
        this.addition = additionIn;
        this.gearItem = gearItem;
    }

    @Override
    public ItemStack assemble(Container inv) {
        ItemStack gear = inv.getItem(0).copy();
        ItemStack upgradeItem = inv.getItem(1);
        return applyUpgrade(gear, upgradeItem);
    }

    protected abstract ItemStack applyUpgrade(ItemStack gear, ItemStack upgradeItem);

    @Override
    public abstract RecipeSerializer<?> getSerializer();
}
