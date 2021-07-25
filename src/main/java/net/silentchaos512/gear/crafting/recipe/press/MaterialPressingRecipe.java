package net.silentchaos512.gear.crafting.recipe.press;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.resources.ResourceLocation;
import net.silentchaos512.gear.api.material.IMaterialInstance;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.item.CraftedMaterialItem;

public class MaterialPressingRecipe extends PressingRecipe {
    public MaterialPressingRecipe(ResourceLocation id, Ingredient ingredient, ItemStack result) {
        super(id, ingredient, result);

        if (!(result.getItem() instanceof CraftedMaterialItem)) {
            throw new IllegalArgumentException("result must be a CraftedMaterialItem");
        }
    }

    @Override
    public ItemStack assemble(Container inv) {
        IMaterialInstance material = MaterialInstance.from(inv.getItem(0));

        if (material != null) {
            CraftedMaterialItem item = (CraftedMaterialItem) this.result.getItem();
            return item.create(material, this.result.getCount());
        }

        return this.result.copy();
    }
}
