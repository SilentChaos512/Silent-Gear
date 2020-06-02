package net.silentchaos512.gear.api.item;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Used for classifying gear for certain purposes, such as rendering. For example, any armor type
 * can be matched individually (helmet, chestplate, etc.), or all together with "armor".
 */
public final class GearType {
    private static final Pattern VALID_NAME = Pattern.compile("[^a-z_]");
    private static final Map<String, GearType> VALUES = new HashMap<>();

    // Parent of everything except armor
    public static final GearType TOOL = getOrCreate("tool");
    // Harvest tools
    public static final GearType HARVEST_TOOL = getOrCreate("harvest_tool", TOOL);
    public static final GearType AXE = getOrCreate("axe", HARVEST_TOOL);
    public static final GearType EXCAVATOR = getOrCreate("excavator", HARVEST_TOOL);
    public static final GearType HAMMER = getOrCreate("hammer", HARVEST_TOOL);
    public static final GearType MATTOCK = getOrCreate("mattock", HARVEST_TOOL);
    public static final GearType PAXEL = getOrCreate("paxel", HARVEST_TOOL);
    public static final GearType PICKAXE = getOrCreate("pickaxe", HARVEST_TOOL);
    public static final GearType SHEARS = getOrCreate("shears", HARVEST_TOOL);
    public static final GearType SHOVEL = getOrCreate("shovel", HARVEST_TOOL);
    public static final GearType SICKLE = getOrCreate("sickle", HARVEST_TOOL);
    // Melee weapons (swords)
    public static final GearType MELEE_WEAPON = getOrCreate("melee_weapon", TOOL);
    public static final GearType DAGGER = getOrCreate("dagger", MELEE_WEAPON);
    public static final GearType KATANA = getOrCreate("katana", MELEE_WEAPON);
    public static final GearType MACHETE = getOrCreate("machete", MELEE_WEAPON);
    public static final GearType SPEAR = getOrCreate("spear", MELEE_WEAPON);
    public static final GearType SWORD = getOrCreate("sword", MELEE_WEAPON);
    // Ranged weapons (bows)
    public static final GearType RANGED_WEAPON = getOrCreate("ranged_weapon", TOOL);
    public static final GearType BOW = getOrCreate("bow", RANGED_WEAPON);
    public static final GearType CROSSBOW = getOrCreate("crossbow", RANGED_WEAPON);
    public static final GearType SLINGSHOT = getOrCreate("slingshot", RANGED_WEAPON);
    // Other
    public static final GearType SHIELD = getOrCreate("shield", TOOL);
    // Armor
    public static final GearType ARMOR = getOrCreate("armor");
    public static final GearType BOOTS = getOrCreate("boots", ARMOR);
    public static final GearType CHESTPLATE = getOrCreate("chestplate", ARMOR);
    public static final GearType HELMET = getOrCreate("helmet", ARMOR);
    public static final GearType LEGGINGS = getOrCreate("leggings", ARMOR);

    @Nullable
    public static GearType get(String name) {
        return VALUES.get(name);
    }

    public static GearType getOrCreate(String name) {
        return getOrCreate(name, null);
    }

    public static GearType getOrCreate(String name, @Nullable GearType parent) {
        if (VALID_NAME.matcher(name).find())
            throw new IllegalArgumentException("Invalid name: " + name);
        return VALUES.computeIfAbsent(name, k -> new GearType(name, parent));
    }

    private final String name;
    @Nullable private final GearType parent;

    private GearType(String name, @Nullable GearType parent) {
        this.name = name;
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    /**
     * Gets the parent gear type, if there is one. The parent type may also have a parent.
     *
     * @return The parent type
     */
    @Nullable
    public GearType getParent() {
        return parent;
    }

    /**
     * Check if this type's name matches the given string, or if its parent type does. The type
     * "all" will match anything.
     *
     * @param type The string representation of the type
     * @return True if this type's name is equal to type, or if its parent matches (recursive)
     */
    public boolean matches(String type) {
        return matches(type, true);
    }

    /**
     * Check if this type's name matches the given string, or if its parent type does. The type
     * "all" will match anything if {@code includeAll} is true.
     *
     * @param type       The string representation of the type
     * @param includeAll Whether or not to consider the "all" type. This should be excluded if
     *                   trying to match more specific types.
     * @return True if this type's name is equal to type, or if its parent matches (recursive)
     */
    public boolean matches(String type, boolean includeAll) {
        if (type.contains("/")) {
            return matches(type.split("/")[1], includeAll);
        }
        return (includeAll && "all".equals(type)) || name.equals(type) || (parent != null && parent.matches(type, includeAll));
    }

    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("gearType.silentgear." + this.name);
    }
}
