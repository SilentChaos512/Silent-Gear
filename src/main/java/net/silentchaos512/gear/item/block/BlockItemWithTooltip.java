package net.silentchaos512.gear.item.block;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;
import net.silentchaos512.lib.util.NameUtils;

import java.util.function.Consumer;

public class BlockItemWithTooltip extends BlockItem {
    public BlockItemWithTooltip(Block block, Properties properties) {
        super(block, properties);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltipDisplay, tooltipAdder, flag);
        var id = NameUtils.fromItem(this);
        tooltipAdder.accept(Component.translatable("block." + id.getNamespace() + "." + id.getPath() + ".desc"));
    }
}
