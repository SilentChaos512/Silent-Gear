package net.silentchaos512.gear.network.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.traits.ITrait;
import net.silentchaos512.gear.gear.trait.TraitSerializers;
import net.silentchaos512.gear.network.SgNetwork;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record SPacketSyncTraits(List<ITrait> traits) implements CustomPacketPayload {
    public static final ResourceLocation ID = SilentGear.getId("sync_traits");

    public SPacketSyncTraits(FriendlyByteBuf buf) {
        this(readTraits(buf));
    }

    @NotNull
    private static List<ITrait> readTraits(FriendlyByteBuf buf) {
        SgNetwork.verifyNetworkVersion(buf);
        return TraitSerializers.readAll(buf);
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        SgNetwork.writeModVersionInfoToNetwork(buf);
        TraitSerializers.writeAll(this.traits, buf);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
