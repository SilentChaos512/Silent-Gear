package net.silentchaos512.gear.network;

import net.minecraft.network.PacketBuffer;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.part.IGearPart;
import net.silentchaos512.gear.gear.part.PartManager;
import net.silentchaos512.gear.gear.part.PartSerializers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class SyncGearPartsPacket extends LoginPacket {
    private final List<IGearPart> parts;

    public SyncGearPartsPacket() {
        this(PartManager.getValues());
    }

    public SyncGearPartsPacket(Collection<IGearPart> parts) {
        this.parts = new ArrayList<>(parts);
    }

    public static SyncGearPartsPacket fromBytes(PacketBuffer buf) {
        SilentGear.LOGGER.debug("Gear parts packet: {} bytes", buf.readableBytes());
        SyncGearPartsPacket packet = new SyncGearPartsPacket(Collections.emptyList());

        // Verify network version
        String netVersion = buf.readString();
        SilentGear.LOGGER.debug("SyncGearPartsPacket: network version {}", netVersion);
        Network.verifyNetworkVersion(netVersion);

        // Read parts
        int count = buf.readVarInt();
        for (int i = 0; i < count; ++i) {
            packet.parts.add(PartSerializers.read(buf));
        }

        return packet;
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeString(Network.VERSION);
        buf.writeVarInt(this.parts.size());
        this.parts.forEach(part -> PartSerializers.write(part, buf));
    }

    public List<IGearPart> getParts() {
        return Collections.unmodifiableList(this.parts);
    }
}
