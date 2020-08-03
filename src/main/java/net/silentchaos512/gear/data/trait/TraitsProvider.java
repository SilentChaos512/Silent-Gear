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
import net.minecraft.potion.Effects;
import net.minecraft.util.SoundEvents;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.init.ModBlocks;
import net.silentchaos512.gear.traits.DamageTypeTrait;
import net.silentchaos512.gear.traits.TraitConst;
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

        ret.add(TraitBuilder.simple(TraitConst.ANCIENT, 5));
        ret.add(TraitBuilder.simple(TraitConst.BRILLIANT, 1));
        ret.add(TraitBuilder.simple(TraitConst.CHILLED, 5));
        ret.add(TraitBuilder.simple(TraitConst.CONFETTI, 5));
        ret.add(TraitBuilder.simple(TraitConst.FLAMMABLE, 1));
        ret.add(TraitBuilder.simple(TraitConst.INDESTRUCTIBLE, 1));
        ret.add(TraitBuilder.simple(TraitConst.JABBERWOCKY, 1));
        ret.add(TraitBuilder.simple(TraitConst.LUSTROUS, 5));
        ret.add(TraitBuilder.simple(TraitConst.MAGMATIC, 1));
        ret.add(TraitBuilder.simple(TraitConst.MAGNETIC, 5));
        ret.add(TraitBuilder.simple(TraitConst.MULTI_BREAK, 5));
        ret.add(TraitBuilder.simple(TraitConst.SPOON, 1));

        // Synergy

        ret.add(new SynergyTraitBuilder(TraitConst.CRUDE, 5, -0.04f)
                .cancelsWith(TraitConst.RUSTIC)
                .cancelsWith(TraitConst.SYNERGISTIC)
        );
        ret.add(new SynergyTraitBuilder(TraitConst.RUSTIC, 5, 0.05f)
                .setRange(0.749f, 1.001f)
                .cancelsWith(TraitConst.SYNERGISTIC)
        );
        ret.add(new SynergyTraitBuilder(TraitConst.SYNERGISTIC, 5, 0.04f)
                .setRangeMin(1f)
                .cancelsWith(TraitConst.CRUDE)
        );

        // Durability

        ret.add(new DurabilityTraitBuilder(TraitConst.BRITTLE, 5, 1, 0.1f)
                .cancelsWith(TraitConst.MALLEABLE));
        ret.add(new DurabilityTraitBuilder(TraitConst.FLEXIBLE, 5, -1, 0.05f)
                .cancelsWith(TraitConst.BRITTLE));
        ret.add(new DurabilityTraitBuilder(TraitConst.MALLEABLE, 5, -1, 0.1f)
                .cancelsWith(TraitConst.BRITTLE));

        // Attribute

        ret.add(new AttributeTraitBuilder(TraitConst.CURSED, 7)
                .addModifiersEitherHand(GearType.ALL,
                        Attributes.LUCK,
                        AttributeModifier.Operation.ADDITION,
                        -0.5f, -1f, -1.5f, -2f, -3f, -4f, -5f)
                .cancelsWith(TraitConst.LUCKY)
        );
        ret.add(new AttributeTraitBuilder(TraitConst.LUCKY, 7)
                .addModifiersEitherHand(GearType.ALL,
                        Attributes.LUCK,
                        AttributeModifier.Operation.ADDITION,
                        0.5f, 1f, 1.5f, 2f, 3f, 4f, 5f)
                .cancelsWith(TraitConst.CURSED)
        );

        // Enchantment

        ret.add(new EnchantmentTraitBuilder(TraitConst.FIERY, 2)
                .addEnchantments(GearType.MELEE_WEAPON, Enchantments.FIRE_ASPECT, 1, 2)
                .addEnchantments(GearType.RANGED_WEAPON, Enchantments.FLAME, 1)
        );
        ret.add(new EnchantmentTraitBuilder(TraitConst.SILKY, 1)
                .addEnchantments(GearType.HARVEST_TOOL, Enchantments.SILK_TOUCH, 1)
        );

        // Potion

        ret.add(new PotionTraitBuilder(TraitConst.ADAMANT, 5)
                .addEffect(GearType.ARMOR, false, Effects.RESISTANCE, 1, 1, 1, 2)
        );
        ret.add(new PotionTraitBuilder(TraitConst.AQUATIC, 5)
                .addEffect(GearType.ARMOR, true, Effects.WATER_BREATHING, 1)
        );
        ret.add(new PotionTraitBuilder(TraitConst.FLAME_WARD, 1)
                .addEffect(GearType.ARMOR, true, Effects.FIRE_RESISTANCE, 1)
                .overridesTrait(TraitConst.FLAMMABLE)
        );
        ret.add(new PotionTraitBuilder(TraitConst.STELLAR, 5)
                .addEffect(GearType.ARMOR, false, Effects.SPEED, 0, 1, 2, 3)
                .addEffect(GearType.ARMOR, false, Effects.JUMP_BOOST, 1, 2, 3, 4)
        );

        // Stat mod

        ret.add(new StatModifierTraitBuilder(TraitConst.BULKY, 5)
                .addStatMod(ItemStats.ATTACK_SPEED, -0.075f, true, false)
        );
        ret.add(new StatModifierTraitBuilder(TraitConst.CHIPPING, 5)
                .addStatMod(ItemStats.ARMOR, -0.075f, true, true)
                .addStatMod(ItemStats.HARVEST_SPEED, 0.25f, true, true)
        );
        ret.add(new StatModifierTraitBuilder(TraitConst.CRUSHING, 5)
                .addStatMod(ItemStats.ARMOR, 0.05f, true, true)
                .addStatMod(ItemStats.MELEE_DAMAGE, -0.1667f, true, true)
        );
        ret.add(new StatModifierTraitBuilder(TraitConst.ERODED, 5)
                .addStatMod(ItemStats.MELEE_DAMAGE, -0.15f, true, true)
                .addStatMod(ItemStats.HARVEST_SPEED, 0.15f, true, true)
                .cancelsWith(TraitConst.JAGGED)
        );
        ret.add(new StatModifierTraitBuilder(TraitConst.HARD, 5)
                .addStatMod(ItemStats.HARVEST_SPEED, 0.05f, true, true)
                .addStatMod(ItemStats.RANGED_DAMAGE, -0.1f, true, true)
                .cancelsWith(TraitConst.SOFT)
        );
        ret.add(new StatModifierTraitBuilder(TraitConst.JAGGED, 5)
                .addStatMod(ItemStats.MELEE_DAMAGE, 0.1667f, true, true)
                .addStatMod(ItemStats.RANGED_DAMAGE, -0.1667f, true, true)
                .cancelsWith(TraitConst.ERODED)
        );
        ret.add(new StatModifierTraitBuilder(TraitConst.ORGANIC, 5)
                .addStatMod(ItemStats.ENCHANTABILITY, 0.1f, true, true)
                .addStatMod(ItemStats.MAGIC_DAMAGE, -0.15f, true, true)
                .cancelsWith(TraitConst.ERODED)
        );
        ret.add(new StatModifierTraitBuilder(TraitConst.SOFT, 5)
                .addStatMod(ItemStats.HARVEST_SPEED, -0.15f, true, true)
                .cancelsWith(TraitConst.HARD)
        );

        // Block placers
        ret.add(new BlockPlacerTraitBuilder(TraitConst.RACKER, 1, Blocks.NETHERRACK, 3));
        ret.add(new BlockPlacerTraitBuilder(TraitConst.REFRACTIVE, 1, ModBlocks.PHANTOM_LIGHT.get(), 5)
                .sound(SoundEvents.ENTITY_ITEM_PICKUP, 0.75f, 0.5f)
        );
        ret.add(new BlockPlacerTraitBuilder(TraitConst.TERMINUS, 1, Blocks.STONE, 3));

        // Misfits

        ret.add(new TraitBuilder(TraitConst.HOLY, 5, DamageTypeTrait.SERIALIZER)
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
