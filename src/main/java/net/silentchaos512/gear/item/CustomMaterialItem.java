package net.silentchaos512.gear.item;

import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.api.material.Material;
import net.silentchaos512.gear.gear.material.CustomCompoundMaterial;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.setup.SgRegistries;
import net.silentchaos512.gear.setup.gear.GearTypes;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.gear.util.Const;

import javax.annotation.Nullable;

public class CustomMaterialItem extends SingleMaterialItem implements IColoredMaterialItem {
    public CustomMaterialItem(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public MaterialInstance getPrimarySubMaterial(ItemStack stack) {
        return getMaterial(stack);
    }

    @Override
    public int getColor(ItemStack stack, int layer) {
        var material = getMaterial(stack);
        if (layer == 0 && material != null) {
            return material.getColor(GearTypes.ALL.get(), PartTypes.MAIN.get()) | 0xFF000000;
        }
        return 0xFFFFFFFF;
    }

    @Override
    public Component getName(ItemStack stack) {
        var material = getMaterial(stack);
        if (material == null) return super.getName(stack);
        return Component.translatable(this.getDescriptionId(), material.getDisplayName(PartTypes.MAIN.get()));
    }

    public void addSubItems(NonNullList<ItemStack> items) {
        items.add(create(MaterialInstance.of(Const.Materials.EXAMPLE)));
        for (Material material : SgRegistries.MATERIAL) {
            if (material instanceof CustomCompoundMaterial) {
                MaterialInstance mat = MaterialInstance.of(material);
                ItemStack stack = create(mat);

                if (mat.getIngredient().test(stack)) {
                    items.add(stack);
                }
            }
        }
    }
}
