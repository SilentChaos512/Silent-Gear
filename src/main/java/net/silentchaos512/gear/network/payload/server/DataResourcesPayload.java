package net.silentchaos512.gear.network.payload.server;

import net.minecraft.resources.Identifier;

import java.util.Map;

public interface DataResourcesPayload<T> {
    Map<Identifier, T> values();
}
