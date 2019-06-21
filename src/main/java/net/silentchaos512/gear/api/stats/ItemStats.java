package net.silentchaos512.gear.api.stats;

import net.minecraft.util.text.TextFormatting;
import net.silentchaos512.gear.SilentGear;

/**
 * Stats used by all equipment types.
 *
 * @author SilentChaos512
 * @since Experimental
 */
public final class ItemStats {
    // Generic
    public static final ItemStat DURABILITY = new ItemStat(SilentGear.getId("durability"),
            1f, 1f, Integer.MAX_VALUE, true, TextFormatting.BLUE)
            .setAffectedByGrades(true)
            .setSynergyApplies(true);
    public static final ItemStat ARMOR_DURABILITY = new ItemStat(SilentGear.getId("armor_durability"),
            1f, 1f, Integer.MAX_VALUE / 16, true, TextFormatting.BLUE)
            .setAffectedByGrades(true)
            .setSynergyApplies(true);
    public static final ItemStat REPAIR_EFFICIENCY = new ItemStat(SilentGear.getId("repair_efficiency"),
            1f, 0f, 1000f, false, TextFormatting.BLUE)
//            .setHidden(true)
            .setAffectedByGrades(false)
            .setSynergyApplies(false);
    public static final ItemStat ENCHANTABILITY = new ItemStat(SilentGear.getId("enchantability"),
            0f, 0f, 10000f, true, TextFormatting.BLUE)
            .setAffectedByGrades(true)
            .setSynergyApplies(true);
    public static final ItemStat RARITY = new ItemStat(SilentGear.getId("rarity"),
            0f, 0f, 10000f, true, TextFormatting.BLUE)
            .setHidden(true)
            .setAffectedByGrades(true)
            .setSynergyApplies(false);

    // Harvesting Tools
    public static final ItemStat HARVEST_LEVEL = new ItemStat(SilentGear.getId("harvest_level"),
            0f, 0f, 10000f, true, TextFormatting.YELLOW)
            .setAffectedByGrades(false)
            .setSynergyApplies(false);
    public static final ItemStat HARVEST_SPEED = new ItemStat(SilentGear.getId("harvest_speed"),
            1f, 0f, 10000f, false, TextFormatting.YELLOW)
            .setAffectedByGrades(true)
            .setSynergyApplies(true)
            .setMissingRodEffect(f -> Math.max(2, f / 8));
    public static final ItemStat REACH_DISTANCE = new ItemStat(SilentGear.getId("reach_distance"),
            0f, -100f, 100f, false, TextFormatting.YELLOW)
            .setAffectedByGrades(false)
            .setSynergyApplies(false)
            .setMissingRodEffect(f -> f - 1.5f);
    // Melee Weapons
    public static final ItemStat MELEE_DAMAGE = new ItemStat(SilentGear.getId("melee_damage"),
            0f, 0f, 10000f, false, TextFormatting.GREEN)
            .setAffectedByGrades(true)
            .setSynergyApplies(true)
            .setMissingRodEffect(f -> f / 2);
    public static final ItemStat MAGIC_DAMAGE = new ItemStat(SilentGear.getId("magic_damage"),
            0f, 0f, 10000f, false, TextFormatting.GREEN)
            .setAffectedByGrades(true)
            .setSynergyApplies(true);
    public static final ItemStat ATTACK_SPEED = new ItemStat(SilentGear.getId("attack_speed"),
            0f, -4f, 4f, false, TextFormatting.GREEN)
            .setAffectedByGrades(false)
            .setSynergyApplies(false);

    // Ranged Weapons
    public static final ItemStat RANGED_DAMAGE = new ItemStat(SilentGear.getId("ranged_damage"),
            0f, 0f, 10000f, false, TextFormatting.AQUA)
            .setAffectedByGrades(true)
            .setSynergyApplies(true);
    public static final ItemStat RANGED_SPEED = new ItemStat(SilentGear.getId("ranged_speed"),
            0f, -10f, 10f, false, TextFormatting.AQUA)
            .setAffectedByGrades(false)
            .setSynergyApplies(false);
    // Armor
    public static final ItemStat ARMOR = new ItemStat(SilentGear.getId("armor"),
            0f, 0f, 40f, false, TextFormatting.LIGHT_PURPLE)
            .setAffectedByGrades(true)
            .setSynergyApplies(true);
    public static final ItemStat ARMOR_TOUGHNESS = new ItemStat(SilentGear.getId("armor_toughness"),
            0f, 0f, 40f, false, TextFormatting.LIGHT_PURPLE)
            .setAffectedByGrades(true)
            .setSynergyApplies(true);
    public static final ItemStat MAGIC_ARMOR = new ItemStat(SilentGear.getId("magic_armor"),
            0f, 0f, 40f, false, TextFormatting.LIGHT_PURPLE)
            .setAffectedByGrades(true)
            .setSynergyApplies(true);

    private ItemStats() {}

    public static void init() {
        // NO-OP
    }
}
