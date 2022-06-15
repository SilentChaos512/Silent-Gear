package net.silentchaos512.gear.api.item;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.init.Registration;
import net.silentchaos512.gear.util.GearHelper;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
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
    private static final Map<GearType, ICoreItem> ITEMS = new HashMap<>();

    // A non-existent gear type which matches nothing
    public static final GearType NONE = getOrCreate("none");
    // A gear type which matches everything
    public static final GearType ALL = getOrCreate("all");
    // Yeah, I know...
    public static final GearType PART = getOrCreate("part");
    public static final GearType FRAGMENT = getOrCreate("fragment");

    public static final GearType PROJECTILE = getOrCreate("projectile", ALL);

    // Parent of everything except armor
    public static final GearType TOOL = getOrCreate("tool", ALL);
    public static final GearType WEAPON = getOrCreate("weapon", TOOL);
    // Harvest tools
    public static final GearType HARVEST_TOOL = getOrCreate("harvest_tool", TOOL);
    public static final GearType AXE = getOrCreate("axe", HARVEST_TOOL, b ->
            b.toolActions(ToolActions.DEFAULT_AXE_ACTIONS));
    public static final GearType PICKAXE = getOrCreate("pickaxe", HARVEST_TOOL, b ->
            b.toolActions(ToolActions.DEFAULT_PICKAXE_ACTIONS));
    public static final GearType SHOVEL = getOrCreate("shovel", HARVEST_TOOL, b ->
            b.toolActions(ToolActions.DEFAULT_SHOVEL_ACTIONS));
    public static final GearType EXCAVATOR = getOrCreate("excavator", SHOVEL, b ->
            b.toolActions(ToolActions.SHOVEL_DIG));
    public static final GearType HAMMER = getOrCreate("hammer", PICKAXE, b ->
            b.toolActions(ToolActions.PICKAXE_DIG));
    public static final GearType SAW = getOrCreate("saw", AXE, b ->
            b.toolActions(ToolActions.AXE_DIG));
    public static final GearType MATTOCK = getOrCreate("mattock", HARVEST_TOOL, b ->
            b.toolActions(ToolActions.AXE_DIG, ToolActions.HOE_DIG, ToolActions.HOE_TILL, ToolActions.SHOVEL_DIG));
    public static final GearType PAXEL = getOrCreate("paxel", HARVEST_TOOL, b ->
            b.toolActions(ToolActions.AXE_DIG, ToolActions.PICKAXE_DIG, ToolActions.SHOVEL_DIG));
    public static final GearType PROSPECTOR_HAMMER = getOrCreate("prospector_hammer", PICKAXE, b ->
            b.toolActions(ToolActions.DEFAULT_PICKAXE_ACTIONS));
    public static final GearType SHEARS = getOrCreate("shears", HARVEST_TOOL, b ->
            b.toolActions(ToolActions.DEFAULT_SHEARS_ACTIONS));
    public static final GearType SICKLE = getOrCreate("sickle", HARVEST_TOOL, b ->
            b.toolActions(ToolActions.HOE_DIG));
    // Melee weapons (swords)
    public static final GearType MELEE_WEAPON = getOrCreate("melee_weapon", WEAPON);
    public static final GearType DAGGER = getOrCreate("dagger", MELEE_WEAPON, b ->
            b.toolActions(ToolActions.DEFAULT_SWORD_ACTIONS));
    public static final GearType KATANA = getOrCreate("katana", MELEE_WEAPON, b ->
            b.toolActions(ToolActions.DEFAULT_SWORD_ACTIONS));
    public static final GearType KNIFE = getOrCreate("knife", MELEE_WEAPON, b ->
            b.toolActions(ToolActions.DEFAULT_SWORD_ACTIONS));
    public static final GearType MACHETE = getOrCreate("machete", MELEE_WEAPON, b ->
            b.toolActions(ToolActions.DEFAULT_SWORD_ACTIONS));
    public static final GearType SPEAR = getOrCreate("spear", MELEE_WEAPON, b ->
            b.toolActions(ToolActions.DEFAULT_SWORD_ACTIONS));
    public static final GearType SWORD = getOrCreate("sword", MELEE_WEAPON, b ->
            b.toolActions(ToolActions.DEFAULT_SWORD_ACTIONS));
    // Ranged weapons (bows)
    public static final GearType RANGED_WEAPON = getOrCreate("ranged_weapon", WEAPON);
    public static final GearType BOW = getOrCreate("bow", RANGED_WEAPON);
    public static final GearType CROSSBOW = getOrCreate("crossbow", RANGED_WEAPON);
    public static final GearType SLINGSHOT = getOrCreate("slingshot", RANGED_WEAPON);
    // Other
    public static final GearType FISHING_ROD = getOrCreate("fishing_rod", TOOL);
    public static final GearType SHIELD = getOrCreate("shield", TOOL, b ->
            b.durabilityStat(() -> ItemStats.ARMOR_DURABILITY));
    // Armor
    public static final GearType ARMOR = getOrCreate("armor", ALL, b ->
            b.durabilityStat(() -> ItemStats.ARMOR_DURABILITY));
    public static final GearType BOOTS = getOrCreate("boots", ARMOR, b ->
            b.durabilityStat(() -> ItemStats.ARMOR_DURABILITY));
    public static final GearType CHESTPLATE = getOrCreate("chestplate", ARMOR, b ->
            b.durabilityStat(() -> ItemStats.ARMOR_DURABILITY));
    public static final GearType ELYTRA = getOrCreate("elytra", ARMOR, b ->
            b.durabilityStat(() -> ItemStats.ARMOR_DURABILITY));
    public static final GearType HELMET = getOrCreate("helmet", ARMOR, b ->
            b.durabilityStat(() -> ItemStats.ARMOR_DURABILITY));
    public static final GearType LEGGINGS = getOrCreate("leggings", ARMOR, b ->
            b.durabilityStat(() -> ItemStats.ARMOR_DURABILITY));
    // Projectiles
    public static final GearType ARROW = getOrCreate("arrow", PROJECTILE);

    // Curios
    public static final GearType CURIO = getOrCreate("curio", ALL);
    public static final GearType BRACELET = getOrCreate("bracelet", CURIO);
    public static final GearType RING = getOrCreate("ring", CURIO);

    /**
     * Gets the gear type of the given name, or null if it does not exist.
     *
     * @param name The gear type name
     * @return The gear type, or null if it does not exist
     */
    public static GearType get(String name) {
        return VALUES.getOrDefault(name, NONE);
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
        return getOrCreate(name, null, b -> {});
    }

    public static GearType getOrCreate(String name, @Nullable GearType parent) {
        return getOrCreate(name, parent, b -> {});
    }

    public static GearType getOrCreate(String name, @Nullable GearType parent, Consumer<Builder> propertiesBuilder) {
        if (VALID_NAME.matcher(name).find()) {
            throw new IllegalArgumentException("Invalid name: " + name);
        }
        return VALUES.computeIfAbsent(name, s -> {
            Builder builder = Builder.of(name, parent);
            propertiesBuilder.accept(builder);
            return builder.build();
        });
    }

    public static GearType fromJson(JsonObject json, String key) {
        String str = GsonHelper.getAsString(json, key);
        GearType type = get(str);
        if (type.isInvalid()) {
            throw new JsonSyntaxException("Unknown gear type: " + str);
        }
        return type;
    }

    private final String name;
    @Nullable private final GearType parent;
    private final int animationFrames;
    private final Supplier<ItemStat> durabilityStat;
    private final Set<ToolAction> toolActions;

    private GearType(String name, @Nullable GearType parent, int animationFrames, Supplier<ItemStat> durabilityStat, Set<ToolAction> toolActions) {
        this.name = name;
        this.parent = parent;
        this.animationFrames = animationFrames;
        this.durabilityStat = durabilityStat;
        this.toolActions = toolActions;
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

    public ItemStat getDurabilityStat() {
        return durabilityStat.get();
    }

    public boolean canPerformAction(ToolAction action) {
        return toolActions.contains(action);
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
    public boolean matches(String type, boolean includeAll) { //FIXME: The way includesAll is handled does not make sense anymore
        if (type.contains("/")) {
            return matches(type.split("/")[1], includeAll);
        }
        return (includeAll && "all".equals(type)) || name.equals(type) || (parent != null && parent.matches(type, includeAll));
    }

    public boolean matches(GearType type, boolean includeAll) {
        return matches(type.name, includeAll);
    }

    public boolean isGear() {
        return matches(ALL, false);
    }

    public boolean isArmor() {
        return matches(ARMOR);
    }

    public boolean isInvalid() {
        return this == NONE;
    }

    public Component getDisplayName() {
        return new TranslatableComponent("gearType.silentgear." + this.name);
    }

    public Optional<ICoreItem> getItem() {
        return Optional.ofNullable(ITEMS.computeIfAbsent(this, gearType -> {
            return Registration.getItems(ICoreItem.class).stream()
                    .filter(item -> item.getGearType() == gearType)
                    .findAny().orElse(null);
        }));
    }

    public Collection<ItemStat> getRelevantStats() {
        return getItem()
                .map(item -> (Collection<ItemStat>) item.getRelevantStats(ItemStack.EMPTY))
                .orElse(ItemStats.allStatsOrdered());
    }

    public Set<ItemStat> getExcludedStats() {
        return getItem()
                .map(item -> item.getExcludedStats(ItemStack.EMPTY))
                .orElse(Collections.emptySet());
    }

    public GearTypeMatcher getMatcher(boolean matchParents) {
        return new GearTypeMatcher(matchParents, this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GearType gearType = (GearType) o;
        return name.equals(gearType.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "GearType{" +
                "name='" + name + '\'' +
                ", parent=" + parent +
                '}';
    }

    public static class Builder {
        private final String name;
        @Nullable private final GearType parent;
        private int animationFrames = 1;
        private Supplier<ItemStat> durabilityStat = () -> ItemStats.DURABILITY;
        private Set<ToolAction> toolActions = Collections.emptySet();

        private Builder(String name, @Nullable GearType parent) {
            this.name = name;
            this.parent = parent;
        }

        public static Builder of(String name) {
            return of(name, null);
        }

        public static Builder of(String name, @Nullable GearType parent) {
            return new Builder(name, parent);
        }

        public GearType build() {
            return new GearType(name, parent, animationFrames, durabilityStat, toolActions);
        }

        public Builder animationFrames(int animationFrames) {
            this.animationFrames = animationFrames;
            return this;
        }

        public Builder durabilityStat(Supplier<ItemStat> durabilityStat) {
            this.durabilityStat = durabilityStat;
            return this;
        }

        public Builder toolActions(ToolAction... actions) {
            this.toolActions = GearHelper.makeToolActionSet(actions);
            return this;
        }

        public Builder toolActions(Set<ToolAction> actions) {
            this.toolActions = Collections.unmodifiableSet(actions);
            return this;
        }
    }
}
