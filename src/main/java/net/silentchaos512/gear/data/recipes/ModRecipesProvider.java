package net.silentchaos512.gear.data.recipes;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredItem;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.part.MaterialGrade;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.util.DataResource;
import net.silentchaos512.gear.crafting.ingredient.BlueprintIngredient;
import net.silentchaos512.gear.crafting.ingredient.GearPartIngredient;
import net.silentchaos512.gear.crafting.ingredient.PartMaterialIngredient;
import net.silentchaos512.gear.crafting.recipe.ShapedGearRecipe;
import net.silentchaos512.gear.crafting.recipe.ShapelessCompoundPartRecipe;
import net.silentchaos512.gear.crafting.recipe.ShapelessGearRecipe;
import net.silentchaos512.gear.gear.material.LazyMaterialInstance;
import net.silentchaos512.gear.gear.material.MaterialCategories;
import net.silentchaos512.gear.setup.SgBlocks;
import net.silentchaos512.gear.setup.SgItems;
import net.silentchaos512.gear.setup.SgRecipes;
import net.silentchaos512.gear.setup.SgTags;
import net.silentchaos512.gear.item.CraftingItems;
import net.silentchaos512.gear.item.RepairKitItem;
import net.silentchaos512.gear.item.blueprint.GearBlueprintItem;
import net.silentchaos512.gear.item.gear.GearArmorItem;
import net.silentchaos512.gear.util.Const;
import net.silentchaos512.lib.data.recipe.ExtendedShapedRecipeBuilder;
import net.silentchaos512.lib.data.recipe.ExtendedShapelessRecipeBuilder;
import net.silentchaos512.lib.data.recipe.ExtendedSingleItemRecipeBuilder;
import net.silentchaos512.lib.data.recipe.LibRecipeProvider;
import net.silentchaos512.lib.util.NameUtils;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Consumer;

public class ModRecipesProvider extends LibRecipeProvider {
    private static final boolean ADD_TEST_RECIPES = false;

    public ModRecipesProvider(DataGenerator generatorIn) {
        super(generatorIn, SilentGear.MOD_ID);
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
        shapedBuilder(RecipeCategory.MISC, Items.BUCKET)
                .pattern("# #")
                .pattern(" # ")
                .define('#', PartMaterialIngredient.builder(PartType.MAIN)
                        .withMaterial(DataResource.material("copper"))
                        .withGrade(MaterialGrade.A, null).build()
                )
                .save(consumer, modId("graded_mat_test"));
    }

    private void registerSpecialRecipes(RecipeOutput consumer) {
        special(consumer, (RecipeSerializer<? extends CraftingRecipe>) SgRecipes.FILL_REPAIR_KIT.get());
        special(consumer, (RecipeSerializer<? extends CraftingRecipe>) SgRecipes.SWAP_GEAR_PART.get());
        special(consumer, (RecipeSerializer<? extends CraftingRecipe>) SgRecipes.QUICK_REPAIR.get());
        special(consumer, (RecipeSerializer<? extends CraftingRecipe>) SgRecipes.COMBINE_FRAGMENTS.get());
        special(consumer, (RecipeSerializer<? extends CraftingRecipe>) SgRecipes.MOD_KIT_REMOVE_PART.get());
    }

    private void registerBlueprints(RecipeOutput consumer) {
        toolBlueprint(consumer, "sword", SgItems.SWORD_BLUEPRINT, SgItems.SWORD_TEMPLATE, "#", "#", "/");
        toolBlueprint(consumer, "katana", SgItems.KATANA_BLUEPRINT, SgItems.KATANA_TEMPLATE, "##", "# ", "/ ");
        toolBlueprint(consumer, "machete", SgItems.MACHETE_BLUEPRINT, SgItems.MACHETE_TEMPLATE, "  #", " ##", "/  ");
        toolBlueprint(consumer, "spear", SgItems.SPEAR_BLUEPRINT, SgItems.SPEAR_TEMPLATE, "#  ", " / ", "  /");
        toolBlueprint(consumer, "knife", SgItems.KNIFE_BLUEPRINT, SgItems.KNIFE_TEMPLATE, " #", "/ ");
        toolBlueprint(consumer, "dagger", SgItems.DAGGER_BLUEPRINT, SgItems.DAGGER_TEMPLATE, "#", "/");
        toolBlueprint(consumer, "pickaxe", SgItems.PICKAXE_BLUEPRINT, SgItems.PICKAXE_TEMPLATE, "###", " / ", " / ");
        toolBlueprint(consumer, "shovel", SgItems.SHOVEL_BLUEPRINT, SgItems.SHOVEL_TEMPLATE, "#", "/", "/");
        toolBlueprint(consumer, "axe", SgItems.AXE_BLUEPRINT, SgItems.AXE_TEMPLATE, "##", "#/", " /");
        toolBlueprint(consumer, "paxel", SgItems.PAXEL_BLUEPRINT, SgItems.PAXEL_TEMPLATE, "###", "#/#", " /#");
        toolBlueprint(consumer, "hammer", SgItems.HAMMER_BLUEPRINT, SgItems.HAMMER_TEMPLATE, "###", "###", " / ");
        toolBlueprint(consumer, "excavator", SgItems.EXCAVATOR_BLUEPRINT, SgItems.EXCAVATOR_TEMPLATE, "# #", "###", " / ");
        toolBlueprint(consumer, "saw", SgItems.SAW_BLUEPRINT, SgItems.SAW_TEMPLATE, "###", "##/", "  /");
        toolBlueprint(consumer, "hoe", SgItems.HOE_BLUEPRINT, SgItems.HOE_TEMPLATE, "##", " /", " /");
        toolBlueprint(consumer, "mattock", SgItems.MATTOCK_BLUEPRINT, SgItems.MATTOCK_TEMPLATE, "## ", "#/#", " / ");
        toolBlueprint(consumer, "prospector_hammer", SgItems.PROSPECTOR_HAMMER_BLUEPRINT, SgItems.PROSPECTOR_HAMMER_TEMPLATE,
                Ingredient.of(Tags.Items.INGOTS_IRON), "##", " /", " @");
        toolBlueprint(consumer, "sickle", SgItems.SICKLE_BLUEPRINT, SgItems.SICKLE_TEMPLATE, " #", "##", "/ ");
        toolBlueprint(consumer, "shears", SgItems.SHEARS_BLUEPRINT, SgItems.SHEARS_TEMPLATE, " #", "#/");
        toolBlueprint(consumer, "fishing_rod", SgItems.FISHING_ROD_BLUEPRINT, SgItems.FISHING_ROD_TEMPLATE, "  /", " /#", "/ #");
        toolBlueprint(consumer, "bow", SgItems.BOW_BLUEPRINT, SgItems.BOW_TEMPLATE, " #/", "# /", " #/");
        toolBlueprint(consumer, "crossbow", SgItems.CROSSBOW_BLUEPRINT, SgItems.CROSSBOW_TEMPLATE, "/#/", "###", " / ");
        toolBlueprint(consumer, "slingshot", SgItems.SLINGSHOT_BLUEPRINT, SgItems.SLINGSHOT_TEMPLATE, "# #", " / ", " / ");
        toolBlueprint(consumer, "shield", SgItems.SHIELD_BLUEPRINT, SgItems.SHIELD_TEMPLATE, "# #", "///", " # ");
        toolBlueprint(consumer, "arrow", SgItems.ARROW_BLUEPRINT, SgItems.ARROW_TEMPLATE, Ingredient.of(Tags.Items.FEATHERS), "#", "/", "@");
        armorBlueprint(consumer, "helmet", SgItems.HELMET_BLUEPRINT, SgItems.HELMET_TEMPLATE, "###", "# #");
        armorBlueprint(consumer, "chestplate", SgItems.CHESTPLATE_BLUEPRINT, SgItems.CHESTPLATE_TEMPLATE, "# #", "###", "###");
        armorBlueprint(consumer, "leggings", SgItems.LEGGINGS_BLUEPRINT, SgItems.LEGGINGS_TEMPLATE, "###", "# #", "# #");
        armorBlueprint(consumer, "boots", SgItems.BOOTS_BLUEPRINT, SgItems.BOOTS_TEMPLATE, "# #", "# #");

        new ShapedRecipeBuilder(RecipeCategory.MISC, SgItems.TRIDENT_BLUEPRINT)
                .define('#', SgTags.Items.BLUEPRINT_PAPER)
                .define('H', Items.HEART_OF_THE_SEA)
                .define('T', Items.TRIDENT)
                .pattern("#H#")
                .pattern("#T#")
                .pattern(" # ")
                .unlockedBy("has_item", has(Items.TRIDENT))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SgItems.ELYTRA_BLUEPRINT)
                .define('#', SgTags.Items.BLUEPRINT_PAPER)
                .define('/', Tags.Items.INGOTS_GOLD)
                .define('e', Items.ELYTRA)
                .define('p', Items.PHANTOM_MEMBRANE)
                .pattern("/e/")
                .pattern("p#p")
                .pattern("p p")
                .unlockedBy("has_item", has(SgTags.Items.BLUEPRINT_PAPER))
                .save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SgItems.ELYTRA_TEMPLATE)
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
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SgItems.RING_BLUEPRINT)
                .group("silentgear:blueprints/ring")
                .define('#', SgTags.Items.BLUEPRINT_PAPER)
                .define('/', PartMaterialIngredient.of(PartType.MAIN, GearType.CURIO, MaterialCategories.METAL))
                .pattern(" #/")
                .pattern("# #")
                .pattern("/# ")
                .unlockedBy("has_item", has(SgTags.Items.BLUEPRINT_PAPER))
                .save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SgItems.RING_BLUEPRINT)
                .group("silentgear:blueprints/ring")
                .define('#', SgTags.Items.BLUEPRINT_PAPER)
                .define('/', Tags.Items.INGOTS_GOLD)
                .pattern(" #/")
                .pattern("# #")
                .pattern("/# ")
                .unlockedBy("has_item", has(SgTags.Items.BLUEPRINT_PAPER))
                .save(consumer, SilentGear.getId("ring_blueprint_alt"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SgItems.RING_TEMPLATE)
                .group("silentgear:blueprints/ring")
                .define('#', SgTags.Items.TEMPLATE_BOARDS)
                .define('/', PartMaterialIngredient.of(PartType.MAIN, GearType.CURIO, MaterialCategories.METAL))
                .pattern(" #/")
                .pattern("# #")
                .pattern("/# ")
                .unlockedBy("has_item", has(SgTags.Items.TEMPLATE_BOARDS))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SgItems.BRACELET_BLUEPRINT)
                .group("silentgear:blueprints/bracelet")
                .define('#', SgTags.Items.BLUEPRINT_PAPER)
                .define('/', PartMaterialIngredient.of(PartType.MAIN, GearType.CURIO, MaterialCategories.METAL))
                .pattern("###")
                .pattern("# #")
                .pattern("/#/")
                .unlockedBy("has_item", has(SgTags.Items.BLUEPRINT_PAPER))
                .save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SgItems.BRACELET_BLUEPRINT)
                .group("silentgear:blueprints/bracelet")
                .define('#', SgTags.Items.BLUEPRINT_PAPER)
                .define('/', Tags.Items.INGOTS_GOLD)
                .pattern("###")
                .pattern("# #")
                .pattern("/#/")
                .unlockedBy("has_item", has(SgTags.Items.BLUEPRINT_PAPER))
                .save(consumer, SilentGear.getId("bracelet_blueprint_alt"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SgItems.BRACELET_TEMPLATE)
                .group("silentgear:blueprints/bracelet")
                .define('#', SgTags.Items.TEMPLATE_BOARDS)
                .define('/', PartMaterialIngredient.of(PartType.MAIN, GearType.CURIO, MaterialCategories.METAL))
                .pattern("###")
                .pattern("# #")
                .pattern("/#/")
                .unlockedBy("has_item", has(SgTags.Items.TEMPLATE_BOARDS))
                .save(consumer);

        // Part blueprints
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SgItems.JEWELER_TOOLS)
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

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, SgItems.BINDING_BLUEPRINT)
                .group("silentgear:blueprints/binding")
                .requires(Ingredient.of(SgTags.Items.BLUEPRINT_PAPER), 1)
                .requires(PartMaterialIngredient.of(PartType.BINDING, GearType.TOOL), 2)
                .unlockedBy("has_item", has(SgTags.Items.BLUEPRINT_PAPER))
                .save(consumer);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, SgItems.BINDING_BLUEPRINT)
                .group("silentgear:blueprints/binding")
                .requires(SgTags.Items.BLUEPRINT_PAPER)
                .requires(Tags.Items.STRING)
                .unlockedBy("has_item", has(SgTags.Items.BLUEPRINT_PAPER))
                .save(consumer, SilentGear.getId("binding_blueprint_alt"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, SgItems.BINDING_TEMPLATE)
                .group("silentgear:blueprints/binding")
                .requires(Ingredient.of(SgTags.Items.TEMPLATE_BOARDS), 1)
                .requires(PartMaterialIngredient.of(PartType.BINDING, GearType.TOOL), 2)
                .unlockedBy("has_item", has(SgTags.Items.TEMPLATE_BOARDS))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, SgItems.CORD_BLUEPRINT)
                .group("silentgear:blueprints/cord")
                .define('#', SgTags.Items.BLUEPRINT_PAPER)
                .define('/', PartMaterialIngredient.of(PartType.CORD, GearType.TOOL))
                .pattern("#/")
                .pattern("#/")
                .pattern("#/")
                .unlockedBy("has_item", has(SgTags.Items.BLUEPRINT_PAPER))
                .save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, SgItems.CORD_BLUEPRINT)
                .group("silentgear:blueprints/cord")
                .define('#', SgTags.Items.BLUEPRINT_PAPER)
                .define('/', Tags.Items.STRING)
                .pattern("#/")
                .pattern("#/")
                .pattern("#/")
                .unlockedBy("has_item", has(SgTags.Items.BLUEPRINT_PAPER))
                .save(consumer, SilentGear.getId("cord_blueprint_alt"));
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, SgItems.CORD_TEMPLATE)
                .group("silentgear:blueprints/cord")
                .define('#', SgTags.Items.TEMPLATE_BOARDS)
                .define('/', PartMaterialIngredient.of(PartType.CORD, GearType.TOOL))
                .pattern("#/")
                .pattern("#/")
                .pattern("#/")
                .unlockedBy("has_item", has(SgTags.Items.TEMPLATE_BOARDS))
                .save(consumer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, SgItems.FLETCHING_BLUEPRINT)
                .group("silentgear:blueprints/fletching")
                .requires(Ingredient.of(SgTags.Items.BLUEPRINT_PAPER), 2)
                .requires(Tags.Items.FEATHERS)
                .unlockedBy("has_item", has(SgTags.Items.BLUEPRINT_PAPER))
                .save(consumer);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, SgItems.FLETCHING_TEMPLATE)
                .group("silentgear:blueprints/fletching")
                .requires(Ingredient.of(SgTags.Items.TEMPLATE_BOARDS), 2)
                .requires(Tags.Items.FEATHERS)
                .unlockedBy("has_item", has(SgTags.Items.TEMPLATE_BOARDS))
                .save(consumer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, SgItems.GRIP_BLUEPRINT)
                .group("silentgear:blueprints/grip")
                .requires(Ingredient.of(SgTags.Items.BLUEPRINT_PAPER), 2)
                .requires(PartMaterialIngredient.of(PartType.GRIP, GearType.TOOL))
                .unlockedBy("has_item", has(SgTags.Items.BLUEPRINT_PAPER))
                .save(consumer);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, SgItems.GRIP_BLUEPRINT)
                .group("silentgear:blueprints/grip")
                .requires(Ingredient.of(SgTags.Items.BLUEPRINT_PAPER), 2)
                .requires(ItemTags.WOOL)
                .unlockedBy("has_item", has(SgTags.Items.BLUEPRINT_PAPER))
                .save(consumer, SilentGear.getId("grip_blueprint_alt"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, SgItems.GRIP_TEMPLATE)
                .group("silentgear:blueprints/grip")
                .requires(Ingredient.of(SgTags.Items.TEMPLATE_BOARDS), 2)
                .requires(PartMaterialIngredient.of(PartType.GRIP, GearType.TOOL))
                .unlockedBy("has_item", has(SgTags.Items.TEMPLATE_BOARDS))
                .save(consumer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, SgItems.LINING_BLUEPRINT)
                .group("silentgear:blueprints/lining")
                .requires(Ingredient.of(SgTags.Items.BLUEPRINT_PAPER), 3)
                .requires(Ingredient.of(ItemTags.WOOL), 2)
                .requires(Ingredient.of(Tags.Items.STRING), 2)
                .unlockedBy("has_item", has(SgTags.Items.BLUEPRINT_PAPER))
                .save(consumer);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, SgItems.LINING_TEMPLATE)
                .group("silentgear:blueprints/lining")
                .requires(Ingredient.of(SgTags.Items.TEMPLATE_BOARDS), 3)
                .requires(Ingredient.of(ItemTags.WOOL), 2)
                .requires(Ingredient.of(Tags.Items.STRING), 2)
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
                .requires(Tags.Items.STONE)
                .unlockedBy("has_item", has(SgTags.Items.BLUEPRINT_PAPER))
                .save(consumer);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, SgItems.TIP_TEMPLATE)
                .group("silentgear:blueprints/tip")
                .requires(Ingredient.of(SgTags.Items.TEMPLATE_BOARDS), 2)
                .requires(SgTags.Items.PAPER)
                .requires(Tags.Items.STONE)
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

    private ExtendedShapelessRecipeBuilder.Basic<ShapelessCompoundPartRecipe> compoundPart(RecipeCategory category, DeferredItem<?> item, int count) {
        return new ExtendedShapelessRecipeBuilder.Basic<>(category, item.toStack(count), ShapelessCompoundPartRecipe::new);
    }

    private void registerCompoundParts(RecipeOutput consumer) {
        compoundPart(SgItems.ADORNMENT, 1)
                .requires(BlueprintIngredient.of(SgItems.JEWELER_TOOLS.get()))
                .requires(CraftingItems.BORT)
                .requires(PartMaterialIngredient.of(PartType.ADORNMENT))
                .save(consumer, SilentGear.getId("part/adornment"));

        compoundPart(SgItems.ROD, 4)
                .requires(BlueprintIngredient.of(SgItems.ROD_BLUEPRINT.get()))
                .requires(PartMaterialIngredient.of(PartType.ROD), 2)
                .save(consumer, SilentGear.getId("part/rod"));

        compoundPart(SgItems.BINDING, 1)
                .requires(BlueprintIngredient.of(SgItems.BINDING_BLUEPRINT.get()))
                .requires(PartMaterialIngredient.of(PartType.BINDING))
                .save(consumer, SilentGear.getId("part/binding"));

        compoundPart(SgItems.BINDING, 2)
                .requires(BlueprintIngredient.of(SgItems.BINDING_BLUEPRINT.get()))
                .requires(PartMaterialIngredient.of(PartType.BINDING), 2)
                .save(consumer, SilentGear.getId("part/binding2"));

        compoundPart(SgItems.CORD, 1)
                .requires(BlueprintIngredient.of(SgItems.CORD_BLUEPRINT.get()))
                .requires(PartMaterialIngredient.of(PartType.CORD), 3)
                .save(consumer, SilentGear.getId("part/cord"));

        compoundPart(SgItems.FLETCHING, 1)
                .requires(BlueprintIngredient.of(SgItems.FLETCHING_BLUEPRINT.get()))
                .requires(PartMaterialIngredient.of(PartType.FLETCHING), 1)
                .save(consumer, SilentGear.getId("part/fletching"));

        compoundPart(SgItems.GRIP, 1)
                .requires(BlueprintIngredient.of(SgItems.GRIP_BLUEPRINT.get()))
                .requires(PartMaterialIngredient.of(PartType.GRIP))
                .save(consumer, SilentGear.getId("part/grip"));

        compoundPart(SgItems.GRIP, 2)
                .requires(BlueprintIngredient.of(SgItems.GRIP_BLUEPRINT.get()))
                .requires(PartMaterialIngredient.of(PartType.GRIP), 2)
                .save(consumer, SilentGear.getId("part/grip2"));

        compoundPart(SgItems.LINING, 1)
                .requires(BlueprintIngredient.of(SgItems.LINING_BLUEPRINT.get()))
                .requires(PartMaterialIngredient.of(PartType.LINING))
                .save(consumer, SilentGear.getId("part/lining"));

        compoundPart(SgItems.TIP, 1)
                .requires(BlueprintIngredient.of(SgItems.TIP_BLUEPRINT.get()))
                .requires(PartMaterialIngredient.of(PartType.TIP))
                .save(consumer, SilentGear.getId("part/tip"));

        compoundPart(SgItems.TIP, 2)
                .requires(BlueprintIngredient.of(SgItems.TIP_BLUEPRINT.get()))
                .requires(PartMaterialIngredient.of(PartType.TIP), 2)
                .save(consumer, SilentGear.getId("part/tip2"));

        compoundPart(SgItems.COATING, 1)
                .requires(BlueprintIngredient.of(SgItems.COATING_BLUEPRINT.get()))
                .requires(PartMaterialIngredient.of(PartType.COATING))
                .save(consumer, SilentGear.getId("part/coating"));

        compoundPart(SgItems.COATING, 1)
                .requires(BlueprintIngredient.of(SgItems.COATING_BLUEPRINT.get()))
                .requires(Items.GLASS_BOTTLE)
                .requires(PartMaterialIngredient.of(PartType.COATING))
                .save(consumer, SilentGear.getId("part/coating_alt"));

        compoundPart(SgItems.COATING, 2)
                .requires(BlueprintIngredient.of(SgItems.COATING_BLUEPRINT.get()))
                .requires(PartMaterialIngredient.of(PartType.COATING), 2)
                .save(consumer, SilentGear.getId("part/coating2"));

        compoundPart(SgItems.COATING, 2)
                .requires(BlueprintIngredient.of(SgItems.COATING_BLUEPRINT.get()))
                .requires(Items.GLASS_BOTTLE)
                .requires(PartMaterialIngredient.of(PartType.COATING), 2)
                .save(consumer, SilentGear.getId("part/coating2_alt"));
    }

    private ExtendedShapelessRecipeBuilder.Basic<ShapelessGearRecipe> shapelessGear(RecipeCategory category, DeferredItem<?> item) {
        return new ExtendedShapelessRecipeBuilder.Basic<>(category, item.toStack(), ShapelessGearRecipe::new);
    }

    private ExtendedShapedRecipeBuilder.Basic<ShapedGearRecipe> shapedGear(RecipeCategory category, DeferredItem<?> item) {
        return new ExtendedShapedRecipeBuilder.Basic<>(category, item.toStack(), ShapedGearRecipe::new);
    }

    private void registerGear(RecipeOutput consumer) {
        toolRecipes(consumer, "sword", 2, SgItems.SWORD, SgItems.SWORD_BLADE, SgItems.SWORD_BLUEPRINT.get());
        toolRecipes(consumer, "katana", 3, SgItems.KATANA, SgItems.KATANA_BLADE, SgItems.KATANA_BLUEPRINT.get());
        toolRecipes(consumer, "machete", 3, SgItems.MACHETE, SgItems.MACHETE_BLADE, SgItems.MACHETE_BLUEPRINT.get());
        toolRecipes(consumer, "spear", 1, SgItems.SPEAR, SgItems.SPEAR_TIP, SgItems.SPEAR_BLUEPRINT.get());
        toolRecipes(consumer, "trident", 3, SgItems.TRIDENT, SgItems.TRIDENT_PRONGS, SgItems.TRIDENT_BLUEPRINT.get());
        toolRecipes(consumer, "knife", 1, SgItems.KNIFE, SgItems.KNIFE_BLADE, SgItems.KNIFE_BLUEPRINT.get());
        toolRecipes(consumer, "dagger", 1, SgItems.DAGGER, SgItems.DAGGER_BLADE, SgItems.DAGGER_BLUEPRINT.get());
        toolRecipes(consumer, "pickaxe", 3, SgItems.PICKAXE, SgItems.PICKAXE_HEAD, SgItems.PICKAXE_BLUEPRINT.get());
        toolRecipes(consumer, "shovel", 1, SgItems.SHOVEL, SgItems.SHOVEL_HEAD, SgItems.SHOVEL_BLUEPRINT.get());
        toolRecipes(consumer, "axe", 3, SgItems.AXE, SgItems.AXE_HEAD, SgItems.AXE_BLUEPRINT.get());
        toolRecipes(consumer, "paxel", 5, SgItems.PAXEL, SgItems.PAXEL_HEAD, SgItems.PAXEL_BLUEPRINT.get());
        toolRecipes(consumer, "hammer", 6, SgItems.HAMMER, SgItems.HAMMER_HEAD, SgItems.HAMMER_BLUEPRINT.get());
        toolRecipes(consumer, "excavator", 5, SgItems.EXCAVATOR, SgItems.EXCAVATOR_HEAD, SgItems.EXCAVATOR_BLUEPRINT.get());
        toolRecipes(consumer, "hoe", 2, SgItems.HOE, SgItems.HOE_HEAD, SgItems.HOE_BLUEPRINT.get());
        toolRecipes(consumer, "mattock", 4, SgItems.MATTOCK, SgItems.MATTOCK_HEAD, SgItems.MATTOCK_BLUEPRINT.get());
        toolRecipes(consumer, "prospector_hammer", 2, SgItems.PROSPECTOR_HAMMER, SgItems.PROSPECTOR_HAMMER_HEAD, SgItems.PROSPECTOR_HAMMER_BLUEPRINT.get());
        toolRecipes(consumer, "saw", 5, SgItems.SAW, SgItems.SAW_BLADE, SgItems.SAW_BLUEPRINT.get());
        toolRecipes(consumer, "sickle", 3, SgItems.SICKLE, SgItems.SICKLE_BLADE, SgItems.SICKLE_BLUEPRINT.get());
        toolRecipes(consumer, "shears", 2, SgItems.SHEARS, SgItems.SHEARS_BLADES, SgItems.SHEARS_BLUEPRINT.get());
        bowRecipes(consumer, "fishing_rod", 2, SgItems.FISHING_ROD, SgItems.FISHING_REEL_AND_HOOK, SgItems.FISHING_ROD_BLUEPRINT.get());
        bowRecipes(consumer, "bow", 3, SgItems.BOW, SgItems.BOW_LIMBS, SgItems.BOW_BLUEPRINT.get());
        bowRecipes(consumer, "crossbow", 3, SgItems.CROSSBOW, SgItems.CROSSBOW_LIMBS, SgItems.CROSSBOW_BLUEPRINT.get());
        bowRecipes(consumer, "slingshot", 2, SgItems.SLINGSHOT, SgItems.SLINGSHOT_LIMBS, SgItems.SLINGSHOT_BLUEPRINT.get());
        arrowRecipes(consumer, "arrow", SgItems.ARROW, SgItems.ARROW_HEADS, SgItems.ARROW_BLUEPRINT.get());

        curioRecipes(consumer, "ring", 2, SgItems.RING, SgItems.RING_SHANK, SgItems.RING_BLUEPRINT.get());
        curioRecipes(consumer, "bracelet", 3, SgItems.BRACELET, SgItems.BRACELET_BAND, SgItems.BRACELET_BLUEPRINT.get());

        shapelessGear(RecipeCategory.COMBAT, SgItems.SHIELD)
                .requires(BlueprintIngredient.of(SgItems.SHIELD_BLUEPRINT.get()))
                .requires(PartMaterialIngredient.of(PartType.MAIN, GearType.ARMOR), 2)
                .requires(GearPartIngredient.of(PartType.ROD))
                .save(consumer, SilentGear.getId("gear/shield"));

        armorRecipes(consumer, 5, SgItems.HELMET.get(), SgItems.HELMET_PLATES, SgItems.HELMET_BLUEPRINT.get());
        armorRecipes(consumer, 8, SgItems.CHESTPLATE.get(), SgItems.CHESTPLATE_PLATES, SgItems.CHESTPLATE_BLUEPRINT.get());
        armorRecipes(consumer, 7, SgItems.LEGGINGS.get(), SgItems.LEGGING_PLATES, SgItems.LEGGINGS_BLUEPRINT.get());
        armorRecipes(consumer, 4, SgItems.BOOTS.get(), SgItems.BOOT_PLATES, SgItems.BOOTS_BLUEPRINT.get());

        compoundPart(RecipeCategory.COMBAT, SgItems.ELYTRA_WINGS, 1)
                .requires(BlueprintIngredient.of(SgItems.ELYTRA_BLUEPRINT.get()))
                .requires(PartMaterialIngredient.of(PartType.MAIN,
                        GearType.ELYTRA,
                        MaterialCategories.CLOTH,
                        MaterialCategories.SHEET), 6)
                .save(consumer, SilentGear.getId("gear/elytra_wings"));

        shapelessGear(RecipeCategory.COMBAT, SgItems.ELYTRA)
                .requires(SgItems.ELYTRA_WINGS)
                .requires(GearPartIngredient.of(PartType.BINDING))
                .save(consumer, SilentGear.getId("gear/elytra"));

        // Rough recipes
        shapedGear(RecipeCategory.COMBAT, SgItems.SWORD)
                .pattern("#")
                .pattern("#")
                .pattern("/")
                .define('#', PartMaterialIngredient.of(PartType.MAIN, GearType.TOOL))
                .define('/', SgTags.Items.RODS_ROUGH)
                .save(consumer, SilentGear.getId("gear/rough/sword"));
        shapedGear(RecipeCategory.COMBAT, SgItems.DAGGER)
                .pattern("#")
                .pattern("/")
                .define('#', PartMaterialIngredient.of(PartType.MAIN, GearType.TOOL))
                .define('/', SgTags.Items.RODS_ROUGH)
                .save(consumer, SilentGear.getId("gear/rough/dagger"));
        shapedGear(RecipeCategory.COMBAT, SgItems.KNIFE)
                .pattern(" #")
                .pattern("/ ")
                .define('#', PartMaterialIngredient.of(PartType.MAIN, GearType.TOOL))
                .define('/', SgTags.Items.RODS_ROUGH)
                .save(consumer, SilentGear.getId("gear/rough/knife"));
        shapedGear(RecipeCategory.TOOLS, SgItems.PICKAXE)
                .pattern("###")
                .pattern(" / ")
                .pattern(" / ")
                .define('#', PartMaterialIngredient.of(PartType.MAIN, GearType.TOOL))
                .define('/', SgTags.Items.RODS_ROUGH)
                .save(consumer, SilentGear.getId("gear/rough/pickaxe"));
        shapedGear(RecipeCategory.TOOLS, SgItems.SHOVEL)
                .pattern("#")
                .pattern("/")
                .pattern("/")
                .define('#', PartMaterialIngredient.of(PartType.MAIN, GearType.TOOL))
                .define('/', SgTags.Items.RODS_ROUGH)
                .save(consumer, SilentGear.getId("gear/rough/shovel"));
        shapedGear(RecipeCategory.TOOLS, SgItems.AXE)
                .pattern("##")
                .pattern("#/")
                .pattern(" /")
                .define('#', PartMaterialIngredient.of(PartType.MAIN, GearType.TOOL))
                .define('/', SgTags.Items.RODS_ROUGH)
                .save(consumer, SilentGear.getId("gear/rough/axe"));

        // Coonversion recipes
        toolConversion(consumer, SgItems.SWORD, Items.NETHERITE_SWORD, Items.DIAMOND_SWORD, Items.GOLDEN_SWORD, Items.IRON_SWORD, Items.STONE_SWORD, Items.WOODEN_SWORD);
        toolConversion(consumer, SgItems.PICKAXE, Items.NETHERITE_PICKAXE, Items.DIAMOND_PICKAXE, Items.GOLDEN_PICKAXE, Items.IRON_PICKAXE, Items.STONE_PICKAXE, Items.WOODEN_PICKAXE);
        toolConversion(consumer, SgItems.SHOVEL, Items.NETHERITE_SHOVEL, Items.DIAMOND_SHOVEL, Items.GOLDEN_SHOVEL, Items.IRON_SHOVEL, Items.STONE_SHOVEL, Items.WOODEN_SHOVEL);
        toolConversion(consumer, SgItems.AXE, Items.NETHERITE_AXE, Items.DIAMOND_AXE, Items.GOLDEN_AXE, Items.IRON_AXE, Items.STONE_AXE, Items.WOODEN_AXE);
        toolConversion(consumer, SgItems.HOE, Items.NETHERITE_HOE, Items.DIAMOND_HOE, Items.GOLDEN_HOE, Items.IRON_HOE, Items.STONE_HOE, Items.WOODEN_HOE);
        armorConversion(consumer, SgItems.HELMET, Items.NETHERITE_HELMET, Items.DIAMOND_HELMET, Items.GOLDEN_HELMET, Items.IRON_HELMET, Items.LEATHER_HELMET);
        armorConversion(consumer, SgItems.CHESTPLATE, Items.NETHERITE_CHESTPLATE, Items.DIAMOND_CHESTPLATE, Items.GOLDEN_CHESTPLATE, Items.IRON_CHESTPLATE, Items.LEATHER_CHESTPLATE);
        armorConversion(consumer, SgItems.LEGGINGS, Items.NETHERITE_LEGGINGS, Items.DIAMOND_LEGGINGS, Items.GOLDEN_LEGGINGS, Items.IRON_LEGGINGS, Items.LEATHER_LEGGINGS);
        armorConversion(consumer, SgItems.BOOTS, Items.NETHERITE_BOOTS, Items.DIAMOND_BOOTS, Items.GOLDEN_BOOTS, Items.IRON_BOOTS, Items.LEATHER_BOOTS);
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
                .define('o', Tags.Items.STONE)
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
            ExtendedShapelessRecipeBuilder.vanillaBuilder(RecipeCategory.MISC, item)
                    .requires(item)
                    .requires(Tags.Items.RODS_WOODEN)
                    .save(consumer, SilentGear.getId(NameUtils.fromItem(item).getPath() + "_empty"));
        }
    }

    private void registerMachines(RecipeOutput consumer) {
        ExtendedShapedRecipeBuilder.vanillaBuilder(RecipeCategory.DECORATIONS, SgBlocks.ALLOY_FORGE)
                .define('/', SgTags.Items.INGOTS_CRIMSON_STEEL)
                .define('i', Tags.Items.STORAGE_BLOCKS_IRON)
                .define('#', Blocks.BLACKSTONE)
                .pattern("/#/")
                .pattern("/ /")
                .pattern("#i#")
                .save(consumer);

        ExtendedShapedRecipeBuilder.vanillaBuilder(RecipeCategory.DECORATIONS, SgBlocks.RECRYSTALLIZER)
                .define('/', SgTags.Items.INGOTS_AZURE_ELECTRUM)
                .define('g', Tags.Items.STORAGE_BLOCKS_GOLD)
                .define('d', Tags.Items.GEMS_DIAMOND)
                .define('e', Tags.Items.GEMS_EMERALD)
                .define('#', Blocks.PURPUR_BLOCK)
                .pattern("/e/")
                .pattern("/d/")
                .pattern("#g#")
                .save(consumer);

        ExtendedShapedRecipeBuilder.vanillaBuilder(RecipeCategory.DECORATIONS, SgBlocks.REFABRICATOR)
                .define('/', Tags.Items.INGOTS_IRON)
                .define('i', Tags.Items.STORAGE_BLOCKS_IRON)
                .define('d', Tags.Items.GEMS_DIAMOND)
                .define('b', SgTags.Items.GEMS_BORT)
                .define('#', ItemTags.PLANKS)
                .pattern("/ /")
                .pattern("dbd")
                .pattern("#i#")
                .save(consumer);

        ExtendedShapedRecipeBuilder.vanillaBuilder(RecipeCategory.DECORATIONS, SgBlocks.METAL_PRESS)
                .define('#', Tags.Items.OBSIDIAN)
                .define('t', SgTags.Items.INGOTS_TYRIAN_STEEL)
                .define('/', SgTags.Items.RODS_IRON)
                .pattern("#t#")
                .pattern("/ /")
                .pattern("#t#")
                .save(consumer);

        ExtendedShapedRecipeBuilder.vanillaBuilder(RecipeCategory.DECORATIONS, SgBlocks.STARLIGHT_CHARGER)
                .define('#', Blocks.POLISHED_BLACKSTONE)
                .define('/', SgTags.Items.STORAGE_BLOCKS_BLAZE_GOLD)
                .define('q', Tags.Items.STORAGE_BLOCKS_QUARTZ)
                .define('g', Tags.Items.GLASS_COLORLESS)
                .pattern("qgq")
                .pattern("#g#")
                .pattern("#/#")
                .save(consumer);
    }

    private void registerCompounding(RecipeOutput consumer) {
        CompoundingRecipeBuilder.gemBuilder(SgItems.CUSTOM_GEM, 1)
                .withCustomMaterial(Const.Materials.DIMERALD)
                .addIngredient(Tags.Items.GEMS_DIAMOND)
                .addIngredient(Tags.Items.GEMS_EMERALD)
                .build(consumer);

        CompoundingRecipeBuilder.metalBuilder(SgItems.CUSTOM_INGOT, 1)
                .withCustomMaterial(DataResource.material("high_carbon_steel"))
                .addIngredient(Tags.Items.INGOTS_IRON)
                .addIngredient(ItemTags.COALS, 3)
                .build(consumer);

        CompoundingRecipeBuilder.metalBuilder(CraftingItems.TYRIAN_STEEL_INGOT, 4)
                .addIngredient(SgTags.Items.INGOTS_CRIMSON_STEEL)
                .addIngredient(SgTags.Items.INGOTS_AZURE_ELECTRUM)
                .addIngredient(CraftingItems.CRUSHED_SHULKER_SHELL)
                .addIngredient(Items.NETHERITE_SCRAP)
                .build(consumer);

        CompoundingRecipeBuilder.metalBuilder(CraftingItems.BLAZE_GOLD_INGOT, 1)
                .addIngredient(Tags.Items.INGOTS_GOLD)
                .addIngredient(Items.BLAZE_POWDER, 2)
                .build(consumer);

        CompoundingRecipeBuilder.metalBuilder(CraftingItems.CRIMSON_STEEL_INGOT, 3)
                .addIngredient(SgTags.Items.STORAGE_BLOCKS_CRIMSON_IRON)
                .addIngredient(Tags.Items.RODS_BLAZE, 2)
                .addIngredient(Items.MAGMA_CREAM)
                .build(consumer);
    }

    private void registerPressing(RecipeOutput consumer) {
        ExtendedSingleItemRecipeBuilder.builder(SgRecipes.PRESSING_MATERIAL.get(),
                        RecipeCategory.MISC,
                        PartMaterialIngredient.of(PartType.MAIN, MaterialCategories.METAL),
                        SgItems.SHEET_METAL, 2)
                .build(consumer);
    }

    private void registerCraftingItems(RecipeOutput consumer) {
        shapelessBuilder(RecipeCategory.MISC, SgItems.GUIDE_BOOK)
                .requires(Items.BOOK)
                .requires(SgTags.Items.TEMPLATE_BOARDS)
                .unlockedBy("has_template_board", has(SgTags.Items.TEMPLATE_BOARDS))
                .save(consumer);

        /*damageGear(CraftingItems.GLOWING_DUST, 4, 4)
                .requires()(ModTags.Items.HAMMERS)
                .requires()(Tags.Items.DUSTS_GLOWSTONE, 2)
                .requires()(Tags.Items.GEMS_QUARTZ)
                .save();(consumer);*/

        shapelessBuilder(RecipeCategory.MISC, CraftingItems.GLOWING_DUST, 4)
                .requires(Items.STICK)
                .requires(Tags.Items.DUSTS_GLOWSTONE, 2)
                .requires(Tags.Items.GEMS_QUARTZ)
                .save(consumer);

        damageGear(SgItems.PEBBLE, 9, 1)
                .requires(SgTags.Items.HAMMERS)
                .requires(Tags.Items.COBBLESTONE)
                .save(consumer);

        /*damageGear(CraftingItems.TEMPLATE_BOARD, 6, 1)
                .requires()(ModTags.Items.KNIVES)
                .requires()(ItemTags.LOGS)
                .save();(consumer);*/

        shapelessBuilder(RecipeCategory.MISC, CraftingItems.TEMPLATE_BOARD, 6)
                .requires(Items.FLINT)
                .requires(ItemTags.LOGS)
                .save(consumer);

        /*damageGear(CraftingItems.CRUSHED_SHULKER_SHELL, 1, 10)
                .requires()(ModTags.Items.HAMMERS)
                .requires()(Items.SHULKER_SHELL)
                .save();(consumer);*/

        shapelessBuilder(RecipeCategory.MISC, CraftingItems.CRUSHED_SHULKER_SHELL, 1)
                .requires(Tags.Items.OBSIDIAN)
                .requires(Items.SHULKER_SHELL)
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

        shapelessBuilder(RecipeCategory.MISC, CraftingItems.WIDE_PLATE_UPGRADE)
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

        ExtendedShapelessRecipeBuilder.vanillaBuilder(RecipeCategory.MISC, Items.FEATHER)
                .requires(CraftingItems.FLUFFY_FEATHER)
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CraftingItems.FLUFFY_STRING)
                .pattern("###")
                .define('#', CraftingItems.FLUFFY_PUFF)
                .unlockedBy("has_item", has(CraftingItems.FLUFFY_PUFF))
                .save(consumer);

        ExtendedShapelessRecipeBuilder.vanillaBuilder(RecipeCategory.MISC, Items.STRING)
                .requires(CraftingItems.FLUFFY_STRING)
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.WHITE_WOOL)
                .pattern("###")
                .pattern("#~#")
                .pattern("###")
                .define('#', CraftingItems.FLUFFY_PUFF)
                .define('~', Tags.Items.STRING)
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

        ExtendedShapelessRecipeBuilder.vanillaBuilder(RecipeCategory.MISC, CraftingItems.NETHER_STAR_FRAGMENT, 9)
                .requires(Items.NETHER_STAR)
                .save(consumer);

        ExtendedShapedRecipeBuilder.vanillaBuilder(RecipeCategory.MISC, Items.NETHER_STAR)
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', CraftingItems.NETHER_STAR_FRAGMENT)
                .save(consumer, SilentGear.getId("nether_star_from_fragments"));

        ExtendedShapelessRecipeBuilder.vanillaBuilder(RecipeCategory.MISC, CraftingItems.STARMETAL_DUST, 3)
                .requires(SgTags.Items.DUSTS_AZURE_ELECTRUM, 1)
                .requires(SgTags.Items.DUSTS_AZURE_SILVER, 2)
                .requires(SgTags.Items.DUSTS_BLAZE_GOLD, 1)
                .requires(CraftingItems.NETHER_STAR_FRAGMENT)
                .save(consumer);

        shapelessBuilder(RecipeCategory.MISC, CraftingItems.BRONZE_INGOT, 4)
                .requires(Tags.Items.INGOTS_COPPER, 3)
                .requires(Tags.Items.INGOTS_IRON, 1)
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
                .define('#', Tags.Items.OBSIDIAN)
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
                .define('#', Tags.Items.COBBLESTONE)
                .pattern("#")
                .pattern("#")
                .unlockedBy("has_item", has(Tags.Items.COBBLESTONE))
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
                .requires(Tags.Items.STONE)
                .unlockedBy("has_item", has(ItemTags.PLANKS))
                .save(consumer);
    }

    private void dyeFluffyBlock(RecipeOutput consumer, ItemLike block, TagKey<Item> dye) {
        shapedBuilder(RecipeCategory.BUILDING_BLOCKS, block, 8)
                .pattern("###")
                .pattern("#d#")
                .pattern("###")
                .define('#', SgTags.Items.FLUFFY_BLOCKS)
                .define('d', dye)
                .unlockedBy("has_item", has(SgBlocks.WHITE_FLUFFY_BLOCK))
                .save(consumer);
    }

    private void registerSmithing(RecipeOutput consumer) {
        SgItems.getItems(item -> item instanceof ICoreItem).forEach(item -> {
            if (((ICoreItem) item).getGearType() != GearType.ELYTRA) {
                GearSmithingRecipeBuilder.coating(item).build(consumer);
            }
            GearSmithingRecipeBuilder.upgrade(item, PartType.MISC_UPGRADE).build(consumer);
        });

        shapelessBuilder(RecipeCategory.MISC, SgItems.COATING_SMITHING_TEMPLATE)
                .requires(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE)
                .requires(SgTags.Items.BLUEPRINT_PAPER)
                .requires(SgTags.Items.GEMS_BORT)
                .unlockedBy("has_item", has(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE))
                .save(consumer);

        shapedBuilder(RecipeCategory.MISC, SgItems.COATING_SMITHING_TEMPLATE, 2)
                .pattern("dtd")
                .pattern("dnd")
                .pattern("ddd")
                .define('d', Tags.Items.GEMS_DIAMOND)
                .define('t', SgItems.COATING_SMITHING_TEMPLATE)
                .define('n', Tags.Items.NETHERRACK)
                .unlockedBy("has_item", has(SgItems.COATING_SMITHING_TEMPLATE))
                .save(consumer, SilentGear.getId("coating_smithing_template_duplication"));
    }

    private void registerSalvaging(RecipeOutput consumer) {
        SgItems.getItems(item -> item instanceof ICoreItem).forEach(item ->
                gearSalvage(consumer, (ICoreItem) item));

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
                .build(consumer, SilentGear.getId("salvaging/diamond_horse_armor"));

        SalvagingRecipeBuilder.builder(Items.GOLDEN_HORSE_ARMOR)
                .addResult(Items.GOLD_INGOT, 6)
                .addResult(Items.LEATHER)
                .build(consumer, SilentGear.getId("salvaging/golden_horse_armor"));

        SalvagingRecipeBuilder.builder(Items.IRON_HORSE_ARMOR)
                .addResult(Items.IRON_INGOT, 6)
                .addResult(Items.LEATHER)
                .build(consumer, SilentGear.getId("salvaging/iron_horse_armor"));

        SalvagingRecipeBuilder.builder(Items.CROSSBOW)
                .addResult(Items.STICK, 3)
                .addResult(Items.STRING, 2)
                .addResult(Items.IRON_INGOT)
                .addResult(Items.TRIPWIRE_HOOK)
                .build(consumer, SilentGear.getId("salvaging/crossbow"));

        SalvagingRecipeBuilder.builder(Items.CLOCK)
                .addResult(Items.GOLD_INGOT, 4)
                .addResult(Items.REDSTONE)
                .build(consumer, SilentGear.getId("salvaging/clock"));

        SalvagingRecipeBuilder.builder(Items.COMPASS)
                .addResult(Items.IRON_INGOT, 4)
                .addResult(Items.REDSTONE)
                .build(consumer, SilentGear.getId("salvaging/compass"));
    }

    private void special(RecipeOutput consumer, RecipeSerializer<? extends CraftingRecipe> serializer) {
        SpecialRecipeBuilder.special(serializer).save(consumer, NameUtils.fromRecipeSerializer(serializer).toString());
    }

    private ExtendedShapelessRecipeBuilder damageGear(ItemLike result, int count, int damage) {
        return shapelessBuilder(SgRecipes.DAMAGE_ITEM.get(), RecipeCategory.MISC, result, count)
                .addExtraData(json -> json.addProperty("damage", damage));
    }

    @SuppressWarnings("MethodWithTooManyParameters")
    private void toolRecipes(RecipeOutput consumer, String name, int mainCount, ItemLike tool, ItemLike toolHead, GearBlueprintItem blueprintItem) {
        // Tool head
        shapelessBuilder(SgRecipes.COMPOUND_PART.get(), RecipeCategory.TOOLS, toolHead)
                .requires(BlueprintIngredient.of(blueprintItem))
                .requires(PartMaterialIngredient.of(PartType.MAIN, GearType.TOOL), mainCount)
                .save(consumer, SilentGear.getId("gear/" + name + "_head"));
        // Tool from head and rod
        shapelessBuilder(SgRecipes.SHAPELESS_GEAR.get(), RecipeCategory.TOOLS, tool)
                .requires(toolHead)
                .requires(GearPartIngredient.of(PartType.ROD))
                .save(consumer, SilentGear.getId("gear/" + name));
        // Quick tool (mains and rods, skipping head)
        shapelessBuilder(SgRecipes.SHAPELESS_GEAR.get(), RecipeCategory.TOOLS, tool)
                .requires(BlueprintIngredient.of(blueprintItem))
                .requires(PartMaterialIngredient.of(PartType.MAIN, GearType.TOOL), mainCount)
                .requires(GearPartIngredient.of(PartType.ROD))
                .save(consumer, SilentGear.getId("gear/" + name + "_quick"));
    }

    @SuppressWarnings("MethodWithTooManyParameters")
    private void bowRecipes(RecipeOutput consumer, String name, int mainCount, ItemLike tool, ItemLike toolHead, GearBlueprintItem blueprintItem) {
        // Main part
        shapelessBuilder(SgRecipes.COMPOUND_PART.get(), RecipeCategory.COMBAT, toolHead)
                .requires(BlueprintIngredient.of(blueprintItem))
                .requires(PartMaterialIngredient.of(PartType.MAIN, GearType.TOOL), mainCount)
                .save(consumer, SilentGear.getId("gear/" + name + "_main"));
        // Tool from main, rod, and cord
        shapelessBuilder(SgRecipes.SHAPELESS_GEAR.get(), RecipeCategory.COMBAT, tool)
                .requires(toolHead)
                .requires(GearPartIngredient.of(PartType.ROD))
                .requires(GearPartIngredient.of(PartType.CORD))
                .save(consumer, SilentGear.getId("gear/" + name));
        // Quick tool (main materials, rod, and cord, skipping main part)
        shapelessBuilder(SgRecipes.SHAPELESS_GEAR.get(), RecipeCategory.COMBAT, tool)
                .requires(BlueprintIngredient.of(blueprintItem))
                .requires(PartMaterialIngredient.of(PartType.MAIN, GearType.TOOL), mainCount)
                .requires(GearPartIngredient.of(PartType.ROD))
                .requires(GearPartIngredient.of(PartType.CORD))
                .save(consumer, SilentGear.getId("gear/" + name + "_quick"));
    }

    private void arrowRecipes(RecipeOutput consumer, String name, ItemLike arrow, ItemLike arrowHead, GearBlueprintItem blueprintItem) {
        BlueprintIngredient blueprint = BlueprintIngredient.of(blueprintItem);
        // Arrow head
        shapelessBuilder(SgRecipes.COMPOUND_PART.get(), RecipeCategory.COMBAT, arrowHead)
                .requires(blueprint)
                .requires(PartMaterialIngredient.of(PartType.MAIN, GearType.PROJECTILE))
                .save(consumer, SilentGear.getId("gear/" + name + "_head"));
        // Arrows from head
        shapelessBuilder(SgRecipes.SHAPELESS_GEAR.get(), RecipeCategory.COMBAT, arrow)
                .requires(arrowHead)
                .requires(GearPartIngredient.of(PartType.ROD))
                .requires(GearPartIngredient.of(PartType.FLETCHING))
                .save(consumer, SilentGear.getId("gear/" + name));
        // Quick arrows
        shapelessBuilder(SgRecipes.SHAPELESS_GEAR.get(), RecipeCategory.COMBAT, arrow)
                .requires(BlueprintIngredient.of(blueprintItem))
                .requires(PartMaterialIngredient.of(PartType.MAIN, GearType.TOOL))
                .requires(GearPartIngredient.of(PartType.ROD))
                .requires(GearPartIngredient.of(PartType.FLETCHING))
                .save(consumer, SilentGear.getId("gear/" + name + "_quick"));
    }

    private void armorRecipes(RecipeOutput consumer, int mainCount, GearArmorItem armor, ItemLike plates, GearBlueprintItem blueprintItem) {
        shapelessBuilder(SgRecipes.COMPOUND_PART.get(), RecipeCategory.COMBAT, plates)
                .requires(BlueprintIngredient.of(blueprintItem))
                .requires(PartMaterialIngredient.of(PartType.MAIN, armor.getGearType()), mainCount)
                .save(consumer, SilentGear.getId("gear/" + NameUtils.fromItem(plates).getPath()));

        shapelessBuilder(SgRecipes.SHAPELESS_GEAR.get(), RecipeCategory.COMBAT, armor)
                .requires(plates)
                .save(consumer, SilentGear.getId("gear/" + NameUtils.fromItem(armor).getPath()));

        shapelessBuilder(SgRecipes.SHAPELESS_GEAR.get(), RecipeCategory.COMBAT, armor)
                .requires(plates)
                .requires(GearPartIngredient.of(PartType.LINING))
                .save(consumer, SilentGear.getId("gear/" + NameUtils.fromItem(armor).getPath() + "_with_lining"));
    }

    private void curioRecipes(RecipeOutput consumer, String name, int mainCount, ItemLike curioItem, ItemLike curioMain, GearBlueprintItem blueprint) {
        shapelessBuilder(SgRecipes.COMPOUND_PART.get(), RecipeCategory.MISC, curioMain)
                .requires(BlueprintIngredient.of(blueprint))
                .requires(PartMaterialIngredient.of(PartType.MAIN, GearType.CURIO, MaterialCategories.METAL), mainCount)
                .save(consumer, SilentGear.getId("gear/" + name + "_main_only"));

        shapelessBuilder(SgRecipes.SHAPELESS_GEAR.get(), RecipeCategory.MISC, curioItem)
                .requires(BlueprintIngredient.of(SgItems.JEWELER_TOOLS.get()))
                .requires(curioMain)
                .save(consumer, SilentGear.getId("gear/" + name));

        shapelessBuilder(SgRecipes.SHAPELESS_GEAR.get(), RecipeCategory.MISC, curioItem)
                .requires(BlueprintIngredient.of(SgItems.JEWELER_TOOLS.get()))
                .requires(curioMain)
                .requires(GearPartIngredient.of(PartType.ADORNMENT))
                .save(consumer, SilentGear.getId("gear/" + name + "_with_gem"));

        shapelessBuilder(SgRecipes.SHAPELESS_GEAR.get(), RecipeCategory.MISC, curioItem)
                .requires(BlueprintIngredient.of(blueprint))
                .requires(PartMaterialIngredient.of(PartType.MAIN, GearType.CURIO, MaterialCategories.METAL), mainCount)
                .requires(GearPartIngredient.of(PartType.ADORNMENT))
                .save(consumer, SilentGear.getId("gear/" + name + "quick"));
    }

    private void toolBlueprint(RecipeOutput consumer, String group, ItemLike blueprint, ItemLike template, String... pattern) {
        toolBlueprint(consumer, group, blueprint, template, Ingredient.EMPTY, pattern);
    }

    private void toolBlueprint(RecipeOutput consumer, String group, ItemLike blueprint, ItemLike template, Ingredient extra, String... pattern) {
        ShapedRecipeBuilder builderBlueprint = ShapedRecipeBuilder.shaped(RecipeCategory.MISC, blueprint)
                .group("silentgear:blueprints/" + group)
                .define('#', SgTags.Items.BLUEPRINT_PAPER)
                .define('/', Tags.Items.RODS_WOODEN)
                .unlockedBy("has_item", has(SgTags.Items.BLUEPRINT_PAPER));

        ShapedRecipeBuilder builderTemplate = ShapedRecipeBuilder.shaped(RecipeCategory.MISC, template)
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

    private void armorBlueprint(RecipeOutput consumer, String group, ItemLike blueprint, ItemLike template, String... pattern) {
        ShapedRecipeBuilder builderBlueprint = ShapedRecipeBuilder.shaped(RecipeCategory.MISC, blueprint)
                .group("silentgear:blueprints/" + group)
                .define('#', SgTags.Items.BLUEPRINT_PAPER)
                .unlockedBy("has_item", has(SgTags.Items.BLUEPRINT_PAPER));
        for (String line : pattern) {
            builderBlueprint.pattern(line);
        }
        builderBlueprint.save(consumer);

        ShapedRecipeBuilder builderTemplate = ShapedRecipeBuilder.shaped(RecipeCategory.MISC, template)
                .group("silentgear:blueprints/" + group)
                .define('#', SgTags.Items.TEMPLATE_BOARDS)
                .unlockedBy("has_item", has(SgTags.Items.TEMPLATE_BOARDS));
        for (String line : pattern) {
            builderTemplate.pattern(line);
        }
        builderTemplate.save(consumer);
    }

    private static final Map<Tier, ResourceLocation> TOOL_MATERIALS = ImmutableMap.<Tier, ResourceLocation>builder()
            .put(Tiers.NETHERITE, SilentGear.getId("diamond"))
            .put(Tiers.DIAMOND, SilentGear.getId("diamond"))
            .put(Tiers.GOLD, SilentGear.getId("gold"))
            .put(Tiers.IRON, SilentGear.getId("iron"))
            .put(Tiers.STONE, SilentGear.getId("stone"))
            .put(Tiers.WOOD, SilentGear.getId("wood"))
            .build();
    private static final Map<ArmorMaterial, ResourceLocation> ARMOR_MATERIALS = ImmutableMap.<ArmorMaterial, ResourceLocation>builder()
            .put(ArmorMaterials.DIAMOND, SilentGear.getId("diamond"))
            .put(ArmorMaterials.GOLD, SilentGear.getId("gold"))
            .put(ArmorMaterials.IRON, SilentGear.getId("iron"))
            .put(ArmorMaterials.LEATHER, SilentGear.getId("leather"))
            .build();

    private void toolConversion(RecipeOutput consumer, ItemLike result, Item... toolItems) {
        for (Item input : toolItems) {
            assert input instanceof TieredItem;
            Tier tier = ((TieredItem) input).getTier();
            ResourceLocation coating = tier == Tiers.NETHERITE ? SilentGear.getId("netherite") : null;
            shapelessBuilder(SgRecipes.CONVERSION.get(), RecipeCategory.MISC, result)
                    .requires(input)
                    .addExtraData(json -> {
                        ResourceLocation material = TOOL_MATERIALS.getOrDefault(tier, SilentGear.getId("emerald"));
                        json.getAsJsonObject("result").add("materials", buildMaterials(material, SilentGear.getId("wood"), coating));
                    })
                    .save(consumer, SilentGear.getId("gear/convert/" + NameUtils.fromItem(input).getPath()));
        }
    }

    private void armorConversion(RecipeOutput consumer, ItemLike result, Item... armorItems) {
        for (Item input : armorItems) {
            assert input instanceof ArmorItem;
            ArmorMaterial armorMaterial = ((ArmorItem) input).getMaterial();
            ResourceLocation coating = armorMaterial == ArmorMaterials.NETHERITE ? SilentGear.getId("netherite") : null;
            shapelessBuilder(SgRecipes.CONVERSION.get(), RecipeCategory.MISC, result)
                    .requires(input)
                    .addExtraData(json -> {
                        ResourceLocation material = ARMOR_MATERIALS.getOrDefault(armorMaterial, SilentGear.getId("emerald"));
                        json.getAsJsonObject("result").add("materials", buildMaterials(material, null, coating));
                    })
                    .save(consumer, SilentGear.getId("gear/convert/" + NameUtils.fromItem(input).getPath()));
        }
    }

    private static void gearSalvage(RecipeOutput consumer, ICoreItem item) {
        SalvagingRecipeBuilder.gearBuilder(item)
                .build(consumer, SilentGear.getId("salvaging/gear/" + NameUtils.fromItem(item).getPath()));
    }

    private static void vanillaSalvage(RecipeOutput consumer, ItemLike gear, ItemLike main, int mainCount, int rodCount) {
        vanillaSalvage(consumer, gear, main, mainCount, rodCount, null);
    }

    private static void vanillaSalvage(RecipeOutput consumer, ItemLike gear, ItemLike main, int mainCount, int rodCount, @Nullable ItemLike secondary) {
        SalvagingRecipeBuilder builder = SalvagingRecipeBuilder.builder(gear).addResult(main, mainCount);
        if (secondary != null) {
            builder.addResult(secondary);
        }
        if (rodCount > 0) {
            builder.addResult(Items.STICK, rodCount);
        }
        ResourceLocation inputId = NameUtils.fromItem(gear);
        builder.build(consumer, SilentGear.getId("salvaging/" + inputId.getPath()));
    }

    private static JsonObject buildMaterials(ResourceLocation main, @Nullable ResourceLocation rod, @Nullable ResourceLocation coating) {
        JsonObject json = new JsonObject();
        json.add("main", LazyMaterialInstance.of(main).serialize());
        if (rod != null) {
            json.add("rod", LazyMaterialInstance.of(rod).serialize());
        }
        if (coating != null) {
            json.add("coating", LazyMaterialInstance.of(coating).serialize());
        }
        return json;
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

        InventoryChangeTrigger.TriggerInstance hasIngot = has(metal.ingotTag);

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
            damageGear(metal.dust, 1, 1)
                    .requires(SgTags.Items.HAMMERS)
                    .requires(metal.ingotTag)
                    .unlockedBy("has_item", hasIngot)
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
