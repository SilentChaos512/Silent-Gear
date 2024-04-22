package net.silentchaos512.gear.network.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.part.IGearPart;
import net.silentchaos512.gear.gear.part.PartSerializers;
import net.silentchaos512.gear.network.SgNetwork;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record SPacketSyncParts(List<IGearPart> parts) implements CustomPacketPayload {
    public static final ResourceLocation ID = SilentGear.getId("sync_parts");

    public SPacketSyncParts(FriendlyByteBuf buf) {
        this(readParts(buf));
    }

    @NotNull
    private static List<IGearPart> readParts(FriendlyByteBuf buf) {
        SgNetwork.verifyNetworkVersion(buf);
        return PartSerializers.readAll(buf);
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        SgNetwork.writeModVersionInfoToNetwork(buf);
        PartSerializers.writeAll(this.parts, buf);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
