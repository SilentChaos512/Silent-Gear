package net.silentchaos512.gear.world.feature;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.silentchaos512.gear.block.NetherwoodSapling;
import net.silentchaos512.gear.init.ModBlocks;
import net.silentchaos512.lib.util.MathUtils;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.Set;

public class NetherwoodTreeFeature extends AbstractTreeFeature<NoFeatureConfig> {
    private static final IBlockState TRUNK = ModBlocks.NETHERWOOD_LOG.asBlockState();
    private static final IBlockState LEAF = ModBlocks.NETHERWOOD_LEAVES.asBlockState();

    public NetherwoodTreeFeature(boolean notify) {
        super(notify);
    }

    @Override
    protected boolean place(Set<BlockPos> changedBlocks, IWorld worldIn, Random rand, BlockPos position) {
        final int startY = Math.min(position.getY(), 96);
        position = new BlockPos(position.getX(), startY, position.getZ());
        for (IBlockState state = worldIn.getBlockState(position);
             state.getBlock().canBeReplacedByLeaves(state, worldIn, position) && startY > 0;
             state = worldIn.getBlockState(position)) {
            position = position.down();
        }

        IBlockState state = worldIn.getBlockState(position);

        if (((NetherwoodSapling) ModBlocks.NETHERWOOD_SAPLING.asBlock()).canBlockStay(worldIn, position, state)) {
            setBlockState(worldIn, position, TRUNK);

            final int height = MathUtils.nextIntInclusive(3, 5);
            final int endY = startY + height;

            // Generate logs and leaves
            for (int y = startY; y < endY + 2; ++y) {
                BlockPos pos = position.up(y - startY);

                // Logs/leave in center
                if (y < endY) {
                    setBlockState(worldIn, pos, TRUNK);
                } else {
                    tryPlaceLeaves(worldIn, pos, null);
                }

                // Leaves surrounding the center
                if (y != startY && y < endY + 1) {
                    for (EnumFacing facing : EnumFacing.Plane.HORIZONTAL) {
                        tryPlaceLeaves(worldIn, pos, facing);
                    }
                }
            }
        }

        return true;
    }

    private void tryPlaceLeaves(IWorld worldIn, BlockPos position, @Nullable EnumFacing side) {
        BlockPos pos = side != null ? position.offset(side) : position;
        IBlockState state = worldIn.getBlockState(pos);

        if (state.getBlock().canBeReplacedByLeaves(state, worldIn, pos)) {
            setBlockState(worldIn, pos, LEAF);
        }
    }
}
