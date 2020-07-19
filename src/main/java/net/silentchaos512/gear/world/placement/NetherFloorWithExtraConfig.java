package net.silentchaos512.gear.world.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.placement.AtSurfaceWithExtraConfig;

public class NetherFloorWithExtraConfig extends AtSurfaceWithExtraConfig {
    public static final Codec<NetherFloorWithExtraConfig> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("count").forGetter(config -> config.count),
                    Codec.FLOAT.fieldOf("extra_chance").forGetter(config -> config.extraChance),
                    Codec.INT.fieldOf("extra_count").forGetter(config -> config.extraCount),
                    Codec.INT.fieldOf("min_height").forGetter(config -> config.minHeight),
                    Codec.INT.fieldOf("max_height").forGetter(config -> config.maxHeight)
            ).apply(instance, NetherFloorWithExtraConfig::new));

    public final int minHeight;
    public final int maxHeight;

    public NetherFloorWithExtraConfig(int count, float extraChanceIn, int extraCountIn, int minHeight, int maxHeight) {
        super(count, extraChanceIn, extraCountIn);
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
    }
}
