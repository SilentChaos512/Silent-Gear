package net.silentchaos512.gear;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.command.arguments.ArgumentSerializer;
import net.minecraft.command.arguments.ArgumentTypes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.silentchaos512.gear.api.parts.MaterialGrade;
import net.silentchaos512.gear.client.ColorHandlers;
import net.silentchaos512.gear.client.DebugOverlay;
import net.silentchaos512.gear.client.event.ExtraBlockBreakHandler;
import net.silentchaos512.gear.client.event.TooltipHandler;
import net.silentchaos512.gear.compat.mineandslash.MineAndSlashCompat;
import net.silentchaos512.gear.config.Config;
import net.silentchaos512.gear.init.*;
import net.silentchaos512.gear.item.CraftingItems;
import net.silentchaos512.gear.network.Network;
import net.silentchaos512.gear.parts.PartManager;
import net.silentchaos512.gear.traits.TraitManager;
import net.silentchaos512.gear.util.IAOETool;
import net.silentchaos512.gear.world.ModWorldFeatures;
import net.silentchaos512.lib.event.Greetings;
import net.silentchaos512.lib.event.InitialSpawnItems;
import net.silentchaos512.lib.util.LibHooks;

import javax.annotation.Nullable;
import java.util.Collections;

class SideProxy implements IProxy {
    @Nullable private static MinecraftServer server;

    SideProxy() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(SideProxy::commonSetup);
        modEventBus.addListener(SideProxy::imcEnqueue);
        modEventBus.addListener(SideProxy::imcProcess);

        modEventBus.addGenericListener(Block.class, ModBlocks::registerAll);
        modEventBus.addGenericListener(ContainerType.class, ModContainers::registerAll);
        modEventBus.addGenericListener(EntityType.class, ModEntities::registerAll);
        modEventBus.addGenericListener(Item.class, ModItems::registerAll);
        modEventBus.addGenericListener(Placement.class, ModPlacement::registerAll);
        modEventBus.addGenericListener(TileEntityType.class, ModTileEntities::registerAll);

        MinecraftForge.EVENT_BUS.addListener(SideProxy::serverAboutToStart);
        MinecraftForge.EVENT_BUS.addListener(SideProxy::serverStarted);
        MinecraftForge.EVENT_BUS.addListener(SideProxy::serverStarting);
        MinecraftForge.EVENT_BUS.addListener(SideProxy::serverStopping);

        Config.init();
        Network.init();

        ModLootStuff.init();
        ModRecipes.init();

        ArgumentTypes.register("material_grade", MaterialGrade.Argument.class, new ArgumentSerializer<>(MaterialGrade.Argument::new));
    }

    private static void commonSetup(FMLCommonSetupEvent event) {
        DeferredWorkQueue.runLater(ModWorldFeatures::addFeaturesToBiomes);

        IAOETool.BreakHandler.buildOreBlocksSet();

        InitialSpawnItems.add(SilentGear.getId("starter_blueprints"), p -> {
            if (Config.GENERAL.spawnWithStarterBlueprints.get())
                return Collections.singleton(ModItems.blueprintPackage.getStack());
            return Collections.emptyList();
        });

        LibHooks.registerCompostable(0.3f, ModItems.flaxseeds);
        LibHooks.registerCompostable(0.5f, CraftingItems.FLAX_FIBER);

        NerfedGear.init();

        if (ModList.get().isLoaded("mmorpg") && Config.GENERAL.mineAndSlashSupport.get()) {
            MineAndSlashCompat.init();
        }

        Greetings.addMessage(SideProxy::detectDataLoadingFailure);
    }

    private static void imcEnqueue(InterModEnqueueEvent event) {}

    private static void imcProcess(InterModProcessEvent event) {}

    private static void serverAboutToStart(FMLServerAboutToStartEvent event) {
        IReloadableResourceManager resourceManager = event.getServer().getResourceManager();
        resourceManager.addReloadListener(TraitManager.INSTANCE);
        resourceManager.addReloadListener(PartManager.INSTANCE);
    }

    private static void serverStarting(FMLServerStartingEvent event) {
        ModCommands.registerAll(event.getServer().getCommandManager().getDispatcher());
    }

    private static void serverStarted(FMLServerStartedEvent event) {
        server = event.getServer();
        SilentGear.LOGGER.info(TraitManager.MARKER, "Gear traits loaded: {}", TraitManager.getValues().size());
        SilentGear.LOGGER.info(PartManager.MARKER, "Gear parts loaded: {}", PartManager.getValues().size());
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

            // FIXME: These do not work!
//            ModelLoaderRegistry.registerLoader(ToolModel.Loader.INSTANCE);
//            ModelLoaderRegistry.registerLoader(ArmorItemModel.Loader.INSTANCE);
        }

        private static void clientSetup(FMLClientSetupEvent event) {
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
        if (PartManager.getValues().isEmpty() || TraitManager.getValues().isEmpty()) {
            String msg = "Parts and/or traits have not loaded! This may be caused by a broken mod, even those not related to Silent Gear. Search your log for \"Failed to reload data packs\" to find the error.";
            SilentGear.LOGGER.error(msg);
            return new StringTextComponent(msg);
        }
        return null;
    }
}
