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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.event.GetTraitsEvent;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.part.PartDataList;
import net.silentchaos512.gear.api.traits.ITrait;
import net.silentchaos512.gear.api.traits.TraitActionContext;
import net.silentchaos512.gear.api.traits.TraitFunction;
import net.silentchaos512.gear.api.traits.TraitInstance;
import net.silentchaos512.gear.api.util.DataResource;
import net.silentchaos512.gear.api.util.IGearComponentInstance;
import net.silentchaos512.gear.api.util.PartGearKey;
import net.silentchaos512.gear.compat.curios.CuriosCompat;
import net.silentchaos512.gear.gear.part.PartData;
import net.silentchaos512.gear.gear.trait.TraitManager;

import javax.annotation.Nullable;
import java.util.*;

public final class TraitHelper {
    private TraitHelper() {throw new IllegalAccessError("Utility class");}

    /**
     * An easy way to activate an item's traits from anywhere. <strong>Use with care!</strong>
     * Calling this frequently (like every render tick) causes FPS to tank.
     * <p>
     * This implementation pulls the item's traits straight from NBT to minimize object creation.
     * The {@link TraitFunction} is applied to every trait.
     *
     * @param gear       The {@link net.silentchaos512.gear.api.item.ICoreItem} affected
     * @param inputValue The base value to have the traits act on.
     * @param action     The specific action to apply to each trait. This is {@code (trait, level,
     *                   value) -> modifiedInputValue}, where 'value' is the currently calculated
     *                   result.
     * @return The {@code inputValue} modified by traits.
     */
    public static float activateTraits(ItemStack gear, final float inputValue, TraitFunction action) {
        if (!GearHelper.isGear(gear)) {
            SilentGear.LOGGER.error("Called activateTraits on non-gear item, {}", gear);
            SilentGear.LOGGER.catching(new IllegalArgumentException());
            return inputValue;
        }

        ListTag tagList = GearData.getPropertiesData(gear).getList("Traits", Tag.TAG_COMPOUND);
        float value = inputValue;

        for (Tag nbt : tagList) {
            if (nbt instanceof CompoundTag) {
                CompoundTag tagCompound = (CompoundTag) nbt;
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
    public static int getTraitLevel(ItemStack gear, DataResource<ITrait> trait) {
        return getTraitLevel(gear, trait.getId());
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
        return getTraitLevel(gear, trait.getId());
    }

    /**
     * Gets the level of the trait on the gear using the trait ID.
     *
     * @param gear    The gear item
     * @param traitId The trait's ID
     * @return The level of the trait on the gear, or zero if the item does not have the trait or
     * the trait does not exist.
     */
    public static int getTraitLevel(ItemStack gear, ResourceLocation traitId) {
        if (GearHelper.isGear(gear)) {
            ListTag tagList = GearData.getPropertiesData(gear).getList("Traits", Tag.TAG_COMPOUND);

            for (Tag nbt : tagList) {
                if (nbt instanceof CompoundTag) {
                    CompoundTag tagCompound = (CompoundTag) nbt;
                    String regName = tagCompound.getString("Name");
                    if (regName.equals(traitId.toString())) {
                        return tagCompound.getByte("Level");
                    }
                }
            }
        }

        return 0;
    }

    public static boolean hasTrait(ItemStack gear, DataResource<ITrait> trait) {
        return hasTrait(gear, trait.getId());
    }

    /**
     * Check if the gear item has the given trait at any level. Use {@link #getTraitLevel(ItemStack,
     * ITrait)} to check the actual level.
     *
     * @param gear  The gear item
     * @param trait The trait
     * @return True if and only if the gear item has the trait
     */
    public static boolean hasTrait(ItemStack gear, ITrait trait) {
        return hasTrait(gear, trait.getId());
    }

    /**
     * Check if the gear item has the given trait at any level. Use {@link #getTraitLevel(ItemStack,
     * ResourceLocation)} to check the actual level.
     *
     * @param gear    The gear item
     * @param traitId The trait ID
     * @return True if and only if the gear item has the trait
     */
    public static boolean hasTrait(ItemStack gear, ResourceLocation traitId) {
        if (GearHelper.isGear(gear)) {
            ListTag tagList = GearData.getPropertiesData(gear).getList("Traits", Tag.TAG_COMPOUND);

            for (Tag nbt : tagList) {
                if (nbt instanceof CompoundTag) {
                    CompoundTag tagCompound = (CompoundTag) nbt;
                    String regName = tagCompound.getString("Name");
                    if (regName.equals(traitId.toString())) {
                        return true;
                    }
                }
            }

            return false;
        }

        return false;
    }

    public static int getHighestLevelEitherHand(Player player, DataResource<ITrait> trait) {
        return getHighestLevelEitherHand(player, trait.getId());
    }

    @Deprecated
    public static int getHighestLevelEitherHand(Player player, ResourceLocation traitId) {
        ItemStack main = player.getMainHandItem();
        ItemStack off = player.getOffhandItem();
        return Math.max(getTraitLevel(main, traitId), getTraitLevel(off, traitId));
    }

    public static int getHighestLevelArmor(Player player, DataResource<ITrait> trait) {
        int max = 0;
        for (ItemStack stack : player.getInventory().armor) {
            max = Math.max(max, getTraitLevel(stack, trait));
        }
        return max;
    }

    public static int getHighestLevelCurio(LivingEntity entity, DataResource<ITrait> trait) {
        if (ModList.get().isLoaded(Const.CURIOS)) {
            return CuriosCompat.getHighestTraitLevel(entity, trait);
        }
        return 0;
    }

    public static int getHighestLevelArmorOrCurio(Player player, DataResource<ITrait> trait) {
        return Math.max(getHighestLevelArmor(player, trait), getHighestLevelCurio(player, trait));
    }

    public static boolean hasTraitEitherHand(Player player, DataResource<ITrait> trait) {
        return hasTraitEitherHand(player, trait.getId());
    }

    @Deprecated
    public static boolean hasTraitEitherHand(Player player, ResourceLocation traitId) {
        ItemStack main = player.getMainHandItem();
        ItemStack off = player.getOffhandItem();
        return hasTrait(main, traitId) || hasTrait(off, traitId);
    }

    public static boolean hasTraitArmor(Player player, DataResource<ITrait> trait) {
        for (ItemStack stack : player.getInventory().armor) {
            if (hasTrait(stack, trait)) {
                return true;
            }
        }
        return false;
    }

    public static Map<ITrait, Integer> getCachedTraits(ItemStack gear) {
        if (!GearHelper.isGear(gear)) return ImmutableMap.of();

        Map<ITrait, Integer> result = new LinkedHashMap<>();
        ListTag tagList = GearData.getPropertiesData(gear).getList("Traits", Tag.TAG_COMPOUND);

        for (Tag nbt : tagList) {
            if (nbt instanceof CompoundTag) {
                CompoundTag tagCompound = (CompoundTag) nbt;
                String name = tagCompound.getString("Name");
                ITrait trait = TraitManager.get(name);
                int level = tagCompound.getByte("Level");
                if (trait != null && level > 0) {
                    result.put(trait, level);
                }
            }
        }

        return result;
    }

    @Deprecated
    public static Map<ITrait, Integer> getTraits(ItemStack gear, PartDataList parts) {
        return getTraits(gear, GearHelper.getType(gear), parts);
    }

    /**
     * Gets a Map of Traits and levels from the parts, used to calculate trait levels and should not
     * be used in most cases. Consider using {@link #getTraitLevel(ItemStack, ResourceLocation)} or
     * {@link #hasTrait(ItemStack, ResourceLocation)} when appropriate.
     *
     * @param gear     The item
     * @param gearType The gear type
     * @param parts    The list of all parts used in constructing the gear.
     * @return A Map of Traits to their levels
     */
    public static Map<ITrait, Integer> getTraits(ItemStack gear, GearType gearType, PartDataList parts) {
        if (parts.isEmpty() || (!gear.isEmpty() && GearHelper.isBroken(gear)))
            return ImmutableMap.of();

        Map<ITrait, Integer> result = new LinkedHashMap<>();

        for (PartData part : parts) {
            PartGearKey key = PartGearKey.of(gearType, part);
            for (TraitInstance inst : part.getTraits(key, gear)) {
                if (inst.conditionsMatch(key, gear, parts)) {
                    ITrait trait = inst.getTrait();
                    // Get the highest value in any part
                    result.merge(trait, inst.getLevel(), Integer::max);
                }
            }
        }

        ITrait[] keys = result.keySet().toArray(new ITrait[0]);

        cancelTraits(result, keys);
        MinecraftForge.EVENT_BUS.post(new GetTraitsEvent(gear, parts, result));
        return result;
    }

    public static List<TraitInstance> getTraits(List<? extends IGearComponentInstance<?>> components, PartGearKey partKey, ItemStack gear) {
        if (components.isEmpty()) {
            return Collections.emptyList();
        }

        Map<ITrait, Integer> map = new LinkedHashMap<>();
        Map<ITrait, Integer> countMatsWithTrait = new HashMap<>();

        for (IGearComponentInstance<?> comp : components) {
            for (TraitInstance inst : comp.getTraits(partKey, gear)) {
                if (inst.conditionsMatch(partKey, gear, components)) {
                    map.merge(inst.getTrait(), inst.getLevel(), Integer::sum);
                    countMatsWithTrait.merge(inst.getTrait(), 1, Integer::sum);
                }
            }
        }

        ITrait[] keys = map.keySet().toArray(new ITrait[0]);

        for (ITrait trait : keys) {
            final int matsWithTrait = countMatsWithTrait.get(trait);
            final float divisor = Math.max(components.size() / 2f, matsWithTrait);
            final int value = Math.round(map.get(trait) / divisor);
            map.put(trait, Mth.clamp(value, 1, trait.getMaxLevel()));
        }

        cancelTraits(map, keys);
        // FIXME
//        MinecraftForge.EVENT_BUS.post(new GetTraitsEvent(gear, materials, result));

        List<TraitInstance> ret = new ArrayList<>();
        map.forEach((trait, level) -> ret.add(TraitInstance.of(trait, level)));
        return ret;
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

    static void tickTraits(Level world, @Nullable Player player, ItemStack gear, boolean isEquipped) {
        ListTag tagList = GearData.getPropertiesData(gear).getList("Traits", Tag.TAG_COMPOUND);

        for (int i = 0; i < tagList.size(); ++i) {
            CompoundTag tagCompound = tagList.getCompound(i);
            String regName = tagCompound.getString("Name");
            ITrait trait = TraitManager.get(regName);

            if (trait != null) {
                int level = tagCompound.getByte("Level");
                TraitActionContext context = new TraitActionContext(player, level, gear);
                trait.onUpdate(context, isEquipped);
            }
        }
    }
}
