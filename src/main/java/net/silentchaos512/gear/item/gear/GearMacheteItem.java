/*
 * Silent Gear -- CoreMachete
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

package net.silentchaos512.gear.item.gear;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.init.ModItems;
import net.silentchaos512.gear.util.GearHelper;

import java.util.Set;

public class GearMacheteItem extends GearSwordItem {
    private static final int BREAK_RANGE = 2;
    private static final Set<Material> EFFECTIVE_MATERIALS = Sets.union(
            GearSickleItem.EFFECTIVE_MATERIALS,
            ImmutableSet.of(Material.BAMBOO)
    );

    public GearMacheteItem(GearType gearType) {
        super(gearType);
    }

    @Override
    public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, Player player) {
        // Allow clearing vegetation, just like sickles but with a smaller range
        if (!player.isCrouching())
            return ModItems.SICKLE.get().onSickleStartBreak(itemstack, pos, player, BREAK_RANGE, EFFECTIVE_MATERIALS);
        return super.onBlockStartBreak(itemstack, pos, player);
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        return GearHelper.isCorrectToolForDrops(stack, state, BlockTags.MINEABLE_WITH_AXE, GearAxeItem.AXE_EFFECTIVE_MATERIALS);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        float speed = GearHelper.getDestroySpeed(stack, state, GearAxeItem.AXE_EFFECTIVE_MATERIALS);
        // Slower on materials normally harvested with axes
        if (GearAxeItem.AXE_EFFECTIVE_MATERIALS.contains(state.getMaterial()))
            return speed * 0.4f;
        return speed;
    }
}
