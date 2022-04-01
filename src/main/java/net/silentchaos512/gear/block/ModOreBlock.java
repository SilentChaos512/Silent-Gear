package net.silentchaos512.gear.block;

import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.OreBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;
import net.silentchaos512.gear.util.TextUtil;
import net.silentchaos512.lib.util.TagUtils;

import javax.annotation.Nullable;
import java.util.List;

public class ModOreBlock extends OreBlock {
    public ModOreBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        // Harvest level tips
        int harvestLevel = guessHarvestLevel(defaultBlockState());
        Component harvestLevelName = TextUtil.misc("harvestLevel." + harvestLevel);
        tooltip.add(TextUtil.misc("harvestLevel", harvestLevel, harvestLevelName));
    }

    private static int guessHarvestLevel(BlockState state) {
        if (TagUtils.contains(Tags.Blocks.NEEDS_NETHERITE_TOOL, state)) return 4;
        if (TagUtils.contains(BlockTags.NEEDS_DIAMOND_TOOL, state)) return 3;
        if (TagUtils.contains(BlockTags.NEEDS_IRON_TOOL, state)) return 2;
        if (TagUtils.contains(BlockTags.NEEDS_STONE_TOOL, state)) return 1;
        return 0;
    }
}
