package net.silentchaos512.gear.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.setup.SgDataComponents;

import javax.annotation.Nullable;

public class SingleMaterialItem extends Item {
    public SingleMaterialItem(Properties pProperties) {
        super(pProperties);
    }

    public ItemStack create(MaterialInstance material) {
        return create(material, 1);
    }

    public ItemStack create(MaterialInstance material, int count) {
        ItemStack stack = new ItemStack(this, count);
        stack.set(SgDataComponents.MATERIAL_SINGLE, material);
        return stack;
    }

    @Nullable
    public static MaterialInstance getMaterial(ItemStack stack) {
        return stack.get(SgDataComponents.MATERIAL_SINGLE);
    }
}
