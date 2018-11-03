package net.silentchaos512.gear;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
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
import net.silentchaos512.gear.api.item.ICoreTool;
import net.silentchaos512.gear.client.ColorHandlers;
import net.silentchaos512.gear.client.KeyTracker;
import net.silentchaos512.gear.client.event.ExtraBlockBreakHandler;
import net.silentchaos512.gear.client.models.ArmorItemModel;
import net.silentchaos512.gear.client.models.ToolHeadModel;
import net.silentchaos512.gear.client.models.ToolModel;
import net.silentchaos512.gear.compat.jei.JeiPlugin;
import net.silentchaos512.gear.event.GearEvents;
import net.silentchaos512.gear.init.ModTraits;
import net.silentchaos512.gear.util.TraitHelper;
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

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(KeyTracker.INSTANCE);

        ModelLoaderRegistry.registerLoader(ToolHeadModel.Loader.INSTANCE);
        ModelLoaderRegistry.registerLoader(ToolModel.Loader.INSTANCE);
        ModelLoaderRegistry.registerLoader(ArmorItemModel.Loader.INSTANCE);

        if (SilentGear.instance.isDevBuild()) {
            MinecraftForge.EVENT_BUS.register(new DebugOverlay());
        }
    }

    @Override
    public void init(SRegistry registry, FMLInitializationEvent event) {
        super.init(registry, event);
        registry.clientInit(event);

        MinecraftForge.EVENT_BUS.register(ExtraBlockBreakHandler.INSTANCE);

        ColorHandlers.init();
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
                SilentGear.log.error(msg);
                event.player.sendMessage(new TextComponentString(TextFormatting.RED + "[Silent Gear] " + msg));
            } else {
                SilentGear.log.info("JEI plugin seems to have loaded correctly.");
            }
        } else {
            SilentGear.log.info("JEI is not installed?");
        }
    }

    private static class DebugOverlay extends DebugRenderOverlay {
        private static final int SPLIT_WIDTH = 160;
        private static final float TEXT_SCALE = 0.7f;

        @Nonnull
        @Override
        public List<String> getDebugText() {
            List<String> list = new ArrayList<>();
//            PartRegistry.getDebugLines(list);
//            list.add("GearClientHelper.modelCache=" + GearClientHelper.modelCache.size());
//            list.add("ColorHandlers.gearColorCache=" + ColorHandlers.gearColorCache.size());

            // Harvest level checks
            RayTraceResult rt = Minecraft.getMinecraft().objectMouseOver;
            if (rt != null && rt.typeOfHit == RayTraceResult.Type.BLOCK) {
                Entity renderViewEntity = Minecraft.getMinecraft().getRenderViewEntity();
                if (renderViewEntity != null) {
                    BlockPos pos = rt.getBlockPos();
                    IBlockState state = renderViewEntity.world.getBlockState(pos);

                    EntityPlayerSP player = Minecraft.getMinecraft().player;
                    ItemStack heldItem = player.getHeldItem(EnumHand.MAIN_HAND);
                    if (heldItem.getItem() instanceof ICoreTool) {
                        String toolClass = state.getBlock().getHarvestTool(state);
                        if (toolClass == null) toolClass = "";
                        final int blockLevel = state.getBlock().getHarvestLevel(state);
                        final int toolLevel = heldItem.getItem().getHarvestLevel(heldItem, toolClass, player, state);

                        final boolean canHarvest = toolLevel >= blockLevel;
                        TextFormatting format = canHarvest ? TextFormatting.GREEN : TextFormatting.RED;
                        list.add(format + String.format("%s=%d (%d)", toolClass, blockLevel, toolLevel));

                        final float destroySpeed = heldItem.getDestroySpeed(state);
                        if (canHarvest) {
                            int level = TraitHelper.getTraitLevel(heldItem, ModTraits.speedBoostLight);
                            float light = GearEvents.getAreaLightBrightness(player.world, player.getPosition());
                            final float newSpeed = destroySpeed + 3 * level * light;
                            list.add(String.format("speed = %.1f", newSpeed));
                        } else {
                            list.add(String.format("speed = %.1f", destroySpeed));
                        }
                    }
                }
            }

            return list;
        }

        @Override
        public float getTextScale() {
            return TEXT_SCALE;
        }

        @Override
        public int getSplitWidth() {
            return SPLIT_WIDTH;
        }

        @Override
        public boolean isHidden() {
            return !SilentGear.instance.isDevBuild();
        }
    }
}
