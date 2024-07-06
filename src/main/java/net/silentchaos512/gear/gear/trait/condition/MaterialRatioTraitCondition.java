package net.silentchaos512.gear.gear.trait.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.traits.ITrait;
import net.silentchaos512.gear.api.traits.ITraitCondition;
import net.silentchaos512.gear.api.traits.TraitConditionSerializer;
import net.silentchaos512.gear.api.traits.TraitInstance;
import net.silentchaos512.gear.api.util.IGearComponentInstance;
import net.silentchaos512.gear.api.util.PartGearKey;
import net.silentchaos512.gear.util.TextUtil;

import java.util.Collection;
import java.util.List;

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

    private static final ResourceLocation NAME = SilentGear.getId("material_ratio");

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
        int count = 0;
        for (IGearComponentInstance<?> comp : components) {
            Collection<TraitInstance> traits = comp.getTraits(key, gear);
            for (TraitInstance inst : traits) {
                if (inst.getTrait() == trait) {
                    ++count;
                    break;
                }
            }
        }
        float ratio = (float) count / components.size();
        return ratio >= this.requiredRatio;
    }

    @Override
    public MutableComponent getDisplayText() {
        return TextUtil.translate("trait.condition", "material_ratio", Math.round(this.requiredRatio * 100));
    }
}
