package net.silentchaos512.gear.setup.gear;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.traits.TraitEffect;
import net.silentchaos512.gear.api.traits.TraitEffectType;
import net.silentchaos512.gear.gear.trait.effect.*;
import net.silentchaos512.gear.setup.SgRegistries;

import java.util.function.Supplier;

public class TraitEffectTypes {
    public static final DeferredRegister<TraitEffectType<?>> REGISTRAR = DeferredRegister.create(SgRegistries.TRAIT_EFFECT_TYPE, SilentGear.MOD_ID);

    public static final Supplier<TraitEffectType<AttachDataComponentsTraitEffect>> ATTACH_DATA_COMPONENTS = register(
            "attach_data_components",
            AttachDataComponentsTraitEffect.CODEC,
            AttachDataComponentsTraitEffect.STREAM_CODEC,
            "Applies a custom data component patch to gear items"
    );
    public static final Supplier<TraitEffectType<AttributeTraitEffect>> ATTRIBUTE = register(
            "attribute",
            AttributeTraitEffect.CODEC,
            AttributeTraitEffect.STREAM_CODEC,
            "Gives the gear item additional attribute modifiers"
    );
    public static final Supplier<TraitEffectType<BlockFillerTraitEffect>> BLOCK_FILLER = register(
            "block_filler",
            BlockFillerTraitEffect.CODEC,
            BlockFillerTraitEffect.STREAM_CODEC,
            "Allows tools to replace specific blocks with another block"
    );
    public static final Supplier<TraitEffectType<BlockMiningSpeedTraitEffect>> BLOCK_MINING_SPEED = register(
            "block_mining_speed",
            BlockMiningSpeedTraitEffect.CODEC,
            BlockMiningSpeedTraitEffect.STREAM_CODEC,
            "Changes mining speed on blocks in a given tag"
    );
    public static final Supplier<TraitEffectType<BlockPlacerTraitEffect>> BLOCK_PLACER = register(
            "block_placer",
            BlockPlacerTraitEffect.CODEC,
            BlockPlacerTraitEffect.STREAM_CODEC,
            "Allows tools to create and place a specific block with a durability cost"
    );
    public static final Supplier<TraitEffectType<BonusDropsTraitEffect>> BONUS_DROPS = register(
            "bonus_drops",
            BonusDropsTraitEffect.CODEC,
            BonusDropsTraitEffect.STREAM_CODEC,
            "Multiplies drops for given items"
    );
    public static final Supplier<TraitEffectType<CancelEffectsTraitEffect>> CANCEL_EFFECTS = register(
            "cancel_effects",
            CancelEffectsTraitEffect.CODEC,
            CancelEffectsTraitEffect.STREAM_CODEC,
            "Cancels certain effects applied to the player"
    );
    public static final Supplier<TraitEffectType<DurabilityTraitEffect>> DURABILITY = register(
            "durability",
            DurabilityTraitEffect.CODEC,
            DurabilityTraitEffect.STREAM_CODEC,
            "Adds a chance of either canceling durability loss or losing extra durability"
    );
    public static final Supplier<TraitEffectType<ExtraDamageTraitEffect>> EXTRA_DAMAGE = register(
            "extra_damage",
            ExtraDamageTraitEffect.CODEC,
            ExtraDamageTraitEffect.STREAM_CODEC,
            "Deals extra damage when attacking certain types of mobs"
    );
    public static final Supplier<TraitEffectType<FireproofTraitEffect>> FIREPROOF = register(
            "fireproof",
            FireproofTraitEffect.CODEC,
            FireproofTraitEffect.STREAM_CODEC,
            "Prevents items from being destroyed when dropped in fire or lava"
    );
    public static final Supplier<TraitEffectType<ItemMagnetTraitEffect>> ITEM_MAGNET = register(
            "item_magnet",
            ItemMagnetTraitEffect.CODEC,
            ItemMagnetTraitEffect.STREAM_CODEC,
            "Attracts certain items to the player"
    );
    public static final Supplier<TraitEffectType<NegateDamageTraitEffect>> NEGATE_DAMAGE = register(
            "negate_damage",
            NegateDamageTraitEffect.CODEC,
            NegateDamageTraitEffect.STREAM_CODEC,
            "Armor reduces damage taken from certain sources"
    );
    public static final Supplier<TraitEffectType<NumberPropertyModifierTraitEffect>> NUMBER_PROPERTY_MODIFIER = register(
            "number_property_modifier",
            NumberPropertyModifierTraitEffect.CODEC,
            NumberPropertyModifierTraitEffect.STREAM_CODEC,
            "Applies modifiers to numerical properties based as durability lost"
    );
    public static final Supplier<TraitEffectType<SelfRepairTraitEffect>> SELF_REPAIR = register(
            "self_repair",
            SelfRepairTraitEffect.CODEC,
            SelfRepairTraitEffect.STREAM_CODEC,
            "Items randomly repair themselves over time"
    );
    public static final Supplier<TraitEffectType<SynergyTraitEffect>> SYNERGY_MULTIPLIER = register(
            "synergy_multiplier",
            SynergyTraitEffect.CODEC,
            SynergyTraitEffect.STREAM_CODEC,
            "Increases synergy on crafted parts"
    );
    public static final Supplier<TraitEffectType<TargetEffectTraitEffect>> TARGET_EFFECT = register(
            "target_effect",
            TargetEffectTraitEffect.CODEC,
            TargetEffectTraitEffect.STREAM_CODEC,
            "Applies potion effects to an attacked entity"
    );
    public static final Supplier<TraitEffectType<WielderEffectTraitEffect>> WIELDER_EFFECT = register(
            "wielder_effect",
            WielderEffectTraitEffect.CODEC,
            WielderEffectTraitEffect.STREAM_CODEC,
            "Applies potion effects to the entity equipped with the item"
    );

    private static <T extends TraitEffect> Supplier<TraitEffectType<T>> register(String name, MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec, String wikiDescription) {
        return REGISTRAR.register(name, () -> new TraitEffectType<>(codec, streamCodec, wikiDescription));
    }
}
