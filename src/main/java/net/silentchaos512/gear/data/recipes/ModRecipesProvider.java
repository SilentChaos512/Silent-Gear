package net.silentchaos512.gear.data.recipes;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.criterion.ImpossibleTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredItem;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearItem;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.material.Material;
import net.silentchaos512.gear.api.part.GearPart;
import net.silentchaos512.gear.api.part.MaterialGrade;
import net.silentchaos512.gear.api.util.DataResource;
import net.silentchaos512.gear.core.SoundPlayback;
import net.silentchaos512.gear.crafting.ingredient.BlueprintIngredient;
import net.silentchaos512.gear.crafting.ingredient.CustomAlloyIngredient;
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
import java.util.function.Function;

public class ModRecipesProvider extends LibRecipeProvider {
    private static final boolean ADD_TEST_RECIPES = false;
    private static final SoundPlayback HAMMER_SOUND = new SoundPlayback(SgSounds.STONE_ANVIL_HAMMER.get(), 1f, 1f, 0.1f);
    private static final SoundPlayback KNIFE_SOUND = new SoundPlayback(SgSounds.STONE_ANVIL_KNIFE.get(), 1f, 1f, 0.1f);

    public ModRecipesProvider(HolderLookup.Provider registries, RecipeOutput recipeOutput) {
        super(registries, recipeOutput, SilentGear.MOD_ID);
    }

    @Override
    protected void buildRecipes() {
        metals(0.5f, new Metals("blaze_gold", CraftingItems.BLAZE_GOLD_INGOT, SgTags.Items.INGOTS_BLAZE_GOLD)
                .block(SgBlocks.BLAZE_GOLD_BLOCK, SgTags.Items.STORAGE_BLOCKS_BLAZE_GOLD)
                .dust(CraftingItems.BLAZE_GOLD_DUST, SgTags.Items.DUSTS_BLAZE_GOLD)
                .nugget(CraftingItems.BLAZE_GOLD_NUGGET, SgTags.Items.NUGGETS_BLAZE_GOLD));
        metals(1.0f, new Metals("crimson_iron", CraftingItems.CRIMSON_IRON_INGOT, SgTags.Items.INGOTS_CRIMSON_IRON)
                .block(SgBlocks.CRIMSON_IRON_BLOCK, SgTags.Items.STORAGE_BLOCKS_CRIMSON_IRON)
                .dust(CraftingItems.CRIMSON_IRON_DUST, SgTags.Items.DUSTS_CRIMSON_IRON)
                .ore(SgBlocks.CRIMSON_IRON_ORE, SgTags.Items.ORES_CRIMSON_IRON, CraftingItems.RAW_CRIMSON_IRON, SgBlocks.RAW_CRIMSON_IRON_BLOCK)
                .nugget(CraftingItems.CRIMSON_IRON_NUGGET, SgTags.Items.NUGGETS_CRIMSON_IRON));
        metals(0.5f, new Metals("crimson_steel", CraftingItems.CRIMSON_STEEL_INGOT, SgTags.Items.INGOTS_CRIMSON_STEEL)
                .block(SgBlocks.CRIMSON_STEEL_BLOCK, SgTags.Items.STORAGE_BLOCKS_CRIMSON_STEEL)
                .dust(CraftingItems.CRIMSON_STEEL_DUST, SgTags.Items.DUSTS_CRIMSON_STEEL)
                .nugget(CraftingItems.CRIMSON_STEEL_NUGGET, SgTags.Items.NUGGETS_CRIMSON_STEEL));
        metals(1.5f, new Metals("azure_silver", CraftingItems.AZURE_SILVER_INGOT, SgTags.Items.INGOTS_AZURE_SILVER)
                .block(SgBlocks.AZURE_SILVER_BLOCK, SgTags.Items.STORAGE_BLOCKS_AZURE_SILVER)
                .dust(CraftingItems.AZURE_SILVER_DUST, SgTags.Items.DUSTS_AZURE_SILVER)
                .ore(SgBlocks.AZURE_SILVER_ORE, SgTags.Items.ORES_AZURE_SILVER, CraftingItems.RAW_AZURE_SILVER, SgBlocks.RAW_AZURE_SILVER_BLOCK)
                .nugget(CraftingItems.AZURE_SILVER_NUGGET, SgTags.Items.NUGGETS_AZURE_SILVER));
        metals(0.5f, new Metals("azure_electrum", CraftingItems.AZURE_ELECTRUM_INGOT, SgTags.Items.INGOTS_AZURE_ELECTRUM)
                .block(SgBlocks.AZURE_ELECTRUM_BLOCK, SgTags.Items.STORAGE_BLOCKS_AZURE_ELECTRUM)
                .dust(CraftingItems.AZURE_ELECTRUM_DUST, SgTags.Items.DUSTS_AZURE_ELECTRUM)
                .nugget(CraftingItems.AZURE_ELECTRUM_NUGGET, SgTags.Items.NUGGETS_AZURE_ELECTRUM));
        metals(0.75f, new Metals("tyrian_steel", CraftingItems.TYRIAN_STEEL_INGOT, SgTags.Items.INGOTS_TYRIAN_STEEL)
                .block(SgBlocks.TYRIAN_STEEL_BLOCK, SgTags.Items.STORAGE_BLOCKS_TYRIAN_STEEL)
                .dust(CraftingItems.TYRIAN_STEEL_DUST, SgTags.Items.DUSTS_TYRIAN_STEEL)
                .nugget(CraftingItems.TYRIAN_STEEL_NUGGET, SgTags.Items.NUGGETS_TYRIAN_STEEL));
        metals(1.0f, new Metals("bort", CraftingItems.BORT, SgTags.Items.GEMS_BORT)
                .block(SgBlocks.BORT_BLOCK, SgTags.Items.STORAGE_BLOCKS_BORT));

        registerSpecialRecipes();
        registerBooks();
        registerCrudeTools();
        registerBlueprints();
        registerCompoundParts();
        registerGear();
        registerModifierKits();
        registerMachines();
        registerCompounding();
        registerPressing();
        registerSmithing();
        registerSalvaging();
        registerCraftingItems();

        if (ADD_TEST_RECIPES) {
            registerTestRecipes();
        }
    }

    private void registerTestRecipes() {
        shaped(RecipeCategory.MISC, Items.BUCKET)
                .pattern("# #")
                .pattern(" # ")
                .define('#', new Ingredient(PartMaterialIngredient.builder(PartTypes.MAIN.get())
                        .withMaterial(DataResource.material("copper"))
                        .withGrade(MaterialGrade.A, null).build()
                ))
                .unlockedBy("impossible", CriteriaTriggers.IMPOSSIBLE.createCriterion(new ImpossibleTrigger.TriggerInstance()))
                .save(this.output, modId("graded_mat_test"));
    }

    private void registerSpecialRecipes() {
        special(this.output, SgRecipes.FILL_REPAIR_KIT.get(), FillRepairKitRecipe::new);
        special(this.output, SgRecipes.SWAP_GEAR_PART.get(), GearPartSwapRecipe::new);
        special(this.output, SgRecipes.QUICK_REPAIR.get(), QuickRepairRecipe::new);
        special(this.output, SgRecipes.MOD_KIT_REMOVE_PART.get(), ModKitRemovePartRecipe::new);
    }

    private void registerBooks() {
        shapeless(RecipeCategory.MISC, SgItems.MATERIAL_BOOK)
                .requires(Items.BOOK)
                .requires(SgTags.Items.BLUEPRINT_PAPER)
                .unlockedBy("has_item", has(SgTags.Items.BLUEPRINT_PAPER))
                .save(this.output);
    }

    private void registerBlueprints() {
        toolBlueprint("sword", GearItemSets.SWORD, "#", "#", "/");
        toolBlueprint("katana", GearItemSets.KATANA, "##", "# ", "/ ");
        toolBlueprint("machete", GearItemSets.MACHETE, "  #", " ##", "/  ");
        toolBlueprint("spear", GearItemSets.SPEAR, "#  ", " / ", "  /");
        toolBlueprint("knife", GearItemSets.KNIFE, " #", "/ ");
        toolBlueprint("dagger", GearItemSets.DAGGER, "#", "/");
        toolBlueprint("pickaxe", GearItemSets.PICKAXE, "###", " / ", " / ");
        toolBlueprint("shovel", GearItemSets.SHOVEL, "#", "/", "/");
        toolBlueprint("axe", GearItemSets.AXE, "##", "#/", " /");
        toolBlueprint("paxel", GearItemSets.PAXEL, "###", "#/#", " /#");
        toolBlueprint("hammer", GearItemSets.HAMMER, "###", "###", " / ");
        toolBlueprint("excavator", GearItemSets.EXCAVATOR, "# #", "###", " / ");
        toolBlueprint("saw", GearItemSets.SAW, "###", "##/", "  /");
        toolBlueprint("hoe", GearItemSets.HOE, "##", " /", " /");
        toolBlueprint("mattock", GearItemSets.MATTOCK, "## ", "#/#", " / ");
        toolBlueprint("prospector_hammer", GearItemSets.PROSPECTOR_HAMMER,
                tag(Tags.Items.INGOTS_IRON), "##", " /", " @");
        toolBlueprint("sickle", GearItemSets.SICKLE, " #", "##", "/ ");
        toolBlueprint("shears", GearItemSets.SHEARS, " #", "#/");
        toolBlueprint("fishing_rod", GearItemSets.FISHING_ROD, "  /", " /#", "/ #");
        toolBlueprint("bow", GearItemSets.BOW, " #/", "# /", " #/");
        toolBlueprint("crossbow", GearItemSets.CROSSBOW, "/#/", "###", " / ");
        toolBlueprint("slingshot", GearItemSets.SLINGSHOT, "# #", " / ", " / ");
        toolBlueprint("shield", GearItemSets.SHIELD, tag(Tags.Items.INGOTS_IRON), "#@#", "///", " # ");
        toolBlueprint("arrow", GearItemSets.ARROW, tag(Tags.Items.FEATHERS), "#", "/", "@");
        armorBlueprint("helmet", GearItemSets.HELMET, "###", "# #");
        armorBlueprint("chestplate", GearItemSets.CHESTPLATE, "# #", "###", "###");
        armorBlueprint("leggings", GearItemSets.LEGGINGS, "###", "# #", "# #");
        armorBlueprint("boots", GearItemSets.BOOTS, "# #", "# #");

        shapedExt(RecipeCategory.MISC, GearItemSets.TRIDENT.blueprint())
                .define('#', SgTags.Items.BLUEPRINT_PAPER)
                .define('H', Items.HEART_OF_THE_SEA)
                .define('T', Items.TRIDENT)
                .pattern("#H#")
                .pattern("#T#")
                .pattern(" # ")
                .unlockedBy("has_item", has(Items.TRIDENT))
                .save(this.output);

        shapedExt(RecipeCategory.MISC, GearItemSets.MACE.blueprint())
                .define('#', SgTags.Items.BLUEPRINT_PAPER)
                .define('/', Tags.Items.RODS_BREEZE)
                .pattern(" # ")
                .pattern("###")
                .pattern(" / ")
                .unlockedBy("has_item", has(Tags.Items.RODS_BREEZE))
                .save(this.output);
        shapedExt(RecipeCategory.MISC, GearItemSets.MACE.template())
                .define('#', SgTags.Items.TEMPLATE_BOARDS)
                .define('/', Tags.Items.RODS_BREEZE)
                .pattern(" # ")
                .pattern("###")
                .pattern(" / ")
                .unlockedBy("has_item", has(Tags.Items.RODS_BREEZE))
                .save(this.output);

        shapedExt(RecipeCategory.MISC, GearItemSets.ELYTRA.blueprint())
                .define('#', SgTags.Items.BLUEPRINT_PAPER)
                .define('/', Tags.Items.INGOTS_GOLD)
                .define('e', Items.ELYTRA)
                .define('p', Items.PHANTOM_MEMBRANE)
                .pattern("/e/")
                .pattern("p#p")
                .pattern("p p")
                .unlockedBy("has_item", has(SgTags.Items.BLUEPRINT_PAPER))
                .save(this.output);
        shapedExt(RecipeCategory.MISC, GearItemSets.ELYTRA.template())
                .define('#', SgTags.Items.TEMPLATE_BOARDS)
                .define('/', Tags.Items.INGOTS_GOLD)
                .define('e', Items.ELYTRA)
                .define('p', Items.PHANTOM_MEMBRANE)
                .pattern("/e/")
                .pattern("p#p")
                .pattern("p p")
                .unlockedBy("has_item", has(SgTags.Items.TEMPLATE_BOARDS))
                .save(this.output);

        // Curio blueprints
        shapedExt(RecipeCategory.MISC, GearItemSets.RING.blueprint())
                .group("silentgear:blueprints/ring")
                .define('#', SgTags.Items.BLUEPRINT_PAPER)
                .define('/', PartMaterialIngredient.of(PartTypes.MAIN.get(), GearTypes.CURIO.get(), MaterialCategories.METAL))
                .pattern(" #/")
                .pattern("# #")
                .pattern("/# ")
                .unlockedBy("has_item", has(SgTags.Items.BLUEPRINT_PAPER))
                .save(this.output);
        shapedExt(RecipeCategory.MISC, GearItemSets.RING.blueprint())
                .group("silentgear:blueprints/ring")
                .define('#', SgTags.Items.BLUEPRINT_PAPER)
                .define('/', Tags.Items.INGOTS_GOLD)
                .pattern(" #/")
                .pattern("# #")
                .pattern("/# ")
                .unlockedBy("has_item", has(SgTags.Items.BLUEPRINT_PAPER))
                .save(this.output, modId("ring_blueprint_alt"));
        shapedExt(RecipeCategory.MISC, GearItemSets.RING.template())
                .group("silentgear:blueprints/ring")
                .define('#', SgTags.Items.TEMPLATE_BOARDS)
                .define('/', PartMaterialIngredient.of(PartTypes.MAIN.get(), GearTypes.CURIO.get(), MaterialCategories.METAL))
                .pattern(" #/")
                .pattern("# #")
                .pattern("/# ")
                .unlockedBy("has_item", has(SgTags.Items.TEMPLATE_BOARDS))
                .save(this.output);

        shapedExt(RecipeCategory.MISC, GearItemSets.BRACELET.blueprint())
                .group("silentgear:blueprints/bracelet")
                .define('#', SgTags.Items.BLUEPRINT_PAPER)
                .define('/', PartMaterialIngredient.of(PartTypes.MAIN.get(), GearTypes.CURIO.get(), MaterialCategories.METAL))
                .pattern("###")
                .pattern("/#/")
                .unlockedBy("has_item", has(SgTags.Items.BLUEPRINT_PAPER))
                .save(this.output);
        shapedExt(RecipeCategory.MISC, GearItemSets.BRACELET.blueprint())
                .group("silentgear:blueprints/bracelet")
                .define('#', SgTags.Items.BLUEPRINT_PAPER)
                .define('/', Tags.Items.INGOTS_GOLD)
                .pattern("###")
                .pattern("/#/")
                .unlockedBy("has_item", has(SgTags.Items.BLUEPRINT_PAPER))
                .save(this.output, modId("bracelet_blueprint_alt"));
        shapedExt(RecipeCategory.MISC, GearItemSets.BRACELET.template())
                .group("silentgear:blueprints/bracelet")
                .define('#', SgTags.Items.TEMPLATE_BOARDS)
                .define('/', PartMaterialIngredient.of(PartTypes.MAIN.get(), GearTypes.CURIO.get(), MaterialCategories.METAL))
                .pattern("###")
                .pattern("/#/")
                .unlockedBy("has_item", has(SgTags.Items.TEMPLATE_BOARDS))
                .save(this.output);

        shapedExt(RecipeCategory.MISC, GearItemSets.NECKLACE.blueprint())
                .group("silentgear:blueprints/necklace")
                .define('#', SgTags.Items.BLUEPRINT_PAPER)
                .define('/', PartMaterialIngredient.of(PartTypes.MAIN.get(), GearTypes.CURIO.get(), MaterialCategories.METAL))
                .pattern("/#/")
                .pattern("# #")
                .pattern("###")
                .unlockedBy("has_item", has(SgTags.Items.BLUEPRINT_PAPER))
                .save(this.output);
        shapedExt(RecipeCategory.MISC, GearItemSets.NECKLACE.blueprint())
                .group("silentgear:blueprints/necklace")
                .define('#', SgTags.Items.BLUEPRINT_PAPER)
                .define('/', Tags.Items.INGOTS_GOLD)
                .pattern("/#/")
                .pattern("# #")
                .pattern("###")
                .unlockedBy("has_item", has(SgTags.Items.BLUEPRINT_PAPER))
                .save(this.output, modId("necklace_blueprint_alt"));
        shapedExt(RecipeCategory.MISC, GearItemSets.NECKLACE.template())
                .group("silentgear:blueprints/necklace")
                .define('#', SgTags.Items.TEMPLATE_BOARDS)
                .define('/', PartMaterialIngredient.of(PartTypes.MAIN.get(), GearTypes.CURIO.get(), MaterialCategories.METAL))
                .pattern("/#/")
                .pattern("# #")
                .pattern("###")
                .unlockedBy("has_item", has(SgTags.Items.TEMPLATE_BOARDS))
                .save(this.output);

        // Part blueprints
        shapedExt(RecipeCategory.MISC, SgItems.JEWELER_TOOLS)
                .pattern("  p")
                .pattern("d#s")
                .pattern("ips")
                .define('p', ItemTags.PLANKS)
                .define('d', Tags.Items.GEMS_DIAMOND)
                .define('#', SgTags.Items.BLUEPRINT_PAPER)
                .define('s', Tags.Items.RODS_WOODEN)
                .define('i', Tags.Items.INGOTS_IRON)
                .unlockedBy("has_item", has(SgTags.Items.BLUEPRINT_PAPER))
                .save(this.output);

        shapelessExt(RecipeCategory.MISC, SgItems.BINDING_BLUEPRINT)
                .group("silentgear:blueprints/binding")
                .requires(tag(SgTags.Items.BLUEPRINT_PAPER), 1)
                .requires(PartMaterialIngredient.of(PartTypes.BINDING.get(), GearTypes.TOOL.get()), 2)
                .unlockedBy("has_item", has(SgTags.Items.BLUEPRINT_PAPER))
                .save(this.output);
        shapelessExt(RecipeCategory.MISC, SgItems.BINDING_BLUEPRINT)
                .group("silentgear:blueprints/binding")
                .requires(SgTags.Items.BLUEPRINT_PAPER)
                .requires(Tags.Items.STRINGS, 2)
                .unlockedBy("has_item", has(SgTags.Items.BLUEPRINT_PAPER))
                .save(this.output, modId("binding_blueprint_alt"));
        shapelessExt(RecipeCategory.MISC, SgItems.BINDING_TEMPLATE)
                .group("silentgear:blueprints/binding")
                .requires(tag(SgTags.Items.TEMPLATE_BOARDS), 1)
                .requires(PartMaterialIngredient.of(PartTypes.BINDING.get(), GearTypes.TOOL.get()), 2)
                .unlockedBy("has_item", has(SgTags.Items.TEMPLATE_BOARDS))
                .save(this.output);

        shapedExt(RecipeCategory.TOOLS, SgItems.CORD_BLUEPRINT)
                .group("silentgear:blueprints/cord")
                .define('#', SgTags.Items.BLUEPRINT_PAPER)
                .define('/', PartMaterialIngredient.of(PartTypes.CORD.get(), GearTypes.TOOL.get()))
                .pattern("#/")
                .pattern("#/")
                .pattern("#/")
                .unlockedBy("has_item", has(SgTags.Items.BLUEPRINT_PAPER))
                .save(this.output);
        shapedExt(RecipeCategory.TOOLS, SgItems.CORD_BLUEPRINT)
                .group("silentgear:blueprints/cord")
                .define('#', SgTags.Items.BLUEPRINT_PAPER)
                .define('/', Tags.Items.STRINGS)
                .pattern("#/")
                .pattern("#/")
                .pattern("#/")
                .unlockedBy("has_item", has(SgTags.Items.BLUEPRINT_PAPER))
                .save(this.output, modId("cord_blueprint_alt"));
        shapedExt(RecipeCategory.TOOLS, SgItems.CORD_TEMPLATE)
                .group("silentgear:blueprints/cord")
                .define('#', SgTags.Items.TEMPLATE_BOARDS)
                .define('/', PartMaterialIngredient.of(PartTypes.CORD.get(), GearTypes.TOOL.get()))
                .pattern("#/")
                .pattern("#/")
                .pattern("#/")
                .unlockedBy("has_item", has(SgTags.Items.TEMPLATE_BOARDS))
                .save(this.output);

        shapelessExt(RecipeCategory.TOOLS, SgItems.FLETCHING_BLUEPRINT)
                .group("silentgear:blueprints/fletching")
                .requires(tag(SgTags.Items.BLUEPRINT_PAPER), 2)
                .requires(Tags.Items.FEATHERS)
                .unlockedBy("has_item", has(SgTags.Items.BLUEPRINT_PAPER))
                .save(this.output);
        shapelessExt(RecipeCategory.TOOLS, SgItems.FLETCHING_TEMPLATE)
                .group("silentgear:blueprints/fletching")
                .requires(tag(SgTags.Items.TEMPLATE_BOARDS), 2)
                .requires(Tags.Items.FEATHERS)
                .unlockedBy("has_item", has(SgTags.Items.TEMPLATE_BOARDS))
                .save(this.output);

        shapelessExt(RecipeCategory.TOOLS, SgItems.GRIP_BLUEPRINT)
                .group("silentgear:blueprints/grip")
                .requires(tag(SgTags.Items.BLUEPRINT_PAPER), 2)
                .requires(PartMaterialIngredient.of(PartTypes.GRIP.get(), GearTypes.TOOL.get()))
                .unlockedBy("has_item", has(SgTags.Items.BLUEPRINT_PAPER))
                .save(this.output);
        shapelessExt(RecipeCategory.TOOLS, SgItems.GRIP_BLUEPRINT)
                .group("silentgear:blueprints/grip")
                .requires(tag(SgTags.Items.BLUEPRINT_PAPER), 2)
                .requires(ItemTags.WOOL)
                .unlockedBy("has_item", has(SgTags.Items.BLUEPRINT_PAPER))
                .save(this.output, modId("grip_blueprint_alt"));
        shapelessExt(RecipeCategory.TOOLS, SgItems.GRIP_TEMPLATE)
                .group("silentgear:blueprints/grip")
                .requires(tag(SgTags.Items.TEMPLATE_BOARDS), 2)
                .requires(PartMaterialIngredient.of(PartTypes.GRIP.get(), GearTypes.TOOL.get()))
                .unlockedBy("has_item", has(SgTags.Items.TEMPLATE_BOARDS))
                .save(this.output);

        shapeless(RecipeCategory.TOOLS, SgItems.LINING_BLUEPRINT)
                .group("silentgear:blueprints/lining")
                .requires(tag(SgTags.Items.BLUEPRINT_PAPER), 3)
                .requires(tag(ItemTags.WOOL), 2)
                .requires(tag(Tags.Items.STRINGS), 2)
                .unlockedBy("has_item", has(SgTags.Items.BLUEPRINT_PAPER))
                .save(this.output);
        shapeless(RecipeCategory.TOOLS, SgItems.LINING_TEMPLATE)
                .group("silentgear:blueprints/lining")
                .requires(tag(SgTags.Items.TEMPLATE_BOARDS), 3)
                .requires(tag(ItemTags.WOOL), 2)
                .requires(tag(Tags.Items.STRINGS), 2)
                .unlockedBy("has_item", has(SgTags.Items.TEMPLATE_BOARDS))
                .save(this.output);

        shaped(RecipeCategory.TOOLS, SgItems.ROD_BLUEPRINT)
                .group("silentgear:blueprints/rod")
                .define('#', SgTags.Items.BLUEPRINT_PAPER)
                .define('/', Tags.Items.RODS_WOODEN)
                .pattern("#/")
                .pattern("#/")
                .unlockedBy("has_item", has(SgTags.Items.BLUEPRINT_PAPER))
                .save(this.output);
        shaped(RecipeCategory.TOOLS, SgItems.ROD_TEMPLATE)
                .group("silentgear:blueprints/rod")
                .define('#', SgTags.Items.TEMPLATE_BOARDS)
                .define('/', Tags.Items.RODS_WOODEN)
                .pattern("#/")
                .pattern("#/")
                .unlockedBy("has_item", has(SgTags.Items.TEMPLATE_BOARDS))
                .save(this.output);

        shapeless(RecipeCategory.TOOLS, SgItems.TIP_BLUEPRINT)
                .group("silentgear:blueprints/tip")
                .requires(tag(SgTags.Items.BLUEPRINT_PAPER), 2)
                .requires(SgTags.Items.PAPER)
                .requires(Tags.Items.STONES)
                .unlockedBy("has_item", has(SgTags.Items.BLUEPRINT_PAPER))
                .save(this.output);
        shapeless(RecipeCategory.TOOLS, SgItems.TIP_TEMPLATE)
                .group("silentgear:blueprints/tip")
                .requires(tag(SgTags.Items.TEMPLATE_BOARDS), 2)
                .requires(SgTags.Items.PAPER)
                .requires(Tags.Items.STONES)
                .unlockedBy("has_item", has(SgTags.Items.TEMPLATE_BOARDS))
                .save(this.output);

        shapeless(RecipeCategory.TOOLS, SgItems.COATING_BLUEPRINT)
                .group("silentgear:blueprints/coating")
                .requires(tag(SgTags.Items.BLUEPRINT_PAPER), 4)
                .requires(Tags.Items.GEMS_DIAMOND)
                .requires(Tags.Items.GEMS_EMERALD)
                .unlockedBy("has_item", has(SgTags.Items.BLUEPRINT_PAPER))
                .save(this.output);
        shapeless(RecipeCategory.TOOLS, SgItems.COATING_TEMPLATE)
                .group("silentgear:blueprints/coating")
                .requires(tag(SgTags.Items.TEMPLATE_BOARDS), 4)
                .requires(Tags.Items.GEMS_DIAMOND)
                .requires(Tags.Items.GEMS_EMERALD)
                .unlockedBy("has_item", has(SgTags.Items.TEMPLATE_BOARDS))
                .save(this.output);

        shapeless(RecipeCategory.MISC, SgItems.BLUEPRINT_BOOK)
                .requires(Items.BOOK)
                .requires(ItemTags.WOOL)
                .requires(Tags.Items.INGOTS_GOLD)
                .requires(tag(SgTags.Items.TEMPLATE_BOARDS), 3)
                .unlockedBy("has_item", has(SgTags.Items.TEMPLATE_BOARDS))
                .save(this.output);
    }

    private ExtendedShapelessRecipeBuilder.Basic<ShapelessCompoundPartRecipe> compoundPart(DeferredItem<?> item, int count) {
        return compoundPart(RecipeCategory.MISC, item, count);
    }

    private ExtendedShapelessRecipeBuilder.Basic<ShapelessCompoundPartRecipe> compoundPart(RecipeCategory category, MainPartItem mainPartItem, int count) {
        var resultStack = new ItemStack(mainPartItem, count);
        return new ExtendedShapelessRecipeBuilder.Basic<>(this.items, category, resultStack, ShapelessCompoundPartRecipe::new);
    }

    private ExtendedShapelessRecipeBuilder.Basic<ShapelessCompoundPartRecipe> compoundPart(RecipeCategory category, DeferredItem<?> item, int count) {
        return new ExtendedShapelessRecipeBuilder.Basic<>(this.items, category, item.toStack(count), ShapelessCompoundPartRecipe::new);
    }

    private void registerCompoundParts() {
        compoundPart(SgItems.SETTING, 1)
                .requires(BlueprintIngredient.of(SgItems.JEWELER_TOOLS.get()))
                .requires(CraftingItems.BORT)
                .requires(PartMaterialIngredient.of(PartTypes.SETTING.get()))
                .save(this.output, modId("part/adornment"));

        compoundPart(SgItems.ROD, 4)
                .requires(BlueprintIngredient.of(SgItems.ROD_BLUEPRINT.get()))
                .requires(PartMaterialIngredient.of(PartTypes.ROD.get()), 2)
                .save(this.output, modId("part/rod"));

        compoundPart(SgItems.BINDING, 1)
                .requires(BlueprintIngredient.of(SgItems.BINDING_BLUEPRINT.get()))
                .requires(PartMaterialIngredient.of(PartTypes.BINDING.get()))
                .save(this.output, modId("part/binding"));

        compoundPart(SgItems.CORD, 1)
                .requires(BlueprintIngredient.of(SgItems.CORD_BLUEPRINT.get()))
                .requires(PartMaterialIngredient.of(PartTypes.CORD.get()), 3)
                .save(this.output, modId("part/cord"));

        compoundPart(SgItems.FLETCHING, 1)
                .requires(BlueprintIngredient.of(SgItems.FLETCHING_BLUEPRINT.get()))
                .requires(PartMaterialIngredient.of(PartTypes.FLETCHING.get()), 1)
                .save(this.output, modId("part/fletching"));

        compoundPart(SgItems.GRIP, 1)
                .requires(BlueprintIngredient.of(SgItems.GRIP_BLUEPRINT.get()))
                .requires(PartMaterialIngredient.of(PartTypes.GRIP.get()))
                .save(this.output, modId("part/grip"));

        compoundPart(SgItems.LINING, 1)
                .requires(BlueprintIngredient.of(SgItems.LINING_BLUEPRINT.get()))
                .requires(PartMaterialIngredient.of(PartTypes.LINING.get()))
                .save(this.output, modId("part/lining"));

        compoundPart(SgItems.TIP, 1)
                .requires(BlueprintIngredient.of(SgItems.TIP_BLUEPRINT.get()))
                .requires(PartMaterialIngredient.of(PartTypes.TIP.get()))
                .save(this.output, modId("part/tip"));

        compoundPart(SgItems.COATING, 1)
                .requires(BlueprintIngredient.of(SgItems.COATING_BLUEPRINT.get()))
                .requires(Items.GLASS_BOTTLE)
                .requires(PartMaterialIngredient.of(PartTypes.COATING.get()))
                .save(this.output, modId("part/coating"));
    }

    private ExtendedShapelessRecipeBuilder.Basic<ShapelessGearRecipe> shapelessGear(RecipeCategory category, DeferredItem<?> item) {
        return new ExtendedShapelessRecipeBuilder.Basic<>(this.items, category, item.toStack(), ShapelessGearRecipe::new);
    }

    private ExtendedShapelessRecipeBuilder.Basic<ShapelessGearRecipe> shapelessGear(RecipeCategory category, ItemLike item) {
        return new ExtendedShapelessRecipeBuilder.Basic<>(this.items, category, new ItemStack(item), ShapelessGearRecipe::new);
    }

    private ExtendedShapedRecipeBuilder.Basic<ShapedGearRecipe> shapedGear(RecipeCategory category, DeferredItem<?> item) {
        return new ExtendedShapedRecipeBuilder.Basic<>(this.items, category, item.toStack(), ShapedGearRecipe::new);
    }

    private ExtendedShapedRecipeBuilder.Basic<ShapedGearRecipe> shapedGear(RecipeCategory category, ItemLike item) {
        return new ExtendedShapedRecipeBuilder.Basic<>(this.items, category, new ItemStack(item), ShapedGearRecipe::new);
    }

    private ExtendedShapelessRecipeBuilder.Basic<ShapelessCompoundPartRecipe> shapelessPart(RecipeCategory category, ItemLike item) {
        return new ExtendedShapelessRecipeBuilder.Basic<>(this.items, category, new ItemStack(item), ShapelessCompoundPartRecipe::new);
    }

    private void registerGear() {
        toolRecipes(this.output, "sword", 2, GearItemSets.SWORD);
        toolRecipes(this.output, "katana", 3, GearItemSets.KATANA);
        toolRecipes(this.output, "machete", 3, GearItemSets.MACHETE);
        toolRecipes(this.output, "spear", 1, GearItemSets.SPEAR);
        toolRecipes(this.output, "trident", 3, GearItemSets.TRIDENT);
        maceRecipes();
        toolRecipes(this.output, "knife", 1, GearItemSets.KNIFE);
        toolRecipes(this.output, "dagger", 1, GearItemSets.DAGGER);
        toolRecipes(this.output, "pickaxe", 3, GearItemSets.PICKAXE);
        toolRecipes(this.output, "shovel", 1, GearItemSets.SHOVEL);
        toolRecipes(this.output, "axe", 3, GearItemSets.AXE);
        toolRecipes(this.output, "paxel", 5, GearItemSets.PAXEL);
        toolRecipes(this.output, "hammer", 6, GearItemSets.HAMMER);
        toolRecipes(this.output, "excavator", 5, GearItemSets.EXCAVATOR);
        toolRecipes(this.output, "hoe", 2, GearItemSets.HOE);
        toolRecipes(this.output, "mattock", 4, GearItemSets.MATTOCK);
        toolRecipes(this.output, "prospector_hammer", 2, GearItemSets.PROSPECTOR_HAMMER);
        toolRecipes(this.output, "saw", 5, GearItemSets.SAW);
        toolRecipes(this.output, "sickle", 3, GearItemSets.SICKLE);
        toolRecipes(this.output, "shears", 2, GearItemSets.SHEARS);
        bowRecipes("fishing_rod", 2, GearItemSets.FISHING_ROD);
        bowRecipes("bow", 3, GearItemSets.BOW);
        bowRecipes("crossbow", 3, GearItemSets.CROSSBOW);
        bowRecipes("slingshot", 2, GearItemSets.SLINGSHOT);
        arrowRecipes("arrow", GearItemSets.ARROW);
        curioRecipes("ring", 2, GearItemSets.RING);
        curioRecipes("bracelet", 3, GearItemSets.BRACELET);
        curioRecipes("necklace", 3, GearItemSets.NECKLACE);

        // Shield
        shapelessPart(RecipeCategory.COMBAT, GearItemSets.SHIELD.mainPart())
                .requires(BlueprintIngredient.of(GearItemSets.SHIELD))
                .requires(PartMaterialIngredient.of(PartTypes.MAIN.get(), GearTypes.SHIELD.get()), 2)
                .save(this.output, modId("gear/shield_plate"));
        shapelessGear(RecipeCategory.COMBAT, GearItemSets.SHIELD.gearItem())
                .requires(GearItemSets.SHIELD.mainPart())
                .requires(GearPartIngredient.of(PartTypes.ROD.get()))
                .save(this.output, modId("gear/shield"));
        shapelessGear(RecipeCategory.COMBAT, GearItemSets.SHIELD.gearItem())
                .requires(BlueprintIngredient.of(GearItemSets.SHIELD))
                .requires(PartMaterialIngredient.of(PartTypes.MAIN.get(), GearTypes.SHIELD.get()), 2)
                .requires(GearPartIngredient.of(PartTypes.ROD.get()))
                .save(this.output, modId("gear/shield_quick"));

        armorRecipes(5, GearItemSets.HELMET);
        armorRecipes(8, GearItemSets.CHESTPLATE);
        armorRecipes(7, GearItemSets.LEGGINGS);
        armorRecipes(4, GearItemSets.BOOTS);

        compoundPart(RecipeCategory.COMBAT, GearItemSets.ELYTRA.mainPart(), 1)
                .requires(BlueprintIngredient.of(GearItemSets.ELYTRA))
                .requires(PartMaterialIngredient.of(PartTypes.MAIN.get(),
                        GearTypes.ELYTRA.get(),
                        MaterialCategories.CLOTH,
                        MaterialCategories.SHEET), 6)
                .save(this.output, modId("gear/elytra_wings"));

        shapelessGear(RecipeCategory.COMBAT, GearItemSets.ELYTRA.gearItem())
                .requires(GearItemSets.ELYTRA.mainPart())
                .requires(GearPartIngredient.of(PartTypes.BINDING.get()))
                .save(this.output, modId("gear/elytra"));

        // Conversion recipes
        gearConversionRecipe(GearItemSets.SWORD, Items.WOODEN_SWORD, 2, Const.Materials.WOOD, Const.Materials.WOOD);
        gearConversionRecipe(GearItemSets.SWORD, Items.STONE_SWORD, 2, Const.Materials.STONE, Const.Materials.WOOD);
        gearConversionRecipe(GearItemSets.SWORD, Items.COPPER_SWORD, 2, Const.Materials.COPPER, Const.Materials.WOOD);
        gearConversionRecipe(GearItemSets.SWORD, Items.IRON_SWORD, 2, Const.Materials.IRON, Const.Materials.WOOD);
        gearConversionRecipe(GearItemSets.SWORD, Items.GOLDEN_SWORD, 2, Const.Materials.GOLD, Const.Materials.WOOD);
        gearConversionRecipe(GearItemSets.SWORD, Items.DIAMOND_SWORD, 2, Const.Materials.DIAMOND, Const.Materials.WOOD);
        gearConversionRecipe(GearItemSets.SWORD, Items.NETHERITE_SWORD, 2, Const.Materials.DIAMOND, Const.Materials.WOOD, Const.Materials.NETHERITE);
        gearConversionRecipe(GearItemSets.PICKAXE, Items.WOODEN_PICKAXE, 3, Const.Materials.WOOD, Const.Materials.WOOD);
        gearConversionRecipe(GearItemSets.PICKAXE, Items.STONE_PICKAXE, 3, Const.Materials.STONE, Const.Materials.WOOD);
        gearConversionRecipe(GearItemSets.PICKAXE, Items.COPPER_PICKAXE, 3, Const.Materials.COPPER, Const.Materials.WOOD);
        gearConversionRecipe(GearItemSets.PICKAXE, Items.IRON_PICKAXE, 3, Const.Materials.IRON, Const.Materials.WOOD);
        gearConversionRecipe(GearItemSets.PICKAXE, Items.GOLDEN_PICKAXE, 3, Const.Materials.GOLD, Const.Materials.WOOD);
        gearConversionRecipe(GearItemSets.PICKAXE, Items.DIAMOND_PICKAXE, 3, Const.Materials.DIAMOND, Const.Materials.WOOD);
        gearConversionRecipe(GearItemSets.PICKAXE, Items.NETHERITE_PICKAXE, 3, Const.Materials.DIAMOND, Const.Materials.WOOD, Const.Materials.NETHERITE);
        gearConversionRecipe(GearItemSets.SHOVEL, Items.WOODEN_SHOVEL, 1, Const.Materials.WOOD, Const.Materials.WOOD);
        gearConversionRecipe(GearItemSets.SHOVEL, Items.STONE_SHOVEL, 1, Const.Materials.STONE, Const.Materials.WOOD);
        gearConversionRecipe(GearItemSets.SHOVEL, Items.COPPER_SHOVEL, 1, Const.Materials.COPPER, Const.Materials.WOOD);
        gearConversionRecipe(GearItemSets.SHOVEL, Items.IRON_SHOVEL, 1, Const.Materials.IRON, Const.Materials.WOOD);
        gearConversionRecipe(GearItemSets.SHOVEL, Items.GOLDEN_SHOVEL, 1, Const.Materials.GOLD, Const.Materials.WOOD);
        gearConversionRecipe(GearItemSets.SHOVEL, Items.DIAMOND_SHOVEL, 1, Const.Materials.DIAMOND, Const.Materials.WOOD);
        gearConversionRecipe(GearItemSets.SHOVEL, Items.NETHERITE_SHOVEL, 1, Const.Materials.DIAMOND, Const.Materials.WOOD, Const.Materials.NETHERITE);
        gearConversionRecipe(GearItemSets.AXE, Items.WOODEN_AXE, 3, Const.Materials.WOOD, Const.Materials.WOOD);
        gearConversionRecipe(GearItemSets.AXE, Items.STONE_AXE, 3, Const.Materials.STONE, Const.Materials.WOOD);
        gearConversionRecipe(GearItemSets.AXE, Items.COPPER_AXE, 3, Const.Materials.COPPER, Const.Materials.WOOD);
        gearConversionRecipe(GearItemSets.AXE, Items.IRON_AXE, 3, Const.Materials.IRON, Const.Materials.WOOD);
        gearConversionRecipe(GearItemSets.AXE, Items.GOLDEN_AXE, 3, Const.Materials.GOLD, Const.Materials.WOOD);
        gearConversionRecipe(GearItemSets.AXE, Items.DIAMOND_AXE, 3, Const.Materials.DIAMOND, Const.Materials.WOOD);
        gearConversionRecipe(GearItemSets.AXE, Items.NETHERITE_AXE, 3, Const.Materials.DIAMOND, Const.Materials.WOOD, Const.Materials.NETHERITE);
        gearConversionRecipe(GearItemSets.HOE, Items.WOODEN_HOE, 2, Const.Materials.WOOD, Const.Materials.WOOD);
        gearConversionRecipe(GearItemSets.HOE, Items.STONE_HOE, 2, Const.Materials.STONE, Const.Materials.WOOD);
        gearConversionRecipe(GearItemSets.HOE, Items.COPPER_HOE, 2, Const.Materials.COPPER, Const.Materials.WOOD);
        gearConversionRecipe(GearItemSets.HOE, Items.IRON_HOE, 2, Const.Materials.IRON, Const.Materials.WOOD);
        gearConversionRecipe(GearItemSets.HOE, Items.GOLDEN_HOE, 2, Const.Materials.GOLD, Const.Materials.WOOD);
        gearConversionRecipe(GearItemSets.HOE, Items.DIAMOND_HOE, 2, Const.Materials.DIAMOND, Const.Materials.WOOD);
        gearConversionRecipe(GearItemSets.HOE, Items.NETHERITE_HOE, 2, Const.Materials.DIAMOND, Const.Materials.WOOD, Const.Materials.NETHERITE);
        gearConversionRecipe(GearItemSets.SPEAR, Items.WOODEN_SPEAR, 1, Const.Materials.WOOD, Const.Materials.WOOD);
        gearConversionRecipe(GearItemSets.SPEAR, Items.STONE_SPEAR, 1, Const.Materials.STONE, Const.Materials.WOOD);
        gearConversionRecipe(GearItemSets.SPEAR, Items.COPPER_SPEAR, 1, Const.Materials.COPPER, Const.Materials.WOOD);
        gearConversionRecipe(GearItemSets.SPEAR, Items.IRON_SPEAR, 1, Const.Materials.IRON, Const.Materials.WOOD);
        gearConversionRecipe(GearItemSets.SPEAR, Items.GOLDEN_SPEAR, 1, Const.Materials.GOLD, Const.Materials.WOOD);
        gearConversionRecipe(GearItemSets.SPEAR, Items.DIAMOND_SPEAR, 1, Const.Materials.DIAMOND, Const.Materials.WOOD);
        gearConversionRecipe(GearItemSets.SPEAR, Items.NETHERITE_SPEAR, 1, Const.Materials.DIAMOND, Const.Materials.WOOD, Const.Materials.NETHERITE);
        gearConversionRecipe(GearItemSets.HELMET, Items.LEATHER_HELMET, 5, Const.Materials.LEATHER);
        gearConversionRecipe(GearItemSets.HELMET, Items.COPPER_HELMET, 5, Const.Materials.COPPER);
        gearConversionRecipe(GearItemSets.HELMET, Items.IRON_HELMET, 5, Const.Materials.IRON);
        gearConversionRecipe(GearItemSets.HELMET, Items.GOLDEN_HELMET, 5, Const.Materials.GOLD);
        gearConversionRecipe(GearItemSets.HELMET, Items.DIAMOND_HELMET, 5, Const.Materials.DIAMOND);
        gearConversionRecipe(GearItemSets.HELMET, Items.NETHERITE_HELMET, 5, Const.Materials.DIAMOND, null, Const.Materials.NETHERITE);
        gearConversionRecipe(GearItemSets.HELMET, Items.TURTLE_HELMET, 5, Const.Materials.TURTLE);
        gearConversionRecipe(GearItemSets.CHESTPLATE, Items.LEATHER_CHESTPLATE, 8, Const.Materials.LEATHER);
        gearConversionRecipe(GearItemSets.CHESTPLATE, Items.COPPER_CHESTPLATE, 8, Const.Materials.COPPER);
        gearConversionRecipe(GearItemSets.CHESTPLATE, Items.IRON_CHESTPLATE, 8, Const.Materials.IRON);
        gearConversionRecipe(GearItemSets.CHESTPLATE, Items.GOLDEN_CHESTPLATE, 8, Const.Materials.GOLD);
        gearConversionRecipe(GearItemSets.CHESTPLATE, Items.DIAMOND_CHESTPLATE, 8, Const.Materials.DIAMOND);
        gearConversionRecipe(GearItemSets.CHESTPLATE, Items.NETHERITE_CHESTPLATE, 8, Const.Materials.DIAMOND, null, Const.Materials.NETHERITE);
        gearConversionRecipe(GearItemSets.LEGGINGS, Items.LEATHER_LEGGINGS, 7, Const.Materials.LEATHER);
        gearConversionRecipe(GearItemSets.LEGGINGS, Items.COPPER_LEGGINGS, 7, Const.Materials.COPPER);
        gearConversionRecipe(GearItemSets.LEGGINGS, Items.IRON_LEGGINGS, 7, Const.Materials.IRON);
        gearConversionRecipe(GearItemSets.LEGGINGS, Items.GOLDEN_LEGGINGS, 7, Const.Materials.GOLD);
        gearConversionRecipe(GearItemSets.LEGGINGS, Items.DIAMOND_LEGGINGS, 7, Const.Materials.DIAMOND);
        gearConversionRecipe(GearItemSets.LEGGINGS, Items.NETHERITE_LEGGINGS, 7, Const.Materials.DIAMOND, null, Const.Materials.NETHERITE);
        gearConversionRecipe(GearItemSets.BOOTS, Items.LEATHER_BOOTS, 4, Const.Materials.LEATHER);
        gearConversionRecipe(GearItemSets.BOOTS, Items.COPPER_BOOTS, 4, Const.Materials.COPPER);
        gearConversionRecipe(GearItemSets.BOOTS, Items.IRON_BOOTS, 4, Const.Materials.IRON);
        gearConversionRecipe(GearItemSets.BOOTS, Items.GOLDEN_BOOTS, 4, Const.Materials.GOLD);
        gearConversionRecipe(GearItemSets.BOOTS, Items.DIAMOND_BOOTS, 4, Const.Materials.DIAMOND);
        gearConversionRecipe(GearItemSets.BOOTS, Items.NETHERITE_BOOTS, 4, Const.Materials.DIAMOND, null, Const.Materials.NETHERITE);
    }

    private void registerModifierKits() {
        shaped(RecipeCategory.MISC, SgItems.MOD_KIT)
                .define('#', SgTags.Items.TEMPLATE_BOARDS)
                .define('/', Tags.Items.RODS_WOODEN)
                .define('o', Tags.Items.INGOTS_IRON)
                .pattern("##o")
                .pattern("##/")
                .unlockedBy("has_item", has(SgTags.Items.TEMPLATE_BOARDS))
                .save(this.output);

        shaped(RecipeCategory.MISC, SgItems.VERY_CRUDE_REPAIR_KIT)
                .define('#', SgTags.Items.TEMPLATE_BOARDS)
                .define('/', Tags.Items.RODS_WOODEN)
                .define('o', Tags.Items.STONES)
                .pattern(" / ")
                .pattern("#o#")
                .pattern("###")
                .unlockedBy("has_item", has(SgTags.Items.TEMPLATE_BOARDS))
                .save(this.output);

        shaped(RecipeCategory.MISC, SgItems.CRUDE_REPAIR_KIT)
                .define('#', SgTags.Items.TEMPLATE_BOARDS)
                .define('/', Tags.Items.RODS_WOODEN)
                .define('o', Tags.Items.INGOTS_IRON)
                .pattern(" / ")
                .pattern("#o#")
                .pattern("###")
                .unlockedBy("has_item", has(SgTags.Items.TEMPLATE_BOARDS))
                .save(this.output);

        shaped(RecipeCategory.MISC, SgItems.STURDY_REPAIR_KIT)
                .define('#', Tags.Items.INGOTS_IRON)
                .define('/', SgTags.Items.RODS_IRON)
                .define('o', Tags.Items.GEMS_DIAMOND)
                .pattern(" / ")
                .pattern("#o#")
                .pattern("###")
                .unlockedBy("has_item", has(Tags.Items.INGOTS_IRON))
                .save(this.output);

        shaped(RecipeCategory.MISC, SgItems.CRIMSON_REPAIR_KIT)
                .define('#', SgTags.Items.INGOTS_CRIMSON_STEEL)
                .define('/', Tags.Items.RODS_BLAZE)
                .define('o', SgTags.Items.INGOTS_BLAZE_GOLD)
                .pattern(" / ")
                .pattern("#o#")
                .pattern("###")
                .unlockedBy("has_item", has(SgTags.Items.INGOTS_CRIMSON_STEEL))
                .save(this.output);

        shaped(RecipeCategory.MISC, SgItems.AZURE_REPAIR_KIT)
                .define('#', SgTags.Items.INGOTS_AZURE_ELECTRUM)
                .define('/', Items.END_ROD)
                .define('o', Tags.Items.GEMS_EMERALD)
                .pattern(" / ")
                .pattern("#o#")
                .pattern("###")
                .unlockedBy("has_item", has(SgTags.Items.INGOTS_AZURE_ELECTRUM))
                .save(this.output);

        for (RepairKitItem item : SgItems.getItems(RepairKitItem.class)) {
            // Empty repair kit recipes
            shapelessExt(RecipeCategory.MISC, item)
                    .requires(item)
                    .requires(Tags.Items.RODS_WOODEN)
                    .save(this.output, modId(NameUtils.fromItem(item).getPath() + "_empty"));
        }
    }

    private void registerMachines() {
        shaped(RecipeCategory.DECORATIONS, SgBlocks.STONE_ANVIL)
                .define('#', Tags.Items.COBBLESTONES)
                .define('/', ItemTags.DIRT)
                .pattern(" # ")
                .pattern("#/#")
                .unlockedBy("has_item", has(Tags.Items.COBBLESTONES))
                .save(this.output);

        shaped(RecipeCategory.DECORATIONS, SgBlocks.ALLOY_FORGE)
                .define('/', SgTags.Items.INGOTS_CRIMSON_STEEL)
                .define('i', Tags.Items.STORAGE_BLOCKS_IRON)
                .define('#', Blocks.BLACKSTONE)
                .pattern("/#/")
                .pattern("/ /")
                .pattern("#i#")
                .unlockedBy("has_item", has(SgTags.Items.INGOTS_CRIMSON_STEEL))
                .save(this.output);

        shaped(RecipeCategory.DECORATIONS, SgBlocks.RECRYSTALLIZER)
                .define('/', SgTags.Items.INGOTS_AZURE_ELECTRUM)
                .define('g', Tags.Items.STORAGE_BLOCKS_GOLD)
                .define('d', Tags.Items.GEMS_DIAMOND)
                .define('e', Tags.Items.GEMS_EMERALD)
                .define('#', Blocks.PURPUR_BLOCK)
                .pattern("/e/")
                .pattern("/d/")
                .pattern("#g#")
                .unlockedBy("has_item", has(SgTags.Items.INGOTS_AZURE_ELECTRUM))
                .save(this.output);

        shaped(RecipeCategory.DECORATIONS, SgBlocks.REFABRICATOR)
                .define('/', Tags.Items.INGOTS_IRON)
                .define('i', Tags.Items.STORAGE_BLOCKS_IRON)
                .define('d', Tags.Items.GEMS_DIAMOND)
                .define('b', SgTags.Items.GEMS_BORT)
                .define('#', ItemTags.PLANKS)
                .pattern("/ /")
                .pattern("dbd")
                .pattern("#i#")
                .unlockedBy("has_item", has(SgTags.Items.GEMS_BORT))
                .save(this.output);

        shaped(RecipeCategory.DECORATIONS, SgBlocks.METAL_PRESS)
                .define('#', Tags.Items.OBSIDIANS)
                .define('t', SgTags.Items.INGOTS_TYRIAN_STEEL)
                .define('/', SgTags.Items.RODS_IRON)
                .pattern("#t#")
                .pattern("/ /")
                .pattern("#t#")
                .unlockedBy("has_item", has(SgTags.Items.INGOTS_TYRIAN_STEEL))
                .save(this.output);

        shaped(RecipeCategory.DECORATIONS, SgBlocks.CRUDE_MIXER)
                .define('#', Tags.Items.STONES)
                .define('/', ItemTags.PLANKS)
                .define('F', CraftingItems.FLAX_STRING)
                .pattern("#F#")
                .pattern("#/#")
                .pattern(" # ")
                .unlockedBy("has_item", has(Tags.Items.STONES))
                .save(this.output);

        shaped(RecipeCategory.DECORATIONS, SgBlocks.SUPER_MIXER)
                .define('#', SgTags.Items.STORAGE_BLOCKS_TYRIAN_STEEL)
                .define('B', Items.BEACON)
                .define('D', new Ingredient(CustomAlloyIngredient.of(SgItems.CUSTOM_GEM.get(), Const.Materials.DIMERALD)))
                .pattern("#D#")
                .pattern("#B#")
                .pattern(" # ")
                .unlockedBy("has_item", has(SgTags.Items.INGOTS_TYRIAN_STEEL))
                .save(this.output);

        shaped(RecipeCategory.DECORATIONS, SgBlocks.STARLIGHT_CHARGER)
                .define('#', Blocks.POLISHED_BLACKSTONE)
                .define('/', SgTags.Items.STORAGE_BLOCKS_BLAZE_GOLD)
                .define('q', Blocks.QUARTZ_BLOCK)
                .define('g', Tags.Items.GLASS_BLOCKS_COLORLESS)
                .pattern("qgq")
                .pattern("#g#")
                .pattern("#/#")
                .unlockedBy("has_item", has(SgTags.Items.INGOTS_BLAZE_GOLD))
                .save(this.output);
    }

    private void registerCompounding() {
        CompoundingRecipeBuilder.gemBuilder(SgItems.CUSTOM_GEM, 1)
                .withCustomMaterial(Const.Materials.DIMERALD)
                .addIngredient(tag(Tags.Items.GEMS_DIAMOND))
                .addIngredient(tag(Tags.Items.GEMS_EMERALD))
                .save(this.output);

        CompoundingRecipeBuilder.metalBuilder(SgItems.CUSTOM_INGOT, 1)
                .withCustomMaterial(DataResource.material("high_carbon_steel"))
                .addIngredient(tag(Tags.Items.INGOTS_IRON))
                .addIngredient(tag(ItemTags.COALS), 3)
                .save(this.output);

        CompoundingRecipeBuilder.metalBuilder(CraftingItems.TYRIAN_STEEL_INGOT, 4)
                .addIngredient(tag(SgTags.Items.INGOTS_CRIMSON_STEEL))
                .addIngredient(tag(SgTags.Items.INGOTS_AZURE_ELECTRUM))
                .addIngredient(CraftingItems.CRUSHED_SHULKER_SHELL)
                .addIngredient(Items.NETHERITE_SCRAP)
                .save(this.output);

        CompoundingRecipeBuilder.metalBuilder(CraftingItems.BLAZE_GOLD_INGOT, 1)
                .addIngredient(tag(Tags.Items.INGOTS_GOLD))
                .addIngredient(Items.BLAZE_POWDER, 2)
                .save(this.output);

        CompoundingRecipeBuilder.metalBuilder(CraftingItems.CRIMSON_STEEL_INGOT, 3)
                .addIngredient(tag(SgTags.Items.STORAGE_BLOCKS_CRIMSON_IRON))
                .addIngredient(tag(Tags.Items.RODS_BLAZE), 2)
                .addIngredient(Items.MAGMA_CREAM)
                .save(this.output);

        CompoundingRecipeBuilder.metalBuilder(CraftingItems.AZURE_ELECTRUM_INGOT, 3)
                .addIngredient(tag(SgTags.Items.STORAGE_BLOCKS_AZURE_SILVER))
                .addIngredient(tag(Tags.Items.INGOTS_GOLD), 2)
                .addIngredient(tag(Tags.Items.ENDER_PEARLS))
                .save(this.output);
    }

    private void registerPressing() {
        new SingleItemRecipeBuilder(RecipeCategory.MISC,
                MaterialPressingRecipe::new,
                new Ingredient(PartMaterialIngredient.of(PartTypes.MAIN.get(), MaterialCategories.METAL)),
                SgItems.SHEET_METAL, 2
        ).unlockedBy("impossible", CriteriaTriggers.IMPOSSIBLE.createCriterion(new ImpossibleTrigger.TriggerInstance()))
                .save(this.output);
    }

    private void registerCraftingItems() {
        shapelessExt(RecipeCategory.MISC, CraftingItems.CRUDE_TOOL_PARTS, 4)
                .requires(Tags.Items.COBBLESTONES)
                .requires(ItemTags.DIRT)
                .requires(Tags.Items.RODS_WOODEN)
                .unlockedBy("has_item", has(Tags.Items.RODS_WOODEN))
                .save(this.output);

        shapelessExt(RecipeCategory.MISC, CraftingItems.GLOWING_DUST, 4)
                .requires(Items.STICK)
                .requires(Tags.Items.DUSTS_GLOWSTONE, 2)
                .requires(Tags.Items.GEMS_QUARTZ)
                .unlockedBy("has_item", has(Tags.Items.DUSTS_GLOWSTONE))
                .save(this.output);

        toolAction(SgTags.Items.TOOLS_HAMMER, Tags.Items.COBBLESTONES, 1, SgItems.PEBBLE, 9, HAMMER_SOUND)
                .save(this.output);

        toolAction(SgTags.Items.TOOLS_KNIFE, ItemTags.LOGS, 1, CraftingItems.TEMPLATE_BOARD, 6, KNIFE_SOUND)
                .save(this.output);

        toolAction(SgTags.Items.TOOLS_HAMMER, Items.SHULKER_SHELL, 10, CraftingItems.CRUSHED_SHULKER_SHELL, 1, HAMMER_SOUND)
                .save(this.output);

        shaped(RecipeCategory.MISC, CraftingItems.AZURE_ELECTRUM_INGOT)
                .define('/', Tags.Items.INGOTS_GOLD)
                .define('#', SgTags.Items.INGOTS_AZURE_SILVER)
                .define('o', Tags.Items.ENDER_PEARLS)
                .pattern("/ /")
                .pattern("#o#")
                .pattern("# #")
                .unlockedBy("has_item", has(CraftingItems.AZURE_SILVER_INGOT))
                .save(this.output);

        shapeless(RecipeCategory.MISC, Items.BLUE_DYE)
                .requires(CraftingItems.FLAX_FLOWERS, 4)
                .unlockedBy("has_item", has(CraftingItems.FLAX_FLOWERS))
                .save(this.output, modId("blue_dye_from_flax_flowers"));

        shaped(RecipeCategory.MISC, CraftingItems.MAGNETIC_UPGRADE)
                .pattern("i i")
                .pattern("c c")
                .pattern("#c/")
                .define('i', Tags.Items.INGOTS_IRON)
                .define('c', SgTags.Items.INGOTS_CRIMSON_STEEL)
                .define('#', CraftingItems.ADVANCED_UPGRADE_BASE)
                .define('/', Tags.Items.RODS_BREEZE)
                .unlockedBy("has_item", has(SgTags.Items.INGOTS_CRIMSON_STEEL))
                .save(this.output);

        shapeless(RecipeCategory.MISC, CraftingItems.ROAD_MAKER_UPGRADE)
                .requires(CraftingItems.ADVANCED_UPGRADE_BASE)
                .requires(Items.IRON_SHOVEL)
                .requires(Tags.Items.DYES_ORANGE)
                .unlockedBy("has_item", has(CraftingItems.UPGRADE_BASE))
                .save(this.output);

        shapeless(RecipeCategory.MISC, CraftingItems.SPOON_UPGRADE)
                .requires(CraftingItems.ADVANCED_UPGRADE_BASE)
                .requires(Items.DIAMOND_SHOVEL)
                .unlockedBy("has_item", has(CraftingItems.UPGRADE_BASE))
                .save(this.output);

        shapeless(RecipeCategory.MISC, CraftingItems.WIDE_PLATE_UPGRADE)
                .requires(CraftingItems.ADVANCED_UPGRADE_BASE)
                .requires(SgTags.Items.STORAGE_BLOCKS_CRIMSON_IRON)
                .requires(SgTags.Items.INGOTS_CRIMSON_STEEL)
                .unlockedBy("has_item", has(CraftingItems.UPGRADE_BASE))
                .save(this.output);

        SimpleCookingRecipeBuilder.smelting(tag(SgTags.Items.NETHERWOOD_LOGS), RecipeCategory.MISC, SgItems.NETHERWOOD_CHARCOAL, 0.15f, 200)
                .unlockedBy("has_item", has(SgTags.Items.NETHERWOOD_LOGS))
                .save(this.output);

        shaped(RecipeCategory.MISC, SgBlocks.NETHERWOOD_CHARCOAL_BLOCK)
                .define('#', SgItems.NETHERWOOD_CHARCOAL)
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .unlockedBy("has_item", has(SgItems.NETHERWOOD_CHARCOAL))
                .save(this.output);
        shapeless(RecipeCategory.MISC, SgItems.NETHERWOOD_CHARCOAL, 9)
                .requires(SgBlocks.NETHERWOOD_CHARCOAL_BLOCK)
                .unlockedBy("has_item", has(SgBlocks.NETHERWOOD_CHARCOAL_BLOCK))
                .save(this.output, modId("netherwood_charcoal_from_block"));

        shaped(RecipeCategory.MISC, CraftingItems.FLUFFY_FABRIC)
                .pattern("##")
                .pattern("##")
                .define('#', CraftingItems.FLUFFY_PUFF)
                .unlockedBy("has_item", has(CraftingItems.FLUFFY_PUFF))
                .save(this.output);
        shapeless(RecipeCategory.MISC, CraftingItems.FLUFFY_PUFF, 4)
                .requires(CraftingItems.FLUFFY_FABRIC)
                .unlockedBy("has_item", has(CraftingItems.FLUFFY_PUFF))
                .save(this.output, modId("fluffy_puff_from_fabric"));

        shaped(RecipeCategory.BUILDING_BLOCKS, SgBlocks.WHITE_FLUFFY_BLOCK)
                .pattern("##")
                .pattern("##")
                .define('#', CraftingItems.FLUFFY_FABRIC)
                .unlockedBy("has_item", has(CraftingItems.FLUFFY_PUFF))
                .save(this.output, modId("fluffy_block_base"));
        shapeless(RecipeCategory.MISC, CraftingItems.FLUFFY_FABRIC, 4)
                .requires(SgTags.Items.FLUFFY_BLOCKS)
                .unlockedBy("has_item", has(CraftingItems.FLUFFY_PUFF))
                .save(this.output, modId("fluffy_fabric_from_block"));

        dyeFluffyBlock(this.output, SgBlocks.WHITE_FLUFFY_BLOCK, Tags.Items.DYES_WHITE);
        dyeFluffyBlock(this.output, SgBlocks.ORANGE_FLUFFY_BLOCK, Tags.Items.DYES_ORANGE);
        dyeFluffyBlock(this.output, SgBlocks.MAGENTA_FLUFFY_BLOCK, Tags.Items.DYES_MAGENTA);
        dyeFluffyBlock(this.output, SgBlocks.LIGHT_BLUE_FLUFFY_BLOCK, Tags.Items.DYES_LIGHT_BLUE);
        dyeFluffyBlock(this.output, SgBlocks.YELLOW_FLUFFY_BLOCK, Tags.Items.DYES_YELLOW);
        dyeFluffyBlock(this.output, SgBlocks.LIME_FLUFFY_BLOCK, Tags.Items.DYES_LIME);
        dyeFluffyBlock(this.output, SgBlocks.PINK_FLUFFY_BLOCK, Tags.Items.DYES_PINK);
        dyeFluffyBlock(this.output, SgBlocks.GRAY_FLUFFY_BLOCK, Tags.Items.DYES_GRAY);
        dyeFluffyBlock(this.output, SgBlocks.LIGHT_GRAY_FLUFFY_BLOCK, Tags.Items.DYES_LIGHT_GRAY);
        dyeFluffyBlock(this.output, SgBlocks.CYAN_FLUFFY_BLOCK, Tags.Items.DYES_CYAN);
        dyeFluffyBlock(this.output, SgBlocks.PURPLE_FLUFFY_BLOCK, Tags.Items.DYES_PURPLE);
        dyeFluffyBlock(this.output, SgBlocks.BLUE_FLUFFY_BLOCK, Tags.Items.DYES_BLUE);
        dyeFluffyBlock(this.output, SgBlocks.BROWN_FLUFFY_BLOCK, Tags.Items.DYES_BROWN);
        dyeFluffyBlock(this.output, SgBlocks.GREEN_FLUFFY_BLOCK, Tags.Items.DYES_GREEN);
        dyeFluffyBlock(this.output, SgBlocks.RED_FLUFFY_BLOCK, Tags.Items.DYES_RED);
        dyeFluffyBlock(this.output, SgBlocks.BLACK_FLUFFY_BLOCK, Tags.Items.DYES_BLACK);

        shapeless(RecipeCategory.MISC, SgItems.FLUFFY_SEEDS)
                .requires(CraftingItems.FLUFFY_PUFF)
                .unlockedBy("has_item", has(CraftingItems.FLUFFY_PUFF))
                .save(this.output);

        shaped(RecipeCategory.MISC, CraftingItems.FLUFFY_FEATHER)
                .pattern(" ##")
                .pattern("## ")
                .pattern("#  ")
                .define('#', CraftingItems.FLUFFY_PUFF)
                .unlockedBy("has_item", has(CraftingItems.FLUFFY_PUFF))
                .save(this.output);

        shapelessExt(RecipeCategory.MISC, Items.FEATHER)
                .requires(CraftingItems.FLUFFY_FEATHER)
                .unlockedBy("has_item", has(CraftingItems.FLUFFY_FEATHER))
                .save(this.output, modId("feather_from_fluffy"));

        shaped(RecipeCategory.MISC, CraftingItems.FLUFFY_STRING)
                .pattern("###")
                .define('#', CraftingItems.FLUFFY_PUFF)
                .unlockedBy("has_item", has(CraftingItems.FLUFFY_PUFF))
                .save(this.output);

        shapelessExt(RecipeCategory.MISC, Items.STRING)
                .requires(CraftingItems.FLUFFY_STRING)
                .unlockedBy("has_item", has(CraftingItems.FLUFFY_STRING))
                .save(this.output, modId("string_from_fluffy"));

        shaped(RecipeCategory.MISC, Items.WHITE_WOOL)
                .pattern("###")
                .pattern("#~#")
                .pattern("###")
                .define('#', CraftingItems.FLUFFY_PUFF)
                .define('~', Tags.Items.STRINGS)
                .unlockedBy("has_item", has(CraftingItems.FLUFFY_PUFF))
                .save(this.output, modId("wool_from_fluffy"));

        shaped(RecipeCategory.MISC, CraftingItems.FINE_SILK_CLOTH)
                .pattern("##")
                .pattern("##")
                .define('#', CraftingItems.FINE_SILK)
                .unlockedBy("has_item", has(CraftingItems.FINE_SILK))
                .save(this.output);

        shapeless(RecipeCategory.MISC, CraftingItems.FINE_SILK, 4)
                .requires(CraftingItems.FINE_SILK_CLOTH)
                .unlockedBy("has_item", has(CraftingItems.FINE_SILK))
                .save(this.output);

        shapelessExt(RecipeCategory.MISC, CraftingItems.NETHER_STAR_FRAGMENT, 9)
                .requires(Items.NETHER_STAR)
                .save(this.output);

        shapedExt(RecipeCategory.MISC, Items.NETHER_STAR)
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', CraftingItems.NETHER_STAR_FRAGMENT)
                .unlockedBy("has_item", has(Items.NETHER_STAR))
                .save(this.output, modId("nether_star_from_fragments"));

        shapelessExt(RecipeCategory.MISC, CraftingItems.STARMETAL_DUST, 3)
                .requires(SgTags.Items.DUSTS_AZURE_ELECTRUM, 1)
                .requires(SgTags.Items.DUSTS_AZURE_SILVER, 2)
                .requires(SgTags.Items.DUSTS_BLAZE_GOLD, 1)
                .requires(CraftingItems.NETHER_STAR_FRAGMENT)
                .unlockedBy("has_item", has(CraftingItems.NETHER_STAR_FRAGMENT))
                .save(this.output);

        /*shapeless(RecipeCategory.MISC, CraftingItems.BRONZE_INGOT, 4)
                .requires(Tags.Items.INGOTS_COPPER, 3)
                .requires(Tags.Items.INGOTS_IRON, 1)
                .unlockedBy("has_item", has(Tags.Items.INGOTS_COPPER))
                .save(this.output);*/


        shaped(RecipeCategory.MISC, CraftingItems.ADVANCED_UPGRADE_BASE)
                .define('/', SgTags.Items.NUGGETS_DIAMOND)
                .define('D', Tags.Items.DYES_BLUE)
                .define('U', CraftingItems.UPGRADE_BASE)
                .define('G', Tags.Items.NUGGETS_GOLD)
                .pattern("///")
                .pattern("DUD")
                .pattern("GGG")
                .unlockedBy("has_item", has(CraftingItems.UPGRADE_BASE))
                .save(this.output);
        shapeless(RecipeCategory.MISC, CraftingItems.BLAZE_GOLD_INGOT)
                .requires(Tags.Items.INGOTS_GOLD)
                .requires(Items.BLAZE_POWDER, 4)
                .unlockedBy("has_item", has(Items.BLAZE_POWDER))
                .save(this.output);
        shapeless(RecipeCategory.MISC, CraftingItems.BLAZING_DUST, 4)
                .requires(SgTags.Items.DUSTS_BLAZE_GOLD)
                .requires(tag(Tags.Items.DUSTS_GLOWSTONE), 2)
                .unlockedBy("has_item", has(SgTags.Items.DUSTS_BLAZE_GOLD))
                .save(this.output);
        shapeless(RecipeCategory.MISC, CraftingItems.BLUEPRINT_PAPER, 4)
                .requires(tag(SgTags.Items.PAPER), 4)
                .requires(Tags.Items.DYES_BLUE)
                .unlockedBy("has_paper", has(SgTags.Items.PAPER))
                .save(this.output);
        shapeless(RecipeCategory.MISC, Blocks.COBBLESTONE)
                .requires(SgItems.PEBBLE, 9)
                .unlockedBy("has_pebble", has(SgItems.PEBBLE))
                .save(this.output, modId("cobblestone_from_pebbles"));
        shaped(RecipeCategory.MISC, CraftingItems.CRIMSON_STEEL_INGOT)
                .define('/', Tags.Items.RODS_BLAZE)
                .define('#', SgTags.Items.INGOTS_CRIMSON_IRON)
                .define('C', Items.MAGMA_CREAM)
                .pattern("/ /")
                .pattern("#C#")
                .pattern("# #")
                .unlockedBy("has_item", has(CraftingItems.CRIMSON_IRON_INGOT))
                .save(this.output);
        shapeless(RecipeCategory.MISC, CraftingItems.DIAMOND_SHARD, 9)
                .requires(Tags.Items.GEMS_DIAMOND)
                .unlockedBy("has_item", has(Tags.Items.GEMS_DIAMOND))
                .save(this.output);
        shaped(RecipeCategory.MISC, Items.DIAMOND)
                .define('#', SgTags.Items.NUGGETS_DIAMOND)
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .unlockedBy("has_item", has(Tags.Items.GEMS_DIAMOND))
                .save(this.output, modId("diamond_from_shards"));
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(CraftingItems.SINEW), RecipeCategory.MISC, CraftingItems.DRIED_SINEW, 0.35f, 200)
                .unlockedBy("has_item", has(CraftingItems.SINEW))
                .save(this.output);
        shapeless(RecipeCategory.MISC, CraftingItems.EMERALD_SHARD, 9)
                .requires(Tags.Items.GEMS_EMERALD)
                .unlockedBy("has_item", has(Tags.Items.GEMS_EMERALD))
                .save(this.output);
        shaped(RecipeCategory.MISC, Items.EMERALD)
                .define('#', SgTags.Items.NUGGETS_EMERALD)
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .unlockedBy("has_item", has(Tags.Items.GEMS_EMERALD))
                .save(this.output, modId("emerald_from_shards"));
        shapeless(RecipeCategory.MISC, CraftingItems.FLAX_STRING)
                .requires(CraftingItems.FLAX_FIBER, 2)
                .unlockedBy("has_item", has(CraftingItems.FLAX_FIBER))
                .save(this.output);
        shaped(RecipeCategory.MISC, CraftingItems.GLITTERY_DUST, 8)
                .define('o', Items.POPPED_CHORUS_FRUIT)
                .define('/', SgTags.Items.NUGGETS_EMERALD)
                .define('#', Tags.Items.DUSTS_GLOWSTONE)
                .define('b', SgItems.NETHER_BANANA)
                .pattern("o/o")
                .pattern("#b#")
                .pattern("o/o")
                .unlockedBy("has_item", has(SgItems.NETHER_BANANA))
                .save(this.output);
        shaped(RecipeCategory.MISC, SgItems.GOLDEN_NETHER_BANANA)
                .define('g', Tags.Items.INGOTS_GOLD)
                .define('b', SgItems.NETHER_BANANA)
                .pattern("ggg")
                .pattern("gbg")
                .pattern("ggg")
                .unlockedBy("has_item", has(SgItems.NETHER_BANANA))
                .save(this.output);
        shaped(RecipeCategory.MISC, CraftingItems.IRON_ROD, 4)
                .define('/', Tags.Items.INGOTS_IRON)
                .pattern("/")
                .pattern("/")
                .unlockedBy("has_item", has(Items.IRON_INGOT))
                .save(this.output);
        shaped(RecipeCategory.MISC, Items.LEATHER)
                .define('#', CraftingItems.LEATHER_SCRAP)
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .unlockedBy("has_item", has(CraftingItems.LEATHER_SCRAP))
                .save(this.output, modId("leather_from_scraps"));
        shapeless(RecipeCategory.MISC, CraftingItems.LEATHER_SCRAP, 9)
                .requires(Items.LEATHER)
                .unlockedBy("has_item", has(CraftingItems.LEATHER_SCRAP))
                .save(this.output);
        shaped(RecipeCategory.MISC, SgBlocks.MATERIAL_GRADER)
                .define('Q', Tags.Items.GEMS_QUARTZ)
                .define('I', Tags.Items.INGOTS_IRON)
                .define('#', CraftingItems.ADVANCED_UPGRADE_BASE)
                .define('G', SgTags.Items.INGOTS_BLAZE_GOLD)
                .pattern("QIQ")
                .pattern("I#I")
                .pattern("GGG")
                .unlockedBy("has_item", has(SgTags.Items.INGOTS_BLAZE_GOLD))
                .save(this.output);
        shaped(RecipeCategory.MISC, SgBlocks.NETHERWOOD_DOOR, 3)
                .define('#', SgBlocks.NETHERWOOD_PLANKS)
                .pattern("##")
                .pattern("##")
                .pattern("##")
                .unlockedBy("has_item", has(SgBlocks.NETHERWOOD_PLANKS))
                .save(this.output);
        shaped(RecipeCategory.MISC, SgBlocks.NETHERWOOD_TRAPDOOR, 2)
                .define('#', SgBlocks.NETHERWOOD_PLANKS)
                .pattern("###")
                .pattern("###")
                .unlockedBy("has_item", has(SgBlocks.NETHERWOOD_PLANKS))
                .save(this.output);
        shaped(RecipeCategory.MISC, SgBlocks.NETHERWOOD_FENCE, 3)
                .define('#', SgBlocks.NETHERWOOD_PLANKS)
                .define('/', Tags.Items.RODS_WOODEN)
                .pattern("#/#")
                .pattern("#/#")
                .unlockedBy("has_item", has(SgBlocks.NETHERWOOD_PLANKS))
                .save(this.output);
        shaped(RecipeCategory.MISC, SgBlocks.NETHERWOOD_FENCE_GATE, 1)
                .define('#', SgBlocks.NETHERWOOD_PLANKS)
                .define('/', Tags.Items.RODS_WOODEN)
                .pattern("/#/")
                .pattern("/#/")
                .unlockedBy("has_item", has(SgBlocks.NETHERWOOD_PLANKS))
                .save(this.output);
        shapeless(RecipeCategory.MISC, SgBlocks.NETHERWOOD_PLANKS, 4)
                .requires(SgTags.Items.NETHERWOOD_LOGS)
                .unlockedBy("has_item", has(SgTags.Items.NETHERWOOD_LOGS))
                .save(this.output);
        shaped(RecipeCategory.MISC, SgBlocks.NETHERWOOD_WOOD, 3)
                .define('#', SgBlocks.NETHERWOOD_LOG)
                .pattern("##")
                .pattern("##")
                .unlockedBy("has_item", has(SgBlocks.NETHERWOOD_LOG))
                .save(this.output);
        shaped(RecipeCategory.MISC, SgBlocks.NETHERWOOD_PLANKS)
                .define('#', SgBlocks.NETHERWOOD_SLAB)
                .pattern("#")
                .pattern("#")
                .unlockedBy("has_item", has(SgBlocks.NETHERWOOD_LOG))
                .save(this.output, modId("netherwood_planks_from_slabs"));
        shaped(RecipeCategory.MISC, SgBlocks.NETHERWOOD_PLANKS, 3)
                .define('#', SgBlocks.NETHERWOOD_STAIRS)
                .pattern("##")
                .pattern("##")
                .unlockedBy("has_item", has(SgBlocks.NETHERWOOD_LOG))
                .save(this.output, modId("netherwood_planks_from_stairs"));
        shaped(RecipeCategory.MISC, SgBlocks.NETHERWOOD_SLAB, 6)
                .define('#', SgBlocks.NETHERWOOD_PLANKS)
                .pattern("###")
                .unlockedBy("has_item", has(SgBlocks.NETHERWOOD_LOG))
                .save(this.output);
        shaped(RecipeCategory.MISC, SgBlocks.NETHERWOOD_STAIRS, 4)
                .define('#', SgBlocks.NETHERWOOD_PLANKS)
                .pattern("#  ")
                .pattern("## ")
                .pattern("###")
                .unlockedBy("has_item", has(SgBlocks.NETHERWOOD_LOG))
                .save(this.output);
        shaped(RecipeCategory.MISC, CraftingItems.NETHERWOOD_STICK, 4)
                .define('#', SgBlocks.NETHERWOOD_PLANKS)
                .pattern(" #")
                .pattern("# ")
                .unlockedBy("has_item", has(SgBlocks.NETHERWOOD_LOG))
                .save(this.output);
        shapeless(RecipeCategory.MISC, CraftingItems.RED_CARD_UPGRADE, 4)
                .requires(CraftingItems.UPGRADE_BASE)
                .requires(Tags.Items.DYES_RED)
                .unlockedBy("has_item", has(CraftingItems.UPGRADE_BASE))
                .save(this.output);
        shaped(RecipeCategory.MISC, SgBlocks.SALVAGER)
                .define('P', Blocks.PISTON)
                .define('/', SgTags.Items.INGOTS_CRIMSON_IRON)
                .define('I', Tags.Items.STORAGE_BLOCKS_IRON)
                .define('#', Tags.Items.OBSIDIANS)
                .pattern(" P ")
                .pattern("/I/")
                .pattern("/#/")
                .unlockedBy("has_item", has(SgTags.Items.INGOTS_CRIMSON_IRON))
                .save(this.output);
        shapeless(RecipeCategory.MISC, CraftingItems.SINEW_FIBER, 3)
                .requires(CraftingItems.DRIED_SINEW)
                .unlockedBy("has_item", has(CraftingItems.SINEW))
                .save(this.output);
        shaped(RecipeCategory.MISC, CraftingItems.STONE_ROD, 4)
                .define('#', Tags.Items.COBBLESTONES)
                .pattern("#")
                .pattern("#")
                .unlockedBy("has_item", has(Tags.Items.COBBLESTONES))
                .save(this.output);
        shaped(RecipeCategory.MISC, SgBlocks.STONE_TORCH, 4)
                .define('#', ItemTags.COALS)
                .define('/', SgTags.Items.RODS_STONE)
                .pattern("#")
                .pattern("/")
                .unlockedBy("has_item", has(ItemTags.COALS))
                .save(this.output);
        shapeless(RecipeCategory.MISC, CraftingItems.UPGRADE_BASE, 4)
                .requires(tag(SgTags.Items.PAPER), 2)
                .requires(ItemTags.PLANKS)
                .requires(Tags.Items.STONES)
                .unlockedBy("has_item", has(ItemTags.PLANKS))
                .save(this.output);
    }

    private void dyeFluffyBlock(RecipeOutput consumer, ItemLike block, TagKey<Item> dye) {
        shapedExt(RecipeCategory.BUILDING_BLOCKS, block, 8)
                .pattern("###")
                .pattern("#d#")
                .pattern("###")
                .define('#', SgTags.Items.FLUFFY_BLOCKS)
                .define('d', dye)
                .unlockedBy("has_item", has(SgBlocks.WHITE_FLUFFY_BLOCK))
                .save(consumer);
    }

    private void registerCrudeTools() {
        shapedExt(RecipeCategory.TOOLS, SgItems.CRUDE_KNIFE)
                .pattern("#")
                .pattern("/")
                .define('#', Tags.Items.COBBLESTONES)
                .define('/', CraftingItems.CRUDE_TOOL_PARTS)
                .unlockedBy("has_item", has(CraftingItems.CRUDE_TOOL_PARTS))
                .save(this.output);
        shapedExt(RecipeCategory.TOOLS, SgItems.CRUDE_HAMMER)
                .pattern("##")
                .pattern(" /")
                .define('#', Tags.Items.COBBLESTONES)
                .define('/', CraftingItems.CRUDE_TOOL_PARTS)
                .unlockedBy("has_item", has(CraftingItems.CRUDE_TOOL_PARTS))
                .save(this.output);
    }

    private void registerSmithing() {
        SgItems.getItems(item -> item instanceof GearItem).forEach(item -> {
            if (((GearItem) item).getGearType() != GearTypes.ELYTRA.get()) {
                GearSmithingRecipeBuilder.coating(item).save(this.output);
            }
            GearSmithingRecipeBuilder.upgrade(item, PartTypes.MISC_UPGRADE.get()).save(this.output);
        });

        shapelessExt(RecipeCategory.MISC, SgItems.COATING_SMITHING_TEMPLATE)
                .requires(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE)
                .requires(SgTags.Items.BLUEPRINT_PAPER)
                .requires(SgTags.Items.GEMS_BORT)
                .unlockedBy("has_item", has(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE))
                .save(this.output);

        shapedExt(RecipeCategory.MISC, SgItems.COATING_SMITHING_TEMPLATE, 2)
                .pattern("dtd")
                .pattern("dnd")
                .pattern("ddd")
                .define('d', Tags.Items.GEMS_DIAMOND)
                .define('t', SgItems.COATING_SMITHING_TEMPLATE)
                .define('n', Tags.Items.NETHERRACKS)
                .unlockedBy("has_item", has(SgItems.COATING_SMITHING_TEMPLATE))
                .save(this.output, modId("coating_smithing_template_duplication"));
    }

    private void registerSalvaging() {
        SgItems.getItems(item -> item instanceof GearItem).forEach(item ->
                gearSalvage((GearItem) item));

        vanillaSalvage(Items.NETHERITE_SWORD, Items.DIAMOND, 2, 1, Items.NETHERITE_INGOT);
        vanillaSalvage(Items.NETHERITE_PICKAXE, Items.DIAMOND, 3, 2, Items.NETHERITE_INGOT);
        vanillaSalvage(Items.NETHERITE_SHOVEL, Items.DIAMOND, 1, 2, Items.NETHERITE_INGOT);
        vanillaSalvage(Items.NETHERITE_AXE, Items.DIAMOND, 3, 2, Items.NETHERITE_INGOT);
        vanillaSalvage(Items.NETHERITE_HOE, Items.DIAMOND, 2, 2, Items.NETHERITE_INGOT);
        vanillaSalvage(Items.NETHERITE_HELMET, Items.DIAMOND, 5, 0, Items.NETHERITE_INGOT);
        vanillaSalvage(Items.NETHERITE_CHESTPLATE, Items.DIAMOND, 8, 0, Items.NETHERITE_INGOT);
        vanillaSalvage(Items.NETHERITE_LEGGINGS, Items.DIAMOND, 7, 0, Items.NETHERITE_INGOT);
        vanillaSalvage(Items.NETHERITE_BOOTS, Items.DIAMOND, 4, 0, Items.NETHERITE_INGOT);

        vanillaSalvage(Items.DIAMOND_SWORD, Items.DIAMOND, 2, 1);
        vanillaSalvage(Items.DIAMOND_PICKAXE, Items.DIAMOND, 3, 2);
        vanillaSalvage(Items.DIAMOND_SHOVEL, Items.DIAMOND, 1, 2);
        vanillaSalvage(Items.DIAMOND_AXE, Items.DIAMOND, 3, 2);
        vanillaSalvage(Items.DIAMOND_HOE, Items.DIAMOND, 2, 2);
        vanillaSalvage(Items.DIAMOND_HELMET, Items.DIAMOND, 5, 0);
        vanillaSalvage(Items.DIAMOND_CHESTPLATE, Items.DIAMOND, 8, 0);
        vanillaSalvage(Items.DIAMOND_LEGGINGS, Items.DIAMOND, 7, 0);
        vanillaSalvage(Items.DIAMOND_BOOTS, Items.DIAMOND, 4, 0);

        vanillaSalvage(Items.GOLDEN_SWORD, Items.GOLD_INGOT, 2, 1);
        vanillaSalvage(Items.GOLDEN_PICKAXE, Items.GOLD_INGOT, 3, 2);
        vanillaSalvage(Items.GOLDEN_SHOVEL, Items.GOLD_INGOT, 1, 2);
        vanillaSalvage(Items.GOLDEN_AXE, Items.GOLD_INGOT, 3, 2);
        vanillaSalvage(Items.GOLDEN_HOE, Items.GOLD_INGOT, 2, 2);
        vanillaSalvage(Items.GOLDEN_HELMET, Items.GOLD_INGOT, 5, 0);
        vanillaSalvage(Items.GOLDEN_CHESTPLATE, Items.GOLD_INGOT, 8, 0);
        vanillaSalvage(Items.GOLDEN_LEGGINGS, Items.GOLD_INGOT, 7, 0);
        vanillaSalvage(Items.GOLDEN_BOOTS, Items.GOLD_INGOT, 4, 0);

        vanillaSalvage(Items.IRON_SWORD, Items.IRON_INGOT, 2, 1);
        vanillaSalvage(Items.IRON_PICKAXE, Items.IRON_INGOT, 3, 2);
        vanillaSalvage(Items.IRON_SHOVEL, Items.IRON_INGOT, 1, 2);
        vanillaSalvage(Items.IRON_AXE, Items.IRON_INGOT, 3, 2);
        vanillaSalvage(Items.IRON_HOE, Items.IRON_INGOT, 2, 2);
        vanillaSalvage(Items.IRON_HELMET, Items.IRON_INGOT, 5, 0);
        vanillaSalvage(Items.IRON_CHESTPLATE, Items.IRON_INGOT, 8, 0);
        vanillaSalvage(Items.IRON_LEGGINGS, Items.IRON_INGOT, 7, 0);
        vanillaSalvage(Items.IRON_BOOTS, Items.IRON_INGOT, 4, 0);
        vanillaSalvage(Items.SHEARS, Items.IRON_INGOT, 2, 0);

        vanillaSalvage(Items.LEATHER_HELMET, Items.LEATHER, 5, 0);
        vanillaSalvage(Items.LEATHER_CHESTPLATE, Items.LEATHER, 8, 0);
        vanillaSalvage(Items.LEATHER_LEGGINGS, Items.LEATHER, 7, 0);
        vanillaSalvage(Items.LEATHER_BOOTS, Items.LEATHER, 4, 0);
        vanillaSalvage(Items.LEATHER_HORSE_ARMOR, Items.LEATHER, 7, 0);

        vanillaSalvage(Items.STONE_SWORD, Items.COBBLESTONE, 2, 1);
        vanillaSalvage(Items.STONE_PICKAXE, Items.COBBLESTONE, 3, 2);
        vanillaSalvage(Items.STONE_SHOVEL, Items.COBBLESTONE, 1, 2);
        vanillaSalvage(Items.STONE_AXE, Items.COBBLESTONE, 3, 2);
        vanillaSalvage(Items.STONE_HOE, Items.COBBLESTONE, 2, 2);

        vanillaSalvage(Items.BOW, Items.STRING, 3, 3);

        SalvagingRecipeBuilder.builder(Items.DIAMOND_HORSE_ARMOR)
                .addResult(Items.DIAMOND, 6)
                .addResult(Items.LEATHER)
                .save(this.output, modId("salvaging/diamond_horse_armor"));

        SalvagingRecipeBuilder.builder(Items.GOLDEN_HORSE_ARMOR)
                .addResult(Items.GOLD_INGOT, 6)
                .addResult(Items.LEATHER)
                .save(this.output, modId("salvaging/golden_horse_armor"));

        SalvagingRecipeBuilder.builder(Items.IRON_HORSE_ARMOR)
                .addResult(Items.IRON_INGOT, 6)
                .addResult(Items.LEATHER)
                .save(this.output, modId("salvaging/iron_horse_armor"));

        SalvagingRecipeBuilder.builder(Items.CROSSBOW)
                .addResult(Items.STICK, 3)
                .addResult(Items.STRING, 2)
                .addResult(Items.IRON_INGOT)
                .addResult(Items.TRIPWIRE_HOOK)
                .save(this.output, modId("salvaging/crossbow"));

        SalvagingRecipeBuilder.builder(Items.CLOCK)
                .addResult(Items.GOLD_INGOT, 4)
                .addResult(Items.REDSTONE)
                .save(this.output, modId("salvaging/clock"));

        SalvagingRecipeBuilder.builder(Items.COMPASS)
                .addResult(Items.IRON_INGOT, 4)
                .addResult(Items.REDSTONE)
                .save(this.output, modId("salvaging/compass"));
    }

    private ExtendedShapelessRecipeBuilder<ShapelessRecipe> shapelessExt(RecipeCategory recipeCategory, ItemLike item) {
        return shapelessExt(recipeCategory, item, 1);
    }

    private ExtendedShapelessRecipeBuilder<ShapelessRecipe> shapelessExt(RecipeCategory recipeCategory, ItemLike item, int count) {
        return new ExtendedShapelessRecipeBuilder.Basic<>(this.items, recipeCategory, new ItemStack(item, count), ShapelessRecipe::new);
    }

    private ExtendedShapedRecipeBuilder.Basic<ShapedRecipe> shapedExt(RecipeCategory recipeCategory, ItemLike item) {
        return shapedExt(recipeCategory, item, 1);
    }

    private ExtendedShapedRecipeBuilder.Basic<ShapedRecipe> shapedExt(RecipeCategory recipeCategory, ItemLike item, int count) {
        return new ExtendedShapedRecipeBuilder.Basic<>(this.items, recipeCategory, new ItemStack(item, count), ShapedRecipe::new);
    }

    private void special(RecipeOutput consumer, RecipeSerializer<? extends CraftingRecipe> serializer, Function<CraftingBookCategory, Recipe<?>> factory) {
        SpecialRecipeBuilder.special(factory).save(consumer, NameUtils.fromRecipeSerializer(serializer).toString());
    }

    @SuppressWarnings("MethodWithTooManyParameters")
    private void toolRecipes(RecipeOutput consumer, String name, int mainCount, GearItemSet<?> itemSet) {
        // Tool head
        shapelessPart(RecipeCategory.TOOLS, itemSet.mainPart())
                .requires(BlueprintIngredient.of(itemSet))
                .requires(PartMaterialIngredient.of(PartTypes.MAIN.get(), itemSet.type()), mainCount)
                .save(this.output, modId("gear/" + name + "_head"));
        // Tool from head and rod
        shapelessGear(RecipeCategory.TOOLS, itemSet.gearItem())
                .requires(itemSet.mainPart())
                .requires(GearPartIngredient.of(PartTypes.ROD.get()))
                .save(this.output, modId("gear/" + name));
        // Quick tool (mains and rods, skipping head)
        shapelessGear(RecipeCategory.TOOLS, itemSet.gearItem())
                .requires(BlueprintIngredient.of(itemSet))
                .requires(PartMaterialIngredient.of(PartTypes.MAIN.get(), itemSet.type()), mainCount)
                .requires(GearPartIngredient.of(PartTypes.ROD.get()))
                .save(this.output, modId("gear/" + name + "_quick"));
    }

    private void maceRecipes() {
        var itemSet = GearItemSets.MACE;
        shapelessPart(RecipeCategory.TOOLS, itemSet.mainPart())
                .requires(BlueprintIngredient.of(itemSet))
                .requires(Items.HEAVY_CORE)
                .requires(PartMaterialIngredient.of(PartTypes.MAIN.get(), itemSet.type()), 3)
                .save(this.output, modId("gear/mace_core"));
        shapelessGear(RecipeCategory.TOOLS, itemSet.gearItem())
                .requires(itemSet.mainPart())
                .requires(GearPartIngredient.of(PartTypes.ROD.get()))
                .save(this.output, modId("gear/mace"));
        shapelessGear(RecipeCategory.TOOLS, itemSet.gearItem())
                .requires(BlueprintIngredient.of(itemSet))
                .requires(Items.HEAVY_CORE)
                .requires(PartMaterialIngredient.of(PartTypes.MAIN.get(), itemSet.type()), 3)
                .requires(GearPartIngredient.of(PartTypes.ROD.get()))
                .save(this.output, modId("gear/mace_quick"));
    }

    @SuppressWarnings("MethodWithTooManyParameters")
    private void bowRecipes(String name, int mainCount, GearItemSet<?> itemSet) {
        // Main part
        shapelessPart(RecipeCategory.COMBAT, itemSet.mainPart())
                .requires(BlueprintIngredient.of(itemSet))
                .requires(PartMaterialIngredient.of(PartTypes.MAIN.get(), itemSet.type()), mainCount)
                .save(this.output, modId("gear/" + name + "_main"));
        // Tool from main, rod, and cord
        shapelessGear(RecipeCategory.COMBAT, itemSet.gearItem())
                .requires(itemSet.mainPart())
                .requires(GearPartIngredient.of(PartTypes.ROD.get()))
                .requires(GearPartIngredient.of(PartTypes.CORD.get()))
                .save(this.output, modId("gear/" + name));
        // Quick tool (main materials, rod, and cord, skipping main part)
        shapelessGear(RecipeCategory.COMBAT, itemSet.gearItem())
                .requires(BlueprintIngredient.of(itemSet))
                .requires(PartMaterialIngredient.of(PartTypes.MAIN.get(), itemSet.type()), mainCount)
                .requires(GearPartIngredient.of(PartTypes.ROD.get()))
                .requires(GearPartIngredient.of(PartTypes.CORD.get()))
                .save(this.output, modId("gear/" + name + "_quick"));
    }

    private void arrowRecipes(String name, GearItemSet<?> itemSet) {
        BlueprintIngredient blueprint = BlueprintIngredient.of(itemSet);
        // Arrow head
        shapelessPart(RecipeCategory.COMBAT, itemSet.mainPart())
                .requires(blueprint)
                .requires(PartMaterialIngredient.of(PartTypes.MAIN.get(), itemSet.type()))
                .save(this.output, modId("gear/" + name + "_head"));
        // Arrows from head
        shapelessGear(RecipeCategory.COMBAT, itemSet.gearItem())
                .requires(itemSet.mainPart())
                .requires(GearPartIngredient.of(PartTypes.ROD.get()))
                .requires(GearPartIngredient.of(PartTypes.FLETCHING.get()))
                .save(this.output, modId("gear/" + name));
        // Quick arrows
        shapelessGear(RecipeCategory.COMBAT, itemSet.gearItem())
                .requires(BlueprintIngredient.of(itemSet))
                .requires(PartMaterialIngredient.of(PartTypes.MAIN.get(), itemSet.type()))
                .requires(GearPartIngredient.of(PartTypes.ROD.get()))
                .requires(GearPartIngredient.of(PartTypes.FLETCHING.get()))
                .save(this.output, modId("gear/" + name + "_quick"));
    }

    private void armorRecipes(int mainCount, GearItemSet<? extends GearArmorItem> itemSet) {
        shapelessPart(RecipeCategory.COMBAT, itemSet.mainPart())
                .requires(BlueprintIngredient.of(itemSet))
                .requires(PartMaterialIngredient.of(PartTypes.MAIN.get(), itemSet.type()), mainCount)
                .save(this.output, modId("gear/" + NameUtils.fromItem(itemSet.mainPart()).getPath()));

        shapelessGear(RecipeCategory.COMBAT, itemSet.gearItem())
                .requires(itemSet.mainPart())
                .save(this.output, modId("gear/" + NameUtils.fromItem(itemSet.gearItem()).getPath()));

        shapelessGear(RecipeCategory.COMBAT, itemSet.gearItem())
                .requires(itemSet.mainPart())
                .requires(GearPartIngredient.of(PartTypes.LINING.get()))
                .save(this.output, modId("gear/" + NameUtils.fromItem(itemSet.gearItem()).getPath() + "_with_lining"));
    }

    private void curioRecipes(String name, int mainCount, GearItemSet<?> itemSet) {
        shapelessPart(RecipeCategory.MISC, itemSet.mainPart())
                .requires(BlueprintIngredient.of(itemSet))
                .requires(PartMaterialIngredient.of(PartTypes.MAIN.get(), itemSet.type(), MaterialCategories.METAL), mainCount)
                .save(this.output, modId("gear/" + name + "_main_only"));

        shapelessGear(RecipeCategory.MISC, itemSet.gearItem())
                .requires(BlueprintIngredient.of(SgItems.JEWELER_TOOLS.get()))
                .requires(itemSet.mainPart())
                .save(this.output, modId("gear/" + name));

        shapelessGear(RecipeCategory.MISC, itemSet.gearItem())
                .requires(BlueprintIngredient.of(SgItems.JEWELER_TOOLS.get()))
                .requires(itemSet.mainPart())
                .requires(GearPartIngredient.of(PartTypes.SETTING.get()))
                .save(this.output, modId("gear/" + name + "_with_gem"));

        shapelessGear(RecipeCategory.MISC, itemSet.gearItem())
                .requires(BlueprintIngredient.of(itemSet))
                .requires(PartMaterialIngredient.of(PartTypes.MAIN.get(), itemSet.type(), MaterialCategories.METAL), mainCount)
                .requires(GearPartIngredient.of(PartTypes.SETTING.get()))
                .save(this.output, modId("gear/" + name + "_quick"));
    }

    private void toolBlueprint(String group, GearItemSet<?> itemSet, String... pattern) {
        toolBlueprint(group, itemSet, null, pattern);
    }

    private void toolBlueprint(String group, GearItemSet<?> itemSet, @Nullable Ingredient extra, String... pattern) {
        ShapedRecipeBuilder builderBlueprint = shaped(RecipeCategory.MISC, itemSet.blueprint())
                .group("silentgear:blueprints/" + group)
                .define('#', SgTags.Items.BLUEPRINT_PAPER)
                .define('/', Tags.Items.RODS_WOODEN)
                .unlockedBy("has_item", has(SgTags.Items.BLUEPRINT_PAPER));

        ShapedRecipeBuilder builderTemplate = shaped(RecipeCategory.MISC, itemSet.template())
                .group("silentgear:blueprints/" + group)
                .define('#', SgTags.Items.TEMPLATE_BOARDS)
                .define('/', Tags.Items.RODS_WOODEN)
                .unlockedBy("has_item", has(SgTags.Items.TEMPLATE_BOARDS));

        if (extra != null) {
            builderBlueprint.define('@', extra);
            builderTemplate.define('@', extra);
        }

        for (String line : pattern) {
            builderBlueprint.pattern(line);
            builderTemplate.pattern(line);
        }

        builderBlueprint.save(this.output);
        builderTemplate.save(this.output);
    }

    private void armorBlueprint(String group, GearItemSet<?> itemSet, String... pattern) {
        ShapedRecipeBuilder builderBlueprint = shaped(RecipeCategory.MISC, itemSet.blueprint())
                .group("silentgear:blueprints/" + group)
                .define('#', SgTags.Items.BLUEPRINT_PAPER)
                .unlockedBy("has_item", has(SgTags.Items.BLUEPRINT_PAPER));
        for (String line : pattern) {
            builderBlueprint.pattern(line);
        }
        builderBlueprint.save(this.output);

        ShapedRecipeBuilder builderTemplate = shaped(RecipeCategory.MISC, itemSet.template())
                .group("silentgear:blueprints/" + group)
                .define('#', SgTags.Items.TEMPLATE_BOARDS)
                .unlockedBy("has_item", has(SgTags.Items.TEMPLATE_BOARDS));
        for (String line : pattern) {
            builderTemplate.pattern(line);
        }
        builderTemplate.save(this.output);
    }

    private ShapelessConversionBuilder shapelessConversion(RecipeCategory category, GearItem result, List<PartInstance> parts) {
        return new ShapelessConversionBuilder(this.items, category, result, parts);
    }

    private void gearConversionRecipe(
            GearItemSet<? extends GearItem> itemSet,
            Item input,
            int mainCount,
            DataResource<Material> mainMaterial
    ) {
        gearConversionRecipe(itemSet, input, mainCount, mainMaterial, null, null);
    }

    private void gearConversionRecipe(
            GearItemSet<? extends GearItem> itemSet,
            Item input,
            int mainCount,
            DataResource<Material> mainMaterial,
            DataResource<Material> rodMaterial
    ) {
        gearConversionRecipe(itemSet, input, mainCount, mainMaterial, rodMaterial, null);
    }

    private void gearConversionRecipe(
            GearItemSet<? extends GearItem> itemSet,
            Item input,
            int mainCount,
            DataResource<Material> mainMaterial,
            @Nullable DataResource<Material> rodMaterial,
            @Nullable DataResource<Material> coatingMaterial
    ) {
        shapelessConversion(RecipeCategory.MISC, itemSet.gearItem(),
                buildConversionParts(
                        itemSet.type(),
                        DataResource.part(itemSet.partName()),
                        mainMaterial,
                        mainCount,
                        rodMaterial,
                        coatingMaterial
                ))
                .requires(input)
                .save(this.output, modId("gear/convert/" + NameUtils.fromItem(input).getPath()));
    }

    private void gearSalvage(GearItem item) {
        if (item == GearItemSets.ARROW.gearItem()) return;

        SalvagingRecipeBuilder.gearBuilder(item)
                .save(this.output, modId("salvaging/gear/" + NameUtils.fromItem(item).getPath()));
    }

    private void vanillaSalvage(ItemLike gear, ItemLike main, int mainCount, int rodCount) {
        vanillaSalvage(gear, main, mainCount, rodCount, null);
    }

    private void vanillaSalvage(ItemLike gear, ItemLike main, int mainCount, int rodCount, @Nullable ItemLike secondary) {
        var builder = SalvagingRecipeBuilder.builder(gear).addResult(main, mainCount);
        if (secondary != null) {
            builder.addResult(secondary);
        }
        if (rodCount > 0) {
            builder.addResult(Items.STICK, rodCount);
        }
        Identifier inputId = NameUtils.fromItem(gear);
        builder.save(this.output, modId("salvaging/" + inputId.getPath()));
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

    private ToolActionRecipeBuilder toolAction(TagKey<Item> tool, ItemLike ingredient, int damageToTool, ItemLike result, int count, SoundPlayback sound) {
        return new ToolActionRecipeBuilder(tag(tool), Ingredient.of(ingredient), damageToTool, new ItemStack(result, count), sound);
    }

    private ToolActionRecipeBuilder toolAction(TagKey<Item> tool, TagKey<Item> ingredient, int damageToTool, ItemLike result, int count, SoundPlayback sound) {
        return new ToolActionRecipeBuilder(tag(tool), tag(ingredient), damageToTool, new ItemStack(result, count), sound);
    }

    private void metals(float smeltingXp, Metals metal) {
        if (metal.ore != null && metal.oreTag != null) {
            var oreTagIngredient = tag(metal.oreTag);
            SimpleCookingRecipeBuilder.blasting(oreTagIngredient, RecipeCategory.MISC, metal.ingot, smeltingXp, 100)
                    .unlockedBy("has_item", has(metal.oreTag))
                    .save(this.output, modId(metal.name + "_ore_blasting"));
            SimpleCookingRecipeBuilder.smelting(oreTagIngredient, RecipeCategory.MISC, metal.ingot, smeltingXp, 200)
                    .unlockedBy("has_item", has(metal.oreTag))
                    .save(this.output, modId(metal.name + "_ore_smelting"));
        }

        if (metal.rawOre != null) {
            SimpleCookingRecipeBuilder.blasting(Ingredient.of(metal.rawOre), RecipeCategory.MISC, metal.ingot, smeltingXp, 100)
                    .unlockedBy("has_item", has(metal.rawOre))
                    .save(this.output, modId(metal.name + "_raw_ore_blasting"));
            SimpleCookingRecipeBuilder.smelting(Ingredient.of(metal.rawOre), RecipeCategory.MISC, metal.ingot, smeltingXp, 200)
                    .unlockedBy("has_item", has(metal.rawOre))
                    .save(this.output, modId(metal.name + "_raw_ore_smelting"));

            if (metal.rawOreBlock != null) {
                compressionRecipes(this.output, metal.rawOreBlock, metal.rawOre, null);
            }
        }

        var hasIngot = has(metal.ingotTag);

        if (metal.block != null) {
            compressionRecipes(this.output, metal.block, metal.ingot, metal.nugget);
        }

        if (metal.dustTag != null) {
            SimpleCookingRecipeBuilder.blasting(tag(metal.dustTag), RecipeCategory.MISC, metal.ingot, smeltingXp, 100)
                    .unlockedBy("has_item", hasIngot)
                    .save(this.output, modId(metal.name + "_dust_blasting"));
            SimpleCookingRecipeBuilder.smelting(tag(metal.dustTag), RecipeCategory.MISC, metal.ingot, smeltingXp, 200)
                    .unlockedBy("has_item", hasIngot)
                    .save(this.output, modId(metal.name + "_dust_smelting"));
        }

        if (metal.dust != null) {
            toolAction(SgTags.Items.TOOLS_HAMMER, metal.ingotTag, 1, metal.dust, 1, HAMMER_SOUND)
                    .save(this.output);
        }
    }

    @SuppressWarnings("WeakerAccess")
    private static class Metals {
        private final String name;
        @Nullable private ItemLike ore;
        @Nullable private TagKey<Item> oreTag;
        @Nullable private ItemLike rawOre;
        @Nullable private ItemLike rawOreBlock;
        @Nullable private ItemLike block;
        @Nullable private TagKey<Item> blockTag;
        private final ItemLike ingot;
        private final TagKey<Item> ingotTag;
        @Nullable private ItemLike nugget;
        @Nullable private TagKey<Item> nuggetTag;
        @Nullable private ItemLike dust;
        @Nullable private TagKey<Item> dustTag;

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
