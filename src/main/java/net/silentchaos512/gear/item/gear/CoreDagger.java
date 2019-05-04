package net.silentchaos512.gear.item.gear;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.config.Config;
import net.silentchaos512.gear.config.ConfigOptionEquipment;

import javax.annotation.Nonnull;

public class CoreDagger extends CoreSword {
    @Nonnull
    @Override
    public ConfigOptionEquipment getConfig() {
        return Config.dagger;
    }

    @Override
    public String[] getAlternativeRecipe() {
        return new String[]{"#", "/"};
    }

    @Override
    public String getGearClass() {
        return "dagger";
    }

    @Override
    public GearType getGearType() {
        return GearType.DAGGER;
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        target.hurtResistantTime *= 0.67f; // Make target vulnerable sooner
        return super.hitEntity(stack, target, attacker);
    }
}
