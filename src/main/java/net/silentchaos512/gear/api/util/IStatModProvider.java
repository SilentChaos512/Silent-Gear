package net.silentchaos512.gear.api.util;

import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.api.stats.StatInstance;

import java.util.Collection;

/**
 * Something that can provide stat modifiers, such as parts and materials
 *
 * @param <D> An object containing more data about this object, such as {@link
 *            net.silentchaos512.gear.api.part.IPartData} or {@link net.silentchaos512.gear.api.material.IMaterialInstance}
 */
public interface IStatModProvider<D> {
    Collection<StatInstance> getStatModifiers(D instance, PartType partType, StatGearKey key, ItemStack gear);

    default Collection<StatInstance> getStatModifiers(D instance, PartType partType, StatGearKey key) {
        return getStatModifiers(instance, partType, key, ItemStack.EMPTY);
    }

    default float getStat(D instance, PartType partType, StatGearKey key, ItemStack gear) {
        ItemStat stat = ItemStats.get(key.getStat());
        if (stat == null) return key.getStat().getDefaultValue();

        Collection<StatInstance> mods = getStatModifiers(instance, partType, key, gear);
        return stat.compute(mods);
    }

    default float getStatUnclamped(D instance, PartType partType, StatGearKey key, ItemStack gear) {
        ItemStat stat = ItemStats.get(key.getStat());
        if (stat == null) return key.getStat().getDefaultValue();

        Collection<StatInstance> mods = getStatModifiers(instance, partType, key, gear);
        return stat.compute(stat.getBaseValue(), false, key.getGearType(), mods);
    }
}
