package net.silentchaos512.gear.crafting.recipe.smithing;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.LegacyUpgradeRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

public abstract class GearSmithingRecipe extends LegacyUpgradeRecipe {
    protected final Ingredient addition;
    protected final ItemStack gearItem;

    public GearSmithingRecipe(ResourceLocation recipeIdIn, ItemStack gearItem, Ingredient additionIn) {
        super(recipeIdIn, Ingredient.of(gearItem.getItem()), additionIn, gearItem);
        this.addition = additionIn;
        this.gearItem = gearItem;
    }

    @Override
    public ItemStack assemble(Container inv, RegistryAccess registryAccess) {
        ItemStack gear = inv.getItem(0).copy();
        ItemStack upgradeItem = inv.getItem(1);
        return applyUpgrade(gear, upgradeItem);
    }

    protected abstract ItemStack applyUpgrade(ItemStack gear, ItemStack upgradeItem);

    @Override
    public abstract RecipeSerializer<?> getSerializer();
}
