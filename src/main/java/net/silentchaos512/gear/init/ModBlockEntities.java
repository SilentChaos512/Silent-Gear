package net.silentchaos512.gear.init;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.silentchaos512.gear.block.charger.ChargerTileEntity;
import net.silentchaos512.gear.block.compounder.CompounderTileEntity;
import net.silentchaos512.gear.block.grader.GraderTileEntity;
import net.silentchaos512.gear.block.press.MetalPressTileEntity;
import net.silentchaos512.gear.block.salvager.SalvagerTileEntity;
import net.silentchaos512.gear.crafting.recipe.compounder.FabricCompoundingRecipe;
import net.silentchaos512.gear.crafting.recipe.compounder.GemCompoundingRecipe;
import net.silentchaos512.gear.crafting.recipe.compounder.MetalCompoundingRecipe;
import net.silentchaos512.gear.util.Const;
import net.silentchaos512.lib.block.IBlockProvider;

import java.util.Arrays;

public final class ModBlockEntities {
    public static final RegistryObject<BlockEntityType<GraderTileEntity>> MATERIAL_GRADER = register("material_grader",
            GraderTileEntity::new,
            ModBlocks.MATERIAL_GRADER);

    public static final RegistryObject<BlockEntityType<CompounderTileEntity<MetalCompoundingRecipe>>> METAL_ALLOYER = register("metal_alloyer",
            (pos, state) -> new CompounderTileEntity<>(Const.METAL_COMPOUNDER_INFO, pos, state),
            ModBlocks.METAL_ALLOYER);

    public static final RegistryObject<BlockEntityType<MetalPressTileEntity>> METAL_PRESS = register("metal_press",
            MetalPressTileEntity::new,
            ModBlocks.METAL_PRESS);

    public static final RegistryObject<BlockEntityType<CompounderTileEntity<GemCompoundingRecipe>>> RECRYSTALLIZER = register("recrystallizer",
            (pos, state) -> new CompounderTileEntity<>(Const.GEM_COMPOUNDER_INFO, pos, state),
            ModBlocks.RECRYSTALLIZER);

    public static final RegistryObject<BlockEntityType<CompounderTileEntity<FabricCompoundingRecipe>>> REFABRICATOR = register("refabricator",
            (pos, state) -> new CompounderTileEntity<>(Const.FABRIC_COMPOUNDER_INFO, pos, state),
            ModBlocks.REFABRICATOR);

    public static final RegistryObject<BlockEntityType<SalvagerTileEntity>> SALVAGER = register("salvager",
            SalvagerTileEntity::new,
            ModBlocks.SALVAGER);

    public static final RegistryObject<BlockEntityType<ChargerTileEntity>> STARLIGHT_CHARGER = register("starlight_charger",
            ChargerTileEntity::createStarlightCharger,
            ModBlocks.STARLIGHT_CHARGER);

    private ModBlockEntities() {}

    static void register() {}

    @OnlyIn(Dist.CLIENT)
    public static void registerRenderers(FMLClientSetupEvent event) {
    }

    private static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> register(String name, BlockEntityType.BlockEntitySupplier<T> factory, IBlockProvider... blocks) {
        return Registration.BLOCK_ENTITIES.register(name, () -> {
            Block[] validBlocks = Arrays.stream(blocks).map(IBlockProvider::asBlock).toArray(Block[]::new);
            //noinspection ConstantConditions - null in build
            return BlockEntityType.Builder.of(factory, validBlocks).build(null);
        });
    }
}
