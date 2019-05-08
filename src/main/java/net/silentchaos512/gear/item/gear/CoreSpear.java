package net.silentchaos512.gear.item.gear;

import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.stats.CommonItemStats;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.StatInstance;

import java.util.Optional;

public class CoreSpear extends CoreSword {
    @Override
    public GearType getGearType() {
        return GearType.SPEAR;
    }

    @Override
    public Optional<StatInstance> getBaseStatModifier(ItemStat stat) {
        if (stat == CommonItemStats.MELEE_DAMAGE)
            return Optional.of(StatInstance.makeBaseMod(2));
        if (stat == CommonItemStats.ATTACK_SPEED)
            return Optional.of(StatInstance.makeBaseMod(-2.7f));
        if (stat == CommonItemStats.REACH_DISTANCE)
            return Optional.of(StatInstance.makeBaseMod(1));
        if (stat == CommonItemStats.REPAIR_EFFICIENCY)
            return Optional.of(StatInstance.makeBaseMod(1.3f));
        return Optional.empty();
    }

    @Override
    public Optional<StatInstance> getStatModifier(ItemStat stat) {
        if (stat == CommonItemStats.DURABILITY)
            return Optional.of(StatInstance.makeGearMod(-0.25f));
        return Optional.empty();
    }
}
