package net.silentchaos512.gear.block;

import net.minecraft.world.level.block.OreBlock;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.BlockGetter;
import net.silentchaos512.gear.util.TextUtil;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class ModOreBlock extends OreBlock {
    private final int harvestLevel;

    public ModOreBlock(Properties properties) {
        super(properties);
        this.harvestLevel = properties.getHarvestLevel();
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        // Harvest level tips
        Component harvestLevelName = TextUtil.misc("harvestLevel." + this.harvestLevel);
        tooltip.add(TextUtil.misc("harvestLevel", this.harvestLevel, harvestLevelName));
    }
}
