package net.silentchaos512.gear.api.traits;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.silentchaos512.gear.api.stats.ItemStat;

public interface ITrait {
    ResourceLocation getId();

    int getMaxLevel();

    boolean willCancelWith(ITrait other);

    default int getCanceledLevel(int level, ITrait other, int otherLevel) {
        if (willCancelWith(other)) {
            final int diff = level - otherLevel;
            return diff < 0
                    ? MathHelper.clamp(diff, -other.getMaxLevel(), 0)
                    : MathHelper.clamp(diff, 0, this.getMaxLevel());
        }
        return level;
    }

    ITextComponent getDisplayName(int level);

    ITextComponent getDescription(int level);

    ITraitSerializer<?> getSerializer();

    float onAttackEntity(TraitActionContext context, LivingEntity target, float baseValue);

    float onDurabilityDamage(TraitActionContext context, int damageTaken);

    void onGearCrafted(TraitActionContext context);

    float onGetStat(TraitActionContext context, ItemStat stat, float value, float damageRatio);

    void onUpdate(TraitActionContext context, boolean isEquipped);
}
