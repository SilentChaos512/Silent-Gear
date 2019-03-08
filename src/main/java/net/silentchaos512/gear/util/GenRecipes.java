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
import net.silentchaos512.gear.item.CraftingItems;
import net.silentchaos512.lib.util.generator.RecipeGenerator;
import net.silentchaos512.lib.util.generator.RecipeGenerator.ShapedBuilder;
import net.silentchaos512.lib.util.generator.RecipeGenerator.ShapelessBuilder;
import net.silentchaos512.lib.util.generator.RecipeGenerator.SmeltingBuilder;

import java.util.Objects;
import java.util.function.Consumer;

public class GenRecipes {
    public static void generateAll() {
        RecipeGenerator.create(name("crimson_iron_smelting"), SmeltingBuilder
                .create(CraftingItems.CRIMSON_IRON_INGOT)
                .ingredient(ModBlocks.CRIMSON_IRON_ORE)
                .experience(1.0f)
        );
        assert CraftingItems.UPGRADE_BASE.getTag() != null;
        RecipeGenerator.create(name("crafting_station"), ShapedBuilder
                .create(ModBlocks.CRAFTING_STATION)
                .layout("#T#", "#U#", "#C#")
                .key('#', ItemTags.PLANKS)
                .key('T', Blocks.CRAFTING_TABLE)
                .key('U', CraftingItems.UPGRADE_BASE.getTag())
                .key('C', Tags.Items.CHESTS_WOODEN)
        );
        assert CraftingItems.ADVANCED_UPGRADE_BASE.getTag() != null;
        RecipeGenerator.create(name("part_analyzer"), ShapedBuilder
                .create(ModBlocks.PART_ANALYZER)
                .layout("QIQ", "I#I", "GGG")
                .key('Q', Tags.Items.GEMS_QUARRTZ)
                .key('I', Tags.Items.INGOTS_IRON)
                .key('#', CraftingItems.ADVANCED_UPGRADE_BASE.getTag())
                .key('G', Tags.Items.INGOTS_GOLD)
        );
        assert CraftingItems.CRIMSON_IRON_INGOT.getTag() != null;
        RecipeGenerator.create(name("salvager"), ShapedBuilder
                .create(ModBlocks.SALVAGER)
                .layout(" P ", "/I/", "/#/")
                .key('P', Blocks.PISTON)
                .key('/', CraftingItems.CRIMSON_IRON_INGOT.getTag())
                .key('I', Tags.Blocks.STORAGE_BLOCKS_IRON)
                .key('#', Blocks.OBSIDIAN)
        );
        RecipeGenerator.create(name("netherwood_planks"), ShapelessBuilder
                .create(ModBlocks.NETHERWOOD_PLANKS, 4)
                .ingredient(ModBlocks.NETHERWOOD_LOG)
        );
        RecipeGenerator.create(name("blue_dye_from_flower"), ShapelessBuilder
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

        assert CraftingItems.BLUEPRINT_PAPER.getTag() != null;
        RecipeGenerator.create(name("blueprint_book"), ShapelessBuilder
                .create(ModItems.blueprintBook)
                .ingredient(Items.BOOK)
                .ingredient(CraftingItems.BLUEPRINT_PAPER.getTag(), 3)
        );

        RecipeGenerator.create(name("blueprint_paper"), ShapelessBuilder
                .create(CraftingItems.BLUEPRINT_PAPER, 4)
                .ingredient("forge:paper", 4)
                .ingredient(Tags.Items.DYES_BLUE)
        );
        RecipeGenerator.create(name("upgrade_base_basic"), ShapelessBuilder
                .create(CraftingItems.UPGRADE_BASE, 4)
                .ingredient("forge:paper", 2)
                .ingredient(ItemTags.PLANKS)
                .ingredient(Tags.Items.STONE)
        );
        assert CraftingItems.DIAMOND_SHARD.getTag() != null;
        RecipeGenerator.create(name("upgrade_base_advanced"), ShapedBuilder
                .create(CraftingItems.ADVANCED_UPGRADE_BASE)
                .layout("///", "DUD", "GGG")
                .key('/', CraftingItems.DIAMOND_SHARD.getTag())
                .key('D', Tags.Items.DYES_BLUE)
                .key('U', CraftingItems.UPGRADE_BASE.getTag())
                .key('G', Tags.Items.NUGGETS_GOLD)
        );
        RecipeGenerator.create(name("crimson_steel_ingot"), ShapedBuilder
                .create(CraftingItems.CRIMSON_STEEL_INGOT)
                .layout("/ /", "#C#", "# #")
                .key('/', Tags.Items.RODS_BLAZE)
                .key('#', CraftingItems.CRIMSON_IRON_INGOT.getTag())
                .key('C', Items.MAGMA_CREAM)
        );
        RecipeGenerator.compress9(name("diamond_shards"), Items.DIAMOND, CraftingItems.DIAMOND_SHARD);
        RecipeGenerator.compress9(name("emerald_shards"), Items.EMERALD, CraftingItems.EMERALD_SHARD);
//        RecipeGenerator.compress9(name("leather_scraps"), Items.LEATHER, CraftingItems.LEATHER_SCRAP);
        RecipeGenerator.compress9(name("crimson_iron"),
                CraftingItems.CRIMSON_IRON_INGOT,
                CraftingItems.CRIMSON_IRON_NUGGET);
        RecipeGenerator.compress9(name("crimson_steel"),
                CraftingItems.CRIMSON_STEEL_INGOT,
                CraftingItems.CRIMSON_STEEL_NUGGET);
        RecipeGenerator.create(name("sinew_smelting"), SmeltingBuilder
                .create(CraftingItems.DRIED_SINEW)
                .ingredient(CraftingItems.SINEW)
                .experience(0.35f)
        );
        RecipeGenerator.create(name("sinew_fibers"), ShapelessBuilder
                .create(CraftingItems.SINEW_FIBER, 3)
                .ingredient(CraftingItems.DRIED_SINEW)
        );
        RecipeGenerator.create(name("flax_string"), ShapelessBuilder
                .create(CraftingItems.FLAX_STRING)
                .ingredient(CraftingItems.FLAX_FIBER, 2)
        );

        tipUpgrade(CraftingItems.IRON_TIPPED_UPGRADE, Tags.Items.INGOTS_IRON, 1);
        tipUpgrade(CraftingItems.GOLD_TIPPED_UPGRADE, Tags.Items.INGOTS_GOLD, 1);
        tipUpgrade(CraftingItems.DIAMOND_TIPPED_UPGRADE, Tags.Items.GEMS_DIAMOND, 1);
        tipUpgrade(CraftingItems.EMERALD_TIPPED_UPGRADE, Tags.Items.GEMS_EMERALD, 1);
        tipUpgrade(CraftingItems.REDSTONE_COATED_UPGRADE, Tags.Items.DUSTS_REDSTONE, 4);
        tipUpgrade(CraftingItems.GLOWSTONE_COATED_UPGRADE, Tags.Items.DUSTS_GLOWSTONE, 4);
        tipUpgrade(CraftingItems.LAPIS_COATED_UPGRADE, Tags.Items.GEMS_LAPIS, 4);
        tipUpgrade(CraftingItems.QUARTZ_TIPPED_UPGRADE, Tags.Items.GEMS_QUARRTZ, 4);

        RecipeGenerator.create(name("spoon_upgrade"), ShapelessBuilder
                .create(CraftingItems.SPOON_UPGRADE)
                .ingredient(CraftingItems.ADVANCED_UPGRADE_BASE.getTag())
                .ingredient(Items.DIAMOND_SHOVEL)
        );
        RecipeGenerator.create(name("red_card_upgrade"), ShapelessBuilder
                .create(CraftingItems.RED_CARD_UPGRADE, 4)
                .ingredient(CraftingItems.UPGRADE_BASE.getTag())
                .ingredient(Tags.Items.DYES_RED)
        );

        // Bowstring (3 string)
        bowstrings(CraftingItems.FLAX_BOWSTRING, CraftingItems.FLAX_STRING.getTag());
        bowstrings(CraftingItems.PLAIN_BOWSTRING, "forge:string/string");
        bowstrings(CraftingItems.SINEW_BOWSTRING, CraftingItems.SINEW_FIBER.getTag());

        // Rods
        rods(CraftingItems.ROUGH_ROD, Tags.Items.RODS_WOODEN, 2, true);
        rods(CraftingItems.STONE_ROD, Tags.Blocks.COBBLESTONE, 4, false);
        rods(CraftingItems.IRON_ROD, Tags.Items.INGOTS_IRON, 4, false);
        rods(CraftingItems.NETHERWOOD_STICK, ModBlocks.NETHERWOOD_PLANKS, 4, true);
    }

    private static void blueprints(ICoreItem item, Consumer<ShapedBuilder> layout) {
        blueprints(item.getGearType().getName(), layout);
    }

    private static void blueprints(String type, Consumer<ShapedBuilder> layout) {
        ResourceLocation nameBlueprint = name("blueprints/blueprint_" + type);
        ResourceLocation nameTemplate = name("blueprints/template_" + type);
        Item blueprint = ForgeRegistries.ITEMS.getValue(nameBlueprint);
        Item template = ForgeRegistries.ITEMS.getValue(nameTemplate);

        if (blueprint != null) {
            RecipeGenerator.ShapedBuilder builder = ShapedBuilder.create(blueprint);
            layout.accept(builder);
            assert CraftingItems.BLUEPRINT_PAPER.getTag() != null;
            RecipeGenerator.create(nameBlueprint, builder
                    .group("silentgear:blueprints_" + type)
                    .key('#', CraftingItems.BLUEPRINT_PAPER.getTag())
                    .key('/', Tags.Items.RODS_WOODEN)
            );
        }
        if (template != null) {
            RecipeGenerator.ShapedBuilder builder = ShapedBuilder.create(template);
            layout.accept(builder);
            RecipeGenerator.create(nameTemplate, builder
                    .group("silentgear:blueprints_" + type)
                    .key('#', ItemTags.WOODEN_SLABS)
                    .key('/', Tags.Items.RODS_WOODEN)
            );
        }
    }

    private static void bowstrings(IItemProvider output, Object material) {
        ResourceLocation name = Objects.requireNonNull(output.asItem().getRegistryName());
        // Normal
        RecipeGenerator.create(name, ShapedBuilder
                .create(output)
                .group("silentgear:bowstrings")
                .layout("/", "/", "/")
                .key('/', material)
        );
        // Blueprint fallback
        ResourceLocation name2 = new ResourceLocation(SilentGear.MOD_ID, name.getPath() + "2");
        RecipeGenerator.create(name2, ShapelessBuilder
                .create(output)
                .group("silentgear:bowstrings")
                .ingredient("silentgear:blueprints/bowstring")
                .ingredient(material, 3)
        );
    }

    private static void rods(IItemProvider output, Object material, int count, boolean tilted) {
        ResourceLocation name = Objects.requireNonNull(output.asItem().getRegistryName());

        // Normal
        RecipeGenerator.create(name, ShapedBuilder
                .create(output, count)
                .layout(tilted ? " /" : "/",
                        tilted ? "/ " : "/")
                .key('/', material)
        );
        // Blueprint fallback
        ResourceLocation name2 = new ResourceLocation(SilentGear.MOD_ID, name.getPath() + "2");
        RecipeGenerator.create(name2, ShapelessBuilder
                .create(output, count)
                .ingredient("silentgear:blueprints/rod")
                .ingredient(material, 2)
        );
    }

    private static void tipUpgrade(IItemProvider output, Tag<Item> material, int materialCount) {
        ResourceLocation name = Objects.requireNonNull(output.asItem().getRegistryName());
        assert CraftingItems.UPGRADE_BASE.getTag() != null;
        RecipeGenerator.create(name, ShapelessBuilder
                .create(output)
                .group("silentgear:tip_upgrades")
                .ingredient(CraftingItems.UPGRADE_BASE.getTag())
                .ingredient(material, materialCount)
        );
    }

    private static ResourceLocation name(String name) {
        return new ResourceLocation(SilentGear.MOD_ID, name);
    }
}
