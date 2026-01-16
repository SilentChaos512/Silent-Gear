package net.silentchaos512.gear.api.property;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.SwingAnimationType;
import net.minecraft.world.item.component.SwingAnimation;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.util.GearComponentInstance;
import net.silentchaos512.gear.api.util.PartGearKey;

import java.util.Collection;
import java.util.List;

public class SwingAnimationProperty extends GearProperty<SwingAnimation, SwingAnimationPropertyValue> {
    public static final Codec<SwingAnimationPropertyValue> CODEC = GearPropertyValue.createSimpleValueCodec(
            SwingAnimation.CODEC,
            SwingAnimationPropertyValue::new
    );

    public static final StreamCodec<ByteBuf, SwingAnimationPropertyValue> STREAM_CODEC = GearPropertyValue.createSimpleStreamCodec(
            SwingAnimation.STREAM_CODEC,
            SwingAnimationPropertyValue::new
    );

    public SwingAnimationProperty(Builder<SwingAnimation> builder) {
        super(builder);
    }

    @Override
    public Codec<SwingAnimationPropertyValue> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, SwingAnimationPropertyValue> streamCodec() {
        return STREAM_CODEC;
    }

    @Override
    public SwingAnimationPropertyValue valueOf(SwingAnimation value) {
        return new SwingAnimationPropertyValue(value);
    }

    @Override
    public SwingAnimation compute(SwingAnimation baseValue, boolean clampResult, GearType itemType, GearType statType, Collection<SwingAnimationPropertyValue> modifiers) {
        if (modifiers.isEmpty()) {
            return SwingAnimation.DEFAULT;
        }
        SwingAnimationType animationType = null;
        int totalDuration = 0;
        for (SwingAnimationPropertyValue value : modifiers) {
            if (animationType == null) {
                animationType = value.value.type();
            }
            totalDuration += value.value.duration();
        }

        int duration = totalDuration / modifiers.size();
        return new SwingAnimation(animationType, duration);
    }

    @Override
    public SwingAnimation getZeroValue() {
        return SwingAnimation.DEFAULT;
    }

    @Override
    public boolean isZero(SwingAnimation value) {
        return value.equals(SwingAnimation.DEFAULT) || value.duration() == 0;
    }

    @Override
    public List<SwingAnimationPropertyValue> compressModifiers(Collection<SwingAnimationPropertyValue> modifiers, PartGearKey key, List<? extends GearComponentInstance<?>> components) {
        return List.of(new SwingAnimationPropertyValue(compute(getBaseValue(), true, key.gearType(), modifiers)));
    }

    @Override
    public Component formatValue(SwingAnimationPropertyValue value, FormatContext formatContext) {
        SwingAnimation animation = value.value;
        float durationInSeconds = animation.duration() / 20f;
        return Component.literal(String.format("%s %.1fs", animation.type().name(), durationInSeconds));
    }

    @Override
    public MutableComponent formatValueWithColor(SwingAnimationPropertyValue value, boolean addColor, FormatContext formatContext) {
        return formatValue(value, formatContext).plainCopy();
    }
}
