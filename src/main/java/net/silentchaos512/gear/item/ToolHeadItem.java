package net.silentchaos512.gear.item;

import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.parts.PartType;

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
    public int getColorWeight(int index, int totalCount) {
        int diff = super.getColorWeight(index, totalCount);
        return diff * diff;
    }
}
