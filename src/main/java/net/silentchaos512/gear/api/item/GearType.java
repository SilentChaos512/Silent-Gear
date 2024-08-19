package net.silentchaos512.gear.api.item;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.common.ToolAction;
import net.silentchaos512.gear.api.property.GearPropertyGroup;
import net.silentchaos512.gear.api.property.GearProperty;
import net.silentchaos512.gear.api.property.NumberProperty;
import net.silentchaos512.gear.setup.SgRegistries;
import net.silentchaos512.gear.setup.gear.GearProperties;
import net.silentchaos512.gear.setup.gear.GearTypes;
import net.silentchaos512.gear.util.CodecUtils;
import net.silentchaos512.gear.util.GearHelper;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Supplier;

public record GearType(
        Supplier<GearType> parent,
        int animationFrames,
        Set<ToolAction> toolActions,
        float armorDurabilityMultiplier,
        Supplier<NumberProperty> durabilityStat,
        Set<GearPropertyGroup> relevantPropertyGroups
) {
    public static final Codec<GearType> CODEC = CodecUtils.byModNameCodec(SgRegistries.GEAR_TYPE);
    public static final StreamCodec<RegistryFriendlyByteBuf, GearType> STREAM_CODEC = ByteBufCodecs.registry(SgRegistries.GEAR_TYPE_KEY);

    private static final Map<GearType, ICoreItem> ITEMS = new HashMap<>();

    public GearType(
            @Nullable Supplier<GearType> parent,
            int animationFrames,
            Set<ToolAction> toolActions,
            float armorDurabilityMultiplier,
            Supplier<NumberProperty> durabilityStat,
            Set<GearPropertyGroup> relevantPropertyGroups
    ) {
        this.parent = parent;
        this.animationFrames = animationFrames;
        this.toolActions = toolActions;
        this.armorDurabilityMultiplier = armorDurabilityMultiplier;
        this.durabilityStat = durabilityStat;
        this.relevantPropertyGroups = ImmutableSet.copyOf(relevantPropertyGroups);
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
        var name = SgRegistries.GEAR_TYPE.getKey(this);
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

    @Override
    public Set<GearPropertyGroup> relevantPropertyGroups() {
        if (relevantPropertyGroups.isEmpty()) {
            return parent.get().relevantPropertyGroups();
        }
        return relevantPropertyGroups;
    }

    public boolean isPropertyRelevant(GearProperty<?, ?> propertyType) {
        for (GearPropertyGroup group : relevantPropertyGroups) {
            if (propertyType.getGroup() == group) {
                return true;
            }
        }
        return false;
    }

    public static class Builder {
        @Nullable
        private final Supplier<GearType> parent;
        private int animationFrames = 1;
        private Supplier<NumberProperty> durabilityStat = GearProperties.DURABILITY;
        private float armorDurabilityMultiplier = 1f;
        private Set<ToolAction> toolActions = Collections.emptySet();
        private final Set<GearPropertyGroup> relevantPropertyGroups = new LinkedHashSet<>();

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
                    relevantPropertyGroups
            );
        }

        public Builder animationFrames(int animationFrames) {
            this.animationFrames = animationFrames;
            return this;
        }

        public Builder durabilityStat(Supplier<NumberProperty> durabilityStat) {
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

        public Builder relevantPropertyGroups(GearPropertyGroup... propertyGroups) {
            return relevantPropertyGroups(Arrays.asList(propertyGroups));
        }

        public Builder relevantPropertyGroups(Collection<GearPropertyGroup> propertyGroups) {
            propertyGroups.addAll(Builder.this.relevantPropertyGroups);
            return this;
        }
    }
}
