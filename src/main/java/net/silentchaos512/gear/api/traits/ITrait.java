package net.silentchaos512.gear.api.traits;

import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.client.KeyTracker;
import net.silentchaos512.gear.config.Config;
import net.silentchaos512.gear.util.TextUtil;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public interface ITrait {
    ResourceLocation getId();

    int getMaxLevel();

    Collection<ITraitCondition> getConditions();

    boolean willCancelWith(ITrait other);

    default int getCanceledLevel(int level, ITrait other, int otherLevel) {
        if (willCancelWith(other)) {
            final int diff = level - otherLevel;
            return diff < 0
                    ? Mth.clamp(diff, -other.getMaxLevel(), 0)
                    : Mth.clamp(diff, 0, this.getMaxLevel());
        }
        return level;
    }

    /**
     * Used to retain data on integrated server which is not sent on connect.
     *
     * @param oldTrait The old trait instance
     */
    default void retainData(@Nullable ITrait oldTrait) {}

    MutableComponent getDisplayName(int level);

    MutableComponent getDescription(int level);

    default boolean isHidden() {
        return false;
    }

    default boolean showInTooltip(TooltipFlag flag) {
        return !isHidden() || flag.isAdvanced();
    }

    @Deprecated
    default void addInformation(int level, List<Component> tooltip) {
        addInformation(level, tooltip, TooltipFlag.NORMAL);
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
    default void addInformation(int level, List<Component> tooltip, TooltipFlag flag) {
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
     * @param affixFirst A function which can be used to make additional changes to the first line
     *                   (display name)
     */
    default void addInformation(int level, List<Component> tooltip, TooltipFlag flag, Function<Component, Component> affixFirst) {
        if (!showInTooltip(flag)) return;

        // Display name
        Component displayName = TextUtil.withColor(this.getDisplayName(level), isHidden() ? ChatFormatting.DARK_GRAY : ChatFormatting.GRAY);
        displayName.getStyle().withColor(ChatFormatting.ITALIC);
        tooltip.add(affixFirst.apply(displayName));

        // Description (usually not shown)
        if (KeyTracker.isDisplayTraitsDown() && !Config.Client.vanillaStyleTooltips.get()) {
            Component description = TextUtil.withColor(this.getDescription(level), ChatFormatting.DARK_GRAY);
            tooltip.add(Component.literal("    ").append(description));
        }
    }

    ITraitSerializer<?> getSerializer();

    float onAttackEntity(TraitActionContext context, LivingEntity target, float baseValue);

    float onDurabilityDamage(TraitActionContext context, int damageTaken);

    void onGearCrafted(TraitActionContext context);

    void onRecalculatePre(TraitActionContext context);

    void onRecalculatePost(TraitActionContext context);

    float onGetStat(TraitActionContext context, ItemStat stat, float value, float damageRatio);

    void onGetAttributeModifiers(TraitActionContext context, Multimap<Attribute, AttributeModifier> modifiers, String slot);

    @Deprecated
    default void onGetAttributeModifiers(TraitActionContext context, Multimap<Attribute, AttributeModifier> modifiers, EquipmentSlot slot) {
        onGetAttributeModifiers(context, modifiers, slot.getName());
    }

    InteractionResult onItemUse(UseOnContext context, int traitLevel);

    /**
     * Called when the player left-clicks with the item in their hand without targeting a block or
     * an entity.
     *
     * @param stack      The gear item
     * @param wielder     The entity using the item
     * @param traitLevel The level of this trait
     */
    void onItemSwing(ItemStack stack, LivingEntity wielder, int traitLevel);

    void onUpdate(TraitActionContext context, boolean isEquipped);

    ItemStack addLootDrops(TraitActionContext context, ItemStack stack);

    default CompoundTag write(int level) {
        CompoundTag tag = new CompoundTag();
        tag.putString("Name", this.getId().toString());
        tag.putByte("Level", (byte) level);
        return tag;
    }

    /**
     * Gets the IDs of the traits that this trait will cancel with. This should only be used to
     * provide information to players. It should <em>not</em> be used to actually check if traits
     * will cancel.
     *
     * @return A collection of trait IDs
     */
    default Collection<String> getCancelsWithSet() {
        return Collections.emptySet();
    }

    /**
     * Used by the trait command's markdown dump subcommand (TraitsCommand#runDumpMd). Note that the
     * trait is responsible for adding indentation, bullets, and newlines if desired. A two-space
     * indentation is assumed (eg "  - My info...")
     *
     * @return Lines to add to the extra info of the wiki page dump
     */
    default Collection<String> getExtraWikiLines() {
        return Collections.emptyList();
    }
}
