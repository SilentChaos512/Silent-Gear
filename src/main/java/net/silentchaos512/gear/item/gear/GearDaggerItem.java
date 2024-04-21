package net.silentchaos512.gear.item.gear;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.api.item.GearType;

public class GearDaggerItem extends GearSwordItem {
    public GearDaggerItem(GearType gearType) {
        super(gearType);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        target.invulnerableTime = (int) (0.67f * target.invulnerableTime); // Make target vulnerable sooner
        return super.hurtEnemy(stack, target, attacker);
    }
}
