package net.silentchaos512.gear.block.compounder;

import com.google.common.collect.ImmutableList;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.tileentity.TileEntityType;
import net.silentchaos512.gear.api.material.IMaterialCategory;
import net.silentchaos512.gear.crafting.recipe.compounder.CompoundingRecipe;
import net.silentchaos512.gear.item.CompoundMaterialItem;

import java.util.Collection;
import java.util.function.Supplier;

public class CompounderInfo {
    private final Supplier<TileEntityType<? extends CompounderTileEntity>> tileEntityType;
    private final Supplier<ContainerType<? extends CompounderContainer>> containerType;
    private final Supplier<IRecipeType<CompoundingRecipe>> recipeType;
    private final Supplier<CompoundMaterialItem> outputItem;
    private final int inputSlotCount;
    private final ImmutableList<IMaterialCategory> categories;

    @SuppressWarnings("ConstructorWithTooManyParameters")
    public CompounderInfo(Supplier<TileEntityType<? extends CompounderTileEntity>> tileEntityType,
                          Supplier<ContainerType<? extends CompounderContainer>> containerType,
                          Supplier<IRecipeType<CompoundingRecipe>> recipeType,
                          Supplier<CompoundMaterialItem> outputItem,
                          int inputSlotCount,
                          Collection<IMaterialCategory> categories) {
        this.tileEntityType = tileEntityType;
        this.containerType = containerType;
        this.recipeType = recipeType;
        this.outputItem = outputItem;
        this.inputSlotCount = inputSlotCount;
        this.categories = ImmutableList.copyOf(categories);
    }

    public TileEntityType<? extends CompounderTileEntity> getTileEntityType() {
        return tileEntityType.get();
    }

    public ContainerType<? extends CompounderContainer> getContainerType() {
        return containerType.get();
    }

    public IRecipeType<CompoundingRecipe> getRecipeType() {
        return recipeType.get();
    }

    public CompoundMaterialItem getOutputItem() {
        return outputItem.get();
    }

    public int getInputSlotCount() {
        return inputSlotCount;
    }

    public Collection<IMaterialCategory> getCategories() {
        return categories;
    }
}
