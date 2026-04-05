package net.silentchaos512.gear;

import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;
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
        PROXY = FMLEnvironment.getDist() == Dist.CLIENT
                ? new SideProxy.Client(modEventBus, modContainer)
                : new SideProxy.Server(modEventBus, modContainer);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.Common.SPEC);
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

    public static boolean isDevBuild() {
        return "NONE".equals(getVersion()) || !FMLEnvironment.isProduction();
    }

    public static Identifier getId(String path) {
        if (path.contains(":")) {
            if (path.startsWith(SilentGear.MOD_ID)) {
                return Identifier.tryParse(path);
            } else {
                throw new IllegalArgumentException("path contains namespace other than " + SilentGear.MOD_ID);
            }
        }
        return Identifier.fromNamespaceAndPath(SilentGear.MOD_ID, path);
    }

    public static Identifier getIdWithModNamespaceAsDefault(String path) {
        if (path.contains(":")) {
            return Identifier.tryParse(path);
        }
        return Identifier.fromNamespaceAndPath(SilentGear.MOD_ID, path);
    }

    @Nullable
    public static Identifier getIdWithDefaultNamespace(String name) {
        if (name.contains(":"))
            return Identifier.tryParse(name);
        return Identifier.tryParse(RESOURCE_PREFIX + name);
    }

    public static String shortenId(@Nullable Identifier id) {
        if (id == null)
            return "null";
        if (MOD_ID.equals(id.getNamespace()))
            return id.getPath();
        return id.toString();
    }
}
