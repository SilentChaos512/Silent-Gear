package net.silentchaos512.gear.gear.material.modifier;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.api.material.modifier.IMaterialModifier;
import net.silentchaos512.gear.api.material.modifier.IMaterialModifierType;
import net.silentchaos512.gear.api.part.MaterialGrade;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.property.GearPropertyValue;
import net.silentchaos512.gear.api.property.NumberProperty;
import net.silentchaos512.gear.api.property.NumberPropertyValue;
import net.silentchaos512.gear.api.util.PropertyKey;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.setup.gear.MaterialModifiers;
import net.silentchaos512.gear.util.Const;
import net.silentchaos512.gear.util.TextUtil;
import net.silentchaos512.lib.util.Color;

import java.util.ArrayList;
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
    public List<GearPropertyValue<?>> modifyStats(MaterialInstance material, PartType partType, PropertyKey<?, ?> key, List<GearPropertyValue<?>> statMods) {
        if (key.property().isAffectedByGrades() && grade != null && key.property() instanceof NumberProperty) {
            float bonus = grade.bonusPercent / 100f;
            List<GearPropertyValue<?>> ret = new ArrayList<>();

            // Apply grade bonus to all modifiers. Makes it easier to see the effect on rods and such.
            for (var mod : statMods) {
                var numberValue = (NumberPropertyValue) mod;
                float value = numberValue.value();
                // Taking the abs of value times bonus makes negative mods become less negative
                ret.add(new NumberPropertyValue(value + Math.abs(value) * bonus, numberValue.operation()));
            }

            return ret;
        }

        return statMods;
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
        public ResourceLocation getId() {
            return Const.GRADE;
        }

        @Override
        public Optional<GradeMaterialModifier> readModifier(ItemStack stack) {
            var grade = MaterialGrade.fromStack(stack);
            if (grade != MaterialGrade.NONE) {
                return Optional.of(new GradeMaterialModifier(grade));
            }
            return Optional.empty();
        }

        @Override
        public void addModifier(GradeMaterialModifier mod, ItemStack stack) {
            mod.grade.setGradeOnStack(stack);
        }

        @Override
        public void removeModifier(ItemStack stack) {
            MaterialGrade.NONE.setGradeOnStack(stack);
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
