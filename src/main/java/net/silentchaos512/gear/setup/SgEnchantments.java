package net.silentchaos512.gear.setup;

import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.silentchaos512.gear.SilentGear;

import java.util.function.Supplier;

public final class SgEnchantments {
    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, SilentGear.MOD_ID);

    private SgEnchantments() {}

    private static <T extends Enchantment> RegistryObject<T> register(String name, Supplier<T> enchantment) {
        return ENCHANTMENTS.register(name, enchantment);
    }
}
