package net.silentchaos512.gear.api.traits;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import net.silentchaos512.gear.api.property.GearProperty;
import net.silentchaos512.gear.api.property.GearPropertyValue;
import net.silentchaos512.gear.setup.SgRegistries;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

public abstract class TraitEffect {
    public static final Codec<TraitEffect> DISPATCH_CODEC = SgRegistries.TRAIT_EFFECT_TYPE.byNameCodec()
            .dispatch(
                    TraitEffect::type,
                    TraitEffectType::codec
            );
    public static final StreamCodec<RegistryFriendlyByteBuf, TraitEffect> STREAM_CODEC = StreamCodec.of(
            (buf, effect) -> {
                buf.writeResourceLocation(Objects.requireNonNull(SgRegistries.TRAIT_EFFECT_TYPE.getKey(effect.type())));
                effect.type().rawStreamCodec().encode(buf, effect);
            },
            buf -> {
                var type = SgRegistries.TRAIT_EFFECT_TYPE.get(buf.readResourceLocation());
                return Objects.requireNonNull(type).streamCodec().decode(buf);
            }
    );

    public abstract TraitEffectType<?> type();

    public abstract Collection<String> getExtraWikiLines();

    public double onCalculateSynergy(double synergy, int traitLevel) {
        return synergy;
    }

    public float onAttackEntity(TraitActionContext context, LivingEntity target, float baseValue) {
        return baseValue;
    }

    public int onDurabilityDamage(TraitActionContext context, int damageTaken) {
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

    public Collection<GearPropertyValue<?>> getBonusProperties(
            int traitLevel,
            @Nullable Player player,
            GearProperty<?, ?> property,
            GearPropertyValue<?> baseValue,
            float damageRatio
    ) {
        return Collections.emptyList();
    }

    public void onGetAttributeModifiers(TraitActionContext context, ItemAttributeModifiers.Builder builder) {
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
     * @param wielder    The entity using the item
     * @param traitLevel The level of this trait
     */
    public void onItemSwing(ItemStack stack, LivingEntity wielder, int traitLevel) {
        // Nothing
    }

    public float getMiningSpeedModifier(int traitLevel, BlockState state) {
        return 0f;
    }

    public void onUpdate(TraitActionContext context, boolean isEquipped) {
        // Nothing
    }

    public ItemStack addLootDrops(TraitActionContext context, ItemStack stack) {
        return ItemStack.EMPTY;
    }
}
