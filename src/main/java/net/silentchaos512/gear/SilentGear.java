package net.silentchaos512.gear;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.silentchaos512.gear.api.stats.CommonItemStats;
import net.silentchaos512.gear.command.CommandSilentGear;
import net.silentchaos512.gear.item.CraftingItems;
import net.silentchaos512.gear.network.MessageExtraBlockBreak;
import net.silentchaos512.lib.network.NetworkHandlerSL;
import net.silentchaos512.lib.registry.SRegistry;
import net.silentchaos512.lib.util.GameUtil;
import net.silentchaos512.lib.util.I18nHelper;
import net.silentchaos512.lib.util.LogHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

@Mod(modid = SilentGear.MOD_ID,
        name = SilentGear.MOD_NAME,
        version = SilentGear.VERSION,
        dependencies = SilentGear.DEPENDENCIES,
        guiFactory = "net.silentchaos512.gear.client.gui.GuiFactorySGear")
@MethodsReturnNonnullByDefault
@SuppressWarnings({"unused", "WeakerAccess"})
public class SilentGear {
    public static final String MOD_ID = "silentgear";
    public static final String MOD_NAME = "Silent Gear";
    public static final String VERSION = "0.6.2";
    public static final String SL_VERSION = "3.0.13";
    public static final int BUILD_NUM = 0;
    public static final String DEPENDENCIES = "required-after:silentlib@[" + SL_VERSION + ",)";

    public static final String RESOURCE_PREFIX = MOD_ID + ":";

    static {
        CommonItemStats.init();
    }

    public static final Random RANDOM = new Random();
    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

    @Deprecated
    public static final LogHelper log = new LogHelper(MOD_NAME, BUILD_NUM);
    @Deprecated
    public static final I18nHelper i18n = new I18nHelper(MOD_ID, log, true);
    @Deprecated
    public static final SRegistry registry = new SRegistry();

    public static NetworkHandlerSL network;

    public static EnumRarity RARITY_LEGENDARY;

    @Instance(MOD_ID)
    public static SilentGear instance;

    @SidedProxy(clientSide = "net.silentchaos512.gear.ClientProxy", serverSide = "net.silentchaos512.gear.CommonProxy")
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        registry.setMod(this);
        registry.setDefaultCreativeTab(ITEM_GROUP);

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

    public static String getVersion() {
        return getVersion(false);
    }

    public static String getVersion(boolean correctInDev) {
        ModContainer o = Loader.instance().getIndexedModList().get(MOD_ID);
        if (o != null) {
            String str = o.getDisplayVersion();
            if (correctInDev && "NONE".equals(str))
                return VERSION;
            return str;
        }
        return "0.0.0";
    }

    public static boolean isDevBuild() {
        //noinspection CallToSimpleGetterFromWithinClass
        return getBuildNum() == 0 || GameUtil.isDeobfuscated();
    }

    public static int getBuildNum() {
        return BUILD_NUM;
    }

    public static ResourceLocation getId(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    public static final CreativeTabs ITEM_GROUP = new CreativeTabs(MOD_ID) {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(CraftingItems.BLUEPRINT_PAPER.getItem());
        }
    };
}
