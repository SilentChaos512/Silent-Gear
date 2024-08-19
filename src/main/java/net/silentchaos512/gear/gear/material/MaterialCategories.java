package net.silentchaos512.gear.gear.material;

import com.mojang.serialization.Codec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.silentchaos512.gear.api.material.IMaterialCategory;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public enum MaterialCategories implements IMaterialCategory {
    METAL,
    GEM,
    ROCK,
    DUST,
    CLOTH,
    FIBER,
    WOOD,
    ORGANIC,
    SLIME,
    SHEET,
    INTANGIBLE,
    BASIC,
    INTERMEDIATE,
    ADVANCED,
    ENDGAME;

    private static final Map<String, IMaterialCategory> CACHE = new HashMap<>();

    @Override
    public String getName() {
        return name().toLowerCase(Locale.ROOT);
    }

    /**
     * Gets a material group given the key. This could be one of the enum values, or an entirely
     * new object if none of those match. Return values are cached.
     *
     * @param key The group key
     * @return Material group
     */
    public static IMaterialCategory get(String key) {
        //noinspection OverlyLongLambda
        return CACHE.computeIfAbsent(key, key1 -> {
            for (MaterialCategories cat : values()) {
                if (cat.getName().equalsIgnoreCase(key1)) {
                    return cat;
                }
            }
            String key2 = key1.toLowerCase(Locale.ROOT);
            return () -> key2;
        });
    }

    public static final Codec<IMaterialCategory> CODEC = Codec.STRING
            .xmap(
                    MaterialCategories::get,
                    IMaterialCategory::getName
            );

    public static final StreamCodec<FriendlyByteBuf, IMaterialCategory> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, IMaterialCategory::getName,
            MaterialCategories::get
    );
}
