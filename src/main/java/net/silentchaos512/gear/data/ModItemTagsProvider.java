package net.silentchaos512.gear.data;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.init.SgBlocks;
import net.silentchaos512.gear.init.SgItems;
import net.silentchaos512.gear.init.SgTags;
import net.silentchaos512.gear.item.CraftingItems;
import net.silentchaos512.gear.item.blueprint.AbstractBlueprintItem;
import net.silentchaos512.gear.item.gear.GearCurioItem;
import net.silentchaos512.gear.util.Const;

import java.util.Arrays;
import java.util.Comparator;

public class ModItemTagsProvider extends ItemTagsProvider {
    public ModItemTagsProvider(DataGenerator generatorIn, BlockTagsProvider blocks, ExistingFileHelper existingFileHelper) {
        super(generatorIn, blocks, SilentGear.MOD_ID, existingFileHelper);
    }

    @Override
    public String getName() {
        return "Silent Gear - Item Tags";
    }

    @SuppressWarnings("OverlyLongMethod")
    @Override
    public void addTags() {
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
        getBuilder(Tags.Items.RAW_MATERIALS)
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
        getBuilder(Tags.Items.DUSTS)
                .addTag(SgTags.Items.DUSTS_BLAZE_GOLD)
                .addTag(SgTags.Items.DUSTS_CRIMSON_IRON)
                .addTag(SgTags.Items.DUSTS_CRIMSON_STEEL)
                .addTag(SgTags.Items.DUSTS_AZURE_SILVER)
                .addTag(SgTags.Items.DUSTS_AZURE_ELECTRUM)
                .addTag(SgTags.Items.DUSTS_TYRIAN_STEEL)
                .addTag(SgTags.Items.DUSTS_STARMETAL);

        builder(SgTags.Items.GEMS_BORT, CraftingItems.BORT);
        getBuilder(Tags.Items.GEMS)
                .addTag(SgTags.Items.GEMS_BORT);

        builder(SgTags.Items.INGOTS_BRONZE, CraftingItems.BRONZE_INGOT);
        builder(SgTags.Items.INGOTS_BLAZE_GOLD, CraftingItems.BLAZE_GOLD_INGOT);
        builder(SgTags.Items.INGOTS_CRIMSON_IRON, CraftingItems.CRIMSON_IRON_INGOT);
        builder(SgTags.Items.INGOTS_CRIMSON_STEEL, CraftingItems.CRIMSON_STEEL_INGOT);
        builder(SgTags.Items.INGOTS_AZURE_SILVER, CraftingItems.AZURE_SILVER_INGOT);
        builder(SgTags.Items.INGOTS_AZURE_ELECTRUM, CraftingItems.AZURE_ELECTRUM_INGOT);
        builder(SgTags.Items.INGOTS_TYRIAN_STEEL, CraftingItems.TYRIAN_STEEL_INGOT);
        getBuilder(Tags.Items.INGOTS)
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
        getBuilder(Tags.Items.NUGGETS)
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
        getBuilder(Tags.Items.RODS)
                .addTag(SgTags.Items.RODS_IRON)
                .addTag(SgTags.Items.RODS_NETHERWOOD)
                .addTag(SgTags.Items.RODS_ROUGH)
                .addTag(SgTags.Items.RODS_STONE);

        builder(SgTags.Items.PAPER, Items.PAPER);
        builder(SgTags.Items.BLUEPRINT_PAPER, CraftingItems.BLUEPRINT_PAPER);
        builder(SgTags.Items.TEMPLATE_BOARDS, CraftingItems.TEMPLATE_BOARD);

        builder(SgTags.Items.FRUITS, SgItems.NETHER_BANANA);
        builder(Tags.Items.SEEDS, SgItems.FLAX_SEEDS, SgItems.FLUFFY_SEEDS);
        builder(Tags.Items.STRING, CraftingItems.FLAX_STRING, CraftingItems.SINEW_FIBER);

        builder(SgTags.Items.AXES, SgItems.AXE, SgItems.SAW, SgItems.MACHETE, SgItems.MATTOCK, SgItems.PAXEL);
        builder(SgTags.Items.BOOTS, SgItems.BOOTS);
        builder(SgTags.Items.BOWS, SgItems.BOW);
        builder(SgTags.Items.CHESTPLATES, SgItems.CHESTPLATE);
        builder(SgTags.Items.CROSSBOWS, SgItems.CROSSBOW);
        builder(SgTags.Items.ELYTRA, SgItems.ELYTRA);
        builder(SgTags.Items.HAMMERS, SgItems.HAMMER, SgItems.PROSPECTOR_HAMMER);
        builder(SgTags.Items.HELMETS, SgItems.HELMET);
        builder(SgTags.Items.HOES, SgItems.MATTOCK);
        builder(SgTags.Items.KNIVES, SgItems.KNIFE, SgItems.DAGGER);
        getBuilder(makeWrapper("forge", "tools/knives")).addTag(SgTags.Items.KNIVES); // alt tag that some mods are apparently using :(
        builder(SgTags.Items.LEGGINGS, SgItems.LEGGINGS);
        builder(SgTags.Items.PICKAXES, SgItems.HAMMER, SgItems.PAXEL, SgItems.PICKAXE);
        builder(SgTags.Items.SAWS, SgItems.SAW);
        builder(Tags.Items.SHEARS, SgItems.SHEARS);
        builder(SgTags.Items.SHIELDS, SgItems.SHIELD);
        builder(SgTags.Items.SHOVELS, SgItems.EXCAVATOR, SgItems.MATTOCK, SgItems.PAXEL, SgItems.SHOVEL);
        builder(SgTags.Items.SICKLES, SgItems.SICKLE);
        builder(SgTags.Items.SWORDS, SgItems.DAGGER, SgItems.KATANA, SgItems.MACHETE, SgItems.SWORD);

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

        getBuilder(ItemTags.ARROWS).add(SgItems.ARROW.get());
        builder(ItemTags.PIGLIN_LOVED,
                SgBlocks.BLAZE_GOLD_BLOCK,
                SgItems.GOLDEN_NETHER_BANANA,
                CraftingItems.BLAZE_GOLD_DUST,
                CraftingItems.BLAZE_GOLD_INGOT);

        builder(ItemTags.CLUSTER_MAX_HARVESTABLES, SgItems.PICKAXE, SgItems.PAXEL, SgItems.PROSPECTOR_HAMMER);

        // Silent Gear

        copy(SgTags.Blocks.FLUFFY_BLOCKS, SgTags.Items.FLUFFY_BLOCKS);
        copy(SgTags.Blocks.NETHERWOOD_LOGS, SgTags.Items.NETHERWOOD_LOGS);

        getBuilder(SgTags.Items.GRADER_CATALYSTS_TIER_1).add(CraftingItems.GLOWING_DUST.asItem());
        getBuilder(SgTags.Items.GRADER_CATALYSTS_TIER_2).add(CraftingItems.BLAZING_DUST.asItem());
        getBuilder(SgTags.Items.GRADER_CATALYSTS_TIER_3).add(CraftingItems.GLITTERY_DUST.asItem());
        getBuilder(SgTags.Items.GRADER_CATALYSTS_TIER_4);
        getBuilder(SgTags.Items.GRADER_CATALYSTS_TIER_5);
        getBuilder(SgTags.Items.GRADER_CATALYSTS)
                .addTag(SgTags.Items.GRADER_CATALYSTS_TIER_1)
                .addTag(SgTags.Items.GRADER_CATALYSTS_TIER_2)
                .addTag(SgTags.Items.GRADER_CATALYSTS_TIER_3)
                .addTag(SgTags.Items.GRADER_CATALYSTS_TIER_4)
                .addTag(SgTags.Items.GRADER_CATALYSTS_TIER_5);

        getBuilder(SgTags.Items.STARLIGHT_CHARGER_CATALYSTS_TIER_1).addTag(SgTags.Items.DUSTS_BLAZE_GOLD);
        getBuilder(SgTags.Items.STARLIGHT_CHARGER_CATALYSTS_TIER_2).addTag(SgTags.Items.DUSTS_AZURE_SILVER);
        getBuilder(SgTags.Items.STARLIGHT_CHARGER_CATALYSTS_TIER_3).addTag(SgTags.Items.DUSTS_STARMETAL);
        getBuilder(SgTags.Items.STARLIGHT_CHARGER_CATALYSTS)
                .addTag(SgTags.Items.STARLIGHT_CHARGER_CATALYSTS_TIER_1)
                .addTag(SgTags.Items.STARLIGHT_CHARGER_CATALYSTS_TIER_2)
                .addTag(SgTags.Items.STARLIGHT_CHARGER_CATALYSTS_TIER_3);

        builder(SgTags.Items.REPAIR_KITS, SgItems.CRUDE_REPAIR_KIT);

        // Blueprints
        Multimap<ResourceLocation, AbstractBlueprintItem> blueprints = MultimapBuilder.linkedHashKeys().arrayListValues().build();
        ForgeRegistries.ITEMS.getValues().stream()
                .filter(item -> item instanceof AbstractBlueprintItem)
                .map(item -> (AbstractBlueprintItem) item)
                .sorted(Comparator.comparing(blueprint -> blueprint.getItemTag().location()))
                .forEach(item -> blueprints.put(item.getItemTag().location(), item));
        TagsProvider.TagAppender<Item> blueprintsBuilder = getBuilder(SgTags.Items.BLUEPRINTS);
        blueprints.keySet().forEach(tagId -> {
            TagKey<Item> tag = ItemTags.create(tagId);
            getBuilder(tag).add(blueprints.get(tagId).toArray(new Item[0]));
            blueprintsBuilder.addTag(tag);
        });

        // Curios
        SgItems.getItems(GearCurioItem.class).forEach(item ->
                builder(makeWrapper(Const.CURIOS, item.getSlot()), item));

        builder(makeWrapper(Const.CURIOS, "back"), SgItems.ELYTRA);
    }

    private TagKey<Item> makeWrapper(String namespace, String path) {
        return ItemTags.create(new ResourceLocation(namespace, path));
    }

    private void builder(TagKey<Item> tag, ItemLike... items) {
        getBuilder(tag).add(Arrays.stream(items).map(ItemLike::asItem).toArray(Item[]::new));
    }

    protected TagsProvider.TagAppender<Item> getBuilder(TagKey<Item> tag) {
        return tag(tag);
    }
}
