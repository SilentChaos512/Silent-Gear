package net.silentchaos512.gear.api.stats;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.resources.ResourceLocation;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.lib.util.Color;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Stats used by all gear types
 */
public final class ItemStats {
    public static final Codec<ItemStat> BY_NAME_CODEC = ResourceLocation.CODEC.flatXmap(
            id -> Optional.ofNullable(byName(id))
                    .map(DataResult::success)
                    .orElseGet(() -> DataResult.error(() -> "Unknown item stat: " + id)),
            stat -> Optional.of(stat.getStatId())
                    .map(DataResult::success)
                    .orElseGet(() -> DataResult.error(() -> "Unknown item stat: " + stat))
    );

    private static final Map<ResourceLocation, ItemStat> REGISTRY = new LinkedHashMap<>();

    static final List<ItemStat> STATS_IN_ORDER = new ArrayList<>();

    // Generic
    public static final ItemStat DURABILITY = register(new ItemStat(SilentGear.getId("durability"), 0f, 0f, Integer.MAX_VALUE, Color.STEELBLUE, new ItemStat.Properties()
            .displayAsInt()
            .affectedByGrades(true)
            .synergyApplies()
    ));
    public static final ItemStat ARMOR_DURABILITY = register(new ItemStat(SilentGear.getId("armor_durability"), 0f, 0f, Integer.MAX_VALUE / 16, Color.STEELBLUE, new ItemStat.Properties()
            .displayAsInt()
            .displayFormat(ItemStat.DisplayFormat.MULTIPLIER)
            .affectedByGrades(true)
            .synergyApplies()
    ));
    public static final ItemStat REPAIR_EFFICIENCY = register(new ItemStat(SilentGear.getId("repair_efficiency"), 1f, 0f, 1000f, Color.STEELBLUE, new ItemStat.Properties()
            .displayFormat(ItemStat.DisplayFormat.PERCENTAGE)
            .affectedByGrades(false)
    ));
    public static final ItemStat REPAIR_VALUE = register(new ItemStat(SilentGear.getId("repair_value"), 1f, 0f, 1000f, Color.STEELBLUE, new ItemStat.Properties()
            .displayFormat(ItemStat.DisplayFormat.PERCENTAGE)
            .affectedByGrades(false)
    ));
    public static final ItemStat ENCHANTMENT_VALUE = register(new ItemStat(SilentGear.getId("enchantment_value"), 0f, 0f, Integer.MAX_VALUE, Color.STEELBLUE, new ItemStat.Properties()
            .displayAsInt()
            .affectedByGrades(true)
            .synergyApplies()
    ));
    public static final ItemStat CHARGING_VALUE = register(new ItemStat(SilentGear.getId("charging_value"), 1f, 0f, Integer.MAX_VALUE, Color.STEELBLUE, new ItemStat.Properties()
            .affectedByGrades(false)
    ));
    public static final ItemStat RARITY = register(new ItemStat(SilentGear.getId("rarity"), 0f, 0f, Integer.MAX_VALUE, Color.STEELBLUE, new ItemStat.Properties()
            .displayAsInt()
            .affectedByGrades(false)
    ));

    // Harvesting Tools
    public static final ItemStat HARVEST_SPEED = register(new ItemStat(SilentGear.getId("harvest_speed"), 0f, 0f, Integer.MAX_VALUE, Color.SEAGREEN, new ItemStat.Properties()
            .affectedByGrades(true)
            .synergyApplies()
    ));
    public static final ItemStat REACH_DISTANCE = register(new ItemStat(SilentGear.getId("reach_distance"), 0f, -100f, 100f, Color.SEAGREEN, new ItemStat.Properties()
            .affectedByGrades(false)
    ));

    // Melee Weapons
    public static final ItemStat MELEE_DAMAGE = register(new ItemStat(SilentGear.getId("melee_damage"), 0f, 0f, Integer.MAX_VALUE, Color.SANDYBROWN, new ItemStat.Properties()
            .affectedByGrades(true)
            .synergyApplies()
    ));
    public static final ItemStat MAGIC_DAMAGE = register(new ItemStat(SilentGear.getId("magic_damage"), 0f, 0f, Integer.MAX_VALUE, Color.SANDYBROWN, new ItemStat.Properties()
            .affectedByGrades(true)
            .synergyApplies()
            .hidden()
    ));
    public static final ItemStat ATTACK_SPEED = register(new ItemStat(SilentGear.getId("attack_speed"), 0f, -3.9f, 4f, Color.SANDYBROWN, new ItemStat.Properties()
            .affectedByGrades(false)
    ));
    public static final ItemStat ATTACK_REACH = register(new ItemStat(SilentGear.getId("attack_reach"), 3f, 0f, 100f, Color.SANDYBROWN, new ItemStat.Properties()
            .baseValue(3f)
            .affectedByGrades(false)
    ));

    // Ranged Weapons
    public static final ItemStat RANGED_DAMAGE = register(new ItemStat(SilentGear.getId("ranged_damage"), 0f, 0f, Integer.MAX_VALUE, Color.SKYBLUE, new ItemStat.Properties()
            .displayFormat(ItemStat.DisplayFormat.MULTIPLIER)
            .affectedByGrades(true)
            .synergyApplies()
    ));
    public static final ItemStat RANGED_SPEED = register(new ItemStat(SilentGear.getId("ranged_speed"), 0f, -10f, 10f, Color.SKYBLUE, new ItemStat.Properties()
            .displayFormat(ItemStat.DisplayFormat.MULTIPLIER)
            .affectedByGrades(false)
    ));
    public static final ItemStat PROJECTILE_SPEED = register(new ItemStat(SilentGear.getId("projectile_speed"), 1f, 0f, Integer.MAX_VALUE, Color.SKYBLUE, new ItemStat.Properties()
            .displayFormat(ItemStat.DisplayFormat.MULTIPLIER)
            .affectedByGrades(false)
            .synergyApplies()
    ));
    public static final ItemStat PROJECTILE_ACCURACY = register(new ItemStat(SilentGear.getId("projectile_accuracy"), 1f, 0f, 10000f, Color.SKYBLUE, new ItemStat.Properties()
            .displayFormat(ItemStat.DisplayFormat.PERCENTAGE)
            .affectedByGrades(false)
    ));

    // Armor
    public static final ItemStat ARMOR = register(new SplitItemStat(SilentGear.getId("armor"), 0f, 0f, Integer.MAX_VALUE, Color.VIOLET,
            ImmutableMap.of(
                    GearType.HELMET, 3f,
                    GearType.CHESTPLATE, 8f,
                    GearType.LEGGINGS, 6f,
                    GearType.BOOTS, 3f
            ),
            new ItemStat.Properties()
                    .affectedByGrades(true)
                    .synergyApplies()
    ));
    public static final ItemStat ARMOR_TOUGHNESS = register(new EvenSplitItemStat(SilentGear.getId("armor_toughness"), 0f, 0f, Integer.MAX_VALUE, Color.VIOLET,
            4,
            new ItemStat.Properties()
                    .affectedByGrades(true)
                    .synergyApplies()
    ));
    public static final ItemStat KNOCKBACK_RESISTANCE = register(new ItemStat(SilentGear.getId("knockback_resistance"), 0f, 0f, Integer.MAX_VALUE, Color.VIOLET, new ItemStat.Properties()
            .affectedByGrades(true)
            .synergyApplies()
    ));
    public static final ItemStat MAGIC_ARMOR = register(new EvenSplitItemStat(SilentGear.getId("magic_armor"), 0f, 0f, Integer.MAX_VALUE, Color.VIOLET,
            4,
            new ItemStat.Properties()
                    .affectedByGrades(true)
                    .synergyApplies()
    ));

    private ItemStats() {
    }

    public static ItemStat register(ItemStat stat) {
        REGISTRY.put(stat.getStatId(), stat);
        return stat;
    }

    /**
     * Returns a collection of all created stats in a pre-determined order.
     *
     * @return Ordered stats list
     */
    public static Collection<ItemStat> allStatsOrdered() {
        return Collections.unmodifiableList(STATS_IN_ORDER);
    }

    public static Collection<ItemStat> allStatsOrderedExcluding(Collection<ItemStat> exclude) {
        Collection<ItemStat> ret = new ArrayList<>(STATS_IN_ORDER);
        ret.removeIf(exclude::contains);
        return ret;
    }

    /**
     * Gets a stat by name. If the namespace is omitted, the silentgear namespace is used.
     *
     * @param name The stat name
     * @return The stat, or null if it does not exist
     */
    @Nullable
    public static ItemStat byName(String name) {
        ResourceLocation id = SilentGear.getIdWithDefaultNamespace(name);
        return id != null ? REGISTRY.get(id) : null;
    }

    /**
     * Gets a stat by registry name
     *
     * @param id The stat's registry name
     * @return The stat, or null if it does not exist
     */
    @Nullable
    public static ItemStat byName(ResourceLocation id) {
        return REGISTRY.get(id);
    }

    @Nullable
    public static ItemStat get(IItemStat stat) {
        return REGISTRY.get(stat.getStatId());
    }
}
