package net.silentchaos512.gear.api.stats;

import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

/**
 * A stat which may not exist. Useful for supporting stats added by other mods, mainly for data
 * generators.
 */
public final class LazyItemStat implements IItemStat {
    private static final Map<ResourceLocation, LazyItemStat> CACHE = new HashMap<>();

    private final ResourceLocation id;

    private LazyItemStat(ResourceLocation id) {
        this.id = id;
    }

    public static LazyItemStat of(ResourceLocation id) {
        return CACHE.computeIfAbsent(id, LazyItemStat::new);
    }

    @Override
    public ResourceLocation getStatId() {
        return id;
    }
}
