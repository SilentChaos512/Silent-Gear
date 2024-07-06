package net.silentchaos512.gear.gear.trait.condition;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.traits.ITrait;
import net.silentchaos512.gear.api.traits.ITraitCondition;
import net.silentchaos512.gear.api.traits.TraitConditionSerializer;
import net.silentchaos512.gear.api.util.IGearComponentInstance;
import net.silentchaos512.gear.api.util.PartGearKey;
import net.silentchaos512.gear.setup.SgRegistries;
import net.silentchaos512.gear.util.TextUtil;

import java.util.List;

public record GearTypeTraitCondition(GearType gearType) implements ITraitCondition {
    public static final MapCodec<GearTypeTraitCondition> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    SgRegistries.GEAR_TYPES.byNameCodec().fieldOf("gear_type").forGetter(c -> c.gearType)
            ).apply(instance, GearTypeTraitCondition::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, GearTypeTraitCondition> STREAM_CODEC = StreamCodec.of(
            (buf, con) -> {
                ByteBufCodecs.registry(SgRegistries.GEAR_TYPES_KEY).encode(buf, con.gearType);
            },
            buf -> {
                var gearType = ByteBufCodecs.registry(SgRegistries.GEAR_TYPES_KEY).decode(buf);
                return new GearTypeTraitCondition(gearType);
            }
    );
    public static final TraitConditionSerializer<GearTypeTraitCondition> SERIALIZER = new TraitConditionSerializer<>(CODEC, STREAM_CODEC);

    private static final ResourceLocation NAME = SilentGear.getId("gear_type");

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
        return gear.isEmpty() || key.getGearType().matches(this.gearType);
    }

    @Override
    public MutableComponent getDisplayText() {
        return TextUtil.translate("trait.condition", "gear_type", this.gearType);
    }
}
