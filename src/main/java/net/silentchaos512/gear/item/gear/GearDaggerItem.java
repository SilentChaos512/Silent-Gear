package net.silentchaos512.gear.item.gear;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.api.item.GearType;

import java.util.function.Supplier;

public class GearDaggerItem extends GearSwordItem {
    public GearDaggerItem(Supplier<GearType> gearType, Item.Properties properties) {
        super(gearType, properties);
    }

    @Override
    public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        target.invulnerableTime = (int) (0.67f * target.invulnerableTime); // Make target vulnerable sooner
    }
}
