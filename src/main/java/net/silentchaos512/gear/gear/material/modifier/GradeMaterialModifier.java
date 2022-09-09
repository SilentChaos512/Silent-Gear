package net.silentchaos512.gear.gear.material.modifier;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.api.material.IMaterialInstance;
import net.silentchaos512.gear.api.material.modifier.IMaterialModifier;
import net.silentchaos512.gear.api.material.modifier.IMaterialModifierType;
import net.silentchaos512.gear.api.part.MaterialGrade;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.stats.StatInstance;
import net.silentchaos512.gear.api.util.StatGearKey;
import net.silentchaos512.gear.util.TextUtil;
import net.silentchaos512.utils.Color;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class GradeMaterialModifier implements IMaterialModifier {
    private final MaterialGrade grade;

    public GradeMaterialModifier(MaterialGrade grade) {
        this.grade = grade;
    }

    public MaterialGrade getGrade() {
        return grade;
    }

    @Override
    public List<StatInstance> modifyStats(PartType partType, StatGearKey key, List<StatInstance> statMods) {
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
        public void removeModifier(ItemStack stack) {
            MaterialGrade.NONE.setGradeOnStack(stack);
        }

        @Nullable
        @Override
        public GradeMaterialModifier read(IMaterialInstance material) {
            MaterialGrade grade = MaterialGrade.fromStack(material.getItem());
            return new GradeMaterialModifier(grade);
        }

        @Override
        public void write(GradeMaterialModifier modifier, ItemStack stack) {
            modifier.grade.setGradeOnStack(stack);
        }
    }
}
