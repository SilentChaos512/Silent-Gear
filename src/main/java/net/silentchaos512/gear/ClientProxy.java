package net.silentchaos512.gear;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.silentchaos512.gear.client.ColorHandlers;
import net.silentchaos512.gear.client.DebugOverlay;
import net.silentchaos512.gear.client.KeyTracker;
import net.silentchaos512.gear.client.event.ExtraBlockBreakHandler;
import net.silentchaos512.gear.client.models.ArmorItemModel;
import net.silentchaos512.gear.client.models.ToolHeadModel;
import net.silentchaos512.gear.client.models.ToolModel;
import net.silentchaos512.gear.compat.jei.JeiPlugin;
import net.silentchaos512.gear.init.ModBlocks;
import net.silentchaos512.lib.registry.SRegistry;

public class ClientProxy extends CommonProxy {
    @Override
    public void preInit(SRegistry registry, FMLPreInitializationEvent event) {
        super.preInit(registry, event);
        registry.clientPreInit(event);

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(KeyTracker.INSTANCE);

        ModelLoaderRegistry.registerLoader(ToolHeadModel.Loader.INSTANCE);
        ModelLoaderRegistry.registerLoader(ToolModel.Loader.INSTANCE);
        ModelLoaderRegistry.registerLoader(ArmorItemModel.Loader.INSTANCE);

        if (SilentGear.isDevBuild()) {
            MinecraftForge.EVENT_BUS.register(new DebugOverlay());
        }
    }

    @Override
    public void init(SRegistry registry, FMLInitializationEvent event) {
        super.init(registry, event);
        registry.clientInit(event);

        MinecraftForge.EVENT_BUS.register(ExtraBlockBreakHandler.INSTANCE);

        ColorHandlers.init();

        ModBlocks.netherwoodLeaves.setGraphicsLevel(Minecraft.getMinecraft().gameSettings.fancyGraphics);
    }

    @Override
    public void postInit(SRegistry registry, FMLPostInitializationEvent event) {
        super.postInit(registry, event);
        registry.clientPostInit(event);
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (Loader.isModLoaded("jei")) {
            if (JeiPlugin.hasInitFailed()) {
                String msg = "The JEI plugin seems to have failed. Some recipes may not be visible. Please report with a copy of your log file.";
                SilentGear.LOGGER.error(msg);
                event.player.sendMessage(new TextComponentString(TextFormatting.RED + "[Silent Gear] " + msg));
            } else {
                SilentGear.LOGGER.info("JEI plugin seems to have loaded correctly.");
            }
        } else {
            SilentGear.LOGGER.info("JEI is not installed?");
        }
    }
}
