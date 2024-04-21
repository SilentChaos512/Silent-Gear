package net.silentchaos512.gear.setup;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.silentchaos512.gear.SilentGear;
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

public final class SgBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, SilentGear.MOD_ID);

    public static final RegistryObject<BlockEntityType<GraderTileEntity>> MATERIAL_GRADER = register("material_grader",
            GraderTileEntity::new,
            SgBlocks.MATERIAL_GRADER);

    public static final RegistryObject<BlockEntityType<CompounderTileEntity<MetalCompoundingRecipe>>> METAL_ALLOYER = register("metal_alloyer",
            (pos, state) -> new CompounderTileEntity<>(Const.METAL_COMPOUNDER_INFO, pos, state),
            SgBlocks.METAL_ALLOYER);

    public static final RegistryObject<BlockEntityType<MetalPressTileEntity>> METAL_PRESS = register("metal_press",
            MetalPressTileEntity::new,
            SgBlocks.METAL_PRESS);

    public static final RegistryObject<BlockEntityType<CompounderTileEntity<GemCompoundingRecipe>>> RECRYSTALLIZER = register("recrystallizer",
            (pos, state) -> new CompounderTileEntity<>(Const.GEM_COMPOUNDER_INFO, pos, state),
            SgBlocks.RECRYSTALLIZER);

    public static final RegistryObject<BlockEntityType<CompounderTileEntity<FabricCompoundingRecipe>>> REFABRICATOR = register("refabricator",
            (pos, state) -> new CompounderTileEntity<>(Const.FABRIC_COMPOUNDER_INFO, pos, state),
            SgBlocks.REFABRICATOR);

    public static final RegistryObject<BlockEntityType<SalvagerTileEntity>> SALVAGER = register("salvager",
            SalvagerTileEntity::new,
            SgBlocks.SALVAGER);

    public static final RegistryObject<BlockEntityType<ChargerTileEntity>> STARLIGHT_CHARGER = register("starlight_charger",
            ChargerTileEntity::createStarlightCharger,
            SgBlocks.STARLIGHT_CHARGER);

    private SgBlockEntities() {
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerRenderers(FMLClientSetupEvent event) {
    }

    private static <T extends BlockEntity> DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> register(String name, BlockEntityType.BlockEntitySupplier<T> factory, IBlockProvider... blocks) {
        return BLOCK_ENTITIES.register(name, () -> {
            Block[] validBlocks = Arrays.stream(blocks).map(IBlockProvider::asBlock).toArray(Block[]::new);
            //noinspection ConstantConditions - null in build
            return BlockEntityType.Builder.of(factory, validBlocks).build(null);
        });
    }
}
