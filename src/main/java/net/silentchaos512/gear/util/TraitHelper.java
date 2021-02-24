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
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.ModList;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.event.GetTraitsEvent;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.part.PartDataList;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.traits.ITrait;
import net.silentchaos512.gear.api.traits.TraitActionContext;
import net.silentchaos512.gear.api.traits.TraitFunction;
import net.silentchaos512.gear.api.traits.TraitInstance;
import net.silentchaos512.gear.api.util.PartGearKey;
import net.silentchaos512.gear.compat.curios.CuriosCompat;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.gear.part.PartData;
import net.silentchaos512.gear.gear.trait.TraitManager;
import net.silentchaos512.utils.MathUtils;

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

        ListNBT tagList = GearData.getPropertiesData(gear).getList("Traits", Constants.NBT.TAG_COMPOUND);
        float value = inputValue;

        for (INBT nbt : tagList) {
            if (nbt instanceof CompoundNBT) {
                CompoundNBT tagCompound = (CompoundNBT) nbt;
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
            ListNBT tagList = GearData.getPropertiesData(gear).getList("Traits", Constants.NBT.TAG_COMPOUND);

            for (INBT nbt : tagList) {
                if (nbt instanceof CompoundNBT) {
                    CompoundNBT tagCompound = (CompoundNBT) nbt;
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
            ListNBT tagList = GearData.getPropertiesData(gear).getList("Traits", Constants.NBT.TAG_COMPOUND);

            for (INBT nbt : tagList) {
                if (nbt instanceof CompoundNBT) {
                    CompoundNBT tagCompound = (CompoundNBT) nbt;
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

    public static int getHighestLevelEitherHand(PlayerEntity player, DataResource<ITrait> trait) {
        return getHighestLevelEitherHand(player, trait.getId());
    }

    @Deprecated
    public static int getHighestLevelEitherHand(PlayerEntity player, ResourceLocation traitId) {
        ItemStack main = player.getHeldItemMainhand();
        ItemStack off = player.getHeldItemOffhand();
        return Math.max(getTraitLevel(main, traitId), getTraitLevel(off, traitId));
    }

    public static int getHighestLevelArmor(PlayerEntity player, DataResource<ITrait> trait) {
        int max = 0;
        for (ItemStack stack : player.inventory.armorInventory) {
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

    public static boolean hasTraitEitherHand(PlayerEntity player, DataResource<ITrait> trait) {
        return hasTraitEitherHand(player, trait.getId());
    }

    @Deprecated
    public static boolean hasTraitEitherHand(PlayerEntity player, ResourceLocation traitId) {
        ItemStack main = player.getHeldItemMainhand();
        ItemStack off = player.getHeldItemOffhand();
        return hasTrait(main, traitId) || hasTrait(off, traitId);
    }

    public static boolean hasTraitArmor(PlayerEntity player, DataResource<ITrait> trait) {
        for (ItemStack stack : player.inventory.armorInventory) {
            if (hasTrait(stack, trait)) {
                return true;
            }
        }
        return false;
    }

    public static Map<ITrait, Integer> getCachedTraits(ItemStack gear) {
        if (!GearHelper.isGear(gear)) return ImmutableMap.of();

        Map<ITrait, Integer> result = new LinkedHashMap<>();
        ListNBT tagList = GearData.getPropertiesData(gear).getList("Traits", Constants.NBT.TAG_COMPOUND);

        for (INBT nbt : tagList) {
            if (nbt instanceof CompoundNBT) {
                CompoundNBT tagCompound = (CompoundNBT) nbt;
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
     * @param gearType
     * @param parts    The list of all parts used in constructing the gear.
     * @return A Map of Traits to their levels
     */
    public static Map<ITrait, Integer> getTraits(ItemStack gear, GearType gearType, PartDataList parts) {
        if (parts.isEmpty() || (!gear.isEmpty() && GearHelper.isBroken(gear)))
            return ImmutableMap.of();

        Map<ITrait, Integer> result = new LinkedHashMap<>();

        for (PartData part : parts) {
            for (TraitInstance inst : part.getTraits(gear)) {
                if (inst.conditionsMatch(PartGearKey.of(gearType, PartType.NONE), gear, parts)) {
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

    @Deprecated
    public static Map<ITrait, Integer> getTraits(List<MaterialInstance> materials, PartType partType, ItemStack gear) {
        return getTraits(materials, GearHelper.getType(gear), partType, gear);
    }

    public static Map<ITrait, Integer> getTraits(List<MaterialInstance> materials, GearType gearType, PartType partType, ItemStack gear) {
        if (materials.isEmpty())
            return Collections.emptyMap();

        Map<ITrait, Integer> result = new LinkedHashMap<>();
        Map<ITrait, Integer> countMatsWithTrait = new HashMap<>();

        for (MaterialInstance material : materials) {
            for (TraitInstance inst : material.getTraits(partType, gearType, gear)) {
                if (inst.conditionsMatch(PartGearKey.of(gearType, partType), gear, materials)) {
                    result.merge(inst.getTrait(), inst.getLevel(), Integer::sum);
                    countMatsWithTrait.merge(inst.getTrait(), 1, Integer::sum);
                }
            }
        }

        ITrait[] keys = result.keySet().toArray(new ITrait[0]);

        for (ITrait trait : keys) {
            final int matsWithTrait = countMatsWithTrait.get(trait);
            final float divisor = Math.max(materials.size() / 2f, matsWithTrait);
            final int value = Math.round(result.get(trait) / divisor);
            result.put(trait, MathHelper.clamp(value, 1, trait.getMaxLevel()));
        }

        cancelTraits(result, keys);
        // FIXME
//        MinecraftForge.EVENT_BUS.post(new GetTraitsEvent(gear, materials, result));
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

    static void tickTraits(World world, @Nullable PlayerEntity player, ItemStack gear, boolean isEquipped) {
        ListNBT tagList = GearData.getPropertiesData(gear).getList("Traits", Constants.NBT.TAG_COMPOUND);

        for (int i = 0; i < tagList.size(); ++i) {
            CompoundNBT tagCompound = tagList.getCompound(i);
            String regName = tagCompound.getString("Name");
            ITrait trait = TraitManager.get(regName);

            if (trait != null) {
                int level = tagCompound.getByte("Level");
                TraitActionContext context = new TraitActionContext(player, level, gear);
                trait.onUpdate(context, isEquipped);
                extraTickFunctions(trait, context);
            }
        }
    }

    private static void extraTickFunctions(ITrait trait, TraitActionContext context) {
        // Stellar repair
        PlayerEntity player = context.getPlayer();
        if (trait.getId().equals(Const.Traits.STELLAR.getId()) && player != null && player.ticksExisted % 20 == 0) {
            float chance = Const.Traits.STELLAR_REPAIR_CHANCE * context.getTraitLevel();
            if (MathUtils.tryPercentage(chance)) {
                GearHelper.attemptDamage(context.getGear(), -1, player, Hand.MAIN_HAND);
            }
        }
    }
}
