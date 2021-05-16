package net.silentchaos512.gear.block.compounder;

import com.google.common.collect.ImmutableList;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.tileentity.TileEntityType;
import net.silentchaos512.gear.api.material.IMaterialCategory;
import net.silentchaos512.gear.crafting.recipe.compounder.CompoundingRecipe;
import net.silentchaos512.gear.item.CompoundMaterialItem;

import java.util.Collection;
import java.util.function.Supplier;

public class CompounderInfo<R extends CompoundingRecipe> {
    private final Supplier<CompounderBlock> block;
    private final Supplier<TileEntityType<? extends CompounderTileEntity>> tileEntityType;
    private final Supplier<ContainerType<? extends CompounderContainer>> containerType;
    private final Supplier<IRecipeType<R>> recipeType;
    private final Supplier<CompoundMaterialItem> outputItem;
    private final Supplier<IRecipeSerializer<?>> recipeSerializer;
    private final Class<R> recipeClass;
    private final int inputSlotCount;
    private final ImmutableList<IMaterialCategory> categories;

    @SuppressWarnings("ConstructorWithTooManyParameters")
    public CompounderInfo(Collection<IMaterialCategory> categories,
                          int inputSlotCount,
                          Supplier<CompoundMaterialItem> outputItem,
                          Supplier<CompounderBlock> block, Supplier<TileEntityType<? extends CompounderTileEntity>> tileEntityType,
                          Supplier<ContainerType<? extends CompounderContainer>> containerType,
                          Supplier<IRecipeSerializer<?>> recipeSerializer,
                          Supplier<IRecipeType<R>> recipeType,
                          Class<R> recipeClass) {
        this.block = block;
        this.tileEntityType = tileEntityType;
        this.containerType = containerType;
        this.recipeType = recipeType;
        this.outputItem = outputItem;
        this.recipeSerializer = recipeSerializer;
        this.inputSlotCount = inputSlotCount;
        this.categories = ImmutableList.copyOf(categories);
        this.recipeClass = recipeClass;
    }

    public CompounderBlock getBlock() {
        return block.get();
    }

    public TileEntityType<? extends CompounderTileEntity> getTileEntityType() {
        return tileEntityType.get();
    }

    public ContainerType<? extends CompounderContainer> getContainerType() {
        return containerType.get();
    }

    public IRecipeType<R> getRecipeType() {
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

    public Class<R> getRecipeClass() {
        return recipeClass;
    }

    public IRecipeSerializer<?> getRecipeSerializer() {
        return recipeSerializer.get();
    }
}
