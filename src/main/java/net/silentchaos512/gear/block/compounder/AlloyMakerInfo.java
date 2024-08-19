package net.silentchaos512.gear.block.compounder;

import com.google.common.collect.ImmutableList;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.silentchaos512.gear.api.material.IMaterialCategory;
import net.silentchaos512.gear.crafting.recipe.alloy.AlloyRecipe;
import net.silentchaos512.gear.item.CompoundMaterialItem;

import java.util.Collection;
import java.util.function.Supplier;

public class AlloyMakerInfo<R extends AlloyRecipe> {
    private final Supplier<AlloyMakerBlock<R>> block;
    private final Supplier<BlockEntityType<AlloyMakerBlockEntity<R>>> blockEntityType;
    private final Supplier<MenuType<? extends AlloyMakerContainer>> containerType;
    private final Supplier<RecipeType<R>> recipeType;
    private final Supplier<CompoundMaterialItem> outputItem;
    private final Supplier<RecipeSerializer<R>> recipeSerializer;
    private final Class<R> recipeClass;
    private final int inputSlotCount;
    private final ImmutableList<IMaterialCategory> categories;

    @SuppressWarnings("ConstructorWithTooManyParameters")
    public AlloyMakerInfo(Collection<IMaterialCategory> categories,
                          int inputSlotCount,
                          Supplier<CompoundMaterialItem> outputItem,
                          Supplier<AlloyMakerBlock<R>> block,
                          Supplier<BlockEntityType<AlloyMakerBlockEntity<R>>> blockEntityType,
                          Supplier<MenuType<? extends AlloyMakerContainer>> containerType,
                          Supplier<RecipeSerializer<R>> recipeSerializer,
                          Supplier<RecipeType<R>> recipeType,
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

    public AlloyMakerBlock<R> getBlock() {
        return block.get();
    }

    public BlockEntityType<AlloyMakerBlockEntity<R>> getBlockEntityType() {
        return blockEntityType.get();
    }

    public BlockEntityTicker<? super AlloyMakerBlockEntity<R>> getServerBlockEntityTicker() {
        return AlloyMakerBlockEntity::tick;
    }

    public MenuType<? extends AlloyMakerContainer> getContainerType() {
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
