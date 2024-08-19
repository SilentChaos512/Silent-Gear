package net.silentchaos512.gear;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
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
import net.silentchaos512.gear.config.Config;
import net.silentchaos512.gear.gear.material.MaterialManager;
import net.silentchaos512.gear.gear.material.MaterialSerializers;
import net.silentchaos512.gear.gear.part.CoreGearPart;
import net.silentchaos512.gear.gear.part.PartManager;
import net.silentchaos512.gear.gear.part.PartSerializers;
import net.silentchaos512.gear.gear.trait.TraitManager;
import net.silentchaos512.gear.setup.*;
import net.silentchaos512.gear.setup.gear.*;
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
        GearPropertyTypes.REGISTRAR.register(modEventBus);
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
        SgEnchantments.ENCHANTMENTS.register(modEventBus);
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

        if (checkClientInstance()) {
            Config.init();
        }

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
        SilentGear.LOGGER.info(TraitManager.MARKER, "Traits loaded: {}", TraitManager.getValues().size());
        SilentGear.LOGGER.info(PartManager.MARKER, "Parts loaded: {}", PartManager.getValues().size());
        SilentGear.LOGGER.info(PartManager.MARKER, "- Compound: {}", PartManager.getValues().stream()
                .filter(part -> part instanceof CoreGearPart).count());
        SilentGear.LOGGER.info(PartManager.MARKER, "- Simple: {}", PartManager.getValues().stream()
                .filter(part -> !(part instanceof CoreGearPart)).count());
        SilentGear.LOGGER.info(MaterialManager.MARKER, "Materials loaded: {}", MaterialManager.getValues().size());
        SilentGear.LOGGER.info(MaterialManager.MARKER, "- Standard: {}", MaterialManager.getValues().stream()
                .filter(mat -> mat.getSerializer() == MaterialSerializers.STANDARD).count());
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

            //noinspection ConstantConditions
            if (Minecraft.getInstance() != null) {
                ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
                if (resourceManager instanceof ReloadableResourceManager) {
                    ((ReloadableResourceManager) resourceManager).registerReloadListener(GearDisplayManager.INSTANCE);
                }
            } else {
                SilentGear.LOGGER.warn("MC instance is null? Must be running data generators! Not registering model loaders...");
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
            /*
            if (Loader.isModLoaded("jei")) {
                if (SGearJeiPlugin.hasInitFailed()) {
                    String msg = "The JEI plugin seems to have failed. Some recipes may not be visible. Please report with a copy of your log file.";
                    SilentGear.log.error(msg);
                    event.player.sendMessage(new TextComponentString(TextFormatting.RED + "[Silent Gear] " + msg));
                } else {
                    SilentGear.log.info("JEI plugin seems to have loaded correctly.");
                }
            } else {
                SilentGear.log.info("JEI is not installed?");
            }
            */
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
