package net.silentchaos512.gear.init;

import net.minecraft.client.gui.ScreenManager;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.network.IContainerFactory;
import net.silentchaos512.gear.block.charger.ChargerContainer;
import net.silentchaos512.gear.block.charger.ChargerScreen;
import net.silentchaos512.gear.block.compounder.CompounderContainer;
import net.silentchaos512.gear.block.compounder.MetalAlloyerScreen;
import net.silentchaos512.gear.block.compounder.RecrystallizerScreen;
import net.silentchaos512.gear.block.compounder.RefabricatorScreen;
import net.silentchaos512.gear.block.grader.GraderContainer;
import net.silentchaos512.gear.block.grader.GraderScreen;
import net.silentchaos512.gear.block.press.MetalPressContainer;
import net.silentchaos512.gear.block.press.MetalPressScreen;
import net.silentchaos512.gear.block.salvager.SalvagerContainer;
import net.silentchaos512.gear.block.salvager.SalvagerScreen;
import net.silentchaos512.gear.item.blueprint.book.BlueprintBookContainer;
import net.silentchaos512.gear.item.blueprint.book.BlueprintBookContainerScreen;

public final class ModContainers {
    public static final RegistryObject<ContainerType<GraderContainer>> MATERIAL_GRADER = register("material_grader",
            GraderContainer::new);

    public static final RegistryObject<ContainerType<MetalPressContainer>> METAL_PRESS = register("metal_press",
            MetalPressContainer::new);

    public static final RegistryObject<ContainerType<CompounderContainer>> METAL_ALLOYER = register("metal_alloyer",
            (id, playerInventory, buffer) -> new CompounderContainer(getMetalAlloyer(),
                    id,
                    playerInventory,
                    buffer,
                    ModBlocks.METAL_ALLOYER.get().getCategories()));

    public static final RegistryObject<ContainerType<CompounderContainer>> RECRYSTALLIZER = register("recrystallizer",
            (id, playerInventory, buffer) -> new CompounderContainer(getRecrystallizer(),
                    id,
                    playerInventory,
                    buffer,
                    ModBlocks.RECRYSTALLIZER.get().getCategories()));

    public static final RegistryObject<ContainerType<CompounderContainer>> REFABRICATOR = register("refabricator",
            (id, playerInventory, buffer) -> new CompounderContainer(getRefabricator(),
                    id,
                    playerInventory,
                    buffer,
                    ModBlocks.REFABRICATOR.get().getCategories()));

    public static final RegistryObject<ContainerType<SalvagerContainer>> SALVAGER = register("salvager",
            SalvagerContainer::new);

    public static final RegistryObject<ContainerType<ChargerContainer>> STARLIGHT_CHARGER = register("starlight_charger",
            ChargerContainer::createStarlightCharger);

    public static final RegistryObject<ContainerType<BlueprintBookContainer>> BLUEPRINT_BOOK = register("blueprint_book",
            BlueprintBookContainer::new);

    private ModContainers() {}

    static void register() {}

    @OnlyIn(Dist.CLIENT)
    public static void registerScreens(FMLClientSetupEvent event) {
        ScreenManager.registerFactory(MATERIAL_GRADER.get(), GraderScreen::new);
        ScreenManager.registerFactory(METAL_ALLOYER.get(), MetalAlloyerScreen::new);
        ScreenManager.registerFactory(METAL_PRESS.get(), MetalPressScreen::new);
        ScreenManager.registerFactory(RECRYSTALLIZER.get(), RecrystallizerScreen::new);
        ScreenManager.registerFactory(REFABRICATOR.get(), RefabricatorScreen::new);
        ScreenManager.registerFactory(SALVAGER.get(), SalvagerScreen::new);
        ScreenManager.registerFactory(STARLIGHT_CHARGER.get(), ChargerScreen::new);

        ScreenManager.registerFactory(BLUEPRINT_BOOK.get(), BlueprintBookContainerScreen::new);
    }

    private static <T extends Container> RegistryObject<ContainerType<T>> register(String name, IContainerFactory<T> factory) {
        return Registration.CONTAINERS.register(name, () -> IForgeContainerType.create(factory));
    }

    private static ContainerType<?> getMetalAlloyer() {
        return METAL_ALLOYER.get();
    }

    private static ContainerType<?> getRecrystallizer() {
        return RECRYSTALLIZER.get();
    }

    private static ContainerType<?> getRefabricator() {
        return REFABRICATOR.get();
    }
}
