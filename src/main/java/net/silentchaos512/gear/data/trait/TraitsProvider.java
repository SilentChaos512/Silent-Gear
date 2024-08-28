package net.silentchaos512.gear.data.trait;

import net.minecraft.data.DataGenerator;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.common.Tags;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.data.trait.*;
import net.silentchaos512.gear.core.SoundPlayback;
import net.silentchaos512.gear.gear.trait.effect.*;
import net.silentchaos512.gear.setup.SgBlocks;
import net.silentchaos512.gear.setup.SgTags;
import net.silentchaos512.gear.setup.gear.GearProperties;
import net.silentchaos512.gear.setup.gear.GearTypes;
import net.silentchaos512.gear.util.Const;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

        ret.add(TraitBuilder.of(Const.Traits.ANCIENT, 5)
                .withGearTypeCondition(GearTypes.TOOL)
        );
        ret.add(TraitBuilder.of(Const.Traits.BOUNCE, 1)
                .withGearTypeCondition(GearTypes.ARMOR)
        );
        ret.add(TraitBuilder.of(Const.Traits.BRILLIANT, 1));
        ret.add(TraitBuilder.of(Const.Traits.CONFETTI, 5)
                .withGearTypeCondition(GearTypes.WEAPON));
        ret.add(TraitBuilder.of(Const.Traits.FIREPROOF, 1)
                .effects(
                        new FireproofTraitEffect()
                )
                .extraWikiLines("  - The item cannot be destroyed by fire or lava")
        );
        ret.add(TraitBuilder.of(Const.Traits.FLAMMABLE, 1));
        ret.add(TraitBuilder.of(Const.Traits.INDESTRUCTIBLE, 1)
                .extraWikiLines(
                        "  - The damage (durability lost) of the item will remain the same as when the trait was added",
                        "  - The item can still be repaired if desired"
                )
        );
        ret.add(TraitBuilder.of(Const.Traits.JABBERWOCKY, 1)
                .withGearTypeCondition(GearTypes.HARVEST_TOOL)
                .extraWikiLines("Something may happen if you mine certain blocks with this"));
        ret.add(TraitBuilder.of(Const.Traits.LUSTROUS, 5)
                .withGearTypeCondition(GearTypes.HARVEST_TOOL));
        ret.add(TraitBuilder.of(Const.Traits.MAGMATIC, 1)
                .withGearTypeCondition(GearTypes.HARVEST_TOOL)
                .extraWikiLines("Smelted drops are not affected by fortune to prevent item duplication"));
        ret.add(TraitBuilder.of(Const.Traits.MAGNETIC, 5)
                .extraWikiLines("Higher levels increase range"));
        ret.add(TraitBuilder.of(Const.Traits.MULTI_BREAK, 5)
                .extraWikiLines("  - This trait has never been coded ~~and has almost achieved meme status~~",
                        "  - Intended effect: mine multiple blocks like vein miner"));
        ret.add(TraitBuilder.of(Const.Traits.RED_CARD, 1));
        ret.add(TraitBuilder.of(Const.Traits.SNOW_WALKER, 1)
                .extraWikiLines("Allows the player to walk on powder snow without sinking. This will work on any armor or curio."));
        ret.add(TraitBuilder.of(Const.Traits.SPOON, 1)
                .withGearTypeCondition(GearTypes.PICKAXE));
        ret.add(TraitBuilder.of(Const.Traits.TURTLE, 1)
                .withGearTypeCondition(GearTypes.HELMET, GearTypes.CURIO));
        ret.add(TraitBuilder.of(Const.Traits.VOID_WARD, 1)
                .withGearTypeCondition(GearTypes.ARMOR)
                .extraWikiLines("When void damage is taken, the player is launched upward and given a levitation and slow falling effect"));
        ret.add(TraitBuilder.of(Const.Traits.WIDEN, 3)
                .withGearTypeCondition(GearTypes.HARVEST_TOOL)
                .extraWikiLines("  - Adds the trait level to the effect radius",
                        "  - Level 1 = 5x5, 2 = 7x7, 3 = 9x9"));

        // Synergy

        ret.add(TraitBuilder.of(Const.Traits.CRUDE, 5)
                .effects(new SynergyTraitEffect(-0.04f))
                .cancelsWith(Const.Traits.RUSTIC)
                .cancelsWith(Const.Traits.SYNERGISTIC)
        );
        ret.add(TraitBuilder.of(Const.Traits.RUSTIC, 5)
                .effects(new SynergyTraitEffect(0.05f, 0.749f, 1.001f))
                .cancelsWith(Const.Traits.SYNERGISTIC)
        );
        ret.add(TraitBuilder.of(Const.Traits.SYNERGISTIC, 5)
                .effects(new SynergyTraitEffect(0.04f, 1f, Float.MAX_VALUE))
                .cancelsWith(Const.Traits.CRUDE)
        );

        // Durability

        ret.add(TraitBuilder.of(Const.Traits.BENDING, 5)
                .effects(new DurabilityTraitEffect(0.075f, 1))
                .cancelsWith(Const.Traits.FLEXIBLE));
        ret.add(TraitBuilder.of(Const.Traits.BRITTLE, 5)
                .effects(new DurabilityTraitEffect(0.1f, 1))
                .cancelsWith(Const.Traits.MALLEABLE));
        ret.add(TraitBuilder.of(Const.Traits.FLEXIBLE, 5)
                .effects(new DurabilityTraitEffect(0.075f, -1))
                .cancelsWith(Const.Traits.BENDING));
        ret.add(TraitBuilder.of(Const.Traits.MALLEABLE, 5)
                .effects(new DurabilityTraitEffect(0.1f, -1))
                .cancelsWith(Const.Traits.BRITTLE));
        ret.add(TraitBuilder.of(Const.Traits.STURDY, 5)
                .effects(new DurabilityTraitEffect(0.175f, -1))
                .cancelsWith(Const.Traits.BRITTLE));

        // Self Repair

        ret.add(TraitBuilder.of(Const.Traits.RENEW, 5)
                .effects(new SelfRepairTraitEffect(0.018f, 1))
        );

        // Attribute

        ret.add(TraitBuilder.of(Const.Traits.BASTION, 5)
                .effects(
                        AttributeTraitEffect.builder()
                                .addAnySlot(GearTypes.ALL,
                                        Attributes.ARMOR,
                                        AttributeModifier.Operation.ADD_VALUE,
                                        1, 2, 3, 4, 5)
                                .build()
                )
                .withGearTypeCondition(GearTypes.ARMOR, GearTypes.CURIO)
        );
        ret.add(TraitBuilder.of(Const.Traits.CURSED, 7)
                .effects(AttributeTraitEffect.builder()
                        .addAnySlot(GearTypes.ALL,
                                Attributes.LUCK,
                                AttributeModifier.Operation.ADD_VALUE,
                                -0.5f, -1f, -1.5f, -2f, -3f, -4f, -5f)
                        .build()
                )
                .cancelsWith(Const.Traits.LUCKY)
                .extraWikiLines("  - Please see the extra info on the Lucky trait and this wiki page: https://minecraft.gamepedia.com/Luck")
        );
        ret.add(TraitBuilder.of(Const.Traits.LUCKY, 7)
                .effects(AttributeTraitEffect.builder()
                        .addAnySlot(GearTypes.ALL,
                                Attributes.LUCK,
                                AttributeModifier.Operation.ADD_VALUE,
                                0.5f, 1f, 1.5f, 2f, 3f, 4f, 5f)
                        .build()
                )
                .cancelsWith(Const.Traits.CURSED)
                .extraWikiLines("  - **Luck has nothing to do with the Fortune enchantment!** It affects loot from some loot tables, but not most. It does not increase drops from normal ores. Please read here for more information: https://minecraft.gamepedia.com/Luck")
        );
        ret.add(TraitBuilder.of(Const.Traits.HEAVY, 5)
                .effects(AttributeTraitEffect.builder()
                        .addArmorSlots(
                                Attributes.MOVEMENT_SPEED,
                                AttributeModifier.Operation.ADD_MULTIPLIED_BASE,
                                -0.01f, -0.02f, -0.03f, -0.04f, -0.05f)
                        .build()
                )
                .cancelsWith(Const.Traits.LIGHT)
                .withGearTypeCondition(GearTypes.ARMOR)
        );
        ret.add(TraitBuilder.of(Const.Traits.LIGHT, 5)
                .effects(AttributeTraitEffect.builder()
                        .addArmorSlots(
                                Attributes.MOVEMENT_SPEED,
                                AttributeModifier.Operation.ADD_MULTIPLIED_BASE,
                                0.01f, 0.02f, 0.03f, 0.04f, 0.05f)
                        .build()
                )
                .cancelsWith(Const.Traits.HEAVY)
                .withGearTypeCondition(GearTypes.ARMOR)
        );
        {
            int maxLevel = 5;
            float[] values = new float[maxLevel];
            for (int i = 0; i < maxLevel; ++i) {
                values[i] = Const.Traits.MOONWALKER_GRAVITY_MOD * (i + 1);
            }
            ret.add(TraitBuilder.of(Const.Traits.MOONWALKER, maxLevel)
                    .effects(
                            AttributeTraitEffect.builder()
                                    .addAnySlot(GearTypes.ALL,
                                            Attributes.GRAVITY,
                                            AttributeModifier.Operation.ADD_MULTIPLIED_BASE,
                                            values)
                                    .build()
                    )
                    .withGearTypeCondition(GearTypes.BOOTS, GearTypes.CURIO)
            );
        }
        ret.add(TraitBuilder.of(Const.Traits.REACH, 5)
                .effects(
                        AttributeTraitEffect.builder()
                                .addAnySlot(GearTypes.ALL,
                                        Attributes.BLOCK_INTERACTION_RANGE,
                                        AttributeModifier.Operation.ADD_VALUE,
                                        0.5f, 1f, 1.5f, 2f, 3f)
                                .build()
                )
        );
        ret.add(TraitBuilder.of(Const.Traits.SWIFT_SWIM, 5)
                .effects(
                        AttributeTraitEffect.builder()
                                .addAnySlot(GearTypes.ALL,
                                        NeoForgeMod.SWIM_SPEED,
                                        AttributeModifier.Operation.ADD_VALUE,
                                        0.2f, 0.4f, 0.6f, 0.8f, 1f)
                                .build()
                )
        );

        // Enchantment

        // FIXME: Bring back enchantment trait
        ret.add(new TraitBuilder(Const.Traits.FIERY, 2));
        ret.add(new TraitBuilder(Const.Traits.SILKY, 1));

        /*ret.add(new EnchantmentTraitBuilder(Const.Traits.FIERY, 2)
                .addEnchantments(GearTypes.MELEE_WEAPON, Enchantments.FIRE_ASPECT, 1, 2)
                .addEnchantments(GearTypes.RANGED_WEAPON, Enchantments.FLAMING_ARROWS, 1)
                .withGearTypeCondition(GearTypes.WEAPON)
        );
        ret.add(new EnchantmentTraitBuilder(Const.Traits.SILKY, 1)
                .addEnchantments(GearTypes.HARVEST_TOOL, Enchantments.SILK_TOUCH, 1)
                .withGearTypeCondition(GearTypes.HARVEST_TOOL)
        );*/

        // Wielder Effect (Potion)

        ret.add(TraitBuilder.of(Const.Traits.ADAMANT, 5)
                .effects(
                        WielderEffectTraitEffect.builder()
                                .add(GearTypes.ARMOR, WielderEffectTraitEffect.LevelType.PIECE_COUNT, MobEffects.DAMAGE_RESISTANCE, 1, 1, 1, 2)
                                .build(),
                        ExtraDamageTraitEffect.affectingHighHealth(2.0f)
                )
        );
        ret.add(TraitBuilder.of(Const.Traits.AQUATIC, 5)
                .effects(
                        WielderEffectTraitEffect.builder()
                                .add(GearTypes.ARMOR, WielderEffectTraitEffect.LevelType.FULL_SET_ONLY, MobEffects.WATER_BREATHING, 1)
                                .build(),
                        ExtraDamageTraitEffect.affectingAquatic(2.0f)
                )
        );
        ret.add(TraitBuilder.of(Const.Traits.FLAME_WARD, 1)
                .effects(
                        new FireproofTraitEffect(),
                        WielderEffectTraitEffect.builder()
                                .add(GearTypes.ARMOR, WielderEffectTraitEffect.LevelType.FULL_SET_ONLY, MobEffects.FIRE_RESISTANCE, 1)
                                .build()
                )
                .overridesTrait(Const.Traits.FLAMMABLE)
                .withGearTypeCondition(GearTypes.ARMOR)
                .extraWikiLines("  - The item cannot be destroyed by fire or lava")
        );
        ret.add(TraitBuilder.of(Const.Traits.KITTY_VISION, 1)
                .effects(
                        WielderEffectTraitEffect.builder()
                                .add(GearTypes.HELMET, WielderEffectTraitEffect.LevelType.TRAIT_LEVEL, MobEffects.NIGHT_VISION, 1)
                                .add(GearTypes.CURIO, WielderEffectTraitEffect.LevelType.TRAIT_LEVEL, MobEffects.NIGHT_VISION, 1)
                                .build()
                )
                .withGearTypeCondition(GearTypes.HELMET, GearTypes.CURIO)
        );
        ret.add(TraitBuilder.of(Const.Traits.MIGHTY, 5)
                .effects(
                        WielderEffectTraitEffect.builder()
                                .add(GearTypes.TOOL, WielderEffectTraitEffect.LevelType.TRAIT_LEVEL, MobEffects.DAMAGE_BOOST, 0, 0, 1, 1, 2)
                                .add(GearTypes.TOOL, WielderEffectTraitEffect.LevelType.TRAIT_LEVEL, MobEffects.DIG_SPEED, 1, 1, 1, 2, 3)
                                .add(GearTypes.CURIO, WielderEffectTraitEffect.LevelType.TRAIT_LEVEL, MobEffects.DIG_SPEED, 1, 1, 2, 2, 3)
                                .build()
                )
                .withGearTypeCondition(GearTypes.TOOL, GearTypes.CURIO)
        );
        ret.add(TraitBuilder.of(Const.Traits.STELLAR, 5)
                .effects(
                        new SelfRepairTraitEffect(0.02f, 1),
                        WielderEffectTraitEffect.builder()
                                .add(GearTypes.ARMOR,
                                        WielderEffectTraitEffect.LevelType.PIECE_COUNT,
                                        MobEffects.MOVEMENT_SPEED,
                                        0, 1, 2, 3
                                )
                                .add(GearTypes.ARMOR,
                                        WielderEffectTraitEffect.LevelType.PIECE_COUNT,
                                        MobEffects.JUMP,
                                        1, 2, 3, 4
                                )
                                .build()
                )
        );

        // Target Effect

        ret.add(TraitBuilder.of(Const.Traits.VENOM, 5)
                .withGearTypeCondition(GearTypes.TOOL)
                .effects(
                        TargetEffectTraitEffect.builder()
                                .addWithDurationByLevel(GearTypes.TOOL, MobEffects.POISON, 5, 4.0f)
                                .build()
                )
        );

        // Stat mod

        ret.add(new TraitBuilder(Const.Traits.ACCELERATE, 5)
                .withGearTypeCondition(GearTypes.TOOL)
                .effects(
                        NumberPropertyModifierTraitEffect.builder()
                                .add(GearProperties.HARVEST_SPEED, 2f, true, false)
                                .add(GearProperties.ATTACK_SPEED, 0.01f, true, false)
                                .add(GearProperties.DRAW_SPEED, 0.01f, true, false)
                                .build()
                )
        );
        ret.add(new TraitBuilder(Const.Traits.BULKY, 5)
                .withGearTypeCondition(GearTypes.TOOL)
                .effects(
                        NumberPropertyModifierTraitEffect.builder()
                                .add(GearProperties.ATTACK_SPEED, -0.075f, true, false)
                                .build()
                )
        );
        ret.add(new TraitBuilder(Const.Traits.CHIPPING, 5)
                .effects(
                        NumberPropertyModifierTraitEffect.builder()
                                .add(GearProperties.ARMOR, -0.075f, true, true)
                                .add(GearProperties.HARVEST_SPEED, 0.25f, true, true)
                                .build()
                )
        );
        ret.add(new TraitBuilder(Const.Traits.CRUSHING, 5)
                .effects(
                        NumberPropertyModifierTraitEffect.builder()
                                .add(GearProperties.ARMOR, 0.05f, true, true)
                                .add(GearProperties.ATTACK_DAMAGE, -0.1667f, true, true)
                                .build()
                )
        );
        ret.add(new TraitBuilder(Const.Traits.ERODED, 5)
                .withGearTypeCondition(GearTypes.TOOL)
                .cancelsWith(Const.Traits.JAGGED)
                .effects(
                        NumberPropertyModifierTraitEffect.builder()
                                .add(GearProperties.ATTACK_DAMAGE, -0.15f, true, true)
                                .add(GearProperties.HARVEST_SPEED, 0.15f, true, true)
                                .build()
                )
        );
        ret.add(new TraitBuilder(Const.Traits.HARD, 5)
                .withGearTypeCondition(GearTypes.TOOL)
                .cancelsWith(Const.Traits.SOFT)
                .effects(
                        NumberPropertyModifierTraitEffect.builder()
                                .add(GearProperties.HARVEST_SPEED, 0.05f, true, true)
                                .add(GearProperties.RANGED_DAMAGE, -0.1f, true, true)
                                .build()
                )
        );
        ret.add(new TraitBuilder(Const.Traits.JAGGED, 5)
                .withGearTypeCondition(GearTypes.TOOL)
                .cancelsWith(Const.Traits.ERODED)
                .effects(
                        NumberPropertyModifierTraitEffect.builder()
                                .add(GearProperties.ATTACK_DAMAGE, 0.1667f, true, true)
                                .add(GearProperties.RANGED_DAMAGE, -0.1667f, true, true)
                                .build()
                )
        );
        ret.add(new TraitBuilder(Const.Traits.ORGANIC, 5)
                .effects(
                        NumberPropertyModifierTraitEffect.builder()
                                .add(GearProperties.ENCHANTMENT_VALUE, 0.1f, true, true)
                                .add(GearProperties.MAGIC_DAMAGE, -0.15f, true, true)
                                .build()
                )
        );
        ret.add(new TraitBuilder(Const.Traits.SHARP, 5)
                .withGearTypeCondition(GearTypes.TOOL)
                .effects(
                        NumberPropertyModifierTraitEffect.builder()
                                .add(GearProperties.HARVEST_SPEED, 0.125f, true, true)
                                .add(GearProperties.ATTACK_DAMAGE, 0.125f, true, true)
                                .build()
                )
        );
        ret.add(new TraitBuilder(Const.Traits.SOFT, 5)
                .withGearTypeCondition(GearTypes.TOOL)
                .cancelsWith(Const.Traits.HARD)
                .effects(
                        NumberPropertyModifierTraitEffect.builder()
                                .add(GearProperties.HARVEST_SPEED, -0.15f, true, true)
                                .build()
                )
        );

        // Block placers
        ret.add(new TraitBuilder(Const.Traits.CRACKLER, 1)
                .withGearTypeCondition(GearTypes.TOOL)
                .effects(
                        new BlockPlacerTraitEffect(
                                Blocks.BASALT.defaultBlockState(),
                                3,
                                0,
                                new SoundPlayback(SoundEvents.BASALT_PLACE, 1f, 1f, 0.2f)
                        )
                )
        );
        ret.add(new TraitBuilder(Const.Traits.FLOATSTONER, 1)
                .withGearTypeCondition(GearTypes.TOOL)
                .effects(
                        new BlockPlacerTraitEffect(
                                Blocks.END_STONE.defaultBlockState(),
                                3,
                                0,
                                new SoundPlayback(SoundEvents.STONE_PLACE, 1f, 1f, 0.2f)
                        )
                )
        );
        ret.add(new TraitBuilder(Const.Traits.IGNITE, 1)
                .withGearTypeCondition(GearTypes.TOOL)
                .effects(
                        new BlockPlacerTraitEffect(
                                Blocks.FIRE.defaultBlockState(),
                                1,
                                0,
                                new SoundPlayback(SoundEvents.FLINTANDSTEEL_USE, 1f, 1f, 0.2f)
                        )
                )
        );
        ret.add(new TraitBuilder(Const.Traits.RACKER, 1)
                .withGearTypeCondition(GearTypes.TOOL)
                .effects(
                        new BlockPlacerTraitEffect(
                                Blocks.NETHERRACK.defaultBlockState(),
                                3,
                                0,
                                new SoundPlayback(SoundEvents.NETHERRACK_PLACE, 1f, 1f, 0.2f)
                        )
                )
        );
        ret.add(new TraitBuilder(Const.Traits.REFRACTIVE, 1)
                .withGearTypeCondition(GearTypes.TOOL)
                .effects(
                        new BlockPlacerTraitEffect(
                                SgBlocks.PHANTOM_LIGHT.get().defaultBlockState(),
                                5,
                                0,
                                new SoundPlayback(SoundEvents.AMETHYST_BLOCK_STEP, 0.75f, 0.5f, 0.1f)
                        )
                )
        );
        ret.add(new TraitBuilder(Const.Traits.TERMINUS, 1)
                .withGearTypeCondition(GearTypes.TOOL)
                .effects(
                        new BlockPlacerTraitEffect(
                                Blocks.STONE.defaultBlockState(),
                                3,
                                0,
                                new SoundPlayback(SoundEvents.STONE_PLACE, 1f, 1f, 0.2f)
                        )
                )
        );
        ret.add(new TraitBuilder(Const.Traits.VULCAN, 1)
                .withGearTypeCondition(GearTypes.TOOL)
                .effects(
                        new BlockPlacerTraitEffect(
                                Blocks.OBSIDIAN.defaultBlockState(),
                                20,
                                100,
                                new SoundPlayback(SoundEvents.STONE_PLACE, 1f, 1f, 0.2f)
                        )
                )
        );

        // Block fillers
        ret.add(new TraitBuilder(Const.Traits.CRACKLER, 1)
                .withGearTypeCondition(GearTypes.TOOL)
                .effects(
                        new BlockFillerTraitEffect(
                                new BlockFillerTraitEffect.TargetBlock(Blocks.GRASS_BLOCK, null),
                                new BlockFillerTraitEffect.FillProperties(
                                        Blocks.DIRT_PATH.defaultBlockState(),
                                        false,
                                        1, 0, 1,
                                        false
                                ),
                                new BlockFillerTraitEffect.UseProperties(BlockFillerTraitEffect.SneakMode.PASS, 0.5f, 0),
                                new SoundPlayback(SoundEvents.GRASS_BREAK, 1f, 1f, 0.1f)
                        )
                )
        );

        // Misfits

        ret.add(new TraitBuilder(Const.Traits.GOLD_DIGGER, 5)
                .withGearTypeCondition(GearTypes.HARVEST_TOOL)
                .effects(
                        new BonusDropsTraitEffect(
                                0.15f,
                                0.5f,
                                Ingredient.of(SgTags.Items.GOLD_DIGGER_DROPS),
                                "nuggets"
                        )
                )
        );
        ret.add(new TraitBuilder(Const.Traits.IMPERIAL, 5)
                .withGearTypeCondition(GearTypes.HARVEST_TOOL)
                .effects(
                        new BonusDropsTraitEffect(
                                0.15f,
                                0.5f,
                                Ingredient.of(SgTags.Items.IMPERIAL_DROPS),
                                "gems"
                        )
                )
        );

        ret.add(new TraitBuilder(Const.Traits.CURE_POISON, 1)
                .effects(
                        new CancelEffectsTraitEffect(List.of(MobEffects.POISON))
                )
        );
        ret.add(new TraitBuilder(Const.Traits.CURE_WITHER, 1)
                .effects(
                        new CancelEffectsTraitEffect(List.of(MobEffects.WITHER))
                )
        );


        ret.add(TraitBuilder.of(Const.Traits.CHILLED, 5)
                .withGearTypeCondition(GearTypes.WEAPON)
                .effects(
                        ExtraDamageTraitEffect.affectingFireImmune(2.0f)
                )
        );
        ret.add(TraitBuilder.of(Const.Traits.HOLY, 5)
                .withGearTypeCondition(GearTypes.WEAPON)
                .effects(
                        ExtraDamageTraitEffect.affecting(EntityTypeTags.UNDEAD, 2.0f)
                )
        );

        ret.add(TraitBuilder.of(Const.Traits.GREEDY, 5)
                .effects(
                        new BlockMiningSpeedTraitEffect(
                                Tags.Blocks.ORES,
                                0.2f
                        ),
                        new ItemMagnetTraitEffect(
                                0.06f,
                                2f,
                                Ingredient.of(SgTags.Items.GREEDY_MAGNET_ATTRACTED),
                                "ores and gems"
                        )
                )
        );

        return ret;
    }
}
