/*
 * Silent Gear -- NetherwoodLog
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

import net.minecraft.block.BlockLog;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class NetherwoodLog extends BlockLog {
    public NetherwoodLog() {
        setDefaultState(blockState.getBaseState().withProperty(LOG_AXIS, EnumAxis.Y));
    }

    @Override
    public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return 0;
    }

    @Override
    public boolean isFlammable(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return false;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        switch (meta) {
            case 0:
                return getDefaultState().withProperty(LOG_AXIS, EnumAxis.Y);
            case 1:
                return getDefaultState().withProperty(LOG_AXIS, EnumAxis.X);
            case 2:
                return getDefaultState().withProperty(LOG_AXIS, EnumAxis.Z);
            case 3:
                return getDefaultState().withProperty(LOG_AXIS, EnumAxis.NONE);
        }
        return getDefaultState();
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        switch (state.getValue(LOG_AXIS)) {
            case X:
                return 1;
            case Z:
                return 2;
            case NONE:
                return 3;
            default:
                return 0;
        }
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, LOG_AXIS);
    }

    @Override
    protected ItemStack getSilkTouchDrop(IBlockState state) {
        return new ItemStack(this);
    }

    @Override
    public int damageDropped(IBlockState state) {
        return 0;
    }
}
