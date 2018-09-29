package net.silentchaos512.gear;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.silentchaos512.gear.api.stats.CommonItemStats;
import net.silentchaos512.gear.command.CommandSilentGear;
import net.silentchaos512.gear.init.ModItems;
import net.silentchaos512.gear.network.MessageExtraBlockBreak;
import net.silentchaos512.gear.util.GearGenerator;
import net.silentchaos512.lib.base.IModBase;
import net.silentchaos512.lib.network.NetworkHandlerSL;
import net.silentchaos512.lib.registry.SRegistry;
import net.silentchaos512.lib.util.I18nHelper;
import net.silentchaos512.lib.util.LogHelper;

import javax.annotation.Nonnull;
import java.util.Random;

@Mod(modid = SilentGear.MOD_ID,
        name = SilentGear.MOD_NAME,
        version = SilentGear.VERSION,
        dependencies = SilentGear.DEPENDENCIES,
        guiFactory = "net.silentchaos512.gear.client.gui.GuiFactorySGear")
@MethodsReturnNonnullByDefault
@SuppressWarnings({"unused", "WeakerAccess"})
public class SilentGear implements IModBase {
    public static final String MOD_ID = "silentgear";
    public static final String MOD_NAME = "Silent Gear";
    public static final String VERSION = "0.2.0";
    public static final String SL_VERSION = "3.0.6";
    public static final int BUILD_NUM = 0;
    public static final String DEPENDENCIES = "required-after:silentlib@[" + SL_VERSION + ",)";

    public static final String RESOURCE_PREFIX = MOD_ID + ":";

    static {
        CommonItemStats.init();
    }

    public static final Random random = new Random();
    public static final LogHelper log = new LogHelper(MOD_NAME, BUILD_NUM);
    public static final I18nHelper i18n = new I18nHelper(MOD_ID, log, true);

    public static final SRegistry registry = new SRegistry();
    public static NetworkHandlerSL network;

    public static EnumRarity RARITY_LEGENDARY;

    public static final CreativeTabs creativeTab = registry.makeCreativeTab(MOD_ID,
            () -> GearGenerator.create(ModItems.katana, 3));

    @Instance(MOD_ID)
    public static SilentGear instance;

    @SidedProxy(clientSide = "net.silentchaos512.gear.ClientProxy", serverSide = "net.silentchaos512.gear.CommonProxy")
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        registry.setMod(this);
        registry.setDefaultCreativeTab(creativeTab);

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

    @EventHandler
    public void onServerLoad(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandSilentGear());
    }

    @Nonnull
    @Override
    public String getModId() {
        return MOD_ID;
    }

    @Nonnull
    @Override
    public String getModName() {
        return MOD_NAME;
    }

    @Nonnull
    @Override
    public String getVersion() {
        return VERSION;
    }

    @Override
    public int getBuildNum() {
        return BUILD_NUM;
    }

    @Override
    public LogHelper getLog() {
        return log;
    }
}
