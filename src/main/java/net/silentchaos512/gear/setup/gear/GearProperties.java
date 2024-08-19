package net.silentchaos512.gear.setup.gear;

import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.property.*;
import net.silentchaos512.gear.setup.SgRegistries;

import java.util.Collections;
import java.util.function.Supplier;

public class GearProperties {
    public static final DeferredRegister<GearProperty<?, ?>> REGISTRAR = DeferredRegister.create(SgRegistries.GEAR_PROPERTY, SilentGear.MOD_ID);

    public static final Supplier<TraitListProperty> TRAITS = REGISTRAR.register(
            "traits",
            () -> new TraitListProperty(
                    new GearProperty.Builder<>(Collections.emptyList())
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
            )
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
            )
    );
    public static final Supplier<NumberProperty> CHARGING_VALUE = REGISTRAR.register(
            "charging_value",
            () -> new NumberProperty(
                    NumberProperty.Operation.AVERAGE,
                    NumberProperty.DisplayFormat.UNIT,
                    true,
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
            )
    );
    public static final Supplier<TierProperty> HARVEST_TIER = REGISTRAR.register(
            "harvest_tier",
            () -> new TierProperty(
                    new GearProperty.Builder<Tier>(Tiers.WOOD)
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
            )
    );
    public static final Supplier<NumberProperty> ATTACK_REACH = REGISTRAR.register(
            "attack_reach",
            () -> new NumberProperty(
                    NumberProperty.Operation.AVERAGE,
                    NumberProperty.DisplayFormat.UNIT,
                    false,
                    new GearProperty.Builder<>(3f, 3f, 0f, 100f)
                            .group(GearPropertyGroups.ATTACK)
                            .affectedByGrades(false)
                            .affectedBySynergy(false)
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
