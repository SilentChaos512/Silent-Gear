package net.silentchaos512.gear.init;

import com.google.common.collect.ImmutableList;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.tags.ItemTags;
import net.minecraft.resources.ResourceLocation;
import net.silentchaos512.gear.SilentGear;

import java.util.List;

public final class ModTags {
    public static final class Blocks {
        public static final Tag.Named<Block> FLUFFY_BLOCKS = mod("fluffy_blocks");
        public static final Tag.Named<Block> NETHERWOOD_LOGS = mod("netherwood_logs");
        public static final Tag.Named<Block> NETHERWOOD_SOIL = mod("netherwood_soil");
        public static final Tag.Named<Block> PROSPECTOR_HAMMER_TARGETS = mod("prospector_hammer_targets");

        public static final Tag.Named<Block> ORES_BORT = forge("ores/bort");
        public static final Tag.Named<Block> ORES_CRIMSON_IRON = forge("ores/crimson_iron");
        public static final Tag.Named<Block> ORES_AZURE_SILVER = forge("ores/azure_silver");

        public static final Tag.Named<Block> STORAGE_BLOCKS_BORT = forge("storage_blocks/bort");
        public static final Tag.Named<Block> STORAGE_BLOCKS_BLAZE_GOLD = forge("storage_blocks/blaze_gold");
        public static final Tag.Named<Block> STORAGE_BLOCKS_CRIMSON_IRON = forge("storage_blocks/crimson_iron");
        public static final Tag.Named<Block> STORAGE_BLOCKS_CRIMSON_STEEL = forge("storage_blocks/crimson_steel");
        public static final Tag.Named<Block> STORAGE_BLOCKS_AZURE_SILVER = forge("storage_blocks/azure_silver");
        public static final Tag.Named<Block> STORAGE_BLOCKS_AZURE_ELECTRUM = forge("storage_blocks/azure_electrum");
        public static final Tag.Named<Block> STORAGE_BLOCKS_TYRIAN_STEEL = forge("storage_blocks/tyrian_steel");

        private Blocks() {}

        private static Tag.Named<Block> forge(String path) {
            return BlockTags.bind(new ResourceLocation("forge", path).toString());
        }

        private static Tag.Named<Block> mod(String path) {
            return BlockTags.bind(SilentGear.getId(path).toString());
        }
    }

    public static final class Items {
        public static final Tag.Named<Item> FLUFFY_BLOCKS = mod("fluffy_blocks");
        public static final Tag.Named<Item> NETHERWOOD_LOGS = mod("netherwood_logs");
        public static final Tag.Named<Item> ORES_BORT = forge("ores/bort");
        public static final Tag.Named<Item> ORES_CRIMSON_IRON = forge("ores/crimson_iron");
        public static final Tag.Named<Item> ORES_AZURE_SILVER = forge("ores/azure_silver");

        public static final Tag.Named<Item> CHUNKS_CRIMSON_IRON = silentsMechanisms("chunks/crimson_iron");
        public static final Tag.Named<Item> CHUNKS_AZURE_SILVER = silentsMechanisms("chunks/azure_silver");
        public static final Tag.Named<Item> COAL_GENERATOR_FUELS = silentsMechanisms("coal_generator_fuels");

        public static final Tag.Named<Item> STORAGE_BLOCKS_BORT = forge("storage_blocks/bort");
        public static final Tag.Named<Item> STORAGE_BLOCKS_BLAZE_GOLD = forge("storage_blocks/blaze_gold");
        public static final Tag.Named<Item> STORAGE_BLOCKS_CRIMSON_IRON = forge("storage_blocks/crimson_iron");
        public static final Tag.Named<Item> STORAGE_BLOCKS_CRIMSON_STEEL = forge("storage_blocks/crimson_steel");
        public static final Tag.Named<Item> STORAGE_BLOCKS_AZURE_SILVER = forge("storage_blocks/azure_silver");
        public static final Tag.Named<Item> STORAGE_BLOCKS_AZURE_ELECTRUM = forge("storage_blocks/azure_electrum");
        public static final Tag.Named<Item> STORAGE_BLOCKS_TYRIAN_STEEL = forge("storage_blocks/tyrian_steel");

        public static final Tag.Named<Item> DUSTS_BLAZE_GOLD = forge("dusts/blaze_gold");
        public static final Tag.Named<Item> DUSTS_CRIMSON_IRON = forge("dusts/crimson_iron");
        public static final Tag.Named<Item> DUSTS_CRIMSON_STEEL = forge("dusts/crimson_steel");
        public static final Tag.Named<Item> DUSTS_AZURE_SILVER = forge("dusts/azure_silver");
        public static final Tag.Named<Item> DUSTS_AZURE_ELECTRUM = forge("dusts/azure_electrum");
        public static final Tag.Named<Item> DUSTS_TYRIAN_STEEL = forge("dusts/tyrian_steel");
        public static final Tag.Named<Item> DUSTS_STARMETAL = forge("dusts/starmetal");

        public static final Tag.Named<Item> GEMS_BORT = forge("gems/bort");

        public static final Tag.Named<Item> INGOTS_BLAZE_GOLD = forge("ingots/blaze_gold");
        public static final Tag.Named<Item> INGOTS_CRIMSON_IRON = forge("ingots/crimson_iron");
        public static final Tag.Named<Item> INGOTS_CRIMSON_STEEL = forge("ingots/crimson_steel");
        public static final Tag.Named<Item> INGOTS_AZURE_SILVER = forge("ingots/azure_silver");
        public static final Tag.Named<Item> INGOTS_AZURE_ELECTRUM = forge("ingots/azure_electrum");
        public static final Tag.Named<Item> INGOTS_TYRIAN_STEEL = forge("ingots/tyrian_steel");

        public static final Tag.Named<Item> NUGGETS_BLAZE_GOLD = forge("nuggets/blaze_gold");
        public static final Tag.Named<Item> NUGGETS_CRIMSON_IRON = forge("nuggets/crimson_iron");
        public static final Tag.Named<Item> NUGGETS_CRIMSON_STEEL = forge("nuggets/crimson_steel");
        public static final Tag.Named<Item> NUGGETS_AZURE_SILVER = forge("nuggets/azure_silver");
        public static final Tag.Named<Item> NUGGETS_AZURE_ELECTRUM = forge("nuggets/azure_electrum");
        public static final Tag.Named<Item> NUGGETS_TYRIAN_STEEL = forge("nuggets/tyrian_steel");
        public static final Tag.Named<Item> NUGGETS_DIAMOND = forge("nuggets/diamond");
        public static final Tag.Named<Item> NUGGETS_EMERALD = forge("nuggets/emerald");

        public static final Tag.Named<Item> PAPER = forge("paper");
        public static final Tag.Named<Item> BLUEPRINT_PAPER = mod("blueprint_paper");
        public static final Tag.Named<Item> REPAIR_KITS = mod("repair_kits");
        public static final Tag.Named<Item> TEMPLATE_BOARDS = mod("template_boards");

        public static final Tag.Named<Item> RODS_IRON = forge("rods/iron");
        public static final Tag.Named<Item> RODS_NETHERWOOD = mod("rods/netherwood");
        public static final Tag.Named<Item> RODS_STONE = forge("rods/stone");
        public static final Tag.Named<Item> RODS_ROUGH = mod("rods/rough");

        public static final Tag.Named<Item> FRUITS = forge("fruits");

        public static final Tag.Named<Item> AXES = forge("axes");
        public static final Tag.Named<Item> BOOTS = forge("boots");
        public static final Tag.Named<Item> BOWS = forge("bows");
        public static final Tag.Named<Item> CHESTPLATES = forge("chestplates");
        public static final Tag.Named<Item> CROSSBOWS = forge("crossbows");
        public static final Tag.Named<Item> ELYTRA = forge("elytra");
        public static final Tag.Named<Item> HAMMERS = forge("hammers");
        public static final Tag.Named<Item> HELMETS = forge("helmets");
        public static final Tag.Named<Item> HOES = forge("hoes");
        public static final Tag.Named<Item> KNIVES = forge("knives");
        public static final Tag.Named<Item> LEGGINGS = forge("leggings");
        public static final Tag.Named<Item> PICKAXES = forge("pickaxes");
        public static final Tag.Named<Item> SAWS = forge("saws");
        public static final Tag.Named<Item> SHIELDS = forge("shields");
        public static final Tag.Named<Item> SHOVELS = forge("shovels");
        public static final Tag.Named<Item> SICKLES = forge("sickles");
        public static final Tag.Named<Item> SWORDS = forge("swords");

        public static final Tag.Named<Item> BLUEPRINTS = mod("blueprints");

        public static final Tag.Named<Item> GRADER_CATALYSTS = mod("grader_catalysts");
        public static final Tag.Named<Item> GRADER_CATALYSTS_TIER_1 = mod("grader_catalysts/tier1");
        public static final Tag.Named<Item> GRADER_CATALYSTS_TIER_2 = mod("grader_catalysts/tier2");
        public static final Tag.Named<Item> GRADER_CATALYSTS_TIER_3 = mod("grader_catalysts/tier3");
        public static final Tag.Named<Item> GRADER_CATALYSTS_TIER_4 = mod("grader_catalysts/tier4");
        public static final Tag.Named<Item> GRADER_CATALYSTS_TIER_5 = mod("grader_catalysts/tier5");
        public static final List<Tag.Named<Item>> GRADER_CATALYSTS_TIERS = ImmutableList.of(GRADER_CATALYSTS_TIER_1, GRADER_CATALYSTS_TIER_2, GRADER_CATALYSTS_TIER_3, GRADER_CATALYSTS_TIER_4, GRADER_CATALYSTS_TIER_5);

        public static final Tag.Named<Item> STARLIGHT_CHARGER_CATALYSTS = mod("starlight_charger_catalysts");
        public static final Tag.Named<Item> STARLIGHT_CHARGER_CATALYSTS_TIER_1 = mod("starlight_charger_catalysts/tier1");
        public static final Tag.Named<Item> STARLIGHT_CHARGER_CATALYSTS_TIER_2 = mod("starlight_charger_catalysts/tier2");
        public static final Tag.Named<Item> STARLIGHT_CHARGER_CATALYSTS_TIER_3 = mod("starlight_charger_catalysts/tier3");
        public static final List<Tag.Named<Item>> STARLIGHT_CHARGER_TIERS = ImmutableList.of(STARLIGHT_CHARGER_CATALYSTS_TIER_1, STARLIGHT_CHARGER_CATALYSTS_TIER_2, STARLIGHT_CHARGER_CATALYSTS_TIER_3);

        private Items() {}

        private static Tag.Named<Item> forge(String path) {
            return ItemTags.bind(new ResourceLocation("forge", path).toString());
        }

        private static Tag.Named<Item> mod(String path) {
            return ItemTags.bind(SilentGear.getId(path).toString());
        }

        private static Tag.Named<Item> silentsMechanisms(String path) {
            return ItemTags.bind(new ResourceLocation("silents_mechanisms", path).toString());
        }
    }

    private ModTags() {}
}
