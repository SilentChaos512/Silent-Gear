package net.silentchaos512.gear.block;

import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ToolAction;
import net.neoforged.neoforge.common.ToolActions;

import java.util.function.Function;

public class WoodBlock extends RotatedPillarBlock {
    private final Function<Block, Block> strippedBlock;

    public WoodBlock(Function<Block, Block> strippedBlock, Properties properties) {
        super(properties);
        this.strippedBlock = strippedBlock;
    }

    @Override
    public @org.jetbrains.annotations.Nullable BlockState getToolModifiedState(BlockState state, UseOnContext context, ToolAction toolAction, boolean simulate) {
        if (toolAction == ToolActions.AXE_STRIP) {
            Block block = this.strippedBlock.apply(state.getBlock());
            if (block != null) {
                return block.defaultBlockState().setValue(RotatedPillarBlock.AXIS, state.getValue(RotatedPillarBlock.AXIS));
            }
        }
        return super.getToolModifiedState(state, context, toolAction, simulate);
    }
}
