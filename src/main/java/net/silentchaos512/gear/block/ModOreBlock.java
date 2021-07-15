package net.silentchaos512.gear.block;

import net.minecraft.block.OreBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;
import net.silentchaos512.gear.util.TextUtil;

import javax.annotation.Nullable;
import java.util.List;

public class ModOreBlock extends OreBlock {
    private final int harvestLevel;

    public ModOreBlock(Properties properties) {
        super(properties);
        this.harvestLevel = properties.getHarvestLevel();
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        // Harvest level tips
        ITextComponent harvestLevelName = TextUtil.misc("harvestLevel." + this.harvestLevel);
        tooltip.add(TextUtil.misc("harvestLevel", this.harvestLevel, harvestLevelName));
    }
}
