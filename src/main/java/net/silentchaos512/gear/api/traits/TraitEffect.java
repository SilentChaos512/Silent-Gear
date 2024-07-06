package net.silentchaos512.gear.api.traits;

import com.google.common.collect.Multimap;
import com.mojang.serialization.Codec;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.silentchaos512.gear.api.stats.ItemStat;

import java.util.Collection;

public abstract class TraitEffect {
    public abstract Codec<? extends TraitEffect> codec();

    public abstract Collection<String> getExtraWikiLines();

    public float onAttackEntity(TraitActionContext context, LivingEntity target, float baseValue) {
        return baseValue;
    }

    public float onDurabilityDamage(TraitActionContext context, int damageTaken) {
        return damageTaken;
    }

    public void onGearCrafted(TraitActionContext context) {
        // Nothing
    }

    public void onRecalculatePre(TraitActionContext context) {
        // Nothing
    }

    public void onRecalculatePost(TraitActionContext context) {
        // Nothing
    }

    public float onGetStat(TraitActionContext context, ItemStat stat, float value, float damageRatio) {
        return value;
    }

    public void onGetAttributeModifiers(TraitActionContext context, Multimap<Attribute, AttributeModifier> modifiers, String slot) {
        // Nothing
    }

    public InteractionResult onItemUse(UseOnContext context, int traitLevel) {
        return InteractionResult.PASS;
    }

    /**
     * Called when the player left-clicks with the item in their hand without targeting a block or
     * an entity.
     *
     * @param stack      The gear item
     * @param wielder     The entity using the item
     * @param traitLevel The level of this trait
     */
    public void onItemSwing(ItemStack stack, LivingEntity wielder, int traitLevel) {
        // Nothing
    }

    public void onUpdate(TraitActionContext context, boolean isEquipped) {
        // Nothing
    }

    public ItemStack addLootDrops(TraitActionContext context, ItemStack stack) {
        return ItemStack.EMPTY;
    }
}
