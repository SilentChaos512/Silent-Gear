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
    public static final DeferredRegister<GearPropertyType<?, ?>> REGISTRAR = DeferredRegister.create(SgRegistries.GEAR_PROPERTIES, SilentGear.MOD_ID);

    public static final Supplier<TraitListPropertyType> TRAITS = REGISTRAR.register("traits", () ->
            new TraitListPropertyType(
                    new GearPropertyType.Builder<>(Collections.emptyList())
            )
    );
    public static final Supplier<NumberPropertyType> DURABILITY = REGISTRAR.register("durability", () ->
            new NumberPropertyType(
                    PropertyOp.AVERAGE,
                    NumberPropertyType.DisplayFormat.UNIT,
                    true,
                    new GearPropertyType.Builder<>(0f, 0f, 0f, (float) Integer.MAX_VALUE)
                            .category(GearPropertyCategories.GENERAL)
                            .affectedByGrades(true)
                            .affectedBySynergy(true)
            )
    );
    public static final Supplier<NumberPropertyType> ARMOR_DURABILITY = REGISTRAR.register("armor_durability", () ->
            new NumberPropertyType(
                    PropertyOp.AVERAGE,
                    NumberPropertyType.DisplayFormat.MULTIPLIER,
                    true,
                    new GearPropertyType.Builder<>(0f, 0f, 0f, (float) (Integer.MAX_VALUE / 16))
                            .category(GearPropertyCategories.GENERAL)
                            .affectedByGrades(true)
                            .affectedBySynergy(true)
            )
    );
    public static final Supplier<NumberPropertyType> REPAIR_EFFICIENCY = REGISTRAR.register("repair_efficiency", () ->
            new NumberPropertyType(
                    PropertyOp.AVERAGE,
                    NumberPropertyType.DisplayFormat.PERCENTAGE,
                    false,
                    new GearPropertyType.Builder<>(1f, 0f, 0f, 1000f)
                            .category(GearPropertyCategories.GENERAL)
                            .affectedByGrades(false)
                            .affectedBySynergy(false)
            )
    );
    public static final Supplier<NumberPropertyType> REPAIR_VALUE = REGISTRAR.register("repair_value", () ->
            new NumberPropertyType(
                    PropertyOp.AVERAGE,
                    NumberPropertyType.DisplayFormat.PERCENTAGE,
                    false,
                    new GearPropertyType.Builder<>(1f, 0f, 0f, 1000f)
                            .category(GearPropertyCategories.GENERAL)
                            .affectedByGrades(false)
                            .affectedBySynergy(false)
            )
    );
    public static final Supplier<NumberPropertyType> ENCHANTMENT_VALUE = REGISTRAR.register("enchantment_value", () ->
            new NumberPropertyType(
                    PropertyOp.AVERAGE,
                    NumberPropertyType.DisplayFormat.UNIT,
                    true,
                    new GearPropertyType.Builder<>(0f, 0f, 0f, (float) Integer.MAX_VALUE)
                            .category(GearPropertyCategories.GENERAL)
                            .affectedByGrades(true)
                            .affectedBySynergy(true)
            )
    );
    public static final Supplier<NumberPropertyType> CHARGING_VALUE = REGISTRAR.register("charging_value", () ->
            new NumberPropertyType(
                    PropertyOp.AVERAGE,
                    NumberPropertyType.DisplayFormat.UNIT,
                    true,
                    new GearPropertyType.Builder<>(0f, 0f, 0f, (float) Integer.MAX_VALUE)
                            .category(GearPropertyCategories.GENERAL)
                            .affectedByGrades(false)
                            .affectedBySynergy(false)
            )
    );
    public static final Supplier<NumberPropertyType> RARITY = REGISTRAR.register("rarity", () ->
            new NumberPropertyType(
                    PropertyOp.AVERAGE,
                    NumberPropertyType.DisplayFormat.UNIT,
                    true,
                    new GearPropertyType.Builder<>(0f, 0f, 0f, (float) Integer.MAX_VALUE)
                            .category(GearPropertyCategories.GENERAL)
                            .affectedByGrades(false)
                            .affectedBySynergy(false)
            )
    );
    public static final Supplier<TierPropertyType> HARVEST_TIER = REGISTRAR.register("harvest_tier", () ->
            new TierPropertyType(
                    new GearPropertyType.Builder<Tier>(Tiers.WOOD)
                            .category(GearPropertyCategories.HARVEST)
                            .affectedByGrades(false)
                            .affectedBySynergy(false)
            )
    );
    public static final Supplier<NumberPropertyType> HARVEST_SPEED = REGISTRAR.register("harvest_speed", () ->
            new NumberPropertyType(
                    PropertyOp.AVERAGE,
                    NumberPropertyType.DisplayFormat.UNIT,
                    false,
                    new GearPropertyType.Builder<>(0f, 0f, 0f, (float) Integer.MAX_VALUE)
                            .category(GearPropertyCategories.HARVEST)
                            .affectedByGrades(true)
                            .affectedBySynergy(true)
            )
    );
    public static final Supplier<NumberPropertyType> BLOCK_REACH = REGISTRAR.register("block_reach", () ->
            new NumberPropertyType(
                    PropertyOp.AVERAGE,
                    NumberPropertyType.DisplayFormat.UNIT,
                    false,
                    new GearPropertyType.Builder<>(0f, 0f, -100f, 100f)
                            .category(GearPropertyCategories.HARVEST)
                            .affectedByGrades(false)
                            .affectedBySynergy(false)
            )
    );
    public static final Supplier<NumberPropertyType> ATTACK_DAMAGE = REGISTRAR.register("attack_damage", () ->
            new NumberPropertyType(
                    PropertyOp.AVERAGE,
                    NumberPropertyType.DisplayFormat.UNIT,
                    false,
                    new GearPropertyType.Builder<>(0f, 0f, 0f, (float) Integer.MAX_VALUE)
                            .category(GearPropertyCategories.ATTACK)
                            .affectedByGrades(true)
                            .affectedBySynergy(true)
            )
    );
    public static final Supplier<NumberPropertyType> ATTACK_SPEED = REGISTRAR.register("attack_speed", () ->
            new NumberPropertyType(
                    PropertyOp.AVERAGE,
                    NumberPropertyType.DisplayFormat.UNIT,
                    false,
                    new GearPropertyType.Builder<>(0f, 0f, -3.9f, 4.0f)
                            .category(GearPropertyCategories.ATTACK)
                            .affectedByGrades(false)
                            .affectedBySynergy(false)
            )
    );
    public static final Supplier<NumberPropertyType> ATTACK_REACH = REGISTRAR.register("attack_reach", () ->
            new NumberPropertyType(
                    PropertyOp.AVERAGE,
                    NumberPropertyType.DisplayFormat.UNIT,
                    false,
                    new GearPropertyType.Builder<>(3f, 3f, 0f, 100f)
                            .category(GearPropertyCategories.ATTACK)
                            .affectedByGrades(false)
                            .affectedBySynergy(false)
            )
    );
    public static final Supplier<NumberPropertyType> MAGIC_DAMAGE = REGISTRAR.register("magic_damage", () ->
            new NumberPropertyType(
                    PropertyOp.AVERAGE,
                    NumberPropertyType.DisplayFormat.UNIT,
                    false,
                    new GearPropertyType.Builder<>(0f, 0f, 0f, (float) Integer.MAX_VALUE)
                            .category(GearPropertyCategories.ATTACK)
                            .affectedByGrades(true)
                            .affectedBySynergy(true)
            )
    );
    public static final Supplier<NumberPropertyType> RANGED_DAMAGE = REGISTRAR.register("ranged_damage", () ->
            new NumberPropertyType(
                    PropertyOp.AVERAGE,
                    NumberPropertyType.DisplayFormat.MULTIPLIER,
                    false,
                    new GearPropertyType.Builder<>(0f, 0f, 0f, (float) Integer.MAX_VALUE)
                            .category(GearPropertyCategories.PROJECTILE)
                            .affectedByGrades(true)
                            .affectedBySynergy(true)
            )
    );
    public static final Supplier<NumberPropertyType> DRAW_SPEED = REGISTRAR.register("draw_speed", () ->
            new NumberPropertyType(
                    PropertyOp.AVERAGE,
                    NumberPropertyType.DisplayFormat.MULTIPLIER,
                    false,
                    new GearPropertyType.Builder<>(0f, 0f, -10f, 10f)
                            .category(GearPropertyCategories.PROJECTILE)
                            .affectedByGrades(false)
                            .affectedBySynergy(false)
            )
    );
    public static final Supplier<NumberPropertyType> PROJECTILE_SPEED = REGISTRAR.register("projectile_speed", () ->
            new NumberPropertyType(
                    PropertyOp.AVERAGE,
                    NumberPropertyType.DisplayFormat.MULTIPLIER,
                    false,
                    new GearPropertyType.Builder<>(1f, 0f, 0f, (float) Integer.MAX_VALUE)
                            .category(GearPropertyCategories.PROJECTILE)
                            .affectedByGrades(false)
                            .affectedBySynergy(true)
            )
    );
    public static final Supplier<NumberPropertyType> PROJECTILE_ACCURACY = REGISTRAR.register("projectile_accuracy", () ->
            new NumberPropertyType(
                    PropertyOp.AVERAGE,
                    NumberPropertyType.DisplayFormat.PERCENTAGE,
                    false,
                    new GearPropertyType.Builder<>(1f, 0f, 0f, 10000f)
                            .category(GearPropertyCategories.PROJECTILE)
                            .affectedByGrades(false)
                            .affectedBySynergy(false)
            )
    );
    public static final Supplier<NumberPropertyType> ARMOR = REGISTRAR.register("armor", () ->
            new NumberPropertyType(
                    PropertyOp.AVERAGE,
                    NumberPropertyType.DisplayFormat.UNIT,
                    false,
                    new GearPropertyType.Builder<>(0f, 0f, 0f, (float) Integer.MAX_VALUE)
                            .category(GearPropertyCategories.ARMOR)
                            .affectedByGrades(true)
                            .affectedBySynergy(true)
            )
    );
    public static final Supplier<NumberPropertyType> ARMOR_TOUGHNESS = REGISTRAR.register("armor_toughness", () ->
            new NumberPropertyType(
                    PropertyOp.AVERAGE,
                    NumberPropertyType.DisplayFormat.UNIT,
                    false,
                    new GearPropertyType.Builder<>(0f, 0f, 0f, (float) Integer.MAX_VALUE)
                            .category(GearPropertyCategories.ARMOR)
                            .affectedByGrades(true)
                            .affectedBySynergy(true)
            )
    );
    public static final Supplier<NumberPropertyType> KNOCKBACK_RESISTANCE = REGISTRAR.register("knockback_resistance", () ->
            new NumberPropertyType(
                    PropertyOp.AVERAGE,
                    NumberPropertyType.DisplayFormat.UNIT,
                    false,
                    new GearPropertyType.Builder<>(0f, 0f, 0f, (float) Integer.MAX_VALUE)
                            .category(GearPropertyCategories.ARMOR)
                            .affectedByGrades(true)
                            .affectedBySynergy(true)
            )
    );
    public static final Supplier<NumberPropertyType> MAGIC_ARMOR = REGISTRAR.register("magic_armor", () ->
            new NumberPropertyType(
                    PropertyOp.AVERAGE,
                    NumberPropertyType.DisplayFormat.UNIT,
                    false,
                    new GearPropertyType.Builder<>(0f, 0f, 0f, (float) Integer.MAX_VALUE)
                            .category(GearPropertyCategories.ARMOR)
                            .affectedByGrades(true)
                            .affectedBySynergy(true)
            )
    );
}
