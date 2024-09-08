package net.silentchaos512.gear.item.gear;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.setup.SgTags;

import java.util.function.Supplier;

public class GearMattockItem extends GearHoeItem {
    public GearMattockItem(Supplier<GearType> gearType) {
        super(gearType);
    }

    @Override
    public TagKey<Block> getToolBlockSet() {
        return SgTags.Blocks.MINEABLE_WITH_MATTOCK;
    }
}
