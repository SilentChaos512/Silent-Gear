package net.silentchaos512.gear.data;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.TagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeBlockTagsProvider;
import net.silentchaos512.gear.block.FluffyBlock;
import net.silentchaos512.gear.init.ModBlocks;
import net.silentchaos512.gear.init.ModTags;
import net.silentchaos512.gear.init.Registration;
import net.silentchaos512.lib.block.IBlockProvider;

import java.util.Arrays;

public class ModBlockTagsProvider extends ForgeBlockTagsProvider {
    public ModBlockTagsProvider(DataGenerator generatorIn, ExistingFileHelper existingFileHelper) {
        super(generatorIn, existingFileHelper);
    }

    @Override
    public String getName() {
        return "Silent Gear - Block Tags";
    }

    @Override
    public void addTags() {
        // Silent Gear
        getBuilder(ModTags.Blocks.FLUFFY_BLOCKS)
                .add(Registration.getBlocks(FluffyBlock.class).toArray(new Block[0]));
        getBuilder(ModTags.Blocks.NETHERWOOD_LOGS)
                .add(ModBlocks.NETHERWOOD_LOG.get())
                .add(ModBlocks.STRIPPED_NETHERWOOD_LOG.get())
                .add(ModBlocks.NETHERWOOD_WOOD.get())
                .add(ModBlocks.STRIPPED_NETHERWOOD_WOOD.get());
        getBuilder(ModTags.Blocks.NETHERWOOD_SOIL)
                .addTag(Tags.Blocks.NETHERRACK)
                .addTag(Tags.Blocks.DIRT)
                .add(Blocks.FARMLAND);
        getBuilder(ModTags.Blocks.PROSPECTOR_HAMMER_TARGETS)
                .add(Blocks.ANCIENT_DEBRIS)
                .addTag(Tags.Blocks.ORES);

        // Forge
        builder(ModTags.Blocks.ORES_BORT, ModBlocks.BORT_ORE, ModBlocks.DEEPSLATE_BORT_ORE);
        builder(ModTags.Blocks.ORES_CRIMSON_IRON, ModBlocks.CRIMSON_IRON_ORE);
        builder(ModTags.Blocks.ORES_AZURE_SILVER, ModBlocks.AZURE_SILVER_ORE);
        getBuilder(Tags.Blocks.ORES)
                .addTag(ModTags.Blocks.ORES_BORT)
                .addTag(ModTags.Blocks.ORES_CRIMSON_IRON)
                .addTag(ModTags.Blocks.ORES_AZURE_SILVER);

        builder(ModTags.Blocks.STORAGE_BLOCKS_BORT, ModBlocks.BORT_BLOCK);
        builder(ModTags.Blocks.STORAGE_BLOCKS_CRIMSON_IRON, ModBlocks.CRIMSON_IRON_BLOCK);
        builder(ModTags.Blocks.STORAGE_BLOCKS_CRIMSON_STEEL, ModBlocks.CRIMSON_STEEL_BLOCK);
        builder(ModTags.Blocks.STORAGE_BLOCKS_BLAZE_GOLD, ModBlocks.BLAZE_GOLD_BLOCK);
        builder(ModTags.Blocks.STORAGE_BLOCKS_AZURE_SILVER, ModBlocks.AZURE_SILVER_BLOCK);
        builder(ModTags.Blocks.STORAGE_BLOCKS_AZURE_ELECTRUM, ModBlocks.AZURE_ELECTRUM_BLOCK);
        builder(ModTags.Blocks.STORAGE_BLOCKS_TYRIAN_STEEL, ModBlocks.TYRIAN_STEEL_BLOCK);
        getBuilder(Tags.Blocks.STORAGE_BLOCKS)
                .addTag(ModTags.Blocks.STORAGE_BLOCKS_BORT)
                .addTag(ModTags.Blocks.STORAGE_BLOCKS_BLAZE_GOLD)
                .addTag(ModTags.Blocks.STORAGE_BLOCKS_CRIMSON_IRON)
                .addTag(ModTags.Blocks.STORAGE_BLOCKS_CRIMSON_STEEL)
                .addTag(ModTags.Blocks.STORAGE_BLOCKS_AZURE_SILVER)
                .addTag(ModTags.Blocks.STORAGE_BLOCKS_AZURE_ELECTRUM)
                .addTag(ModTags.Blocks.STORAGE_BLOCKS_TYRIAN_STEEL);

        // Minecraft
        builder(BlockTags.CROPS, ModBlocks.FLAX_PLANT, ModBlocks.FLUFFY_PLANT);
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
        return tag(tag);
    }
}
