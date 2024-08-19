package net.silentchaos512.gear.util;

import com.google.common.collect.ImmutableMap;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.NeoForge;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.event.GetTraitsEvent;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.part.PartList;
import net.silentchaos512.gear.api.property.TraitListPropertyValue;
import net.silentchaos512.gear.api.traits.TraitActionContext;
import net.silentchaos512.gear.api.traits.TraitFunction;
import net.silentchaos512.gear.api.traits.TraitInstance;
import net.silentchaos512.gear.api.util.DataResource;
import net.silentchaos512.gear.api.util.GearComponentInstance;
import net.silentchaos512.gear.api.util.PartGearKey;
import net.silentchaos512.gear.compat.curios.CuriosCompat;
import net.silentchaos512.gear.gear.part.PartInstance;
import net.silentchaos512.gear.gear.trait.Trait;
import net.silentchaos512.gear.setup.gear.GearProperties;

import javax.annotation.Nullable;
import java.util.*;

public final class TraitHelper {
    private TraitHelper() {
        throw new IllegalAccessError("Utility class");
    }

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

        var traitListProperty = GearData.getProperties(gear).get(GearProperties.TRAITS.get());
        if (traitListProperty == null) return inputValue;

        var traits = traitListProperty.value();
        float value = inputValue;

        for (TraitInstance trait : traits) {
            value = action.apply(trait, value);
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
    public static int getTraitLevel(ItemStack gear, DataResource<Trait> trait) {
        if (GearHelper.isGear(gear)) {
            var list = GearData.getProperties(gear).getOrDefault(GearProperties.TRAITS, TraitListPropertyValue.empty());
            for (var traitInstance : list.value()) {
                if (traitInstance.getTrait() == trait.get()) {
                    return traitInstance.getLevel();
                }
            }
        }

        return 0;
    }

    /**
     * Check if the gear item has the given trait at any level.
     *
     * @param gear  The gear item
     * @param trait The trait
     * @return True if and only if the gear item has the trait
     */
    public static boolean hasTrait(ItemStack gear, DataResource<Trait> trait) {
        if (GearHelper.isGear(gear)) {
            var list = GearData.getProperties(gear).getOrDefault(GearProperties.TRAITS, TraitListPropertyValue.empty());
            for (var traitInstance : list.value()) {
                if (traitInstance.getTrait() == trait.get()) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Check if the gear item has the given trait at any level.
     *
     * @param gear  The gear item
     * @param trait The trait
     * @return True if and only if the gear item has the trait
     */
    public static boolean hasTrait(ItemStack gear, Trait trait) {
        if (GearHelper.isGear(gear)) {
            var list = GearData.getProperties(gear).getOrDefault(GearProperties.TRAITS, TraitListPropertyValue.empty());
            for (var traitInstance : list.value()) {
                if (traitInstance.getTrait() == trait) {
                    return true;
                }
            }
        }

        return false;
    }

    public static int getHighestLevelEitherHand(Player player, DataResource<Trait> trait) {
        ItemStack main = player.getMainHandItem();
        ItemStack off = player.getOffhandItem();
        return Math.max(getTraitLevel(main, trait), getTraitLevel(off, trait));
    }

    public static int getHighestLevelArmor(Player player, DataResource<Trait> trait) {
        int max = 0;
        for (ItemStack stack : player.getInventory().armor) {
            max = Math.max(max, getTraitLevel(stack, trait));
        }
        return max;
    }

    public static int getHighestLevelCurio(LivingEntity entity, DataResource<Trait> trait) {
        if (ModList.get().isLoaded(Const.CURIOS)) {
            return CuriosCompat.getHighestTraitLevel(entity, trait);
        }
        return 0;
    }

    public static int getHighestLevelArmorOrCurio(Player player, DataResource<Trait> trait) {
        return Math.max(getHighestLevelArmor(player, trait), getHighestLevelCurio(player, trait));
    }

    public static boolean hasTraitEitherHand(Player player, DataResource<Trait> trait) {
        ItemStack main = player.getMainHandItem();
        ItemStack off = player.getOffhandItem();
        return hasTrait(main, trait) || hasTrait(off, trait);
    }

    public static boolean hasTraitArmor(Player player, DataResource<Trait> trait) {
        for (ItemStack stack : player.getInventory().armor) {
            if (hasTrait(stack, trait)) {
                return true;
            }
        }
        return false;
    }

    public static List<TraitInstance> getTraits(ItemStack gear) {
        if (!GearHelper.isGear(gear)) return Collections.emptyList();

        var properties = GearData.getProperties(gear);
        var traitList = properties.get(GearProperties.TRAITS);
        if (traitList != null) {
            return traitList.value();
        }
        return Collections.emptyList();
    }

    /**
     * Gets a Map of Traits and levels from the parts, used to calculate trait levels and should not
     * be used in most cases. Consider using {@link #getTraitLevel(ItemStack, DataResource)} when
     * appropriate.
     *
     * @param gear     The item
     * @param gearType The gear type
     * @param parts    The list of all parts used in constructing the gear.
     * @return A Map of Traits to their levels
     */
    public static Map<Trait, Integer> getTraitsFromParts(ItemStack gear, GearType gearType, PartList parts) {
        if (parts.isEmpty() || (!gear.isEmpty() && GearHelper.isBroken(gear)))
            return ImmutableMap.of();

        Map<Trait, Integer> result = new LinkedHashMap<>();

        for (PartInstance part : parts) {
            PartGearKey key = PartGearKey.of(gearType, part);
            for (TraitInstance inst : part.getTraits(key)) {
                if (inst.conditionsMatch(key, gear, parts)) {
                    Trait trait = inst.getTrait();
                    // Get the highest value in any part
                    result.merge(trait, inst.getLevel(), Integer::max);
                }
            }
        }

        Trait[] keys = result.keySet().toArray(new Trait[0]);

        cancelTraits(result, keys);
        NeoForge.EVENT_BUS.post(new GetTraitsEvent(gear, parts, result));
        return result;
    }

    public static List<TraitInstance> getTraitsFromComponents(List<? extends GearComponentInstance<?>> components, PartGearKey partKey, ItemStack gear) {
        if (components.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Trait, Integer> map = new LinkedHashMap<>();
        Map<Trait, Integer> countMatsWithTrait = new HashMap<>();

        for (GearComponentInstance<?> comp : components) {
            for (TraitInstance inst : comp.getTraits(partKey)) {
                if (inst.conditionsMatch(partKey, gear, components)) {
                    map.merge(inst.getTrait(), inst.getLevel(), Integer::sum);
                    countMatsWithTrait.merge(inst.getTrait(), 1, Integer::sum);
                }
            }
        }

        Trait[] keys = map.keySet().toArray(new Trait[0]);

        for (Trait trait : keys) {
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

    private static void cancelTraits(Map<Trait, Integer> mapToModify, Trait[] keys) {
        /*for (int i = 0; i < keys.length; ++i) {
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
        }*/
    }

    static void tickTraits(Level world, @Nullable Player player, ItemStack gear, boolean isEquipped) {
        var traits = GearData.getProperties(gear).get(GearProperties.TRAITS);
        if (traits == null) return;

        for (var trait : traits.value()) {
            trait.getTrait().onUpdate(new TraitActionContext(player, trait, gear), isEquipped);
        }
    }
}
