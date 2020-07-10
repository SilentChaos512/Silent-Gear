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

import net.minecraft.block.BlockState;
import net.minecraft.block.LogBlock;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.init.ModBlocks;
import net.silentchaos512.gear.util.GearHelper;

public class NetherwoodLog extends LogBlock {
    private final boolean stripped;

    public NetherwoodLog(boolean stripped) {
        super(MaterialColor.ADOBE, Properties.create(Material.WOOD)
                .hardnessAndResistance(2)
                .sound(SoundType.WOOD)
        );
        this.stripped = stripped;
    }

    @Override
    public int getFlammability(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
        return 0;
    }

    @Override
    public boolean isFlammable(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
        return false;
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (!this.stripped) {
            ItemStack stack = player.getHeldItem(handIn);
            if (isAxe(stack)) {
                worldIn.playSound(player, pos, SoundEvents.ITEM_AXE_STRIP, SoundCategory.BLOCKS, 1f, 1f);
                if (!worldIn.isRemote) {
                    worldIn.setBlockState(pos, ModBlocks.STRIPPED_NETHERWOOD_LOG.asBlockState().with(RotatedPillarBlock.AXIS, state.get(RotatedPillarBlock.AXIS)), 11);
                    stack.damageItem(1, player, p -> p.sendBreakAnimation(handIn));
                }

                return ActionResultType.SUCCESS;
            }
        }

        return ActionResultType.PASS;
    }

    private static boolean isAxe(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof AxeItem && (!(stack.getItem() instanceof ICoreItem) || !GearHelper.isBroken(stack));
    }
}
