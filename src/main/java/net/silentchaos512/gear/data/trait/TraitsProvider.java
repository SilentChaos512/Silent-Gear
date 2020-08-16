package net.silentchaos512.gear.data.trait;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.potion.Effects;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.common.ForgeMod;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.init.ModBlocks;
import net.silentchaos512.gear.gear.trait.DamageTypeTrait;
import net.silentchaos512.gear.util.Const;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class TraitsProvider implements IDataProvider {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
    private final DataGenerator generator;

    public TraitsProvider(DataGenerator generator) {
        this.generator = generator;
    }

    @Override
    public String getName() {
        return "Silent Gear - Traits";
    }

    @SuppressWarnings("MethodMayBeStatic")
    protected Collection<TraitBuilder> getTraits() {
        Collection<TraitBuilder> ret = new ArrayList<>();

        // Simple

        ret.add(TraitBuilder.simple(Const.Traits.ANCIENT, 5));
        ret.add(TraitBuilder.simple(Const.Traits.BRILLIANT, 1));
        ret.add(TraitBuilder.simple(Const.Traits.CHILLED, 5));
        ret.add(TraitBuilder.simple(Const.Traits.CONFETTI, 5));
        ret.add(TraitBuilder.simple(Const.Traits.FLAMMABLE, 1));
        ret.add(TraitBuilder.simple(Const.Traits.INDESTRUCTIBLE, 1));
        ret.add(TraitBuilder.simple(Const.Traits.JABBERWOCKY, 1));
        ret.add(TraitBuilder.simple(Const.Traits.LUSTROUS, 5));
        ret.add(TraitBuilder.simple(Const.Traits.MAGMATIC, 1));
        ret.add(TraitBuilder.simple(Const.Traits.MAGNETIC, 5));
        ret.add(TraitBuilder.simple(Const.Traits.MULTI_BREAK, 5));
        ret.add(TraitBuilder.simple(Const.Traits.SPOON, 1));

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

        ret.add(new DurabilityTraitBuilder(Const.Traits.BRITTLE, 5, 1, 0.1f)
                .cancelsWith(Const.Traits.MALLEABLE));
        ret.add(new DurabilityTraitBuilder(Const.Traits.FLEXIBLE, 5, -1, 0.05f)
                .cancelsWith(Const.Traits.BRITTLE));
        ret.add(new DurabilityTraitBuilder(Const.Traits.MALLEABLE, 5, -1, 0.1f)
                .cancelsWith(Const.Traits.BRITTLE));

        // Attribute

        ret.add(new AttributeTraitBuilder(Const.Traits.CURSED, 7)
                .addModifiersEitherHand(GearType.ALL,
                        Attributes.LUCK,
                        AttributeModifier.Operation.ADDITION,
                        -0.5f, -1f, -1.5f, -2f, -3f, -4f, -5f)
                .cancelsWith(Const.Traits.LUCKY)
        );
        ret.add(new AttributeTraitBuilder(Const.Traits.LUCKY, 7)
                .addModifiersEitherHand(GearType.ALL,
                        Attributes.LUCK,
                        AttributeModifier.Operation.ADDITION,
                        0.5f, 1f, 1.5f, 2f, 3f, 4f, 5f)
                .cancelsWith(Const.Traits.CURSED)
        );
        ret.add(new AttributeTraitBuilder(Const.Traits.HEAVY, 5)
                .addArmorModifier(
                        Attributes.MOVEMENT_SPEED,
                        AttributeModifier.Operation.MULTIPLY_BASE,
                        -0.01f, -0.02f, -0.03f, -0.04f, -0.05f)
                .cancelsWith(Const.Traits.LIGHT)
        );
        ret.add(new AttributeTraitBuilder(Const.Traits.LIGHT, 5)
                .addArmorModifier(
                        Attributes.MOVEMENT_SPEED,
                        AttributeModifier.Operation.MULTIPLY_BASE,
                        0.01f, 0.02f, 0.03f, 0.04f, 0.05f)
                .cancelsWith(Const.Traits.HEAVY)
        );
        {
            int maxLevel = 5;
            float[] values = new float[maxLevel];
            for (int i = 0; i < maxLevel; ++i) {
                values[i] = Const.Traits.MOONWALKER_GRAVITY_MOD * (i + 1);
            }
            ret.add(new AttributeTraitBuilder(Const.Traits.MOONWALKER, maxLevel)
                    .addModifier(GearType.BOOTS, EquipmentSlotType.FEET,
                            ForgeMod.ENTITY_GRAVITY.get(),
                            AttributeModifier.Operation.MULTIPLY_BASE,
                            values)
            );
        }

        // Enchantment

        ret.add(new EnchantmentTraitBuilder(Const.Traits.FIERY, 2)
                .addEnchantments(GearType.MELEE_WEAPON, Enchantments.FIRE_ASPECT, 1, 2)
                .addEnchantments(GearType.RANGED_WEAPON, Enchantments.FLAME, 1)
        );
        ret.add(new EnchantmentTraitBuilder(Const.Traits.SILKY, 1)
                .addEnchantments(GearType.HARVEST_TOOL, Enchantments.SILK_TOUCH, 1)
        );

        // Potion

        ret.add(new PotionTraitBuilder(Const.Traits.ADAMANT, 5)
                .addEffect(GearType.ARMOR, false, Effects.RESISTANCE, 1, 1, 1, 2)
        );
        ret.add(new PotionTraitBuilder(Const.Traits.AQUATIC, 5)
                .addEffect(GearType.ARMOR, true, Effects.WATER_BREATHING, 1)
        );
        ret.add(new PotionTraitBuilder(Const.Traits.FLAME_WARD, 1)
                .addEffect(GearType.ARMOR, true, Effects.FIRE_RESISTANCE, 1)
                .overridesTrait(Const.Traits.FLAMMABLE)
        );
        ret.add(new PotionTraitBuilder(Const.Traits.STELLAR, 5)
                .addEffect(GearType.ARMOR, false, Effects.SPEED, 0, 1, 2, 3)
                .addEffect(GearType.ARMOR, false, Effects.JUMP_BOOST, 1, 2, 3, 4)
        );

        // Stat mod

        ret.add(new StatModifierTraitBuilder(Const.Traits.ACCELERATE, 5)
                .addStatMod(ItemStats.HARVEST_SPEED, 2f, true, false)
                .addStatMod(ItemStats.ATTACK_SPEED, 0.01f, true, false)
                .addStatMod(ItemStats.RANGED_SPEED, 0.01f, true, false)
        );
        ret.add(new StatModifierTraitBuilder(Const.Traits.BULKY, 5)
                .addStatMod(ItemStats.ATTACK_SPEED, -0.075f, true, false)
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
        );
        ret.add(new StatModifierTraitBuilder(Const.Traits.HARD, 5)
                .addStatMod(ItemStats.HARVEST_SPEED, 0.05f, true, true)
                .addStatMod(ItemStats.RANGED_DAMAGE, -0.1f, true, true)
                .cancelsWith(Const.Traits.SOFT)
        );
        ret.add(new StatModifierTraitBuilder(Const.Traits.JAGGED, 5)
                .addStatMod(ItemStats.MELEE_DAMAGE, 0.1667f, true, true)
                .addStatMod(ItemStats.RANGED_DAMAGE, -0.1667f, true, true)
                .cancelsWith(Const.Traits.ERODED)
        );
        ret.add(new StatModifierTraitBuilder(Const.Traits.ORGANIC, 5)
                .addStatMod(ItemStats.ENCHANTABILITY, 0.1f, true, true)
                .addStatMod(ItemStats.MAGIC_DAMAGE, -0.15f, true, true)
                .cancelsWith(Const.Traits.ERODED)
        );
        ret.add(new StatModifierTraitBuilder(Const.Traits.SOFT, 5)
                .addStatMod(ItemStats.HARVEST_SPEED, -0.15f, true, true)
                .cancelsWith(Const.Traits.HARD)
        );

        // Block placers
        ret.add(new BlockPlacerTraitBuilder(Const.Traits.RACKER, 1, Blocks.NETHERRACK, 3));
        ret.add(new BlockPlacerTraitBuilder(Const.Traits.REFRACTIVE, 1, ModBlocks.PHANTOM_LIGHT.get(), 5)
                .sound(SoundEvents.ENTITY_ITEM_PICKUP, 0.75f, 0.5f)
        );
        ret.add(new BlockPlacerTraitBuilder(Const.Traits.TERMINUS, 1, Blocks.STONE, 3));

        // Misfits

        ret.add(new TraitBuilder(Const.Traits.HOLY, 5, DamageTypeTrait.SERIALIZER)
                .extraData(json -> {
                    json.addProperty("damage_type", "holy");
                    json.addProperty("damage_bonus", 2);
                })
        );

        return ret;
    }

    @Override
    public void act(DirectoryCache cache) {
        Path outputFolder = this.generator.getOutputFolder();

        for (TraitBuilder builder : getTraits()) {
            try {
                String jsonStr = GSON.toJson(builder.serialize());
                String hashStr = HASH_FUNCTION.hashUnencodedChars(jsonStr).toString();
                Path path = outputFolder.resolve(String.format("data/%s/silentgear_traits/%s.json", builder.traitId.getNamespace(), builder.traitId.getPath()));
                if (!Objects.equals(cache.getPreviousHash(outputFolder), hashStr) || !Files.exists(path)) {
                    Files.createDirectories(path.getParent());

                    try (BufferedWriter writer = Files.newBufferedWriter(path)) {
                        writer.write(jsonStr);
                    }
                }

                cache.recordHash(path, hashStr);
            } catch (IOException ex) {
                LOGGER.error("Could not save traits to {}", outputFolder, ex);
            }
        }
    }
}
