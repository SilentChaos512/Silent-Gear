package net.silentchaos512.gear.network;

import net.minecraft.network.PacketBuffer;
import net.silentchaos512.gear.api.traits.ITrait;
import net.silentchaos512.gear.traits.TraitManager;
import net.silentchaos512.gear.traits.TraitSerializers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SyncTraitsPacket extends LoginPacket {
    private List<ITrait> traits;

    public SyncTraitsPacket() {
        this(TraitManager.getValues());
    }

    public SyncTraitsPacket(Collection<ITrait> traits) {
        this.traits = new ArrayList<>(traits);
    }

    public static SyncTraitsPacket fromBytes(PacketBuffer buf) {
        SyncTraitsPacket packet = new SyncTraitsPacket();
        packet.traits = new ArrayList<>();
        int count = buf.readVarInt();

        for (int i = 0; i < count; ++i) {
            packet.traits.add(TraitSerializers.read(buf));
        }

        return packet;
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeVarInt(this.traits.size());
        this.traits.forEach(trait -> TraitSerializers.write(trait, buf));
    }

    public List<ITrait> getTraits() {
        return this.traits;
    }
}
