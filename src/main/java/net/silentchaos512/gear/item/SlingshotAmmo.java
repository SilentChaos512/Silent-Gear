package net.silentchaos512.gear.item;

import net.minecraft.item.Item;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.ISlingshotAmmo;

public class SlingshotAmmo extends Item implements ISlingshotAmmo {
    public SlingshotAmmo() {
        super(new Properties().group(SilentGear.ITEM_GROUP));
    }
}
