package net.silentchaos512.gear.block.compounder;

import com.google.common.collect.ImmutableList;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.silentchaos512.gear.api.material.IMaterialCategory;
import net.silentchaos512.gear.crafting.recipe.compounder.CompoundingRecipe;
import net.silentchaos512.gear.item.CompoundMaterialItem;

import java.util.Collection;
import java.util.function.Supplier;

public class CompounderInfo<R extends CompoundingRecipe> {
    private final Supplier<CompounderBlock> block;
    private final Supplier<BlockEntityType<? extends CompounderTileEntity>> tileEntityType;
    private final Supplier<MenuType<? extends CompounderContainer>> containerType;
    private final Supplier<RecipeType<R>> recipeType;
    private final Supplier<CompoundMaterialItem> outputItem;
    private final Supplier<RecipeSerializer<?>> recipeSerializer;
    private final Class<R> recipeClass;
    private final int inputSlotCount;
    private final ImmutableList<IMaterialCategory> categories;

    @SuppressWarnings("ConstructorWithTooManyParameters")
    public CompounderInfo(Collection<IMaterialCategory> categories,
                          int inputSlotCount,
                          Supplier<CompoundMaterialItem> outputItem,
                          Supplier<CompounderBlock> block, Supplier<BlockEntityType<? extends CompounderTileEntity>> tileEntityType,
                          Supplier<MenuType<? extends CompounderContainer>> containerType,
                          Supplier<RecipeSerializer<?>> recipeSerializer,
                          Supplier<RecipeType<R>> recipeType,
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

    public BlockEntityType<? extends CompounderTileEntity> getTileEntityType() {
        return tileEntityType.get();
    }

    public MenuType<? extends CompounderContainer> getContainerType() {
        return containerType.get();
    }

    public RecipeType<R> getRecipeType() {
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

    public RecipeSerializer<?> getRecipeSerializer() {
        return recipeSerializer.get();
    }
}
