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

package net.silentchaos512.gear.traits;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumLightType;
import net.minecraft.world.World;
import net.silentchaos512.gear.api.traits.TraitActionContext;
import net.silentchaos512.gear.init.ModBlocks;
import net.silentchaos512.lib.util.MathUtils;

public class TraitRefractive extends SimpleTrait {
    // TODO: Serializer needed to load trait correctly
    private static final int ACTIVATE_RATE = 20;
    private static final int CHECK_RANGE = 3;
    private static final int VERTICAL_RANGE = 5;

    public TraitRefractive(ResourceLocation name) {
        super(name);
    }

    @Override
    public void onUpdate(TraitActionContext context) {
        super.onUpdate(context);

        EntityPlayer player = context.getPlayer();
        if (player != null && player.ticksExisted % ACTIVATE_RATE == 0) {
            // TODO: Phantom lights, block config?
            // This fails with torches to some extent, they don't attach to walls
            placeLight(player.world, player, ModBlocks.PHANTOM_LIGHT.asBlockState());
        }
    }

    @SuppressWarnings("TypeMayBeWeakened")
    private static void placeLight(World world, EntityPlayer player, IBlockState state) {
        BlockPos bottomPos = player.getPosition()
                .offset(EnumFacing.NORTH, MathUtils.nextIntInclusive(-CHECK_RANGE, CHECK_RANGE))
                .offset(EnumFacing.WEST, MathUtils.nextIntInclusive(-CHECK_RANGE, CHECK_RANGE));
        if (world.getLightFor(EnumLightType.BLOCK, bottomPos) > 7)
            return;

        for (BlockPos pos = bottomPos.up(VERTICAL_RANGE); pos.getY() >= bottomPos.getY(); pos = pos.down()) {
            // FIXME
            /*
            if (world.isAirBlock(pos) && state.getBlock().canPlaceBlockAt(world, pos)) {
                world.setBlockState(pos, state, 3);
                break;
            }
            */
        }
    }
}
