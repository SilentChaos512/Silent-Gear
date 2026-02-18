package net.silentchaos512.gear.api.property;

import com.mojang.serialization.Codec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.util.GearComponentInstance;
import net.silentchaos512.gear.api.util.PartGearKey;
import net.silentchaos512.gear.client.tooltip.FormatColorScheme;
import net.silentchaos512.gear.client.tooltip.GearTooltipStyle;
import net.silentchaos512.gear.client.util.GearTooltipFlag;
import net.silentchaos512.gear.client.util.TextListBuilder;
import net.silentchaos512.gear.gear.part.PartInstance;
import net.silentchaos512.gear.setup.SgRegistries;
import net.silentchaos512.gear.setup.gear.GearTypes;
import net.silentchaos512.gear.util.TextUtil;
import net.silentchaos512.lib.util.Color;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class GearProperty<T, V extends GearPropertyValue<T>> {
    protected final T baseValue;
    protected final T defaultValue;
    protected final T minimumValue;
    protected final T maximumValue;
    protected final GearPropertyGroup group;
    protected final Color nameColor;
    protected final boolean affectedBySynergy;
    protected final boolean affectedByGrades;
    protected final boolean forMaterialsOnly;
    protected final boolean visible;

    protected GearProperty(Builder<T> builder) {
        builder.validate();
        this.baseValue = builder.baseValue;
        this.defaultValue = builder.defaultValue;
        this.minimumValue = builder.minimumValue;
        this.maximumValue = builder.maximumValue;
        this.affectedBySynergy = builder.affectedBySynergy;
        this.affectedByGrades = builder.affectedByGrades;
        this.forMaterialsOnly = builder.forMaterialsOnly;
        this.group = builder.group;
        this.nameColor = builder.nameColor;
        this.visible = builder.visible;
    }

    public abstract Codec<V> codec();

    public abstract StreamCodec<? super RegistryFriendlyByteBuf, V> streamCodec();

    public StreamCodec<FriendlyByteBuf, GearPropertyValue<?>> rawStreamCodec() {
        //noinspection unchecked
        return (StreamCodec<FriendlyByteBuf, GearPropertyValue<?>>) streamCodec();
    };

    public abstract V valueOf(T value);

    public T compute(Collection<V> modifiers) {
        return compute(this.baseValue, true, GearTypes.ALL.get(), modifiers);
    }

    public T compute(T baseValue, Collection<V> modifiers) {
        return compute(baseValue, true, GearTypes.ALL.get(), modifiers);
    }

    public T compute(T baseValue, boolean clampResult, GearType gearType, Collection<V> modifiers) {
        return compute(baseValue, clampResult, gearType, gearType, modifiers);
    }

    public abstract T compute(T baseValue, boolean clampResult, GearType itemType, GearType statType, Collection<V> modifiers);

    public T computeForGear(T baseValue, boolean clampResult, GearType itemType, GearType statType, Collection<V> modifiers, List<PartInstance> parts) {
        return compute(baseValue, clampResult, itemType, statType, modifiers);
    }

    @SuppressWarnings("unchecked")
    public V computeUnchecked(boolean clampResult, GearType itemType, GearType statType, Collection<GearPropertyValue<?>> modifiers) {
        return valueOf(compute(getBaseValue(), clampResult, itemType, statType, (Collection<V>) modifiers));
    }

    @SuppressWarnings("unchecked")
    public V computeUncheckedForGear(GearType itemType, GearType statType, Collection<GearPropertyValue<?>> modifiers, List<PartInstance> parts) {
        return valueOf(computeForGear(getBaseValue(), true, itemType, statType, (Collection<V>) modifiers, parts));
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public T getBaseValue() {
        return baseValue;
    }

    public T getMinimumValue() {
        return minimumValue;
    }

    public T getMaximumValue() {
        return maximumValue;
    }

    public abstract T getZeroValue();

    public abstract boolean isZero(T value);

    public boolean isAffectedByGrades() {
        return affectedByGrades;
    }

    public boolean isAffectedBySynergy() {
        return affectedBySynergy;
    }

    public boolean isForMaterialsOnly() {
        return forMaterialsOnly;
    }

    public GearPropertyGroup getGroup() {
        return group;
    }

    public abstract List<V> compressModifiers(Collection<V> modifiers, PartGearKey key, List<? extends GearComponentInstance<?>> components);

    public V applySynergy(V value, float synergy) {
        return value;
    }

    @SuppressWarnings("unchecked")
    public final boolean isHiddenUnchecked(GearPropertyValue<?> value, GearTooltipFlag flag) {
        return isHidden((V) value, flag);
    }

    public boolean isHidden(V value, GearTooltipFlag flag) {
        return false;
    }

    @SuppressWarnings("unchecked")
    public final void buildTooltipUnchecked(TextListBuilder listBuilder, GearPropertyValue<?> value, ItemStack gearItemStack, GearTooltipFlag flag, FormatColorScheme colorScheme) {
        buildTooltip(listBuilder, (V) value, gearItemStack, flag, colorScheme);
    }

    public void buildTooltip(TextListBuilder listBuilder, V value, ItemStack gearItemStack, GearTooltipFlag flag, FormatColorScheme colorScheme) {
        listBuilder.add(formatText(value, flag, colorScheme));
    }

    public void buildModifiersTooltip(TextListBuilder listBuilder, Collection<V> modifiers, GearTooltipStyle format, FormatColorScheme colorScheme) {

    }

    @SuppressWarnings("unchecked")
    public final Component formatTextUnchecked(GearPropertyValue<?> value, GearTooltipFlag flag, FormatColorScheme colorScheme) {
        return formatText((V) value, flag, colorScheme);
    }

    public Component formatText(V value, GearTooltipFlag flag, FormatColorScheme colorScheme) {
        var valueText = formatValue(value, FormatContext.GEAR, colorScheme);
        return formatText(valueText);
    }

    public Component formatText(Component valueText) {
        var propertyName = TextUtil.withColor(getDisplayName(), this.nameColor);
        return Component.translatable("property.silentgear.displayFormat", propertyName, valueText);
    }

    public abstract Component formatValue(V value, FormatContext formatContext, FormatColorScheme colorScheme);

    public abstract MutableComponent formatValueWithColor(V value, FormatContext formatContext, FormatColorScheme colorScheme);

    @SuppressWarnings("unchecked")
    public Component formatModifiersUnchecked(Collection<? extends GearPropertyValue<?>> mods, FormatContext formatContext, FormatColorScheme colorScheme) {
        return formatModifiers((Collection<V>) mods, formatContext, colorScheme);
    }

    public Component formatModifiers(Collection<V> mods, FormatContext formatContext, FormatColorScheme colorScheme) {
        if (mods.size() == 1) {
            V inst = mods.iterator().next();
            int decimalPlaces = getPreferredDecimalPlaces(inst);
            return formatValueWithColor(inst, formatContext, colorScheme);
        }

        // Sort modifiers by operation
        MutableComponent result = Component.literal("");
        List<V> toSort = sortForDisplay(mods);

        for (V value : toSort) {
            if (!result.getSiblings().isEmpty()) {
                result.append(", ");
            }
            result.append(formatValueWithColor(value, formatContext, colorScheme));
        }

        return result;
    }

    public MutableComponent formatModifiersWithColorUnchecked(
            Collection<GearPropertyValue<?>> mods,
            FormatContext formatContext,
            FormatColorScheme colorScheme
    ) {
        //noinspection unchecked
        V value = valueOf(compute((Collection<V>) mods));
        return formatValueWithColor(value, formatContext, colorScheme);
    }

    public int getPreferredDecimalPlaces(V value) {
        return 0;
    }

    public List<V> sortForDisplay(Collection<V> mods) {
        return new ArrayList<>(mods);
    }

    public MutableComponent getDisplayName() {
        ResourceLocation name = SgRegistries.GEAR_PROPERTY.getKey(this);
        if (name == null) return Component.literal("ERROR");
        return Component.translatable("property." + name.getNamespace() + "." + name.getPath());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;

        if (!(obj instanceof GearProperty<?,?> other)) return false;

        var key = SgRegistries.GEAR_PROPERTY.getKey(this);
        assert key != null;
        return key.equals(SgRegistries.GEAR_PROPERTY.getKey(other));
    }

    @Override
    public int hashCode() {
        var key = SgRegistries.GEAR_PROPERTY.getKey(this);
        return key != null ? key.hashCode() : super.hashCode();
    }

    public enum FormatContext {
        GEAR,
        PART,
        MATERIAL,
        ANY
    }

    public static class Builder<T> {
        private final T baseValue;
        private final T defaultValue;
        private final T minimumValue;
        private final T maximumValue;
        private GearPropertyGroup group;
        private Color nameColor;
        private boolean affectedBySynergy;
        private boolean affectedByGrades;
        public boolean forMaterialsOnly;
        private boolean visible;

        public Builder(T defaultValue) {
            this (defaultValue, defaultValue);
        }

        public Builder(T defaultValue, T baseValue) {
            this(defaultValue, baseValue, defaultValue, defaultValue);
        }

        public Builder(T defaultValue, T baseValue, T minimumValue, T maximumValue) {
            this.defaultValue = defaultValue;
            this.baseValue = baseValue;
            this.minimumValue = minimumValue;
            this.maximumValue = maximumValue;
        }

        public void validate() {
            if (group == null) {
                throw new IllegalStateException("Gear property group is null");
            } else if (nameColor == null) {
                throw new IllegalStateException("Gear property name color is null");
            }
        }

        public Builder<T> group(GearPropertyGroup category) {
            this.group = category;
            if (this.nameColor == null) {
                this.nameColor = this.group.getColor();
            }
            return this;
        }

        public Builder<T> nameColor(Color color) {
            this.nameColor = color;
            return this;
        }

        public Builder<T> affectedBySynergy(boolean value) {
            affectedBySynergy = value;
            return this;
        }

        public Builder<T> affectedByGrades(boolean value) {
            affectedByGrades = value;
            return this;
        }

        public Builder<T> forMaterialsOnly(boolean value) {
            forMaterialsOnly = value;
            return this;
        }

        public Builder<T> visible(boolean visible) {
            this.visible = visible;
            return this;
        }
    }
}
