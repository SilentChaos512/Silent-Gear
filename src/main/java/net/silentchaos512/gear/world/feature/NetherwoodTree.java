/*
 * Silent Gear -- NetherwoodTree
 * Copyright (C) 2018 SilentChaos512
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 3
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.silentchaos512.gear.world.feature;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenShrub;
import net.silentchaos512.gear.init.ModBlocks;
import net.silentchaos512.lib.util.MathUtils;

import javax.annotation.Nullable;
import java.util.Random;

public class NetherwoodTree extends WorldGenShrub {
    private final IBlockState wood;
    private final IBlockState leaves;

    public NetherwoodTree(IBlockState wood, IBlockState leaves) {
        super(wood, leaves);
        this.wood = wood;
        this.leaves = leaves;
    }

    @Override
    public boolean generate(World worldIn, Random rand, BlockPos position) {
        final int startY = position.getY();
        for (IBlockState iblockstate = worldIn.getBlockState(position); (iblockstate.getBlock().isAir(iblockstate, worldIn, position) || iblockstate.getBlock().isLeaves(iblockstate, worldIn, position)) && startY > 0; iblockstate = worldIn.getBlockState(position)) {
            position = position.down();
        }

        IBlockState state = worldIn.getBlockState(position);

        if (ModBlocks.netherwoodSapling.canBlockStay(worldIn, position, state)) {
            setBlockAndNotifyAdequately(worldIn, position, wood);

            final int height = MathUtils.nextIntInclusive(3, 5);
            final int endY = startY + height;

            // Generate logs and leaves
            for (int y = startY; y < endY + 2; ++y) {
                BlockPos pos = position.up(y - startY);

                // Logs/leave in center
                if (y < endY) {
                    setBlockAndNotifyAdequately(worldIn, pos, wood);
                } else {
                    tryPlaceLeaves(worldIn, pos, null);
                }

                // Leaves surrounding the center
                if (y != startY && y < endY + 1) {
                    for (EnumFacing facing : EnumFacing.HORIZONTALS) {
                        tryPlaceLeaves(worldIn, pos, facing);
                    }
                }
            }
        }

        return true;
    }

    private void tryPlaceLeaves(World worldIn, BlockPos position, @Nullable EnumFacing side) {
        BlockPos pos = side != null ? position.offset(side) : position;
        IBlockState state = worldIn.getBlockState(pos);

        if (state.getBlock().canBeReplacedByLeaves(state, worldIn, pos)) {
            setBlockAndNotifyAdequately(worldIn, pos, leaves);
        }
    }
}
