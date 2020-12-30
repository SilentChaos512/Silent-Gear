package net.silentchaos512.gear.util;

import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;
import net.minecraft.network.PacketBuffer;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.stats.IItemStat;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.ItemStats;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class StatGearKey {
    private static final Map<Pair<IItemStat, GearType>, StatGearKey> CACHE = new HashMap<>();

    private final String key;
    private final IItemStat stat;
    private final GearType gearType;

    private StatGearKey(IItemStat stat, GearType gearType) {
        this.stat = stat;
        this.gearType = gearType;

        if (gearType != GearType.ALL) {
            this.key = SilentGear.shortenId(stat.getStatId()) + "/" + gearType.getName();
        } else {
            this.key = SilentGear.shortenId(stat.getStatId());
        }
    }

    public static StatGearKey of(IItemStat stat, GearType gearType) {
        return CACHE.computeIfAbsent(Pair.of(stat, gearType), pair ->
                new StatGearKey(pair.getFirst(), pair.getSecond()));
    }

    @Nullable
    public StatGearKey getParent() {
        GearType parent = this.gearType.getParent();
        if (parent != null) {
            return of(this.stat, parent);
        }
        return null;
    }

    public IItemStat getStat() {
        return stat;
    }

    public GearType getGearType() {
        return gearType;
    }

    @Nullable
    public static StatGearKey read(String key) {
        String[] parts = key.split("/");
        if (parts.length > 2) {
            throw new JsonParseException("invalid key: " + key);
        }

        ItemStat stat = ItemStats.REGISTRY.get().getValue(SilentGear.getIdWithDefaultNamespace(parts[0]));
        if (stat == null) {
            return null;
        }

        GearType gearType;
        if (parts.length > 1) {
            gearType = GearType.get(parts[1]);
            if (gearType.isInvalid()) {
                throw new JsonParseException("Unknown gear type: " + parts[1]);
            }
        } else {
            gearType = GearType.ALL;
        }

        return new StatGearKey(stat, gearType);
    }

    @Nullable
    public static StatGearKey read(PacketBuffer buffer) {
        return read(buffer.readString());
    }

    public void write(PacketBuffer buffer) {
        buffer.writeString(this.key);
    }

    @Override
    public String toString() {
        return key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StatGearKey that = (StatGearKey) o;
        return Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }
}
