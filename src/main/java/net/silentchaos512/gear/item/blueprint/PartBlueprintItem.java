package net.silentchaos512.gear.item.blueprint;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.parts.PartType;

import javax.annotation.Nullable;
import java.util.List;

public class PartBlueprintItem extends AbstractBlueprintItem {
    private final PartType partType;

    public PartBlueprintItem(boolean singleUse, PartType partType) {
        super(new Properties().group(SilentGear.ITEM_GROUP), singleUse);
        this.partType = partType;
    }

    @Override
    protected ITextComponent getCraftedName(ItemStack stack) {
        return this.partType.getDisplayName(0);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent("item.silentgear.blueprint.partTypeWarning1").applyTextStyle(TextFormatting.ITALIC));
        tooltip.add(new TranslationTextComponent("item.silentgear.blueprint.partTypeWarning2").applyTextStyle(TextFormatting.ITALIC));
    }
}
