package net.silentchaos512.gear.init;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.item.CraftingItems;
import net.silentchaos512.lib.util.generator.TagGenerator;

public final class ModTags {
    private ModTags() {}

    public static final class Blocks {
        public static final Tag<Block> ORES_CRIMSON_IRON = TagGenerator.block(
                nameForge("ores/crimson_iron"), ModBlocks.CRIMSON_IRON_ORE);

        private Blocks() {}
    }

    public static final class Items {
        public static final Tag<Item> INGOTS_CRIMSON_IRON = TagGenerator.item(
                nameForge("ingots/crimson_iron"), CraftingItems.CRIMSON_IRON_INGOT);
        public static final Tag<Item> INGOTS_CRIMSON_STEEL = TagGenerator.item(
                nameForge("ingots/crimson_steel"), CraftingItems.CRIMSON_STEEL_INGOT);
        public static final Tag<Item> NUGGETS_DIAMOND = TagGenerator.item(
                nameForge("nuggets/diamond"), CraftingItems.DIAMOND_SHARD);
        public static final Tag<Item> NUGGETS_EMERALD = TagGenerator.item(
                nameForge("nuggets/emerald"), CraftingItems.EMERALD_SHARD);
        public static final Tag<Item> ORES_CRIMSON_IRON = TagGenerator.item(
                nameForge("ores/crimson_iron"), ModBlocks.CRIMSON_IRON_ORE);
        public static final Tag<Item> PAPER_BLUEPRINT = TagGenerator.item(
                nameForge("paper/blueprint"), CraftingItems.BLUEPRINT_PAPER);
        public static final Tag<Item> RODS_IRON = TagGenerator.item(
                nameForge("rods/iron"), CraftingItems.IRON_ROD);
        public static final Tag<Item> RODS_NETHERWOOD = TagGenerator.item(
                nameMod("rods/netherwood"), CraftingItems.NETHERWOOD_STICK);
        public static final Tag<Item> RODS_ROUGH = TagGenerator.item(
                nameMod("rods/rough"), CraftingItems.ROUGH_ROD);
        public static final Tag<Item> RODS_STONE = TagGenerator.item(
                nameForge("rods/stone"), CraftingItems.STONE_ROD);
        public static final Tag<Item> UPGRADE_BASES_BASIC = TagGenerator.item(
                nameMod("upgrade_bases/basic"), CraftingItems.UPGRADE_BASE);
        public static final Tag<Item> UPGRADE_BASES_ADVANCED = TagGenerator.item(
                nameMod("upgrade_bases/advanced"), CraftingItems.ADVANCED_UPGRADE_BASE);

        private Items() {}
    }

    private static ResourceLocation nameForge(String path) {
        return name("forge", path);
    }

    private static ResourceLocation nameMod(String path) {
        return name(SilentGear.MOD_ID, path);
    }

    private static ResourceLocation name(String namespace, String path) {
        return new ResourceLocation(namespace, path);
    }

    public static void init() {
        // Mostly here so TagGenerator calls are done at the right time.
        // TagGenerator should generate JSON files in dev only.

        TagGenerator.block(nameForge("ores"), Blocks.ORES_CRIMSON_IRON);
        TagGenerator.item(nameForge("ores"), Items.ORES_CRIMSON_IRON);

        TagGenerator.item(nameForge("ingots"), Items.INGOTS_CRIMSON_IRON);
        TagGenerator.item(nameForge("ingots"), Items.INGOTS_CRIMSON_STEEL);
        TagGenerator.item(nameForge("nuggets"), Items.NUGGETS_DIAMOND);
        TagGenerator.item(nameForge("nuggets"), Items.NUGGETS_EMERALD);
        // TODO: Will there be a standard paper tag?
        TagGenerator.item(nameForge("paper"), net.minecraft.init.Items.PAPER);
        TagGenerator.item(nameForge("paper"), Items.PAPER_BLUEPRINT);
        TagGenerator.item(nameForge("rods"), Items.RODS_IRON);
        TagGenerator.item(nameForge("rods"), Items.RODS_NETHERWOOD);
        TagGenerator.item(nameForge("rods"), Items.RODS_ROUGH);
        TagGenerator.item(nameForge("rods"), Items.RODS_STONE);

        for (ICoreItem item : ModItems.gearClasses.values()) {
            String type = item.getGearClass();
            Tag<Item> tag = TagGenerator.item(
                    nameMod("blueprints/" + type),
                    nameMod("blueprint_" + type).toString(),
                    nameMod("template_" + type).toString()
            );
            TagGenerator.item(nameMod("blueprints"), tag);
        }
        Tag<Item> tagBlueprintRod = TagGenerator.item(nameMod("blueprints/rod"), nameMod("blueprint_rod").toString());
        TagGenerator.item(nameMod("blueprints"), tagBlueprintRod);

        TagGenerator.item(nameForge("dyes/blue"), CraftingItems.BLUE_DYE);
        TagGenerator.item(nameForge("dyes/black"), CraftingItems.BLACK_DYE);
        TagGenerator.item(nameForge("rods/wooden"), CraftingItems.NETHERWOOD_STICK);

        // Calling because Silent Lib is not receiving events?
        TagGenerator.generateFiles();
    }
}
