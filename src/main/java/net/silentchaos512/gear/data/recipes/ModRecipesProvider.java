package net.silentchaos512.gear.data.recipes;

import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.block.Blocks;
import net.minecraft.data.*;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.init.ModBlocks;
import net.silentchaos512.gear.init.ModItems;
import net.silentchaos512.gear.init.ModTags;
import net.silentchaos512.gear.item.CraftingItems;

import java.util.function.Consumer;

public class ModRecipesProvider extends RecipeProvider {
    public ModRecipesProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    public String getName() {
        return "Silent Gear - Recipes";
    }

    @Override
    protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
        metals(consumer, 1.0f, new Metals("blaze_gold", CraftingItems.BLAZE_GOLD_INGOT, ModTags.Items.INGOTS_BLAZE_GOLD)
                .block(ModBlocks.BLAZE_GOLD_BLOCK, ModTags.Items.STORAGE_BLOCKS_BLAZE_GOLD)
                .dust(CraftingItems.BLAZE_GOLD_DUST, ModTags.Items.DUSTS_BLAZE_GOLD)
                .nugget(CraftingItems.BLAZE_GOLD_NUGGET, ModTags.Items.NUGGETS_BLAZE_GOLD));
        metals(consumer, 1.0f, new Metals("crimson_iron", CraftingItems.CRIMSON_IRON_INGOT, ModTags.Items.INGOTS_CRIMSON_IRON)
                .block(ModBlocks.CRIMSON_IRON_BLOCK, ModTags.Items.STORAGE_BLOCKS_CRIMSON_IRON)
                .dust(CraftingItems.CRIMSON_IRON_DUST, ModTags.Items.DUSTS_CRIMSON_IRON)
                .ore(ModBlocks.CRIMSON_IRON_ORE, ModTags.Items.ORES_CRIMSON_IRON)
                .nugget(CraftingItems.CRIMSON_IRON_NUGGET, ModTags.Items.NUGGETS_CRIMSON_IRON));
        metals(consumer, 1.0f, new Metals("crimson_steel", CraftingItems.CRIMSON_STEEL_INGOT, ModTags.Items.INGOTS_CRIMSON_STEEL)
                .block(ModBlocks.CRIMSON_STEEL_BLOCK, ModTags.Items.STORAGE_BLOCKS_CRIMSON_STEEL)
                .nugget(CraftingItems.CRIMSON_STEEL_NUGGET, ModTags.Items.NUGGETS_CRIMSON_STEEL));

        // Blueprints
        // TODO

        // A
        ShapedRecipeBuilder.shapedRecipe(CraftingItems.ADVANCED_UPGRADE_BASE)
                .key('/', ModTags.Items.NUGGETS_DIAMOND)
                .key('D', Tags.Items.DYES_BLUE)
                .key('U', CraftingItems.UPGRADE_BASE)
                .key('G', Tags.Items.NUGGETS_GOLD)
                .patternLine("///")
                .patternLine("DUD")
                .patternLine("GGG")
                .addCriterion("has_item", hasItem(CraftingItems.UPGRADE_BASE))
                .build(consumer);
        // B
        ShapelessRecipeBuilder.shapelessRecipe(CraftingItems.BLAZE_GOLD_INGOT)
                .addIngredient(Tags.Items.INGOTS_GOLD)
                .addIngredient(Items.BLAZE_POWDER, 4)
                .addCriterion("has_item", hasItem(Items.BLAZE_POWDER))
                .build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(CraftingItems.BLAZING_DUST, 4)
                .addIngredient(ModTags.Items.DUSTS_BLAZE_GOLD)
                .addIngredient(Ingredient.fromTag(Tags.Items.DUSTS_GLOWSTONE), 2)
                .addCriterion("has_item", hasItem(ModTags.Items.DUSTS_BLAZE_GOLD))
                .build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(CraftingItems.BLUEPRINT_PAPER, 4)
                .addIngredient(Ingredient.fromTag(ModTags.Items.PAPER), 4)
                .addIngredient(Tags.Items.DYES_BLUE)
                .addCriterion("has_paper", hasItem(ModTags.Items.PAPER))
                .build(consumer);
        // C
        ShapelessRecipeBuilder.shapelessRecipe(Blocks.COBBLESTONE)
                .addIngredient(ModItems.pebble, 9)
                .addCriterion("has_pebble", hasItem(ModItems.pebble))
                .build(consumer, SilentGear.getId("cobblestone_from_pebbles"));
        ShapedRecipeBuilder.shapedRecipe(ModBlocks.CRAFTING_STATION)
                .key('#', ItemTags.PLANKS)
                .key('C', Tags.Items.CHESTS_WOODEN)
                .key('T', Blocks.CRAFTING_TABLE)
                .key('U', CraftingItems.UPGRADE_BASE)
                .patternLine("#T#")
                .patternLine("#U#")
                .patternLine("#C#")
                .addCriterion("has_item", hasItem(Blocks.CRAFTING_TABLE))
                .build(consumer);
        ShapedRecipeBuilder.shapedRecipe(CraftingItems.CRIMSON_STEEL_INGOT)
                .key('/', Tags.Items.RODS_BLAZE)
                .key('#', ModTags.Items.INGOTS_CRIMSON_IRON)
                .key('C', Items.MAGMA_CREAM)
                .patternLine("/ /")
                .patternLine("#C#")
                .patternLine("# #")
                .addCriterion("has_item", hasItem(CraftingItems.CRIMSON_IRON_INGOT))
                .build(consumer);
        // D
        ShapelessRecipeBuilder.shapelessRecipe(CraftingItems.DIAMOND_SHARD, 9)
                .addIngredient(Tags.Items.GEMS_DIAMOND)
                .addCriterion("has_item", hasItem(Tags.Items.GEMS_DIAMOND))
                .build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Items.DIAMOND)
                .key('#', ModTags.Items.NUGGETS_DIAMOND)
                .patternLine("###")
                .patternLine("###")
                .patternLine("###")
                .addCriterion("has_item", hasItem(Tags.Items.GEMS_DIAMOND))
                .build(consumer, SilentGear.getId("diamond_from_shards"));
        ShapelessRecipeBuilder.shapelessRecipe(CraftingItems.DIAMOND_TIPPED_UPGRADE)
                .setGroup("silentgear:tip_upgrades")
                .addIngredient(CraftingItems.UPGRADE_BASE)
                .addIngredient(Tags.Items.GEMS_DIAMOND)
                .addCriterion("has_item", hasItem(Tags.Items.GEMS_DIAMOND))
                .build(consumer);
        CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(CraftingItems.SINEW), CraftingItems.DRIED_SINEW, 0.35f, 200)
                .addCriterion("has_item", hasItem(CraftingItems.SINEW))
                .build(consumer);
        // E
        ShapelessRecipeBuilder.shapelessRecipe(CraftingItems.EMERALD_SHARD, 9)
                .addIngredient(Tags.Items.GEMS_EMERALD)
                .addCriterion("has_item", hasItem(Tags.Items.GEMS_EMERALD))
                .build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Items.EMERALD)
                .key('#', ModTags.Items.NUGGETS_EMERALD)
                .patternLine("###")
                .patternLine("###")
                .patternLine("###")
                .addCriterion("has_item", hasItem(Tags.Items.GEMS_EMERALD))
                .build(consumer, SilentGear.getId("emerald_from_shards"));
        ShapelessRecipeBuilder.shapelessRecipe(CraftingItems.EMERALD_TIPPED_UPGRADE)
                .setGroup("silentgear:tip_upgrades")
                .addIngredient(CraftingItems.UPGRADE_BASE)
                .addIngredient(Tags.Items.GEMS_EMERALD)
                .addCriterion("has_item", hasItem(Tags.Items.GEMS_EMERALD))
                .build(consumer);
        // F
        ShapedRecipeBuilder.shapedRecipe(CraftingItems.FLAX_BOWSTRING)
                .key('/', CraftingItems.FLAX_STRING)
                .patternLine("/")
                .patternLine("/")
                .patternLine("/")
                .addCriterion("has_item", hasItem(CraftingItems.FLAX_FIBER))
                .build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(CraftingItems.FLAX_BOWSTRING)
                .addIngredient(ModTags.Items.BLUEPRINTS_BOWSTRING)
                .addIngredient(CraftingItems.FLAX_STRING, 3)
                .addCriterion("has_item", hasItem(ModTags.Items.BLUEPRINTS_BOWSTRING))
                .build(consumer, SilentGear.getId("flax_bowstring2"));
        ShapelessRecipeBuilder.shapelessRecipe(CraftingItems.FLAX_STRING)
                .addIngredient(CraftingItems.FLAX_FIBER, 2)
                .addCriterion("has_item", hasItem(CraftingItems.FLAX_FIBER))
                .build(consumer);
        // G
        ShapedRecipeBuilder.shapedRecipe(CraftingItems.GLITTERY_DUST, 8)
                .key('o', Items.POPPED_CHORUS_FRUIT)
                .key('/', ModTags.Items.NUGGETS_EMERALD)
                .key('#', Tags.Items.DUSTS_GLOWSTONE)
                .key('b', ModItems.netherBanana)
                .patternLine("o/o")
                .patternLine("#b#")
                .patternLine("o/o")
                .addCriterion("has_item", hasItem(ModItems.netherBanana))
                .build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(CraftingItems.GLOWSTONE_COATED_UPGRADE)
                .setGroup("silentgear:tip_upgrades")
                .addIngredient(CraftingItems.UPGRADE_BASE)
                .addIngredient(Ingredient.fromTag(Tags.Items.DUSTS_GLOWSTONE), 4)
                .addCriterion("has_item", hasItem(Tags.Items.DUSTS_GLOWSTONE))
                .build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(CraftingItems.GOLD_TIPPED_UPGRADE)
                .setGroup("silentgear:tip_upgrades")
                .addIngredient(CraftingItems.UPGRADE_BASE)
                .addIngredient(Tags.Items.INGOTS_GOLD)
                .addCriterion("has_item", hasItem(Tags.Items.INGOTS_GOLD))
                .build(consumer);
        ShapedRecipeBuilder.shapedRecipe(ModItems.goldenNetherBanana)
                .key('g', Tags.Items.INGOTS_GOLD)
                .key('b', ModItems.netherBanana)
                .patternLine("ggg")
                .patternLine("gbg")
                .patternLine("ggg")
                .addCriterion("has_item", hasItem(ModItems.netherBanana))
                .build(consumer);
        // I
        ShapedRecipeBuilder.shapedRecipe(CraftingItems.IRON_ROD, 4)
                .key('/', Tags.Items.INGOTS_IRON)
                .patternLine("/")
                .patternLine("/")
                .addCriterion("has_item", hasItem(Items.IRON_INGOT))
                .build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(CraftingItems.IRON_ROD, 2)
                .addIngredient(ModTags.Items.BLUEPRINTS_ROD)
                .addIngredient(Ingredient.fromTag(Tags.Items.INGOTS_IRON), 2)
                .addCriterion("has_item", hasItem(ModTags.Items.BLUEPRINTS_ROD))
                .build(consumer, SilentGear.getId("iron_rod2"));
        ShapelessRecipeBuilder.shapelessRecipe(CraftingItems.IRON_TIPPED_UPGRADE)
                .setGroup("silentgear:tip_upgrades")
                .addIngredient(CraftingItems.UPGRADE_BASE)
                .addIngredient(Tags.Items.INGOTS_IRON)
                .addCriterion("has_item", hasItem(Tags.Items.INGOTS_IRON))
                .build(consumer);
        // L
        ShapelessRecipeBuilder.shapelessRecipe(CraftingItems.LAPIS_COATED_UPGRADE)
                .setGroup("silentgear:tip_upgrades")
                .addIngredient(CraftingItems.UPGRADE_BASE)
                .addIngredient(Ingredient.fromTag(Tags.Items.GEMS_LAPIS), 4)
                .addCriterion("has_item", hasItem(Tags.Items.GEMS_LAPIS))
                .build(consumer);
        ShapedRecipeBuilder.shapedRecipe(Items.LEATHER)
                .key('#', CraftingItems.LEATHER_SCRAP)
                .patternLine("###")
                .patternLine("###")
                .patternLine("###")
                .addCriterion("has_item", hasItem(CraftingItems.LEATHER_SCRAP))
                .build(consumer, SilentGear.getId("leather_from_scraps"));
        ShapelessRecipeBuilder.shapelessRecipe(CraftingItems.LEATHER_SCRAP, 9)
                .addIngredient(Items.LEATHER)
                .addCriterion("has_item", hasItem(CraftingItems.LEATHER_SCRAP))
                .build(consumer);
        // M
        ShapedRecipeBuilder.shapedRecipe(ModBlocks.MATERIAL_GRADER)
                .key('Q', Tags.Items.GEMS_QUARTZ)
                .key('I', Tags.Items.INGOTS_IRON)
                .key('#', CraftingItems.ADVANCED_UPGRADE_BASE)
                .key('G', ModTags.Items.INGOTS_BLAZE_GOLD)
                .patternLine("QIQ")
                .patternLine("I#I")
                .patternLine("GGG")
                .addCriterion("has_item", hasItem(ModTags.Items.INGOTS_BLAZE_GOLD))
                .build(consumer);
        // N
        ShapelessRecipeBuilder.shapelessRecipe(ModBlocks.NETHERWOOD_PLANKS, 4)
                .addIngredient(ModBlocks.NETHERWOOD_LOG)
                .addCriterion("has_item", hasItem(ModBlocks.NETHERWOOD_LOG))
                .build(consumer);
        ShapedRecipeBuilder.shapedRecipe(ModBlocks.NETHERWOOD_PLANKS)
                .key('#', ModBlocks.NETHERWOOD_SLAB)
                .patternLine("#")
                .patternLine("#")
                .addCriterion("has_item", hasItem(ModBlocks.NETHERWOOD_LOG))
                .build(consumer, SilentGear.getId("netherwood_planks_from_slabs"));
        ShapedRecipeBuilder.shapedRecipe(ModBlocks.NETHERWOOD_PLANKS, 3)
                .key('#', ModBlocks.NETHERWOOD_STAIRS)
                .patternLine("##")
                .patternLine("##")
                .addCriterion("has_item", hasItem(ModBlocks.NETHERWOOD_LOG))
                .build(consumer, SilentGear.getId("netherwood_planks_from_stairs"));
        ShapedRecipeBuilder.shapedRecipe(ModBlocks.NETHERWOOD_SLAB, 6)
                .key('#', ModBlocks.NETHERWOOD_PLANKS)
                .patternLine("###")
                .addCriterion("has_item", hasItem(ModBlocks.NETHERWOOD_LOG))
                .build(consumer);
        ShapedRecipeBuilder.shapedRecipe(ModBlocks.NETHERWOOD_STAIRS, 4)
                .key('#', ModBlocks.NETHERWOOD_PLANKS)
                .patternLine("#  ")
                .patternLine("## ")
                .patternLine("###")
                .addCriterion("has_item", hasItem(ModBlocks.NETHERWOOD_LOG))
                .build(consumer);
        ShapedRecipeBuilder.shapedRecipe(CraftingItems.NETHERWOOD_STICK, 4)
                .key('#', ModBlocks.NETHERWOOD_PLANKS)
                .patternLine(" #")
                .patternLine("# ")
                .addCriterion("has_item", hasItem(ModBlocks.NETHERWOOD_LOG))
                .build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(CraftingItems.NETHERWOOD_STICK, 4)
                .addIngredient(ModTags.Items.BLUEPRINTS_ROD)
                .addIngredient(ModBlocks.NETHERWOOD_PLANKS, 2)
                .addCriterion("has_item", hasItem(ModBlocks.NETHERWOOD_LOG))
                .build(consumer, SilentGear.getId("netherwood_stick2"));
        // P
        ShapedRecipeBuilder.shapedRecipe(CraftingItems.PLAIN_BOWSTRING)
                .key('/', Items.STRING)
                .patternLine("/")
                .patternLine("/")
                .patternLine("/")
                .addCriterion("has_item", hasItem(ModTags.Items.BLUEPRINTS_BOWSTRING))
                .build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(CraftingItems.PLAIN_BOWSTRING)
                .addIngredient(ModTags.Items.BLUEPRINTS_BOWSTRING)
                .addIngredient(Items.STRING, 3)
                .addCriterion("has_item", hasItem(ModTags.Items.BLUEPRINTS_BOWSTRING))
                .build(consumer, SilentGear.getId("plain_bowstring2"));
        // Q
        ShapelessRecipeBuilder.shapelessRecipe(CraftingItems.QUARTZ_TIPPED_UPGRADE)
                .setGroup("silentgear:tip_upgrades")
                .addIngredient(CraftingItems.UPGRADE_BASE)
                .addIngredient(Ingredient.fromTag(Tags.Items.GEMS_QUARTZ), 4)
                .addCriterion("has_item", hasItem(Tags.Items.GEMS_QUARTZ))
                .build(consumer);
        // R
        ShapelessRecipeBuilder.shapelessRecipe(CraftingItems.RED_CARD_UPGRADE, 4)
                .addIngredient(CraftingItems.UPGRADE_BASE)
                .addIngredient(Tags.Items.DYES_RED)
                .addCriterion("has_item", hasItem(CraftingItems.UPGRADE_BASE))
                .build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(CraftingItems.REDSTONE_COATED_UPGRADE)
                .setGroup("silentgear:tip_upgrades")
                .addIngredient(CraftingItems.UPGRADE_BASE)
                .addIngredient(Ingredient.fromTag(Tags.Items.DUSTS_REDSTONE), 4)
                .addCriterion("has_item", hasItem(Tags.Items.DUSTS_REDSTONE))
                .build(consumer);
        ShapedRecipeBuilder.shapedRecipe(CraftingItems.ROUGH_ROD, 2)
                .key('/', Tags.Items.RODS_WOODEN)
                .patternLine(" /")
                .patternLine("/ ")
                .addCriterion("has_item", hasItem(Items.STICK))
                .build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(CraftingItems.ROUGH_ROD, 2)
                .addIngredient(ModTags.Items.BLUEPRINTS_ROD)
                .addIngredient(Ingredient.fromTag(Tags.Items.RODS_WOODEN), 2)
                .addCriterion("has_item", hasItem(ModTags.Items.BLUEPRINTS_ROD))
                .build(consumer, SilentGear.getId("rough_rod2"));
        // S
        ShapedRecipeBuilder.shapedRecipe(ModBlocks.SALVAGER)
                .key('P', Blocks.PISTON)
                .key('/', ModTags.Items.INGOTS_CRIMSON_IRON)
                .key('I', Tags.Items.STORAGE_BLOCKS_IRON)
                .key('#', Tags.Items.OBSIDIAN)
                .patternLine(" P ")
                .patternLine("/I/")
                .patternLine("/#/")
                .addCriterion("has_item", hasItem(ModTags.Items.INGOTS_CRIMSON_IRON))
                .build(consumer);
        ShapedRecipeBuilder.shapedRecipe(CraftingItems.SINEW_BOWSTRING)
                .key('/', CraftingItems.SINEW_FIBER)
                .patternLine("/")
                .patternLine("/")
                .patternLine("/")
                .addCriterion("has_item", hasItem(ModTags.Items.BLUEPRINTS_BOWSTRING))
                .build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(CraftingItems.SINEW_BOWSTRING)
                .addIngredient(ModTags.Items.BLUEPRINTS_BOWSTRING)
                .addIngredient(CraftingItems.SINEW_FIBER, 3)
                .addCriterion("has_item", hasItem(ModTags.Items.BLUEPRINTS_BOWSTRING))
                .build(consumer, SilentGear.getId("sinew_bowstring2"));
        ShapelessRecipeBuilder.shapelessRecipe(CraftingItems.SINEW_FIBER, 3)
                .addIngredient(CraftingItems.DRIED_SINEW)
                .addCriterion("has_item", hasItem(CraftingItems.SINEW))
                .build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(CraftingItems.SPOON_UPGRADE)
                .addIngredient(CraftingItems.ADVANCED_UPGRADE_BASE)
                .addIngredient(Items.DIAMOND_SHOVEL)
                .addCriterion("has_item", hasItem(CraftingItems.UPGRADE_BASE))
                .build(consumer);
        ShapedRecipeBuilder.shapedRecipe(CraftingItems.STONE_ROD, 4)
                .key('#', Tags.Items.COBBLESTONE)
                .patternLine("#")
                .patternLine("#")
                .addCriterion("has_item", hasItem(Tags.Items.COBBLESTONE))
                .build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(CraftingItems.STONE_ROD, 4)
                .addIngredient(ModTags.Items.BLUEPRINTS_ROD)
                .addIngredient(Ingredient.fromTag(Tags.Items.COBBLESTONE), 2)
                .addCriterion("has_item", hasItem(Tags.Items.COBBLESTONE))
                .build(consumer, SilentGear.getId("stone_rod2"));
        ShapedRecipeBuilder.shapedRecipe(ModBlocks.STONE_TORCH, 4)
                .key('#', ItemTags.COALS)
                .key('/', Tags.Items.RODS_WOODEN)
                .patternLine("#")
                .patternLine("/")
                .addCriterion("has_item", hasItem(ItemTags.COALS))
                .build(consumer);
        // U
        ShapelessRecipeBuilder.shapelessRecipe(CraftingItems.UPGRADE_BASE, 4)
                .addIngredient(Ingredient.fromTag(ModTags.Items.PAPER), 2)
                .addIngredient(ItemTags.PLANKS)
                .addIngredient(Tags.Items.STONE)
                .addCriterion("has_item", hasItem(ItemTags.PLANKS))
                .build(consumer);
    }

    private void metals(Consumer<IFinishedRecipe> consumer, float smeltingXp, Metals metal) {
        if (metal.ore != null) {
            CookingRecipeBuilder.blastingRecipe(Ingredient.fromTag(metal.oreTag), metal.ingot, smeltingXp, 100)
                    .addCriterion("has_item", hasItem(metal.oreTag))
                    .build(consumer, SilentGear.getId(metal.name + "_ore_blasting"));
            CookingRecipeBuilder.smeltingRecipe(Ingredient.fromTag(metal.oreTag), metal.ingot, smeltingXp, 200)
                    .addCriterion("has_item", hasItem(metal.oreTag))
                    .build(consumer, SilentGear.getId(metal.name + "_ore_smelting"));
        }

        InventoryChangeTrigger.Instance hasIngot = hasItem(metal.ingotTag);

        if (metal.block != null) {
            ShapelessRecipeBuilder.shapelessRecipe(metal.ingot, 9)
                    .addIngredient(metal.blockTag)
                    .addCriterion("has_item", hasIngot)
                    .build(consumer, new ResourceLocation(metal.ingot.asItem().getRegistryName() + "_from_block"));
            ShapedRecipeBuilder.shapedRecipe(metal.block)
                    .key('#', metal.ingotTag)
                    .patternLine("###")
                    .patternLine("###")
                    .patternLine("###")
                    .addCriterion("has_item", hasIngot)
                    .build(consumer);
        }
        if (metal.nugget != null) {
            ShapelessRecipeBuilder.shapelessRecipe(metal.nugget, 9)
                    .addIngredient(metal.ingotTag)
                    .addCriterion("has_item", hasIngot)
                    .build(consumer);
            ShapedRecipeBuilder.shapedRecipe(metal.ingot)
                    .key('#', metal.nuggetTag)
                    .patternLine("###")
                    .patternLine("###")
                    .patternLine("###")
                    .addCriterion("has_item", hasIngot)
                    .build(consumer, new ResourceLocation(metal.ingot.asItem().getRegistryName() + "_from_nuggets"));
        }
        if (metal.dustTag != null) {
            CookingRecipeBuilder.blastingRecipe(Ingredient.fromTag(metal.dustTag), metal.ingot, smeltingXp, 100)
                    .addCriterion("has_item", hasIngot)
                    .build(consumer, SilentGear.getId(metal.name + "_dust_blasting"));
            CookingRecipeBuilder.smeltingRecipe(Ingredient.fromTag(metal.dustTag), metal.ingot, smeltingXp, 200)
                    .addCriterion("has_item", hasIngot)
                    .build(consumer, SilentGear.getId(metal.name + "_dust_smelting"));
        }
    }

    private static class Metals {
        private final String name;
        private IItemProvider ore;
        private Tag<Item> oreTag;
        private IItemProvider block;
        private Tag<Item> blockTag;
        private final IItemProvider ingot;
        private final Tag<Item> ingotTag;
        private IItemProvider nugget;
        private Tag<Item> nuggetTag;
        private IItemProvider dust;
        private Tag<Item> dustTag;

        public Metals(String name, IItemProvider ingot, Tag<Item> ingotTag) {
            this.name = name;
            this.ingot = ingot;
            this.ingotTag = ingotTag;
        }

        public Metals ore(IItemProvider item, Tag<Item> tag) {
            this.ore = item;
            this.oreTag = tag;
            return this;
        }

        public Metals block(IItemProvider item, Tag<Item> tag) {
            this.block = item;
            this.blockTag = tag;
            return this;
        }

        public Metals nugget(IItemProvider item, Tag<Item> tag) {
            this.nugget = item;
            this.nuggetTag = tag;
            return this;
        }

        public Metals dust(IItemProvider item, Tag<Item> tag) {
            this.dust = item;
            this.dustTag = tag;
            return this;
        }
    }
}
