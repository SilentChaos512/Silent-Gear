package net.silentchaos512.gear.data.material;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.Tags;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.material.MaterialLayer;
import net.silentchaos512.gear.api.parts.PartType;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.api.stats.StatInstance;
import net.silentchaos512.gear.api.traits.ITraitCondition;
import net.silentchaos512.gear.client.model.PartTextures;
import net.silentchaos512.gear.crafting.ingredient.ExclusionIngredient;
import net.silentchaos512.gear.init.ModBlocks;
import net.silentchaos512.gear.init.ModTags;
import net.silentchaos512.gear.item.CraftingItems;
import net.silentchaos512.gear.parts.PartTextureType;
import net.silentchaos512.gear.traits.TraitConst;
import net.silentchaos512.gear.traits.conditions.GearTypeTraitCondition;
import net.silentchaos512.gear.traits.conditions.MaterialCountTraitCondition;
import net.silentchaos512.gear.traits.conditions.MaterialRatioTraitCondition;
import net.silentchaos512.gear.traits.conditions.OrTraitCondition;
import net.silentchaos512.gear.util.TextUtil;
import net.silentchaos512.utils.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class MaterialsProvider implements IDataProvider {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
    private final DataGenerator generator;

    public MaterialsProvider(DataGenerator generator) {
        this.generator = generator;
    }

    @Override
    public String getName() {
        return "Silent Gear - Materials";
    }

    @SuppressWarnings({"MethodMayBeStatic", "OverlyLongMethod"})
    protected Collection<MaterialBuilder> getMaterials() {
        ResourceLocation chargeability = new ResourceLocation("silentgems", "chargeability");

        Collection<MaterialBuilder> ret = new ArrayList<>();

        //region Base Materials

        // Barrier
        ret.add(new MaterialBuilder(SilentGear.getId("barrier"), 5, Items.BARRIER)
                .visible(false)
                .stat(PartType.MAIN, ItemStats.DURABILITY, 1337)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 84)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 5)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 0)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 5)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 1)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 1)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, 0f)
                .stat(PartType.MAIN, ItemStats.ARMOR, 20)
                .stat(PartType.MAIN, ItemStats.ARMOR_TOUGHNESS, 10)
                .stat(PartType.MAIN, ItemStats.MAGIC_ARMOR, 10)
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 1)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, 0f)
                .stat(PartType.MAIN, ItemStats.RARITY, 111)
                .stat(PartType.MAIN, chargeability, 0.5f)
                .trait(PartType.MAIN, TraitConst.ADAMANT, 5)
                .trait(PartType.MAIN, TraitConst.HOLY, 5)
                .name(new TranslationTextComponent(Items.BARRIER.getTranslationKey()))
                .display(PartType.MAIN, PartTextureType.HIGH_CONTRAST_WITH_HIGHLIGHT, 0xFF0000)
        );
        // Blackstone
        ret.add(new MaterialBuilder(SilentGear.getId("blackstone"), 1, Items.BLACKSTONE)
                .stat(PartType.MAIN, ItemStats.DURABILITY, 151)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 4)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 4)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 1)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 4)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 1)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 0)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, 0.0f)
                .stat(PartType.MAIN, ItemStats.ARMOR, 5)
                .stat(PartType.MAIN, ItemStats.ARMOR_TOUGHNESS, 0)
                .stat(PartType.MAIN, ItemStats.MAGIC_ARMOR, 0)
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 0)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, -0.2f)
                .stat(PartType.MAIN, ItemStats.RARITY, 8)
                .stat(PartType.MAIN, chargeability, 0.6f)
                .stat(PartType.ROD, ItemStats.DURABILITY, 0.1f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.HARVEST_SPEED, -0.1f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RARITY, 4)
                .trait(PartType.ROD, TraitConst.BRITTLE, 2)
                .trait(PartType.ROD, TraitConst.JAGGED, 2)
                .trait(PartType.ROD, TraitConst.HARD, 1)
                .display(PartType.MAIN, PartTextureType.LOW_CONTRAST, 0x3C3947)
                .display(PartType.ROD, PartTextureType.LOW_CONTRAST, 0x3C3947)
        );
        // Blaze Gold
        ret.add(new MaterialBuilder(SilentGear.getId("blaze_gold"), 3, ModTags.Items.INGOTS_BLAZE_GOLD)
                .stat(PartType.MAIN, ItemStats.DURABILITY, 69)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 9)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 24)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 2)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 15)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 2)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 5)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, 0.1f)
                .stat(PartType.MAIN, ItemStats.ARMOR, 13)
                .stat(PartType.MAIN, ItemStats.ARMOR_TOUGHNESS, 1)
                .stat(PartType.MAIN, ItemStats.MAGIC_ARMOR, 10)
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 1)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, 0.2f)
                .stat(PartType.MAIN, ItemStats.RARITY, 45)
                .stat(PartType.MAIN, chargeability, 1.2f)
                .stat(PartType.ROD, ItemStats.DURABILITY, 0.35f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.ENCHANTABILITY, 0.55f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.HARVEST_LEVEL, 2, StatInstance.Operation.MAX)
                .stat(PartType.ROD, ItemStats.HARVEST_SPEED, 0.1f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.MELEE_DAMAGE, 0.2f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.MAGIC_DAMAGE, 0.3f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RANGED_DAMAGE, 0.2f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RARITY, 55)
                .stat(PartType.TIP, ItemStats.DURABILITY, 32, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.ARMOR_DURABILITY, 3, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.HARVEST_LEVEL, 2, StatInstance.Operation.MAX)
                .stat(PartType.TIP, ItemStats.HARVEST_SPEED, 4, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.MELEE_DAMAGE, 1, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.MAGIC_DAMAGE, 1, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.RARITY, 14, StatInstance.Operation.ADD)
                .trait(PartType.MAIN, TraitConst.MALLEABLE, 3)
                .trait(PartType.MAIN, TraitConst.SOFT, 1)
                .trait(PartType.ROD, TraitConst.FLEXIBLE, 2, new MaterialRatioTraitCondition(0.5f))
                .trait(PartType.ROD, TraitConst.SYNERGISTIC, 2)
                .trait(PartType.TIP, TraitConst.SOFT, 2)
                .trait(PartType.TIP, TraitConst.FIERY, 4, new GearTypeTraitCondition(GearType.WEAPON))
                .display(PartType.MAIN, PartTextureType.HIGH_CONTRAST_WITH_HIGHLIGHT, 0xE48534)
                .display(PartType.ROD, PartTextureType.HIGH_CONTRAST, 0xE48534)
                .displayTip(PartTextures.TIP_SMOOTH, 0xE48534)
        );
        // Crimson Iron
        ret.add(new MaterialBuilder(SilentGear.getId("crimson_iron"), 3, ModTags.Items.INGOTS_CRIMSON_IRON)
                .stat(PartType.MAIN, ItemStats.DURABILITY, 420)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 27)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 14)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 3)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 10)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 3)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 3)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, -0.1f)
                .stat(PartType.MAIN, ItemStats.ARMOR, 18)
                .stat(PartType.MAIN, ItemStats.ARMOR_TOUGHNESS, 2)
                .stat(PartType.MAIN, ItemStats.MAGIC_ARMOR, 6)
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 2)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, -0.1f)
                .stat(PartType.MAIN, ItemStats.RARITY, 35)
                .stat(PartType.MAIN, chargeability, 0.7f)
                .stat(PartType.ROD, ItemStats.DURABILITY, 0.25f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.ENCHANTABILITY, 1, StatInstance.Operation.ADD)
                .stat(PartType.ROD, ItemStats.ENCHANTABILITY, 0.1f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.MELEE_DAMAGE, 0.1f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RARITY, 30)
                .stat(PartType.TIP, ItemStats.DURABILITY, 224, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.ARMOR_DURABILITY, 8, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.HARVEST_LEVEL, 3, StatInstance.Operation.MAX)
                .stat(PartType.TIP, ItemStats.HARVEST_SPEED, 2, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.MELEE_DAMAGE, 2, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.RARITY, 10, StatInstance.Operation.ADD)
                .trait(PartType.MAIN, TraitConst.MALLEABLE, 3)
                .trait(PartType.MAIN, TraitConst.HARD, 2)
                .trait(PartType.ROD, TraitConst.MALLEABLE, 3, new MaterialRatioTraitCondition(0.5f))
                .trait(PartType.TIP, TraitConst.FIERY, 1, new GearTypeTraitCondition(GearType.WEAPON))
                .display(PartType.MAIN, PartTextureType.HIGH_CONTRAST_WITH_HIGHLIGHT, 0xFF6189)
                .display(PartType.ROD, PartTextureType.LOW_CONTRAST, 0xFF6189)
                .displayTip(PartTextures.TIP_SHARP, 0xFF6189)
        );
        // Crimson Steel
        ret.add(new MaterialBuilder(SilentGear.getId("crimson_steel"), 3, ModTags.Items.INGOTS_CRIMSON_STEEL)
                .stat(PartType.MAIN, ItemStats.DURABILITY, 2400)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 42)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 19)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 4)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 15)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 6)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 6)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, -0.1f)
                .stat(PartType.MAIN, ItemStats.ARMOR, 22)
                .stat(PartType.MAIN, ItemStats.ARMOR_TOUGHNESS, 4)
                .stat(PartType.MAIN, ItemStats.MAGIC_ARMOR, 8)
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 3)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, -0.1f)
                .stat(PartType.MAIN, ItemStats.RARITY, 51)
                .stat(PartType.MAIN, chargeability, 0.9f)
                .stat(PartType.ROD, ItemStats.DURABILITY, 0.5f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.HARVEST_SPEED, 0.2f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RANGED_DAMAGE, 1, StatInstance.Operation.ADD)
                .stat(PartType.ROD, ItemStats.RARITY, 42)
                .stat(PartType.TIP, ItemStats.DURABILITY, 448, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.ARMOR_DURABILITY, 16, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.HARVEST_LEVEL, 4, StatInstance.Operation.MAX)
                .stat(PartType.TIP, ItemStats.RARITY, 20, StatInstance.Operation.ADD)
                .trait(PartType.MAIN, TraitConst.MALLEABLE, 5)
                .trait(PartType.MAIN, TraitConst.HARD, 3)
                .trait(PartType.MAIN, TraitConst.FLAME_WARD, 1,
                        new GearTypeTraitCondition(GearType.ARMOR),
                        materialCountOrRatio(3, 0.33f)
                )
                .trait(PartType.ROD, TraitConst.MALLEABLE, 5, new MaterialRatioTraitCondition(0.5f))
                .trait(PartType.TIP, TraitConst.MAGMATIC, 1, new GearTypeTraitCondition(GearType.HARVEST_TOOL))
                .display(PartType.MAIN, PartTextureType.HIGH_CONTRAST_WITH_HIGHLIGHT, 0xDC143C)
                .display(PartType.ROD, PartTextureType.LOW_CONTRAST, 0xDC143C)
                .displayTip(PartTextures.TIP_SHARP, 0xDC143C)
        );
        // Diamond
        ret.add(new MaterialBuilder(SilentGear.getId("diamond"), 3, Tags.Items.GEMS_DIAMOND)
                .stat(PartType.MAIN, ItemStats.DURABILITY, 1561)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 33)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 10)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 3)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 8)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 3)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 1)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, 0f)
                .stat(PartType.MAIN, ItemStats.ARMOR, 20)
                .stat(PartType.MAIN, ItemStats.ARMOR_TOUGHNESS, 8)
                .stat(PartType.MAIN, ItemStats.MAGIC_ARMOR, 4)
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 2)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, -0.2f)
                .stat(PartType.MAIN, ItemStats.RARITY, 70)
                .stat(PartType.MAIN, chargeability, 0.8f)
                .stat(PartType.ROD, ItemStats.DURABILITY, 0.2f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.HARVEST_SPEED, 0.2f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RARITY, 50)
                .stat(PartType.TIP, ItemStats.DURABILITY, 256, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.ARMOR_DURABILITY, 9, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.HARVEST_LEVEL, 4, StatInstance.Operation.MAX)
                .stat(PartType.TIP, ItemStats.HARVEST_SPEED, 2, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.MELEE_DAMAGE, 2, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.MAGIC_DAMAGE, 1, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.RANGED_DAMAGE, 0.5f, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.RARITY, 20, StatInstance.Operation.ADD)
                .trait(PartType.MAIN, TraitConst.BRITTLE, 2)
                .trait(PartType.MAIN, TraitConst.LUSTROUS, 1,
                        new GearTypeTraitCondition(GearType.HARVEST_TOOL),
                        materialCountOrRatio(3, 0.5f)
                )
                .trait(PartType.ROD, TraitConst.BRITTLE, 5, new MaterialRatioTraitCondition(0.5f))
                .trait(PartType.ROD, TraitConst.LUSTROUS, 4,
                        new GearTypeTraitCondition(GearType.HARVEST_TOOL),
                        new MaterialRatioTraitCondition(0.5f)
                )
                .trait(PartType.TIP, TraitConst.BRITTLE, 2)
                .trait(PartType.TIP, TraitConst.LUSTROUS, 2)
                .display(PartType.MAIN, PartTextureType.HIGH_CONTRAST_WITH_HIGHLIGHT, 0x33EBCB)
                .display(PartType.ROD, PartTextureType.HIGH_CONTRAST, 0x33EBCB)
                .displayTip(PartTextures.TIP_SHARP, 0x33EBCB)
        );
        // Emerald
        ret.add(new MaterialBuilder(SilentGear.getId("emerald"), 3, Tags.Items.GEMS_EMERALD)
                .stat(PartType.MAIN, ItemStats.DURABILITY, 1080)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 24)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 16)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 2)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 10)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 2)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 2)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, 0f)
                .stat(PartType.MAIN, ItemStats.ARMOR, 16)
                .stat(PartType.MAIN, ItemStats.ARMOR_TOUGHNESS, 4)
                .stat(PartType.MAIN, ItemStats.MAGIC_ARMOR, 6)
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 1)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, -0.1f)
                .stat(PartType.MAIN, ItemStats.RARITY, 40)
                .stat(PartType.MAIN, chargeability, 1.0f)
                .stat(PartType.ROD, ItemStats.DURABILITY, -0.2f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.HARVEST_SPEED, 0.3f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RARITY, 30)
                .stat(PartType.TIP, ItemStats.DURABILITY, 512, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.ARMOR_DURABILITY, 12, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.HARVEST_LEVEL, 2, StatInstance.Operation.MAX)
                .stat(PartType.TIP, ItemStats.HARVEST_SPEED, 2, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.MELEE_DAMAGE, 1, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.MAGIC_DAMAGE, 2, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.RANGED_DAMAGE, 1, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.RARITY, 20, StatInstance.Operation.ADD)
                .trait(PartType.MAIN, TraitConst.BRITTLE, 1)
                .trait(PartType.MAIN, TraitConst.SYNERGISTIC, 2)
                .trait(PartType.ROD, TraitConst.BRITTLE, 4, new MaterialRatioTraitCondition(0.5f))
                .trait(PartType.ROD, TraitConst.SYNERGISTIC, 3, new MaterialRatioTraitCondition(0.5f))
                .trait(PartType.TIP, TraitConst.BRITTLE, 1)
                .trait(PartType.TIP, TraitConst.SYNERGISTIC, 2)
                .display(PartType.MAIN, PartTextureType.HIGH_CONTRAST_WITH_HIGHLIGHT, 0x00B038)
                .display(PartType.ROD, PartTextureType.HIGH_CONTRAST, 0x00B038)
                .displayTip(PartTextures.TIP_SHARP, 0x00B038)
        );
        // End Stone
        ret.add(new MaterialBuilder(SilentGear.getId("end_stone"), 1, Tags.Items.END_STONES)
                .stat(PartType.MAIN, ItemStats.DURABILITY, 1164)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 15)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 10)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 2)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 7)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 2)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 3)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, 0.1f)
                .stat(PartType.MAIN, ItemStats.ARMOR, 15)
                .stat(PartType.MAIN, ItemStats.ARMOR_TOUGHNESS, 1)
                .stat(PartType.MAIN, ItemStats.MAGIC_ARMOR, 6)
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 0)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, 0f)
                .stat(PartType.MAIN, ItemStats.RARITY, 32)
                .stat(PartType.MAIN, chargeability, 0.9f)
                .stat(PartType.ROD, ItemStats.DURABILITY, 0.1f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.MELEE_DAMAGE, 1, StatInstance.Operation.ADD)
                .stat(PartType.ROD, ItemStats.RARITY, 26)
                .trait(PartType.MAIN, TraitConst.JAGGED, 3)
                .trait(PartType.MAIN, TraitConst.ANCIENT, 2)
                .trait(PartType.ROD, TraitConst.JAGGED, 2)
                .trait(PartType.ROD, TraitConst.ANCIENT, 4)
                .display(PartType.MAIN, PartTextureType.HIGH_CONTRAST_WITH_HIGHLIGHT, 0xFFFFCC)
                .display(PartType.ROD, PartTextureType.HIGH_CONTRAST, 0xFFFFCC)
        );
        // Example
        ret.add(new MaterialBuilder(SilentGear.getId("example"), 0, Ingredient.EMPTY)
                .visible(false)
                .blacklistGearType("all")
                .stat(PartType.MAIN, ItemStats.DURABILITY, 100)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 6)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 1)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 0)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 1)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 1)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 1)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, 0f)
                .stat(PartType.MAIN, ItemStats.ARMOR, 1)
                .stat(PartType.MAIN, ItemStats.ARMOR_TOUGHNESS, 1)
                .stat(PartType.MAIN, ItemStats.MAGIC_ARMOR, 1)
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 0)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, 0f)
                .stat(PartType.MAIN, ItemStats.RARITY, 0)
                .stat(PartType.MAIN, chargeability, 1f)
                .noStats(PartType.ROD)
                .noStats(PartType.TIP)
                .noStats(PartType.COATING)
                .noStats(PartType.GRIP)
                .noStats(PartType.BINDING)
                .noStats(PartType.BOWSTRING)
                .noStats(PartType.FLETCHING)
                .displayAll(PartTextureType.LOW_CONTRAST, Color.VALUE_WHITE)
        );
        // Feather
        ret.add(new MaterialBuilder(SilentGear.getId("feather"), 1, Tags.Items.FEATHERS)
                .noStats(PartType.FLETCHING)
                .displayAll(PartTextureType.LOW_CONTRAST, Color.VALUE_WHITE)
        );
        // Flax
        ret.add(new MaterialBuilder(SilentGear.getId("flax"), 1, CraftingItems.FLAX_STRING)
                .stat(PartType.BINDING, ItemStats.DURABILITY, -0.05f, StatInstance.Operation.MUL1)
                .stat(PartType.BINDING, ItemStats.DURABILITY, 10, StatInstance.Operation.ADD)
                .stat(PartType.BINDING, ItemStats.HARVEST_SPEED, 0.05f, StatInstance.Operation.MUL1)
                .trait(PartType.BINDING, TraitConst.FLEXIBLE, 2)
                .stat(PartType.BOWSTRING, ItemStats.DURABILITY, 0.05f, StatInstance.Operation.MUL1)
                .stat(PartType.BOWSTRING, ItemStats.RANGED_DAMAGE, -0.1f, StatInstance.Operation.MUL1)
                .stat(PartType.BOWSTRING, ItemStats.RANGED_SPEED, 0.2f, StatInstance.Operation.MUL1)
                .stat(PartType.BOWSTRING, ItemStats.RARITY, 6, StatInstance.Operation.ADD)
                .trait(PartType.BOWSTRING, TraitConst.SYNERGISTIC, 2)
                .display(PartType.BINDING, PartTextureType.LOW_CONTRAST, 0xB3804B)
                .displayBowstring(0x845E37)
        );
        // Flint
        ret.add(new MaterialBuilder(SilentGear.getId("flint"), 1, Items.FLINT)
                .stat(PartType.MAIN, ItemStats.DURABILITY, 124)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 4)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 3)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 1)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 5)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 2)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 0)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, -0.1f)
                .stat(PartType.MAIN, ItemStats.ARMOR, 4)
                .stat(PartType.MAIN, ItemStats.ARMOR_TOUGHNESS, 0)
                .stat(PartType.MAIN, ItemStats.MAGIC_ARMOR, 0)
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 1)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, -0.3f)
                .stat(PartType.MAIN, ItemStats.RARITY, 6)
                .stat(PartType.MAIN, chargeability, 0.8f)
                .stat(PartType.ROD, ItemStats.DURABILITY, -0.3f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RARITY, 2)
                .trait(PartType.MAIN, TraitConst.JAGGED, 3)
                .trait(PartType.ROD, TraitConst.BRITTLE, 3)
                .trait(PartType.ROD, TraitConst.JAGGED, 2)
                .display(PartType.MAIN, PartTextureType.HIGH_CONTRAST, 0x969696)
                .display(PartType.ROD, PartTextureType.HIGH_CONTRAST, 0x969696)
        );
        // Glowstone
        ret.add(new MaterialBuilder(SilentGear.getId("glowstone"), 2, Tags.Items.DUSTS_GLOWSTONE)
                .stat(PartType.TIP, ItemStats.HARVEST_SPEED, 0.4f, StatInstance.Operation.MUL2)
                .stat(PartType.TIP, ItemStats.MELEE_DAMAGE, 2, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.MAGIC_DAMAGE, 2, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.RANGED_SPEED, 0.3f, StatInstance.Operation.MUL2)
                .stat(PartType.TIP, ItemStats.RARITY, 15, StatInstance.Operation.ADD)
                .trait(PartType.TIP, TraitConst.REFRACTIVE, 1, new OrTraitCondition(
                        new GearTypeTraitCondition(GearType.HARVEST_TOOL), new GearTypeTraitCondition(GearType.MELEE_WEAPON)
                ))
                .trait(PartType.TIP, TraitConst.LUSTROUS, 4, new GearTypeTraitCondition(GearType.HARVEST_TOOL))
                .displayTip(PartTextures.TIP_SMOOTH, 0xD2D200)
        );
        // Gold
        ret.add(new MaterialBuilder(SilentGear.getId("gold"), 2, Tags.Items.INGOTS_GOLD)
                .stat(PartType.MAIN, ItemStats.DURABILITY, 32)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 7)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 22)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 0)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 12)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 0)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 4)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, 0.2f)
                .stat(PartType.MAIN, ItemStats.ARMOR, 11)
                .stat(PartType.MAIN, ItemStats.ARMOR_TOUGHNESS, 0)
                .stat(PartType.MAIN, ItemStats.MAGIC_ARMOR, 8)
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 0)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, 0.3f)
                .stat(PartType.MAIN, ItemStats.RARITY, 50)
                .stat(PartType.MAIN, chargeability, 1.2f)
                .stat(PartType.ROD, ItemStats.DURABILITY, 0.05f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.ENCHANTABILITY, 3, StatInstance.Operation.ADD)
                .stat(PartType.ROD, ItemStats.RARITY, 40)
                .stat(PartType.TIP, ItemStats.DURABILITY, 16, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.ARMOR_DURABILITY, 1, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.HARVEST_SPEED, 6, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.MAGIC_DAMAGE, 2, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.RANGED_SPEED, 0.2f, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.RARITY, 30, StatInstance.Operation.ADD)
                .trait(PartType.MAIN, TraitConst.MALLEABLE, 1)
                .trait(PartType.MAIN, TraitConst.SOFT, 3)
                .trait(PartType.ROD, TraitConst.MALLEABLE, 1, new MaterialRatioTraitCondition(0.5f))
                .trait(PartType.TIP, TraitConst.MALLEABLE, 1)
                .trait(PartType.TIP, TraitConst.SOFT, 3)
                .display(PartType.MAIN, PartTextureType.HIGH_CONTRAST_WITH_HIGHLIGHT, 0xEAEE57)
                .display(PartType.ROD, PartTextureType.LOW_CONTRAST, 0xEAEE57)
                .displayTip(PartTextures.TIP_SMOOTH, 0xEAEE57)
        );
        // Iron
        ret.add(new MaterialBuilder(SilentGear.getId("iron"), 2, Tags.Items.INGOTS_IRON)
                .stat(PartType.MAIN, ItemStats.DURABILITY, 250)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 15)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 14)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 2)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 6)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 2)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 1)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, -0.1f)
                .stat(PartType.MAIN, ItemStats.ARMOR, 15)
                .stat(PartType.MAIN, ItemStats.ARMOR_TOUGHNESS, 0)
                .stat(PartType.MAIN, ItemStats.MAGIC_ARMOR, 6)
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 1)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, 0.1f)
                .stat(PartType.MAIN, ItemStats.RARITY, 20)
                .stat(PartType.MAIN, chargeability, 0.7f)
                .stat(PartType.ROD, ItemStats.DURABILITY, 0.15f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.ENCHANTABILITY, -2, StatInstance.Operation.ADD)
                .stat(PartType.ROD, ItemStats.ENCHANTABILITY, -0.1f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.MELEE_DAMAGE, 0.1f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RARITY, 20)
                .stat(PartType.TIP, ItemStats.DURABILITY, 128, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.ARMOR_DURABILITY, 4, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.HARVEST_LEVEL, 2, StatInstance.Operation.MAX)
                .stat(PartType.TIP, ItemStats.HARVEST_SPEED, 1, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.MELEE_DAMAGE, 1, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.RANGED_SPEED, 0.2f, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.RARITY, 8, StatInstance.Operation.ADD)
                .trait(PartType.MAIN, TraitConst.MALLEABLE, 3)
                .trait(PartType.MAIN, TraitConst.MAGNETIC, 1, new MaterialRatioTraitCondition(0.66f))
                .trait(PartType.ROD, TraitConst.MAGNETIC, 3, new MaterialRatioTraitCondition(0.5f))
                .trait(PartType.TIP, TraitConst.MALLEABLE, 2)
                .display(PartType.MAIN, PartTextureType.HIGH_CONTRAST_WITH_HIGHLIGHT, Color.VALUE_WHITE)
                .display(PartType.ROD, PartTextureType.LOW_CONTRAST, 0xD8D8D8)
                .displayTip(PartTextures.TIP_SHARP, Color.VALUE_WHITE)
        );
        // Lapis Lazuli
        ret.add(new MaterialBuilder(SilentGear.getId("lapis_lazuli"), 2, Tags.Items.GEMS_LAPIS)
                .stat(PartType.TIP, ItemStats.ENCHANTABILITY, 0.5f, StatInstance.Operation.MUL2)
                .stat(PartType.TIP, ItemStats.HARVEST_SPEED, -0.1f, StatInstance.Operation.MUL2)
                .stat(PartType.TIP, ItemStats.MAGIC_DAMAGE, 2, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.ATTACK_SPEED, 0.3f, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.RARITY, 10, StatInstance.Operation.ADD)
                .trait(PartType.TIP, TraitConst.HOLY, 1, new OrTraitCondition(
                        new GearTypeTraitCondition(GearType.WEAPON),
                        new MaterialRatioTraitCondition(0.75f)
                ))
                .trait(PartType.TIP, TraitConst.LUCKY, 4, new OrTraitCondition(
                        new GearTypeTraitCondition(GearType.TOOL),
                        new GearTypeTraitCondition(GearType.WEAPON),
                        new MaterialRatioTraitCondition(0.75f)
                ))
                .displayTip(PartTextures.TIP_SMOOTH, 0x224BAF)
        );
        // Leather
        ret.add(new MaterialBuilder(SilentGear.getId("leather"), 0, Tags.Items.LEATHER)
                .stat(PartType.MAIN, ItemStats.DURABILITY, 0)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 5)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 15)
                .stat(PartType.MAIN, ItemStats.ARMOR, 6)
                .stat(PartType.MAIN, ItemStats.ARMOR_TOUGHNESS, 0)
                .stat(PartType.MAIN, ItemStats.MAGIC_ARMOR, 8)
                .stat(PartType.MAIN, ItemStats.RARITY, 11)
                .stat(PartType.MAIN, chargeability, 0.8f)
                .stat(PartType.GRIP, ItemStats.DURABILITY, 0.05f, StatInstance.Operation.MUL1)
                .stat(PartType.GRIP, ItemStats.REPAIR_EFFICIENCY, 0.1f, StatInstance.Operation.MUL1)
                .stat(PartType.GRIP, ItemStats.HARVEST_SPEED, 0.15f, StatInstance.Operation.MUL1)
                .stat(PartType.GRIP, ItemStats.ATTACK_SPEED, 0.15f, StatInstance.Operation.ADD)
                .stat(PartType.GRIP, ItemStats.RARITY, 5, StatInstance.Operation.ADD)
                .trait(PartType.GRIP, TraitConst.FLEXIBLE, 3)
                .displayAll(PartTextureType.LOW_CONTRAST, 0xC65C35)
        );
        // Leaves
        ret.add(new MaterialBuilder(SilentGear.getId("leaves"), 1, ItemTags.LEAVES)
                .noStats(PartType.FLETCHING)
                .displayAll(PartTextureType.LOW_CONTRAST, 0x4A8F28)
        );
        // Netherrack
        ret.add(new MaterialBuilder(SilentGear.getId("netherrack"), 1, Tags.Items.NETHERRACK)
                .stat(PartType.MAIN, ItemStats.DURABILITY, 198)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 8)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 8)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 1)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 5)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 0.5f)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 0.5f)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, -0.1f)
                .stat(PartType.MAIN, ItemStats.ARMOR, 8)
                .stat(PartType.MAIN, ItemStats.ARMOR_TOUGHNESS, 0)
                .stat(PartType.MAIN, ItemStats.MAGIC_ARMOR, 4)
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 0.5f)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, 0.1f)
                .stat(PartType.MAIN, ItemStats.RARITY, 11)
                .stat(PartType.MAIN, chargeability, 0.8f)
                .stat(PartType.ROD, ItemStats.DURABILITY, -0.2f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RARITY, 11)
                .trait(PartType.MAIN, TraitConst.ERODED, 3)
                .trait(PartType.MAIN, TraitConst.FLEXIBLE, 2)
                .trait(PartType.ROD, TraitConst.ERODED, 2)
                .trait(PartType.ROD, TraitConst.FLEXIBLE, 3)
                .display(PartType.MAIN, PartTextureType.HIGH_CONTRAST_WITH_HIGHLIGHT, 0x854242)
                .display(PartType.ROD, PartTextureType.LOW_CONTRAST, 0x854242)
        );
        // Netherite
        ret.add(new MaterialBuilder(SilentGear.getId("netherite"), 4, Items.NETHERITE_INGOT)
                .namePrefix(TextUtil.translate("material", "netherite"))
                .stat(PartType.COATING, ItemStats.DURABILITY, 0.3f, StatInstance.Operation.MUL2)
                .stat(PartType.COATING, ItemStats.DURABILITY, 2, StatInstance.Operation.ADD)
                .stat(PartType.COATING, ItemStats.HARVEST_SPEED, 0.125f, StatInstance.Operation.MUL2)
                .stat(PartType.COATING, ItemStats.HARVEST_LEVEL, 4, StatInstance.Operation.MAX)
                .stat(PartType.COATING, ItemStats.MELEE_DAMAGE, 1f / 3f, StatInstance.Operation.MUL2)
                .stat(PartType.COATING, ItemStats.MAGIC_DAMAGE, 1f / 3f, StatInstance.Operation.MUL2)
                .stat(PartType.COATING, ItemStats.KNOCKBACK_RESISTANCE, 0.1f, StatInstance.Operation.ADD)
                .stat(PartType.COATING, ItemStats.ENCHANTABILITY, 5, StatInstance.Operation.ADD)
                .displayCoating(PartTextureType.HIGH_CONTRAST_WITH_HIGHLIGHT, 0x867B86)
        );
        // Netherwood
        ret.add(new MaterialBuilder(SilentGear.getId("netherwood"), 0, ModBlocks.NETHERWOOD_PLANKS)
                .stat(PartType.MAIN, ItemStats.DURABILITY, 72)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 12)
                .stat(PartType.MAIN, ItemStats.REPAIR_EFFICIENCY, 0.5f)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 13)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 0)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 2)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 0)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 0)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, 0.2f)
                .stat(PartType.MAIN, ItemStats.ARMOR, 8)
                .stat(PartType.MAIN, ItemStats.ARMOR_TOUGHNESS, 0)
                .stat(PartType.MAIN, ItemStats.MAGIC_ARMOR, 0)
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 0)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, 0.0f)
                .stat(PartType.MAIN, ItemStats.RARITY, 4)
                .stat(PartType.MAIN, chargeability, 0.7f)
                .stat(PartType.ROD, ItemStats.DURABILITY, 70, StatInstance.Operation.MAX)
                .stat(PartType.ROD, ItemStats.DURABILITY, -50, StatInstance.Operation.ADD)
                .stat(PartType.ROD, ItemStats.HARVEST_SPEED, 1, StatInstance.Operation.ADD)
                .stat(PartType.ROD, ItemStats.MELEE_DAMAGE, 0.5f, StatInstance.Operation.ADD)
                .stat(PartType.ROD, ItemStats.RARITY, 3)
                .trait(PartType.MAIN, TraitConst.FLEXIBLE, 3)
                .trait(PartType.MAIN, TraitConst.JAGGED, 2)
                .trait(PartType.ROD, TraitConst.FLEXIBLE, 2)
                .display(PartType.MAIN, PartTextureType.LOW_CONTRAST, 0xD83200)
                .display(PartType.ROD, PartTextureType.LOW_CONTRAST, 0xD83200)
        );
        // Obsidian
        ret.add(new MaterialBuilder(SilentGear.getId("obsidian"), 3, Tags.Items.OBSIDIAN)
                .stat(PartType.MAIN, ItemStats.DURABILITY, 3072)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 37)
                .stat(PartType.MAIN, ItemStats.REPAIR_EFFICIENCY, -0.75f)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 7)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 3)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 6)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 3)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 2)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, -0.4f)
                .stat(PartType.MAIN, ItemStats.ARMOR, 20)
                .stat(PartType.MAIN, ItemStats.ARMOR_TOUGHNESS, 4)
                .stat(PartType.MAIN, ItemStats.MAGIC_ARMOR, 8)
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 0)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, -0.4f)
                .stat(PartType.MAIN, ItemStats.RARITY, 10)
                .stat(PartType.MAIN, chargeability, 0.6f)
                .stat(PartType.ROD, ItemStats.HARVEST_SPEED, -0.1f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RARITY, 5)
                .trait(PartType.MAIN, TraitConst.JAGGED, 3)
                .trait(PartType.MAIN, TraitConst.CRUSHING, 2)
                .trait(PartType.ROD, TraitConst.BRITTLE, 2)
                .trait(PartType.ROD, TraitConst.CHIPPING, 3)
                .display(PartType.MAIN, PartTextureType.LOW_CONTRAST, 0x443464)
                .display(PartType.ROD, PartTextureType.LOW_CONTRAST, 0x443464)
        );
        // Quartz
        ret.add(new MaterialBuilder(SilentGear.getId("quartz"), 2, Tags.Items.GEMS_QUARTZ)
                .stat(PartType.MAIN, ItemStats.DURABILITY, 330)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 13)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 10)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 2)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 7)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 2)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 0)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, 0.1f)
                .stat(PartType.MAIN, ItemStats.ARMOR, 14)
                .stat(PartType.MAIN, ItemStats.ARMOR_TOUGHNESS, 0)
                .stat(PartType.MAIN, ItemStats.MAGIC_ARMOR, 4)
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 0)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, 0.1f)
                .stat(PartType.MAIN, ItemStats.RARITY, 40)
                .stat(PartType.MAIN, chargeability, 1.2f)
                .stat(PartType.ROD, ItemStats.DURABILITY, -0.2f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.HARVEST_SPEED, 0.2f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.MELEE_DAMAGE, 0.1f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RARITY, 30)
                .stat(PartType.TIP, ItemStats.DURABILITY, 64, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.ARMOR_DURABILITY, 64, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.HARVEST_LEVEL, 2, StatInstance.Operation.MAX)
                .stat(PartType.TIP, ItemStats.HARVEST_SPEED, 2, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.MELEE_DAMAGE, 4, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.RANGED_DAMAGE, 1.5f, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.RARITY, 20, StatInstance.Operation.ADD)
                .trait(PartType.MAIN, TraitConst.CRUSHING, 3)
                .trait(PartType.MAIN, TraitConst.JAGGED, 2)
                .trait(PartType.ROD, TraitConst.BRITTLE, 2)
                .trait(PartType.TIP, TraitConst.CHIPPING, 1)
                .trait(PartType.TIP, TraitConst.JAGGED, 3)
                .display(PartType.MAIN, PartTextureType.HIGH_CONTRAST_WITH_HIGHLIGHT, 0xD4CABA)
                .display(PartType.ROD, PartTextureType.LOW_CONTRAST, 0xD4CABA)
                .displayTip(PartTextures.TIP_SHARP, 0xD4CABA)
        );
        // Redstone
        ret.add(new MaterialBuilder(SilentGear.getId("redstone"), 2, Tags.Items.DUSTS_REDSTONE)
                .stat(PartType.TIP, ItemStats.HARVEST_SPEED, 0.2f, StatInstance.Operation.MUL2)
                .stat(PartType.TIP, ItemStats.MELEE_DAMAGE, 2, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.ATTACK_SPEED, 0.5f, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.RANGED_DAMAGE, 2, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.RARITY, 10, StatInstance.Operation.ADD)
                .displayTip(PartTextures.TIP_SMOOTH, 0xBB0000)
        );
        // Sandstone
        ResourceLocation sgSandstone = SilentGear.getId("sandstone");
        ret.add(new MaterialBuilder(sgSandstone, 1, ExclusionIngredient.of(Tags.Items.SANDSTONE,
                Items.RED_SANDSTONE, Items.CHISELED_RED_SANDSTONE, Items.CUT_RED_SANDSTONE, Items.SMOOTH_RED_SANDSTONE))
                .stat(PartType.MAIN, ItemStats.DURABILITY, 117)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 6)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 7)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 1)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 4)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 1)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 0)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, 0.1f)
                .stat(PartType.MAIN, ItemStats.ARMOR, 5)
                .stat(PartType.MAIN, ItemStats.ARMOR_TOUGHNESS, 0)
                .stat(PartType.MAIN, ItemStats.MAGIC_ARMOR, 0)
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 0)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, -0.1f)
                .stat(PartType.MAIN, ItemStats.RARITY, 7)
                .stat(PartType.MAIN, chargeability, 0.7f)
                .stat(PartType.ROD, ItemStats.DURABILITY, -0.2f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.ENCHANTABILITY, -0.1f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RARITY, 7)
                .displayAll(PartTextureType.LOW_CONTRAST, 0xE3DBB0)
        );
        // Red sandstone
        ret.add(new MaterialBuilder(SilentGear.getId("sandstone/red"), -1, Ingredient.fromItems(
                Items.RED_SANDSTONE, Items.CHISELED_RED_SANDSTONE, Items.CUT_RED_SANDSTONE, Items.SMOOTH_RED_SANDSTONE))
                .parent(sgSandstone)
                .display(PartType.MAIN, PartTextureType.LOW_CONTRAST, 0xD97B30)
                .display(PartType.ROD, PartTextureType.LOW_CONTRAST, 0xD97B30)
        );
        // Sinew
        ret.add(new MaterialBuilder(SilentGear.getId("sinew"), 1, CraftingItems.SINEW_FIBER)
                .stat(PartType.BINDING, ItemStats.DURABILITY, 0.05f, StatInstance.Operation.MUL1)
                .stat(PartType.BINDING, ItemStats.REPAIR_EFFICIENCY, -0.05f, StatInstance.Operation.MUL1)
                .trait(PartType.BINDING, TraitConst.FLEXIBLE, 2)
                .stat(PartType.BOWSTRING, ItemStats.DURABILITY, 0.25f, StatInstance.Operation.MUL1)
                .stat(PartType.BOWSTRING, ItemStats.ENCHANTABILITY, -0.1f, StatInstance.Operation.MUL1)
                .stat(PartType.BOWSTRING, ItemStats.RANGED_DAMAGE, 0.2f, StatInstance.Operation.MUL1)
                .stat(PartType.BOWSTRING, ItemStats.RARITY, 8, StatInstance.Operation.ADD)
                .trait(PartType.BOWSTRING, TraitConst.FLEXIBLE, 3)
                .display(PartType.BINDING, PartTextureType.LOW_CONTRAST, 0xD8995B)
                .displayBowstring(0x7E6962)
        );

        // Stone
        ResourceLocation stone = SilentGear.getId("stone");
        ret.add(new MaterialBuilder(stone, 1, Tags.Items.COBBLESTONE)
                .stat(PartType.MAIN, ItemStats.DURABILITY, 131)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 5)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 5)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 1)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 4)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 1)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 0)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, 0.0f)
                .stat(PartType.MAIN, ItemStats.ARMOR, 5)
                .stat(PartType.MAIN, ItemStats.ARMOR_TOUGHNESS, 0)
                .stat(PartType.MAIN, ItemStats.MAGIC_ARMOR, 0)
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 0)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, -0.2f)
                .stat(PartType.MAIN, ItemStats.RARITY, 4)
                .stat(PartType.MAIN, chargeability, 0.5f)
                .stat(PartType.ROD, ItemStats.DURABILITY, -0.1f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.HARVEST_SPEED, 0.2f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RARITY, 2)
                .trait(PartType.MAIN, TraitConst.ANCIENT, 1)
                .trait(PartType.ROD, TraitConst.BRITTLE, 1)
                .trait(PartType.ROD, TraitConst.CRUSHING, 2)
                .display(PartType.MAIN, PartTextureType.LOW_CONTRAST, 0x9A9A9A)
                .display(PartType.ROD, PartTextureType.LOW_CONTRAST, 0x9A9A9A)
        );
        ret.add(new MaterialBuilder(SilentGear.getId("stone/andesite"), -1, Items.ANDESITE)
                .parent(stone)
                .display(PartType.MAIN, new MaterialLayer(PartTextures.MAIN_GENERIC_LC, 0x8A8A8E))
                .display(PartType.ROD, new MaterialLayer(PartTextures.ROD_GENERIC_LC, 0x8A8A8E))
        );
        ret.add(new MaterialBuilder(SilentGear.getId("stone/diorite"), -1, Items.DIORITE)
                .parent(stone)
                .display(PartType.MAIN, new MaterialLayer(PartTextures.MAIN_GENERIC_LC, 0xFFFFFF))
                .display(PartType.ROD, new MaterialLayer(PartTextures.ROD_GENERIC_LC, 0xFFFFFF))
        );
        ret.add(new MaterialBuilder(SilentGear.getId("stone/granite"), -1, Items.GRANITE)
                .parent(stone)
                .display(PartType.MAIN, new MaterialLayer(PartTextures.MAIN_GENERIC_LC, 0x9F6B58))
                .display(PartType.ROD, new MaterialLayer(PartTextures.ROD_GENERIC_LC, 0x9F6B58))
        );

        // String
        ret.add(new MaterialBuilder(SilentGear.getId("string"), 0, ExclusionIngredient.of(Tags.Items.STRING,
                CraftingItems.FLAX_STRING, CraftingItems.SINEW_FIBER))
                .stat(PartType.BINDING, ItemStats.DURABILITY, -0.05f, StatInstance.Operation.MUL1)
                .stat(PartType.BINDING, ItemStats.REPAIR_EFFICIENCY, 0.05f, StatInstance.Operation.MUL1)
                .trait(PartType.BINDING, TraitConst.FLEXIBLE, 2)
                .stat(PartType.BOWSTRING, ItemStats.RANGED_SPEED, 0.1f, StatInstance.Operation.MUL1)
                .stat(PartType.BOWSTRING, ItemStats.RARITY, 4, StatInstance.Operation.ADD)
                .trait(PartType.BOWSTRING, TraitConst.ORGANIC, 2)
                .display(PartType.BINDING, PartTextureType.LOW_CONTRAST, 0xFFFFFF)
                .displayBowstring(0x444444)
        );
        // Terracotta
        ResourceLocation sgTerracotta = SilentGear.getId("terracotta");
        ret.add(new MaterialBuilder(sgTerracotta, 1, Items.TERRACOTTA)
                .stat(PartType.MAIN, ItemStats.DURABILITY, 165)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 11)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 9)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 1)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 4)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 1.5f)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 0)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, 0.2f)
                .stat(PartType.MAIN, ItemStats.ARMOR, 9)
                .stat(PartType.MAIN, ItemStats.ARMOR_TOUGHNESS, 0)
                .stat(PartType.MAIN, ItemStats.MAGIC_ARMOR, 3)
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 0)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, -0.2f)
                .stat(PartType.MAIN, ItemStats.RARITY, 7)
                .stat(PartType.ROD, ItemStats.DURABILITY, -0.1f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.ENCHANTABILITY, -0.1f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.MELEE_DAMAGE, 0.1f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RARITY, 6)
                .trait(PartType.MAIN, TraitConst.BRITTLE, 1)
                .trait(PartType.MAIN, TraitConst.CHIPPING, 2)
                .trait(PartType.MAIN, TraitConst.RUSTIC, 1)
                .trait(PartType.ROD, TraitConst.BRITTLE, 2)
                .trait(PartType.ROD, TraitConst.CRUSHING, 1)
                .trait(PartType.ROD, TraitConst.RUSTIC, 2)
                .displayAll(PartTextureType.LOW_CONTRAST, 0x985F45)
        );
        ret.add(terracotta(sgTerracotta, "black", Items.BLACK_TERRACOTTA, 0x251610));
        ret.add(terracotta(sgTerracotta, "blue", Items.BLUE_TERRACOTTA, 0x4A3B5B));
        ret.add(terracotta(sgTerracotta, "brown", Items.BROWN_TERRACOTTA, 0x4D3224));
        ret.add(terracotta(sgTerracotta, "cyan", Items.CYAN_TERRACOTTA, 0xD1B1A1));
        ret.add(terracotta(sgTerracotta, "gray", Items.GRAY_TERRACOTTA, 0xD1B1A1));
        ret.add(terracotta(sgTerracotta, "green", Items.GREEN_TERRACOTTA, 0x4B522A));
        ret.add(terracotta(sgTerracotta, "light_blue", Items.LIGHT_BLUE_TERRACOTTA, 0x706C8A));
        ret.add(terracotta(sgTerracotta, "light_gray", Items.LIGHT_GRAY_TERRACOTTA, 0x876A61));
        ret.add(terracotta(sgTerracotta, "lime", Items.LIME_TERRACOTTA, 0x677534));
        ret.add(terracotta(sgTerracotta, "magenta", Items.MAGENTA_TERRACOTTA, 0x95576C));
        ret.add(terracotta(sgTerracotta, "orange", Items.ORANGE_TERRACOTTA, 0xA05325));
        ret.add(terracotta(sgTerracotta, "pink", Items.PINK_TERRACOTTA, 0xA04D4E));
        ret.add(terracotta(sgTerracotta, "purple", Items.PURPLE_TERRACOTTA, 0x764556));
        ret.add(terracotta(sgTerracotta, "red", Items.RED_TERRACOTTA, 0x8E3C2E));
        ret.add(terracotta(sgTerracotta, "white", Items.WHITE_TERRACOTTA, 0xD1B1A1));
        ret.add(terracotta(sgTerracotta, "yellow", Items.YELLOW_TERRACOTTA, 0xB98423));

        // Wood
        ResourceLocation sgWood = SilentGear.getId("wood");
        ret.add(new MaterialBuilder(sgWood, 0, ExclusionIngredient.of(ItemTags.PLANKS,
                Items.ACACIA_PLANKS, Items.BIRCH_PLANKS, Items.DARK_OAK_PLANKS, Items.JUNGLE_PLANKS, Items.OAK_PLANKS, Items.SPRUCE_PLANKS, ModBlocks.NETHERWOOD_PLANKS, Items.CRIMSON_PLANKS, Items.WARPED_PLANKS))
                .stat(PartType.MAIN, ItemStats.DURABILITY, 59)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 9)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 15)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 0)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 2)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 0)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 0)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, 0f)
                .stat(PartType.MAIN, ItemStats.ARMOR, 7)
                .stat(PartType.MAIN, ItemStats.ARMOR_TOUGHNESS, 0)
                .stat(PartType.MAIN, ItemStats.MAGIC_ARMOR, 1)
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 0)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, 0f)
                .stat(PartType.MAIN, ItemStats.RARITY, 1)
                .stat(PartType.MAIN, chargeability, 0.6f)
                .stat(PartType.ROD, ItemStats.ENCHANTABILITY, 0.1f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RARITY, 1)
                .trait(PartType.MAIN, TraitConst.FLAMMABLE, 1)
                .trait(PartType.MAIN, TraitConst.FLEXIBLE, 2)
                .trait(PartType.MAIN, TraitConst.JAGGED, 1)
                .trait(PartType.ROD, TraitConst.FLEXIBLE, 1)
                .displayAll(PartTextureType.LOW_CONTRAST, 0x896727)
        );
        ret.add(wood(sgWood, "acacia", Items.ACACIA_PLANKS, 0xBA6337));
        ret.add(wood(sgWood, "birch", Items.BIRCH_PLANKS, 0xD7C185));
        ret.add(wood(sgWood, "dark_oak", Items.DARK_OAK_PLANKS, 0x4F3218));
        ret.add(wood(sgWood, "jungle", Items.JUNGLE_PLANKS, 0xB88764));
        ret.add(wood(sgWood, "oak", Items.OAK_PLANKS, 0xB8945F));
        ret.add(wood(sgWood, "spruce", Items.SPRUCE_PLANKS, 0x82613A));
        ret.add(wood(sgWood, "crimson", Items.CRIMSON_PLANKS, 0x7E3A56));
        ret.add(wood(sgWood, "warped", Items.WARPED_PLANKS, 0x398382));
        // Wool
        ResourceLocation sgWool = SilentGear.getId("wool");
        ret.add(new MaterialBuilder(sgWool, 0, ExclusionIngredient.of(ItemTags.WOOL,
                Items.BLACK_WOOL, Items.BLUE_WOOL, Items.BROWN_WOOL, Items.CYAN_WOOL, Items.GRAY_WOOL, Items.GREEN_WOOL, Items.LIGHT_BLUE_WOOL, Items.LIGHT_GRAY_WOOL, Items.LIME_WOOL, Items.MAGENTA_WOOL, Items.ORANGE_WOOL, Items.PINK_WOOL, Items.PURPLE_WOOL, Items.RED_WOOL, Items.WHITE_WOOL, Items.YELLOW_WOOL))
                .stat(PartType.GRIP, ItemStats.REPAIR_EFFICIENCY, 0.2f, StatInstance.Operation.MUL1)
                .stat(PartType.GRIP, ItemStats.HARVEST_SPEED, 0.1f, StatInstance.Operation.MUL1)
                .stat(PartType.GRIP, ItemStats.ATTACK_SPEED, 0.2f, StatInstance.Operation.ADD)
                .stat(PartType.GRIP, ItemStats.RARITY, 4, StatInstance.Operation.ADD)
                .trait(PartType.GRIP, TraitConst.SYNERGISTIC, 1)
                .displayAll(PartTextureType.LOW_CONTRAST, Color.VALUE_WHITE)
        );
        ret.add(wool(sgWool, "black", Items.BLACK_WOOL, 0x141519));
        ret.add(wool(sgWool, "blue", Items.BLUE_WOOL, 0x35399D));
        ret.add(wool(sgWool, "brown", Items.BROWN_WOOL, 0x724728));
        ret.add(wool(sgWool, "cyan", Items.CYAN_WOOL, 0x158991));
        ret.add(wool(sgWool, "gray", Items.GRAY_WOOL, 0x3E4447));
        ret.add(wool(sgWool, "green", Items.GREEN_WOOL, 0x546D1B));
        ret.add(wool(sgWool, "light_blue", Items.LIGHT_BLUE_WOOL, 0x3AAFD9));
        ret.add(wool(sgWool, "light_gray", Items.LIGHT_GRAY_WOOL, 0x8E8E86));
        ret.add(wool(sgWool, "lime", Items.LIME_WOOL, 0x70B919));
        ret.add(wool(sgWool, "magenta", Items.MAGENTA_WOOL, 0xBD44B3));
        ret.add(wool(sgWool, "orange", Items.ORANGE_WOOL, 0xF07613));
        ret.add(wool(sgWool, "pink", Items.PINK_WOOL, 0xED8DAC));
        ret.add(wool(sgWool, "purple", Items.PURPLE_WOOL, 0x792AAC));
        ret.add(wool(sgWool, "red", Items.RED_WOOL, 0xA12722));
        ret.add(wool(sgWool, "white", Items.WHITE_WOOL, 0xE9ECEC));
        ret.add(wool(sgWool, "yellow", Items.YELLOW_WOOL, 0xF8C627));

        //endregion

        //region Extra Mod Metals
        // Aluminum
        ret.add(extraMetal("aluminum", 2, forgeId("ingots/aluminum"))
                .stat(PartType.MAIN, ItemStats.DURABILITY, 365)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 15)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 14)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 2)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 8)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 2)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 2)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, 0.2f)
                .stat(PartType.MAIN, ItemStats.ARMOR, 14)
                .stat(PartType.MAIN, ItemStats.ARMOR_TOUGHNESS, 0.5f)
                .stat(PartType.MAIN, ItemStats.MAGIC_ARMOR, 3)
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 2)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, 0.2f)
                .stat(PartType.MAIN, ItemStats.RARITY, 25)
                .stat(PartType.MAIN, chargeability, 0.9f)
                .stat(PartType.ROD, ItemStats.DURABILITY, 0.2f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RANGED_DAMAGE, 0.1f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RARITY, 30)
                .trait(PartType.MAIN, TraitConst.MALLEABLE, 3)
                .trait(PartType.MAIN, TraitConst.SOFT, 2, new MaterialRatioTraitCondition(0.5f))
                .trait(PartType.MAIN, TraitConst.SYNERGISTIC, 1)
                .trait(PartType.ROD, TraitConst.MALLEABLE, 3)
                .trait(PartType.ROD, TraitConst.SYNERGISTIC, 2)
                .displayAll(PartTextureType.HIGH_CONTRAST_WITH_HIGHLIGHT, 0xBFD4DE)
        );
        // Aluminum Steel
        ret.add(extraMetal("aluminum_steel", 3, forgeId("ingots/aluminum_steel"))
                .stat(PartType.MAIN, ItemStats.DURABILITY, 660)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 18)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 11)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 3)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 8)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 4)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 1.5f)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, 0.1f)
                .stat(PartType.MAIN, ItemStats.ARMOR, 18)
                .stat(PartType.MAIN, ItemStats.ARMOR_TOUGHNESS, 2)
                .stat(PartType.MAIN, ItemStats.MAGIC_ARMOR, 6)
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 2)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, 0f)
                .stat(PartType.MAIN, ItemStats.RARITY, 45)
                .stat(PartType.MAIN, chargeability, 1.0f)
                .stat(PartType.ROD, ItemStats.DURABILITY, 0.3f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.HARVEST_SPEED, 0.1f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RARITY, 45)
                .trait(PartType.MAIN, TraitConst.MALLEABLE, 4)
                .trait(PartType.MAIN, TraitConst.SYNERGISTIC, 2, new MaterialRatioTraitCondition(0.5f))
                .trait(PartType.ROD, TraitConst.MALLEABLE, 3)
                .trait(PartType.ROD, TraitConst.SYNERGISTIC, 3)
                .displayAll(PartTextureType.HIGH_CONTRAST_WITH_HIGHLIGHT, 0x98D9DA)
        );
        // Bismuth
        ret.add(extraMetal("bismuth", 2, forgeId("ingots/bismuth"))
                .stat(PartType.MAIN, ItemStats.DURABILITY, 330)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 10)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 12)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 2)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 5)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 2.5f)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 3.5f)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, 0.1f)
                .stat(PartType.MAIN, ItemStats.ARMOR, 16)
                .stat(PartType.MAIN, ItemStats.ARMOR_TOUGHNESS, 2)
                .stat(PartType.MAIN, ItemStats.MAGIC_ARMOR, 6)
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 1)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, 0f)
                .stat(PartType.MAIN, ItemStats.RARITY, 35)
                .stat(PartType.MAIN, chargeability, 0.9f)
                .stat(PartType.ROD, ItemStats.DURABILITY, 0.1f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.HARVEST_SPEED, 0.1f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RARITY, 35)
                .trait(PartType.MAIN, TraitConst.MALLEABLE, 2)
                .trait(PartType.MAIN, TraitConst.LUSTROUS, 2, new MaterialRatioTraitCondition(0.5f))
                .trait(PartType.ROD, TraitConst.MALLEABLE, 2)
                .displayAll(PartTextureType.HIGH_CONTRAST_WITH_HIGHLIGHT, 0xD1C2D5)
        );
        // Bismuth Brass
        ret.add(extraMetal("bismuth_brass", 2, forgeId("ingots/bismuth_brass"))
                .stat(PartType.MAIN, ItemStats.DURABILITY, 580)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 15)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 14)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 2)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 9)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 3f)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 2f)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, 0.1f)
                .stat(PartType.MAIN, ItemStats.ARMOR, 18)
                .stat(PartType.MAIN, ItemStats.ARMOR_TOUGHNESS, 2)
                .stat(PartType.MAIN, ItemStats.MAGIC_ARMOR, 8)
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 2)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, 0.2f)
                .stat(PartType.MAIN, ItemStats.RARITY, 50)
                .stat(PartType.MAIN, chargeability, 1.1f)
                .stat(PartType.ROD, ItemStats.DURABILITY, -0.1f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.HARVEST_SPEED, 0.2f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RARITY, 50)
                .trait(PartType.MAIN, TraitConst.MALLEABLE, 2)
                .trait(PartType.MAIN, TraitConst.LUSTROUS, 2, new MaterialRatioTraitCondition(0.5f))
                .trait(PartType.ROD, TraitConst.MALLEABLE, 2)
                .displayAll(PartTextureType.HIGH_CONTRAST_WITH_HIGHLIGHT, 0xE9C1B4)
        );
        // Bismuth Brass
        ret.add(extraMetal("bismuth_steel", 3, forgeId("ingots/bismuth_steel"))
                .stat(PartType.MAIN, ItemStats.DURABILITY, 1050)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 25)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 14)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 3)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 10)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 4f)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 5f)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, -0.1f)
                .stat(PartType.MAIN, ItemStats.ARMOR, 20)
                .stat(PartType.MAIN, ItemStats.ARMOR_TOUGHNESS, 3)
                .stat(PartType.MAIN, ItemStats.MAGIC_ARMOR, 8)
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 3)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, 0.1f)
                .stat(PartType.MAIN, ItemStats.RARITY, 60)
                .stat(PartType.MAIN, chargeability, 1.0f)
                .stat(PartType.ROD, ItemStats.DURABILITY, 0.25f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.HARVEST_SPEED, 0.15f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RARITY, 60)
                .trait(PartType.MAIN, TraitConst.MALLEABLE, 3)
                .trait(PartType.MAIN, TraitConst.LUSTROUS, 2, new MaterialRatioTraitCondition(0.5f))
                .trait(PartType.ROD, TraitConst.MALLEABLE, 3)
                .displayAll(PartTextureType.HIGH_CONTRAST_WITH_HIGHLIGHT, 0xDC9FE7)
        );
        // Brass
        ret.add(extraMetal("brass", 2, forgeId("ingots/brass"))
                .stat(PartType.MAIN, ItemStats.DURABILITY, 240)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 8)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 13)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 2)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 7)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 1.5f)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 1.5f)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, 0.1f)
                .stat(PartType.MAIN, ItemStats.ARMOR, 14)
                .stat(PartType.MAIN, ItemStats.ARMOR_TOUGHNESS, 1)
                .stat(PartType.MAIN, ItemStats.MAGIC_ARMOR, 6)
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 2)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, 0.2f)
                .stat(PartType.MAIN, ItemStats.RARITY, 25)
                .stat(PartType.MAIN, chargeability, 1.2f)
                .stat(PartType.ROD, ItemStats.DURABILITY, 0.05f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.HARVEST_SPEED, 0.05f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RARITY, 25)
                .trait(PartType.MAIN, TraitConst.MALLEABLE, 2)
                .trait(PartType.MAIN, TraitConst.SILKY, 1, new MaterialRatioTraitCondition(0.66f))
                .trait(PartType.ROD, TraitConst.MALLEABLE, 2)
                .displayAll(PartTextureType.HIGH_CONTRAST_WITH_HIGHLIGHT, 0xF2D458)
        );
        // Bronze
        ret.add(extraMetal("bronze", 2, forgeId("ingots/bronze"))
                .stat(PartType.MAIN, ItemStats.DURABILITY, 480)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 18)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 12)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 2)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 6)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 2.5f)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 1f)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, 0.2f)
                .stat(PartType.MAIN, ItemStats.ARMOR, 17)
                .stat(PartType.MAIN, ItemStats.ARMOR_TOUGHNESS, 1)
                .stat(PartType.MAIN, ItemStats.MAGIC_ARMOR, 6)
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 2)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, -0.2f)
                .stat(PartType.MAIN, ItemStats.RARITY, 35)
                .stat(PartType.MAIN, chargeability, 1.2f)
                .stat(PartType.ROD, ItemStats.DURABILITY, 0.15f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.MELEE_DAMAGE, 0.05f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RARITY, 35)
                .trait(PartType.MAIN, TraitConst.MALLEABLE, 3)
                .trait(PartType.MAIN, TraitConst.ADAMANT, 1,
                        new GearTypeTraitCondition(GearType.ARMOR),
                        new MaterialRatioTraitCondition(0.35f))
                .trait(PartType.ROD, TraitConst.MALLEABLE, 3)
                .displayAll(PartTextureType.HIGH_CONTRAST_WITH_HIGHLIGHT, 0xD96121)
        );
        // Compressed Iron
        ret.add(extraMetal("compressed_iron", 3, forgeId("ingots/compressed_iron"))
                .stat(PartType.MAIN, ItemStats.DURABILITY, 1024)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 24)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 12)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 3)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 9)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 4f)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 1f)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, -0.2f)
                .stat(PartType.MAIN, ItemStats.ARMOR, 20)
                .stat(PartType.MAIN, ItemStats.ARMOR_TOUGHNESS, 2)
                .stat(PartType.MAIN, ItemStats.MAGIC_ARMOR, 8)
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 3)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, 0.0f)
                .stat(PartType.MAIN, ItemStats.RARITY, 40)
                .stat(PartType.MAIN, chargeability, 0.8f)
                .stat(PartType.ROD, ItemStats.DURABILITY, 0.25f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RARITY, 40)
                .trait(PartType.MAIN, TraitConst.MALLEABLE, 2)
                .trait(PartType.MAIN, TraitConst.HARD, 3)
                .trait(PartType.ROD, TraitConst.MALLEABLE, 2)
                .trait(PartType.ROD, TraitConst.MAGNETIC, 4,
                        new GearTypeTraitCondition(GearType.TOOL),
                        new MaterialRatioTraitCondition(0.5f))
                .displayAll(PartTextureType.HIGH_CONTRAST_WITH_HIGHLIGHT, 0xA6A6A6)
        );
        // Copper
        ret.add(extraMetal("copper", 1, forgeId("ingots/copper"))
                .stat(PartType.MAIN, ItemStats.DURABILITY, 128)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 8)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 15)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 1)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 4)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 1f)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 2f)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, 0.2f)
                .stat(PartType.MAIN, ItemStats.ARMOR, 10)
                .stat(PartType.MAIN, ItemStats.ARMOR_TOUGHNESS, 0)
                .stat(PartType.MAIN, ItemStats.MAGIC_ARMOR, 5)
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 0)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, 0.2f)
                .stat(PartType.MAIN, ItemStats.RARITY, 15)
                .stat(PartType.MAIN, chargeability, 1.3f)
                .stat(PartType.ROD, ItemStats.DURABILITY, -0.1f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.HARVEST_SPEED, 0.2f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RARITY, 20)
                .trait(PartType.MAIN, TraitConst.MALLEABLE, 2)
                .trait(PartType.MAIN, TraitConst.SOFT, 1, new MaterialRatioTraitCondition(0.5f))
                .trait(PartType.ROD, TraitConst.SOFT, 3)
                .displayAll(PartTextureType.HIGH_CONTRAST_WITH_HIGHLIGHT, 0xFD804C)
        );
        // Electrum
        ret.add(extraMetal("electrum", 2, forgeId("ingots/electrum"))
                .stat(PartType.MAIN, ItemStats.DURABILITY, 96)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 10)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 25)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 2)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 14)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 2f)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 5f)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, 0.3f)
                .stat(PartType.MAIN, ItemStats.ARMOR, 15)
                .stat(PartType.MAIN, ItemStats.ARMOR_TOUGHNESS, 0)
                .stat(PartType.MAIN, ItemStats.MAGIC_ARMOR, 11)
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 1)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, 0.4f)
                .stat(PartType.MAIN, ItemStats.RARITY, 40)
                .stat(PartType.MAIN, chargeability, 1.5f)
                .stat(PartType.ROD, ItemStats.ATTACK_SPEED, 0.1f, StatInstance.Operation.ADD)
                .stat(PartType.ROD, ItemStats.RANGED_SPEED, 0.1f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RARITY, 40)
                .trait(PartType.MAIN, TraitConst.MALLEABLE, 2)
                .trait(PartType.MAIN, TraitConst.SOFT, 2, new MaterialRatioTraitCondition(0.5f))
                .trait(PartType.ROD, TraitConst.LUSTROUS, 3, new MaterialRatioTraitCondition(0.5f))
                .displayAll(PartTextureType.HIGH_CONTRAST_WITH_HIGHLIGHT, 0xD6E037)
        );
        // Enderium
        ret.add(extraMetal("enderium", 4, forgeId("ingots/enderium"))
                .stat(PartType.MAIN, ItemStats.DURABILITY, 1200)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 34)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 13)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 4)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 18)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 6f)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 4f)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, 0f)
                .stat(PartType.MAIN, ItemStats.ARMOR, 22)
                .stat(PartType.MAIN, ItemStats.ARMOR_TOUGHNESS, 4)
                .stat(PartType.MAIN, ItemStats.MAGIC_ARMOR, 8)
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 3)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, 0f)
                .stat(PartType.MAIN, ItemStats.RARITY, 80)
                .stat(PartType.MAIN, chargeability, 1.2f)
                .stat(PartType.ROD, ItemStats.DURABILITY, 0.2f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.HARVEST_SPEED, 0.2f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RARITY, 60)
                .trait(PartType.MAIN, TraitConst.MALLEABLE, 3)
                .trait(PartType.ROD, TraitConst.MALLEABLE, 2)
                .displayAll(PartTextureType.HIGH_CONTRAST_WITH_HIGHLIGHT, 0x468C75)
        );
        // Invar
        ret.add(extraMetal("invar", 2, forgeId("ingots/invar"))
                .stat(PartType.MAIN, ItemStats.DURABILITY, 640)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 20)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 13)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 2)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 7)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 3.5f)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 3f)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, 0f)
                .stat(PartType.MAIN, ItemStats.ARMOR, 18)
                .stat(PartType.MAIN, ItemStats.ARMOR_TOUGHNESS, 1)
                .stat(PartType.MAIN, ItemStats.MAGIC_ARMOR, 6)
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 2)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, 0f)
                .stat(PartType.MAIN, ItemStats.RARITY, 50)
                .stat(PartType.MAIN, chargeability, 1.2f)
                .stat(PartType.ROD, ItemStats.DURABILITY, 0.3f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.MELEE_DAMAGE, 0.1f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RARITY, 50)
                .trait(PartType.MAIN, TraitConst.MALLEABLE, 3)
                .trait(PartType.MAIN, TraitConst.ADAMANT, 2,
                        materialCountOrRatio(3, 0.35f),
                        new GearTypeTraitCondition(GearType.ARMOR))
                .trait(PartType.ROD, TraitConst.MALLEABLE, 3)
                .displayAll(PartTextureType.HIGH_CONTRAST_WITH_HIGHLIGHT, 0xC2CBB8)
        );
        // Lead
        ret.add(extraMetal("lead", 2, forgeId("ingots/lead"))
                .stat(PartType.MAIN, ItemStats.DURABILITY, 260)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 14)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 15)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 2)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 4)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 2f)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 2f)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, -0.4f)
                .stat(PartType.MAIN, ItemStats.ARMOR, 13)
                .stat(PartType.MAIN, ItemStats.ARMOR_TOUGHNESS, 0)
                .stat(PartType.MAIN, ItemStats.MAGIC_ARMOR, 3)
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 1)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, -0.3f)
                .stat(PartType.MAIN, ItemStats.RARITY, 40)
                .stat(PartType.MAIN, chargeability, 0.8f)
                .stat(PartType.ROD, ItemStats.DURABILITY, 0.1f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.HARVEST_SPEED, -0.1f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RARITY, 40)
                .trait(PartType.MAIN, TraitConst.MALLEABLE, 2)
                .trait(PartType.MAIN, TraitConst.AQUATIC, 2,
                        materialCountOrRatio(3, 0.35f),
                        new GearTypeTraitCondition(GearType.ARMOR))
                .trait(PartType.ROD, TraitConst.SOFT, 4)
                .displayAll(PartTextureType.HIGH_CONTRAST_WITH_HIGHLIGHT, 0xC2CBB8)
        );
        // Lumium
        ret.add(extraMetal("lumium", 3, forgeId("ingots/lumium"))
                .stat(PartType.MAIN, ItemStats.DURABILITY, 920)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 20)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 14)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 3)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 15)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 4f)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 3f)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, 0.2f)
                .stat(PartType.MAIN, ItemStats.ARMOR, 18)
                .stat(PartType.MAIN, ItemStats.ARMOR_TOUGHNESS, 3)
                .stat(PartType.MAIN, ItemStats.MAGIC_ARMOR, 3)
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 2)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, 0f)
                .stat(PartType.MAIN, ItemStats.RARITY, 75)
                .stat(PartType.MAIN, chargeability, 1.3f)
                .stat(PartType.ROD, ItemStats.HARVEST_SPEED, 0.2f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RANGED_DAMAGE, 0.1f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RARITY, 75)
                .trait(PartType.MAIN, TraitConst.MALLEABLE, 3)
                .trait(PartType.MAIN, TraitConst.REFRACTIVE, 1,
                        new MaterialRatioTraitCondition(0.5f),
                        new GearTypeTraitCondition(GearType.TOOL))
                .trait(PartType.ROD, TraitConst.MALLEABLE, 3)
                .trait(PartType.ROD, TraitConst.REFRACTIVE, 1,
                        new MaterialRatioTraitCondition(0.5f),
                        new GearTypeTraitCondition(GearType.TOOL))
                .displayAll(PartTextureType.HIGH_CONTRAST_WITH_HIGHLIGHT, 0x468C75)
        );
        // Nickel
        ret.add(extraMetal("nickel", 2, forgeId("ingots/nickel"))
                .stat(PartType.MAIN, ItemStats.DURABILITY, 380)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 17)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 12)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 2)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 7)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 2.5f)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 1f)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, 0.1f)
                .stat(PartType.MAIN, ItemStats.ARMOR, 13)
                .stat(PartType.MAIN, ItemStats.ARMOR_TOUGHNESS, 0)
                .stat(PartType.MAIN, ItemStats.MAGIC_ARMOR, 6)
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 0.5f)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, 0.1f)
                .stat(PartType.MAIN, ItemStats.RARITY, 40)
                .stat(PartType.MAIN, chargeability, 1.0f)
                .stat(PartType.ROD, ItemStats.DURABILITY, 0.2f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.MELEE_DAMAGE, 0.1f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RARITY, 40)
                .trait(PartType.MAIN, TraitConst.MALLEABLE, 3)
                .trait(PartType.MAIN, TraitConst.ADAMANT, 1,
                        materialCountOrRatio(3, 0.35f),
                        new GearTypeTraitCondition(GearType.ARMOR))
                .trait(PartType.ROD, TraitConst.MALLEABLE, 3)
                .displayAll(PartTextureType.HIGH_CONTRAST_WITH_HIGHLIGHT, 0xEFE87B)
        );
        // Osmium
        ret.add(extraMetal("osmium", 2, forgeId("ingots/osmium"))
                .stat(PartType.MAIN, ItemStats.DURABILITY, 500)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 14)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 10)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 2)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 7)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 4f)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 2f)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, 0.1f)
                .stat(PartType.MAIN, ItemStats.ARMOR, 17)
                .stat(PartType.MAIN, ItemStats.ARMOR_TOUGHNESS, 0)
                .stat(PartType.MAIN, ItemStats.MAGIC_ARMOR, 5)
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 1f)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, 0f)
                .stat(PartType.MAIN, ItemStats.RARITY, 30)
                .stat(PartType.MAIN, chargeability, 1.1f)
                .stat(PartType.ROD, ItemStats.DURABILITY, 0.1f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.REPAIR_EFFICIENCY, 0.1f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RARITY, 30)
                .trait(PartType.MAIN, TraitConst.MALLEABLE, 2)
                .trait(PartType.ROD, TraitConst.MALLEABLE, 2)
                .displayAll(PartTextureType.HIGH_CONTRAST_WITH_HIGHLIGHT, 0x92A6B8)
        );
        // Platinum
        ret.add(extraMetal("platinum", 3, forgeId("ingots/platinum"))
                .stat(PartType.MAIN, ItemStats.DURABILITY, 900)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 21)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 14)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 3)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 12)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 4f)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 4f)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, 0.0f)
                .stat(PartType.MAIN, ItemStats.ARMOR, 18)
                .stat(PartType.MAIN, ItemStats.ARMOR_TOUGHNESS, 2)
                .stat(PartType.MAIN, ItemStats.MAGIC_ARMOR, 12)
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 1f)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, 0f)
                .stat(PartType.MAIN, ItemStats.RARITY, 80)
                .stat(PartType.MAIN, chargeability, 1.2f)
                .stat(PartType.ROD, ItemStats.DURABILITY, -0.1f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.REPAIR_EFFICIENCY, 0.1f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RARITY, 70)
                .trait(PartType.MAIN, TraitConst.MALLEABLE, 3)
                .trait(PartType.MAIN, TraitConst.SOFT, 2)
                .trait(PartType.ROD, TraitConst.MALLEABLE, 2)
                .trait(PartType.ROD, TraitConst.SOFT, 4)
                .displayAll(PartTextureType.HIGH_CONTRAST_WITH_HIGHLIGHT, 0xB3B3FF)
        );
        // Redstone Alloy
        ret.add(extraMetal("redstone_alloy", 2, forgeId("ingots/redstone_alloy"))
                .stat(PartType.MAIN, ItemStats.DURABILITY, 840)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 20)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 18)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 2)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 11)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 2f)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 4f)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, 0.2f)
                .stat(PartType.MAIN, ItemStats.ARMOR, 17)
                .stat(PartType.MAIN, ItemStats.ARMOR_TOUGHNESS, 1)
                .stat(PartType.MAIN, ItemStats.MAGIC_ARMOR, 10)
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 1)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, 0.2f)
                .stat(PartType.MAIN, ItemStats.RARITY, 45)
                .stat(PartType.MAIN, chargeability, 0.9f)
                .stat(PartType.ROD, ItemStats.HARVEST_SPEED, 0.2f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RARITY, 45)
                .trait(PartType.MAIN, TraitConst.MALLEABLE, 3)
                .trait(PartType.MAIN, TraitConst.ERODED, 3)
                .trait(PartType.ROD, TraitConst.MALLEABLE, 3)
                .displayAll(PartTextureType.HIGH_CONTRAST_WITH_HIGHLIGHT, 0xE60006)
        );
        // Refined Iron
        ret.add(extraMetal("refined_iron", 2, forgeId("ingots/refined_iron"))
                .stat(PartType.MAIN, ItemStats.DURABILITY, 512)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 20)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 15)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 2)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 7)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 3f)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 2f)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, 0.0f)
                .stat(PartType.MAIN, ItemStats.ARMOR, 18)
                .stat(PartType.MAIN, ItemStats.ARMOR_TOUGHNESS, 0)
                .stat(PartType.MAIN, ItemStats.MAGIC_ARMOR, 7)
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 2)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, 0.0f)
                .stat(PartType.MAIN, ItemStats.RARITY, 25)
                .stat(PartType.MAIN, chargeability, 0.8f)
                .stat(PartType.ROD, ItemStats.DURABILITY, 0.15f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RANGED_DAMAGE, 0.1f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RARITY, 25)
                .trait(PartType.MAIN, TraitConst.MALLEABLE, 4)
                .trait(PartType.MAIN, TraitConst.STELLAR, 1)
                .trait(PartType.ROD, TraitConst.MALLEABLE, 4)
                .trait(PartType.ROD, TraitConst.MAGNETIC, 3,
                        new GearTypeTraitCondition(GearType.TOOL),
                        new MaterialRatioTraitCondition(0.35f))
                .displayAll(PartTextureType.HIGH_CONTRAST_WITH_HIGHLIGHT, 0xD7D7D7)
        );
        // Signalum
        ret.add(extraMetal("signalum", 4, forgeId("ingots/signalum"))
                .stat(PartType.MAIN, ItemStats.DURABILITY, 800)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 25)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 16)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 3)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 13)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 3f)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 4f)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, 0.0f)
                .stat(PartType.MAIN, ItemStats.ARMOR, 16)
                .stat(PartType.MAIN, ItemStats.ARMOR_TOUGHNESS, 2)
                .stat(PartType.MAIN, ItemStats.MAGIC_ARMOR, 4)
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 2f)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, 0.2f)
                .stat(PartType.MAIN, ItemStats.RARITY, 50)
                .stat(PartType.MAIN, chargeability, 1.2f)
                .stat(PartType.ROD, ItemStats.HARVEST_SPEED, 0.3f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.REPAIR_EFFICIENCY, -0.1f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RARITY, 50)
                .trait(PartType.MAIN, TraitConst.FLEXIBLE, 2)
                .trait(PartType.MAIN, TraitConst.LUSTROUS, 4)
                .trait(PartType.ROD, TraitConst.FLEXIBLE, 4)
                .trait(PartType.ROD, TraitConst.LUSTROUS, 2)
                .displayAll(PartTextureType.HIGH_CONTRAST_WITH_HIGHLIGHT, 0x468C75)
        );
        // Silver
        ret.add(extraMetal("silver", 2, forgeId("ingots/silver"))
                .stat(PartType.MAIN, ItemStats.DURABILITY, 64)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 8)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 20)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 0)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 11)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 0f)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 4f)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, 0.2f)
                .stat(PartType.MAIN, ItemStats.ARMOR, 12)
                .stat(PartType.MAIN, ItemStats.ARMOR_TOUGHNESS, 0)
                .stat(PartType.MAIN, ItemStats.MAGIC_ARMOR, 10)
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 0f)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, 0.3f)
                .stat(PartType.MAIN, ItemStats.RARITY, 40)
                .stat(PartType.MAIN, chargeability, 1.1f)
                .stat(PartType.ROD, ItemStats.HARVEST_SPEED, 0.1f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.MAGIC_DAMAGE, 0.1f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RARITY, 40)
                .trait(PartType.MAIN, TraitConst.MALLEABLE, 2)
                .trait(PartType.MAIN, TraitConst.SOFT, 1)
                .trait(PartType.ROD, TraitConst.MALLEABLE, 1)
                .trait(PartType.ROD, TraitConst.SOFT, 2)
                .displayAll(PartTextureType.HIGH_CONTRAST_WITH_HIGHLIGHT, 0xCBCCEA)
        );
        // Steel
        ret.add(extraMetal("steel", 2, forgeId("ingots/steel"))
                .stat(PartType.MAIN, ItemStats.DURABILITY, 500)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 20)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 11)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 2)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 6)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 4f)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 1f)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, -0.2f)
                .stat(PartType.MAIN, ItemStats.ARMOR, 20)
                .stat(PartType.MAIN, ItemStats.ARMOR_TOUGHNESS, 2)
                .stat(PartType.MAIN, ItemStats.MAGIC_ARMOR, 6)
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 2f)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, -0.2f)
                .stat(PartType.MAIN, ItemStats.RARITY, 40)
                .stat(PartType.MAIN, chargeability, 0.8f)
                .stat(PartType.ROD, ItemStats.DURABILITY, 0.25f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RARITY, 40)
                .trait(PartType.MAIN, TraitConst.MALLEABLE, 5)
                .trait(PartType.ROD, TraitConst.MALLEABLE, 5)
                .displayAll(PartTextureType.HIGH_CONTRAST_WITH_HIGHLIGHT, 0x929292)
        );
        // Tin
        ret.add(extraMetal("tin", 1, forgeId("ingots/tin"))
                .stat(PartType.MAIN, ItemStats.DURABILITY, 192)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 13)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 12)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 1)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 5)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 1.5f)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 1f)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, 0.0f)
                .stat(PartType.MAIN, ItemStats.ARMOR, 12)
                .stat(PartType.MAIN, ItemStats.ARMOR_TOUGHNESS, 0)
                .stat(PartType.MAIN, ItemStats.MAGIC_ARMOR, 1)
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 0.5f)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, 0f)
                .stat(PartType.MAIN, ItemStats.RARITY, 15)
                .stat(PartType.MAIN, chargeability, 1.1f)
                .stat(PartType.ROD, ItemStats.ATTACK_SPEED, 0.2f, StatInstance.Operation.ADD)
                .stat(PartType.ROD, ItemStats.RARITY, 15)
                .trait(PartType.MAIN, TraitConst.MALLEABLE, 1)
                .trait(PartType.MAIN, TraitConst.SOFT, 2)
                .trait(PartType.ROD, TraitConst.MALLEABLE, 2)
                .trait(PartType.ROD, TraitConst.SOFT, 1)
                .displayAll(PartTextureType.HIGH_CONTRAST_WITH_HIGHLIGHT, 0x89A5B4)
        );
        // Titanium
        ret.add(extraMetal("titanium", 4, forgeId("ingots/titanium"))
                .stat(PartType.MAIN, ItemStats.DURABILITY, 1600)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 37)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 12)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 4)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 8)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 6f)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 1f)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, 0.0f)
                .stat(PartType.MAIN, ItemStats.ARMOR, 24)
                .stat(PartType.MAIN, ItemStats.ARMOR_TOUGHNESS, 4)
                .stat(PartType.MAIN, ItemStats.MAGIC_ARMOR, 4)
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 1f)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, -0.2f)
                .stat(PartType.MAIN, ItemStats.RARITY, 80)
                .stat(PartType.MAIN, chargeability, 1.0f)
                .stat(PartType.ROD, ItemStats.DURABILITY, 0.1f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.MELEE_DAMAGE, 0.2f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.MELEE_DAMAGE, 2, StatInstance.Operation.ADD)
                .stat(PartType.ROD, ItemStats.RARITY, 80)
                .trait(PartType.MAIN, TraitConst.MALLEABLE, 2)
                .trait(PartType.MAIN, TraitConst.HARD, 4)
                .trait(PartType.ROD, TraitConst.FLEXIBLE, 2)
                .trait(PartType.ROD, TraitConst.HARD, 4)
                .displayAll(PartTextureType.HIGH_CONTRAST_WITH_HIGHLIGHT, 0x2E4CE6)
        );
        // Uranium
        ret.add(extraMetal("uranium", 3, forgeId("ingots/uranium"))
                .stat(PartType.MAIN, ItemStats.DURABILITY, 800)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 20)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 17)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 3)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 6)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 2f)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 2f)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, 0.1f)
                .stat(PartType.MAIN, ItemStats.ARMOR, 13)
                .stat(PartType.MAIN, ItemStats.ARMOR_TOUGHNESS, 1)
                .stat(PartType.MAIN, ItemStats.MAGIC_ARMOR, 3)
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 0.5f)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, 0.1f)
                .stat(PartType.MAIN, ItemStats.RARITY, 50)
                .stat(PartType.MAIN, chargeability, 1.5f)
                .stat(PartType.ROD, ItemStats.HARVEST_SPEED, 0.1f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RANGED_SPEED, 0.1f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RARITY, 40)
                .trait(PartType.MAIN, TraitConst.MALLEABLE, 2)
                .trait(PartType.ROD, TraitConst.MALLEABLE, 2)
                .displayAll(PartTextureType.HIGH_CONTRAST_WITH_HIGHLIGHT, 0x21FF0F)
        );
        // Zinc
        ret.add(extraMetal("zinc", 1, forgeId("ingots/zinc"))
                .stat(PartType.MAIN, ItemStats.DURABILITY, 192)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 10)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 15)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 1)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 3)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 1.5f)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 1f)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, 0.0f)
                .stat(PartType.MAIN, ItemStats.ARMOR, 11)
                .stat(PartType.MAIN, ItemStats.ARMOR_TOUGHNESS, 0)
                .stat(PartType.MAIN, ItemStats.MAGIC_ARMOR, 1)
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 0f)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, 0.0f)
                .stat(PartType.MAIN, ItemStats.RARITY, 10)
                .stat(PartType.MAIN, chargeability, 1.1f)
                .stat(PartType.ROD, ItemStats.DURABILITY, -0.05f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.MELEE_DAMAGE, 0.05f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RARITY, 15)
                .trait(PartType.MAIN, TraitConst.MALLEABLE, 1)
                .trait(PartType.MAIN, TraitConst.SOFT, 2)
                .trait(PartType.ROD, TraitConst.MALLEABLE, 2)
                .trait(PartType.ROD, TraitConst.SOFT, 3)
                .displayAll(PartTextureType.HIGH_CONTRAST_WITH_HIGHLIGHT, 0xC9D3CE)
        );
        //endregion

        return ret;
    }

    private static MaterialBuilder extraMetal(String name, int tier, ResourceLocation tag) {
        return new MaterialBuilder(SilentGear.getId(name), tier, tag).loadConditionTagExists(tag);
    }

    private static MaterialBuilder terracotta(ResourceLocation parent, String suffix, IItemProvider item, int color) {
        return new MaterialBuilder(new ResourceLocation(parent.getNamespace(), parent.getPath() + "/" + suffix), -1, item)
                .parent(parent)
                .display(PartType.MAIN, PartTextureType.LOW_CONTRAST, color)
                .display(PartType.ROD, PartTextureType.LOW_CONTRAST, color);
    }

    private static MaterialBuilder wood(ResourceLocation parent, String suffix, IItemProvider item, int color) {
        return new MaterialBuilder(new ResourceLocation(parent.getNamespace(), parent.getPath() + "/" + suffix), -1, item)
                .parent(parent)
                .display(PartType.MAIN, PartTextureType.LOW_CONTRAST, color)
                .display(PartType.ROD, PartTextureType.LOW_CONTRAST, color);
    }

    private static MaterialBuilder wool(ResourceLocation parent, String suffix, IItemProvider item, int color) {
        return new MaterialBuilder(new ResourceLocation(parent.getNamespace(), parent.getPath() + "/" + suffix), -1, item)
                .parent(parent)
                .display(PartType.GRIP, PartTextureType.LOW_CONTRAST, color);
    }

    @SuppressWarnings({"WeakerAccess", "SameParameterValue"})
    protected static ITraitCondition materialCountOrRatio(int count, float ratio) {
        return new OrTraitCondition(new MaterialCountTraitCondition(count), new MaterialRatioTraitCondition(ratio));
    }

    private static ResourceLocation forgeId(String path) {
        return new ResourceLocation("forge", path);
    }

    @Override
    public void act(DirectoryCache cache) {
        Path outputFolder = this.generator.getOutputFolder();

        for (MaterialBuilder builder : getMaterials()) {
            try {
                String jsonStr = GSON.toJson(builder.serialize());
                String hashStr = HASH_FUNCTION.hashUnencodedChars(jsonStr).toString();
                Path path = outputFolder.resolve(String.format("data/%s/silentgear_materials/%s.json", builder.id.getNamespace(), builder.id.getPath()));
                if (!Objects.equals(cache.getPreviousHash(outputFolder), hashStr) || !Files.exists(path)) {
                    Files.createDirectories(path.getParent());

                    try (BufferedWriter writer = Files.newBufferedWriter(path)) {
                        writer.write(jsonStr);
                    }
                }

                cache.recordHash(path, hashStr);
            } catch (IOException ex) {
                LOGGER.error("Could not save materials to {}", outputFolder, ex);
            }

            // Model data
            try {
                String jsonStr = GSON.toJson(builder.serializeModel());
                String hashStr = HASH_FUNCTION.hashUnencodedChars(jsonStr).toString();
                Path path = outputFolder.resolve(String.format("assets/%s/silentgear_materials/%s.json", builder.id.getNamespace(), builder.id.getPath()));
                if (!Objects.equals(cache.getPreviousHash(outputFolder), hashStr) || !Files.exists(path)) {
                    Files.createDirectories(path.getParent());

                    try (BufferedWriter writer = Files.newBufferedWriter(path)) {
                        writer.write(jsonStr);
                    }
                }

                cache.recordHash(path, hashStr);
            } catch (IOException ex) {
                LOGGER.error("Could not save material models to {}", outputFolder, ex);
            }
        }
    }
}
