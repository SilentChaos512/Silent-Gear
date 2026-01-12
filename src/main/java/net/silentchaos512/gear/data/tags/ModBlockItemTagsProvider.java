package net.silentchaos512.gear.data.tags;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.silentchaos512.gear.block.FluffyBlock;
import net.silentchaos512.gear.setup.SgBlocks;
import net.silentchaos512.gear.setup.SgTags;
import net.silentchaos512.lib.data.tag.LibBlockItemTagsProvider;

public abstract class ModBlockItemTagsProvider extends LibBlockItemTagsProvider {
    @Override
    public void run() {
        // Common

        tag(SgTags.Blocks.ORES_BORT, SgTags.Items.ORES_BORT)
                .add(SgBlocks.BORT_ORE.get())
                .add(SgBlocks.DEEPSLATE_BORT_ORE.get());
        tag(SgTags.Blocks.ORES_CRIMSON_IRON, SgTags.Items.ORES_CRIMSON_IRON)
                .add(SgBlocks.CRIMSON_IRON_ORE.get())
                .add(SgBlocks.BLACKSTONE_CRIMSON_IRON_ORE.get());
        tag(SgTags.Blocks.ORES_AZURE_SILVER, SgTags.Items.ORES_AZURE_SILVER)
                .add(SgBlocks.AZURE_SILVER_ORE.get());
        tag(Tags.Blocks.ORES, Tags.Items.ORES)
                .addTag(SgTags.Blocks.ORES_BORT)
                .addTag(SgTags.Blocks.ORES_CRIMSON_IRON)
                .addTag(SgTags.Blocks.ORES_AZURE_SILVER);

        tag(SgTags.Blocks.STORAGE_BLOCKS_NETHERWOOD_CHARCOAL, SgTags.Items.STORAGE_BLOCKS_NETHERWOOD_CHARCOAL)
                .add(SgBlocks.NETHERWOOD_CHARCOAL_BLOCK.get());
        tag(SgTags.Blocks.STORAGE_BLOCKS_BORT, SgTags.Items.STORAGE_BLOCKS_BORT)
                .add(SgBlocks.BORT_BLOCK.get());
        tag(SgTags.Blocks.STORAGE_BLOCKS_BLAZE_GOLD, SgTags.Items.STORAGE_BLOCKS_BLAZE_GOLD)
                .add(SgBlocks.BLAZE_GOLD_BLOCK.get());
        tag(SgTags.Blocks.STORAGE_BLOCKS_CRIMSON_IRON, SgTags.Items.STORAGE_BLOCKS_CRIMSON_IRON)
                .add(SgBlocks.CRIMSON_IRON_BLOCK.get());
        tag(SgTags.Blocks.STORAGE_BLOCKS_CRIMSON_STEEL, SgTags.Items.STORAGE_BLOCKS_CRIMSON_STEEL)
                .add(SgBlocks.CRIMSON_STEEL_BLOCK.get());
        tag(SgTags.Blocks.STORAGE_BLOCKS_AZURE_SILVER, SgTags.Items.STORAGE_BLOCKS_AZURE_SILVER)
                .add(SgBlocks.AZURE_SILVER_BLOCK.get());
        tag(SgTags.Blocks.STORAGE_BLOCKS_AZURE_ELECTRUM, SgTags.Items.STORAGE_BLOCKS_AZURE_ELECTRUM)
                .add(SgBlocks.AZURE_ELECTRUM_BLOCK.get());
        tag(SgTags.Blocks.STORAGE_BLOCKS_TYRIAN_STEEL, SgTags.Items.STORAGE_BLOCKS_TYRIAN_STEEL)
                .add(SgBlocks.TYRIAN_STEEL_BLOCK.get());
        tag(Tags.Blocks.STORAGE_BLOCKS, Tags.Items.STORAGE_BLOCKS)
                .addTag(SgTags.Blocks.STORAGE_BLOCKS_NETHERWOOD_CHARCOAL)
                .addTag(SgTags.Blocks.STORAGE_BLOCKS_BORT)
                .addTag(SgTags.Blocks.STORAGE_BLOCKS_BLAZE_GOLD)
                .addTag(SgTags.Blocks.STORAGE_BLOCKS_CRIMSON_IRON)
                .addTag(SgTags.Blocks.STORAGE_BLOCKS_CRIMSON_STEEL)
                .addTag(SgTags.Blocks.STORAGE_BLOCKS_AZURE_SILVER)
                .addTag(SgTags.Blocks.STORAGE_BLOCKS_AZURE_ELECTRUM)
                .addTag(SgTags.Blocks.STORAGE_BLOCKS_TYRIAN_STEEL);

        tag(BlockTags.LEAVES, ItemTags.LEAVES).add(SgBlocks.NETHERWOOD_LEAVES.get());
        tag(BlockTags.LOGS, ItemTags.LOGS).addTag(SgTags.Blocks.NETHERWOOD_LOGS);
        tag(BlockTags.PLANKS, ItemTags.PLANKS).add(SgBlocks.NETHERWOOD_PLANKS.get());
        tag(BlockTags.SAPLINGS, ItemTags.SAPLINGS).add(SgBlocks.NETHERWOOD_SAPLING.get());
        tag(BlockTags.WOODEN_DOORS, ItemTags.WOODEN_DOORS).add(SgBlocks.NETHERWOOD_DOOR.get());
        tag(BlockTags.WOODEN_FENCES, ItemTags.WOODEN_FENCES).add(SgBlocks.NETHERWOOD_FENCE.get());
        tag(BlockTags.FENCE_GATES, ItemTags.FENCE_GATES).add(SgBlocks.NETHERWOOD_FENCE_GATE.get());
        tag(Tags.Blocks.FENCE_GATES_WOODEN, Tags.Items.FENCE_GATES_WOODEN).add(SgBlocks.NETHERWOOD_FENCE_GATE.get());
        tag(BlockTags.WOODEN_SLABS, ItemTags.WOODEN_SLABS).add(SgBlocks.NETHERWOOD_SLAB.get());
        tag(BlockTags.WOODEN_STAIRS, ItemTags.WOODEN_STAIRS).add(SgBlocks.NETHERWOOD_STAIRS.get());
        tag(BlockTags.WOODEN_TRAPDOORS, ItemTags.WOODEN_TRAPDOORS).add(SgBlocks.NETHERWOOD_TRAPDOOR.get());

        // Silent Gear

        tag(SgTags.Blocks.FLUFFY_BLOCKS, SgTags.Items.FLUFFY_BLOCKS)
                .add(SgBlocks.getBlocks(FluffyBlock.class).toArray(new Block[0]));
        tag(SgTags.Blocks.NETHERWOOD_LOGS, SgTags.Items.NETHERWOOD_LOGS)
                .add(SgBlocks.NETHERWOOD_LOG.get())
                .add(SgBlocks.STRIPPED_NETHERWOOD_LOG.get())
                .add(SgBlocks.NETHERWOOD_WOOD.get())
                .add(SgBlocks.STRIPPED_NETHERWOOD_WOOD.get());
    }
}
