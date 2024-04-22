package net.silentchaos512.gear.network.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.material.IMaterial;
import net.silentchaos512.gear.gear.material.MaterialSerializers;
import net.silentchaos512.gear.network.SgNetwork;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record SPacketSyncMaterials(List<IMaterial> materials) implements CustomPacketPayload {
    public static final ResourceLocation ID = SilentGear.getId("sync_materials");

    public SPacketSyncMaterials(FriendlyByteBuf buf) {
        this(readMaterials(buf));
    }

    @NotNull
    private static List<IMaterial> readMaterials(FriendlyByteBuf buf) {
        SgNetwork.verifyNetworkVersion(buf);
        return MaterialSerializers.readAll(buf);
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        SgNetwork.writeModVersionInfoToNetwork(buf);
        MaterialSerializers.writeAll(this.materials, buf);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
