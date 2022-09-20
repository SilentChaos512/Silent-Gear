package net.silentchaos512.gear.api.item;

import com.google.common.collect.ImmutableSet;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.api.stats.ItemStat;

import java.util.Set;

public interface ICoreWeapon extends ICoreTool {
    Set<ItemStat> RELEVANT_STATS = ImmutableSet.of(
            ItemStats.DURABILITY,
            ItemStats.REPAIR_EFFICIENCY,
            ItemStats.ENCHANTMENT_VALUE,
            ItemStats.MELEE_DAMAGE,
            ItemStats.ATTACK_SPEED,
            ItemStats.ATTACK_REACH
    );

    @Override
    default Set<ItemStat> getRelevantStats(ItemStack stack) {
        return RELEVANT_STATS;
    }

    @Override
    default int getDamageOnHitEntity(ItemStack gear, LivingEntity target, LivingEntity attacker) {
        return 1;
    }
}
