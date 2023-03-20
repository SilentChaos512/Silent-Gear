package net.silentchaos512.gear.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.block.FluffyBlock;
import net.silentchaos512.gear.init.SgBlocks;
import net.silentchaos512.gear.init.SgTags;
import net.silentchaos512.lib.block.IBlockProvider;

import java.util.Arrays;

public class ModBlockTagsProvider extends BlockTagsProvider {
    public ModBlockTagsProvider(GatherDataEvent event) {
        super(event.getGenerator().getPackOutput(), event.getLookupProvider(), SilentGear.MOD_ID, event.getExistingFileHelper());
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        // Harvesting
        tag(Tags.Blocks.NEEDS_NETHERITE_TOOL)
                .addTag(SgTags.Blocks.ORES_AZURE_SILVER);
        tag(BlockTags.NEEDS_IRON_TOOL)
                .addTag(SgTags.Blocks.ORES_BORT)
                .addTag(SgTags.Blocks.ORES_CRIMSON_IRON);
        tag(BlockTags.NEEDS_STONE_TOOL)
                .add(SgBlocks.RAW_AZURE_SILVER_BLOCK.get())
                .add(SgBlocks.RAW_CRIMSON_IRON_BLOCK.get());

        tag(BlockTags.MINEABLE_WITH_AXE)
                .addTag(SgTags.Blocks.NETHERWOOD_LOGS)
                .add(SgBlocks.GEAR_SMITHING_TABLE.get())
                .add(SgBlocks.NETHERWOOD_PLANKS.get())
                .add(SgBlocks.NETHERWOOD_SLAB.get())
                .add(SgBlocks.NETHERWOOD_STAIRS.get())
                .add(SgBlocks.NETHERWOOD_FENCE.get())
                .add(SgBlocks.NETHERWOOD_FENCE_GATE.get())
                .add(SgBlocks.NETHERWOOD_DOOR.get())
                .add(SgBlocks.NETHERWOOD_TRAPDOOR.get());
        tag(BlockTags.MINEABLE_WITH_HOE)
                .add(SgBlocks.NETHERWOOD_LEAVES.get());
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .addTag(SgTags.Blocks.ORES_BORT)
                .addTag(SgTags.Blocks.ORES_CRIMSON_IRON)
                .addTag(SgTags.Blocks.ORES_AZURE_SILVER)
                .add(SgBlocks.RAW_CRIMSON_IRON_BLOCK.get())
                .add(SgBlocks.RAW_AZURE_SILVER_BLOCK.get())
                .addTag(SgTags.Blocks.STORAGE_BLOCKS_AZURE_SILVER)
                .addTag(SgTags.Blocks.STORAGE_BLOCKS_AZURE_ELECTRUM)
                .addTag(SgTags.Blocks.STORAGE_BLOCKS_BORT)
                .addTag(SgTags.Blocks.STORAGE_BLOCKS_BLAZE_GOLD)
                .addTag(SgTags.Blocks.STORAGE_BLOCKS_CRIMSON_IRON)
                .addTag(SgTags.Blocks.STORAGE_BLOCKS_CRIMSON_STEEL)
                .addTag(SgTags.Blocks.STORAGE_BLOCKS_TYRIAN_STEEL)
                .add(SgBlocks.MATERIAL_GRADER.get())
                .add(SgBlocks.SALVAGER.get())
                .add(SgBlocks.STARLIGHT_CHARGER.get())
                .add(SgBlocks.METAL_ALLOYER.get())
                .add(SgBlocks.RECRYSTALLIZER.get())
                .add(SgBlocks.REFABRICATOR.get())
                .add(SgBlocks.METAL_PRESS.get())
                .add(SgBlocks.NETHERWOOD_CHARCOAL_BLOCK.get());

        // Silent Gear
        tag(SgTags.Blocks.FLUFFY_BLOCKS)
                .add(SgBlocks.getBlocks(FluffyBlock.class).toArray(new Block[0]));
        tag(SgTags.Blocks.NETHERWOOD_LOGS)
                .add(SgBlocks.NETHERWOOD_LOG.get())
                .add(SgBlocks.STRIPPED_NETHERWOOD_LOG.get())
                .add(SgBlocks.NETHERWOOD_WOOD.get())
                .add(SgBlocks.STRIPPED_NETHERWOOD_WOOD.get());
        tag(SgTags.Blocks.NETHERWOOD_SOIL)
                .addTag(Tags.Blocks.NETHERRACK)
                .addTag(BlockTags.DIRT)
                .add(Blocks.FARMLAND);
        tag(SgTags.Blocks.PROSPECTOR_HAMMER_TARGETS)
                .add(Blocks.ANCIENT_DEBRIS)
                .addTag(Tags.Blocks.ORES);

        // Forge
        builder(SgTags.Blocks.ORES_BORT, SgBlocks.BORT_ORE, SgBlocks.DEEPSLATE_BORT_ORE);
        builder(SgTags.Blocks.ORES_CRIMSON_IRON, SgBlocks.CRIMSON_IRON_ORE, SgBlocks.BLACKSTONE_CRIMSON_IRON_ORE);
        builder(SgTags.Blocks.ORES_AZURE_SILVER, SgBlocks.AZURE_SILVER_ORE);
        getBuilder(Tags.Blocks.ORES)
                .addTag(SgTags.Blocks.ORES_BORT)
                .addTag(SgTags.Blocks.ORES_CRIMSON_IRON)
                .addTag(SgTags.Blocks.ORES_AZURE_SILVER);

        builder(SgTags.Blocks.STORAGE_BLOCKS_RAW_CRIMSON_IRON, SgBlocks.RAW_CRIMSON_IRON_BLOCK);
        builder(SgTags.Blocks.STORAGE_BLOCKS_RAW_AZURE_SILVER, SgBlocks.RAW_AZURE_SILVER_BLOCK);

        builder(SgTags.Blocks.STORAGE_BLOCKS_BORT, SgBlocks.BORT_BLOCK);
        builder(SgTags.Blocks.STORAGE_BLOCKS_CRIMSON_IRON, SgBlocks.CRIMSON_IRON_BLOCK);
        builder(SgTags.Blocks.STORAGE_BLOCKS_CRIMSON_STEEL, SgBlocks.CRIMSON_STEEL_BLOCK);
        builder(SgTags.Blocks.STORAGE_BLOCKS_BLAZE_GOLD, SgBlocks.BLAZE_GOLD_BLOCK);
        builder(SgTags.Blocks.STORAGE_BLOCKS_AZURE_SILVER, SgBlocks.AZURE_SILVER_BLOCK);
        builder(SgTags.Blocks.STORAGE_BLOCKS_AZURE_ELECTRUM, SgBlocks.AZURE_ELECTRUM_BLOCK);
        builder(SgTags.Blocks.STORAGE_BLOCKS_TYRIAN_STEEL, SgBlocks.TYRIAN_STEEL_BLOCK);
        getBuilder(Tags.Blocks.STORAGE_BLOCKS)
                .addTag(SgTags.Blocks.STORAGE_BLOCKS_BORT)
                .addTag(SgTags.Blocks.STORAGE_BLOCKS_BLAZE_GOLD)
                .addTag(SgTags.Blocks.STORAGE_BLOCKS_CRIMSON_IRON)
                .addTag(SgTags.Blocks.STORAGE_BLOCKS_CRIMSON_STEEL)
                .addTag(SgTags.Blocks.STORAGE_BLOCKS_AZURE_SILVER)
                .addTag(SgTags.Blocks.STORAGE_BLOCKS_AZURE_ELECTRUM)
                .addTag(SgTags.Blocks.STORAGE_BLOCKS_TYRIAN_STEEL);

        // Minecraft
        builder(BlockTags.CROPS, SgBlocks.FLAX_PLANT, SgBlocks.FLUFFY_PLANT);
        builder(BlockTags.LEAVES, SgBlocks.NETHERWOOD_LEAVES);
        getBuilder(BlockTags.LOGS).addTag(SgTags.Blocks.NETHERWOOD_LOGS);
        builder(BlockTags.PLANKS, SgBlocks.NETHERWOOD_PLANKS);
        builder(BlockTags.SAPLINGS, SgBlocks.NETHERWOOD_SAPLING);
        builder(BlockTags.WOODEN_DOORS, SgBlocks.NETHERWOOD_DOOR);
        builder(BlockTags.WOODEN_FENCES, SgBlocks.NETHERWOOD_FENCE);
        builder(BlockTags.WOODEN_SLABS, SgBlocks.NETHERWOOD_SLAB);
        builder(BlockTags.WOODEN_STAIRS, SgBlocks.NETHERWOOD_STAIRS);
        builder(BlockTags.WOODEN_TRAPDOORS, SgBlocks.NETHERWOOD_TRAPDOOR);
    }

    private void builder(TagKey<Block> tag, IBlockProvider... items) {
        tag(tag).add(Arrays.stream(items).map(IBlockProvider::asBlock).toArray(Block[]::new));
    }

    protected TagsProvider.TagAppender<Block> getBuilder(TagKey<Block> tag) {
        return tag(tag);
    }
}
