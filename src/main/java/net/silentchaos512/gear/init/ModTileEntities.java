package net.silentchaos512.gear.init;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.block.analyzer.PartAnalyzerTileEntity;
import net.silentchaos512.gear.block.craftingstation.CraftingStationTileEntity;
import net.silentchaos512.gear.block.salvager.SalvagerTileEntity;
import net.silentchaos512.utils.Lazy;

import java.util.Locale;
import java.util.function.Supplier;

public enum ModTileEntities {
    CRAFTING_STATION(CraftingStationTileEntity::new, ModBlocks.CRAFTING_STATION::asBlock),
    PART_ANALYZER(PartAnalyzerTileEntity::new, ModBlocks.PART_ANALYZER::asBlock),
    SALVAGER(SalvagerTileEntity::new, ModBlocks.SALVAGER::asBlock);

    private final Lazy<TileEntityType<?>> type;

    ModTileEntities(Supplier<TileEntity> tileEntitySupplier, Supplier<Block> blockSupplier) {
        //noinspection ConstantConditions -- null in build
        this.type = Lazy.of(() -> TileEntityType.Builder.create(tileEntitySupplier, blockSupplier.get()).build(null));
    }

    public TileEntityType<?> type() {
        return type.get();
    }

    public static void registerAll(RegistryEvent.Register<TileEntityType<?>> event) {
        if (!event.getName().equals(ForgeRegistries.TILE_ENTITIES.getRegistryName())) return;

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
