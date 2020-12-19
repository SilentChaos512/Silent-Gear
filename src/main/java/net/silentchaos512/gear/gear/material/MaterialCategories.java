package net.silentchaos512.gear.gear.material;

import net.silentchaos512.gear.api.material.IMaterialCategory;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public enum MaterialCategories implements IMaterialCategory {
    METAL, GEM, ROCK, DUST, CLOTH, WOOD, ORGANIC, SLIME, INTANGIBLE;

    private static final Map<String, IMaterialCategory> CACHE = new HashMap<>();

    @Override
    public String getName() {
        return name().toLowerCase(Locale.ROOT);
    }

    /**
     * Gets a material category given the key. This could be one of the enum values, or an entirely
     * new object if none of those match. Return values are cached.
     *
     * @param key The category key
     * @return Material category
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
}
