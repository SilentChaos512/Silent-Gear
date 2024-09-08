package net.silentchaos512.gear.api.property;

import com.mojang.serialization.Codec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.StreamCodec;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.util.GearComponentInstance;
import net.silentchaos512.gear.api.util.PartGearKey;

import java.util.Collection;
import java.util.List;

public class HarvestTierProperty extends GearProperty<HarvestTier, HarvestTierPropertyValue> {
    public static final Codec<HarvestTierPropertyValue> CODEC = GearPropertyValue.createSimpleValueCodec(
            HarvestTier.CODEC,
            HarvestTierPropertyValue::new
    );

    public static final StreamCodec<FriendlyByteBuf, HarvestTierPropertyValue> STREAM_CODEC = GearPropertyValue.createSimpleStreamCodec(
            HarvestTier.STREAM_CODEC,
            HarvestTierPropertyValue::new
    );

    public HarvestTierProperty(Builder<HarvestTier> builder) {
        super(builder);
    }

    @Override
    public Codec<HarvestTierPropertyValue> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, HarvestTierPropertyValue> streamCodec() {
        return STREAM_CODEC;
    }

    @Override
    public HarvestTierPropertyValue valueOf(HarvestTier value) {
        return new HarvestTierPropertyValue(value);
    }

    @Override
    public HarvestTier compute(HarvestTier baseValue, boolean clampResult, GearType itemType, GearType statType, Collection<HarvestTierPropertyValue> modifiers) {
        HarvestTier possibleBest = baseValue;

        for (var mod : modifiers) {
            if (mod.value.isBetterThan(possibleBest)) {
                possibleBest = mod.value;
            }
        }

        return possibleBest;
    }

    @Override
    public HarvestTier getZeroValue() {
        return HarvestTier.ZERO;
    }

    @Override
    public boolean isZero(HarvestTier value) {
        return value.incorrectForTool().equals(HarvestTier.ZERO.incorrectForTool());
    }

    @Override
    public List<HarvestTierPropertyValue> compressModifiers(Collection<HarvestTierPropertyValue> modifiers, PartGearKey key, List<? extends GearComponentInstance<?>> components) {
        var value = compute(getBaseValue(), true, key.gearType(), modifiers);
        return List.of(new HarvestTierPropertyValue(value));
    }

    @Override
    public Component formatValue(HarvestTierPropertyValue value) {
        return Component.literal(value.value.name());
    }

    @Override
    public MutableComponent formatValueWithColor(HarvestTierPropertyValue value, boolean addColor) {
        return Component.literal(value.value.name());
    }
}
