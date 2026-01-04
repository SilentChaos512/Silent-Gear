package net.silentchaos512.gear.gear.material.modifier;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.api.material.modifier.IMaterialModifier;
import net.silentchaos512.gear.api.material.modifier.IMaterialModifierType;
import net.silentchaos512.gear.api.part.MaterialGrade;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.property.GearPropertyValue;
import net.silentchaos512.gear.api.util.PropertyKey;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.setup.SgDataComponents;
import net.silentchaos512.gear.setup.gear.MaterialModifiers;
import net.silentchaos512.gear.util.Const;
import net.silentchaos512.gear.util.TextUtil;
import net.silentchaos512.lib.util.Color;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public record GradeMaterialModifier(MaterialGrade grade) implements IMaterialModifier {
    public static final MapCodec<GradeMaterialModifier> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    MaterialGrade.CODEC.fieldOf("grade").forGetter(m -> m.grade)
            ).apply(instance, GradeMaterialModifier::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, GradeMaterialModifier> STREAM_CODEC = StreamCodec.composite(
            MaterialGrade.STREAM_CODEC, m -> m.grade,
            GradeMaterialModifier::new
    );

    @Override
    public IMaterialModifierType<?> getType() {
        return MaterialModifiers.GRADE.get();
    }

    @Override
    public <T, V extends GearPropertyValue<T>> Collection<V> modifyProperties(MaterialInstance material, PartType partType, PropertyKey<T, V> key, Collection<V> mods) {
        if (key.property().isAffectedByGrades() && grade != null) {
            final float bonus = grade.bonusPercent / 100f;
            return IMaterialModifier.Helper.modifyNumberValuesWithBonusOrPenalty(key, mods, bonus);
        }

        return mods;
    }

    @Override
    public void appendTooltip(List<Component> tooltip) {
        Component text = TextUtil.withColor(grade.getDisplayName(), Color.DEEPSKYBLUE);
        tooltip.add(Component.translatable("part.silentgear.gradeOnPart", text));
    }

    @Override
    public MutableComponent modifyMaterialName(MutableComponent name) {
        if (grade != MaterialGrade.NONE) {
            return name.append(TextUtil.translate("misc", "spaceBrackets", grade.getDisplayName()));
        }

        return name;
    }

    public static class Type implements IMaterialModifierType<GradeMaterialModifier> {
        @Override
        public Identifier getId() {
            return Const.GRADE;
        }

        @Override
        public Optional<GradeMaterialModifier> readModifier(ItemStack stack) {
            var grade = stack.get(SgDataComponents.MATERIAL_GRADE);
            if (grade != null) {
                return Optional.of(new GradeMaterialModifier(grade));
            }
            return Optional.empty();
        }

        @Override
        public void addModifier(GradeMaterialModifier mod, ItemStack stack) {
            stack.set(SgDataComponents.MATERIAL_GRADE, mod.grade);
        }

        @Override
        public void removeModifier(ItemStack stack) {
            stack.remove(SgDataComponents.MATERIAL_GRADE);
        }

        @Override
        public MapCodec<GradeMaterialModifier> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, GradeMaterialModifier> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
