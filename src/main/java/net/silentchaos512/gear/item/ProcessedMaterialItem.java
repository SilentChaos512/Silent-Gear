package net.silentchaos512.gear.item;

import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.setup.gear.GearTypes;
import net.silentchaos512.gear.setup.gear.PartTypes;

@Deprecated
public class ProcessedMaterialItem extends SingleMaterialItem implements IColoredMaterialItem {
    public ProcessedMaterialItem(Properties properties) {
        super(properties);
    }

    @Override
    public int getColor(ItemStack stack, int layer) {
        var baseMaterial = getMaterial(stack);
        if (baseMaterial != null && layer == 0) {
            return baseMaterial.getColor(GearTypes.ALL.get(), PartTypes.MAIN.get());
        }
        return 0xFFFFFFFF;
    }
}
