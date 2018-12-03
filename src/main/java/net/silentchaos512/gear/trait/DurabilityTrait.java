/*
 * Silent Gear -- DurabilityTrait
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
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.traits.Trait;
import net.silentchaos512.lib.advancements.LibTriggers;

import javax.annotation.Nullable;

/**
 * A Trait that modifies durability damage taken by gear, similar to the unbreaking enchantment (or
 * the reverse in some cases). Chance of modification increases with trait level. Effect scale does
 * not change with level. A negative scale reduces damage taken, positive increases it.
 */
public class DurabilityTrait extends Trait {
    private static final ResourceLocation TRIGGER_BRITTLE = new ResourceLocation(SilentGear.MOD_ID, "brittle_proc");

    private final float effectScale;

    /**
     * Constructor
     *
     * @param name         Registry name
     * @param maxLevel     Max level that can be obtained
     * @param nameColor    Name color for tooltips
     * @param effectChance Chance of taking extra/reduced durability damage (0.0-1.0)
     * @param effectScale  Scale/direction of effect. Positive numbers increase damage, negative
     *                     numbers decrease it. Chance will be multiplied by trait level.
     */
    public DurabilityTrait(ResourceLocation name, int maxLevel, TextFormatting nameColor, float effectChance, float effectScale) {
        super(name, maxLevel, nameColor, effectChance);
        this.effectScale = effectScale;
    }

    @Override
    public float onDurabilityDamage(@Nullable EntityPlayer player, int level, ItemStack gear, int damageTaken) {
        if (damageTaken != 0 && shouldActivate(level, gear)) {
            if (effectScale > 0 && player instanceof EntityPlayerMP)
                LibTriggers.GENERIC_INT.trigger((EntityPlayerMP) player, TRIGGER_BRITTLE, 1);
            return Math.round(damageTaken + effectScale);
        }

        return super.onDurabilityDamage(player, level, gear, damageTaken);
    }
}
