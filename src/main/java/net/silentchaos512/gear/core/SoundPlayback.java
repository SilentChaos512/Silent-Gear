package net.silentchaos512.gear.core;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.silentchaos512.gear.SilentGear;

public record SoundPlayback(
        SoundEvent sound,
        float volume,
        float pitch,
        float pitchDeviation
) {
    public static final Codec<SoundPlayback> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    BuiltInRegistries.SOUND_EVENT.byNameCodec().fieldOf("sound").forGetter(s -> s.sound),
                    Codec.FLOAT.fieldOf("volume").forGetter(s -> s.volume),
                    Codec.FLOAT.fieldOf("pitch").forGetter(s -> s.pitch),
                    Codec.FLOAT.optionalFieldOf("pitch_deviation", 0f).forGetter(s -> s.pitchDeviation)
            ).apply(instance, SoundPlayback::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, SoundPlayback> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.registry(Registries.SOUND_EVENT), s -> s.sound,
            ByteBufCodecs.FLOAT, s -> s.volume,
            ByteBufCodecs.FLOAT, s -> s.pitch,
            ByteBufCodecs.FLOAT, s -> s.pitchDeviation,
            SoundPlayback::new
    );

    public void playAt(Level level, BlockPos pos, SoundSource source) {
        float randomPitch = this.pitch * (float) (1 + this.pitchDeviation * SilentGear.RANDOM.nextGaussian());
        level.playSound(null, pos, this.sound, source, this.volume, randomPitch);
    }
}
