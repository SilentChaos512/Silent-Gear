package net.silentchaos512.gear.api.stats;

import net.minecraft.util.text.TextFormatting;

/**
 * Stats used by all equipment types.
 *
 * @author SilentChaos512
 * @since Experimental
 */
public class CommonItemStats {

    // Generic
    public static final ItemStat DURABILITY = new ItemStat("durability", 0f, 0f, 32767f, true, TextFormatting.BLUE).setSynergyApplies(true); // short max is 32767
    public static final ItemStat REPAIR_EFFICIENCY = new ItemStat("repair_efficiency", 1f, 0f, 1000f, false, TextFormatting.BLUE).setHidden(true).setAffectedByGrades(false);
    public static final ItemStat ENCHANTABILITY = new ItemStat("enchantability", 0f, 0f, 10000f, true, TextFormatting.BLUE).setSynergyApplies(true);
    public static final ItemStat RARITY = new ItemStat("rarity", 0f, 0f, 10000f, true, TextFormatting.BLUE).setHidden(true);
    // Harvesting Tools
    public static final ItemStat HARVEST_LEVEL = new ItemStat("harvest_level", 0f, 0f, 10000f, true, TextFormatting.YELLOW).setAffectedByGrades(false);
    public static final ItemStat HARVEST_SPEED = new ItemStat("harvest_speed", 1f, 0f, 10000f, false, TextFormatting.YELLOW).setSynergyApplies(true);
    // Melee Weapons
    public static final ItemStat MELEE_DAMAGE = new ItemStat("melee_damage", 0f, 0f, 10000f, false, TextFormatting.GREEN).setSynergyApplies(true);
    public static final ItemStat MAGIC_DAMAGE = new ItemStat("magic_damage", 0f, 0f, 10000f, false, TextFormatting.GREEN).setSynergyApplies(true);
    public static final ItemStat ATTACK_SPEED = new ItemStat("attack_speed", 0f, -4f, 4f, false, TextFormatting.GREEN);
    // Ranged Weapons
    public static final ItemStat RANGED_DAMAGE = new ItemStat("ranged_damage", 0f, 0f, 10000f, false, TextFormatting.AQUA).setSynergyApplies(true);
    public static final ItemStat RANGED_SPEED = new ItemStat("ranged_speed", 0f, -10f, 10f, false, TextFormatting.AQUA);
    // Armor
    public static final ItemStat ARMOR = new ItemStat("armor", 0f, 0f, 40f, false, TextFormatting.LIGHT_PURPLE).setSynergyApplies(true);
    public static final ItemStat ARMOR_TOUGHNESS = new ItemStat("armor_toughness", 0f, 0f, 40f, false, TextFormatting.LIGHT_PURPLE).setSynergyApplies(true);
    public static final ItemStat MAGIC_ARMOR = new ItemStat("magic_armor", 0f, 0f, 40f, false, TextFormatting.LIGHT_PURPLE).setSynergyApplies(true);

    public static void init() {
        // NO-OP
    }
}
