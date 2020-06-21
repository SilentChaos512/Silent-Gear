package net.silentchaos512.gear.api.stats;

import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class LazyItemStat implements IItemStat {
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
