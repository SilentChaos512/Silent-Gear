package net.silentchaos512.gear.block.container;

import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.silentchaos512.gear.network.payload.client.ToggleWorkModePayload;

public interface ToggleableWorkMode {
    boolean getWorkEnabled();

    void setWorkEnabled(boolean enabled);

    default void toggleWorkEnabled() {
        boolean newMode = !getWorkEnabled();
        setWorkEnabled(newMode);
        ClientPacketDistributor.sendToServer(new ToggleWorkModePayload(newMode));
    }
}
