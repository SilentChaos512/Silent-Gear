package net.silentchaos512.gear.block.compounder;

import com.google.common.collect.ImmutableList;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.silentchaos512.gear.api.material.IMaterialCategory;
import net.silentchaos512.gear.crafting.recipe.compounder.CompoundingRecipe;
import net.silentchaos512.gear.item.CompoundMaterialItem;

import java.util.Collection;
import java.util.function.Supplier;

public class CompounderInfo<R extends CompoundingRecipe> {
    private final Supplier<CompounderBlock> block;
    private final Supplier<BlockEntityType<CompounderTileEntity<R>>> blockEntityType;
    private final Supplier<MenuType<? extends CompounderContainer>> containerType;
    private final DeferredHolder<RecipeType<?>, RecipeType<R>> recipeType;
    private final Supplier<CompoundMaterialItem> outputItem;
    private final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> recipeSerializer;
    private final Class<R> recipeClass;
    private final int inputSlotCount;
    private final ImmutableList<IMaterialCategory> categories;

    @SuppressWarnings("ConstructorWithTooManyParameters")
    public CompounderInfo(Collection<IMaterialCategory> categories,
                          int inputSlotCount,
                          Supplier<CompoundMaterialItem> outputItem,
                          Supplier<CompounderBlock> block,
                          Supplier<BlockEntityType<CompounderTileEntity<R>>> blockEntityType,
                          Supplier<MenuType<? extends CompounderContainer>> containerType,
                          DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> recipeSerializer,
                          DeferredHolder<RecipeType<?>, RecipeType<R>> recipeType,
                          Class<R> recipeClass) {
        this.block = block;
        this.blockEntityType = blockEntityType;
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

    public BlockEntityType<CompounderTileEntity<R>> getBlockEntityType() {
        return blockEntityType.get();
    }

    public BlockEntityTicker<? super CompounderTileEntity<R>> getServerBlockEntityTicker() {
        return CompounderTileEntity::tick;
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
