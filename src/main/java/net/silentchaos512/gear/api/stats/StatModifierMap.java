package net.silentchaos512.gear.api.stats;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class StatModifierMap implements Multimap<ItemStat, StatInstance> {

    Multimap<ItemStat, StatInstance> map = ArrayListMultimap.create();

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    public boolean containsEntry(Object key, Object value) {
        return map.containsEntry(key, value);
    }

    @Override
    public boolean put(ItemStat key, StatInstance value) {
        // Disallow duplicate IDs
        for (StatInstance inst : map.get(key))
            if (inst.getId().equals(value.getId()))
                return false;
        return map.put(key, value);
    }

    @Override
    public boolean remove(Object key, Object value) {
        return map.remove(key, value);
    }

    @Override
    public boolean putAll(ItemStat key, Iterable<? extends StatInstance> values) {
        return map.putAll(key, values);
    }

    @Override
    public boolean putAll(Multimap<? extends ItemStat, ? extends StatInstance> multimap) {
        return map.putAll(multimap);
    }

    @Override
    public Collection<StatInstance> replaceValues(ItemStat key, Iterable<? extends StatInstance> values) {
        return map.replaceValues(key, values);
    }

    @Override
    public Collection<StatInstance> removeAll(Object key) {
        return map.removeAll(key);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Collection<StatInstance> get(ItemStat key) {
        return map.get(key);
    }

    @Override
    public Set<ItemStat> keySet() {
        return map.keySet();
    }

    @Override
    public Multiset<ItemStat> keys() {
        return map.keys();
    }

    @Override
    public Collection<StatInstance> values() {
        return map.values();
    }

    @Override
    public Collection<Entry<ItemStat, StatInstance>> entries() {
        return map.entries();
    }

    @Override
    public Map<ItemStat, Collection<StatInstance>> asMap() {
        return map.asMap();
    }
}
