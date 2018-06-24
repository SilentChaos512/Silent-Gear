package net.silentchaos512.gear;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.silentchaos512.gear.init.ModItems;
import net.silentchaos512.gear.network.MessageExtraBlockBreak;
import net.silentchaos512.lib.SilentLib;
import net.silentchaos512.lib.network.NetworkHandlerSL;
import net.silentchaos512.lib.registry.SRegistry;
import net.silentchaos512.lib.util.LocalizationHelper;
import net.silentchaos512.lib.util.LogHelper;

import java.util.Random;

@Mod(modid = SilentGear.MOD_ID, name = SilentGear.MOD_NAME, version = "0.0.1")
public class SilentGear {

    public static final String MOD_ID = "silentgear";
    public static final String MOD_NAME = "Silent Gear";
    public static final int BUILD_NUM = 0;

    public static final String RESOURCE_PREFIX = MOD_ID + ":";

    public static Random random = new Random();
    public static LogHelper log = new LogHelper(MOD_NAME, BUILD_NUM);
    public static LocalizationHelper localization;

    public static SRegistry registry = new SRegistry(MOD_ID, log);
    public static NetworkHandlerSL network;

    public static EnumRarity RARITY_LEGENDARY;

    public static CreativeTabs creativeTab = new CreativeTabs(MOD_ID) {

        @Override
        public ItemStack getTabIconItem() {

            return new ItemStack(ModItems.blueprint);
        }
    };

    @Instance(MOD_ID)
    public static SilentGear instance;

    @SidedProxy(clientSide = "net.silentchaos512.gear.ClientProxy", serverSide = "net.silentchaos512.gear.CommonProxy")
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {

        localization = new LocalizationHelper(MOD_ID).setReplaceAmpersand(true);
        SilentLib.instance.registerLocalizationHelperForMod(MOD_ID, localization);

        network = new NetworkHandlerSL(MOD_ID);
        network.register(MessageExtraBlockBreak.class, Side.CLIENT);

        RARITY_LEGENDARY = EnumHelper.addRarity("Legendary", TextFormatting.GOLD, "Legendary");

        proxy.preInit(registry, event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {

        proxy.init(registry, event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {

        proxy.postInit(registry, event);
    }
}
