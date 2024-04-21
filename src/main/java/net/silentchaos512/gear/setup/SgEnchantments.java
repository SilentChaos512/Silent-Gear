package net.silentchaos512.gear.setup;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.silentchaos512.gear.SilentGear;

import java.util.function.Supplier;

public final class SgEnchantments {
    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(BuiltInRegistries.ENCHANTMENT, SilentGear.MOD_ID);

    private SgEnchantments() {}

    private static <T extends Enchantment> DeferredHolder<Enchantment, T> register(String name, Supplier<T> enchantment) {
        return ENCHANTMENTS.register(name, enchantment);
    }
}
