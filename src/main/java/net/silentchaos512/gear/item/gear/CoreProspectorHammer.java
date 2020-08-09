package net.silentchaos512.gear.item.gear;

import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.silentchaos512.gear.api.item.GearType;

public class CoreProspectorHammer extends CorePickaxe {
    @Override
    public GearType getGearType() {
        return GearType.PROSPECTOR_HAMMER;
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        // TODO
        return super.onItemUse(context);
    }
}
