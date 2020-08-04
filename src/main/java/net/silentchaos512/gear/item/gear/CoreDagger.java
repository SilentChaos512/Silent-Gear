package net.silentchaos512.gear.item.gear;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.silentchaos512.gear.api.item.GearType;

public class CoreDagger extends CoreSword {
    public CoreDagger(GearType gearType) {
        super(gearType);
    }

    @Override
    public boolean hitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        target.hurtResistantTime *= 0.67f; // Make target vulnerable sooner
        return super.hitEntity(stack, target, attacker);
    }
}
