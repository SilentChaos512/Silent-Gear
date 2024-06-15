package net.silentchaos512.gear.setup;

import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.silentchaos512.gear.SilentGear;

import java.util.function.Supplier;

public class SgCriteriaTriggers {
    public static final DeferredRegister<CriterionTrigger<?>> TRIGGER_TYPES = DeferredRegister.create(BuiltInRegistries.TRIGGER_TYPES, SilentGear.MOD_ID);

    public static final DeferredHolder<CriterionTrigger<?>, PlayerTrigger> DAMAGE_FACTOR_CHANGE = register("damage_factor_change", PlayerTrigger::new);

    private static <T extends CriterionTrigger<?>> DeferredHolder<CriterionTrigger<?>, T> register(String name, Supplier<T> factory) {
        return TRIGGER_TYPES.register(name, factory);
    }
}
