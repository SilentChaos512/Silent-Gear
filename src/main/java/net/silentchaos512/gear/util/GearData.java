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
import net.silentchaos512.gear.api.material.IMaterialDisplay;
import net.silentchaos512.gear.api.material.IMaterialInstance;
import net.silentchaos512.gear.api.material.MaterialLayer;
import net.silentchaos512.gear.api.part.IGearPart;
import net.silentchaos512.gear.api.part.IPartData;
import net.silentchaos512.gear.api.part.PartDataList;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.stats.*;
import net.silentchaos512.gear.api.traits.ITrait;
import net.silentchaos512.gear.api.traits.TraitActionContext;
import net.silentchaos512.gear.api.util.DataResource;
import net.silentchaos512.gear.api.util.StatGearKey;
import net.silentchaos512.gear.compat.curios.CuriosCompat;
import net.silentchaos512.gear.config.Config;
import net.silentchaos512.gear.core.component.GearConstructionData;
import net.silentchaos512.gear.core.component.GearPropertiesData;
import net.silentchaos512.gear.gear.part.PartData;
import net.silentchaos512.gear.gear.trait.EnchantmentTrait;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.gear.setup.SgDataComponents;
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
        GearPropertiesData newData = recalculateStats(gear);
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
        return data != null ? data : new GearConstructionData(PartDataList.empty(), false, 0, 0);
    }

    /**
     * Recalculate gear stats and setup NBT.
     *
     * @param stack The gear item
     * @deprecated Prefer using {@link #recalculateStats(ItemStack, Player)}
     */
    @Deprecated
    public static void recalculateStats(ItemStack stack) {
        recalculateStats(stack, null);
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
        TraitHelper.activateTraits(gear, 0f, (trait, level, value) -> {
            trait.onRecalculatePre(new TraitActionContext(player, level, gear));
            return 0f;
        });

        final String playerName = player != null ? player.getScoreboardName() : "somebody";
        final String playersItemText = String.format("%s's %s", playerName, gear.getHoverName().getString());

        var construction = gear.get(SgDataComponents.GEAR_CONSTRUCTION);
        if (construction == null) {
            SilentGear.LOGGER.error("{}: gear item has no construction data?", playersItemText);
            return;
        }

        final PartDataList parts = construction.parts();
        final ICoreItem item = (ICoreItem) gear.getItem();
        final boolean partsListValid = !parts.isEmpty() && !parts.getMains().isEmpty();

        if (partsListValid) {
            // We should recalculate the item's stats!
            SilentGear.LOGGER.debug("Recalculating for {}", playersItemText);

            Map<ITrait, Integer> traits = TraitHelper.getTraits(gear, item.getGearType(), parts);

            // Get all stat modifiers from all parts and item class modifiers
            StatModifierMap stats = getStatModifiers(gear, item, parts);

            // For debugging
            Map<ItemStat, Float> oldStatValues = getCurrentStatsForDebugging(gear);

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
                final float withTraits = TraitHelper.activateTraits(gear, initialValue, (trait, level, val) -> {
                    TraitActionContext context = new TraitActionContext(player, level, gear);
                    return trait.onGetStat(context, stat, val, damageRatio);
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
            TraitHelper.activateTraits(gear, 0f, (trait, level, value) -> {
                trait.onRecalculatePost(new TraitActionContext(player, level, gear));
                return 0f;
            });
        } else {
            SilentGear.LOGGER.debug("Not recalculating stats for {}", playersItemText);
        }

        // Update rendering info even if we didn't update stats
        updateRenderingInfo(gear, parts);
    }

    @Nullable
    private static Map<ItemStat, Float> getCurrentStatsForDebugging(ItemStack stack) {
        // Get current stats from the item, this is used for logging stat changes
        if (Config.Common.statsDebugLogging.get()) {
            Map<ItemStat, Float> map = new HashMap<>();
            ItemStats.allStatsOrdered().forEach(stat -> map.put(stat, getStat(stack, stat, false)));
            return map;
        }
        return null;
    }

    private static void printStatsForDebugging(ItemStack stack, StatModifierMap stats, @Nullable Map<ItemStat, Float> oldStats) {
        // Prints stats that have changed for debugging purposes
        if (oldStats != null && SilentGear.LOGGER.isDebugEnabled()) {
            GearType gearType = GearHelper.getType(stack);
            Map<ItemStat, Float> newStats = getCurrentStatsForDebugging(stack);
            assert newStats != null;

            for (ItemStat stat : stats.getStats()) {
                float oldValue = oldStats.get(stat);
                float newValue = newStats.get(stat);
                float change = newValue - oldValue;
                SilentGear.LOGGER.debug(" - {}: {} -> {} ({}) - mods: [{}]",
                        stat.getDisplayName().getString(),
                        oldValue,
                        newValue,
                        change < 0 ? change : "+" + change,
                        StatModifierMap.formatText(stats.get(stat, gearType), stat, 5).getString()
                );
            }
        }
    }

    private static String calculateModelKey(ItemStack stack, Collection<? extends IPartData> parts) {
        StringBuilder s = new StringBuilder(SilentGear.shortenId(NameUtils.fromItem(stack)) + ":");

        for (IPartData part : parts) {
            s.append(part.getModelKey()).append(',');
        }

        return s.toString();
    }

    public static StatModifierMap getStatModifiers(ItemStack stack, ICoreItem item, PartDataList parts) {
        GearType gearType = item.getGearType();
        StatModifierMap stats = new StatModifierMap();

        for (ItemStat stat : ItemStats.allStatsOrderedExcluding(item.getExcludedStats(stack))) {
            StatGearKey itemKey = StatGearKey.of(stat, gearType);

            for (IPartData part : parts) {
                for (StatInstance mod : part.getStatModifiers(itemKey, stack)) {
                    StatInstance modCopy = StatInstance.of(mod.getValue(), mod.getOp(), itemKey);
                    stats.put(modCopy.getKey(), modCopy);
                }
            }
        }

        return stats;
    }

    private static int calculateModelIndex(ItemStack gear) {
        var data = getConstruction(gear);
        var coatingOrMainPart = data.getCoatingOrMainPart();
        if (coatingOrMainPart == null || coatingOrMainPart.getMaterials().isEmpty()) {
            // Data packs may not be fully loaded yet, or something else has gone wrong
            return -1;
        }
        IMaterialInstance mainMaterial = coatingOrMainPart.getMaterials().getFirst();
        IMaterialDisplay main = mainMaterial.getDisplayProperties();
        MaterialLayer firstLayer = main.getLayerList(GearHelper.getType(gear), PartTypes.MAIN.get(), mainMaterial).getFirstLayer();
        boolean highContrast = firstLayer == null || !firstLayer.getTextureId().toString().endsWith("lc");

        int ret = highContrast ? 3 : 2;

        if (getPartOfType(gear, PartTypes.TIP.get()) != null) {
            ret |= 4;
        }
        if (getPartOfType(gear, PartTypes.GRIP.get()) != null) {
            ret |= 8;
        }

        return ret;
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
    public static IPartData getPartOfType(ItemStack stack, PartType type) {
        var data = stack.get(SgDataComponents.GEAR_CONSTRUCTION);
        if (data == null) return null;

        for (IPartData part : data.parts()) {
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

        for (IPartData part : data.parts()) {
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
    public static void addUpgradePart(ItemStack gear, PartData part) {
        if (!GearHelper.isGear(gear)) return;

        PartDataList parts = getConstructionParts(gear);

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
        for (IPartData partInList : parts) {
            if (partInList.get() == part.get()) {
                return;
            }
        }

        parts.add(part);
        writeConstructionParts(gear, parts);
    }

    public static boolean hasPart(ItemStack gear, PartType partType, Predicate<IPartData> predicate) {
        for (IPartData partData : getConstructionParts(gear)) {
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
    public static boolean hasPart(ItemStack gear, IGearPart part) {
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
    public static boolean hasPart(ItemStack gear, DataResource<IGearPart> part) {
        if (checkNonGearItem(gear, "hasPart")) return false;

        String partId = part.getId().toString();
        return hasPart(gear, partId);
    }

    private static boolean hasPart(ItemStack gear, String partId) {
        var data = gear.get(SgDataComponents.GEAR_CONSTRUCTION);
        if (data == null) return false;

        for (IPartData part : data.parts()) {
            if (part.getId().toString().equalsIgnoreCase(partId)) {
                return true;
            }
        }

        return false;
    }

    public static void addOrReplacePart(ItemStack gear, PartData part) {
        PartType partType = part.getType();
        PartDataList parts = getConstructionParts(gear);
        List<IPartData> partsOfType = parts.getPartsOfType(partType);
        IPartData removedPart = null;

        if (!partsOfType.isEmpty() && partsOfType.size() >= partType.maxPerItem()) {
            removedPart = partsOfType.getFirst();
            parts.remove(removedPart);
            removedPart.onRemoveFromGear(gear);
        }

        parts.add(part);
        writeConstructionParts(gear, parts);
    }

    public static void addPart(ItemStack gear, PartData part) {
        PartDataList parts = getConstructionParts(gear);
        parts.add(part);
        writeConstructionParts(gear, parts);
        part.onAddToGear(gear);
    }

    public static boolean removeFirstPartOfType(ItemStack gear, PartType type) {
        PartDataList parts = getConstructionParts(gear);
        List<IPartData> partsOfType = new ArrayList<>(parts.getPartsOfType(type));

        if (!partsOfType.isEmpty()) {
            IPartData removed = partsOfType.removeFirst();
            parts.remove(removed);
            writeConstructionParts(gear, parts);
            removed.onRemoveFromGear(gear);
            return true;
        }

        return false;
    }

    public static void writeConstructionParts(ItemStack gear, Collection<? extends IPartData> parts) {
        if (checkNonGearItem(gear, "writeConstructionParts")) return;

        var data = gear.get(SgDataComponents.GEAR_CONSTRUCTION);
        var newData = new GearConstructionData(
                PartDataList.immutable(parts),
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
