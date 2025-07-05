package net.silentchaos512.gear.item.block;

import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.Tags;
import net.silentchaos512.gear.util.TextUtil;

import java.util.function.Consumer;

public class OreBlockItem extends BlockItem {
    public OreBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        // Harvest level tips
        int harvestLevel = guessHarvestLevel(this.getBlock().defaultBlockState());
        Component harvestLevelName = TextUtil.misc("harvestLevel." + harvestLevel);
        tooltipAdder.accept(TextUtil.misc("harvestLevel", harvestLevelName));
    }

    private static int guessHarvestLevel(BlockState state) {
        if (state.is(Tags.Blocks.NEEDS_NETHERITE_TOOL)) return 4;
        if (state.is(BlockTags.NEEDS_DIAMOND_TOOL)) return 3;
        if (state.is(BlockTags.NEEDS_IRON_TOOL)) return 2;
        if (state.is(BlockTags.NEEDS_STONE_TOOL)) return 1;
        return 0;
    }
}
