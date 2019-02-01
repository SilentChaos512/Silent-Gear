package net.silentchaos512.gear.init;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.LazyLoadBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.block.analyzer.TilePartAnalyzer;
import net.silentchaos512.gear.block.craftingstation.TileCraftingStation;
import net.silentchaos512.gear.block.salvager.TileSalvager;

import java.util.Locale;
import java.util.function.Supplier;

public enum ModTileEntities {
    CRAFTING_STATION(TileCraftingStation::new),
    PART_ANALYZER(TilePartAnalyzer::new),
    SALVAGER(TileSalvager::new);

    private final LazyLoadBase<TileEntityType<?>> type;

    ModTileEntities(Supplier<TileEntity> tileEntitySupplier) {
        this.type = new LazyLoadBase<>(() ->
                TileEntityType.Builder.create(tileEntitySupplier).build(null));
    }

    public TileEntityType<?> type() {
        return type.getValue();
    }

    public static void registerAll(RegistryEvent.Register<TileEntityType<?>> event) {
        if (!event.getName().equals(ForgeRegistries.TILE_ENTITIES.getRegistryName())) return;

        for (ModTileEntities tileEnum : values()) {
            register(tileEnum.name().toLowerCase(Locale.ROOT), tileEnum.type());
        }
    }

    private static <T extends TileEntity> void register(String name, TileEntityType<T> type) {
        ResourceLocation id = new ResourceLocation(SilentGear.MOD_ID, name);
        type.setRegistryName(id);
        ForgeRegistries.TILE_ENTITIES.register(type);
    }
}
