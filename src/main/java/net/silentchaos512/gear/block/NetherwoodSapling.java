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

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.silentchaos512.gear.init.ModBlocks;
import net.silentchaos512.gear.world.feature.NetherwoodTree;
import net.silentchaos512.lib.util.MathUtils;

import java.util.Random;

import static net.minecraft.block.BlockSapling.STAGE;

public class NetherwoodSapling extends BlockBush implements IGrowable {
    private static final AxisAlignedBB SAPLING_AABB = new AxisAlignedBB(0.1, 0, 0.1, 0.9, 0.8, 0.9);

    public NetherwoodSapling() {
        setDefaultState(blockState.getBaseState().withProperty(STAGE, 0));
    }

    @SuppressWarnings("deprecation")
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return SAPLING_AABB;
    }

    @Override
    public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {
        Block blockBelow = worldIn.getBlockState(pos.down()).getBlock();
        return state.getBlock() == this && blockBelow == Blocks.NETHERRACK || super.canBlockStay(worldIn, pos, state);
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        IBlockState soil = worldIn.getBlockState(pos.down());
        Block target = worldIn.getBlockState(pos).getBlock();
        return target.isReplaceable(worldIn, pos)
                && (soil.getBlock() == Blocks.NETHERRACK || soil.getBlock().canSustainPlant(soil, worldIn, pos.down(), EnumFacing.UP, this))
                || super.canPlaceBlockAt(worldIn, pos);
    }

    private static void generateTree(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        new NetherwoodTree(ModBlocks.netherwoodLog.getDefaultState(), ModBlocks.netherwoodLeaves.getDefaultState())
                .generate(worldIn, rand, pos);
    }

    @Override
    public int damageDropped(IBlockState state) {
        return 0;
    }

    @SuppressWarnings("deprecation")
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(STAGE, meta);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(STAGE);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, STAGE);
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        items.add(new ItemStack(this));
    }

    @Override
    public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
        return true;
    }

    @Override
    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
        return MathUtils.tryPercentage(0.45);
    }

    @Override
    public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
        grow(worldIn, pos, state, rand);
    }

    private static void grow(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (state.getValue(STAGE) == 0)
            worldIn.setBlockState(pos, state.cycleProperty(STAGE), 4);
        else
            generateTree(worldIn, pos, state, rand);
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (!worldIn.isRemote) {
            super.updateTick(worldIn, pos, state, rand);

            int darkness = 15 - worldIn.getLightFromNeighbors(pos.up());
            // Can grow at any light level, but more light improves chances
            if (worldIn.isAreaLoaded(pos, 1) && rand.nextInt(5 + darkness / 2) == 0) {
                grow(worldIn, pos, state, rand);
            }
        }
    }
}
