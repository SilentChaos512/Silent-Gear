/*
 * Silent Gear -- PhantomLight
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

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public class PhantomLight extends Block {
    private static final VoxelShape VOXEL_SHAPE = Block.makeCuboidShape(5, 5, 5, 11, 11, 11);

    public PhantomLight() {
        super(Properties.create(Material.CIRCUITS)
                .doesNotBlockMovement()
                .hardnessAndResistance(0.5f, 6000000.0f)
                .lightValue(15)
        );
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
        return VOXEL_SHAPE;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isReplaceable(IBlockState state, BlockItemUseContext useContext) {
        return true;
    }

    @Override
    public boolean canBeReplacedByLeaves(IBlockState state, IWorldReaderBase world, BlockPos pos) {
        return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void dropBlockAsItemWithChance(IBlockState state, World worldIn, BlockPos pos, float chancePerItem, int fortune) {
        // No drop
    }

    @Override
    public IItemProvider getItemDropped(IBlockState state, World worldIn, BlockPos pos, int fortune) {
        return Items.AIR;
    }

    @SuppressWarnings("deprecation")
    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }
}
