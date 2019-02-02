package net.silentchaos512.gear.util;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.init.ModBlocks;
import net.silentchaos512.gear.init.ModItems;
import net.silentchaos512.gear.init.ModTags;
import net.silentchaos512.gear.item.CraftingItems;
import net.silentchaos512.lib.util.generator.RecipeGenerator;

import java.util.Objects;
import java.util.function.Consumer;

public class GenRecipes {
    public static void generateAll() {
        RecipeGenerator.create(name("crimson_iron_smelting"), RecipeGenerator.SmeltingBuilder
                .create(CraftingItems.CRIMSON_IRON_INGOT)
                .ingredient(ModBlocks.CRIMSON_IRON_ORE)
                .experience(1.0f)
        );
        RecipeGenerator.create(name("crafting_station"), RecipeGenerator.ShapedBuilder
                .create(ModBlocks.CRAFTING_STATION)
                .layout("#T#", "#U#", "#C#")
                .key('#', ItemTags.PLANKS)
                .key('T', Blocks.CRAFTING_TABLE)
                .key('U', ModTags.Items.UPGRADE_BASES_BASIC)
                .key('C', Tags.Items.CHESTS_WOODEN)
        );
        RecipeGenerator.create(name("part_analyzer"), RecipeGenerator.ShapedBuilder
                .create(ModBlocks.PART_ANALYZER)
                .layout("QIQ", "I#I", "GGG")
                .key('Q', Tags.Items.GEMS_QUARRTZ)
                .key('I', Tags.Items.INGOTS_IRON)
                .key('#', ModTags.Items.UPGRADE_BASES_ADVANCED)
                .key('G', Tags.Items.INGOTS_GOLD)
        );
        RecipeGenerator.create(name("salvager"), RecipeGenerator.ShapedBuilder
                .create(ModBlocks.SALVAGER)
                .layout(" P ", "/I/", "/#/")
                .key('P', Blocks.PISTON)
                .key('/', ModTags.Items.INGOTS_CRIMSON_IRON)
                .key('I', Tags.Blocks.STORAGE_BLOCKS_IRON)
                .key('#', Blocks.OBSIDIAN)
        );
        RecipeGenerator.create(name("netherwood_planks"), RecipeGenerator.ShapelessBuilder
                .create(ModBlocks.NETHERWOOD_PLANKS, 4)
                .ingredient(ModBlocks.NETHERWOOD_LOG)
        );
        RecipeGenerator.create(name("blue_dye_from_flower"), RecipeGenerator.ShapelessBuilder
                .create(CraftingItems.BLUE_DYE, 2)
                .ingredient(ModBlocks.FLOWER)
        );

        // Blueprints and templates
        blueprints(ModItems.sword, b -> b.layout("#", "#", "/"));
        blueprints(ModItems.dagger, b -> b.layout("#", "/"));
        blueprints(ModItems.katana, b -> b.layout("##", "# ", "/ "));
        blueprints(ModItems.machete, b -> b.layout("  #", " ##", "/  "));
        blueprints(ModItems.pickaxe, b -> b.layout("###", " / ", " / "));
        blueprints(ModItems.shovel, b -> b.layout("#", "/", "/"));
        blueprints(ModItems.axe, b -> b.layout("##", "#/", " /"));
        blueprints(ModItems.hammer, b -> b.layout("###", "###", "#/#"));
        blueprints(ModItems.excavator, b -> b.layout("# #", "###", " / "));
        blueprints(ModItems.mattock, b -> b.layout("## ", "#/#", " / "));
        blueprints(ModItems.sickle, b -> b.layout(" #", "##", "/ "));
        blueprints(ModItems.bow, b -> b.layout(" #/", "# /", " #/"));
        blueprints(ModItems.helmet, b -> b.layout("###", "#/#"));
        blueprints(ModItems.chestplate, b -> b.layout("#/#", "###", "###"));
        blueprints(ModItems.leggings, b -> b.layout("###", "#/#", "# #"));
        blueprints(ModItems.boots, b -> b.layout("#/#", "# #"));
        blueprints("rod", b -> b.layout("#/", "#/"));

        RecipeGenerator.create(name("blueprint_book"), RecipeGenerator.ShapelessBuilder
                .create(ModItems.blueprintBook)
                .ingredient(Items.BOOK)
                .ingredient(ModTags.Items.PAPER_BLUEPRINT, 3)
        );

        RecipeGenerator.create(name("blueprint_paper"), RecipeGenerator.ShapelessBuilder
                .create(CraftingItems.BLUEPRINT_PAPER, 4)
                .ingredient("forge:paper", 4)
                .ingredient(Tags.Items.DYES_BLUE)
        );
        RecipeGenerator.create(name("upgrade_base_basic"), RecipeGenerator.ShapelessBuilder
                .create(CraftingItems.UPGRADE_BASE, 4)
                .ingredient("forge:paper", 2)
                .ingredient(ItemTags.PLANKS)
                .ingredient(Tags.Items.STONE)
        );
        RecipeGenerator.create(name("upgrade_base_advanced"), RecipeGenerator.ShapedBuilder
                .create(CraftingItems.ADVANCED_UPGRADE_BASE)
                .layout("///", "DUD", "GGG")
                .key('/', ModTags.Items.NUGGETS_DIAMOND)
                .key('D', Tags.Items.DYES_BLUE)
                .key('U', ModTags.Items.UPGRADE_BASES_BASIC)
                .key('G', Tags.Items.NUGGETS_GOLD)
        );
        RecipeGenerator.create(name("rough_rods"), RecipeGenerator.ShapedBuilder
                .create(CraftingItems.ROUGH_ROD, 2)
                .layout(" /", "/ ")
                .key('/', Tags.Items.RODS_WOODEN)
        );
        RecipeGenerator.create(name("stone_rods"), RecipeGenerator.ShapedBuilder
                .create(CraftingItems.STONE_ROD, 4)
                .layout("#", "#")
                .key('#', Blocks.COBBLESTONE)
        );
//        RecipeGenerator.create(name("stone_rods_from_blueprint"), RecipeGenerator.ShapelessBuilder
//                .create(CraftingItems.STONE_ROD, 4)
//                .ingredient("silentgear:blueprint_rod")
//                .ingredient(Blocks.COBBLESTONE, 2)
//        );
        RecipeGenerator.create(name("iron_rods"), RecipeGenerator.ShapedBuilder
                .create(CraftingItems.IRON_ROD, 4)
                .layout("#", "#")
                .key('#', Tags.Items.INGOTS_IRON)
        );
//        RecipeGenerator.create(name("iron_rods_from_blueprint"), RecipeGenerator.ShapelessBuilder
//                .create(CraftingItems.IRON_ROD, 4)
//                .ingredient("silentgear:blueprint_rod")
//                .ingredient(Tags.Items.INGOTS_IRON, 2)
//        );
        RecipeGenerator.create(name("netherwood_sticks"), RecipeGenerator.ShapedBuilder
                .create(CraftingItems.NETHERWOOD_STICK, 4)
                .layout(" #", "# ")
                .key('#', ModBlocks.NETHERWOOD_PLANKS)
        );
//        RecipeGenerator.create(name("netherwood_sticks_from_blueprint"), RecipeGenerator.ShapelessBuilder
//                .create(CraftingItems.NETHERWOOD_STICK, 4)
//                .ingredient("silentgear:blueprint_rod")
//                .ingredient(ModBlocks.NETHERWOOD_PLANKS, 2)
//        );
        RecipeGenerator.create(name("crimson_steel_ingot"), RecipeGenerator.ShapedBuilder
                .create(CraftingItems.CRIMSON_STEEL_INGOT)
                .layout("/ /", "#C#", "# #")
                .key('/', Tags.Items.RODS_BLAZE)
                .key('#', ModTags.Items.INGOTS_CRIMSON_IRON)
                .key('C', Items.MAGMA_CREAM)
        );
        RecipeGenerator.compress9(name("diamond_shards"), Items.DIAMOND, CraftingItems.DIAMOND_SHARD);
        RecipeGenerator.compress9(name("emerald_shards"), Items.EMERALD, CraftingItems.EMERALD_SHARD);
        RecipeGenerator.compress9(name("leather_scraps"), Items.LEATHER, CraftingItems.LEATHER_SCRAP);
        RecipeGenerator.create(name("sinew_smelting"), RecipeGenerator.SmeltingBuilder
                .create(CraftingItems.DRIED_SINEW)
                .ingredient(CraftingItems.SINEW)
                .experience(0.35f)
        );
        RecipeGenerator.create(name("sinew_fibers"), RecipeGenerator.ShapelessBuilder
                .create(CraftingItems.SINEW_FIBER, 3)
                .ingredient(CraftingItems.DRIED_SINEW)
        );
        RecipeGenerator.create(name("flax_string"), RecipeGenerator.ShapelessBuilder
                .create(CraftingItems.FLAX_STRING)
                .ingredient(CraftingItems.FLAX_FIBER, 2)
        );

        tipUpgrade(CraftingItems.IRON_TIPPED_UPGRADE, Tags.Items.INGOTS_IRON);
        tipUpgrade(CraftingItems.GOLD_TIPPED_UPGRADE, Tags.Items.INGOTS_GOLD);
        tipUpgrade(CraftingItems.DIAMOND_TIPPED_UPGRADE, Tags.Items.GEMS_DIAMOND);
        tipUpgrade(CraftingItems.EMERALD_TIPPED_UPGRADE, Tags.Items.GEMS_EMERALD);
        tipUpgrade(CraftingItems.REDSTONE_COATED_UPGRADE, Tags.Items.DUSTS_REDSTONE);
        tipUpgrade(CraftingItems.GLOWSTONE_COATED_UPGRADE, Tags.Items.DUSTS_GLOWSTONE);
        tipUpgrade(CraftingItems.LAPIS_COATED_UPGRADE, Tags.Items.GEMS_LAPIS);
        tipUpgrade(CraftingItems.QUARTZ_TIPPED_UPGRADE, Tags.Items.GEMS_QUARRTZ);

        RecipeGenerator.create(name("spoon_upgrade"), RecipeGenerator.ShapelessBuilder
                .create(CraftingItems.SPOON_UPGRADE)
                .ingredient(ModTags.Items.UPGRADE_BASES_ADVANCED)
                .ingredient(Items.DIAMOND_SHOVEL)
        );
        RecipeGenerator.create(name("red_card_upgrade"), RecipeGenerator.ShapelessBuilder
                .create(CraftingItems.RED_CARD_UPGRADE, 4)
                .ingredient(ModTags.Items.UPGRADE_BASES_BASIC)
                .ingredient(Tags.Items.DYES_RED)
        );
    }

    private static void blueprints(ICoreItem item, Consumer<RecipeGenerator.ShapedBuilder> layout) {
        blueprints(item.getGearClass(), layout);
    }

    private static void blueprints(String type, Consumer<RecipeGenerator.ShapedBuilder> layout) {
        ResourceLocation nameBlueprint = name("blueprint_" + type);
        ResourceLocation nameTemplate = name("template_" + type);
        Item blueprint = ForgeRegistries.ITEMS.getValue(nameBlueprint);
        Item template = ForgeRegistries.ITEMS.getValue(nameTemplate);

        if (blueprint != null) {
            RecipeGenerator.ShapedBuilder builder = RecipeGenerator.ShapedBuilder.create(blueprint);
            layout.accept(builder);
            RecipeGenerator.create(nameBlueprint, builder
                    .group("silentgear:blueprints_" + type)
                    .key('#', ModTags.Items.PAPER_BLUEPRINT)
                    .key('/', Tags.Items.RODS_WOODEN)
            );
        }
        if (template != null) {
            RecipeGenerator.ShapedBuilder builder = RecipeGenerator.ShapedBuilder.create(template);
            layout.accept(builder);
            RecipeGenerator.create(nameTemplate, builder
                    .group("silentgear:blueprints_" + type)
                    .key('#', ItemTags.WOODEN_SLABS)
                    .key('/', Tags.Items.RODS_WOODEN)
            );
        }
    }

    private static void tipUpgrade(IItemProvider output, Tag<Item> material) {
        ResourceLocation name = Objects.requireNonNull(output.asItem().getRegistryName());
        RecipeGenerator.create(name, RecipeGenerator.ShapelessBuilder
                .create(output)
                .group("silentgear:tip_upgrades")
                .ingredient(ModTags.Items.UPGRADE_BASES_BASIC)
                .ingredient(material)
        );
    }

    private static ResourceLocation name(String name) {
        return new ResourceLocation(SilentGear.MOD_ID, name);
    }
}
