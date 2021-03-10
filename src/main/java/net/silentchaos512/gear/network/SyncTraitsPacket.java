package net.silentchaos512.gear.network;

import net.minecraft.network.PacketBuffer;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.traits.ITrait;
import net.silentchaos512.gear.gear.trait.TraitManager;
import net.silentchaos512.gear.gear.trait.TraitSerializers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class SyncTraitsPacket extends LoginPacket {
    private final List<ITrait> traits;

    public SyncTraitsPacket() {
        this(TraitManager.getValues());
    }

    public SyncTraitsPacket(Collection<ITrait> traits) {
        this.traits = new ArrayList<>(traits);
    }

    public static SyncTraitsPacket fromBytes(PacketBuffer buf) {
        SyncTraitsPacket packet = new SyncTraitsPacket(Collections.emptyList());

        // Verify network version
        String netVersion = buf.readString();
        SilentGear.LOGGER.debug("SyncTraitsPacket: network version {}", netVersion);
        Network.verifyNetworkVersion(netVersion);

        int count = buf.readVarInt();
        for (int i = 0; i < count; ++i) {
            packet.traits.add(TraitSerializers.read(buf));
        }

        return packet;
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeString(Network.VERSION);
        buf.writeVarInt(this.traits.size());
        this.traits.forEach(trait -> TraitSerializers.write(trait, buf));
    }

    public List<ITrait> getTraits() {
        return Collections.unmodifiableList(this.traits);
    }
}
