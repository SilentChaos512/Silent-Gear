package net.silentchaos512.gear.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.item.blueprint.PartBlueprintItem;

public class JewelerKitItem extends PartBlueprintItem {
    public JewelerKitItem(PartType partType, boolean singleUse, Properties properties) {
        super(partType, singleUse, properties);
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.translatable(this.getDescriptionId(stack));
    }

    @Override
    public boolean hasStandardModel() {
        return false;
    }
}
