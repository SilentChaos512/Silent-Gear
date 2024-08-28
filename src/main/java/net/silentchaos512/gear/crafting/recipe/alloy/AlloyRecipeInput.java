package net.silentchaos512.gear.crafting.recipe.alloy;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.silentchaos512.gear.block.compounder.AlloyMakerBlockEntity;

public class AlloyRecipeInput implements RecipeInput {
    private final AlloyMakerBlockEntity<?> blockEntity;

    private AlloyRecipeInput(AlloyMakerBlockEntity<?> blockEntity) {
        this.blockEntity = blockEntity;
    }

    public static AlloyRecipeInput of(AlloyMakerBlockEntity<?> blockEntity) {
        return new AlloyRecipeInput(blockEntity);
    }

    @Override
    public ItemStack getItem(int pIndex) {
        return this.blockEntity.getItem(pIndex);
    }

    @Override
    public int size() {
        return this.blockEntity.getInputSlotCount();
    }
}
