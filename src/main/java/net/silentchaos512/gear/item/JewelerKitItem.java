package net.silentchaos512.gear.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.item.blueprint.BlueprintType;
import net.silentchaos512.gear.item.blueprint.PartBlueprintItem;

import java.util.function.Supplier;

public class JewelerKitItem extends PartBlueprintItem {
    public JewelerKitItem(Supplier<PartType> partType, BlueprintType blueprintType, Properties properties) {
        super(partType, blueprintType, properties);
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
