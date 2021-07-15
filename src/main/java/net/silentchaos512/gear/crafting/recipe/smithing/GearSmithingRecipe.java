package net.silentchaos512.gear.crafting.recipe.smithing;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.SmithingRecipe;
import net.minecraft.util.ResourceLocation;

public abstract class GearSmithingRecipe extends SmithingRecipe {
    protected final Ingredient addition;
    protected final ItemStack gearItem;

    public GearSmithingRecipe(ResourceLocation recipeIdIn, ItemStack gearItem, Ingredient additionIn) {
        super(recipeIdIn, Ingredient.of(gearItem.getItem()), additionIn, gearItem);
        this.addition = additionIn;
        this.gearItem = gearItem;
    }

    @Override
    public ItemStack assemble(IInventory inv) {
        ItemStack gear = inv.getItem(0).copy();
        ItemStack upgradeItem = inv.getItem(1);
        return applyUpgrade(gear, upgradeItem);
    }

    protected abstract ItemStack applyUpgrade(ItemStack gear, ItemStack upgradeItem);

    @Override
    public abstract IRecipeSerializer<?> getSerializer();
}
