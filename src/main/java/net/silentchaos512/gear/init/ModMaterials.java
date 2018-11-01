package net.silentchaos512.gear.init;

import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.parts.*;
import net.silentchaos512.gear.config.Config;
import net.silentchaos512.gear.item.MiscUpgrades;
import net.silentchaos512.gear.item.TipUpgrades;
import net.silentchaos512.gear.item.ToolRods;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.lib.registry.IPhasedInitializer;
import net.silentchaos512.lib.registry.SRegistry;

import java.io.File;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModMaterials implements IPhasedInitializer {
    public static final ModMaterials INSTANCE = new ModMaterials();

    public static PartMain mainWood, mainStone, mainFlint, mainIron, mainGold, mainEmerald, mainDiamond, mainObsidian, mainNetherrack, mainTerracotta;
    public static PartBowstring bowstringString, bowstringSinew;

    @Override
    public void preInit(SRegistry registry, FMLPreInitializationEvent event) {
        mainWood = PartRegistry.putPart(new PartMain(getPath("main_wood"), PartOrigins.BUILTIN_CORE));
        mainStone = PartRegistry.putPart(new PartMain(getPath("main_stone"), PartOrigins.BUILTIN_CORE));
        mainFlint = PartRegistry.putPart(new PartMain(getPath("main_flint"), PartOrigins.BUILTIN_CORE));
        mainTerracotta = PartRegistry.putPart(new PartMain(getPath("main_terracotta"), PartOrigins.BUILTIN_CORE));
        mainNetherrack = PartRegistry.putPart(new PartMain(getPath("main_netherrack"), PartOrigins.BUILTIN_CORE));
        mainIron = PartRegistry.putPart(new PartMain(getPath("main_iron"), PartOrigins.BUILTIN_CORE));
        mainGold = PartRegistry.putPart(new PartMain(getPath("main_gold"), PartOrigins.BUILTIN_CORE));
        mainEmerald = PartRegistry.putPart(new PartMain(getPath("main_emerald"), PartOrigins.BUILTIN_CORE));
        mainDiamond = PartRegistry.putPart(new PartMain(getPath("main_diamond"), PartOrigins.BUILTIN_CORE));
        mainObsidian = PartRegistry.putPart(new PartMain(getPath("main_obsidian"), PartOrigins.BUILTIN_CORE));
//        PartRegistry.putPart(new PartMain(getPath("main_test")));

        for (ToolRods rod : ToolRods.values())
            PartRegistry.putPart(rod.getPart());

        for (TipUpgrades tip : TipUpgrades.values())
            PartRegistry.putPart(tip.getPart());

        for (EnumDyeColor color : EnumDyeColor.values())
            PartRegistry.putPart(new PartGrip(getPath("grip_wool_" + color.name().toLowerCase(Locale.ROOT)), PartOrigins.BUILTIN_CORE));
        PartRegistry.putPart(new PartGrip(getPath("grip_leather"), PartOrigins.BUILTIN_CORE));

        bowstringString = PartRegistry.putPart(new PartBowstring(getPath("bowstring_string"), PartOrigins.BUILTIN_CORE));
        bowstringSinew = PartRegistry.putPart(new PartBowstring(getPath("bowstring_sinew"), PartOrigins.BUILTIN_CORE));
        PartRegistry.putPart(new PartBowstring(getPath("bowstring_flax"), PartOrigins.BUILTIN_CORE));

        for (MiscUpgrades upgrade : MiscUpgrades.values())
            PartRegistry.putPart(upgrade.getPart());

        UserDefined.loadUserParts();
    }

    @Override
    public void init(SRegistry registry, FMLInitializationEvent event) {
        // Update part caches
        // All mods should have added their parts during pre-init
        PartRegistry.resetVisiblePartCaches();
        GearHelper.resetSubItemsCache();
    }

    private static ResourceLocation getPath(String key) {
        return new ResourceLocation(SilentGear.MOD_ID, key);
    }

    private static final class UserDefined {
        static void loadUserParts() {
            final File directory = new File(Config.INSTANCE.getDirectory(), "materials");
            final File[] files = directory.listFiles();
            if (!directory.isDirectory() || files == null) {
                SilentGear.log.warn("File \"{}\" is not a directory?", directory);
                return;
            }

            final Pattern typeRegex = Pattern.compile("^[a-z]+");
            for (File file : files) {
                SilentGear.log.info("Material file found: {}", file);
                String filename = file.getName().replace(".json", "");
                ResourceLocation name = getPath(filename);

                // Add to registered parts if it doesn't exist
                if (!PartRegistry.getKeySet().contains(name.toString())) {
                    Matcher match = typeRegex.matcher(filename);
                    if (match.find()) {
                        String typeName = match.group();
                        PartType type = PartType.get(typeName);
                        if (type != null) {
                            SilentGear.log.info("Trying to add part {}, typeName {}", name, typeName);
                            PartRegistry.putPart(type.construct(name, PartOrigins.USER_DEFINED));
                        } else {
                            SilentGear.log.warn("Unknown part typeName \"{}\" for {}", typeName, filename);
                        }
                    }
                } else {
                    SilentGear.log.info("Part already registered. Must be an override.");
                }
            }
        }
    }
}
