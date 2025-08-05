package net.silentchaos512.gear.api.traits;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.silentchaos512.gear.util.Serializer;

public class TraitEffectType<T extends TraitEffect> extends Serializer<RegistryFriendlyByteBuf, T> {
    private final String wikiDescription;

    @Deprecated(forRemoval = true)
    public TraitEffectType(MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {
        this(codec, streamCodec, "No description provided");
    }

    public TraitEffectType(MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec, String wikiDescription) {
        super(codec, streamCodec);
        this.wikiDescription = wikiDescription;
    }

    public StreamCodec<RegistryFriendlyByteBuf, TraitEffect> rawStreamCodec() {
        //noinspection unchecked
        return (StreamCodec<RegistryFriendlyByteBuf, TraitEffect>) streamCodec();
    }

    public String getWikiDescription() {
        return wikiDescription;
    }
}
