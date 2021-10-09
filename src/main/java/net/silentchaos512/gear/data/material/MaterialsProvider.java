package net.silentchaos512.gear.data.material;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.Tags;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.material.MaterialLayer;
import net.silentchaos512.gear.api.material.StaticLayer;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.api.stats.StatInstance;
import net.silentchaos512.gear.api.traits.ITraitCondition;
import net.silentchaos512.gear.client.model.PartTextures;
import net.silentchaos512.gear.crafting.ingredient.CustomCompoundIngredient;
import net.silentchaos512.gear.gear.material.MaterialCategories;
import net.silentchaos512.gear.gear.material.MaterialSerializers;
import net.silentchaos512.gear.gear.part.PartTextureSet;
import net.silentchaos512.gear.gear.trait.condition.MaterialCountTraitCondition;
import net.silentchaos512.gear.gear.trait.condition.MaterialRatioTraitCondition;
import net.silentchaos512.gear.gear.trait.condition.NotTraitCondition;
import net.silentchaos512.gear.gear.trait.condition.OrTraitCondition;
import net.silentchaos512.gear.init.ModBlocks;
import net.silentchaos512.gear.init.ModItems;
import net.silentchaos512.gear.init.ModTags;
import net.silentchaos512.gear.item.CraftingItems;
import net.silentchaos512.gear.item.CustomMaterialItem;
import net.silentchaos512.gear.util.Const;
import net.silentchaos512.gear.util.TextUtil;
import net.silentchaos512.lib.crafting.ingredient.ExclusionIngredient;
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

public class MaterialsProvider implements DataProvider {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();

    private final DataGenerator generator;
    private final String modId;

    @Deprecated
    public MaterialsProvider(DataGenerator generator) {
        this(generator, SilentGear.MOD_ID);
    }

    public MaterialsProvider(DataGenerator generator, String modId) {
        this.generator = generator;
        this.modId = modId;
    }

    @Override
    public String getName() {
        return "Silent Gear - Materials";
    }

    protected Collection<MaterialBuilder> getMaterials() {
        Collection<MaterialBuilder> ret = new ArrayList<>();

        addCraftedMaterials(ret);

        addIntangibles(ret);
        addModMetals(ret);
        addVanillaMetals(ret);
        addGems(ret);
        addDusts(ret);
        addStones(ret);
        addWoods(ret);

        addClothLikes(ret);
        addStringsAndFibers(ret);
        addRandomOrganics(ret);

        addCompounds(ret);
        addSimpleRods(ret);

        addExtraMetals(ret);

        return ret;
    }

    //region Material builders

    private void addCraftedMaterials(Collection<MaterialBuilder> ret) {
        ret.add(new MaterialBuilder(modId("sheet_metal"), 0, ModItems.SHEET_METAL)
                .type(MaterialSerializers.CRAFTED, false)
                .categories(MaterialCategories.SHEET)
                .stat(PartType.MAIN, ItemStats.DURABILITY, -1, StatInstance.Operation.MUL2)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, -0.4f, StatInstance.Operation.MUL2)
                .stat(PartType.MAIN, ItemStats.ARMOR, -0.5f, StatInstance.Operation.MUL2)
                .stat(PartType.MAIN, ItemStats.ARMOR_TOUGHNESS, -0.5f, StatInstance.Operation.MUL2)
                .noModels()
        );
    }

    private void addIntangibles(Collection<MaterialBuilder> ret) {
        // Barrier
        ret.add(new MaterialBuilder(modId("barrier"), 5, Items.BARRIER)
                .categories(MaterialCategories.INTANGIBLE)
                .visible(false)
                .canSalvage(false)
                .stat(PartType.MAIN, ItemStats.DURABILITY, 1337)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 84)
                .stat(PartType.MAIN, ItemStats.REPAIR_VALUE, -1f)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 5)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 0)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 5)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 1)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 1)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, 0.0f)
                .stat(PartType.MAIN, ItemStats.ARMOR, 20)
                .stat(PartType.MAIN, ItemStats.ARMOR_TOUGHNESS, 10)
                .stat(PartType.MAIN, ItemStats.MAGIC_ARMOR, 10)
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 1)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, 0f)
                .stat(PartType.MAIN, ItemStats.RARITY, 111)
                .stat(PartType.MAIN, ItemStats.CHARGEABILITY, 0.5f)
                .trait(PartType.MAIN, Const.Traits.ADAMANT, 5)
                .trait(PartType.MAIN, Const.Traits.HOLY, 5)
                .name(new TranslatableComponent(Items.BARRIER.getDescriptionId()))
                .display(PartType.MAIN, PartTextureSet.HIGH_CONTRAST_WITH_HIGHLIGHT, 0xFF0000)
        );

        // Example
        ret.add(new MaterialBuilder(modId("example"), 0, Ingredient.EMPTY)
                .categories(MaterialCategories.INTANGIBLE)
                .visible(false)
                .canSalvage(false)
                .blacklistGearType("all")
                .stat(PartType.MAIN, ItemStats.DURABILITY, 100)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 6)
                .stat(PartType.MAIN, ItemStats.REPAIR_VALUE, -1f)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 1)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 0)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 1)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 1)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 1)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, 0.0f)
                .stat(PartType.MAIN, ItemStats.ARMOR, 1)
                .stat(PartType.MAIN, ItemStats.ARMOR_TOUGHNESS, 1)
                .stat(PartType.MAIN, ItemStats.MAGIC_ARMOR, 1)
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 0)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, 0f)
                .stat(PartType.MAIN, ItemStats.RARITY, 0)
                .stat(PartType.MAIN, ItemStats.CHARGEABILITY, 1f)
                .noStats(PartType.ROD)
                .noStats(PartType.TIP)
                .noStats(PartType.COATING)
                .noStats(PartType.GRIP)
                .noStats(PartType.BINDING)
                .noStats(PartType.LINING)
                .noStats(PartType.BOWSTRING)
                .noStats(PartType.FLETCHING)
                .noStats(PartType.ADORNMENT)
                .displayAll(PartTextureSet.LOW_CONTRAST, Color.VALUE_WHITE)
        );
    }

    private void addModMetals(Collection<MaterialBuilder> ret) {
        // Azure Electrum
        ret.add(new MaterialBuilder(modId("azure_electrum"), 4, ModTags.Items.INGOTS_AZURE_ELECTRUM)
                .categories(MaterialCategories.METAL)
                .stat(PartType.MAIN, ItemStats.DURABILITY, 1259)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 61)
                .stat(PartType.MAIN, ItemStats.REPAIR_VALUE, 0.5f)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 37)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 5)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 29)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 7)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 11)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, 0.0f)
                .mainStatsArmor(3, 7, 6, 3, 8, 19) //19
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 3)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, 0.0f)
                .stat(PartType.MAIN, ItemStats.PROJECTILE_SPEED, 2f)
                .stat(PartType.MAIN, ItemStats.PROJECTILE_ACCURACY, 1.5f)
                .stat(PartType.MAIN, ItemStats.RARITY, 109)
                .stat(PartType.MAIN, ItemStats.CHARGEABILITY, 1.5f)
                .stat(PartType.ROD, ItemStats.DURABILITY, -0.2f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.HARVEST_SPEED, 5, StatInstance.Operation.ADD)
                .stat(PartType.ROD, ItemStats.PROJECTILE_SPEED, 1.5f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RARITY, 67)
                .stat(PartType.TIP, ItemStats.DURABILITY, 401, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.ARMOR_DURABILITY, 11, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.HARVEST_SPEED, 5, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.MAGIC_DAMAGE, 3, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.ATTACK_SPEED, 0.3f, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.RARITY, 41, StatInstance.Operation.ADD)
                .trait(PartType.MAIN, Const.Traits.MALLEABLE, 5)
                .trait(PartType.MAIN, Const.Traits.ACCELERATE, 3, new MaterialRatioTraitCondition(0.35f))
                .trait(PartType.MAIN, Const.Traits.LIGHT, 4, new MaterialRatioTraitCondition(0.5f))
                .trait(PartType.ROD, Const.Traits.FLEXIBLE, 2)
                .trait(PartType.ROD, Const.Traits.ACCELERATE, 5, new MaterialRatioTraitCondition(0.66f))
                .trait(PartType.TIP, Const.Traits.MALLEABLE, 3)
                //.trait(PartType.TIP, Const.Traits.ACCELERATE, 2)
                .display(PartType.MAIN, PartTextureSet.HIGH_CONTRAST_WITH_HIGHLIGHT, 0x4575E3)
                .display(PartType.ROD, PartTextureSet.LOW_CONTRAST, 0x4575E3)
                .displayTip(PartTextures.TIP_SHARP, 0x4575E3)
        );
        // Azure Silver
        ret.add(new MaterialBuilder(modId("azure_silver"), 3, ModTags.Items.INGOTS_AZURE_SILVER)
                .categories(MaterialCategories.METAL)
                .stat(PartType.MAIN, ItemStats.DURABILITY, 197)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 17)
                .stat(PartType.MAIN, ItemStats.REPAIR_VALUE, 0.5f)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 29)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 3)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 19)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 5)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 7)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, 0.0f)
                .mainStatsArmor(2, 5, 4, 2, 0, 13) //13
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 2)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, 0.0f)
                .stat(PartType.MAIN, ItemStats.PROJECTILE_SPEED, 1.2f)
                .stat(PartType.MAIN, ItemStats.PROJECTILE_ACCURACY, 1.1f)
                .stat(PartType.MAIN, ItemStats.RARITY, 83)
                .stat(PartType.MAIN, ItemStats.CHARGEABILITY, 1.4f)
                .stat(PartType.ROD, ItemStats.DURABILITY, -0.2f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.HARVEST_SPEED, 3, StatInstance.Operation.ADD)
                .stat(PartType.ROD, ItemStats.RARITY, 47)
                .stat(PartType.TIP, ItemStats.DURABILITY, 83, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.ARMOR_DURABILITY, 3, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.HARVEST_SPEED, 3, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.MAGIC_DAMAGE, 2, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.ATTACK_SPEED, 0.2f, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.RARITY, 31, StatInstance.Operation.ADD)
                .trait(PartType.MAIN, Const.Traits.MALLEABLE, 3)
                .trait(PartType.MAIN, Const.Traits.SOFT, 2)
                .trait(PartType.MAIN, Const.Traits.MOONWALKER, 4, new MaterialRatioTraitCondition(0.5f))
                .trait(PartType.ROD, Const.Traits.FLEXIBLE, 1)
                .trait(PartType.TIP, Const.Traits.MALLEABLE, 3)
                .trait(PartType.TIP, Const.Traits.SOFT, 2)
                .display(PartType.MAIN, PartTextureSet.HIGH_CONTRAST_WITH_HIGHLIGHT, 0xCBBAFF)
                .display(PartType.ROD, PartTextureSet.LOW_CONTRAST, 0xCBBAFF)
                .displayTip(PartTextures.TIP_SHARP, 0xCBBAFF)
        );
        // Blaze Gold
        ret.add(new MaterialBuilder(modId("blaze_gold"), 3, ModTags.Items.INGOTS_BLAZE_GOLD)
                .categories(MaterialCategories.METAL)
                .stat(PartType.MAIN, ItemStats.DURABILITY, 69)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 9)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 24)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 2)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 15)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 2)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 5)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, 0.1f)
                .mainStatsArmor(2, 5, 4, 2, 1, 10) //13
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 1)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, 0.2f)
                .stat(PartType.MAIN, ItemStats.PROJECTILE_SPEED, 1.2f)
                .stat(PartType.MAIN, ItemStats.PROJECTILE_ACCURACY, 0.9f)
                .stat(PartType.MAIN, ItemStats.RARITY, 45)
                .stat(PartType.MAIN, ItemStats.CHARGEABILITY, 1.2f)
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
                .stat(PartType.COATING, ItemStats.DURABILITY, -0.05f, StatInstance.Operation.MUL2)
                .stat(PartType.COATING, ItemStats.ARMOR_DURABILITY, -0.05f, StatInstance.Operation.MUL2)
                .stat(PartType.COATING, ItemStats.RARITY, 20, StatInstance.Operation.ADD)
                .trait(PartType.MAIN, Const.Traits.BRILLIANT, 1, new MaterialRatioTraitCondition(0.7f))
                .trait(PartType.MAIN, Const.Traits.MALLEABLE, 3)
                .trait(PartType.MAIN, Const.Traits.SOFT, 1)
                .trait(PartType.ROD, Const.Traits.FLEXIBLE, 2, new MaterialRatioTraitCondition(0.5f))
                .trait(PartType.ROD, Const.Traits.SYNERGISTIC, 2)
                .trait(PartType.TIP, Const.Traits.SOFT, 2)
                .trait(PartType.TIP, Const.Traits.FIERY, 4)
                .trait(PartType.COATING, Const.Traits.BRILLIANT, 1)
                .trait(PartType.COATING, Const.Traits.SOFT, 2)
                .display(PartType.MAIN, PartTextureSet.HIGH_CONTRAST_WITH_HIGHLIGHT, 0xDD8500)
                .display(PartType.ROD, PartTextureSet.HIGH_CONTRAST, 0xDD8500)
                .displayTip(PartTextures.TIP_SMOOTH, 0xDD8500)
                .displayCoating(PartTextureSet.HIGH_CONTRAST_WITH_HIGHLIGHT, 0xDD8500)
        );
        // Crimson Iron
        ret.add(new MaterialBuilder(modId("crimson_iron"), 3, ModTags.Items.INGOTS_CRIMSON_IRON)
                .categories(MaterialCategories.METAL)
                .stat(PartType.MAIN, ItemStats.DURABILITY, 420)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 27)
                .stat(PartType.MAIN, ItemStats.REPAIR_VALUE, 0.5f)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 14)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 3)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 10)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 3)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 3)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, -0.1f)
                .mainStatsArmor(3, 7, 5, 3, 2, 6) //18
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 2)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, -0.1f)
                .stat(PartType.MAIN, ItemStats.PROJECTILE_SPEED, 1f)
                .stat(PartType.MAIN, ItemStats.PROJECTILE_ACCURACY, 1.1f)
                .stat(PartType.MAIN, ItemStats.RARITY, 31)
                .stat(PartType.MAIN, ItemStats.CHARGEABILITY, 0.7f)
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
                .trait(PartType.MAIN, Const.Traits.MALLEABLE, 3)
                .trait(PartType.MAIN, Const.Traits.HARD, 2)
                .trait(PartType.ROD, Const.Traits.MALLEABLE, 3, new MaterialRatioTraitCondition(0.5f))
                .trait(PartType.TIP, Const.Traits.FIERY, 1)
                .display(PartType.MAIN, PartTextureSet.HIGH_CONTRAST_WITH_HIGHLIGHT, 0xFF6189)
                .display(PartType.ROD, PartTextureSet.LOW_CONTRAST, 0xFF6189)
                .displayTip(PartTextures.TIP_SHARP, 0xFF6189)
        );
        // Crimson Steel
        ret.add(new MaterialBuilder(modId("crimson_steel"), 4, ModTags.Items.INGOTS_CRIMSON_STEEL)
                .categories(MaterialCategories.METAL)
                .stat(PartType.MAIN, ItemStats.DURABILITY, 2400)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 42)
                .stat(PartType.MAIN, ItemStats.REPAIR_VALUE, 0.5f)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 19)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 4)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 15)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 6)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 6)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, -0.1f)
                .mainStatsArmor(4, 8, 6, 4, 10, 10) //22
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 3)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, -0.1f)
                .stat(PartType.MAIN, ItemStats.PROJECTILE_SPEED, 1f)
                .stat(PartType.MAIN, ItemStats.PROJECTILE_ACCURACY, 1.3f)
                .stat(PartType.MAIN, ItemStats.RARITY, 83)
                .stat(PartType.MAIN, ItemStats.CHARGEABILITY, 0.9f)
                .stat(PartType.ROD, ItemStats.DURABILITY, 0.5f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.HARVEST_SPEED, 0.2f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RANGED_DAMAGE, 1, StatInstance.Operation.ADD)
                .stat(PartType.ROD, ItemStats.RARITY, 42)
                .stat(PartType.TIP, ItemStats.DURABILITY, 448, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.ARMOR_DURABILITY, 16, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.HARVEST_LEVEL, 4, StatInstance.Operation.MAX)
                .stat(PartType.TIP, ItemStats.RARITY, 20, StatInstance.Operation.ADD)
                .trait(PartType.MAIN, Const.Traits.MALLEABLE, 5)
                .trait(PartType.MAIN, Const.Traits.HARD, 3)
                .trait(PartType.MAIN, Const.Traits.FLAME_WARD, 1, materialCountOrRatio(3, 0.33f))
                .trait(PartType.ROD, Const.Traits.MALLEABLE, 5, new MaterialRatioTraitCondition(0.5f))
                .trait(PartType.TIP, Const.Traits.MAGMATIC, 1)
                .display(PartType.MAIN, PartTextureSet.HIGH_CONTRAST_WITH_HIGHLIGHT, 0xDC143C)
                .display(PartType.ROD, PartTextureSet.LOW_CONTRAST, 0xDC143C)
                .displayTip(PartTextures.TIP_SHARP, 0xDC143C)
        );
        // Tyrian Steel
        ret.add(new MaterialBuilder(modId("tyrian_steel"), 4, ModTags.Items.INGOTS_TYRIAN_STEEL)
                .categories(MaterialCategories.METAL)
                .mainStatsCommon(3652, 81, 16, 100, 1.1f)
                .mainStatsHarvest(5, 18)
                .mainStatsMelee(8, 6, 0.0f)
                .mainStatsRanged(4, 0.0f)
                .mainStatsArmor(5, 9, 7, 4, 12, 12) //25
                .stat(PartType.MAIN, ItemStats.PROJECTILE_SPEED, 1.1f)
                .stat(PartType.MAIN, ItemStats.PROJECTILE_ACCURACY, 1.1f)
                .stat(PartType.ROD, ItemStats.DURABILITY, 0.1f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RARITY, 100)
                .stat(PartType.TIP, ItemStats.DURABILITY, 251, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.HARVEST_LEVEL, 5, StatInstance.Operation.MAX)
                .stat(PartType.TIP, ItemStats.RARITY, 30, StatInstance.Operation.ADD)
                .trait(PartType.MAIN, Const.Traits.STURDY, 3, new MaterialRatioTraitCondition(0.5f))
                .trait(PartType.MAIN, Const.Traits.VOID_WARD, 1, materialCountOrRatio(3, 0.5f))
                .trait(PartType.ROD, Const.Traits.STURDY, 4, new MaterialRatioTraitCondition(0.5f))
                .trait(PartType.TIP, Const.Traits.IMPERIAL, 3)
                .trait(PartType.TIP, Const.Traits.GOLD_DIGGER, 3)
                .display(PartType.MAIN, PartTextureSet.HIGH_CONTRAST_WITH_HIGHLIGHT, 0xB01080)
                .display(PartType.ROD, PartTextureSet.LOW_CONTRAST, 0xB01080)
                .displayTip(PartTextures.TIP_SHARP, 0xB01080)
        );
    }

    private void addVanillaMetals(Collection<MaterialBuilder> ret) {
        // Copper
        ret.add(new MaterialBuilder(modId("copper"), 2, Items.COPPER_INGOT)
                .categories(MaterialCategories.METAL)
                .mainStatsCommon(151, 12, 15, 12, 1.3f)
                .mainStatsHarvest(1, 5)
                .mainStatsMelee(1.5f, 1.0f, 0.1f)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, GearType.AXE, -0.1f)
                .mainStatsRanged(0.1f, 0.0f)
                .mainStatsArmor(2, 4, 3, 1, 0, 8) //10
                .stat(PartType.ROD, ItemStats.DURABILITY, -0.1f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.HARVEST_SPEED, 0.2f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RARITY, 15)
                .trait(PartType.MAIN, Const.Traits.SOFT, 1, new MaterialRatioTraitCondition(0.5f))
                .trait(PartType.ROD, Const.Traits.SOFT, 3)
                .displayAll(PartTextureSet.HIGH_CONTRAST_WITH_HIGHLIGHT, 0xFD804C)
        );
        // Gold
        ret.add(new MaterialBuilder(modId("gold"), 2, Tags.Items.INGOTS_GOLD)
                .categories(MaterialCategories.METAL)
                .stat(PartType.MAIN, ItemStats.DURABILITY, 32)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 7)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 22)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 0)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 12)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 0)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 4)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, 0.0f)
                .mainStatsArmor(2, 5, 3, 1, 0, 8) //11
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 0)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, 0.3f)
                .stat(PartType.MAIN, ItemStats.PROJECTILE_SPEED, 1.1f)
                .stat(PartType.MAIN, ItemStats.PROJECTILE_ACCURACY, 1.0f)
                .stat(PartType.MAIN, ItemStats.RARITY, 50)
                .stat(PartType.MAIN, ItemStats.CHARGEABILITY, 1.2f)
                .stat(PartType.ROD, ItemStats.DURABILITY, 0.05f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.ENCHANTABILITY, 3, StatInstance.Operation.ADD)
                .stat(PartType.ROD, ItemStats.RARITY, 40)
                .stat(PartType.TIP, ItemStats.DURABILITY, 16, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.ARMOR_DURABILITY, 1, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.HARVEST_SPEED, 6, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.MAGIC_DAMAGE, 2, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.RANGED_SPEED, 0.2f, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.RARITY, 30, StatInstance.Operation.ADD)
                .stat(PartType.COATING, ItemStats.DURABILITY, -0.1f, StatInstance.Operation.MUL2)
                .stat(PartType.COATING, ItemStats.ARMOR_DURABILITY, -0.1f, StatInstance.Operation.MUL2)
                .stat(PartType.COATING, ItemStats.RARITY, 10, StatInstance.Operation.ADD)
                .trait(PartType.MAIN, Const.Traits.BRILLIANT, 1, new MaterialRatioTraitCondition(0.7f))
                .trait(PartType.MAIN, Const.Traits.MALLEABLE, 1)
                .trait(PartType.MAIN, Const.Traits.SOFT, 3)
                .trait(PartType.ROD, Const.Traits.MALLEABLE, 1, new MaterialRatioTraitCondition(0.5f))
                .trait(PartType.TIP, Const.Traits.MALLEABLE, 1)
                .trait(PartType.TIP, Const.Traits.SOFT, 3)
                .trait(PartType.COATING, Const.Traits.BRILLIANT, 1)
                .trait(PartType.COATING, Const.Traits.SOFT, 3)
                .display(PartType.MAIN, PartTextureSet.HIGH_CONTRAST_WITH_HIGHLIGHT, 0xFDFF70)
                .display(PartType.ROD, PartTextureSet.LOW_CONTRAST, 0xFDFF70)
                .displayTip(PartTextures.TIP_SMOOTH, 0xFDFF70)
                .displayCoating(PartTextureSet.HIGH_CONTRAST_WITH_HIGHLIGHT, 0xFDFF70)
        );
        // Iron
        ret.add(new MaterialBuilder(modId("iron"), 2, Tags.Items.INGOTS_IRON)
                .categories(MaterialCategories.METAL)
                .partSubstitute(PartType.ROD, ModTags.Items.RODS_IRON)
                .stat(PartType.MAIN, ItemStats.DURABILITY, 250)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 15)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 14)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 2)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 6)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 2)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 1)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, 0.0f)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, GearType.AXE, -0.1f)
                .mainStatsArmor(2, 6, 5, 2, 0, 6) //15
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 1)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, 0.1f)
                .stat(PartType.MAIN, ItemStats.PROJECTILE_SPEED, 1.0f)
                .stat(PartType.MAIN, ItemStats.PROJECTILE_ACCURACY, 1.1f)
                .stat(PartType.MAIN, ItemStats.RARITY, 20)
                .stat(PartType.MAIN, ItemStats.CHARGEABILITY, 0.7f)
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
                .trait(PartType.MAIN, Const.Traits.MALLEABLE, 3)
                .trait(PartType.MAIN, Const.Traits.MAGNETIC, 1, new MaterialRatioTraitCondition(0.66f))
                .trait(PartType.ROD, Const.Traits.MAGNETIC, 3, new MaterialRatioTraitCondition(0.5f))
                .trait(PartType.TIP, Const.Traits.MALLEABLE, 2)
                .display(PartType.MAIN, PartTextureSet.HIGH_CONTRAST_WITH_HIGHLIGHT, Color.VALUE_WHITE)
                .display(PartType.ROD, PartTextureSet.LOW_CONTRAST, 0xD8D8D8)
                .displayTip(PartTextures.TIP_SHARP, Color.VALUE_WHITE)
        );
        // Netherite
        ret.add(new MaterialBuilder(modId("netherite"), 4, Items.NETHERITE_INGOT)
                .categories(MaterialCategories.METAL)
                .namePrefix(TextUtil.translate("material", "netherite"))
                .stat(PartType.COATING, ItemStats.DURABILITY, 0.3f, StatInstance.Operation.MUL2)
                .stat(PartType.COATING, ItemStats.DURABILITY, 2, StatInstance.Operation.ADD)
                .stat(PartType.COATING, ItemStats.ARMOR_DURABILITY, 37f / 33f - 1f, StatInstance.Operation.MUL2)
                .stat(PartType.COATING, ItemStats.HARVEST_SPEED, 0.125f, StatInstance.Operation.MUL2)
                .stat(PartType.COATING, ItemStats.HARVEST_LEVEL, 4, StatInstance.Operation.MAX)
                .stat(PartType.COATING, ItemStats.MELEE_DAMAGE, 1f / 3f, StatInstance.Operation.MUL2)
                .stat(PartType.COATING, ItemStats.MAGIC_DAMAGE, 1f / 3f, StatInstance.Operation.MUL2)
                .stat(PartType.COATING, ItemStats.RANGED_DAMAGE, 1f / 3f, StatInstance.Operation.MUL2)
                .stat(PartType.COATING, ItemStats.ARMOR_TOUGHNESS, 4, StatInstance.Operation.ADD)
                .stat(PartType.COATING, ItemStats.KNOCKBACK_RESISTANCE, 1f, StatInstance.Operation.ADD)
                .stat(PartType.COATING, ItemStats.ENCHANTABILITY, 5, StatInstance.Operation.ADD)
                .trait(PartType.COATING, Const.Traits.FIREPROOF, 1)
                .displayCoating(PartTextureSet.HIGH_CONTRAST_WITH_HIGHLIGHT, 0x867B86)
                .display(PartType.COATING, GearType.ARMOR, new StaticLayer(new ResourceLocation("netherite")))
                .displayFragment(PartTextures.DUST, 0x867B86)
        );
    }

    private void addGems(Collection<MaterialBuilder> ret) {
        // Diamond
        ret.add(new MaterialBuilder(modId("diamond"), 3, Tags.Items.GEMS_DIAMOND)
                .categories(MaterialCategories.GEM)
                // main
                .stat(PartType.MAIN, ItemStats.DURABILITY, 1561)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 33)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 10)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 3)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 8)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 3)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 1)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, 0.0f)
                .mainStatsArmor(3, 8, 6, 3, 8, 4) //20
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 2)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, -0.2f)
                .stat(PartType.MAIN, ItemStats.PROJECTILE_SPEED, 0.9f)
                .stat(PartType.MAIN, ItemStats.PROJECTILE_ACCURACY, 1.1f)
                .stat(PartType.MAIN, ItemStats.RARITY, 70)
                .stat(PartType.MAIN, ItemStats.CHARGEABILITY, 0.8f)
                .trait(PartType.MAIN, Const.Traits.BRITTLE, 2)
                .trait(PartType.MAIN, Const.Traits.LUSTROUS, 1, materialCountOrRatio(3, 0.5f))
                // rod
                .stat(PartType.ROD, ItemStats.DURABILITY, 0.2f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.HARVEST_SPEED, 0.2f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RARITY, 50)
                .trait(PartType.ROD, Const.Traits.BRITTLE, 5, new MaterialRatioTraitCondition(0.5f))
                .trait(PartType.ROD, Const.Traits.LUSTROUS, 4, new MaterialRatioTraitCondition(0.5f))
                // tip
                .stat(PartType.TIP, ItemStats.DURABILITY, 256, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.ARMOR_DURABILITY, 9, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.HARVEST_LEVEL, 3, StatInstance.Operation.MAX)
                .stat(PartType.TIP, ItemStats.HARVEST_SPEED, 2, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.MELEE_DAMAGE, 2, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.MAGIC_DAMAGE, 1, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.RANGED_DAMAGE, 0.5f, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.RARITY, 20, StatInstance.Operation.ADD)
                .trait(PartType.TIP, Const.Traits.BRITTLE, 2)
                .trait(PartType.TIP, Const.Traits.LUSTROUS, 2)
                // adornment
                .noStats(PartType.ADORNMENT)
                .trait(PartType.ADORNMENT, Const.Traits.BASTION, 1)

                .display(PartType.MAIN, PartTextureSet.HIGH_CONTRAST_WITH_HIGHLIGHT, 0x33EBCB)
                .display(PartType.ROD, PartTextureSet.HIGH_CONTRAST, 0x33EBCB)
                .displayTip(PartTextures.TIP_SHARP, 0x33EBCB)
                .displayAdornment(PartTextureSet.HIGH_CONTRAST_WITH_HIGHLIGHT, 0x33EBCB)
        );
        // Emerald
        ret.add(new MaterialBuilder(modId("emerald"), 3, Tags.Items.GEMS_EMERALD)
                .categories(MaterialCategories.GEM)
                // main
                .stat(PartType.MAIN, ItemStats.DURABILITY, 1080)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 24)
                .stat(PartType.MAIN, ItemStats.REPAIR_VALUE, 0.25f)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 16)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 2)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 10)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 2)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 2)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, 0.0f)
                .mainStatsArmor(3, 6, 4, 3, 4, 6) //16
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 1)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, -0.1f)
                .stat(PartType.MAIN, ItemStats.PROJECTILE_SPEED, 1.1f)
                .stat(PartType.MAIN, ItemStats.PROJECTILE_ACCURACY, 0.9f)
                .stat(PartType.MAIN, ItemStats.RARITY, 40)
                .stat(PartType.MAIN, ItemStats.CHARGEABILITY, 1.0f)
                .trait(PartType.MAIN, Const.Traits.BRITTLE, 1)
                .trait(PartType.MAIN, Const.Traits.SYNERGISTIC, 2)
                // rod
                .stat(PartType.ROD, ItemStats.DURABILITY, -0.2f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.HARVEST_SPEED, 0.3f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RARITY, 30)
                .trait(PartType.ROD, Const.Traits.BRITTLE, 4, new MaterialRatioTraitCondition(0.5f))
                .trait(PartType.ROD, Const.Traits.SYNERGISTIC, 3, new MaterialRatioTraitCondition(0.5f))
                // tip
                .stat(PartType.TIP, ItemStats.DURABILITY, 512, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.ARMOR_DURABILITY, 12, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.HARVEST_LEVEL, 2, StatInstance.Operation.MAX)
                .stat(PartType.TIP, ItemStats.HARVEST_SPEED, 2, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.MELEE_DAMAGE, 1, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.MAGIC_DAMAGE, 2, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.RANGED_DAMAGE, 1, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.RARITY, 20, StatInstance.Operation.ADD)
                .trait(PartType.TIP, Const.Traits.BRITTLE, 1)
                .trait(PartType.TIP, Const.Traits.SYNERGISTIC, 2)
                // adornment
                .noStats(PartType.ADORNMENT)
                .trait(PartType.ADORNMENT, Const.Traits.REACH, 2)

                .display(PartType.MAIN, PartTextureSet.HIGH_CONTRAST_WITH_HIGHLIGHT, 0x00B038)
                .display(PartType.ROD, PartTextureSet.HIGH_CONTRAST, 0x00B038)
                .displayTip(PartTextures.TIP_SHARP, 0x00B038)
                .displayAdornment(PartTextureSet.HIGH_CONTRAST_WITH_HIGHLIGHT, 0x00B038)
        );
        // Lapis Lazuli
        ret.add(new MaterialBuilder(modId("lapis_lazuli"), 2, Tags.Items.GEMS_LAPIS)
                .categories(MaterialCategories.GEM)
                // main
                .stat(PartType.MAIN, ItemStats.DURABILITY, 200)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 13)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 17)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 2)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 5)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 2)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 3)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, 0.0f)
                .mainStatsArmor(2, 6, 5, 2, 0, 10) //15
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 0)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, -0.1f)
                .stat(PartType.MAIN, ItemStats.PROJECTILE_SPEED, 1.0f)
                .stat(PartType.MAIN, ItemStats.PROJECTILE_ACCURACY, 0.8f)
                .stat(PartType.MAIN, ItemStats.RARITY, 30)
                .stat(PartType.MAIN, ItemStats.CHARGEABILITY, 1.3f)
                // tip
                .stat(PartType.TIP, ItemStats.ENCHANTABILITY, 0.5f, StatInstance.Operation.MUL2)
                .stat(PartType.TIP, ItemStats.HARVEST_SPEED, -0.1f, StatInstance.Operation.MUL2)
                .stat(PartType.TIP, ItemStats.MAGIC_DAMAGE, 2, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.ATTACK_SPEED, 0.3f, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.RARITY, 10, StatInstance.Operation.ADD)
                .trait(PartType.TIP, Const.Traits.HOLY, 1, new MaterialRatioTraitCondition(0.75f))
                .trait(PartType.TIP, Const.Traits.LUCKY, 4, new MaterialRatioTraitCondition(0.75f))
                // adornment
                .noStats(PartType.ADORNMENT)
                .trait(PartType.ADORNMENT, Const.Traits.LUCKY, 3, new MaterialRatioTraitCondition(0.75f))

                .display(PartType.MAIN, PartTextureSet.LOW_CONTRAST, 0x224BAF)
                .displayTip(PartTextures.TIP_SMOOTH, 0x224BAF)
                .displayAdornment(PartTextureSet.LOW_CONTRAST, 0x224BAF)
                .displayFragment(PartTextures.METAL, 0x224BAF)
        );
        // Prismarine
        ret.add(new MaterialBuilder(modId("prismarine"), 3, Tags.Items.GEMS_PRISMARINE)
                .categories(MaterialCategories.GEM, MaterialCategories.ORGANIC)
                .namePrefix(TextUtil.translate("material", "prismarine"))
                // coating
                .stat(PartType.COATING, ItemStats.DURABILITY, 0.075f, StatInstance.Operation.MUL2)
                .stat(PartType.COATING, ItemStats.ARMOR_DURABILITY, 0.125f, StatInstance.Operation.MUL2)
                .stat(PartType.COATING, ItemStats.ARMOR_TOUGHNESS, 1, StatInstance.Operation.ADD)
                .stat(PartType.COATING, ItemStats.KNOCKBACK_RESISTANCE, 0.25f, StatInstance.Operation.ADD)
                .trait(PartType.COATING, Const.Traits.AQUATIC, 5, new MaterialRatioTraitCondition(0.67f))
                .trait(PartType.COATING, Const.Traits.AQUATIC, 3, new NotTraitCondition(new MaterialRatioTraitCondition(0.67f)))
                // adornment
                .noStats(PartType.ADORNMENT)
                .trait(PartType.ADORNMENT, Const.Traits.SWIFT_SWIM, 3, new MaterialRatioTraitCondition(0.67f))

                .displayCoating(PartTextureSet.HIGH_CONTRAST_WITH_HIGHLIGHT, 0x91C5B7)
                .displayAdornment(PartTextureSet.HIGH_CONTRAST_WITH_HIGHLIGHT, 0x91C5B7)
                .displayFragment(PartTextures.DUST, 0x91C5B7)
        );
        // Quartz
        ret.add(new MaterialBuilder(modId("quartz"), 2, Tags.Items.GEMS_QUARTZ)
                .categories(MaterialCategories.GEM)
                // main
                .stat(PartType.MAIN, ItemStats.DURABILITY, 330)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 13)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 10)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 2)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 7)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 2)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 0)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, 0.1f)
                .mainStatsArmor(3, 5, 4, 2, 0, 4) //14
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 0)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, 0.1f)
                .stat(PartType.MAIN, ItemStats.PROJECTILE_SPEED, 1f)
                .stat(PartType.MAIN, ItemStats.PROJECTILE_ACCURACY, 1f)
                .stat(PartType.MAIN, ItemStats.RARITY, 40)
                .stat(PartType.MAIN, ItemStats.CHARGEABILITY, 1.2f)
                .trait(PartType.MAIN, Const.Traits.CRUSHING, 3)
                .trait(PartType.MAIN, Const.Traits.JAGGED, 2)
                // rod
                .trait(PartType.ROD, Const.Traits.BRITTLE, 2)
                .stat(PartType.ROD, ItemStats.DURABILITY, -0.2f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.HARVEST_SPEED, 0.2f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.MELEE_DAMAGE, 0.1f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RARITY, 30)
                // tip
                .stat(PartType.TIP, ItemStats.DURABILITY, 64, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.ARMOR_DURABILITY, 64, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.HARVEST_LEVEL, 2, StatInstance.Operation.MAX)
                .stat(PartType.TIP, ItemStats.HARVEST_SPEED, 2, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.MELEE_DAMAGE, 4, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.RANGED_DAMAGE, 1.5f, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.RARITY, 20, StatInstance.Operation.ADD)
                .trait(PartType.TIP, Const.Traits.CHIPPING, 1)
                .trait(PartType.TIP, Const.Traits.JAGGED, 3)
                // adornment
                .noStats(PartType.ADORNMENT)
                .trait(PartType.ADORNMENT, Const.Traits.MIGHTY, 2, new MaterialRatioTraitCondition(0.5f))

                .display(PartType.MAIN, PartTextureSet.HIGH_CONTRAST_WITH_HIGHLIGHT, 0xD4CABA)
                .display(PartType.ROD, PartTextureSet.LOW_CONTRAST, 0xD4CABA)
                .displayTip(PartTextures.TIP_SHARP, 0xD4CABA)
                .displayAdornment(PartTextureSet.HIGH_CONTRAST_WITH_HIGHLIGHT, 0xD4CABA)
        );
        // Amethyst
        ret.add(new MaterialBuilder(modId("amethyst"), 2, Items.AMETHYST_SHARD)
                .categories(MaterialCategories.GEM)
                // main
                .mainStatsCommon(210, 10, 16, 35, 1.3f)
                .mainStatsHarvest(2, 6)
                .mainStatsMelee(1, 3, 0)
                .mainStatsArmor(3, 5, 4, 3, 0, 10) //15
                .mainStatsRanged(1, 0)
                .mainStatsProjectile(1, 1)
                .trait(PartType.MAIN, Const.Traits.RENEW, 1, new MaterialRatioTraitCondition(0.7f))
                // tip
                .stat(PartType.TIP, ItemStats.DURABILITY, -0.25f, StatInstance.Operation.MUL2)
                .trait(PartType.TIP, Const.Traits.SILKY, 1, new MaterialRatioTraitCondition(0.66f))
                //adornment
                .noStats(PartType.ADORNMENT)
                .trait(PartType.ADORNMENT, Const.Traits.CURSED, 4)

                .displayAll(PartTextureSet.HIGH_CONTRAST_WITH_HIGHLIGHT, 0xA31DE6)
        );
    }

    private void addDusts(Collection<MaterialBuilder> ret) {
        // Glowstone
        ret.add(new MaterialBuilder(modId("glowstone"), 2, Tags.Items.DUSTS_GLOWSTONE)
                .categories(MaterialCategories.GEM, MaterialCategories.DUST)
                .stat(PartType.TIP, ItemStats.HARVEST_SPEED, 0.4f, StatInstance.Operation.MUL2)
                .stat(PartType.TIP, ItemStats.MELEE_DAMAGE, 2, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.MAGIC_DAMAGE, 2, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.RANGED_SPEED, 0.3f, StatInstance.Operation.MUL2)
                .stat(PartType.TIP, ItemStats.RARITY, 15, StatInstance.Operation.ADD)
                .trait(PartType.TIP, Const.Traits.REFRACTIVE, 1)
                .trait(PartType.TIP, Const.Traits.LUSTROUS, 4)

                .displayTip(PartTextures.TIP_SMOOTH, 0xD2D200)
                .displayFragment(PartTextures.DUST, 0xD2D200)
        );
        // Redstone
        ret.add(new MaterialBuilder(modId("redstone"), 2, Tags.Items.DUSTS_REDSTONE)
                .categories(MaterialCategories.GEM, MaterialCategories.DUST)
                .stat(PartType.TIP, ItemStats.HARVEST_SPEED, 0.2f, StatInstance.Operation.MUL2)
                .stat(PartType.TIP, ItemStats.MELEE_DAMAGE, 2, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.ATTACK_SPEED, 0.5f, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.RANGED_DAMAGE, 2, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.RARITY, 10, StatInstance.Operation.ADD)

                .displayTip(PartTextures.TIP_SMOOTH, 0xBB0000)
                .displayFragment(PartTextures.DUST, 0xBB0000)
        );
    }

    @SuppressWarnings("OverlyLongMethod")
    private void addStones(Collection<MaterialBuilder> ret) {
        // Basalt
        ret.add(new MaterialBuilder(modId("basalt"), 1, Items.BASALT)
                .categories(MaterialCategories.ROCK)
                .stat(PartType.MAIN, ItemStats.DURABILITY, 137)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 4)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 6)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 1)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 4)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 1)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 0)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, 0.0f)
                .mainStatsArmor(1, 3, 1, 1, 0, 0) //6
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 0)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, -0.1f)
                .stat(PartType.MAIN, ItemStats.PROJECTILE_SPEED, 1f)
                .stat(PartType.MAIN, ItemStats.PROJECTILE_ACCURACY, 0.8f)
                .stat(PartType.MAIN, ItemStats.RARITY, 7)
                .stat(PartType.MAIN, ItemStats.CHARGEABILITY, 0.7f)
                .stat(PartType.ROD, ItemStats.HARVEST_SPEED, 0.1f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.MELEE_DAMAGE, -0.1f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RARITY, 7)
                .trait(PartType.MAIN, Const.Traits.BRITTLE, 2)
                .trait(PartType.MAIN, Const.Traits.CHIPPING, 3)
                .trait(PartType.ROD, Const.Traits.BRITTLE, 3)
                .trait(PartType.ROD, Const.Traits.CHIPPING, 2)
                .display(PartType.MAIN,
                        new MaterialLayer(PartTextures.MAIN_GENERIC_LC, 0x4F4B4F),
                        new MaterialLayer(PartTextures.SPLOTCHES, 0x32333D))
                .display(PartType.MAIN, GearType.ARMOR, PartTextureSet.LOW_CONTRAST, 0x4F4B4F)
                .display(PartType.ROD, PartTextureSet.LOW_CONTRAST, 0x4F4B4F)
        );
        // Blackstone
        ret.add(new MaterialBuilder(modId("blackstone"), 1, Items.BLACKSTONE)
                .categories(MaterialCategories.ROCK)
                .stat(PartType.MAIN, ItemStats.DURABILITY, 151)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 5)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 4)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 1)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 4)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 1)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 0)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, 0.0f)
                .mainStatsArmor(1, 2, 1, 1, 0, 0) //5
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 0)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, -0.2f)
                .stat(PartType.MAIN, ItemStats.PROJECTILE_SPEED, 1f)
                .stat(PartType.MAIN, ItemStats.PROJECTILE_ACCURACY, 0.8f)
                .stat(PartType.MAIN, ItemStats.RARITY, 9)
                .stat(PartType.MAIN, ItemStats.CHARGEABILITY, 0.6f)
                .stat(PartType.ROD, ItemStats.DURABILITY, 0.1f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.HARVEST_SPEED, -0.1f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RARITY, 9)
                .trait(PartType.MAIN, Const.Traits.BRITTLE, 1)
                .trait(PartType.MAIN, Const.Traits.JAGGED, 2)
                .trait(PartType.MAIN, Const.Traits.HARD, 2)
                .trait(PartType.ROD, Const.Traits.BRITTLE, 2)
                .trait(PartType.ROD, Const.Traits.JAGGED, 2)
                .trait(PartType.ROD, Const.Traits.HARD, 1)
                .display(PartType.MAIN,
                        new MaterialLayer(PartTextures.MAIN_GENERIC_LC, 0x3C3947),
                        new MaterialLayer(PartTextures.SPLOTCHES, 0x1F121B))
                .display(PartType.MAIN, GearType.ARMOR, PartTextureSet.LOW_CONTRAST, 0x3C3947)
                .display(PartType.ROD, PartTextureSet.LOW_CONTRAST, 0x3C3947)
        );
        // End Stone
        ret.add(new MaterialBuilder(modId("end_stone"), 1, Tags.Items.END_STONES)
                .categories(MaterialCategories.ROCK)
                .stat(PartType.MAIN, ItemStats.DURABILITY, 1164)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 15)
                .stat(PartType.MAIN, ItemStats.REPAIR_VALUE, -0.2f)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 10)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 2)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 7)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 2)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 3)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, 0.1f)
                .mainStatsArmor(3, 5, 4, 3, 1, 6) //15
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 0)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, 0f)
                .stat(PartType.MAIN, ItemStats.PROJECTILE_SPEED, 1f)
                .stat(PartType.MAIN, ItemStats.PROJECTILE_ACCURACY, 0.8f)
                .stat(PartType.MAIN, ItemStats.RARITY, 32)
                .stat(PartType.MAIN, ItemStats.CHARGEABILITY, 0.9f)
                .stat(PartType.ROD, ItemStats.DURABILITY, 0.1f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.MELEE_DAMAGE, 1, StatInstance.Operation.ADD)
                .stat(PartType.ROD, ItemStats.RARITY, 26)
                .trait(PartType.MAIN, Const.Traits.JAGGED, 3)
                .trait(PartType.MAIN, Const.Traits.ANCIENT, 2)
                .trait(PartType.ROD, Const.Traits.JAGGED, 2)
                .trait(PartType.ROD, Const.Traits.ANCIENT, 4)
                .display(PartType.MAIN, PartTextureSet.HIGH_CONTRAST_WITH_HIGHLIGHT, 0xFFFFCC)
                .display(PartType.ROD, PartTextureSet.HIGH_CONTRAST, 0xFFFFCC)
        );
        // Flint
        ret.add(new MaterialBuilder(modId("flint"), 1, Items.FLINT)
                .categories(MaterialCategories.ROCK)
                .stat(PartType.MAIN, ItemStats.DURABILITY, 124)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 4)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 3)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 1)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 5)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 2)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 0)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, -0.1f)
                .mainStatsArmor(0.5f, 2f, 1f, 0.5f, 0, 0) //4
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 1)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, -0.3f)
                .stat(PartType.MAIN, ItemStats.PROJECTILE_SPEED, 1.0f)
                .stat(PartType.MAIN, ItemStats.PROJECTILE_ACCURACY, 1.0f)
                .stat(PartType.MAIN, ItemStats.RARITY, 6)
                .stat(PartType.MAIN, ItemStats.CHARGEABILITY, 0.8f)
                .stat(PartType.ROD, ItemStats.DURABILITY, -0.3f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RARITY, 2)
                .noStats(PartType.ADORNMENT)
                .trait(PartType.MAIN, Const.Traits.JAGGED, 3)
                .trait(PartType.ROD, Const.Traits.BRITTLE, 3)
                .trait(PartType.ROD, Const.Traits.JAGGED, 2)
                .displayAll(PartTextureSet.HIGH_CONTRAST, 0x969696)
        );
        // Netherrack
        ret.add(new MaterialBuilder(modId("netherrack"), 1, Tags.Items.NETHERRACK)
                .categories(MaterialCategories.ROCK, MaterialCategories.ORGANIC)
                .mainStatsCommon(142, 5, 8, 11, 0.8f)
                .stat(PartType.MAIN, ItemStats.REPAIR_VALUE, -0.2f)
                .mainStatsHarvest(1, 5)
                .mainStatsMelee(0.5f, 0.5f, 0.0f)
                .mainStatsRanged(0.5f, 0.1f)
                .mainStatsArmor(1, 4, 2, 1, 0, 4) //8
                .stat(PartType.MAIN, ItemStats.PROJECTILE_SPEED, 0.8f)
                .stat(PartType.MAIN, ItemStats.PROJECTILE_ACCURACY, 1.0f)
                .stat(PartType.ROD, ItemStats.DURABILITY, -0.2f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RARITY, 11)
                .trait(PartType.MAIN, Const.Traits.ERODED, 3)
                .trait(PartType.MAIN, Const.Traits.FLEXIBLE, 2)
                .trait(PartType.ROD, Const.Traits.ERODED, 2)
                .trait(PartType.ROD, Const.Traits.FLEXIBLE, 3)
                .display(PartType.MAIN, PartTextureSet.LOW_CONTRAST, 0x854242)
                .display(PartType.ROD, PartTextureSet.LOW_CONTRAST, 0x854242)
        );
        // Obsidian
        ret.add(new MaterialBuilder(modId("obsidian"), 3, Tags.Items.OBSIDIAN)
                .categories(MaterialCategories.ROCK)
                .stat(PartType.MAIN, ItemStats.DURABILITY, 3072)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 37)
                .stat(PartType.MAIN, ItemStats.REPAIR_VALUE, -0.5f)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 7)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 3)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 6)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 3)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 2)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, -0.4f)
                .mainStatsArmor(3, 8, 6, 3, 4, 8) //20
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 0)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, -0.4f)
                .stat(PartType.MAIN, ItemStats.PROJECTILE_SPEED, 0.7f)
                .stat(PartType.MAIN, ItemStats.PROJECTILE_ACCURACY, 0.7f)
                .stat(PartType.MAIN, ItemStats.RARITY, 10)
                .stat(PartType.MAIN, ItemStats.CHARGEABILITY, 0.6f)
                .stat(PartType.ROD, ItemStats.HARVEST_SPEED, -0.1f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RARITY, 5)
                .trait(PartType.MAIN, Const.Traits.JAGGED, 3)
                .trait(PartType.MAIN, Const.Traits.CRUSHING, 2)
                .trait(PartType.ROD, Const.Traits.BRITTLE, 2)
                .trait(PartType.ROD, Const.Traits.CHIPPING, 3)
                .display(PartType.MAIN, PartTextureSet.LOW_CONTRAST, 0x443464)
                .display(PartType.ROD, PartTextureSet.LOW_CONTRAST, 0x443464)
        );
        // Sandstone
        ResourceLocation sgSandstone = modId("sandstone");
        ret.add(new MaterialBuilder(sgSandstone, 1,
                ExclusionIngredient.of(Tags.Items.SANDSTONE,
                        Items.RED_SANDSTONE, Items.CHISELED_RED_SANDSTONE, Items.CUT_RED_SANDSTONE, Items.SMOOTH_RED_SANDSTONE))
                .categories(MaterialCategories.ROCK)
                .stat(PartType.MAIN, ItemStats.DURABILITY, 117)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 6)
                .stat(PartType.MAIN, ItemStats.REPAIR_VALUE, -0.1f)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 7)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 1)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 4)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 1)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 0)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, 0.1f)
                .mainStatsArmor(1, 2, 1, 1, 0, 0) //5
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 0)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, -0.1f)
                .stat(PartType.MAIN, ItemStats.PROJECTILE_SPEED, 1f)
                .stat(PartType.MAIN, ItemStats.PROJECTILE_ACCURACY, 0.8f)
                .stat(PartType.MAIN, ItemStats.RARITY, 7)
                .stat(PartType.MAIN, ItemStats.CHARGEABILITY, 0.7f)
                .stat(PartType.ROD, ItemStats.DURABILITY, -0.2f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.ENCHANTABILITY, -0.1f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RARITY, 7)
                .displayAll(PartTextureSet.LOW_CONTRAST, 0xE3DBB0)
        );
        // Red sandstone
        ret.add(new MaterialBuilder(modId("sandstone/red"), -1, Ingredient.of(
                Items.RED_SANDSTONE, Items.CHISELED_RED_SANDSTONE, Items.CUT_RED_SANDSTONE, Items.SMOOTH_RED_SANDSTONE))
                .parent(sgSandstone)
                .display(PartType.MAIN, PartTextureSet.LOW_CONTRAST, 0xD97B30)
                .display(PartType.ROD, PartTextureSet.LOW_CONTRAST, 0xD97B30)
        );
        // Stone
        ResourceLocation stone = modId("stone");
        ret.add(new MaterialBuilder(stone, 1, Tags.Items.COBBLESTONE)
                .categories(MaterialCategories.ROCK)
                .partSubstitute(PartType.ROD, ModTags.Items.RODS_STONE)
                .stat(PartType.MAIN, ItemStats.DURABILITY, 131)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 5)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 5)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 1)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 4)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 1)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 0)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, 0.0f)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, GearType.AXE, -0.2f)
                .mainStatsArmor(1, 2, 1, 1, 0, 0) //5
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 0)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, -0.2f)
                .stat(PartType.MAIN, ItemStats.PROJECTILE_SPEED, 1f)
                .stat(PartType.MAIN, ItemStats.PROJECTILE_ACCURACY, 0.8f)
                .stat(PartType.MAIN, ItemStats.RARITY, 4)
                .stat(PartType.MAIN, ItemStats.CHARGEABILITY, 0.5f)
                .stat(PartType.ROD, ItemStats.DURABILITY, -0.1f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.HARVEST_SPEED, 0.2f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RARITY, 2)
                .trait(PartType.MAIN, Const.Traits.ANCIENT, 1)
                .trait(PartType.ROD, Const.Traits.BRITTLE, 1)
                .trait(PartType.ROD, Const.Traits.CRUSHING, 2)
                .display(PartType.MAIN, PartTextureSet.LOW_CONTRAST, 0x9A9A9A)
                .display(PartType.ROD, PartTextureSet.LOW_CONTRAST, 0x9A9A9A)
        );
        ret.add(new MaterialBuilder(modId("stone/andesite"), -1, Items.ANDESITE)
                .parent(stone)
                .display(PartType.MAIN, PartTextureSet.LOW_CONTRAST, 0x8A8A8E)
                .display(PartType.ROD, PartTextureSet.LOW_CONTRAST, 0x8A8A8E)
        );
        ret.add(new MaterialBuilder(modId("stone/diorite"), -1, Items.DIORITE)
                .parent(stone)
                .display(PartType.MAIN, PartTextureSet.LOW_CONTRAST, 0xFFFFFF)
                .display(PartType.ROD, PartTextureSet.LOW_CONTRAST, 0xFFFFFF)
        );
        ret.add(new MaterialBuilder(modId("stone/granite"), -1, Items.GRANITE)
                .parent(stone)
                .display(PartType.MAIN, PartTextureSet.LOW_CONTRAST, 0x9F6B58)
                .display(PartType.ROD, PartTextureSet.LOW_CONTRAST, 0x9F6B58)
        );
        // Terracotta
        ResourceLocation sgTerracotta = modId("terracotta");
        ret.add(new MaterialBuilder(sgTerracotta, 1, Items.TERRACOTTA)
                .categories(MaterialCategories.ROCK)
                .stat(PartType.MAIN, ItemStats.DURABILITY, 165)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 11)
                .stat(PartType.MAIN, ItemStats.REPAIR_VALUE, -0.1f)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 9)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 1)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 4)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 1.5f)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 0)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, 0.2f)
                .mainStatsArmor(2, 3, 3, 1, 0, 3) //9
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 0)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, -0.2f)
                .stat(PartType.MAIN, ItemStats.PROJECTILE_SPEED, 1f)
                .stat(PartType.MAIN, ItemStats.PROJECTILE_ACCURACY, 0.9f)
                .stat(PartType.MAIN, ItemStats.RARITY, 7)
                .stat(PartType.ROD, ItemStats.DURABILITY, -0.1f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.ENCHANTABILITY, -0.1f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.MELEE_DAMAGE, 0.1f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RARITY, 6)
                .trait(PartType.MAIN, Const.Traits.BRITTLE, 1)
                .trait(PartType.MAIN, Const.Traits.CHIPPING, 2)
                .trait(PartType.MAIN, Const.Traits.RUSTIC, 1)
                .trait(PartType.ROD, Const.Traits.BRITTLE, 2)
                .trait(PartType.ROD, Const.Traits.CRUSHING, 1)
                .trait(PartType.ROD, Const.Traits.RUSTIC, 2)
                .displayAll(PartTextureSet.LOW_CONTRAST, 0x985F45)
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
    }

    private void addWoods(Collection<MaterialBuilder> ret) {
        // Wood
        ExclusionIngredient planksIngredient = ExclusionIngredient.of(ItemTags.PLANKS,
                Items.ACACIA_PLANKS,
                Items.BIRCH_PLANKS,
                Items.DARK_OAK_PLANKS,
                Items.JUNGLE_PLANKS,
                Items.OAK_PLANKS,
                Items.SPRUCE_PLANKS,
                ModBlocks.NETHERWOOD_PLANKS,
                Items.CRIMSON_PLANKS,
                Items.WARPED_PLANKS
        );
        ExclusionIngredient woodRodSubstitute = ExclusionIngredient.of(Tags.Items.RODS_WOODEN,
                CraftingItems.NETHERWOOD_STICK
        );
        ret.add(new MaterialBuilder(Const.Materials.WOOD.getId(), 0, planksIngredient)
                .categories(MaterialCategories.ORGANIC, MaterialCategories.WOOD)
                .partSubstitute(PartType.ROD, woodRodSubstitute)
                .stat(PartType.MAIN, ItemStats.DURABILITY, 59)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 8)
                .stat(PartType.MAIN, ItemStats.REPAIR_VALUE, 0.25f)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 15)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 0)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 2)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 0)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 0)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, 0.0f)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, GearType.AXE, -0.2f)
                .mainStatsArmor(1, 3, 2, 1, 0, 2) //7
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 0)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, 0f)
                .stat(PartType.MAIN, ItemStats.PROJECTILE_SPEED, 1f)
                .stat(PartType.MAIN, ItemStats.PROJECTILE_ACCURACY, 0.9f)
                .stat(PartType.MAIN, ItemStats.RARITY, 1)
                .stat(PartType.MAIN, ItemStats.CHARGEABILITY, 0.6f)
                .stat(PartType.ROD, ItemStats.ENCHANTABILITY, 0.1f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RARITY, 1)
                .trait(PartType.MAIN, Const.Traits.FLAMMABLE, 1)
                .trait(PartType.MAIN, Const.Traits.FLEXIBLE, 2)
                .trait(PartType.MAIN, Const.Traits.JAGGED, 1)
                .trait(PartType.ROD, Const.Traits.FLEXIBLE, 1)
                .displayAll(PartTextureSet.LOW_CONTRAST, 0x896727)
                .displayFragment(PartTextures.WOOD, 0X896727)
        );
        ret.add(wood("acacia", Items.ACACIA_PLANKS, 0xBA6337));
        ret.add(wood("birch", Items.BIRCH_PLANKS, 0xD7C185));
        ret.add(wood("dark_oak", Items.DARK_OAK_PLANKS, 0x4F3218));
        ret.add(wood("jungle", Items.JUNGLE_PLANKS, 0xB88764));
        ret.add(wood("oak", Items.OAK_PLANKS, 0xB8945F));
        ret.add(wood("spruce", Items.SPRUCE_PLANKS, 0x82613A));
        ret.add(wood("crimson", Items.CRIMSON_PLANKS, 0x7E3A56)
                .trait(PartType.MAIN, Const.Traits.FLEXIBLE, 2)
                .trait(PartType.MAIN, Const.Traits.JAGGED, 1)
                .trait(PartType.ROD, Const.Traits.FLEXIBLE, 1)
        );
        ret.add(wood("warped", Items.WARPED_PLANKS, 0x398382)
                .trait(PartType.MAIN, Const.Traits.FLEXIBLE, 2)
                .trait(PartType.MAIN, Const.Traits.JAGGED, 1)
                .trait(PartType.ROD, Const.Traits.FLEXIBLE, 1)
        );

        // Rough wood
        ret.add(new MaterialBuilder(modId("wood/rough"), 0, Ingredient.EMPTY)
                .categories(MaterialCategories.ORGANIC, MaterialCategories.WOOD)
                .namePrefix(TextUtil.misc("crude"))
                .partSubstitute(PartType.ROD, ModTags.Items.RODS_ROUGH)
                .stat(PartType.ROD, ItemStats.DURABILITY, -0.25f, StatInstance.Operation.MUL1)
                .stat(PartType.ROD, ItemStats.RARITY, -5, StatInstance.Operation.ADD)
                .trait(PartType.ROD, Const.Traits.CRUDE, 3)
                .display(PartType.ROD, PartTextureSet.LOW_CONTRAST, 0x6B4909)
                .displayFragment(PartTextures.WOOD, 0x6B4909)
        );

        // Netherwood
        ret.add(new MaterialBuilder(modId("netherwood"), 0, ModBlocks.NETHERWOOD_PLANKS)
                .categories(MaterialCategories.ORGANIC, MaterialCategories.WOOD)
                .partSubstitute(PartType.ROD, ModTags.Items.RODS_NETHERWOOD)
                .stat(PartType.MAIN, ItemStats.DURABILITY, 72)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 12)
                .stat(PartType.MAIN, ItemStats.REPAIR_VALUE, 0.1f)
                .stat(PartType.MAIN, ItemStats.REPAIR_EFFICIENCY, 0.5f)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 13)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 0)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 2)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 0)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 0)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, 0.2f)
                .mainStatsArmor(1, 4, 2, 1, 0, 6) //8
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 0)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, 0.0f)
                .stat(PartType.MAIN, ItemStats.PROJECTILE_SPEED, 1f)
                .stat(PartType.MAIN, ItemStats.PROJECTILE_ACCURACY, 0.8f)
                .stat(PartType.MAIN, ItemStats.RARITY, 4)
                .stat(PartType.MAIN, ItemStats.CHARGEABILITY, 0.7f)
                .stat(PartType.ROD, ItemStats.DURABILITY, 70, StatInstance.Operation.MAX)
                .stat(PartType.ROD, ItemStats.DURABILITY, -50, StatInstance.Operation.ADD)
                .stat(PartType.ROD, ItemStats.HARVEST_SPEED, 1, StatInstance.Operation.ADD)
                .stat(PartType.ROD, ItemStats.MELEE_DAMAGE, 0.5f, StatInstance.Operation.ADD)
                .stat(PartType.ROD, ItemStats.RARITY, 3)
                .trait(PartType.MAIN, Const.Traits.FLEXIBLE, 3)
                .trait(PartType.MAIN, Const.Traits.JAGGED, 2)
                .trait(PartType.ROD, Const.Traits.FLEXIBLE, 2)
                .display(PartType.MAIN, PartTextureSet.LOW_CONTRAST, 0xD83200)
                .display(PartType.ROD, PartTextureSet.LOW_CONTRAST, 0xD83200)
                .displayFragment(PartTextures.WOOD, 0xD83200)
        );
        // Bamboo
        ret.add(new MaterialBuilder(modId("bamboo"), 0, Items.BAMBOO)
                .categories(MaterialCategories.ORGANIC)
                .partSubstitute(PartType.ROD, Items.BAMBOO)
                .stat(PartType.ROD, ItemStats.DURABILITY, 0.05f, StatInstance.Operation.MUL1)
                .stat(PartType.ROD, ItemStats.RARITY, 4)
                .display(PartType.ROD, PartTextureSet.LOW_CONTRAST, 0x9AC162)
        );
    }

    private void addClothLikes(Collection<MaterialBuilder> ret) {
        // Phantom Membrane
        ret.add(new MaterialBuilder(modId("phantom_membrane"), 2, Items.PHANTOM_MEMBRANE)
                .categories(MaterialCategories.ORGANIC, MaterialCategories.CLOTH)
                // main
                .mainStatsCommon(0, 12, 10, 35, 0.7f)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, GearType.ELYTRA, 17)
                .mainStatsArmor(1, 2, 2, 1, 0, 8) //6
                .trait(PartType.MAIN, Const.Traits.RENEW, 2, new MaterialRatioTraitCondition(0.5f))
                // grip
                .stat(PartType.GRIP, ItemStats.REPAIR_EFFICIENCY, 0.15f, StatInstance.Operation.MUL1)
                .stat(PartType.GRIP, ItemStats.HARVEST_SPEED, 0.1f, StatInstance.Operation.MUL1)
                .stat(PartType.GRIP, ItemStats.ATTACK_SPEED, 0.2f, StatInstance.Operation.ADD)
                .stat(PartType.GRIP, ItemStats.RARITY, 8, StatInstance.Operation.ADD)
                .trait(PartType.GRIP, Const.Traits.ANCIENT, 2)
                // lining
                .stat(PartType.LINING, ItemStats.ARMOR_DURABILITY, 4, StatInstance.Operation.ADD)
                .trait(PartType.LINING, Const.Traits.LIGHT, 2)

                .displayAll(PartTextureSet.LOW_CONTRAST, 0xC3B9A1)
                .displayFragment(PartTextures.CLOTH, 0xC3B9A1)
        );
        // Fine Silk Cloth
        ret.add(new MaterialBuilder(modId("fine_silk_cloth"), 2, CraftingItems.FINE_SILK_CLOTH)
                .categories(MaterialCategories.ORGANIC, MaterialCategories.CLOTH)
                // main
                .mainStatsCommon(0, 14, 14, 40, 0.9f)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, GearType.ELYTRA, 18)
                .mainStatsArmor(1, 2, 2, 1, 0, 14) //6
                // grip
                .stat(PartType.GRIP, ItemStats.REPAIR_EFFICIENCY, 0.1f, StatInstance.Operation.MUL1)
                .stat(PartType.GRIP, ItemStats.HARVEST_SPEED, 0.15f, StatInstance.Operation.MUL1)
                .stat(PartType.GRIP, ItemStats.RARITY, 10, StatInstance.Operation.ADD)
                .trait(PartType.GRIP, Const.Traits.ACCELERATE, 1)
                // lining
                .stat(PartType.LINING, ItemStats.ARMOR_DURABILITY, 5, StatInstance.Operation.ADD)
                .stat(PartType.LINING, ItemStats.MAGIC_ARMOR, 2, StatInstance.Operation.ADD)
                .trait(PartType.LINING, Const.Traits.FLEXIBLE, 3)

                .displayAll(PartTextureSet.LOW_CONTRAST, 0xC3B9A1)
                .displayFragment(PartTextures.CLOTH, 0xC3B9A1)
        );
        // Leather
        ret.add(new MaterialBuilder(modId("leather"), 0, Tags.Items.LEATHER)
                .categories(MaterialCategories.ORGANIC, MaterialCategories.CLOTH)
                // main
                .mainStatsCommon(0, 5, 15, 11, 0.8f)
                .mainStatsArmor(1, 3, 2, 1, 0, 8) //7
                // grip
                .stat(PartType.GRIP, ItemStats.DURABILITY, 0.05f, StatInstance.Operation.MUL1)
                .stat(PartType.GRIP, ItemStats.REPAIR_EFFICIENCY, 0.1f, StatInstance.Operation.MUL1)
                .stat(PartType.GRIP, ItemStats.HARVEST_SPEED, 0.15f, StatInstance.Operation.MUL1)
                .stat(PartType.GRIP, ItemStats.ATTACK_SPEED, 0.15f, StatInstance.Operation.ADD)
                .stat(PartType.GRIP, ItemStats.RARITY, 5, StatInstance.Operation.ADD)
                .trait(PartType.GRIP, Const.Traits.FLEXIBLE, 3)
                // lining
                .stat(PartType.LINING, ItemStats.ARMOR_DURABILITY, 1f, StatInstance.Operation.ADD)
                .trait(PartType.LINING, Const.Traits.FLEXIBLE, 2)

                .displayAll(PartTextureSet.LOW_CONTRAST, 0xC65C35)
                .displayFragment(PartTextures.CLOTH, 0xC65C35)
        );
        // Wool
        ExclusionIngredient woolIngredient = ExclusionIngredient.of(ItemTags.WOOL, Items.BLACK_WOOL,
                Items.BLUE_WOOL, Items.BROWN_WOOL, Items.CYAN_WOOL, Items.GRAY_WOOL,
                Items.GREEN_WOOL, Items.LIGHT_BLUE_WOOL, Items.LIGHT_GRAY_WOOL, Items.LIME_WOOL,
                Items.MAGENTA_WOOL, Items.ORANGE_WOOL, Items.PINK_WOOL, Items.PURPLE_WOOL,
                Items.RED_WOOL, Items.WHITE_WOOL, Items.YELLOW_WOOL
        );
        ret.add(new MaterialBuilder(Const.Materials.WOOL.getId(), 0, woolIngredient)
                .categories(MaterialCategories.ORGANIC, MaterialCategories.CLOTH)
                // main
                .mainStatsCommon(0, 4, 7, 7, 0.7f)
                .mainStatsArmor(0.5f, 2f, 1.0f, 0.5f, 0, 4) //4
                // grip
                .stat(PartType.GRIP, ItemStats.REPAIR_EFFICIENCY, 0.2f, StatInstance.Operation.MUL1)
                .stat(PartType.GRIP, ItemStats.HARVEST_SPEED, 0.1f, StatInstance.Operation.MUL1)
                .stat(PartType.GRIP, ItemStats.ATTACK_SPEED, 0.2f, StatInstance.Operation.ADD)
                .stat(PartType.GRIP, ItemStats.RARITY, 4, StatInstance.Operation.ADD)
                .trait(PartType.GRIP, Const.Traits.FLEXIBLE, 1)
                // lining
                .stat(PartType.LINING, ItemStats.KNOCKBACK_RESISTANCE, 0.1f, StatInstance.Operation.ADD)
                .trait(PartType.LINING, Const.Traits.FLEXIBLE, 1)

                .displayAll(PartTextureSet.LOW_CONTRAST, Color.VALUE_WHITE)
                .displayFragment(PartTextures.CLOTH, Color.VALUE_WHITE)
        );
        ret.add(wool("black", Items.BLACK_WOOL, 0x141519));
        ret.add(wool("blue", Items.BLUE_WOOL, 0x35399D));
        ret.add(wool("brown", Items.BROWN_WOOL, 0x724728));
        ret.add(wool("cyan", Items.CYAN_WOOL, 0x158991));
        ret.add(wool("gray", Items.GRAY_WOOL, 0x3E4447));
        ret.add(wool("green", Items.GREEN_WOOL, 0x546D1B));
        ret.add(wool("light_blue", Items.LIGHT_BLUE_WOOL, 0x3AAFD9));
        ret.add(wool("light_gray", Items.LIGHT_GRAY_WOOL, 0x8E8E86));
        ret.add(wool("lime", Items.LIME_WOOL, 0x70B919));
        ret.add(wool("magenta", Items.MAGENTA_WOOL, 0xBD44B3));
        ret.add(wool("orange", Items.ORANGE_WOOL, 0xF07613));
        ret.add(wool("pink", Items.PINK_WOOL, 0xED8DAC));
        ret.add(wool("purple", Items.PURPLE_WOOL, 0x792AAC));
        ret.add(wool("red", Items.RED_WOOL, 0xA12722));
        ret.add(wool("white", Items.WHITE_WOOL, 0xE9ECEC));
        ret.add(wool("yellow", Items.YELLOW_WOOL, 0xF8C627));
    }

    private void addStringsAndFibers(Collection<MaterialBuilder> ret) {
        // Fine Silk
        ret.add(new MaterialBuilder(modId("fine_silk"), 2, CraftingItems.FINE_SILK)
                .categories(MaterialCategories.ORGANIC, MaterialCategories.FIBER)

                .stat(PartType.BINDING, ItemStats.DURABILITY, 0.1f, StatInstance.Operation.MUL1)
                .stat(PartType.BINDING, ItemStats.ARMOR_DURABILITY, 0.1f, StatInstance.Operation.MUL1)
                .stat(PartType.BINDING, ItemStats.MAGIC_ARMOR, 2, StatInstance.Operation.ADD)
                .stat(PartType.BINDING, ItemStats.RARITY, 10, StatInstance.Operation.ADD)
                .trait(PartType.BINDING, Const.Traits.LUCKY, 1)

                .stat(PartType.BOWSTRING, ItemStats.DURABILITY, 0.1f, StatInstance.Operation.MUL1)
                .stat(PartType.BOWSTRING, ItemStats.RANGED_DAMAGE, 0.07f, StatInstance.Operation.MUL1)
                .stat(PartType.BOWSTRING, ItemStats.RARITY, 10, StatInstance.Operation.ADD)
                .trait(PartType.BOWSTRING, Const.Traits.FLEXIBLE, 2)

                .display(PartType.BINDING, PartTextureSet.LOW_CONTRAST, 0xCCFFFF)
                .displayBowstring(0xCCFFFF)
                .displayFragment(PartTextures.CLOTH, 0xCCFFFF)
        );
        // Flax
        ret.add(new MaterialBuilder(modId("flax"), 1, CraftingItems.FLAX_STRING)
                .categories(MaterialCategories.ORGANIC, MaterialCategories.FIBER)

                .stat(PartType.BINDING, ItemStats.DURABILITY, -0.05f, StatInstance.Operation.MUL1)
                .stat(PartType.BINDING, ItemStats.DURABILITY, 10, StatInstance.Operation.ADD)
                .stat(PartType.BINDING, ItemStats.ARMOR_DURABILITY, 0.05f, StatInstance.Operation.MUL1)
                .stat(PartType.BINDING, ItemStats.HARVEST_SPEED, 0.05f, StatInstance.Operation.MUL1)
                .trait(PartType.BINDING, Const.Traits.FLEXIBLE, 1)

                .stat(PartType.BOWSTRING, ItemStats.DURABILITY, 0.05f, StatInstance.Operation.MUL1)
                .stat(PartType.BOWSTRING, ItemStats.RANGED_DAMAGE, -0.1f, StatInstance.Operation.MUL1)
                .stat(PartType.BOWSTRING, ItemStats.RANGED_SPEED, 0.2f, StatInstance.Operation.MUL1)
                .stat(PartType.BOWSTRING, ItemStats.RARITY, 6, StatInstance.Operation.ADD)

                .display(PartType.BINDING, PartTextureSet.LOW_CONTRAST, 0xB3804B)
                .displayBowstring(0x845E37)
                .displayFragment(PartTextures.CLOTH, 0x845E37)
        );
        // Fluffy String
        ret.add(new MaterialBuilder(modId("fluffy_string"), 1, CraftingItems.FLUFFY_STRING)
                .categories(MaterialCategories.ORGANIC, MaterialCategories.FIBER)

                .stat(PartType.BINDING, ItemStats.DURABILITY, 0.05f, StatInstance.Operation.MUL1)
                .stat(PartType.BINDING, ItemStats.DURABILITY, -10, StatInstance.Operation.ADD)
                .stat(PartType.BINDING, ItemStats.ARMOR_DURABILITY, 0.05f, StatInstance.Operation.MUL1)
                .stat(PartType.BINDING, ItemStats.HARVEST_SPEED, -0.05f, StatInstance.Operation.MUL1)
                .trait(PartType.BINDING, Const.Traits.FLEXIBLE, 1)

                .stat(PartType.BOWSTRING, ItemStats.DURABILITY, 0.05f, StatInstance.Operation.MUL1)
                .stat(PartType.BOWSTRING, ItemStats.RANGED_DAMAGE, 0.05f, StatInstance.Operation.MUL1)
                .stat(PartType.BOWSTRING, ItemStats.RANGED_SPEED, -0.05f, StatInstance.Operation.MUL1)
                .stat(PartType.BOWSTRING, ItemStats.RARITY, 7, StatInstance.Operation.ADD)

                .display(PartType.BINDING, PartTextureSet.LOW_CONTRAST, 0xFFFAE5)
                .displayBowstring(0xFFFAE5)
                .displayFragment(PartTextures.CLOTH, 0xFFFAE5)
        );
        // Sinew
        ret.add(new MaterialBuilder(modId("sinew"), 1, CraftingItems.SINEW_FIBER)
                .categories(MaterialCategories.ORGANIC, MaterialCategories.FIBER)

                .stat(PartType.BINDING, ItemStats.DURABILITY, 0.05f, StatInstance.Operation.MUL1)
                .stat(PartType.BINDING, ItemStats.REPAIR_EFFICIENCY, -0.05f, StatInstance.Operation.MUL1)
                .trait(PartType.BINDING, Const.Traits.FLEXIBLE, 2)

                .stat(PartType.BOWSTRING, ItemStats.DURABILITY, 0.2f, StatInstance.Operation.MUL1)
                .stat(PartType.BOWSTRING, ItemStats.RANGED_DAMAGE, 0.2f, StatInstance.Operation.MUL1)
                .stat(PartType.BOWSTRING, ItemStats.RARITY, 8, StatInstance.Operation.ADD)
                .trait(PartType.BOWSTRING, Const.Traits.FLEXIBLE, 1)

                .display(PartType.BINDING, PartTextureSet.LOW_CONTRAST, 0xD8995B)
                .displayBowstring(0x7E6962)
                .displayFragment(PartTextures.CLOTH, 0x7E6962)
        );
        // String
        ExclusionIngredient stringIngredient = ExclusionIngredient.of(Tags.Items.STRING,
                CraftingItems.FLAX_STRING,
                CraftingItems.FLUFFY_STRING,
                CraftingItems.SINEW_FIBER
        );
        ret.add(new MaterialBuilder(modId("string"), 1, stringIngredient)
                .categories(MaterialCategories.ORGANIC, MaterialCategories.FIBER)

                .stat(PartType.BINDING, ItemStats.DURABILITY, -0.05f, StatInstance.Operation.MUL1)
                .stat(PartType.BINDING, ItemStats.ARMOR_DURABILITY, 0.05f, StatInstance.Operation.MUL1)
                .stat(PartType.BINDING, ItemStats.REPAIR_EFFICIENCY, 0.05f, StatInstance.Operation.MUL1)
                .trait(PartType.BINDING, Const.Traits.FLEXIBLE, 1)

                .stat(PartType.BOWSTRING, ItemStats.RANGED_SPEED, 0.1f, StatInstance.Operation.MUL1)
                .stat(PartType.BOWSTRING, ItemStats.RARITY, 4, StatInstance.Operation.ADD)

                .display(PartType.BINDING, PartTextureSet.LOW_CONTRAST, Color.VALUE_WHITE)
                .displayBowstring(Color.VALUE_WHITE)
                .displayFragment(PartTextures.CLOTH, Color.VALUE_WHITE)
        );
        // Vines
        ret.add(new MaterialBuilder(modId("vine"), 0, Items.VINE)
                .categories(MaterialCategories.ORGANIC)
                .stat(PartType.BINDING, ItemStats.DURABILITY, 0.03f, StatInstance.Operation.MUL1)
                .stat(PartType.BINDING, ItemStats.ARMOR_DURABILITY, -0.03f, StatInstance.Operation.MUL1)
                .stat(PartType.BINDING, ItemStats.REPAIR_EFFICIENCY, 0.03f, StatInstance.Operation.MUL1)

                .display(PartType.BINDING, PartTextureSet.LOW_CONTRAST, 0x007F0E)
        );
    }

    private void addRandomOrganics(Collection<MaterialBuilder> ret) {
        // Feather
        ret.add(new MaterialBuilder(modId("feather"), 1, Tags.Items.FEATHERS)
                .categories(MaterialCategories.ORGANIC)
                .stat(PartType.FLETCHING, ItemStats.PROJECTILE_SPEED, 0.9f)
                .stat(PartType.FLETCHING, ItemStats.PROJECTILE_ACCURACY, 1.1f)
                .displayAll(PartTextureSet.LOW_CONTRAST, Color.VALUE_WHITE)
                .displayFragment(PartTextures.CLOTH, Color.VALUE_WHITE)
        );
        // Leaves
        ret.add(new MaterialBuilder(modId("leaves"), 1, ItemTags.LEAVES)
                .categories(MaterialCategories.ORGANIC)
                .stat(PartType.FLETCHING, ItemStats.PROJECTILE_SPEED, 1.1f)
                .stat(PartType.FLETCHING, ItemStats.PROJECTILE_ACCURACY, 0.9f)
                .displayAll(PartTextureSet.LOW_CONTRAST, 0x4A8F28)
                .displayFragment(PartTextures.CLOTH, 0x4A8F28)
        );
        // Paper
        ret.add(new MaterialBuilder(modId("paper"), 0, ModTags.Items.PAPER)
                .categories(MaterialCategories.ORGANIC)
                .stat(PartType.FLETCHING, ItemStats.PROJECTILE_SPEED, 1.1f)
                .stat(PartType.FLETCHING, ItemStats.PROJECTILE_ACCURACY, 0.9f)
                .display(PartType.FLETCHING, PartTextureSet.LOW_CONTRAST, 0xFFFFFF)
        );
        // Slime
        ret.add(new MaterialBuilder(modId("slime"), 1, Items.SLIME_BLOCK)
                .categories(MaterialCategories.SLIME, MaterialCategories.ORGANIC)
                .stat(PartType.LINING, ItemStats.ARMOR_TOUGHNESS, 0.5f, StatInstance.Operation.ADD)
                .trait(PartType.LINING, Const.Traits.BOUNCE, 1)
                .display(PartType.LINING, GearType.PART,
                        new MaterialLayer(PartTextures.LINING_SLIME, 0x8CD782),
                        new MaterialLayer(modId("lining_slime_highlight"), Color.VALUE_WHITE))
                .displayFragment(PartTextures.DUST, 0x8CD782)
        );
        // Turtle
        ret.add(new MaterialBuilder(modId("turtle"), 2, Items.SCUTE)
                .categories(MaterialCategories.ORGANIC)
                .stat(PartType.MAIN, ItemStats.DURABILITY, 0)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, GearType.HELMET, 25)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 9)
                .stat(PartType.MAIN, ItemStats.RARITY, 20)
                .mainStatsArmor(2, 0, 0, 0, 0, 4)
                .stat(PartType.MAIN, ItemStats.CHARGEABILITY, 0.5f)
                .trait(PartType.MAIN, Const.Traits.TURTLE, 1,
                        new MaterialCountTraitCondition(3))
                .display(PartType.MAIN, GearType.HELMET, new MaterialLayer(modId("turtle"), 0x47BF4A))
        );
    }

    private void addCompounds(Collection<MaterialBuilder> ret) {
        ret.add(compoundBuilder(modId("hybrid_gem"), ModItems.HYBRID_GEM));
        ret.add(compoundBuilder(modId("metal_alloy"), ModItems.ALLOY_INGOT));
        ret.add(compoundBuilder(modId("mixed_fabric"), ModItems.MIXED_FABRIC));

        // Dimerald
        ret.add(customCompoundBuilder(modId("dimerald"), 3, ModItems.CUSTOM_GEM.get())
                .categories(MaterialCategories.GEM)
                .mainStatsCommon(1776, 36, 12, 80, 0.7f)
                .mainStatsHarvest(3, 9)
                .mainStatsMelee(3, 3, 0.1f)
                .mainStatsRanged(3, 0.1f)
                .mainStatsArmor(4, 9, 6, 3, 10, 10) //22
                .stat(PartType.MAIN, ItemStats.PROJECTILE_SPEED, 1.0f)
                .stat(PartType.MAIN, ItemStats.PROJECTILE_ACCURACY, 1.2f)
                .stat(PartType.ROD, ItemStats.DURABILITY, 0.25f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.HARVEST_SPEED, 0.15f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RARITY, 70)
                .stat(PartType.TIP, ItemStats.DURABILITY, 360, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.HARVEST_LEVEL, 3, StatInstance.Operation.MAX)
                .stat(PartType.TIP, ItemStats.HARVEST_SPEED, 2, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.MELEE_DAMAGE, 2, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.MAGIC_DAMAGE, 1, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.RANGED_DAMAGE, 0.5f, StatInstance.Operation.ADD)
                .stat(PartType.TIP, ItemStats.RARITY, 25, StatInstance.Operation.ADD)
                .noStats(PartType.ADORNMENT)
                .trait(PartType.MAIN, Const.Traits.BRITTLE, 1)
                .trait(PartType.MAIN, Const.Traits.GOLD_DIGGER, 2, materialCountOrRatio(3, 0.5f))
                .trait(PartType.ROD, Const.Traits.BRITTLE, 4, new MaterialRatioTraitCondition(0.5f))
                .trait(PartType.ROD, Const.Traits.ANCIENT, 3, new MaterialRatioTraitCondition(0.5f))
                .trait(PartType.TIP, Const.Traits.IMPERIAL, 2)
                .trait(PartType.ADORNMENT, Const.Traits.KITTY_VISION, 1)
                .display(PartType.MAIN, PartTextureSet.HIGH_CONTRAST_WITH_HIGHLIGHT, 0x1ACE82)
                .display(PartType.ROD, PartTextureSet.HIGH_CONTRAST, 0x1ACE82)
                .displayTip(PartTextures.TIP_SHARP, 0x1ACE82)
                .displayAdornment(PartTextureSet.HIGH_CONTRAST_WITH_HIGHLIGHT, 0x1ACE82)
        );
    }

    private void addSimpleRods(Collection<MaterialBuilder> ret) {
        // Blaze Rod
        ret.add(new MaterialBuilder(modId("blaze_rod"), 3, Ingredient.EMPTY)
                .categories(MaterialCategories.METAL)
                .partSubstitute(PartType.ROD, Tags.Items.RODS_BLAZE)
                .stat(PartType.ROD, ItemStats.DURABILITY, 0.25f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.HARVEST_LEVEL, 2, StatInstance.Operation.MAX)
                .stat(PartType.ROD, ItemStats.HARVEST_SPEED, 0.1f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.MELEE_DAMAGE, 0.2f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RANGED_DAMAGE, 0.1f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RARITY, 30)
                .trait(PartType.ROD, Const.Traits.FLEXIBLE, 3)
                .trait(PartType.ROD, Const.Traits.REACH, 1)
                .display(PartType.ROD, PartTextureSet.HIGH_CONTRAST, 0xFFC600)
        );
        // Bone
        ret.add(new MaterialBuilder(modId("bone"), 1, Items.BONE_BLOCK)
                .categories(MaterialCategories.ORGANIC)
                .partSubstitute(PartType.ROD, Items.BONE)
                .stat(PartType.MAIN, ItemStats.DURABILITY, 108)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 4)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 5)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 1)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 4)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 2)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 1)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, 0.1f)
                .mainStatsArmor(1, 2, 1, 1, 0, 1) //5
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 1)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, 0.0f)
                .stat(PartType.MAIN, ItemStats.PROJECTILE_SPEED, 0.9f)
                .stat(PartType.MAIN, ItemStats.PROJECTILE_ACCURACY, 1f)
                .stat(PartType.MAIN, ItemStats.RARITY, 8)
                .stat(PartType.MAIN, ItemStats.CHARGEABILITY, 0.9f)
                .stat(PartType.ROD, ItemStats.MELEE_DAMAGE, 0.2f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RARITY, 8)
                .trait(PartType.MAIN, Const.Traits.CHIPPING, 2)
                .trait(PartType.ROD, Const.Traits.FLEXIBLE, 2)
                .display(PartType.MAIN, PartTextureSet.LOW_CONTRAST, 0xFCFBED)
                .display(PartType.ROD, PartTextureSet.LOW_CONTRAST, 0xFCFBED)
        );
        // End Rod
        ret.add(new MaterialBuilder(modId("end_rod"), 3, Ingredient.EMPTY)
                .categories(MaterialCategories.METAL)
                .partSubstitute(PartType.ROD, Items.END_ROD)
                .stat(PartType.ROD, ItemStats.DURABILITY, 0.35f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.HARVEST_LEVEL, 3, StatInstance.Operation.MAX)
                .stat(PartType.ROD, ItemStats.HARVEST_SPEED, 0.2f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.MELEE_DAMAGE, 0.1f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RANGED_DAMAGE, 0.1f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RARITY, 50)
                .trait(PartType.ROD, Const.Traits.FLEXIBLE, 2)
                .trait(PartType.ROD, Const.Traits.REFRACTIVE, 1)
                .display(PartType.ROD, PartTextureSet.HIGH_CONTRAST, 0xF6E2CD)
        );
    }

    private void addExtraMetals(Collection<MaterialBuilder> ret) {
        // Aluminum
        ret.add(extraMetal("aluminum", 2, forgeId("ingots/aluminum"))
                .categories(MaterialCategories.METAL)
                .stat(PartType.MAIN, ItemStats.DURABILITY, 365)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 15)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 14)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 2)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 8)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 2)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 2)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, 0.2f)
                .mainStatsArmor(2, 6, 4, 2, 0.5f, 3) //14
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 2)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, 0.2f)
                .stat(PartType.MAIN, ItemStats.RARITY, 25)
                .stat(PartType.MAIN, ItemStats.CHARGEABILITY, 0.9f)
                .stat(PartType.ROD, ItemStats.DURABILITY, 0.2f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RANGED_DAMAGE, 0.1f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RARITY, 30)
                .trait(PartType.MAIN, Const.Traits.MALLEABLE, 3)
                .trait(PartType.MAIN, Const.Traits.SOFT, 2, new MaterialRatioTraitCondition(0.5f))
                .trait(PartType.MAIN, Const.Traits.SYNERGISTIC, 1)
                .trait(PartType.ROD, Const.Traits.MALLEABLE, 3)
                .trait(PartType.ROD, Const.Traits.SYNERGISTIC, 2)
                .displayAll(PartTextureSet.HIGH_CONTRAST_WITH_HIGHLIGHT, 0xBFD4DE)
        );
        // Aluminum Steel
        ret.add(extraMetal("aluminum_steel", 3, forgeId("ingots/aluminum_steel"))
                .categories(MaterialCategories.METAL)
                .stat(PartType.MAIN, ItemStats.DURABILITY, 660)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 18)
                .stat(PartType.MAIN, ItemStats.REPAIR_VALUE, -0.15f)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 11)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 3)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 8)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 4)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 1.5f)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, 0.1f)
                .mainStatsArmor(3, 7, 5, 3, 2, 6) //18
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 2)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, 0f)
                .stat(PartType.MAIN, ItemStats.RARITY, 45)
                .stat(PartType.MAIN, ItemStats.CHARGEABILITY, 1.0f)
                .stat(PartType.ROD, ItemStats.DURABILITY, 0.3f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.HARVEST_SPEED, 0.1f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RARITY, 45)
                .trait(PartType.MAIN, Const.Traits.MALLEABLE, 4)
                .trait(PartType.MAIN, Const.Traits.SYNERGISTIC, 2, new MaterialRatioTraitCondition(0.5f))
                .trait(PartType.ROD, Const.Traits.MALLEABLE, 3)
                .trait(PartType.ROD, Const.Traits.SYNERGISTIC, 3)
                .displayAll(PartTextureSet.HIGH_CONTRAST_WITH_HIGHLIGHT, 0x98D9DA)
        );
        // Bismuth
        ret.add(extraMetal("bismuth", 2, forgeId("ingots/bismuth"))
                .categories(MaterialCategories.METAL)
                .stat(PartType.MAIN, ItemStats.DURABILITY, 330)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 10)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 12)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 2)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 5)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 2.5f)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 3.5f)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, 0.1f)
                .mainStatsArmor(3, 6, 5, 2, 2, 6) //16
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 1)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, 0f)
                .stat(PartType.MAIN, ItemStats.RARITY, 35)
                .stat(PartType.MAIN, ItemStats.CHARGEABILITY, 0.9f)
                .stat(PartType.ROD, ItemStats.DURABILITY, 0.1f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.HARVEST_SPEED, 0.1f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RARITY, 35)
                .trait(PartType.MAIN, Const.Traits.MALLEABLE, 2)
                .trait(PartType.MAIN, Const.Traits.LUSTROUS, 2, new MaterialRatioTraitCondition(0.5f))
                .trait(PartType.ROD, Const.Traits.MALLEABLE, 2)
                .displayAll(PartTextureSet.HIGH_CONTRAST_WITH_HIGHLIGHT, 0xD1C2D5)
        );
        // Bismuth Brass
        ret.add(extraMetal("bismuth_brass", 2, forgeId("ingots/bismuth_brass"))
                .categories(MaterialCategories.METAL)
                .stat(PartType.MAIN, ItemStats.DURABILITY, 580)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 15)
                .stat(PartType.MAIN, ItemStats.REPAIR_VALUE, 0.15f)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 14)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 2)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 9)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 3f)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 2f)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, 0.1f)
                .mainStatsArmor(3, 7, 5, 3, 2, 8) //18
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 2)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, 0.2f)
                .stat(PartType.MAIN, ItemStats.RARITY, 50)
                .stat(PartType.MAIN, ItemStats.CHARGEABILITY, 1.1f)
                .stat(PartType.ROD, ItemStats.DURABILITY, -0.1f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.HARVEST_SPEED, 0.2f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RARITY, 50)
                .trait(PartType.MAIN, Const.Traits.MALLEABLE, 2)
                .trait(PartType.MAIN, Const.Traits.LUSTROUS, 2, new MaterialRatioTraitCondition(0.5f))
                .trait(PartType.ROD, Const.Traits.MALLEABLE, 2)
                .displayAll(PartTextureSet.HIGH_CONTRAST_WITH_HIGHLIGHT, 0xE9C1B4)
        );
        // Bismuth Brass
        ret.add(extraMetal("bismuth_steel", 3, forgeId("ingots/bismuth_steel"))
                .categories(MaterialCategories.METAL)
                .stat(PartType.MAIN, ItemStats.DURABILITY, 1050)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 25)
                .stat(PartType.MAIN, ItemStats.REPAIR_VALUE, -0.15f)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 14)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 3)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 10)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 4f)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 5f)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, -0.1f)
                .mainStatsArmor(3, 8, 6, 3, 4, 8) //20
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 3)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, 0.1f)
                .stat(PartType.MAIN, ItemStats.RARITY, 60)
                .stat(PartType.MAIN, ItemStats.CHARGEABILITY, 1.0f)
                .stat(PartType.ROD, ItemStats.DURABILITY, 0.25f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.HARVEST_SPEED, 0.15f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RARITY, 60)
                .trait(PartType.MAIN, Const.Traits.MALLEABLE, 3)
                .trait(PartType.MAIN, Const.Traits.LUSTROUS, 2, new MaterialRatioTraitCondition(0.5f))
                .trait(PartType.ROD, Const.Traits.MALLEABLE, 3)
                .displayAll(PartTextureSet.HIGH_CONTRAST_WITH_HIGHLIGHT, 0xDC9FE7)
        );
        // Brass
        ret.add(extraMetal("brass", 2, forgeId("ingots/brass"))
                .categories(MaterialCategories.METAL)
                .stat(PartType.MAIN, ItemStats.DURABILITY, 240)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 8)
                .stat(PartType.MAIN, ItemStats.REPAIR_VALUE, 0.15f)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 13)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 2)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 7)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 1.5f)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 1.5f)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, 0.1f)
                .mainStatsArmor(2, 6, 4, 2, 1, 6) //14
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 2)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, 0.2f)
                .stat(PartType.MAIN, ItemStats.RARITY, 25)
                .stat(PartType.MAIN, ItemStats.CHARGEABILITY, 1.2f)
                .stat(PartType.ROD, ItemStats.DURABILITY, 0.05f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.HARVEST_SPEED, 0.05f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RARITY, 25)
                .trait(PartType.MAIN, Const.Traits.MALLEABLE, 2)
                .trait(PartType.MAIN, Const.Traits.SILKY, 1, new MaterialRatioTraitCondition(0.66f))
                .trait(PartType.ROD, Const.Traits.MALLEABLE, 2)
                .displayAll(PartTextureSet.HIGH_CONTRAST_WITH_HIGHLIGHT, 0xF2D458)
        );
        // Bronze
        ret.add(extraMetal("bronze", 2, forgeId("ingots/bronze"))
                .categories(MaterialCategories.METAL)
                .stat(PartType.MAIN, ItemStats.DURABILITY, 480)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 18)
                .stat(PartType.MAIN, ItemStats.REPAIR_VALUE, 0.15f)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 12)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 2)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 6)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 2.5f)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 1f)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, 0.2f)
                .mainStatsArmor(3, 7, 5, 2, 1, 6) //17
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 2)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, -0.2f)
                .stat(PartType.MAIN, ItemStats.RARITY, 35)
                .stat(PartType.MAIN, ItemStats.CHARGEABILITY, 1.2f)
                .stat(PartType.ROD, ItemStats.DURABILITY, 0.15f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.MELEE_DAMAGE, 0.05f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RARITY, 35)
                .trait(PartType.MAIN, Const.Traits.MALLEABLE, 3)
                .trait(PartType.MAIN, Const.Traits.ADAMANT, 1, new MaterialRatioTraitCondition(0.35f))
                .trait(PartType.ROD, Const.Traits.MALLEABLE, 3)
                .displayAll(PartTextureSet.HIGH_CONTRAST_WITH_HIGHLIGHT, 0xD96121)
        );
        // Compressed Iron
        ret.add(extraMetal("compressed_iron", 3, forgeId("ingots/compressed_iron"))
                .categories(MaterialCategories.METAL)
                .stat(PartType.MAIN, ItemStats.DURABILITY, 1024)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 24)
                .stat(PartType.MAIN, ItemStats.REPAIR_VALUE, -0.15f)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 12)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 3)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 9)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 4f)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 1f)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, -0.2f)
                .mainStatsArmor(3, 8, 6, 3, 2, 4) //20
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 3)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, 0.0f)
                .stat(PartType.MAIN, ItemStats.RARITY, 40)
                .stat(PartType.MAIN, ItemStats.CHARGEABILITY, 0.8f)
                .stat(PartType.ROD, ItemStats.DURABILITY, 0.25f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RARITY, 40)
                .trait(PartType.MAIN, Const.Traits.MALLEABLE, 2)
                .trait(PartType.MAIN, Const.Traits.HARD, 3)
                .trait(PartType.ROD, Const.Traits.MALLEABLE, 2)
                .trait(PartType.ROD, Const.Traits.MAGNETIC, 4, new MaterialRatioTraitCondition(0.5f))
                .displayAll(PartTextureSet.HIGH_CONTRAST_WITH_HIGHLIGHT, 0xA6A6A6)
        );
        // Electrum
        ret.add(extraMetal("electrum", 2, forgeId("ingots/electrum"))
                .categories(MaterialCategories.METAL)
                .stat(PartType.MAIN, ItemStats.DURABILITY, 96)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 10)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 25)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 2)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 14)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 2f)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 5f)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, 0.3f)
                .mainStatsArmor(2, 6, 5, 2, 0, 11) //15
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 1)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, 0.4f)
                .stat(PartType.MAIN, ItemStats.RARITY, 40)
                .stat(PartType.MAIN, ItemStats.CHARGEABILITY, 1.5f)
                .stat(PartType.ROD, ItemStats.ATTACK_SPEED, 0.1f, StatInstance.Operation.ADD)
                .stat(PartType.ROD, ItemStats.RANGED_SPEED, 0.1f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RARITY, 40)
                .trait(PartType.MAIN, Const.Traits.MALLEABLE, 2)
                .trait(PartType.MAIN, Const.Traits.SOFT, 2, new MaterialRatioTraitCondition(0.5f))
                .trait(PartType.ROD, Const.Traits.LUSTROUS, 3, new MaterialRatioTraitCondition(0.5f))
                .displayAll(PartTextureSet.HIGH_CONTRAST_WITH_HIGHLIGHT, 0xD6E037)
        );
        // Enderium
        ret.add(extraMetal("enderium", 4, forgeId("ingots/enderium"))
                .categories(MaterialCategories.METAL)
                .stat(PartType.MAIN, ItemStats.DURABILITY, 1200)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 34)
                .stat(PartType.MAIN, ItemStats.REPAIR_VALUE, 0.15f)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 13)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 4)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 18)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 6f)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 4f)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, 0.0f)
                .mainStatsArmor(3, 9, 7, 3, 8, 10) //22
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 3)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, 0f)
                .stat(PartType.MAIN, ItemStats.RARITY, 80)
                .stat(PartType.MAIN, ItemStats.CHARGEABILITY, 1.2f)
                .stat(PartType.ROD, ItemStats.DURABILITY, 0.2f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.HARVEST_SPEED, 0.2f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RARITY, 60)
                .trait(PartType.MAIN, Const.Traits.MALLEABLE, 3)
                .trait(PartType.ROD, Const.Traits.MALLEABLE, 2)
                .displayAll(PartTextureSet.HIGH_CONTRAST_WITH_HIGHLIGHT, 0x468C75)
        );
        // Invar
        ret.add(extraMetal("invar", 2, forgeId("ingots/invar"))
                .categories(MaterialCategories.METAL)
                .stat(PartType.MAIN, ItemStats.DURABILITY, 640)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 20)
                .stat(PartType.MAIN, ItemStats.REPAIR_VALUE, 0.15f)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 13)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 2)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 7)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 3.5f)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 3f)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, 0.0f)
                .mainStatsArmor(2, 8, 6, 2, 2, 6) //18
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 2)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, 0f)
                .stat(PartType.MAIN, ItemStats.RARITY, 50)
                .stat(PartType.MAIN, ItemStats.CHARGEABILITY, 1.2f)
                .stat(PartType.ROD, ItemStats.DURABILITY, 0.3f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.MELEE_DAMAGE, 0.1f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RARITY, 50)
                .trait(PartType.MAIN, Const.Traits.MALLEABLE, 3)
                .trait(PartType.MAIN, Const.Traits.ADAMANT, 2, materialCountOrRatio(3, 0.35f))
                .trait(PartType.ROD, Const.Traits.MALLEABLE, 3)
                .displayAll(PartTextureSet.HIGH_CONTRAST_WITH_HIGHLIGHT, 0xC2CBB8)
        );
        // Lead
        ret.add(extraMetal("lead", 2, forgeId("ingots/lead"))
                .categories(MaterialCategories.METAL)
                .stat(PartType.MAIN, ItemStats.DURABILITY, 260)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 14)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 15)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 2)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 4)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 2f)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 2f)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, -0.4f)
                .mainStatsArmor(2, 5, 4, 2, 0, 4) //13
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 1)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, -0.3f)
                .stat(PartType.MAIN, ItemStats.RARITY, 40)
                .stat(PartType.MAIN, ItemStats.CHARGEABILITY, 0.8f)
                .stat(PartType.ROD, ItemStats.DURABILITY, 0.1f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.HARVEST_SPEED, -0.1f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RARITY, 40)
                .trait(PartType.MAIN, Const.Traits.MALLEABLE, 2)
                .trait(PartType.MAIN, Const.Traits.AQUATIC, 2, materialCountOrRatio(3, 0.35f))
                .trait(PartType.ROD, Const.Traits.SOFT, 4)
                .displayAll(PartTextureSet.HIGH_CONTRAST_WITH_HIGHLIGHT, 0xC2CBB8)
        );
        // Lumium
        ret.add(extraMetal("lumium", 3, forgeId("ingots/lumium"))
                .categories(MaterialCategories.METAL)
                .stat(PartType.MAIN, ItemStats.DURABILITY, 920)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 20)
                .stat(PartType.MAIN, ItemStats.REPAIR_VALUE, 0.15f)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 14)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 3)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 15)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 4f)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 3f)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, 0.2f)
                .mainStatsArmor(2, 8, 6, 2, 4, 10) //18
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 2)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, 0f)
                .stat(PartType.MAIN, ItemStats.RARITY, 75)
                .stat(PartType.MAIN, ItemStats.CHARGEABILITY, 1.3f)
                .stat(PartType.ROD, ItemStats.HARVEST_SPEED, 0.2f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RANGED_DAMAGE, 0.1f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RARITY, 75)
                .trait(PartType.MAIN, Const.Traits.MALLEABLE, 3)
                .trait(PartType.MAIN, Const.Traits.REFRACTIVE, 1, new MaterialRatioTraitCondition(0.5f))
                .trait(PartType.ROD, Const.Traits.MALLEABLE, 3)
                .trait(PartType.ROD, Const.Traits.REFRACTIVE, 1, new MaterialRatioTraitCondition(0.5f))
                .displayAll(PartTextureSet.HIGH_CONTRAST_WITH_HIGHLIGHT, 0xFFD789)
        );
        // Nickel
        ret.add(extraMetal("nickel", 2, forgeId("ingots/nickel"))
                .categories(MaterialCategories.METAL)
                .stat(PartType.MAIN, ItemStats.DURABILITY, 380)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 17)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 12)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 2)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 7)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 2.5f)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 1f)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, 0.1f)
                .mainStatsArmor(2, 5, 4, 2, 0, 6) //13
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 0.5f)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, 0.1f)
                .stat(PartType.MAIN, ItemStats.RARITY, 40)
                .stat(PartType.MAIN, ItemStats.CHARGEABILITY, 1.0f)
                .stat(PartType.ROD, ItemStats.DURABILITY, 0.2f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.MELEE_DAMAGE, 0.1f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RARITY, 40)
                .trait(PartType.MAIN, Const.Traits.MALLEABLE, 3)
                .trait(PartType.MAIN, Const.Traits.ADAMANT, 1, materialCountOrRatio(3, 0.35f))
                .trait(PartType.ROD, Const.Traits.MALLEABLE, 3)
                .displayAll(PartTextureSet.HIGH_CONTRAST_WITH_HIGHLIGHT, 0xEFE87B)
        );
        // Osmium
        ret.add(extraMetal("osmium", 2, forgeId("ingots/osmium"))
                .categories(MaterialCategories.METAL)
                .stat(PartType.MAIN, ItemStats.DURABILITY, 500)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 30)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 12)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 2)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 10)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 4f)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 2f)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, 0.1f)
                .mainStatsArmor(3, 6, 5, 3, 0, 4) //17
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 1f)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, 0f)
                .stat(PartType.MAIN, ItemStats.RARITY, 35)
                .stat(PartType.MAIN, ItemStats.CHARGEABILITY, 1.1f)
                .stat(PartType.ROD, ItemStats.DURABILITY, 0.1f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.REPAIR_EFFICIENCY, 0.1f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RARITY, 35)
                .trait(PartType.MAIN, Const.Traits.MALLEABLE, 2)
                .trait(PartType.ROD, Const.Traits.MALLEABLE, 2)
                .displayAll(PartTextureSet.HIGH_CONTRAST_WITH_HIGHLIGHT, 0x92A6B8)
        );
        // Platinum
        ret.add(extraMetal("platinum", 3, forgeId("ingots/platinum"))
                .categories(MaterialCategories.METAL)
                .stat(PartType.MAIN, ItemStats.DURABILITY, 900)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 21)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 14)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 3)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 12)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 4f)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 4f)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, 0.0f)
                .mainStatsArmor(2, 8, 6, 2, 2, 12) //18
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 1f)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, 0f)
                .stat(PartType.MAIN, ItemStats.RARITY, 80)
                .stat(PartType.MAIN, ItemStats.CHARGEABILITY, 1.2f)
                .stat(PartType.ROD, ItemStats.DURABILITY, -0.1f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.REPAIR_EFFICIENCY, 0.1f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RARITY, 70)
                .trait(PartType.MAIN, Const.Traits.MALLEABLE, 3)
                .trait(PartType.MAIN, Const.Traits.SOFT, 2)
                .trait(PartType.ROD, Const.Traits.MALLEABLE, 2)
                .trait(PartType.ROD, Const.Traits.SOFT, 4)
                .displayAll(PartTextureSet.HIGH_CONTRAST_WITH_HIGHLIGHT, 0xB3B3FF)
        );
        // Redstone Alloy
        ret.add(extraMetal("redstone_alloy", 2, forgeId("ingots/redstone_alloy"))
                .categories(MaterialCategories.METAL)
                .stat(PartType.MAIN, ItemStats.DURABILITY, 840)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 20)
                .stat(PartType.MAIN, ItemStats.REPAIR_VALUE, -0.15f)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 18)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 2)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 11)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 2f)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 4f)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, 0.2f)
                .mainStatsArmor(3, 7, 5, 2, 1, 8) //17
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 1)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, 0.2f)
                .stat(PartType.MAIN, ItemStats.RARITY, 45)
                .stat(PartType.MAIN, ItemStats.CHARGEABILITY, 0.9f)
                .stat(PartType.ROD, ItemStats.HARVEST_SPEED, 0.2f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RARITY, 45)
                .trait(PartType.MAIN, Const.Traits.MALLEABLE, 3)
                .trait(PartType.MAIN, Const.Traits.ERODED, 3)
                .trait(PartType.ROD, Const.Traits.MALLEABLE, 3)
                .displayAll(PartTextureSet.HIGH_CONTRAST_WITH_HIGHLIGHT, 0xE60006)
        );
        // Refined glowstone
        ret.add(extraMetal("refined_glowstone", 3, forgeId("ingots/refined_glowstone"))
                .categories(MaterialCategories.METAL)
                .stat(PartType.MAIN, ItemStats.DURABILITY, 300)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 18)
                .stat(PartType.MAIN, ItemStats.REPAIR_VALUE, 0.15f)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 18)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 2)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 14)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 5f)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 3f)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, 0.0f)
                .mainStatsArmor(3, 7, 6, 3, 0, 8) //19
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 3)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, 0.0f)
                .stat(PartType.MAIN, ItemStats.RARITY, 45)
                .stat(PartType.MAIN, ItemStats.CHARGEABILITY, 0.8f)
                .stat(PartType.ROD, ItemStats.DURABILITY, -0.1f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.HARVEST_SPEED, 0.2f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RARITY, 45)
                .stat(PartType.TIP, ItemStats.HARVEST_SPEED, 5, StatInstance.Operation.ADD)
                .trait(PartType.MAIN, Const.Traits.MALLEABLE, 2)
                .trait(PartType.MAIN, Const.Traits.LUSTROUS, 4)
                .trait(PartType.ROD, Const.Traits.MALLEABLE, 4)
                .trait(PartType.ROD, Const.Traits.LUSTROUS, 1, new MaterialRatioTraitCondition(0.75f))
                .trait(PartType.TIP, Const.Traits.REFRACTIVE, 1)
                .displayAll(PartTextureSet.HIGH_CONTRAST_WITH_HIGHLIGHT, 0xFDE054)
        );
        // Refined Iron
        ret.add(extraMetal("refined_iron", 2, forgeId("ingots/refined_iron"))
                .categories(MaterialCategories.METAL)
                .stat(PartType.MAIN, ItemStats.DURABILITY, 512)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 20)
                .stat(PartType.MAIN, ItemStats.REPAIR_VALUE, -0.15f)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 15)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 2)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 7)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 3f)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 2f)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, 0.0f)
                .mainStatsArmor(3, 7, 5, 3, 2, 6) //18
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 2)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, 0.0f)
                .stat(PartType.MAIN, ItemStats.RARITY, 25)
                .stat(PartType.MAIN, ItemStats.CHARGEABILITY, 0.8f)
                .stat(PartType.ROD, ItemStats.DURABILITY, 0.15f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RANGED_DAMAGE, 0.1f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RARITY, 25)
                .trait(PartType.MAIN, Const.Traits.MALLEABLE, 4)
                .trait(PartType.MAIN, Const.Traits.STELLAR, 1)
                .trait(PartType.ROD, Const.Traits.MALLEABLE, 4)
                .trait(PartType.ROD, Const.Traits.MAGNETIC, 3, new MaterialRatioTraitCondition(0.35f))
                .displayAll(PartTextureSet.HIGH_CONTRAST_WITH_HIGHLIGHT, 0xD7D7D7)
        );
        // Refined obsidian
        ret.add(extraMetal("refined_obsidian", 4, forgeId("ingots/refined_obsidian"))
                .categories(MaterialCategories.METAL)
                .stat(PartType.MAIN, ItemStats.DURABILITY, 2500)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 50)
                .stat(PartType.MAIN, ItemStats.REPAIR_VALUE, -0.3f)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 40)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 3)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 20)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 10f)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 4f)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, 0.3f)
                .mainStatsArmor(5, 12, 8, 5, 16, 6) //30
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 4)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, 0.2f)
                .stat(PartType.MAIN, ItemStats.RARITY, 70)
                .stat(PartType.MAIN, ItemStats.CHARGEABILITY, 0.8f)
                .stat(PartType.ROD, ItemStats.DURABILITY, 0.25f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RARITY, 70)
                .stat(PartType.TIP, ItemStats.DURABILITY, 600, StatInstance.Operation.ADD)
                .trait(PartType.MAIN, Const.Traits.MALLEABLE, 3)
                .trait(PartType.MAIN, Const.Traits.HARD, 4)
                .trait(PartType.ROD, Const.Traits.MALLEABLE, 4)
                .trait(PartType.ROD, Const.Traits.HARD, 3)
                .trait(PartType.TIP, Const.Traits.VULCAN, 1)
                .displayAll(PartTextureSet.HIGH_CONTRAST_WITH_HIGHLIGHT, 0x665482)
        );
        // Signalum
        ret.add(extraMetal("signalum", 4, forgeId("ingots/signalum"))
                .categories(MaterialCategories.METAL)
                .stat(PartType.MAIN, ItemStats.DURABILITY, 800)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 25)
                .stat(PartType.MAIN, ItemStats.REPAIR_VALUE, 0.15f)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 16)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 3)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 13)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 3f)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 4f)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, 0.0f)
                .mainStatsArmor(2, 7, 5, 2, 2, 4) //16
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 2f)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, 0.2f)
                .stat(PartType.MAIN, ItemStats.RARITY, 50)
                .stat(PartType.MAIN, ItemStats.CHARGEABILITY, 1.2f)
                .stat(PartType.ROD, ItemStats.HARVEST_SPEED, 0.3f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.REPAIR_EFFICIENCY, -0.1f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RARITY, 50)
                .trait(PartType.MAIN, Const.Traits.FLEXIBLE, 2)
                .trait(PartType.MAIN, Const.Traits.LUSTROUS, 4)
                .trait(PartType.ROD, Const.Traits.FLEXIBLE, 4)
                .trait(PartType.ROD, Const.Traits.LUSTROUS, 2)
                .displayAll(PartTextureSet.HIGH_CONTRAST_WITH_HIGHLIGHT, 0xFF5E28)
        );
        // Silver
        ret.add(extraMetal("silver", 2, forgeId("ingots/silver"))
                .categories(MaterialCategories.METAL)
                .stat(PartType.MAIN, ItemStats.DURABILITY, 64)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 9)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 20)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 0)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 11)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 0f)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 4f)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, 0.2f)
                .mainStatsArmor(2, 5, 3, 1, 0, 10) //11
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 0f)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, 0.3f)
                .stat(PartType.MAIN, ItemStats.RARITY, 40)
                .stat(PartType.MAIN, ItemStats.CHARGEABILITY, 1.1f)
                .stat(PartType.ROD, ItemStats.HARVEST_SPEED, 0.1f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.MAGIC_DAMAGE, 0.1f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RARITY, 40)
                .trait(PartType.MAIN, Const.Traits.MALLEABLE, 2)
                .trait(PartType.MAIN, Const.Traits.SOFT, 1)
                .trait(PartType.ROD, Const.Traits.MALLEABLE, 1)
                .trait(PartType.ROD, Const.Traits.SOFT, 2)
                .displayAll(PartTextureSet.HIGH_CONTRAST_WITH_HIGHLIGHT, 0xCBCCEA)
        );
        // Steel
        ret.add(extraMetal("steel", 2, forgeId("ingots/steel"))
                .categories(MaterialCategories.METAL)
                .stat(PartType.MAIN, ItemStats.DURABILITY, 500)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 20)
                .stat(PartType.MAIN, ItemStats.REPAIR_VALUE, 0.15f)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 11)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 2)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 6)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 4f)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 1f)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, -0.2f)
                .mainStatsArmor(3, 8, 6, 3, 2, 6) //20
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 2f)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, -0.2f)
                .stat(PartType.MAIN, ItemStats.RARITY, 40)
                .stat(PartType.MAIN, ItemStats.CHARGEABILITY, 0.8f)
                .stat(PartType.ROD, ItemStats.DURABILITY, 0.25f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RARITY, 40)
                .trait(PartType.MAIN, Const.Traits.MALLEABLE, 5)
                .trait(PartType.ROD, Const.Traits.MALLEABLE, 5)
                .displayAll(PartTextureSet.HIGH_CONTRAST_WITH_HIGHLIGHT, 0x929292)
        );
        // Tin
        ret.add(extraMetal("tin", 1, forgeId("ingots/tin"))
                .categories(MaterialCategories.METAL)
                .stat(PartType.MAIN, ItemStats.DURABILITY, 192)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 13)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 12)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 1)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 5)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 1.5f)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 1f)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, 0.0f)
                .mainStatsArmor(2, 5, 3, 2, 0, 2) //12
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 0.5f)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, 0f)
                .stat(PartType.MAIN, ItemStats.RARITY, 15)
                .stat(PartType.MAIN, ItemStats.CHARGEABILITY, 1.1f)
                .stat(PartType.ROD, ItemStats.ATTACK_SPEED, 0.2f, StatInstance.Operation.ADD)
                .stat(PartType.ROD, ItemStats.RARITY, 15)
                .trait(PartType.MAIN, Const.Traits.MALLEABLE, 1)
                .trait(PartType.MAIN, Const.Traits.SOFT, 2)
                .trait(PartType.ROD, Const.Traits.MALLEABLE, 2)
                .trait(PartType.ROD, Const.Traits.SOFT, 1)
                .displayAll(PartTextureSet.HIGH_CONTRAST_WITH_HIGHLIGHT, 0x89A5B4)
        );
        // Titanium
        ret.add(extraMetal("titanium", 4, forgeId("ingots/titanium"))
                .categories(MaterialCategories.METAL)
                .stat(PartType.MAIN, ItemStats.DURABILITY, 1600)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 37)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 12)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 4)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 8)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 6f)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 1f)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, 0.0f)
                .mainStatsArmor(4, 9, 7, 4, 8, 4) //24
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 1f)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, -0.2f)
                .stat(PartType.MAIN, ItemStats.RARITY, 80)
                .stat(PartType.MAIN, ItemStats.CHARGEABILITY, 1.0f)
                .stat(PartType.ROD, ItemStats.DURABILITY, 0.1f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.MELEE_DAMAGE, 0.2f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.MELEE_DAMAGE, 2, StatInstance.Operation.ADD)
                .stat(PartType.ROD, ItemStats.RARITY, 80)
                .trait(PartType.MAIN, Const.Traits.MALLEABLE, 2)
                .trait(PartType.MAIN, Const.Traits.HARD, 4)
                .trait(PartType.ROD, Const.Traits.FLEXIBLE, 2)
                .trait(PartType.ROD, Const.Traits.HARD, 4)
                .displayAll(PartTextureSet.HIGH_CONTRAST_WITH_HIGHLIGHT, 0x2E4CE6)
        );
        // Uranium
        ret.add(extraMetal("uranium", 3, forgeId("ingots/uranium"))
                .categories(MaterialCategories.METAL)
                .stat(PartType.MAIN, ItemStats.DURABILITY, 800)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 20)
                .stat(PartType.MAIN, ItemStats.REPAIR_VALUE, -0.15f)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 17)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 3)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 6)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 2f)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 2f)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, 0.1f)
                .mainStatsArmor(2, 5, 4, 2, 1, 3) //13
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 0.5f)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, 0.1f)
                .stat(PartType.MAIN, ItemStats.RARITY, 50)
                .stat(PartType.MAIN, ItemStats.CHARGEABILITY, 1.5f)
                .stat(PartType.ROD, ItemStats.HARVEST_SPEED, 0.1f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RANGED_SPEED, 0.1f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RARITY, 40)
                .trait(PartType.MAIN, Const.Traits.MALLEABLE, 2)
                .trait(PartType.ROD, Const.Traits.MALLEABLE, 2)
                .displayAll(PartTextureSet.HIGH_CONTRAST_WITH_HIGHLIGHT, 0x21FF0F)
        );
        // Zinc
        ret.add(extraMetal("zinc", 1, forgeId("ingots/zinc"))
                .categories(MaterialCategories.METAL)
                .stat(PartType.MAIN, ItemStats.DURABILITY, 192)
                .stat(PartType.MAIN, ItemStats.ARMOR_DURABILITY, 10)
                .stat(PartType.MAIN, ItemStats.ENCHANTABILITY, 15)
                .stat(PartType.MAIN, ItemStats.HARVEST_LEVEL, 1)
                .stat(PartType.MAIN, ItemStats.HARVEST_SPEED, 3)
                .stat(PartType.MAIN, ItemStats.MELEE_DAMAGE, 1.5f)
                .stat(PartType.MAIN, ItemStats.MAGIC_DAMAGE, 1f)
                .stat(PartType.MAIN, ItemStats.ATTACK_SPEED, 0.0f)
                .mainStatsArmor(1, 5, 3, 1, 0, 2) //11
                .stat(PartType.MAIN, ItemStats.RANGED_DAMAGE, 0f)
                .stat(PartType.MAIN, ItemStats.RANGED_SPEED, 0.0f)
                .stat(PartType.MAIN, ItemStats.RARITY, 10)
                .stat(PartType.MAIN, ItemStats.CHARGEABILITY, 1.1f)
                .stat(PartType.ROD, ItemStats.DURABILITY, -0.05f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.MELEE_DAMAGE, 0.05f, StatInstance.Operation.MUL2)
                .stat(PartType.ROD, ItemStats.RARITY, 15)
                .trait(PartType.MAIN, Const.Traits.MALLEABLE, 1)
                .trait(PartType.MAIN, Const.Traits.SOFT, 2)
                .trait(PartType.ROD, Const.Traits.MALLEABLE, 2)
                .trait(PartType.ROD, Const.Traits.SOFT, 3)
                .displayAll(PartTextureSet.HIGH_CONTRAST_WITH_HIGHLIGHT, 0xC9D3CE)
        );
    }

    //endregion

    @SuppressWarnings("WeakerAccess")
    protected MaterialBuilder compoundBuilder(ResourceLocation id, ItemLike item) {
        return new MaterialBuilder(id, -1, item)
                .type(MaterialSerializers.COMPOUND, false)
                .categories(MaterialCategories.GEM);
    }

    @SuppressWarnings("WeakerAccess")
    protected MaterialBuilder customCompoundBuilder(ResourceLocation id, int tier, CustomMaterialItem item) {
        return new MaterialBuilder(id, tier, CustomCompoundIngredient.of(item, id))
                .type(MaterialSerializers.CUSTOM_COMPOUND, false);
    }

    private static MaterialBuilder extraMetal(String name, int tier, ResourceLocation tag) {
        return new MaterialBuilder(SilentGear.getId(name), tier, tag).loadConditionTagExists(tag);
    }

    private static MaterialBuilder terracotta(ResourceLocation parent, String suffix, ItemLike item, int color) {
        return new MaterialBuilder(new ResourceLocation(parent.getNamespace(), parent.getPath() + "/" + suffix), -1, item)
                .parent(parent)
                .display(PartType.MAIN, PartTextureSet.LOW_CONTRAST, color)
                .display(PartType.ROD, PartTextureSet.LOW_CONTRAST, color);
    }

    private static MaterialBuilder wood(String suffix, ItemLike item, int color) {
        ResourceLocation parent = Const.Materials.WOOD.getId();
        return new MaterialBuilder(new ResourceLocation(parent.getNamespace(), parent.getPath() + "/" + suffix), -1, item)
                .parent(parent)
                .display(PartType.MAIN, PartTextureSet.LOW_CONTRAST, color)
                .display(PartType.ROD, PartTextureSet.LOW_CONTRAST, color)
                .displayFragment(PartTextures.WOOD, color);
    }

    private static MaterialBuilder wool(String suffix, ItemLike item, int color) {
        ResourceLocation parent = Const.Materials.WOOL.getId();
        return new MaterialBuilder(new ResourceLocation(parent.getNamespace(), parent.getPath() + "/" + suffix), -1, item)
                .parent(parent)
                .display(PartType.MAIN, PartTextureSet.LOW_CONTRAST, color)
                .display(PartType.GRIP, PartTextureSet.LOW_CONTRAST, color)
                .displayLining(PartTextureSet.LOW_CONTRAST, color)
                .displayFragment(PartTextures.CLOTH, color);
    }

    @SuppressWarnings({"WeakerAccess", "SameParameterValue"})
    protected static ITraitCondition materialCountOrRatio(int count, float ratio) {
        return new OrTraitCondition(new MaterialCountTraitCondition(count), new MaterialRatioTraitCondition(ratio));
    }

    @SuppressWarnings("WeakerAccess")
    protected static ResourceLocation forgeId(String path) {
        return new ResourceLocation("forge", path);
    }

    protected ResourceLocation modId(String path) {
        return new ResourceLocation(this.modId, path);
    }

    @Override
    public void run(HashCache cache) {
        Path outputFolder = this.generator.getOutputFolder();

        for (MaterialBuilder builder : getMaterials()) {
            try {
                String jsonStr = GSON.toJson(builder.serialize());
                String hashStr = SHA1.hashUnencodedChars(jsonStr).toString();
                Path path = outputFolder.resolve(String.format("data/%s/silentgear_materials/%s.json", builder.id.getNamespace(), builder.id.getPath()));
                if (!Objects.equals(cache.getHash(outputFolder), hashStr) || !Files.exists(path)) {
                    Files.createDirectories(path.getParent());

                    try (BufferedWriter writer = Files.newBufferedWriter(path)) {
                        writer.write(jsonStr);
                    }
                }

                cache.putNew(path, hashStr);
            } catch (IOException ex) {
                LOGGER.error("Could not save materials to {}", outputFolder, ex);
            }

            // Model data
            // Unfortunately, these files still need to exist to allow models to find all the
            // textures. There may be some way to work around the issue and reduce data duplication
            // in the future.
            try {
                JsonObject json = builder.serializeModel();
                if (json.entrySet().isEmpty()) {
                    continue;
                }

                String jsonStr = GSON.toJson(json);
                String hashStr = SHA1.hashUnencodedChars(jsonStr).toString();
                // TODO: change path?
                Path path = outputFolder.resolve(String.format("assets/%s/silentgear_materials/%s.json", builder.id.getNamespace(), builder.id.getPath()));
                if (!Objects.equals(cache.getHash(outputFolder), hashStr) || !Files.exists(path)) {
                    Files.createDirectories(path.getParent());

                    try (BufferedWriter writer = Files.newBufferedWriter(path)) {
                        writer.write(jsonStr);
                    }
                }

                cache.putNew(path, hashStr);
            } catch (IOException ex) {
                LOGGER.error("Could not save material models to {}", outputFolder, ex);
            }
        }
    }
}
