package net.silentchaos512.gear.data;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.item.CraftingItems;
import net.silentchaos512.gear.item.blueprint.AbstractBlueprintItem;
import net.silentchaos512.gear.item.gear.GearCurioItem;
import net.silentchaos512.gear.setup.GearItemSets;
import net.silentchaos512.gear.setup.SgBlocks;
import net.silentchaos512.gear.setup.SgItems;
import net.silentchaos512.gear.setup.SgTags;
import net.silentchaos512.gear.util.Const;

import java.util.Arrays;
import java.util.Comparator;

public class ModItemTagsProvider extends ItemTagsProvider {
    public ModItemTagsProvider(GatherDataEvent event, BlockTagsProvider blocks) {
        super(event.getGenerator().getPackOutput(), event.getLookupProvider(), blocks.contentsGetter(), SilentGear.MOD_ID, event.getExistingFileHelper());
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        // Forge
        copy(SgTags.Blocks.ORES_BORT, SgTags.Items.ORES_BORT);
        copy(SgTags.Blocks.ORES_CRIMSON_IRON, SgTags.Items.ORES_CRIMSON_IRON);
        copy(SgTags.Blocks.ORES_AZURE_SILVER, SgTags.Items.ORES_AZURE_SILVER);
        copy(Tags.Blocks.ORES, Tags.Items.ORES);

        copy(SgTags.Blocks.STORAGE_BLOCKS_BORT, SgTags.Items.STORAGE_BLOCKS_BORT);
        copy(SgTags.Blocks.STORAGE_BLOCKS_BLAZE_GOLD, SgTags.Items.STORAGE_BLOCKS_BLAZE_GOLD);
        copy(SgTags.Blocks.STORAGE_BLOCKS_CRIMSON_IRON, SgTags.Items.STORAGE_BLOCKS_CRIMSON_IRON);
        copy(SgTags.Blocks.STORAGE_BLOCKS_CRIMSON_STEEL, SgTags.Items.STORAGE_BLOCKS_CRIMSON_STEEL);
        copy(SgTags.Blocks.STORAGE_BLOCKS_AZURE_SILVER, SgTags.Items.STORAGE_BLOCKS_AZURE_SILVER);
        copy(SgTags.Blocks.STORAGE_BLOCKS_AZURE_ELECTRUM, SgTags.Items.STORAGE_BLOCKS_AZURE_ELECTRUM);
        copy(SgTags.Blocks.STORAGE_BLOCKS_TYRIAN_STEEL, SgTags.Items.STORAGE_BLOCKS_TYRIAN_STEEL);
        copy(Tags.Blocks.STORAGE_BLOCKS, Tags.Items.STORAGE_BLOCKS);

        builder(SgTags.Items.STORAGE_BLOCKS_RAW_CRIMSON_IRON, SgBlocks.RAW_CRIMSON_IRON_BLOCK);
        builder(SgTags.Items.STORAGE_BLOCKS_RAW_AZURE_SILVER, SgBlocks.RAW_AZURE_SILVER_BLOCK);

        builder(SgTags.Items.RAW_MATERIALS_CRIMSON_IRON, CraftingItems.RAW_CRIMSON_IRON);
        builder(SgTags.Items.RAW_MATERIALS_AZURE_SILVER, CraftingItems.RAW_AZURE_SILVER);
        tag(Tags.Items.RAW_MATERIALS)
                .addTag(SgTags.Items.RAW_MATERIALS_CRIMSON_IRON)
                .addTag(SgTags.Items.RAW_MATERIALS_AZURE_SILVER);

        builder(SgTags.Items.COAL_GENERATOR_FUELS, SgItems.NETHERWOOD_CHARCOAL, SgBlocks.NETHERWOOD_CHARCOAL_BLOCK);

        builder(SgTags.Items.DUSTS_BLAZE_GOLD, CraftingItems.BLAZE_GOLD_DUST);
        builder(SgTags.Items.DUSTS_CRIMSON_IRON, CraftingItems.CRIMSON_IRON_DUST);
        builder(SgTags.Items.DUSTS_CRIMSON_STEEL, CraftingItems.CRIMSON_STEEL_DUST);
        builder(SgTags.Items.DUSTS_AZURE_SILVER, CraftingItems.AZURE_SILVER_DUST);
        builder(SgTags.Items.DUSTS_AZURE_ELECTRUM, CraftingItems.AZURE_ELECTRUM_DUST);
        builder(SgTags.Items.DUSTS_TYRIAN_STEEL, CraftingItems.TYRIAN_STEEL_DUST);
        builder(SgTags.Items.DUSTS_STARMETAL, CraftingItems.STARMETAL_DUST);
        tag(Tags.Items.DUSTS)
                .addTag(SgTags.Items.DUSTS_BLAZE_GOLD)
                .addTag(SgTags.Items.DUSTS_CRIMSON_IRON)
                .addTag(SgTags.Items.DUSTS_CRIMSON_STEEL)
                .addTag(SgTags.Items.DUSTS_AZURE_SILVER)
                .addTag(SgTags.Items.DUSTS_AZURE_ELECTRUM)
                .addTag(SgTags.Items.DUSTS_TYRIAN_STEEL)
                .addTag(SgTags.Items.DUSTS_STARMETAL);

        builder(SgTags.Items.GEMS_BORT, CraftingItems.BORT);
        tag(Tags.Items.GEMS)
                .addTag(SgTags.Items.GEMS_BORT);

        builder(SgTags.Items.INGOTS_BRONZE, CraftingItems.BRONZE_INGOT);
        builder(SgTags.Items.INGOTS_BLAZE_GOLD, CraftingItems.BLAZE_GOLD_INGOT);
        builder(SgTags.Items.INGOTS_CRIMSON_IRON, CraftingItems.CRIMSON_IRON_INGOT);
        builder(SgTags.Items.INGOTS_CRIMSON_STEEL, CraftingItems.CRIMSON_STEEL_INGOT);
        builder(SgTags.Items.INGOTS_AZURE_SILVER, CraftingItems.AZURE_SILVER_INGOT);
        builder(SgTags.Items.INGOTS_AZURE_ELECTRUM, CraftingItems.AZURE_ELECTRUM_INGOT);
        builder(SgTags.Items.INGOTS_TYRIAN_STEEL, CraftingItems.TYRIAN_STEEL_INGOT);
        tag(Tags.Items.INGOTS)
                .addTag(SgTags.Items.INGOTS_BLAZE_GOLD)
                .addTag(SgTags.Items.INGOTS_CRIMSON_IRON)
                .addTag(SgTags.Items.INGOTS_CRIMSON_STEEL)
                .addTag(SgTags.Items.INGOTS_AZURE_SILVER)
                .addTag(SgTags.Items.INGOTS_AZURE_ELECTRUM)
                .addTag(SgTags.Items.INGOTS_TYRIAN_STEEL);

        builder(SgTags.Items.NUGGETS_BLAZE_GOLD, CraftingItems.BLAZE_GOLD_NUGGET);
        builder(SgTags.Items.NUGGETS_CRIMSON_IRON, CraftingItems.CRIMSON_IRON_NUGGET);
        builder(SgTags.Items.NUGGETS_CRIMSON_STEEL, CraftingItems.CRIMSON_STEEL_NUGGET);
        builder(SgTags.Items.NUGGETS_AZURE_SILVER, CraftingItems.AZURE_SILVER_NUGGET);
        builder(SgTags.Items.NUGGETS_AZURE_ELECTRUM, CraftingItems.AZURE_ELECTRUM_NUGGET);
        builder(SgTags.Items.NUGGETS_TYRIAN_STEEL, CraftingItems.TYRIAN_STEEL_NUGGET);
        builder(SgTags.Items.NUGGETS_DIAMOND, CraftingItems.DIAMOND_SHARD);
        builder(SgTags.Items.NUGGETS_EMERALD, CraftingItems.EMERALD_SHARD);
        tag(Tags.Items.NUGGETS)
                .addTag(SgTags.Items.NUGGETS_BLAZE_GOLD)
                .addTag(SgTags.Items.NUGGETS_CRIMSON_IRON)
                .addTag(SgTags.Items.NUGGETS_CRIMSON_STEEL)
                .addTag(SgTags.Items.NUGGETS_AZURE_SILVER)
                .addTag(SgTags.Items.NUGGETS_AZURE_ELECTRUM)
                .addTag(SgTags.Items.NUGGETS_TYRIAN_STEEL)
                .addTag(SgTags.Items.NUGGETS_DIAMOND)
                .addTag(SgTags.Items.NUGGETS_EMERALD);

        builder(SgTags.Items.RODS_IRON, CraftingItems.IRON_ROD);
        builder(SgTags.Items.RODS_NETHERWOOD, CraftingItems.NETHERWOOD_STICK);
        builder(SgTags.Items.RODS_ROUGH, CraftingItems.ROUGH_ROD);
        builder(SgTags.Items.RODS_STONE, CraftingItems.STONE_ROD);
        builder(Tags.Items.RODS_WOODEN, CraftingItems.NETHERWOOD_STICK);
        tag(Tags.Items.RODS)
                .addTag(SgTags.Items.RODS_IRON)
                .addTag(SgTags.Items.RODS_NETHERWOOD)
                .addTag(SgTags.Items.RODS_ROUGH)
                .addTag(SgTags.Items.RODS_STONE);

        builder(SgTags.Items.PAPER, Items.PAPER);
        builder(SgTags.Items.BLUEPRINT_PAPER, CraftingItems.BLUEPRINT_PAPER);
        builder(SgTags.Items.TEMPLATE_BOARDS, CraftingItems.TEMPLATE_BOARD);

        builder(SgTags.Items.FRUITS, SgItems.NETHER_BANANA);
        builder(Tags.Items.SEEDS, SgItems.FLAX_SEEDS, SgItems.FLUFFY_SEEDS);
        builder(Tags.Items.STRINGS, CraftingItems.FLAX_STRING, CraftingItems.SINEW_FIBER);

        builder(ItemTags.HEAD_ARMOR,
                GearItemSets.HELMET.gearItem());
        builder(ItemTags.CHEST_ARMOR,
                GearItemSets.CHESTPLATE.gearItem());
        builder(ItemTags.LEG_ARMOR,
                GearItemSets.LEGGINGS.gearItem());
        builder(ItemTags.FOOT_ARMOR,
                GearItemSets.BOOTS.gearItem());
        builder(SgTags.Items.ARMORS_ELYTRA,
                GearItemSets.ELYTRA.gearItem());
        builder(ItemTags.AXES,
                GearItemSets.AXE.gearItem(),
                GearItemSets.SAW.gearItem(),
                GearItemSets.MACHETE.gearItem(),
                GearItemSets.MATTOCK.gearItem(),
                GearItemSets.PAXEL.gearItem());
        builder(Tags.Items.TOOLS_BOWS,
                GearItemSets.BOW.gearItem());
        builder(Tags.Items.TOOLS_CROSSBOWS,
                GearItemSets.CROSSBOW.gearItem());
        builder(SgTags.Items.HAMMERS,
                GearItemSets.HAMMER.gearItem(),
                GearItemSets.PROSPECTOR_HAMMER.gearItem());
        builder(ItemTags.HOES,
                GearItemSets.HOE.gearItem(),
                GearItemSets.MATTOCK.gearItem());
        builder(SgTags.Items.KNIVES,
                GearItemSets.KNIFE.gearItem(),
                GearItemSets.DAGGER.gearItem());
        builder(ItemTags.PICKAXES,
                GearItemSets.HAMMER.gearItem(),
                GearItemSets.PAXEL.gearItem(),
                GearItemSets.PICKAXE.gearItem());
        builder(SgTags.Items.TOOLS_SAWS,
                GearItemSets.SAW.gearItem());
        builder(Tags.Items.TOOLS_SHEARS,
                GearItemSets.SHEARS.gearItem());
        builder(Tags.Items.TOOLS_SHIELDS,
                GearItemSets.SHIELD.gearItem());
        builder(ItemTags.SHOVELS,
                GearItemSets.EXCAVATOR.gearItem(),
                GearItemSets.MATTOCK.gearItem(),
                GearItemSets.PAXEL.gearItem(),
                GearItemSets.SHOVEL.gearItem());
        builder(SgTags.Items.TOOLS_SICKLES,
                GearItemSets.SICKLE.gearItem());
        builder(ItemTags.SWORDS,
                GearItemSets.DAGGER.gearItem(),
                GearItemSets.KATANA.gearItem(),
                GearItemSets.MACHETE.gearItem(),
                GearItemSets.SWORD.gearItem());

        // Minecraft
        copy(BlockTags.LEAVES, ItemTags.LEAVES);
        copy(BlockTags.LOGS, ItemTags.LOGS);
        copy(BlockTags.PLANKS, ItemTags.PLANKS);
        copy(BlockTags.SAPLINGS, ItemTags.SAPLINGS);
        copy(BlockTags.WOODEN_DOORS, ItemTags.WOODEN_DOORS);
        copy(BlockTags.WOODEN_FENCES, ItemTags.WOODEN_FENCES);
        copy(BlockTags.WOODEN_SLABS, ItemTags.WOODEN_SLABS);
        copy(BlockTags.WOODEN_STAIRS, ItemTags.WOODEN_STAIRS);
        copy(BlockTags.WOODEN_TRAPDOORS, ItemTags.WOODEN_TRAPDOORS);

        tag(ItemTags.ARROWS).add(GearItemSets.ARROW.gearItem());
        builder(ItemTags.PIGLIN_LOVED,
                SgBlocks.BLAZE_GOLD_BLOCK,
                SgItems.GOLDEN_NETHER_BANANA,
                CraftingItems.BLAZE_GOLD_DUST,
                CraftingItems.BLAZE_GOLD_INGOT);

        builder(ItemTags.CLUSTER_MAX_HARVESTABLES,
                GearItemSets.PICKAXE.gearItem(),
                GearItemSets.PAXEL.gearItem(),
                GearItemSets.PROSPECTOR_HAMMER.gearItem()
        );

        // Silent Gear

        copy(SgTags.Blocks.FLUFFY_BLOCKS, SgTags.Items.FLUFFY_BLOCKS);
        copy(SgTags.Blocks.NETHERWOOD_LOGS, SgTags.Items.NETHERWOOD_LOGS);

        tag(SgTags.Items.GRADER_CATALYSTS_TIER_1).add(CraftingItems.GLOWING_DUST.asItem());
        tag(SgTags.Items.GRADER_CATALYSTS_TIER_2).add(CraftingItems.BLAZING_DUST.asItem());
        tag(SgTags.Items.GRADER_CATALYSTS_TIER_3).add(CraftingItems.GLITTERY_DUST.asItem());
        tag(SgTags.Items.GRADER_CATALYSTS_TIER_4);
        tag(SgTags.Items.GRADER_CATALYSTS_TIER_5);
        tag(SgTags.Items.GRADER_CATALYSTS)
                .addTag(SgTags.Items.GRADER_CATALYSTS_TIER_1)
                .addTag(SgTags.Items.GRADER_CATALYSTS_TIER_2)
                .addTag(SgTags.Items.GRADER_CATALYSTS_TIER_3)
                .addTag(SgTags.Items.GRADER_CATALYSTS_TIER_4)
                .addTag(SgTags.Items.GRADER_CATALYSTS_TIER_5);

        tag(SgTags.Items.STARLIGHT_CHARGER_CATALYSTS_TIER_1).addTag(SgTags.Items.DUSTS_BLAZE_GOLD);
        tag(SgTags.Items.STARLIGHT_CHARGER_CATALYSTS_TIER_2).addTag(SgTags.Items.DUSTS_AZURE_SILVER);
        tag(SgTags.Items.STARLIGHT_CHARGER_CATALYSTS_TIER_3).addTag(SgTags.Items.DUSTS_STARMETAL);
        tag(SgTags.Items.STARLIGHT_CHARGER_CATALYSTS)
                .addTag(SgTags.Items.STARLIGHT_CHARGER_CATALYSTS_TIER_1)
                .addTag(SgTags.Items.STARLIGHT_CHARGER_CATALYSTS_TIER_2)
                .addTag(SgTags.Items.STARLIGHT_CHARGER_CATALYSTS_TIER_3);

        builder(SgTags.Items.REPAIR_KITS,
                SgItems.VERY_CRUDE_REPAIR_KIT,
                SgItems.CRUDE_REPAIR_KIT,
                SgItems.STURDY_REPAIR_KIT,
                SgItems.CRIMSON_REPAIR_KIT,
                SgItems.AZURE_REPAIR_KIT);

        tag(SgTags.Items.IMPERIAL_DROPS).addTag(Tags.Items.GEMS);
        tag(SgTags.Items.GOLD_DIGGER_DROPS).addTag(Tags.Items.NUGGETS);
        tag(SgTags.Items.GREEDY_MAGNET_ATTRACTED)
                .addTag(Tags.Items.GEMS)
                .addTag(Tags.Items.ORES)
                .addTag(Tags.Items.INGOTS)
                .addTag(Tags.Items.NUGGETS)
                .add(Items.ANCIENT_DEBRIS)
                .add(Items.NETHERITE_SCRAP);

        // Blueprints
        Multimap<ResourceLocation, AbstractBlueprintItem> blueprints = MultimapBuilder.linkedHashKeys().arrayListValues().build();
        BuiltInRegistries.ITEM.stream()
                .filter(item -> item instanceof AbstractBlueprintItem)
                .map(item -> (AbstractBlueprintItem) item)
                .sorted(Comparator.comparing(blueprint -> blueprint.getItemTag().location()))
                .forEach(item -> blueprints.put(item.getItemTag().location(), item));
        TagsProvider.TagAppender<Item> blueprintsBuilder = tag(SgTags.Items.BLUEPRINTS);
        blueprints.keySet().forEach(tagId -> {
            TagKey<Item> tag = ItemTags.create(tagId);
            tag(tag).add(blueprints.get(tagId).toArray(new Item[0]));
            blueprintsBuilder.addTag(tag);
        });

        // Curios
        SgItems.getItems(GearCurioItem.class).forEach(item ->
                builder(makeWrapper(Const.CURIOS, item.getSlot()), item));

        builder(makeWrapper(Const.CURIOS, "back"), GearItemSets.ELYTRA.gearItem());
    }

    private TagKey<Item> makeWrapper(String namespace, String path) {
        return ItemTags.create(new ResourceLocation(namespace, path));
    }

    private void builder(TagKey<Item> tag, ItemLike... items) {
        tag(tag).add(Arrays.stream(items).map(ItemLike::asItem).toArray(Item[]::new));
    }
}
