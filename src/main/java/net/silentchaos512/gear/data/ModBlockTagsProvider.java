package net.silentchaos512.gear.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.block.FluffyBlock;
import net.silentchaos512.gear.init.ModBlocks;
import net.silentchaos512.gear.init.ModTags;
import net.silentchaos512.gear.init.Registration;
import net.silentchaos512.lib.block.IBlockProvider;

import java.util.Arrays;

public class ModBlockTagsProvider extends BlockTagsProvider {
    public ModBlockTagsProvider(DataGenerator generatorIn, ExistingFileHelper existingFileHelper) {
        super(generatorIn, SilentGear.MOD_ID, existingFileHelper);
    }

    @Override
    public String getName() {
        return "Silent Gear - Block Tags";
    }

    @Override
    public void addTags() {
        // Harvesting
        tag(Tags.Blocks.NEEDS_NETHERITE_TOOL)
                .addTag(ModTags.Blocks.ORES_AZURE_SILVER);
        tag(BlockTags.NEEDS_IRON_TOOL)
                .addTag(ModTags.Blocks.ORES_BORT)
                .addTag(ModTags.Blocks.ORES_CRIMSON_IRON);
        tag(BlockTags.NEEDS_STONE_TOOL)
                .add(ModBlocks.RAW_AZURE_SILVER_BLOCK.get())
                .add(ModBlocks.RAW_CRIMSON_IRON_BLOCK.get());

        tag(BlockTags.MINEABLE_WITH_AXE)
                .addTag(ModTags.Blocks.NETHERWOOD_LOGS)
                .add(ModBlocks.GEAR_SMITHING_TABLE.get())
                .add(ModBlocks.NETHERWOOD_PLANKS.get())
                .add(ModBlocks.NETHERWOOD_SLAB.get())
                .add(ModBlocks.NETHERWOOD_STAIRS.get())
                .add(ModBlocks.NETHERWOOD_FENCE.get())
                .add(ModBlocks.NETHERWOOD_FENCE_GATE.get())
                .add(ModBlocks.NETHERWOOD_DOOR.get())
                .add(ModBlocks.NETHERWOOD_TRAPDOOR.get());
        tag(BlockTags.MINEABLE_WITH_HOE)
                .add(ModBlocks.NETHERWOOD_LEAVES.get());
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .addTag(ModTags.Blocks.ORES_BORT)
                .addTag(ModTags.Blocks.ORES_CRIMSON_IRON)
                .addTag(ModTags.Blocks.ORES_AZURE_SILVER)
                .add(ModBlocks.RAW_CRIMSON_IRON_BLOCK.get())
                .add(ModBlocks.RAW_AZURE_SILVER_BLOCK.get())
                .addTag(ModTags.Blocks.STORAGE_BLOCKS_AZURE_SILVER)
                .addTag(ModTags.Blocks.STORAGE_BLOCKS_AZURE_ELECTRUM)
                .addTag(ModTags.Blocks.STORAGE_BLOCKS_BORT)
                .addTag(ModTags.Blocks.STORAGE_BLOCKS_BLAZE_GOLD)
                .addTag(ModTags.Blocks.STORAGE_BLOCKS_CRIMSON_IRON)
                .addTag(ModTags.Blocks.STORAGE_BLOCKS_CRIMSON_STEEL)
                .addTag(ModTags.Blocks.STORAGE_BLOCKS_TYRIAN_STEEL)
                .add(ModBlocks.MATERIAL_GRADER.get())
                .add(ModBlocks.SALVAGER.get())
                .add(ModBlocks.STARLIGHT_CHARGER.get())
                .add(ModBlocks.METAL_ALLOYER.get())
                .add(ModBlocks.RECRYSTALLIZER.get())
                .add(ModBlocks.REFABRICATOR.get())
                .add(ModBlocks.METAL_PRESS.get())
                .add(ModBlocks.NETHERWOOD_CHARCOAL_BLOCK.get());

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
                .addTag(BlockTags.DIRT)
                .add(Blocks.FARMLAND);
        getBuilder(ModTags.Blocks.PROSPECTOR_HAMMER_TARGETS)
                .add(Blocks.ANCIENT_DEBRIS)
                .addTag(Tags.Blocks.ORES);

        // Forge
        builder(ModTags.Blocks.ORES_BORT, ModBlocks.BORT_ORE, ModBlocks.DEEPSLATE_BORT_ORE);
        builder(ModTags.Blocks.ORES_CRIMSON_IRON, ModBlocks.CRIMSON_IRON_ORE, ModBlocks.BLACKSTONE_CRIMSON_IRON_ORE);
        builder(ModTags.Blocks.ORES_AZURE_SILVER, ModBlocks.AZURE_SILVER_ORE);
        getBuilder(Tags.Blocks.ORES)
                .addTag(ModTags.Blocks.ORES_BORT)
                .addTag(ModTags.Blocks.ORES_CRIMSON_IRON)
                .addTag(ModTags.Blocks.ORES_AZURE_SILVER);

        builder(ModTags.Blocks.STORAGE_BLOCKS_RAW_CRIMSON_IRON, ModBlocks.RAW_CRIMSON_IRON_BLOCK);
        builder(ModTags.Blocks.STORAGE_BLOCKS_RAW_AZURE_SILVER, ModBlocks.RAW_AZURE_SILVER_BLOCK);

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

    private void builder(TagKey<Block> tag, IBlockProvider... items) {
        getBuilder(tag).add(Arrays.stream(items).map(IBlockProvider::asBlock).toArray(Block[]::new));
    }

    protected TagsProvider.TagAppender<Block> getBuilder(TagKey<Block> tag) {
        return tag(tag);
    }
}
