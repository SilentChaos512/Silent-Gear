package net.silentchaos512.gear.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.setup.gear.GearTypes;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.gear.util.TextUtil;

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

    @Override
    public Component getName(ItemStack stack) {
        var baseMaterial = getMaterial(stack);
        var materialName = baseMaterial != null ? baseMaterial.getSimpleName() : TextUtil.misc("unknown");
        return Component.translatable(this.getDescriptionId(), materialName);
    }
}
