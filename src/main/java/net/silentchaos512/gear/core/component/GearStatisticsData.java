package net.silentchaos512.gear.core.component;

import net.minecraft.network.chat.Component;

public record GearStatisticsData(
        Component creator,
        int blocksMined,
        int mobsHit,
        int mobsKilled
) {
}
