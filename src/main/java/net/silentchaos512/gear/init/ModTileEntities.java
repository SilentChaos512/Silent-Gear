package net.silentchaos512.gear.init;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.silentchaos512.gear.block.compounder.CompounderTileEntity;
import net.silentchaos512.gear.block.grader.GraderTileEntity;
import net.silentchaos512.gear.block.salvager.SalvagerTileEntity;
import net.silentchaos512.gear.gear.material.MaterialCategories;
import net.silentchaos512.lib.block.IBlockProvider;

import java.util.Arrays;
import java.util.function.Supplier;

public final class ModTileEntities {
    public static final RegistryObject<TileEntityType<GraderTileEntity>> MATERIAL_GRADER = register("material_grader", GraderTileEntity::new, ModBlocks.MATERIAL_GRADER);
    public static final RegistryObject<TileEntityType<CompounderTileEntity>> METAL_ALLOYER = register("metal_alloy", ModTileEntities::getMetalAlloyer, ModBlocks.METAL_ALLOYER);
    public static final RegistryObject<TileEntityType<CompounderTileEntity>> RECRYSTALLIZER = register("recrystallizer", ModTileEntities::getRecrystallizer, ModBlocks.RECRYSTALLIZER);
    public static final RegistryObject<TileEntityType<SalvagerTileEntity>> SALVAGER = register("salvager", SalvagerTileEntity::new, ModBlocks.SALVAGER);

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

    private static CompounderTileEntity getMetalAlloyer() {
        return new CompounderTileEntity(METAL_ALLOYER.get(),
                ModContainers.METAL_ALLOYER.get(),
                ModRecipes.COMPOUNDING_METAL_TYPE,
                ModItems.ALLOY_INGOT,
                CompounderTileEntity.STANDARD_INPUT_SLOTS,
                ImmutableList.of(MaterialCategories.METAL, MaterialCategories.DUST));
    }

    private static CompounderTileEntity getRecrystallizer() {
        return new CompounderTileEntity(RECRYSTALLIZER.get(),
                ModContainers.RECRYSTALLIZER.get(),
                ModRecipes.COMPOUNDING_GEM_TYPE,
                ModItems.HYBRID_GEM,
                CompounderTileEntity.STANDARD_INPUT_SLOTS,
                ImmutableList.of(MaterialCategories.GEM, MaterialCategories.DUST));
    }
}
