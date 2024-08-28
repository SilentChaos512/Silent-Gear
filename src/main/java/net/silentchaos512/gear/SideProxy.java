package net.silentchaos512.gear;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.*;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.silentchaos512.gear.client.ColorHandlers;
import net.silentchaos512.gear.client.event.ExtraBlockBreakHandler;
import net.silentchaos512.gear.client.event.GearHudOverlay;
import net.silentchaos512.gear.client.event.TooltipHandler;
import net.silentchaos512.gear.client.util.ModItemModelProperties;
import net.silentchaos512.gear.gear.material.MaterialSerializers;
import net.silentchaos512.gear.gear.part.CoreGearPart;
import net.silentchaos512.gear.gear.part.PartSerializers;
import net.silentchaos512.gear.setup.*;
import net.silentchaos512.gear.setup.gear.GearProperties;
import net.silentchaos512.gear.setup.gear.GearTypes;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.gear.setup.gear.TraitEffectTypes;
import net.silentchaos512.gear.world.SgWorldFeatures;
import net.silentchaos512.lib.event.Greetings;
import net.silentchaos512.lib.event.InitialSpawnItems;

import javax.annotation.Nullable;
import java.util.Collections;

class SideProxy implements IProxy {
    @Nullable
    private static MinecraftServer server;
    @Nullable
    private static CreativeModeTab creativeModeTab;

    SideProxy(IEventBus modEventBus) {
        GearProperties.REGISTRAR.register(modEventBus);
        GearTypes.REGISTRAR.register(modEventBus);
        PartTypes.REGISTRAR.register(modEventBus);
        TraitEffectTypes.REGISTRAR.register(modEventBus);
        MaterialSerializers.REGISTRAR.register(modEventBus);
        PartSerializers.REGISTRAR.register(modEventBus);

        SgBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        SgBlocks.BLOCKS.register(modEventBus);
        SgCreativeTabs.CREATIVE_TABS.register(modEventBus);
        SgCriteriaTriggers.TRIGGER_TYPES.register(modEventBus);
        SgDataComponents.REGISTRAR.register(modEventBus);
        SgEntities.ENTITIES.register(modEventBus);
        SgIngredientTypes.REGISTRAR.register(modEventBus);
        SgItems.ITEMS.register(modEventBus);
        SgLoot.LOOT_CONDITIONS.register(modEventBus);
        SgLoot.LOOT_FUNCTIONS.register(modEventBus);
        SgLoot.LOOT_MODIFIERS.register(modEventBus);
        SgMenuTypes.MENU_TYPES.register(modEventBus);
        SgVillages.POINTS_OF_INTEREST.register(modEventBus);
        SgVillages.PROFESSIONS.register(modEventBus);
        SgRecipes.RECIPE_SERIALIZERS.register(modEventBus);
        SgRecipes.RECIPE_TYPES.register(modEventBus);

        modEventBus.addListener(SgWorldFeatures::registerFeatures);
        modEventBus.addListener(SideProxy::commonSetup);
        modEventBus.addListener(SideProxy::registerCapabilities);
        modEventBus.addListener(SideProxy::imcEnqueue);
        modEventBus.addListener(SideProxy::imcProcess);

//        modEventBus.addGenericListener(ItemStat.class, ItemStats::registerStats);

        NeoForge.EVENT_BUS.addListener(SgCommands::registerAll);
        NeoForge.EVENT_BUS.addListener(SideProxy::onAddReloadListeners);
        NeoForge.EVENT_BUS.addListener(SideProxy::serverStarted);
        NeoForge.EVENT_BUS.addListener(SideProxy::serverStopping);
    }

    private static void commonSetup(FMLCommonSetupEvent event) {
        InitialSpawnItems.add(SilentGear.getId("starter_blueprints"), p -> {
            if (Config.Common.spawnWithStarterBlueprints.get())
                return Collections.singleton(SgItems.BLUEPRINT_PACKAGE.get().getStack());
            return Collections.emptyList();
        });

        NerfedGear.init();

        Greetings.addMessage(SideProxy::detectDataLoadingFailure);
    }

    private static void registerCapabilities(RegisterCapabilitiesEvent event) {
        /*if (ModList.get().isLoaded(Const.CURIOS)) {
            event.register(CurioGearItemCapability.class);
        }*/
    }

    private static void imcEnqueue(InterModEnqueueEvent event) {
    }

    private static void imcProcess(InterModProcessEvent event) {
    }

    private static void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(SgRegistries.TRAIT);
        event.addListener(SgRegistries.MATERIAL);
        event.addListener(SgRegistries.PART);
    }

    private static void serverStarted(ServerStartedEvent event) {
        server = event.getServer();
        SilentGear.LOGGER.info( "Traits loaded: {}", SgRegistries.TRAIT.stream().count());
        SilentGear.LOGGER.info("Parts loaded: {}", SgRegistries.PART.stream().count());
        SilentGear.LOGGER.info("- Compound: {}", SgRegistries.PART.stream()
                .filter(part -> part instanceof CoreGearPart).count());
        SilentGear.LOGGER.info("- Simple: {}", SgRegistries.PART.stream()
                .filter(part -> !(part instanceof CoreGearPart)).count());
        SilentGear.LOGGER.info("Materials loaded: {}", SgRegistries.MATERIAL.stream().count());
        SilentGear.LOGGER.info("- Standard: {}", SgRegistries.MATERIAL.stream()
                .filter(mat -> mat.getSerializer() == MaterialSerializers.SIMPLE.get()).count());
    }

    private static void serverStopping(ServerStoppingEvent event) {
        server = null;
    }

    @Nullable
    @Override
    public Player getClientPlayer() {
        return null;
    }

    @Nullable
    @Override
    public Level getClientLevel() {
        return null;
    }

    @Override
    public boolean checkClientInstance() {
        return true;
    }

    @Override
    public boolean checkClientConnection() {
        return true;
    }

    @Nullable
    @Override
    public MinecraftServer getServer() {
        return server;
    }

    static class Client extends SideProxy {
        Client(IEventBus modEventBus) {
            super(modEventBus);

            modEventBus.addListener(Client::clientSetup);
            modEventBus.addListener(Client::postSetup);
            modEventBus.addListener(ColorHandlers::onItemColors);

            NeoForge.EVENT_BUS.register(ExtraBlockBreakHandler.INSTANCE);
            NeoForge.EVENT_BUS.register(new GearHudOverlay());
            NeoForge.EVENT_BUS.register(TooltipHandler.INSTANCE);
            NeoForge.EVENT_BUS.addListener(this::onPlayerLoggedIn);

            if (SilentGear.isDevBuild()) {
                //NeoForge.EVENT_BUS.register(new DebugOverlay());
            }
        }

        private static void clientSetup(FMLClientSetupEvent event) {
            ModItemModelProperties.register(event);
        }

        private static void postSetup(FMLLoadCompleteEvent event) {
            /*EntityRenderDispatcher rendererManager = Minecraft.getInstance().getEntityRenderDispatcher();
            rendererManager.getSkinMap().values().forEach(renderer ->
                    renderer.addLayer(new GearElytraLayer<>(renderer)));*/
        }

        private void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        }

        @Nullable
        @Override
        public Player getClientPlayer() {
            return Minecraft.getInstance().player;
        }

        @Nullable
        @Override
        public Level getClientLevel() {
            Minecraft mc = Minecraft.getInstance();
            //noinspection ConstantConditions -- mc can be null during runData and some other circumstances
            return mc != null ? mc.level : null;
        }

        @Override
        public boolean checkClientInstance() {
            //noinspection ConstantConditions -- mc can be null during runData and some other circumstances
            return Minecraft.getInstance() != null;
        }

        @Override
        public boolean checkClientConnection() {
            Minecraft mc = Minecraft.getInstance();
            //noinspection ConstantConditions -- mc can be null during runData and some other circumstances
            return mc != null && mc.getConnection() != null;
        }
    }

    static class Server extends SideProxy {
        Server(IEventBus modEventBus) {
            super(modEventBus);

            modEventBus.addListener(this::serverSetup);
        }

        private void serverSetup(FMLDedicatedServerSetupEvent event) {
        }
    }

    @Nullable
    public static Component detectDataLoadingFailure(Player player) {
        // Check if parts/traits have loaded. If not, a mod has likely broken the data loading process.
        // We should inform the user and tell them what to look for in the log.
        if (SgRegistries.MATERIAL.keySet().isEmpty() || SgRegistries.PART.keySet().isEmpty() || SgRegistries.TRAIT.keySet().isEmpty()) {
            String msg = "Materials, parts, and/or traits have not loaded! This may be caused by a broken mod, even those not related to Silent Gear. Search your log for \"Failed to reload data packs\" to find the error.";
            SilentGear.LOGGER.error(msg);
            return Component.literal(msg);
        }
        return null;
    }
}
