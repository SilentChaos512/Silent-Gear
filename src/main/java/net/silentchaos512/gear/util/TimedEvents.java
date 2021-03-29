package net.silentchaos512.gear.util;

import java.time.LocalDateTime;
import java.time.Month;

public final class TimedEvents {
    private TimedEvents() {throw new IllegalAccessError("Utility class");}

    public static boolean isAprilFools() {
        LocalDateTime now = LocalDateTime.now();
        return now.getMonth() == Month.APRIL && now.getDayOfMonth() == 1;
    }
}
