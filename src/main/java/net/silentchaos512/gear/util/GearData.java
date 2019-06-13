package net.silentchaos512.gear.util;

import com.google.common.collect.Multimap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.EndNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.item.ICoreTool;
import net.silentchaos512.gear.api.parts.IGearPart;
import net.silentchaos512.gear.api.parts.IUpgradePart;
import net.silentchaos512.gear.api.parts.PartDataList;
import net.silentchaos512.gear.api.parts.PartType;
import net.silentchaos512.gear.api.stats.CommonItemStats;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.StatInstance;
import net.silentchaos512.gear.api.stats.StatInstance.Operation;
import net.silentchaos512.gear.api.stats.StatModifierMap;
import net.silentchaos512.gear.api.traits.ITrait;
import net.silentchaos512.gear.api.traits.TraitActionContext;
import net.silentchaos512.gear.parts.PartData;
import net.silentchaos512.gear.parts.PartManager;
import net.silentchaos512.gear.traits.TraitConst;
import net.silentchaos512.gear.traits.TraitManager;
import net.silentchaos512.lib.collection.StackList;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class GearData {
    private static final String NBT_ROOT = "SGear_Data";
    private static final String NBT_ROOT_CONSTRUCTION = "Construction";
    private static final String NBT_ROOT_PROPERTIES = "Properties";
    private static final String NBT_ROOT_MODEL_KEYS = "ModelKeys";
    private static final String NBT_ROOT_STATISTICS = "Statistics";

    private static final String NBT_CONSTRUCTION_PARTS = "Parts";
    private static final String NBT_LOCK_STATS = "LockStats";
    private static final String NBT_IS_EXAMPLE = "IsExample";
    private static final String NBT_RANDOM_GRADING_DONE = "RandomGradingDone";
    private static final String NBT_SYNERGY_DISPLAY = "synergy";
    private static final String NBT_UUID = "SGear_UUID";

    private static final String NBT_BROKEN_COUNT = "BrokenCount";
    private static final String NBT_REPAIR_COUNT = "RepairCount";

    private GearData() {throw new IllegalAccessError("Utility class");}

    /**
     * Recalculate gear stats and setup NBT. This should be called ANY TIME an item is modified!
     */
    public static void recalculateStats(PlayerEntity player, ItemStack stack) {
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

            // Calculate and write stats
            final float damageRatio = (float) stack.getDamage() / (float) stack.getMaxDamage();
            for (ItemStat stat : stats.keySet()) {
                final float initialValue = stat.compute(0f, stats.get(stat));
                // Some stats will be reduced if tool rod is missing (and required)
                final float withMissingParts = hasMissingRod
                        ? stat.withMissingRodEffect(initialValue)
                        : initialValue;
                final float value = TraitHelper.activateTraits(stack, withMissingParts, (trait, level, val) ->
                        trait.onGetStat(new TraitActionContext(player, level, stack), stat, val, damageRatio));
                // SilentGear.log.debug(stat, value);
                propertiesCompound.putFloat(stat.getName().getPath(), value);
            }

            // Cache traits in properties compound as well
            ListNBT traitList = new ListNBT();
            for (ITrait trait : traits.keySet()) {
                int level = traits.get(trait);
                CompoundNBT tag = new CompoundNBT();
                tag.putString("Name", trait.getId().toString());
                tag.putByte("Level", (byte) level);
                traitList.add(tag);
            }
            propertiesCompound.put("Traits", traitList);

            propertiesCompound.putFloat(NBT_SYNERGY_DISPLAY, (float) synergy);
        }

        // Update model keys even if we didn't update stats
        createAndSaveModelKeys(stack, item, parts);
    }

    private static void addOrRemoveHighlightPart(ItemStack stack, PartDataList parts) {
        final PartData primary = parts.getPrimaryMain();
        if (primary == null) return;

        boolean changed = false;

        if (primary.getPart().getDisplayProperties(primary, stack, 0).hasHighlight()) {
            // Add highlight part if missing
            if (parts.getParts(p -> p.getType() == PartType.HIGHLIGHT).isEmpty()) {
                IGearPart highlight = PartManager.get(new ResourceLocation(SilentGear.MOD_ID, "highlight"));
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

        if (changed) {
            writeConstructionParts(stack, parts);
        }
    }

    @Deprecated
    public static void recalculateStats(ItemStack stack) {recalculateStats(null, stack);}

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
        if (!(stack.getItem() instanceof ICoreItem))
            return "Invalid item!";

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
        float primaryRarity = primaryMain == null ? 0 : primaryMain.computeStat(CommonItemStats.RARITY);
        float maxRarity = primaryRarity;
        int maxTier = 0;
        for (PartData data : uniqueParts) {
            maxRarity = Math.max(maxRarity, data.computeStat(CommonItemStats.RARITY));
            maxTier = Math.max(maxTier, data.getPart().getTier());
        }
        for (PartData data : uniqueParts) {
            if (maxRarity > 0) {
                float rarity = data.computeStat(CommonItemStats.RARITY);
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
        if (!GearHelper.isGear(stack)) {
            SilentGear.LOGGER.error("Called getStat on non-gear item, {}", stack);
            SilentGear.LOGGER.catching(new IllegalArgumentException());
            return stat.getDefaultValue();
        }

        CompoundNBT tags = getData(stack, NBT_ROOT_PROPERTIES);
        String key = stat.getName().getPath();

        if (tags.contains(key)) {
            return tags.getFloat(key);
        } else {
            return stat.getDefaultValue();
        }
    }

    public static int getStatInt(ItemStack stack, ItemStat stat) {
        return Math.round(getStat(stack, stat));
    }

    public static boolean hasLockedStats(ItemStack stack) {
        if (!GearHelper.isGear(stack)) {
            SilentGear.LOGGER.error("Called hasLockedStats on non-gear item, {}", stack);
            SilentGear.LOGGER.catching(new IllegalArgumentException());
            return false;
        }
        return getData(stack, NBT_ROOT_PROPERTIES).getBoolean(NBT_LOCK_STATS);
    }

    public static void setLockedStats(ItemStack stack, boolean lock) {
        if (!GearHelper.isGear(stack)) {
            SilentGear.LOGGER.error("Called setLockedStats on non-gear item, {}", stack);
            SilentGear.LOGGER.catching(new IllegalArgumentException());
            return;
        }
        getData(stack, NBT_ROOT_PROPERTIES).putBoolean(NBT_LOCK_STATS, lock);
    }

    public static PartDataList getConstructionParts(ItemStack stack) {
        if (!GearHelper.isGear(stack)) {
            SilentGear.LOGGER.error("Called getConstructionParts on non-gear item, {}", stack);
            SilentGear.LOGGER.catching(new IllegalArgumentException());
            return PartDataList.of();
        }

        CompoundNBT tags = getData(stack, NBT_ROOT_CONSTRUCTION);
        ListNBT tagList = tags.getList(NBT_CONSTRUCTION_PARTS, 10);
        PartDataList list = PartDataList.of();
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
        if (!GearHelper.isGear(gear)) {
            SilentGear.LOGGER.error("Called recalculateStats on non-gear item, {}", gear);
            SilentGear.LOGGER.catching(new IllegalArgumentException());
            return 1;
        }
        return getData(gear, NBT_ROOT_PROPERTIES).getFloat(NBT_SYNERGY_DISPLAY);
    }

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
        if (!GearHelper.isGear(stack)) {
            SilentGear.LOGGER.error("Called getPrimaryRenderPartFast on non-gear item, {}", stack);
            SilentGear.LOGGER.catching(new IllegalArgumentException());
            return null;
        }

        CompoundNBT tags = getData(stack, NBT_ROOT_CONSTRUCTION);
        ListNBT tagList = tags.getList(NBT_CONSTRUCTION_PARTS, 10);

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

        INBT nbt = tagList.get(index);
        if (nbt instanceof EndNBT) return null;

        PartData data = PartData.readFast((CompoundNBT) nbt);
        return data != null && data.getType() == PartType.MAIN ? data : null;
    }

    @Nullable
    public static PartData getPartOfType(ItemStack stack, PartType type) {
        if (!GearHelper.isGear(stack)) {
            SilentGear.LOGGER.error("Called getPartOfType on non-gear item, {}", stack);
            SilentGear.LOGGER.catching(new IllegalArgumentException());
            return null;
        }

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

    public static boolean hasPartOftype(ItemStack stack, PartType type) {
        if (!GearHelper.isGear(stack)) return false;

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

    public static void addUpgradePart(ItemStack gear, ItemStack partStack) {
        PartData part = PartData.from(partStack);
        if (part != null) {
            addUpgradePart(gear, part);
        }
    }

    public static void addUpgradePart(ItemStack gear, PartData part) {
        if (!(gear.getItem() instanceof ICoreItem)) {
            SilentGear.LOGGER.error("Tried to add upgrade part to non-gear item {}", gear);
            throw new IllegalArgumentException("Invalid Item type");
        }

        PartDataList parts = getConstructionParts(gear);

        if (part.getPart() instanceof IUpgradePart) {
            IUpgradePart upgradePart = (IUpgradePart) part.getPart();
            // Make sure the upgrade is valid for the gear type
            if (!upgradePart.isValidFor((ICoreItem) gear.getItem()))
                return;
            // Only one allowed in this position? Remove existing if needed.
            if (upgradePart.replacesExisting())
                parts.removeIf(p -> p.getPart().getPartPosition() == part.getPart().getPartPosition());
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
        return hasPart(gear, part);
    }

    /**
     * Determine if the gear has the specified part. This scans the construction NBT directly for
     * speed, no part data list is created. Compares part registry names only.
     *
     * @param gear    The gear item
     * @param upgrade The part to check for
     * @return True if the item has the part in its construction, false otherwise
     */
    public static boolean hasPart(ItemStack gear, IGearPart upgrade) {
        if (!GearHelper.isGear(gear)) {
            SilentGear.LOGGER.error("Called hasPart on non-gear item, {}", gear);
            SilentGear.LOGGER.catching(new IllegalArgumentException());
            return false;
        }

        CompoundNBT tags = getData(gear, NBT_ROOT_CONSTRUCTION);
        ListNBT tagList = tags.getList(NBT_CONSTRUCTION_PARTS, 10);
        String upgradeName = upgrade.getId().toString();

        for (INBT nbt : tagList) {
            if (nbt instanceof CompoundNBT) {
                CompoundNBT partCompound = (CompoundNBT) nbt;
                String partKey = partCompound.getString(PartData.NBT_ID);
                if (partKey.equals(upgradeName)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static void writeConstructionParts(ItemStack stack, Collection<PartData> parts) {
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

    static void setExampleTag(ItemStack stack, boolean value) {
        if (!GearHelper.isGear(stack)) {
            SilentGear.LOGGER.error("Called setExampleTag on non-gear item, {}", stack);
            SilentGear.LOGGER.catching(new IllegalArgumentException());
            return;
        }
        getData(stack, NBT_ROOT_CONSTRUCTION).putBoolean(NBT_IS_EXAMPLE, value);
    }

    public static boolean isExampleGear(ItemStack stack) {
        if (!GearHelper.isGear(stack)) {
            SilentGear.LOGGER.error("Called isExampleGear on non-gear item, {}", stack);
            SilentGear.LOGGER.catching(new IllegalArgumentException());
            return false;
        }
        return getData(stack, NBT_ROOT_CONSTRUCTION).getBoolean(NBT_IS_EXAMPLE);
    }

    public static boolean isRandomGradingDone(ItemStack stack) {
        if (!GearHelper.isGear(stack)) {
            SilentGear.LOGGER.error("Called isRandomGradingDone on non-gear item, {}", stack);
            SilentGear.LOGGER.catching(new IllegalArgumentException());
            return true;
        }
        return getData(stack, NBT_ROOT_CONSTRUCTION).getBoolean(NBT_RANDOM_GRADING_DONE);
    }

    static void setRandomGradingDone(ItemStack stack, boolean value) {
        getData(stack, NBT_ROOT_CONSTRUCTION).putBoolean(NBT_RANDOM_GRADING_DONE, value);
    }

    public static int getBrokenCount(ItemStack stack) {
        if (!GearHelper.isGear(stack)) {
            SilentGear.LOGGER.error("Called getBrokenCount on non-gear item, {}", stack);
            SilentGear.LOGGER.catching(new IllegalArgumentException());
            return 0;
        }
        return getData(stack, NBT_ROOT_CONSTRUCTION).getInt(NBT_BROKEN_COUNT);
    }

    static void incrementBrokenCount(ItemStack stack) {
        getData(stack, NBT_ROOT_CONSTRUCTION).putInt(NBT_BROKEN_COUNT, getBrokenCount(stack) + 1);
    }

    public static int getRepairCount(ItemStack stack) {
        if (!GearHelper.isGear(stack)) {
            SilentGear.LOGGER.error("Called getRepairCount on non-gear item, {}", stack);
            SilentGear.LOGGER.catching(new IllegalArgumentException());
            return 0;
        }
        return getData(stack, NBT_ROOT_CONSTRUCTION).getInt(NBT_REPAIR_COUNT);
    }

    public static void incrementRepairCount(ItemStack stack, int amount) {
        if (!GearHelper.isGear(stack)) {
            SilentGear.LOGGER.error("Called incrementRepairCount on non-gear item, {}", stack);
            SilentGear.LOGGER.catching(new IllegalArgumentException());
            return;
        }
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
                    .forEach(s -> recalculateStats(player, s));
        }
    }
}
