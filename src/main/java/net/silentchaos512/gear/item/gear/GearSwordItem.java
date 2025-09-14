package net.silentchaos512.gear.item.gear;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.silentchaos512.gear.api.item.GearTool;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.item.GearWeapon;
import net.silentchaos512.gear.core.component.GearPropertiesData;

import java.util.List;
import java.util.function.Supplier;

public class GearSwordItem extends BasicGearItem implements GearWeapon {
    private final Supplier<GearType> gearType;

    public GearSwordItem(Supplier<GearType> gearType, Item.Properties properties) {
        super(properties);
        this.gearType = gearType;
    }

    @Override
    public GearType getGearType() {
        return this.gearType.get();
    }

    @Override
    public Tool createToolProperties(ItemStack gear, GearPropertiesData properties, HolderGetter<Block> blocks) {
        return new Tool(
                List.of(
                        Tool.Rule.minesAndDrops(HolderSet.direct(Holder.direct(Blocks.COBWEB)), 15.0F),
                        Tool.Rule.overrideSpeed(blocks.getOrThrow(BlockTags.SWORD_INSTANTLY_MINES), Float.MAX_VALUE),
                        Tool.Rule.overrideSpeed(blocks.getOrThrow(BlockTags.SWORD_EFFICIENT), 1.5f)
                ),
                1.0F,
                2,
                false
        );
    }
}
