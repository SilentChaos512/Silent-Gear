package net.silentchaos512.gear.gear.trait.effect;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.api.traits.TraitEffect;
import net.silentchaos512.gear.api.traits.TraitEffectType;
import net.silentchaos512.gear.setup.gear.TraitEffectTypes;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AttachDataComponentsTraitEffect extends TraitEffect {
    public static final MapCodec<AttachDataComponentsTraitEffect> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    DataComponentPatch.CODEC.fieldOf("components").forGetter(e -> e.components)
            ).apply(instance, AttachDataComponentsTraitEffect::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, AttachDataComponentsTraitEffect> STREAM_CODEC = StreamCodec.composite(
            DataComponentPatch.STREAM_CODEC, e -> e.components,
            AttachDataComponentsTraitEffect::new
    );

    private final DataComponentPatch components;

    public AttachDataComponentsTraitEffect(DataComponentPatch components) {
        this.components = components;
    }

    @Override
    public TraitEffectType<?> type() {
        return TraitEffectTypes.ATTACH_DATA_COMPONENTS.get();
    }

    @Override
    public Collection<String> getExtraWikiLines() {
        return List.of();
    }

    @Override
    public void onRecalculatePre(ItemStack gear, int traitLevel) {
        for (Map.Entry<DataComponentType<?>, Optional<?>> entry : this.components.entrySet()) {
            gear.remove(entry.getKey());
        }
    }

    @Override
    public void onRecalculatePost(ItemStack gear, int traitLevel) {
        gear.applyComponentsAndValidate(this.components);
    }
}
