package net.silentchaos512.gear.network;

import net.minecraft.network.PacketBuffer;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.material.IMaterial;
import net.silentchaos512.gear.gear.material.MaterialManager;
import net.silentchaos512.gear.gear.material.MaterialSerializers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class SyncMaterialsPacket extends LoginPacket {
    private final List<IMaterial> materials;

    public SyncMaterialsPacket() {
        this(MaterialManager.getValues());
    }

    public SyncMaterialsPacket(Collection<IMaterial> materials) {
        this.materials = new ArrayList<>(materials);
    }

    public static SyncMaterialsPacket fromBytes(PacketBuffer buf) {
        SilentGear.LOGGER.debug("Materials packet: {} bytes", buf.readableBytes());
        SyncMaterialsPacket packet = new SyncMaterialsPacket(Collections.emptyList());

        // Verify network version
        String netVersion = buf.readString();
        SilentGear.LOGGER.debug("SyncMaterialsPacket: network version {}", netVersion);
        Network.verifyNetworkVersion(netVersion);

        // Read materials
        int count = buf.readVarInt();
        for (int i = 0; i < count; ++i) {
            packet.materials.add(MaterialSerializers.read(buf));
        }

        return packet;
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeString(Network.VERSION);
        buf.writeVarInt(this.materials.size());
        this.materials.forEach(mat -> MaterialSerializers.write(mat, buf));
    }

    public List<IMaterial> getMaterials() {
        return Collections.unmodifiableList(this.materials);
    }
}
