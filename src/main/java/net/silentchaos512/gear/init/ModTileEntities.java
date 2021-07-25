package net.silentchaos512.gear.init;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.silentchaos512.gear.block.charger.ChargerTileEntity;
import net.silentchaos512.gear.block.compounder.CompounderTileEntity;
import net.silentchaos512.gear.block.grader.GraderTileEntity;
import net.silentchaos512.gear.block.press.MetalPressTileEntity;
import net.silentchaos512.gear.block.salvager.SalvagerTileEntity;
import net.silentchaos512.gear.util.Const;
import net.silentchaos512.lib.block.IBlockProvider;

import java.util.Arrays;
import java.util.function.Supplier;

public final class ModTileEntities {
    public static final RegistryObject<BlockEntityType<GraderTileEntity>> MATERIAL_GRADER = register("material_grader",
            GraderTileEntity::new,
            ModBlocks.MATERIAL_GRADER);

    public static final RegistryObject<BlockEntityType<CompounderTileEntity>> METAL_ALLOYER = register("metal_alloyer",
            () -> new CompounderTileEntity<>(Const.METAL_COMPOUNDER_INFO),
            ModBlocks.METAL_ALLOYER);

    public static final RegistryObject<BlockEntityType<MetalPressTileEntity>> METAL_PRESS = register("metal_press",
            () -> new MetalPressTileEntity(),
            ModBlocks.METAL_PRESS);

    public static final RegistryObject<BlockEntityType<CompounderTileEntity>> RECRYSTALLIZER = register("recrystallizer",
            () -> new CompounderTileEntity<>(Const.GEM_COMPOUNDER_INFO),
            ModBlocks.RECRYSTALLIZER);

    public static final RegistryObject<BlockEntityType<CompounderTileEntity>> REFABRICATOR = register("refabricator",
            () -> new CompounderTileEntity<>(Const.FABRIC_COMPOUNDER_INFO),
            ModBlocks.REFABRICATOR);

    public static final RegistryObject<BlockEntityType<SalvagerTileEntity>> SALVAGER = register("salvager",
            SalvagerTileEntity::new,
            ModBlocks.SALVAGER);

    public static final RegistryObject<BlockEntityType<ChargerTileEntity>> STARLIGHT_CHARGER = register("starlight_charger",
            ChargerTileEntity::createStarlightCharger,
            ModBlocks.STARLIGHT_CHARGER);

    private ModTileEntities() {}

    static void register() {}

    @OnlyIn(Dist.CLIENT)
    public static void registerRenderers(FMLClientSetupEvent event) {
    }

    private static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> register(String name, Supplier<T> factory, IBlockProvider... blocks) {
        return Registration.TILE_ENTITIES.register(name, () -> {
            Block[] validBlocks = Arrays.stream(blocks).map(IBlockProvider::asBlock).toArray(Block[]::new);
            //noinspection ConstantConditions - null in build
            return BlockEntityType.Builder.of(factory, validBlocks).build(null);
        });
    }
}
