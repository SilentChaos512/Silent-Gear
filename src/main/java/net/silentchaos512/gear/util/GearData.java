package net.silentchaos512.gear.util;

import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.silentchaos512.gear.Config;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.item.GearItem;
import net.silentchaos512.gear.api.material.TextureType;
import net.silentchaos512.gear.api.part.GearPart;
import net.silentchaos512.gear.api.part.PartList;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.property.GearProperty;
import net.silentchaos512.gear.api.property.GearPropertyMap;
import net.silentchaos512.gear.api.property.GearPropertyValue;
import net.silentchaos512.gear.api.property.TraitListPropertyValue;
import net.silentchaos512.gear.api.traits.TraitInstance;
import net.silentchaos512.gear.api.util.DataResource;
import net.silentchaos512.gear.api.util.PropertyKey;
import net.silentchaos512.gear.compat.curios.CuriosCompat;
import net.silentchaos512.gear.core.component.GearConstructionData;
import net.silentchaos512.gear.core.component.GearPropertiesData;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.gear.part.PartInstance;
import net.silentchaos512.gear.setup.SgDataComponents;
import net.silentchaos512.gear.setup.SgRegistries;
import net.silentchaos512.gear.setup.gear.GearProperties;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.lib.collection.StackList;
import net.silentchaos512.lib.util.NameUtils;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

public final class GearData {
    private GearData() {
        throw new IllegalAccessError("Utility class");
    }

    public static GearPropertiesData getProperties(ItemStack gear) {
        return getProperties(gear, null);
    }

    public static GearPropertiesData getProperties(ItemStack gear, @Nullable Player player) {
        var data = gear.get(SgDataComponents.GEAR_PROPERTIES);
        if (data != null) {
            return data;
        }
        recalculateGearData(gear, null);
        return gear.getOrDefault(SgDataComponents.GEAR_PROPERTIES, GearPropertiesData.EMPTY);
    }

    /**
     * Gets the gear construction data component. If the component is not present, this instead
     * returns a default object to help eliminate frequent null checks.
     *
     * @param gear The gear item
     * @return The gear construction data component, or a new, empty component
     */
    public static GearConstructionData getConstruction(ItemStack gear) {
        if (!(gear.getItem() instanceof GearItem)) {
            throw new IllegalArgumentException("Not a gear item: " + gear);
        }
        return gear.getOrDefault(SgDataComponents.GEAR_CONSTRUCTION, GearConstructionData.EMPTY);
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
        var gearConstructionData = gear.get(SgDataComponents.GEAR_CONSTRUCTION);
        try {
            var gearType = GearHelper.getType(gear);
            tryRecalculateGearData(gear, player, gearType, gearConstructionData);
        } catch (Throwable ex) {
            CrashReport report = CrashReport.forThrowable(ex, "Failed to recalculate gear item properties");

            CrashReportCategory itemCategory = report.addCategory("Gear Item");
            itemCategory.setDetail("Name", gear.getHoverName().getString() + " (" + NameUtils.fromItem(gear) + ")");
            itemCategory.setDetail("Data", gearConstructionData != null ? gearConstructionData : "null");

            throw new ReportedException(report);
        }
    }

    private static void tryRecalculateGearData(ItemStack gear, @Nullable Player player, GearType gearType, GearConstructionData gearConstructionData) {
        if (gearConstructionData == null) {
            SilentGear.LOGGER.error("{}: gear item has no GearConstructionData?", getPlayersItemNameText(gear, player));
            return;
        }

        final PartList parts = gearConstructionData.parts();
        if (parts.isEmpty() || parts.getMains().isEmpty()) {
            SilentGear.LOGGER.debug("Not recalculating stats for {}", getPlayersItemNameText(gear, player));
        }

        onRecalculatePre(gear, player);

        @Nullable var oldProperties = gear.get(SgDataComponents.GEAR_PROPERTIES);

        // Calculate base values, then bonuses from traits and such, then the final values!
        // All of these are stored for tooltip purposes
        var baseProperties = calculateBaseProperties(gear, player, gearType, gearConstructionData);
        gear.set(SgDataComponents.GEAR_BASE_PROPERTIES, baseProperties);
        var bonusValues = calculateBonusProperties(gear, player, gearType);
        gear.set(SgDataComponents.GEAR_BONUS_PROPERTIES, bonusValues);
        var finalProperties = calculateFinalProperties(gear, player, gearType);
        gear.set(SgDataComponents.GEAR_PROPERTIES, finalProperties);

        printStatsForDebugging(gear, oldProperties, baseProperties, bonusValues, finalProperties);

        onRecalculatePost(gear, player);
    }

    private static void onRecalculatePre(ItemStack gear, @Nullable Player player) {
        // TODO: Remove trait-added enchantments
    }

    private static void onRecalculatePost(ItemStack gear, @Nullable Player player) {
        // TODO: Add trait-added enchantments

        var modelIndex = calculateModelIndex(gear);
        gear.set(SgDataComponents.GEAR_MODEL_INDEX, modelIndex);
    }

    private static GearPropertiesData calculateBaseProperties(ItemStack gear, @Nullable Player player, GearType gearType, GearConstructionData gearConstructionData) {
        // Get all property modifiers from all parts and item class modifiers
        final PartList parts = gearConstructionData.parts();
        final GearPropertyMap propertyMods = parts.getPropertyModifiersFromParts(gearType);

        final Map<GearProperty<?, ?>, GearPropertyValue<?>> finalBaseValues = new LinkedHashMap<>();

        for (var property : SgRegistries.GEAR_PROPERTY) {
            var key = PropertyKey.of(property, gearType);
            Collection<GearPropertyValue<?>> modifiers = propertyMods.get(key);
            GearType statGearType = propertyMods.getMostSpecificKey(key).gearType();

            final GearPropertyValue<?> value = property.computeUnchecked(true, gearType, statGearType, modifiers);
            finalBaseValues.put(property, value);
        }

        return new GearPropertiesData(finalBaseValues);
    }

    private static GearPropertyMap calculateBonusProperties(ItemStack gear, @Nullable Player player, GearType gearType) {
        var baseProperties = gear.getOrDefault(SgDataComponents.GEAR_BASE_PROPERTIES, GearPropertiesData.EMPTY);
        var bonusProperties = new GearPropertyMap();

        List<TraitInstance> traits = baseProperties.getOrDefault(GearProperties.TRAITS, TraitListPropertyValue.empty()).value();

        final int maxDamage = gear.getMaxDamage() > 0 ? gear.getMaxDamage() : 1;
        final float damageRatio = Mth.clamp((float) gear.getDamageValue() / maxDamage, 0f, 1f);

        for (var property : SgRegistries.GEAR_PROPERTY) {
            if (property != GearProperties.TRAITS && baseProperties.contains(property)) {
                var key = PropertyKey.of(property, gearType);

                // Trait configBonus modifiers
                for (TraitInstance trait : traits) {
                    GearPropertyValue<?> baseValue = baseProperties.get(property);
                    assert baseValue != null;
                    bonusProperties.putAll(key, trait.getTrait().getBonusProperties(trait.getLevel(), player, property, baseValue, damageRatio));
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

    private static GearPropertiesData calculateFinalProperties(ItemStack gear, @Nullable Player player, GearType gearType) {
        var baseProperties = gear.getOrDefault(SgDataComponents.GEAR_BASE_PROPERTIES, GearPropertiesData.EMPTY);
        var bonusProperties = gear.getOrDefault(SgDataComponents.GEAR_BONUS_PROPERTIES, GearPropertyMap.EMPTY);

        GearPropertyMap combinedMods = new GearPropertyMap();
        Map<GearProperty<?, ?>, GearPropertyValue<?>> finalValues = new LinkedHashMap<>();

        for (var property : SgRegistries.GEAR_PROPERTY) {
            var key = PropertyKey.of(property, gearType);
            combinedMods.put(key, baseProperties.get(property));
            combinedMods.putAll(key, bonusProperties.get(key));
            finalValues.put(property, property.computeUnchecked(true, gearType, gearType, combinedMods.get(key)));
        }

        return new GearPropertiesData(finalValues);
    }

    private static void modifyEnchantmentData(ItemStack gear, @Nullable Player player) {
        final var playersItemText = getPlayersItemNameText(gear, player);

        if (gear.isEnchanted() && Config.Common.forceRemoveEnchantments.get()) {
            SilentGear.LOGGER.debug("Forcibly removing all enchantments from {} as per config settings", playersItemText);
            gear.set(DataComponents.ENCHANTMENTS, null);
        }

        // TODO: Remove enchantments added by enchantment traits, and let the traits re-add them later
        //EnchantmentTrait.removeTraitEnchantments(gear);
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
        if (oldProperties != null && SilentGear.LOGGER.isDebugEnabled()) {
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

    public static String getModelKey(ItemStack stack, int animationFrame) {
        var key = stack.get(SgDataComponents.GEAR_MODEL_KEY);
        if (key == null) return "null";
        return key + (animationFrame > 0 ? "_" + animationFrame : "");
    }

    private static String calculateModelKey(ItemStack stack, Collection<? extends PartInstance> parts) {
        StringBuilder s = new StringBuilder(SilentGear.shortenId(NameUtils.fromItem(stack)) + ":");

        for (PartInstance part : parts) {
            s.append(part.getModelKey()).append(',');
        }

        return s.toString();
    }

    private static int calculateModelIndex(ItemStack gear) {
        if (GearHelper.isBroken(gear)) {
            // Special broken gear model
            return 0;
        }

        var data = getConstruction(gear);
        var coatingOrMainPart = data.getCoatingOrMainPart();
        if (coatingOrMainPart == null || coatingOrMainPart.getPrimaryMaterial() == null) {
            // Data packs may not be fully loaded yet, or something else has gone wrong
            return -1;
        }
        MaterialInstance mainMaterial = coatingOrMainPart.getPrimaryMaterial();
        boolean highContrast = mainMaterial.getMainTextureType() == TextureType.HIGH_CONTRAST;

        int ret = highContrast ? 3 : 2;

        if (getPartOfType(gear, PartTypes.TIP.get()) != null) {
            ret |= 4;
        }
        if (getPartOfType(gear, PartTypes.GRIP.get()) != null) {
            ret |= 8;
        }

        return ret;
    }

    public static int getModelIndex(ItemStack stack) {
        return stack.getOrDefault(SgDataComponents.GEAR_MODEL_INDEX, 0);
    }

    //region Part getters and checks

    /**
     * Gets the first part in the construction parts list that is of the given type.
     *
     * @param stack The gear item
     * @param type  The part type
     * @return The first part of the given type, or null if there is none
     */
    @Nullable
    public static PartInstance getPartOfType(ItemStack stack, PartType type) {
        var data = stack.get(SgDataComponents.GEAR_CONSTRUCTION);
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
     * @param stack The gear item
     * @param type  The part type
     * @return True if and only if the construction parts include a part of the given type
     */
    public static boolean hasPartOfType(ItemStack stack, PartType type) {
        var data = stack.get(SgDataComponents.GEAR_CONSTRUCTION);
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
        if (!GearHelper.isGear(gear)) return;

        PartList parts = getConstruction(gear).parts();

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

    public static boolean hasPart(ItemStack gear, PartType partType, Predicate<PartInstance> predicate) {
        for (PartInstance partData : getConstruction(gear).parts()) {
            if (predicate.test(partData)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determine if the gear has the specified part. This scans the construction NBT directly for
     * speed, no part data list is created. Compares part registry names only.
     *
     * @param gear The gear item
     * @param part The part to check for
     * @return True if the item has the part in its construction, false otherwise
     */
    public static boolean hasPart(ItemStack gear, GearPart part) {
        if (checkNonGearItem(gear, "hasPart")) return false;

        for (var partInstance : getConstruction(gear).parts()) {
            if (partInstance.get() == part) {
                return true;
            }
        }

        return false;
    }

    /**
     * Determine if the gear has the specified part. This scans the construction NBT directly for
     * speed, no part data list is created. Compares part registry names only.
     *
     * @param gear The gear item
     * @param part The part to check for
     * @return True if the item has the part in its construction, false otherwise
     */
    public static boolean hasPart(ItemStack gear, DataResource<GearPart> part) {
        if (checkNonGearItem(gear, "hasPart")) return false;

        String partId = part.getId().toString();
        return hasPart(gear, partId);
    }

    private static boolean hasPart(ItemStack gear, String partId) {
        var data = gear.get(SgDataComponents.GEAR_CONSTRUCTION);
        if (data == null) return false;

        for (PartInstance part : data.parts()) {
            if (part.getId().toString().equalsIgnoreCase(partId)) {
                return true;
            }
        }

        return false;
    }

    public static void addOrReplacePart(ItemStack gear, PartInstance part) {
        PartType partType = part.getType();
        PartList parts = getConstruction(gear).parts();
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
        PartList parts = getConstruction(gear).parts();
        parts.add(part);
        writeConstructionParts(gear, parts);
        part.onAddToGear(gear);
    }

    public static boolean removeFirstPartOfType(ItemStack gear, PartType type) {
        PartList parts = getConstruction(gear).parts();
        List<PartInstance> partsOfType = new ArrayList<>(parts.getPartsOfType(type));

        if (!partsOfType.isEmpty()) {
            PartInstance removed = partsOfType.removeFirst();
            parts.remove(removed);
            writeConstructionParts(gear, parts);
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
                data != null && data.isExample(),
                data != null ? data.brokenCount() : 0,
                data != null ? data.repairedCount() : 0
        );
        gear.set(SgDataComponents.GEAR_CONSTRUCTION, newData);
    }

    //endregion

    public static boolean isExampleGear(ItemStack stack) {
        var data = stack.get(SgDataComponents.GEAR_CONSTRUCTION);
        return data != null && data.isExample();
    }

    public static int getBrokenCount(ItemStack stack) {
        var data = stack.get(SgDataComponents.GEAR_CONSTRUCTION);
        return data != null ? data.brokenCount() : 0;
    }

    static void incrementBrokenCount(ItemStack stack) {
        var data = stack.get(SgDataComponents.GEAR_CONSTRUCTION);
        if (data != null) {
            var newData = new GearConstructionData(data.parts(), data.isExample(), data.brokenCount() + 1, data.repairedCount());
            stack.set(SgDataComponents.GEAR_CONSTRUCTION, newData);
        }
    }

    public static int getRepairedCount(ItemStack stack) {
        var data = stack.get(SgDataComponents.GEAR_CONSTRUCTION);
        return data != null ? data.repairedCount() : 0;
    }

    public static void incrementRepairedCount(ItemStack stack, int amount) {
        var data = stack.get(SgDataComponents.GEAR_CONSTRUCTION);
        if (data != null) {
            var newData = new GearConstructionData(data.parts(), data.isExample(), data.brokenCount(), data.repairedCount() + 1);
            stack.set(SgDataComponents.GEAR_CONSTRUCTION, newData);
        }
    }

    private static boolean checkNonGearItem(ItemStack stack, String methodName) {
        if (GearHelper.isGear(stack)) return false;

        SilentGear.LOGGER.error("Called {} on non-gear item, {}", methodName, stack);
        SilentGear.LOGGER.catching(new IllegalArgumentException());
        return true;
    }

    public static void setExampleTag(ItemStack result, boolean value) {
        result.set(SgDataComponents.GEAR_IS_EXAMPLE, value);
    }

    @EventBusSubscriber(modid = SilentGear.MOD_ID)
    public static final class EventHandler {
        private EventHandler() {
        }

        @SubscribeEvent
        public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
            Player player = event.getEntity();
            StackList.from(player.getInventory())
                    .stream()
                    .filter(s -> s.getItem() instanceof GearItem)
                    .forEach(s -> recalculateGearData(s, player));

            if (ModList.get().isLoaded(Const.CURIOS)) {
                CuriosCompat.getEquippedCurios(player).forEach(s -> recalculateGearData(s, player));
            }
        }
    }
}
