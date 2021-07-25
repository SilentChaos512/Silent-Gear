package net.silentchaos512.gear.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;
import java.util.function.Function;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class WoodBlock extends RotatedPillarBlock {
    private final Function<Block, Block> strippedBlock;

    public WoodBlock(Function<Block, Block> strippedBlock, Properties properties) {
        super(properties);
        this.strippedBlock = strippedBlock;
    }

    @Nullable
    @Override
    public BlockState getToolModifiedState(BlockState state, Level world, BlockPos pos, Player player, ItemStack stack, ToolType toolType) {
        Block block = this.strippedBlock.apply(state.getBlock());
        if (block != null) {
            return block.defaultBlockState().setValue(RotatedPillarBlock.AXIS, state.getValue(RotatedPillarBlock.AXIS));
        }
        return super.getToolModifiedState(state, world, pos, player, stack, toolType);
    }
}
