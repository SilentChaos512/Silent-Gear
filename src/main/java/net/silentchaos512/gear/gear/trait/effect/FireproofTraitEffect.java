package net.silentchaos512.gear.gear.trait.effect;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.silentchaos512.gear.api.traits.TraitEffect;
import net.silentchaos512.gear.api.traits.TraitEffectType;
import net.silentchaos512.gear.setup.gear.TraitEffectTypes;

import java.util.Collection;
import java.util.List;

public class FireproofTraitEffect extends TraitEffect {
    public static final FireproofTraitEffect INSTANCE = new FireproofTraitEffect();

    public static final MapCodec<FireproofTraitEffect> CODEC =
            MapCodec.unit(INSTANCE);
    public static final StreamCodec<RegistryFriendlyByteBuf, FireproofTraitEffect> STREAM_CODEC =
            StreamCodec.unit(INSTANCE);

    private FireproofTraitEffect() {
    }

    @Override
    public TraitEffectType<?> type() {
        return TraitEffectTypes.FIREPROOF.get();
    }

    @Override
    public Collection<String> getExtraWikiLines() {
        return List.of("The item cannot be destroyed by fire or lava");
    }
}
