package net.silentchaos512.gear.gear.trait.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.api.traits.ITraitCondition;
import net.silentchaos512.gear.api.traits.TraitConditionSerializer;
import net.silentchaos512.gear.api.util.GearComponentInstance;
import net.silentchaos512.gear.api.util.PartGearKey;
import net.silentchaos512.gear.gear.trait.Trait;
import net.silentchaos512.gear.util.CodecUtils;
import net.silentchaos512.gear.util.TextUtil;

import java.util.Arrays;
import java.util.List;

public record OrTraitCondition(List<ITraitCondition> children) implements ITraitCondition {
    public static final MapCodec<OrTraitCondition> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Codec.list(ITraitCondition.DISPATCH_CODEC).fieldOf("values").forGetter(c -> c.children)
            ).apply(instance, OrTraitCondition::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, OrTraitCondition> STREAM_CODEC = StreamCodec.of(
            (buf, con) -> CodecUtils.encodeList(buf, con.children, ITraitCondition.STREAM_CODEC),
            buf -> new OrTraitCondition(CodecUtils.decodeList(buf, ITraitCondition.STREAM_CODEC))
    );
    public static final TraitConditionSerializer<OrTraitCondition> SERIALIZER = new TraitConditionSerializer<>(CODEC, STREAM_CODEC);

    public OrTraitCondition(ITraitCondition... values) {
        this(Arrays.asList(values));
    }

    public OrTraitCondition(List<ITraitCondition> children) {
        this.children = children;

        if (this.children.isEmpty()) {
            throw new IllegalArgumentException("Values must not be empty");
        }

        for (ITraitCondition child : this.children) {
            if (child == null) {
                throw new IllegalArgumentException("Value must not be null");
            }
        }
    }

    @Override
    public TraitConditionSerializer<?> serializer() {
        return SERIALIZER;
    }

    @Override
    public boolean matches(Trait trait, PartGearKey key, ItemStack gear, List<? extends GearComponentInstance<?>> components) {
        for (ITraitCondition child : this.children) {
            if (child.matches(trait, key, gear, components)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public MutableComponent getDisplayText() {
        Component text = this.children.stream()
                .map(ITraitCondition::getDisplayText)
                .reduce((t1, t2) -> t1.append(TextUtil.translate("trait.condition", "or")).append(t2))
                .orElseGet(() -> Component.literal(""));
        return Component.literal("(").append(text).append(")");
    }
}
