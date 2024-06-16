package net.silentchaos512.gear.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.silentchaos512.gear.block.trees.NetherwoodTree;
import net.silentchaos512.gear.setup.SgTags;

public class NetherwoodSapling extends SaplingBlock {
    public NetherwoodSapling(Properties properties) {
        super(NetherwoodTree.GROWER, properties);
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return state.is(SgTags.Blocks.NETHERWOOD_SOIL);
    }
}
