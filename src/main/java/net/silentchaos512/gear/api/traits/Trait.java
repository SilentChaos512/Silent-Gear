/*
 * Silent Gear -- Trait
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

package net.silentchaos512.gear.api.traits;

import lombok.Getter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.lib.util.MathUtils;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

public class Trait {
    @Getter private final float activationChance;
    @Getter private final int maxLevel;
    @Getter private final ResourceLocation name;
    @Getter private final TextFormatting nameColor;
    private final Set<Trait> cancelsWith = new HashSet<>();

    public Trait(ResourceLocation name, int maxLevel, TextFormatting nameColor, float activationChance) {
        this.name = name;
        this.maxLevel = maxLevel;
        this.nameColor = nameColor;
        this.activationChance = activationChance;
    }

    public static void setCancelsWith(Trait t1, Trait t2) {
        SilentGear.log.debug("Set trait cancels with: '{}' and '{}'", t1.name, t2.name);
        t1.cancelsWith.add(t2);
        t2.cancelsWith.add(t1);
    }

    public final int getCanceledLevel(int level, Trait other, int otherLevel) {
        if (cancelsWith.contains(other)) {
            final int diff = level - otherLevel;

            int newLevel;
            if (diff < 0)
                newLevel = MathHelper.clamp(diff, -other.maxLevel, 0);
            else
                newLevel = MathHelper.clamp(diff, 0, this.maxLevel);

            return newLevel;
        }
        return level;
    }

    public final boolean willCancelWith(Trait other) {
        return cancelsWith.contains(other);
    }

    public String getTranslatedName(int level) {
        String translatedName = SilentGear.i18n.translate("trait." + name);
        String levelString = SilentGear.i18n.translate("enchantment.level." + level);
        return SilentGear.i18n.translate("trait", "displayFormat", translatedName, levelString);
    }

    @Override
    public String toString() {
        return "Trait{" +
                "name=" + name +
                ", maxLevel=" + maxLevel +
                ", nameColor=" + nameColor +
                '}';
    }

    public NBTTagCompound writeToNBT(int level) {
        NBTTagCompound tagCompound = new NBTTagCompound();
        tagCompound.setString("Name", name.toString());
        tagCompound.setByte("Level", (byte) level);
        return tagCompound;
    }

    //region Handlers

    protected boolean shouldActivate(int level, ItemStack gear) {
        final float chance = activationChance * level;
        return MathUtils.tryPercentage(chance);
    }

    public float onDurabilityDamage(@Nullable EntityPlayer player, int level, ItemStack gear, int damageTaken) {
        return damageTaken;
    }

    public float onGetStat(@Nullable EntityPlayer player, ItemStat stat, int level, ItemStack gear, float value) {
        return value;
    }

    //endregion
}
