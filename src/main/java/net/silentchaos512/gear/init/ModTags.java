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
    public static final class Blocks {
        public static final ITag.INamedTag<Block> FLUFFY_BLOCKS = mod("fluffy_blocks");
        public static final ITag.INamedTag<Block> NETHERWOOD_LOGS = mod("netherwood_logs");
        public static final ITag.INamedTag<Block> NETHERWOOD_SOIL = mod("netherwood_soil");
        public static final ITag.INamedTag<Block> PROSPECTOR_HAMMER_TARGETS = mod("prospector_hammer_targets");

        public static final ITag.INamedTag<Block> ORES_BORT = forge("ores/bort");
        public static final ITag.INamedTag<Block> ORES_CRIMSON_IRON = forge("ores/crimson_iron");
        public static final ITag.INamedTag<Block> ORES_AZURE_SILVER = forge("ores/azure_silver");

        public static final ITag.INamedTag<Block> STORAGE_BLOCKS_BORT = forge("storage_blocks/bort");
        public static final ITag.INamedTag<Block> STORAGE_BLOCKS_BLAZE_GOLD = forge("storage_blocks/blaze_gold");
        public static final ITag.INamedTag<Block> STORAGE_BLOCKS_CRIMSON_IRON = forge("storage_blocks/crimson_iron");
        public static final ITag.INamedTag<Block> STORAGE_BLOCKS_CRIMSON_STEEL = forge("storage_blocks/crimson_steel");
        public static final ITag.INamedTag<Block> STORAGE_BLOCKS_AZURE_SILVER = forge("storage_blocks/azure_silver");
        public static final ITag.INamedTag<Block> STORAGE_BLOCKS_AZURE_ELECTRUM = forge("storage_blocks/azure_electrum");
        public static final ITag.INamedTag<Block> STORAGE_BLOCKS_TYRIAN_STEEL = forge("storage_blocks/tyrian_steel");

        private Blocks() {}

        private static ITag.INamedTag<Block> forge(String path) {
            return BlockTags.makeWrapperTag(new ResourceLocation("forge", path).toString());
        }

        private static ITag.INamedTag<Block> mod(String path) {
            return BlockTags.makeWrapperTag(SilentGear.getId(path).toString());
        }
    }

    public static final class Items {
        public static final ITag.INamedTag<Item> FLUFFY_BLOCKS = mod("fluffy_blocks");
        public static final ITag.INamedTag<Item> NETHERWOOD_LOGS = mod("netherwood_logs");
        public static final ITag.INamedTag<Item> ORES_BORT = forge("ores/bort");
        public static final ITag.INamedTag<Item> ORES_CRIMSON_IRON = forge("ores/crimson_iron");
        public static final ITag.INamedTag<Item> ORES_AZURE_SILVER = forge("ores/azure_silver");

        public static final ITag.INamedTag<Item> CHUNKS_CRIMSON_IRON = silentsMechanisms("chunks/crimson_iron");
        public static final ITag.INamedTag<Item> CHUNKS_AZURE_SILVER = silentsMechanisms("chunks/azure_silver");
        public static final ITag.INamedTag<Item> COAL_GENERATOR_FUELS = silentsMechanisms("coal_generator_fuels");

        public static final ITag.INamedTag<Item> STORAGE_BLOCKS_BORT = forge("storage_blocks/bort");
        public static final ITag.INamedTag<Item> STORAGE_BLOCKS_BLAZE_GOLD = forge("storage_blocks/blaze_gold");
        public static final ITag.INamedTag<Item> STORAGE_BLOCKS_CRIMSON_IRON = forge("storage_blocks/crimson_iron");
        public static final ITag.INamedTag<Item> STORAGE_BLOCKS_CRIMSON_STEEL = forge("storage_blocks/crimson_steel");
        public static final ITag.INamedTag<Item> STORAGE_BLOCKS_AZURE_SILVER = forge("storage_blocks/azure_silver");
        public static final ITag.INamedTag<Item> STORAGE_BLOCKS_AZURE_ELECTRUM = forge("storage_blocks/azure_electrum");
        public static final ITag.INamedTag<Item> STORAGE_BLOCKS_TYRIAN_STEEL = forge("storage_blocks/tyrian_steel");

        public static final ITag.INamedTag<Item> DUSTS_BLAZE_GOLD = forge("dusts/blaze_gold");
        public static final ITag.INamedTag<Item> DUSTS_CRIMSON_IRON = forge("dusts/crimson_iron");
        public static final ITag.INamedTag<Item> DUSTS_CRIMSON_STEEL = forge("dusts/crimson_steel");
        public static final ITag.INamedTag<Item> DUSTS_AZURE_SILVER = forge("dusts/azure_silver");
        public static final ITag.INamedTag<Item> DUSTS_AZURE_ELECTRUM = forge("dusts/azure_electrum");
        public static final ITag.INamedTag<Item> DUSTS_TYRIAN_STEEL = forge("dusts/tyrian_steel");
        public static final ITag.INamedTag<Item> DUSTS_STARMETAL = forge("dusts/starmetal");

        public static final ITag.INamedTag<Item> GEMS_BORT = forge("gems/bort");

        public static final ITag.INamedTag<Item> INGOTS_BLAZE_GOLD = forge("ingots/blaze_gold");
        public static final ITag.INamedTag<Item> INGOTS_CRIMSON_IRON = forge("ingots/crimson_iron");
        public static final ITag.INamedTag<Item> INGOTS_CRIMSON_STEEL = forge("ingots/crimson_steel");
        public static final ITag.INamedTag<Item> INGOTS_AZURE_SILVER = forge("ingots/azure_silver");
        public static final ITag.INamedTag<Item> INGOTS_AZURE_ELECTRUM = forge("ingots/azure_electrum");
        public static final ITag.INamedTag<Item> INGOTS_TYRIAN_STEEL = forge("ingots/tyrian_steel");

        public static final ITag.INamedTag<Item> NUGGETS_BLAZE_GOLD = forge("nuggets/blaze_gold");
        public static final ITag.INamedTag<Item> NUGGETS_CRIMSON_IRON = forge("nuggets/crimson_iron");
        public static final ITag.INamedTag<Item> NUGGETS_CRIMSON_STEEL = forge("nuggets/crimson_steel");
        public static final ITag.INamedTag<Item> NUGGETS_AZURE_SILVER = forge("nuggets/azure_silver");
        public static final ITag.INamedTag<Item> NUGGETS_AZURE_ELECTRUM = forge("nuggets/azure_electrum");
        public static final ITag.INamedTag<Item> NUGGETS_TYRIAN_STEEL = forge("nuggets/tyrian_steel");
        public static final ITag.INamedTag<Item> NUGGETS_DIAMOND = forge("nuggets/diamond");
        public static final ITag.INamedTag<Item> NUGGETS_EMERALD = forge("nuggets/emerald");

        public static final ITag.INamedTag<Item> PAPER = forge("paper");
        public static final ITag.INamedTag<Item> BLUEPRINT_PAPER = mod("blueprint_paper");
        public static final ITag.INamedTag<Item> REPAIR_KITS = mod("repair_kits");
        public static final ITag.INamedTag<Item> TEMPLATE_BOARDS = mod("template_boards");

        public static final ITag.INamedTag<Item> RODS_IRON = forge("rods/iron");
        public static final ITag.INamedTag<Item> RODS_NETHERWOOD = mod("rods/netherwood");
        public static final ITag.INamedTag<Item> RODS_STONE = forge("rods/stone");
        public static final ITag.INamedTag<Item> RODS_ROUGH = mod("rods/rough");

        public static final ITag.INamedTag<Item> FRUITS = forge("fruits");

        public static final ITag.INamedTag<Item> AXES = forge("axes");
        public static final ITag.INamedTag<Item> BOOTS = forge("boots");
        public static final ITag.INamedTag<Item> BOWS = forge("bows");
        public static final ITag.INamedTag<Item> CHESTPLATES = forge("chestplates");
        public static final ITag.INamedTag<Item> CROSSBOWS = forge("crossbows");
        public static final ITag.INamedTag<Item> ELYTRA = forge("elytra");
        public static final ITag.INamedTag<Item> HAMMERS = forge("hammers");
        public static final ITag.INamedTag<Item> HELMETS = forge("helmets");
        public static final ITag.INamedTag<Item> HOES = forge("hoes");
        public static final ITag.INamedTag<Item> KNIVES = forge("knives");
        public static final ITag.INamedTag<Item> LEGGINGS = forge("leggings");
        public static final ITag.INamedTag<Item> PICKAXES = forge("pickaxes");
        public static final ITag.INamedTag<Item> SAWS = forge("saws");
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

        public static final ITag.INamedTag<Item> STARLIGHT_CHARGER_CATALYSTS = mod("starlight_charger_catalysts");
        public static final ITag.INamedTag<Item> STARLIGHT_CHARGER_CATALYSTS_TIER_1 = mod("starlight_charger_catalysts/tier1");
        public static final ITag.INamedTag<Item> STARLIGHT_CHARGER_CATALYSTS_TIER_2 = mod("starlight_charger_catalysts/tier2");
        public static final ITag.INamedTag<Item> STARLIGHT_CHARGER_CATALYSTS_TIER_3 = mod("starlight_charger_catalysts/tier3");
        public static final List<ITag.INamedTag<Item>> STARLIGHT_CHARGER_TIERS = ImmutableList.of(STARLIGHT_CHARGER_CATALYSTS_TIER_1, STARLIGHT_CHARGER_CATALYSTS_TIER_2, STARLIGHT_CHARGER_CATALYSTS_TIER_3);

        private Items() {}

        private static ITag.INamedTag<Item> forge(String path) {
            return ItemTags.makeWrapperTag(new ResourceLocation("forge", path).toString());
        }

        private static ITag.INamedTag<Item> mod(String path) {
            return ItemTags.makeWrapperTag(SilentGear.getId(path).toString());
        }

        private static ITag.INamedTag<Item> silentsMechanisms(String path) {
            return ItemTags.makeWrapperTag(new ResourceLocation("silents_mechanisms", path).toString());
        }
    }

    private ModTags() {}
}
