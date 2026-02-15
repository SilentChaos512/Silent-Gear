package net.silentchaos512.gear.setup.gear;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.KineticWeapon;
import net.minecraft.world.item.component.SwingAnimation;
import net.minecraft.world.item.enchantment.Enchantable;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.silentchaos512.gear.Config;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.property.*;
import net.silentchaos512.gear.api.traits.TraitInstance;
import net.silentchaos512.gear.client.tooltip.FormatColorScheme;
import net.silentchaos512.gear.client.util.GearTooltipFlag;
import net.silentchaos512.gear.client.util.TextListBuilder;
import net.silentchaos512.gear.setup.SgRegistries;
import net.silentchaos512.gear.util.GearHelper;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class GearProperties {
    public static final DeferredRegister<GearProperty<?, ?>> REGISTRAR = DeferredRegister.create(SgRegistries.GEAR_PROPERTY, SilentGear.MOD_ID);

    public static final Supplier<BooleanProperty> ADDITIVE = REGISTRAR.register(
            "additive",
            () -> new BooleanProperty(
                    new GearProperty.Builder<>(false, false, false, true)
                            .group(GearPropertyGroups.SPECIAL)
                            .forMaterialsOnly(true)
            )
    );
    public static final Supplier<TraitListProperty> TRAITS = REGISTRAR.register(
            "traits",
            () -> new TraitListProperty(
                    new GearProperty.Builder<List<TraitInstance>>(Collections.emptyList())
                            .group(GearPropertyGroups.SPECIAL)
            )
    );
    public static final Supplier<NumberProperty> DURABILITY = REGISTRAR.register(
            "durability",
            () -> new NumberProperty(
                    NumberProperty.Operation.AVERAGE,
                    NumberProperty.DisplayFormat.UNIT,
                    true,
                    new GearProperty.Builder<>(0f, 0f, 0f, (float) Integer.MAX_VALUE)
                            .group(GearPropertyGroups.GENERAL)
                            .affectedByGrades(true)
                            .affectedBySynergy(true)
            ) {
                @Override
                public void buildTooltip(TextListBuilder listBuilder, NumberPropertyValue value, ItemStack stack, GearTooltipFlag flag, FormatColorScheme colorScheme) {
                    // Durability-specific formatting
                    int durabilityLeft = stack.getMaxDamage() - stack.getDamageValue();
                    int durabilityMax = stack.getMaxDamage();
                    var text = Component.translatable("property.silentgear.durabilityFormat", durabilityLeft, durabilityMax);
                    listBuilder.add(formatText(text));
                }
            }
    );
    public static final Supplier<NumberProperty> ARMOR_DURABILITY = REGISTRAR.register(
            "armor_durability",
            () -> new NumberProperty(
                    NumberProperty.Operation.AVERAGE,
                    NumberProperty.DisplayFormat.MULTIPLIER,
                    true,
                    new GearProperty.Builder<>(0f, 0f, 0f, (float) (Integer.MAX_VALUE / 16))
                            .group(GearPropertyGroups.GENERAL)
                            .affectedByGrades(true)
                            .affectedBySynergy(true)
            )
    );
    public static final Supplier<NumberProperty> REPAIR_EFFICIENCY = REGISTRAR.register(
            "repair_efficiency",
            () -> new NumberProperty(
                    NumberProperty.Operation.AVERAGE,
                    NumberProperty.DisplayFormat.PERCENTAGE,
                    false,
                    new GearProperty.Builder<>(1f, 0f, 0f, 1000f)
                            .group(GearPropertyGroups.GENERAL)
                            .affectedByGrades(false)
                            .affectedBySynergy(false)
            )
    );
    public static final Supplier<NumberProperty> REPAIR_VALUE = REGISTRAR.register(
            "repair_value",
            () -> new NumberProperty(
                    NumberProperty.Operation.AVERAGE,
                    NumberProperty.DisplayFormat.PERCENTAGE,
                    false,
                    new GearProperty.Builder<>(1f, 0f, 0f, 1000f)
                            .group(GearPropertyGroups.GENERAL)
                            .affectedByGrades(false)
                            .affectedBySynergy(false)
            )
    );
    public static final Supplier<NumberProperty> ENCHANTMENT_VALUE = REGISTRAR.register(
            "enchantment_value",
            () -> new NumberProperty(
                    NumberProperty.Operation.AVERAGE,
                    NumberProperty.DisplayFormat.UNIT,
                    true,
                    new GearProperty.Builder<>(0f, 0f, 0f, (float) Integer.MAX_VALUE)
                            .group(GearPropertyGroups.GENERAL)
                            .affectedByGrades(true)
                            .affectedBySynergy(true)
                            .onAddDataComponents((stack, value) -> {
                                if (value.intValue() > 0 && Config.Common.isLoaded() && Config.Common.allowEnchanting.get()) {
                                    stack.set(DataComponents.ENCHANTABLE, new Enchantable(value.intValue()));
                                }
                            })
            ) {
                @Override
                public boolean isHidden(NumberPropertyValue value, GearTooltipFlag flag) {
                    // No need to display if enchanting is not allowed per config
                    return Config.Common.isLoaded() && !Config.Common.allowEnchanting.get();
                }
            }
    );
    public static final Supplier<NumberProperty> CHARGING_VALUE = REGISTRAR.register(
            "charging_value",
            () -> new NumberProperty(
                    NumberProperty.Operation.AVERAGE,
                    NumberProperty.DisplayFormat.UNIT,
                    false,
                    new GearProperty.Builder<>(0f, 0f, 0f, (float) Integer.MAX_VALUE)
                            .group(GearPropertyGroups.GENERAL)
                            .affectedByGrades(false)
                            .affectedBySynergy(false)
            )
    );
    public static final Supplier<NumberProperty> RARITY = REGISTRAR.register(
            "rarity",
            () -> new NumberProperty(
                    NumberProperty.Operation.AVERAGE,
                    NumberProperty.DisplayFormat.UNIT,
                    true,
                    new GearProperty.Builder<>(0f, 0f, 0f, (float) Integer.MAX_VALUE)
                            .group(GearPropertyGroups.GENERAL)
                            .affectedByGrades(false)
                            .affectedBySynergy(false)
                            .onAddDataComponents((stack, value) -> {
                                stack.set(DataComponents.RARITY, GearHelper.getRarity(stack));
                            })
            )
    );
    public static final Supplier<HarvestTierProperty> HARVEST_TIER = REGISTRAR.register(
            "harvest_tier",
            () -> new HarvestTierProperty(
                    new GearProperty.Builder<>(HarvestTier.ZERO)
                            .group(GearPropertyGroups.HARVEST)
                            .affectedByGrades(false)
                            .affectedBySynergy(false)
            )
    );
    public static final Supplier<NumberProperty> HARVEST_SPEED = REGISTRAR.register(
            "harvest_speed",
            () -> new NumberProperty(
                    NumberProperty.Operation.AVERAGE,
                    NumberProperty.DisplayFormat.UNIT,
                    false,
                    new GearProperty.Builder<>(0f, 0f, 0f, (float) Integer.MAX_VALUE)
                            .group(GearPropertyGroups.HARVEST)
                            .affectedByGrades(true)
                            .affectedBySynergy(true)
            )
    );
    public static final Supplier<NumberProperty> BLOCK_REACH = REGISTRAR.register(
            "block_reach",
            () -> new NumberProperty(
                    NumberProperty.Operation.AVERAGE,
                    NumberProperty.DisplayFormat.UNIT,
                    false,
                    new GearProperty.Builder<>(0f, 0f, -100f, 100f)
                            .group(GearPropertyGroups.HARVEST)
                            .affectedByGrades(false)
                            .affectedBySynergy(false)
                            .onGetAttributes(GearHelper::onAddBlockReachModifier)
            )
    );
    public static final Supplier<NumberProperty> ATTACK_DAMAGE = REGISTRAR.register(
            "attack_damage",
            () -> new NumberProperty(
                    NumberProperty.Operation.AVERAGE,
                    NumberProperty.DisplayFormat.UNIT,
                    false,
                    new GearProperty.Builder<>(0f, 0f, 0f, (float) Integer.MAX_VALUE)
                            .group(GearPropertyGroups.ATTACK)
                            .affectedByGrades(true)
                            .affectedBySynergy(true)
                            .onGetAttributes(GearHelper::onAddAttackDamageModifier)
            )
    );
    public static final Supplier<NumberProperty> ATTACK_SPEED = REGISTRAR.register(
            "attack_speed",
            () -> new NumberProperty(
                    NumberProperty.Operation.AVERAGE,
                    NumberProperty.DisplayFormat.UNIT,
                    false,
                    new GearProperty.Builder<>(0f, 0f, -3.9f, 4.0f)
                            .group(GearPropertyGroups.ATTACK)
                            .affectedByGrades(false)
                            .affectedBySynergy(false)
                            .onGetAttributes(GearHelper::onAddAttackSpeedModifier)
            )
    );
    public static final Supplier<NumberProperty> ATTACK_REACH = REGISTRAR.register(
            "attack_reach",
            () -> new NumberProperty(
                    NumberProperty.Operation.AVERAGE,
                    NumberProperty.DisplayFormat.UNIT,
                    false,
                    new GearProperty.Builder<>(0f, 0f, -100f, 100f)
                            .group(GearPropertyGroups.ATTACK)
                            .affectedByGrades(false)
                            .affectedBySynergy(false)
            )
    );
    public static final Supplier<SwingAnimationProperty> SWING_ANIMATION = REGISTRAR.register(
            "swing_animation",
            () -> new SwingAnimationProperty(
                    new GearProperty.Builder<>(SwingAnimation.DEFAULT)
                            .group(GearPropertyGroups.ATTACK)
                            .affectedByGrades(false)
                            .affectedBySynergy(false)
                            .visible(false)
            )
    );
    public static final Supplier<KineticWeaponProperty> KINETIC_WEAPON = REGISTRAR.register(
            "kinetic_weapon",
            () -> new KineticWeaponProperty(
                    new GearProperty.Builder<>(KineticWeaponProperty.ZERO)
                            .group(GearPropertyGroups.ATTACK)
                            .affectedByGrades(false)
                            .affectedBySynergy(false)
                            .visible(false)
            )
    );
    public static final Supplier<NumberProperty> MAGIC_DAMAGE = REGISTRAR.register(
            "magic_damage",
            () -> new NumberProperty(
                    NumberProperty.Operation.AVERAGE,
                    NumberProperty.DisplayFormat.UNIT,
                    false,
                    new GearProperty.Builder<>(0f, 0f, 0f, (float) Integer.MAX_VALUE)
                            .group(GearPropertyGroups.ATTACK)
                            .affectedByGrades(true)
                            .affectedBySynergy(true)
            )
    );
    public static final Supplier<NumberProperty> RANGED_DAMAGE = REGISTRAR.register(
            "ranged_damage",
            () -> new NumberProperty(
                    NumberProperty.Operation.AVERAGE,
                    NumberProperty.DisplayFormat.MULTIPLIER,
                    false,
                    new GearProperty.Builder<>(0f, 0f, 0f, (float) Integer.MAX_VALUE)
                            .group(GearPropertyGroups.PROJECTILE)
                            .affectedByGrades(true)
                            .affectedBySynergy(true)
            )
    );
    public static final Supplier<NumberProperty> DRAW_SPEED = REGISTRAR.register(
            "draw_speed",
            () -> new NumberProperty(
                    NumberProperty.Operation.AVERAGE,
                    NumberProperty.DisplayFormat.MULTIPLIER,
                    false,
                    new GearProperty.Builder<>(0f, 0f, -10f, 10f)
                            .group(GearPropertyGroups.PROJECTILE)
                            .affectedByGrades(false)
                            .affectedBySynergy(false)
            )
    );
    public static final Supplier<NumberProperty> PROJECTILE_SPEED = REGISTRAR.register(
            "projectile_speed",
            () -> new NumberProperty(
                    NumberProperty.Operation.AVERAGE,
                    NumberProperty.DisplayFormat.MULTIPLIER,
                    false,
                    new GearProperty.Builder<>(1f, 0f, 0f, (float) Integer.MAX_VALUE)
                            .group(GearPropertyGroups.PROJECTILE)
                            .affectedByGrades(false)
                            .affectedBySynergy(true)
            )
    );
    public static final Supplier<NumberProperty> PROJECTILE_ACCURACY = REGISTRAR.register(
            "projectile_accuracy",
            () -> new NumberProperty(
                    NumberProperty.Operation.AVERAGE,
                    NumberProperty.DisplayFormat.PERCENTAGE,
                    false,
                    new GearProperty.Builder<>(1f, 0f, 0f, 10000f)
                            .group(GearPropertyGroups.PROJECTILE)
                            .affectedByGrades(false)
                            .affectedBySynergy(false)
            )
    );
    public static final Supplier<NumberProperty> ARMOR = REGISTRAR.register(
            "armor",
            () -> new NumberProperty(
                    NumberProperty.Operation.AVERAGE,
                    NumberProperty.DisplayFormat.UNIT,
                    false,
                    new GearProperty.Builder<>(0f, 0f, 0f, (float) Integer.MAX_VALUE)
                            .group(GearPropertyGroups.ARMOR)
                            .affectedByGrades(true)
                            .affectedBySynergy(true)
            )
    );
    public static final Supplier<NumberProperty> ARMOR_TOUGHNESS = REGISTRAR.register(
            "armor_toughness",
            () -> new NumberProperty(
                    NumberProperty.Operation.AVERAGE,
                    NumberProperty.DisplayFormat.UNIT,
                    false,
                    new GearProperty.Builder<>(0f, 0f, 0f, (float) Integer.MAX_VALUE)
                            .group(GearPropertyGroups.ARMOR)
                            .affectedByGrades(true)
                            .affectedBySynergy(true)
            )
    );
    public static final Supplier<NumberProperty> KNOCKBACK_RESISTANCE = REGISTRAR.register(
            "knockback_resistance",
            () -> new NumberProperty(
                    NumberProperty.Operation.AVERAGE,
                    NumberProperty.DisplayFormat.UNIT,
                    false,
                    new GearProperty.Builder<>(0f, 0f, 0f, (float) Integer.MAX_VALUE)
                            .group(GearPropertyGroups.ARMOR)
                            .affectedByGrades(true)
                            .affectedBySynergy(true)
            )
    );
    public static final Supplier<NumberProperty> MAGIC_ARMOR = REGISTRAR.register(
            "magic_armor",
            () -> new NumberProperty(
                    NumberProperty.Operation.AVERAGE,
                    NumberProperty.DisplayFormat.UNIT,
                    false,
                    new GearProperty.Builder<>(0f, 0f, 0f, (float) Integer.MAX_VALUE)
                            .group(GearPropertyGroups.ARMOR)
                            .affectedByGrades(true)
                            .affectedBySynergy(true)
            )
    );
}
