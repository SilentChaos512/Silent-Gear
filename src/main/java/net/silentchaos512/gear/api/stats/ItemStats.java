package net.silentchaos512.gear.api.stats;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.lib.util.Lazy;

import javax.annotation.Nullable;

/**
 * Stats used by all equipment types.
 *
 * @author SilentChaos512
 * @since Experimental
 */
public final class ItemStats {
    public static final Lazy<IForgeRegistry<ItemStat>> REGISTRY = Lazy.of(() -> new RegistryBuilder<ItemStat>().setType(ItemStat.class).setName(SilentGear.getId("stat")).create());

    // Generic
    public static final ItemStat DURABILITY = new ItemStat(1f, 1f, Integer.MAX_VALUE, true, TextFormatting.BLUE)
            .setAffectedByGrades(true)
            .setSynergyApplies(true);
    public static final ItemStat ARMOR_DURABILITY = new ItemStat(1f, 1f, Integer.MAX_VALUE / 16, true, TextFormatting.BLUE)
            .setAffectedByGrades(true)
            .setSynergyApplies(true);
    public static final ItemStat REPAIR_EFFICIENCY = new ItemStat(1f, 0f, 1000f, false, TextFormatting.BLUE)
//            .setHidden(true)
            .setAffectedByGrades(false)
            .setSynergyApplies(false);
    public static final ItemStat ENCHANTABILITY = new ItemStat(0f, 0f, 10000f, true, TextFormatting.BLUE)
            .setAffectedByGrades(true)
            .setSynergyApplies(true);
    public static final ItemStat RARITY = new ItemStat(0f, 0f, 10000f, true, TextFormatting.BLUE)
            .setHidden(true)
            .setAffectedByGrades(true)
            .setSynergyApplies(false);

    // Harvesting Tools
    public static final ItemStat HARVEST_LEVEL = new ItemStat(0f, 0f, 10000f, true, TextFormatting.YELLOW)
            .setAffectedByGrades(false)
            .setSynergyApplies(false);
    public static final ItemStat HARVEST_SPEED = new ItemStat(1f, 0f, 10000f, false, TextFormatting.YELLOW)
            .setAffectedByGrades(true)
            .setSynergyApplies(true)
            .setMissingRodEffect(f -> Math.max(2, f / 8));
    public static final ItemStat REACH_DISTANCE = new ItemStat(0f, -100f, 100f, false, TextFormatting.YELLOW)
            .setAffectedByGrades(false)
            .setSynergyApplies(false)
            .setMissingRodEffect(f -> f - 1.5f);
    // Melee Weapons
    public static final ItemStat MELEE_DAMAGE = new ItemStat(0f, 0f, 10000f, false, TextFormatting.GREEN)
            .setAffectedByGrades(true)
            .setSynergyApplies(true)
            .setMissingRodEffect(f -> f / 2);
    public static final ItemStat MAGIC_DAMAGE = new ItemStat(0f, 0f, 10000f, false, TextFormatting.GREEN)
            .setAffectedByGrades(true)
            .setSynergyApplies(true);
    public static final ItemStat ATTACK_SPEED = new ItemStat(0f, -4f, 4f, false, TextFormatting.GREEN)
            .setAffectedByGrades(false)
            .setSynergyApplies(false);

    // Ranged Weapons
    public static final ItemStat RANGED_DAMAGE = new ItemStat(0f, 0f, 10000f, false, TextFormatting.AQUA)
            .setAffectedByGrades(true)
            .setSynergyApplies(true);
    public static final ItemStat RANGED_SPEED = new ItemStat(0f, -10f, 10f, false, TextFormatting.AQUA)
            .setAffectedByGrades(false)
            .setSynergyApplies(false);
    // Armor
    public static final ItemStat ARMOR = new ItemStat(0f, 0f, 40f, false, TextFormatting.LIGHT_PURPLE)
            .setAffectedByGrades(true)
            .setSynergyApplies(true);
    public static final ItemStat ARMOR_TOUGHNESS = new ItemStat(0f, 0f, 40f, false, TextFormatting.LIGHT_PURPLE)
            .setAffectedByGrades(true)
            .setSynergyApplies(true);
    public static final ItemStat MAGIC_ARMOR = new ItemStat(0f, 0f, 40f, false, TextFormatting.LIGHT_PURPLE)
            .setAffectedByGrades(true)
            .setSynergyApplies(true);

    private ItemStats() {}

    @Nullable
    public static ItemStat byName(String name) {
        ResourceLocation id = withDefaultNamespace(name);
        return id != null ? REGISTRY.get().getValue(id) : null;
    }

    @Nullable
    private static ResourceLocation withDefaultNamespace(String name) {
        if (name.contains(":"))
            return ResourceLocation.tryCreate(name);
        return ResourceLocation.tryCreate(SilentGear.RESOURCE_PREFIX + name);
    }

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
}
