package net.silentchaos512.gear.setup;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.block.charger.ChargerContainerMenu;
import net.silentchaos512.gear.block.charger.ChargerContainerScreen;
import net.silentchaos512.gear.block.compounder.AlloyForgeScreen;
import net.silentchaos512.gear.block.compounder.CompoundMakerContainer;
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
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(BuiltInRegistries.MENU, SilentGear.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<GraderContainer>> MATERIAL_GRADER = register("material_grader",
            GraderContainer::new);

    public static final DeferredHolder<MenuType<?>, MenuType<MetalPressContainer>> METAL_PRESS = register("metal_press",
            MetalPressContainer::new);

    public static final DeferredHolder<MenuType<?>, MenuType<CompoundMakerContainer>> METAL_ALLOYER = register("metal_alloyer",
            (id, playerInventory, buffer) -> new CompoundMakerContainer(getMetalAlloyer(),
                    id,
                    playerInventory,
                    buffer,
                    SgBlocks.ALLOY_FORGE.get().getCategories()));

    public static final DeferredHolder<MenuType<?>, MenuType<CompoundMakerContainer>> RECRYSTALLIZER = register("recrystallizer",
            (id, playerInventory, buffer) -> new CompoundMakerContainer(getRecrystallizer(),
                    id,
                    playerInventory,
                    buffer,
                    SgBlocks.RECRYSTALLIZER.get().getCategories()));

    public static final DeferredHolder<MenuType<?>, MenuType<CompoundMakerContainer>> REFABRICATOR = register("refabricator",
            (id, playerInventory, buffer) -> new CompoundMakerContainer(getRefabricator(),
                    id,
                    playerInventory,
                    buffer,
                    SgBlocks.REFABRICATOR.get().getCategories()));

    public static final DeferredHolder<MenuType<?>, MenuType<SalvagerContainer>> SALVAGER = register("salvager",
            SalvagerContainer::new);

    public static final DeferredHolder<MenuType<?>, MenuType<ChargerContainerMenu>> STARLIGHT_CHARGER = register("starlight_charger",
            ChargerContainerMenu::createStarlightCharger);

    public static final DeferredHolder<MenuType<?>, MenuType<BlueprintBookContainer>> BLUEPRINT_BOOK = register("blueprint_book",
            BlueprintBookContainer::new);

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
    @Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
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

            MenuScreens.register(BLUEPRINT_BOOK.get(), BlueprintBookContainerScreen::new);
        }
    }
}
