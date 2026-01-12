package net.silentchaos512.gear.util;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.neoforged.fml.ModList;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearItem;
import net.silentchaos512.gear.api.property.TraitListPropertyValue;
import net.silentchaos512.gear.api.traits.*;
import net.silentchaos512.gear.api.util.DataResource;
import net.silentchaos512.gear.compat.curios.CuriosCompat;
import net.silentchaos512.gear.core.component.GearPropertiesData;
import net.silentchaos512.gear.gear.trait.Trait;
import net.silentchaos512.gear.setup.gear.GearProperties;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

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
     * @param gear       The {@link GearItem} affected
     * @param inputValue The base value to have the traits act on.
     * @param action     The specific action to apply to each trait. This is {@code (trait, level,
     *                   value) -> modifiedInputValue}, where 'value' is the currently calculated
     *                   result.
     * @return The {@code inputValue} modified by traits.
     */
    public static <T> T activateTraits(ItemStack gear, final T inputValue, TraitFunction<T> action) {
        if (!GearHelper.isGear(gear)) {
            SilentGear.LOGGER.error("Called activateTraits on non-gear item, {}", gear);
            SilentGear.LOGGER.catching(new IllegalArgumentException());
            return inputValue;
        }

        var traitListProperty = GearData.getProperties(gear).get(GearProperties.TRAITS.get());
        if (traitListProperty == null) return inputValue;

        var traits = traitListProperty.value();
        T value = inputValue;

        for (TraitInstance trait : traits) {
            value = action.apply(trait, value);
        }

        return value;
    }

    /**
     * Gets the level of the trait on the gear, or zero if it does not have the trait. Similar to
     * {@link #activateTraits(ItemStack, Object, TraitFunction)}, this pulls the traits straight from
     * NBT to minimize object creation.
     *
     * @param gear  The {@link GearItem}
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
            return hasTrait(GearData.getProperties(gear), trait);
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
            return hasTrait(GearData.getProperties(gear), trait);
        }

        return false;
    }

    public static boolean hasTrait(GearPropertiesData properties, DataResource<Trait> trait) {
        var list = properties.getOrDefault(GearProperties.TRAITS, TraitListPropertyValue.empty());
        for (var traitInstance : list.value()) {
            if (traitInstance.getTrait() == trait.get()) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasTrait(GearPropertiesData properties, Trait trait) {
        var list = properties.getOrDefault(GearProperties.TRAITS, TraitListPropertyValue.empty());
        for (var traitInstance : list.value()) {
            if (traitInstance.getTrait() == trait) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasTraitEffect(ItemStack gear, TraitEffectType<?> traitEffectType) {
        if (!GearHelper.isGear(gear)) return false;

        var traitList = GearData.getProperties(gear)
                .getOrDefault(GearProperties.TRAITS, TraitListPropertyValue.empty())
                .value();
        for (TraitInstance traitInstance : traitList) {
            for (TraitEffect effect : traitInstance.getTrait().getEffects()) {
                if (effect.type() == traitEffectType) {
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
        for (EquipmentSlot equipmentSlot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
            ItemStack stack = player.getItemBySlot(equipmentSlot);
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
        for (EquipmentSlot equipmentSlot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
            ItemStack stack = player.getItemBySlot(equipmentSlot);
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

    public static void addAttributeModifiersFromTraits(ItemStack gear, BiConsumer<Holder<Attribute>, AttributeModifier> adder) {
        ItemAttributeModifiers.Builder traitAttributesBuilder = ItemAttributeModifiers.builder();
        for (TraitInstance trait : TraitHelper.getTraits(gear)) {
            var context = new TraitActionContext(null, trait, gear);
            trait.getTrait().onGetAttributeModifiers(context, traitAttributesBuilder);
        }
        for (ItemAttributeModifiers.Entry modifier : traitAttributesBuilder.build().modifiers()) {
            adder.accept(modifier.attribute(), modifier.modifier());
        }
    }

    public static void cancelTraits(Map<Trait, Integer> mapToModify, Trait[] keys) {
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
        var traits = GearData.getProperties(gear, player).get(GearProperties.TRAITS);
        if (traits == null) return;

        for (var trait : traits.value()) {
            trait.getTrait().onUpdate(new TraitActionContext(player, trait, gear), isEquipped);
        }
    }
}
