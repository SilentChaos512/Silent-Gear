package net.silentchaos512.gear.api.traits;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.client.KeyTracker;

import java.util.List;

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

    default boolean isHidden() {
        return false;
    }

    default boolean showInTooltip(ITooltipFlag flag) {
        return !isHidden() || flag.isAdvanced();
    }

    @Deprecated
    default void addInformation(int level, List<ITextComponent> tooltip) {
        addInformation(level, tooltip, () -> false);
    }

    /**
     * Add tooltip information for this trait. Normally, this consists of just the trait's
     * translated name and level, but may include a description under certain conditions. If the
     * trait is hidden ({@link #isHidden()}), nothing is shown unless advanced tooltips are
     * enabled.
     *
     * @param level   The trait level
     * @param tooltip The tooltip list
     * @param flag    The tooltip flag
     */
    default void addInformation(int level, List<ITextComponent> tooltip, ITooltipFlag flag) {
        if (!showInTooltip(flag)) return;

        // Display name
        ITextComponent displayName = this.getDisplayName(level).applyTextStyle(TextFormatting.ITALIC);
        if (isHidden()) displayName.applyTextStyle(TextFormatting.DARK_GRAY);
        tooltip.add(displayName);

        // Description (usually not shown)
        if (KeyTracker.isAltDown()) {
            ITextComponent description = this.getDescription(level).applyTextStyle(TextFormatting.DARK_GRAY);
            tooltip.add(new StringTextComponent("  ").appendSibling(description));
        }
    }

    ITraitSerializer<?> getSerializer();

    float onAttackEntity(TraitActionContext context, LivingEntity target, float baseValue);

    float onDurabilityDamage(TraitActionContext context, int damageTaken);

    void onGearCrafted(TraitActionContext context);

    float onGetStat(TraitActionContext context, ItemStat stat, float value, float damageRatio);

    void onUpdate(TraitActionContext context, boolean isEquipped);

    default CompoundNBT write(int level) {
        CompoundNBT tag = new CompoundNBT();
        tag.putString("Name", this.getId().toString());
        tag.putByte("Level", (byte) level);
        return tag;
    }
}
