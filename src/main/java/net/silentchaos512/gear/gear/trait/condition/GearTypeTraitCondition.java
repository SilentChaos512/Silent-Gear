package net.silentchaos512.gear.gear.trait.condition;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.property.ComputeContext;
import net.silentchaos512.gear.api.traits.ITraitCondition;
import net.silentchaos512.gear.api.traits.TraitConditionSerializer;
import net.silentchaos512.gear.gear.trait.Trait;
import net.silentchaos512.gear.setup.SgRegistries;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.gear.util.TextUtil;

import java.util.Optional;
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
    public boolean matches(Trait trait, ComputeContext context) {
        if (context instanceof ComputeContext.Part partCtx) {
            if (partCtx.part().getType().is(PartTypes.MAIN)) {
                // Filter on main parts
                return isMatch(context);
            } else {
                // Don't filter yet, this would fail on upgrades
                return true;
            }
        }
        // Gear items
        return isMatch(context);
    }

    private boolean isMatch(ComputeContext context) {
        return context.partGearKey().gearType().matches(this.gearType);
    }

    @Override
    public Optional<ITraitCondition> reduce(Trait trait, ComputeContext context) {
        if (context.partType().is(PartTypes.MAIN) && matches(trait, context)) {
            return Optional.empty();
        }
        return Optional.of(this);
    }

    @Override
    public MutableComponent getDisplayText() {
        return TextUtil.translate("trait.condition", "gear_type", this.gearType.getDisplayName());
    }
}
