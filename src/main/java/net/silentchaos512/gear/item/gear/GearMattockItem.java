package net.silentchaos512.gear.item.gear;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.util.GearHelper;

public class GearMattockItem extends GearHoeItem {
    public GearMattockItem() {
        super(GearType.MATTOCK);
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        return GearHelper.isCorrectToolForDrops(stack, state, BlockTags.MINEABLE_WITH_AXE)
                || GearHelper.isCorrectToolForDrops(stack, state, BlockTags.MINEABLE_WITH_SHOVEL)
                || GearHelper.isCorrectToolForDrops(stack, state, BlockTags.MINEABLE_WITH_HOE);
    }
}
