package net.silentchaos512.gear.api.util;

import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;
import net.minecraft.network.FriendlyByteBuf;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.part.IPartData;
import net.silentchaos512.gear.api.part.PartType;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class PartGearKey {
    private static final Map<Pair<GearType, PartType>, PartGearKey> CACHE = new HashMap<>();

    private final String key;
    private final GearType gearType;
    private final PartType partType;

    private PartGearKey(GearType gearType, PartType partType) {
        this.key = SilentGear.shortenId(partType.getName()) + "/" + gearType.getName();
        this.gearType = gearType;
        this.partType = partType;
    }

    public static PartGearKey of(GearType gearType, IPartData part) {
        return of(gearType, part.getType());
    }

    public static PartGearKey of(GearType gearType, PartType partType) {
        return CACHE.computeIfAbsent(Pair.of(gearType, partType), pair ->
                new PartGearKey(pair.getFirst(), pair.getSecond()));
    }

    public static PartGearKey ofAll(PartType partType) {
        return of(GearType.ALL, partType);
    }

    @Nullable
    public PartGearKey getParent() {
        GearType parent = this.gearType.getParent();
        if (parent != null) {
            return of(parent, this.partType);
        }
        return null;
    }

    public GearType getGearType() {
        return gearType;
    }

    public PartType getPartType() {
        return partType;
    }

    public static PartGearKey read(String key) {
        String[] parts = key.split("/");
        if (parts.length != 2) {
            throw new JsonParseException("invalid key: " + key);
        }

        PartType partType = PartType.getNonNull(Objects.requireNonNull(SilentGear.getIdWithDefaultNamespace(parts[0])));
        if (partType.isInvalid()) {
            throw new JsonParseException("Unknown part type: " + parts[0]);
        }

        GearType gearType = GearType.get(parts[1]);
        if (gearType.isInvalid()) {
            throw new JsonParseException("Unknown gear type: " + parts[1]);
        }

        return new PartGearKey(gearType, partType);
    }

    public static PartGearKey fromNetwork(FriendlyByteBuf buf) {
        GearType gearType = GearType.get(buf.readUtf());
        PartType partType = Objects.requireNonNull(PartType.get(buf.readResourceLocation()));
        return of(gearType, partType);
    }

    public void toNetwork(FriendlyByteBuf buf) {
        buf.writeUtf(gearType.getName());
        buf.writeResourceLocation(partType.getName());
    }

    @Override
    public String toString() {
        return key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PartGearKey that = (PartGearKey) o;
        return Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }
}
