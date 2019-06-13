package net.silentchaos512.gear.world.placement;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.gen.placement.AtSurfaceWithExtraConfig;

public class NetherFloorWithExtraConfig extends AtSurfaceWithExtraConfig {
    public final int minHeight;
    public final int maxHeight;

    public NetherFloorWithExtraConfig(int count, float extraChanceIn, int extraCountIn, int minHeight, int maxHeight) {
        super(count, extraChanceIn, extraCountIn);
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
        return new Dynamic<>(dynamicOps, dynamicOps.createMap(ImmutableMap.of(
                dynamicOps.createString("count"), dynamicOps.createInt(this.count),
                dynamicOps.createString("extra_chance"), dynamicOps.createFloat(this.extraChance),
                dynamicOps.createString("extra_count"), dynamicOps.createInt(this.extraCount),
                dynamicOps.createString("min_height"), dynamicOps.createInt(this.minHeight),
                dynamicOps.createString("max_height"), dynamicOps.createInt(this.maxHeight)
        )));
    }

    public static NetherFloorWithExtraConfig deserialize(Dynamic<?> dynamic) {
        int count = dynamic.get("count").asInt(0);
        float extraChance = dynamic.get("extra_chance").asFloat(0.0F);
        int extraCount = dynamic.get("extra_count").asInt(0);
        int minHeight = dynamic.get("min_height").asInt(0);
        int maxHeight = dynamic.get("max_height").asInt(128);
        return new NetherFloorWithExtraConfig(count, extraChance, extraCount, minHeight, maxHeight);
    }
}
