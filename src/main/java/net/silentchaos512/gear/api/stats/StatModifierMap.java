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
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.part.IGearPart;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.Map.Entry;

public class StatModifierMap implements Multimap<IItemStat, StatInstance> {
    private final Multimap<IItemStat, StatInstance> map = MultimapBuilder.linkedHashKeys().arrayListValues().build();

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
                result.appendString(", ");
            }
            result.append(inst.getFormattedText(stat, inst.getPreferredDecimalPlaces(stat, maxDecimalPlaces), addModColors));
        }

        return result;
    }

    public Set<ItemStat> getStats() {
        Set<ItemStat> set = new HashSet<>();
        for (IItemStat s : this.keySet()) {
            if (s instanceof ItemStat) {
                set.add((ItemStat) s);
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

    @Override
    public boolean put(@Nullable IItemStat key, @Nullable StatInstance value) {
        return this.map.put(key, value);
    }

    @Override
    public boolean remove(@Nullable Object key, @Nullable Object value) {
        return this.map.remove(key, value);
    }

    @Override
    public boolean putAll(@Nullable IItemStat key, @Nonnull Iterable<? extends StatInstance> values) {
        return this.map.putAll(key, values);
    }

    @Override
    public boolean putAll(@Nonnull Multimap<? extends IItemStat, ? extends StatInstance> multimap) {
        return this.map.putAll(multimap);
    }

    @Override
    public Collection<StatInstance> replaceValues(@Nullable IItemStat key, @Nonnull Iterable<? extends StatInstance> values) {
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

    @Override
    public Collection<StatInstance> get(@Nullable IItemStat key) {
        return this.map.get(key);
    }

    @Override
    public Set<IItemStat> keySet() {
        return this.map.keySet();
    }

    @Override
    public Multiset<IItemStat> keys() {
        return this.map.keys();
    }

    @Override
    public Collection<StatInstance> values() {
        return this.map.values();
    }

    @Override
    public Collection<Entry<IItemStat, StatInstance>> entries() {
        return this.map.entries();
    }

    @Override
    public Map<IItemStat, Collection<StatInstance>> asMap() {
        return this.map.asMap();
    }

    public JsonObject serialize() {
        JsonObject json = new JsonObject();
        for (IItemStat stat : this.keySet()) {
            Collection<StatInstance> mods = this.get(stat);
            String shortStatId = SilentGear.shortenId(stat.getStatId());
            if (mods.size() > 1) {
                JsonArray array = new JsonArray();
                mods.forEach(mod -> array.add(mod.serialize(stat)));
                json.add(shortStatId, array);
            } else if (mods.size() == 1) {
                json.add(shortStatId, mods.iterator().next().serialize(stat));
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
                ItemStat stat = ItemStats.byName(entry.getKey());
                if (stat != null) {
                    JsonElement value = entry.getValue();
                    if (value.isJsonArray())
                        value.getAsJsonArray().forEach(e -> map.put(stat, StatInstance.read(stat, e)));
                    else
                        map.put(stat, StatInstance.read(stat, value));
                }
            }
        } else if (json.isJsonArray()) {
            for (JsonElement element : json.getAsJsonArray()) {
                JsonObject jsonObj = element.getAsJsonObject();
                ItemStat stat = ItemStats.byName(JSONUtils.getString(jsonObj, "name"));
                if (stat != null) {
                    map.put(stat, StatInstance.read(stat, element));
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
            ItemStat stat = ItemStats.REGISTRY.get().getValue(buffer.readResourceLocation());
            StatInstance instance = StatInstance.read(buffer);
            map.put(stat, instance);
        }

        return map;
    }

    public void write(PacketBuffer buffer) {
        buffer.writeVarInt(this.size());
        this.forEach((stat, instance) -> {
            buffer.writeResourceLocation(stat.getStatId());
            instance.write(buffer);
        });
    }
}
