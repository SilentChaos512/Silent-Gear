package net.silentchaos512.gear.util;

import com.google.common.collect.Multimap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagEnd;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.parts.*;
import net.silentchaos512.gear.api.stats.CommonItemStats;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.StatInstance;
import net.silentchaos512.gear.api.stats.StatInstance.Operation;
import net.silentchaos512.gear.api.stats.StatModifierMap;
import net.silentchaos512.gear.api.traits.Trait;
import net.silentchaos512.gear.init.ModTraits;
import net.silentchaos512.lib.util.PlayerHelper;
import net.silentchaos512.lib.util.StackHelper;

import javax.annotation.Nullable;
import java.util.*;

public final class GearData {
    /**
     * A fake material for tools. Tools need a gear material, even if it's not used. Unfortunately,
     * some mods still reference the gear material instead of calling the appropriate methods.
     */
    public static final ToolMaterial FAKE_MATERIAL = EnumHelper.addToolMaterial("silentgems:fake_material", 1, 512, 5.12f, 5.12f, 32);

    private static final String NBT_ROOT = "ToolCore_Data";                 // TODO: Update this
    private static final String NBT_ROOT_CONSTRUCTION = "Construction";
    private static final String NBT_ROOT_PROPERTIES = "Properties";
    private static final String NBT_ROOT_MODEL_KEYS = "ModelKeys";
    private static final String NBT_ROOT_STATISTICS = "Statistics";

    private static final String NBT_CONSTRUCTION_PARTS = "Parts";
    private static final String NBT_LOCK_STATS = "LockStats";
    private static final String NBT_IS_EXAMPLE = "IsExample";
    private static final String NBT_RANDOM_GRADING_DONE = "RandomGradingDone";
    private static final String NBT_SYNERGY_DISPLAY = "synergy";
    private static final String NBT_UUID = "ToolCore_UUID";

    private static final String NBT_BROKEN_COUNT = "BrokenCount";
    private static final String NBT_REPAIR_COUNT = "RepairCount";

    private static final int MAX_MAIN_PARTS = 9;
    private static final int MAX_ROD_PARTS = 1;
    private static final int MAX_TIP_PARTS = 1;

    private GearData() {throw new IllegalAccessError("Utility class");}

    /**
     * Recalculate gear stats and setup NBT. This should be called ANY TIME an item is modified!
     */
    public static void recalculateStats(EntityPlayer player, ItemStack stack) {
        getUUID(stack);
        ICoreItem item = (ICoreItem) stack.getItem();
        PartDataList parts = getConstructionParts(stack);

        NBTTagCompound propertiesCompound = getData(stack, NBT_ROOT_PROPERTIES);
        if (!propertiesCompound.hasKey(NBT_LOCK_STATS))
            propertiesCompound.setBoolean(NBT_LOCK_STATS, false);

        final boolean statsUnlocked = !propertiesCompound.getBoolean(NBT_LOCK_STATS);
        final boolean partsListValid = !parts.isEmpty() && !parts.getMains().isEmpty();
        if (statsUnlocked && partsListValid) {
            // We should recalculate the item's stats!
            PartDataList uniqueParts = parts.getUniqueParts(true);
            Map<Trait, Integer> traits = TraitHelper.getTraits(parts);

            double synergy = calculateSynergyValue(parts, uniqueParts, traits);

            // Only consider stats relevant to the item
            // Collection<ItemStat> relevantStats = stack.getItem() instanceof ICoreItem
            // ? item.getRelevantStats(stack)
            // : ItemStat.ALL_STATS.values();

            // Get all stat modifiers from all parts and item class modifiers
            Multimap<ItemStat, StatInstance> stats = getStatModifiers(item, parts, synergy);

            // Calculate and write stats
            final float damageRatio = (float) stack.getItemDamage() / (float) stack.getMaxDamage();
            for (ItemStat stat : stats.keySet()) {
                final float initialValue = stat.compute(0f, stats.get(stat));
                final float value = TraitHelper.activateTraits(stack, initialValue, (trait, level, val) ->
                        trait.onGetStat(null, stat, level, stack, val, damageRatio));
                // SilentGear.log.debug(stat, value);
                propertiesCompound.setFloat(stat.getName().getPath(), value);
            }

            // Cache traits in properties compound as well
            NBTTagList traitList = new NBTTagList();
            for (Trait trait : traits.keySet()) {
                int level = traits.get(trait);
                traitList.appendTag(trait.writeToNBT(level));
            }
            propertiesCompound.setTag("Traits", traitList);

            propertiesCompound.setFloat(NBT_SYNERGY_DISPLAY, (float) synergy);
        }

        // Update model keys even if we didn't update stats
        createAndSaveModelKeys(stack, item, parts);
    }

    @Deprecated
    public static void recalculateStats(ItemStack stack) {recalculateStats(null, stack);}

    private static void createAndSaveModelKeys(ItemStack stack, ICoreItem item, PartDataList parts) {
        // Save model keys for performance
        // Remove the old keys first, then get new ones from ICoreItem
        stack.getOrCreateSubCompound(NBT_ROOT).removeTag(NBT_ROOT_MODEL_KEYS);
        NBTTagCompound modelKeys = getData(stack, NBT_ROOT_MODEL_KEYS);
        for (int i = 0; i < item.getAnimationFrames(); ++i) {
            modelKeys.setString(Integer.toString(i), item.getModelKey(stack, i, parts.toArray(new ItemPartData[0])));
        }
    }

    public static String getCachedModelKey(ItemStack stack, int animationFrame) {
        if (!(stack.getItem() instanceof ICoreItem))
            return "Invalid item!";

        NBTTagCompound tags = getData(stack, NBT_ROOT_MODEL_KEYS);
        String key = Integer.toString(animationFrame);
        if (!tags.hasKey(key))
            tags.setString(key, ((ICoreItem) stack.getItem()).getModelKey(stack, animationFrame));
        return tags.getString(Integer.toString(animationFrame));
    }

    public static Multimap<ItemStat, StatInstance> getStatModifiers(@Nullable ICoreItem item, PartDataList parts, double synergy) {
        Multimap<ItemStat, StatInstance> stats = new StatModifierMap();
        for (ItemStat stat : ItemStat.ALL_STATS.values()) {
            // Item class modifiers
            if (item != null) {
                stats.put(stat, item.getConfig().getBaseModifier(stat));
                stats.put(stat, item.getConfig().getStatModifier(stat));
            }
            // Part modifiers
            int partCount = 0;
            for (ItemPartData partData : parts) {
                String idSuffix = "_" + (++partCount);
                // Allow "duplicate" AVG modifiers
                for (StatInstance inst : partData.getStatModifiers(stat)) {
                    if (inst.getOp() == Operation.AVG && stat.isAffectedByGrades()) {
                        float gradeBonus = 1f + partData.getGrade().bonusPercent / 100f;
                        float statValue = inst.getValue() * gradeBonus;
                        stats.put(stat, new StatInstance(inst.getId() + idSuffix, statValue, Operation.AVG));
                    } else {
                        stats.put(stat, inst);
                    }
                }
            }
            // Synergy bonus?
            if (stat.doesSynergyApply())
                stats.put(stat, new StatInstance("synergy_multi", (float) synergy - 1, StatInstance.Operation.MUL2));
        }
        return stats;
    }

    public static double calculateSynergyValue(PartDataList parts, PartDataList uniqueParts, Map<Trait, Integer> traits) {
        // First, we add a bonus for the number of unique main parts
        double synergy = 1.0 + 0.16 * Math.log(5 * uniqueParts.getMains().size() - 4);
        // Second, reduce synergy for difference in rarity and tier
        ItemPartData primaryMain = parts.getPrimaryMain();
        float primaryRarity = primaryMain == null ? 0 : primaryMain.computeStat(CommonItemStats.RARITY);
        float maxRarity = primaryRarity;
        int maxTier = 0;
        for (ItemPartData data : uniqueParts) {
            maxRarity = Math.max(maxRarity, data.computeStat(CommonItemStats.RARITY));
            maxTier = Math.max(maxTier, data.getPart().getTier());
        }
        for (ItemPartData data : uniqueParts) {
            if (maxRarity > 0) {
                float rarity = data.computeStat(CommonItemStats.RARITY);
                synergy -= 0.005 * Math.abs(primaryRarity - rarity);
            }
            if (maxTier > 0) {
                int tier = data.getPart().getTier();
                synergy -= 0.16f * Math.abs(maxTier - tier);
            }
        }
        if (synergy > 1) {
            synergy = Math.sqrt(synergy);

            if (traits.containsKey(ModTraits.synergyBoost)) {
                final double oldVal = synergy;
                int level = traits.get(ModTraits.synergyBoost);
                synergy += synergy * level * ModTraits.SYNERGY_BOOST_MULTI;
//                SilentGear.log.debug("synergy: {} -> {}", oldVal, synergy);
            }
        }


        return synergy;
    }

    public static float getStat(ItemStack stack, ItemStat stat) {
        NBTTagCompound tags = getData(stack, NBT_ROOT_PROPERTIES);
        String key = stat.getName().getPath();

        if (tags.hasKey(key)) {
            return tags.getFloat(key);
        } else {
            return stat.getDefaultValue();
        }
    }

    public static int getStatInt(ItemStack stack, ItemStat stat) {
        return Math.round(getStat(stack, stat));
    }

    public static boolean hasLockedStats(ItemStack stack) {
        return getData(stack, NBT_ROOT_PROPERTIES).getBoolean(NBT_LOCK_STATS);
    }

    public static void setLockedStats(ItemStack stack, boolean lock) {
        getData(stack, NBT_ROOT_PROPERTIES).setBoolean(NBT_LOCK_STATS, lock);
    }

    public static PartDataList getConstructionParts(ItemStack stack) {
        NBTTagCompound tags = getData(stack, NBT_ROOT_CONSTRUCTION);
        NBTTagList tagList = tags.getTagList(NBT_CONSTRUCTION_PARTS, 10);
        PartDataList list = PartDataList.of();
        int mainsFound = 0;
        int rodsFound = 0;
        int tipsFound = 0;

        for (NBTBase nbt : tagList) {
            if (nbt instanceof NBTTagCompound) {
                NBTTagCompound partCompound = (NBTTagCompound) nbt;
                ItemPartData data = ItemPartData.readFromNBT(partCompound);
                if (data != null) {
                    if (data.getPart() instanceof PartMain && ++mainsFound <= MAX_MAIN_PARTS)
                        list.add(data);
                    else if (data.getPart() instanceof PartRod && ++rodsFound <= MAX_ROD_PARTS)
                        list.add(data);
                    else if (data.getPart() instanceof PartTip && ++tipsFound <= MAX_TIP_PARTS)
                        list.add(data);
                    else
                        list.add(data);
                }
            }
        }
        return list;
    }

    public static float getSynergyDisplayValue(ItemStack equipment) {
        return getData(equipment, NBT_ROOT_PROPERTIES).getFloat(NBT_SYNERGY_DISPLAY);
    }

    @Nullable
    public static ItemPartData getPrimaryPart(ItemStack stack) {
        return getPartByIndex(stack, 0);
    }

    @Nullable
    public static ItemPartData getSecondaryPart(ItemStack stack) {
        return getPartByIndex(stack, 1);
    }

    /**
     * Gets the primary part, but only the part itself. Grade and crafting stack are omitted so that
     * the cached ItemPartData can be retrieved instead of constructing a new one.
     *
     * @param stack The gear item
     * @return Cached part data excluding grade and crafting item, or null if it does not exist.
     */
    @Nullable
    public static ItemPartData getPrimaryRenderPartFast(ItemStack stack) {
        NBTTagCompound tags = getData(stack, NBT_ROOT_CONSTRUCTION);
        NBTTagList tagList = tags.getTagList(NBT_CONSTRUCTION_PARTS, 10);

        NBTBase nbt = tagList.get(0);
        if (nbt instanceof NBTTagEnd) return null;

        ItemPart part = ItemPart.fromNBT((NBTTagCompound) nbt);
        if (part == null) return null;
        return ItemPartData.instance(part);
    }

    /**
     * Gets the main part in the given position (zero-indexed)
     *
     * @return The part if it exists in NBT, null if the index is out of bounds, the data is
     * invalid, or the part is not a main part.
     */
    @Nullable
    public static ItemPartData getPartByIndex(ItemStack stack, int index) {
        NBTTagCompound tags = getData(stack, NBT_ROOT_CONSTRUCTION);
        NBTTagList tagList = tags.getTagList(NBT_CONSTRUCTION_PARTS, 10);

        NBTBase nbt = tagList.get(index);
        if (nbt instanceof NBTTagEnd) return null;

        ItemPartData data = ItemPartData.readFromNBT((NBTTagCompound) nbt);
        return data != null && data.getPart() instanceof PartMain ? data : null;
    }

    public static void addUpgradePart(ItemStack gear, ItemStack partStack) {
        ItemPartData part = ItemPartData.fromStack(partStack);
        if (part != null) addUpgradePart(gear, part);
    }

    public static void addUpgradePart(ItemStack gear, ItemPartData part) {
        if (!(gear.getItem() instanceof ICoreItem)) {
            SilentGear.log.error("Tried to add upgrade part to non-gear item {}", gear);
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
        for (ItemPartData partInList : parts)
            if (partInList.getPart() == part.getPart())
                return;

        parts.add(part);
        writeConstructionParts(gear, parts);
    }

    /**
     * Determine if the gear has the specified part. This scans the construction NBT directly for
     * speed, no part data list is created. Compares part registry names only.
     *
     * @param gear    The gear item
     * @param upgrade The part to check for
     * @return True if the item has the part in its construction, false otherwise
     */
    public static boolean hasPart(ItemStack gear, ItemPart upgrade) {
        NBTTagCompound tags = getData(gear, NBT_ROOT_CONSTRUCTION);
        NBTTagList tagList = tags.getTagList(NBT_CONSTRUCTION_PARTS, 10);
        String upgradeName = upgrade.getRegistryName().toString();

        for (NBTBase nbt : tagList) {
            if (nbt instanceof NBTTagCompound) {
                NBTTagCompound partCompound = (NBTTagCompound) nbt;
                String partKey = partCompound.getString(ItemPart.NBT_KEY);
                if (partKey.equals(upgradeName))
                    return true;
            }
        }

        return false;
    }

    public static void writeConstructionParts(ItemStack stack, Collection<ItemPartData> parts) {
        NBTTagCompound tags = getData(stack, NBT_ROOT_CONSTRUCTION);
        NBTTagList tagList = new NBTTagList();

        for (ItemPartData data : parts)
            tagList.appendTag(data.writeToNBT(new NBTTagCompound()));
        tags.setTag(NBT_CONSTRUCTION_PARTS, tagList);
    }

    /**
     * Gets the item's UUID, creating it if it doesn't have one yet.
     *
     * @param gear ItemStack of an ICoreItem
     * @return The UUID, or null if gear's item is not an ICoreItem
     */
    public static UUID getUUID(ItemStack gear) {
        if (!(gear.getItem() instanceof ICoreItem))
            return null;

        NBTTagCompound tags = StackHelper.getTagCompound(gear, true);
        if (!tags.hasUniqueId(NBT_UUID)) {
            UUID uuid = UUID.randomUUID();
            tags.setUniqueId(NBT_UUID, uuid);
            return uuid;
        }
        return tags.getUniqueId(NBT_UUID);
    }

    private static NBTTagCompound getData(ItemStack stack, String compoundKey) {
        NBTTagCompound rootTag = stack.getOrCreateSubCompound(NBT_ROOT);
        if (!rootTag.hasKey(compoundKey))
            rootTag.setTag(compoundKey, new NBTTagCompound());
        return rootTag.getCompoundTag(compoundKey);
    }

    static NBTTagCompound getPropertiesData(ItemStack stack) {
        return getData(stack, NBT_ROOT_PROPERTIES);
    }

    static NBTTagCompound getStatisticsCompound(ItemStack stack) {
        return getData(stack, NBT_ROOT_STATISTICS);
    }

    static void setExampleTag(ItemStack stack, boolean value) {
        getData(stack, NBT_ROOT_CONSTRUCTION).setBoolean(NBT_IS_EXAMPLE, value);
    }

    public static boolean isExampleGear(ItemStack stack) {
        return getData(stack, NBT_ROOT_CONSTRUCTION).getBoolean(NBT_IS_EXAMPLE);
    }

    public static boolean isRandomGradingDone(ItemStack stack) {
        return getData(stack, NBT_ROOT_CONSTRUCTION).getBoolean(NBT_RANDOM_GRADING_DONE);
    }

    public static void setRandomGradingDone(ItemStack stack, boolean value) {
        getData(stack, NBT_ROOT_CONSTRUCTION).setBoolean(NBT_RANDOM_GRADING_DONE, value);
    }

    public static int getBrokenCount(ItemStack stack) {
        return getData(stack, NBT_ROOT_CONSTRUCTION).getInteger(NBT_BROKEN_COUNT);
    }

    public static void incrementBrokenCount(ItemStack stack) {
        getData(stack, NBT_ROOT_CONSTRUCTION).setInteger(NBT_BROKEN_COUNT, getBrokenCount(stack) + 1);
    }

    public static int getRepairCount(ItemStack stack) {
        return getData(stack, NBT_ROOT_CONSTRUCTION).getInteger(NBT_REPAIR_COUNT);
    }

    public static void incrementRepairCount(ItemStack stack, int amount) {
        getData(stack, NBT_ROOT_CONSTRUCTION).setInteger(NBT_REPAIR_COUNT, getRepairCount(stack) + amount);
    }

    public static class EventHandler {

        public static final EventHandler INSTANCE = new EventHandler();

        private EventHandler() {
        }

        @SubscribeEvent
        public void onPlayerLoggedIn(PlayerLoggedInEvent event) {
            for (ItemStack stack : PlayerHelper.getNonEmptyStacks(event.player, s -> s.getItem() instanceof ICoreItem)) {
                recalculateStats(stack);
            }
        }
    }
}
