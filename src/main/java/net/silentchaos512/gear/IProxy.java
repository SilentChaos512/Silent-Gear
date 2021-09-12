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

    @Nullable
    MinecraftServer getServer();
}
