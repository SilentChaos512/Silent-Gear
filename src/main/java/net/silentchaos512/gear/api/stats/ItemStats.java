package net.silentchaos512.gear.api.stats;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.lib.util.Lazy;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Stats used by all equipment types.
 *
 * @author SilentChaos512
 * @since Experimental
 */
public final class ItemStats {
    static final List<ItemStat> STATS_IN_ORDER = new ArrayList<>();

    public static final Lazy<IForgeRegistry<ItemStat>> REGISTRY = Lazy.of(() -> new RegistryBuilder<ItemStat>()
            .setType(ItemStat.class)
            .setName(SilentGear.getId("stat"))
//            .add((IForgeRegistry.AddCallback<ItemStat>) (owner, stage, id, obj, oldObj) -> STATS_IN_ORDER.add(obj))
//            .add((IForgeRegistry.ClearCallback<ItemStat>) (owner, stage) -> STATS_IN_ORDER.clear())
            .create());

    // Generic
    public static final ItemStat DURABILITY = new ItemStat(1f, 1f, Integer.MAX_VALUE, TextFormatting.BLUE, new ItemStat.Properties().displayAsInt().synergyApplies());
    public static final ItemStat ARMOR_DURABILITY = new ItemStat(1f, 1f, Integer.MAX_VALUE / 16, TextFormatting.BLUE, new ItemStat.Properties().displayAsInt().synergyApplies());
    public static final ItemStat REPAIR_EFFICIENCY = new ItemStat(1f, 0f, 1000f, TextFormatting.BLUE, new ItemStat.Properties());
    public static final ItemStat ENCHANTABILITY = new ItemStat(0f, 0f, 10000f, TextFormatting.BLUE, new ItemStat.Properties().displayAsInt().synergyApplies());
    public static final ItemStat RARITY = new ItemStat(0f, 0f, 10000f, TextFormatting.BLUE, new ItemStat.Properties().displayAsInt().hidden());

    // Harvesting Tools
    public static final ItemStat HARVEST_LEVEL = new ItemStat(0f, 0f, 10000f, TextFormatting.YELLOW, new ItemStat.Properties().defaultOp(StatInstance.Operation.MAX).displayAsInt());
    public static final ItemStat HARVEST_SPEED = new ItemStat(1f, 0f, 10000f, TextFormatting.YELLOW, new ItemStat.Properties().synergyApplies().missingRodFunction(f -> Math.max(2, f / 8)));
    public static final ItemStat REACH_DISTANCE = new ItemStat(0f, -100f, 100f, TextFormatting.YELLOW, new ItemStat.Properties().missingRodFunction(f -> f - 1.5f));

    // Melee Weapons
    public static final ItemStat MELEE_DAMAGE = new ItemStat(0f, 0f, 10000f, TextFormatting.GREEN, new ItemStat.Properties().synergyApplies().missingRodFunction(f -> f / 2));
    public static final ItemStat MAGIC_DAMAGE = new ItemStat(0f, 0f, 10000f, TextFormatting.GREEN, new ItemStat.Properties().synergyApplies());
    public static final ItemStat ATTACK_SPEED = new ItemStat(0f, -4f, 4f, TextFormatting.GREEN, new ItemStat.Properties());

    // Ranged Weapons
    public static final ItemStat RANGED_DAMAGE = new ItemStat(0f, 0f, 10000f, TextFormatting.AQUA, new ItemStat.Properties().synergyApplies());
    public static final ItemStat RANGED_SPEED = new ItemStat(0f, -10f, 10f, TextFormatting.AQUA, new ItemStat.Properties());

    // Armor
    public static final ItemStat ARMOR = new ItemStat(0f, 0f, 40f, TextFormatting.LIGHT_PURPLE, new ItemStat.Properties().synergyApplies());
    public static final ItemStat ARMOR_TOUGHNESS = new ItemStat(0f, 0f, 40f, TextFormatting.LIGHT_PURPLE, new ItemStat.Properties().synergyApplies());
    public static final ItemStat MAGIC_ARMOR = new ItemStat(0f, 0f, 40f, TextFormatting.LIGHT_PURPLE, new ItemStat.Properties().synergyApplies());

    private ItemStats() {
    }

    /**
     * Returns a collection of all created stats in a pre-determined order.
     * @return Ordered stats list
     */
    public static Collection<ItemStat> allStatsOrdered() {
        return Collections.unmodifiableList(STATS_IN_ORDER);
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
        return id != null ? REGISTRY.get().getValue(id) : null;
    }

    // region Registry creation - other mods should not call these methods!

    public static void createRegistry(RegistryEvent.NewRegistry event) {
        REGISTRY.get();
    }

    public static void registerStats(RegistryEvent.Register<ItemStat> event) {
        register(event.getRegistry(), DURABILITY, "durability");
        register(event.getRegistry(), ARMOR_DURABILITY, "armor_durability");
        register(event.getRegistry(), REPAIR_EFFICIENCY, "repair_efficiency");
        register(event.getRegistry(), ENCHANTABILITY, "enchantability");
        register(event.getRegistry(), RARITY, "rarity");
        register(event.getRegistry(), HARVEST_LEVEL, "harvest_level");
        register(event.getRegistry(), HARVEST_SPEED, "harvest_speed");
        register(event.getRegistry(), REACH_DISTANCE, "reach_distance");
        register(event.getRegistry(), MELEE_DAMAGE, "melee_damage");
        register(event.getRegistry(), MAGIC_DAMAGE, "magic_damage");
        register(event.getRegistry(), ATTACK_SPEED, "attack_speed");
        register(event.getRegistry(), RANGED_DAMAGE, "ranged_damage");
        register(event.getRegistry(), RANGED_SPEED, "ranged_speed");
        register(event.getRegistry(), ARMOR, "armor");
        register(event.getRegistry(), ARMOR_TOUGHNESS, "armor_toughness");
        register(event.getRegistry(), MAGIC_ARMOR, "magic_armor");
    }

    private static void register(IForgeRegistry<ItemStat> registry, ItemStat stat, String name) {
        registry.register(stat.setRegistryName(SilentGear.getId(name)));
    }

    //endregion
}
