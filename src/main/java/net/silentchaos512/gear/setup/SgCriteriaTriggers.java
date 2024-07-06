package net.silentchaos512.gear.setup;

import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.advancements.criterion.GearRepairedTrigger;
import net.silentchaos512.gear.advancements.criterion.HasPartTrigger;
import net.silentchaos512.gear.advancements.criterion.GearPropertyTrigger;

import java.util.function.Supplier;

public class SgCriteriaTriggers {
    public static final DeferredRegister<CriterionTrigger<?>> TRIGGER_TYPES = DeferredRegister.create(BuiltInRegistries.TRIGGER_TYPES, SilentGear.MOD_ID);

    public static final DeferredHolder<CriterionTrigger<?>, PlayerTrigger> BRITTLE_DAMAGE = register("brittle_proc", PlayerTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, PlayerTrigger> CRAFTED_WITH_ROUGH_ROD = register("crafted_with_rough_rod", PlayerTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, PlayerTrigger> DAMAGE_FACTOR_CHANGE = register("damage_factor_change", PlayerTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, PlayerTrigger> FALL_WITH_MOONWALKER = register("fall_with_moonwalker", PlayerTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, GearRepairedTrigger> GEAR_REPAIRED = register("gear_repair", GearRepairedTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, HasPartTrigger> HAS_PART = register("has_part", HasPartTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, GearPropertyTrigger> ITEM_STAT = register("item_stat", GearPropertyTrigger::new);

    private static <T extends CriterionTrigger<?>> DeferredHolder<CriterionTrigger<?>, T> register(String name, Supplier<T> factory) {
        return TRIGGER_TYPES.register(name, factory);
    }
}
