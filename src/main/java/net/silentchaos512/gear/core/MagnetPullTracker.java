package net.silentchaos512.gear.core;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber
public class MagnetPullTracker {
    private static final Map<ItemEntity, Vec3> ITEMS_TO_PUSH = new HashMap<>();

    public static void pushItem(ItemEntity item, Vec3 force) {
        var currentForce = ITEMS_TO_PUSH.getOrDefault(item, Vec3.ZERO);
        if (force.lengthSqr() > currentForce.lengthSqr()) {
            ITEMS_TO_PUSH.put(item, force);
        }
    }

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        ITEMS_TO_PUSH.forEach((item, force) -> {
            item.push(force.x, force.y, force.z);
        });
        ITEMS_TO_PUSH.clear();
    }
}
