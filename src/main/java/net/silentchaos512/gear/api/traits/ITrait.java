package net.silentchaos512.gear.api.traits;

import com.google.common.collect.Multimap;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.client.KeyTracker;
import net.silentchaos512.gear.util.TextUtil;

import java.util.List;
import java.util.function.Function;

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

    IFormattableTextComponent getDisplayName(int level);

    IFormattableTextComponent getDescription(int level);

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
        addInformation(level, tooltip, flag, t -> t);
    }

    /**
     * Add tooltip information for this trait. Normally, this consists of just the trait's
     * translated name and level, but may include a description under certain conditions. If the
     * trait is hidden ({@link #isHidden()}), nothing is shown unless advanced tooltips are
     * enabled.
     *
     * @param level      The trait level
     * @param tooltip    The tooltip list
     * @param flag       The tooltip flag
     * @param affixFirst A function which can be used to make additional changes to the first line (display name)
     */
    default void addInformation(int level, List<ITextComponent> tooltip, ITooltipFlag flag, Function<ITextComponent, ITextComponent> affixFirst) {
        if (!showInTooltip(flag)) return;

        // Display name
        ITextComponent displayName = TextUtil.withColor(this.getDisplayName(level), isHidden() ? TextFormatting.DARK_GRAY : TextFormatting.GRAY);
        displayName.getStyle().setFormatting(TextFormatting.ITALIC);
        tooltip.add(affixFirst.apply(displayName));

        // Description (usually not shown)
        if (KeyTracker.isDisplayTraitsDown()) {
            ITextComponent description = TextUtil.withColor(this.getDescription(level), TextFormatting.DARK_GRAY);
            tooltip.add(new StringTextComponent("    ").func_230529_a_(description));
        }
    }

    ITraitSerializer<?> getSerializer();

    float onAttackEntity(TraitActionContext context, LivingEntity target, float baseValue);

    float onDurabilityDamage(TraitActionContext context, int damageTaken);

    void onGearCrafted(TraitActionContext context);

    float onGetStat(TraitActionContext context, ItemStat stat, float value, float damageRatio);

    void onGetAttributeModifiers(TraitActionContext context, Multimap<Attribute, AttributeModifier> modifiers, EquipmentSlotType slot);

    ActionResultType onItemUse(ItemUseContext context, int traitLevel);

    void onUpdate(TraitActionContext context, boolean isEquipped);

    default CompoundNBT write(int level) {
        CompoundNBT tag = new CompoundNBT();
        tag.putString("Name", this.getId().toString());
        tag.putByte("Level", (byte) level);
        return tag;
    }
}
