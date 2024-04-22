package net.silentchaos512.gear.network.config;

import com.google.common.collect.ImmutableList;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.network.ConfigurationTask;
import net.neoforged.neoforge.network.configuration.ICustomConfigurationTask;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.gear.trait.TraitManager;
import net.silentchaos512.gear.network.server.SPacketSyncTraits;

import java.util.function.Consumer;

public record SyncTraitsConfigurationTask() implements ICustomConfigurationTask {
    public static final ConfigurationTask.Type TYPE = new ConfigurationTask.Type(SilentGear.getId("sync_traits"));

    @Override
    public void run(Consumer<CustomPacketPayload> consumer) {
        SPacketSyncTraits payload = new SPacketSyncTraits(ImmutableList.copyOf(TraitManager.getValues()));
        consumer.accept(payload);
    }

    @Override
    public Type type() {
        return TYPE;
    }
}
