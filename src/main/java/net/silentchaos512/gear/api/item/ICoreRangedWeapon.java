package net.silentchaos512.gear.api.item;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.client.util.ModelPropertiesHelper;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Set;

public interface ICoreRangedWeapon extends ICoreTool {
    Set<ItemStat> RELEVANT_STATS = ImmutableSet.of(
            ItemStats.DURABILITY,
            ItemStats.REPAIR_EFFICIENCY,
            ItemStats.ENCHANTABILITY,
            ItemStats.RANGED_DAMAGE,
            ItemStats.RANGED_SPEED
    );

    @Override
    default Set<ItemStat> getRelevantStats(ItemStack stack) {
        return RELEVANT_STATS;
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
    default int getAnimationFrame(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity) {
        IItemPropertyGetter pullingProperty = ModelPropertiesHelper.get(stack, new ResourceLocation("pulling"));
        if (pullingProperty != null) {
            float pulling = pullingProperty.call(stack, world, entity);
            if (pulling > 0) {
                IItemPropertyGetter pullProperty = ModelPropertiesHelper.get(stack, new ResourceLocation("pull"));
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

    @Override
    default boolean hasTexturesFor(PartType partType) {
        return partType != PartType.FLETCHING;
    }
}
