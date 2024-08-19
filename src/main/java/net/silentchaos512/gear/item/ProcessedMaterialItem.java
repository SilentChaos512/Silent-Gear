package net.silentchaos512.gear.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.setup.gear.GearTypes;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.lib.util.Color;
import org.jetbrains.annotations.Nullable;

public class ProcessedMaterialItem extends SingleMaterialItem implements IColoredMaterialItem {
    public ProcessedMaterialItem(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public MaterialInstance getPrimarySubMaterial(ItemStack stack) {
        return getMaterial(stack);
    }

    @Override
    public int getColor(ItemStack stack, int layer) {
        var baseMaterial = getMaterial(stack);
        return baseMaterial != null ? baseMaterial.getColor(GearTypes.ALL.get(), PartTypes.MAIN.get()) : Color.VALUE_WHITE;
    }

    @Override
    public Component getName(ItemStack stack) {
        var baseMaterial = getMaterial(stack);
        if (baseMaterial == null) return super.getName(stack);
        return Component.translatable(this.getDescriptionId(), baseMaterial.getDisplayName(PartTypes.MAIN.get()));
    }
}
