package net.silentchaos512.gear;

import net.minecraft.client.Minecraft;
import net.minecraft.command.arguments.ArgumentSerializer;
import net.minecraft.command.arguments.ArgumentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.silentchaos512.gear.api.parts.MaterialGrade;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.client.ColorHandlers;
import net.silentchaos512.gear.client.DebugOverlay;
import net.silentchaos512.gear.client.KeyTracker;
import net.silentchaos512.gear.client.event.ExtraBlockBreakHandler;
import net.silentchaos512.gear.client.event.TooltipHandler;
import net.silentchaos512.gear.client.model.GearModelLoader;
import net.silentchaos512.gear.compat.gamestages.GameStagesCompat;
import net.silentchaos512.gear.compat.mineandslash.MineAndSlashCompat;
import net.silentchaos512.gear.config.Config;
import net.silentchaos512.gear.data.DataGenerators;
import net.silentchaos512.gear.gear.material.MaterialManager;
import net.silentchaos512.gear.gear.material.MaterialSerializers;
import net.silentchaos512.gear.init.*;
import net.silentchaos512.gear.item.CraftingItems;
import net.silentchaos512.gear.network.Network;
import net.silentchaos512.gear.parts.PartManager;
import net.silentchaos512.gear.parts.type.CompoundPart;
import net.silentchaos512.gear.traits.TraitManager;
import net.silentchaos512.gear.world.ModWorldFeatures;
import net.silentchaos512.lib.event.Greetings;
import net.silentchaos512.lib.event.InitialSpawnItems;
import net.silentchaos512.lib.util.LibHooks;

import javax.annotation.Nullable;
import java.util.Collections;

class SideProxy implements IProxy {
    @Nullable private static MinecraftServer server;

    SideProxy() {
        Registration.register();
        Config.init();
        Network.init();

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(DataGenerators::gatherData);
        modEventBus.addListener(SideProxy::commonSetup);
        modEventBus.addListener(SideProxy::imcEnqueue);
        modEventBus.addListener(SideProxy::imcProcess);

        modEventBus.addListener(ItemStats::createRegistry);
        modEventBus.addGenericListener(Feature.class, ModWorldFeatures::registerFeatures);
        modEventBus.addGenericListener(GlobalLootModifierSerializer.class, ModLootStuff::registerGlobalModifiers);
        modEventBus.addGenericListener(IRecipeSerializer.class, ModRecipes::registerRecipeSerializers);
        modEventBus.addGenericListener(ItemStat.class, ItemStats::registerStats);
        modEventBus.addGenericListener(Placement.class, ModWorldFeatures::registerPlacements);

        MinecraftForge.EVENT_BUS.addListener(ModCommands::registerAll);
        MinecraftForge.EVENT_BUS.addListener(SideProxy::onAddReloadListeners);
        MinecraftForge.EVENT_BUS.addListener(SideProxy::serverStarted);
        MinecraftForge.EVENT_BUS.addListener(SideProxy::serverStopping);

        ModLootStuff.init();

        ArgumentTypes.register("material_grade", MaterialGrade.Argument.class, new ArgumentSerializer<>(MaterialGrade.Argument::new));
    }

    private static void commonSetup(FMLCommonSetupEvent event) {
        DeferredWorkQueue.runLater(ModWorldFeatures::addFeaturesToBiomes);

        InitialSpawnItems.add(SilentGear.getId("starter_blueprints"), p -> {
            if (Config.Server.spawnWithStarterBlueprints.get())
                return Collections.singleton(ModItems.BLUEPRINT_PACKAGE.get().getStack());
            return Collections.emptyList();
        });

        LibHooks.registerCompostable(0.3f, ModItems.FLAX_SEEDS);
        LibHooks.registerCompostable(0.5f, CraftingItems.FLAX_FIBER);

        NerfedGear.init();

        if (ModList.get().isLoaded("mmorpg") && Config.Server.mineAndSlashSupport.get()) {
            MineAndSlashCompat.init();
        }

        Greetings.addMessage(SideProxy::detectDataLoadingFailure);
    }

    private static void imcEnqueue(InterModEnqueueEvent event) {}

    private static void imcProcess(InterModProcessEvent event) {}

    private static void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(TraitManager.INSTANCE);
        event.addListener(PartManager.INSTANCE);
        event.addListener(MaterialManager.INSTANCE);

        if (ModList.get().isLoaded("gamestages")) {
            event.addListener(GameStagesCompat.INSTANCE);
        }
    }

    private static void serverStarted(FMLServerStartedEvent event) {
        server = event.getServer();
        SilentGear.LOGGER.info(TraitManager.MARKER, "Traits loaded: {}", TraitManager.getValues().size());
        SilentGear.LOGGER.info(PartManager.MARKER, "Parts loaded: {}", PartManager.getValues().size());
        SilentGear.LOGGER.info(PartManager.MARKER, "- Compound: {}", PartManager.getValues().stream()
                .filter(part -> part instanceof CompoundPart).count());
        SilentGear.LOGGER.info(PartManager.MARKER, "- Simple/Legacy: {}", PartManager.getValues().stream()
                .filter(part -> !(part instanceof CompoundPart)).count());
        SilentGear.LOGGER.info(MaterialManager.MARKER, "Materials loaded: {}", MaterialManager.getValues().size());
        SilentGear.LOGGER.info(MaterialManager.MARKER, "- Standard: {}", MaterialManager.getValues().stream()
                .filter(mat -> mat.getSerializer() == MaterialSerializers.STANDARD).count());
    }

    private static void serverStopping(FMLServerStoppingEvent event) {
        server = null;
    }

    @Nullable
    @Override
    public PlayerEntity getClientPlayer() {
        return null;
    }

    @Nullable
    @Override
    public MinecraftServer getServer() {
        return server;
    }

    static class Client extends SideProxy {
        Client() {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(Client::clientSetup);
            FMLJavaModLoadingContext.get().getModEventBus().addListener(ColorHandlers::onItemColors);

            MinecraftForge.EVENT_BUS.register(ExtraBlockBreakHandler.INSTANCE);
            MinecraftForge.EVENT_BUS.register(TooltipHandler.INSTANCE);
            MinecraftForge.EVENT_BUS.addListener(this::onPlayerLoggedIn);

            if (SilentGear.isDevBuild()) {
                MinecraftForge.EVENT_BUS.register(new DebugOverlay());
            }

            // FIXME: Crashes on runData because MC instance is null
            //noinspection ConstantConditions
            if (Minecraft.getInstance() != null) {
                ModelLoaderRegistry.registerLoader(SilentGear.getId("gear_model"), new GearModelLoader());
            } else {
                SilentGear.LOGGER.warn("MC instance is null? Must be running data generators! Not registering model loaders...");
            }
        }

        private static void clientSetup(FMLClientSetupEvent event) {
            KeyTracker.register(event);
            ModBlocks.registerRenderTypes(event);
            ModEntities.registerRenderers(event);
            ModTileEntities.registerRenderers(event);
            ModContainers.registerScreens(event);
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
        public PlayerEntity getClientPlayer() {
            return Minecraft.getInstance().player;
        }
    }

    static class Server extends SideProxy {
        Server() {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(this::serverSetup);
        }

        private void serverSetup(FMLDedicatedServerSetupEvent event) {}
    }

    @Nullable
    public static ITextComponent detectDataLoadingFailure(PlayerEntity player) {
        // Check if parts/traits have loaded. If not, a mod has likely broken the data loading process.
        // We should inform the user and tell them what to look for in the log.
        if (MaterialManager.getValues().isEmpty() || PartManager.getValues().isEmpty() || TraitManager.getValues().isEmpty()) {
            String msg = "Materials, parts, and/or traits have not loaded! This may be caused by a broken mod, even those not related to Silent Gear. Search your log for \"Failed to reload data packs\" to find the error.";
            SilentGear.LOGGER.error(msg);
            return new StringTextComponent(msg);
        }
        return null;
    }
}
