package net.silentchaos512.gear.util;

import com.google.common.collect.Multimap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.EndNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.item.ICoreTool;
import net.silentchaos512.gear.api.parts.*;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.api.stats.StatInstance;
import net.silentchaos512.gear.api.stats.StatInstance.Operation;
import net.silentchaos512.gear.api.stats.StatModifierMap;
import net.silentchaos512.gear.api.traits.ITrait;
import net.silentchaos512.gear.api.traits.TraitActionContext;
import net.silentchaos512.gear.config.Config;
import net.silentchaos512.gear.parts.PartConst;
import net.silentchaos512.gear.parts.PartData;
import net.silentchaos512.gear.parts.PartManager;
import net.silentchaos512.gear.traits.TraitConst;
import net.silentchaos512.gear.traits.TraitManager;
import net.silentchaos512.lib.collection.StackList;
import net.silentchaos512.utils.Color;
import net.silentchaos512.utils.MathUtils;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Includes many methods for getting values from the NBT of gear items. Please make sure all
 * ItemStacks passed in are of {@link ICoreItem}s! Calling these methods with invalid items should
 * not crash the game, but will spam the log with stack traces and will return invalid values.
 */
public final class GearData {
    private static final String NBT_ROOT = "SGear_Data";
    private static final String NBT_ROOT_CONSTRUCTION = "Construction";
    private static final String NBT_ROOT_PROPERTIES = "Properties";
    private static final String NBT_ROOT_MODEL_KEYS = "ModelKeys";
    private static final String NBT_ROOT_RENDERING = "Rendering";
    private static final String NBT_ROOT_STATISTICS = "Statistics";

    private static final String NBT_BLENDED_HEAD_COLOR = "BlendedHeadColor";
    private static final String NBT_CONSTRUCTION_PARTS = "Parts";
    private static final String NBT_LOCK_STATS = "LockStats";
    private static final String NBT_IS_EXAMPLE = "IsExample";
    private static final String NBT_RANDOM_GRADING_DONE = "RandomGradingDone";
    private static final String NBT_SYNERGY = "synergy";
    private static final String NBT_TIER = "Tier";
    private static final String NBT_UUID = "SGear_UUID";

    private static final String NBT_BROKEN_COUNT = "BrokenCount";
    private static final String NBT_REPAIR_COUNT = "RepairCount";

    private GearData() {
        throw new IllegalAccessError("Utility class");
    }

    /**
     * Recalculate gear stats and setup NBT.
     *
     * @param stack The gear item
     * @deprecated Prefer using {@link #recalculateStats(ItemStack, PlayerEntity)}
     */
    @Deprecated
    public static void recalculateStats(ItemStack stack) {
        recalculateStats(stack, null);
    }

    /**
     * Recalculate gear stats and setup NBT. This should be called ANY TIME an item is modified!
     *
     * @param stack  The gear item
     * @param player The player who has the item. Can be null if no player can be obtained. You can
     *               use {@link net.minecraftforge.common.ForgeHooks#getCraftingPlayer} the get the
     *               player during crafting.
     */
    public static void recalculateStats(ItemStack stack, @Nullable PlayerEntity player) {
        if (!GearHelper.isGear(stack)) {
            SilentGear.LOGGER.error("Called recalculateStats on non-gear item, {}", stack);
            SilentGear.LOGGER.catching(new IllegalArgumentException());
            return;
        }

        getUUID(stack);
        ICoreItem item = (ICoreItem) stack.getItem();
        PartDataList parts = getConstructionParts(stack);

        CompoundNBT propertiesCompound = getData(stack, NBT_ROOT_PROPERTIES);
        if (!propertiesCompound.contains(NBT_LOCK_STATS))
            propertiesCompound.putBoolean(NBT_LOCK_STATS, false);

        final boolean statsUnlocked = !propertiesCompound.getBoolean(NBT_LOCK_STATS);
        final boolean partsListValid = !parts.isEmpty() && !parts.getMains().isEmpty();
        if (statsUnlocked && partsListValid) {
            // We should recalculate the item's stats!
            clearCachedData(stack);
            addOrRemoveHighlightPart(stack, parts);
            PartDataList uniqueParts = parts.getUniqueParts(true);
            Map<ITrait, Integer> traits = TraitHelper.getTraits(stack, parts);

            double synergy = calculateSynergyValue(parts, uniqueParts, traits);
            boolean hasMissingRod = item instanceof ICoreTool && parts.getRods().isEmpty();

            // Only consider stats relevant to the item
            // Collection<ItemStat> relevantStats = stack.getItem() instanceof ICoreItem
            // ? item.getRelevantStats(stack)
            // : ItemStat.ALL_STATS.values();

            // Get all stat modifiers from all parts and item class modifiers
            Multimap<ItemStat, StatInstance> stats = getStatModifiers(stack, item, parts, synergy);

            // For debugging
            Map<ItemStat, Float> oldStatValues = getCurrentStatsForDebugging(stack);

            // Calculate and write stats
            final float damageRatio = (float) stack.getDamage() / (float) stack.getMaxDamage();
            for (ItemStat stat : stats.keySet()) {
                final float initialValue = stat.compute(0f, stats.get(stat));
                // Some stats will be reduced if tool rod is missing (and required)
                final float withMissingParts = hasMissingRod ? stat.withMissingRodEffect(initialValue) : initialValue;
                // Allow traits to modify stat
                final float withTraits = TraitHelper.activateTraits(stack, withMissingParts, (trait, level, val) -> {
                    TraitActionContext context = new TraitActionContext(player, level, stack);
                    return trait.onGetStat(context, stat, val, damageRatio);
                });
                final float value = Config.GENERAL.getStatWithMultiplier(stat, withTraits);
                // SilentGear.log.debug(stat, value);
                propertiesCompound.putFloat(stat.getName().getPath(), stat.clampValue(value));
            }

            if (player != null) {
                printStatsForDebugging(stack, oldStatValues);
            }

            // Cache traits in properties compound as well
            ListNBT traitList = new ListNBT();
            traits.forEach((trait, level) -> traitList.add(trait.write(level)));
            propertiesCompound.put("Traits", traitList);

            propertiesCompound.putFloat(NBT_SYNERGY, (float) synergy);
        }

        // Update rendering info even if we didn't update stats
        updateRenderingInfo(stack, parts);
    }

    private static final boolean STAT_DEBUGGING = true;

    @Nullable
    private static Map<ItemStat, Float> getCurrentStatsForDebugging(ItemStack stack) {
        // Get current stats from the item, this is used for logging stat changes
        if (STAT_DEBUGGING) { // TODO: Add config
            Map<ItemStat, Float> map = new HashMap<>();
            ItemStat.ALL_STATS.values().forEach(stat -> map.put(stat, getStat(stack, stat)));
            return map;
        }
        return null;
    }

    private static void printStatsForDebugging(ItemStack stack, @Nullable Map<ItemStat, Float> oldStats) {
        // Prints stats that have changed for debugging purposes
        if (oldStats != null && SilentGear.LOGGER.isDebugEnabled()) {
            SilentGear.LOGGER.debug("Stats change on {}", stack.getDisplayName().getFormattedText());
            Map<ItemStat, Float> newStats = getCurrentStatsForDebugging(stack);
            assert newStats != null;

            int changeCount = 0;
            for (ItemStat stat : ItemStat.ALL_STATS.values()) {
                float oldValue = oldStats.get(stat);
                float newValue = newStats.get(stat);
                if (!MathUtils.doublesEqual(oldValue, newValue)) {
                    SilentGear.LOGGER.debug(" - {}: {} -> {}", stat.getName().getPath(), oldValue, newValue);
                    ++changeCount;
                }
            }

            if (changeCount == 0)
                SilentGear.LOGGER.debug(" - No changes");
        }
    }

    private static void addOrRemoveHighlightPart(ItemStack stack, PartDataList parts) {
        final PartData primary = parts.getPrimaryMain();
        if (primary == null) return;

        boolean changed = false;

        if (primary.getPart().getDisplayProperties(primary, stack, 0).hasHighlight()) {
            // Add highlight part if missing
            if (parts.getParts(p -> p.getType() == PartType.HIGHLIGHT).isEmpty()) {
                IGearPart highlight = PartManager.get(PartConst.HIGHLIGHT);
                if (highlight != null) {
                    parts.add(PartData.of(highlight));
                    changed = true;
                } else {
                    SilentGear.LOGGER.error("GearData#addOrRemoveHighlightPart: highlight part is missing?");
                }
            }
        } else {
            // Remove unneeded highlight part if present
            changed = parts.removeIf(p -> p.getType() == PartType.HIGHLIGHT);
        }

        if (changed) writeConstructionParts(stack, parts);
    }

    /**
     * Clears NBT which is created on-demand.
     */
    private static void clearCachedData(ItemStack stack) {
        CompoundNBT construction = getData(stack, NBT_ROOT_CONSTRUCTION);
        construction.remove(NBT_TIER);
    }

    private static void updateRenderingInfo(ItemStack stack, PartDataList parts) {
        CompoundNBT nbt = getData(stack, NBT_ROOT_RENDERING);
        nbt.putInt(NBT_BLENDED_HEAD_COLOR, calculateBlendedHeadColor(stack, parts.getMains()));

        createAndSaveModelKeys(stack, ((ICoreItem) stack.getItem()), parts);
    }

    private static void createAndSaveModelKeys(ItemStack stack, ICoreItem item, PartDataList parts) {
        // Save model keys for performance
        // Remove the old keys first, then get new ones from ICoreItem
        stack.getOrCreateChildTag(NBT_ROOT).remove(NBT_ROOT_MODEL_KEYS);
        CompoundNBT modelKeys = getData(stack, NBT_ROOT_MODEL_KEYS);
        for (int i = 0; i < item.getAnimationFrames(); ++i) {
            modelKeys.putString(Integer.toString(i), item.getModelKey(stack, i, parts.toArray(new PartData[0])));
        }
    }

    public static String getCachedModelKey(ItemStack stack, int animationFrame) {
        if (!(stack.getItem() instanceof ICoreItem)) return "Invalid item!";

        CompoundNBT tags = getData(stack, NBT_ROOT_MODEL_KEYS);
        String key = Integer.toString(animationFrame);
        if (!tags.contains(key))
            tags.putString(key, ((ICoreItem) stack.getItem()).getModelKey(stack, animationFrame));
        return tags.getString(Integer.toString(animationFrame));
    }

    public static Multimap<ItemStat, StatInstance> getStatModifiers(ItemStack stack, @Nullable ICoreItem item, PartDataList parts, double synergy) {
        Multimap<ItemStat, StatInstance> stats = new StatModifierMap();
        for (ItemStat stat : ItemStat.ALL_STATS.values()) {
            // Item class modifiers
            if (item != null) {
                item.getBaseStatModifier(stat).ifPresent(mod -> stats.put(stat, mod));
                item.getStatModifier(stat).ifPresent(mod -> stats.put(stat, mod));
            }
            // Part modifiers
            int partCount = 0;
            for (PartData partData : parts) {
                String idSuffix = "_" + (++partCount);
                // Allow "duplicate" AVG modifiers
                for (StatInstance inst : partData.getStatModifiers(stack, stat)) {
                    if (inst.getOp() == Operation.AVG && stat.isAffectedByGrades()) {
                        float gradeBonus = 1f + partData.getGrade().bonusPercent / 100f;
                        float statValue = inst.getValue() * gradeBonus;
                        stats.put(stat, new StatInstance(inst.getId() + idSuffix, statValue, Operation.AVG));
                    } else {
                        stats.put(stat, inst.copyAppendId(idSuffix));
                    }
                }
            }
            // Synergy bonus?
            if (stat.doesSynergyApply())
                stats.put(stat, new StatInstance("synergy_multi", (float) synergy - 1, StatInstance.Operation.MUL2));
        }
        return stats;
    }

    private static final double SYNERGY_MULTI = 1.1;

    private static double getBaseSynergy(PartDataList parts) {
        final int x = parts.getMains().size();
        final double a = SYNERGY_MULTI;
        return a * (x / (x + a)) + (1 / (1 + a));
    }

    public static double calculateSynergyValue(PartDataList parts, PartDataList uniqueParts, Map<ITrait, Integer> traits) {
        // First, we add a bonus for the number of unique main parts
//        double synergy = 1.0 + 0.2 * Math.log(5 * uniqueParts.getMains().size() - 4);
        double synergy = getBaseSynergy(uniqueParts);

        // Second, reduce synergy for difference in rarity and tier
        PartData primaryMain = parts.getPrimaryMain();
        float primaryRarity = primaryMain == null ? 0 : primaryMain.computeStat(ItemStats.RARITY);
        float maxRarity = primaryRarity;
        int maxTier = 0;
        for (PartData data : uniqueParts) {
            maxRarity = Math.max(maxRarity, data.computeStat(ItemStats.RARITY));
            maxTier = Math.max(maxTier, data.getPart().getTier());
        }
        for (PartData data : uniqueParts) {
            if (maxRarity > 0) {
                float rarity = data.computeStat(ItemStats.RARITY);
                synergy -= 0.005 * Math.abs(primaryRarity - rarity);
            }
            if (maxTier > 0) {
                int tier = data.getPart().getTier();
                synergy -= 0.16f * Math.abs(maxTier - tier);
            }
        }

        // Synergy Boost (only if higher than 100%)
        ITrait synergistic = TraitManager.get(TraitConst.SYNERGISTIC);
        if (synergy > 1 && traits.containsKey(synergistic)) {
            int level = traits.get(synergistic);
            synergy += level * TraitConst.SYNERGY_BOOST_MULTI;
        }
        ITrait crude = TraitManager.get(TraitConst.CRUDE);
        if (traits.containsKey(crude)) {
            int level = traits.get(crude);
            synergy -= level * TraitConst.SYNERGY_BOOST_MULTI;
        }

        return synergy;
    }

    public static float getStat(ItemStack stack, ItemStat stat) {
        CompoundNBT tags = getData(stack, NBT_ROOT_PROPERTIES);
        String key = stat.getName().getPath();
        return tags.contains(key) ? tags.getFloat(key) : stat.getDefaultValue();
    }

    public static int getStatInt(ItemStack stack, ItemStat stat) {
        return Math.round(getStat(stack, stat));
    }

    public static boolean hasLockedStats(ItemStack stack) {
        return getData(stack, NBT_ROOT_PROPERTIES).getBoolean(NBT_LOCK_STATS);
    }

    public static void setLockedStats(ItemStack stack, boolean lock) {
        getData(stack, NBT_ROOT_PROPERTIES).putBoolean(NBT_LOCK_STATS, lock);
    }

    public static PartDataList getConstructionParts(ItemStack stack) {
        if (!GearHelper.isGear(stack)) return PartDataList.empty();

        CompoundNBT tags = getData(stack, NBT_ROOT_CONSTRUCTION);
        ListNBT tagList = tags.getList(NBT_CONSTRUCTION_PARTS, 10);
        PartDataList list = PartDataList.empty();
        Map<PartType, Integer> partCounts = new HashMap<>();

        for (INBT nbt : tagList) {
            if (nbt instanceof CompoundNBT) {
                CompoundNBT partCompound = (CompoundNBT) nbt;
                PartData part = PartData.read(partCompound);

                if (part != null) {
                    // Add to list if max per item of type is not exceeded
                    // Max is 9 for mains, and typically 1 for others
                    PartType type = part.getType();
                    int count = partCounts.getOrDefault(type, 0);
                    if (count < type.getMaxPerItem()) {
                        list.add(part);
                        partCounts.put(type, count + 1);
                    }
                }
            }
        }

        return list;
    }

    public static float getSynergyDisplayValue(ItemStack gear) {
        return getData(gear, NBT_ROOT_PROPERTIES).getFloat(NBT_SYNERGY);
    }

    /**
     * Gets the tier of the gear item. The tier is the same as the main part with the highest tier.
     *
     * @param gear The gear item
     * @return The gear tier
     */
    public static int getTier(ItemStack gear) {
        if (!GearHelper.isGear(gear)) return -1;

        CompoundNBT data = getData(gear, NBT_ROOT_CONSTRUCTION);
        if (!data.contains(NBT_TIER)) {
            List<PartData> parts = getConstructionParts(gear).getMains();
            int max = 0;
            for (PartData part : parts) {
                if (part.getTier() > max) {
                    max = part.getTier();
                }
            }
            data.putInt(NBT_TIER, max);
        }
        return data.getInt(NBT_TIER);
    }

    public static int getHeadColor(ItemStack stack, boolean colorBlending) {
        if (!colorBlending) {
            PartData part = getPrimaryRenderPartFast(stack);
            if (part == null) return Color.VALUE_WHITE;
            return part.getFallbackColor(stack, 0);
        }
        return getData(stack, NBT_ROOT_RENDERING).getInt(NBT_BLENDED_HEAD_COLOR);
    }

    @SuppressWarnings("OverlyLongMethod")
    private static int calculateBlendedHeadColor(ItemStack gear, List<PartData> parts) {
        int[] componentSums = new int[3];
        int maxColorSum = 0;
        int colorCount = 0;

        int partCount = parts.size();
        for (int i = 0; i < partCount; ++i) {
            PartData part = parts.get(i);
            int color = part.getFallbackColor(gear, 0);
            int r = (color >> 16) & 0xFF;
            int g = (color >> 8) & 0xFF;
            int b = color & 0xFF;
            // Add earlier colors multiple times, to give them greater weight
            int colorWeight = (partCount - i) * (partCount - i);
            for (int j = 0; j < colorWeight; ++j) {
                maxColorSum += Math.max(r, Math.max(g, b));
                componentSums[0] += r;
                componentSums[1] += g;
                componentSums[2] += b;
                ++colorCount;
            }
        }

        if (colorCount > 0) {
            int r = componentSums[0] / colorCount;
            int g = componentSums[1] / colorCount;
            int b = componentSums[2] / colorCount;
            float maxAverage = (float) maxColorSum / (float) colorCount;
            float max = (float) Math.max(r, Math.max(g, b));
            r = (int) ((float) r * maxAverage / max);
            g = (int) ((float) g * maxAverage / max);
            b = (int) ((float) b * maxAverage / max);
            int finalColor = (r << 8) + g;
            finalColor = (finalColor << 8) + b;
            return finalColor;
        }

        return Color.VALUE_WHITE;
    }

    //region Part getters and checks

    /**
     * Gets the primary (first) main part.
     *
     * @param stack The gear item
     * @return The primary part, or null if there are no main parts
     */
    @Nullable
    public static PartData getPrimaryPart(ItemStack stack) {
        return getPartByIndex(stack, 0);
    }

    @Nullable
    public static PartData getSecondaryPart(ItemStack stack) {
        return getPartByIndex(stack, 1);
    }

    /**
     * Gets the primary part, but only the part itself. Grade and crafting stack are omitted so that
     * the cached PartData can be retrieved instead of constructing a new one.
     *
     * @param stack The gear item
     * @return Cached part data excluding grade and crafting item, or null if it does not exist.
     */
    @Nullable
    public static PartData getPrimaryRenderPartFast(ItemStack stack) {
        CompoundNBT tags = getData(stack, NBT_ROOT_CONSTRUCTION);
        ListNBT tagList = tags.getList(NBT_CONSTRUCTION_PARTS, 10);

        if (tagList.isEmpty()) return null;

        INBT nbt = tagList.get(0);
        if (nbt instanceof CompoundNBT) {
            return PartData.readFast((CompoundNBT) nbt);
        }
        return null;
    }

    /**
     * Gets the main part in the given position (zero-indexed)
     *
     * @return The part if it exists in NBT, null if the index is out of bounds, the data is
     * invalid, or the part is not a main part.
     */
    @Nullable
    private static PartData getPartByIndex(ItemStack stack, int index) {
        CompoundNBT tags = getData(stack, NBT_ROOT_CONSTRUCTION);
        ListNBT tagList = tags.getList(NBT_CONSTRUCTION_PARTS, 10);

        if (index >= tagList.size()) return null;

        INBT nbt = tagList.get(index);
        if (nbt instanceof EndNBT) return null;

        PartData data = PartData.readFast((CompoundNBT) nbt);
        return data != null && data.getType() == PartType.MAIN ? data : null;
    }

    /**
     * Gets the first part in the construction parts list that is of the given type.
     *
     * @param stack The gear item
     * @param type  The part type
     * @return The first part of the given type, or null if there is none
     */
    @Nullable
    public static PartData getPartOfType(ItemStack stack, PartType type) {
        CompoundNBT tags = getData(stack, NBT_ROOT_CONSTRUCTION);
        ListNBT tagList = tags.getList(NBT_CONSTRUCTION_PARTS, 10);

        for (INBT nbt : tagList) {
            if (nbt instanceof CompoundNBT) {
                PartData part = PartData.readFast((CompoundNBT) nbt);
                if (part != null && part.getType() == type) return part;
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
        CompoundNBT tags = getData(stack, NBT_ROOT_CONSTRUCTION);
        ListNBT tagList = tags.getList(NBT_CONSTRUCTION_PARTS, 10);

        for (INBT nbt : tagList) {
            if (nbt instanceof CompoundNBT) {
                PartData part = PartData.readFast((CompoundNBT) nbt);
                if (part != null && part.getType() == type) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Check if the gear item requires a part of the given type and does not have one.
     *
     * @param gear The gear item
     * @param type The part type
     * @return True if the item requires the part type and does not have one, false otherwise
     */
    public static boolean isMissingRequiredPart(ItemStack gear, PartType type) {
        if (!GearHelper.isGear(gear)) return false;
        return ((ICoreItem) gear.getItem()).requiresPartOfType(type) && !hasPartOfType(gear, type);
    }

    /**
     * Add an upgrade part to the gear item, assuming {@code partStack} represents a part.
     *
     * @param gear      The gear item
     * @param partStack The upgrade item
     */
    public static void addUpgradePart(ItemStack gear, ItemStack partStack) {
        PartData part = PartData.from(partStack);
        if (part != null) {
            addUpgradePart(gear, part);
        }
    }

    /**
     * Add an upgrade part to the gear item. Depending on the upgrade, this may replace an existing
     * part.
     * <p>
     * TODO: Should we return something to indicate if the upgrade cannot be applied or replaces an
     * existing upgrade?
     *
     * @param gear The gear item
     * @param part The upgrade part
     */
    public static void addUpgradePart(ItemStack gear, PartData part) {
        if (!GearHelper.isGear(gear)) return;

        PartDataList parts = getConstructionParts(gear);

        if (part.getPart() instanceof IUpgradePart) {
            IUpgradePart upgradePart = (IUpgradePart) part.getPart();
            // Make sure the upgrade is valid for the gear type
            if (!upgradePart.isValidFor((ICoreItem) gear.getItem()))
                return;
            // Only one allowed in this position? Remove existing if needed.
            if (upgradePart.replacesExisting())
                parts.removeIf(p -> p.getPart().getPartPosition() == part.getPart().getPartPosition());

            // Allow the part to make additional changes if needed
            upgradePart.onAddToGear(gear, part.getCraftingItem());
        }

        // Other upgrades allow no exact duplicates, but any number of total upgrades
        for (PartData partInList : parts) {
            if (partInList.getPart() == part.getPart()) {
                return;
            }
        }

        parts.add(part);
        writeConstructionParts(gear, parts);
    }

    /**
     * Determine if the gear has the specified part. This scans the construction NBT directly for
     * speed, no part data list is created. Compares part registry names only.
     *
     * @param gear   The gear item
     * @param partId The ID of the part
     * @return True if the item has the part in its construction, false if it does not or the part
     * does not exist.
     */
    public static boolean hasPart(ItemStack gear, ResourceLocation partId) {
        IGearPart part = PartManager.get(partId);
        if (part == null) return false;
        return hasPart(gear, part, MaterialGrade.Range.OPEN);
    }

    /**
     * Determine if the gear has the specified part. This scans the construction NBT directly for
     * speed, no part data list is created. Compares part registry names only.
     *
     * @param gear       The gear item
     * @param partId     The ID of the part
     * @param gradeRange The grade of part to look for
     * @return True if the item has the part in its construction, false if it does not or the part
     * does not exist.
     */
    public static boolean hasPart(ItemStack gear, ResourceLocation partId, MaterialGrade.Range gradeRange) {
        IGearPart part = PartManager.get(partId);
        if (part == null) return false;
        return hasPart(gear, part, gradeRange);
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
        return hasPart(gear, part, MaterialGrade.Range.OPEN);
    }

    /**
     * Determine if the gear has the specified part. This scans the construction NBT directly for
     * speed, no part data list is created. Compares part registry names and checks grades.
     *
     * @param gear       The gear item
     * @param part       The part to check for
     * @param gradeRange The grade of part to look for
     * @return True if the item has the part in its construction, false otherwise
     */
    public static boolean hasPart(ItemStack gear, IGearPart part, MaterialGrade.Range gradeRange) {
        if (!GearHelper.isGear(gear)) {
            SilentGear.LOGGER.error("Called hasPart on non-gear item, {}", gear);
            SilentGear.LOGGER.catching(new IllegalArgumentException());
            return false;
        }

        CompoundNBT tags = getData(gear, NBT_ROOT_CONSTRUCTION);
        ListNBT tagList = tags.getList(NBT_CONSTRUCTION_PARTS, 10);
        String upgradeName = part.getId().toString();

        for (INBT nbt : tagList) {
            if (nbt instanceof CompoundNBT) {
                CompoundNBT partCompound = (CompoundNBT) nbt;
                String partKey = partCompound.getString(PartData.NBT_ID);
                MaterialGrade grade = MaterialGrade.fromString(partCompound.getString("Grade"));
                if (partKey.equals(upgradeName) && gradeRange.test(grade)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static void writeConstructionParts(ItemStack stack, Collection<? extends IPartData> parts) {
        if (!GearHelper.isGear(stack)) {
            SilentGear.LOGGER.error("Called writeConstructionParts on non-gear item, {}", stack);
            SilentGear.LOGGER.catching(new IllegalArgumentException());
            return;
        }

        CompoundNBT tags = getData(stack, NBT_ROOT_CONSTRUCTION);
        ListNBT tagList = new ListNBT();

        // Mains must be first in the list!
        parts.stream().filter(p -> p.getType() == PartType.MAIN)
                .map(p -> p.write(new CompoundNBT()))
                .forEach(tagList::add);
        // Write everything else in any order
        parts.stream().filter(p -> p.getType() != PartType.MAIN)
                .map(p -> p.write(new CompoundNBT()))
                .forEach(tagList::add);

        tags.put(NBT_CONSTRUCTION_PARTS, tagList);
    }

    //endregion

    /**
     * Gets the item's UUID, creating it if it doesn't have one yet.
     *
     * @param gear ItemStack of an ICoreItem
     * @return The UUID, or null if gear's item is not an ICoreItem
     */
    public static UUID getUUID(ItemStack gear) {
        if (!GearHelper.isGear(gear)) {
            SilentGear.LOGGER.error("Called getUUID on non-gear item, {}", gear);
            SilentGear.LOGGER.catching(new IllegalArgumentException());
            return null;
        }

        CompoundNBT tags = gear.getOrCreateTag();
        if (!tags.hasUniqueId(NBT_UUID)) {
            UUID uuid = UUID.randomUUID();
            tags.putUniqueId(NBT_UUID, uuid);
            return uuid;
        }
        return tags.getUniqueId(NBT_UUID);
    }

    private static CompoundNBT getData(ItemStack stack, String compoundKey) {
        if (SilentGear.isDevBuild() && !GearHelper.isGear(stack)) {
            SilentGear.LOGGER.error("Called getData on non-gear item, {}", stack);
            SilentGear.LOGGER.catching(new IllegalArgumentException());
            return new CompoundNBT();
        }

        CompoundNBT rootTag = stack.getOrCreateChildTag(NBT_ROOT);
        if (!rootTag.contains(compoundKey))
            rootTag.put(compoundKey, new CompoundNBT());
        return rootTag.getCompound(compoundKey);
    }

    static CompoundNBT getPropertiesData(ItemStack stack) {
        return getData(stack, NBT_ROOT_PROPERTIES);
    }

    static CompoundNBT getStatisticsCompound(ItemStack stack) {
        return getData(stack, NBT_ROOT_STATISTICS);
    }

    public static void setExampleTag(ItemStack stack, boolean value) {
        getData(stack, NBT_ROOT_CONSTRUCTION).putBoolean(NBT_IS_EXAMPLE, value);
    }

    public static boolean isExampleGear(ItemStack stack) {
        return getData(stack, NBT_ROOT_CONSTRUCTION).getBoolean(NBT_IS_EXAMPLE);
    }

    public static boolean isRandomGradingDone(ItemStack stack) {
        return getData(stack, NBT_ROOT_CONSTRUCTION).getBoolean(NBT_RANDOM_GRADING_DONE);
    }

    static void setRandomGradingDone(ItemStack stack, boolean value) {
        getData(stack, NBT_ROOT_CONSTRUCTION).putBoolean(NBT_RANDOM_GRADING_DONE, value);
    }

    public static int getBrokenCount(ItemStack stack) {
        return getData(stack, NBT_ROOT_CONSTRUCTION).getInt(NBT_BROKEN_COUNT);
    }

    static void incrementBrokenCount(ItemStack stack) {
        getData(stack, NBT_ROOT_CONSTRUCTION).putInt(NBT_BROKEN_COUNT, getBrokenCount(stack) + 1);
    }

    public static int getRepairCount(ItemStack stack) {
        return getData(stack, NBT_ROOT_CONSTRUCTION).getInt(NBT_REPAIR_COUNT);
    }

    public static void incrementRepairCount(ItemStack stack, int amount) {
        getData(stack, NBT_ROOT_CONSTRUCTION).putInt(NBT_REPAIR_COUNT, getRepairCount(stack) + amount);
    }

    @Mod.EventBusSubscriber(modid = SilentGear.MOD_ID)
    public static final class EventHandler {
        private EventHandler() { }

        @SubscribeEvent
        public static void onPlayerLoggedIn(PlayerLoggedInEvent event) {
            PlayerEntity player = event.getPlayer();
            StackList.from(player.inventory)
                    .stream()
                    .filter(s -> s.getItem() instanceof ICoreItem)
                    .forEach(s -> recalculateStats(s, player));
        }
    }
}
