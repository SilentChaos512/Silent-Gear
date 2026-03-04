package net.silentchaos512.gear.api.property;

import com.mojang.serialization.Codec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.util.GearComponentInstance;
import net.silentchaos512.gear.api.util.PartGearKey;
import net.silentchaos512.gear.client.tooltip.FormatColorScheme;
import net.silentchaos512.gear.client.util.GearTooltipFlag;

import java.util.Collection;
import java.util.List;

public class BooleanProperty extends GearProperty<Boolean, BooleanPropertyValue> {
    public static final Codec<BooleanPropertyValue> CODEC = GearPropertyValue.createSimpleValueCodec(
            Codec.BOOL,
            BooleanPropertyValue::new
    );

    public static final StreamCodec<FriendlyByteBuf, BooleanPropertyValue> STREAM_CODEC = StreamCodec.of(
            (buf, val) -> ByteBufCodecs.BOOL.encode(buf, val.value),
            buf -> new BooleanPropertyValue(ByteBufCodecs.BOOL.decode(buf))
    );

    public BooleanProperty(Builder<Boolean> builder) {
        super(builder);
    }

    @Override
    public Codec<BooleanPropertyValue> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, BooleanPropertyValue> streamCodec() {
        return STREAM_CODEC;
    }

    @Override
    public BooleanPropertyValue valueOf(Boolean value) {
        return new BooleanPropertyValue(value);
    }

    @Override
    public Boolean compute(ComputeContext context, Boolean baseValue, boolean clampResult, GearType itemType, GearType statType, Collection<BooleanPropertyValue> modifiers) {
        for (BooleanPropertyValue mod : modifiers) {
            if (mod.value) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Boolean getZeroValue() {
        return false;
    }

    @Override
    public boolean isZero(Boolean value) {
        return !value;
    }

    @Override
    public List<BooleanPropertyValue> compressModifiers(ComputeContext context, Collection<BooleanPropertyValue> modifiers, PartGearKey key, List<? extends GearComponentInstance<?>> components) {
        return List.of(valueOf(compute(context, getBaseValue(), true, key.gearType(), modifiers)));
    }

    @Override
    public Component formatValue(BooleanPropertyValue value, FormatContext formatContext, FormatColorScheme colorScheme) {
        return value.value
                ? Component.translatable("property.silentgear.boolean_true")
                : Component.translatable("property.silentgear.boolean_false");
    }

    @Override
    public MutableComponent formatValueWithColor(BooleanPropertyValue value, FormatContext formatContext, FormatColorScheme colorScheme) {
        return formatValue(value, formatContext, FormatColorScheme.NO_COLORS).plainCopy();
    }

    @Override
    public boolean isHidden(BooleanPropertyValue value, GearTooltipFlag flag) {
        return !value.value;
    }
}
