package net.silentchaos512.gear.world.feature;

import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IWorldGenerationBaseReader;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.silentchaos512.gear.init.ModBlocks;
import net.silentchaos512.gear.init.ModTags;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.Set;

public class NetherwoodTreeFeature extends AbstractTreeFeature<NoFeatureConfig> {
    private static final BlockState LOG = ModBlocks.NETHERWOOD_LOG.asBlockState();
    private static final BlockState LEAF = ModBlocks.NETHERWOOD_LEAVES.asBlockState();

    public NetherwoodTreeFeature(boolean notify) {
        super(dynamic -> new NoFeatureConfig(), notify);
    }

    @Override
    protected boolean place(Set<BlockPos> changedBlocks, IWorldGenerationReader worldIn, Random rand, BlockPos position, MutableBoundingBox p_208519_5_) {
        int height = rand.nextInt(5) + 5;

        if (position.getY() >= 1 && position.getY() + height + 1 <= worldIn.getMaxHeight()) {
            boolean flag = true;
            for (int y = position.getY(); y <= position.getY() + 1 + height; ++y) {
                int leavesReach = 2;
                if (y == position.getY()) {
                    leavesReach = 0;
                }

                BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();

                for (int x = position.getX() - leavesReach; x <= position.getX() + leavesReach && flag; ++x) {
                    for (int z = position.getZ() - leavesReach; z <= position.getZ() + leavesReach && flag; ++z) {
                        if (y >= 0 && y < worldIn.getMaxHeight()) {
                            if (!func_214587_a(worldIn, blockPos.setPos(x, y, z))) {
                                flag = false;
                            }
                        } else {
                            flag = false;
                        }
                    }
                }
            }

//            SilentGear.LOGGER.debug("Netherwood Tree: pos={}, flag={}, isSoil={}", position, flag, isSoil(worldIn, position.down(), getSapling()));

            if (!flag) {
                return false;
            } else if (isSoil(worldIn, position.down(), getSapling()) && position.getY() < worldIn.getMaxHeight() - height - 1) {
                this.setDirtAt(worldIn, position.down(), position);

                // Leaves
                for (int y = position.getY() + 2; y <= position.getY() + height; ++y) {
                    int dy = y - position.getY();
                    int leavesReach = dy > 3 ? 2 : 1;

                    for (int x = position.getX() - leavesReach; x <= position.getX() + leavesReach; ++x) {
                        int dx = x - position.getX();

                        for (int z = position.getZ() - leavesReach; z <= position.getZ() + leavesReach; ++z) {
                            int k1 = z - position.getZ();
                            if (Math.abs(dx) != leavesReach || Math.abs(k1) != leavesReach || rand.nextInt(2) != 0 && dy != 0) {
                                BlockPos blockpos = new BlockPos(x, y, z);
                                if (isAirOrLeaves(worldIn, blockpos)) {
                                    this.setLogState(changedBlocks, worldIn, blockpos, LEAF, p_208519_5_);
                                }
                            }
                        }
                    }
                }

                // Trunk
                for (int i2 = 0; i2 < height; ++i2) {
                    if (isAirOrLeaves(worldIn, position.up(i2))) {
                        this.setLogState(changedBlocks, worldIn, position.up(i2), LOG, p_208519_5_);
                    }
                }

                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    protected static boolean isSoil(IWorldGenerationBaseReader reader, BlockPos pos, net.minecraftforge.common.IPlantable sapling) {
        return reader.hasBlockState(pos, state -> state.isIn(ModTags.Blocks.NETHERWOOD_SOIL));
    }

    @Override
    protected void setDirtAt(IWorldGenerationReader reader, BlockPos pos, BlockPos origin) {
        if (!(reader instanceof IWorld)) return;
        ((IWorld)reader).getBlockState(pos).onPlantGrow((IWorld)reader, pos, origin);
    }

    private void tryPlaceLeaves(IWorld worldIn, BlockPos position, @Nullable Direction side) {
        BlockPos pos = side != null ? position.offset(side) : position;
        BlockState state = worldIn.getBlockState(pos);

        if (state.getBlock().canBeReplacedByLeaves(state, worldIn, pos)) {
            setBlockState(worldIn, pos, LEAF);
        }
    }
}
