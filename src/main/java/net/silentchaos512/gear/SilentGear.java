package net.silentchaos512.gear;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLLoader;
import net.silentchaos512.gear.compat.curios.CuriosCompat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Random;

@Mod(SilentGear.MOD_ID)
public final class SilentGear {
    public static final String MOD_ID = "silentgear";
    public static final String MOD_NAME = "Silent Gear";

    public static final String RESOURCE_PREFIX = MOD_ID + ":";

    public static final Random RANDOM = new Random();
    public static final RandomSource RANDOM_SOURCE = RandomSource.create();
    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

    public static SilentGear INSTANCE;
    public static IProxy PROXY;

    public SilentGear(IEventBus modEventBus, ModContainer modContainer) {
        INSTANCE = this;
        PROXY = FMLEnvironment.dist == Dist.CLIENT
                ? new SideProxy.Client(modEventBus)
                : new SideProxy.Server(modEventBus);

        modContainer.registerConfig(ModConfig.Type.SERVER, Config.Common.SPEC);
        modContainer.registerConfig(ModConfig.Type.CLIENT, Config.Client.SPEC);

        if (ModList.get().isLoaded("curios")) {
            CuriosCompat.registerEventHandlers(modEventBus);
        }
    }

    public static String getVersion() {
        Optional<? extends ModContainer> o = ModList.get().getModContainerById(MOD_ID);
        if (o.isPresent()) {
            return o.get().getModInfo().getVersion().toString();
        }
        return "0.0.0";
    }

    @Deprecated
    public static String getVersion(boolean correctInDev) {
        return getVersion();
    }

    public static boolean isDevBuild() {
        return "NONE".equals(getVersion()) || !FMLLoader.isProduction();
    }

    public static ResourceLocation getId(String path) {
        if (path.contains(":")) {
            if (path.startsWith(SilentGear.MOD_ID)) {
                return ResourceLocation.tryParse(path);
            } else {
                throw new IllegalArgumentException("path contains namespace other than " + SilentGear.MOD_ID);
            }
        }
        return ResourceLocation.fromNamespaceAndPath(SilentGear.MOD_ID, path);
    }

    @Nullable
    public static ResourceLocation getIdWithDefaultNamespace(String name) {
        if (name.contains(":"))
            return ResourceLocation.tryParse(name);
        return ResourceLocation.tryParse(RESOURCE_PREFIX + name);
    }

    public static String shortenId(@Nullable ResourceLocation id) {
        if (id == null)
            return "null";
        if (MOD_ID.equals(id.getNamespace()))
            return id.getPath();
        return id.toString();
    }
}
