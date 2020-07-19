package net.silentchaos512.gear.init;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.SilentGear;

import java.util.List;

public final class ModTags {
    private ModTags() {}

    public static final class Blocks {
        public static final ITag.INamedTag<Block> NETHERWOOD_SOIL = mod("netherwood_soil");

        public static final ITag.INamedTag<Block> ORES_CRIMSON_IRON = forge("ores/crimson_iron");

        public static final ITag.INamedTag<Block> STORAGE_BLOCKS_BLAZE_GOLD = forge("storage_blocks/blaze_gold");
        public static final ITag.INamedTag<Block> STORAGE_BLOCKS_CRIMSON_IRON = forge("storage_blocks/crimson_iron");
        public static final ITag.INamedTag<Block> STORAGE_BLOCKS_CRIMSON_STEEL = forge("storage_blocks/crimson_steel");

        private Blocks() {}

        private static ITag.INamedTag<Block> forge(String path) {
            return BlockTags.makeWrapperTag(new ResourceLocation("forge", path).toString());
        }

        private static ITag.INamedTag<Block> mod(String path) {
            return BlockTags.makeWrapperTag(SilentGear.getId(path).toString());
        }
    }

    public static final class Items {
        public static final ITag.INamedTag<Item> ORES_CRIMSON_IRON = forge("ores/crimson_iron");

        public static final ITag.INamedTag<Item> STORAGE_BLOCKS_BLAZE_GOLD = forge("storage_blocks/blaze_gold");
        public static final ITag.INamedTag<Item> STORAGE_BLOCKS_CRIMSON_IRON = forge("storage_blocks/crimson_iron");
        public static final ITag.INamedTag<Item> STORAGE_BLOCKS_CRIMSON_STEEL = forge("storage_blocks/crimson_steel");

        public static final ITag.INamedTag<Item> DUSTS_BLAZE_GOLD = forge("dusts/blaze_gold");
        public static final ITag.INamedTag<Item> DUSTS_CRIMSON_IRON = forge("dusts/crimson_iron");

        public static final ITag.INamedTag<Item> INGOTS_BLAZE_GOLD = forge("ingots/blaze_gold");
        public static final ITag.INamedTag<Item> INGOTS_CRIMSON_IRON = forge("ingots/crimson_iron");
        public static final ITag.INamedTag<Item> INGOTS_CRIMSON_STEEL = forge("ingots/crimson_steel");

        public static final ITag.INamedTag<Item> NUGGETS_BLAZE_GOLD = forge("nuggets/blaze_gold");
        public static final ITag.INamedTag<Item> NUGGETS_CRIMSON_IRON = forge("nuggets/crimson_iron");
        public static final ITag.INamedTag<Item> NUGGETS_CRIMSON_STEEL = forge("nuggets/crimson_steel");
        public static final ITag.INamedTag<Item> NUGGETS_DIAMOND = forge("nuggets/diamond");
        public static final ITag.INamedTag<Item> NUGGETS_EMERALD = forge("nuggets/emerald");

        public static final ITag.INamedTag<Item> PAPER = forge("paper");
        // TODO: Change to silentgear:blueprint_paper?
        public static final ITag.INamedTag<Item> PAPER_BLUEPRINT = forge("paper/blueprint");
        public static final ITag.INamedTag<Item> REPAIR_KITS = mod("repair_kits");
        public static final ITag.INamedTag<Item> TEMPLATE_BOARDS = mod("template_boards");

        // TODO: Remove iron rods, maybe netherwood?
        public static final ITag.INamedTag<Item> RODS_IRON = forge("rods/iron");
        public static final ITag.INamedTag<Item> RODS_NETHERWOOD = mod("rods/netherwood");
        public static final ITag.INamedTag<Item> RODS_STONE = forge("rods/stone");
        public static final ITag.INamedTag<Item> RODS_ROUGH = mod("rods/rough");

        // TODO: Remove the string sub-tags
        public static final ITag.INamedTag<Item> STRING_FLAX = forge("string/flax");
        public static final ITag.INamedTag<Item> STRING_SINEW = forge("string/sinew");

        public static final ITag.INamedTag<Item> FRUITS = forge("fruits");

        public static final ITag.INamedTag<Item> AXES = forge("axes");
        public static final ITag.INamedTag<Item> BOOTS = forge("boots");
        public static final ITag.INamedTag<Item> BOWS = forge("bows");
        public static final ITag.INamedTag<Item> CHESTPLATES = forge("chestplates");
        public static final ITag.INamedTag<Item> CROSSBOWS = forge("crossbows");
        public static final ITag.INamedTag<Item> HAMMERS = forge("hammers");
        public static final ITag.INamedTag<Item> HELMETS = forge("helmets");
        public static final ITag.INamedTag<Item> HOES = forge("hoes");
        public static final ITag.INamedTag<Item> KNIVES = forge("knives");
        public static final ITag.INamedTag<Item> LEGGINGS = forge("leggings");
        public static final ITag.INamedTag<Item> PICKAXES = forge("pickaxes");
        public static final ITag.INamedTag<Item> SHEARS = forge("shears");
        public static final ITag.INamedTag<Item> SHIELDS = forge("shields");
        public static final ITag.INamedTag<Item> SHOVELS = forge("shovels");
        public static final ITag.INamedTag<Item> SICKLES = forge("sickles");
        public static final ITag.INamedTag<Item> SWORDS = forge("swords");

        public static final ITag.INamedTag<Item> BLUEPRINTS = mod("blueprints");

        public static final ITag.INamedTag<Item> GRADER_CATALYSTS = mod("grader_catalysts");
        public static final ITag.INamedTag<Item> GRADER_CATALYSTS_TIER_1 = mod("grader_catalysts/tier1");
        public static final ITag.INamedTag<Item> GRADER_CATALYSTS_TIER_2 = mod("grader_catalysts/tier2");
        public static final ITag.INamedTag<Item> GRADER_CATALYSTS_TIER_3 = mod("grader_catalysts/tier3");
        public static final ITag.INamedTag<Item> GRADER_CATALYSTS_TIER_4 = mod("grader_catalysts/tier4");
        public static final ITag.INamedTag<Item> GRADER_CATALYSTS_TIER_5 = mod("grader_catalysts/tier5");
        public static final List<ITag.INamedTag<Item>> GRADER_CATALYSTS_TIERS = ImmutableList.of(GRADER_CATALYSTS_TIER_1, GRADER_CATALYSTS_TIER_2, GRADER_CATALYSTS_TIER_3, GRADER_CATALYSTS_TIER_4, GRADER_CATALYSTS_TIER_5);

        public static final ITag.INamedTag<Item> BLUEPRINTS_BOWSTRING = mod("blueprints/bowstring");
        public static final ITag.INamedTag<Item> BLUEPRINTS_ROD = mod("blueprints/rod");

        private Items() {}

        private static ITag.INamedTag<Item> forge(String path) {
            return ItemTags.makeWrapperTag(new ResourceLocation("forge", path).toString());
        }

        private static ITag.INamedTag<Item> mod(String path) {
            return ItemTags.makeWrapperTag(SilentGear.getId(path).toString());
        }
    }
}
