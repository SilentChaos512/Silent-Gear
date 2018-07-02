package net.silentchaos512.gear.util;

import com.google.common.collect.Multimap;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagEnd;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.lib.ItemPartData;
import net.silentchaos512.gear.api.lib.PartDataList;
import net.silentchaos512.gear.api.parts.PartMain;
import net.silentchaos512.gear.api.parts.PartRod;
import net.silentchaos512.gear.api.parts.PartTip;
import net.silentchaos512.gear.api.stats.CommonItemStats;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.StatInstance;
import net.silentchaos512.gear.api.stats.StatInstance.Operation;
import net.silentchaos512.gear.api.stats.StatModifierMap;
import net.silentchaos512.lib.util.PlayerHelper;
import net.silentchaos512.lib.util.StackHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.UUID;

public class GearData {

    /**
     * A fake material for tools. Tools need a gear material, even if it's not used. Unfortunately, some mods still
     * reference the gear material instead of calling the appropriate methods.
     */
    public static final ToolMaterial FAKE_MATERIAL = EnumHelper.addToolMaterial("silentgems:fake_material", 1, 512, 5.12f, 5.12f, 32);

    private static final String NBT_ROOT = "ToolCore_Data";
    private static final String NBT_ROOT_CONSTRUCTION = "Construction";
    private static final String NBT_ROOT_PROPERTIES = "Properties";

    private static final String NBT_CONSTRUCTION_PARTS = "Parts";
    private static final String NBT_LOCK_STATS = "LockStats";
    private static final String NBT_IS_EXAMPLE = "IsExample";
    private static final String NBT_SYNERGY_DISPLAY = "synergy";
    private static final String NBT_UUID = "ToolCore_UUID";

    private static final int MAX_MAIN_PARTS = 9;
    private static final int MAX_ROD_PARTS = 1;
    private static final int MAX_TIP_PARTS = 1;

    /**
     * Recalculate gear stats and setup NBT. This should be call ANY TIME an item is modified!
     *
     * @param stack
     */
    public static void recalculateStats(ItemStack stack) {
        getUUID(stack);

        // Has locked stats tag?
        NBTTagCompound propertiesCompound = getData(stack, NBT_ROOT_PROPERTIES);
        if (!propertiesCompound.hasKey(NBT_LOCK_STATS))
            propertiesCompound.setBoolean(NBT_LOCK_STATS, false);
        else if (propertiesCompound.getBoolean(NBT_LOCK_STATS))
            return;

        ICoreItem item = (ICoreItem) stack.getItem();

        // Get parts the item was made with
        PartDataList parts = getConstructionParts(stack);
        if (parts.size() == 0)
            return;
        // Build unique parts set
        PartDataList uniqueParts = parts.getUniqueParts(true);

        // Calculate synergy value
        double synergy = calculateSynergyValue(parts, uniqueParts);

        // Only consider stats relevant to the item
        // Collection<ItemStat> relevantStats = stack.getItem() instanceof ICoreItem
        // ? item.getRelevantStats(stack)
        // : ItemStat.ALL_STATS.values();

        // Get all stat modifiers from all parts and item class modifiers
        Multimap<ItemStat, StatInstance> stats = getStatModifiers(item, parts, synergy);

        // Calculate and write stats
        for (ItemStat stat : stats.keySet()) {
            float value = stat.compute(0f, stats.get(stat));
            // SilentGear.log.debug(stat, value);
            propertiesCompound.setFloat(stat.getUnlocalizedName(), value);
        }
        propertiesCompound.setFloat(NBT_SYNERGY_DISPLAY, (float) synergy);
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
            int pos = 0;
            for (ItemPartData partData : parts) {
                String idSuffix = "_" + (++pos);
                // Allow "duplicate" AVG modifiers
                for (StatInstance inst : partData.getStatModifiers(stat))
                    stats.put(stat, inst.getOp() == Operation.AVG ? inst.copyWithNewId(inst.getId() + idSuffix) : inst);
            }
            // Synergy bonus?
            if (stat.doesSynergyApply())
                stats.put(stat, new StatInstance("synergy_multi", (float) synergy - 1, StatInstance.Operation.MUL2));
        }
        return stats;
    }

    public static double calculateSynergyValue(PartDataList parts, PartDataList uniqueParts) {
        // First, we add a bonus for the number of unique main parts
        double synergy = 1.0 + 0.16 * Math.log(5 * uniqueParts.getMains().size() - 4);
        // Second, reduce synergy for difference in rarity and tier
        ItemPartData primaryMain = parts.getPrimaryMain();
        float primaryRarity = primaryMain == null ? 0 : primaryMain.computeStat(CommonItemStats.RARITY);
        float maxRarity = primaryRarity;
        int maxTier = 0;
        for (ItemPartData data : uniqueParts) {
            maxRarity = Math.max(maxRarity, data.computeStat(CommonItemStats.RARITY));
            maxTier = Math.max(maxTier, data.part.getTier());
        }
        for (ItemPartData data : uniqueParts) {
            if (maxRarity > 0) {
                float rarity = data.computeStat(CommonItemStats.RARITY);
                synergy -= 0.005 * Math.abs(primaryRarity - rarity);
            }
            if (maxTier > 0) {
                int tier = data.part.getTier();
                synergy -= 0.16f * Math.abs(maxTier - tier);
            }
        }
        if (synergy > 1)
            synergy = Math.sqrt(synergy);
        // if (synergy != 1)
        // SilentGear.log.debug(uniqueParts.size(), synergy);
        return synergy;
    }

    public static float getStat(@Nonnull ItemStack stack, @Nonnull ItemStat stat) {
        NBTTagCompound tags = getData(stack, NBT_ROOT_PROPERTIES);
        String key = stat.getUnlocalizedName();
        if (tags.hasKey(key))
            return tags.getFloat(key);
        else
            return stat.getDefaultValue();
    }

    public static int getStatInt(@Nonnull ItemStack stack, @Nonnull ItemStat stat) {
        return Math.round(getStat(stack, stat));
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
                    if (data.part instanceof PartMain && ++mainsFound <= MAX_MAIN_PARTS)
                        list.add(data);
                    else if (data.part instanceof PartRod && ++rodsFound <= MAX_ROD_PARTS)
                        list.add(data);
                    else if (data.part instanceof PartTip && ++tipsFound <= MAX_TIP_PARTS)
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
     * Gets the main part in the given position (zero-indexed)
     *
     * @return The part if it exists in NBT, null if the index is out of bounds, the data is invalid, or the part is not a
     * main part.
     */
    @Nullable
    public static ItemPartData getPartByIndex(ItemStack stack, int index) {
        NBTTagCompound tags = getData(stack, NBT_ROOT_CONSTRUCTION);
        NBTTagList tagList = tags.getTagList(NBT_CONSTRUCTION_PARTS, 10);

        NBTBase nbt = tagList.get(index);
        if (nbt instanceof NBTTagEnd)
            return null;

        ItemPartData data = ItemPartData.readFromNBT((NBTTagCompound) nbt);
        if (data != null && data.part instanceof PartMain)
            return data;

        return null;
    }

    public static void addUpgradePart(ItemStack gear, ItemStack partStack) {
        ItemPartData partData = ItemPartData.fromStack(partStack);
        if (partData == null || partData.part == null)
            return;

        PartDataList parts = getConstructionParts(gear);
        // Only one tip upgrade allowed
        parts.removeIf(data -> data.part instanceof PartTip && partData.part instanceof PartTip);

        parts.add(partData);
        writeConstructionParts(gear, parts);
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

    static void setExampleTag(ItemStack stack, boolean value) {
        getData(stack, NBT_ROOT_CONSTRUCTION).setBoolean(NBT_IS_EXAMPLE, value);
    }

    public static boolean isExampleGear(ItemStack stack) {
        return getData(stack, NBT_ROOT_CONSTRUCTION).getBoolean(NBT_IS_EXAMPLE);
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
