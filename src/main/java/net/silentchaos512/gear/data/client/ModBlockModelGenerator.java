package net.silentchaos512.gear.data.client;

import net.minecraft.client.data.models.ItemModelOutput;
import net.minecraft.client.data.models.blockstates.BlockModelDefinitionGenerator;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelInstance;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.block.ModCropBlock;
import net.silentchaos512.gear.data.SgBlockFamilies;
import net.silentchaos512.gear.setup.SgBlocks;
import net.silentchaos512.lib.data.client.LibBlockModelGenerators;
import net.silentchaos512.lib.data.client.LibModelTemplates;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ModBlockModelGenerator extends LibBlockModelGenerators {
    public ModBlockModelGenerator(Consumer<BlockModelDefinitionGenerator> blockStateOutput, ItemModelOutput itemModelOutput, BiConsumer<Identifier, ModelInstance> modelOutput) {
        super(blockStateOutput, itemModelOutput, modelOutput);
    }

    @Override
    public void run() {
        // Ores and storage blocks
        createTrivialCube(SgBlocks.BORT_ORE.get());
        createTrivialCube(SgBlocks.DEEPSLATE_BORT_ORE.get());
        createTrivialCube(SgBlocks.CRIMSON_IRON_ORE.get());
        createTrivialCube(SgBlocks.BLACKSTONE_CRIMSON_IRON_ORE.get());
        createTrivialCube(SgBlocks.AZURE_SILVER_ORE.get());
        createTrivialCube(SgBlocks.RAW_CRIMSON_IRON_BLOCK.get());
        createTrivialCube(SgBlocks.RAW_AZURE_SILVER_BLOCK.get());
        createTrivialCube(SgBlocks.BORT_BLOCK.get());
        createTrivialCube(SgBlocks.CRIMSON_IRON_BLOCK.get());
        createTrivialCube(SgBlocks.CRIMSON_STEEL_BLOCK.get());
        createTrivialCube(SgBlocks.BLAZE_GOLD_BLOCK.get());
        createTrivialCube(SgBlocks.AZURE_SILVER_BLOCK.get());
        createTrivialCube(SgBlocks.AZURE_ELECTRUM_BLOCK.get());
        createTrivialCube(SgBlocks.TYRIAN_STEEL_BLOCK.get());

        // Netherwood
        createTrivialCube(SgBlocks.NETHERWOOD_CHARCOAL_BLOCK.get());
        createTrivialCube(SgBlocks.NETHERWOOD_LEAVES.get());
        woodProvider(SgBlocks.NETHERWOOD_LOG.get()).logWithHorizontal(SgBlocks.NETHERWOOD_LOG.get()).wood(SgBlocks.NETHERWOOD_WOOD.get());
        woodProvider(SgBlocks.STRIPPED_NETHERWOOD_LOG.get()).logWithHorizontal(SgBlocks.STRIPPED_NETHERWOOD_LOG.get()).wood(SgBlocks.STRIPPED_NETHERWOOD_WOOD.get());
        createPlantCutoutWithDefaultItem(SgBlocks.NETHERWOOD_SAPLING.get(), SgBlocks.POTTED_NETHERWOOD_SAPLING.get());
        family(SgBlocks.NETHERWOOD_PLANKS.get()).generateFor(SgBlockFamilies.NETHERWOOD.get());

        // Fluffy blocks
        createTrivialCube(SgBlocks.WHITE_FLUFFY_BLOCK.get());
        createTrivialCube(SgBlocks.ORANGE_FLUFFY_BLOCK.get());
        createTrivialCube(SgBlocks.MAGENTA_FLUFFY_BLOCK.get());
        createTrivialCube(SgBlocks.LIGHT_BLUE_FLUFFY_BLOCK.get());
        createTrivialCube(SgBlocks.YELLOW_FLUFFY_BLOCK.get());
        createTrivialCube(SgBlocks.LIME_FLUFFY_BLOCK.get());
        createTrivialCube(SgBlocks.PINK_FLUFFY_BLOCK.get());
        createTrivialCube(SgBlocks.GRAY_FLUFFY_BLOCK.get());
        createTrivialCube(SgBlocks.LIGHT_GRAY_FLUFFY_BLOCK.get());
        createTrivialCube(SgBlocks.CYAN_FLUFFY_BLOCK.get());
        createTrivialCube(SgBlocks.PURPLE_FLUFFY_BLOCK.get());
        createTrivialCube(SgBlocks.BLUE_FLUFFY_BLOCK.get());
        createTrivialCube(SgBlocks.BROWN_FLUFFY_BLOCK.get());
        createTrivialCube(SgBlocks.GREEN_FLUFFY_BLOCK.get());
        createTrivialCube(SgBlocks.RED_FLUFFY_BLOCK.get());
        createTrivialCube(SgBlocks.BLACK_FLUFFY_BLOCK.get());

        // Oddballs
        createPhantomLight();
        createNormalTorch(SgBlocks.STONE_TORCH.get(), SgBlocks.WALL_STONE_TORCH.get());

        // Crafters and Machines
        createSimpleCustomModel(SgBlocks.GEAR_SMITHING_TABLE, "block/gear_smithing_table");
        createSimpleCustomModel(SgBlocks.STARLIGHT_CHARGER, "block/starlight_charger");
        createSimpleCustomModel(SgBlocks.STONE_ANVIL, "block/stone_anvil");
        orientableMachineBlock(SgBlocks.MATERIAL_GRADER, "material_grader");
        orientableMachineBlock(SgBlocks.METAL_PRESS, "metal_press");
        orientableMachineBlock(SgBlocks.SALVAGER, "salvager");
        createTrivialCube(SgBlocks.PAINT_MIXER.get());

        // Alloy Makers
        orientableMachineBlock(SgBlocks.ALLOY_FORGE, "alloy_forge");
        orientableMachineBlock(SgBlocks.RECRYSTALLIZER, "recrystallizer");
        orientableMachineBlock(SgBlocks.REFABRICATOR, "refabricator");
        orientableMachineBlockWithoutLitState(SgBlocks.CRUDE_MIXER, "crude_mixer");
        orientableMachineBlockWithoutLitState(SgBlocks.SUPER_MIXER, "super_mixer");

        // Plants
        createCropCutoutBlock(SgBlocks.FLAX_PLANT.get(), ModCropBlock.AGE, 0, 0, 1, 1, 2, 2, 2, 3);
        createCropCutoutBlock(SgBlocks.FLUFFY_PLANT.get(), ModCropBlock.AGE, 0, 0, 1, 1, 2, 2, 2, 3);
        createWildCrop(SgBlocks.WILD_FLAX_PLANT, "block/flax_plant_stage3");
        createWildCrop(SgBlocks.WILD_FLUFFY_PLANT, "block/fluffy_plant_stage3");
    }

    private void createSimpleCustomModel(DeferredBlock<?> block, String modelPath) {
        this.blockStateOutput.accept(
                createSimpleBlock(block.get(), plainVariant(SilentGear.getId(modelPath)))
        );
    }

    private void createPhantomLight() {
        ItemModel.Unbaked itemModel = ItemModelUtils.plainModel(createFlatItemModel(SgBlocks.PHANTOM_LIGHT.asItem()));
        this.itemModelOutput.accept(SgBlocks.PHANTOM_LIGHT.asItem(), itemModel);
    }

    private void createWildCrop(DeferredBlock<?> block, String texturePath) {
        var multivariant = plainVariant(
                LibModelTemplates.CROP_CUTOUT.create(
                        block.get(),
                        TextureMapping.crop(new Material(SilentGear.getId(texturePath))),
                        this.modelOutput
                )
        );
        this.blockStateOutput.accept(
                createSimpleBlock(block.get(), multivariant)
        );
    }

    private void orientableMachineBlock(DeferredBlock<? extends Block> block, String name) {
        var offModel = plainVariant(SilentGear.getId("block/" + name));
        var onModel = plainVariant(SilentGear.getId("block/" + name + "_on"));
        this.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(block.get())
                        .with(createBooleanModelDispatch(BlockStateProperties.LIT, onModel, offModel))
                        .with(ROTATION_HORIZONTAL_FACING)
        );
    }

    private void orientableMachineBlockWithoutLitState(DeferredBlock<? extends Block> block, String name) {
        var model = plainVariant(SilentGear.getId("block/" + name));
        this.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(block.get(), model)
                        .with(ROTATION_HORIZONTAL_FACING)
        );
    }
}
