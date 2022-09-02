package net.silentchaos512.gear.api.stats;

import com.google.common.collect.ImmutableMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.*;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.utils.Color;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * Stats used by all gear types
 */
public final class ItemStats {
    static final List<ItemStat> STATS_IN_ORDER = new ArrayList<>();

    private static Supplier<IForgeRegistry<ItemStat>> REGISTRY;
    private static Lazy<DeferredRegister<ItemStat>> DEFERRED_REGISTER = Lazy.of(() -> DeferredRegister.create(REGISTRY.get(), SilentGear.MOD_ID));

    public static IForgeRegistry<ItemStat> getRegistry() {
        return REGISTRY.get();
    }

    // Generic
    public static final RegistryObject<ItemStat> DURABILITY = DEFERRED_REGISTER.get().register("durability", () -> new ItemStat(0f, 0f,Integer.MAX_VALUE, Color.STEELBLUE, new ItemStat.Properties()
            .displayAsInt()
            .affectedByGrades(true)
            .synergyApplies()
    ));
    public static final RegistryObject<ItemStat> ARMOR_DURABILITY = DEFERRED_REGISTER.get().register("armor_durability", () -> new ItemStat(0f, 0f, Integer.MAX_VALUE / 16, Color.STEELBLUE, new ItemStat.Properties()
            .displayAsInt()
            .displayFormat(ItemStat.DisplayFormat.MULTIPLIER)
            .affectedByGrades(true)
            .synergyApplies()
    ));
    public static final RegistryObject<ItemStat> REPAIR_EFFICIENCY = DEFERRED_REGISTER.get().register("repair_efficiency", () -> new ItemStat(1f, 0f, 1000f, Color.STEELBLUE, new ItemStat.Properties()
            .displayFormat(ItemStat.DisplayFormat.PERCENTAGE)
            .affectedByGrades(false)
    ));
    public static final RegistryObject<ItemStat> REPAIR_VALUE = DEFERRED_REGISTER.get().register("repair_value", () -> new ItemStat(1f, 0f, 1000f, Color.STEELBLUE, new ItemStat.Properties()
            .displayFormat(ItemStat.DisplayFormat.PERCENTAGE)
            .affectedByGrades(false)
    ));
    public static final RegistryObject<ItemStat> ENCHANTABILITY = DEFERRED_REGISTER.get().register("enchantability", () -> new ItemStat(0f, 0f, Integer.MAX_VALUE, Color.STEELBLUE, new ItemStat.Properties()
            .displayAsInt()
            .affectedByGrades(true)
            .synergyApplies()
    ));
    public static final RegistryObject<ItemStat> CHARGEABILITY = DEFERRED_REGISTER.get().register("chargeability", () -> new ItemStat(1f, 0f, Integer.MAX_VALUE, Color.STEELBLUE, new ItemStat.Properties()
            .affectedByGrades(false)
    ));
    public static final RegistryObject<ItemStat> RARITY = DEFERRED_REGISTER.get().register("rarity", () -> new ItemStat(0f, 0f, Integer.MAX_VALUE, Color.STEELBLUE, new ItemStat.Properties()
            .displayAsInt()
            .affectedByGrades(false)
    ));

    // Harvesting Tools
    public static final RegistryObject<ItemStat> HARVEST_LEVEL = DEFERRED_REGISTER.get().register("harvest_level", () -> new ItemStat(0f, 0f, Integer.MAX_VALUE, Color.SEAGREEN, new ItemStat.Properties()
            .defaultOp(StatInstance.Operation.MAX)
            .displayAsInt()
            .affectedByGrades(false)
    ));
    public static final RegistryObject<ItemStat> HARVEST_SPEED = DEFERRED_REGISTER.get().register("harvest_speed", () -> new ItemStat(0f, 0f, Integer.MAX_VALUE, Color.SEAGREEN, new ItemStat.Properties()
            .affectedByGrades(true)
            .synergyApplies()
    ));
    public static final RegistryObject<ItemStat> REACH_DISTANCE = DEFERRED_REGISTER.get().register("reach_distance", () -> new ItemStat(0f, -100f, 100f, Color.SEAGREEN, new ItemStat.Properties()
            .affectedByGrades(false)
    ));

    // Melee Weapons
    public static final RegistryObject<ItemStat> MELEE_DAMAGE = DEFERRED_REGISTER.get().register("melee_damage", () -> new ItemStat(0f, 0f, Integer.MAX_VALUE, Color.SANDYBROWN, new ItemStat.Properties()
            .affectedByGrades(true)
            .synergyApplies()
    ));
    public static final RegistryObject<ItemStat> MAGIC_DAMAGE = DEFERRED_REGISTER.get().register("magic_damage", () -> new ItemStat(0f, 0f, Integer.MAX_VALUE, Color.SANDYBROWN, new ItemStat.Properties()
            .affectedByGrades(true)
            .synergyApplies()
            .hidden()
    ));
    public static final RegistryObject<ItemStat> ATTACK_SPEED = DEFERRED_REGISTER.get().register("attack_speed", () -> new ItemStat(0f, -3.9f, 4f, Color.SANDYBROWN, new ItemStat.Properties()
            .affectedByGrades(false)
    ));
    public static final RegistryObject<ItemStat> ATTACK_REACH = DEFERRED_REGISTER.get().register("attack_reach", () -> new ItemStat(3f, 0f, 100f, Color.SANDYBROWN, new ItemStat.Properties()
            .baseValue(3f)
            .affectedByGrades(false)
    ));

    // Ranged Weapons
    public static final RegistryObject<ItemStat> RANGED_DAMAGE = DEFERRED_REGISTER.get().register("ranged_damage", () -> new ItemStat(0f, 0f, Integer.MAX_VALUE, Color.SKYBLUE, new ItemStat.Properties()
            .displayFormat(ItemStat.DisplayFormat.MULTIPLIER)
            .affectedByGrades(true)
            .synergyApplies()
    ));
    public static final RegistryObject<ItemStat> RANGED_SPEED = DEFERRED_REGISTER.get().register("ranged_speed", () -> new ItemStat(0f, -10f, 10f, Color.SKYBLUE, new ItemStat.Properties()
            .displayFormat(ItemStat.DisplayFormat.MULTIPLIER)
            .affectedByGrades(false)
    ));
    public static final RegistryObject<ItemStat> PROJECTILE_SPEED = DEFERRED_REGISTER.get().register("projectile_speed", () -> new ItemStat(1f, 0f, Integer.MAX_VALUE, Color.SKYBLUE, new ItemStat.Properties()
            .displayFormat(ItemStat.DisplayFormat.MULTIPLIER)
            .affectedByGrades(false)
            .synergyApplies()
    ));
    public static final RegistryObject<ItemStat> PROJECTILE_ACCURACY = DEFERRED_REGISTER.get().register("projectile_accuracy", () -> new ItemStat(1f, 0f, 10000f, Color.SKYBLUE, new ItemStat.Properties()
            .displayFormat(ItemStat.DisplayFormat.PERCENTAGE)
            .affectedByGrades(false)
    ));

    // Armor
    public static final RegistryObject<ItemStat> ARMOR = DEFERRED_REGISTER.get().register("armor", () -> new SplitItemStat(0f, 0f, Integer.MAX_VALUE, Color.VIOLET,
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
    public static final RegistryObject<ItemStat> ARMOR_TOUGHNESS = DEFERRED_REGISTER.get().register("armor_toughness", () -> new EvenSplitItemStat(0f, 0f, Integer.MAX_VALUE, Color.VIOLET,
            4,
            new ItemStat.Properties()
                    .affectedByGrades(true)
                    .synergyApplies()
    ));
    public static final RegistryObject<ItemStat> KNOCKBACK_RESISTANCE = DEFERRED_REGISTER.get().register("knockback_resistance", () -> new ItemStat(0f, 0f, Integer.MAX_VALUE, Color.VIOLET, new ItemStat.Properties()
            .affectedByGrades(true)
            .synergyApplies()
    ));
    public static final RegistryObject<ItemStat> MAGIC_ARMOR = DEFERRED_REGISTER.get().register("magic_armor", () -> new EvenSplitItemStat(0f, 0f, Integer.MAX_VALUE, Color.VIOLET,
            4,
            new ItemStat.Properties()
                    .affectedByGrades(true)
                    .synergyApplies()
    ));

    private ItemStats() {}

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
        return id != null ? getRegistry().getValue(id) : null;
    }

    /**
     * Gets a stat by registry name
     *
     * @param id The stat's registry name
     * @return The stat, or null if it does not exist
     */
    @Nullable
    public static ItemStat byName(ResourceLocation id) {
        return getRegistry().getValue(id);
    }

    @Nullable
    public static ItemStat get(IItemStat stat) {
        return getRegistry().getValue(stat.getStatId());
    }

    // region Registry creation - other mods should not call these methods!

    public static void createRegistry(NewRegistryEvent event) {
        REGISTRY = event.create(new RegistryBuilder<ItemStat>()
                .setName(SilentGear.getId("stat"))
        );
    }

    public static void registerStats(RegistryEvent.Register<ItemStat> event) {
        // TODO: Replace with DeferredRegister?
        register(event.getRegistry(), DURABILITY, "durability");
        register(event.getRegistry(), ARMOR_DURABILITY, "armor_durability");
        register(event.getRegistry(), REPAIR_EFFICIENCY, "repair_efficiency");
        register(event.getRegistry(), REPAIR_VALUE, "repair_value");
        register(event.getRegistry(), ENCHANTABILITY, "enchantability");
        register(event.getRegistry(), CHARGEABILITY, "chargeability");
        register(event.getRegistry(), RARITY, "rarity");
        register(event.getRegistry(), HARVEST_LEVEL, "harvest_level");
        register(event.getRegistry(), HARVEST_SPEED, "harvest_speed");
        register(event.getRegistry(), REACH_DISTANCE, "reach_distance");
        register(event.getRegistry(), MELEE_DAMAGE, "melee_damage");
        register(event.getRegistry(), MAGIC_DAMAGE, "magic_damage");
        register(event.getRegistry(), ATTACK_SPEED, "attack_speed");
        register(event.getRegistry(), ATTACK_REACH, "attack_reach");
        register(event.getRegistry(), RANGED_DAMAGE, "ranged_damage");
        register(event.getRegistry(), RANGED_SPEED, "ranged_speed");
        register(event.getRegistry(), PROJECTILE_ACCURACY, "projectile_accuracy");
        register(event.getRegistry(), PROJECTILE_SPEED, "projectile_speed");
        register(event.getRegistry(), ARMOR, "armor");
        register(event.getRegistry(), ARMOR_TOUGHNESS, "armor_toughness");
        register(event.getRegistry(), KNOCKBACK_RESISTANCE, "knockback_resistance");
        register(event.getRegistry(), MAGIC_ARMOR, "magic_armor");
    }

    private static void register(IForgeRegistry<ItemStat> registry, ItemStat stat, String name) {
        registry.register(stat.setRegistryName(SilentGear.getId(name)));
    }

    //endregion
}
