package net.silentchaos512.gear.crafting.recipe.press;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.item.ProcessedMaterialItem;
import net.silentchaos512.gear.setup.SgRecipes;

public class MaterialPressingRecipe extends PressingRecipe {
    public MaterialPressingRecipe(String group, Ingredient ingredient, ItemStack result) {
        super(SgRecipes.PRESSING_MATERIAL.get(), group, ingredient, result);

        if (!(result.getItem() instanceof ProcessedMaterialItem)) {
            throw new IllegalArgumentException("result must be a CraftedMaterialItem");
        }
    }

    @Override
    public ItemStack assemble(Container inv, HolderLookup.Provider registryAccess) {
        IMaterialInstance material = MaterialInstance.from(inv.getItem(0));

        if (material != null) {
            ProcessedMaterialItem item = (ProcessedMaterialItem) this.result.getItem();
            return item.create(material, this.result.getCount());
        }

        return this.result.copy();
    }
}
