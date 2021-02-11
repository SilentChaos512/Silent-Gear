package net.silentchaos512.gear.data.client;

import net.minecraft.block.FenceBlock;
import net.minecraft.data.DataGenerator;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.block.FlaxPlant;
import net.silentchaos512.gear.init.ModBlocks;
import net.silentchaos512.lib.util.NameUtils;

import javax.annotation.Nonnull;

import static net.minecraftforge.client.model.generators.ModelProvider.BLOCK_FOLDER;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, SilentGear.MOD_ID, exFileHelper);
    }

    @Nonnull
    @Override
    public String getName() {
        return "Silent Gear - Block States/Models";
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlock(ModBlocks.BORT_ORE.get());
        simpleBlock(ModBlocks.CRIMSON_IRON_ORE.get());
        simpleBlock(ModBlocks.AZURE_SILVER_ORE.get());
        simpleBlock(ModBlocks.BORT_BLOCK.get());
        simpleBlock(ModBlocks.CRIMSON_IRON_BLOCK.get());
        simpleBlock(ModBlocks.CRIMSON_STEEL_BLOCK.get());
        simpleBlock(ModBlocks.BLAZE_GOLD_BLOCK.get());
        simpleBlock(ModBlocks.AZURE_SILVER_BLOCK.get());
        simpleBlock(ModBlocks.AZURE_ELECTRUM_BLOCK.get());

        simpleBlock(ModBlocks.NETHERWOOD_CHARCOAL_BLOCK.get());
        simpleBlock(ModBlocks.NETHERWOOD_PLANKS.get());
        simpleBlock(ModBlocks.NETHERWOOD_LEAVES.get());
        axisBlock(ModBlocks.NETHERWOOD_LOG.get(), modLoc("block/netherwood_log"), modLoc("block/netherwood_log_top"));
        axisBlock(ModBlocks.STRIPPED_NETHERWOOD_LOG.get(), modLoc("block/stripped_netherwood_log"), modLoc("block/stripped_netherwood_log_top"));
        simpleBlock(ModBlocks.NETHERWOOD_WOOD.get(), models().cubeAll("netherwood_wood", modLoc("block/netherwood_log")));
        simpleBlock(ModBlocks.STRIPPED_NETHERWOOD_WOOD.get(), models().cubeAll("stripped_netherwood_wood", modLoc("block/stripped_netherwood_log")));
        simpleBlock(ModBlocks.NETHERWOOD_SAPLING.get(), models().cross("netherwood_sapling", modLoc("block/netherwood_sapling")));
        ResourceLocation planks = modLoc("block/netherwood_planks");
        slabBlock(ModBlocks.NETHERWOOD_SLAB.get(), planks, planks);
        stairsBlock(ModBlocks.NETHERWOOD_STAIRS.get(), planks);
        fenceBlock(ModBlocks.NETHERWOOD_FENCE.get(), planks);
        fenceGateBlock(ModBlocks.NETHERWOOD_FENCE_GATE.get(), planks);
        doorBlock(ModBlocks.NETHERWOOD_DOOR.get(), modLoc("block/netherwood_door_bottom"), modLoc("block/netherwood_door_top"));
        trapdoorBlock(ModBlocks.NETHERWOOD_TRAPDOOR.get(), modLoc("block/netherwood_trapdoor"), true);

        simpleBlock(ModBlocks.PHANTOM_LIGHT.get(), models().cubeAll("phantom_light", modLoc("item/blank")));
        simpleBlock(ModBlocks.POTTED_NETHERWOOD_SAPLING.get(), models()
                .withExistingParent("potted_netherwood_sapling", "block/flower_pot_cross")
                .texture("plant", "block/netherwood_sapling"));
        simpleBlock(ModBlocks.STONE_TORCH.get(), models().torch("stone_torch", modLoc("block/stone_torch")));
        getVariantBuilder(ModBlocks.WALL_STONE_TORCH.get())
                .forAllStates(state -> ConfiguredModel.builder()
                        .modelFile(wallTorch("wall_stone_torch", modLoc("block/stone_torch")))
                        .rotationY((int) state.get(BlockStateProperties.HORIZONTAL_FACING).getHorizontalAngle() + 90)
                        .build());

        // Compounders
        {
            BlockModelBuilder offModel = models().orientable("metal_alloyer",
                    modLoc("block/metal_alloyer_side"),
                    modLoc("block/metal_alloyer_front"),
                    modLoc("block/metal_alloyer_top"));
            BlockModelBuilder onModel = models().orientable("metal_alloyer_on",
                    modLoc("block/metal_alloyer_side"),
                    modLoc("block/metal_alloyer_front_on"),
                    modLoc("block/metal_alloyer_top"));
            horizontalFaceBlock(ModBlocks.METAL_ALLOYER.get(), state ->
                    state.get(BlockStateProperties.LIT) ? onModel : offModel);
        }
        {
            BlockModelBuilder offModel = models().cubeTop("recrystallizer",
                    modLoc("block/recrystallizer_side"),
                    modLoc("block/recrystallizer_top"));
            BlockModelBuilder onModel = models().cubeTop("recrystallizer_on",
                    modLoc("block/recrystallizer_side"),
                    modLoc("block/recrystallizer_top_on"));
            getVariantBuilder(ModBlocks.RECRYSTALLIZER.get()).forAllStates(state -> {
                return ConfiguredModel.builder()
                        .modelFile(state.get(BlockStateProperties.LIT) ? onModel : offModel)
                        .build();
            });
        }

        getVariantBuilder(ModBlocks.FLAX_PLANT.get()).forAllStates(state -> {
            int i = cropAgeToIndex(state.get(FlaxPlant.AGE));
            return ConfiguredModel.builder()
                    .modelFile(models().crop("flax_plant" + i, modLoc("block/flax_plant" + i)))
                    .build();
        });
        simpleBlock(ModBlocks.WILD_FLAX_PLANT.get(), models().crop("wild_flax_plant", SilentGear.getId("block/flax_plant3")));
    }

    public ModelBuilder<BlockModelBuilder> wallTorch(String name, ResourceLocation torch) {
        return models().singleTexture(name, mcLoc(BLOCK_FOLDER + "/wall_torch"), "torch", torch);
    }

    private int cropAgeToIndex(int age) {
        if (age > 6)
            return 3;
        if (age > 3)
            return 2;
        if (age > 1)
            return 1;
        return 0;
    }

    @Override
    public void fenceBlock(FenceBlock block, ResourceLocation texture) {
        super.fenceBlock(block, texture);
        models().withExistingParent(NameUtils.from(block).getPath() + "_inventory", mcLoc("block/fence_inventory"))
                .texture("texture", texture);
    }
}
