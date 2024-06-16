package net.silentchaos512.gear.network.payload.server;

import com.google.common.collect.ImmutableList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.part.IGearPart;
import net.silentchaos512.gear.gear.part.PartManager;
import net.silentchaos512.gear.gear.part.PartSerializers;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record SyncPartsPayload(List<IGearPart> parts) implements CustomPacketPayload {
    public static final ResourceLocation ID = SilentGear.getId("sync_parts");

    public SyncPartsPayload() {
        this(ImmutableList.copyOf(PartManager.getValues()));
    }

    public SyncPartsPayload(FriendlyByteBuf buf) {
        this(readParts(buf));
    }

    @NotNull
    private static List<IGearPart> readParts(FriendlyByteBuf buf) {
        return PartSerializers.readAll(buf);
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        PartSerializers.writeAll(this.parts, buf);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
