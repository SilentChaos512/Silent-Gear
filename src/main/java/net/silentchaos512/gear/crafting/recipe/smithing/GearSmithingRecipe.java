package net.silentchaos512.gear.crafting.recipe.smithing;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.silentchaos512.gear.util.GearData;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public abstract class GearSmithingRecipe implements SmithingRecipe {
    protected final Optional<Ingredient> template;
    protected final Optional<Ingredient> addition;
    protected final Ingredient base;
    protected final ItemStack gearItem;
    @Nullable private PlacementInfo placementInfo;

    public GearSmithingRecipe(ItemStack gearItem, Optional<Ingredient> template, Optional<Ingredient> addition) {
        this.template = template;
        this.addition = addition;
        this.gearItem = gearItem;
        this.base = Ingredient.of(this.gearItem.getItem());
    }

    @Override
    public abstract RecipeSerializer<? extends GearSmithingRecipe> getSerializer();

    @Override
    public ItemStack assemble(SmithingRecipeInput input, HolderLookup.Provider registryAccess) {
        ItemStack result = input.base().transmuteCopy(this.gearItem.getItem(), 1);
        ItemStack upgradeItem = input.addition();
        applyUpgrade(result, upgradeItem);
        GearData.recalculateGearData(result, null);
        return result;
    }

    protected abstract void applyUpgrade(ItemStack stackToModify, ItemStack upgradeItem);

    @Override
    public Optional<Ingredient> templateIngredient() {
        return this.template;
    }

    @Override
    public Ingredient baseIngredient() {
        return this.base;
    }

    @Override
    public Optional<Ingredient> additionIngredient() {
        return this.addition;
    }

    @Override
    public PlacementInfo placementInfo() {
        if (this.placementInfo == null) {
            this.placementInfo = PlacementInfo.createFromOptionals(List.of(this.template, Optional.of(this.base), this.addition));
        }
        return this.placementInfo;
    }

    @FunctionalInterface
    public interface Factory<R extends GearSmithingRecipe> {
        R create(ItemStack gearItem, Optional<Ingredient> template, Optional<Ingredient> addition);
    }
}
