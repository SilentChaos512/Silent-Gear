package net.silentchaos512.gear.api.item;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.common.ToolAction;
import net.silentchaos512.gear.api.property.NumberPropertyType;
import net.silentchaos512.gear.api.stats.IItemStat;
import net.silentchaos512.gear.setup.SgRegistries;
import net.silentchaos512.gear.setup.gear.GearProperties;
import net.silentchaos512.gear.setup.gear.GearTypes;
import net.silentchaos512.gear.util.CodecUtils;
import net.silentchaos512.gear.util.GearHelper;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Supplier;

/**
 * Used for classifying gear for certain purposes, such as rendering. For example, any armor type
 * can be matched individually (helmet, chestplate, etc.), or all together with "armor".
 * <p>
 * New gear types can be added with {@link #getOrCreate(String, GearType)}. It is recommended to
 * store this in a static final field in your item class, but the location doesn't matter.
 */
public final class GearType {
    public static final Codec<GearType> CODEC = CodecUtils.byModNameCodec(SgRegistries.GEAR_TYPES);
    public static final StreamCodec<RegistryFriendlyByteBuf, GearType> STREAM_CODEC = ByteBufCodecs.registry(SgRegistries.GEAR_TYPES_KEY);

    private static final Map<GearType, ICoreItem> ITEMS = new HashMap<>();

    @Nullable
    private final Supplier<GearType> parent;
    private final int animationFrames;
    private final Set<ToolAction> toolActions;
    private final float armorDurabilityMultiplier;
    private final Supplier<NumberPropertyType> durabilityStat;
    private final Set<IItemStat> relevantStats;
    private final Set<IItemStat> excludedStats;

    /**
     *
     */
    public GearType(
            @Nullable Supplier<GearType> parent,
            int animationFrames,
            Set<ToolAction> toolActions,
            float armorDurabilityMultiplier,
            Supplier<NumberPropertyType> durabilityStat,
            Set<IItemStat> relevantStats, // TODO: Get rid of this? Allow item stats to determine compatible items.
            Set<IItemStat> excludedStats // TODO: Get rid of this? It's not necessary to limit what stats an item can choose to have.
    ) {
        this.parent = parent;
        this.animationFrames = animationFrames;
        this.toolActions = toolActions;
        this.armorDurabilityMultiplier = armorDurabilityMultiplier;
        this.durabilityStat = durabilityStat;
        this.relevantStats = ImmutableSet.copyOf(relevantStats);
        this.excludedStats = ImmutableSet.copyOf(excludedStats);
    }

    public boolean canPerformAction(ToolAction action) {
        return toolActions.contains(action);
    }

    public boolean matches(GearType type) {
        return matches(type, true);
    }

    /**
     * Check if this type equals the given type, or if its parent type does. The type {@code ALL} will match anything
     * if {@code includeAll} is true.
     *
     * @param type       The string representation of the type
     * @param includeAll Whether to consider the ALL type. This should be false if trying to match more specific types.
     * @return True if this type is equal to {@code type}, or if its parent matches (recursive)
     */
    public boolean matches(GearType type, boolean includeAll) {
        return (includeAll && GearTypes.ALL.get().equals(type))
                || this.equals(type)
                || (parent != null && parent.get().matches(type, includeAll));
    }

    public boolean isGear() {
        return matches(GearTypes.ALL.get(), false);
    }

    public boolean isArmor() {
        return matches(GearTypes.ARMOR.get(), false);
    }

    public Component getDisplayName() {
        var name = SgRegistries.GEAR_TYPES.getKey(this);
        if (name == null) {
            return Component.literal("Unknown Gear Type");
        }
        return Component.translatable("gearType." + name.getNamespace() + "." + name.getPath());
    }

    public GearTypeMatcher getMatcher(boolean matchParents) {
        return new GearTypeMatcher(matchParents, this);
    }

    @Nullable
    public Supplier<GearType> parent() {
        return parent;
    }

    public int animationFrames() {
        return animationFrames;
    }

    public Set<ToolAction> toolActions() {
        return toolActions;
    }

    public float armorDurabilityMultiplier() {
        return armorDurabilityMultiplier;
    }

    public Supplier<NumberPropertyType> durabilityStat() {
        return durabilityStat;
    }

    public Set<IItemStat> relevantStats() {
        return relevantStats;
    }

    public Set<IItemStat> excludedStats() {
        return excludedStats;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (GearType) obj;
        return Objects.equals(this.parent, that.parent) &&
                this.animationFrames == that.animationFrames &&
                Objects.equals(this.toolActions, that.toolActions) &&
                Float.floatToIntBits(this.armorDurabilityMultiplier) == Float.floatToIntBits(that.armorDurabilityMultiplier) &&
                Objects.equals(this.durabilityStat, that.durabilityStat) &&
                Objects.equals(this.relevantStats, that.relevantStats) &&
                Objects.equals(this.excludedStats, that.excludedStats);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parent, animationFrames, toolActions, armorDurabilityMultiplier, durabilityStat, relevantStats, excludedStats);
    }

    @Override
    public String toString() {
        return "GearType[" +
                "parent=" + parent + ", " +
                "animationFrames=" + animationFrames + ", " +
                "toolActions=" + toolActions + ", " +
                "armorDurabilityMultiplier=" + armorDurabilityMultiplier + ", " +
                "durabilityStat=" + durabilityStat + ", " +
                "relevantStats=" + relevantStats + ", " +
                "excludedStats=" + excludedStats + ']';
    }


    public static class Builder {
        @Nullable
        private final Supplier<GearType> parent;
        private int animationFrames = 1;
        private Supplier<NumberPropertyType> durabilityStat = GearProperties.DURABILITY;
        private float armorDurabilityMultiplier = 1f;
        private Set<ToolAction> toolActions = Collections.emptySet();
        private final Set<IItemStat> relevantStats = new LinkedHashSet<>();
        private final Set<IItemStat> excludedStats = new LinkedHashSet<>();

        private Builder(@Nullable Supplier<GearType> parent) {
            this.parent = parent;
        }

        public static Builder of() {
            return of(null);
        }

        public static Builder of(@Nullable Supplier<GearType> parent) {
            return new Builder(parent);
        }

        public GearType build() {
            return new GearType(parent,
                    animationFrames,
                    toolActions,
                    armorDurabilityMultiplier,
                    durabilityStat,
                    relevantStats,
                    excludedStats
            );
        }

        public Builder animationFrames(int animationFrames) {
            this.animationFrames = animationFrames;
            return this;
        }

        public Builder durabilityStat(Supplier<NumberPropertyType> durabilityStat) {
            this.durabilityStat = durabilityStat;
            return this;
        }

        public Builder armorDurabilityMultiplier(float amount) {
            this.armorDurabilityMultiplier = amount;
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

        public Builder relevantStats(IItemStat... stats) {
            return relevantStats(Arrays.asList(stats));
        }

        public Builder relevantStats(Collection<IItemStat> stats) {
            relevantStats.addAll(stats);
            return this;
        }

        public Builder excludedStats(IItemStat... stats) {
            return excludedStats(Arrays.asList(stats));
        }

        private Builder excludedStats(Collection<IItemStat> stats) {
            excludedStats.addAll(stats);
            return this;
        }
    }
}
