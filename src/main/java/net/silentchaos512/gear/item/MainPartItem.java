package net.silentchaos512.gear.item;

import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.util.PropertyKey;
import net.silentchaos512.gear.gear.part.PartInstance;
import net.silentchaos512.gear.setup.gear.PartTypes;

public class MainPartItem extends CompoundPartItem {
    private final GearType gearType;

    public MainPartItem(GearType gearType, Properties properties) {
        super(PartTypes.MAIN.get(), properties.durability(100));
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

    @Override
    public int getMaxDamage(ItemStack stack) {
        PartInstance part = PartInstance.from(stack);
        if (part != null) {
            return Math.round(part.getProperty(PartTypes.MAIN, PropertyKey.of(gearType.durabilityStat().get(), gearType)));
        }
        return super.getMaxDamage(stack);
    }
}
