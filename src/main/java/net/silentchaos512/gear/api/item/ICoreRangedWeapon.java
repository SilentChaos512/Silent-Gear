package net.silentchaos512.gear.api.item;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.silentchaos512.gear.api.parts.PartType;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.api.stats.ItemStat;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Set;

public interface ICoreRangedWeapon extends ICoreTool {
    Set<ItemStat> RELEVANT_STATS = ImmutableSet.of(
            ItemStats.RANGED_DAMAGE,
            ItemStats.RANGED_SPEED,
            ItemStats.DURABILITY,
            ItemStats.ENCHANTABILITY,
            ItemStats.RARITY
    );

    @Override
    default Set<ItemStat> getRelevantStats(ItemStack stack) {
        return RELEVANT_STATS;
    }

    @Override
    default boolean supportsPartOfType(PartType type) {
        return requiresPartOfType(type) || type == PartType.GRIP || type == PartType.MISC_UPGRADE || type == PartType.TIP;
    }

    @Override
    default Collection<PartType> getRequiredParts() {
        return ImmutableList.of(PartType.MAIN, PartType.ROD, PartType.BOWSTRING);
    }

    @Override
    default int getAnimationFrames() {
        return 4;
    }

    @Override
    default int getAnimationFrame(ItemStack stack, @Nullable World world, @Nullable LivingEntity entity) {
        IItemPropertyGetter pullingProperty = stack.getItem().getPropertyGetter(new ResourceLocation("pulling"));
        if (pullingProperty != null) {
            float pulling = pullingProperty.call(stack, world, entity);
            if (pulling > 0) {
                IItemPropertyGetter pullProperty = stack.getItem().getPropertyGetter(new ResourceLocation("pull"));
                if (pullProperty != null) {
                    float pull = pullProperty.call(stack, world, entity);

                    if (pull > 0.9f)
                        return 3;
                    if (pull > 0.65f)
                        return 2;
                    return 1;
                }
            }
        }
        return 0;
    }

    default float getBaseDrawDelay(ItemStack stack) {
        return 20;
    }

    default float getDrawDelay(ItemStack stack) {
        float speed = getStat(stack, ItemStats.RANGED_SPEED);
        if (speed <= 0) speed = 1f;
        return getBaseDrawDelay(stack) / speed;
    }
}
