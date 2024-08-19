package net.silentchaos512.gear.api.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.network.chat.Component;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.gear.part.PartInstance;
import net.silentchaos512.gear.setup.SgRegistries;
import net.silentchaos512.gear.setup.gear.GearTypes;
import net.silentchaos512.gear.setup.gear.PartTypes;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public record PartGearKey (
        GearType gearType,
        PartType partType
){
    private static final Map<Pair<GearType, PartType>, PartGearKey> CACHE = new ConcurrentHashMap<>();
    
    public static final Codec<PartGearKey> CODEC = Codec.STRING
            .comapFlatMap(PartGearKey::tryParseKey, PartGearKey::key)
            .stable();

    public static final PartGearKey ALL_MAIN = of(GearTypes.ALL.get(), PartTypes.MAIN.get());

    public static PartGearKey of(GearType gearType, PartInstance part) {
        return of(gearType, part.getType());
    }

    public static PartGearKey of(GearType gearType, PartType partType) {
        return CACHE.computeIfAbsent(Pair.of(gearType, partType), pair ->
                new PartGearKey(pair.getFirst(), pair.getSecond()));
    }

    public static PartGearKey of(Supplier<GearType> gearType, Supplier<PartType> partType) {
        return of(gearType.get(), partType.get());
    }

    public static PartGearKey ofAll(Supplier<PartType> partType) {
        return ofAll(partType.get());
    }

    public static PartGearKey ofAll(PartType partType) {
        return of(GearTypes.ALL.get(), partType);
    }

    public static PartGearKey ofMain(Supplier<GearType> gearType) {
        return ofMain(gearType.get());
    }

    public static PartGearKey ofMain(GearType gearType) {
        return of(gearType, PartTypes.MAIN.get());
    }

    @Nullable
    public PartGearKey getParent() {
        var parent = this.gearType.parent();
        if (parent != null) {
            return of(parent.get(), this.partType);
        }
        return null;
    }

    public GearType getGearType() {
        return gearType;
    }

    public PartType getPartType() {
        return partType;
    }

    public Component getDisplayName() {
        return partType.getDisplayName().append(" / ").append(gearType.getDisplayName());
    }

    private static DataResult<PartGearKey> tryParseKey(String str) {
        var split = str.split("/");
        if (split.length != 2) {
            return DataResult.error(() -> "Invalid key: " + str);
        }
        var partTypeId = SilentGear.getIdWithDefaultNamespace(split[0]);
        var gearTypeId = SilentGear.getIdWithDefaultNamespace(split[1]);
        var gearType = SgRegistries.GEAR_TYPE.get(gearTypeId);
        if (gearType == null || gearType == GearTypes.NONE.get()) {
            return DataResult.error(() -> "Unknown gear type: " + gearTypeId);
        }
        var partType = SgRegistries.PART_TYPE.get(partTypeId);
        if (partType == null || partType == PartTypes.NONE.get()) {
            return DataResult.error(() -> "Unknown part type: " + partTypeId);
        }
        return DataResult.success(of(gearType, partType));
    }

    private String key() {
        var partTypeShortStr = SilentGear.shortenId(SgRegistries.PART_TYPE.getKey(partType()));
        var gearTypeShortStr = SilentGear.shortenId(SgRegistries.GEAR_TYPE.getKey(gearType()));
        return partTypeShortStr + "/" + gearTypeShortStr;
    }
}
