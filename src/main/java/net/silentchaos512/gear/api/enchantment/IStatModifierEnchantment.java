package net.silentchaos512.gear.api.enchantment;

import net.silentchaos512.gear.api.stats.ChargedProperties;
import net.silentchaos512.gear.api.stats.StatInstance;
import net.silentchaos512.gear.api.util.StatGearKey;

import javax.annotation.Nullable;

public interface IStatModifierEnchantment {
    @Nullable
    StatInstance modifyStat(StatGearKey stat, StatInstance mod, ChargedProperties charge);
}
