package net.silentchaos512.gear;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;

import javax.annotation.Nullable;

public interface IProxy {
    @Nullable
    PlayerEntity getClientPlayer();

    @Nullable
    MinecraftServer getServer();
}
