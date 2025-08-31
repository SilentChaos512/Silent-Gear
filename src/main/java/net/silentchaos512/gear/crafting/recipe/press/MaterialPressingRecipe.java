package net.silentchaos512.gear.crafting.recipe.press;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.item.ProcessedMaterialItem;
import net.silentchaos512.gear.setup.SgDataComponents;
import net.silentchaos512.gear.setup.SgRecipes;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.gear.util.TextUtil;

public class MaterialPressingRecipe extends PressingRecipe {
    public MaterialPressingRecipe(String group, Ingredient ingredient, ItemStack result) {
        super(SgRecipes.PRESSING_MATERIAL.get(), group, ingredient, result);
    }

    @Override
    public ItemStack assemble(SingleRecipeInput input, HolderLookup.Provider registryAccess) {
        var material = MaterialInstance.from(input.getItem(0));

        ItemStack ret = this.result().copy();
        if (material != null) {
            ret.set(SgDataComponents.MATERIAL_SINGLE, material);
            var materialName = material.getDisplayName(PartTypes.MAIN.get());
            ret.set(DataComponents.ITEM_NAME, Component.translatable(ret.getItem().getDescriptionId(), materialName));
        } else {
            ret.set(DataComponents.ITEM_NAME, Component.translatable(ret.getItem().getDescriptionId(), TextUtil.misc("unknown")));
        }
        return ret;
    }
}
