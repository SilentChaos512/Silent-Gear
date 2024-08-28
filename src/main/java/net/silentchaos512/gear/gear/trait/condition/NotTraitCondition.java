package net.silentchaos512.gear.gear.trait.condition;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.api.traits.ITraitCondition;
import net.silentchaos512.gear.api.traits.TraitConditionSerializer;
import net.silentchaos512.gear.api.util.GearComponentInstance;
import net.silentchaos512.gear.api.util.PartGearKey;
import net.silentchaos512.gear.gear.trait.Trait;
import net.silentchaos512.gear.util.TextUtil;

import java.util.List;

public record NotTraitCondition(ITraitCondition child) implements ITraitCondition {
    public static final MapCodec<NotTraitCondition> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    ITraitCondition.DISPATCH_CODEC.fieldOf("value").forGetter(c -> c.child)
            ).apply(instance, NotTraitCondition::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, NotTraitCondition> STREAM_CODEC = StreamCodec.of(
            (buf, con) -> ITraitCondition.STREAM_CODEC.encode(buf, con.child),
            buf -> new NotTraitCondition(ITraitCondition.STREAM_CODEC.decode(buf))
    );
    public static final TraitConditionSerializer<NotTraitCondition> SERIALIZER = new TraitConditionSerializer<>(CODEC, STREAM_CODEC);

    @Override
    public TraitConditionSerializer<?> serializer() {
        return SERIALIZER;
    }

    @Override
    public boolean matches(Trait trait, PartGearKey key, ItemStack gear, List<? extends GearComponentInstance<?>> components) {
        return !this.child.matches(trait, key, gear, components);
    }

    @Override
    public MutableComponent getDisplayText() {
        return TextUtil.translate("trait.condition", "not", this.child.getDisplayText());
    }
}
