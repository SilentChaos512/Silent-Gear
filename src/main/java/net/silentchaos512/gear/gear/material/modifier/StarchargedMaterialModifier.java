package net.silentchaos512.gear.gear.material.modifier;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.silentchaos512.gear.api.material.modifier.IMaterialModifierType;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.property.GearPropertyValue;
import net.silentchaos512.gear.api.property.NumberProperty;
import net.silentchaos512.gear.api.property.NumberPropertyValue;
import net.silentchaos512.gear.api.util.ChargedProperties;
import net.silentchaos512.gear.api.util.PropertyKey;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.gear.material.MaterialModifiers;
import net.silentchaos512.gear.setup.gear.GearProperties;
import net.silentchaos512.gear.util.TextUtil;
import net.silentchaos512.lib.util.Color;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class StarchargedMaterialModifier extends ChargedMaterialModifier {
    public StarchargedMaterialModifier(int level) {
        super(level);
    }

    @Override
    public IMaterialModifierType<?> getType() {
        return MaterialModifiers.STARCHARGED;
    }

    @Override
    public List<GearPropertyValue<?>> modifyStats(MaterialInstance material, PartType partType, PropertyKey<?, ?> key, List<GearPropertyValue<?>> modifiers) {
        List<GearPropertyValue<?>> ret = new ArrayList<>();

        if (key.property() == GearProperties.CHARGING_VALUE.get()) {
            return ret;
        }

        for (GearPropertyValue<?> mod : modifiers) {
            GearPropertyValue<?> newMod = modifyStat(key, mod, getChargedProperties(material));
            ret.add(newMod != null ? newMod : mod);
        }

        return ret;
    }

    @Override
    public void appendTooltip(List<Component> tooltip) {
        MutableComponent text = getNameWithLevel();
        tooltip.add(TextUtil.withColor(text, Color.AQUAMARINE));
    }

    @Override
    public MutableComponent modifyMaterialName(MutableComponent name) {
        return getNameWithLevel().append(" ").append(name);
    }

    private MutableComponent getNameWithLevel() {
        MutableComponent text = TextUtil.translate("materialModifier", "starcharged");
        text.append(" ").append(Component.translatable("enchantment.level." + level));
        return text;
    }

    @Nullable
    private static GearPropertyValue<?> modifyStat(PropertyKey<?, ?> key, GearPropertyValue<?> mod, ChargedProperties charge) {
        if (key.property() == GearProperties.CHARGING_VALUE.get()) {
            return null;
        }

        if (mod instanceof NumberPropertyValue numberPropertyValue && isSupportedModifierOp(numberPropertyValue)) {
            //noinspection unchecked
            var numberPropertyKey = (PropertyKey<Float, NumberPropertyValue>) key;
            var modifiedStatValue = (float) getModifiedStatValue(numberPropertyKey, numberPropertyValue, charge);
            return new NumberPropertyValue(modifiedStatValue, numberPropertyValue.operation());
        }
        return null;
    }

    @SuppressWarnings("OverlyComplexMethod")
    private static double getModifiedStatValue(PropertyKey<Float, NumberPropertyValue> key, NumberPropertyValue mod, ChargedProperties charge) {
        if (key.property() == GearProperties.DURABILITY.get())
            return mod.value() * Math.pow(1.25, charge.getChargeValue());
        if (key.property() == GearProperties.ARMOR_DURABILITY.get())
            return mod.value() * Math.pow(1.1, charge.getChargeValue());
        if (key.property() == GearProperties.ENCHANTMENT_VALUE.get())
            return mod.value() * (1 + charge.chargeLevel() * (Math.sqrt(charge.chargeValue() - 1)));
        if (key.property() == GearProperties.HARVEST_SPEED.get())
            return mod.value() + 1.5 * charge.chargeLevel() * charge.getChargeValue();
        if (key.property() == GearProperties.ATTACK_DAMAGE.get())
            return mod.value() + charge.getChargeValue();
        if (key.property() == GearProperties.MAGIC_DAMAGE.get())
            return mod.value() + charge.getChargeValue();
        if (key.property() == GearProperties.RANGED_DAMAGE.get())
            return mod.value() + charge.getChargeValue() / 2.0;
        if (key.property() == GearProperties.ARMOR.get())
            return mod.value() + charge.getChargeValue() * 2.0;
        if (key.property() == GearProperties.ARMOR_TOUGHNESS.get())
            return mod.value() + charge.getChargeValue() * 2.0;
        if (key.property() == GearProperties.MAGIC_ARMOR.get())
            return mod.value() + charge.getChargeValue() * 2.0;

        return mod.value();
    }

    private static boolean isSupportedModifierOp(NumberPropertyValue mod) {
        return mod.operation() == NumberProperty.Operation.AVERAGE
                || mod.operation() == NumberProperty.Operation.MAX
                || mod.operation() == NumberProperty.Operation.ADD;
    }
}
