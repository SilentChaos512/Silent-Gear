package net.silentchaos512.gear.util;

import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.item.ICoreItem;
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
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.gear.part.CompoundPart;
import net.silentchaos512.gear.gear.part.PartData;
import net.silentchaos512.gear.gear.part.PartManager;
import net.silentchaos512.gear.gear.trait.EnchantmentTrait;
import net.silentchaos512.gear.item.CompoundPartItem;
import net.silentchaos512.gear.network.Network;
import net.silentchaos512.gear.network.RecalculateStatsPacket;
import net.silentchaos512.lib.collection.StackList;
import net.silentchaos512.lib.util.NameUtils;
import net.silentchaos512.utils.Color;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

/**
 * Includes many methods for getting values from the NBT of gear items. Please make sure all
 * ItemStacks passed in are of {@link ICoreItem}s! Calling these methods with invalid items should
 * not crash the game, but will spam the log with stack traces and will return invalid values.
 */
public final class GearData {
    private static final String NBT_ROOT = "SGear_Data";
    private static final String NBT_ROOT_CONSTRUCTION = "Construction";
    private static final String NBT_ROOT_PROPERTIES = "Properties";
    private static final String NBT_ROOT_RENDERING = "Rendering";
    private static final String NBT_ROOT_STATISTICS = "Statistics";

    private static final String NBT_CONSTRUCTION_PARTS = "Parts";
    private static final String NBT_LOCK_STATS = "LockStats";
    private static final String NBT_IS_EXAMPLE = "IsExample";
    private static final String NBT_MODEL_KEY = "ModelKey";
    private static final String NBT_SYNERGY = "synergy";
    private static final String NBT_TIER = "Tier";
    private static final String NBT_UUID = "SGear_UUID";

    private static final String NBT_BROKEN_COUNT = "BrokenCount";
    private static final String NBT_REPAIR_COUNT = "RepairCount";
    private static final String NBT_STATS = "Stats";

    private GearData() {
        throw new IllegalAccessError("Utility class");
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
     *               use {@link net.minecraftforge.common.ForgeHooks#getCraftingPlayer} the get the
     *               player during crafting.
     */
    public static void recalculateStats(ItemStack gear, @Nullable Player player) {
        try {
            tryRecalculateStats(gear, player);
        } catch (Throwable ex) {
            CrashReport report = CrashReport.forThrowable(ex, "Failed to recalculate gear properties");

            CrashReportCategory itemCategory = report.addCategory("Gear Item");
            itemCategory.setDetail("Name", gear.getHoverName().getString() + " (" + NameUtils.fromItem(gear) + ")");
            itemCategory.setDetail("Data", gear.getOrCreateTag().toString());

            throw new ReportedException(report);
        }
    }

    @SuppressWarnings({"OverlyLongMethod", "OverlyComplexMethod"})
    private static void tryRecalculateStats(ItemStack gear, @Nullable Player player) {
        if (checkNonGearItem(gear, "recalculateStats")) return;

        getUUID(gear);

        TraitHelper.activateTraits(gear, 0f, (trait, level, value) -> {
            trait.onRecalculatePre(new TraitActionContext(player, level, gear));
            return 0f;
        });

        ICoreItem item = (ICoreItem) gear.getItem();
        PartDataList parts = getConstructionParts(gear);

        CompoundTag propertiesCompound = getData(gear, NBT_ROOT_PROPERTIES);
        if (!propertiesCompound.contains(NBT_LOCK_STATS))
            propertiesCompound.putBoolean(NBT_LOCK_STATS, false);

        String playerName = player != null ? player.getScoreboardName() : "somebody";
        String playersItemText = String.format("%s's %s", playerName, gear.getHoverName().getString());

        final boolean statsUnlocked = !propertiesCompound.getBoolean(NBT_LOCK_STATS);
        final boolean partsListValid = !parts.isEmpty() && !parts.getMains().isEmpty();
        if (statsUnlocked && partsListValid) {
            // We should recalculate the item's stats!
            if (player != null) {
                SilentGear.LOGGER.debug("Recalculating for {}", playersItemText);
            }
            clearCachedData(gear);
            propertiesCompound.putString("ModVersion", SilentGear.getVersion());
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

    /**
     * Clears NBT which is created on-demand.
     */
    private static void clearCachedData(ItemStack stack) {
        CompoundTag construction = getData(stack, NBT_ROOT_CONSTRUCTION);
        construction.remove(NBT_TIER);
    }

    public static String getModelKey(ItemStack stack, int animationFrame) {
        String fromNbt = getData(stack, NBT_ROOT_RENDERING).getString(NBT_MODEL_KEY);
        String key = fromNbt.isEmpty() ? stack.getOrCreateTag().toString() : fromNbt;
        return animationFrame > 0 ? key + "_" + animationFrame : key;
    }

    private static String calculateModelKey(ItemStack stack, Collection<? extends IPartData> parts) {
        StringBuilder s = new StringBuilder(SilentGear.shortenId(NameUtils.fromItem(stack)) + ":");

        for (IPartData part : parts) {
            s.append(part.getModelKey()).append(',');
        }

        return s.toString();
    }

    private static void updateRenderingInfo(ItemStack stack, Collection<? extends IPartData> parts) {
        CompoundTag nbt = getData(stack, NBT_ROOT_RENDERING);

        // Remove deprecated keys
        nbt.remove("ArmorColor");
        nbt.remove("BlendedHeadColor");

        nbt.putString(NBT_MODEL_KEY, calculateModelKey(stack, parts));

        // Remove old model keys
        stack.getOrCreateTagElement(NBT_ROOT).remove("ModelKeys");
    }

    public static StatModifierMap getStatModifiers(ItemStack stack, ICoreItem item, PartDataList parts) {
        GearType gearType = item.getGearType();
        StatModifierMap stats = new StatModifierMap();

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

    public static float getStat(ItemStack stack, IItemStat stat) {
        return getStat(stack, stat, true);
    }

    public static float getStat(ItemStack stack, IItemStat stat, boolean calculateIfMissing) {
        CompoundTag tags = getData(stack, NBT_ROOT_PROPERTIES).getCompound(NBT_STATS);
        String key = stat.getStatId().toString();
        if (tags.contains(key)) {
            return tags.getFloat(key);
        }

        if (calculateIfMissing) {
            // Stat is missing, notify server to recalculate
            Level level = SilentGear.PROXY.getClientLevel();

            if (level != null && SilentGear.PROXY.checkClientConnection() && GearHelper.isValidGear(stack) && ((ICoreItem) stack.getItem()).getRelevantStats(stack).contains(stat)) {
                SilentGear.LOGGER.debug("Sending recalculate stats packet for item with missing {} stat: {}", stat.getStatId(), stack.getHoverName().getString());
                Network.channel.sendToServer(new RecalculateStatsPacket(level, stack, stat));
                // Prevent the packet from being spammed...
                putStatInNbtIfMissing(stack, stat);
            }
        }

        return stat.getDefaultValue();
    }

    public static int getStatInt(ItemStack stack, IItemStat stat) {
        return Math.round(getStat(stack, stat));
    }

    public static void putStatInNbtIfMissing(ItemStack stack, IItemStat stat) {
        CompoundTag tags = getData(stack, NBT_ROOT_PROPERTIES).getCompound(NBT_STATS);
        String key = stat.getStatId().toString();
        if (!tags.contains(key)) {
            tags.putFloat(key, stat.getDefaultValue());
        }
    }

    public static boolean hasLockedStats(ItemStack stack) {
        return getData(stack, NBT_ROOT_PROPERTIES).getBoolean(NBT_LOCK_STATS);
    }

    public static void setLockedStats(ItemStack stack, boolean lock) {
        getData(stack, NBT_ROOT_PROPERTIES).putBoolean(NBT_LOCK_STATS, lock);
    }

    public static PartDataList getConstructionParts(ItemStack stack) {
        if (!GearHelper.isGear(stack)) return PartDataList.empty();

        CompoundTag tags = getData(stack, NBT_ROOT_CONSTRUCTION);
        ListTag tagList = tags.getList(NBT_CONSTRUCTION_PARTS, Tag.TAG_COMPOUND);
        PartDataList list = PartDataList.of();

        for (Tag nbt : tagList) {
            if (nbt instanceof CompoundTag) {
                CompoundTag partCompound = (CompoundTag) nbt;
                PartData part = PartData.read(partCompound);

                if (part != null) {
                    list.add(part);
                }
            }
        }

        return list;
    }

    @Deprecated
    public static float getSynergyDisplayValue(ItemStack gear) {
        return 0;
    }

    /**
     * Gets the tier of the gear item. The tier is the same as the main part with the highest tier.
     *
     * @param gear The gear item
     * @return The gear tier
     */
    public static int getTier(ItemStack gear) {
        if (!GearHelper.isGear(gear)) return -1;

        CompoundTag data = getData(gear, NBT_ROOT_CONSTRUCTION);
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

    @Deprecated
    public static int getBlendedColor(ItemStack stack, PartType partType) {
        List<PartData> list = getConstructionParts(stack).getPartsOfType(partType);
        if (!list.isEmpty()) {
            return getBlendedColor(stack, list) & 0xFFFFFF;
        }
        return Color.VALUE_WHITE;
    }

    @Deprecated
    private static int getBlendedColor(ItemStack gear, List<PartData> parts) {
        int[] componentSums = new int[3];
        int maxColorSum = 0;
        int colorCount = 0;

        int partCount = parts.size();
        for (int i = 0; i < partCount; ++i) {
            PartData part = parts.get(i);
            int color = part.get().getColor(part, gear, 0, 0);
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
        return getPartOfType(stack, PartType.MAIN);
    }

    @Nullable
    public static MaterialInstance getPrimaryMainMaterial(ItemStack stack) {
        PartData part = getPrimaryPart(stack);
        if (part != null && part.get() instanceof CompoundPart) {
            return CompoundPartItem.getPrimaryMaterial(part.getItem());
        }
        return null;
    }

    @Nullable
    public static MaterialInstance getPrimaryArmorMaterial(ItemStack stack) {
        PartData coating = getPartOfType(stack, PartType.COATING);
        if (coating != null && coating.get() instanceof CompoundPart) {
            return CompoundPartItem.getPrimaryMaterial(coating.getItem());
        }
        return getPrimaryMainMaterial(stack);
    }

    @Nullable
    public static PartData getCoatingOrMainPart(ItemStack stack) {
        PartData coating = getPartOfType(stack, PartType.COATING);
        if (coating != null) {
            return coating;
        }
        return getPartOfType(stack, PartType.MAIN);
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
        CompoundTag tags = getData(stack, NBT_ROOT_CONSTRUCTION);
        ListTag tagList = tags.getList(NBT_CONSTRUCTION_PARTS, Tag.TAG_COMPOUND);

        for (int i = 0; i < tagList.size(); ++i) {
            CompoundTag nbt = tagList.getCompound(i);
            PartData part = PartData.read(nbt);

            if (part != null && part.getType() == type) {
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
        CompoundTag tags = getData(stack, NBT_ROOT_CONSTRUCTION);
        ListTag tagList = tags.getList(NBT_CONSTRUCTION_PARTS, Tag.TAG_COMPOUND);

        for (int i = 0; i < tagList.size(); ++i) {
            CompoundTag nbt = tagList.getCompound(i);
            String key = nbt.getString("ID");
            IGearPart part = PartManager.get(key);

            if (part != null && part.getType() == type) {
                return true;
            }
        }

        return false;
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
        for (PartData partInList : parts) {
            if (partInList.get() == part.get()) {
                return;
            }
        }

        parts.add(part);
        writeConstructionParts(gear, parts);
    }

    public static boolean hasPart(ItemStack gear, PartType partType, Predicate<PartData> predicate) {
        for (PartData partData : getConstructionParts(gear)) {
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
        CompoundTag tags = getData(gear, NBT_ROOT_CONSTRUCTION);
        ListTag tagList = tags.getList(NBT_CONSTRUCTION_PARTS, Tag.TAG_COMPOUND);

        for (Tag nbt : tagList) {
            if (nbt instanceof CompoundTag) {
                CompoundTag partCompound = (CompoundTag) nbt;
                String partKey = partCompound.getString(PartData.NBT_ID);
                if (partKey.equals(partId)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static Optional<PartData> addOrReplacePart(ItemStack gear, PartData part) {
        PartType partType = part.getType();
        PartDataList parts = getConstructionParts(gear);
        List<PartData> partsOfType = parts.getPartsOfType(partType);
        PartData removedPart = null;

        if (!partsOfType.isEmpty() && partsOfType.size() >= partType.getMaxPerItem(GearHelper.getType(gear))) {
            removedPart = partsOfType.get(0);
            parts.remove(removedPart);
            removedPart.onRemoveFromGear(gear);
        }

        parts.add(part);
        writeConstructionParts(gear, parts);

        return Optional.ofNullable(removedPart);
    }

    public static void addPart(ItemStack gear, PartData part) {
        PartDataList parts = getConstructionParts(gear);
        parts.add(part);
        writeConstructionParts(gear, parts);
        part.onAddToGear(gear);
    }

    public static boolean removeFirstPartOfType(ItemStack gear, PartType type) {
        PartDataList parts = getConstructionParts(gear);
        List<PartData> partsOfType = new ArrayList<>(parts.getPartsOfType(type));

        if (!partsOfType.isEmpty()) {
            PartData removed = partsOfType.remove(0);
            parts.remove(removed);
            writeConstructionParts(gear, parts);
            removed.onRemoveFromGear(gear);
            return true;
        }

        return false;
    }

    public static void removeExcessParts(ItemStack gear) {
        for (PartType type : PartType.getValues()) {
            removeExcessParts(gear, type);
        }
    }

    public static void removeExcessParts(ItemStack gear, PartType partType) {
        // Mostly just to correct https://github.com/SilentChaos512/Silent-Gear/issues/242
        PartDataList parts = getConstructionParts(gear);
        List<PartData> partsOfType = new ArrayList<>(parts.getPartsOfType(partType));
        int maxCount = partType.getMaxPerItem(GearHelper.getType(gear));
        int removed = 0;

        while (partsOfType.size() > maxCount) {
            PartData toRemove = partsOfType.get(0);
            partsOfType.remove(toRemove);
            parts.remove(toRemove);
            toRemove.onRemoveFromGear(gear);
            ++removed;
            SilentGear.LOGGER.debug("Removed excess part '{}' from '{}'",
                    toRemove.getDisplayName(gear).getString(),
                    gear.getHoverName().getString());
        }

        if (removed > 0) {
            writeConstructionParts(gear, parts);
        }
    }

    public static void writeConstructionParts(ItemStack gear, Collection<? extends IPartData> parts) {
        if (checkNonGearItem(gear, "writeConstructionParts")) return;

        CompoundTag tags = getData(gear, NBT_ROOT_CONSTRUCTION);
        ListTag tagList = new ListTag();

        // Mains must be first in the list!
        parts.stream().filter(p -> p.getType() == PartType.MAIN)
                .map(p -> p.write(new CompoundTag()))
                .forEach(tagList::add);
        // Write everything else in any order
        parts.stream().filter(p -> p.getType() != PartType.MAIN)
                .map(p -> p.write(new CompoundTag()))
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
        if (checkNonGearItem(gear, "getUUID")) return new UUID(0, 0);

        CompoundTag tags = gear.getOrCreateTag();
        if (!tags.hasUUID(NBT_UUID)) {
            UUID uuid = UUID.randomUUID();
            tags.putUUID(NBT_UUID, uuid);
            return uuid;
        }
        return tags.getUUID(NBT_UUID);
    }

    private static CompoundTag getData(ItemStack gear, String compoundKey) {
        if (checkNonGearItem(gear, "getData")) return new CompoundTag();

        CompoundTag rootTag = gear.getOrCreateTagElement(NBT_ROOT);
        if (!rootTag.contains(compoundKey))
            rootTag.put(compoundKey, new CompoundTag());
        return rootTag.getCompound(compoundKey);
    }

    static CompoundTag getPropertiesData(ItemStack stack) {
        return getData(stack, NBT_ROOT_PROPERTIES);
    }

    static CompoundTag getStatisticsCompound(ItemStack stack) {
        return getData(stack, NBT_ROOT_STATISTICS);
    }

    public static void setExampleTag(ItemStack stack, boolean value) {
        getData(stack, NBT_ROOT_CONSTRUCTION).putBoolean(NBT_IS_EXAMPLE, value);
    }

    public static boolean isExampleGear(ItemStack stack) {
        return getData(stack, NBT_ROOT_CONSTRUCTION).getBoolean(NBT_IS_EXAMPLE);
    }

    @Deprecated
    public static boolean isRandomGradingDone(ItemStack stack) {
        return getData(stack, NBT_ROOT_CONSTRUCTION).getBoolean("RandomGradingDone");
    }

    @Deprecated
    static void setRandomGradingDone(ItemStack stack, boolean value) {
        getData(stack, NBT_ROOT_CONSTRUCTION).putBoolean("RandomGradingDone", value);
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

    private static boolean checkNonGearItem(ItemStack stack, String methodName) {
        if (GearHelper.isGear(stack)) return false;

        SilentGear.LOGGER.error("Called {} on non-gear item, {}", methodName, stack);
        SilentGear.LOGGER.catching(new IllegalArgumentException());
        return true;
    }

    @Mod.EventBusSubscriber(modid = SilentGear.MOD_ID)
    public static final class EventHandler {
        private EventHandler() { }

        @SubscribeEvent
        public static void onPlayerLoggedIn(PlayerLoggedInEvent event) {
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
