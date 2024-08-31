package net.silentchaos512.gear.data.recipes;

import com.google.common.collect.ImmutableMap;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.ImpossibleTrigger;
import net.minecraft.core.Holder;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.item.GearItem;
import net.silentchaos512.gear.api.material.Material;
import net.silentchaos512.gear.api.part.GearPart;
import net.silentchaos512.gear.api.part.MaterialGrade;
import net.silentchaos512.gear.api.util.DataResource;
import net.silentchaos512.gear.crafting.ingredient.BlueprintIngredient;
import net.silentchaos512.gear.crafting.ingredient.GearPartIngredient;
import net.silentchaos512.gear.crafting.ingredient.PartMaterialIngredient;
import net.silentchaos512.gear.crafting.recipe.*;
import net.silentchaos512.gear.crafting.recipe.press.MaterialPressingRecipe;
import net.silentchaos512.gear.gear.material.MaterialCategories;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.gear.part.PartInstance;
import net.silentchaos512.gear.item.CraftingItems;
import net.silentchaos512.gear.item.GearItemSet;
import net.silentchaos512.gear.item.MainPartItem;
import net.silentchaos512.gear.item.RepairKitItem;
import net.silentchaos512.gear.item.gear.GearArmorItem;
import net.silentchaos512.gear.setup.*;
import net.silentchaos512.gear.setup.gear.GearTypes;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.gear.util.Const;
import net.silentchaos512.lib.data.recipe.ExtendedShapedRecipeBuilder;
import net.silentchaos512.lib.data.recipe.ExtendedShapelessRecipeBuilder;
import net.silentchaos512.lib.data.recipe.LibRecipeProvider;
import net.silentchaos512.lib.util.NameUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ModRecipesProvider extends LibRecipeProvider {
    private static final boolean ADD_TEST_RECIPES = true;

    public ModRecipesProvider(GatherDataEvent event) {
        super(event.getGenerator().getPackOutput(), event.getLookupProvider(), SilentGear.MOD_ID);
    }

    @Override
    protected void buildRecipes(RecipeOutput consumer) {
        metals(consumer, 0.5f, new Metals("blaze_gold", CraftingItems.BLAZE_GOLD_INGOT, SgTags.Items.INGOTS_BLAZE_GOLD)
                .block(SgBlocks.BLAZE_GOLD_BLOCK, SgTags.Items.STORAGE_BLOCKS_BLAZE_GOLD)
                .dust(CraftingItems.BLAZE_GOLD_DUST, SgTags.Items.DUSTS_BLAZE_GOLD)
                .nugget(CraftingItems.BLAZE_GOLD_NUGGET, SgTags.Items.NUGGETS_BLAZE_GOLD));
        metals(consumer, 1.0f, new Metals("crimson_iron", CraftingItems.CRIMSON_IRON_INGOT, SgTags.Items.INGOTS_CRIMSON_IRON)
                .block(SgBlocks.CRIMSON_IRON_BLOCK, SgTags.Items.STORAGE_BLOCKS_CRIMSON_IRON)
                .dust(CraftingItems.CRIMSON_IRON_DUST, SgTags.Items.DUSTS_CRIMSON_IRON)
                .ore(SgBlocks.CRIMSON_IRON_ORE, SgTags.Items.ORES_CRIMSON_IRON, CraftingItems.RAW_CRIMSON_IRON, SgBlocks.RAW_CRIMSON_IRON_BLOCK)
                .nugget(CraftingItems.CRIMSON_IRON_NUGGET, SgTags.Items.NUGGETS_CRIMSON_IRON));
        metals(consumer, 0.5f, new Metals("crimson_steel", CraftingItems.CRIMSON_STEEL_INGOT, SgTags.Items.INGOTS_CRIMSON_STEEL)
                .block(SgBlocks.CRIMSON_STEEL_BLOCK, SgTags.Items.STORAGE_BLOCKS_CRIMSON_STEEL)
                .dust(CraftingItems.CRIMSON_STEEL_DUST, SgTags.Items.DUSTS_CRIMSON_STEEL)
                .nugget(CraftingItems.CRIMSON_STEEL_NUGGET, SgTags.Items.NUGGETS_CRIMSON_STEEL));
        metals(consumer, 1.5f, new Metals("azure_silver", CraftingItems.AZURE_SILVER_INGOT, SgTags.Items.INGOTS_AZURE_SILVER)
                .block(SgBlocks.AZURE_SILVER_BLOCK, SgTags.Items.STORAGE_BLOCKS_AZURE_SILVER)
                .dust(CraftingItems.AZURE_SILVER_DUST, SgTags.Items.DUSTS_AZURE_SILVER)
                .ore(SgBlocks.AZURE_SILVER_ORE, SgTags.Items.ORES_AZURE_SILVER, CraftingItems.RAW_AZURE_SILVER, SgBlocks.RAW_AZURE_SILVER_BLOCK)
                .nugget(CraftingItems.AZURE_SILVER_NUGGET, SgTags.Items.NUGGETS_AZURE_SILVER));
        metals(consumer, 0.5f, new Metals("azure_electrum", CraftingItems.AZURE_ELECTRUM_INGOT, SgTags.Items.INGOTS_AZURE_ELECTRUM)
                .block(SgBlocks.AZURE_ELECTRUM_BLOCK, SgTags.Items.STORAGE_BLOCKS_AZURE_ELECTRUM)
                .dust(CraftingItems.AZURE_ELECTRUM_DUST, SgTags.Items.DUSTS_AZURE_ELECTRUM)
                .nugget(CraftingItems.AZURE_ELECTRUM_NUGGET, SgTags.Items.NUGGETS_AZURE_ELECTRUM));
        metals(consumer, 0.75f, new Metals("tyrian_steel", CraftingItems.TYRIAN_STEEL_INGOT, SgTags.Items.INGOTS_TYRIAN_STEEL)
                .block(SgBlocks.TYRIAN_STEEL_BLOCK, SgTags.Items.STORAGE_BLOCKS_TYRIAN_STEEL)
                .dust(CraftingItems.TYRIAN_STEEL_DUST, SgTags.Items.DUSTS_TYRIAN_STEEL)
                .nugget(CraftingItems.TYRIAN_STEEL_NUGGET, SgTags.Items.NUGGETS_TYRIAN_STEEL));
        metals(consumer, 1.0f, new Metals("bort", CraftingItems.BORT, SgTags.Items.GEMS_BORT)
                .block(SgBlocks.BORT_BLOCK, SgTags.Items.STORAGE_BLOCKS_BORT));

        registerSpecialRecipes(consumer);
        registerCraftingItems(consumer);
        registerBlueprints(consumer);
        registerCompoundParts(consumer);
        registerGear(consumer);
        registerModifierKits(consumer);
        registerMachines(consumer);
        registerCompounding(consumer);
        registerPressing(consumer);
        registerSmithing(consumer);
        registerSalvaging(consumer);

        if (ADD_TEST_RECIPES) {
            registerTestRecipes(consumer);
        }
    }

    private void registerTestRecipes(RecipeOutput consumer) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.BUCKET)
                .pattern("# #")
                .pattern(" # ")
                .define('#', new Ingredient(PartMaterialIngredient.builder(PartTypes.MAIN.get())
                        .withMaterial(DataResource.material("copper"))
                        .withGrade(MaterialGrade.A, null).build()
                ))
                .unlockedBy("impossible", CriteriaTriggers.IMPOSSIBLE.createCriterion(new ImpossibleTrigger.TriggerInstance()))
                .save(consumer, modId("graded_mat_test"));
    }

    private void registerSpecialRecipes(RecipeOutput consumer) {
        special(consumer, SgRecipes.FILL_REPAIR_KIT.get(), FillRepairKitRecipe::new);
        special(consumer, SgRecipes.SWAP_GEAR_PART.get(), GearPartSwapRecipe::new);
        special(consumer, SgRecipes.QUICK_REPAIR.get(), QuickRepairRecipe::new);
        special(consumer, SgRecipes.MOD_KIT_REMOVE_PART.get(), ModKitRemovePartRecipe::new);
    }

    private void registerBlueprints(RecipeOutput consumer) {
        toolBlueprint(consumer, "sword", GearItemSets.SWORD, "#", "#", "/");
        toolBlueprint(consumer, "katana", GearItemSets.KATANA, "##", "# ", "/ ");
        toolBlueprint(consumer, "machete", GearItemSets.MACHETE, "  #", " ##", "/  ");
        toolBlueprint(consumer, "spear", GearItemSets.SPEAR, "#  ", " / ", "  /");
        toolBlueprint(consumer, "knife", GearItemSets.KNIFE, " #", "/ ");
        toolBlueprint(consumer, "dagger", GearItemSets.DAGGER, "#", "/");
        toolBlueprint(consumer, "pickaxe", GearItemSets.PICKAXE, "###", " / ", " / ");
        toolBlueprint(consumer, "shovel", GearItemSets.SHOVEL, "#", "/", "/");
        toolBlueprint(consumer, "axe", GearItemSets.AXE, "##", "#/", " /");
        toolBlueprint(consumer, "paxel", GearItemSets.PAXEL, "###", "#/#", " /#");
        toolBlueprint(consumer, "hammer", GearItemSets.HAMMER, "###", "###", " / ");
        toolBlueprint(consumer, "excavator", GearItemSets.EXCAVATOR, "# #", "###", " / ");
        toolBlueprint(consumer, "saw", GearItemSets.SAW, "###", "##/", "  /");
        toolBlueprint(consumer, "hoe", GearItemSets.HOE, "##", " /", " /");
        toolBlueprint(consumer, "mattock", GearItemSets.MATTOCK, "## ", "#/#", " / ");
        toolBlueprint(consumer, "prospector_hammer", GearItemSets.PROSPECTOR_HAMMER,
                Ingredient.of(Tags.Items.INGOTS_IRON), "##", " /", " @");
        toolBlueprint(consumer, "sickle", GearItemSets.SICKLE, " #", "##", "/ ");
        toolBlueprint(consumer, "shears", GearItemSets.SHEARS, " #", "#/");
        toolBlueprint(consumer, "fishing_rod", GearItemSets.FISHING_ROD, "  /", " /#", "/ #");
        toolBlueprint(consumer, "bow", GearItemSets.BOW, " #/", "# /", " #/");
        toolBlueprint(consumer, "crossbow", GearItemSets.CROSSBOW, "/#/", "###", " / ");
        toolBlueprint(consumer, "slingshot", GearItemSets.SLINGSHOT, "# #", " / ", " / ");
        toolBlueprint(consumer, "shield", GearItemSets.SHIELD, "# #", "///", " # ");
        toolBlueprint(consumer, "arrow", GearItemSets.ARROW, Ingredient.of(Tags.Items.FEATHERS), "#", "/", "@");
        armorBlueprint(consumer, "helmet", GearItemSets.HELMET, "###", "# #");
        armorBlueprint(consumer, "chestplate", GearItemSets.CHESTPLATE, "# #", "###", "###");
        armorBlueprint(consumer, "leggings", GearItemSets.LEGGINGS, "###", "# #", "# #");
        armorBlueprint(consumer, "boots", GearItemSets.BOOTS, "# #", "# #");

        shaped(RecipeCategory.MISC, GearItemSets.TRIDENT.blueprint())
                .define('#', SgTags.Items.BLUEPRINT_PAPER)
                .define('H', Items.HEART_OF_THE_SEA)
                .define('T', Items.TRIDENT)
                .pattern("#H#")
                .pattern("#T#")
                .pattern(" # ")
                .unlockedBy("has_item", has(Items.TRIDENT))
                .save(consumer);

        shaped(RecipeCategory.MISC, GearItemSets.ELYTRA.blueprint())
                .define('#', SgTags.Items.BLUEPRINT_PAPER)
                .define('/', Tags.Items.INGOTS_GOLD)
                .define('e', Items.ELYTRA)
                .define('p', Items.PHANTOM_MEMBRANE)
                .pattern("/e/")
                .pattern("p#p")
                .pattern("p p")
                .unlockedBy("has_item", has(SgTags.Items.BLUEPRINT_PAPER))
                .save(consumer);
        shaped(RecipeCategory.MISC, GearItemSets.ELYTRA.template())
                .define('#', SgTags.Items.TEMPLATE_BOARDS)
                .define('/', Tags.Items.INGOTS_GOLD)
                .define('e', Items.ELYTRA)
                .define('p', Items.PHANTOM_MEMBRANE)
                .pattern("/e/")
                .pattern("p#p")
                .pattern("p p")
                .unlockedBy("has_item", has(SgTags.Items.TEMPLATE_BOARDS))
                .save(consumer);

        // Curio blueprints
        shaped(RecipeCategory.MISC, GearItemSets.RING.blueprint())
                .group("silentgear:blueprints/ring")
                .define('#', SgTags.Items.BLUEPRINT_PAPER)
                .define('/', PartMaterialIngredient.of(PartTypes.MAIN.get(), GearTypes.CURIO.get(), MaterialCategories.METAL))
                .pattern(" #/")
                .pattern("# #")
                .pattern("/# ")
                .unlockedBy("has_item", has(SgTags.Items.BLUEPRINT_PAPER))
                .save(consumer);
        shaped(RecipeCategory.MISC, GearItemSets.RING.blueprint())
                .group("silentgear:blueprints/ring")
                .define('#', SgTags.Items.BLUEPRINT_PAPER)
                .define('/', Tags.Items.INGOTS_GOLD)
                .pattern(" #/")
                .pattern("# #")
                .pattern("/# ")
                .unlockedBy("has_item", has(SgTags.Items.BLUEPRINT_PAPER))
                .save(consumer, SilentGear.getId("ring_blueprint_alt"));
        shaped(RecipeCategory.MISC, GearItemSets.RING.template())
                .group("silentgear:blueprints/ring")
                .define('#', SgTags.Items.TEMPLATE_BOARDS)
                .define('/', PartMaterialIngredient.of(PartTypes.MAIN.get(), GearTypes.CURIO.get(), MaterialCategories.METAL))
                .pattern(" #/")
                .pattern("# #")
                .pattern("/# ")
                .unlockedBy("has_item", has(SgTags.Items.TEMPLATE_BOARDS))
                .save(consumer);

        shaped(RecipeCategory.MISC, GearItemSets.BRACELET.blueprint())
                .group("silentgear:blueprints/bracelet")
                .define('#', SgTags.Items.BLUEPRINT_PAPER)
                .define('/', PartMaterialIngredient.of(PartTypes.MAIN.get(), GearTypes.CURIO.get(), MaterialCategories.METAL))
                .pattern("###")
                .pattern("# #")
                .pattern("/#/")
                .unlockedBy("has_item", has(SgTags.Items.BLUEPRINT_PAPER))
                .save(consumer);
        shaped(RecipeCategory.MISC, GearItemSets.BRACELET.blueprint())
                .group("silentgear:blueprints/bracelet")
                .define('#', SgTags.Items.BLUEPRINT_PAPER)
                .define('/', Tags.Items.INGOTS_GOLD)
                .pattern("###")
                .pattern("# #")
                .pattern("/#/")
                .unlockedBy("has_item", has(SgTags.Items.BLUEPRINT_PAPER))
                .save(consumer, SilentGear.getId("bracelet_blueprint_alt"));
        shaped(RecipeCategory.MISC, GearItemSets.BRACELET.template())
                .group("silentgear:blueprints/bracelet")
                .define('#', SgTags.Items.TEMPLATE_BOARDS)
                .define('/', PartMaterialIngredient.of(PartTypes.MAIN.get(), GearTypes.CURIO.get(), MaterialCategories.METAL))
                .pattern("###")
                .pattern("# #")
                .pattern("/#/")
                .unlockedBy("has_item", has(SgTags.Items.TEMPLATE_BOARDS))
                .save(consumer);

        // Part blueprints
        shaped(RecipeCategory.MISC, SgItems.JEWELER_TOOLS)
                .pattern("  p")
                .pattern("d#s")
                .pattern("ips")
                .define('p', ItemTags.PLANKS)
                .define('d', Tags.Items.GEMS_DIAMOND)
                .define('#', SgTags.Items.BLUEPRINT_PAPER)
                .define('s', Tags.Items.RODS_WOODEN)
                .define('i', Tags.Items.INGOTS_IRON)
                .unlockedBy("has_item", has(SgTags.Items.BLUEPRINT_PAPER))
                .save(consumer);

        shapeless(RecipeCategory.MISC, SgItems.BINDING_BLUEPRINT)
                .group("silentgear:blueprints/binding")
                .requires(Ingredient.of(SgTags.Items.BLUEPRINT_PAPER), 1)
                .requires(PartMaterialIngredient.of(PartTypes.BINDING.get(), GearTypes.TOOL.get()), 2)
                .unlockedBy("has_item", has(SgTags.Items.BLUEPRINT_PAPER))
                .save(consumer);
        shapeless(RecipeCategory.MISC, SgItems.BINDING_BLUEPRINT)
                .group("silentgear:blueprints/binding")
                .requires(SgTags.Items.BLUEPRINT_PAPER)
                .requires(Tags.Items.STRINGS)
                .unlockedBy("has_item", has(SgTags.Items.BLUEPRINT_PAPER))
                .save(consumer, SilentGear.getId("binding_blueprint_alt"));
        shapeless(RecipeCategory.MISC, SgItems.BINDING_TEMPLATE)
                .group("silentgear:blueprints/binding")
                .requires(Ingredient.of(SgTags.Items.TEMPLATE_BOARDS), 1)
                .requires(PartMaterialIngredient.of(PartTypes.BINDING.get(), GearTypes.TOOL.get()), 2)
                .unlockedBy("has_item", has(SgTags.Items.TEMPLATE_BOARDS))
                .save(consumer);

        shaped(RecipeCategory.TOOLS, SgItems.CORD_BLUEPRINT)
                .group("silentgear:blueprints/cord")
                .define('#', SgTags.Items.BLUEPRINT_PAPER)
                .define('/', PartMaterialIngredient.of(PartTypes.CORD.get(), GearTypes.TOOL.get()))
                .pattern("#/")
                .pattern("#/")
                .pattern("#/")
                .unlockedBy("has_item", has(SgTags.Items.BLUEPRINT_PAPER))
                .save(consumer);
        shaped(RecipeCategory.TOOLS, SgItems.CORD_BLUEPRINT)
                .group("silentgear:blueprints/cord")
                .define('#', SgTags.Items.BLUEPRINT_PAPER)
                .define('/', Tags.Items.STRINGS)
                .pattern("#/")
                .pattern("#/")
                .pattern("#/")
                .unlockedBy("has_item", has(SgTags.Items.BLUEPRINT_PAPER))
                .save(consumer, SilentGear.getId("cord_blueprint_alt"));
        shaped(RecipeCategory.TOOLS, SgItems.CORD_TEMPLATE)
                .group("silentgear:blueprints/cord")
                .define('#', SgTags.Items.TEMPLATE_BOARDS)
                .define('/', PartMaterialIngredient.of(PartTypes.CORD.get(), GearTypes.TOOL.get()))
                .pattern("#/")
                .pattern("#/")
                .pattern("#/")
                .unlockedBy("has_item", has(SgTags.Items.TEMPLATE_BOARDS))
                .save(consumer);

        shapeless(RecipeCategory.TOOLS, SgItems.FLETCHING_BLUEPRINT)
                .group("silentgear:blueprints/fletching")
                .requires(Ingredient.of(SgTags.Items.BLUEPRINT_PAPER), 2)
                .requires(Tags.Items.FEATHERS)
                .unlockedBy("has_item", has(SgTags.Items.BLUEPRINT_PAPER))
                .save(consumer);
        shapeless(RecipeCategory.TOOLS, SgItems.FLETCHING_TEMPLATE)
                .group("silentgear:blueprints/fletching")
                .requires(Ingredient.of(SgTags.Items.TEMPLATE_BOARDS), 2)
                .requires(Tags.Items.FEATHERS)
                .unlockedBy("has_item", has(SgTags.Items.TEMPLATE_BOARDS))
                .save(consumer);

        shapeless(RecipeCategory.TOOLS, SgItems.GRIP_BLUEPRINT)
                .group("silentgear:blueprints/grip")
                .requires(Ingredient.of(SgTags.Items.BLUEPRINT_PAPER), 2)
                .requires(PartMaterialIngredient.of(PartTypes.GRIP.get(), GearTypes.TOOL.get()))
                .unlockedBy("has_item", has(SgTags.Items.BLUEPRINT_PAPER))
                .save(consumer);
        shapeless(RecipeCategory.TOOLS, SgItems.GRIP_BLUEPRINT)
                .group("silentgear:blueprints/grip")
                .requires(Ingredient.of(SgTags.Items.BLUEPRINT_PAPER), 2)
                .requires(ItemTags.WOOL)
                .unlockedBy("has_item", has(SgTags.Items.BLUEPRINT_PAPER))
                .save(consumer, SilentGear.getId("grip_blueprint_alt"));
        shapeless(RecipeCategory.TOOLS, SgItems.GRIP_TEMPLATE)
                .group("silentgear:blueprints/grip")
                .requires(Ingredient.of(SgTags.Items.TEMPLATE_BOARDS), 2)
                .requires(PartMaterialIngredient.of(PartTypes.GRIP.get(), GearTypes.TOOL.get()))
                .unlockedBy("has_item", has(SgTags.Items.TEMPLATE_BOARDS))
                .save(consumer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, SgItems.LINING_BLUEPRINT)
                .group("silentgear:blueprints/lining")
                .requires(Ingredient.of(SgTags.Items.BLUEPRINT_PAPER), 3)
                .requires(Ingredient.of(ItemTags.WOOL), 2)
                .requires(Ingredient.of(Tags.Items.STRINGS), 2)
                .unlockedBy("has_item", has(SgTags.Items.BLUEPRINT_PAPER))
                .save(consumer);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, SgItems.LINING_TEMPLATE)
                .group("silentgear:blueprints/lining")
                .requires(Ingredient.of(SgTags.Items.TEMPLATE_BOARDS), 3)
                .requires(Ingredient.of(ItemTags.WOOL), 2)
                .requires(Ingredient.of(Tags.Items.STRINGS), 2)
                .unlockedBy("has_item", has(SgTags.Items.TEMPLATE_BOARDS))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, SgItems.ROD_BLUEPRINT)
                .group("silentgear:blueprints/rod")
                .define('#', SgTags.Items.BLUEPRINT_PAPER)
                .define('/', Tags.Items.RODS_WOODEN)
                .pattern("#/")
                .pattern("#/")
                .unlockedBy("has_item", has(SgTags.Items.BLUEPRINT_PAPER))
                .save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, SgItems.ROD_TEMPLATE)
                .group("silentgear:blueprints/rod")
                .define('#', SgTags.Items.TEMPLATE_BOARDS)
                .define('/', Tags.Items.RODS_WOODEN)
                .pattern("#/")
                .pattern("#/")
                .unlockedBy("has_item", has(SgTags.Items.TEMPLATE_BOARDS))
                .save(consumer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, SgItems.TIP_BLUEPRINT)
                .group("silentgear:blueprints/tip")
                .requires(Ingredient.of(SgTags.Items.BLUEPRINT_PAPER), 2)
                .requires(SgTags.Items.PAPER)
                .requires(Tags.Items.STONES)
                .unlockedBy("has_item", has(SgTags.Items.BLUEPRINT_PAPER))
                .save(consumer);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, SgItems.TIP_TEMPLATE)
                .group("silentgear:blueprints/tip")
                .requires(Ingredient.of(SgTags.Items.TEMPLATE_BOARDS), 2)
                .requires(SgTags.Items.PAPER)
                .requires(Tags.Items.STONES)
                .unlockedBy("has_item", has(SgTags.Items.TEMPLATE_BOARDS))
                .save(consumer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, SgItems.COATING_BLUEPRINT)
                .group("silentgear:blueprints/coating")
                .requires(Ingredient.of(SgTags.Items.BLUEPRINT_PAPER), 4)
                .requires(Tags.Items.GEMS_DIAMOND)
                .requires(Tags.Items.GEMS_EMERALD)
                .unlockedBy("has_item", has(SgTags.Items.BLUEPRINT_PAPER))
                .save(consumer);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, SgItems.COATING_TEMPLATE)
                .group("silentgear:blueprints/coating")
                .requires(Ingredient.of(SgTags.Items.TEMPLATE_BOARDS), 4)
                .requires(Tags.Items.GEMS_DIAMOND)
                .requires(Tags.Items.GEMS_EMERALD)
                .unlockedBy("has_item", has(SgTags.Items.TEMPLATE_BOARDS))
                .save(consumer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, SgItems.BLUEPRINT_BOOK)
                .requires(Items.BOOK)
                .requires(ItemTags.WOOL)
                .requires(Tags.Items.INGOTS_GOLD)
                .requires(Ingredient.of(SgTags.Items.TEMPLATE_BOARDS), 3)
                .unlockedBy("has_item", has(SgTags.Items.TEMPLATE_BOARDS))
                .save(consumer);
    }

    private ExtendedShapelessRecipeBuilder.Basic<ShapelessCompoundPartRecipe> compoundPart(DeferredItem<?> item, int count) {
        return compoundPart(RecipeCategory.MISC, item, count);
    }

    private ExtendedShapelessRecipeBuilder.Basic<ShapelessCompoundPartRecipe> compoundPart(RecipeCategory category, MainPartItem mainPartItem, int count) {
        var resultStack = new ItemStack(mainPartItem, count);
        return new ExtendedShapelessRecipeBuilder.Basic<>(category, resultStack, ShapelessCompoundPartRecipe::new);
    }

    private ExtendedShapelessRecipeBuilder.Basic<ShapelessCompoundPartRecipe> compoundPart(RecipeCategory category, DeferredItem<?> item, int count) {
        return new ExtendedShapelessRecipeBuilder.Basic<>(category, item.toStack(count), ShapelessCompoundPartRecipe::new);
    }

    private void registerCompoundParts(RecipeOutput consumer) {
        compoundPart(SgItems.SETTING, 1)
                .requires(BlueprintIngredient.of(SgItems.JEWELER_TOOLS.get()))
                .requires(CraftingItems.BORT)
                .requires(PartMaterialIngredient.of(PartTypes.SETTING.get()))
                .save(consumer, SilentGear.getId("part/adornment"));

        compoundPart(SgItems.ROD, 4)
                .requires(BlueprintIngredient.of(SgItems.ROD_BLUEPRINT.get()))
                .requires(PartMaterialIngredient.of(PartTypes.ROD.get()), 2)
                .save(consumer, SilentGear.getId("part/rod"));

        compoundPart(SgItems.BINDING, 1)
                .requires(BlueprintIngredient.of(SgItems.BINDING_BLUEPRINT.get()))
                .requires(PartMaterialIngredient.of(PartTypes.BINDING.get()))
                .save(consumer, SilentGear.getId("part/binding"));

        compoundPart(SgItems.BINDING, 2)
                .requires(BlueprintIngredient.of(SgItems.BINDING_BLUEPRINT.get()))
                .requires(PartMaterialIngredient.of(PartTypes.BINDING.get()), 2)
                .save(consumer, SilentGear.getId("part/binding2"));

        compoundPart(SgItems.CORD, 1)
                .requires(BlueprintIngredient.of(SgItems.CORD_BLUEPRINT.get()))
                .requires(PartMaterialIngredient.of(PartTypes.CORD.get()), 3)
                .save(consumer, SilentGear.getId("part/cord"));

        compoundPart(SgItems.FLETCHING, 1)
                .requires(BlueprintIngredient.of(SgItems.FLETCHING_BLUEPRINT.get()))
                .requires(PartMaterialIngredient.of(PartTypes.FLETCHING.get()), 1)
                .save(consumer, SilentGear.getId("part/fletching"));

        compoundPart(SgItems.GRIP, 1)
                .requires(BlueprintIngredient.of(SgItems.GRIP_BLUEPRINT.get()))
                .requires(PartMaterialIngredient.of(PartTypes.GRIP.get()))
                .save(consumer, SilentGear.getId("part/grip"));

        compoundPart(SgItems.GRIP, 2)
                .requires(BlueprintIngredient.of(SgItems.GRIP_BLUEPRINT.get()))
                .requires(PartMaterialIngredient.of(PartTypes.GRIP.get()), 2)
                .save(consumer, SilentGear.getId("part/grip2"));

        compoundPart(SgItems.LINING, 1)
                .requires(BlueprintIngredient.of(SgItems.LINING_BLUEPRINT.get()))
                .requires(PartMaterialIngredient.of(PartTypes.LINING.get()))
                .save(consumer, SilentGear.getId("part/lining"));

        compoundPart(SgItems.TIP, 1)
                .requires(BlueprintIngredient.of(SgItems.TIP_BLUEPRINT.get()))
                .requires(PartMaterialIngredient.of(PartTypes.TIP.get()))
                .save(consumer, SilentGear.getId("part/tip"));

        compoundPart(SgItems.TIP, 2)
                .requires(BlueprintIngredient.of(SgItems.TIP_BLUEPRINT.get()))
                .requires(PartMaterialIngredient.of(PartTypes.TIP.get()), 2)
                .save(consumer, SilentGear.getId("part/tip2"));

        compoundPart(SgItems.COATING, 1)
                .requires(BlueprintIngredient.of(SgItems.COATING_BLUEPRINT.get()))
                .requires(PartMaterialIngredient.of(PartTypes.COATING.get()))
                .save(consumer, SilentGear.getId("part/coating"));

        compoundPart(SgItems.COATING, 1)
                .requires(BlueprintIngredient.of(SgItems.COATING_BLUEPRINT.get()))
                .requires(Items.GLASS_BOTTLE)
                .requires(PartMaterialIngredient.of(PartTypes.COATING.get()))
                .save(consumer, SilentGear.getId("part/coating_alt"));

        compoundPart(SgItems.COATING, 2)
                .requires(BlueprintIngredient.of(SgItems.COATING_BLUEPRINT.get()))
                .requires(PartMaterialIngredient.of(PartTypes.COATING.get()), 2)
                .save(consumer, SilentGear.getId("part/coating2"));

        compoundPart(SgItems.COATING, 2)
                .requires(BlueprintIngredient.of(SgItems.COATING_BLUEPRINT.get()))
                .requires(Items.GLASS_BOTTLE)
                .requires(PartMaterialIngredient.of(PartTypes.COATING.get()), 2)
                .save(consumer, SilentGear.getId("part/coating2_alt"));
    }

    private ExtendedShapelessRecipeBuilder.Basic<ShapelessGearRecipe> shapelessGear(RecipeCategory category, DeferredItem<?> item) {
        return new ExtendedShapelessRecipeBuilder.Basic<>(category, item.toStack(), ShapelessGearRecipe::new);
    }

    private ExtendedShapelessRecipeBuilder.Basic<ShapelessGearRecipe> shapelessGear(RecipeCategory category, ItemLike item) {
        return new ExtendedShapelessRecipeBuilder.Basic<>(category, new ItemStack(item), ShapelessGearRecipe::new);
    }

    private ExtendedShapedRecipeBuilder.Basic<ShapedGearRecipe> shapedGear(RecipeCategory category, DeferredItem<?> item) {
        return new ExtendedShapedRecipeBuilder.Basic<>(category, item.toStack(), ShapedGearRecipe::new);
    }

    private ExtendedShapedRecipeBuilder.Basic<ShapedGearRecipe> shapedGear(RecipeCategory category, ItemLike item) {
        return new ExtendedShapedRecipeBuilder.Basic<>(category, new ItemStack(item), ShapedGearRecipe::new);
    }

    private ExtendedShapelessRecipeBuilder.Basic<ShapelessCompoundPartRecipe> shapelessPart(RecipeCategory category, ItemLike item) {
        return new ExtendedShapelessRecipeBuilder.Basic<>(category, new ItemStack(item), ShapelessCompoundPartRecipe::new);
    }

    private void registerGear(RecipeOutput consumer) {
        toolRecipes(consumer, "sword", 2, GearItemSets.SWORD);
        toolRecipes(consumer, "katana", 3, GearItemSets.KATANA);
        toolRecipes(consumer, "machete", 3, GearItemSets.MACHETE);
        toolRecipes(consumer, "spear", 1, GearItemSets.SPEAR);
        toolRecipes(consumer, "trident", 3, GearItemSets.TRIDENT);
        toolRecipes(consumer, "mace", 6, GearItemSets.MACE);
        toolRecipes(consumer, "knife", 1, GearItemSets.KNIFE);
        toolRecipes(consumer, "dagger", 1, GearItemSets.DAGGER);
        toolRecipes(consumer, "pickaxe", 3, GearItemSets.PICKAXE);
        toolRecipes(consumer, "shovel", 1, GearItemSets.SHOVEL);
        toolRecipes(consumer, "axe", 3, GearItemSets.AXE);
        toolRecipes(consumer, "paxel", 5, GearItemSets.PAXEL);
        toolRecipes(consumer, "hammer", 6, GearItemSets.HAMMER);
        toolRecipes(consumer, "excavator", 5, GearItemSets.EXCAVATOR);
        toolRecipes(consumer, "hoe", 2, GearItemSets.HOE);
        toolRecipes(consumer, "mattock", 4, GearItemSets.MATTOCK);
        toolRecipes(consumer, "prospector_hammer", 2, GearItemSets.PROSPECTOR_HAMMER);
        toolRecipes(consumer, "saw", 5, GearItemSets.SAW);
        toolRecipes(consumer, "sickle", 3, GearItemSets.SICKLE);
        toolRecipes(consumer, "shears", 2, GearItemSets.SHEARS);
        bowRecipes(consumer, "fishing_rod", 2, GearItemSets.FISHING_ROD);
        bowRecipes(consumer, "bow", 3, GearItemSets.BOW);
        bowRecipes(consumer, "crossbow", 3, GearItemSets.CROSSBOW);
        bowRecipes(consumer, "slingshot", 2, GearItemSets.SLINGSHOT);
        arrowRecipes(consumer, "arrow", GearItemSets.ARROW);
        curioRecipes(consumer, "ring", 2, GearItemSets.RING);
        curioRecipes(consumer, "bracelet", 3, GearItemSets.BRACELET);
        curioRecipes(consumer, "necklace", 3, GearItemSets.NECKLACE);

        shapelessGear(RecipeCategory.COMBAT, GearItemSets.SHIELD.gearItem())
                .requires(BlueprintIngredient.of(GearItemSets.SHIELD))
                .requires(PartMaterialIngredient.of(PartTypes.MAIN.get(), GearTypes.ARMOR.get()), 2)
                .requires(GearPartIngredient.of(PartTypes.ROD.get()))
                .save(consumer, SilentGear.getId("gear/shield"));

        armorRecipes(consumer, 5, GearItemSets.HELMET);
        armorRecipes(consumer, 8, GearItemSets.CHESTPLATE);
        armorRecipes(consumer, 7, GearItemSets.LEGGINGS);
        armorRecipes(consumer, 4, GearItemSets.BOOTS);

        compoundPart(RecipeCategory.COMBAT, GearItemSets.ELYTRA.mainPart(), 1)
                .requires(BlueprintIngredient.of(GearItemSets.ELYTRA))
                .requires(PartMaterialIngredient.of(PartTypes.MAIN.get(),
                        GearTypes.ELYTRA.get(),
                        MaterialCategories.CLOTH,
                        MaterialCategories.SHEET), 6)
                .save(consumer, SilentGear.getId("gear/elytra_wings"));

        shapelessGear(RecipeCategory.COMBAT, GearItemSets.ELYTRA.gearItem())
                .requires(GearItemSets.ELYTRA.mainPart())
                .requires(GearPartIngredient.of(PartTypes.BINDING.get()))
                .save(consumer, SilentGear.getId("gear/elytra"));

        // Rough recipes
        shapedGear(RecipeCategory.COMBAT, GearItemSets.SWORD.gearItem())
                .pattern("#")
                .pattern("#")
                .pattern("/")
                .define('#', PartMaterialIngredient.of(PartTypes.MAIN.get(), GearTypes.TOOL.get()))
                .define('/', SgTags.Items.RODS_ROUGH)
                .save(consumer, SilentGear.getId("gear/rough/sword"));
        shapedGear(RecipeCategory.COMBAT, GearItemSets.DAGGER.gearItem())
                .pattern("#")
                .pattern("/")
                .define('#', PartMaterialIngredient.of(PartTypes.MAIN.get(), GearTypes.TOOL.get()))
                .define('/', SgTags.Items.RODS_ROUGH)
                .save(consumer, SilentGear.getId("gear/rough/dagger"));
        shapedGear(RecipeCategory.COMBAT, GearItemSets.KNIFE.gearItem())
                .pattern(" #")
                .pattern("/ ")
                .define('#', PartMaterialIngredient.of(PartTypes.MAIN.get(), GearTypes.TOOL.get()))
                .define('/', SgTags.Items.RODS_ROUGH)
                .save(consumer, SilentGear.getId("gear/rough/knife"));
        shapedGear(RecipeCategory.TOOLS, GearItemSets.PICKAXE.gearItem())
                .pattern("###")
                .pattern(" / ")
                .pattern(" / ")
                .define('#', PartMaterialIngredient.of(PartTypes.MAIN.get(), GearTypes.TOOL.get()))
                .define('/', SgTags.Items.RODS_ROUGH)
                .save(consumer, SilentGear.getId("gear/rough/pickaxe"));
        shapedGear(RecipeCategory.TOOLS, GearItemSets.SHOVEL.gearItem())
                .pattern("#")
                .pattern("/")
                .pattern("/")
                .define('#', PartMaterialIngredient.of(PartTypes.MAIN.get(), GearTypes.TOOL.get()))
                .define('/', SgTags.Items.RODS_ROUGH)
                .save(consumer, SilentGear.getId("gear/rough/shovel"));
        shapedGear(RecipeCategory.TOOLS, GearItemSets.AXE.gearItem())
                .pattern("##")
                .pattern("#/")
                .pattern(" /")
                .define('#', PartMaterialIngredient.of(PartTypes.MAIN.get(), GearTypes.TOOL.get()))
                .define('/', SgTags.Items.RODS_ROUGH)
                .save(consumer, SilentGear.getId("gear/rough/axe"));

        // Coonversion recipes
        toolConversion(consumer, GearItemSets.SWORD.gearItem(), Const.Parts.SWORD_BLADE, 2, Items.NETHERITE_SWORD, Items.DIAMOND_SWORD, Items.GOLDEN_SWORD, Items.IRON_SWORD, Items.STONE_SWORD, Items.WOODEN_SWORD);
        toolConversion(consumer, GearItemSets.PICKAXE.gearItem(), Const.Parts.PICKAXE_HEAD, 3, Items.NETHERITE_PICKAXE, Items.DIAMOND_PICKAXE, Items.GOLDEN_PICKAXE, Items.IRON_PICKAXE, Items.STONE_PICKAXE, Items.WOODEN_PICKAXE);
        toolConversion(consumer, GearItemSets.SHOVEL.gearItem(), Const.Parts.SHOVEL_HEAD, 1, Items.NETHERITE_SHOVEL, Items.DIAMOND_SHOVEL, Items.GOLDEN_SHOVEL, Items.IRON_SHOVEL, Items.STONE_SHOVEL, Items.WOODEN_SHOVEL);
        toolConversion(consumer, GearItemSets.AXE.gearItem(), Const.Parts.AXE_HEAD, 3, Items.NETHERITE_AXE, Items.DIAMOND_AXE, Items.GOLDEN_AXE, Items.IRON_AXE, Items.STONE_AXE, Items.WOODEN_AXE);
        toolConversion(consumer, GearItemSets.HOE.gearItem(), Const.Parts.HOE_HEAD, 2, Items.NETHERITE_HOE, Items.DIAMOND_HOE, Items.GOLDEN_HOE, Items.IRON_HOE, Items.STONE_HOE, Items.WOODEN_HOE);
        armorConversion(consumer, GearItemSets.HELMET.gearItem(), Const.Parts.HELMET_PLATES, 5, Items.NETHERITE_HELMET, Items.DIAMOND_HELMET, Items.GOLDEN_HELMET, Items.IRON_HELMET, Items.LEATHER_HELMET);
        armorConversion(consumer, GearItemSets.CHESTPLATE.gearItem(), Const.Parts.CHESTPLATE_PLATES, 8, Items.NETHERITE_CHESTPLATE, Items.DIAMOND_CHESTPLATE, Items.GOLDEN_CHESTPLATE, Items.IRON_CHESTPLATE, Items.LEATHER_CHESTPLATE);
        armorConversion(consumer, GearItemSets.LEGGINGS.gearItem(), Const.Parts.LEGGINGS_PLATES, 7, Items.NETHERITE_LEGGINGS, Items.DIAMOND_LEGGINGS, Items.GOLDEN_LEGGINGS, Items.IRON_LEGGINGS, Items.LEATHER_LEGGINGS);
        armorConversion(consumer, GearItemSets.BOOTS.gearItem(), Const.Parts.BOOTS_PLATES, 4, Items.NETHERITE_BOOTS, Items.DIAMOND_BOOTS, Items.GOLDEN_BOOTS, Items.IRON_BOOTS, Items.LEATHER_BOOTS);
    }

    private void registerModifierKits(RecipeOutput consumer) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SgItems.MOD_KIT)
                .define('#', SgTags.Items.TEMPLATE_BOARDS)
                .define('/', Tags.Items.RODS_WOODEN)
                .define('o', Tags.Items.INGOTS_IRON)
                .pattern("##o")
                .pattern("##/")
                .unlockedBy("has_item", has(SgTags.Items.TEMPLATE_BOARDS))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SgItems.VERY_CRUDE_REPAIR_KIT)
                .define('#', SgTags.Items.TEMPLATE_BOARDS)
                .define('/', Tags.Items.RODS_WOODEN)
                .define('o', Tags.Items.STONES)
                .pattern(" / ")
                .pattern("#o#")
                .pattern("###")
                .unlockedBy("has_item", has(SgTags.Items.TEMPLATE_BOARDS))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SgItems.CRUDE_REPAIR_KIT)
                .define('#', SgTags.Items.TEMPLATE_BOARDS)
                .define('/', Tags.Items.RODS_WOODEN)
                .define('o', Tags.Items.INGOTS_IRON)
                .pattern(" / ")
                .pattern("#o#")
                .pattern("###")
                .unlockedBy("has_item", has(SgTags.Items.TEMPLATE_BOARDS))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SgItems.STURDY_REPAIR_KIT)
                .define('#', Tags.Items.INGOTS_IRON)
                .define('/', SgTags.Items.RODS_IRON)
                .define('o', Tags.Items.GEMS_DIAMOND)
                .pattern(" / ")
                .pattern("#o#")
                .pattern("###")
                .unlockedBy("has_item", has(Tags.Items.INGOTS_IRON))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SgItems.CRIMSON_REPAIR_KIT)
                .define('#', SgTags.Items.INGOTS_CRIMSON_STEEL)
                .define('/', Tags.Items.RODS_BLAZE)
                .define('o', SgTags.Items.INGOTS_BLAZE_GOLD)
                .pattern(" / ")
                .pattern("#o#")
                .pattern("###")
                .unlockedBy("has_item", has(SgTags.Items.INGOTS_CRIMSON_STEEL))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SgItems.AZURE_REPAIR_KIT)
                .define('#', SgTags.Items.INGOTS_AZURE_ELECTRUM)
                .define('/', Items.END_ROD)
                .define('o', Tags.Items.GEMS_EMERALD)
                .pattern(" / ")
                .pattern("#o#")
                .pattern("###")
                .unlockedBy("has_item", has(SgTags.Items.INGOTS_AZURE_ELECTRUM))
                .save(consumer);

        for (RepairKitItem item : SgItems.getItems(RepairKitItem.class)) {
            // Empty repair kit recipes
            shapeless(RecipeCategory.MISC, item)
                    .requires(item)
                    .requires(Tags.Items.RODS_WOODEN)
                    .save(consumer, SilentGear.getId(NameUtils.fromItem(item).getPath() + "_empty"));
        }
    }

    private void registerMachines(RecipeOutput consumer) {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, SgBlocks.STONE_ANVIL)
                .define('#', Tags.Items.COBBLESTONES)
                .define('/', ItemTags.DIRT)
                .pattern(" # ")
                .pattern("#/#")
                .unlockedBy("has_item", has(Tags.Items.COBBLESTONES))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, SgBlocks.ALLOY_FORGE)
                .define('/', SgTags.Items.INGOTS_CRIMSON_STEEL)
                .define('i', Tags.Items.STORAGE_BLOCKS_IRON)
                .define('#', Blocks.BLACKSTONE)
                .pattern("/#/")
                .pattern("/ /")
                .pattern("#i#")
                .unlockedBy("has_item", has(SgTags.Items.INGOTS_CRIMSON_STEEL))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, SgBlocks.RECRYSTALLIZER)
                .define('/', SgTags.Items.INGOTS_AZURE_ELECTRUM)
                .define('g', Tags.Items.STORAGE_BLOCKS_GOLD)
                .define('d', Tags.Items.GEMS_DIAMOND)
                .define('e', Tags.Items.GEMS_EMERALD)
                .define('#', Blocks.PURPUR_BLOCK)
                .pattern("/e/")
                .pattern("/d/")
                .pattern("#g#")
                .unlockedBy("has_item", has(SgTags.Items.INGOTS_AZURE_ELECTRUM))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, SgBlocks.REFABRICATOR)
                .define('/', Tags.Items.INGOTS_IRON)
                .define('i', Tags.Items.STORAGE_BLOCKS_IRON)
                .define('d', Tags.Items.GEMS_DIAMOND)
                .define('b', SgTags.Items.GEMS_BORT)
                .define('#', ItemTags.PLANKS)
                .pattern("/ /")
                .pattern("dbd")
                .pattern("#i#")
                .unlockedBy("has_item", has(SgTags.Items.GEMS_BORT))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, SgBlocks.METAL_PRESS)
                .define('#', Tags.Items.OBSIDIANS)
                .define('t', SgTags.Items.INGOTS_TYRIAN_STEEL)
                .define('/', SgTags.Items.RODS_IRON)
                .pattern("#t#")
                .pattern("/ /")
                .pattern("#t#")
                .unlockedBy("has_item", has(SgTags.Items.INGOTS_TYRIAN_STEEL))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, SgBlocks.STARLIGHT_CHARGER)
                .define('#', Blocks.POLISHED_BLACKSTONE)
                .define('/', SgTags.Items.STORAGE_BLOCKS_BLAZE_GOLD)
                .define('q', Blocks.QUARTZ_BLOCK)
                .define('g', Tags.Items.GLASS_BLOCKS_COLORLESS)
                .pattern("qgq")
                .pattern("#g#")
                .pattern("#/#")
                .unlockedBy("has_item", has(SgTags.Items.INGOTS_BLAZE_GOLD))
                .save(consumer);
    }

    private void registerCompounding(RecipeOutput consumer) {
        CompoundingRecipeBuilder.gemBuilder(SgItems.CUSTOM_GEM, 1)
                .withCustomMaterial(Const.Materials.DIMERALD)
                .addIngredient(Tags.Items.GEMS_DIAMOND)
                .addIngredient(Tags.Items.GEMS_EMERALD)
                .save(consumer);

        CompoundingRecipeBuilder.metalBuilder(SgItems.CUSTOM_INGOT, 1)
                .withCustomMaterial(DataResource.material("high_carbon_steel"))
                .addIngredient(Tags.Items.INGOTS_IRON)
                .addIngredient(ItemTags.COALS, 3)
                .save(consumer);

        CompoundingRecipeBuilder.metalBuilder(CraftingItems.TYRIAN_STEEL_INGOT, 4)
                .addIngredient(SgTags.Items.INGOTS_CRIMSON_STEEL)
                .addIngredient(SgTags.Items.INGOTS_AZURE_ELECTRUM)
                .addIngredient(CraftingItems.CRUSHED_SHULKER_SHELL)
                .addIngredient(Items.NETHERITE_SCRAP)
                .save(consumer);

        CompoundingRecipeBuilder.metalBuilder(CraftingItems.BLAZE_GOLD_INGOT, 1)
                .addIngredient(Tags.Items.INGOTS_GOLD)
                .addIngredient(Items.BLAZE_POWDER, 2)
                .save(consumer);

        CompoundingRecipeBuilder.metalBuilder(CraftingItems.CRIMSON_STEEL_INGOT, 3)
                .addIngredient(SgTags.Items.STORAGE_BLOCKS_CRIMSON_IRON)
                .addIngredient(Tags.Items.RODS_BLAZE, 2)
                .addIngredient(Items.MAGMA_CREAM)
                .save(consumer);
    }

    private void registerPressing(RecipeOutput consumer) {
        new SingleItemRecipeBuilder(RecipeCategory.MISC,
                MaterialPressingRecipe::new,
                new Ingredient(PartMaterialIngredient.of(PartTypes.MAIN.get(), MaterialCategories.METAL)),
                SgItems.SHEET_METAL, 2
        ).unlockedBy("impossible", CriteriaTriggers.IMPOSSIBLE.createCriterion(new ImpossibleTrigger.TriggerInstance()))
                .save(consumer);
    }

    private void registerCraftingItems(RecipeOutput consumer) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, SgItems.GUIDE_BOOK)
                .requires(Items.BOOK)
                .requires(SgTags.Items.TEMPLATE_BOARDS)
                .unlockedBy("has_template_board", has(SgTags.Items.TEMPLATE_BOARDS))
                .save(consumer);

        // TODO: stone anvil recipe
        /*damageGear(CraftingItems.GLOWING_DUST, 4, 4)
                .requires()(ModTags.Items.HAMMERS)
                .requires()(Tags.Items.DUSTS_GLOWSTONE, 2)
                .requires()(Tags.Items.GEMS_QUARTZ)
                .save();(consumer);*/

        shapeless(RecipeCategory.MISC, CraftingItems.GLOWING_DUST, 4)
                .requires(Items.STICK)
                .requires(Tags.Items.DUSTS_GLOWSTONE, 2)
                .requires(Tags.Items.GEMS_QUARTZ)
                .unlockedBy("has_item", has(Tags.Items.DUSTS_GLOWSTONE))
                .save(consumer);

        toolAction(consumer, SgTags.Items.HAMMERS, Tags.Items.COBBLESTONES, 1, SgItems.PEBBLE, 9)
                .save(consumer);

        toolAction(consumer, SgTags.Items.KNIVES, ItemTags.LOGS, 1, CraftingItems.TEMPLATE_BOARD, 6)
                .save(consumer);

        toolAction(consumer, SgTags.Items.HAMMERS, Items.SHULKER_SHELL, 10, CraftingItems.CRUSHED_SHULKER_SHELL, 1)
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CraftingItems.AZURE_ELECTRUM_INGOT)
                .define('/', Tags.Items.INGOTS_GOLD)
                .define('#', SgTags.Items.INGOTS_AZURE_SILVER)
                .define('o', Tags.Items.ENDER_PEARLS)
                .pattern("/ /")
                .pattern("#o#")
                .pattern("# #")
                .unlockedBy("has_item", has(CraftingItems.AZURE_SILVER_INGOT))
                .save(consumer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.BLUE_DYE)
                .requires(CraftingItems.FLAX_FLOWERS, 4)
                .unlockedBy("has_item", has(CraftingItems.FLAX_FLOWERS))
                .save(consumer, SilentGear.getId("blue_dye_from_flax_flowers"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, CraftingItems.ROAD_MAKER_UPGRADE)
                .requires(CraftingItems.ADVANCED_UPGRADE_BASE)
                .requires(Items.IRON_SHOVEL)
                .requires(Tags.Items.DYES_ORANGE)
                .unlockedBy("has_item", has(CraftingItems.UPGRADE_BASE))
                .save(consumer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, CraftingItems.SPOON_UPGRADE)
                .requires(CraftingItems.ADVANCED_UPGRADE_BASE)
                .requires(Items.DIAMOND_SHOVEL)
                .unlockedBy("has_item", has(CraftingItems.UPGRADE_BASE))
                .save(consumer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, CraftingItems.WIDE_PLATE_UPGRADE)
                .requires(CraftingItems.ADVANCED_UPGRADE_BASE)
                .requires(SgTags.Items.STORAGE_BLOCKS_CRIMSON_IRON)
                .requires(SgTags.Items.INGOTS_CRIMSON_STEEL)
                .unlockedBy("has_item", has(CraftingItems.UPGRADE_BASE))
                .save(consumer);

        SimpleCookingRecipeBuilder.smelting(Ingredient.of(SgTags.Items.NETHERWOOD_LOGS), RecipeCategory.MISC, SgItems.NETHERWOOD_CHARCOAL, 0.15f, 200)
                .unlockedBy("has_item", has(SgTags.Items.NETHERWOOD_LOGS))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SgBlocks.NETHERWOOD_CHARCOAL_BLOCK)
                .define('#', SgItems.NETHERWOOD_CHARCOAL)
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .unlockedBy("has_item", has(SgItems.NETHERWOOD_CHARCOAL))
                .save(consumer);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, SgItems.NETHERWOOD_CHARCOAL, 9)
                .requires(SgBlocks.NETHERWOOD_CHARCOAL_BLOCK)
                .unlockedBy("has_item", has(SgBlocks.NETHERWOOD_CHARCOAL_BLOCK))
                .save(consumer, SilentGear.getId("netherwood_charcoal_from_block"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CraftingItems.FLUFFY_FABRIC)
                .pattern("##")
                .pattern("##")
                .define('#', CraftingItems.FLUFFY_PUFF)
                .unlockedBy("has_item", has(CraftingItems.FLUFFY_PUFF))
                .save(consumer);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, CraftingItems.FLUFFY_PUFF, 4)
                .requires(CraftingItems.FLUFFY_FABRIC)
                .unlockedBy("has_item", has(CraftingItems.FLUFFY_PUFF))
                .save(consumer, SilentGear.getId("fluffy_puff_from_fabric"));

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, SgBlocks.WHITE_FLUFFY_BLOCK)
                .pattern("##")
                .pattern("##")
                .define('#', CraftingItems.FLUFFY_FABRIC)
                .unlockedBy("has_item", has(CraftingItems.FLUFFY_PUFF))
                .save(consumer, SilentGear.getId("fluffy_block_base"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, CraftingItems.FLUFFY_FABRIC, 4)
                .requires(SgTags.Items.FLUFFY_BLOCKS)
                .unlockedBy("has_item", has(CraftingItems.FLUFFY_PUFF))
                .save(consumer, SilentGear.getId("fluffy_fabric_from_block"));

        dyeFluffyBlock(consumer, SgBlocks.WHITE_FLUFFY_BLOCK, Tags.Items.DYES_WHITE);
        dyeFluffyBlock(consumer, SgBlocks.ORANGE_FLUFFY_BLOCK, Tags.Items.DYES_ORANGE);
        dyeFluffyBlock(consumer, SgBlocks.MAGENTA_FLUFFY_BLOCK, Tags.Items.DYES_MAGENTA);
        dyeFluffyBlock(consumer, SgBlocks.LIGHT_BLUE_FLUFFY_BLOCK, Tags.Items.DYES_LIGHT_BLUE);
        dyeFluffyBlock(consumer, SgBlocks.YELLOW_FLUFFY_BLOCK, Tags.Items.DYES_YELLOW);
        dyeFluffyBlock(consumer, SgBlocks.LIME_FLUFFY_BLOCK, Tags.Items.DYES_LIME);
        dyeFluffyBlock(consumer, SgBlocks.PINK_FLUFFY_BLOCK, Tags.Items.DYES_PINK);
        dyeFluffyBlock(consumer, SgBlocks.GRAY_FLUFFY_BLOCK, Tags.Items.DYES_GRAY);
        dyeFluffyBlock(consumer, SgBlocks.LIGHT_GRAY_FLUFFY_BLOCK, Tags.Items.DYES_LIGHT_GRAY);
        dyeFluffyBlock(consumer, SgBlocks.CYAN_FLUFFY_BLOCK, Tags.Items.DYES_CYAN);
        dyeFluffyBlock(consumer, SgBlocks.PURPLE_FLUFFY_BLOCK, Tags.Items.DYES_PURPLE);
        dyeFluffyBlock(consumer, SgBlocks.BLUE_FLUFFY_BLOCK, Tags.Items.DYES_BLUE);
        dyeFluffyBlock(consumer, SgBlocks.BROWN_FLUFFY_BLOCK, Tags.Items.DYES_BROWN);
        dyeFluffyBlock(consumer, SgBlocks.GREEN_FLUFFY_BLOCK, Tags.Items.DYES_GREEN);
        dyeFluffyBlock(consumer, SgBlocks.RED_FLUFFY_BLOCK, Tags.Items.DYES_RED);
        dyeFluffyBlock(consumer, SgBlocks.BLACK_FLUFFY_BLOCK, Tags.Items.DYES_BLACK);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, SgItems.FLUFFY_SEEDS)
                .requires(CraftingItems.FLUFFY_PUFF)
                .unlockedBy("has_item", has(CraftingItems.FLUFFY_PUFF))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CraftingItems.FLUFFY_FEATHER)
                .pattern(" ##")
                .pattern("## ")
                .pattern("#  ")
                .define('#', CraftingItems.FLUFFY_PUFF)
                .unlockedBy("has_item", has(CraftingItems.FLUFFY_PUFF))
                .save(consumer);

        shapeless(RecipeCategory.MISC, Items.FEATHER)
                .requires(CraftingItems.FLUFFY_FEATHER)
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CraftingItems.FLUFFY_STRING)
                .pattern("###")
                .define('#', CraftingItems.FLUFFY_PUFF)
                .unlockedBy("has_item", has(CraftingItems.FLUFFY_PUFF))
                .save(consumer);

        shapeless(RecipeCategory.MISC, Items.STRING)
                .requires(CraftingItems.FLUFFY_STRING)
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.WHITE_WOOL)
                .pattern("###")
                .pattern("#~#")
                .pattern("###")
                .define('#', CraftingItems.FLUFFY_PUFF)
                .define('~', Tags.Items.STRINGS)
                .unlockedBy("has_item", has(CraftingItems.FLUFFY_PUFF))
                .save(consumer, SilentGear.getId("fluffy_wool"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CraftingItems.FINE_SILK_CLOTH)
                .pattern("##")
                .pattern("##")
                .define('#', CraftingItems.FINE_SILK)
                .unlockedBy("has_item", has(CraftingItems.FINE_SILK))
                .save(consumer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, CraftingItems.FINE_SILK, 4)
                .requires(CraftingItems.FINE_SILK_CLOTH)
                .unlockedBy("has_item", has(CraftingItems.FINE_SILK))
                .save(consumer);

        shapeless(RecipeCategory.MISC, CraftingItems.NETHER_STAR_FRAGMENT, 9)
                .requires(Items.NETHER_STAR)
                .save(consumer);

        shaped(RecipeCategory.MISC, Items.NETHER_STAR)
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', CraftingItems.NETHER_STAR_FRAGMENT)
                .unlockedBy("has_item", has(Items.NETHER_STAR))
                .save(consumer, SilentGear.getId("nether_star_from_fragments"));

        shapeless(RecipeCategory.MISC, CraftingItems.STARMETAL_DUST, 3)
                .requires(SgTags.Items.DUSTS_AZURE_ELECTRUM, 1)
                .requires(SgTags.Items.DUSTS_AZURE_SILVER, 2)
                .requires(SgTags.Items.DUSTS_BLAZE_GOLD, 1)
                .requires(CraftingItems.NETHER_STAR_FRAGMENT)
                .unlockedBy("has_item", has(CraftingItems.NETHER_STAR_FRAGMENT))
                .save(consumer);

        shapeless(RecipeCategory.MISC, CraftingItems.BRONZE_INGOT, 4)
                .requires(Tags.Items.INGOTS_COPPER, 3)
                .requires(Tags.Items.INGOTS_IRON, 1)
                .unlockedBy("has_item", has(Tags.Items.INGOTS_COPPER))
                .save(consumer);

        // TODO: Maybe should organize these better...
        // A
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CraftingItems.ADVANCED_UPGRADE_BASE)
                .define('/', SgTags.Items.NUGGETS_DIAMOND)
                .define('D', Tags.Items.DYES_BLUE)
                .define('U', CraftingItems.UPGRADE_BASE)
                .define('G', Tags.Items.NUGGETS_GOLD)
                .pattern("///")
                .pattern("DUD")
                .pattern("GGG")
                .unlockedBy("has_item", has(CraftingItems.UPGRADE_BASE))
                .save(consumer);
        // B
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, CraftingItems.BLAZE_GOLD_INGOT)
                .requires(Tags.Items.INGOTS_GOLD)
                .requires(Items.BLAZE_POWDER, 4)
                .unlockedBy("has_item", has(Items.BLAZE_POWDER))
                .save(consumer);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, CraftingItems.BLAZING_DUST, 4)
                .requires(SgTags.Items.DUSTS_BLAZE_GOLD)
                .requires(Ingredient.of(Tags.Items.DUSTS_GLOWSTONE), 2)
                .unlockedBy("has_item", has(SgTags.Items.DUSTS_BLAZE_GOLD))
                .save(consumer);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, CraftingItems.BLUEPRINT_PAPER, 4)
                .requires(Ingredient.of(SgTags.Items.PAPER), 4)
                .requires(Tags.Items.DYES_BLUE)
                .unlockedBy("has_paper", has(SgTags.Items.PAPER))
                .save(consumer);
        // C
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Blocks.COBBLESTONE)
                .requires(SgItems.PEBBLE, 9)
                .unlockedBy("has_pebble", has(SgItems.PEBBLE))
                .save(consumer, SilentGear.getId("cobblestone_from_pebbles"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CraftingItems.CRIMSON_STEEL_INGOT)
                .define('/', Tags.Items.RODS_BLAZE)
                .define('#', SgTags.Items.INGOTS_CRIMSON_IRON)
                .define('C', Items.MAGMA_CREAM)
                .pattern("/ /")
                .pattern("#C#")
                .pattern("# #")
                .unlockedBy("has_item", has(CraftingItems.CRIMSON_IRON_INGOT))
                .save(consumer);
        // D
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, CraftingItems.DIAMOND_SHARD, 9)
                .requires(Tags.Items.GEMS_DIAMOND)
                .unlockedBy("has_item", has(Tags.Items.GEMS_DIAMOND))
                .save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.DIAMOND)
                .define('#', SgTags.Items.NUGGETS_DIAMOND)
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .unlockedBy("has_item", has(Tags.Items.GEMS_DIAMOND))
                .save(consumer, SilentGear.getId("diamond_from_shards"));
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(CraftingItems.SINEW), RecipeCategory.MISC, CraftingItems.DRIED_SINEW, 0.35f, 200)
                .unlockedBy("has_item", has(CraftingItems.SINEW))
                .save(consumer);
        // E
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, CraftingItems.EMERALD_SHARD, 9)
                .requires(Tags.Items.GEMS_EMERALD)
                .unlockedBy("has_item", has(Tags.Items.GEMS_EMERALD))
                .save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.EMERALD)
                .define('#', SgTags.Items.NUGGETS_EMERALD)
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .unlockedBy("has_item", has(Tags.Items.GEMS_EMERALD))
                .save(consumer, SilentGear.getId("emerald_from_shards"));
        // F
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, CraftingItems.FLAX_STRING)
                .requires(CraftingItems.FLAX_FIBER, 2)
                .unlockedBy("has_item", has(CraftingItems.FLAX_FIBER))
                .save(consumer);
        // G
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CraftingItems.GLITTERY_DUST, 8)
                .define('o', Items.POPPED_CHORUS_FRUIT)
                .define('/', SgTags.Items.NUGGETS_EMERALD)
                .define('#', Tags.Items.DUSTS_GLOWSTONE)
                .define('b', SgItems.NETHER_BANANA)
                .pattern("o/o")
                .pattern("#b#")
                .pattern("o/o")
                .unlockedBy("has_item", has(SgItems.NETHER_BANANA))
                .save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SgItems.GOLDEN_NETHER_BANANA)
                .define('g', Tags.Items.INGOTS_GOLD)
                .define('b', SgItems.NETHER_BANANA)
                .pattern("ggg")
                .pattern("gbg")
                .pattern("ggg")
                .unlockedBy("has_item", has(SgItems.NETHER_BANANA))
                .save(consumer);
        // I
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CraftingItems.IRON_ROD, 4)
                .define('/', Tags.Items.INGOTS_IRON)
                .pattern("/")
                .pattern("/")
                .unlockedBy("has_item", has(Items.IRON_INGOT))
                .save(consumer);
        // L
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.LEATHER)
                .define('#', CraftingItems.LEATHER_SCRAP)
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .unlockedBy("has_item", has(CraftingItems.LEATHER_SCRAP))
                .save(consumer, SilentGear.getId("leather_from_scraps"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, CraftingItems.LEATHER_SCRAP, 9)
                .requires(Items.LEATHER)
                .unlockedBy("has_item", has(CraftingItems.LEATHER_SCRAP))
                .save(consumer);
        // M
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SgBlocks.MATERIAL_GRADER)
                .define('Q', Tags.Items.GEMS_QUARTZ)
                .define('I', Tags.Items.INGOTS_IRON)
                .define('#', CraftingItems.ADVANCED_UPGRADE_BASE)
                .define('G', SgTags.Items.INGOTS_BLAZE_GOLD)
                .pattern("QIQ")
                .pattern("I#I")
                .pattern("GGG")
                .unlockedBy("has_item", has(SgTags.Items.INGOTS_BLAZE_GOLD))
                .save(consumer);
        // N
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SgBlocks.NETHERWOOD_DOOR, 3)
                .define('#', SgBlocks.NETHERWOOD_PLANKS)
                .pattern("##")
                .pattern("##")
                .pattern("##")
                .unlockedBy("has_item", has(SgBlocks.NETHERWOOD_PLANKS))
                .save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SgBlocks.NETHERWOOD_TRAPDOOR, 2)
                .define('#', SgBlocks.NETHERWOOD_PLANKS)
                .pattern("###")
                .pattern("###")
                .unlockedBy("has_item", has(SgBlocks.NETHERWOOD_PLANKS))
                .save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SgBlocks.NETHERWOOD_FENCE, 3)
                .define('#', SgBlocks.NETHERWOOD_PLANKS)
                .define('/', Tags.Items.RODS_WOODEN)
                .pattern("#/#")
                .pattern("#/#")
                .unlockedBy("has_item", has(SgBlocks.NETHERWOOD_PLANKS))
                .save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SgBlocks.NETHERWOOD_FENCE_GATE, 1)
                .define('#', SgBlocks.NETHERWOOD_PLANKS)
                .define('/', Tags.Items.RODS_WOODEN)
                .pattern("/#/")
                .pattern("/#/")
                .unlockedBy("has_item", has(SgBlocks.NETHERWOOD_PLANKS))
                .save(consumer);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, SgBlocks.NETHERWOOD_PLANKS, 4)
                .requires(SgTags.Items.NETHERWOOD_LOGS)
                .unlockedBy("has_item", has(SgTags.Items.NETHERWOOD_LOGS))
                .save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SgBlocks.NETHERWOOD_WOOD, 3)
                .define('#', SgBlocks.NETHERWOOD_LOG)
                .pattern("##")
                .pattern("##")
                .unlockedBy("has_item", has(SgBlocks.NETHERWOOD_LOG))
                .save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SgBlocks.NETHERWOOD_PLANKS)
                .define('#', SgBlocks.NETHERWOOD_SLAB)
                .pattern("#")
                .pattern("#")
                .unlockedBy("has_item", has(SgBlocks.NETHERWOOD_LOG))
                .save(consumer, SilentGear.getId("netherwood_planks_from_slabs"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SgBlocks.NETHERWOOD_PLANKS, 3)
                .define('#', SgBlocks.NETHERWOOD_STAIRS)
                .pattern("##")
                .pattern("##")
                .unlockedBy("has_item", has(SgBlocks.NETHERWOOD_LOG))
                .save(consumer, SilentGear.getId("netherwood_planks_from_stairs"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SgBlocks.NETHERWOOD_SLAB, 6)
                .define('#', SgBlocks.NETHERWOOD_PLANKS)
                .pattern("###")
                .unlockedBy("has_item", has(SgBlocks.NETHERWOOD_LOG))
                .save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SgBlocks.NETHERWOOD_STAIRS, 4)
                .define('#', SgBlocks.NETHERWOOD_PLANKS)
                .pattern("#  ")
                .pattern("## ")
                .pattern("###")
                .unlockedBy("has_item", has(SgBlocks.NETHERWOOD_LOG))
                .save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CraftingItems.NETHERWOOD_STICK, 4)
                .define('#', SgBlocks.NETHERWOOD_PLANKS)
                .pattern(" #")
                .pattern("# ")
                .unlockedBy("has_item", has(SgBlocks.NETHERWOOD_LOG))
                .save(consumer);
        // R
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, CraftingItems.RED_CARD_UPGRADE, 4)
                .requires(CraftingItems.UPGRADE_BASE)
                .requires(Tags.Items.DYES_RED)
                .unlockedBy("has_item", has(CraftingItems.UPGRADE_BASE))
                .save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CraftingItems.ROUGH_ROD, 2)
                .define('/', Tags.Items.RODS_WOODEN)
                .pattern(" /")
                .pattern("/ ")
                .unlockedBy("has_item", has(Items.STICK))
                .save(consumer);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, CraftingItems.ROUGH_ROD, 2)
                .requires(SgItems.ROD_BLUEPRINT.get().getItemTag())
                .requires(Ingredient.of(Tags.Items.RODS_WOODEN), 2)
                .unlockedBy("has_item", has(SgItems.ROD_BLUEPRINT.get().getItemTag()))
                .save(consumer, SilentGear.getId("rough_rod2"));
        // S
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SgBlocks.SALVAGER)
                .define('P', Blocks.PISTON)
                .define('/', SgTags.Items.INGOTS_CRIMSON_IRON)
                .define('I', Tags.Items.STORAGE_BLOCKS_IRON)
                .define('#', Tags.Items.OBSIDIANS)
                .pattern(" P ")
                .pattern("/I/")
                .pattern("/#/")
                .unlockedBy("has_item", has(SgTags.Items.INGOTS_CRIMSON_IRON))
                .save(consumer);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, CraftingItems.SINEW_FIBER, 3)
                .requires(CraftingItems.DRIED_SINEW)
                .unlockedBy("has_item", has(CraftingItems.SINEW))
                .save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CraftingItems.STONE_ROD, 4)
                .define('#', Tags.Items.COBBLESTONES)
                .pattern("#")
                .pattern("#")
                .unlockedBy("has_item", has(Tags.Items.COBBLESTONES))
                .save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SgBlocks.STONE_TORCH, 4)
                .define('#', ItemTags.COALS)
                .define('/', SgTags.Items.RODS_STONE)
                .pattern("#")
                .pattern("/")
                .unlockedBy("has_item", has(ItemTags.COALS))
                .save(consumer);
        // U
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, CraftingItems.UPGRADE_BASE, 4)
                .requires(Ingredient.of(SgTags.Items.PAPER), 2)
                .requires(ItemTags.PLANKS)
                .requires(Tags.Items.STONES)
                .unlockedBy("has_item", has(ItemTags.PLANKS))
                .save(consumer);
    }

    private void dyeFluffyBlock(RecipeOutput consumer, ItemLike block, TagKey<Item> dye) {
        shaped(RecipeCategory.BUILDING_BLOCKS, block, 8)
                .pattern("###")
                .pattern("#d#")
                .pattern("###")
                .define('#', SgTags.Items.FLUFFY_BLOCKS)
                .define('d', dye)
                .unlockedBy("has_item", has(SgBlocks.WHITE_FLUFFY_BLOCK))
                .save(consumer);
    }

    private void registerSmithing(RecipeOutput consumer) {
        SgItems.getItems(item -> item instanceof GearItem).forEach(item -> {
            if (((GearItem) item).getGearType() != GearTypes.ELYTRA.get()) {
                GearSmithingRecipeBuilder.coating(item).save(consumer);
            }
            GearSmithingRecipeBuilder.upgrade(item, PartTypes.MISC_UPGRADE.get()).save(consumer);
        });

        shapeless(RecipeCategory.MISC, SgItems.COATING_SMITHING_TEMPLATE)
                .requires(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE)
                .requires(SgTags.Items.BLUEPRINT_PAPER)
                .requires(SgTags.Items.GEMS_BORT)
                .unlockedBy("has_item", has(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE))
                .save(consumer);

        shaped(RecipeCategory.MISC, SgItems.COATING_SMITHING_TEMPLATE, 2)
                .pattern("dtd")
                .pattern("dnd")
                .pattern("ddd")
                .define('d', Tags.Items.GEMS_DIAMOND)
                .define('t', SgItems.COATING_SMITHING_TEMPLATE)
                .define('n', Tags.Items.NETHERRACKS)
                .unlockedBy("has_item", has(SgItems.COATING_SMITHING_TEMPLATE))
                .save(consumer, SilentGear.getId("coating_smithing_template_duplication"));
    }

    private void registerSalvaging(RecipeOutput consumer) {
        SgItems.getItems(item -> item instanceof GearItem).forEach(item ->
                gearSalvage(consumer, (GearItem) item));

        vanillaSalvage(consumer, Items.NETHERITE_SWORD, Items.DIAMOND, 2, 1, Items.NETHERITE_INGOT);
        vanillaSalvage(consumer, Items.NETHERITE_PICKAXE, Items.DIAMOND, 3, 2, Items.NETHERITE_INGOT);
        vanillaSalvage(consumer, Items.NETHERITE_SHOVEL, Items.DIAMOND, 1, 2, Items.NETHERITE_INGOT);
        vanillaSalvage(consumer, Items.NETHERITE_AXE, Items.DIAMOND, 3, 2, Items.NETHERITE_INGOT);
        vanillaSalvage(consumer, Items.NETHERITE_HOE, Items.DIAMOND, 2, 2, Items.NETHERITE_INGOT);
        vanillaSalvage(consumer, Items.NETHERITE_HELMET, Items.DIAMOND, 5, 0, Items.NETHERITE_INGOT);
        vanillaSalvage(consumer, Items.NETHERITE_CHESTPLATE, Items.DIAMOND, 8, 0, Items.NETHERITE_INGOT);
        vanillaSalvage(consumer, Items.NETHERITE_LEGGINGS, Items.DIAMOND, 7, 0, Items.NETHERITE_INGOT);
        vanillaSalvage(consumer, Items.NETHERITE_BOOTS, Items.DIAMOND, 4, 0, Items.NETHERITE_INGOT);

        vanillaSalvage(consumer, Items.DIAMOND_SWORD, Items.DIAMOND, 2, 1);
        vanillaSalvage(consumer, Items.DIAMOND_PICKAXE, Items.DIAMOND, 3, 2);
        vanillaSalvage(consumer, Items.DIAMOND_SHOVEL, Items.DIAMOND, 1, 2);
        vanillaSalvage(consumer, Items.DIAMOND_AXE, Items.DIAMOND, 3, 2);
        vanillaSalvage(consumer, Items.DIAMOND_HOE, Items.DIAMOND, 2, 2);
        vanillaSalvage(consumer, Items.DIAMOND_HELMET, Items.DIAMOND, 5, 0);
        vanillaSalvage(consumer, Items.DIAMOND_CHESTPLATE, Items.DIAMOND, 8, 0);
        vanillaSalvage(consumer, Items.DIAMOND_LEGGINGS, Items.DIAMOND, 7, 0);
        vanillaSalvage(consumer, Items.DIAMOND_BOOTS, Items.DIAMOND, 4, 0);

        vanillaSalvage(consumer, Items.GOLDEN_SWORD, Items.GOLD_INGOT, 2, 1);
        vanillaSalvage(consumer, Items.GOLDEN_PICKAXE, Items.GOLD_INGOT, 3, 2);
        vanillaSalvage(consumer, Items.GOLDEN_SHOVEL, Items.GOLD_INGOT, 1, 2);
        vanillaSalvage(consumer, Items.GOLDEN_AXE, Items.GOLD_INGOT, 3, 2);
        vanillaSalvage(consumer, Items.GOLDEN_HOE, Items.GOLD_INGOT, 2, 2);
        vanillaSalvage(consumer, Items.GOLDEN_HELMET, Items.GOLD_INGOT, 5, 0);
        vanillaSalvage(consumer, Items.GOLDEN_CHESTPLATE, Items.GOLD_INGOT, 8, 0);
        vanillaSalvage(consumer, Items.GOLDEN_LEGGINGS, Items.GOLD_INGOT, 7, 0);
        vanillaSalvage(consumer, Items.GOLDEN_BOOTS, Items.GOLD_INGOT, 4, 0);

        vanillaSalvage(consumer, Items.IRON_SWORD, Items.IRON_INGOT, 2, 1);
        vanillaSalvage(consumer, Items.IRON_PICKAXE, Items.IRON_INGOT, 3, 2);
        vanillaSalvage(consumer, Items.IRON_SHOVEL, Items.IRON_INGOT, 1, 2);
        vanillaSalvage(consumer, Items.IRON_AXE, Items.IRON_INGOT, 3, 2);
        vanillaSalvage(consumer, Items.IRON_HOE, Items.IRON_INGOT, 2, 2);
        vanillaSalvage(consumer, Items.IRON_HELMET, Items.IRON_INGOT, 5, 0);
        vanillaSalvage(consumer, Items.IRON_CHESTPLATE, Items.IRON_INGOT, 8, 0);
        vanillaSalvage(consumer, Items.IRON_LEGGINGS, Items.IRON_INGOT, 7, 0);
        vanillaSalvage(consumer, Items.IRON_BOOTS, Items.IRON_INGOT, 4, 0);
        vanillaSalvage(consumer, Items.SHEARS, Items.IRON_INGOT, 2, 0);

        vanillaSalvage(consumer, Items.LEATHER_HELMET, Items.LEATHER, 5, 0);
        vanillaSalvage(consumer, Items.LEATHER_CHESTPLATE, Items.LEATHER, 8, 0);
        vanillaSalvage(consumer, Items.LEATHER_LEGGINGS, Items.LEATHER, 7, 0);
        vanillaSalvage(consumer, Items.LEATHER_BOOTS, Items.LEATHER, 4, 0);
        vanillaSalvage(consumer, Items.LEATHER_HORSE_ARMOR, Items.LEATHER, 7, 0);

        vanillaSalvage(consumer, Items.STONE_SWORD, Items.COBBLESTONE, 2, 1);
        vanillaSalvage(consumer, Items.STONE_PICKAXE, Items.COBBLESTONE, 3, 2);
        vanillaSalvage(consumer, Items.STONE_SHOVEL, Items.COBBLESTONE, 1, 2);
        vanillaSalvage(consumer, Items.STONE_AXE, Items.COBBLESTONE, 3, 2);
        vanillaSalvage(consumer, Items.STONE_HOE, Items.COBBLESTONE, 2, 2);

        vanillaSalvage(consumer, Items.BOW, Items.STRING, 3, 3);

        SalvagingRecipeBuilder.builder(Items.DIAMOND_HORSE_ARMOR)
                .addResult(Items.DIAMOND, 6)
                .addResult(Items.LEATHER)
                .save(consumer, SilentGear.getId("salvaging/diamond_horse_armor"));

        SalvagingRecipeBuilder.builder(Items.GOLDEN_HORSE_ARMOR)
                .addResult(Items.GOLD_INGOT, 6)
                .addResult(Items.LEATHER)
                .save(consumer, SilentGear.getId("salvaging/golden_horse_armor"));

        SalvagingRecipeBuilder.builder(Items.IRON_HORSE_ARMOR)
                .addResult(Items.IRON_INGOT, 6)
                .addResult(Items.LEATHER)
                .save(consumer, SilentGear.getId("salvaging/iron_horse_armor"));

        SalvagingRecipeBuilder.builder(Items.CROSSBOW)
                .addResult(Items.STICK, 3)
                .addResult(Items.STRING, 2)
                .addResult(Items.IRON_INGOT)
                .addResult(Items.TRIPWIRE_HOOK)
                .save(consumer, SilentGear.getId("salvaging/crossbow"));

        SalvagingRecipeBuilder.builder(Items.CLOCK)
                .addResult(Items.GOLD_INGOT, 4)
                .addResult(Items.REDSTONE)
                .save(consumer, SilentGear.getId("salvaging/clock"));

        SalvagingRecipeBuilder.builder(Items.COMPASS)
                .addResult(Items.IRON_INGOT, 4)
                .addResult(Items.REDSTONE)
                .save(consumer, SilentGear.getId("salvaging/compass"));
    }

    private ExtendedShapelessRecipeBuilder<ShapelessRecipe> shapeless(RecipeCategory recipeCategory, ItemLike item) {
        return shapeless(recipeCategory, item, 1);
    }

    private ExtendedShapelessRecipeBuilder<ShapelessRecipe> shapeless(RecipeCategory recipeCategory, ItemLike item, int count) {
        return new ExtendedShapelessRecipeBuilder.Basic<>(recipeCategory, new ItemStack(item, count), ShapelessRecipe::new);
    }

    private ExtendedShapedRecipeBuilder.Basic<ShapedRecipe> shaped(RecipeCategory recipeCategory, ItemLike item) {
        return shaped(recipeCategory, item, 1);
    }

    private ExtendedShapedRecipeBuilder.Basic<ShapedRecipe> shaped(RecipeCategory recipeCategory, ItemLike item, int count) {
        return new ExtendedShapedRecipeBuilder.Basic<>(recipeCategory, new ItemStack(item, count), ShapedRecipe::new);
    }

    private void special(RecipeOutput consumer, RecipeSerializer<? extends CraftingRecipe> serializer, Function<CraftingBookCategory, Recipe<?>> factory) {
        SpecialRecipeBuilder.special(factory).save(consumer, NameUtils.fromRecipeSerializer(serializer).toString());
    }

    @SuppressWarnings("MethodWithTooManyParameters")
    private void toolRecipes(RecipeOutput consumer, String name, int mainCount, GearItemSet<?> itemSet) {
        // Tool head
        shapelessPart(RecipeCategory.TOOLS, itemSet.mainPart())
                .requires(BlueprintIngredient.of(itemSet))
                .requires(PartMaterialIngredient.of(PartTypes.MAIN.get(), GearTypes.TOOL.get()), mainCount)
                .save(consumer, SilentGear.getId("gear/" + name + "_head"));
        // Tool from head and rod
        shapelessGear(RecipeCategory.TOOLS, itemSet.gearItem())
                .requires(itemSet.mainPart())
                .requires(GearPartIngredient.of(PartTypes.ROD.get()))
                .save(consumer, SilentGear.getId("gear/" + name));
        // Quick tool (mains and rods, skipping head)
        shapelessGear(RecipeCategory.TOOLS, itemSet.gearItem())
                .requires(BlueprintIngredient.of(itemSet))
                .requires(PartMaterialIngredient.of(PartTypes.MAIN.get(), GearTypes.TOOL.get()), mainCount)
                .requires(GearPartIngredient.of(PartTypes.ROD.get()))
                .save(consumer, SilentGear.getId("gear/" + name + "_quick"));
    }

    @SuppressWarnings("MethodWithTooManyParameters")
    private void bowRecipes(RecipeOutput consumer, String name, int mainCount, GearItemSet<?> itemSet) {
        // Main part
        shapelessPart(RecipeCategory.COMBAT, itemSet.mainPart())
                .requires(BlueprintIngredient.of(itemSet))
                .requires(PartMaterialIngredient.of(PartTypes.MAIN.get(), GearTypes.TOOL.get()), mainCount)
                .save(consumer, SilentGear.getId("gear/" + name + "_main"));
        // Tool from main, rod, and cord
        shapelessGear(RecipeCategory.COMBAT, itemSet.gearItem())
                .requires(itemSet.mainPart())
                .requires(GearPartIngredient.of(PartTypes.ROD.get()))
                .requires(GearPartIngredient.of(PartTypes.CORD.get()))
                .save(consumer, SilentGear.getId("gear/" + name));
        // Quick tool (main materials, rod, and cord, skipping main part)
        shapelessGear(RecipeCategory.COMBAT, itemSet.gearItem())
                .requires(BlueprintIngredient.of(itemSet))
                .requires(PartMaterialIngredient.of(PartTypes.MAIN.get(), GearTypes.TOOL.get()), mainCount)
                .requires(GearPartIngredient.of(PartTypes.ROD.get()))
                .requires(GearPartIngredient.of(PartTypes.CORD.get()))
                .save(consumer, SilentGear.getId("gear/" + name + "_quick"));
    }

    private void arrowRecipes(RecipeOutput consumer, String name, GearItemSet<?> itemSet) {
        BlueprintIngredient blueprint = BlueprintIngredient.of(itemSet);
        // Arrow head
        shapelessPart(RecipeCategory.COMBAT, itemSet.mainPart())
                .requires(blueprint)
                .requires(PartMaterialIngredient.of(PartTypes.MAIN.get(), GearTypes.PROJECTILE.get()))
                .save(consumer, SilentGear.getId("gear/" + name + "_head"));
        // Arrows from head
        shapelessGear(RecipeCategory.COMBAT, itemSet.gearItem())
                .requires(itemSet.mainPart())
                .requires(GearPartIngredient.of(PartTypes.ROD.get()))
                .requires(GearPartIngredient.of(PartTypes.FLETCHING.get()))
                .save(consumer, SilentGear.getId("gear/" + name));
        // Quick arrows
        shapelessGear(RecipeCategory.COMBAT, itemSet.gearItem())
                .requires(BlueprintIngredient.of(itemSet))
                .requires(PartMaterialIngredient.of(PartTypes.MAIN.get(), GearTypes.TOOL.get()))
                .requires(GearPartIngredient.of(PartTypes.ROD.get()))
                .requires(GearPartIngredient.of(PartTypes.FLETCHING.get()))
                .save(consumer, SilentGear.getId("gear/" + name + "_quick"));
    }

    private void armorRecipes(RecipeOutput consumer, int mainCount, GearItemSet<? extends GearArmorItem> itemSet) {
        shapelessPart(RecipeCategory.COMBAT, itemSet.mainPart())
                .requires(BlueprintIngredient.of(itemSet))
                .requires(PartMaterialIngredient.of(PartTypes.MAIN.get(), itemSet.gearItem().getGearType()), mainCount)
                .save(consumer, SilentGear.getId("gear/" + NameUtils.fromItem(itemSet.mainPart()).getPath()));

        shapelessGear(RecipeCategory.COMBAT, itemSet.gearItem())
                .requires(itemSet.mainPart())
                .save(consumer, SilentGear.getId("gear/" + NameUtils.fromItem(itemSet.gearItem()).getPath()));

        shapelessGear(RecipeCategory.COMBAT, itemSet.gearItem())
                .requires(itemSet.mainPart())
                .requires(GearPartIngredient.of(PartTypes.LINING.get()))
                .save(consumer, SilentGear.getId("gear/" + NameUtils.fromItem(itemSet.gearItem()).getPath() + "_with_lining"));
    }

    private void curioRecipes(RecipeOutput consumer, String name, int mainCount, GearItemSet<?> itemSet) {
        shapelessPart(RecipeCategory.MISC, itemSet.mainPart())
                .requires(BlueprintIngredient.of(itemSet))
                .requires(PartMaterialIngredient.of(PartTypes.MAIN.get(), GearTypes.CURIO.get(), MaterialCategories.METAL), mainCount)
                .save(consumer, SilentGear.getId("gear/" + name + "_main_only"));

        shapelessGear(RecipeCategory.MISC, itemSet.gearItem())
                .requires(BlueprintIngredient.of(SgItems.JEWELER_TOOLS.get()))
                .requires(itemSet.mainPart())
                .save(consumer, SilentGear.getId("gear/" + name));

        shapelessGear(RecipeCategory.MISC, itemSet.gearItem())
                .requires(BlueprintIngredient.of(SgItems.JEWELER_TOOLS.get()))
                .requires(itemSet.mainPart())
                .requires(GearPartIngredient.of(PartTypes.SETTING.get()))
                .save(consumer, SilentGear.getId("gear/" + name + "_with_gem"));

        shapelessGear(RecipeCategory.MISC, itemSet.gearItem())
                .requires(BlueprintIngredient.of(itemSet))
                .requires(PartMaterialIngredient.of(PartTypes.MAIN.get(), GearTypes.CURIO.get(), MaterialCategories.METAL), mainCount)
                .requires(GearPartIngredient.of(PartTypes.SETTING.get()))
                .save(consumer, SilentGear.getId("gear/" + name + "_quick"));
    }

    private void toolBlueprint(RecipeOutput consumer, String group, GearItemSet<?> itemSet, String... pattern) {
        toolBlueprint(consumer, group, itemSet, Ingredient.EMPTY, pattern);
    }

    private void toolBlueprint(RecipeOutput consumer, String group, GearItemSet<?> itemSet, Ingredient extra, String... pattern) {
        ShapedRecipeBuilder builderBlueprint = ShapedRecipeBuilder.shaped(RecipeCategory.MISC, itemSet.blueprint())
                .group("silentgear:blueprints/" + group)
                .define('#', SgTags.Items.BLUEPRINT_PAPER)
                .define('/', Tags.Items.RODS_WOODEN)
                .unlockedBy("has_item", has(SgTags.Items.BLUEPRINT_PAPER));

        ShapedRecipeBuilder builderTemplate = ShapedRecipeBuilder.shaped(RecipeCategory.MISC, itemSet.template())
                .group("silentgear:blueprints/" + group)
                .define('#', SgTags.Items.TEMPLATE_BOARDS)
                .define('/', Tags.Items.RODS_WOODEN)
                .unlockedBy("has_item", has(SgTags.Items.TEMPLATE_BOARDS));

        if (extra != Ingredient.EMPTY) {
            builderBlueprint.define('@', extra);
            builderTemplate.define('@', extra);
        }

        for (String line : pattern) {
            builderBlueprint.pattern(line);
            builderTemplate.pattern(line);
        }

        builderBlueprint.save(consumer);
        builderTemplate.save(consumer);
    }

    private void armorBlueprint(RecipeOutput consumer, String group, GearItemSet<?> itemSet, String... pattern) {
        ShapedRecipeBuilder builderBlueprint = ShapedRecipeBuilder.shaped(RecipeCategory.MISC, itemSet.blueprint())
                .group("silentgear:blueprints/" + group)
                .define('#', SgTags.Items.BLUEPRINT_PAPER)
                .unlockedBy("has_item", has(SgTags.Items.BLUEPRINT_PAPER));
        for (String line : pattern) {
            builderBlueprint.pattern(line);
        }
        builderBlueprint.save(consumer);

        ShapedRecipeBuilder builderTemplate = ShapedRecipeBuilder.shaped(RecipeCategory.MISC, itemSet.template())
                .group("silentgear:blueprints/" + group)
                .define('#', SgTags.Items.TEMPLATE_BOARDS)
                .unlockedBy("has_item", has(SgTags.Items.TEMPLATE_BOARDS));
        for (String line : pattern) {
            builderTemplate.pattern(line);
        }
        builderTemplate.save(consumer);
    }

    private ShapelessConversionBuilder shapelessConversion(RecipeCategory category, GearItem result, List<PartInstance> parts) {
        return new ShapelessConversionBuilder(category, result, parts);
    }

    private static final Map<Tier, DataResource<Material>> TOOL_MATERIALS = ImmutableMap.<Tier, DataResource<Material>>builder()
            .put(Tiers.NETHERITE, Const.Materials.DIAMOND) // Yes, diamond is correct, this is for the main part
            .put(Tiers.DIAMOND, Const.Materials.DIAMOND)
            .put(Tiers.GOLD, Const.Materials.GOLD)
            .put(Tiers.IRON, Const.Materials.IRON)
            .put(Tiers.STONE, Const.Materials.STONE)
            .put(Tiers.WOOD, Const.Materials.WOOD)
            .build();
    private static final Map<Holder<ArmorMaterial>, DataResource<Material>> ARMOR_MATERIALS = ImmutableMap.<Holder<ArmorMaterial>, DataResource<Material>>builder()
            .put(ArmorMaterials.NETHERITE, Const.Materials.DIAMOND) // Again, this is correct (see TOOL_MATERIALS)
            .put(ArmorMaterials.DIAMOND, Const.Materials.DIAMOND)
            .put(ArmorMaterials.GOLD, Const.Materials.GOLD)
            .put(ArmorMaterials.IRON, Const.Materials.IRON)
            .put(ArmorMaterials.LEATHER, Const.Materials.LEATHER)
            .build();

    private void toolConversion(RecipeOutput consumer, GearItem result, DataResource<GearPart> mainPart, int mainCount, Item... toolItems) {
        for (Item input : toolItems) {
            assert input instanceof TieredItem;
            Tier tier = ((TieredItem) input).getTier();
            DataResource<Material> coating = tier == Tiers.NETHERITE ? Const.Materials.NETHERITE : null;
            shapelessConversion(RecipeCategory.MISC, result,
                    buildConversionParts(
                            result.getGearType(),
                            mainPart,
                            TOOL_MATERIALS.getOrDefault(tier, Const.Materials.EMERALD), mainCount,
                            Const.Materials.WOOD,
                            coating
                    )
            )
                    .requires(input)
                    .save(consumer, SilentGear.getId("gear/convert/" + NameUtils.fromItem(input).getPath()));
        }
    }

    private void armorConversion(RecipeOutput consumer, GearItem result, DataResource<GearPart> mainPart, int mainCount, Item... armorItems) {
        for (Item input : armorItems) {
            assert input instanceof ArmorItem;
            var armorMaterial = ((ArmorItem) input).getMaterial();
            DataResource<Material> coating = armorMaterial == ArmorMaterials.NETHERITE ? Const.Materials.NETHERITE : null;
            shapelessConversion(RecipeCategory.MISC, result,
                    buildConversionParts(
                            result.getGearType(),
                            mainPart,
                            ARMOR_MATERIALS.getOrDefault(armorMaterial, Const.Materials.EMERALD), mainCount,
                            null,
                            coating
                    )
            )
                    .requires(input)
                    .save(consumer, SilentGear.getId("gear/convert/" + NameUtils.fromItem(input).getPath()));
        }
    }

    private static void gearSalvage(RecipeOutput consumer, GearItem item) {
        SalvagingRecipeBuilder.gearBuilder(item)
                .save(consumer, SilentGear.getId("salvaging/gear/" + NameUtils.fromItem(item).getPath()));
    }

    private static void vanillaSalvage(RecipeOutput consumer, ItemLike gear, ItemLike main, int mainCount, int rodCount) {
        vanillaSalvage(consumer, gear, main, mainCount, rodCount, null);
    }

    private static void vanillaSalvage(RecipeOutput consumer, ItemLike gear, ItemLike main, int mainCount, int rodCount, @Nullable ItemLike secondary) {
        var builder = SalvagingRecipeBuilder.builder(gear).addResult(main, mainCount);
        if (secondary != null) {
            builder.addResult(secondary);
        }
        if (rodCount > 0) {
            builder.addResult(Items.STICK, rodCount);
        }
        ResourceLocation inputId = NameUtils.fromItem(gear);
        builder.save(consumer, SilentGear.getId("salvaging/" + inputId.getPath()));
    }

    private static List<PartInstance> buildConversionParts(
            GearType gearType,
            DataResource<GearPart> mainPart, DataResource<Material> main, int mainCount,
            @Nullable DataResource<Material> rod,
            @Nullable DataResource<Material> coating
    ) {
        List<PartInstance> ret = new ArrayList<>();
        List<MaterialInstance> mainMaterials = new ArrayList<>();
        for (int i = 0; i < mainCount; ++i) {
            mainMaterials.add(MaterialInstance.of(main));
        }

        ret.add(PartInstance.create(
                mainPart,
                PartTypes.MAIN.get().getCompoundPartItem(gearType).orElseThrow(),
                mainMaterials
        ));

        if (rod != null) {
            ret.add(PartInstance.create(
                    Const.Parts.ROD,
                    SgItems.ROD.get(),
                    rod
            ));
        }

        if (coating != null) {
            ret.add(PartInstance.create(
                    Const.Parts.COATING,
                    SgItems.COATING.get(),
                    coating
            ));
        }

        return ret;
    }

    private ToolActionRecipeBuilder toolAction(RecipeOutput recipeOutput, TagKey<Item> tool, ItemLike ingredient, int damageToTool, ItemLike result, int count) {
        return new ToolActionRecipeBuilder(Ingredient.of(tool), Ingredient.of(ingredient), damageToTool, new ItemStack(result, count));
    }

    private ToolActionRecipeBuilder toolAction(RecipeOutput recipeOutput, TagKey<Item> tool, TagKey<Item> ingredient, int damageToTool, ItemLike result, int count) {
        return new ToolActionRecipeBuilder(Ingredient.of(tool), Ingredient.of(ingredient), damageToTool, new ItemStack(result, count));
    }

    private void metals(RecipeOutput consumer, float smeltingXp, Metals metal) {
        if (metal.ore != null) {
            SimpleCookingRecipeBuilder.blasting(Ingredient.of(metal.oreTag), RecipeCategory.MISC, metal.ingot, smeltingXp, 100)
                    .unlockedBy("has_item", has(metal.oreTag))
                    .save(consumer, SilentGear.getId(metal.name + "_ore_blasting"));
            SimpleCookingRecipeBuilder.smelting(Ingredient.of(metal.oreTag), RecipeCategory.MISC, metal.ingot, smeltingXp, 200)
                    .unlockedBy("has_item", has(metal.oreTag))
                    .save(consumer, SilentGear.getId(metal.name + "_ore_smelting"));
        }

        if (metal.rawOre != null) {
            SimpleCookingRecipeBuilder.blasting(Ingredient.of(metal.rawOre), RecipeCategory.MISC, metal.ingot, smeltingXp, 100)
                    .unlockedBy("has_item", has(metal.rawOre))
                    .save(consumer, SilentGear.getId(metal.name + "_raw_ore_blasting"));
            SimpleCookingRecipeBuilder.smelting(Ingredient.of(metal.rawOre), RecipeCategory.MISC, metal.ingot, smeltingXp, 200)
                    .unlockedBy("has_item", has(metal.rawOre))
                    .save(consumer, SilentGear.getId(metal.name + "_raw_ore_smelting"));

            compressionRecipes(consumer, metal.rawOreBlock, metal.rawOre, null);
        }

        var hasIngot = has(metal.ingotTag);

        if (metal.block != null) {
            compressionRecipes(consumer, metal.block, metal.ingot, metal.nugget);
        }

        if (metal.dustTag != null) {
            Ingredient dust = Ingredient.of(metal.dustTag);
            SimpleCookingRecipeBuilder.blasting(dust, RecipeCategory.MISC, metal.ingot, smeltingXp, 100)
                    .unlockedBy("has_item", hasIngot)
                    .save(consumer, SilentGear.getId(metal.name + "_dust_blasting"));
            SimpleCookingRecipeBuilder.smelting(dust, RecipeCategory.MISC, metal.ingot, smeltingXp, 200)
                    .unlockedBy("has_item", hasIngot)
                    .save(consumer, SilentGear.getId(metal.name + "_dust_smelting"));
        }

        if (metal.dust != null) {
            toolAction(consumer, SgTags.Items.HAMMERS, metal.ingotTag, 1, metal.dust, 1)
                    .save(consumer);
        }
    }

    @SuppressWarnings("WeakerAccess")
    private static class Metals {
        private final String name;
        private ItemLike ore;
        private TagKey<Item> oreTag;
        private ItemLike rawOre;
        private ItemLike rawOreBlock;
        private ItemLike block;
        private TagKey<Item> blockTag;
        private final ItemLike ingot;
        private final TagKey<Item> ingotTag;
        private ItemLike nugget;
        private TagKey<Item> nuggetTag;
        private ItemLike dust;
        private TagKey<Item> dustTag;

        public Metals(String name, ItemLike ingot, TagKey<Item> ingotTag) {
            this.name = name;
            this.ingot = ingot;
            this.ingotTag = ingotTag;
        }

        public Metals ore(ItemLike item, TagKey<Item> tag) {
            this.ore = item;
            this.oreTag = tag;
            return this;
        }

        public Metals ore(ItemLike oreBlockItem, TagKey<Item> oreTag, ItemLike rawOre, ItemLike rawOreBlock) {
            this.ore = oreBlockItem;
            this.oreTag = oreTag;
            this.rawOre = rawOre;
            this.rawOreBlock = rawOreBlock;
            return this;
        }

        public Metals block(ItemLike item, TagKey<Item> tag) {
            this.block = item;
            this.blockTag = tag;
            return this;
        }

        public Metals nugget(ItemLike item, TagKey<Item> tag) {
            this.nugget = item;
            this.nuggetTag = tag;
            return this;
        }

        public Metals dust(ItemLike item, TagKey<Item> tag) {
            this.dust = item;
            this.dustTag = tag;
            return this;
        }
    }
}
