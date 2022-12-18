package net.silentchaos512.gear.util;

import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

// Deprecated: use api package class instead
@Deprecated
public class DataResource<T> extends net.silentchaos512.gear.api.util.DataResource<T> {
    public DataResource(ResourceLocation id, Function<ResourceLocation, T> getter) {
        super(id, getter);
    }
}
