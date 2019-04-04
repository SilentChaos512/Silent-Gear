package net.silentchaos512.gear.api.stats;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.silentchaos512.gear.SilentGear;

/**
 * Stats used by all equipment types.
 *
 * @author SilentChaos512
 * @since Experimental
 */
public final class CommonItemStats {
    // Generic
    public static final ItemStat DURABILITY = new ItemStat(new ResourceLocation(SilentGear.MOD_ID, "durability"),
            1f, 1f, Integer.MAX_VALUE, true, TextFormatting.BLUE)
            .setAffectedByGrades(true)
            .setSynergyApplies(true);
    public static final ItemStat ARMOR_DURABILITY = new ItemStat(new ResourceLocation(SilentGear.MOD_ID, "armor_durability"),
            1f, 1f, Integer.MAX_VALUE / 16, true, TextFormatting.BLUE)
            .setAffectedByGrades(true)
            .setSynergyApplies(true);
    public static final ItemStat REPAIR_EFFICIENCY = new ItemStat(new ResourceLocation(SilentGear.MOD_ID, "repair_efficiency"),
            1f, 0f, 1000f, false, TextFormatting.BLUE)
//            .setHidden(true)
            .setAffectedByGrades(false)
            .setSynergyApplies(false);
    public static final ItemStat ENCHANTABILITY = new ItemStat(new ResourceLocation(SilentGear.MOD_ID, "enchantability"),
            0f, 0f, 10000f, true, TextFormatting.BLUE)
            .setAffectedByGrades(true)
            .setSynergyApplies(true);
    public static final ItemStat RARITY = new ItemStat(new ResourceLocation(SilentGear.MOD_ID, "rarity"),
            0f, 0f, 10000f, true, TextFormatting.BLUE)
            .setHidden(true)
            .setAffectedByGrades(true)
            .setSynergyApplies(false);

    // Harvesting Tools
    public static final ItemStat HARVEST_LEVEL = new ItemStat(new ResourceLocation(SilentGear.MOD_ID, "harvest_level"),
            0f, 0f, 10000f, true, TextFormatting.YELLOW)
            .setAffectedByGrades(false)
            .setSynergyApplies(false);
    public static final ItemStat HARVEST_SPEED = new ItemStat(new ResourceLocation(SilentGear.MOD_ID, "harvest_speed"),
            1f, 0f, 10000f, false, TextFormatting.YELLOW)
            .setAffectedByGrades(true)
            .setSynergyApplies(true)
            .setMissingRodEffect(f -> Math.max(2, f / 8));
    public static final ItemStat REACH_DISTANCE = new ItemStat(new ResourceLocation(SilentGear.MOD_ID, "reach_distance"),
            0f, -100f, 100f, false, TextFormatting.YELLOW)
            .setAffectedByGrades(false)
            .setSynergyApplies(false)
            .setMissingRodEffect(f -> f - 1.5f);
    // Melee Weapons
    public static final ItemStat MELEE_DAMAGE = new ItemStat(new ResourceLocation(SilentGear.MOD_ID, "melee_damage"),
            0f, 0f, 10000f, false, TextFormatting.GREEN)
            .setAffectedByGrades(true)
            .setSynergyApplies(true)
            .setMissingRodEffect(f -> f / 2);
    public static final ItemStat MAGIC_DAMAGE = new ItemStat(new ResourceLocation(SilentGear.MOD_ID, "magic_damage"),
            0f, 0f, 10000f, false, TextFormatting.GREEN)
            .setAffectedByGrades(true)
            .setSynergyApplies(true);
    public static final ItemStat ATTACK_SPEED = new ItemStat(new ResourceLocation(SilentGear.MOD_ID, "attack_speed"),
            0f, -4f, 4f, false, TextFormatting.GREEN)
            .setAffectedByGrades(false)
            .setSynergyApplies(false);

    // Ranged Weapons
    public static final ItemStat RANGED_DAMAGE = new ItemStat(new ResourceLocation(SilentGear.MOD_ID, "ranged_damage"),
            0f, 0f, 10000f, false, TextFormatting.AQUA)
            .setAffectedByGrades(true)
            .setSynergyApplies(true);
    public static final ItemStat RANGED_SPEED = new ItemStat(new ResourceLocation(SilentGear.MOD_ID, "ranged_speed"),
            0f, -10f, 10f, false, TextFormatting.AQUA)
            .setAffectedByGrades(false)
            .setSynergyApplies(false);
    // Armor
    public static final ItemStat ARMOR = new ItemStat(new ResourceLocation(SilentGear.MOD_ID, "armor"),
            0f, 0f, 40f, false, TextFormatting.LIGHT_PURPLE)
            .setAffectedByGrades(true)
            .setSynergyApplies(true);
    public static final ItemStat ARMOR_TOUGHNESS = new ItemStat(new ResourceLocation(SilentGear.MOD_ID, "armor_toughness"),
            0f, 0f, 40f, false, TextFormatting.LIGHT_PURPLE)
            .setAffectedByGrades(true)
            .setSynergyApplies(true);
    public static final ItemStat MAGIC_ARMOR = new ItemStat(new ResourceLocation(SilentGear.MOD_ID, "magic_armor"),
            0f, 0f, 40f, false, TextFormatting.LIGHT_PURPLE)
            .setAffectedByGrades(true)
            .setSynergyApplies(true);

    private CommonItemStats() {}

    public static void init() {
        // NO-OP
    }
}
