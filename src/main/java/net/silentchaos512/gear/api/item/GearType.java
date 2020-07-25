package net.silentchaos512.gear.api.item;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Used for classifying gear for certain purposes, such as rendering. For example, any armor type
 * can be matched individually (helmet, chestplate, etc.), or all together with "armor".
 * <p>
 * New gear types can be added with {@link #getOrCreate(String, GearType)}. It is recommended to
 * store this in a static final field in your item class, but the location doesn't matter.
 */
public final class GearType {
    private static final Pattern VALID_NAME = Pattern.compile("[^a-z_]");
    private static final Map<String, GearType> VALUES = new HashMap<>();

    // A non-existent gear type which matches nothing
    public static final GearType NONE = getOrCreate("none");
    // A gear type which matches everything
    public static final GearType ALL = getOrCreate("all");
    // Yeah, I know...
    public static final GearType PART = getOrCreate("part");

    // Parent of everything except armor
    public static final GearType TOOL = getOrCreate("tool", ALL);
    public static final GearType WEAPON = getOrCreate("weapon", TOOL);
    // Harvest tools
    public static final GearType HARVEST_TOOL = getOrCreate("harvest_tool", TOOL);
    public static final GearType AXE = getOrCreate("axe", HARVEST_TOOL);
    public static final GearType PICKAXE = getOrCreate("pickaxe", HARVEST_TOOL);
    public static final GearType SHOVEL = getOrCreate("shovel", HARVEST_TOOL);
    public static final GearType EXCAVATOR = getOrCreate("excavator", SHOVEL);
    public static final GearType HAMMER = getOrCreate("hammer", PICKAXE);
    public static final GearType LUMBER_AXE = getOrCreate("lumber_axe", AXE);
    public static final GearType MATTOCK = getOrCreate("mattock", HARVEST_TOOL);
    public static final GearType PAXEL = getOrCreate("paxel", HARVEST_TOOL);
    public static final GearType SHEARS = getOrCreate("shears", HARVEST_TOOL);
    public static final GearType SICKLE = getOrCreate("sickle", HARVEST_TOOL);
    // Melee weapons (swords)
    public static final GearType MELEE_WEAPON = getOrCreate("melee_weapon", WEAPON);
    public static final GearType DAGGER = getOrCreate("dagger", MELEE_WEAPON);
    public static final GearType KATANA = getOrCreate("katana", MELEE_WEAPON);
    public static final GearType MACHETE = getOrCreate("machete", MELEE_WEAPON);
    public static final GearType SPEAR = getOrCreate("spear", MELEE_WEAPON);
    public static final GearType SWORD = getOrCreate("sword", MELEE_WEAPON);
    // Ranged weapons (bows)
    public static final GearType RANGED_WEAPON = getOrCreate("ranged_weapon", WEAPON);
    public static final GearType BOW = getOrCreate("bow", RANGED_WEAPON);
    public static final GearType CROSSBOW = getOrCreate("crossbow", RANGED_WEAPON);
    public static final GearType SLINGSHOT = getOrCreate("slingshot", RANGED_WEAPON);
    // Other
    public static final GearType SHIELD = getOrCreate("shield", TOOL);
    // Armor
    public static final GearType ARMOR = getOrCreate("armor", ALL);
    public static final GearType BOOTS = getOrCreate("boots", ARMOR);
    public static final GearType CHESTPLATE = getOrCreate("chestplate", ARMOR);
    public static final GearType HELMET = getOrCreate("helmet", ARMOR);
    public static final GearType LEGGINGS = getOrCreate("leggings", ARMOR);

    /**
     * Gets the gear type of the given name, or null if it does not exist.
     *
     * @param name The gear type name
     * @return The gear type, or null if it does not exist
     */
    @Nullable
    public static GearType get(String name) {
        return VALUES.get(name);
    }

    /**
     * Gets or creates a new gear type without a parent. This should NOT be used in most cases. If
     * the gear type already exists, the existing instance is not modified in any way.
     *
     * @param name The gear type name. Must be unique and contain only lowercase letters and
     *             underscores.
     * @return The newly created gear type, or the existing instance if it already exists
     * @throws IllegalArgumentException if the name is invalid
     */
    public static GearType getOrCreate(String name) {
        return getOrCreate(name, null);
    }

    public static GearType getOrCreate(String name, @Nullable GearType parent) {
        return getOrCreate(name, parent, 1);
    }

    /**
     * Gets or creates a new gear type with the given parent. If the gear type already exists, the
     * existing instance is not modified in any way.
     *
     * @param name   The gear type name. Must be unique and contain only lowercase letters and
     *               underscores.
     * @param parent The parent gear type. This will typically be harvest_tool, melee_weapon, or
     *               ranged_weapon, but it could be any existing type.
     * @return The newly created gear type, or the existing instance if it already exists
     * @throws IllegalArgumentException if the name is invalid
     */
    public static GearType getOrCreate(String name, @Nullable GearType parent, int animationFrames) {
        if (VALID_NAME.matcher(name).find())
            throw new IllegalArgumentException("Invalid name: " + name);
        return VALUES.computeIfAbsent(name, k -> new GearType(name, parent, animationFrames));
    }

    public static GearType fromJson(JsonObject json, String key) {
        String str = JSONUtils.getString(json, key);
        GearType type = get(str);
        if (type == null) {
            throw new JsonSyntaxException("Unknown gear type: " + str);
        }
        return type;
    }

    private final String name;
    @Nullable private final GearType parent;
    private final int animationFrames;

    private GearType(String name, @Nullable GearType parent, int animationFrames) {
        this.name = name;
        this.parent = parent;
        this.animationFrames = animationFrames;
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

    public int getAnimationFrames() {
        return animationFrames;
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

    public boolean matches(GearType type) {
        return matches(type.name, true);
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

    public boolean matches(GearType type, boolean includeAll) {
        return matches(type.name, includeAll);
    }

    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("gearType.silentgear." + this.name);
    }

    @Override
    public String toString() {
        return "GearType{" +
                "name='" + name + '\'' +
                ", parent=" + parent +
                '}';
    }
}
