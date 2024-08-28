package net.silentchaos512.gear.api.property;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.Multiset;
import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.StreamCodec;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.util.PropertyKey;
import net.silentchaos512.gear.setup.gear.GearTypes;
import net.silentchaos512.gear.util.CodecUtils;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.Map.Entry;

public class GearPropertyMap implements Multimap<PropertyKey<?, ?>, GearPropertyValue<?>> {
    public static final Codec<GearPropertyMap> CODEC = Codec.dispatchedMap(
            PropertyKey.CODEC,
            key -> CodecUtils.singleOrListCodec(key.property().codec())
    ).xmap(
            map -> {
                GearPropertyMap ret = new GearPropertyMap();
                map.forEach(ret::putAll);
                return ret;
            },
            propertyMap -> {
                Map<PropertyKey<?, ?>, List<GearPropertyValue<?>>> ret = new LinkedHashMap<>();
                propertyMap.keySet().forEach(key -> ret.put(key, new ArrayList<>(propertyMap.getValues(key))));
                return (Map) ret;
            }
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, GearPropertyMap> STREAM_CODEC = StreamCodec.of(
            GearPropertyMap::encode,
            GearPropertyMap::decode
    );

    public static final GearPropertyMap EMPTY = new GearPropertyMap();

    private final Multimap<PropertyKey<?, ?>, GearPropertyValue<?>> map = MultimapBuilder.linkedHashKeys().arrayListValues().build();

    public GearPropertyMap() {
    }

    public GearPropertyMap(GearPropertyMap values) {
        this.map.putAll(values);
    }

    public GearPropertyMap.Immutable toImmutable() {
        return new Immutable(this);
    }

    public GearPropertyMap toMutable() {
        return this;
    }

    public static Component formatTextUnchecked(
            Collection<? extends GearPropertyValue<?>> mods,
            GearProperty<?, ?> property,
            boolean addModColors
    ) {
        return property.formatModifiersUnchecked(mods, addModColors);
    }

    public static <T, V extends GearPropertyValue<T>, P extends GearProperty<T, V>> MutableComponent formatText(
            Collection<V> mods,
            P property,
            int maxDecimalPlaces
    ) {
        return formatText(mods, property, maxDecimalPlaces, false);
    }

    public static <T, V extends GearPropertyValue<T>, P extends GearProperty<T, V>> MutableComponent formatText(
            Collection<V> mods,
            P property,
            int maxDecimalPlaces,
            boolean addModColors
    ) {
        if (mods.size() == 1) {
            V inst = mods.iterator().next();
            int decimalPlaces = property.getPreferredDecimalPlaces(inst);
            return property.formatValueWithColor(inst, addModColors);
        }

        // Sort modifiers by operation
        MutableComponent result = Component.literal("");
        List<V> toSort = property.sortForDisplay(mods);

        for (V inst : toSort) {
            if (!result.getSiblings().isEmpty()) {
                result.append(", ");
            }
            result.append(property.formatValueWithColor(inst, addModColors));
        }

        return result;
    }

    private static void encode(RegistryFriendlyByteBuf buf, GearPropertyMap map) {
        buf.writeVarInt(map.keySet().size());
        map.keySet().forEach(key -> {
            PropertyKey.STREAM_CODEC.encode(buf, key);
            var values = map.get(key);
            buf.writeVarInt(values.size());
            values.forEach(val -> key.property().rawStreamCodec().encode(buf, val));
        });
    }

    private static GearPropertyMap decode(RegistryFriendlyByteBuf buf) {
        GearPropertyMap ret = new GearPropertyMap();
        int keyCount = buf.readVarInt();
        for (int i = 0; i < keyCount; ++i) {
            PropertyKey<?, ?> key = PropertyKey.STREAM_CODEC.decode(buf);
            int propertyCount = buf.readVarInt();
            for (int j = 0; j < propertyCount; ++j) {
                var value = key.property().streamCodec().decode(buf);
                ret.put(key, value);
            }
        }
        return ret.toImmutable();
    }

    public Set<GearProperty<?, ?>> getPropertyTypes() {
        Set<GearProperty<?, ?>> set = new HashSet<>();
        for (PropertyKey<?, ?> key : this.keySet()) {
            set.add(key.property());
        }
        return set;
    }

    @Override
    public int size() {
        return this.map.size();
    }

    @Override
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    @Override
    public boolean containsKey(@Nullable Object key) {
        return this.map.containsKey(key);
    }

    @Override
    public boolean containsValue(@Nullable Object value) {
        return this.map.containsValue(value);
    }

    @Override
    public boolean containsEntry(@Nullable Object key, @Nullable Object value) {
        return this.map.containsEntry(key, value);
    }

    public <V, I extends GearPropertyValue<V>> boolean put(GearProperty<V, I> stat, GearType gearType, I value) {
        return put(PropertyKey.of(stat, gearType), value);
    }

    @Override
    public boolean put(@Nullable PropertyKey<?, ?> key, @Nullable GearPropertyValue<?> value) {
        return this.map.put(key, value);
    }

    @Override
    public boolean remove(@Nullable Object key, @Nullable Object value) {
        return this.map.remove(key, value);
    }

    @Override
    public boolean putAll(@Nullable PropertyKey<?, ?> key, @Nonnull Iterable<? extends GearPropertyValue<?>> values) {
        return this.map.putAll(key, values);
    }

    @Override
    public boolean putAll(@Nonnull Multimap<? extends PropertyKey<?, ?>, ? extends GearPropertyValue<?>> multimap) {
        return this.map.putAll(multimap);
    }

    @Override
    public Collection<GearPropertyValue<?>> replaceValues(@Nullable PropertyKey<?, ?> key, @Nonnull Iterable<? extends GearPropertyValue<?>> values) {
        return this.map.replaceValues(key, values);
    }

    @Override
    public Collection<GearPropertyValue<?>> removeAll(@Nullable Object key) {
        return this.map.removeAll(key);
    }

    @Override
    public void clear() {
        this.map.clear();
    }

    public <T, V extends GearPropertyValue<T>> Collection<V> getValues(GearProperty<T, V> stat, GearType gearType) {
        //noinspection unchecked
        return (Collection<V>) get(PropertyKey.of(stat, gearType));
    }

    public <T, V extends GearPropertyValue<T>> Collection<V> getValues(PropertyKey<T, V> key) {
        //noinspection unchecked
        return (Collection<V>) get(key);
    }

    @Override
    public Collection<GearPropertyValue<?>> get(@Nullable PropertyKey<?, ?> key) {
        if (key == null || this.map.containsKey(key)) {
            return this.map.get(key);
        }

        PropertyKey<?, ?> parent = key.getParent();
        while (parent != null) {
            if (this.map.containsKey(parent)) {
                return this.map.get(parent);
            }
            parent = parent.getParent();
        }

        return Collections.emptyList();
    }

    public PropertyKey<?, ?> getMostSpecificKey(PropertyKey<?, ?> key) {
        if (this.map.containsKey(key)) {
            return key;
        }

        PropertyKey<?, ?> parent = key.getParent();
        while (parent != null) {
            if (this.map.containsKey(parent)) {
                return parent;
            }
            parent = parent.getParent();
        }

        return PropertyKey.of(key.property(), GearTypes.ALL.get());
    }

    @Override
    public Set<PropertyKey<?, ?>> keySet() {
        return this.map.keySet();
    }

    @Override
    public Multiset<PropertyKey<?, ?>> keys() {
        return this.map.keys();
    }

    @Override
    public Collection<GearPropertyValue<?>> values() {
        return this.map.values();
    }

    @Override
    public Collection<Entry<PropertyKey<?, ?>, GearPropertyValue<?>>> entries() {
        return this.map.entries();
    }

    @Override
    public Map<PropertyKey<?, ?>, Collection<GearPropertyValue<?>>> asMap() {
        return this.map.asMap();
    }

    public static final class Immutable extends GearPropertyMap {
        public static final Codec<Immutable> CODEC = GearPropertyMap.CODEC
                .xmap(
                        GearPropertyMap::toImmutable,
                        Immutable::toMutable
                );

        public Immutable(GearPropertyMap values) {
            super(values);
        }

        @Override
        public GearPropertyMap toMutable() {
            return new GearPropertyMap(this);
        }

        @Override
        public <V, I extends GearPropertyValue<V>> boolean put(GearProperty<V, I> stat, GearType gearType, I value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean put(@Nullable PropertyKey<?, ?> key, @Nullable GearPropertyValue<?> value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(@Nullable Object key, @Nullable Object value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean putAll(@Nullable PropertyKey<?, ?> key, @NotNull Iterable<? extends GearPropertyValue<?>> values) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean putAll(@NotNull Multimap<? extends PropertyKey<?, ?>, ? extends GearPropertyValue<?>> multimap) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Collection<GearPropertyValue<?>> replaceValues(@Nullable PropertyKey<?, ?> key, @NotNull Iterable<? extends GearPropertyValue<?>> values) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Collection<GearPropertyValue<?>> removeAll(@Nullable Object key) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }
    }
}
