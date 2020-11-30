package net.silentchaos512.gear.item;

import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.item.blueprint.PartBlueprintItem;

public class JewelerKitItem extends PartBlueprintItem {
    public JewelerKitItem(PartType partType, boolean singleUse, Properties properties) {
        super(partType, singleUse, properties);
    }

    @Override
    public ITextComponent getDisplayName(ItemStack stack) {
        return new TranslationTextComponent(this.getTranslationKey(stack));
    }

    @Override
    public boolean hasStandardModel() {
        return false;
    }
}
