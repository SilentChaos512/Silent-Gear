package net.silentchaos512.gear.item;

import net.minecraft.item.BlockNamedItem;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.init.ModBlocks;

public class Flaxseeds extends BlockNamedItem {
    public Flaxseeds() {
        super(ModBlocks.FLAX_PLANT.asBlock(), new Properties().group(SilentGear.ITEM_GROUP));
    }
}
