package net.silentchaos512.gear.data;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.TagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraftforge.common.Tags;
import net.silentchaos512.gear.init.ModBlocks;
import net.silentchaos512.gear.init.ModTags;
import net.silentchaos512.lib.block.IBlockProvider;

import java.util.Arrays;

public class ModBlockTagsProvider extends BlockTagsProvider {
    public ModBlockTagsProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    public String getName() {
        return "Silent Gear - Block Tags";
    }

    @Override
    protected void registerTags() {
        // Forge
        builder(ModTags.Blocks.ORES_CRIMSON_IRON, ModBlocks.CRIMSON_IRON_ORE);
        getBuilder(Tags.Blocks.ORES).func_240531_a_(
                ModTags.Blocks.ORES_CRIMSON_IRON
        );
        builder(ModTags.Blocks.STORAGE_BLOCKS_CRIMSON_IRON, ModBlocks.CRIMSON_IRON_BLOCK);
        builder(ModTags.Blocks.STORAGE_BLOCKS_CRIMSON_STEEL, ModBlocks.CRIMSON_STEEL_BLOCK);
        builder(ModTags.Blocks.STORAGE_BLOCKS_BLAZE_GOLD, ModBlocks.BLAZE_GOLD_BLOCK);
        getBuilder(Tags.Blocks.STORAGE_BLOCKS)
                .func_240531_a_(ModTags.Blocks.STORAGE_BLOCKS_BLAZE_GOLD)
                .func_240531_a_(ModTags.Blocks.STORAGE_BLOCKS_CRIMSON_IRON)
                .func_240531_a_(ModTags.Blocks.STORAGE_BLOCKS_CRIMSON_STEEL);

        // Minecraft
        builder(BlockTags.LEAVES, ModBlocks.NETHERWOOD_LEAVES);
        builder(BlockTags.LOGS, ModBlocks.NETHERWOOD_LOG);
        builder(BlockTags.PLANKS, ModBlocks.NETHERWOOD_PLANKS);
        builder(BlockTags.SAPLINGS, ModBlocks.NETHERWOOD_SAPLING);
        builder(BlockTags.WOODEN_DOORS, ModBlocks.NETHERWOOD_DOOR);
        builder(BlockTags.WOODEN_FENCES, ModBlocks.NETHERWOOD_FENCE);
        builder(BlockTags.WOODEN_SLABS, ModBlocks.NETHERWOOD_SLAB);
        builder(BlockTags.WOODEN_STAIRS, ModBlocks.NETHERWOOD_STAIRS);
        builder(BlockTags.WOODEN_TRAPDOORS, ModBlocks.NETHERWOOD_TRAPDOOR);

        // Silent Gear
        getBuilder(ModTags.Blocks.NETHERWOOD_SOIL)
                .func_240531_a_(Tags.Blocks.NETHERRACK)
                .func_240531_a_(Tags.Blocks.DIRT)
                .func_240532_a_(Blocks.FARMLAND);
    }

    private void builder(ITag.INamedTag<Block> tag, IBlockProvider... items) {
        getBuilder(tag).func_240534_a_(Arrays.stream(items).map(IBlockProvider::asBlock).toArray(Block[]::new));
    }

    protected TagsProvider.Builder<Block> getBuilder(ITag.INamedTag<Block> p_240522_1_) {
        return func_240522_a_(p_240522_1_);
    }
}
