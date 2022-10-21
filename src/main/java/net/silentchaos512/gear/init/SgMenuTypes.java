package net.silentchaos512.gear.init;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.silentchaos512.gear.SilentGear;
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

public final class SgMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, SilentGear.MOD_ID);

    public static final RegistryObject<MenuType<GraderContainer>> MATERIAL_GRADER = register("material_grader",
            GraderContainer::new);

    public static final RegistryObject<MenuType<MetalPressContainer>> METAL_PRESS = register("metal_press",
            MetalPressContainer::new);

    public static final RegistryObject<MenuType<CompounderContainer>> METAL_ALLOYER = register("metal_alloyer",
            (id, playerInventory, buffer) -> new CompounderContainer(getMetalAlloyer(),
                    id,
                    playerInventory,
                    buffer,
                    SgBlocks.METAL_ALLOYER.get().getCategories()));

    public static final RegistryObject<MenuType<CompounderContainer>> RECRYSTALLIZER = register("recrystallizer",
            (id, playerInventory, buffer) -> new CompounderContainer(getRecrystallizer(),
                    id,
                    playerInventory,
                    buffer,
                    SgBlocks.RECRYSTALLIZER.get().getCategories()));

    public static final RegistryObject<MenuType<CompounderContainer>> REFABRICATOR = register("refabricator",
            (id, playerInventory, buffer) -> new CompounderContainer(getRefabricator(),
                    id,
                    playerInventory,
                    buffer,
                    SgBlocks.REFABRICATOR.get().getCategories()));

    public static final RegistryObject<MenuType<SalvagerContainer>> SALVAGER = register("salvager",
            SalvagerContainer::new);

    public static final RegistryObject<MenuType<ChargerContainer>> STARLIGHT_CHARGER = register("starlight_charger",
            ChargerContainer::createStarlightCharger);

    public static final RegistryObject<MenuType<BlueprintBookContainer>> BLUEPRINT_BOOK = register("blueprint_book",
            BlueprintBookContainer::new);

    private SgMenuTypes() {
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerScreens(FMLClientSetupEvent event) {
        MenuScreens.register(MATERIAL_GRADER.get(), GraderScreen::new);
        MenuScreens.register(METAL_ALLOYER.get(), MetalAlloyerScreen::new);
        MenuScreens.register(METAL_PRESS.get(), MetalPressScreen::new);
        MenuScreens.register(RECRYSTALLIZER.get(), RecrystallizerScreen::new);
        MenuScreens.register(REFABRICATOR.get(), RefabricatorScreen::new);
        MenuScreens.register(SALVAGER.get(), SalvagerScreen::new);
        MenuScreens.register(STARLIGHT_CHARGER.get(), ChargerScreen::new);

        MenuScreens.register(BLUEPRINT_BOOK.get(), BlueprintBookContainerScreen::new);
    }

    private static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> register(String name, IContainerFactory<T> factory) {
        return MENU_TYPES.register(name, () -> IForgeMenuType.create(factory));
    }

    private static MenuType<?> getMetalAlloyer() {
        return METAL_ALLOYER.get();
    }

    private static MenuType<?> getRecrystallizer() {
        return RECRYSTALLIZER.get();
    }

    private static MenuType<?> getRefabricator() {
        return REFABRICATOR.get();
    }
}
