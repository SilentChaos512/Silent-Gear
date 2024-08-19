package net.silentchaos512.gear.api.property;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.traits.TraitInstance;
import net.silentchaos512.gear.api.util.GearComponentInstance;
import net.silentchaos512.gear.api.util.PartGearKey;
import net.silentchaos512.gear.gear.trait.Trait;
import net.silentchaos512.gear.util.CodecUtils;
import net.silentchaos512.gear.util.TraitHelper;

import java.util.*;
import java.util.stream.Collectors;

public class TraitListProperty extends GearProperty<List<TraitInstance>, TraitListPropertyValue> {
    public static final Codec<TraitListPropertyValue> CODEC = Codec.list(TraitInstance.CODEC)
            .xmap(
                    TraitListPropertyValue::new,
                    GearPropertyValue::value
            );
    public static final StreamCodec<RegistryFriendlyByteBuf, TraitListPropertyValue> STREAM_CODEC = StreamCodec.of(
            (buf, val) -> CodecUtils.encodeList(buf, val.value, TraitInstance.STREAM_CODEC),
            buf -> new TraitListPropertyValue(CodecUtils.decodeList(buf, TraitInstance.STREAM_CODEC))
    );

    public TraitListProperty(Builder<List<TraitInstance>> builder) {
        super(builder);
    }

    @Override
    public Codec<TraitListPropertyValue> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, TraitListPropertyValue> streamCodec() {
        return STREAM_CODEC;
    }

    @Override
    public TraitListPropertyValue valueOf(List<TraitInstance> value) {
        return new TraitListPropertyValue(value);
    }

    @Override
    public List<TraitInstance> compute(List<TraitInstance> baseValue, boolean clampResult, GearType itemType, GearType statType, Collection<TraitListPropertyValue> modifiers) {
        if (modifiers.isEmpty()) {
            return baseValue;
        }

        Map<Trait, Integer> map = new LinkedHashMap<>();
        Map<Trait, Integer> count = new HashMap<>();

        for (TraitListPropertyValue mod : modifiers) {
            for (TraitInstance traitInstance : mod.value) {
                map.merge(traitInstance.getTrait(), traitInstance.getLevel(), Integer::sum);
                count.merge(traitInstance.getTrait(), 1, Integer::sum);
            }
        }

        Trait[] keys = map.keySet().toArray(new Trait[0]);

        for (Trait trait : keys) {
            final int matsWithTrait = count.get(trait);
            final float divisor = Math.max(modifiers.size() / 2f, matsWithTrait);
            final int value = Math.round(map.get(trait) / divisor);
            map.put(trait, Mth.clamp(value, 1, trait.getMaxLevel()));
        }

        // TODO: Trait cancelling? Events?

        List<TraitInstance> ret = new ArrayList<>();
        map.forEach((trait, level) -> ret.add(TraitInstance.of(trait, level)));
        return ret;
    }

    @Override
    public List<TraitListPropertyValue> compressModifiers(Collection<TraitListPropertyValue> modifiers, PartGearKey key, List<? extends GearComponentInstance<?>> components) {
        var value = TraitHelper.getTraitsFromComponents(components, key, ItemStack.EMPTY);
        return Collections.singletonList(new TraitListPropertyValue(value));
    }

    @Override
    public List<TraitInstance> getZeroValue() {
        return List.of();
    }

    @Override
    public MutableComponent getFormattedText(TraitListPropertyValue value, int decimalPlaces, boolean addColor) {
        return Component.literal(
                value.value.stream()
                        .map(TraitInstance::getDisplayName)
                        .map(Component::getString)
                        .collect(Collectors.joining(", "))
        );
    }
}
