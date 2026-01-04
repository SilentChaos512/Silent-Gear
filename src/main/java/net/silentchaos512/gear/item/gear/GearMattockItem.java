package net.silentchaos512.gear.item.gear;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.core.component.GearPropertiesData;
import net.silentchaos512.gear.setup.SgTags;

import java.util.function.Supplier;

public class GearMattockItem extends GearHoeItem {
    public GearMattockItem(Supplier<GearType> gearType, Item.Properties properties) {
        super(gearType, properties);
    }

    @Override
    public TagKey<Block> getToolBlockSet(GearPropertiesData properties) {
        return SgTags.Blocks.MINEABLE_WITH_MATTOCK;
    }
}
