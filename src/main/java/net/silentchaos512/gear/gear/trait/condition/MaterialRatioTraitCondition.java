package net.silentchaos512.gear.gear.trait.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.silentchaos512.gear.api.property.ComputeContext;
import net.silentchaos512.gear.api.traits.ITraitCondition;
import net.silentchaos512.gear.api.traits.TraitConditionSerializer;
import net.silentchaos512.gear.api.traits.TraitInstance;
import net.silentchaos512.gear.api.util.GearComponentInstance;
import net.silentchaos512.gear.gear.trait.Trait;
import net.silentchaos512.gear.util.TextUtil;

import java.util.Optional;

public record MaterialRatioTraitCondition(float requiredRatio) implements ITraitCondition {
    public static final MapCodec<MaterialRatioTraitCondition> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Codec.FLOAT.fieldOf("ratio").forGetter(c -> c.requiredRatio)
            ).apply(instance, MaterialRatioTraitCondition::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, MaterialRatioTraitCondition> STREAM_CODEC = StreamCodec.of(
            (buf, con) -> ByteBufCodecs.FLOAT.encode(buf, con.requiredRatio),
            buf -> new MaterialRatioTraitCondition(ByteBufCodecs.FLOAT.decode(buf))
    );
    public static final TraitConditionSerializer<MaterialRatioTraitCondition> SERIALIZER = new TraitConditionSerializer<>(CODEC, STREAM_CODEC);

    @Override
    public TraitConditionSerializer<?> serializer() {
        return SERIALIZER;
    }

    @Override
    public boolean matches(Trait trait, ComputeContext context) {
        int count = 0;
        for (GearComponentInstance<?> comp : context.components()) {
            for (TraitInstance inst : comp.getTraits(context.partGearKey())) {
                if (inst.isValid() && inst.getTrait() == trait) {
                    ++count;
                    break;
                }
            }
        }
        float ratio = (float) count / context.components().size();
        return ratio >= this.requiredRatio;
    }

    @Override
    public Optional<ITraitCondition> reduce(Trait trait, ComputeContext context) {
        if (context instanceof ComputeContext.Gear || context instanceof ComputeContext.Part) {
            return Optional.empty();
        }
        return Optional.of(this);
    }

    @Override
    public MutableComponent getDisplayText() {
        return TextUtil.translate("trait.condition", "material_ratio", Math.round(this.requiredRatio * 100));
    }
}
