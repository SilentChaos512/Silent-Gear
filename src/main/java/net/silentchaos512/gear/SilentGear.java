package net.silentchaos512.gear;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.item.CraftingItems;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;
import java.util.Random;

@Mod(SilentGear.MOD_ID)
public final class SilentGear {
    public static final String MOD_ID = "silentgear";
    public static final String MOD_NAME = "Silent Gear";
    public static final String VERSION = "1.2.0";

    public static final String RESOURCE_PREFIX = MOD_ID + ":";

    static {
        ItemStats.init();
    }

    public static final Random random = new Random();
    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

    public static SilentGear INSTANCE;
    public static IProxy PROXY;

    @SuppressWarnings("Convert2MethodRef")
    public SilentGear() {
        INSTANCE = this;
        PROXY = DistExecutor.runForDist(() -> () -> new SideProxy.Client(), () -> () -> new SideProxy.Server());
    }

    public static String getVersion() {
        return getVersion(false);
    }

    public static String getVersion(boolean correctInDev) {
        Optional<? extends ModContainer> o = ModList.get().getModContainerById(MOD_ID);
        if (o.isPresent()) {
            String str = o.get().getModInfo().getVersion().toString();
            if (correctInDev && "NONE".equals(str))
                return VERSION;
            return str;
        }
        return "0.0.0";
    }

    public static String getLongVersion() {
        return "1.14.4-" + getVersion();
    }

    public static boolean isDevBuild() {
        // TODO: Is there a better way? Guess it works though...
        String version = getVersion(false);
        return "NONE".equals(version);
    }

    public static ResourceLocation getId(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    public static final ItemGroup ITEM_GROUP = new ItemGroup(MOD_ID) {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(CraftingItems.BLUEPRINT_PAPER);
        }
    };
}
