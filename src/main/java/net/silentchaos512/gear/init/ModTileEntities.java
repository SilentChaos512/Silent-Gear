package net.silentchaos512.gear.init;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.silentchaos512.gear.block.compounder.CompounderTileEntity;
import net.silentchaos512.gear.block.grader.GraderTileEntity;
import net.silentchaos512.gear.block.press.MetalPressTileEntity;
import net.silentchaos512.gear.block.salvager.SalvagerTileEntity;
import net.silentchaos512.gear.util.Const;
import net.silentchaos512.lib.block.IBlockProvider;

import java.util.Arrays;
import java.util.function.Supplier;

public final class ModTileEntities {
    public static final RegistryObject<TileEntityType<GraderTileEntity>> MATERIAL_GRADER = register("material_grader",
            GraderTileEntity::new,
            ModBlocks.MATERIAL_GRADER);

    public static final RegistryObject<TileEntityType<CompounderTileEntity>> METAL_ALLOYER = register("metal_alloyer",
            () -> new CompounderTileEntity<>(Const.METAL_COMPOUNDER_INFO),
            ModBlocks.METAL_ALLOYER);

    public static final RegistryObject<TileEntityType<CompounderTileEntity>> RECRYSTALLIZER = register("recrystallizer",
            () -> new CompounderTileEntity<>(Const.GEM_COMPOUNDER_INFO),
            ModBlocks.RECRYSTALLIZER);

    public static final RegistryObject<TileEntityType<SalvagerTileEntity>> SALVAGER = register("salvager",
            SalvagerTileEntity::new,
            ModBlocks.SALVAGER);

    public static final RegistryObject<TileEntityType<MetalPressTileEntity>> METAL_PRESS = register("metal_press",
            () -> new MetalPressTileEntity(),
            ModBlocks.METAL_PRESS);

    private ModTileEntities() {}

    static void register() {}

    @OnlyIn(Dist.CLIENT)
    public static void registerRenderers(FMLClientSetupEvent event) {
    }

    private static <T extends TileEntity> RegistryObject<TileEntityType<T>> register(String name, Supplier<T> factory, IBlockProvider... blocks) {
        return Registration.TILE_ENTITIES.register(name, () -> {
            Block[] validBlocks = Arrays.stream(blocks).map(IBlockProvider::asBlock).toArray(Block[]::new);
            //noinspection ConstantConditions - null in build
            return TileEntityType.Builder.create(factory, validBlocks).build(null);
        });
    }
}
