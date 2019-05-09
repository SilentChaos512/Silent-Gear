package net.silentchaos512.gear.api.stats;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import net.minecraft.network.PacketBuffer;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class StatModifierMap implements Multimap<ItemStat, StatInstance> {
    private final Multimap<ItemStat, StatInstance> map = ArrayListMultimap.create();

    @Override
    public int size() {
        return this.map.size();
    }

    @Override
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return this.map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.map.containsValue(value);
    }

    @Override
    public boolean containsEntry(Object key, Object value) {
        return this.map.containsEntry(key, value);
    }

    @Override
    public boolean put(ItemStat key, StatInstance value) {
        // Disallow duplicate IDs
        for (StatInstance inst : this.map.get(key))
            if (inst.getId().equals(value.getId()))
                return false;
        return this.map.put(key, value);
    }

    @Override
    public boolean remove(Object key, Object value) {
        return this.map.remove(key, value);
    }

    @Override
    public boolean putAll(ItemStat key, @Nonnull Iterable<? extends StatInstance> values) {
        return this.map.putAll(key, values);
    }

    @Override
    public boolean putAll(@Nonnull Multimap<? extends ItemStat, ? extends StatInstance> multimap) {
        return this.map.putAll(multimap);
    }

    @Override
    public Collection<StatInstance> replaceValues(ItemStat key, @Nonnull Iterable<? extends StatInstance> values) {
        return this.map.replaceValues(key, values);
    }

    @Override
    public Collection<StatInstance> removeAll(Object key) {
        return this.map.removeAll(key);
    }

    @Override
    public void clear() {
        this.map.clear();
    }

    @Override
    public Collection<StatInstance> get(ItemStat key) {
        return this.map.get(key);
    }

    @Override
    public Set<ItemStat> keySet() {
        return this.map.keySet();
    }

    @Override
    public Multiset<ItemStat> keys() {
        return this.map.keys();
    }

    @Override
    public Collection<StatInstance> values() {
        return this.map.values();
    }

    @Override
    public Collection<Entry<ItemStat, StatInstance>> entries() {
        return this.map.entries();
    }

    @Override
    public Map<ItemStat, Collection<StatInstance>> asMap() {
        return this.map.asMap();
    }

    public static StatModifierMap read(PacketBuffer buffer) {
        StatModifierMap map = new StatModifierMap();

        int count = buffer.readVarInt();
        for (int i = 0; i < count; ++i) {
            ItemStat stat = ItemStat.ALL_STATS.get(buffer.readString(255));
            StatInstance instance = StatInstance.read("p" + i, buffer);
            map.put(stat, instance);
        }

        return map;
    }

    public void write(PacketBuffer buffer) {
        buffer.writeVarInt(this.size());
        this.forEach((stat, instance) -> {
            buffer.writeString(stat.name.getPath());
            instance.write(buffer);
        });
    }
}
