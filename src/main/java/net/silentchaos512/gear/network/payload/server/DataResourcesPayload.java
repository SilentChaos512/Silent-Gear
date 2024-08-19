package net.silentchaos512.gear.network.payload.server;

import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public interface DataResourcesPayload<T> {
    Map<ResourceLocation, T> values();
}
