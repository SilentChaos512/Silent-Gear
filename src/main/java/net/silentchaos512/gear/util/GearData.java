package net.silentchaos512.gear.util;

import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.silentchaos512.gear.Config;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearItem;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.part.GearPart;
import net.silentchaos512.gear.api.part.PartList;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.property.*;
import net.silentchaos512.gear.api.traits.TraitActionContext;
import net.silentchaos512.gear.api.traits.TraitInstance;
import net.silentchaos512.gear.api.util.DataResource;
import net.silentchaos512.gear.api.util.PropertyKey;
import net.silentchaos512.gear.compat.curios.CuriosCompat;
import net.silentchaos512.gear.core.component.GearConstructionData;
import net.silentchaos512.gear.core.component.GearPropertiesData;
import net.silentchaos512.gear.gear.part.PartInstance;
import net.silentchaos512.gear.setup.SgDataComponents;
import net.silentchaos512.gear.setup.SgRegistries;
import net.silentchaos512.gear.setup.gear.GearProperties;
import net.silentchaos512.lib.util.NameUtils;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

public final class GearData {
    private GearData() {
        throw new IllegalAccessError("Utility class");
    }

    public static GearPropertiesData getProperties(ItemInstance instance) {
        return getProperties(instance, null);
    }

    public static GearPropertiesData getProperties(ItemInstance instance, @Nullable Player player) {
        var data = instance.get(SgDataComponents.GEAR_PROPERTIES);
        if (data != null) {
            return data;
        }
        if (instance instanceof ItemStack stack) {
            recalculateGearData(stack, null);
        }
        return instance.getOrDefault(SgDataComponents.GEAR_PROPERTIES, GearPropertiesData.EMPTY);
    }

    /**
     * Gets the gear construction data component. If the component is not present, this instead
     * returns a default object to help eliminate frequent null checks.
     *
     * @param instance The gear item
     * @return The gear construction data component, or a new, empty component
     */
    public static GearConstructionData getConstruction(ItemInstance instance) {
        if (!GearHelper.isGear(instance)) {
            throw new IllegalArgumentException("Not a gear item: " + instance);
        }
        return instance.getOrDefault(SgDataComponents.GEAR_CONSTRUCTION, GearConstructionData.EMPTY);
    }

    /**
     * Recalculate gear properties and set up the properties data component. This should be
     * called ANY TIME an item is modified!
     *
     * @param gear   The gear item
     * @param player The player who has the item. Can be null if no player can be obtained. You can
     *               use {@link net.neoforged.neoforge.common.CommonHooks#getCraftingPlayer} the get the
     *               player during crafting.
     */
    public static void recalculateGearData(ItemStack gear, @Nullable Player player) {
        var gearConstructionData = gear.getOrDefault(SgDataComponents.GEAR_CONSTRUCTION, GearConstructionData.EMPTY);
        try {
            var gearType = GearHelper.getType(gear);
            tryRecalculateGearData(gear, player, gearType, gearConstructionData);
        } catch (Throwable ex) {
            CrashReport report = CrashReport.forThrowable(ex, "Failed to recalculate gear item properties");

            CrashReportCategory itemCategory = report.addCategory("Gear Item");
            itemCategory.setDetail("Name", gear.getHoverName().getString() + " (" + NameUtils.fromItem(gear) + ")");
            itemCategory.setDetail("Data", gearConstructionData);

            throw new ReportedException(report);
        }
    }

    private static void tryRecalculateGearData(ItemStack gear, @Nullable Player player, GearType gearType, GearConstructionData gearConstructionData) {
        final PartList parts = gearConstructionData.parts();
        if (parts.isEmpty() || parts.getMains().isEmpty()) {
            SilentGear.LOGGER.debug("Not recalculating stats for {}", getPlayersItemNameText(gear, player));
        }

        @Nullable var oldProperties = gear.get(SgDataComponents.GEAR_PROPERTIES);
        ComputeContext.Gear context = ComputeContext.gear(gear, parts);

        onRecalculatePre(gear, player, oldProperties, gearConstructionData);

        // Calculate base values, then bonuses from traits and such, then the final values!
        // All of these are stored for tooltip purposes
        // First, calculate base properties (first pass, creates the traits list and everything)
        var baseProperties = calculateBaseProperties(context, player, gearType, gearConstructionData);
        // Second, calculate bonus modifiers provided by traits
        var bonusValues = calculateBonusProperties(context, player, gearType, baseProperties);
        // Finally, combine the base and bonus modifiers into the final property values
        var finalProperties = calculateFinalProperties(context, player, gearType, baseProperties, bonusValues);
        gear.set(SgDataComponents.GEAR_PROPERTIES, finalProperties);

        printStatsForDebugging(gear, oldProperties, baseProperties, bonusValues, finalProperties);

        onRecalculatePost(gear, player, finalProperties);
    }

    private static void onRecalculatePre(ItemStack gear, @Nullable Player player, @Nullable GearPropertiesData oldProperties, GearConstructionData gearConstructionData) {
        // Set item name
        Component itemName = GearHelper.getItemName(gear, gearConstructionData);
        if (itemName != null) {
            gear.set(DataComponents.ITEM_NAME, itemName);
        }

        if (oldProperties == null) return;

        // Remove data components that need to be completely redone
        gear.remove(DataComponents.ATTRIBUTE_MODIFIERS);
        gear.remove(SgDataComponents.TRAIT_ENCHANTMENTS);

        if (gear.getItem() instanceof GearItem gearItem) {
            gearItem.onRecalculatePre(gear, player, oldProperties, gearConstructionData);
        }

        // Let traits do their thing
        for (var trait : oldProperties.getTraits()) {
            if (trait.isValid()) {
                trait.getTrait().onRecalculatePre(gear, trait.getLevel());
            }
        }
    }

    private static void onRecalculatePost(ItemStack gear, @Nullable Player player, GearPropertiesData finalProperties) {
        // Set other data components
        if (gear.getItem() instanceof GearItem gearItem) {
            gearItem.onRecalculatePost(gear, player, finalProperties);
        }

        setGearAttributeModifiers(gear, finalProperties);
        setGearDataComponentsFromProperties(gear, finalProperties);
        modifyEnchantmentData(gear, player);

        // TODO: Add trait-added enchantments

        // Let traits do their thing
        for (var trait : finalProperties.getTraits()) {
            if (trait.isValid()) {
                trait.getTrait().onRecalculatePost(gear, trait.getLevel());
            }
        }
    }

    private static void setGearAttributeModifiers(ItemStack gear, GearPropertiesData finalProperties) {
        ItemAttributeModifiers.Builder attributesBuilder = ItemAttributeModifiers.builder();
        // Let items handle base modifiers
        if (gear.getItem() instanceof GearItem gearItem) {
            gearItem.buildAttributes(gear, attributesBuilder);
        }
        // Some properties append their own attribute modifiers
        for (GearProperty<?, ? extends GearPropertyValue<?>> property : finalProperties.keySet()) {
            addAttributesForProperty(gear, attributesBuilder, finalProperties, property);
        }
        // Let traits add modifiers
        for (TraitInstance inst : TraitHelper.getTraits(gear)) {
            var context = new TraitActionContext(null, inst, gear);
            inst.getTrait().onGetAttributeModifiers(context, attributesBuilder);
        }
        gear.set(DataComponents.ATTRIBUTE_MODIFIERS, attributesBuilder.build());
    }

    private static void setGearDataComponentsFromProperties(ItemStack gear, GearPropertiesData finalProperties) {
        for (GearProperty<?, ? extends GearPropertyValue<?>> property : finalProperties.keySet()) {
            addDataComponentsForProperty(gear, finalProperties, property);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T, V extends GearPropertyValue<T>, P extends GearProperty<T, V>> void addAttributesForProperty(
            ItemStack stack,
            ItemAttributeModifiers.Builder builder,
            GearPropertiesData propertiesData,
            GearProperty<?, ?> propertyIn
    ) {
        // Must cast the property into its true type to call the addAttributes method
        P property = (P) propertyIn;
        @Nullable V valueInstance = propertiesData.get(property);
        if (valueInstance != null) {
            T value = valueInstance.value();
            property.addAttributes(stack, value, builder);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T, V extends GearPropertyValue<T>, P extends GearProperty<T, V>> void addDataComponentsForProperty(
            ItemStack stack,
            GearPropertiesData propertiesData,
            GearProperty<?, ?> propertyIn
    ) {
        // Must cast the property into its true type to call the addDataComponents method
        P property = (P) propertyIn;
        @Nullable V valueInstance = propertiesData.get(property);
        if (valueInstance != null) {
            T value = valueInstance.value();
            property.addDataComponents(stack, value);
        }
    }

    private static GearPropertiesData calculateBaseProperties(ComputeContext.Gear context, @Nullable Player player, GearType gearType, GearConstructionData gearConstructionData) {
        // Get all property modifiers from all parts and item class modifiers
        final PartList parts = gearConstructionData.parts();
        final GearPropertyMap propertyMods = parts.getPropertyModifiersFromParts(gearType);

        final Map<GearProperty<?, ?>, GearPropertyValue<?>> finalBaseValues = new LinkedHashMap<>();

        for (var property : gearType.getRelevantProperties()) {
            if (property.isForMaterialsOnly()) {
                // No parts should return material-only property values, but this safety check makes sure they don't end
                // up on gear items, just in case!
                continue;
            }
            var key = PropertyKey.of(property, gearType);
            Collection<GearPropertyValue<?>> modifiers = propertyMods.get(key);
            GearType statGearType = propertyMods.getMostSpecificKey(key).gearType();

            final GearPropertyValue<?> value = property.computeUncheckedForGear(context, gearType, statGearType, modifiers, parts);
            finalBaseValues.put(property, value);
        }

        return new GearPropertiesData(finalBaseValues);
    }

    private static GearPropertyMap calculateBonusProperties(ComputeContext.Gear context, @Nullable Player player, GearType gearType, GearPropertiesData baseProperties) {
        var bonusProperties = new GearPropertyMap();

        List<TraitInstance> traits = baseProperties.getOrDefault(GearProperties.TRAITS, TraitListPropertyValue.empty()).value();

        final int damageValue = context.gear().getOrDefault(DataComponents.DAMAGE, 0); // Cannot use gear#getDamageValue (infinite recursion)
        final int baseDurability = gearType.getBaseDurability(baseProperties); // Cannot use gear#getMaxDamage (infinite recursion)
        final float damageRatio = Mth.clamp((float) damageValue / baseDurability, 0f, 1f);

        for (var property : SgRegistries.GEAR_PROPERTY) {
            if (property != GearProperties.TRAITS && baseProperties.contains(property)) {
                var key = PropertyKey.of(property, gearType);

                // Trait configBonus modifiers
                for (TraitInstance trait : traits) {
                    if (trait.isValid()) {
                        GearPropertyValue<?> baseValue = baseProperties.get(property);
                        assert baseValue != null;
                        bonusProperties.putAll(key, trait.getTrait().getBonusProperties(trait.getLevel(), player, property, baseValue, damageRatio));
                    }
                }

                // Config global property modifiers
                var configBonus = Config.Common.getPropertyBonusMultiplier(property);
                if (configBonus != null) {
                    bonusProperties.put(key, configBonus);
                }
            }
        }

        return bonusProperties;
    }

    private static GearPropertiesData calculateFinalProperties(ComputeContext.Gear context, @Nullable Player player, GearType gearType, GearPropertiesData baseProperties, GearPropertyMap bonusProperties) {
        GearPropertyMap combinedMods = new GearPropertyMap();
        Map<GearProperty<?, ?>, GearPropertyValue<?>> finalValues = new LinkedHashMap<>();

        for (var property : SgRegistries.GEAR_PROPERTY) {
            if (baseProperties.contains(property)) {
                var key = PropertyKey.of(property, gearType);
                combinedMods.put(key, baseProperties.get(property));
                combinedMods.putAll(key, bonusProperties.get(key));
                finalValues.put(property, property.computeUnchecked(context, true, gearType, gearType, combinedMods.get(key)));
            }
        }

        return new GearPropertiesData(finalValues);
    }

    private static void modifyEnchantmentData(ItemStack gear, @Nullable Player player) {
        final var playersItemText = getPlayersItemNameText(gear, player);

        if (gear.isEnchanted() && Config.Common.forceRemoveEnchantments.get()) {
            SilentGear.LOGGER.debug("Forcibly removing all enchantments from {} as per config settings", playersItemText);
            gear.set(DataComponents.ENCHANTMENTS, null);
        }
    }

    private static String getPlayersItemNameText(ItemStack gear, @org.jetbrains.annotations.Nullable Player player) {
        final String playerName = player != null ? player.getScoreboardName() : "somebody";
        return String.format("%s's %s", playerName, gear.getHoverName().getString());
    }

    private static void printStatsForDebugging(
            ItemStack stack,
            @Nullable GearPropertiesData oldProperties,
            GearPropertiesData baseProperties,
            GearPropertyMap bonusValues,
            GearPropertiesData newProperties
    ) {
        // Prints stats that have changed for debugging purposes
        if (oldProperties != null && Config.Common.isLoaded() && Config.Common.propertiesDebugLogging.get()) {
            SilentGear.LOGGER.debug("{}: properties updated", stack.getDisplayName().getString());

            GearType gearType = GearHelper.getType(stack);

            for (var property : SgRegistries.GEAR_PROPERTY) {
                var oldValue = oldProperties.get(property);
                var newValue = newProperties.get(property);
                SilentGear.LOGGER.debug(" - {}: {} -> {} -- base: {}, bonuses: [{}]",
                        property.getDisplayName().getString(),
                        oldValue,
                        newValue,
                        baseProperties.get(property),
                        bonusValues.get(PropertyKey.of(property, gearType))
                );
            }
        }
    }

    //region Part getters and checks

    /**
     * Gets the first part in the construction parts list that is of the given type.
     *
     * @param instance The gear item
     * @param type  The part type
     * @return The first part of the given type, or null if there is none
     */
    @Nullable
    public static PartInstance getPartOfType(ItemInstance instance, PartType type) {
        var data = instance.get(SgDataComponents.GEAR_CONSTRUCTION);
        if (data == null) return null;

        for (PartInstance part : data.parts()) {
            if (part.getType() == type) {
                return part;
            }
        }

        return null;
    }

    /**
     * Check if the gear item has at least one part of the given type.
     *
     * @param instance The gear item
     * @param type  The part type
     * @return True if and only if the construction parts include a part of the given type
     */
    public static boolean hasPartOfType(ItemInstance instance, PartType type) {
        var data = instance.get(SgDataComponents.GEAR_CONSTRUCTION);
        if (data == null) return false;

        for (PartInstance part : data.parts()) {
            if (part.getType() == type) {
                return true;
            }
        }

        return false;
    }

    /**
     * Add an upgrade part to the gear item. Depending on the upgrade, this may replace an existing
     * part.
     *
     * @param gear The gear item
     * @param part The upgrade part
     */
    public static void addUpgradePart(ItemStack gear, PartInstance part) {
        if (!GearHelper.isGear(gear) || !part.isValid()) return;

        List<PartInstance> parts = new ArrayList<>(getConstruction(gear).parts());

        // Make sure the upgrade is valid for the gear type
        if (!part.get().canAddToGear(gear, part))
            return;
        // Only one allowed of this type? Remove existing if needed.
        if (part.get().replacesExistingInPosition(part)) {
            parts.removeIf(p -> p.getType() == part.getType());
        }

        // Allow the part to make additional changes if needed
        part.onAddToGear(gear);

        // Other upgrades allow no exact duplicates, but any number of total upgrades
        for (PartInstance partInList : parts) {
            if (partInList.get() == part.get()) {
                return;
            }
        }

        parts.add(part);
        writeConstructionParts(gear, parts);
    }

    public static boolean hasPart(ItemInstance instance, PartType partType, Predicate<PartInstance> predicate) {
        for (PartInstance part : getConstruction(instance).parts()) {
            if (part.getType().equals(partType) && predicate.test(part)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determine if the gear has the specified part. This scans the construction NBT directly for
     * speed, no part data list is created. Compares part registry names only.
     *
     * @param instance The gear item
     * @param part The part to check for
     * @return True if the item has the part in its construction, false otherwise
     */
    public static boolean hasPart(ItemInstance instance, GearPart part) {
        if (checkNonGearItem(instance, "hasPart")) return false;

        for (var partInstance : getConstruction(instance).parts()) {
            if (partInstance.isValid() && partInstance.get() == part) {
                return true;
            }
        }

        return false;
    }

    /**
     * Determine if the gear has the specified part. This scans the construction NBT directly for
     * speed, no part data list is created. Compares part registry names only.
     *
     * @param instance The gear item
     * @param part The part to check for
     * @return True if the item has the part in its construction, false otherwise
     */
    public static boolean hasPart(ItemInstance instance, DataResource<GearPart> part) {
        if (checkNonGearItem(instance, "hasPart")) return false;

        var data = instance.getOrDefault(SgDataComponents.GEAR_CONSTRUCTION, GearConstructionData.EMPTY);
        for (PartInstance partInConstruction : data.parts()) {
            if (partInConstruction.is(part)) {
                return true;
            }
        }

        return false;
    }

    public static void addOrReplacePart(ItemStack gear, PartInstance part) {
        PartType partType = part.getType();
        PartList parts = getConstruction(gear).parts().mutableCopy();
        List<PartInstance> partsOfType = parts.getPartsOfType(partType);
        PartInstance removedPart = null;

        if (!partsOfType.isEmpty() && partsOfType.size() >= partType.maxPerItem()) {
            removedPart = partsOfType.getFirst();
            parts.remove(removedPart);
            removedPart.onRemoveFromGear(gear);
        }

        parts.add(part);
        writeConstructionParts(gear, parts);
    }

    public static void addPart(ItemStack gear, PartInstance part) {
        PartList parts = getConstruction(gear).parts().mutableCopy();
        parts.add(part);
        writeConstructionParts(gear, parts);
        part.onAddToGear(gear);
    }

    public static boolean removeFirstPartOfType(ItemStack gear, PartType type) {
        PartList parts = getConstruction(gear).parts();
        List<PartInstance> partsMutable = new ArrayList<>(parts);
        List<PartInstance> partsOfType = new ArrayList<>(parts.getPartsOfType(type));

        if (!partsOfType.isEmpty()) {
            PartInstance removed = partsOfType.removeFirst();
            partsMutable.remove(removed);
            writeConstructionParts(gear, partsMutable);
            removed.onRemoveFromGear(gear);
            return true;
        }

        return false;
    }

    public static void writeConstructionParts(ItemStack gear, Collection<PartInstance> parts) {
        if (checkNonGearItem(gear, "writeConstructionParts")) return;

        var data = gear.get(SgDataComponents.GEAR_CONSTRUCTION);
        var newData = new GearConstructionData(
                PartList.immutable(parts),
                parts.isEmpty(), // clear example flag, assuming the item actually has any parts
                data != null ? data.brokenCount() : 0,
                data != null ? data.repairedCount() : 0
        );
        gear.set(SgDataComponents.GEAR_CONSTRUCTION, newData);
    }

    //endregion

    public static boolean isExampleGear(ItemInstance instance) {
        var data = instance.get(SgDataComponents.GEAR_CONSTRUCTION);
        return data != null && data.isExample();
    }

    public static int getBrokenCount(ItemInstance instance) {
        var data = instance.get(SgDataComponents.GEAR_CONSTRUCTION);
        return data != null ? data.brokenCount() : 0;
    }

    static void incrementBrokenCount(ItemStack stack) {
        var data = stack.get(SgDataComponents.GEAR_CONSTRUCTION);
        if (data != null) {
            var newData = new GearConstructionData(data.parts(), data.isExample(), data.brokenCount() + 1, data.repairedCount());
            stack.set(SgDataComponents.GEAR_CONSTRUCTION, newData);
        }
    }

    public static int getRepairedCount(ItemInstance instance) {
        var data = instance.get(SgDataComponents.GEAR_CONSTRUCTION);
        return data != null ? data.repairedCount() : 0;
    }

    public static void incrementRepairedCount(ItemStack stack, int amount) {
        var data = stack.get(SgDataComponents.GEAR_CONSTRUCTION);
        if (data != null) {
            var newData = new GearConstructionData(data.parts(), data.isExample(), data.brokenCount(), data.repairedCount() + amount);
            stack.set(SgDataComponents.GEAR_CONSTRUCTION, newData);
        }
    }

    private static boolean checkNonGearItem(ItemInstance instance, String methodName) {
        if (GearHelper.isGear(instance)) return false;

        SilentGear.LOGGER.error("Called {} on non-gear item, {}", methodName, instance);
        SilentGear.LOGGER.catching(new IllegalArgumentException());
        return true;
    }

    public static void setExampleTag(ItemStack result, boolean value) {
        result.set(SgDataComponents.GEAR_IS_EXAMPLE, value);
    }

    public static String getColorCacheKey(ItemStack stack, int animationFrame) {
        return String.format("%d.%d", stack.getComponents().hashCode(), animationFrame);
    }

    @EventBusSubscriber(modid = SilentGear.MOD_ID)
    public static final class EventHandler {
        private EventHandler() {
        }

        @SubscribeEvent
        public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
            Player player = event.getEntity();
            for (ItemStack stack : player.getInventory()) {
                if (stack.getItem() instanceof GearItem) {
                    recalculateGearData(stack, player);
                }
            }
            if (ModList.get().isLoaded(Const.CURIOS)) {
                CuriosCompat.getEquippedCurios(player).forEach(s -> recalculateGearData(s, player));
            }
        }
    }
}
