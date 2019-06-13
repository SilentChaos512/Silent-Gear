package net.silentchaos512.gear;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.item.ItemStack;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.silentchaos512.gear.client.DebugOverlay;
import net.silentchaos512.gear.client.event.ExtraBlockBreakHandler;
import net.silentchaos512.gear.client.event.TooltipHandler;
import net.silentchaos512.gear.command.GradeTestCommand;
import net.silentchaos512.gear.command.LockStatsCommand;
import net.silentchaos512.gear.command.RecalculateStatsCommand;
import net.silentchaos512.gear.command.SGearPartsCommand;
import net.silentchaos512.gear.config.Config;
import net.silentchaos512.gear.init.*;
import net.silentchaos512.gear.network.Network;
import net.silentchaos512.gear.parts.PartManager;
import net.silentchaos512.gear.traits.TraitManager;
import net.silentchaos512.gear.util.IAOETool;
import net.silentchaos512.gear.world.ModWorldFeatures;
import net.silentchaos512.lib.event.InitialSpawnItems;

class SideProxy {
    SideProxy() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(SideProxy::commonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(SideProxy::imcEnqueue);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(SideProxy::imcProcess);

        FMLJavaModLoadingContext.get().getModEventBus().addListener(ModBlocks::registerAll);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ModContainers::registerAll);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ModEntities::registerAll);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ModItems::registerAll);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ModTileEntities::registerAll);

        MinecraftForge.EVENT_BUS.addListener(SideProxy::serverAboutToStart);
        MinecraftForge.EVENT_BUS.addListener(SideProxy::serverStarted);

        Config.init();
        Network.init();

        ModLootStuff.init();
        ModRecipes.init();
    }

    private static void commonSetup(FMLCommonSetupEvent event) {
        DeferredWorkQueue.runLater(ModWorldFeatures::addFeaturesToBiomes);

        IAOETool.BreakHandler.buildOreBlocksSet();

        InitialSpawnItems.add(new ResourceLocation(SilentGear.MOD_ID, "starter_blueprints"), () -> {
            if (Config.GENERAL.spawnWithStarterBlueprints.get())
                return ModItems.blueprintPackage.getStack();
            return ItemStack.EMPTY;
        });
    }

    private static void imcEnqueue(InterModEnqueueEvent event) { }

    private static void imcProcess(InterModProcessEvent event) { }

    private static void serverAboutToStart(FMLServerAboutToStartEvent event) {
        IReloadableResourceManager resourceManager = event.getServer().getResourceManager();
        resourceManager.addReloadListener(TraitManager.INSTANCE);
        resourceManager.addReloadListener(PartManager.INSTANCE);

        CommandDispatcher<CommandSource> dispatcher = event.getServer().getCommandManager().getDispatcher();
        LockStatsCommand.register(dispatcher);
        RecalculateStatsCommand.register(dispatcher);
        SGearPartsCommand.register(dispatcher);
        if (SilentGear.isDevBuild()) {
            GradeTestCommand.register(dispatcher);
        }
    }

    private static void serverStarted(FMLServerStartedEvent event) {
        SilentGear.LOGGER.info(PartManager.MARKER, "Total gear parts loaded: {}", PartManager.getValues().size());
    }

    static class Client extends SideProxy {
        Client() {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(Client::clientSetup);

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
    }

    static class Server extends SideProxy {
        Server() {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(this::serverSetup);
        }

        private void serverSetup(FMLDedicatedServerSetupEvent event) { }
    }
}
