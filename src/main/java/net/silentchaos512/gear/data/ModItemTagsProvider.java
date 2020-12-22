package net.silentchaos512.gear.data;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.TagsProvider;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeItemTagsProvider;
import net.minecraftforge.registries.ForgeRegistries;
import net.silentchaos512.gear.init.ModItems;
import net.silentchaos512.gear.init.ModTags;
import net.silentchaos512.gear.init.Registration;
import net.silentchaos512.gear.item.CraftingItems;
import net.silentchaos512.gear.item.blueprint.AbstractBlueprintItem;
import net.silentchaos512.gear.item.gear.CoreCurio;
import net.silentchaos512.gear.util.Const;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

public class ModItemTagsProvider extends ForgeItemTagsProvider {
    public ModItemTagsProvider(DataGenerator generatorIn, BlockTagsProvider blocks, ExistingFileHelper existingFileHelper) {
        super(generatorIn, blocks, existingFileHelper);
    }

    @Override
    public String getName() {
        return "Silent Gear - Item Tags";
    }

    @SuppressWarnings("OverlyLongMethod")
    @Override
    public void registerTags() {
        // Forge
        copy(ModTags.Blocks.ORES_CRIMSON_IRON, ModTags.Items.ORES_CRIMSON_IRON);
        copy(ModTags.Blocks.ORES_AZURE_SILVER, ModTags.Items.ORES_AZURE_SILVER);
        copy(Tags.Blocks.ORES, Tags.Items.ORES);

        copy(ModTags.Blocks.STORAGE_BLOCKS_BLAZE_GOLD, ModTags.Items.STORAGE_BLOCKS_BLAZE_GOLD);
        copy(ModTags.Blocks.STORAGE_BLOCKS_CRIMSON_IRON, ModTags.Items.STORAGE_BLOCKS_CRIMSON_IRON);
        copy(ModTags.Blocks.STORAGE_BLOCKS_CRIMSON_STEEL, ModTags.Items.STORAGE_BLOCKS_CRIMSON_STEEL);
        copy(ModTags.Blocks.STORAGE_BLOCKS_AZURE_SILVER, ModTags.Items.STORAGE_BLOCKS_AZURE_SILVER);
        copy(ModTags.Blocks.STORAGE_BLOCKS_AZURE_ELECTRUM, ModTags.Items.STORAGE_BLOCKS_AZURE_ELECTRUM);
        copy(Tags.Blocks.STORAGE_BLOCKS, Tags.Items.STORAGE_BLOCKS);

        builder(ModTags.Items.CHUNKS_CRIMSON_IRON, CraftingItems.CRIMSON_IRON_CHUNKS);
        builder(ModTags.Items.CHUNKS_AZURE_SILVER, CraftingItems.AZURE_SILVER_CHUNKS);

        builder(ModTags.Items.DUSTS_BLAZE_GOLD, CraftingItems.BLAZE_GOLD_DUST);
        builder(ModTags.Items.DUSTS_CRIMSON_IRON, CraftingItems.CRIMSON_IRON_DUST);
        builder(ModTags.Items.DUSTS_CRIMSON_STEEL, CraftingItems.CRIMSON_STEEL_DUST);
        builder(ModTags.Items.DUSTS_AZURE_SILVER, CraftingItems.AZURE_SILVER_DUST);
        builder(ModTags.Items.DUSTS_AZURE_ELECTRUM, CraftingItems.AZURE_ELECTRUM_DUST);
        getBuilder(Tags.Items.DUSTS)
                .addTag(ModTags.Items.DUSTS_BLAZE_GOLD)
                .addTag(ModTags.Items.DUSTS_CRIMSON_IRON)
                .addTag(ModTags.Items.DUSTS_CRIMSON_STEEL)
                .addTag(ModTags.Items.DUSTS_AZURE_SILVER)
                .addTag(ModTags.Items.DUSTS_AZURE_ELECTRUM);

        builder(ModTags.Items.INGOTS_BLAZE_GOLD, CraftingItems.BLAZE_GOLD_INGOT);
        builder(ModTags.Items.INGOTS_CRIMSON_IRON, CraftingItems.CRIMSON_IRON_INGOT);
        builder(ModTags.Items.INGOTS_CRIMSON_STEEL, CraftingItems.CRIMSON_STEEL_INGOT);
        builder(ModTags.Items.INGOTS_AZURE_SILVER, CraftingItems.AZURE_SILVER_INGOT);
        builder(ModTags.Items.INGOTS_AZURE_ELECTRUM, CraftingItems.AZURE_ELECTRUM_INGOT);
        getBuilder(Tags.Items.INGOTS)
                .addTag(ModTags.Items.INGOTS_BLAZE_GOLD)
                .addTag(ModTags.Items.INGOTS_CRIMSON_IRON)
                .addTag(ModTags.Items.INGOTS_CRIMSON_STEEL)
                .addTag(ModTags.Items.INGOTS_AZURE_SILVER)
                .addTag(ModTags.Items.INGOTS_AZURE_ELECTRUM);

        builder(ModTags.Items.NUGGETS_BLAZE_GOLD, CraftingItems.BLAZE_GOLD_NUGGET);
        builder(ModTags.Items.NUGGETS_CRIMSON_IRON, CraftingItems.CRIMSON_IRON_NUGGET);
        builder(ModTags.Items.NUGGETS_CRIMSON_STEEL, CraftingItems.CRIMSON_STEEL_NUGGET);
        builder(ModTags.Items.NUGGETS_AZURE_SILVER, CraftingItems.AZURE_SILVER_NUGGET);
        builder(ModTags.Items.NUGGETS_AZURE_ELECTRUM, CraftingItems.AZURE_ELECTRUM_NUGGET);
        builder(ModTags.Items.NUGGETS_DIAMOND, CraftingItems.DIAMOND_SHARD);
        builder(ModTags.Items.NUGGETS_EMERALD, CraftingItems.EMERALD_SHARD);
        getBuilder(Tags.Items.NUGGETS)
                .addTag(ModTags.Items.NUGGETS_BLAZE_GOLD)
                .addTag(ModTags.Items.NUGGETS_CRIMSON_IRON)
                .addTag(ModTags.Items.NUGGETS_CRIMSON_STEEL)
                .addTag(ModTags.Items.NUGGETS_AZURE_SILVER)
                .addTag(ModTags.Items.NUGGETS_AZURE_ELECTRUM)
                .addTag(ModTags.Items.NUGGETS_DIAMOND)
                .addTag(ModTags.Items.NUGGETS_EMERALD);

        builder(ModTags.Items.RODS_IRON, CraftingItems.IRON_ROD);
        builder(ModTags.Items.RODS_NETHERWOOD, CraftingItems.NETHERWOOD_STICK);
        builder(ModTags.Items.RODS_ROUGH, CraftingItems.ROUGH_ROD);
        builder(ModTags.Items.RODS_STONE, CraftingItems.STONE_ROD);
        builder(Tags.Items.RODS_WOODEN, CraftingItems.NETHERWOOD_STICK);
        getBuilder(Tags.Items.RODS)
                .addTag(ModTags.Items.RODS_IRON)
                .addTag(ModTags.Items.RODS_NETHERWOOD)
                .addTag(ModTags.Items.RODS_ROUGH)
                .addTag(ModTags.Items.RODS_STONE);

        builder(ModTags.Items.PAPER, Items.PAPER);
        builder(ModTags.Items.BLUEPRINT_PAPER, CraftingItems.BLUEPRINT_PAPER);
        builder(ModTags.Items.TEMPLATE_BOARDS, CraftingItems.TEMPLATE_BOARD);

        builder(ModTags.Items.FRUITS, ModItems.NETHER_BANANA);
        builder(Tags.Items.SEEDS, ModItems.FLAX_SEEDS);
        builder(Tags.Items.STRING, CraftingItems.FLAX_STRING, CraftingItems.SINEW_FIBER);

        builder(ModTags.Items.AXES, ModItems.AXE, ModItems.SAW, ModItems.MACHETE, ModItems.MATTOCK, ModItems.PAXEL);
        builder(ModTags.Items.BOOTS, ModItems.BOOTS);
        builder(ModTags.Items.BOWS, ModItems.BOW);
        builder(ModTags.Items.CHESTPLATES, ModItems.CHESTPLATE);
        builder(ModTags.Items.CROSSBOWS, ModItems.CROSSBOW);
        builder(ModTags.Items.HAMMERS, ModItems.HAMMER, ModItems.PROSPECTOR_HAMMER);
        builder(ModTags.Items.HELMETS, ModItems.HELMET);
        builder(ModTags.Items.HOES, ModItems.MATTOCK);
        builder(ModTags.Items.KNIVES, ModItems.KNIFE, ModItems.DAGGER);
        builder(ModTags.Items.LEGGINGS, ModItems.LEGGINGS);
        builder(ModTags.Items.PICKAXES, ModItems.HAMMER, ModItems.PAXEL, ModItems.PICKAXE);
        builder(ModTags.Items.SAWS, ModItems.SAW);
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
        copy(BlockTags.WOODEN_DOORS, ItemTags.WOODEN_DOORS);
        copy(BlockTags.WOODEN_FENCES, ItemTags.WOODEN_FENCES);
        copy(BlockTags.WOODEN_SLABS, ItemTags.WOODEN_SLABS);
        copy(BlockTags.WOODEN_STAIRS, ItemTags.WOODEN_STAIRS);
        copy(BlockTags.WOODEN_TRAPDOORS, ItemTags.WOODEN_TRAPDOORS);
        getBuilder(ItemTags.ARROWS).add(ModItems.ARROW.get());

        // Silent Gear
        copy(ModTags.Blocks.NETHERWOOD_LOGS, ModTags.Items.NETHERWOOD_LOGS);
        getBuilder(ModTags.Items.GRADER_CATALYSTS_TIER_1)
                .add(CraftingItems.GLOWING_DUST.asItem())
                .addTag(Tags.Items.DUSTS_GLOWSTONE);
        getBuilder(ModTags.Items.GRADER_CATALYSTS_TIER_2).add(CraftingItems.BLAZING_DUST.asItem());
        getBuilder(ModTags.Items.GRADER_CATALYSTS_TIER_3).add(CraftingItems.GLITTERY_DUST.asItem());
        getBuilder(ModTags.Items.GRADER_CATALYSTS_TIER_4);
        getBuilder(ModTags.Items.GRADER_CATALYSTS_TIER_5);
        getBuilder(ModTags.Items.GRADER_CATALYSTS)
                .addTag(ModTags.Items.GRADER_CATALYSTS_TIER_1)
                .addTag(ModTags.Items.GRADER_CATALYSTS_TIER_2)
                .addTag(ModTags.Items.GRADER_CATALYSTS_TIER_3)
                .addTag(ModTags.Items.GRADER_CATALYSTS_TIER_4)
                .addTag(ModTags.Items.GRADER_CATALYSTS_TIER_5);
        builder(ModTags.Items.REPAIR_KITS, ModItems.CRUDE_REPAIR_KIT);
        // Blueprints
        Multimap<ResourceLocation, AbstractBlueprintItem> blueprints = MultimapBuilder.linkedHashKeys().arrayListValues().build();
        ForgeRegistries.ITEMS.getValues().stream()
                .filter(item -> item instanceof AbstractBlueprintItem)
                .map(item -> (AbstractBlueprintItem) item)
                .sorted(Comparator.comparing(blueprint -> blueprint.getItemTag().getName()))
                .forEach(item -> blueprints.put(item.getItemTag().getName(), item));
        TagsProvider.Builder<Item> blueprintsBuilder = getBuilder(ModTags.Items.BLUEPRINTS);
        blueprints.keySet().forEach(tagId -> {
            ITag.INamedTag<Item> tag = ItemTags.makeWrapperTag(tagId.toString());
            getBuilder(tag).add(blueprints.get(tagId).toArray(new Item[0]));
            blueprintsBuilder.addTag(tag);
        });

        // Curios
        Registration.getItems(CoreCurio.class).forEach(item ->
                builder(makeWrapper(Const.CURIOS, item.getSlot()), item));

        builder(makeWrapper(Const.CURIOS, "back"), ModItems.ELYTRA);
    }

    private ITag.INamedTag<Item> makeWrapper(String namespace, String path) {
        return ItemTags.makeWrapperTag(new ResourceLocation(namespace, path).toString());
    }

    private void builder(ITag.INamedTag<Item> tag, IItemProvider... items) {
        getBuilder(tag).add(Arrays.stream(items).map(IItemProvider::asItem).toArray(Item[]::new));
    }

    protected TagsProvider.Builder<Item> getBuilder(ITag.INamedTag<Item> tag) {
        return getOrCreateBuilder(tag);
    }

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();

    @Override
    public void act(DirectoryCache cache) {
        // Temp fix that removes the broken safety check
        this.tagToBuilder.clear();
        this.registerTags();
        this.tagToBuilder.forEach((p_240524_4_, p_240524_5_) -> {
            JsonObject jsonobject = p_240524_5_.serialize();
            Path path = this.makePath(p_240524_4_);
            if (path == null)
                return; //Forge: Allow running this data provider without writing it. Recipe provider needs valid tags.

            try {
                String s = GSON.toJson((JsonElement) jsonobject);
                String s1 = HASH_FUNCTION.hashUnencodedChars(s).toString();
                if (!Objects.equals(cache.getPreviousHash(path), s1) || !Files.exists(path)) {
                    Files.createDirectories(path.getParent());

                    try (BufferedWriter bufferedwriter = Files.newBufferedWriter(path)) {
                        bufferedwriter.write(s);
                    }
                }

                cache.recordHash(path, s1);
            } catch (IOException ioexception) {
                LOGGER.error("Couldn't save tags to {}", path, ioexception);
            }

        });
    }
}
