package net.silentchaos512.gear.config;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.init.ModItems;
import net.silentchaos512.lib.collection.EntityMatchList;
import net.silentchaos512.lib.config.ConfigBase;
import net.silentchaos512.lib.config.ConfigMultiValueLineParser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Config extends ConfigBase {

    public static final Config INSTANCE = new Config();

    public static final String CAT_ITEMS = "items";
    public static final String CAT_TOOLS = CAT_ITEMS + SEP + "tools";

    /*
     * Items
     */
    public static EntityMatchList sinewAnimals = new EntityMatchList();
    private static final String[] SINEW_ANIMALS_DEFAULT = {"minecraft:cow", "minecraft:sheep", "minecraft:pig"};
    private static final String SINEW_ANIMALS_COMMENT = "These entities can drop sinew. It is not restricted to animals.";

    public static float sinewDropRate;
    private static final float SINEW_DROP_RATE_DEFAULT = 0.2f;
    private static final String SINEW_DROP_RATE_COMMENT = "The probability an animal will drop sinew.";

    /*
     * Tools
     */

    private static List<ConfigOptionEquipment> equipmentConfigs = new ArrayList<>();
    public static ConfigOptionEquipment sword = forEquipment(ModItems.sword);
    public static ConfigOptionEquipment pickaxe = forEquipment(ModItems.pickaxe);
    public static ConfigOptionEquipment shovel = forEquipment(ModItems.shovel);
    public static ConfigOptionEquipment axe = forEquipment(ModItems.axe);
    public static ConfigOptionEquipment hammer = forEquipment(ModItems.hammer);
    public static ConfigOptionEquipment mattock = forEquipment(ModItems.mattock);
    public static ConfigOptionEquipment bow = forEquipment(ModItems.bow);
    public static ConfigOptionEquipment helmet = forEquipment(ModItems.helmet);
    public static ConfigOptionEquipment chestplate = forEquipment(ModItems.chestplate);
    public static ConfigOptionEquipment leggings = forEquipment(ModItems.leggings);
    public static ConfigOptionEquipment boots = forEquipment(ModItems.boots);

    public static boolean toolsBreakPermanently;
    private static final boolean TOOLS_BREAK_DEFAULT = false;
    private static final String TOOLS_BREAK_COMMENT = "If enabled, tools/weapons/armor are destroyed when broken, just like vanilla.";

    File directory;

    public Config() {
        super(SilentGear.MOD_ID);
    }

    public void onPreInit(FMLPreInitializationEvent event) {
        this.directory = new File(event.getModConfigurationDirectory().getPath(), "silentchaos512/" + SilentGear.MOD_ID + "/");
        config = new Configuration(new File(directory.getPath(), SilentGear.MOD_ID + ".cfg"));
        new File(directory.getPath(), "materials/").mkdirs();
        new File(directory.getPath(), "equipment/").mkdirs();
    }

    @Override
    public void init(File file) {
        load();
    }

    @Override
    public void load() {
        try {
            ConfigMultiValueLineParser parser;

            /*
             * Items
             */

            // Sinew
            String catSinew = CAT_ITEMS + SEP + "sinew";
            sinewAnimals.loadConfig(config, "Animals That Drop Sinew", catSinew, SINEW_ANIMALS_DEFAULT, true, false, SINEW_ANIMALS_COMMENT);
            sinewDropRate = loadFloat("Drop Rate", catSinew, SINEW_DROP_RATE_DEFAULT, SINEW_DROP_RATE_COMMENT);

            /*
             * Tools
             */

            for (ConfigOptionEquipment option : equipmentConfigs)
                option.loadValue(config);

            toolsBreakPermanently = loadBoolean("Equipment Breaks Permanently", CAT_ITEMS, TOOLS_BREAK_DEFAULT, TOOLS_BREAK_COMMENT);
        } catch (Exception ex) {
            SilentGear.log.severe("Could not load configuration file!");
            ex.printStackTrace();
        }
    }

    private static ConfigOptionEquipment forEquipment(ICoreItem item) {
        ConfigOptionEquipment option = new ConfigOptionEquipment(item);
        equipmentConfigs.add(option);
        return option;
    }

    public File getDirectory() {
        return directory;
    }
}
