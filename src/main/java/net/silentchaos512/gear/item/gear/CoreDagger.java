package net.silentchaos512.gear.item.gear;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.stats.CommonItemStats;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.StatInstance;

import java.util.Optional;

public class CoreDagger extends CoreSword {

    @Override
    public GearType getGearType() {
        return GearType.DAGGER;
    }

    @Override
    public Optional<StatInstance> getBaseStatModifier(ItemStat stat) {
        if (stat == CommonItemStats.MELEE_DAMAGE)
            return Optional.of(StatInstance.makeBaseMod(2));
        if (stat == CommonItemStats.ATTACK_SPEED)
            return Optional.of(StatInstance.makeBaseMod(-1.2f));
        if (stat == CommonItemStats.REACH_DISTANCE)
            return Optional.of(StatInstance.makeBaseMod(-1));
        if (stat == CommonItemStats.REPAIR_EFFICIENCY)
            return Optional.of(StatInstance.makeBaseMod(2));
        return Optional.empty();
    }

    @Override
    public Optional<StatInstance> getStatModifier(ItemStat stat) {
        if (stat == CommonItemStats.MELEE_DAMAGE)
            return Optional.of(StatInstance.makeGearMod(-0.5f));
        return Optional.empty();
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        target.hurtResistantTime *= 0.67f; // Make target vulnerable sooner
        return super.hitEntity(stack, target, attacker);
    }
}
