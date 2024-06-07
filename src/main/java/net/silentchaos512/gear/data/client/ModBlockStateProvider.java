package net.silentchaos512.gear.data.client;

import net.minecraft.core.Direction;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.*;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.block.ModCropBlock;
import net.silentchaos512.gear.setup.SgBlocks;
import net.silentchaos512.lib.util.NameUtils;

import javax.annotation.Nonnull;

import static net.neoforged.neoforge.client.model.generators.ModelProvider.BLOCK_FOLDER;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen.getPackOutput(), SilentGear.MOD_ID, exFileHelper);
    }

    @Nonnull
    @Override
    public String getName() {
        return "Silent Gear - Block States/Models";
    }

    @Override
    protected void registerStatesAndModels() {
        // Ores and storage blocks
        simpleBlock(SgBlocks.BORT_ORE.get());
        simpleBlock(SgBlocks.DEEPSLATE_BORT_ORE.get());
        simpleBlock(SgBlocks.CRIMSON_IRON_ORE.get());
        simpleBlock(SgBlocks.BLACKSTONE_CRIMSON_IRON_ORE.get());
        simpleBlock(SgBlocks.AZURE_SILVER_ORE.get());
        simpleBlock(SgBlocks.RAW_CRIMSON_IRON_BLOCK.get());
        simpleBlock(SgBlocks.RAW_AZURE_SILVER_BLOCK.get());
        simpleBlock(SgBlocks.BORT_BLOCK.get());
        simpleBlock(SgBlocks.CRIMSON_IRON_BLOCK.get());
        simpleBlock(SgBlocks.CRIMSON_STEEL_BLOCK.get());
        simpleBlock(SgBlocks.BLAZE_GOLD_BLOCK.get());
        simpleBlock(SgBlocks.AZURE_SILVER_BLOCK.get());
        simpleBlock(SgBlocks.AZURE_ELECTRUM_BLOCK.get());
        simpleBlock(SgBlocks.TYRIAN_STEEL_BLOCK.get());

        // Netherwood
        simpleBlock(SgBlocks.NETHERWOOD_CHARCOAL_BLOCK.get());
        simpleBlock(SgBlocks.NETHERWOOD_PLANKS.get());
        simpleBlock(SgBlocks.NETHERWOOD_LEAVES.get());
        axisBlock(SgBlocks.NETHERWOOD_LOG.get(), modLoc("block/netherwood_log"), modLoc("block/netherwood_log_top"));
        axisBlock(SgBlocks.STRIPPED_NETHERWOOD_LOG.get(), modLoc("block/stripped_netherwood_log"), modLoc("block/stripped_netherwood_log_top"));
        simpleBlock(SgBlocks.NETHERWOOD_WOOD.get(), models().cubeAll("netherwood_wood", modLoc("block/netherwood_log")));
        simpleBlock(SgBlocks.STRIPPED_NETHERWOOD_WOOD.get(), models().cubeAll("stripped_netherwood_wood", modLoc("block/stripped_netherwood_log")));
        simpleBlock(SgBlocks.NETHERWOOD_SAPLING.get(), models().cross("netherwood_sapling", modLoc("block/netherwood_sapling")).renderType("cutout"));
        ResourceLocation planks = modLoc("block/netherwood_planks");
        slabBlock(SgBlocks.NETHERWOOD_SLAB.get(), planks, planks);
        stairsBlock(SgBlocks.NETHERWOOD_STAIRS.get(), planks);
        fenceBlock(SgBlocks.NETHERWOOD_FENCE.get(), planks);
        fenceGateBlock(SgBlocks.NETHERWOOD_FENCE_GATE.get(), planks);
        doorBlockInternal(SgBlocks.NETHERWOOD_DOOR.get(), modLoc("block/netherwood_door_bottom"), modLoc("block/netherwood_door_top"));
        trapdoorBlockInternal(SgBlocks.NETHERWOOD_TRAPDOOR.get(), modLoc("block/netherwood_trapdoor"), true);

        // Fluffy blocks
        simpleBlock(SgBlocks.WHITE_FLUFFY_BLOCK.get());
        simpleBlock(SgBlocks.ORANGE_FLUFFY_BLOCK.get());
        simpleBlock(SgBlocks.MAGENTA_FLUFFY_BLOCK.get());
        simpleBlock(SgBlocks.LIGHT_BLUE_FLUFFY_BLOCK.get());
        simpleBlock(SgBlocks.YELLOW_FLUFFY_BLOCK.get());
        simpleBlock(SgBlocks.LIME_FLUFFY_BLOCK.get());
        simpleBlock(SgBlocks.PINK_FLUFFY_BLOCK.get());
        simpleBlock(SgBlocks.GRAY_FLUFFY_BLOCK.get());
        simpleBlock(SgBlocks.LIGHT_GRAY_FLUFFY_BLOCK.get());
        simpleBlock(SgBlocks.CYAN_FLUFFY_BLOCK.get());
        simpleBlock(SgBlocks.PURPLE_FLUFFY_BLOCK.get());
        simpleBlock(SgBlocks.BLUE_FLUFFY_BLOCK.get());
        simpleBlock(SgBlocks.BROWN_FLUFFY_BLOCK.get());
        simpleBlock(SgBlocks.GREEN_FLUFFY_BLOCK.get());
        simpleBlock(SgBlocks.RED_FLUFFY_BLOCK.get());
        simpleBlock(SgBlocks.BLACK_FLUFFY_BLOCK.get());

        // Oddballs
        simpleBlock(SgBlocks.PHANTOM_LIGHT.get(), models().cubeAll("phantom_light", modLoc("item/blank")));
        simpleBlock(SgBlocks.POTTED_NETHERWOOD_SAPLING.get(), models()
                .withExistingParent("potted_netherwood_sapling", "block/flower_pot_cross")
                .texture("plant", "block/netherwood_sapling"));
        simpleBlock(SgBlocks.STONE_TORCH.get(), models().torch("stone_torch", modLoc("block/stone_torch")).renderType("cutout"));
        getVariantBuilder(SgBlocks.WALL_STONE_TORCH.get())
                .forAllStates(state -> ConfiguredModel.builder()
                        .modelFile(wallTorch("wall_stone_torch", modLoc("block/stone_torch")))
                        .rotationY((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 90)
                        .build());

        // Crafters and Machines
        simpleBlock(SgBlocks.GEAR_SMITHING_TABLE.get(), getExistingModel("gear_smithing_table"));
        simpleBlock(SgBlocks.STARLIGHT_CHARGER.get(), getExistingModel("starlight_charger"));
        orientableMachineBlock(SgBlocks.MATERIAL_GRADER, "material_grader");
        orientableMachineBlock(SgBlocks.METAL_PRESS, "metal_press");
        orientableMachineBlock(SgBlocks.SALVAGER, "salvager");

        // Compounders
        orientableMachineBlock(SgBlocks.ALLOY_FORGE, "metal_alloyer");
        orientableMachineBlock(SgBlocks.RECRYSTALLIZER, "recrystallizer");
        orientableMachineBlock(SgBlocks.REFABRICATOR, "refabricator");

        // Plants
        getVariantBuilder(SgBlocks.FLAX_PLANT.get()).forAllStates(state -> {
            int i = cropAgeToIndex(state.getValue(ModCropBlock.AGE));
            return ConfiguredModel.builder()
                    .modelFile(models().crop("flax_plant" + i, modLoc("block/flax_plant" + i)).renderType("cutout"))
                    .build();
        });
        simpleBlock(SgBlocks.WILD_FLAX_PLANT.get(), models().crop("wild_flax_plant", modLoc("block/flax_plant3")).renderType("cutout"));

        getVariantBuilder(SgBlocks.FLUFFY_PLANT.get()).forAllStates(state -> {
            int i = cropAgeToIndex(state.getValue(ModCropBlock.AGE));
            return ConfiguredModel.builder()
                    .modelFile(models().crop("fluffy_plant" + i, modLoc("block/fluffy_plant" + i)).renderType("cutout"))
                    .build();
        });
        simpleBlock(SgBlocks.WILD_FLUFFY_PLANT.get(), models().crop("wild_fluffy_plant", modLoc("block/fluffy_plant3")).renderType("cutout"));
    }

    private ModelFile.ExistingModelFile getExistingModel(String blockName) {
        return models().getExistingFile(modLoc(blockName));
    }

    private void orientableMachineBlock(DeferredBlock<? extends Block> block, String name) {
        ModelFile.ExistingModelFile offModel = getExistingModel(name);
        ModelFile.ExistingModelFile onModel = getExistingModel(name + "_on");
        getVariantBuilder(block.value()).forAllStates(state -> {
            Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            boolean lit = state.getValue(BlockStateProperties.LIT);
            return ConfiguredModel.builder()
                    .modelFile(lit ? onModel : offModel)
                    .rotationY((int) facing.getOpposite().toYRot())
                    .build();
        });
    }

    public ModelBuilder<BlockModelBuilder> wallTorch(String name, ResourceLocation torch) {
        return models().singleTexture(name, mcLoc(BLOCK_FOLDER + "/wall_torch"), "torch", torch).renderType("cutout");
    }

    private static int cropAgeToIndex(int age) {
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
        models().withExistingParent(NameUtils.fromBlock(block).getPath() + "_inventory", mcLoc("block/fence_inventory"))
                .texture("texture", texture);
    }

    private void doorBlockInternal(DoorBlock block, ResourceLocation bottom, ResourceLocation top) {
        String baseName = NameUtils.fromBlock(block).toString();
        ModelFile bottomLeft = models().doorBottomLeft(baseName + "_bottom_left", bottom, top).renderType("cutout");
        ModelFile bottomLeftOpen = models().doorBottomLeftOpen(baseName + "_bottom_left_open", bottom, top).renderType("cutout");
        ModelFile bottomRight = models().doorBottomRight(baseName + "_bottom_right", bottom, top).renderType("cutout");
        ModelFile bottomRightOpen = models().doorBottomRightOpen(baseName + "_bottom_right_open", bottom, top).renderType("cutout");
        ModelFile topLeft = models().doorTopLeft(baseName + "_top_left", bottom, top).renderType("cutout");
        ModelFile topLeftOpen = models().doorTopLeftOpen(baseName + "_top_left_open", bottom, top).renderType("cutout");
        ModelFile topRight = models().doorTopRight(baseName + "_top_right", bottom, top).renderType("cutout");
        ModelFile topRightOpen = models().doorTopRightOpen(baseName + "_top_right_open", bottom, top).renderType("cutout");
        doorBlock(block, bottomLeft, bottomLeftOpen, bottomRight, bottomRightOpen, topLeft, topLeftOpen, topRight, topRightOpen);
    }

    private void trapdoorBlockInternal(TrapDoorBlock block, ResourceLocation texture, boolean orientable) {
        String baseName = NameUtils.fromBlock(block).toString();
        ModelFile bottom = orientable
                ? models().trapdoorOrientableBottom(baseName + "_bottom", texture).renderType("cutout")
                : models().trapdoorBottom(baseName + "_bottom", texture).renderType("cutout");
        ModelFile top = orientable
                ? models().trapdoorOrientableTop(baseName + "_top", texture).renderType("cutout")
                : models().trapdoorTop(baseName + "_top", texture).renderType("cutout");
        ModelFile open = orientable
                ? models().trapdoorOrientableOpen(baseName + "_open", texture).renderType("cutout")
                : models().trapdoorOpen(baseName + "_open", texture).renderType("cutout");
        trapdoorBlock(block, bottom, top, open, orientable);
    }
}
