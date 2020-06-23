package net.silentchaos512.gear.init;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.SilentGear;

import java.util.List;

public final class ModTags {
    private ModTags() {}

    public static final class Blocks {
        public static final Tag<Block> NETHERWOOD_SOIL = new BlockTags.Wrapper(modId("netherwood_soil"));

        public static final Tag<Block> ORES_CRIMSON_IRON = new BlockTags.Wrapper(forgeId("ores/crimson_iron"));

        public static final Tag<Block> STORAGE_BLOCKS_BLAZE_GOLD = new BlockTags.Wrapper(forgeId("storage_blocks/blaze_gold"));
        public static final Tag<Block> STORAGE_BLOCKS_CRIMSON_IRON = new BlockTags.Wrapper(forgeId("storage_blocks/crimson_iron"));
        public static final Tag<Block> STORAGE_BLOCKS_CRIMSON_STEEL = new BlockTags.Wrapper(forgeId("storage_blocks/crimson_steel"));

        private Blocks() {}
    }

    public static final class Items {
        public static final Tag<Item> ORES_CRIMSON_IRON = new ItemTags.Wrapper(forgeId("ores/crimson_iron"));

        public static final Tag<Item> STORAGE_BLOCKS_BLAZE_GOLD = new ItemTags.Wrapper(forgeId("storage_blocks/blaze_gold"));
        public static final Tag<Item> STORAGE_BLOCKS_CRIMSON_IRON = new ItemTags.Wrapper(forgeId("storage_blocks/crimson_iron"));
        public static final Tag<Item> STORAGE_BLOCKS_CRIMSON_STEEL = new ItemTags.Wrapper(forgeId("storage_blocks/crimson_steel"));

        public static final Tag<Item> DUSTS_BLAZE_GOLD = new ItemTags.Wrapper(forgeId("dusts/blaze_gold"));
        public static final Tag<Item> DUSTS_CRIMSON_IRON = new ItemTags.Wrapper(forgeId("dusts/crimson_iron"));

        public static final Tag<Item> INGOTS_BLAZE_GOLD = new ItemTags.Wrapper(forgeId("ingots/blaze_gold"));
        public static final Tag<Item> INGOTS_CRIMSON_IRON = new ItemTags.Wrapper(forgeId("ingots/crimson_iron"));
        public static final Tag<Item> INGOTS_CRIMSON_STEEL = new ItemTags.Wrapper(forgeId("ingots/crimson_steel"));

        public static final Tag<Item> NUGGETS_BLAZE_GOLD = new ItemTags.Wrapper(forgeId("nuggets/blaze_gold"));
        public static final Tag<Item> NUGGETS_CRIMSON_IRON = new ItemTags.Wrapper(forgeId("nuggets/crimson_iron"));
        public static final Tag<Item> NUGGETS_CRIMSON_STEEL = new ItemTags.Wrapper(forgeId("nuggets/crimson_steel"));
        public static final Tag<Item> NUGGETS_DIAMOND = new ItemTags.Wrapper(forgeId("nuggets/diamond"));
        public static final Tag<Item> NUGGETS_EMERALD = new ItemTags.Wrapper(forgeId("nuggets/emerald"));

        public static final Tag<Item> PAPER = new ItemTags.Wrapper(forgeId("paper"));
        // TODO: Change to silentgear:blueprint_paper?
        public static final Tag<Item> PAPER_BLUEPRINT = new ItemTags.Wrapper(forgeId("paper/blueprint"));
        public static final Tag<Item> TEMPLATE_BOARDS = new ItemTags.Wrapper(modId("template_boards"));

        // TODO: Remove iron rods, maybe netherwood?
        public static final Tag<Item> RODS_IRON = new ItemTags.Wrapper(forgeId("rods/iron"));
        public static final Tag<Item> RODS_NETHERWOOD = new ItemTags.Wrapper(modId("rods/netherwood"));
        public static final Tag<Item> RODS_STONE = new ItemTags.Wrapper(forgeId("rods/stone"));
        public static final Tag<Item> RODS_ROUGH = new ItemTags.Wrapper(modId("rods/rough"));

        // TODO: Remove the string sub-tags
        public static final Tag<Item> STRING_FLAX = new ItemTags.Wrapper(forgeId("string/flax"));
        public static final Tag<Item> STRING_SINEW = new ItemTags.Wrapper(forgeId("string/sinew"));

        public static final Tag<Item> FRUITS = new ItemTags.Wrapper(forgeId("fruits"));

        public static final Tag<Item> AXES = new ItemTags.Wrapper(forgeId("axes"));
        public static final Tag<Item> BOOTS = new ItemTags.Wrapper(forgeId("boots"));
        public static final Tag<Item> BOWS = new ItemTags.Wrapper(forgeId("bows"));
        public static final Tag<Item> CHESTPLATES = new ItemTags.Wrapper(forgeId("chestplates"));
        public static final Tag<Item> CROSSBOWS = new ItemTags.Wrapper(forgeId("crossbows"));
        public static final Tag<Item> HAMMERS = new ItemTags.Wrapper(forgeId("hammers"));
        public static final Tag<Item> HELMETS = new ItemTags.Wrapper(forgeId("helmets"));
        public static final Tag<Item> HOES = new ItemTags.Wrapper(forgeId("hoes"));
        public static final Tag<Item> KNIVES = new ItemTags.Wrapper(forgeId("knives"));
        public static final Tag<Item> LEGGINGS = new ItemTags.Wrapper(forgeId("leggings"));
        public static final Tag<Item> PICKAXES = new ItemTags.Wrapper(forgeId("pickaxes"));
        public static final Tag<Item> SHEARS = new ItemTags.Wrapper(forgeId("shears"));
        public static final Tag<Item> SHIELDS = new ItemTags.Wrapper(forgeId("shields"));
        public static final Tag<Item> SHOVELS = new ItemTags.Wrapper(forgeId("shovels"));
        public static final Tag<Item> SICKLES = new ItemTags.Wrapper(forgeId("sickles"));
        public static final Tag<Item> SWORDS = new ItemTags.Wrapper(forgeId("swords"));

        public static final Tag<Item> BLUEPRINTS = new ItemTags.Wrapper(SilentGear.getId("blueprints"));

        public static final Tag<Item> GRADER_CATALYSTS = new ItemTags.Wrapper(modId("grader_catalysts"));
        public static final Tag<Item> GRADER_CATALYSTS_TIER_1 = new ItemTags.Wrapper(SilentGear.getId("grader_catalysts/tier1"));
        public static final Tag<Item> GRADER_CATALYSTS_TIER_2 = new ItemTags.Wrapper(SilentGear.getId("grader_catalysts/tier2"));
        public static final Tag<Item> GRADER_CATALYSTS_TIER_3 = new ItemTags.Wrapper(SilentGear.getId("grader_catalysts/tier3"));
        public static final Tag<Item> GRADER_CATALYSTS_TIER_4 = new ItemTags.Wrapper(SilentGear.getId("grader_catalysts/tier4"));
        public static final Tag<Item> GRADER_CATALYSTS_TIER_5 = new ItemTags.Wrapper(SilentGear.getId("grader_catalysts/tier5"));
        public static final List<Tag<Item>> GRADER_CATALYSTS_TIERS = ImmutableList.of(GRADER_CATALYSTS_TIER_1, GRADER_CATALYSTS_TIER_2, GRADER_CATALYSTS_TIER_3, GRADER_CATALYSTS_TIER_4, GRADER_CATALYSTS_TIER_5);

        public static final Tag<Item> BLUEPRINTS_BOWSTRING = new ItemTags.Wrapper(modId("blueprints/bowstring"));
        public static final Tag<Item> BLUEPRINTS_ROD = new ItemTags.Wrapper(modId("blueprints/rod"));

        private Items() {}
    }

    private static ResourceLocation forgeId(String path) {
        return new ResourceLocation("forge", path);
    }

    private static ResourceLocation modId(String path) {
        return new ResourceLocation(SilentGear.MOD_ID, path);
    }

    public static void init() {
        // Mostly here so TagGenerator calls are done at the right time.
        // TagGenerator should generate JSON files in dev only.

        // REMOVED: no longer needed
    }
}
