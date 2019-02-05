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
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.config.Config;
import net.silentchaos512.gear.config.ConfigOptionEquipment;
import net.silentchaos512.gear.init.ModItems;
import net.silentchaos512.gear.util.GearHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

public class CoreMachete extends CoreSword {
    private static final int BREAK_RANGE = 2; // TODO: Config?

    private final Set<String> toolClasses = new HashSet<>();

    public CoreMachete() {
        setHarvestLevel("axe", 0);
    }

    @Nonnull
    @Override
    public ConfigOptionEquipment getConfig() {
        return Config.machete;
    }

    @Override
    public String getGearClass() {
        return "machete";
    }

    @Override
    public GearType getGearType() {
        return GearType.MACHETE;
    }

    @Override
    public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, EntityPlayer player) {
        // Allow clearing vegetation, just like sickles but with a smaller range
        if (!player.isSneaking())
            return ModItems.sickle.onSickleStartBreak(itemstack, pos, player, BREAK_RANGE);
        return super.onBlockStartBreak(itemstack, pos, player);
    }

    @Override
    public int getHarvestLevel(ItemStack stack, String toolClass, @Nullable EntityPlayer player, @Nullable IBlockState state) {
        return GearHelper.getHarvestLevel(stack, toolClass, state, null);
    }

    @Override
    public void setHarvestLevel(String toolClass, int level) {
        super.setHarvestLevel(toolClass, level);
        GearHelper.setHarvestLevel(this, toolClass, level, this.toolClasses);
    }

    @Override
    public Set<String> getToolClasses(ItemStack stack) {
        return GearHelper.isBroken(stack) ? ImmutableSet.of() : ImmutableSet.copyOf(toolClasses);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, IBlockState state) {
        float speed = GearHelper.getDestroySpeed(stack, state, CoreAxe.EXTRA_EFFECTIVE_MATERIALS);
        // Slower on materials normally harvested with axes
        if (state.getMaterial() == Material.WOOD || state.getMaterial() == Material.GOURD || state.getMaterial() == Material.CIRCUITS)
            return speed * 0.4f; // TODO: Add config!
        return speed;
    }
}
