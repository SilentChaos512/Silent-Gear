package net.silentchaos512.gear.setup;

import com.google.common.collect.ImmutableList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.silentchaos512.gear.SilentGear;

import java.util.List;

public final class SgTags {
    public static final class Blocks {
        public static final TagKey<Block> MINEABLE_WITH_MACHETE = mod("mineable/machete");
        public static final TagKey<Block> MINEABLE_WITH_MATTOCK = mod("mineable/mattock");
        public static final TagKey<Block> MINEABLE_WITH_PAXEL = mod("mineable/paxel");
        public static final TagKey<Block> MINEABLE_WITH_SICKLE = mod("mineable/sickle");

        public static final TagKey<Block> NEEDS_COPPER_TOOL = mod("needs_copper_tool");
        public static final TagKey<Block> INCORRECT_FOR_COPPER_TOOL = mod("incorrect_for_copper_tool");

        public static final TagKey<Block> FLUFFY_BLOCKS = mod("fluffy_blocks");
        public static final TagKey<Block> NETHERWOOD_LOGS = mod("netherwood_logs");
        public static final TagKey<Block> NETHERWOOD_SOIL = mod("netherwood_soil");
        public static final TagKey<Block> PROSPECTOR_HAMMER_TARGETS = mod("prospector_hammer_targets");

        public static final TagKey<Block> ORES_BORT = common("ores/bort");
        public static final TagKey<Block> ORES_CRIMSON_IRON = common("ores/crimson_iron");
        public static final TagKey<Block> ORES_AZURE_SILVER = common("ores/azure_silver");

        public static final TagKey<Block> STORAGE_BLOCKS_RAW_CRIMSON_IRON = common("storage_blocks/raw_crimson_iron");
        public static final TagKey<Block> STORAGE_BLOCKS_RAW_AZURE_SILVER = common("storage_blocks/raw_azure_silver");

        public static final TagKey<Block> STORAGE_BLOCKS_BORT = common("storage_blocks/bort");
        public static final TagKey<Block> STORAGE_BLOCKS_BLAZE_GOLD = common("storage_blocks/blaze_gold");
        public static final TagKey<Block> STORAGE_BLOCKS_CRIMSON_IRON = common("storage_blocks/crimson_iron");
        public static final TagKey<Block> STORAGE_BLOCKS_CRIMSON_STEEL = common("storage_blocks/crimson_steel");
        public static final TagKey<Block> STORAGE_BLOCKS_AZURE_SILVER = common("storage_blocks/azure_silver");
        public static final TagKey<Block> STORAGE_BLOCKS_AZURE_ELECTRUM = common("storage_blocks/azure_electrum");
        public static final TagKey<Block> STORAGE_BLOCKS_TYRIAN_STEEL = common("storage_blocks/tyrian_steel");

        private Blocks() {}

        private static TagKey<Block> common(String path) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", path));
        }

        private static TagKey<Block> mod(String path) {
            return BlockTags.create(SilentGear.getId(path));
        }
    }

    public static final class Items {
        public static final TagKey<Item> FLUFFY_BLOCKS = mod("fluffy_blocks");
        public static final TagKey<Item> NETHERWOOD_LOGS = mod("netherwood_logs");
        public static final TagKey<Item> ORES_BORT = common("ores/bort");
        public static final TagKey<Item> ORES_CRIMSON_IRON = common("ores/crimson_iron");
        public static final TagKey<Item> ORES_AZURE_SILVER = common("ores/azure_silver");

        public static final TagKey<Item> COAL_GENERATOR_FUELS = silentsMechanisms("coal_generator_fuels");

        public static final TagKey<Item> STORAGE_BLOCKS_BORT = common("storage_blocks/bort");
        public static final TagKey<Item> STORAGE_BLOCKS_BLAZE_GOLD = common("storage_blocks/blaze_gold");
        public static final TagKey<Item> STORAGE_BLOCKS_CRIMSON_IRON = common("storage_blocks/crimson_iron");
        public static final TagKey<Item> STORAGE_BLOCKS_CRIMSON_STEEL = common("storage_blocks/crimson_steel");
        public static final TagKey<Item> STORAGE_BLOCKS_AZURE_SILVER = common("storage_blocks/azure_silver");
        public static final TagKey<Item> STORAGE_BLOCKS_AZURE_ELECTRUM = common("storage_blocks/azure_electrum");
        public static final TagKey<Item> STORAGE_BLOCKS_TYRIAN_STEEL = common("storage_blocks/tyrian_steel");

        public static final TagKey<Item> STORAGE_BLOCKS_RAW_CRIMSON_IRON = common("storage_blocks/raw_crimson_iron");
        public static final TagKey<Item> STORAGE_BLOCKS_RAW_AZURE_SILVER = common("storage_blocks/raw_azure_silver");

        public static final TagKey<Item> RAW_MATERIALS_CRIMSON_IRON = common("raw_materials/crimson_iron");
        public static final TagKey<Item> RAW_MATERIALS_AZURE_SILVER = common("raw_materials/azure_silver");

        public static final TagKey<Item> DUSTS_BLAZE_GOLD = common("dusts/blaze_gold");
        public static final TagKey<Item> DUSTS_CRIMSON_IRON = common("dusts/crimson_iron");
        public static final TagKey<Item> DUSTS_CRIMSON_STEEL = common("dusts/crimson_steel");
        public static final TagKey<Item> DUSTS_AZURE_SILVER = common("dusts/azure_silver");
        public static final TagKey<Item> DUSTS_AZURE_ELECTRUM = common("dusts/azure_electrum");
        public static final TagKey<Item> DUSTS_TYRIAN_STEEL = common("dusts/tyrian_steel");
        public static final TagKey<Item> DUSTS_STARMETAL = common("dusts/starmetal");

        public static final TagKey<Item> GEMS_BORT = common("gems/bort");

        public static final TagKey<Item> INGOTS_BLAZE_GOLD = common("ingots/blaze_gold");
        public static final TagKey<Item> INGOTS_BRONZE = common("ingots/bronze");
        public static final TagKey<Item> INGOTS_CRIMSON_IRON = common("ingots/crimson_iron");
        public static final TagKey<Item> INGOTS_CRIMSON_STEEL = common("ingots/crimson_steel");
        public static final TagKey<Item> INGOTS_AZURE_SILVER = common("ingots/azure_silver");
        public static final TagKey<Item> INGOTS_AZURE_ELECTRUM = common("ingots/azure_electrum");
        public static final TagKey<Item> INGOTS_TYRIAN_STEEL = common("ingots/tyrian_steel");

        public static final TagKey<Item> NUGGETS_BLAZE_GOLD = common("nuggets/blaze_gold");
        public static final TagKey<Item> NUGGETS_CRIMSON_IRON = common("nuggets/crimson_iron");
        public static final TagKey<Item> NUGGETS_CRIMSON_STEEL = common("nuggets/crimson_steel");
        public static final TagKey<Item> NUGGETS_AZURE_SILVER = common("nuggets/azure_silver");
        public static final TagKey<Item> NUGGETS_AZURE_ELECTRUM = common("nuggets/azure_electrum");
        public static final TagKey<Item> NUGGETS_TYRIAN_STEEL = common("nuggets/tyrian_steel");
        public static final TagKey<Item> NUGGETS_DIAMOND = common("nuggets/diamond");
        public static final TagKey<Item> NUGGETS_EMERALD = common("nuggets/emerald");

        public static final TagKey<Item> PAPER = common("paper");
        public static final TagKey<Item> BLUEPRINT_PAPER = mod("blueprint_paper");
        public static final TagKey<Item> REPAIR_KITS = mod("repair_kits");
        public static final TagKey<Item> TEMPLATE_BOARDS = mod("template_boards");

        public static final TagKey<Item> RODS_IRON = common("rods/iron");
        public static final TagKey<Item> RODS_NETHERWOOD = mod("rods/netherwood");
        public static final TagKey<Item> RODS_STONE = common("rods/stone");
        public static final TagKey<Item> RODS_ROUGH = mod("rods/rough");

        public static final TagKey<Item> FRUITS = common("fruits");

        public static final TagKey<Item> ARMORS_ELYTRA = common("armors/elytra");
        public static final TagKey<Item> TOOLS_HAMMER = common("tools/hammer");
        public static final TagKey<Item> TOOLS_KNIFE = common("tools/knife");
        public static final TagKey<Item> TOOLS_SAW = common("tools/saw");
        public static final TagKey<Item> TOOLS_SICKLE = common("tools/sickle");

        public static final TagKey<Item> BLUEPRINTS = mod("blueprints");

        public static final TagKey<Item> GRADER_CATALYSTS = mod("grader_catalysts");
        public static final TagKey<Item> GRADER_CATALYSTS_TIER_1 = mod("grader_catalysts/tier1");
        public static final TagKey<Item> GRADER_CATALYSTS_TIER_2 = mod("grader_catalysts/tier2");
        public static final TagKey<Item> GRADER_CATALYSTS_TIER_3 = mod("grader_catalysts/tier3");
        public static final TagKey<Item> GRADER_CATALYSTS_TIER_4 = mod("grader_catalysts/tier4");
        public static final TagKey<Item> GRADER_CATALYSTS_TIER_5 = mod("grader_catalysts/tier5");
        public static final List<TagKey<Item>> GRADER_CATALYSTS_TIERS = ImmutableList.of(GRADER_CATALYSTS_TIER_1, GRADER_CATALYSTS_TIER_2, GRADER_CATALYSTS_TIER_3, GRADER_CATALYSTS_TIER_4, GRADER_CATALYSTS_TIER_5);

        public static final TagKey<Item> STARLIGHT_CHARGER_CATALYSTS = mod("starlight_charger_catalysts");
        public static final TagKey<Item> STARLIGHT_CHARGER_CATALYSTS_TIER_1 = mod("starlight_charger_catalysts/tier1");
        public static final TagKey<Item> STARLIGHT_CHARGER_CATALYSTS_TIER_2 = mod("starlight_charger_catalysts/tier2");
        public static final TagKey<Item> STARLIGHT_CHARGER_CATALYSTS_TIER_3 = mod("starlight_charger_catalysts/tier3");
        public static final List<TagKey<Item>> STARLIGHT_CHARGER_TIERS = ImmutableList.of(STARLIGHT_CHARGER_CATALYSTS_TIER_1, STARLIGHT_CHARGER_CATALYSTS_TIER_2, STARLIGHT_CHARGER_CATALYSTS_TIER_3);

        public static final TagKey<Item> IMPERIAL_DROPS = mod("imperial_drops");
        public static final TagKey<Item> GOLD_DIGGER_DROPS = mod("gold_digger_drops");
        public static final TagKey<Item> GREEDY_MAGNET_ATTRACTED = mod("greedy_magnet_attracted");

        private Items() {}

        private static TagKey<Item> common(String path) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", path));
        }

        private static TagKey<Item> mod(String path) {
            return ItemTags.create(SilentGear.getId(path));
        }

        private static TagKey<Item> silentsMechanisms(String path) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath("silents_mechanisms", path));
        }
    }

    private SgTags() {}
}
