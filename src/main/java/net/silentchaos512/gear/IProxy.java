package net.silentchaos512.gear;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public interface IProxy {
    @Nullable
    Player getClientPlayer();

    @Nullable
    Level getClientLevel();

    /**
     * Checks if the Minecraft instance is not null (client only).
     *
     * @return True if on the server side or if Minecraft.getInstance() returns non-null.
     */
    boolean checkClientInstance();

    /**
     * Checks if Minecraft.getConnection() is not null (client only).
     *
     * @return True if on the server side or if Minecraft.getInstance().getConnection() returns non-null.
     */
    boolean checkClientConnection();

    @Nullable
    MinecraftServer getServer();
}
