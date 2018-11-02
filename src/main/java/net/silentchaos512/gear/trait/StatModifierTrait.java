/*
 * Silent Gear -- StatModifierTrait
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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.traits.Trait;

import javax.annotation.Nullable;

public abstract class StatModifierTrait extends Trait {
    public StatModifierTrait(ResourceLocation name, int maxLevel, TextFormatting nameColor) {
        super(name, maxLevel, nameColor, 1);
    }

    @Override
    public abstract float onGetStat(@Nullable EntityPlayer player, ItemStat stat, int level, ItemStack gear, float value);
}
