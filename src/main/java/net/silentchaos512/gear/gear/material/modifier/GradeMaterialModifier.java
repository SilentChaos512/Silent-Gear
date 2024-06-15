package net.silentchaos512.gear.gear.material.modifier;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.api.material.IMaterialInstance;
import net.silentchaos512.gear.api.material.modifier.IMaterialModifier;
import net.silentchaos512.gear.api.material.modifier.IMaterialModifierType;
import net.silentchaos512.gear.api.part.MaterialGrade;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.stats.StatInstance;
import net.silentchaos512.gear.api.util.StatGearKey;
import net.silentchaos512.gear.gear.material.MaterialModifiers;
import net.silentchaos512.gear.util.Const;
import net.silentchaos512.gear.util.TextUtil;
import net.silentchaos512.lib.util.Color;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public record GradeMaterialModifier(MaterialGrade grade) implements IMaterialModifier {
    public static final Codec<GradeMaterialModifier> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    MaterialGrade.CODEC.fieldOf("grade").forGetter(m -> m.grade)
            ).apply(instance, GradeMaterialModifier::new)
    );

    @Override
    public IMaterialModifierType<?> getType() {
        return MaterialModifiers.GRADE;
    }

    @Override
    public List<StatInstance> modifyStats(IMaterialInstance material, PartType partType, StatGearKey key, List<StatInstance> statMods) {
        if (key.getStat().isAffectedByGrades() && grade != null) {
            float bonus = grade.bonusPercent / 100f;
            List<StatInstance> ret = new ArrayList<>();

            // Apply grade bonus to all modifiers. Makes it easier to see the effect on rods and such.
            for (StatInstance mod : statMods) {
                float value = mod.getValue();
                // Taking the abs of value times bonus makes negative mods become less negative
                ret.add(mod.copySetValue(value + Math.abs(value) * bonus));
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
        public void removeModifier(ItemStack stack) {
            MaterialGrade.NONE.setGradeOnStack(stack);
        }

        @Nullable
        @Override
        public GradeMaterialModifier read(CompoundTag tag) {
            MaterialGrade grade = MaterialGrade.fromNbt(tag);
            return new GradeMaterialModifier(grade);
        }

        @Override
        public void write(GradeMaterialModifier modifier, CompoundTag tag) {
            modifier.grade.writeToNbt(tag);
        }

        @Override
        public GradeMaterialModifier readFromNetwork(FriendlyByteBuf buf) {
            MaterialGrade grade = MaterialGrade.fromString(buf.readUtf());
            return new GradeMaterialModifier(grade);
        }

        @Override
        public void writeToNetwork(GradeMaterialModifier modifier, FriendlyByteBuf buf) {
            buf.writeUtf(modifier.grade.name());
        }

        @Override
        public GradeMaterialModifier deserialize(JsonObject json) {
            MaterialGrade grade = MaterialGrade.fromString(GsonHelper.getAsString(json, "grade"));
            return new GradeMaterialModifier(grade);
        }

        @Override
        public JsonObject serialize(GradeMaterialModifier modifier) {
            JsonObject json = new JsonObject();
            json.addProperty("grade", modifier.grade.name());
            return json;
        }

        @Override
        public Codec<GradeMaterialModifier> codec() {
            return CODEC;
        }
    }
}
