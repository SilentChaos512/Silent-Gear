/*
 * Silent Gear -- TraitRefractive
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

package net.silentchaos512.gear.trait;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.silentchaos512.gear.api.lib.ResourceOrigin;
import net.silentchaos512.gear.api.traits.Trait;
import net.silentchaos512.gear.init.ModBlocks;
import net.silentchaos512.lib.util.MathUtils;

import javax.annotation.Nullable;

public class TraitRefractive extends Trait {
    private static final int ACTIVATE_RATE = 20;
    private static final int CHECK_RANGE = 3;
    private static final int VERTICAL_RANGE = 5;

    public TraitRefractive(ResourceLocation name, ResourceOrigin origin) {
        super(name, origin);
    }

    @Override
    public void tick(World world, @Nullable EntityPlayer player, int level, ItemStack gear, boolean isEquipped) {
        super.tick(world, player, level, gear, isEquipped);

        if (player != null && player.ticksExisted % ACTIVATE_RATE == 0) {
            // TODO: Phantom lights, block config?
            // This fails with torches to some extent, they don't attach to walls
            placeLight(player.world, player, ModBlocks.phantomLight.getDefaultState());
        }
    }

    @SuppressWarnings("TypeMayBeWeakened")
    private static void placeLight(World world, EntityPlayer player, IBlockState state) {
        BlockPos bottomPos = player.getPosition()
                .offset(EnumFacing.NORTH, MathUtils.nextIntInclusive(-CHECK_RANGE, CHECK_RANGE))
                .offset(EnumFacing.WEST, MathUtils.nextIntInclusive(-CHECK_RANGE, CHECK_RANGE));
        if (world.getLightFor(EnumSkyBlock.BLOCK, bottomPos) > 7)
            return;

        for (BlockPos pos = bottomPos.up(VERTICAL_RANGE); pos.getY() >= bottomPos.getY(); pos = pos.down()) {
            if (world.isAirBlock(pos) && state.getBlock().canPlaceBlockAt(world, pos)) {
                world.setBlockState(pos, state, 3);
                break;
            }
        }
    }
}
