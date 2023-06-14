package net.silentchaos512.gear.data.trait;

import net.minecraft.data.DataGenerator;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.Tags;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.data.trait.*;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.gear.trait.StellarTrait;
import net.silentchaos512.gear.init.SgBlocks;
import net.silentchaos512.gear.init.SgTags;
import net.silentchaos512.gear.util.Const;

import java.util.ArrayList;
import java.util.Collection;

@SuppressWarnings({"WeakerAccess", "SameParameterValue"})
public class TraitsProvider extends TraitsProviderBase {
    public TraitsProvider(DataGenerator generator) {
        super(generator, SilentGear.MOD_ID);
    }

    @Override
    @SuppressWarnings({"OverlyLongMethod", "MethodMayBeStatic"})
    public Collection<TraitBuilder> getTraits() {
        Collection<TraitBuilder> ret = new ArrayList<>();

        // Simple

        ret.add(TraitBuilder.simple(Const.Traits.ANCIENT, 5)
                .withGearTypeCondition(GearType.TOOL));
        ret.add(TraitBuilder.simple(Const.Traits.BOUNCE, 1)
                .withGearTypeCondition(GearType.ARMOR));
        ret.add(TraitBuilder.simple(Const.Traits.BRILLIANT, 1));
        ret.add(TraitBuilder.simple(Const.Traits.CONFETTI, 5)
                .withGearTypeCondition(GearType.WEAPON));
        ret.add(TraitBuilder.simple(Const.Traits.FIREPROOF, 1)
                .extraWikiLines("  - The item cannot be destroyed by fire or lava"));
        ret.add(TraitBuilder.simple(Const.Traits.FLAMMABLE, 1));
        ret.add(TraitBuilder.simple(Const.Traits.INDESTRUCTIBLE, 1)
                .extraWikiLines("  - The damage (durability lost) of the item will remain the same as when the trait was added",
                        "  - The item can still be repaired if desired"));
        ret.add(TraitBuilder.simple(Const.Traits.JABBERWOCKY, 1)
                .withGearTypeCondition(GearType.HARVEST_TOOL)
                .extraWikiLines("Something may happen if you mine certain blocks with this"));
        ret.add(TraitBuilder.simple(Const.Traits.LUSTROUS, 5)
                .withGearTypeCondition(GearType.HARVEST_TOOL));
        ret.add(TraitBuilder.simple(Const.Traits.MAGMATIC, 1)
                .withGearTypeCondition(GearType.HARVEST_TOOL)
                .extraWikiLines("Smelted drops are not affected by fortune to prevent item duplication"));
        ret.add(TraitBuilder.simple(Const.Traits.MAGNETIC, 5)
                .extraWikiLines("Higher levels increase range"));
        ret.add(TraitBuilder.simple(Const.Traits.MULTI_BREAK, 5)
                .extraWikiLines("  - This trait has never been coded ~~and has almost achieved meme status~~",
                        "  - Intended effect: mine multiple blocks like vein miner"));
        ret.add(TraitBuilder.simple(Const.Traits.RED_CARD, 1));
        ret.add(TraitBuilder.simple(Const.Traits.SNOW_WALKER, 1)
                .extraWikiLines("Allows the player to walk on powder snow without sinking. This will work on any armor or curio."));
        ret.add(TraitBuilder.simple(Const.Traits.SPOON, 1)
                .withGearTypeCondition(GearType.PICKAXE));
        ret.add(TraitBuilder.simple(Const.Traits.TURTLE, 1)
                .withGearTypeCondition(GearType.HELMET, GearType.CURIO));
        ret.add(TraitBuilder.simple(Const.Traits.VOID_WARD, 1)
                .withGearTypeCondition(GearType.ARMOR)
                .extraWikiLines("When void damage is taken, the player is launched upward and given a levitation and slow falling effect"));
        ret.add(TraitBuilder.simple(Const.Traits.WIDEN, 3)
                .withGearTypeCondition(GearType.HARVEST_TOOL)
                .extraWikiLines("  - Adds the trait level to the effect radius",
                        "  - Level 1 = 5x5, 2 = 7x7, 3 = 9x9"));

        // Synergy

        ret.add(new SynergyTraitBuilder(Const.Traits.CRUDE, 5, -0.04f)
                .cancelsWith(Const.Traits.RUSTIC)
                .cancelsWith(Const.Traits.SYNERGISTIC)
        );
        ret.add(new SynergyTraitBuilder(Const.Traits.RUSTIC, 5, 0.05f)
                .setRange(0.749f, 1.001f)
                .cancelsWith(Const.Traits.SYNERGISTIC)
        );
        ret.add(new SynergyTraitBuilder(Const.Traits.SYNERGISTIC, 5, 0.04f)
                .setRangeMin(1f)
                .cancelsWith(Const.Traits.CRUDE)
        );

        // Durability

        ret.add(new DurabilityTraitBuilder(Const.Traits.BENDING, 5, -1, 0.075f)
                .cancelsWith(Const.Traits.FLEXIBLE));
        ret.add(new DurabilityTraitBuilder(Const.Traits.BRITTLE, 5, 1, 0.1f)
                .cancelsWith(Const.Traits.MALLEABLE));
        ret.add(new DurabilityTraitBuilder(Const.Traits.FLEXIBLE, 5, -1, 0.075f)
                .cancelsWith(Const.Traits.BENDING));
        ret.add(new DurabilityTraitBuilder(Const.Traits.MALLEABLE, 5, -1, 0.1f)
                .cancelsWith(Const.Traits.BRITTLE));
        ret.add(new DurabilityTraitBuilder(Const.Traits.STURDY, 5, -1, 0.175f)
                .cancelsWith(Const.Traits.BRITTLE));

        // Self Repair

        ret.add(selfRepairTrait(Const.Traits.RENEW, 5, 0.018f, 1));

        // Attribute

        ret.add(new AttributeTraitBuilder(Const.Traits.BASTION, 5)
                .addModifierAnySlot(GearType.ALL,
                        Attributes.ARMOR,
                        AttributeModifier.Operation.ADDITION,
                        1, 2, 3, 4, 5)
                .withGearTypeCondition(GearType.ARMOR, GearType.CURIO)
        );
        ret.add(new AttributeTraitBuilder(Const.Traits.CURSED, 7)
                .addModifierAnySlot(GearType.ALL,
                        Attributes.LUCK,
                        AttributeModifier.Operation.ADDITION,
                        -0.5f, -1f, -1.5f, -2f, -3f, -4f, -5f)
                .cancelsWith(Const.Traits.LUCKY)
                .extraWikiLines("  - Please see the extra info on the Lucky trait and this wiki page: https://minecraft.gamepedia.com/Luck")
        );
        ret.add(new AttributeTraitBuilder(Const.Traits.LUCKY, 7)
                .addModifierAnySlot(GearType.ALL,
                        Attributes.LUCK,
                        AttributeModifier.Operation.ADDITION,
                        0.5f, 1f, 1.5f, 2f, 3f, 4f, 5f)
                .cancelsWith(Const.Traits.CURSED)
                .extraWikiLines("  - **Luck has nothing to do with the Fortune enchantment!** It affects loot from some loot tables, but not most. It does not increase drops from normal ores. Please read here for more information: https://minecraft.gamepedia.com/Luck")
        );
        ret.add(new AttributeTraitBuilder(Const.Traits.HEAVY, 5)
                .addArmorModifier(
                        Attributes.MOVEMENT_SPEED,
                        AttributeModifier.Operation.MULTIPLY_BASE,
                        -0.01f, -0.02f, -0.03f, -0.04f, -0.05f)
                .cancelsWith(Const.Traits.LIGHT)
                .withGearTypeCondition(GearType.ARMOR)
        );
        ret.add(new AttributeTraitBuilder(Const.Traits.LIGHT, 5)
                .addArmorModifier(
                        Attributes.MOVEMENT_SPEED,
                        AttributeModifier.Operation.MULTIPLY_BASE,
                        0.01f, 0.02f, 0.03f, 0.04f, 0.05f)
                .cancelsWith(Const.Traits.HEAVY)
                .withGearTypeCondition(GearType.ARMOR)
        );
        {
            int maxLevel = 5;
            float[] values = new float[maxLevel];
            for (int i = 0; i < maxLevel; ++i) {
                values[i] = Const.Traits.MOONWALKER_GRAVITY_MOD * (i + 1);
            }
            ret.add(new AttributeTraitBuilder(Const.Traits.MOONWALKER, maxLevel)
                    .addModifierAnySlot(GearType.ALL,
                            ForgeMod.ENTITY_GRAVITY.get(),
                            AttributeModifier.Operation.MULTIPLY_BASE,
                            values)
                    .withGearTypeCondition(GearType.BOOTS, GearType.CURIO)
            );
        }
        ret.add(new AttributeTraitBuilder(Const.Traits.REACH, 5)
                .addModifierAnySlot(GearType.ALL,
                        ForgeMod.REACH_DISTANCE.get(),
                        AttributeModifier.Operation.ADDITION,
                        0.5f, 1f, 1.5f, 2f, 3f)
        );
        ret.add(new AttributeTraitBuilder(Const.Traits.SWIFT_SWIM, 5)
                .addModifierAnySlot(GearType.ALL,
                        ForgeMod.SWIM_SPEED.get(),
                        AttributeModifier.Operation.ADDITION,
                        0.2f, 0.4f, 0.6f, 0.8f, 1f)
        );

        // Enchantment

        ret.add(new EnchantmentTraitBuilder(Const.Traits.FIERY, 2)
                .addEnchantments(GearType.MELEE_WEAPON, Enchantments.FIRE_ASPECT, 1, 2)
                .addEnchantments(GearType.RANGED_WEAPON, Enchantments.FLAMING_ARROWS, 1)
                .withGearTypeCondition(GearType.WEAPON)
        );
        ret.add(new EnchantmentTraitBuilder(Const.Traits.SILKY, 1)
                .addEnchantments(GearType.HARVEST_TOOL, Enchantments.SILK_TOUCH, 1)
                .withGearTypeCondition(GearType.HARVEST_TOOL)
        );

        // Wielder Effect (Potion)

        ret.add(new WielderEffectTraitBuilder(Const.Traits.ADAMANT, 5)
                .addEffect(GearType.ARMOR, WielderEffectTraitBuilder.LevelType.PIECE_COUNT, MobEffects.DAMAGE_RESISTANCE, 1, 1, 1, 2)
        );
        ret.add(new WielderEffectTraitBuilder(Const.Traits.AQUATIC, 5)
                .addEffect(GearType.ARMOR, WielderEffectTraitBuilder.LevelType.FULL_SET_ONLY, MobEffects.WATER_BREATHING, 1)
        );
        ret.add(new WielderEffectTraitBuilder(Const.Traits.FLAME_WARD, 1)
                .addEffect(GearType.ARMOR, WielderEffectTraitBuilder.LevelType.FULL_SET_ONLY, MobEffects.FIRE_RESISTANCE, 1)
                .overridesTrait(Const.Traits.FLAMMABLE)
                .withGearTypeCondition(GearType.ARMOR)
                .extraWikiLines("  - The item cannot be destroyed by fire or lava")
        );
        ret.add(new WielderEffectTraitBuilder(Const.Traits.KITTY_VISION, 1)
                .addEffect(GearType.HELMET, WielderEffectTraitBuilder.LevelType.TRAIT_LEVEL, MobEffects.NIGHT_VISION, 1)
                .addEffect(GearType.CURIO, WielderEffectTraitBuilder.LevelType.TRAIT_LEVEL, MobEffects.NIGHT_VISION, 1)
                .withGearTypeCondition(GearType.HELMET, GearType.CURIO)
        );
        ret.add(new WielderEffectTraitBuilder(Const.Traits.MIGHTY, 5)
                .addEffect(GearType.TOOL, WielderEffectTraitBuilder.LevelType.TRAIT_LEVEL, MobEffects.DAMAGE_BOOST, 0, 0, 1, 1, 2)
                .addEffect(GearType.TOOL, WielderEffectTraitBuilder.LevelType.TRAIT_LEVEL, MobEffects.DIG_SPEED, 1, 1, 1, 2, 3)
                .addEffect(GearType.CURIO, WielderEffectTraitBuilder.LevelType.TRAIT_LEVEL, MobEffects.DIG_SPEED, 1, 1, 2, 2, 3)
                .withGearTypeCondition(GearType.TOOL, GearType.CURIO)
        );
        ret.add(new WielderEffectTraitBuilder(Const.Traits.STELLAR, 5, StellarTrait.SERIALIZER.getName())
                .addEffect(GearType.ARMOR, WielderEffectTraitBuilder.LevelType.PIECE_COUNT, MobEffects.MOVEMENT_SPEED, 0, 1, 2, 3)
                .addEffect(GearType.ARMOR, WielderEffectTraitBuilder.LevelType.PIECE_COUNT, MobEffects.JUMP, 1, 2, 3, 4)
                .extraWikiLines(String.format("  - Has a %d%% chance per level to restore 1 durability each second",
                        (int) (100 * Const.Traits.STELLAR_REPAIR_CHANCE)))
        );

        // Target Effect

        ret.add(new TargetEffectTraitBuilder(Const.Traits.VENOM, 5)
                .withDurationByLevel(GearType.TOOL, MobEffects.POISON, 0, 4.0f)
                .withGearTypeCondition(GearType.TOOL)
        );

        // Stat mod

        ret.add(new StatModifierTraitBuilder(Const.Traits.ACCELERATE, 5)
                .addStatMod(ItemStats.HARVEST_SPEED, 2f, true, false)
                .addStatMod(ItemStats.ATTACK_SPEED, 0.01f, true, false)
                .addStatMod(ItemStats.RANGED_SPEED, 0.01f, true, false)
                .withGearTypeCondition(GearType.TOOL)
        );
        ret.add(new StatModifierTraitBuilder(Const.Traits.BULKY, 5)
                .addStatMod(ItemStats.ATTACK_SPEED, -0.075f, true, false)
                .withGearTypeCondition(GearType.TOOL)
        );
        ret.add(new StatModifierTraitBuilder(Const.Traits.CHIPPING, 5)
                .addStatMod(ItemStats.ARMOR, -0.075f, true, true)
                .addStatMod(ItemStats.HARVEST_SPEED, 0.25f, true, true)
        );
        ret.add(new StatModifierTraitBuilder(Const.Traits.CRUSHING, 5)
                .addStatMod(ItemStats.ARMOR, 0.05f, true, true)
                .addStatMod(ItemStats.MELEE_DAMAGE, -0.1667f, true, true)
        );
        ret.add(new StatModifierTraitBuilder(Const.Traits.ERODED, 5)
                .addStatMod(ItemStats.MELEE_DAMAGE, -0.15f, true, true)
                .addStatMod(ItemStats.HARVEST_SPEED, 0.15f, true, true)
                .cancelsWith(Const.Traits.JAGGED)
                .withGearTypeCondition(GearType.TOOL)
        );
        ret.add(new StatModifierTraitBuilder(Const.Traits.HARD, 5)
                .addStatMod(ItemStats.HARVEST_SPEED, 0.05f, true, true)
                .addStatMod(ItemStats.RANGED_DAMAGE, -0.1f, true, true)
                .cancelsWith(Const.Traits.SOFT)
                .withGearTypeCondition(GearType.TOOL)
        );
        ret.add(new StatModifierTraitBuilder(Const.Traits.JAGGED, 5)
                .addStatMod(ItemStats.MELEE_DAMAGE, 0.1667f, true, true)
                .addStatMod(ItemStats.RANGED_DAMAGE, -0.1667f, true, true)
                .cancelsWith(Const.Traits.ERODED)
        );
        ret.add(new StatModifierTraitBuilder(Const.Traits.ORGANIC, 5)
                .addStatMod(ItemStats.ENCHANTMENT_VALUE, 0.1f, true, true)
                .addStatMod(ItemStats.MAGIC_DAMAGE, -0.15f, true, true)
                .cancelsWith(Const.Traits.ERODED)
        );
        ret.add(new StatModifierTraitBuilder(Const.Traits.SHARP, 5)
                .addStatMod(ItemStats.HARVEST_SPEED, 0.125f, true, true)
                .addStatMod(ItemStats.MELEE_DAMAGE, 0.125f, true, true)
                .withGearTypeCondition(GearType.TOOL)
        );
        ret.add(new StatModifierTraitBuilder(Const.Traits.SOFT, 5)
                .addStatMod(ItemStats.HARVEST_SPEED, -0.15f, true, true)
                .cancelsWith(Const.Traits.HARD)
                .withGearTypeCondition(GearType.TOOL)
        );

        // Block placers
        ret.add(new BlockPlacerTraitBuilder(Const.Traits.CRACKLER, 1, Blocks.BASALT, 3)
                .withGearTypeCondition(GearType.TOOL));
        ret.add(new BlockPlacerTraitBuilder(Const.Traits.FLOATSTONER, 1, Blocks.END_STONE, 3)
                .withGearTypeCondition(GearType.TOOL));
        ret.add(new BlockPlacerTraitBuilder(Const.Traits.IGNITE, 1, Blocks.FIRE, 1)
                .sound(SoundEvents.FLINTANDSTEEL_USE, 1f, 1f)
                .withGearTypeCondition(GearType.TOOL)
        );
        ret.add(new BlockPlacerTraitBuilder(Const.Traits.RACKER, 1, Blocks.NETHERRACK, 3)
                .withGearTypeCondition(GearType.TOOL));
        ret.add(new BlockPlacerTraitBuilder(Const.Traits.REFRACTIVE, 1, SgBlocks.PHANTOM_LIGHT.get(), 5)
                .sound(SoundEvents.AMETHYST_BLOCK_STEP, 0.75f, 0.5f)
                .withGearTypeCondition(GearType.TOOL)
        );
        ret.add(new BlockPlacerTraitBuilder(Const.Traits.TERMINUS, 1, Blocks.STONE, 3)
                .withGearTypeCondition(GearType.TOOL));
        ret.add(new BlockPlacerTraitBuilder(Const.Traits.VULCAN, 1, Blocks.OBSIDIAN, 20)
                .cooldown(100)
                .withGearTypeCondition(GearType.TOOL)
        );

        // Block fillers
        ret.add(new BlockFillerTraitBuilder(Const.Traits.ROAD_MAKER, 1, Blocks.DIRT_PATH, 0.5f)
                .target(Blocks.GRASS_BLOCK)
                .fillRange(1, 0, 1, false)
        );

        // Misfits

        ret.add(bonusDropsTraits(Const.Traits.GOLD_DIGGER, 5, 0.15f, 0.5f, Ingredient.of(SgTags.Items.GOLD_DIGGER_DROPS))
                .withGearTypeCondition(GearType.HARVEST_TOOL));
        ret.add(bonusDropsTraits(Const.Traits.IMPERIAL, 5, 0.08f, 1f, Ingredient.of(SgTags.Items.IMPERIAL_DROPS))
                .withGearTypeCondition(GearType.HARVEST_TOOL));

        ret.add(cancelEffectsTrait(Const.Traits.CURE_POISON, MobEffects.POISON));
        ret.add(cancelEffectsTrait(Const.Traits.CURE_WITHER, MobEffects.WITHER));

        ret.add(damageTypeTrait(Const.Traits.CHILLED, 5, "chilled", 2)
                .withGearTypeCondition(GearType.WEAPON));
        ret.add(damageTypeTrait(Const.Traits.HOLY, 5, "holy", 2)
                .withGearTypeCondition(GearType.WEAPON));

        ret.add(new BlockMiningSpeedTraitBuilder(Const.Traits.GREEDY, 5, 0.2f, Tags.Blocks.ORES));

        return ret;
    }
}
