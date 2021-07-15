package net.silentchaos512.gear.api.stats;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.Multiset;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.part.IGearPart;
import net.silentchaos512.gear.api.util.StatGearKey;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.Map.Entry;

public class StatModifierMap implements Multimap<StatGearKey, StatInstance> {
    public static final StatModifierMap EMPTY_STAT_MAP = new StatModifierMap();

    private final Multimap<StatGearKey, StatInstance> map = MultimapBuilder.linkedHashKeys().arrayListValues().build();

    public static IFormattableTextComponent formatText(Collection<StatInstance> mods, ItemStat stat, int maxDecimalPlaces) {
        return formatText(mods, stat, maxDecimalPlaces, false);
    }

    public static IFormattableTextComponent formatText(Collection<StatInstance> mods, ItemStat stat, int maxDecimalPlaces, boolean addModColors) {
        if (mods.size() == 1) {
            StatInstance inst = mods.iterator().next();
            int decimalPlaces = inst.getPreferredDecimalPlaces(stat, maxDecimalPlaces);
            return inst.getFormattedText(stat, decimalPlaces, addModColors);
        }

        // Sort modifiers by operation
        IFormattableTextComponent result = new StringTextComponent("");
        List<StatInstance> toSort = new ArrayList<>(mods);
        toSort.sort(Comparator.comparing(inst -> inst.getOp().ordinal()));

        for (StatInstance inst : toSort) {
            if (!result.getSiblings().isEmpty()) {
                result.append(", ");
            }
            result.append(inst.getFormattedText(stat, inst.getPreferredDecimalPlaces(stat, maxDecimalPlaces), addModColors));
        }

        return result;
    }

    public Set<ItemStat> getStats() {
        Set<ItemStat> set = new HashSet<>();
        for (StatGearKey key : this.keySet()) {
            if (key.getStat() instanceof ItemStat) {
                set.add((ItemStat) key.getStat());
            }
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

    public boolean put(IItemStat stat, GearType gearType, StatInstance value) {
        return put(StatGearKey.of(stat, gearType), value);
    }

    @Override
    public boolean put(@Nullable StatGearKey key, @Nullable StatInstance value) {
        return this.map.put(key, value);
    }

    @Override
    public boolean remove(@Nullable Object key, @Nullable Object value) {
        return this.map.remove(key, value);
    }

    @Override
    public boolean putAll(@Nullable StatGearKey key, @Nonnull Iterable<? extends StatInstance> values) {
        return this.map.putAll(key, values);
    }

    @Override
    public boolean putAll(@Nonnull Multimap<? extends StatGearKey, ? extends StatInstance> multimap) {
        return this.map.putAll(multimap);
    }

    @Override
    public Collection<StatInstance> replaceValues(@Nullable StatGearKey key, @Nonnull Iterable<? extends StatInstance> values) {
        return this.map.replaceValues(key, values);
    }

    @Override
    public Collection<StatInstance> removeAll(@Nullable Object key) {
        return this.map.removeAll(key);
    }

    @Override
    public void clear() {
        this.map.clear();
    }

    public Collection<StatInstance> get(IItemStat stat, GearType gearType) {
        return get(StatGearKey.of(stat, gearType));
    }

    @Override
    public Collection<StatInstance> get(@Nullable StatGearKey key) {
        if (key == null || this.map.containsKey(key)) {
            return this.map.get(key);
        }

        StatGearKey parent = key.getParent();
        while (parent != null) {
            if (this.map.containsKey(parent)) {
                return this.map.get(parent);
            }
            parent = parent.getParent();
        }

        return Collections.emptyList();
    }

    public StatGearKey getMostSpecificKey(StatGearKey key) {
        if (this.map.containsKey(key)) {
            return key;
        }

        StatGearKey parent = key.getParent();
        while (parent != null) {
            if (this.map.containsKey(parent)) {
                return parent;
            }
            parent = parent.getParent();
        }

        return StatGearKey.of(key.getStat(), GearType.ALL);
    }

    @Override
    public Set<StatGearKey> keySet() {
        return this.map.keySet();
    }

    @Override
    public Multiset<StatGearKey> keys() {
        return this.map.keys();
    }

    @Override
    public Collection<StatInstance> values() {
        return this.map.values();
    }

    @Override
    public Collection<Entry<StatGearKey, StatInstance>> entries() {
        return this.map.entries();
    }

    @Override
    public Map<StatGearKey, Collection<StatInstance>> asMap() {
        return this.map.asMap();
    }

    public JsonObject serialize() {
        JsonObject json = new JsonObject();

        for (StatGearKey key : this.keySet()) {
            Collection<StatInstance> mods = this.get(key);

            if (mods.size() > 1) {
                JsonArray array = new JsonArray();
                mods.forEach(mod -> array.add(mod.serialize()));
                json.add(key.toString(), array);
            } else if (mods.size() == 1) {
                json.add(key.toString(), mods.iterator().next().serialize());
            }
        }

        return json;
    }

    @Deprecated
    public static StatModifierMap deserialize(IGearPart part, JsonElement json) {
        return deserialize(json);
    }

    public static StatModifierMap deserialize(JsonElement json) {
        StatModifierMap map = new StatModifierMap();
        if (json.isJsonObject()) {
            for (Entry<String, JsonElement> entry : json.getAsJsonObject().entrySet()) {
                StatGearKey key = StatGearKey.read(entry.getKey());
                if (key != null) {
                    JsonElement value = entry.getValue();
                    if (value.isJsonArray()) {
                        for (JsonElement je : value.getAsJsonArray()) {
                            StatInstance mod = StatInstance.read(key, je);
                            map.put(key, mod);
                        }
                    } else {
                        map.put(key, StatInstance.read(key, value));
                    }
                }
            }
        } else if (json.isJsonArray()) {
            for (JsonElement element : json.getAsJsonArray()) {
                JsonObject jsonObj = element.getAsJsonObject();
                StatGearKey key = StatGearKey.read(JSONUtils.getAsString(jsonObj, "name"));
                if (key != null) {
                    map.put(key, StatInstance.read(key, element));
                }
            }
        } else {
            throw new JsonParseException("Expected object or array");
        }
        return map;
    }

    public static StatModifierMap read(PacketBuffer buffer) {
        StatModifierMap map = new StatModifierMap();

        int count = buffer.readVarInt();
        for (int i = 0; i < count; ++i) {
            StatGearKey key = StatGearKey.read(buffer);
            StatInstance instance = StatInstance.read(key, buffer);
            map.put(key, instance);
        }

        return map;
    }

    public void write(PacketBuffer buffer) {
        buffer.writeVarInt(this.size());
        this.forEach((key, instance) -> {
            key.write(buffer);
            instance.write(buffer);
        });
    }
}
