package net.silentchaos512.gear.gear.trait.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.traits.ITrait;
import net.silentchaos512.gear.api.traits.ITraitCondition;
import net.silentchaos512.gear.api.traits.TraitConditionSerializer;
import net.silentchaos512.gear.api.util.IGearComponentInstance;
import net.silentchaos512.gear.api.util.PartGearKey;
import net.silentchaos512.gear.util.CodecUtils;
import net.silentchaos512.gear.util.TextUtil;

import java.util.Arrays;
import java.util.List;

public record AndTraitCondition(List<ITraitCondition> children) implements ITraitCondition {
    public static final MapCodec<AndTraitCondition> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Codec.list(ITraitCondition.DISPATCH_CODEC).fieldOf("values").forGetter(c -> c.children)
            ).apply(instance, AndTraitCondition::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, AndTraitCondition> STREAM_CODEC = StreamCodec.of(
            (buf, con) -> CodecUtils.encodeList(buf, con.children, ITraitCondition.STREAM_CODEC),
            buf -> new AndTraitCondition(CodecUtils.decodeList(buf, ITraitCondition.STREAM_CODEC))
    );
    public static final TraitConditionSerializer<AndTraitCondition> SERIALIZER = new TraitConditionSerializer<>(CODEC, STREAM_CODEC);

    private static final ResourceLocation NAME = SilentGear.getId("and");

    public AndTraitCondition(ITraitCondition... values) {
        this(Arrays.asList(values));
    }

    public AndTraitCondition(List<ITraitCondition> children) {
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
    public ResourceLocation getId() {
        return NAME;
    }

    @Override
    public TraitConditionSerializer<?> serializer() {
        return SERIALIZER;
    }

    @Override
    public boolean matches(ITrait trait, PartGearKey key, ItemStack gear, List<? extends IGearComponentInstance<?>> components) {
        for (ITraitCondition child : this.children) {
            if (!child.matches(trait, key, gear, components)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public MutableComponent getDisplayText() {
        Component text = this.children.stream()
                .map(ITraitCondition::getDisplayText)
                .reduce((t1, t2) -> t1.append(TextUtil.translate("trait.condition", "and")).append(t2))
                .orElseGet(() -> Component.literal(""));
        return Component.literal("(").append(text).append(")");
    }
}
