/*
 * Silent Gear -- TraitHelper
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

package net.silentchaos512.gear.util;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.silentchaos512.gear.api.traits.ITrait;
import net.silentchaos512.gear.api.traits.TraitActionContext;
import net.silentchaos512.gear.api.traits.TraitFunction;
import net.silentchaos512.gear.parts.PartData;
import net.silentchaos512.gear.traits.TraitManager;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiFunction;

public final class TraitHelper {
    private TraitHelper() {throw new IllegalAccessError("Utility class");}

    /**
     * An easy way to activate an item's traits from anywhere. <strong>Use with care!</strong>
     * Calling this frequently (like every render tick) causes FPS to tank.
     * <p>
     * This implementation pulls the item's traits straight from NBT to minimize object creation.
     * The {@link BiFunction} is applied to every trait.
     *
     * @param gear       The {@link net.silentchaos512.gear.api.item.ICoreItem} affected
     * @param inputValue The base value to have the traits act on.
     * @param action     The specific action to apply to each trait. This is {@code (trait, level,
     *                   value) -> modifiedInputValue}, where 'value' is the currently calculated
     *                   result.
     * @return The {@code inputValue} modified by traits.
     */
    public static float activateTraits(ItemStack gear, final float inputValue, TraitFunction action) {
        NBTTagList tagList = GearData.getPropertiesData(gear).getList("Traits", 10);
        float value = inputValue;

        for (INBTBase nbt : tagList) {
            if (nbt instanceof NBTTagCompound) {
                NBTTagCompound tagCompound = (NBTTagCompound) nbt;
                String regName = tagCompound.getString("Name");
                ITrait trait = TraitManager.get(regName);

                if (trait != null) {
                    int level = tagCompound.getByte("Level");
                    value = action.apply(trait, level, value);
                }
            }
        }

        return value;
    }

    /**
     * Gets the level of the trait on the gear, or zero if it does not have the trait. Similar to
     * {@link #activateTraits(ItemStack, float, TraitFunction)}, this pulls the traits straight from
     * NBT to minimize object creation.
     *
     * @param gear  The {@link net.silentchaos512.gear.api.item.ICoreItem}
     * @param trait The trait to look for
     * @return The level of the trait on the gear, or zero if it does not have the trait
     */
    public static int getTraitLevel(ItemStack gear, ITrait trait) {
        NBTTagList tagList = GearData.getPropertiesData(gear).getList("Traits", 10);

        for (INBTBase nbt : tagList) {
            if (nbt instanceof NBTTagCompound) {
                NBTTagCompound tagCompound = (NBTTagCompound) nbt;
                String regName = tagCompound.getString("Name");
                ITrait traitOnGear = TraitManager.get(regName);

                if (traitOnGear == trait) {
                    return tagCompound.getByte("Level");
                }
            }
        }

        return 0;
    }

    /**
     * Shortcut for {@link #getTraitLevel(ItemStack, ITrait)}, which acquires the trait from the
     * TraitManager for you.
     *
     * @param gear    The gear item
     * @param traitId The trait's ID
     * @return The level of the trait on the gear, or zero if the item does not have the trait or
     * the trait does not exist.
     */
    public static int getTraitLevel(ItemStack gear, ResourceLocation traitId) {
        ITrait trait = TraitManager.get(traitId);
        return trait != null ? getTraitLevel(gear, trait) : 0;
    }

    /**
     * Gets a Map of Traits and levels from the parts, used to calculate trait levels and should not
     * be used in most cases. Consider using {@link #getTraitLevel(ItemStack, ITrait)} when
     * appropriate.
     *
     * @param parts The list of all parts used in constructing the gear.
     * @return A Map of Traits to their levels
     */
    public static Map<ITrait, Integer> getTraits(Collection<PartData> parts) {
        if (parts.isEmpty())
            return ImmutableMap.of();

        Map<ITrait, Integer> result = new LinkedHashMap<>();
        Map<ITrait, Integer> countPartsWithTrait = new HashMap<>();

        for (PartData part : parts) {
            part.getTraits().forEach(((trait, level) -> {
                // Count total levels for each trait from all parts
                result.merge(trait, level, (i1, i2) -> i1 + i2);
                // Count number of parts with each trait
                countPartsWithTrait.merge(trait, 1, (i1, i2) -> i1 + i2);
            }));
        }

        ITrait[] keys = result.keySet().toArray(new ITrait[0]);

        for (ITrait trait : keys) {
            final int partsWithTrait = countPartsWithTrait.get(trait);
            final float divisor = Math.max(1, partsWithTrait);
            final int value = Math.round(result.get(trait) / divisor);
            result.put(trait, MathHelper.clamp(value, 1, trait.getMaxLevel()));
        }

        // TODO: Consider non-mains

        cancelTraits(result, keys);

        return result;
    }

    private static void cancelTraits(Map<ITrait, Integer> mapToModify, ITrait[] keys) {
        for (int i = 0; i < keys.length; ++i) {
            ITrait t1 = keys[i];

            if (mapToModify.containsKey(t1)) {
                for (int j = i + 1; j < keys.length; ++j) {
                    ITrait t2 = keys[j];

                    if (mapToModify.containsKey(t2) && t1.willCancelWith(t2)) {
                        final int level = mapToModify.get(t1);
                        final int otherLevel = mapToModify.get(t2);
                        final int cancelLevel = t1.getCanceledLevel(level, t2, otherLevel);

                        if (cancelLevel > 0) {
                            mapToModify.put(t1, cancelLevel);
                            mapToModify.remove(t2);
                        } else if (cancelLevel < 0) {
                            mapToModify.put(t2, -cancelLevel);
                            mapToModify.remove(t1);
                            break;
                        } else {
                            mapToModify.remove(t1);
                            mapToModify.remove(t2);
                            break;
                        }
                    }
                }
            }
        }
    }

    static void tickTraits(@Nullable EntityPlayer player, ItemStack gear) {
        // Performance test on 2018-11-26 - roughly 5% FPS loss max (negligible), average ~420 FPS
        NBTTagList tagList = GearData.getPropertiesData(gear).getList("Traits", 10);

        for (INBTBase nbt : tagList) {
            if (nbt instanceof NBTTagCompound) {
                NBTTagCompound tagCompound = (NBTTagCompound) nbt;
                String regName = tagCompound.getString("Name");
                ITrait trait = TraitManager.get(regName);

                if (trait != null) {
                    int level = tagCompound.getByte("Level");
                    trait.onUpdate(new TraitActionContext(player, level, gear));
                }
            }
        }
    }
}
