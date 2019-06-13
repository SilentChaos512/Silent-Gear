package net.silentchaos512.gear.init;

import net.minecraft.client.gui.ScreenManager;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.block.analyzer.PartAnalyzerContainer;
import net.silentchaos512.gear.block.analyzer.PartAnalyzerScreen;
import net.silentchaos512.gear.block.craftingstation.CraftingStationContainer;
import net.silentchaos512.gear.block.craftingstation.CraftingStationScreen;
import net.silentchaos512.gear.block.salvager.SalvagerContainer;
import net.silentchaos512.gear.block.salvager.SalvagerScreen;
import net.silentchaos512.utils.Lazy;

import java.util.Locale;

public enum ModContainers {
    CRAFTING_STATION(CraftingStationContainer::new),
    PART_ANALYZER(PartAnalyzerContainer::new),
    SALVAGER(SalvagerContainer::new);

    private final Lazy<ContainerType<?>> type;

    ModContainers(ContainerType.IFactory<?> factory) {
        this.type = Lazy.of(() -> new ContainerType<>(factory));
    }

    public ContainerType<?> type() {
        return type.get();
    }

    public static void registerAll(RegistryEvent.Register<ContainerType<?>> event) {
        if (!event.getName().equals(ForgeRegistries.CONTAINERS.getRegistryName())) return;

        for (ModContainers container : values()) {
            register(container.name().toLowerCase(Locale.ROOT), container.type());
        }
    }

    @SuppressWarnings("unchecked")
    @OnlyIn(Dist.CLIENT)
    public static void registerScreens(FMLClientSetupEvent event) {
        ScreenManager.registerFactory((ContainerType<? extends CraftingStationContainer>) CRAFTING_STATION.type(), CraftingStationScreen::new);
        ScreenManager.registerFactory((ContainerType<? extends PartAnalyzerContainer>) PART_ANALYZER.type(), PartAnalyzerScreen::new);
        ScreenManager.registerFactory((ContainerType<? extends SalvagerContainer>) SALVAGER.type(), SalvagerScreen::new);
    }

    private static void register(String name, ContainerType<?> type) {
        ResourceLocation id = SilentGear.getId(name);
        type.setRegistryName(id);
        ForgeRegistries.CONTAINERS.register(type);
    }
}
