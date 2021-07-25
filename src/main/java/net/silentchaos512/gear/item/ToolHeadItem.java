package net.silentchaos512.gear.item;

import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.part.PartType;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.world.item.Item.Properties;

public class ToolHeadItem extends CompoundPartItem { // TODO: Rename to MainPartItem?
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
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        if (this.gearType == GearType.ARMOR) {
            // Old armor body item
            tooltip.add(new TextComponent("DEPRECATED").withStyle(ChatFormatting.RED));
        }

        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }
}
