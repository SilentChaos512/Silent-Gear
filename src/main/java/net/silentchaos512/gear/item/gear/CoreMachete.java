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
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.ToolType;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.api.stats.StatInstance;
import net.silentchaos512.gear.init.ModItems;
import net.silentchaos512.gear.util.GearHelper;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Set;

public class CoreMachete extends CoreSword {
    private static final int BREAK_RANGE = 2; // TODO: Config?
    private static final Set<Material> EFFECTIVE_MATERIALS = Sets.union(
            CoreSickle.EFFECTIVE_MATERIALS,
            ImmutableSet.of(Material.BAMBOO)
    );

    public CoreMachete() {
        super(ToolType.AXE);
    }

    @Override
    public GearType getGearType() {
        return GearType.MACHETE;
    }

    @Override
    public Optional<StatInstance> getBaseStatModifier(ItemStat stat) {
        if (stat == ItemStats.MELEE_DAMAGE)
            return Optional.of(StatInstance.makeBaseMod(4));
        if (stat == ItemStats.ATTACK_SPEED)
            return Optional.of(StatInstance.makeBaseMod(-2.6f));
        if (stat == ItemStats.REPAIR_EFFICIENCY)
            return Optional.of(StatInstance.makeBaseMod(1));
        return Optional.empty();
    }

    @Override
    public Optional<StatInstance> getStatModifier(ItemStat stat) {
        if (stat == ItemStats.DURABILITY)
            return Optional.of(StatInstance.makeGearMod(0.2f));
        if (stat == ItemStats.ENCHANTABILITY)
            return Optional.of(StatInstance.makeGearMod(-0.1f));
        return Optional.empty();
    }

    @Override
    public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, PlayerEntity player) {
        // Allow clearing vegetation, just like sickles but with a smaller range
        if (!player.isSneaking())
            return ModItems.sickle.onSickleStartBreak(itemstack, pos, player, BREAK_RANGE, EFFECTIVE_MATERIALS);
        return super.onBlockStartBreak(itemstack, pos, player);
    }

    @Override
    public int getHarvestLevel(ItemStack stack, ToolType tool, @Nullable PlayerEntity player, @Nullable BlockState blockState) {
        return GearHelper.getHarvestLevel(stack, tool, blockState, null);
    }

//    @Override
//    public void setHarvestLevel(String toolClass, int level) {
//        super.setHarvestLevel(toolClass, level);
//        GearHelper.setHarvestLevel(this, toolClass, level, this.toolClasses);
//    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        float speed = GearHelper.getDestroySpeed(stack, state, CoreAxe.EXTRA_EFFECTIVE_MATERIALS);
        // Slower on materials normally harvested with axes
        if (CoreAxe.BASE_EFFECTIVE_MATERIALS.contains(state.getMaterial()))
            return speed * 0.4f; // TODO: Add config!
        return speed;
    }
}
