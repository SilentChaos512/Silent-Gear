package net.silentchaos512.gear.api.property;

import com.mojang.serialization.Codec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.util.GearComponentInstance;
import net.silentchaos512.gear.api.util.PartGearKey;
import net.silentchaos512.gear.setup.SgRegistries;
import net.silentchaos512.gear.setup.gear.GearTypes;
import net.silentchaos512.lib.util.Color;

import javax.annotation.Nonnegative;
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
    protected final boolean visible;

    protected GearProperty(Builder<T> builder) {
        builder.validate();
        this.baseValue = builder.baseValue;
        this.defaultValue = builder.defaultValue;
        this.minimumValue = builder.minimumValue;
        this.maximumValue = builder.maximumValue;
        this.affectedBySynergy = builder.affectedBySynergy;
        this.affectedByGrades = builder.affectedByGrades;
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

    public boolean isAffectedByGrades() {
        return affectedByGrades;
    }

    public boolean isAffectedBySynergy() {
        return affectedBySynergy;
    }

    public GearPropertyGroup getGroup() {
        return group;
    }

    public abstract List<V> compressModifiers(Collection<V> modifiers, PartGearKey key, List<? extends GearComponentInstance<?>> components);

    public V applySynergy(V value, float synergy) {
        return value;
    }

    public abstract MutableComponent getFormattedText(V value, @Nonnegative int decimalPlaces, boolean addColor);

    public MutableComponent getFormattedText(V value) {
        return getFormattedText(value, 2, false);
    }

    public MutableComponent getFormattedText(Collection<GearPropertyValue<?>> mods, @Nonnegative int decimalPlaces, boolean addColor) {
        //noinspection unchecked
        V value = valueOf(compute((Collection<V>) mods));
        return getFormattedText(value, decimalPlaces, addColor);
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

    public static class Builder<T> {
        private final T baseValue;
        private final T defaultValue;
        private final T minimumValue;
        private final T maximumValue;
        private GearPropertyGroup group;
        private Color nameColor;
        private boolean affectedBySynergy;
        private boolean affectedByGrades;
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

        public Builder<T> visible(boolean visible) {
            this.visible = visible;
            return this;
        }
    }
}
