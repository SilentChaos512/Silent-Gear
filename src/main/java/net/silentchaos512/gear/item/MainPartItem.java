package net.silentchaos512.gear.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.util.StatGearKey;
import net.silentchaos512.gear.gear.part.PartData;

import javax.annotation.Nullable;
import java.util.List;

public class MainPartItem extends CompoundPartItem {
    private final GearType gearType;

    public MainPartItem(GearType gearType, Properties properties) {
        super(PartType.MAIN, properties.defaultDurability(100));
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
    public int getMaxDamage(ItemStack stack) {
        // TODO: Cache durability stat?
        PartData part = PartData.from(stack);
        if (part != null) {
            StatGearKey statKey = StatGearKey.of(gearType.getDurabilityStat(), gearType);
            return Math.round(part.getStat(PartType.MAIN, statKey));
        }
        return super.getMaxDamage(stack);
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
