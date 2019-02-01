/*
 * Silent Gear -- NetherwoodSapling
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

package net.silentchaos512.gear.block;

import net.minecraft.block.BlockSapling;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.silentchaos512.gear.block.trees.NetherwoodTree;

public class NetherwoodSapling extends BlockSapling {
    public NetherwoodSapling() {
        super(new NetherwoodTree(), Builder.create(Material.PLANTS)
                .doesNotBlockMovement()
                .needsRandomTick()
                .hardnessAndResistance(0)
                .sound(SoundType.PLANT)
        );
    }

    //    @Override
    public boolean canBlockStay(IWorld worldIn, BlockPos pos, IBlockState state) {
        IBlockState soil = worldIn.getBlockState(pos.down());
        return isValidGround(soil, worldIn, pos);
    }

    @Override
    protected boolean isValidGround(IBlockState state, IBlockReader worldIn, BlockPos pos) {
        return state.getBlock() == Blocks.NETHERRACK || super.isValidGround(state, worldIn, pos);
    }
}
