package net.silentchaos512.gear;

import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.silentchaos512.gear.api.parts.PartRegistry;
import net.silentchaos512.gear.client.ColorHandlers;
import net.silentchaos512.gear.client.KeyTracker;
import net.silentchaos512.gear.client.event.ExtraBlockBreakHandler;
import net.silentchaos512.gear.client.event.TooltipHandler;
import net.silentchaos512.gear.client.models.ArmorItemModel;
import net.silentchaos512.gear.client.models.ToolHeadModel;
import net.silentchaos512.gear.client.models.ToolModel;
import net.silentchaos512.gear.client.renderer.TEISREquipment;
import net.silentchaos512.gear.client.util.GearClientHelper;
import net.silentchaos512.gear.init.ModItems;
import net.silentchaos512.lib.client.gui.DebugRenderOverlay;
import net.silentchaos512.lib.registry.SRegistry;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(SRegistry registry, FMLPreInitializationEvent event) {
        super.preInit(registry, event);
        registry.clientPreInit(event);

        MinecraftForge.EVENT_BUS.register(KeyTracker.INSTANCE);
        MinecraftForge.EVENT_BUS.register(TooltipHandler.INSTANCE);

        ModelLoaderRegistry.registerLoader(ToolHeadModel.Loader.INSTANCE);
        ModelLoaderRegistry.registerLoader(ToolModel.Loader.INSTANCE);
        ModelLoaderRegistry.registerLoader(ArmorItemModel.Loader.INSTANCE);

        if (0 == SilentGear.instance.getBuildNum()) {
            MinecraftForge.EVENT_BUS.register(new DebugRenderOverlay() {
                @Nonnull
                @Override
                public List<String> getDebugText() {
                    List<String> list = new ArrayList<>();
                    PartRegistry.getDebugLines(list);
                    list.add("GearClientHelper.modelCache=" + GearClientHelper.modelCache.size());
                    list.add("ColorHandlers.gearColorCache=" + ColorHandlers.gearColorCache.size());
                    return list;
                }

                @Override
                public float getTextScale() {
                    return 0.7f;
                }

                @Override
                public int getSplitWidth() {
                    return 160;
                }

                @Override
                public boolean isHidden() {
                    return false;
                }
            });
        }
    }

    @Override
    public void init(SRegistry registry, FMLInitializationEvent event) {
        super.init(registry, event);
        registry.clientInit(event);

        MinecraftForge.EVENT_BUS.register(ExtraBlockBreakHandler.INSTANCE);

        ColorHandlers.init();
        ModItems.toolClasses.values().forEach(item -> item.getItem().setTileEntityItemStackRenderer(TEISREquipment.INSTANCE));
        ModItems.armorClasses.values().forEach(item -> item.getItem().setTileEntityItemStackRenderer(TEISREquipment.INSTANCE));
    }

    @Override
    public void postInit(SRegistry registry, FMLPostInitializationEvent event) {
        super.postInit(registry, event);
        registry.clientPostInit(event);
    }
}
