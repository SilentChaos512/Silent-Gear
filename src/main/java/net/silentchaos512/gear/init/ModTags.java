package net.silentchaos512.gear.init;

import com.google.common.collect.ImmutableList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.silentchaos512.gear.SilentGear;

import java.util.List;

public final class ModTags {
    public static final class Blocks {
        public static final TagKey<Block> FLUFFY_BLOCKS = mod("fluffy_blocks");
        public static final TagKey<Block> NETHERWOOD_LOGS = mod("netherwood_logs");
        public static final TagKey<Block> NETHERWOOD_SOIL = mod("netherwood_soil");
        public static final TagKey<Block> PROSPECTOR_HAMMER_TARGETS = mod("prospector_hammer_targets");

        public static final TagKey<Block> ORES_BORT = forge("ores/bort");
        public static final TagKey<Block> ORES_CRIMSON_IRON = forge("ores/crimson_iron");
        public static final TagKey<Block> ORES_AZURE_SILVER = forge("ores/azure_silver");

        public static final TagKey<Block> STORAGE_BLOCKS_RAW_CRIMSON_IRON = forge("storage_blocks/raw_crimson_iron");
        public static final TagKey<Block> STORAGE_BLOCKS_RAW_AZURE_SILVER = forge("storage_blocks/raw_azure_silver");

        public static final TagKey<Block> STORAGE_BLOCKS_BORT = forge("storage_blocks/bort");
        public static final TagKey<Block> STORAGE_BLOCKS_BLAZE_GOLD = forge("storage_blocks/blaze_gold");
        public static final TagKey<Block> STORAGE_BLOCKS_CRIMSON_IRON = forge("storage_blocks/crimson_iron");
        public static final TagKey<Block> STORAGE_BLOCKS_CRIMSON_STEEL = forge("storage_blocks/crimson_steel");
        public static final TagKey<Block> STORAGE_BLOCKS_AZURE_SILVER = forge("storage_blocks/azure_silver");
        public static final TagKey<Block> STORAGE_BLOCKS_AZURE_ELECTRUM = forge("storage_blocks/azure_electrum");
        public static final TagKey<Block> STORAGE_BLOCKS_TYRIAN_STEEL = forge("storage_blocks/tyrian_steel");

        private Blocks() {}

        private static TagKey<Block> forge(String path) {
            return BlockTags.create(new ResourceLocation("forge", path));
        }

        private static TagKey<Block> mod(String path) {
            return BlockTags.create(SilentGear.getId(path));
        }
    }

    public static final class Items {
        public static final TagKey<Item> FLUFFY_BLOCKS = mod("fluffy_blocks");
        public static final TagKey<Item> NETHERWOOD_LOGS = mod("netherwood_logs");
        public static final TagKey<Item> ORES_BORT = forge("ores/bort");
        public static final TagKey<Item> ORES_CRIMSON_IRON = forge("ores/crimson_iron");
        public static final TagKey<Item> ORES_AZURE_SILVER = forge("ores/azure_silver");

        public static final TagKey<Item> COAL_GENERATOR_FUELS = silentsMechanisms("coal_generator_fuels");

        public static final TagKey<Item> STORAGE_BLOCKS_BORT = forge("storage_blocks/bort");
        public static final TagKey<Item> STORAGE_BLOCKS_BLAZE_GOLD = forge("storage_blocks/blaze_gold");
        public static final TagKey<Item> STORAGE_BLOCKS_CRIMSON_IRON = forge("storage_blocks/crimson_iron");
        public static final TagKey<Item> STORAGE_BLOCKS_CRIMSON_STEEL = forge("storage_blocks/crimson_steel");
        public static final TagKey<Item> STORAGE_BLOCKS_AZURE_SILVER = forge("storage_blocks/azure_silver");
        public static final TagKey<Item> STORAGE_BLOCKS_AZURE_ELECTRUM = forge("storage_blocks/azure_electrum");
        public static final TagKey<Item> STORAGE_BLOCKS_TYRIAN_STEEL = forge("storage_blocks/tyrian_steel");

        public static final TagKey<Item> STORAGE_BLOCKS_RAW_CRIMSON_IRON = forge("storage_blocks/raw_crimson_iron");
        public static final TagKey<Item> STORAGE_BLOCKS_RAW_AZURE_SILVER = forge("storage_blocks/raw_azure_silver");

        public static final TagKey<Item> RAW_MATERIALS_CRIMSON_IRON = forge("raw_materials/crimson_iron");
        public static final TagKey<Item> RAW_MATERIALS_AZURE_SILVER = forge("raw_materials/azure_silver");

        public static final TagKey<Item> DUSTS_BLAZE_GOLD = forge("dusts/blaze_gold");
        public static final TagKey<Item> DUSTS_CRIMSON_IRON = forge("dusts/crimson_iron");
        public static final TagKey<Item> DUSTS_CRIMSON_STEEL = forge("dusts/crimson_steel");
        public static final TagKey<Item> DUSTS_AZURE_SILVER = forge("dusts/azure_silver");
        public static final TagKey<Item> DUSTS_AZURE_ELECTRUM = forge("dusts/azure_electrum");
        public static final TagKey<Item> DUSTS_TYRIAN_STEEL = forge("dusts/tyrian_steel");
        public static final TagKey<Item> DUSTS_STARMETAL = forge("dusts/starmetal");

        public static final TagKey<Item> GEMS_BORT = forge("gems/bort");

        public static final TagKey<Item> INGOTS_BRONZE = forge("ingots/bronze");
        public static final TagKey<Item> INGOTS_BLAZE_GOLD = forge("ingots/blaze_gold");
        public static final TagKey<Item> INGOTS_CRIMSON_IRON = forge("ingots/crimson_iron");
        public static final TagKey<Item> INGOTS_CRIMSON_STEEL = forge("ingots/crimson_steel");
        public static final TagKey<Item> INGOTS_AZURE_SILVER = forge("ingots/azure_silver");
        public static final TagKey<Item> INGOTS_AZURE_ELECTRUM = forge("ingots/azure_electrum");
        public static final TagKey<Item> INGOTS_TYRIAN_STEEL = forge("ingots/tyrian_steel");

        public static final TagKey<Item> NUGGETS_BLAZE_GOLD = forge("nuggets/blaze_gold");
        public static final TagKey<Item> NUGGETS_CRIMSON_IRON = forge("nuggets/crimson_iron");
        public static final TagKey<Item> NUGGETS_CRIMSON_STEEL = forge("nuggets/crimson_steel");
        public static final TagKey<Item> NUGGETS_AZURE_SILVER = forge("nuggets/azure_silver");
        public static final TagKey<Item> NUGGETS_AZURE_ELECTRUM = forge("nuggets/azure_electrum");
        public static final TagKey<Item> NUGGETS_TYRIAN_STEEL = forge("nuggets/tyrian_steel");
        public static final TagKey<Item> NUGGETS_DIAMOND = forge("nuggets/diamond");
        public static final TagKey<Item> NUGGETS_EMERALD = forge("nuggets/emerald");

        public static final TagKey<Item> PAPER = forge("paper");
        public static final TagKey<Item> BLUEPRINT_PAPER = mod("blueprint_paper");
        public static final TagKey<Item> REPAIR_KITS = mod("repair_kits");
        public static final TagKey<Item> TEMPLATE_BOARDS = mod("template_boards");

        public static final TagKey<Item> RODS_IRON = forge("rods/iron");
        public static final TagKey<Item> RODS_NETHERWOOD = mod("rods/netherwood");
        public static final TagKey<Item> RODS_STONE = forge("rods/stone");
        public static final TagKey<Item> RODS_ROUGH = mod("rods/rough");

        public static final TagKey<Item> FRUITS = forge("fruits");

        public static final TagKey<Item> AXES = forge("axes");
        public static final TagKey<Item> BOOTS = forge("boots");
        public static final TagKey<Item> BOWS = forge("bows");
        public static final TagKey<Item> CHESTPLATES = forge("chestplates");
        public static final TagKey<Item> CROSSBOWS = forge("crossbows");
        public static final TagKey<Item> ELYTRA = forge("elytra");
        public static final TagKey<Item> HAMMERS = forge("hammers");
        public static final TagKey<Item> HELMETS = forge("helmets");
        public static final TagKey<Item> HOES = forge("hoes");
        public static final TagKey<Item> KNIVES = forge("knives");
        public static final TagKey<Item> LEGGINGS = forge("leggings");
        public static final TagKey<Item> PICKAXES = forge("pickaxes");
        public static final TagKey<Item> SAWS = forge("saws");
        public static final TagKey<Item> SHIELDS = forge("shields");
        public static final TagKey<Item> SHOVELS = forge("shovels");
        public static final TagKey<Item> SICKLES = forge("sickles");
        public static final TagKey<Item> SWORDS = forge("swords");

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

        private Items() {}

        private static TagKey<Item> forge(String path) {
            return ItemTags.create(new ResourceLocation("forge", path));
        }

        private static TagKey<Item> mod(String path) {
            return ItemTags.create(SilentGear.getId(path));
        }

        private static TagKey<Item> silentsMechanisms(String path) {
            return ItemTags.create(new ResourceLocation("silents_mechanisms", path));
        }
    }

    private ModTags() {}
}
