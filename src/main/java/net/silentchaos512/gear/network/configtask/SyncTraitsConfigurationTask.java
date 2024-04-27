package net.silentchaos512.gear.network.configtask;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.network.ConfigurationTask;
import net.neoforged.neoforge.network.configuration.ICustomConfigurationTask;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.network.payload.server.SyncTraitsPayload;

import java.util.function.Consumer;

public record SyncTraitsConfigurationTask() implements ICustomConfigurationTask {
    public static final ConfigurationTask.Type TYPE = new ConfigurationTask.Type(SilentGear.getId("sync_traits"));

    @Override
    public void run(Consumer<CustomPacketPayload> consumer) {
        SyncTraitsPayload payload = new SyncTraitsPayload();
        consumer.accept(payload);
    }

    @Override
    public Type type() {
        return TYPE;
    }
}
