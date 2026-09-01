package net.silentchaos512.gear.data;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.DataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;


import net.neoforged.neoforge.common.Tags;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.data.material.MaterialBuilder;
import net.silentchaos512.gear.api.data.material.MaterialsProviderBase;
import net.silentchaos512.gear.api.material.IMaterialCategory;
import net.silentchaos512.gear.api.material.Material;
import net.silentchaos512.gear.api.material.MaterialCraftingData;
import net.silentchaos512.gear.api.material.TextureType;
import net.silentchaos512.gear.api.property.BooleanPropertyValue;
import net.silentchaos512.gear.api.property.HarvestTier;
import net.silentchaos512.gear.api.property.HarvestTierPropertyValue;
import net.silentchaos512.gear.api.property.NumberProperty;
import net.silentchaos512.gear.api.traits.ITraitCondition;
import net.silentchaos512.gear.api.util.DataResource;
import net.silentchaos512.gear.api.util.PartGearKey;
import net.silentchaos512.gear.client.material.SgEquippableInfo;
import net.silentchaos512.gear.core.BuiltinMaterials;
import net.silentchaos512.gear.crafting.ingredient.CustomAlloyIngredient;
import net.silentchaos512.gear.crafting.ingredient.OptionalTagIngredient;
import net.silentchaos512.gear.gear.material.CompoundMaterial;
import net.silentchaos512.gear.gear.material.CustomCompoundMaterial;
import net.silentchaos512.gear.gear.material.MaterialCategories;
import net.silentchaos512.gear.gear.material.SimpleMaterial;
import net.silentchaos512.gear.gear.trait.condition.*;
import net.silentchaos512.gear.item.CraftingItems;
import net.silentchaos512.gear.item.CustomMaterialItem;
import net.silentchaos512.gear.setup.SgBlocks;
import net.silentchaos512.gear.setup.SgItems;
import net.silentchaos512.gear.setup.SgTags;
import net.silentchaos512.gear.setup.gear.GearProperties;
import net.silentchaos512.gear.setup.gear.GearTypes;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.gear.util.Const;
import net.silentchaos512.gear.util.TextUtil;
import net.silentchaos512.lib.util.Color;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class MaterialsProvider extends MaterialsProviderBase {
    private HolderGetter<Item> items;

    public MaterialsProvider(CompletableFuture<HolderLookup.Provider> lookupProvider, DataGenerator generator) {
        super(lookupProvider, generator, SilentGear.MOD_ID);
    }

    @Override
    protected Collection<MaterialBuilder<?>> getMaterials(HolderLookup.Provider registries) {
        this.items = registries.lookupOrThrow(Registries.ITEM);
        Collection<MaterialBuilder<?>> ret = new ArrayList<>();

        addCraftedMaterials(ret, registries);

        addIntangibles(ret, registries);
        addModMetals(ret, registries);
        addVanillaMetals(ret, registries);
        addGems(ret, registries);
        addDusts(ret, registries);
        addStones(ret, registries);
        addWoods(ret, registries);

        addClothLikes(ret, registries);
        addStringsAndFibers(ret, registries);
        addRandomOrganics(ret, registries);

        addCompounds(ret, registries);
        addSimpleRods(ret, registries);

        addExtraMetals(ret, registries);

        return ret;
    }

    //region Material builders

    private void addCraftedMaterials(Collection<MaterialBuilder<?>> ret, HolderLookup.Provider registries) {
        ret.add(MaterialBuilder.processed(modId("sheet_metal"))
                .crafting(SgItems.SHEET_METAL, MaterialCategories.SHEET)
                .stat(PartTypes.MAIN, GearProperties.DURABILITY, -1, NumberProperty.Operation.MULTIPLY_TOTAL)
                .stat(PartTypes.MAIN, GearProperties.ARMOR_DURABILITY, -0.4f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .stat(PartTypes.MAIN, GearProperties.ARMOR, -0.5f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .stat(PartTypes.MAIN, GearProperties.ARMOR_TOUGHNESS, -0.5f, NumberProperty.Operation.MULTIPLY_TOTAL)
        );
    }

    private void addIntangibles(Collection<MaterialBuilder<?>> ret, HolderLookup.Provider registries) {
        // Barrier
        ret.add(MaterialBuilder.simple(modId("barrier"))
                .crafting(new MaterialCraftingData(
                        Ingredient.of(Items.BARRIER),
                        Collections.singletonList(MaterialCategories.INTANGIBLE),
                        Collections.emptyList(),
                        Collections.emptyMap(),
                        false)
                )
                .display(Component.translatable(Items.BARRIER.getDescriptionId()), 0xFF0000)
                .mainStatsCommon(1337, 84, 5, 111, 0.5f)
                .stat(PartTypes.MAIN, GearProperties.REPAIR_VALUE, -1f)
                .mainStatsHarvest(HarvestTier.WOOD, 5)
                .mainStatsMelee(1, 1, 0f)
                .mainStatsRanged(1, 0f)
                .mainStatsArmor(3, 8, 6, 3, 10, 10)
                .trait(PartTypes.MAIN, Const.Traits.ADAMANT, 5)
                .trait(PartTypes.MAIN, Const.Traits.HOLY, 5)
        );

        // Example
        ret.add(MaterialBuilder.simple(modId("example"))
                .crafting(new MaterialCraftingData(
                        Optional.empty(),
                        Collections.singletonList(MaterialCategories.INTANGIBLE),
                        Collections.singletonList(GearTypes.ALL.get()),
                        Collections.emptyMap(),
                        false)
                )
                .displayWithDefaultName(Color.VALUE_WHITE, TextureType.LOW_CONTRAST)
                .mainStatsCommon(100, 6, 1, 0, 1f)
                .mainStatsHarvest(HarvestTier.WOOD, 1)
                .mainStatsMelee(1, 1, 0f)
                .mainStatsRanged(0, 0f)
                .mainStatsArmor(1, 1, 1, 1, 1, 1)
                .noProperties(PartTypes.ROD)
                .noProperties(PartTypes.TIP)
                .noProperties(PartTypes.COATING)
                .noProperties(PartTypes.GRIP)
                .noProperties(PartTypes.BINDING)
                .noProperties(PartTypes.LINING)
                .noProperties(PartTypes.CORD)
                .noProperties(PartTypes.FLETCHING)
                .noProperties(PartTypes.SETTING)
        );
    }

    private void addModMetals(Collection<MaterialBuilder<?>> ret, HolderLookup.Provider registries) {
        // Azure Electrum
        ret.add(MaterialBuilder.builtin(BuiltinMaterials.AZURE_ELECTRUM)
                .craftingWithRodSubstitute(this.items, SgTags.Items.INGOTS_AZURE_ELECTRUM, SgTags.Items.RODS_AZURE_ELECTRUM, MaterialCategories.METAL, MaterialCategories.ENDGAME)
                .displayWithDefaultName(0x4575E3, TextureType.HIGH_CONTRAST)
                //main
                .mainStatsCommon(1259, 61, 37, 109, 1.5f)
                .stat(PartTypes.MAIN, GearProperties.REPAIR_VALUE, 0.5f)
                .mainStatsHarvest(29)
                .mainStatsMelee(7, 11, 0.0f)
                .mainStatsRanged(3, 0.0f)
                .mainStatsProjectile(2f, 1.5f)
                .mainStatsArmor(3, 7, 6, 3, 8, 19) //19
                .trait(PartTypes.MAIN, Const.Traits.MALLEABLE, 3)
                .trait(PartTypes.MAIN, Const.Traits.ACCELERATE, 3, new MaterialRatioTraitCondition(0.35f))
                .trait(PartTypes.MAIN, Const.Traits.LIGHT, 4, new MaterialRatioTraitCondition(0.5f))
                //rod
                .stat(PartTypes.ROD, GearProperties.HARVEST_SPEED, 3, NumberProperty.Operation.ADD)
                .stat(PartTypes.ROD, GearProperties.PROJECTILE_SPEED, 1.5f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .trait(PartTypes.ROD, Const.Traits.FLEXIBLE, 2)
                .trait(PartTypes.ROD, Const.Traits.ACCELERATE, 5, new MaterialRatioTraitCondition(0.66f))
                //tip
                .stat(PartTypes.TIP, GearProperties.DURABILITY, 401, NumberProperty.Operation.ADD)
                .stat(PartTypes.TIP, GearProperties.ARMOR_DURABILITY, 11, NumberProperty.Operation.ADD)
                .harvestTierBuiltin(PartTypes.TIP)
                .stat(PartTypes.TIP, GearProperties.HARVEST_SPEED, 5, NumberProperty.Operation.ADD)
                .trait(PartTypes.TIP, Const.Traits.MALLEABLE, 3)
        );
        // Azure Silver
        ret.add(MaterialBuilder.builtin(BuiltinMaterials.AZURE_SILVER)
                .craftingWithRodSubstitute(this.items, SgTags.Items.INGOTS_AZURE_SILVER, SgTags.Items.RODS_AZURE_SILVER, MaterialCategories.METAL, MaterialCategories.ADVANCED)
                .displayWithDefaultName(0xCBBAFF, TextureType.HIGH_CONTRAST)
                //main
                .mainStatsCommon(197, 17, 29, 83, 1.4f)
                .stat(PartTypes.MAIN, GearProperties.REPAIR_VALUE, 0.5f)
                .mainStatsHarvest(19)
                .mainStatsMelee(5, 7, 0.0f)
                .mainStatsRanged(2, 0.0f)
                .mainStatsProjectile(1.2f, 1.1f)
                .mainStatsArmor(2, 5, 4, 2, 0, 13) //13
                .trait(PartTypes.MAIN, Const.Traits.MALLEABLE, 3)
                .trait(PartTypes.MAIN, Const.Traits.SOFT, 2)
                .trait(PartTypes.MAIN, Const.Traits.MOONWALKER, 4, new MaterialRatioTraitCondition(0.5f))
                //rod
                .stat(PartTypes.ROD, GearProperties.DURABILITY, -0.2f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .stat(PartTypes.ROD, GearProperties.HARVEST_SPEED, 3, NumberProperty.Operation.ADD)
                .trait(PartTypes.ROD, Const.Traits.BENDING, 2)
                //tip
                .stat(PartTypes.TIP, GearProperties.DURABILITY, 83, NumberProperty.Operation.ADD)
                .stat(PartTypes.TIP, GearProperties.ARMOR_DURABILITY, 3, NumberProperty.Operation.ADD)
                .harvestTierBuiltin(PartTypes.TIP)
                .stat(PartTypes.TIP, GearProperties.HARVEST_SPEED, 3, NumberProperty.Operation.ADD)
                .stat(PartTypes.TIP, GearProperties.MAGIC_DAMAGE, 2, NumberProperty.Operation.ADD)
                .stat(PartTypes.TIP, GearProperties.ATTACK_SPEED, 0.2f, NumberProperty.Operation.ADD)
                .stat(PartTypes.TIP, GearProperties.RARITY, 31, NumberProperty.Operation.ADD)
                .trait(PartTypes.TIP, Const.Traits.MALLEABLE, 2)
                .trait(PartTypes.TIP, Const.Traits.FORTUNATE, 3)
        );
        // Blaze Gold
        ret.add(MaterialBuilder.builtin(BuiltinMaterials.BLAZE_GOLD)
                .craftingWithRodSubstitute(this.items, SgTags.Items.INGOTS_BLAZE_GOLD, SgTags.Items.RODS_BLAZE_GOLD, MaterialCategories.METAL, MaterialCategories.ADVANCED)
                .displayWithDefaultName(0xDD8500)
                //main
                .mainStatsCommon(69, 9, 24, 45, 1.2f)
                .mainStatsHarvest(15)
                .mainStatsMelee(2, 5, 0.1f)
                .mainStatsRanged(1, 0.2f)
                .mainStatsProjectile(1.2f, 0.9f)
                .mainStatsArmor(2, 5, 4, 2, 1, 10) //13
                .trait(PartTypes.MAIN, Const.Traits.BRILLIANT, 1, new MaterialRatioTraitCondition(0.7f))
                .trait(PartTypes.MAIN, Const.Traits.GREEDY, 3)
                .trait(PartTypes.MAIN, Const.Traits.MALLEABLE, 3)
                //rod
                .stat(PartTypes.ROD, GearProperties.HARVEST_SPEED, 0.1f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .trait(PartTypes.ROD, Const.Traits.FLEXIBLE, 3)
                //tip
                .stat(PartTypes.TIP, GearProperties.DURABILITY, 32, NumberProperty.Operation.ADD)
                .stat(PartTypes.TIP, GearProperties.ARMOR_DURABILITY, 3, NumberProperty.Operation.ADD)
                .harvestTierBuiltin(PartTypes.TIP)
                .stat(PartTypes.TIP, GearProperties.HARVEST_SPEED, 4, NumberProperty.Operation.ADD)
                .stat(PartTypes.TIP, GearProperties.ATTACK_DAMAGE, 1, NumberProperty.Operation.ADD)
                .stat(PartTypes.TIP, GearProperties.MAGIC_DAMAGE, 1, NumberProperty.Operation.ADD)
                .stat(PartTypes.TIP, GearProperties.RARITY, 14, NumberProperty.Operation.ADD)
                .trait(PartTypes.TIP, Const.Traits.SOFT, 2)
                .trait(PartTypes.TIP, Const.Traits.FIERY, 2)
                //coating
                .stat(PartTypes.COATING, GearProperties.DURABILITY, -0.05f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .stat(PartTypes.COATING, GearProperties.ARMOR_DURABILITY, -0.05f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .stat(PartTypes.COATING, GearProperties.RARITY, 20, NumberProperty.Operation.ADD)
                .trait(PartTypes.COATING, Const.Traits.BRILLIANT, 1)
                .trait(PartTypes.COATING, Const.Traits.SOFT, 2)
        );
        // Bronze
        ret.add(MaterialBuilder.builtin(BuiltinMaterials.BRONZE)
                .craftingWithRodSubstitute(this.items, SgTags.Items.INGOTS_BRONZE, SgTags.Items.RODS_BRONZE, MaterialCategories.METAL, MaterialCategories.INTERMEDIATE)
                .displayWithDefaultName(0xD6903B, TextureType.HIGH_CONTRAST)
                .mainStatsCommon(300, 13, 12, 15, 1.1f)
                .stat(PartTypes.MAIN, GearProperties.REPAIR_VALUE, 0.15f)
                .mainStatsHarvest(6)
                .mainStatsMelee(2.5f, 1f, 0.2f)
                .mainStatsRanged(2, -0.2f)
                .mainStatsArmor(3, 6, 4, 2, 1, 6) //15
                .trait(PartTypes.MAIN, Const.Traits.SHARP, 1)
                // rod
                .stat(PartTypes.ROD, GearProperties.ATTACK_DAMAGE, 0.05f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .trait(PartTypes.ROD, Const.Traits.FLEXIBLE, 1)
                //tip
                .stat(PartTypes.TIP, GearProperties.DURABILITY, 96, NumberProperty.Operation.ADD)
                .stat(PartTypes.TIP, GearProperties.ARMOR_DURABILITY, 2, NumberProperty.Operation.ADD)
                .harvestTierBuiltin(PartTypes.TIP)
                .stat(PartTypes.TIP, GearProperties.HARVEST_SPEED, 0.75f, NumberProperty.Operation.ADD)
                .stat(PartTypes.TIP, GearProperties.ATTACK_DAMAGE, 0.75f, NumberProperty.Operation.ADD)
                .stat(PartTypes.TIP, GearProperties.ATTACK_SPEED, 0.1f, NumberProperty.Operation.ADD)
                .stat(PartTypes.TIP, GearProperties.RARITY, 6, NumberProperty.Operation.ADD)
                .trait(PartTypes.TIP, Const.Traits.MALLEABLE, 2)
        );
        // Crimson Iron
        ret.add(MaterialBuilder.builtin(BuiltinMaterials.CRIMSON_IRON)
                .craftingWithRodSubstitute(this.items, SgTags.Items.INGOTS_CRIMSON_IRON, SgTags.Items.RODS_CRIMSON_IRON, MaterialCategories.METAL, MaterialCategories.ADVANCED)
                .displayWithDefaultName(0xFF6189, TextureType.HIGH_CONTRAST)
                //main
                .mainStatsCommon(420, 27, 14, 31, 0.7f)
                .stat(PartTypes.MAIN, GearProperties.REPAIR_VALUE, 0.5f)
                .mainStatsHarvest(10)
                .mainStatsMelee(3, 3, -0.1f)
                .mainStatsRanged(2, -0.1f)
                .mainStatsProjectile(1, 1.1f)
                .mainStatsArmor(3, 7, 5, 3, 2, 6) //18
                .trait(PartTypes.MAIN, Const.Traits.MALLEABLE, 3)
                .trait(PartTypes.MAIN, Const.Traits.HARD, 2)
                .trait(PartTypes.MAIN, Const.Traits.HEAT_RESISTANT, 4)
                //rod
                .stat(PartTypes.ROD, GearProperties.ATTACK_DAMAGE, 0.1f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .trait(PartTypes.ROD, Const.Traits.FLEXIBLE, 3)
                //tip
                .stat(PartTypes.TIP, GearProperties.DURABILITY, 224, NumberProperty.Operation.ADD)
                .stat(PartTypes.TIP, GearProperties.ARMOR_DURABILITY, 8, NumberProperty.Operation.ADD)
                .harvestTierBuiltin(PartTypes.TIP)
                .stat(PartTypes.TIP, GearProperties.HARVEST_SPEED, 2, NumberProperty.Operation.ADD)
                .stat(PartTypes.TIP, GearProperties.ATTACK_DAMAGE, 2, NumberProperty.Operation.ADD)
                .stat(PartTypes.TIP, GearProperties.RARITY, 10, NumberProperty.Operation.ADD)
                .trait(PartTypes.TIP, Const.Traits.FIERY, 1)
        );
        // Crimson Steel
        ret.add(MaterialBuilder.builtin(BuiltinMaterials.CRIMSON_STEEL)
                .craftingWithRodSubstitute(this.items, SgTags.Items.INGOTS_CRIMSON_STEEL, SgTags.Items.RODS_CRIMSON_STEEL, MaterialCategories.METAL, MaterialCategories.ENDGAME)
                .displayWithDefaultName(0xDC143C, TextureType.HIGH_CONTRAST, SgEquippableInfo.CRIMSON_STEEL)
                //main
                .mainStatsCommon(2400, 42, 19, 83, 0.9f)
                .stat(PartTypes.MAIN, GearProperties.REPAIR_VALUE, 0.5f)
                .mainStatsHarvest(15)
                .mainStatsMelee(6, 6, -0.1f)
                .mainStatsRanged(3, -0.1f)
                .mainStatsProjectile(1f, 1.3f)
                .mainStatsArmor(4, 8, 6, 4, 10, 10) //22
                .trait(PartTypes.MAIN, Const.Traits.MALLEABLE, 5)
                .trait(PartTypes.MAIN, Const.Traits.HARD, 3)
                .trait(PartTypes.MAIN, Const.Traits.FLAME_WARD, 1, materialCountOrRatio(3, 0.33f))
                //rod
                .stat(PartTypes.ROD, GearProperties.HARVEST_SPEED, 0.2f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .stat(PartTypes.ROD, GearProperties.RANGED_DAMAGE, 1, NumberProperty.Operation.ADD)
                .trait(PartTypes.ROD, Const.Traits.FLEXIBLE, 4)
                .trait(PartTypes.ROD, Const.Traits.STURDY, 1, new MaterialRatioTraitCondition(0.5f))
                //tip
                .stat(PartTypes.TIP, GearProperties.DURABILITY, 448, NumberProperty.Operation.ADD)
                .stat(PartTypes.TIP, GearProperties.ARMOR_DURABILITY, 16, NumberProperty.Operation.ADD)
                .harvestTierBuiltin(PartTypes.TIP)
                .stat(PartTypes.TIP, GearProperties.RARITY, 20, NumberProperty.Operation.ADD)
                .trait(PartTypes.TIP, Const.Traits.MAGMATIC, 1)
        );
        // Tyrian Steel
        ret.add(MaterialBuilder.builtin(BuiltinMaterials.TYRIAN_STEEL)
                .craftingWithRodSubstitute(this.items, SgTags.Items.INGOTS_TYRIAN_STEEL, SgTags.Items.RODS_TYRIAN_STEEL, MaterialCategories.METAL, MaterialCategories.ENDGAME)
                .displayWithDefaultName(0xB01080, TextureType.HIGH_CONTRAST)
                //main
                .mainStatsCommon(3652, 81, 16, 100, 1.1f)
                .mainStatsHarvest(18)
                .mainStatsMelee(8, 6, 0.0f)
                .mainStatsRanged(4, 0.0f)
                .mainStatsProjectile(1.1f, 1.1f)
                .mainStatsArmor(5, 9, 7, 4, 12, 12) //25
                .trait(PartTypes.MAIN, Const.Traits.STURDY, 3, new MaterialRatioTraitCondition(0.5f))
                .trait(PartTypes.MAIN, Const.Traits.VOID_WARD, 1, materialCountOrRatio(3, 0.5f))
                //rod
                .stat(PartTypes.ROD, GearProperties.HARVEST_SPEED, -2, NumberProperty.Operation.ADD)
                .trait(PartTypes.ROD, Const.Traits.STURDY, 4, new MaterialRatioTraitCondition(0.5f))
                //tip
                .stat(PartTypes.TIP, GearProperties.DURABILITY, 251, NumberProperty.Operation.ADD)
                .stat(PartTypes.TIP, GearProperties.RARITY, 30, NumberProperty.Operation.ADD)
                .harvestTierBuiltin(PartTypes.TIP)
                .trait(PartTypes.TIP, Const.Traits.IMPERIAL, 3)
                .trait(PartTypes.TIP, Const.Traits.GOLD_DIGGER, 3)
        );
    }

    private void addVanillaMetals(Collection<MaterialBuilder<?>> ret, HolderLookup.Provider registries) {
        // Copper
        ret.add(MaterialBuilder.builtin(BuiltinMaterials.COPPER)
                .craftingWithRodSubstitute(this.items, Tags.Items.INGOTS_COPPER, SgTags.Items.RODS_COPPER, MaterialCategories.METAL, MaterialCategories.BASIC)
                .displayWithDefaultName(0xFD804C, TextureType.HIGH_CONTRAST)
                //main
                .mainStatsCommon(190, 11, 13, 12, 1.3f)
                .mainStatsHarvest(5)
                .mainStatsMelee(1.0f, 1.0f, 0.0f)
                .stat(PartGearKey.ofMain(GearTypes.AXE), GearProperties.ATTACK_SPEED, -0.1f)
                .stat(PartGearKey.ofMain(GearTypes.HOE), GearProperties.ATTACK_SPEED, 0f)
                .mainStatsRanged(0.1f, 0.0f)
                .mainStatsArmor(2, 4, 3, 1, 0, 8) //10
                .trait(PartTypes.MAIN, Const.Traits.POLISHED, 1, new MaterialRatioTraitCondition(1f))
                //rod
                .stat(PartTypes.ROD, GearProperties.HARVEST_SPEED, 0.2f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .trait(PartTypes.ROD, Const.Traits.BENDING, 3)
                .trait(PartTypes.ROD, Const.Traits.SOFT, 3)
                //tip
                .stat(PartTypes.TIP, GearProperties.DURABILITY, 32, NumberProperty.Operation.ADD)
                .stat(PartTypes.TIP, GearProperties.ARMOR_DURABILITY, 1, NumberProperty.Operation.ADD)
                .harvestTierBuiltin(PartTypes.TIP)
                .stat(PartTypes.TIP, GearProperties.HARVEST_SPEED, 1, NumberProperty.Operation.ADD)
                .stat(PartTypes.TIP, GearProperties.RARITY, 4, NumberProperty.Operation.ADD)
                .trait(PartTypes.TIP, Const.Traits.MALLEABLE, 1)
                .trait(PartTypes.TIP, Const.Traits.DULLING, 1)
        );
        // Gold
        ret.add(MaterialBuilder.builtin(BuiltinMaterials.GOLD)
                .craftingWithRodSubstitute(this.items, Tags.Items.INGOTS_GOLD, SgTags.Items.RODS_GOLD, MaterialCategories.METAL, MaterialCategories.INTERMEDIATE)
                .displayWithDefaultName(0xFDFF70, TextureType.HIGH_CONTRAST)
                //main
                .mainStatsCommon(32, 7, 22, 50, 1.2f)
                .mainStatsHarvest(12)
                .mainStatsMelee(0, 4, 0.0f)
                .stat(PartGearKey.ofMain(GearTypes.HOE), GearProperties.ATTACK_SPEED, -2f)
                .mainStatsRanged(0, 0.3f)
                .mainStatsProjectile(1.1f, 1.0f)
                .mainStatsArmor(2, 5, 3, 1, 0, 8) //11
                .trait(PartTypes.MAIN, Const.Traits.BRILLIANT, 1, new MaterialRatioTraitCondition(0.7f))
                .trait(PartTypes.MAIN, Const.Traits.MALLEABLE, 1)
                .trait(PartTypes.MAIN, Const.Traits.SOFT, 3)
                //rod
                .stat(PartTypes.ROD, GearProperties.HARVEST_SPEED, 3, NumberProperty.Operation.ADD)
                .trait(PartTypes.ROD, Const.Traits.BENDING, 4)
                //tip
                .stat(PartTypes.TIP, GearProperties.DURABILITY, 16, NumberProperty.Operation.ADD)
                .stat(PartTypes.TIP, GearProperties.ARMOR_DURABILITY, 1, NumberProperty.Operation.ADD)
                .stat(PartTypes.TIP, GearProperties.HARVEST_SPEED, 6, NumberProperty.Operation.ADD)
                .stat(PartTypes.TIP, GearProperties.MAGIC_DAMAGE, 2, NumberProperty.Operation.ADD)
                .stat(PartTypes.TIP, GearProperties.DRAW_SPEED, 0.2f, NumberProperty.Operation.ADD)
                .stat(PartTypes.TIP, GearProperties.RARITY, 30, NumberProperty.Operation.ADD)
                .trait(PartTypes.TIP, Const.Traits.MALLEABLE, 1)
                .trait(PartTypes.TIP, Const.Traits.SOFT, 3)
                //coating
                .stat(PartTypes.COATING, GearProperties.DURABILITY, -0.1f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .stat(PartTypes.COATING, GearProperties.ARMOR_DURABILITY, -0.1f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .stat(PartTypes.COATING, GearProperties.RARITY, 10, NumberProperty.Operation.ADD)
                .trait(PartTypes.COATING, Const.Traits.BRILLIANT, 1)
                .trait(PartTypes.COATING, Const.Traits.SOFT, 3)
        );
        // Iron
        ret.add(MaterialBuilder.builtin(BuiltinMaterials.IRON)
                .craftingWithRodSubstitute(this.items, Tags.Items.INGOTS_IRON, SgTags.Items.RODS_IRON, MaterialCategories.METAL, MaterialCategories.INTERMEDIATE)
                .displayWithDefaultName(Color.VALUE_WHITE, TextureType.HIGH_CONTRAST)
                //main
                .mainStatsCommon(250, 15, 14, 20, 0.7f)
                .mainStatsHarvest(6)
                .mainStatsMelee(2, 1, 0.0f)
                .stat(PartGearKey.ofMain(GearTypes.AXE), GearProperties.ATTACK_SPEED, -0.1f)
                .stat(PartGearKey.ofMain(GearTypes.HOE), GearProperties.ATTACK_SPEED, 0f)
                .mainStatsRanged(1, 0.1f)
                .mainStatsProjectile(1.0f, 1.1f)
                .mainStatsArmor(2, 6, 5, 2, 0, 6) //15
                .trait(PartTypes.MAIN, Const.Traits.MALLEABLE, 3)
                //rod
                .trait(PartTypes.ROD, Const.Traits.FLEXIBLE, 2)
                //tip
                .stat(PartTypes.TIP, GearProperties.DURABILITY, 128, NumberProperty.Operation.ADD)
                .stat(PartTypes.TIP, GearProperties.ARMOR_DURABILITY, 4, NumberProperty.Operation.ADD)
                .harvestTierBuiltin(PartTypes.TIP)
                .stat(PartTypes.TIP, GearProperties.HARVEST_SPEED, 1, NumberProperty.Operation.ADD)
                .stat(PartTypes.TIP, GearProperties.ATTACK_DAMAGE, 1, NumberProperty.Operation.ADD)
                .stat(PartTypes.TIP, GearProperties.DRAW_SPEED, 0.2f, NumberProperty.Operation.ADD)
                .stat(PartTypes.TIP, GearProperties.RARITY, 8, NumberProperty.Operation.ADD)
                .trait(PartTypes.TIP, Const.Traits.MALLEABLE, 2)
        );
        // Netherite
        ret.add(MaterialBuilder.simple(modId("netherite"))
                .crafting(this.items, Tags.Items.INGOTS_NETHERITE, MaterialCategories.METAL, MaterialCategories.ENDGAME)
                .display(
                        TextUtil.translate("material", "netherite"),
                        TextUtil.translate("material", "netherite"),
                        0x867B86
                )
                //coating
                .stat(PartTypes.COATING, GearProperties.DURABILITY, 0.3f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .stat(PartTypes.COATING, GearProperties.DURABILITY, 2, NumberProperty.Operation.ADD)
                .stat(PartTypes.COATING, GearProperties.ARMOR_DURABILITY, 37f / 33f - 1f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .stat(PartTypes.COATING, GearProperties.RARITY, 40, NumberProperty.Operation.ADD)
                .stat(PartTypes.COATING, GearProperties.HARVEST_TIER, new HarvestTierPropertyValue(
                        HarvestTier.create(
                                "netherite",
                                "4",
                                BlockTags.INCORRECT_FOR_NETHERITE_TOOL
                        )
                ))
                .stat(PartTypes.COATING, GearProperties.HARVEST_SPEED, 0.125f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .stat(PartTypes.COATING, GearProperties.ATTACK_DAMAGE, 1f / 3f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .stat(PartTypes.COATING, GearProperties.MAGIC_DAMAGE, 1f / 3f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .stat(PartTypes.COATING, GearProperties.RANGED_DAMAGE, 1f / 3f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .stat(PartTypes.COATING, GearProperties.ARMOR_TOUGHNESS, 4, NumberProperty.Operation.ADD)
                .stat(PartTypes.COATING, GearProperties.KNOCKBACK_RESISTANCE, 1f, NumberProperty.Operation.ADD)
                .stat(PartTypes.COATING, GearProperties.ENCHANTMENT_VALUE, 5, NumberProperty.Operation.ADD)
                .trait(PartTypes.COATING, Const.Traits.FIREPROOF, 1)
        );
    }

    private void addGems(Collection<MaterialBuilder<?>> ret, HolderLookup.Provider registries) {
        // Diamond
        ret.add(MaterialBuilder.builtin(BuiltinMaterials.DIAMOND)
                .craftingWithRodSubstitute(this.items, Tags.Items.GEMS_DIAMOND, SgTags.Items.RODS_DIAMOND, MaterialCategories.GEM, MaterialCategories.ADVANCED)
                .displayWithDefaultName(0x33EBCB, TextureType.HIGH_CONTRAST)
                // main
                .mainStatsCommon(1561, 33, 10, 70, 0.8f)
                .stat(PartGearKey.ofMain(GearTypes.HOE), GearProperties.ATTACK_SPEED, 1f)
                .mainStatsHarvest(8)
                .mainStatsMelee(3, 1, 0.0f)
                .mainStatsRanged(2, -0.2f)
                .mainStatsProjectile(0.9f, 1.1f)
                .mainStatsArmor(3, 8, 6, 3, 8, 4) //20
                .trait(PartTypes.MAIN, Const.Traits.BRITTLE, 2)
                .trait(PartTypes.MAIN, Const.Traits.LUSTROUS, 1, materialCountOrRatio(3, 0.5f))
                // rod
                .stat(PartTypes.ROD, GearProperties.HARVEST_SPEED, 0.2f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .trait(PartTypes.ROD, Const.Traits.BRITTLE, 5, new MaterialRatioTraitCondition(0.5f))
                .trait(PartTypes.ROD, Const.Traits.LUSTROUS, 4, new MaterialRatioTraitCondition(0.5f))
                // tip
                .stat(PartTypes.TIP, GearProperties.DURABILITY, 256, NumberProperty.Operation.ADD)
                .stat(PartTypes.TIP, GearProperties.ARMOR_DURABILITY, 9, NumberProperty.Operation.ADD)
                .harvestTierBuiltin(PartTypes.TIP)
                .stat(PartTypes.TIP, GearProperties.HARVEST_SPEED, 2, NumberProperty.Operation.ADD)
                .stat(PartTypes.TIP, GearProperties.ATTACK_DAMAGE, 2, NumberProperty.Operation.ADD)
                .stat(PartTypes.TIP, GearProperties.MAGIC_DAMAGE, 1, NumberProperty.Operation.ADD)
                .stat(PartTypes.TIP, GearProperties.RANGED_DAMAGE, 0.5f, NumberProperty.Operation.ADD)
                .stat(PartTypes.TIP, GearProperties.RARITY, 20, NumberProperty.Operation.ADD)
                .trait(PartTypes.TIP, Const.Traits.BRITTLE, 2)
                .trait(PartTypes.TIP, Const.Traits.LUSTROUS, 2)
                // adornment
                .trait(PartTypes.SETTING, Const.Traits.BASTION, 1)
        );
        // Emerald
        ret.add(MaterialBuilder.builtin(BuiltinMaterials.EMERALD)
                .craftingWithRodSubstitute(this.items, Tags.Items.GEMS_EMERALD, SgTags.Items.RODS_EMERALD, MaterialCategories.GEM, MaterialCategories.ADVANCED)
                .displayWithDefaultName(0x00B038, TextureType.HIGH_CONTRAST)
                // main
                .mainStatsCommon(1080, 24, 16, 40, 1.0f)
                .stat(PartTypes.MAIN, GearProperties.REPAIR_VALUE, 0.25f)
                .mainStatsHarvest(10)
                .mainStatsMelee(2, 2, 0.0f)
                .stat(PartGearKey.ofMain(GearTypes.HOE), GearProperties.ATTACK_SPEED, 1f)
                .mainStatsRanged(1, -0.1f)
                .mainStatsProjectile(1.1f, 0.9f)
                .mainStatsArmor(3, 6, 4, 3, 4, 6) //16
                .trait(PartTypes.MAIN, Const.Traits.BRITTLE, 1)
                .trait(PartTypes.MAIN, Const.Traits.SYNERGISTIC, 2)
                // rod
                .stat(PartTypes.ROD, GearProperties.HARVEST_SPEED, 0.3f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .trait(PartTypes.ROD, Const.Traits.BRITTLE, 4, new MaterialRatioTraitCondition(0.5f))
                // tip
                .stat(PartTypes.TIP, GearProperties.DURABILITY, 512, NumberProperty.Operation.ADD)
                .stat(PartTypes.TIP, GearProperties.ARMOR_DURABILITY, 12, NumberProperty.Operation.ADD)
                .harvestTierBuiltin(PartTypes.TIP)
                .stat(PartTypes.TIP, GearProperties.HARVEST_SPEED, 2, NumberProperty.Operation.ADD)
                .stat(PartTypes.TIP, GearProperties.ATTACK_DAMAGE, 1, NumberProperty.Operation.ADD)
                .stat(PartTypes.TIP, GearProperties.MAGIC_DAMAGE, 2, NumberProperty.Operation.ADD)
                .stat(PartTypes.TIP, GearProperties.RANGED_DAMAGE, 1, NumberProperty.Operation.ADD)
                .stat(PartTypes.TIP, GearProperties.RARITY, 20, NumberProperty.Operation.ADD)
                .trait(PartTypes.TIP, Const.Traits.BRITTLE, 1)
                .trait(PartTypes.TIP, Const.Traits.SYNERGISTIC, 2)
                // adornment
                .trait(PartTypes.SETTING, Const.Traits.REACH, 2)
        );
        // Lapis Lazuli
        ret.add(MaterialBuilder.builtin(BuiltinMaterials.LAPIS_LAZULI)
                .crafting(this.items, Tags.Items.GEMS_LAPIS, MaterialCategories.GEM, MaterialCategories.INTERMEDIATE)
                .displayWithDefaultName(0x224BAF, TextureType.HIGH_CONTRAST)
                // main
                .mainStatsCommon(200, 13, 17, 30, 1.3f)
                .mainStatsHarvest(5)
                .mainStatsMelee(2, 3, 0.0f)
                .mainStatsRanged(0, -0.1f)
                .mainStatsProjectile(1.0f, 0.8f)
                .mainStatsArmor(2, 6, 5, 2, 0, 10) //15
                // tip
                .harvestTierBuiltin(PartTypes.TIP)
                .stat(PartTypes.TIP, GearProperties.HARVEST_SPEED, -0.1f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .stat(PartTypes.TIP, GearProperties.MAGIC_DAMAGE, 2, NumberProperty.Operation.ADD)
                .stat(PartTypes.TIP, GearProperties.ATTACK_SPEED, 0.3f, NumberProperty.Operation.ADD)
                .trait(PartTypes.TIP, Const.Traits.HOLY, 1, new MaterialRatioTraitCondition(0.75f))
                .trait(PartTypes.TIP, Const.Traits.LUCKY, 4, new MaterialRatioTraitCondition(0.75f))
                // adornment
                .trait(PartTypes.SETTING, Const.Traits.LUCKY, 3, new MaterialRatioTraitCondition(0.75f))
        );
        // Prismarine
        ret.add(MaterialBuilder.simple(modId("prismarine"))
                .crafting(this.items, Tags.Items.GEMS_PRISMARINE, MaterialCategories.GEM, MaterialCategories.ORGANIC, MaterialCategories.ADVANCED)
                .displayWithDefaultName(TextUtil.translate("material", "prismarine"), 0x91C5B7, TextureType.HIGH_CONTRAST)
                // coating
                .stat(PartTypes.COATING, GearProperties.DURABILITY, 0.075f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .stat(PartTypes.COATING, GearProperties.ARMOR_DURABILITY, 0.125f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .stat(PartTypes.COATING, GearProperties.ARMOR_TOUGHNESS, 1, NumberProperty.Operation.ADD)
                .stat(PartTypes.COATING, GearProperties.KNOCKBACK_RESISTANCE, 0.25f, NumberProperty.Operation.ADD)
                .trait(PartTypes.COATING, Const.Traits.AQUATIC, 5, new MaterialRatioTraitCondition(0.67f))
                .trait(PartTypes.COATING, Const.Traits.AQUATIC, 3, new NotTraitCondition(new MaterialRatioTraitCondition(0.67f)))
                // adornment
                .trait(PartTypes.SETTING, Const.Traits.SWIFT_SWIM, 3, new MaterialRatioTraitCondition(0.67f))
        );
        // Quartz
        ret.add(MaterialBuilder.builtin(BuiltinMaterials.QUARTZ)
                .craftingWithRodSubstitute(this.items, Tags.Items.GEMS_QUARTZ, SgTags.Items.RODS_QUARTZ, MaterialCategories.GEM, MaterialCategories.INTERMEDIATE)
                .displayWithDefaultName(0xD4CABA, TextureType.HIGH_CONTRAST)
                // main
                .mainStatsCommon(330, 13, 10, 40, 1.2f)
                .mainStatsHarvest(7)
                .mainStatsMelee(2, 0, 0.1f)
                .mainStatsRanged(0, 0.1f)
                .mainStatsProjectile(1f, 1f)
                .mainStatsArmor(3, 5, 4, 2, 0, 4) //14
                .trait(PartTypes.MAIN, Const.Traits.CRUSHING, 3)
                .trait(PartTypes.MAIN, Const.Traits.JAGGED, 2)
                // rod
                .stat(PartTypes.ROD, GearProperties.HARVEST_SPEED, 0.2f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .stat(PartTypes.ROD, GearProperties.ATTACK_DAMAGE, 0.1f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .trait(PartTypes.ROD, Const.Traits.BRITTLE, 3)
                // tip
                .stat(PartTypes.TIP, GearProperties.DURABILITY, 64, NumberProperty.Operation.ADD)
                .stat(PartTypes.TIP, GearProperties.ARMOR_DURABILITY, 4, NumberProperty.Operation.ADD)
                .harvestTierBuiltin(PartTypes.TIP)
                .stat(PartTypes.TIP, GearProperties.HARVEST_SPEED, 2, NumberProperty.Operation.ADD)
                .stat(PartTypes.TIP, GearProperties.ATTACK_DAMAGE, 4, NumberProperty.Operation.ADD)
                .stat(PartTypes.TIP, GearProperties.RANGED_DAMAGE, 1.5f, NumberProperty.Operation.ADD)
                .stat(PartTypes.TIP, GearProperties.RARITY, 20, NumberProperty.Operation.ADD)
                .trait(PartTypes.TIP, Const.Traits.CHIPPING, 1)
                .trait(PartTypes.TIP, Const.Traits.JAGGED, 3)
                // adornment
                .trait(PartTypes.SETTING, Const.Traits.MIGHTY, 2, new MaterialRatioTraitCondition(0.5f))
        );
        // Amethyst
        ret.add(MaterialBuilder.builtin(BuiltinMaterials.AMETHYST)
                .crafting(this.items, Tags.Items.GEMS_AMETHYST, MaterialCategories.GEM, MaterialCategories.INTERMEDIATE)
                .displayWithDefaultName(0xA31DE6, TextureType.HIGH_CONTRAST)
                // main
                .mainStatsCommon(210, 10, 16, 35, 1.3f)
                .mainStatsHarvest(6)
                .mainStatsMelee(1, 3, 0)
                .mainStatsRanged(1, 0)
                .mainStatsProjectile(1, 1)
                .mainStatsArmor(3, 5, 4, 3, 0, 10) //15
                .trait(PartTypes.MAIN, Const.Traits.RENEW, 1, new MaterialRatioTraitCondition(0.7f))
                // tip
                .stat(PartTypes.TIP, GearProperties.DURABILITY, -0.25f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .harvestTierBuiltin(PartTypes.TIP)
                .trait(PartTypes.TIP, Const.Traits.SILKY, 1, new MaterialRatioTraitCondition(0.66f))
                //adornment
                .trait(PartTypes.SETTING, Const.Traits.CURSED, 4)
        );
    }

    private void addDusts(Collection<MaterialBuilder<?>> ret, HolderLookup.Provider registries) {
        ret.add(MaterialBuilder.simple(modId("crushed_shulker_shell"))
                .crafting(CraftingItems.CRUSHED_SHULKER_SHELL, MaterialCategories.ROCK, MaterialCategories.DUST, MaterialCategories.ENDGAME)
                .displayWithDefaultName(0xE08BDF, TextureType.LOW_CONTRAST)
                // main (additive)
                .stat(PartTypes.MAIN, GearProperties.ADDITIVE, new BooleanPropertyValue(true))
                .stat(PartTypes.MAIN, GearProperties.DURABILITY, 2500)
                .stat(PartTypes.MAIN, GearProperties.ARMOR_DURABILITY, 50)
                .stat(PartTypes.MAIN, GearProperties.ENCHANTMENT_VALUE, 10)
                .stat(PartTypes.MAIN, GearProperties.CHARGING_VALUE, -0.15f)
                .stat(PartTypes.MAIN, GearProperties.RARITY, 91)
                .trait(PartTypes.MAIN, Const.Traits.ERODED, 4)
        );
        // Glowstone
        ret.add(MaterialBuilder.simple(modId("glowstone"))
                .crafting(this.items, Tags.Items.DUSTS_GLOWSTONE, MaterialCategories.GEM, MaterialCategories.DUST, MaterialCategories.INTERMEDIATE)
                .displayWithDefaultName(0xD2D200, TextureType.HIGH_CONTRAST)
                // main (additive)
                .stat(PartTypes.MAIN, GearProperties.ADDITIVE, new BooleanPropertyValue(true))
                .stat(PartTypes.MAIN, GearProperties.CHARGING_VALUE, 0.15f, NumberProperty.Operation.MULTIPLY_BASE)
                .stat(PartTypes.MAIN, GearProperties.RARITY, 40)
                .stat(PartTypes.MAIN, GearProperties.HARVEST_SPEED, 15)
                .stat(PartTypes.MAIN, GearProperties.ATTACK_DAMAGE, 3)
                .stat(PartTypes.MAIN, GearProperties.ATTACK_SPEED, 0.4f)
                .stat(PartTypes.MAIN, GearProperties.RANGED_DAMAGE, 3)
                .trait(PartTypes.MAIN, Const.Traits.RENEW, 3)
                //tip
                .stat(PartTypes.TIP, GearProperties.HARVEST_SPEED, 0.4f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .stat(PartTypes.TIP, GearProperties.ATTACK_DAMAGE, 2, NumberProperty.Operation.ADD)
                .stat(PartTypes.TIP, GearProperties.MAGIC_DAMAGE, 2, NumberProperty.Operation.ADD)
                .stat(PartTypes.TIP, GearProperties.DRAW_SPEED, 0.3f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .stat(PartTypes.TIP, GearProperties.RARITY, 15, NumberProperty.Operation.ADD)
                .trait(PartTypes.TIP, Const.Traits.REFRACTIVE, 1)
                .trait(PartTypes.TIP, Const.Traits.LUSTROUS, 4)
        );
        // Redstone
        ret.add(MaterialBuilder.simple(modId("redstone"))
                .crafting(this.items, Tags.Items.DUSTS_REDSTONE, MaterialCategories.METAL, MaterialCategories.DUST, MaterialCategories.INTERMEDIATE)
                .displayWithDefaultName(0xBB0000, TextureType.HIGH_CONTRAST)
                // main (additive)
                .stat(PartTypes.MAIN, GearProperties.ADDITIVE, new BooleanPropertyValue(true))
                .stat(PartTypes.MAIN, GearProperties.ENCHANTMENT_VALUE, -0.1f, NumberProperty.Operation.MULTIPLY_BASE)
                .stat(PartTypes.MAIN, GearProperties.CHARGING_VALUE, 0.1f, NumberProperty.Operation.MULTIPLY_BASE)
                .stat(PartTypes.MAIN, GearProperties.RARITY, 30)
                .stat(PartTypes.MAIN, GearProperties.HARVEST_SPEED, 12)
                .stat(PartTypes.MAIN, GearProperties.ATTACK_DAMAGE, 4)
                .stat(PartTypes.MAIN, GearProperties.RANGED_DAMAGE, 2)
                .trait(PartTypes.MAIN, Const.Traits.IMPERIAL, 3)
                //tip
                .stat(PartTypes.TIP, GearProperties.HARVEST_SPEED, 0.2f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .stat(PartTypes.TIP, GearProperties.ATTACK_DAMAGE, 2, NumberProperty.Operation.ADD)
                .stat(PartTypes.TIP, GearProperties.ATTACK_SPEED, 0.3f, NumberProperty.Operation.ADD)
                .stat(PartTypes.TIP, GearProperties.RANGED_DAMAGE, 2, NumberProperty.Operation.ADD)
                .stat(PartTypes.TIP, GearProperties.RARITY, 10, NumberProperty.Operation.ADD)
        );
    }

    @SuppressWarnings("OverlyLongMethod")
    private void addStones(Collection<MaterialBuilder<?>> ret, HolderLookup.Provider registries) {
        // Basalt
        ret.add(MaterialBuilder.builtin(BuiltinMaterials.BASALT)
                .crafting(Items.BASALT, MaterialCategories.ROCK, MaterialCategories.BASIC)
                .displayWithDefaultName(0x4F4B4F, TextureType.LOW_CONTRAST)
                //main
                .mainStatsCommon(137, 4, 6, 7, 0.7f)
                .mainStatsHarvest(4)
                .mainStatsMelee(1, 0, 0.0f)
                .stat(PartGearKey.ofMain(GearTypes.AXE), GearProperties.ATTACK_SPEED, -0.2f)
                .stat(PartGearKey.ofMain(GearTypes.HOE), GearProperties.ATTACK_SPEED, -1f)
                .mainStatsRanged(0, -0.1f, 1f, 0.8f)
                .mainStatsArmor(1, 3, 1, 1, 0, 0) //6
                .trait(PartTypes.MAIN, Const.Traits.BRITTLE, 2)
                .trait(PartTypes.MAIN, Const.Traits.CHIPPING, 3)
                //rod
                .stat(PartTypes.ROD, GearProperties.HARVEST_SPEED, 0.1f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .stat(PartTypes.ROD, GearProperties.ATTACK_DAMAGE, -0.1f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .trait(PartTypes.ROD, Const.Traits.BRITTLE, 3)
                .trait(PartTypes.ROD, Const.Traits.CHIPPING, 2)
        );
        // Blackstone
        ret.add(MaterialBuilder.builtin(BuiltinMaterials.BLACKSTONE)
                .crafting(Items.BLACKSTONE, MaterialCategories.ROCK, MaterialCategories.BASIC)
                .displayWithDefaultName(0x3C3947, TextureType.LOW_CONTRAST)
                //main
                .mainStatsCommon(151, 5, 4, 9, 0.6f)
                .mainStatsHarvest(4)
                .mainStatsMelee(1, 0, 0.0f)
                .stat(PartGearKey.ofMain(GearTypes.AXE), GearProperties.ATTACK_SPEED, -0.2f)
                .stat(PartGearKey.ofMain(GearTypes.HOE), GearProperties.ATTACK_SPEED, -1f)
                .mainStatsRanged(0, -0.2f, 1f, 0.8f)
                .mainStatsArmor(1, 2, 1, 1, 0, 0) //5
                .trait(PartTypes.MAIN, Const.Traits.BRITTLE, 1)
                .trait(PartTypes.MAIN, Const.Traits.JAGGED, 2)
                .trait(PartTypes.MAIN, Const.Traits.HARD, 2)
                //rod
                .stat(PartTypes.ROD, GearProperties.HARVEST_SPEED, -0.1f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .trait(PartTypes.ROD, Const.Traits.BRITTLE, 1)
                .trait(PartTypes.ROD, Const.Traits.JAGGED, 2)
                .trait(PartTypes.ROD, Const.Traits.HARD, 1)
        );
        // End Stone
        ret.add(MaterialBuilder.builtin(BuiltinMaterials.END_STONE)
                .crafting(this.items, Tags.Items.END_STONES, MaterialCategories.ROCK, MaterialCategories.BASIC)
                .displayWithDefaultName(0xFFFFCC)
                //main
                .mainStatsCommon(1164, 15, 10, 32, 0.9f)
                .stat(PartTypes.MAIN, GearProperties.REPAIR_VALUE, -0.2f)
                .mainStatsHarvest(7)
                .mainStatsMelee(2, 3, 0.1f)
                .mainStatsRanged(0, 0f, 1f, 0.8f)
                .mainStatsArmor(3, 5, 4, 3, 1, 6) //15
                .trait(PartTypes.MAIN, Const.Traits.JAGGED, 3)
                .trait(PartTypes.MAIN, Const.Traits.ANCIENT, 2)
                //rod
                .stat(PartTypes.ROD, GearProperties.ATTACK_DAMAGE, 1, NumberProperty.Operation.ADD)
                .trait(PartTypes.ROD, Const.Traits.MALLEABLE, 1)
                .trait(PartTypes.ROD, Const.Traits.ANCIENT, 4)
        );
        // Flint
        ret.add(MaterialBuilder.builtin(BuiltinMaterials.FLINT)
                .crafting(Items.FLINT, MaterialCategories.ROCK, MaterialCategories.BASIC)
                .displayWithDefaultName(0x969696, TextureType.HIGH_CONTRAST)
                //main
                .mainStatsCommon(124, 4, 3, 6, 0.8f)
                .mainStatsHarvest(5)
                .mainStatsMelee(2, 0, -0.1f)
                .mainStatsRanged(1, -0.3f, 1.0f, 1.0f)
                .mainStatsArmor(0.5f, 2f, 1f, 0.5f, 0, 0) //4
                .trait(PartTypes.MAIN, Const.Traits.JAGGED, 3)
                //rod
                .trait(PartTypes.ROD, Const.Traits.BRITTLE, 3)
                .trait(PartTypes.ROD, Const.Traits.JAGGED, 2)
                //setting (deliberately has no traits lol)
                .noProperties(PartTypes.SETTING)
        );
        // Netherrack
        ret.add(MaterialBuilder.builtin(BuiltinMaterials.NETHERRACK)
                .crafting(this.items, Tags.Items.NETHERRACKS, MaterialCategories.ROCK, MaterialCategories.ORGANIC, MaterialCategories.BASIC)
                .displayWithDefaultName(0x854242, TextureType.LOW_CONTRAST)
                //main
                .mainStatsCommon(142, 5, 8, 11, 0.8f)
                .stat(PartTypes.MAIN, GearProperties.REPAIR_VALUE, -0.2f)
                .mainStatsHarvest(5)
                .mainStatsMelee(0.5f, 0.5f, 0.0f)
                .mainStatsRanged(0.5f, 0.1f, 0.8f, 1.0f)
                .mainStatsArmor(1, 4, 2, 1, 0, 4) //8
                .trait(PartTypes.MAIN, Const.Traits.ERODED, 3)
                .trait(PartTypes.MAIN, Const.Traits.FLEXIBLE, 2)
                //rod
                .trait(PartTypes.ROD, Const.Traits.ERODED, 2)
                .trait(PartTypes.ROD, Const.Traits.BENDING, 2)
        );
        // Obsidian
        ret.add(MaterialBuilder.builtin(BuiltinMaterials.OBSIDIAN)
                .crafting(this.items, Tags.Items.OBSIDIANS, MaterialCategories.ROCK, MaterialCategories.ADVANCED)
                .displayWithDefaultName(0x443464, TextureType.LOW_CONTRAST)
                //main
                .mainStatsCommon(1024, 13, 7, 10, 0.6f)
                .stat(PartTypes.MAIN, GearProperties.REPAIR_VALUE, -0.25f)
                .mainStatsHarvest(6)
                .mainStatsMelee(3, 2, -0.4f)
                .mainStatsRanged(0, -0.4f, 0.7f, 0.7f)
                .mainStatsArmor(3, 8, 6, 3, 4, 8) //20
                .trait(PartTypes.MAIN, Const.Traits.JAGGED, 3)
                .trait(PartTypes.MAIN, Const.Traits.CRUSHING, 2)
                //rod
                .stat(PartTypes.ROD, GearProperties.HARVEST_SPEED, -0.1f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .trait(PartTypes.ROD, Const.Traits.BRITTLE, 2)
                .trait(PartTypes.ROD, Const.Traits.CHIPPING, 3)
        );
        // Sandstone
        ret.add(MaterialBuilder.builtin(BuiltinMaterials.SANDSTONE)
                .crafting(this.items, Tags.Items.SANDSTONE_UNCOLORED_BLOCKS, MaterialCategories.ROCK, MaterialCategories.BASIC)
                .displayWithDefaultName(0xE3DBB0, TextureType.LOW_CONTRAST)
                //main
                .mainStatsCommon(117, 6, 7, 7, 0.7f)
                .stat(PartTypes.MAIN, GearProperties.REPAIR_VALUE, -0.1f)
                .mainStatsHarvest(4)
                .mainStatsMelee(1, 0, 0.1f)
                .stat(PartGearKey.ofMain(GearTypes.AXE), GearProperties.ATTACK_SPEED, -0.1f)
                .stat(PartGearKey.ofMain(GearTypes.HOE), GearProperties.ATTACK_SPEED, -1f)
                .mainStatsRanged(0, -0.1f, 1f, 0.8f)
                .mainStatsArmor(1, 2, 1, 1, 0, 0) //5
                //rod
                .trait(PartTypes.ROD, Const.Traits.BRITTLE, 1)
        );
        // Red sandstone
        ret.add(MaterialBuilder.simple(modId("sandstone/red"))
                .parent(BuiltinMaterials.SANDSTONE.getMaterial())
                .crafting(this.items, Tags.Items.SANDSTONE_RED_BLOCKS)
                .displayWithDefaultName(0xD97B30)
        );
        // Stone
        ret.add(MaterialBuilder.builtin(BuiltinMaterials.STONE)
                .crafting(new MaterialCraftingData(
                        taggedItems(Tags.Items.COBBLESTONES),
                        List.of(MaterialCategories.ROCK, MaterialCategories.BASIC),
                        List.of(),
                        Map.of(PartTypes.ROD.get(), taggedItems(SgTags.Items.RODS_STONE)),
                        true
                ))
                .displayWithDefaultName(0x9A9A9A, TextureType.LOW_CONTRAST)
                //main
                .mainStatsCommon(131, 5, 5, 4, 0.5f)
                .mainStatsHarvest(4)
                .mainStatsMelee(1, 0, 0.0f)
                .stat(PartGearKey.ofMain(GearTypes.AXE), GearProperties.ATTACK_SPEED, -0.2f)
                .stat(PartGearKey.ofMain(GearTypes.HOE), GearProperties.ATTACK_SPEED, -1f)
                .mainStatsRanged(0, -0.2f, 1f, 0.8f)
                .mainStatsArmor(1, 2, 1, 1, 0, 0) //5
                .trait(PartTypes.MAIN, Const.Traits.ANCIENT, 1)
                //rod
                .stat(PartTypes.ROD, GearProperties.HARVEST_SPEED, 0.1f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .trait(PartTypes.ROD, Const.Traits.BRITTLE, 2)
                .trait(PartTypes.ROD, Const.Traits.CRUSHING, 2)
        );
        ret.add(MaterialBuilder.simple(modId("stone/andesite"))
                .parent(BuiltinMaterials.STONE.getMaterial())
                .crafting(Items.ANDESITE, MaterialCategories.ROCK, MaterialCategories.BASIC)
                .displayWithDefaultName(0x8A8A8E, TextureType.LOW_CONTRAST)
        );
        ret.add(MaterialBuilder.simple(modId("stone/diorite"))
                .parent(BuiltinMaterials.STONE.getMaterial())
                .crafting(Items.DIORITE, MaterialCategories.ROCK, MaterialCategories.BASIC)
                .displayWithDefaultName(0xFFFFFF, TextureType.LOW_CONTRAST)
        );
        ret.add(MaterialBuilder.simple(modId("stone/granite"))
                .parent(BuiltinMaterials.STONE.getMaterial())
                .crafting(Items.GRANITE, MaterialCategories.ROCK, MaterialCategories.BASIC)
                .displayWithDefaultName(0x9F6B58, TextureType.LOW_CONTRAST)
        );
        // Terracotta
        var sgTerracotta = BuiltinMaterials.TERRACOTTA.getMaterial();
        ret.add(MaterialBuilder.builtin(BuiltinMaterials.TERRACOTTA)
                .crafting(Blocks.TERRACOTTA, MaterialCategories.ROCK, MaterialCategories.BASIC)
                .displayWithDefaultName(0x985F45, TextureType.LOW_CONTRAST)
                //main
                .mainStatsCommon(165, 11, 9, 7, 0.8f)
                .stat(PartTypes.MAIN, GearProperties.REPAIR_VALUE, -0.1f)
                .mainStatsHarvest(4)
                .mainStatsMelee(1.5f, 0, 0.2f)
                .mainStatsRanged(0, -0.2f, 1f, 0.9f)
                .mainStatsArmor(2, 3, 3, 1, 0, 3) //9
                .trait(PartTypes.MAIN, Const.Traits.BRITTLE, 1)
                .trait(PartTypes.MAIN, Const.Traits.CHIPPING, 2)
                .trait(PartTypes.MAIN, Const.Traits.RUSTIC, 1)
                //rod
                .stat(PartTypes.ROD, GearProperties.ATTACK_DAMAGE, 0.1f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .trait(PartTypes.ROD, Const.Traits.BRITTLE, 2)
                .trait(PartTypes.ROD, Const.Traits.CRUSHING, 1)
        );
        ret.add(terracotta(sgTerracotta, "black", BuiltInRegistries.BLOCK.get(Identifier.withDefaultNamespace("black_terracotta")).orElseThrow().value(), 0x251610));
        ret.add(terracotta(sgTerracotta, "blue", BuiltInRegistries.BLOCK.get(Identifier.withDefaultNamespace("blue_terracotta")).orElseThrow().value(), 0x4A3B5B));
        ret.add(terracotta(sgTerracotta, "brown", BuiltInRegistries.BLOCK.get(Identifier.withDefaultNamespace("brown_terracotta")).orElseThrow().value(), 0x4D3224));
        ret.add(terracotta(sgTerracotta, "cyan", BuiltInRegistries.BLOCK.get(Identifier.withDefaultNamespace("cyan_terracotta")).orElseThrow().value(), 0xD1B1A1));
        ret.add(terracotta(sgTerracotta, "gray", BuiltInRegistries.BLOCK.get(Identifier.withDefaultNamespace("gray_terracotta")).orElseThrow().value(), 0xD1B1A1));
        ret.add(terracotta(sgTerracotta, "green", BuiltInRegistries.BLOCK.get(Identifier.withDefaultNamespace("green_terracotta")).orElseThrow().value(), 0x4B522A));
        ret.add(terracotta(sgTerracotta, "light_blue", BuiltInRegistries.BLOCK.get(Identifier.withDefaultNamespace("light_blue_terracotta")).orElseThrow().value(), 0x706C8A));
        ret.add(terracotta(sgTerracotta, "light_gray", BuiltInRegistries.BLOCK.get(Identifier.withDefaultNamespace("light_gray_terracotta")).orElseThrow().value(), 0x876A61));
        ret.add(terracotta(sgTerracotta, "lime", BuiltInRegistries.BLOCK.get(Identifier.withDefaultNamespace("lime_terracotta")).orElseThrow().value(), 0x677534));
        ret.add(terracotta(sgTerracotta, "magenta", BuiltInRegistries.BLOCK.get(Identifier.withDefaultNamespace("magenta_terracotta")).orElseThrow().value(), 0x95576C));
        ret.add(terracotta(sgTerracotta, "orange", BuiltInRegistries.BLOCK.get(Identifier.withDefaultNamespace("orange_terracotta")).orElseThrow().value(), 0xA05325));
        ret.add(terracotta(sgTerracotta, "pink", BuiltInRegistries.BLOCK.get(Identifier.withDefaultNamespace("pink_terracotta")).orElseThrow().value(), 0xA04D4E));
        ret.add(terracotta(sgTerracotta, "purple", BuiltInRegistries.BLOCK.get(Identifier.withDefaultNamespace("purple_terracotta")).orElseThrow().value(), 0x764556));
        ret.add(terracotta(sgTerracotta, "red", BuiltInRegistries.BLOCK.get(Identifier.withDefaultNamespace("red_terracotta")).orElseThrow().value(), 0x8E3C2E));
        ret.add(terracotta(sgTerracotta, "white", BuiltInRegistries.BLOCK.get(Identifier.withDefaultNamespace("white_terracotta")).orElseThrow().value(), 0xD1B1A1));
        ret.add(terracotta(sgTerracotta, "yellow", BuiltInRegistries.BLOCK.get(Identifier.withDefaultNamespace("yellow_terracotta")).orElseThrow().value(), 0xB98423));
    }

    private void addWoods(Collection<MaterialBuilder<?>> ret, HolderLookup.Provider registries) {
        // Wood
        ret.add(MaterialBuilder.builtin(BuiltinMaterials.WOOD)
                .crafting(new MaterialCraftingData(
                        taggedItems(ItemTags.PLANKS),
                        List.of(MaterialCategories.ORGANIC, MaterialCategories.WOOD),
                        List.of(),
                        Map.of(PartTypes.ROD.get(), Ingredient.of(Items.STICK)),
                        true
                ))
                .displayWithDefaultName(0x896727, TextureType.LOW_CONTRAST)
                //main
                .mainStatsCommon(59, 8, 15, 1, 0.6f)
                .stat(PartTypes.MAIN, GearProperties.REPAIR_VALUE, 0.25f)
                .mainStatsHarvest(2)
                .mainStatsMelee(0, 0, 0.0f)
                .stat(PartGearKey.ofMain(GearTypes.AXE), GearProperties.ATTACK_SPEED, -0.2f)
                .stat(PartGearKey.ofMain(GearTypes.HOE), GearProperties.ATTACK_SPEED, -2f)
                .mainStatsRanged(0f, 0f, 1f, 0.9f)
                .mainStatsArmor(1, 3, 2, 1, 0, 2) //7
                .trait(PartTypes.MAIN, Const.Traits.FLAMMABLE, 1)
                .trait(PartTypes.MAIN, Const.Traits.JAGGED, 1)
                //rod
                .trait(PartTypes.ROD, Const.Traits.FLEXIBLE, 2)
        );
        ret.add(wood("acacia", Items.ACACIA_PLANKS, 0xBA6337));
        ret.add(wood("birch", Items.BIRCH_PLANKS, 0xD7C185));
        ret.add(wood("dark_oak", Items.DARK_OAK_PLANKS, 0x4F3218));
        ret.add(wood("jungle", Items.JUNGLE_PLANKS, 0xB88764));
        ret.add(wood("oak", Items.OAK_PLANKS, 0xB8945F));
        ret.add(wood("spruce", Items.SPRUCE_PLANKS, 0x82613A));
        ret.add(wood("crimson", Items.CRIMSON_PLANKS, 0x7E3A56)
                .trait(PartTypes.MAIN, Const.Traits.JAGGED, 1)
                .trait(PartTypes.ROD, Const.Traits.FLEXIBLE, 2)
        );
        ret.add(wood("warped", Items.WARPED_PLANKS, 0x398382)
                .trait(PartTypes.MAIN, Const.Traits.JAGGED, 1)
                .trait(PartTypes.ROD, Const.Traits.FLEXIBLE, 2)
        );

        // Netherwood
        ret.add(MaterialBuilder.builtin(BuiltinMaterials.NETHERWOOD)
                .parent(Const.Materials.WOOD)
                .crafting(new MaterialCraftingData(
                        Ingredient.of(SgBlocks.NETHERWOOD_PLANKS),
                        List.of(MaterialCategories.ORGANIC, MaterialCategories.WOOD),
                        List.of(),
                        Map.of(PartTypes.ROD.get(), taggedItems(SgTags.Items.RODS_NETHERWOOD)),
                        true
                ))
                .displayWithDefaultName(0x7D272D, TextureType.LOW_CONTRAST)
                //main
                .mainStatsCommon(72, 12, 13, 4, 0.7f)
                .stat(PartTypes.MAIN, GearProperties.REPAIR_VALUE, 0.1f)
                .stat(PartTypes.MAIN, GearProperties.REPAIR_EFFICIENCY, 0.5f)
                .mainStatsHarvest(2)
                .mainStatsMelee(0, 0, 0.2f)
                .mainStatsRanged(0, 0f, 1f, 0.8f)
                .mainStatsArmor(1, 4, 2, 1, 0, 6) //8
                .trait(PartTypes.MAIN, Const.Traits.FLEXIBLE, 3)
                .trait(PartTypes.MAIN, Const.Traits.JAGGED, 2)
                //rod
                .stat(PartTypes.ROD, GearProperties.HARVEST_SPEED, 1, NumberProperty.Operation.ADD)
                .stat(PartTypes.ROD, GearProperties.ATTACK_DAMAGE, 0.5f, NumberProperty.Operation.ADD)
                .trait(PartTypes.ROD, Const.Traits.FLEXIBLE, 1)
        );
        // Bamboo
        ret.add(MaterialBuilder.builtin(BuiltinMaterials.BAMBOO)
                .crafting(new MaterialCraftingData(
                        Ingredient.of(Items.BAMBOO_PLANKS),
                        List.of(MaterialCategories.ORGANIC, MaterialCategories.WOOD, MaterialCategories.BASIC),
                        List.of(),
                        Map.of(PartTypes.ROD.get(), Ingredient.of(Items.BAMBOO)),
                        true
                ))
                .displayWithDefaultName(0x9AC162, TextureType.LOW_CONTRAST)
                //main
                .mainStatsCommon(65, 9, 15, 4, 0.7f)
                .stat(PartTypes.MAIN, GearProperties.REPAIR_VALUE, 0.25f)
                .mainStatsHarvest(2)
                .mainStatsMelee(0, 0, 0.0f)
                .stat(PartGearKey.ofMain(GearTypes.AXE), GearProperties.ATTACK_SPEED, -0.2f)
                .stat(PartGearKey.ofMain(GearTypes.HOE), GearProperties.ATTACK_SPEED, -2f)
                .mainStatsRanged(0f, 0f, 1f, 0.9f)
                .mainStatsArmor(1, 3, 2, 1, 0, 2) //7
                .trait(PartTypes.MAIN, Const.Traits.FLAMMABLE, 1)
                .trait(PartTypes.MAIN, Const.Traits.FLEXIBLE, 2)
                // rod
                .trait(PartTypes.ROD, Const.Traits.FLEXIBLE, 3)
        );
    }

    private void addClothLikes(Collection<MaterialBuilder<?>> ret, HolderLookup.Provider registries) {
        // Phantom Membrane
        ret.add(MaterialBuilder.simple(modId("phantom_membrane"))
                .crafting(Items.PHANTOM_MEMBRANE, MaterialCategories.ORGANIC, MaterialCategories.CLOTH, MaterialCategories.INTERMEDIATE)
                .displayWithDefaultName(0xC3B9A1, TextureType.LOW_CONTRAST)
                // main
                .mainStatsCommon(0, 12, 10, 35, 0.7f)
                .stat(PartGearKey.ofMain(GearTypes.ELYTRA), GearProperties.ARMOR_DURABILITY, 17)
                .mainStatsArmor(1, 2, 2, 1, 0, 8) //6
                .trait(PartTypes.MAIN, Const.Traits.RENEW, 2, new MaterialRatioTraitCondition(0.5f))
                // grip
                .stat(PartTypes.GRIP, GearProperties.REPAIR_EFFICIENCY, 0.15f, NumberProperty.Operation.MULTIPLY_BASE)
                .stat(PartTypes.GRIP, GearProperties.HARVEST_SPEED, 0.1f, NumberProperty.Operation.MULTIPLY_BASE)
                .stat(PartTypes.GRIP, GearProperties.ATTACK_SPEED, 0.2f, NumberProperty.Operation.ADD)
                .trait(PartTypes.GRIP, Const.Traits.ANCIENT, 2)
                // lining
                .trait(PartTypes.LINING, Const.Traits.LIGHT, 2)
                .trait(PartTypes.LINING, Const.Traits.FLEXIBLE, 3)
        );
        // Fine Silk Cloth
        ret.add(MaterialBuilder.simple(modId("fine_silk_cloth"))
                .crafting(CraftingItems.FINE_SILK_CLOTH, MaterialCategories.ORGANIC, MaterialCategories.CLOTH, MaterialCategories.INTERMEDIATE)
                .displayWithDefaultName(0xC3B9A1, TextureType.LOW_CONTRAST)
                // main
                .mainStatsCommon(0, 14, 14, 40, 0.9f)
                .stat(PartGearKey.ofMain(GearTypes.ELYTRA), GearProperties.ARMOR_DURABILITY, 18)
                .mainStatsArmor(1, 2, 2, 1, 0, 14) //6
                .trait(PartTypes.MAIN, Const.Traits.SNOW_WALKER, 1, new GearTypeTraitCondition(GearTypes.BOOTS))
                // grip
                .stat(PartTypes.GRIP, GearProperties.REPAIR_EFFICIENCY, 0.1f, NumberProperty.Operation.MULTIPLY_BASE)
                .stat(PartTypes.GRIP, GearProperties.HARVEST_SPEED, 0.15f, NumberProperty.Operation.MULTIPLY_BASE)
                .trait(PartTypes.GRIP, Const.Traits.ACCELERATE, 1)
                // lining
                .stat(PartTypes.LINING, GearProperties.MAGIC_ARMOR, 2, NumberProperty.Operation.ADD)
                .trait(PartTypes.LINING, Const.Traits.FLEXIBLE, 4)
        );
        // Leather
        ret.add(MaterialBuilder.simple(modId("leather"))
                .crafting(this.items, Tags.Items.LEATHERS, MaterialCategories.ORGANIC, MaterialCategories.CLOTH, MaterialCategories.BASIC)
                .displayWithDefaultName(0x805133, TextureType.LOW_CONTRAST)
                // main
                .mainStatsCommon(0, 5, 15, 11, 0.8f)
                .mainStatsArmor(1, 3, 2, 1, 0, 8) //7
                .trait(PartTypes.MAIN, Const.Traits.SNOW_WALKER, 1, new GearTypeTraitCondition(GearTypes.BOOTS))
                // grip
                .stat(PartTypes.GRIP, GearProperties.REPAIR_EFFICIENCY, 0.1f, NumberProperty.Operation.MULTIPLY_BASE)
                .stat(PartTypes.GRIP, GearProperties.HARVEST_SPEED, 0.15f, NumberProperty.Operation.MULTIPLY_BASE)
                .stat(PartTypes.GRIP, GearProperties.ATTACK_SPEED, 0.15f, NumberProperty.Operation.ADD)
                .trait(PartTypes.GRIP, Const.Traits.FLEXIBLE, 3)
                // lining
                .trait(PartTypes.LINING, Const.Traits.FLEXIBLE, 4)
        );
        // Wool
        ret.add(MaterialBuilder.simple(Const.Materials.WOOL)
                .crafting(this.items, ItemTags.WOOL, MaterialCategories.ORGANIC, MaterialCategories.CLOTH, MaterialCategories.BASIC)
                .displayWithDefaultName(Color.VALUE_WHITE, TextureType.LOW_CONTRAST)
                // main
                .mainStatsCommon(0, 4, 7, 7, 0.7f)
                .mainStatsArmor(0.5f, 2f, 1.0f, 0.5f, 0, 4) //4
                .trait(PartTypes.MAIN, Const.Traits.SNOW_WALKER, 1, new GearTypeTraitCondition(GearTypes.BOOTS))
                // grip
                .stat(PartTypes.GRIP, GearProperties.REPAIR_EFFICIENCY, 0.2f, NumberProperty.Operation.MULTIPLY_BASE)
                .stat(PartTypes.GRIP, GearProperties.HARVEST_SPEED, 0.1f, NumberProperty.Operation.MULTIPLY_BASE)
                .stat(PartTypes.GRIP, GearProperties.ATTACK_SPEED, 0.2f, NumberProperty.Operation.ADD)
                .trait(PartTypes.GRIP, Const.Traits.FLEXIBLE, 2)
                // lining
                .stat(PartTypes.LINING, GearProperties.KNOCKBACK_RESISTANCE, 0.1f, NumberProperty.Operation.ADD)
                .trait(PartTypes.LINING, Const.Traits.FLEXIBLE, 2)
        );
        ret.add(wool("black", BuiltInRegistries.BLOCK.get(Identifier.withDefaultNamespace("black_wool")).orElseThrow().value(), 0x141519));
        ret.add(wool("blue", BuiltInRegistries.BLOCK.get(Identifier.withDefaultNamespace("blue_wool")).orElseThrow().value(), 0x35399D));
        ret.add(wool("brown", BuiltInRegistries.BLOCK.get(Identifier.withDefaultNamespace("brown_wool")).orElseThrow().value(), 0x724728));
        ret.add(wool("cyan", BuiltInRegistries.BLOCK.get(Identifier.withDefaultNamespace("cyan_wool")).orElseThrow().value(), 0x158991));
        ret.add(wool("gray", BuiltInRegistries.BLOCK.get(Identifier.withDefaultNamespace("gray_wool")).orElseThrow().value(), 0x3E4447));
        ret.add(wool("green", BuiltInRegistries.BLOCK.get(Identifier.withDefaultNamespace("green_wool")).orElseThrow().value(), 0x546D1B));
        ret.add(wool("light_blue", BuiltInRegistries.BLOCK.get(Identifier.withDefaultNamespace("light_blue_wool")).orElseThrow().value(), 0x3AAFD9));
        ret.add(wool("light_gray", BuiltInRegistries.BLOCK.get(Identifier.withDefaultNamespace("light_gray_wool")).orElseThrow().value(), 0x8E8E86));
        ret.add(wool("lime", BuiltInRegistries.BLOCK.get(Identifier.withDefaultNamespace("lime_wool")).orElseThrow().value(), 0x70B919));
        ret.add(wool("magenta", BuiltInRegistries.BLOCK.get(Identifier.withDefaultNamespace("magenta_wool")).orElseThrow().value(), 0xBD44B3));
        ret.add(wool("orange", BuiltInRegistries.BLOCK.get(Identifier.withDefaultNamespace("orange_wool")).orElseThrow().value(), 0xF07613));
        ret.add(wool("pink", BuiltInRegistries.BLOCK.get(Identifier.withDefaultNamespace("pink_wool")).orElseThrow().value(), 0xED8DAC));
        ret.add(wool("purple", BuiltInRegistries.BLOCK.get(Identifier.withDefaultNamespace("purple_wool")).orElseThrow().value(), 0x792AAC));
        ret.add(wool("red", BuiltInRegistries.BLOCK.get(Identifier.withDefaultNamespace("red_wool")).orElseThrow().value(), 0xA12722));
        ret.add(wool("white", BuiltInRegistries.BLOCK.get(Identifier.withDefaultNamespace("white_wool")).orElseThrow().value(), 0xE9ECEC));
        ret.add(wool("yellow", BuiltInRegistries.BLOCK.get(Identifier.withDefaultNamespace("yellow_wool")).orElseThrow().value(), 0xF8C627));
    }

    private void addStringsAndFibers(Collection<MaterialBuilder<?>> ret, HolderLookup.Provider registries) {
        // Fine Silk
        ret.add(MaterialBuilder.simple(modId("fine_silk"))
                .crafting(CraftingItems.FINE_SILK, MaterialCategories.ORGANIC, MaterialCategories.FIBER, MaterialCategories.BASIC)
                .displayWithDefaultName(0xCCFFFF, TextureType.LOW_CONTRAST)
                //binding
                .stat(PartTypes.BINDING, GearProperties.MAGIC_ARMOR, 2, NumberProperty.Operation.ADD)
                .trait(PartTypes.BINDING, Const.Traits.LUCKY, 1)
                .trait(PartTypes.BINDING, Const.Traits.FLEXIBLE, 4)
                //cord
                .stat(PartTypes.CORD, GearProperties.RANGED_DAMAGE, 0.07f, NumberProperty.Operation.MULTIPLY_BASE)
                .trait(PartTypes.CORD, Const.Traits.FLEXIBLE, 2)
        );
        // Flax
        ret.add(MaterialBuilder.simple(modId("flax"))
                .crafting(CraftingItems.FLAX_STRING, MaterialCategories.ORGANIC, MaterialCategories.FIBER, MaterialCategories.BASIC)
                .displayWithDefaultName(0x845E37, TextureType.LOW_CONTRAST)
                //binding
                .stat(PartTypes.BINDING, GearProperties.ARMOR_DURABILITY, 0.05f, NumberProperty.Operation.MULTIPLY_BASE)
                .stat(PartTypes.BINDING, GearProperties.HARVEST_SPEED, 0.05f, NumberProperty.Operation.MULTIPLY_BASE)
                .trait(PartTypes.BINDING, Const.Traits.FLEXIBLE, 3)
                //cord
                .stat(PartTypes.CORD, GearProperties.RANGED_DAMAGE, -0.1f, NumberProperty.Operation.MULTIPLY_BASE)
                .stat(PartTypes.CORD, GearProperties.DRAW_SPEED, 0.2f, NumberProperty.Operation.MULTIPLY_BASE)
        );
        // Fluffy String
        ret.add(MaterialBuilder.simple(modId("fluffy_string"))
                .crafting(CraftingItems.FLUFFY_STRING, MaterialCategories.ORGANIC, MaterialCategories.FIBER, MaterialCategories.BASIC)
                .displayWithDefaultName(0xFFFAE5, TextureType.LOW_CONTRAST)
                //binding
                .stat(PartTypes.BINDING, GearProperties.ARMOR_DURABILITY, 0.05f, NumberProperty.Operation.MULTIPLY_BASE)
                .stat(PartTypes.BINDING, GearProperties.HARVEST_SPEED, -0.05f, NumberProperty.Operation.MULTIPLY_BASE)
                .trait(PartTypes.BINDING, Const.Traits.FLEXIBLE, 3)
                //cord
                .stat(PartTypes.CORD, GearProperties.RANGED_DAMAGE, 0.05f, NumberProperty.Operation.MULTIPLY_BASE)
                .stat(PartTypes.CORD, GearProperties.DRAW_SPEED, -0.05f, NumberProperty.Operation.MULTIPLY_BASE)
        );
        // Sinew
        ret.add(MaterialBuilder.simple(modId("sinew"))
                .crafting(CraftingItems.SINEW_FIBER, MaterialCategories.ORGANIC, MaterialCategories.FIBER, MaterialCategories.BASIC)
                .displayWithDefaultName(0xD8995B, TextureType.LOW_CONTRAST)
                //binding
                .stat(PartTypes.BINDING, GearProperties.REPAIR_EFFICIENCY, -0.05f, NumberProperty.Operation.MULTIPLY_BASE)
                .trait(PartTypes.BINDING, Const.Traits.FLEXIBLE, 4)
                //cord
                .stat(PartTypes.CORD, GearProperties.RANGED_DAMAGE, 0.2f, NumberProperty.Operation.MULTIPLY_BASE)
                .trait(PartTypes.CORD, Const.Traits.FLEXIBLE, 2)
        );
        // String
        ret.add(MaterialBuilder.simple(modId("string"))
                .crafting(Items.STRING, MaterialCategories.ORGANIC, MaterialCategories.FIBER, MaterialCategories.BASIC)
                .displayWithDefaultName(Color.VALUE_WHITE, TextureType.LOW_CONTRAST)
                //binding
                .stat(PartTypes.BINDING, GearProperties.REPAIR_EFFICIENCY, 0.05f, NumberProperty.Operation.MULTIPLY_BASE)
                .trait(PartTypes.BINDING, Const.Traits.FLEXIBLE, 1)
                //cord
                .stat(PartTypes.CORD, GearProperties.DRAW_SPEED, 0.1f, NumberProperty.Operation.MULTIPLY_BASE)
        );
        // Vines
        ret.add(MaterialBuilder.simple(modId("vine"))
                .crafting(Items.VINE, MaterialCategories.ORGANIC, MaterialCategories.BASIC)
                .displayWithDefaultName(0x007F0E, TextureType.LOW_CONTRAST)
                // binding
                .stat(PartTypes.BINDING, GearProperties.REPAIR_EFFICIENCY, 0.03f, NumberProperty.Operation.MULTIPLY_BASE)
                // cord
                .stat(PartTypes.CORD, GearProperties.RANGED_DAMAGE, -0.1f, NumberProperty.Operation.MULTIPLY_BASE)
        );
    }

    private void addRandomOrganics(Collection<MaterialBuilder<?>> ret, HolderLookup.Provider registries) {
        // Feather
        ret.add(MaterialBuilder.simple(modId("feather"))
                .crafting(this.items, Tags.Items.FEATHERS, MaterialCategories.ORGANIC, MaterialCategories.BASIC)
                .displayWithDefaultName(Color.VALUE_WHITE, TextureType.LOW_CONTRAST)
                // main
                .mainStatsCommon(0, 2, 9, 1, 1.1f)
                .mainStatsArmor(0.5f, 1f, 1f, 0.5f, 0f, 0f)
                .trait(PartTypes.MAIN, Const.Traits.FLUTTER, 1)
                // fletching
                .stat(PartTypes.FLETCHING, GearProperties.PROJECTILE_SPEED, 0.9f)
                .stat(PartTypes.FLETCHING, GearProperties.PROJECTILE_ACCURACY, 1.1f)
        );
        // Leaves
        ret.add(MaterialBuilder.simple(modId("leaves"))
                .crafting(this.items, ItemTags.LEAVES, MaterialCategories.ORGANIC, MaterialCategories.BASIC)
                .displayWithDefaultName(0x4A8F28, TextureType.LOW_CONTRAST)
                // main
                .mainStatsCommon(0, 2, 12, 1, 1.1f)
                .mainStatsArmor(0.5f, 1f, 1f, 0.5f, 0f, 0f)
                .trait(PartTypes.MAIN, Const.Traits.FLAMMABLE, 1)
                .trait(PartTypes.MAIN, Const.Traits.LIGHT, 1)
                // fletching
                .stat(PartTypes.FLETCHING, GearProperties.PROJECTILE_SPEED, 1.1f)
                .stat(PartTypes.FLETCHING, GearProperties.PROJECTILE_ACCURACY, 0.9f)
        );
        // Paper
        ret.add(MaterialBuilder.simple(modId("paper"))
                .crafting(this.items, SgTags.Items.PAPER, MaterialCategories.ORGANIC, MaterialCategories.SHEET, MaterialCategories.BASIC)
                .displayWithDefaultName(Color.VALUE_WHITE, TextureType.LOW_CONTRAST)
                .mainStatsCommon(0, 2, 11, 3, 1.0f)
                .mainStatsArmor(1, 1, 1, 1, 0, 0)
                .trait(PartTypes.MAIN, Const.Traits.FLAMMABLE, 1)
                .trait(PartTypes.MAIN, Const.Traits.RUSTIC, 3)
                .stat(PartTypes.FLETCHING, GearProperties.PROJECTILE_SPEED, 1.1f)
                .stat(PartTypes.FLETCHING, GearProperties.PROJECTILE_ACCURACY, 0.9f)
        );
        // Slime
        ret.add(MaterialBuilder.simple(modId("slime"))
                .crafting(Items.SLIME_BLOCK, MaterialCategories.SLIME, MaterialCategories.ORGANIC, MaterialCategories.INTERMEDIATE)
                .displayWithDefaultName(0x8CD782, TextureType.LOW_CONTRAST)
                // lining
                .stat(PartTypes.LINING, GearProperties.ARMOR_TOUGHNESS, 0.5f, NumberProperty.Operation.ADD)
                .trait(PartTypes.LINING, Const.Traits.BOUNCE, 1)
        );
        // Turtle
        ret.add(MaterialBuilder.simple(modId("turtle"))
                .crafting(Items.TURTLE_SCUTE, MaterialCategories.ORGANIC, MaterialCategories.INTERMEDIATE)
                .displayWithDefaultName(0x47BF4A, TextureType.LOW_CONTRAST)
                // main
                .stat(PartTypes.MAIN, GearProperties.DURABILITY, 0)
                .stat(PartGearKey.ofMain(GearTypes.HELMET), GearProperties.ARMOR_DURABILITY, 25)
                .stat(PartTypes.MAIN, GearProperties.ENCHANTMENT_VALUE, 9)
                .stat(PartTypes.MAIN, GearProperties.CHARGING_VALUE, 0.5f)
                .stat(PartTypes.MAIN, GearProperties.RARITY, 20)
                .mainStatsArmor(2, 0, 0, 0, 0, 4)
                .trait(PartTypes.MAIN, Const.Traits.TURTLE, 1, new MaterialCountTraitCondition(2))
        );
    }

    private void addCompounds(Collection<MaterialBuilder<?>> ret, HolderLookup.Provider registries) {
        ret.add(compoundBuilder(modId("crude_alloy"), SgItems.CRUDE_ALLOY));
        ret.add(compoundBuilder(modId("hybrid_gem"), SgItems.HYBRID_GEM));
        ret.add(compoundBuilder(modId("metal_alloy"), SgItems.ALLOY_INGOT));
        ret.add(compoundBuilder(modId("mixed_fabric"), SgItems.MIXED_FABRIC));
        ret.add(compoundBuilder(modId("super_alloy"), SgItems.SUPER_ALLOY));

        // Dimerald
        ret.add(customCompoundBuilder(modId("dimerald"), SgItems.CUSTOM_GEM.get(), MaterialCategories.GEM, MaterialCategories.ADVANCED)
                .displayWithDefaultName(0x1ACE82, TextureType.HIGH_CONTRAST)
                //main
                .mainStatsCommon(1776, 36, 12, 80, 0.7f)
                .mainStatsHarvest(BuiltinMaterials.DIMERALD.getHarvestTier(), 9)
                .mainStatsMelee(3, 3, 0.1f)
                .mainStatsRanged(3, 0.1f, 1.0f, 1.2f)
                .mainStatsArmor(4, 9, 6, 3, 10, 10) //22
                .trait(PartTypes.MAIN, Const.Traits.BRITTLE, 1)
                .trait(PartTypes.MAIN, Const.Traits.GOLD_DIGGER, 2, materialCountOrRatio(3, 0.5f))
                //rod
                .stat(PartTypes.ROD, GearProperties.HARVEST_SPEED, 0.15f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .trait(PartTypes.ROD, Const.Traits.BRITTLE, 1, new MaterialRatioTraitCondition(0.5f))
                .trait(PartTypes.ROD, Const.Traits.ANCIENT, 3, new MaterialRatioTraitCondition(0.5f))
                //tip
                .stat(PartTypes.TIP, GearProperties.DURABILITY, 360, NumberProperty.Operation.ADD)
                .harvestTierBuiltin(PartTypes.TIP)
                .stat(PartTypes.TIP, GearProperties.HARVEST_SPEED, 2, NumberProperty.Operation.ADD)
                .stat(PartTypes.TIP, GearProperties.ATTACK_DAMAGE, 2, NumberProperty.Operation.ADD)
                .stat(PartTypes.TIP, GearProperties.MAGIC_DAMAGE, 1, NumberProperty.Operation.ADD)
                .stat(PartTypes.TIP, GearProperties.RANGED_DAMAGE, 0.5f, NumberProperty.Operation.ADD)
                .stat(PartTypes.TIP, GearProperties.RARITY, 25, NumberProperty.Operation.ADD)
                .trait(PartTypes.TIP, Const.Traits.IMPERIAL, 2)
                //setting
                .trait(PartTypes.SETTING, Const.Traits.KITTY_VISION, 1)
        );

        // High-Carbon Steel
        ret.add(customCompoundBuilder(modId("high_carbon_steel"), SgItems.CUSTOM_INGOT.get(), MaterialCategories.METAL, MaterialCategories.ADVANCED)
                .displayWithDefaultName(0x848484, TextureType.HIGH_CONTRAST)
                .mainStatsCommon(420, 24, 11, 40, 0.8f)
                .stat(PartTypes.MAIN, GearProperties.REPAIR_VALUE, 0.05f)
                .mainStatsHarvest(BuiltinMaterials.HIGH_CARBON_STEEL.getHarvestTier(), 6)
                .mainStatsMelee(4, 1, -0.2f)
                .mainStatsArmor(3, 8, 6, 3, 2, 6)
                .mainStatsRanged(2, -0.2f)
                .trait(PartTypes.MAIN, Const.Traits.MALLEABLE, 3)
        );
    }

    private void addSimpleRods(Collection<MaterialBuilder<?>> ret, HolderLookup.Provider registries) {
        // Blaze Rod
        ret.add(MaterialBuilder.simple(modId("blaze_rod"))
                .crafting(new MaterialCraftingData(
                        Optional.empty(),
                        List.of(MaterialCategories.METAL),
                        List.of(),
                        Map.of(PartTypes.ROD.get(), taggedItems(Tags.Items.RODS_BLAZE)),
                        true
                ))
                .displayWithDefaultName(0xFFC600, TextureType.HIGH_CONTRAST)
                .stat(PartTypes.ROD, GearProperties.HARVEST_SPEED, 0.1f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .stat(PartTypes.ROD, GearProperties.ATTACK_DAMAGE, 0.2f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .stat(PartTypes.ROD, GearProperties.RANGED_DAMAGE, 0.1f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .trait(PartTypes.ROD, Const.Traits.FLEXIBLE, 4)
                .trait(PartTypes.ROD, Const.Traits.REACH, 1)
        );
        // Bone
        ret.add(MaterialBuilder.builtin(BuiltinMaterials.BONE)
                .crafting(new MaterialCraftingData(
                        Ingredient.of(Items.BONE_BLOCK),
                        List.of(MaterialCategories.ORGANIC, MaterialCategories.BASIC),
                        List.of(),
                        Map.of(PartTypes.ROD.get(), Ingredient.of(Items.BONE)),
                        true
                ))
                .displayWithDefaultName(0xFCFBED, TextureType.LOW_CONTRAST)
                //main
                .mainStatsCommon(108, 4, 5, 8, 0.9f)
                .mainStatsHarvest(4)
                .mainStatsMelee(2, 1, 0.1f)
                .mainStatsRanged(1f, 0f, 0.9f, 1f)
                .mainStatsArmor(1, 2, 1, 1, 0, 1) //5
                .trait(PartTypes.MAIN, Const.Traits.CHIPPING, 2)
                //rod
                .stat(PartTypes.ROD, GearProperties.ATTACK_DAMAGE, 0.2f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .trait(PartTypes.ROD, Const.Traits.FLEXIBLE, 2)
        );
        // Breeze Rod
        ret.add(MaterialBuilder.simple(modId("breeze_rod"))
                .crafting(new MaterialCraftingData(
                        Optional.empty(),
                        List.of(MaterialCategories.METAL),
                        List.of(),
                        Map.of(PartTypes.ROD.get(), taggedItems(Tags.Items.RODS_BREEZE)),
                        true
                ))
                .displayWithDefaultName(0x9398C1, TextureType.HIGH_CONTRAST)
                .stat(PartTypes.ROD, GearProperties.HARVEST_SPEED, 0.2f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .stat(PartTypes.ROD, GearProperties.ATTACK_DAMAGE, 0.1f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .stat(PartTypes.ROD, GearProperties.RANGED_DAMAGE, 0.2f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .trait(PartTypes.ROD, Const.Traits.FLEXIBLE, 4)
                .trait(PartTypes.ROD, Const.Traits.WIND_BLAST, 1)
        );
        // End Rod
        ret.add(MaterialBuilder.simple(modId("end_rod"))
                .crafting(new MaterialCraftingData(
                        Optional.empty(),
                        List.of(MaterialCategories.METAL),
                        List.of(),
                        Map.of(PartTypes.ROD.get(), Ingredient.of(Items.END_ROD)),
                        true
                ))
                .displayWithDefaultName(0xF6E2CD, TextureType.HIGH_CONTRAST)
                .stat(PartTypes.ROD, GearProperties.HARVEST_SPEED, 0.2f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .stat(PartTypes.ROD, GearProperties.ATTACK_DAMAGE, 0.1f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .stat(PartTypes.ROD, GearProperties.RANGED_DAMAGE, 0.1f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .trait(PartTypes.ROD, Const.Traits.FLEXIBLE, 4)
                .trait(PartTypes.ROD, Const.Traits.STURDY, 2)
                .trait(PartTypes.ROD, Const.Traits.REFRACTIVE, 1)
        );
    }

    private void addExtraMetals(Collection<MaterialBuilder<?>> ret, HolderLookup.Provider registries) {
        // Aluminum
        ret.add(extraMetal(ExtraMetals.ALUMINUM, 2)
                .displayWithDefaultName(0xBFD4DE, TextureType.HIGH_CONTRAST)
                .stat(PartTypes.MAIN, GearProperties.DURABILITY, 365)
                .stat(PartTypes.MAIN, GearProperties.ARMOR_DURABILITY, 15)
                .stat(PartTypes.MAIN, GearProperties.ENCHANTMENT_VALUE, 14)
                .stat(PartTypes.MAIN, GearProperties.HARVEST_TIER, HarvestTier.IRON)
                .stat(PartTypes.MAIN, GearProperties.HARVEST_SPEED, 8)
                .stat(PartTypes.MAIN, GearProperties.ATTACK_DAMAGE, 2)
                .stat(PartTypes.MAIN, GearProperties.MAGIC_DAMAGE, 2)
                .stat(PartTypes.MAIN, GearProperties.ATTACK_SPEED, 0.2f)
                .mainStatsArmor(2, 6, 4, 2, 0.5f, 3) //14
                .stat(PartTypes.MAIN, GearProperties.RANGED_DAMAGE, 2)
                .stat(PartTypes.MAIN, GearProperties.DRAW_SPEED, 0.2f)
                .stat(PartTypes.MAIN, GearProperties.RARITY, 25)
                .stat(PartTypes.MAIN, GearProperties.CHARGING_VALUE, 0.9f)
                .stat(PartTypes.ROD, GearProperties.DURABILITY, 0.2f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .stat(PartTypes.ROD, GearProperties.RANGED_DAMAGE, 0.1f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .stat(PartTypes.ROD, GearProperties.RARITY, 30)
                .trait(PartTypes.MAIN, Const.Traits.MALLEABLE, 3)
                .trait(PartTypes.MAIN, Const.Traits.SOFT, 2, new MaterialRatioTraitCondition(0.5f))
                .trait(PartTypes.MAIN, Const.Traits.SYNERGISTIC, 1)
                .trait(PartTypes.ROD, Const.Traits.MALLEABLE, 3)
                .trait(PartTypes.ROD, Const.Traits.SYNERGISTIC, 2)
        );
        // Aluminum Steel
        ret.add(extraMetal(ExtraMetals.ALUMINUM_STEEL, 3)
                .displayWithDefaultName(0x98D9DA, TextureType.HIGH_CONTRAST)
                .stat(PartTypes.MAIN, GearProperties.DURABILITY, 660)
                .stat(PartTypes.MAIN, GearProperties.ARMOR_DURABILITY, 18)
                .stat(PartTypes.MAIN, GearProperties.REPAIR_VALUE, -0.15f)
                .stat(PartTypes.MAIN, GearProperties.ENCHANTMENT_VALUE, 11)
                .stat(PartTypes.MAIN, GearProperties.HARVEST_TIER, HarvestTier.DIAMOND)
                .stat(PartTypes.MAIN, GearProperties.HARVEST_SPEED, 8)
                .stat(PartTypes.MAIN, GearProperties.ATTACK_DAMAGE, 4)
                .stat(PartTypes.MAIN, GearProperties.MAGIC_DAMAGE, 1.5f)
                .stat(PartTypes.MAIN, GearProperties.ATTACK_SPEED, 0.1f)
                .mainStatsArmor(3, 7, 5, 3, 2, 6) //18
                .stat(PartTypes.MAIN, GearProperties.RANGED_DAMAGE, 2)
                .stat(PartTypes.MAIN, GearProperties.DRAW_SPEED, 0f)
                .stat(PartTypes.MAIN, GearProperties.RARITY, 45)
                .stat(PartTypes.MAIN, GearProperties.CHARGING_VALUE, 1.0f)
                .stat(PartTypes.ROD, GearProperties.DURABILITY, 0.3f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .stat(PartTypes.ROD, GearProperties.HARVEST_SPEED, 0.1f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .stat(PartTypes.ROD, GearProperties.RARITY, 45)
                .trait(PartTypes.MAIN, Const.Traits.MALLEABLE, 4)
                .trait(PartTypes.MAIN, Const.Traits.SYNERGISTIC, 2, new MaterialRatioTraitCondition(0.5f))
                .trait(PartTypes.ROD, Const.Traits.MALLEABLE, 3)
                .trait(PartTypes.ROD, Const.Traits.SYNERGISTIC, 3)
        );
        // Bismuth
        ret.add(extraMetal(ExtraMetals.BISMUTH, 2)
                .displayWithDefaultName(0xD1C2D5, TextureType.HIGH_CONTRAST)
                .stat(PartTypes.MAIN, GearProperties.DURABILITY, 330)
                .stat(PartTypes.MAIN, GearProperties.ARMOR_DURABILITY, 10)
                .stat(PartTypes.MAIN, GearProperties.ENCHANTMENT_VALUE, 12)
                .stat(PartTypes.MAIN, GearProperties.HARVEST_TIER, HarvestTier.IRON)
                .stat(PartTypes.MAIN, GearProperties.HARVEST_SPEED, 5)
                .stat(PartTypes.MAIN, GearProperties.ATTACK_DAMAGE, 2.5f)
                .stat(PartTypes.MAIN, GearProperties.MAGIC_DAMAGE, 3.5f)
                .stat(PartTypes.MAIN, GearProperties.ATTACK_SPEED, 0.1f)
                .mainStatsArmor(3, 6, 5, 2, 2, 6) //16
                .stat(PartTypes.MAIN, GearProperties.RANGED_DAMAGE, 1)
                .stat(PartTypes.MAIN, GearProperties.DRAW_SPEED, 0f)
                .stat(PartTypes.MAIN, GearProperties.RARITY, 35)
                .stat(PartTypes.MAIN, GearProperties.CHARGING_VALUE, 0.9f)
                .stat(PartTypes.ROD, GearProperties.DURABILITY, 0.1f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .stat(PartTypes.ROD, GearProperties.HARVEST_SPEED, 0.1f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .stat(PartTypes.ROD, GearProperties.RARITY, 35)
                .trait(PartTypes.MAIN, Const.Traits.MALLEABLE, 2)
                .trait(PartTypes.MAIN, Const.Traits.LUSTROUS, 2, new MaterialRatioTraitCondition(0.5f))
                .trait(PartTypes.ROD, Const.Traits.MALLEABLE, 2)
        );
        // Bismuth Brass
        ret.add(extraMetal(ExtraMetals.BISMUTH_BRASS, 2)
                .displayWithDefaultName(0xE9C1B4, TextureType.HIGH_CONTRAST)
                .stat(PartTypes.MAIN, GearProperties.DURABILITY, 580)
                .stat(PartTypes.MAIN, GearProperties.ARMOR_DURABILITY, 15)
                .stat(PartTypes.MAIN, GearProperties.REPAIR_VALUE, 0.15f)
                .stat(PartTypes.MAIN, GearProperties.ENCHANTMENT_VALUE, 14)
                .stat(PartTypes.MAIN, GearProperties.HARVEST_TIER, HarvestTier.IRON)
                .stat(PartTypes.MAIN, GearProperties.HARVEST_SPEED, 9)
                .stat(PartTypes.MAIN, GearProperties.ATTACK_DAMAGE, 3f)
                .stat(PartTypes.MAIN, GearProperties.MAGIC_DAMAGE, 2f)
                .stat(PartTypes.MAIN, GearProperties.ATTACK_SPEED, 0.1f)
                .mainStatsArmor(3, 7, 5, 3, 2, 8) //18
                .stat(PartTypes.MAIN, GearProperties.RANGED_DAMAGE, 2)
                .stat(PartTypes.MAIN, GearProperties.DRAW_SPEED, 0.2f)
                .stat(PartTypes.MAIN, GearProperties.RARITY, 50)
                .stat(PartTypes.MAIN, GearProperties.CHARGING_VALUE, 1.1f)
                .stat(PartTypes.ROD, GearProperties.DURABILITY, -0.1f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .stat(PartTypes.ROD, GearProperties.HARVEST_SPEED, 0.2f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .stat(PartTypes.ROD, GearProperties.RARITY, 50)
                .trait(PartTypes.MAIN, Const.Traits.MALLEABLE, 2)
                .trait(PartTypes.MAIN, Const.Traits.LUSTROUS, 2, new MaterialRatioTraitCondition(0.5f))
                .trait(PartTypes.ROD, Const.Traits.MALLEABLE, 2)
        );
        // Bismuth Steel
        ret.add(extraMetal(ExtraMetals.BISMUTH_STEEL, 3)
                .displayWithDefaultName(0xDC9FE7, TextureType.HIGH_CONTRAST)
                .stat(PartTypes.MAIN, GearProperties.DURABILITY, 1050)
                .stat(PartTypes.MAIN, GearProperties.ARMOR_DURABILITY, 25)
                .stat(PartTypes.MAIN, GearProperties.REPAIR_VALUE, -0.15f)
                .stat(PartTypes.MAIN, GearProperties.ENCHANTMENT_VALUE, 14)
                .stat(PartTypes.MAIN, GearProperties.HARVEST_TIER, HarvestTier.DIAMOND)
                .stat(PartTypes.MAIN, GearProperties.HARVEST_SPEED, 10)
                .stat(PartTypes.MAIN, GearProperties.ATTACK_DAMAGE, 4f)
                .stat(PartTypes.MAIN, GearProperties.MAGIC_DAMAGE, 5f)
                .stat(PartTypes.MAIN, GearProperties.ATTACK_SPEED, -0.1f)
                .mainStatsArmor(3, 8, 6, 3, 4, 8) //20
                .stat(PartTypes.MAIN, GearProperties.RANGED_DAMAGE, 3)
                .stat(PartTypes.MAIN, GearProperties.DRAW_SPEED, 0.1f)
                .stat(PartTypes.MAIN, GearProperties.RARITY, 60)
                .stat(PartTypes.MAIN, GearProperties.CHARGING_VALUE, 1.0f)
                .stat(PartTypes.ROD, GearProperties.DURABILITY, 0.25f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .stat(PartTypes.ROD, GearProperties.HARVEST_SPEED, 0.15f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .stat(PartTypes.ROD, GearProperties.RARITY, 60)
                .trait(PartTypes.MAIN, Const.Traits.MALLEABLE, 3)
                .trait(PartTypes.MAIN, Const.Traits.LUSTROUS, 2, new MaterialRatioTraitCondition(0.5f))
                .trait(PartTypes.ROD, Const.Traits.MALLEABLE, 3)
        );
        // Brass
        ret.add(extraMetal(ExtraMetals.BRASS, 2)
                .displayWithDefaultName(0xF2D458, TextureType.HIGH_CONTRAST)
                .stat(PartTypes.MAIN, GearProperties.DURABILITY, 240)
                .stat(PartTypes.MAIN, GearProperties.ARMOR_DURABILITY, 8)
                .stat(PartTypes.MAIN, GearProperties.REPAIR_VALUE, 0.15f)
                .stat(PartTypes.MAIN, GearProperties.ENCHANTMENT_VALUE, 13)
                .stat(PartTypes.MAIN, GearProperties.HARVEST_TIER, HarvestTier.IRON)
                .stat(PartTypes.MAIN, GearProperties.HARVEST_SPEED, 7)
                .stat(PartTypes.MAIN, GearProperties.ATTACK_DAMAGE, 1.5f)
                .stat(PartTypes.MAIN, GearProperties.MAGIC_DAMAGE, 1.5f)
                .stat(PartTypes.MAIN, GearProperties.ATTACK_SPEED, 0.1f)
                .mainStatsArmor(2, 6, 4, 2, 1, 6) //14
                .stat(PartTypes.MAIN, GearProperties.RANGED_DAMAGE, 2)
                .stat(PartTypes.MAIN, GearProperties.DRAW_SPEED, 0.2f)
                .stat(PartTypes.MAIN, GearProperties.RARITY, 25)
                .stat(PartTypes.MAIN, GearProperties.CHARGING_VALUE, 1.2f)
                .stat(PartTypes.ROD, GearProperties.DURABILITY, 0.05f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .stat(PartTypes.ROD, GearProperties.HARVEST_SPEED, 0.05f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .stat(PartTypes.ROD, GearProperties.RARITY, 25)
                .trait(PartTypes.MAIN, Const.Traits.MALLEABLE, 2)
                .trait(PartTypes.MAIN, Const.Traits.SILKY, 1, new MaterialRatioTraitCondition(0.66f))
                .trait(PartTypes.ROD, Const.Traits.MALLEABLE, 2)
        );
        // Compressed Iron
        ret.add(extraMetal(ExtraMetals.COMPRESSED_IRON, 3)
                .displayWithDefaultName(0xA6A6A6, TextureType.HIGH_CONTRAST)
                .stat(PartTypes.MAIN, GearProperties.DURABILITY, 1024)
                .stat(PartTypes.MAIN, GearProperties.ARMOR_DURABILITY, 24)
                .stat(PartTypes.MAIN, GearProperties.REPAIR_VALUE, -0.15f)
                .stat(PartTypes.MAIN, GearProperties.ENCHANTMENT_VALUE, 12)
                .stat(PartTypes.MAIN, GearProperties.HARVEST_TIER, HarvestTier.DIAMOND)
                .stat(PartTypes.MAIN, GearProperties.HARVEST_SPEED, 9)
                .stat(PartTypes.MAIN, GearProperties.ATTACK_DAMAGE, 4f)
                .stat(PartTypes.MAIN, GearProperties.MAGIC_DAMAGE, 1f)
                .stat(PartTypes.MAIN, GearProperties.ATTACK_SPEED, -0.2f)
                .mainStatsArmor(3, 8, 6, 3, 2, 4) //20
                .stat(PartTypes.MAIN, GearProperties.RANGED_DAMAGE, 3)
                .stat(PartTypes.MAIN, GearProperties.DRAW_SPEED, 0.0f)
                .stat(PartTypes.MAIN, GearProperties.RARITY, 40)
                .stat(PartTypes.MAIN, GearProperties.CHARGING_VALUE, 0.8f)
                .stat(PartTypes.ROD, GearProperties.DURABILITY, 0.25f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .stat(PartTypes.ROD, GearProperties.RARITY, 40)
                .trait(PartTypes.MAIN, Const.Traits.MALLEABLE, 2)
                .trait(PartTypes.MAIN, Const.Traits.HARD, 3)
                .trait(PartTypes.ROD, Const.Traits.MALLEABLE, 2)
        );
        // Electrum
        ret.add(extraMetal(ExtraMetals.ELECTRUM, 2)
                .displayWithDefaultName(0xD6E037, TextureType.HIGH_CONTRAST)
                .stat(PartTypes.MAIN, GearProperties.DURABILITY, 96)
                .stat(PartTypes.MAIN, GearProperties.ARMOR_DURABILITY, 10)
                .stat(PartTypes.MAIN, GearProperties.ENCHANTMENT_VALUE, 25)
                .stat(PartTypes.MAIN, GearProperties.HARVEST_TIER, HarvestTier.IRON)
                .stat(PartTypes.MAIN, GearProperties.HARVEST_SPEED, 14)
                .stat(PartTypes.MAIN, GearProperties.ATTACK_DAMAGE, 2f)
                .stat(PartTypes.MAIN, GearProperties.MAGIC_DAMAGE, 5f)
                .stat(PartTypes.MAIN, GearProperties.ATTACK_SPEED, 0.3f)
                .mainStatsArmor(2, 6, 5, 2, 0, 11) //15
                .stat(PartTypes.MAIN, GearProperties.RANGED_DAMAGE, 1)
                .stat(PartTypes.MAIN, GearProperties.DRAW_SPEED, 0.4f)
                .stat(PartTypes.MAIN, GearProperties.RARITY, 40)
                .stat(PartTypes.MAIN, GearProperties.CHARGING_VALUE, 1.5f)
                .stat(PartTypes.ROD, GearProperties.ATTACK_SPEED, 0.1f, NumberProperty.Operation.ADD)
                .stat(PartTypes.ROD, GearProperties.DRAW_SPEED, 0.1f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .stat(PartTypes.ROD, GearProperties.RARITY, 40)
                .trait(PartTypes.MAIN, Const.Traits.MALLEABLE, 2)
                .trait(PartTypes.MAIN, Const.Traits.SOFT, 2, new MaterialRatioTraitCondition(0.5f))
                .trait(PartTypes.ROD, Const.Traits.LUSTROUS, 3, new MaterialRatioTraitCondition(0.5f))
        );
        // Enderium
        ret.add(extraMetal(ExtraMetals.ENDERIUM, 4)
                .displayWithDefaultName(0x468C75, TextureType.HIGH_CONTRAST)
                .stat(PartTypes.MAIN, GearProperties.DURABILITY, 1200)
                .stat(PartTypes.MAIN, GearProperties.ARMOR_DURABILITY, 34)
                .stat(PartTypes.MAIN, GearProperties.REPAIR_VALUE, 0.15f)
                .stat(PartTypes.MAIN, GearProperties.ENCHANTMENT_VALUE, 13)
                .stat(PartTypes.MAIN, GearProperties.HARVEST_TIER, HarvestTier.NETHERITE)
                .stat(PartTypes.MAIN, GearProperties.HARVEST_SPEED, 18)
                .stat(PartTypes.MAIN, GearProperties.ATTACK_DAMAGE, 6f)
                .stat(PartTypes.MAIN, GearProperties.MAGIC_DAMAGE, 4f)
                .stat(PartTypes.MAIN, GearProperties.ATTACK_SPEED, 0.0f)
                .mainStatsArmor(3, 9, 7, 3, 8, 10) //22
                .stat(PartTypes.MAIN, GearProperties.RANGED_DAMAGE, 3)
                .stat(PartTypes.MAIN, GearProperties.DRAW_SPEED, 0f)
                .stat(PartTypes.MAIN, GearProperties.RARITY, 80)
                .stat(PartTypes.MAIN, GearProperties.CHARGING_VALUE, 1.2f)
                .stat(PartTypes.ROD, GearProperties.DURABILITY, 0.2f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .stat(PartTypes.ROD, GearProperties.HARVEST_SPEED, 0.2f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .stat(PartTypes.ROD, GearProperties.RARITY, 60)
                .trait(PartTypes.MAIN, Const.Traits.MALLEABLE, 3)
                .trait(PartTypes.ROD, Const.Traits.MALLEABLE, 2)
        );
        // Invar
        ret.add(extraMetal(ExtraMetals.INVAR, 2)
                .displayWithDefaultName(0xC2CBB8, TextureType.HIGH_CONTRAST)
                .stat(PartTypes.MAIN, GearProperties.DURABILITY, 640)
                .stat(PartTypes.MAIN, GearProperties.ARMOR_DURABILITY, 20)
                .stat(PartTypes.MAIN, GearProperties.REPAIR_VALUE, 0.15f)
                .stat(PartTypes.MAIN, GearProperties.ENCHANTMENT_VALUE, 13)
                .stat(PartTypes.MAIN, GearProperties.HARVEST_TIER, HarvestTier.IRON)
                .stat(PartTypes.MAIN, GearProperties.HARVEST_SPEED, 7)
                .stat(PartTypes.MAIN, GearProperties.ATTACK_DAMAGE, 3.5f)
                .stat(PartTypes.MAIN, GearProperties.MAGIC_DAMAGE, 3f)
                .stat(PartTypes.MAIN, GearProperties.ATTACK_SPEED, 0.0f)
                .mainStatsArmor(2, 8, 6, 2, 2, 6) //18
                .stat(PartTypes.MAIN, GearProperties.RANGED_DAMAGE, 2)
                .stat(PartTypes.MAIN, GearProperties.DRAW_SPEED, 0f)
                .stat(PartTypes.MAIN, GearProperties.RARITY, 50)
                .stat(PartTypes.MAIN, GearProperties.CHARGING_VALUE, 1.2f)
                .stat(PartTypes.ROD, GearProperties.DURABILITY, 0.3f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .stat(PartTypes.ROD, GearProperties.ATTACK_DAMAGE, 0.1f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .stat(PartTypes.ROD, GearProperties.RARITY, 50)
                .trait(PartTypes.MAIN, Const.Traits.MALLEABLE, 3)
                .trait(PartTypes.MAIN, Const.Traits.ADAMANT, 2, materialCountOrRatio(3, 0.35f))
                .trait(PartTypes.ROD, Const.Traits.MALLEABLE, 3)
        );
        // Lead
        ret.add(extraMetal(ExtraMetals.LEAD, 2)
                .displayWithDefaultName(0xC2CBB8, TextureType.HIGH_CONTRAST)
                .stat(PartTypes.MAIN, GearProperties.DURABILITY, 260)
                .stat(PartTypes.MAIN, GearProperties.ARMOR_DURABILITY, 14)
                .stat(PartTypes.MAIN, GearProperties.ENCHANTMENT_VALUE, 15)
                .stat(PartTypes.MAIN, GearProperties.HARVEST_TIER, HarvestTier.IRON)
                .stat(PartTypes.MAIN, GearProperties.HARVEST_SPEED, 4)
                .stat(PartTypes.MAIN, GearProperties.ATTACK_DAMAGE, 2f)
                .stat(PartTypes.MAIN, GearProperties.MAGIC_DAMAGE, 2f)
                .stat(PartTypes.MAIN, GearProperties.ATTACK_SPEED, -0.4f)
                .mainStatsArmor(2, 5, 4, 2, 0, 4) //13
                .stat(PartTypes.MAIN, GearProperties.RANGED_DAMAGE, 1)
                .stat(PartTypes.MAIN, GearProperties.DRAW_SPEED, -0.3f)
                .stat(PartTypes.MAIN, GearProperties.RARITY, 40)
                .stat(PartTypes.MAIN, GearProperties.CHARGING_VALUE, 0.8f)
                .stat(PartTypes.ROD, GearProperties.DURABILITY, 0.1f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .stat(PartTypes.ROD, GearProperties.HARVEST_SPEED, -0.1f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .stat(PartTypes.ROD, GearProperties.RARITY, 40)
                .trait(PartTypes.MAIN, Const.Traits.MALLEABLE, 2)
                .trait(PartTypes.MAIN, Const.Traits.AQUATIC, 2, materialCountOrRatio(3, 0.35f))
                .trait(PartTypes.ROD, Const.Traits.SOFT, 4)
        );
        // Lumium
        ret.add(extraMetal(ExtraMetals.LUMIUM, 3)
                .displayWithDefaultName(0xFFD789, TextureType.HIGH_CONTRAST)
                .stat(PartTypes.MAIN, GearProperties.DURABILITY, 920)
                .stat(PartTypes.MAIN, GearProperties.ARMOR_DURABILITY, 20)
                .stat(PartTypes.MAIN, GearProperties.REPAIR_VALUE, 0.15f)
                .stat(PartTypes.MAIN, GearProperties.ENCHANTMENT_VALUE, 14)
                .stat(PartTypes.MAIN, GearProperties.HARVEST_TIER, HarvestTier.DIAMOND)
                .stat(PartTypes.MAIN, GearProperties.HARVEST_SPEED, 15)
                .stat(PartTypes.MAIN, GearProperties.ATTACK_DAMAGE, 4f)
                .stat(PartTypes.MAIN, GearProperties.MAGIC_DAMAGE, 3f)
                .stat(PartTypes.MAIN, GearProperties.ATTACK_SPEED, 0.2f)
                .mainStatsArmor(2, 8, 6, 2, 4, 10) //18
                .stat(PartTypes.MAIN, GearProperties.RANGED_DAMAGE, 2)
                .stat(PartTypes.MAIN, GearProperties.DRAW_SPEED, 0f)
                .stat(PartTypes.MAIN, GearProperties.RARITY, 75)
                .stat(PartTypes.MAIN, GearProperties.CHARGING_VALUE, 1.3f)
                .stat(PartTypes.ROD, GearProperties.HARVEST_SPEED, 0.2f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .stat(PartTypes.ROD, GearProperties.RANGED_DAMAGE, 0.1f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .stat(PartTypes.ROD, GearProperties.RARITY, 75)
                .trait(PartTypes.MAIN, Const.Traits.MALLEABLE, 3)
                .trait(PartTypes.MAIN, Const.Traits.REFRACTIVE, 1, new MaterialRatioTraitCondition(0.5f))
                .trait(PartTypes.ROD, Const.Traits.MALLEABLE, 3)
                .trait(PartTypes.ROD, Const.Traits.REFRACTIVE, 1, new MaterialRatioTraitCondition(0.5f))
        );
        // Nickel
        ret.add(extraMetal(ExtraMetals.NICKEL, 2)
                .displayWithDefaultName(0xEFE87B, TextureType.HIGH_CONTRAST)
                .stat(PartTypes.MAIN, GearProperties.DURABILITY, 380)
                .stat(PartTypes.MAIN, GearProperties.ARMOR_DURABILITY, 17)
                .stat(PartTypes.MAIN, GearProperties.ENCHANTMENT_VALUE, 12)
                .stat(PartTypes.MAIN, GearProperties.HARVEST_SPEED, 7)
                .stat(PartTypes.MAIN, GearProperties.HARVEST_TIER, HarvestTier.IRON)
                .stat(PartTypes.MAIN, GearProperties.ATTACK_DAMAGE, 2.5f)
                .stat(PartTypes.MAIN, GearProperties.MAGIC_DAMAGE, 1f)
                .stat(PartTypes.MAIN, GearProperties.ATTACK_SPEED, 0.1f)
                .mainStatsArmor(2, 5, 4, 2, 0, 6) //13
                .stat(PartTypes.MAIN, GearProperties.RANGED_DAMAGE, 0.5f)
                .stat(PartTypes.MAIN, GearProperties.DRAW_SPEED, 0.1f)
                .stat(PartTypes.MAIN, GearProperties.RARITY, 40)
                .stat(PartTypes.MAIN, GearProperties.CHARGING_VALUE, 1.0f)
                .stat(PartTypes.ROD, GearProperties.DURABILITY, 0.2f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .stat(PartTypes.ROD, GearProperties.ATTACK_DAMAGE, 0.1f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .stat(PartTypes.ROD, GearProperties.RARITY, 40)
                .trait(PartTypes.MAIN, Const.Traits.MALLEABLE, 3)
                .trait(PartTypes.MAIN, Const.Traits.ADAMANT, 1, materialCountOrRatio(3, 0.35f))
                .trait(PartTypes.ROD, Const.Traits.MALLEABLE, 3)
        );
        // Osmium
        ret.add(extraMetal(ExtraMetals.OSMIUM, 2)
                .displayWithDefaultName(0x92A6B8, TextureType.HIGH_CONTRAST)
                .stat(PartTypes.MAIN, GearProperties.DURABILITY, 500)
                .stat(PartTypes.MAIN, GearProperties.ARMOR_DURABILITY, 30)
                .stat(PartTypes.MAIN, GearProperties.ENCHANTMENT_VALUE, 12)
                .stat(PartTypes.MAIN, GearProperties.HARVEST_TIER, HarvestTier.IRON)
                .stat(PartTypes.MAIN, GearProperties.HARVEST_SPEED, 10)
                .stat(PartTypes.MAIN, GearProperties.ATTACK_DAMAGE, 4f)
                .stat(PartTypes.MAIN, GearProperties.MAGIC_DAMAGE, 2f)
                .stat(PartTypes.MAIN, GearProperties.ATTACK_SPEED, 0.1f)
                .mainStatsArmor(3, 6, 5, 3, 0, 4) //17
                .stat(PartTypes.MAIN, GearProperties.RANGED_DAMAGE, 1f)
                .stat(PartTypes.MAIN, GearProperties.DRAW_SPEED, 0f)
                .stat(PartTypes.MAIN, GearProperties.RARITY, 35)
                .stat(PartTypes.MAIN, GearProperties.CHARGING_VALUE, 1.1f)
                .stat(PartTypes.ROD, GearProperties.DURABILITY, 0.1f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .stat(PartTypes.ROD, GearProperties.REPAIR_EFFICIENCY, 0.1f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .stat(PartTypes.ROD, GearProperties.RARITY, 35)
                .trait(PartTypes.MAIN, Const.Traits.MALLEABLE, 2)
                .trait(PartTypes.ROD, Const.Traits.MALLEABLE, 2)
        );
        // Platinum
        ret.add(extraMetal(ExtraMetals.PLATINUM, 3)
                .displayWithDefaultName(0xB3B3FF, TextureType.HIGH_CONTRAST)
                .stat(PartTypes.MAIN, GearProperties.DURABILITY, 900)
                .stat(PartTypes.MAIN, GearProperties.ARMOR_DURABILITY, 21)
                .stat(PartTypes.MAIN, GearProperties.ENCHANTMENT_VALUE, 14)
                .stat(PartTypes.MAIN, GearProperties.HARVEST_TIER, HarvestTier.DIAMOND)
                .stat(PartTypes.MAIN, GearProperties.HARVEST_SPEED, 12)
                .stat(PartTypes.MAIN, GearProperties.ATTACK_DAMAGE, 4f)
                .stat(PartTypes.MAIN, GearProperties.MAGIC_DAMAGE, 4f)
                .stat(PartTypes.MAIN, GearProperties.ATTACK_SPEED, 0.0f)
                .mainStatsArmor(2, 8, 6, 2, 2, 12) //18
                .stat(PartTypes.MAIN, GearProperties.RANGED_DAMAGE, 1f)
                .stat(PartTypes.MAIN, GearProperties.DRAW_SPEED, 0f)
                .stat(PartTypes.MAIN, GearProperties.RARITY, 80)
                .stat(PartTypes.MAIN, GearProperties.CHARGING_VALUE, 1.2f)
                .stat(PartTypes.ROD, GearProperties.DURABILITY, -0.1f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .stat(PartTypes.ROD, GearProperties.REPAIR_EFFICIENCY, 0.1f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .stat(PartTypes.ROD, GearProperties.RARITY, 70)
                .trait(PartTypes.MAIN, Const.Traits.MALLEABLE, 3)
                .trait(PartTypes.MAIN, Const.Traits.SOFT, 2)
                .trait(PartTypes.ROD, Const.Traits.MALLEABLE, 2)
                .trait(PartTypes.ROD, Const.Traits.SOFT, 4)
        );
        // Redstone Alloy
        ret.add(extraMetal(ExtraMetals.REDSTONE_ALLOY, 2)
                .displayWithDefaultName(0xE60006, TextureType.HIGH_CONTRAST)
                .stat(PartTypes.MAIN, GearProperties.DURABILITY, 840)
                .stat(PartTypes.MAIN, GearProperties.ARMOR_DURABILITY, 20)
                .stat(PartTypes.MAIN, GearProperties.REPAIR_VALUE, -0.15f)
                .stat(PartTypes.MAIN, GearProperties.ENCHANTMENT_VALUE, 18)
                .stat(PartTypes.MAIN, GearProperties.HARVEST_TIER, HarvestTier.IRON)
                .stat(PartTypes.MAIN, GearProperties.HARVEST_SPEED, 11)
                .stat(PartTypes.MAIN, GearProperties.ATTACK_DAMAGE, 2f)
                .stat(PartTypes.MAIN, GearProperties.MAGIC_DAMAGE, 4f)
                .stat(PartTypes.MAIN, GearProperties.ATTACK_SPEED, 0.2f)
                .mainStatsArmor(3, 7, 5, 2, 1, 8) //17
                .stat(PartTypes.MAIN, GearProperties.RANGED_DAMAGE, 1)
                .stat(PartTypes.MAIN, GearProperties.DRAW_SPEED, 0.2f)
                .stat(PartTypes.MAIN, GearProperties.RARITY, 45)
                .stat(PartTypes.MAIN, GearProperties.CHARGING_VALUE, 0.9f)
                .stat(PartTypes.ROD, GearProperties.HARVEST_SPEED, 0.2f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .stat(PartTypes.ROD, GearProperties.RARITY, 45)
                .trait(PartTypes.MAIN, Const.Traits.MALLEABLE, 3)
                .trait(PartTypes.MAIN, Const.Traits.ERODED, 3)
                .trait(PartTypes.ROD, Const.Traits.MALLEABLE, 3)
        );
        // Refined glowstone
        ret.add(extraMetal(ExtraMetals.REFINED_GLOWSTONE, 3)
                .displayWithDefaultName(0xFDE054, TextureType.HIGH_CONTRAST)
                .stat(PartTypes.MAIN, GearProperties.DURABILITY, 300)
                .stat(PartTypes.MAIN, GearProperties.ARMOR_DURABILITY, 18)
                .stat(PartTypes.MAIN, GearProperties.REPAIR_VALUE, 0.15f)
                .stat(PartTypes.MAIN, GearProperties.ENCHANTMENT_VALUE, 18)
                .stat(PartTypes.MAIN, GearProperties.HARVEST_TIER, HarvestTier.IRON)
                .stat(PartTypes.MAIN, GearProperties.HARVEST_SPEED, 14)
                .stat(PartTypes.MAIN, GearProperties.ATTACK_DAMAGE, 5f)
                .stat(PartTypes.MAIN, GearProperties.MAGIC_DAMAGE, 3f)
                .stat(PartTypes.MAIN, GearProperties.ATTACK_SPEED, 0.0f)
                .mainStatsArmor(3, 7, 6, 3, 0, 8) //19
                .stat(PartTypes.MAIN, GearProperties.RANGED_DAMAGE, 3)
                .stat(PartTypes.MAIN, GearProperties.DRAW_SPEED, 0.0f)
                .stat(PartTypes.MAIN, GearProperties.RARITY, 45)
                .stat(PartTypes.MAIN, GearProperties.CHARGING_VALUE, 0.8f)
                .stat(PartTypes.ROD, GearProperties.DURABILITY, -0.1f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .stat(PartTypes.ROD, GearProperties.HARVEST_SPEED, 0.2f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .stat(PartTypes.ROD, GearProperties.RARITY, 45)
                .stat(PartTypes.TIP, GearProperties.HARVEST_SPEED, 5, NumberProperty.Operation.ADD)
                .trait(PartTypes.MAIN, Const.Traits.MALLEABLE, 2)
                .trait(PartTypes.MAIN, Const.Traits.LUSTROUS, 4)
                .trait(PartTypes.ROD, Const.Traits.MALLEABLE, 4)
                .trait(PartTypes.ROD, Const.Traits.LUSTROUS, 1, new MaterialRatioTraitCondition(0.75f))
                .trait(PartTypes.TIP, Const.Traits.REFRACTIVE, 1)
        );
        // Refined Iron
        ret.add(extraMetal(ExtraMetals.REFINED_IRON, 2)
                .displayWithDefaultName(0xD7D7D7, TextureType.HIGH_CONTRAST)
                .stat(PartTypes.MAIN, GearProperties.DURABILITY, 512)
                .stat(PartTypes.MAIN, GearProperties.ARMOR_DURABILITY, 20)
                .stat(PartTypes.MAIN, GearProperties.REPAIR_VALUE, -0.15f)
                .stat(PartTypes.MAIN, GearProperties.ENCHANTMENT_VALUE, 15)
                .stat(PartTypes.MAIN, GearProperties.HARVEST_TIER, HarvestTier.IRON)
                .stat(PartTypes.MAIN, GearProperties.HARVEST_SPEED, 7)
                .stat(PartTypes.MAIN, GearProperties.ATTACK_DAMAGE, 3f)
                .stat(PartTypes.MAIN, GearProperties.MAGIC_DAMAGE, 2f)
                .stat(PartTypes.MAIN, GearProperties.ATTACK_SPEED, 0.0f)
                .mainStatsArmor(3, 7, 5, 3, 2, 6) //18
                .stat(PartTypes.MAIN, GearProperties.RANGED_DAMAGE, 2)
                .stat(PartTypes.MAIN, GearProperties.DRAW_SPEED, 0.0f)
                .stat(PartTypes.MAIN, GearProperties.RARITY, 25)
                .stat(PartTypes.MAIN, GearProperties.CHARGING_VALUE, 0.8f)
                .stat(PartTypes.ROD, GearProperties.DURABILITY, 0.15f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .stat(PartTypes.ROD, GearProperties.RANGED_DAMAGE, 0.1f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .stat(PartTypes.ROD, GearProperties.RARITY, 25)
                .trait(PartTypes.MAIN, Const.Traits.MALLEABLE, 4)
                .trait(PartTypes.MAIN, Const.Traits.STELLAR, 1)
                .trait(PartTypes.ROD, Const.Traits.MALLEABLE, 4)
        );
        // Refined obsidian
        ret.add(extraMetal(ExtraMetals.REFINED_OBSIDIAN, 4)
                .displayWithDefaultName(0x665482, TextureType.HIGH_CONTRAST)
                .stat(PartTypes.MAIN, GearProperties.DURABILITY, 2500)
                .stat(PartTypes.MAIN, GearProperties.ARMOR_DURABILITY, 50)
                .stat(PartTypes.MAIN, GearProperties.REPAIR_VALUE, -0.3f)
                .stat(PartTypes.MAIN, GearProperties.ENCHANTMENT_VALUE, 40)
                .stat(PartTypes.MAIN, GearProperties.HARVEST_TIER, HarvestTier.DIAMOND)
                .stat(PartTypes.MAIN, GearProperties.HARVEST_SPEED, 20)
                .stat(PartTypes.MAIN, GearProperties.ATTACK_DAMAGE, 10f)
                .stat(PartTypes.MAIN, GearProperties.MAGIC_DAMAGE, 4f)
                .stat(PartTypes.MAIN, GearProperties.ATTACK_SPEED, 0.3f)
                .mainStatsArmor(5, 12, 8, 5, 16, 6) //30
                .stat(PartTypes.MAIN, GearProperties.RANGED_DAMAGE, 4)
                .stat(PartTypes.MAIN, GearProperties.DRAW_SPEED, 0.2f)
                .stat(PartTypes.MAIN, GearProperties.RARITY, 70)
                .stat(PartTypes.MAIN, GearProperties.CHARGING_VALUE, 0.8f)
                .stat(PartTypes.ROD, GearProperties.DURABILITY, 0.25f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .stat(PartTypes.ROD, GearProperties.RARITY, 70)
                .stat(PartTypes.TIP, GearProperties.DURABILITY, 600, NumberProperty.Operation.ADD)
                .trait(PartTypes.MAIN, Const.Traits.MALLEABLE, 3)
                .trait(PartTypes.MAIN, Const.Traits.HARD, 4)
                .trait(PartTypes.ROD, Const.Traits.MALLEABLE, 4)
                .trait(PartTypes.ROD, Const.Traits.HARD, 3)
                .trait(PartTypes.TIP, Const.Traits.VULCAN, 1)
        );
        // Signalum
        ret.add(extraMetal(ExtraMetals.SIGNALUM, 4)
                .displayWithDefaultName(0xFF5E28, TextureType.HIGH_CONTRAST)
                .stat(PartTypes.MAIN, GearProperties.DURABILITY, 800)
                .stat(PartTypes.MAIN, GearProperties.ARMOR_DURABILITY, 25)
                .stat(PartTypes.MAIN, GearProperties.REPAIR_VALUE, 0.15f)
                .stat(PartTypes.MAIN, GearProperties.ENCHANTMENT_VALUE, 16)
                .stat(PartTypes.MAIN, GearProperties.HARVEST_TIER, HarvestTier.DIAMOND)
                .stat(PartTypes.MAIN, GearProperties.HARVEST_SPEED, 13)
                .stat(PartTypes.MAIN, GearProperties.ATTACK_DAMAGE, 3f)
                .stat(PartTypes.MAIN, GearProperties.MAGIC_DAMAGE, 4f)
                .stat(PartTypes.MAIN, GearProperties.ATTACK_SPEED, 0.0f)
                .mainStatsArmor(2, 7, 5, 2, 2, 4) //16
                .stat(PartTypes.MAIN, GearProperties.RANGED_DAMAGE, 2f)
                .stat(PartTypes.MAIN, GearProperties.DRAW_SPEED, 0.2f)
                .stat(PartTypes.MAIN, GearProperties.RARITY, 50)
                .stat(PartTypes.MAIN, GearProperties.CHARGING_VALUE, 1.2f)
                .stat(PartTypes.ROD, GearProperties.HARVEST_SPEED, 0.3f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .stat(PartTypes.ROD, GearProperties.REPAIR_EFFICIENCY, -0.1f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .stat(PartTypes.ROD, GearProperties.RARITY, 50)
                .trait(PartTypes.MAIN, Const.Traits.FLEXIBLE, 2)
                .trait(PartTypes.MAIN, Const.Traits.LUSTROUS, 4)
                .trait(PartTypes.ROD, Const.Traits.FLEXIBLE, 4)
                .trait(PartTypes.ROD, Const.Traits.LUSTROUS, 2)
        );
        // Silver
        ret.add(extraMetal(ExtraMetals.SILVER, 2)
                .displayWithDefaultName(0xCBCCEA, TextureType.HIGH_CONTRAST)
                .stat(PartTypes.MAIN, GearProperties.DURABILITY, 64)
                .stat(PartTypes.MAIN, GearProperties.ARMOR_DURABILITY, 9)
                .stat(PartTypes.MAIN, GearProperties.ENCHANTMENT_VALUE, 20)
                .stat(PartTypes.MAIN, GearProperties.HARVEST_TIER, HarvestTier.GOLD)
                .stat(PartTypes.MAIN, GearProperties.HARVEST_SPEED, 11)
                .stat(PartTypes.MAIN, GearProperties.ATTACK_DAMAGE, 0f)
                .stat(PartTypes.MAIN, GearProperties.MAGIC_DAMAGE, 4f)
                .stat(PartTypes.MAIN, GearProperties.ATTACK_SPEED, 0.2f)
                .mainStatsArmor(2, 5, 3, 1, 0, 10) //11
                .stat(PartTypes.MAIN, GearProperties.RANGED_DAMAGE, 0f)
                .stat(PartTypes.MAIN, GearProperties.DRAW_SPEED, 0.3f)
                .stat(PartTypes.MAIN, GearProperties.RARITY, 40)
                .stat(PartTypes.MAIN, GearProperties.CHARGING_VALUE, 1.1f)
                .stat(PartTypes.ROD, GearProperties.HARVEST_SPEED, 0.1f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .stat(PartTypes.ROD, GearProperties.MAGIC_DAMAGE, 0.1f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .stat(PartTypes.ROD, GearProperties.RARITY, 40)
                .trait(PartTypes.MAIN, Const.Traits.MALLEABLE, 2)
                .trait(PartTypes.MAIN, Const.Traits.SOFT, 1)
                .trait(PartTypes.ROD, Const.Traits.MALLEABLE, 1)
                .trait(PartTypes.ROD, Const.Traits.SOFT, 2)
        );
        // Steel
        ret.add(extraMetal(ExtraMetals.STEEL, 2)
                .displayWithDefaultName(0x929292, TextureType.HIGH_CONTRAST)
                .stat(PartTypes.MAIN, GearProperties.DURABILITY, 500)
                .stat(PartTypes.MAIN, GearProperties.ARMOR_DURABILITY, 20)
                .stat(PartTypes.MAIN, GearProperties.REPAIR_VALUE, 0.15f)
                .stat(PartTypes.MAIN, GearProperties.ENCHANTMENT_VALUE, 11)
                .stat(PartTypes.MAIN, GearProperties.HARVEST_TIER, HarvestTier.IRON)
                .stat(PartTypes.MAIN, GearProperties.HARVEST_SPEED, 6)
                .stat(PartTypes.MAIN, GearProperties.ATTACK_DAMAGE, 4f)
                .stat(PartTypes.MAIN, GearProperties.MAGIC_DAMAGE, 1f)
                .stat(PartTypes.MAIN, GearProperties.ATTACK_SPEED, -0.2f)
                .mainStatsArmor(3, 8, 6, 3, 2, 6) //20
                .stat(PartTypes.MAIN, GearProperties.RANGED_DAMAGE, 2f)
                .stat(PartTypes.MAIN, GearProperties.DRAW_SPEED, -0.2f)
                .stat(PartTypes.MAIN, GearProperties.RARITY, 40)
                .stat(PartTypes.MAIN, GearProperties.CHARGING_VALUE, 0.8f)
                .stat(PartTypes.ROD, GearProperties.DURABILITY, 0.25f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .stat(PartTypes.ROD, GearProperties.RARITY, 40)
                .trait(PartTypes.MAIN, Const.Traits.MALLEABLE, 5)
                .trait(PartTypes.ROD, Const.Traits.MALLEABLE, 5)
        );
        // Tin
        ret.add(extraMetal(ExtraMetals.TIN, 1)
                .displayWithDefaultName(0x89A5B4, TextureType.HIGH_CONTRAST)
                .stat(PartTypes.MAIN, GearProperties.DURABILITY, 192)
                .stat(PartTypes.MAIN, GearProperties.ARMOR_DURABILITY, 13)
                .stat(PartTypes.MAIN, GearProperties.ENCHANTMENT_VALUE, 12)
                .stat(PartTypes.MAIN, GearProperties.HARVEST_TIER, HarvestTier.STONE)
                .stat(PartTypes.MAIN, GearProperties.HARVEST_SPEED, 5)
                .stat(PartTypes.MAIN, GearProperties.ATTACK_DAMAGE, 1.5f)
                .stat(PartTypes.MAIN, GearProperties.MAGIC_DAMAGE, 1f)
                .stat(PartTypes.MAIN, GearProperties.ATTACK_SPEED, 0.0f)
                .mainStatsArmor(2, 5, 3, 2, 0, 2) //12
                .stat(PartTypes.MAIN, GearProperties.RANGED_DAMAGE, 0.5f)
                .stat(PartTypes.MAIN, GearProperties.DRAW_SPEED, 0f)
                .stat(PartTypes.MAIN, GearProperties.RARITY, 15)
                .stat(PartTypes.MAIN, GearProperties.CHARGING_VALUE, 1.1f)
                .stat(PartTypes.ROD, GearProperties.ATTACK_SPEED, 0.2f, NumberProperty.Operation.ADD)
                .stat(PartTypes.ROD, GearProperties.RARITY, 15)
                .trait(PartTypes.MAIN, Const.Traits.MALLEABLE, 1)
                .trait(PartTypes.MAIN, Const.Traits.SOFT, 2)
                .trait(PartTypes.ROD, Const.Traits.MALLEABLE, 2)
                .trait(PartTypes.ROD, Const.Traits.SOFT, 1)
        );
        // Titanium
        ret.add(extraMetal(ExtraMetals.TITANIUM, 4)
                .displayWithDefaultName(0x2E4CE6, TextureType.HIGH_CONTRAST)
                .stat(PartTypes.MAIN, GearProperties.DURABILITY, 1600)
                .stat(PartTypes.MAIN, GearProperties.ARMOR_DURABILITY, 37)
                .stat(PartTypes.MAIN, GearProperties.ENCHANTMENT_VALUE, 12)
                .stat(PartTypes.MAIN, GearProperties.HARVEST_TIER, HarvestTier.NETHERITE)
                .stat(PartTypes.MAIN, GearProperties.HARVEST_SPEED, 8)
                .stat(PartTypes.MAIN, GearProperties.ATTACK_DAMAGE, 6f)
                .stat(PartTypes.MAIN, GearProperties.MAGIC_DAMAGE, 1f)
                .stat(PartTypes.MAIN, GearProperties.ATTACK_SPEED, 0.0f)
                .mainStatsArmor(4, 9, 7, 4, 8, 4) //24
                .stat(PartTypes.MAIN, GearProperties.RANGED_DAMAGE, 1f)
                .stat(PartTypes.MAIN, GearProperties.DRAW_SPEED, -0.2f)
                .stat(PartTypes.MAIN, GearProperties.RARITY, 80)
                .stat(PartTypes.MAIN, GearProperties.CHARGING_VALUE, 1.0f)
                .stat(PartTypes.ROD, GearProperties.DURABILITY, 0.1f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .stat(PartTypes.ROD, GearProperties.ATTACK_DAMAGE, 0.2f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .stat(PartTypes.ROD, GearProperties.ATTACK_DAMAGE, 2, NumberProperty.Operation.ADD)
                .stat(PartTypes.ROD, GearProperties.RARITY, 80)
                .trait(PartTypes.MAIN, Const.Traits.MALLEABLE, 2)
                .trait(PartTypes.MAIN, Const.Traits.HARD, 4)
                .trait(PartTypes.ROD, Const.Traits.FLEXIBLE, 2)
                .trait(PartTypes.ROD, Const.Traits.HARD, 4)
        );
        // Uranium
        ret.add(extraMetal(ExtraMetals.URANIUM, 3)
                .displayWithDefaultName(0x21FF0F, TextureType.HIGH_CONTRAST)
                .stat(PartTypes.MAIN, GearProperties.DURABILITY, 800)
                .stat(PartTypes.MAIN, GearProperties.ARMOR_DURABILITY, 20)
                .stat(PartTypes.MAIN, GearProperties.REPAIR_VALUE, -0.15f)
                .stat(PartTypes.MAIN, GearProperties.ENCHANTMENT_VALUE, 17)
                .stat(PartTypes.MAIN, GearProperties.HARVEST_TIER, HarvestTier.DIAMOND)
                .stat(PartTypes.MAIN, GearProperties.HARVEST_SPEED, 6)
                .stat(PartTypes.MAIN, GearProperties.ATTACK_DAMAGE, 2f)
                .stat(PartTypes.MAIN, GearProperties.MAGIC_DAMAGE, 2f)
                .stat(PartTypes.MAIN, GearProperties.ATTACK_SPEED, 0.1f)
                .mainStatsArmor(2, 5, 4, 2, 1, 3) //13
                .stat(PartTypes.MAIN, GearProperties.RANGED_DAMAGE, 0.5f)
                .stat(PartTypes.MAIN, GearProperties.DRAW_SPEED, 0.1f)
                .stat(PartTypes.MAIN, GearProperties.RARITY, 50)
                .stat(PartTypes.MAIN, GearProperties.CHARGING_VALUE, 1.5f)
                .stat(PartTypes.ROD, GearProperties.HARVEST_SPEED, 0.1f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .stat(PartTypes.ROD, GearProperties.DRAW_SPEED, 0.1f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .stat(PartTypes.ROD, GearProperties.RARITY, 40)
                .trait(PartTypes.MAIN, Const.Traits.MALLEABLE, 2)
                .trait(PartTypes.ROD, Const.Traits.MALLEABLE, 2)
        );
        // Zinc
        ret.add(extraMetal(ExtraMetals.ZINC, 1)
                .displayWithDefaultName(0xC9D3CE, TextureType.HIGH_CONTRAST)
                .stat(PartTypes.MAIN, GearProperties.DURABILITY, 192)
                .stat(PartTypes.MAIN, GearProperties.ARMOR_DURABILITY, 10)
                .stat(PartTypes.MAIN, GearProperties.ENCHANTMENT_VALUE, 15)
                .stat(PartTypes.MAIN, GearProperties.HARVEST_TIER, HarvestTier.STONE)
                .stat(PartTypes.MAIN, GearProperties.HARVEST_SPEED, 3)
                .stat(PartTypes.MAIN, GearProperties.ATTACK_DAMAGE, 1.5f)
                .stat(PartTypes.MAIN, GearProperties.MAGIC_DAMAGE, 1f)
                .stat(PartTypes.MAIN, GearProperties.ATTACK_SPEED, 0.0f)
                .mainStatsArmor(1, 5, 3, 1, 0, 2) //11
                .stat(PartTypes.MAIN, GearProperties.RANGED_DAMAGE, 0f)
                .stat(PartTypes.MAIN, GearProperties.DRAW_SPEED, 0.0f)
                .stat(PartTypes.MAIN, GearProperties.RARITY, 10)
                .stat(PartTypes.MAIN, GearProperties.CHARGING_VALUE, 1.1f)
                .stat(PartTypes.ROD, GearProperties.DURABILITY, -0.05f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .stat(PartTypes.ROD, GearProperties.ATTACK_DAMAGE, 0.05f, NumberProperty.Operation.MULTIPLY_TOTAL)
                .stat(PartTypes.ROD, GearProperties.RARITY, 15)
                .trait(PartTypes.MAIN, Const.Traits.MALLEABLE, 1)
                .trait(PartTypes.MAIN, Const.Traits.SOFT, 2)
                .trait(PartTypes.ROD, Const.Traits.MALLEABLE, 2)
                .trait(PartTypes.ROD, Const.Traits.SOFT, 3)
        );
    }

    //endregion

    @SuppressWarnings("WeakerAccess")
    protected MaterialBuilder<CompoundMaterial> compoundBuilder(DataResource<Material> material, ItemLike item) {
        return new MaterialBuilder<>(material.getId(), (parent, crafting, display, __) -> new CompoundMaterial(parent, crafting, display))
                .crafting(item);
    }

    @SuppressWarnings("WeakerAccess")
    protected MaterialBuilder<CustomCompoundMaterial> customCompoundBuilder(DataResource<Material> material, CustomMaterialItem item, IMaterialCategory... categories) {
        return MaterialBuilder.customCompound(material)
                .crafting(new Ingredient(CustomAlloyIngredient.of(item, material)), categories);
    }

    private MaterialBuilder<SimpleMaterial> extraMetal(ExtraMetals metal, int tier) {
        var name = metal.getName();
        var tierCategory = List.of(MaterialCategories.BASIC, MaterialCategories.BASIC, MaterialCategories.INTERMEDIATE, MaterialCategories.ADVANCED, MaterialCategories.ENDGAME)
                .get(tier);
        TagKey<Item> materialTag = metal.getMainTag();
        TagKey<Item> rodTag = metal.getRodTag();
        return MaterialBuilder.simple(DataResource.material(SilentGear.getId(name)))
                .crafting(new MaterialCraftingData(
                        OptionalTagIngredient.create(this.items, materialTag),
                        List.of(MaterialCategories.METAL, tierCategory),
                        List.of(),
                        Map.of(PartTypes.ROD.get(), OptionalTagIngredient.create(this.items, rodTag)),
                        true
                ));
    }

    private static MaterialBuilder<SimpleMaterial> terracotta(DataResource<Material> parent, String suffix, ItemLike item, int color) {
        var id = Identifier.fromNamespaceAndPath(parent.getId().getNamespace(), parent.getId().getPath() + "/" + suffix);
        return MaterialBuilder.simple(DataResource.material(id))
                .parent(parent)
                .crafting(item)
                .displayWithDefaultName(color, TextureType.LOW_CONTRAST);
    }

    private static MaterialBuilder<SimpleMaterial> wood(String suffix, ItemLike item, int color) {
        var parent = Const.Materials.WOOD;
        var id = Identifier.fromNamespaceAndPath(parent.getId().getNamespace(), parent.getId().getPath() + "/" + suffix);
        return MaterialBuilder.simple(DataResource.material(id))
                .parent(parent)
                .crafting(item)
                .displayWithDefaultName(color, TextureType.LOW_CONTRAST);
    }

    private static MaterialBuilder<SimpleMaterial> wool(String suffix, ItemLike item, int color) {
        var parent = Const.Materials.WOOL;
        var id = Identifier.fromNamespaceAndPath(parent.getId().getNamespace(), parent.getId().getPath() + "/" + suffix);
        return MaterialBuilder.simple(DataResource.material(id))
                .parent(parent)
                .crafting(item)
                .displayWithDefaultName(color, TextureType.LOW_CONTRAST);
    }

    @SuppressWarnings({"WeakerAccess", "SameParameterValue"})
    protected static ITraitCondition materialCountOrRatio(int count, float ratio) {
        return new OrTraitCondition(new MaterialCountTraitCondition(count), new MaterialRatioTraitCondition(ratio));
    }

    private Ingredient taggedItems(TagKey<Item> tag) {
        return Ingredient.of(this.items.getOrThrow(tag));
    }
}
