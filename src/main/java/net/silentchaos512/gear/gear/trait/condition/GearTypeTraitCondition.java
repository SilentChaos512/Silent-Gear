package net.silentchaos512.gear.gear.trait.condition;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.traits.ITraitCondition;
import net.silentchaos512.gear.api.traits.TraitConditionSerializer;
import net.silentchaos512.gear.api.util.GearComponentInstance;
import net.silentchaos512.gear.api.util.PartGearKey;
import net.silentchaos512.gear.gear.trait.Trait;
import net.silentchaos512.gear.setup.SgRegistries;
import net.silentchaos512.gear.util.TextUtil;

import java.util.List;
import java.util.function.Supplier;

public record GearTypeTraitCondition(GearType gearType) implements ITraitCondition {
    public static final MapCodec<GearTypeTraitCondition> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    SgRegistries.GEAR_TYPE.byNameCodec().fieldOf("gear_type").forGetter(c -> c.gearType)
            ).apply(instance, GearTypeTraitCondition::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, GearTypeTraitCondition> STREAM_CODEC = StreamCodec.of(
            (buf, con) -> {
                ByteBufCodecs.registry(SgRegistries.GEAR_TYPE_KEY).encode(buf, con.gearType);
            },
            buf -> {
                var gearType = ByteBufCodecs.registry(SgRegistries.GEAR_TYPE_KEY).decode(buf);
                return new GearTypeTraitCondition(gearType);
            }
    );
    public static final TraitConditionSerializer<GearTypeTraitCondition> SERIALIZER = new TraitConditionSerializer<>(CODEC, STREAM_CODEC);

    public GearTypeTraitCondition(Supplier<GearType> gearType) {
        this(gearType.get());
    }

    @Override
    public TraitConditionSerializer<?> serializer() {
        return SERIALIZER;
    }

    @Override
    public boolean matches(Trait trait, PartGearKey key, List<? extends GearComponentInstance<?>> components) {
        return key.getGearType().matches(this.gearType);
    }

    @Override
    public MutableComponent getDisplayText() {
        return TextUtil.translate("trait.condition", "gear_type", this.gearType.getDisplayName());
    }
}
