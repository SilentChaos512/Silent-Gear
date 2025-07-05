package net.silentchaos512.gear.item.block;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;
import net.silentchaos512.gear.block.alloymaker.AlloyMakerBlock;

import java.util.function.Consumer;

public class AlloyMakerBlockItem extends BlockItem {
    public AlloyMakerBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        if (!(this.getBlock() instanceof AlloyMakerBlock<?> alloyMakerBlock)) {
            return;
        }
        alloyMakerBlock.getTooltipLines().forEach(tooltipAdder);
    }
}
