package net.silentchaos512.gear.api.property;

import com.mojang.serialization.Codec;
import net.minecraft.network.chat.MutableComponent;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.setup.gear.GearTypes;
import net.silentchaos512.lib.util.Color;

import javax.annotation.Nonnegative;
import java.util.Collection;

public abstract class GearPropertyType<V, I extends GearProperty<V>> {
    protected final V baseValue;
    protected final V defaultValue;
    protected final V minimumValue;
    protected final V maximumValue;
    protected final GearPropertyCategory category;
    protected final Color nameColor;
    protected final boolean affectedBySynergy;
    protected final boolean affectedByGrades;
    protected final boolean visible;

    protected GearPropertyType(Builder<V> builder) {
        builder.validate();
        this.baseValue = builder.baseValue;
        this.defaultValue = builder.defaultValue;
        this.minimumValue = builder.minimumValue;
        this.maximumValue = builder.maximumValue;
        this.affectedBySynergy = builder.affectedBySynergy;
        this.affectedByGrades = builder.affectedByGrades;
        this.category = builder.category;
        this.nameColor = builder.nameColor;
        this.visible = builder.visible;
    }

    public abstract Codec<I> codec();

    public V compute(Collection<I> modifiers) {
        return compute(this.baseValue, true, GearTypes.ALL.get(), modifiers);
    }

    public V compute(V baseValue, Collection<I> modifiers) {
        return compute(baseValue, true, GearTypes.ALL.get(), modifiers);
    }

    public V compute(V baseValue, boolean clampResult, GearType gearType, Collection<I> modifiers) {
        return compute(baseValue, clampResult, gearType, gearType, modifiers);
    }

    public abstract V compute(V baseValue, boolean clampResult, GearType itemType, GearType statType, Collection<I> modifiers);

    public MutableComponent getFormattedText(I value) {
        return getFormattedText(value, 2, false);
    }

    public abstract MutableComponent getFormattedText(I value, @Nonnegative int decimalPlaces, boolean addColor);

    public V getDefaultValue() {
        return defaultValue;
    }

    public V getBaseValue() {
        return baseValue;
    }

    public V getMinimumValue() {
        return minimumValue;
    }

    public V getMaximumValue() {
        return maximumValue;
    }

    public GearPropertyCategory getCategory() {
        return category;
    }

    public static class Builder<T> {
        private final T baseValue;
        private final T defaultValue;
        private final T minimumValue;
        private final T maximumValue;
        private GearPropertyCategory category;
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
            if (category == null) {
                throw new IllegalStateException("Gear property category is null");
            } else if (nameColor == null) {
                throw new IllegalStateException("Gear property name color is null");
            }
        }

        public Builder<T> category(GearPropertyCategory category) {
            this.category = category;
            if (this.nameColor == null) {
                this.nameColor = this.category.getColor();
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
