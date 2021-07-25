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

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.silentchaos512.gear.block.trees.NetherwoodTree;
import net.silentchaos512.gear.init.ModTags;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class NetherwoodSapling extends SaplingBlock {
    public NetherwoodSapling(Properties properties) {
        super(new NetherwoodTree(), properties);
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return state.is(ModTags.Blocks.NETHERWOOD_SOIL);
    }
}
