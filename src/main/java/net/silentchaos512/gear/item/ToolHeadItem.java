package net.silentchaos512.gear.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.part.PartType;

import javax.annotation.Nullable;
import java.util.List;

public class ToolHeadItem extends CompoundPartItem {
    private final GearType gearType;

    public ToolHeadItem(GearType gearType, Properties properties) {
        super(PartType.MAIN, properties);
        this.gearType = gearType;
    }

    @Override
    public GearType getGearType() {
        return gearType;
    }

    @Override
    public int getCraftedCount(ItemStack stack) {
        return 1;
    }

    @Override
    public int getColorWeight(int index, int totalCount) {
        int diff = super.getColorWeight(index, totalCount);
        return diff * diff;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (this.gearType == GearType.ARMOR) {
            // Old armor body item
            tooltip.add(new StringTextComponent("DEPRECATED").mergeStyle(TextFormatting.RED));
        }

        super.addInformation(stack, worldIn, tooltip, flagIn);
    }
}
