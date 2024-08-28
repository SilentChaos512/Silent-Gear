package net.silentchaos512.gear.setup;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.block.charger.ChargerContainerMenu;
import net.silentchaos512.gear.block.charger.ChargerContainerScreen;
import net.silentchaos512.gear.block.compounder.AlloyForgeScreen;
import net.silentchaos512.gear.block.compounder.AlloyMakerContainer;
import net.silentchaos512.gear.block.compounder.RecrystallizerScreen;
import net.silentchaos512.gear.block.compounder.RefabricatorScreen;
import net.silentchaos512.gear.block.grader.GraderContainer;
import net.silentchaos512.gear.block.grader.GraderScreen;
import net.silentchaos512.gear.block.press.MetalPressContainer;
import net.silentchaos512.gear.block.press.MetalPressScreen;
import net.silentchaos512.gear.block.salvager.SalvagerContainer;
import net.silentchaos512.gear.block.salvager.SalvagerScreen;
import net.silentchaos512.gear.item.blueprint.book.BlueprintBookContainerMenu;
import net.silentchaos512.gear.item.blueprint.book.BlueprintBookContainerScreen;

public final class SgMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(BuiltInRegistries.MENU, SilentGear.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<GraderContainer>> MATERIAL_GRADER = register("material_grader",
            GraderContainer::new);

    public static final DeferredHolder<MenuType<?>, MenuType<MetalPressContainer>> METAL_PRESS = register("metal_press",
            MetalPressContainer::new);

    public static final DeferredHolder<MenuType<?>, MenuType<AlloyMakerContainer>> METAL_ALLOYER = register("metal_alloyer",
            (id, playerInventory, buffer) -> new AlloyMakerContainer(getMetalAlloyer(),
                    id,
                    playerInventory,
                    buffer,
                    SgBlocks.ALLOY_FORGE.get().getCategories()));

    public static final DeferredHolder<MenuType<?>, MenuType<AlloyMakerContainer>> RECRYSTALLIZER = register("recrystallizer",
            (id, playerInventory, buffer) -> new AlloyMakerContainer(getRecrystallizer(),
                    id,
                    playerInventory,
                    buffer,
                    SgBlocks.RECRYSTALLIZER.get().getCategories()));

    public static final DeferredHolder<MenuType<?>, MenuType<AlloyMakerContainer>> REFABRICATOR = register("refabricator",
            (id, playerInventory, buffer) -> new AlloyMakerContainer(getRefabricator(),
                    id,
                    playerInventory,
                    buffer,
                    SgBlocks.REFABRICATOR.get().getCategories()));

    public static final DeferredHolder<MenuType<?>, MenuType<SalvagerContainer>> SALVAGER = register("salvager",
            SalvagerContainer::new);

    public static final DeferredHolder<MenuType<?>, MenuType<ChargerContainerMenu>> STARLIGHT_CHARGER = register("starlight_charger",
            ChargerContainerMenu::createStarlightCharger);

    public static final DeferredHolder<MenuType<?>, MenuType<BlueprintBookContainerMenu>> BLUEPRINT_BOOK = register("blueprint_book",
            BlueprintBookContainerMenu::new);

    private SgMenuTypes() {
    }

    private static <T extends AbstractContainerMenu> DeferredHolder<MenuType<?>, MenuType<T>> register(String name, IContainerFactory<T> factory) {
        return MENU_TYPES.register(name, () -> IMenuTypeExtension.create(factory));
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

    @OnlyIn(Dist.CLIENT)
    @EventBusSubscriber(value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
    public static class ClientEvents {
        @SubscribeEvent
        public static void registerScreens(RegisterMenuScreensEvent event) {
            event.register(MATERIAL_GRADER.get(), GraderScreen::new);
            event.register(METAL_ALLOYER.get(), AlloyForgeScreen::new);
            event.register(METAL_PRESS.get(), MetalPressScreen::new);
            event.register(RECRYSTALLIZER.get(), RecrystallizerScreen::new);
            event.register(REFABRICATOR.get(), RefabricatorScreen::new);
            event.register(SALVAGER.get(), SalvagerScreen::new);
            event.register(STARLIGHT_CHARGER.get(), ChargerContainerScreen::new);

            event.register(BLUEPRINT_BOOK.get(), BlueprintBookContainerScreen::new);
        }
    }
}
