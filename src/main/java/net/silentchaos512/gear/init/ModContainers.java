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
import net.silentchaos512.gear.block.grader.GraderContainer;
import net.silentchaos512.gear.block.grader.GraderScreen;
import net.silentchaos512.gear.block.salvager.SalvagerContainer;
import net.silentchaos512.gear.block.salvager.SalvagerScreen;

public final class ModContainers {
    public static final RegistryObject<ContainerType<GraderContainer>> MATERIAL_GRADER = register("material_grader", GraderContainer::new);
    public static final RegistryObject<ContainerType<SalvagerContainer>> SALVAGER = register("salvager", SalvagerContainer::new);

    private ModContainers() {}

    static void register() {}

    @OnlyIn(Dist.CLIENT)
    public static void registerScreens(FMLClientSetupEvent event) {
        ScreenManager.registerFactory(MATERIAL_GRADER.get(), GraderScreen::new);
        ScreenManager.registerFactory(SALVAGER.get(), SalvagerScreen::new);
    }

    private static <T extends Container> RegistryObject<ContainerType<T>> register(String name, IContainerFactory<T> factory) {
        return Registration.CONTAINERS.register(name, () -> IForgeContainerType.create(factory));
    }
}
