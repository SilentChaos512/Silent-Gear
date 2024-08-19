package net.silentchaos512.gear.network.payload.server;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.material.Material;
import net.silentchaos512.gear.setup.SgRegistries;

import java.util.HashMap;
import java.util.Map;

public record SyncMaterialsPayload(Map<ResourceLocation, Material> materials) implements CustomPacketPayload, DataResourcesPayload<Material> {
    public static final Type<SyncMaterialsPayload> TYPE = new Type<>(SilentGear.getId("sync_materials"));

    private static final StreamCodec<RegistryFriendlyByteBuf, HashMap<ResourceLocation, Material>> MAP_STREAM_CODEC = ByteBufCodecs.map(
            HashMap::new,
            ResourceLocation.STREAM_CODEC,
            Material.STREAM_CODEC
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncMaterialsPayload> STREAM_CODEC = StreamCodec.of(
            (buf, data) -> MAP_STREAM_CODEC.encode(buf, new HashMap<>(data.materials)),
            buf -> new SyncMaterialsPayload(MAP_STREAM_CODEC.decode(buf))
    );

    public SyncMaterialsPayload() {
        this(SgRegistries.MATERIAL.copyOfMap());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public Map<ResourceLocation, Material> values() {
        return materials;
    }
}
