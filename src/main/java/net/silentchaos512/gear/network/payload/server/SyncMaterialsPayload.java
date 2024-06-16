package net.silentchaos512.gear.network.payload.server;

import com.google.common.collect.ImmutableList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.material.IMaterial;
import net.silentchaos512.gear.gear.material.MaterialManager;
import net.silentchaos512.gear.gear.material.MaterialSerializers;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record SyncMaterialsPayload(List<IMaterial> materials) implements CustomPacketPayload {
    public static final ResourceLocation ID = SilentGear.getId("sync_materials");

    public SyncMaterialsPayload() {
        this(ImmutableList.copyOf(MaterialManager.getValues()));
    }

    public SyncMaterialsPayload(FriendlyByteBuf buf) {
        this(readMaterials(buf));
    }

    @NotNull
    private static List<IMaterial> readMaterials(FriendlyByteBuf buf) {
        return MaterialSerializers.readAll(buf);
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        MaterialSerializers.writeAll(this.materials, buf);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
