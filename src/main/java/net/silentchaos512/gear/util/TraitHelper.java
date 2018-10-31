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
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.parts.ItemPartData;
import net.silentchaos512.gear.api.traits.Trait;
import net.silentchaos512.gear.api.traits.TraitRegistry;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;

public final class TraitHelper {
    private TraitHelper() {throw new IllegalAccessError("Utility class");}

    public static float activateTraits(ItemStack gear, final int inputValue, BiFunction<Trait, Integer, Float> action) {
        NBTTagList tagList = GearData.getPropertiesData(gear).getTagList("Traits", 10);
        float value = inputValue;

        for (NBTBase nbt : tagList) {
            if (nbt instanceof NBTTagCompound) {
                NBTTagCompound tagCompound = (NBTTagCompound) nbt;
                String regName = tagCompound.getString("Name");
                Trait trait = TraitRegistry.get(regName);

                if (trait != null) {
                    int level = tagCompound.getByte("Level");
                    value = action.apply(trait, level);
                }
            }
        }

        SilentGear.log.debug("activateTraits: {} -> {}", inputValue, value);
        return value;
    }

    public static Map<Trait, Integer> getTraits(Collection<ItemPartData> parts) {
        if (parts.isEmpty())
            return ImmutableMap.of();

        Map<Trait, Integer> result = new LinkedHashMap<>();
//        Map<ItemPart, Integer> uniquePartCounts = new HashMap<>();

        for (ItemPartData part : parts) {
            part.getTraits().forEach(((trait, level) -> {
                if (result.containsKey(trait)) {
                    result.put(trait, result.get(trait) + level);
                } else {
                    result.put(trait, level);
                }
            }));

//            final ItemPart p = part.getPart();
//            SilentGear.log.debug("GearData#getTraits: {} level {}", p.getRegistryName(), uniquePartCounts.get(p));
//            if (uniquePartCounts.containsKey(p)) {
//                uniquePartCounts.put(p, uniquePartCounts.get(p) + 1);
//            } else {
//                uniquePartCounts.put(p, 1);
//            }
        }

        Trait[] keys = result.keySet().toArray(new Trait[0]);

        for (Trait trait : keys) {
            int avg = Math.round((float) result.get(trait) / parts.size());
            result.put(trait, avg);
        }

        cancelTraits(result, keys);

        return result;
    }

    private static void cancelTraits(Map<Trait, Integer> mapToModify, Trait[] keys) {
        for (int i = 0; i < keys.length; ++i) {
            Trait t1 = keys[i];

            if (mapToModify.containsKey(t1)) {
                for (int j = i + 1; j < keys.length; ++j) {
                    Trait t2 = keys[j];

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
}
