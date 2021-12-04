package net.silentchaos512.gear.init;

import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public final class GearEnchantments {
    private GearEnchantments() {}

    public static void register() {}

    private static <T extends Enchantment> RegistryObject<T> register(String name, Supplier<T> enchantment) {
        return Registration.ENCHANTMENTS.register(name, enchantment);
    }
}
