package net.silentchaos512.gear.init;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraftforge.fml.RegistryObject;
import net.silentchaos512.gear.enchantment.StatModifierEnchantment;

import java.util.function.Supplier;

public final class GearEnchantments {
    public static final RegistryObject<Enchantment> STAR_CHARGED = register("star_charged", () ->
            new StatModifierEnchantment(Enchantment.Rarity.VERY_RARE, EnchantmentType.VANISHABLE, new EquipmentSlotType[0]));

    private GearEnchantments() {}

    public static void register() {}

    private static <T extends Enchantment> RegistryObject<T> register(String name, Supplier<T> enchantment) {
        return Registration.ENCHANTMENTS.register(name, enchantment);
    }
}
