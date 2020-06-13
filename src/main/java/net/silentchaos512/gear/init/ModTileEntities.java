package net.silentchaos512.gear.init;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.block.craftingstation.CraftingStationTileEntity;
import net.silentchaos512.gear.block.grader.GraderTileEntity;
import net.silentchaos512.gear.block.salvager.SalvagerTileEntity;
import net.silentchaos512.lib.block.IBlockProvider;
import net.silentchaos512.utils.Lazy;

import java.util.Locale;
import java.util.function.Supplier;

public enum ModTileEntities {
    MATERIAL_GRADER(GraderTileEntity::new, ModBlocks.MATERIAL_GRADER),
    CRAFTING_STATION(CraftingStationTileEntity::new, ModBlocks.CRAFTING_STATION),
    SALVAGER(SalvagerTileEntity::new, ModBlocks.SALVAGER);

    private final Lazy<TileEntityType<?>> type;

    ModTileEntities(Supplier<TileEntity> tileEntitySupplier, IBlockProvider block) {
        //noinspection ConstantConditions -- null in build
        this.type = Lazy.of(() -> TileEntityType.Builder.create(tileEntitySupplier, block.asBlock()).build(null));
    }

    public TileEntityType<?> type() {
        return type.get();
    }

    public static void registerAll(RegistryEvent.Register<TileEntityType<?>> event) {
        for (ModTileEntities tileEnum : values()) {
            register(tileEnum.name().toLowerCase(Locale.ROOT), tileEnum.type());
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerRenderers(FMLClientSetupEvent event) {
    }

    private static <T extends TileEntity> void register(String name, TileEntityType<T> type) {
        ResourceLocation id = new ResourceLocation(SilentGear.MOD_ID, name);
        type.setRegistryName(id);
        ForgeRegistries.TILE_ENTITIES.register(type);
    }
}
