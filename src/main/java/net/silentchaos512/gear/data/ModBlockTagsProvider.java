package net.silentchaos512.gear.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.TagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeBlockTagsProvider;
import net.silentchaos512.gear.init.ModBlocks;
import net.silentchaos512.gear.init.ModTags;
import net.silentchaos512.lib.block.IBlockProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;

public class ModBlockTagsProvider extends ForgeBlockTagsProvider {
    public ModBlockTagsProvider(DataGenerator generatorIn, ExistingFileHelper existingFileHelper) {
        super(generatorIn, existingFileHelper);
    }

    @Override
    public String getName() {
        return "Silent Gear - Block Tags";
    }

    @Override
    public void registerTags() {
        // Silent Gear
        getBuilder(ModTags.Blocks.NETHERWOOD_LOGS)
                .add(ModBlocks.NETHERWOOD_LOG.get())
                .add(ModBlocks.STRIPPED_NETHERWOOD_LOG.get());
        getBuilder(ModTags.Blocks.NETHERWOOD_SOIL)
                .addTag(Tags.Blocks.NETHERRACK)
                .addTag(Tags.Blocks.DIRT)
                .add(Blocks.FARMLAND);
        getBuilder(ModTags.Blocks.PROSPECTOR_HAMMER_TARGETS)
                .add(Blocks.ANCIENT_DEBRIS)
                .addTag(Tags.Blocks.ORES);

        // Forge
        builder(ModTags.Blocks.ORES_CRIMSON_IRON, ModBlocks.CRIMSON_IRON_ORE);
        builder(ModTags.Blocks.ORES_AZURE_SILVER, ModBlocks.AZURE_SILVER_ORE);
        getBuilder(Tags.Blocks.ORES)
                .addTag(ModTags.Blocks.ORES_CRIMSON_IRON)
                .addTag(ModTags.Blocks.ORES_AZURE_SILVER);
        builder(ModTags.Blocks.STORAGE_BLOCKS_CRIMSON_IRON, ModBlocks.CRIMSON_IRON_BLOCK);
        builder(ModTags.Blocks.STORAGE_BLOCKS_CRIMSON_STEEL, ModBlocks.CRIMSON_STEEL_BLOCK);
        builder(ModTags.Blocks.STORAGE_BLOCKS_BLAZE_GOLD, ModBlocks.BLAZE_GOLD_BLOCK);
        builder(ModTags.Blocks.STORAGE_BLOCKS_AZURE_SILVER, ModBlocks.AZURE_SILVER_BLOCK);
        builder(ModTags.Blocks.STORAGE_BLOCKS_AZURE_ELECTRUM, ModBlocks.AZURE_ELECTRUM_BLOCK);
        getBuilder(Tags.Blocks.STORAGE_BLOCKS)
                .addTag(ModTags.Blocks.STORAGE_BLOCKS_BLAZE_GOLD)
                .addTag(ModTags.Blocks.STORAGE_BLOCKS_CRIMSON_IRON)
                .addTag(ModTags.Blocks.STORAGE_BLOCKS_CRIMSON_STEEL)
                .addTag(ModTags.Blocks.STORAGE_BLOCKS_AZURE_SILVER)
                .addTag(ModTags.Blocks.STORAGE_BLOCKS_AZURE_ELECTRUM);

        // Minecraft
        builder(BlockTags.LEAVES, ModBlocks.NETHERWOOD_LEAVES);
        getBuilder(BlockTags.LOGS).addTag(ModTags.Blocks.NETHERWOOD_LOGS);
        builder(BlockTags.PLANKS, ModBlocks.NETHERWOOD_PLANKS);
        builder(BlockTags.SAPLINGS, ModBlocks.NETHERWOOD_SAPLING);
        builder(BlockTags.WOODEN_DOORS, ModBlocks.NETHERWOOD_DOOR);
        builder(BlockTags.WOODEN_FENCES, ModBlocks.NETHERWOOD_FENCE);
        builder(BlockTags.WOODEN_SLABS, ModBlocks.NETHERWOOD_SLAB);
        builder(BlockTags.WOODEN_STAIRS, ModBlocks.NETHERWOOD_STAIRS);
        builder(BlockTags.WOODEN_TRAPDOORS, ModBlocks.NETHERWOOD_TRAPDOOR);
    }

    private void builder(ITag.INamedTag<Block> tag, IBlockProvider... items) {
        getBuilder(tag).add(Arrays.stream(items).map(IBlockProvider::asBlock).toArray(Block[]::new));
    }

    protected TagsProvider.Builder<Block> getBuilder(ITag.INamedTag<Block> tag) {
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
