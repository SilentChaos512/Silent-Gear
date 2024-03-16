package net.silentchaos512.gear.api.item;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.client.util.ColorUtils;
import net.silentchaos512.gear.client.util.ModelPropertiesHelper;
import net.silentchaos512.utils.Color;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Set;

public interface ICoreRangedWeapon extends ICoreTool {
    Set<ItemStat> RELEVANT_STATS = ImmutableSet.of(
            ItemStats.DURABILITY,
            ItemStats.REPAIR_EFFICIENCY,
            ItemStats.ENCHANTMENT_VALUE,
            ItemStats.RANGED_DAMAGE,
            ItemStats.RANGED_SPEED
    );

    Set<ItemStat> EXCLUDED_STATS = ImmutableSet.of(
            ItemStats.ARMOR_DURABILITY,
            ItemStats.REPAIR_VALUE,
            ItemStats.HARVEST_SPEED,
            ItemStats.REACH_DISTANCE,
            ItemStats.MELEE_DAMAGE,
            ItemStats.ARMOR,
            ItemStats.ARMOR_TOUGHNESS,
            ItemStats.MAGIC_ARMOR,
            ItemStats.KNOCKBACK_RESISTANCE
    );

    @Override
    default Set<ItemStat> getRelevantStats(ItemStack stack) {
        return RELEVANT_STATS;
    }

    @Override
    default Set<ItemStat> getExcludedStats(ItemStack stack) {
        return EXCLUDED_STATS;
    }

    @Override
    default Collection<PartType> getRequiredParts() {
        return ImmutableList.of(PartType.MAIN, PartType.ROD, PartType.CORD);
    }

    @Override
    default int getAnimationFrames() {
        return 4;
    }

    @Override
    default int getAnimationFrame(ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity) {
        ItemPropertyFunction pullingProperty = ModelPropertiesHelper.get(stack, new ResourceLocation("pulling"));
        if (pullingProperty != null) {
            float pulling = pullingProperty.call(stack, world, entity, 0);
            if (pulling > 0) {
                ItemPropertyFunction pullProperty = ModelPropertiesHelper.get(stack, new ResourceLocation("pull"));
                if (pullProperty != null) {
                    float pull = pullProperty.call(stack, world, entity, 0);

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
        return partType != PartType.FLETCHING
                && partType != PartType.ADORNMENT
                && partType != PartType.LINING;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    default ItemColor getItemColors() {
//        return (stack, tintIndex) -> Color.VALUE_WHITE;
        //noinspection OverlyLongLambda
        return (stack, tintIndex) -> {
            return switch (tintIndex) {
                case 0 -> ColorUtils.getBlendedColor(stack, PartType.ROD);
                case 1 -> ColorUtils.getBlendedColor(stack, PartType.MAIN);
                case 3 -> ColorUtils.getBlendedColor(stack, PartType.CORD);
                default -> Color.VALUE_WHITE;
            };
        };
    }
}
