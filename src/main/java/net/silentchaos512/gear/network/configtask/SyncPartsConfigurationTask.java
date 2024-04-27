package net.silentchaos512.gear.network.configtask;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.configuration.ICustomConfigurationTask;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.network.payload.server.SyncPartsPayload;

import java.util.function.Consumer;

public record SyncPartsConfigurationTask() implements ICustomConfigurationTask {
    public static final Type TYPE = new Type(SilentGear.getId("sync_parts"));

    @Override
    public void run(Consumer<CustomPacketPayload> consumer) {
        SyncPartsPayload payload = new SyncPartsPayload();
        consumer.accept(payload);
    }

    @Override
    public Type type() {
        return TYPE;
    }
}
