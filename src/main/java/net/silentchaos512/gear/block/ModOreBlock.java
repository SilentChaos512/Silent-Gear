package net.silentchaos512.gear.block;

import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.Tags;
import net.silentchaos512.gear.util.TextUtil;

import javax.annotation.Nullable;
import java.util.List;

public class ModOreBlock extends DropExperienceBlock {
    public ModOreBlock(IntProvider xpDrop, Properties properties) {
        super(xpDrop, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        // Harvest level tips
        int harvestLevel = guessHarvestLevel(defaultBlockState());
        Component harvestLevelName = TextUtil.misc("harvestLevel." + harvestLevel);
        tooltip.add(TextUtil.misc("harvestLevel", harvestLevelName));
    }

    private static int guessHarvestLevel(BlockState state) {
        if (state.is(Tags.Blocks.NEEDS_NETHERITE_TOOL)) return 4;
        if (state.is(BlockTags.NEEDS_DIAMOND_TOOL)) return 3;
        if (state.is(BlockTags.NEEDS_IRON_TOOL)) return 2;
        if (state.is(BlockTags.NEEDS_STONE_TOOL)) return 1;
        return 0;
    }
}
