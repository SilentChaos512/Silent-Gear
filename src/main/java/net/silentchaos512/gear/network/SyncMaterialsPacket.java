package net.silentchaos512.gear.network;

import net.minecraft.network.PacketBuffer;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.material.IPartMaterial;
import net.silentchaos512.gear.gear.material.MaterialManager;
import net.silentchaos512.gear.gear.material.PartMaterial;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class SyncMaterialsPacket extends LoginPacket {
    private List<IPartMaterial> materials;

    public SyncMaterialsPacket() {
        this(MaterialManager.getValues());
    }

    public SyncMaterialsPacket(Collection<IPartMaterial> materials) {
        this.materials = new ArrayList<>(materials);
    }

    public static SyncMaterialsPacket fromBytes(PacketBuffer buf) {
        SilentGear.LOGGER.debug("Gear parts packet: {} bytes", buf.readableBytes());
        SyncMaterialsPacket packet = new SyncMaterialsPacket();
        packet.materials = new ArrayList<>();
        int count = buf.readVarInt();

        for (int i = 0; i < count; ++i) {
            packet.materials.add(PartMaterial.Serializer.read(buf));
        }

        return packet;
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeVarInt(this.materials.size());
        this.materials.forEach(mat -> PartMaterial.Serializer.write(buf, (PartMaterial) mat));
    }

    public List<IPartMaterial> getMaterials() {
        return Collections.unmodifiableList(this.materials);
    }
}
