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
        public static final Tag<Item> ORES_CRIMSON_IRON = TagGenerator.item(
                nameForge("ores/crimson_iron"), ModBlocks.CRIMSON_IRON_ORE);

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

        assert CraftingItems.CRIMSON_IRON_INGOT.getTag() != null;
        TagGenerator.item(nameForge("ingots"), CraftingItems.CRIMSON_IRON_INGOT.getTag());
        assert CraftingItems.CRIMSON_STEEL_INGOT.getTag() != null;
        TagGenerator.item(nameForge("ingots"), CraftingItems.CRIMSON_STEEL_INGOT.getTag());
        assert CraftingItems.DIAMOND_SHARD.getTag() != null;
        TagGenerator.item(nameForge("nuggets"), CraftingItems.DIAMOND_SHARD.getTag());
        assert CraftingItems.EMERALD_SHARD.getTag() != null;
        TagGenerator.item(nameForge("nuggets"), CraftingItems.EMERALD_SHARD.getTag());
        // TODO: Will there be a standard paper tag?
        TagGenerator.item(nameForge("paper"), net.minecraft.init.Items.PAPER);
        assert CraftingItems.BLUEPRINT_PAPER.getTag() != null;
        TagGenerator.item(nameForge("paper"), CraftingItems.BLUEPRINT_PAPER.getTag());
        assert CraftingItems.IRON_ROD.getTag() != null;
        TagGenerator.item(nameForge("rods"), CraftingItems.IRON_ROD.getTag());
        assert CraftingItems.NETHERWOOD_STICK.getTag() != null;
        TagGenerator.item(nameForge("rods"), CraftingItems.NETHERWOOD_STICK.getTag());
        assert CraftingItems.ROUGH_ROD.getTag() != null;
        TagGenerator.item(nameForge("rods"), CraftingItems.ROUGH_ROD.getTag());
        assert CraftingItems.STONE_ROD.getTag() != null;
        TagGenerator.item(nameForge("rods"), CraftingItems.STONE_ROD.getTag());
        // TODO: String tag?
        TagGenerator.item(nameForge("string"), "forge:string/string", "forge:string/sinew", "forge:string/flax");

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
    }
}
