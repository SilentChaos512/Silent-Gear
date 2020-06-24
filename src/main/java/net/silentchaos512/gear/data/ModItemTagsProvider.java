package net.silentchaos512.gear.data;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;
import net.silentchaos512.gear.init.ModItems;
import net.silentchaos512.gear.init.ModTags;
import net.silentchaos512.gear.item.CraftingItems;
import net.silentchaos512.gear.item.blueprint.AbstractBlueprintItem;

import java.util.Arrays;
import java.util.Comparator;

public class ModItemTagsProvider extends ItemTagsProvider {
    public ModItemTagsProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    public String getName() {
        return "Silent Gear - Item Tags";
    }

    @Override
    protected void registerTags() {
        // Forge
        copy(ModTags.Blocks.ORES_CRIMSON_IRON, ModTags.Items.ORES_CRIMSON_IRON);
        copy(Tags.Blocks.ORES, Tags.Items.ORES);
        copy(ModTags.Blocks.STORAGE_BLOCKS_BLAZE_GOLD, ModTags.Items.STORAGE_BLOCKS_BLAZE_GOLD);
        copy(ModTags.Blocks.STORAGE_BLOCKS_CRIMSON_IRON, ModTags.Items.STORAGE_BLOCKS_CRIMSON_IRON);
        copy(ModTags.Blocks.STORAGE_BLOCKS_CRIMSON_STEEL, ModTags.Items.STORAGE_BLOCKS_CRIMSON_STEEL);
        copy(Tags.Blocks.STORAGE_BLOCKS, Tags.Items.STORAGE_BLOCKS);

        builder(ModTags.Items.DUSTS_BLAZE_GOLD, CraftingItems.BLAZE_GOLD_DUST);
        builder(ModTags.Items.DUSTS_CRIMSON_IRON, CraftingItems.CRIMSON_IRON_DUST);
        getBuilder(Tags.Items.DUSTS).add(
                ModTags.Items.DUSTS_BLAZE_GOLD,
                ModTags.Items.DUSTS_CRIMSON_IRON
        );

        builder(ModTags.Items.INGOTS_BLAZE_GOLD, CraftingItems.BLAZE_GOLD_INGOT);
        builder(ModTags.Items.INGOTS_CRIMSON_IRON, CraftingItems.CRIMSON_IRON_INGOT);
        builder(ModTags.Items.INGOTS_CRIMSON_STEEL, CraftingItems.CRIMSON_STEEL_INGOT);
        getBuilder(Tags.Items.INGOTS).add(
                ModTags.Items.INGOTS_BLAZE_GOLD,
                ModTags.Items.INGOTS_CRIMSON_IRON,
                ModTags.Items.INGOTS_CRIMSON_STEEL
        );

        builder(ModTags.Items.NUGGETS_BLAZE_GOLD, CraftingItems.BLAZE_GOLD_NUGGET);
        builder(ModTags.Items.NUGGETS_CRIMSON_IRON, CraftingItems.CRIMSON_IRON_NUGGET);
        builder(ModTags.Items.NUGGETS_CRIMSON_STEEL, CraftingItems.CRIMSON_STEEL_NUGGET);
        builder(ModTags.Items.NUGGETS_DIAMOND, CraftingItems.DIAMOND_SHARD);
        builder(ModTags.Items.NUGGETS_EMERALD, CraftingItems.EMERALD_SHARD);
        getBuilder(Tags.Items.NUGGETS).add(
                ModTags.Items.NUGGETS_BLAZE_GOLD,
                ModTags.Items.NUGGETS_CRIMSON_IRON,
                ModTags.Items.NUGGETS_CRIMSON_STEEL,
                ModTags.Items.NUGGETS_DIAMOND,
                ModTags.Items.NUGGETS_EMERALD
        );

        builder(ModTags.Items.RODS_IRON, CraftingItems.IRON_ROD);
        builder(ModTags.Items.RODS_NETHERWOOD, CraftingItems.NETHERWOOD_STICK);
        builder(ModTags.Items.RODS_ROUGH, CraftingItems.ROUGH_ROD);
        builder(ModTags.Items.RODS_STONE, CraftingItems.STONE_ROD);
        builder(Tags.Items.RODS_WOODEN, CraftingItems.NETHERWOOD_STICK);
        getBuilder(Tags.Items.RODS).add(
                ModTags.Items.RODS_IRON,
                ModTags.Items.RODS_NETHERWOOD,
                ModTags.Items.RODS_ROUGH,
                ModTags.Items.RODS_STONE
        );

        builder(ModTags.Items.PAPER, Items.PAPER);
        builder(ModTags.Items.PAPER_BLUEPRINT, CraftingItems.BLUEPRINT_PAPER);
        builder(ModTags.Items.TEMPLATE_BOARDS, CraftingItems.TEMPLATE_BOARD);

        builder(ModTags.Items.FRUITS, ModItems.NETHER_BANANA);
        builder(Tags.Items.SEEDS, ModItems.FLAXSEEDS);
        builder(Tags.Items.STRING, CraftingItems.FLAX_STRING, CraftingItems.SINEW_FIBER);

        builder(ModTags.Items.AXES, ModItems.AXE, ModItems.LUMBER_AXE, ModItems.MACHETE, ModItems.MATTOCK, ModItems.PAXEL);
        builder(ModTags.Items.BOOTS, ModItems.BOOTS);
        builder(ModTags.Items.BOWS, ModItems.BOW);
        builder(ModTags.Items.CHESTPLATES, ModItems.CHESTPLATE);
        builder(ModTags.Items.CROSSBOWS, ModItems.CROSSBOW);
        builder(ModTags.Items.HAMMERS, ModItems.HAMMER);
        builder(ModTags.Items.HELMETS, ModItems.HELMET);
        builder(ModTags.Items.HOES, ModItems.MATTOCK);
        builder(ModTags.Items.KNIVES, ModItems.DAGGER);
        builder(ModTags.Items.LEGGINGS, ModItems.LEGGINGS);
        builder(ModTags.Items.PICKAXES, ModItems.HAMMER, ModItems.PAXEL, ModItems.PICKAXE);
        builder(ModTags.Items.SHEARS, ModItems.SHEARS);
        builder(ModTags.Items.SHIELDS, ModItems.SHIELD);
        builder(ModTags.Items.SHOVELS, ModItems.EXCAVATOR, ModItems.MATTOCK, ModItems.PAXEL, ModItems.SHOVEL);
        builder(ModTags.Items.SICKLES, ModItems.SICKLE);
        builder(ModTags.Items.SWORDS, ModItems.DAGGER, ModItems.KATANA, ModItems.MACHETE, ModItems.SWORD);

        // Minecraft
        copy(BlockTags.LEAVES, ItemTags.LEAVES);
        copy(BlockTags.LOGS, ItemTags.LOGS);
        copy(BlockTags.PLANKS, ItemTags.PLANKS);
        copy(BlockTags.SAPLINGS, ItemTags.SAPLINGS);

        // Silent Gear
        getBuilder(ModTags.Items.GRADER_CATALYSTS_TIER_1).add(Tags.Items.DUSTS_GLOWSTONE);
        getBuilder(ModTags.Items.GRADER_CATALYSTS_TIER_2).add(CraftingItems.BLAZING_DUST.asItem());
        getBuilder(ModTags.Items.GRADER_CATALYSTS_TIER_3).add(CraftingItems.GLITTERY_DUST.asItem());
        getBuilder(ModTags.Items.GRADER_CATALYSTS_TIER_4);
        getBuilder(ModTags.Items.GRADER_CATALYSTS_TIER_5);
        getBuilder(ModTags.Items.GRADER_CATALYSTS).add(
                ModTags.Items.GRADER_CATALYSTS_TIER_1,
                ModTags.Items.GRADER_CATALYSTS_TIER_2,
                ModTags.Items.GRADER_CATALYSTS_TIER_3,
                ModTags.Items.GRADER_CATALYSTS_TIER_4,
                ModTags.Items.GRADER_CATALYSTS_TIER_5
        );
        // Blueprints
        Multimap<ResourceLocation, AbstractBlueprintItem> blueprints = MultimapBuilder.linkedHashKeys().arrayListValues().build();
        ForgeRegistries.ITEMS.getValues().stream()
                .filter(item -> item instanceof AbstractBlueprintItem)
                .map(item -> (AbstractBlueprintItem) item)
                .sorted(Comparator.comparing(AbstractBlueprintItem::getItemTag))
                .forEach(item -> blueprints.put(item.getItemTag(), item));
        Tag.Builder<Item> blueprintsBuilder = getBuilder(ModTags.Items.BLUEPRINTS);
        blueprints.keySet().forEach(tagId -> {
            ItemTags.Wrapper tag = new ItemTags.Wrapper(tagId);
            getBuilder(tag).add(blueprints.get(tagId).toArray(new Item[0]));
            blueprintsBuilder.add(tag);
        });
    }

    private void builder(Tag<Item> tag, IItemProvider... items) {
        getBuilder(tag).add(Arrays.stream(items).map(IItemProvider::asItem).toArray(Item[]::new));
    }
}
