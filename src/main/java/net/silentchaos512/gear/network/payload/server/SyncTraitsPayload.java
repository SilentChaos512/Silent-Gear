package net.silentchaos512.gear.network.payload.server;

import com.google.common.collect.ImmutableList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.traits.ITrait;
import net.silentchaos512.gear.gear.trait.TraitManager;
import net.silentchaos512.gear.gear.trait.TraitSerializers;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record SyncTraitsPayload(List<ITrait> traits) implements CustomPacketPayload {
    public static final ResourceLocation ID = SilentGear.getId("sync_traits");

    public SyncTraitsPayload() {
        this(ImmutableList.copyOf(TraitManager.getValues()));
    }

    public SyncTraitsPayload(FriendlyByteBuf buf) {
        this(readTraits(buf));
    }

    @NotNull
    private static List<ITrait> readTraits(FriendlyByteBuf buf) {
        return TraitSerializers.readAll(buf);
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        TraitSerializers.writeAll(this.traits, buf);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
