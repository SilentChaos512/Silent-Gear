package net.silentchaos512.gear.api.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface ICoreWeapon extends ICoreTool {
    @Override
    default int getDamageOnHitEntity(ItemStack gear, LivingEntity target, LivingEntity attacker) {
        return 1;
    }
}
