package net.silentchaos512.gear.init;

import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraftforge.fml.RegistryObject;
import net.silentchaos512.gear.enchantment.StatModifierEnchantment;

import java.util.function.Supplier;

public final class GearEnchantments {
    public static final RegistryObject<Enchantment> STAR_CHARGED = register("star_charged", () ->
            new StatModifierEnchantment(Enchantment.Rarity.VERY_RARE, EnchantmentCategory.VANISHABLE, new EquipmentSlot[0]));

    private GearEnchantments() {}

    public static void register() {}

    private static <T extends Enchantment> RegistryObject<T> register(String name, Supplier<T> enchantment) {
        return Registration.ENCHANTMENTS.register(name, enchantment);
    }
}
