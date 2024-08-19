package net.silentchaos512.gear.util;

import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.material.TextureType;
import net.silentchaos512.gear.api.part.GearPart;
import net.silentchaos512.gear.api.part.PartList;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.property.GearPropertyMap;
import net.silentchaos512.gear.api.stats.*;
import net.silentchaos512.gear.api.traits.TraitActionContext;
import net.silentchaos512.gear.api.util.DataResource;
import net.silentchaos512.gear.compat.curios.CuriosCompat;
import net.silentchaos512.gear.config.Config;
import net.silentchaos512.gear.core.component.GearConstructionData;
import net.silentchaos512.gear.core.component.GearPropertiesData;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.gear.part.PartInstance;
import net.silentchaos512.gear.gear.trait.Trait;
import net.silentchaos512.gear.setup.SgDataComponents;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.lib.collection.StackList;
import net.silentchaos512.lib.util.NameUtils;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

/**
 * Includes many methods for getting values from the NBT of gear items. Please make sure all
 * ItemStacks passed in are of {@link ICoreItem}s! Calling these methods with invalid items should
 * not crash the game, but will spam the log with stack traces and will return invalid values.
 */
public final class GearData {
    private GearData() {
        throw new IllegalAccessError("Utility class");
    }

    public static GearPropertiesData getProperties(ItemStack gear) {
        var data = gear.get(SgDataComponents.GEAR_PROPERTIES);
        if (data != null) {
            return data;
        }
        // FIXME
        GearPropertiesData newData = recalculateStats(gear, null);
        gear.set(SgDataComponents.GEAR_PROPERTIES, newData);
        return newData;
    }

    /**
     * Gets the gear construction data component. If the component is not present, this instead
     * returns a default object to help eliminate frequent null checks.
     *
     * @param gear The gear item
     * @return The gear construction data component, or a new, empty component
     */
    public static GearConstructionData getConstruction(ItemStack gear) {
        var data = gear.get(SgDataComponents.GEAR_CONSTRUCTION);
        if (data == null) {
            throw new IllegalArgumentException("Not a gear item: " + gear);
        }
        return data;
    }

    /**
     * Recalculate gear stats and setup NBT. This should be called ANY TIME an item is modified!
     *
     * @param gear   The gear item
     * @param player The player who has the item. Can be null if no player can be obtained. You can
     *               use {@link net.neoforged.neoforge.common.CommonHooks#getCraftingPlayer} the get the
     *               player during crafting.
     */
    public static void recalculateStats(ItemStack gear, @Nullable Player player) {
        try {
            tryRecalculateStats(gear, player);
        } catch (Throwable ex) {
            CrashReport report = CrashReport.forThrowable(ex, "Failed to recalculate gear properties");

            CrashReportCategory itemCategory = report.addCategory("Gear Item");
            itemCategory.setDetail("Name", gear.getHoverName().getString() + " (" + NameUtils.fromItem(gear) + ")");
            var construction = gear.get(SgDataComponents.GEAR_CONSTRUCTION);
            itemCategory.setDetail("Data", construction != null ? construction : "null");

            throw new ReportedException(report);
        }
    }

    @SuppressWarnings({"OverlyLongMethod", "OverlyComplexMethod"})
    private static void tryRecalculateStats(ItemStack gear, @Nullable Player player) {
        if (checkNonGearItem(gear, "recalculateStats")) return;

        // TODO: What's the point of this? Traits not calculated yet, right?
        TraitHelper.activateTraits(gear, 0f, (trait, value) -> {
            trait.getTrait().onRecalculatePre(new TraitActionContext(player, trait, gear));
            return 0f;
        });

        final String playerName = player != null ? player.getScoreboardName() : "somebody";
        final String playersItemText = String.format("%s's %s", playerName, gear.getHoverName().getString());

        GearConstructionData gearConstructionData = gear.get(SgDataComponents.GEAR_CONSTRUCTION);
        if (gearConstructionData == null) {
            SilentGear.LOGGER.error("{}: gear item has no gearConstructionData data?", playersItemText);
            return;
        }

        final PartList parts = gearConstructionData.parts();
        final ICoreItem item = (ICoreItem) gear.getItem();
        final boolean partsListValid = !parts.isEmpty() && !parts.getMains().isEmpty();

        if (partsListValid) {
            // We should recalculate the item's stats!
            SilentGear.LOGGER.debug("Recalculating for {}", playersItemText);

            Map<Trait, Integer> traits = TraitHelper.getTraitsFromParts(gear, item.getGearType(), parts);

            // Get all stat modifiers from all parts and item class modifiers
            GearPropertyMap stats = getStatModifiers(gear, item, parts);

            // For debugging
            GearPropertiesData oldGearProperties = getProperties(gear);

            // Cache traits in properties compound as well
            ListTag traitList = new ListTag();
            traits.forEach((trait, level) -> traitList.add(trait.write(level)));
            propertiesCompound.put("Traits", traitList);

            propertiesCompound.remove(NBT_SYNERGY);

            // Calculate and write stats
            int maxDamage = gear.getMaxDamage() > 0 ? gear.getMaxDamage() : 1;
            final float damageRatio = Mth.clamp((float) gear.getDamageValue() / maxDamage, 0f, 1f);
            CompoundTag statsCompound = new CompoundTag();
            for (ItemStat stat : ItemStats.allStatsOrderedExcluding(item.getExcludedStats(gear))) {
                StatGearKey key = StatGearKey.of(stat, item.getGearType());
                Collection<StatInstance> modifiers = stats.get(key);
                GearType statGearType = stats.getMostSpecificKey(key).getGearType();

                final float initialValue = stat.compute(stat.getBaseValue(), true, item.getGearType(), statGearType, modifiers);
                // Allow traits to modify stat
                final float withTraits = TraitHelper.activateTraits(gear, initialValue, (trait, val) -> {
                    TraitActionContext context = new TraitActionContext(player, trait, gear);
                    return trait.getTrait().onGetProperty(context, stat, val, damageRatio);
                });
                final float value = Config.Common.getStatWithMultiplier(stat, withTraits);
                if (!Mth.equal(value, 0f) || stats.containsKey(key)) {
                    ResourceLocation statId = Objects.requireNonNull(stat.getStatId());
                    propertiesCompound.remove(statId.getPath()); // Remove old keys
                    statsCompound.putFloat(statId.toString(), stat.clampValue(value));
                }
            }
            // Put missing relevant stats in the map to avoid recalculate stats packet spam
            for (ItemStat stat : item.getRelevantStats(gear)) {
                String statKey = stat.getStatId().toString();
                if (!statsCompound.contains(statKey)) {
                    statsCompound.putFloat(statKey, stat.getDefaultValue());
                }
            }
            propertiesCompound.put(NBT_STATS, statsCompound);

            if (player != null) {
                printStatsForDebugging(gear, stats, oldStatValues);
            }

            // Remove enchantments if mod is configured to. Must be done before traits add enchantments!
            if (gear.getOrCreateTag().contains("Enchantments") && Config.Common.forceRemoveEnchantments.get()) {
                SilentGear.LOGGER.debug("Forcibly removing all enchantments from {} as per config settings", playersItemText);
                gear.removeTagKey("Enchantments");
            }

            // Remove trait-added enchantments then let traits re-add them
            EnchantmentTrait.removeTraitEnchantments(gear);
            TraitHelper.activateTraits(gear, 0f, (trait, value) -> {
                trait.getTrait().onRecalculatePost(new TraitActionContext(player, trait, gear));
                return 0f;
            });
        } else {
            SilentGear.LOGGER.debug("Not recalculating stats for {}", playersItemText);
        }

        // Update rendering info even if we didn't update stats
        updateRenderingInfo(gear, parts);
    }

    private static void printStatsForDebugging(ItemStack stack, GearPropertyMap stats, @Nullable Map<ItemStat, Float> oldStats) {
        // Prints stats that have changed for debugging purposes
        if (oldStats != null && SilentGear.LOGGER.isDebugEnabled()) {
            GearType gearType = GearHelper.getType(stack);
            Map<ItemStat, Float> newStats = getCurrentStatsForDebugging(stack);
            assert newStats != null;

            for (ItemStat stat : stats.getPropertyTypes()) {
                float oldValue = oldStats.get(stat);
                float newValue = newStats.get(stat);
                float change = newValue - oldValue;
                SilentGear.LOGGER.debug(" - {}: {} -> {} ({}) - mods: [{}]",
                        stat.getDisplayName().getString(),
                        oldValue,
                        newValue,
                        change < 0 ? change : "+" + change,
                        GearPropertyMap.formatText(stats.getValues(stat, gearType), stat, 5).getString()
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
        var data = getConstruction(gear);
        var coatingOrMainPart = data.getCoatingOrMainPart();
        if (coatingOrMainPart == null || coatingOrMainPart.getMaterial().isEmpty()) {
            // Data packs may not be fully loaded yet, or something else has gone wrong
            return -1;
        }
        MaterialInstance mainMaterial = coatingOrMainPart.getMaterial().get();
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

    public static GearPropertyMap getStatModifiers(ItemStack stack, ICoreItem item, PartList parts) {
        GearType gearType = item.getGearType();
        GearPropertyMap stats = new GearPropertyMap();

        for (ItemStat stat : ItemStats.allStatsOrderedExcluding(item.getExcludedStats(stack))) {
            StatGearKey itemKey = StatGearKey.of(stat, gearType);

            for (PartData part : parts) {
                for (StatInstance mod : part.getStatModifiers(itemKey, stack)) {
                    StatInstance modCopy = StatInstance.of(mod.getValue(), mod.getOp(), itemKey);
                    stats.put(modCopy.getKey(), modCopy);
                }
            }
        }

        return stats;
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

        String partId = part.getId().toString();
        return hasPart(gear, partId);
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

    @EventBusSubscriber(modid = SilentGear.MOD_ID)
    public static final class EventHandler {
        private EventHandler() { }

        @SubscribeEvent
        public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
            Player player = event.getEntity();
            StackList.from(player.getInventory())
                    .stream()
                    .filter(s -> s.getItem() instanceof ICoreItem)
                    .forEach(s -> recalculateStats(s, player));

            if (ModList.get().isLoaded(Const.CURIOS)) {
                CuriosCompat.getEquippedCurios(player).forEach(s -> recalculateStats(s, player));
            }
        }
    }
}
