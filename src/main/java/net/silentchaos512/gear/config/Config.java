package net.silentchaos512.gear.config;

import com.google.common.collect.ImmutableSet;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.init.ModItems;
import net.silentchaos512.gear.util.IAOETool;
import net.silentchaos512.lib.collection.EntityMatchList;
import net.silentchaos512.lib.collection.ItemMatchList;
import net.silentchaos512.lib.config.ConfigBaseNew;
import net.silentchaos512.lib.config.ConfigOption;
import net.silentchaos512.lib.util.I18nHelper;
import net.silentchaos512.lib.util.LogHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Config extends ConfigBaseNew {
    public static final Config INSTANCE = new Config();

    public static final String CAT_ITEMS = "items";
    public static final String CAT_GEAR = CAT_ITEMS + SEP + "gear";
    public static final String CAT_NERFED_GEAR = CAT_ITEMS + SEP + "nerfed_gear";
    public static final String CAT_SINEW = CAT_ITEMS + SEP + "sinew";

    /*
     * Items
     */

    @ConfigOption(name = "Spawn With Starter Blueprints", category = CAT_ITEMS)
    @ConfigOption.BooleanDefault(true)
    @ConfigOption.Comment("Spawn players with a blueprint package containing some starter blueprints (set false to disable). This uses the starter_blueprints loot table.")
    public static boolean spawnWithStarterBlueprints;

    @ConfigOption(name = "Drop Rate", category = CAT_SINEW)
    @ConfigOption.RangeFloat(value = 0.2f, min = 0f, max = 1f)
    @ConfigOption.Comment("The probability an animal will drop sinew.")
    public static float sinewDropRate;

    @ConfigOption(name = "Upgrades Anvil Only", category = CAT_GEAR)
    @ConfigOption.BooleanDefault(false)
    @ConfigOption.Comment("If enabled, upgrades (like tip upgrades) can only be applied in anvils.")
    public static boolean upgradesAnvilOnly;

    @ConfigOption(name = "Repair Factor Anvil", category = CAT_GEAR)
    @ConfigOption.RangeFloat(value = 0.5f, min = 0f, max = 1f)
    @ConfigOption.Comment("The effectiveness of anvil repairs (based on material durability). Set to 0 to disable.")
    public static float anvilRepairFactor;

    @ConfigOption(name = "Repair Factor Quick", category = CAT_GEAR)
    @ConfigOption.RangeFloat(value = 0.35f, min = 0f, max = 1f)
    @ConfigOption.Comment("The effectiveness of quick repairs (based on material durability). Set to 0 to disable.")
    public static float quickRepairFactor;

    @ConfigOption(name = "Gear Breaks Permanently", category = CAT_GEAR)
    @ConfigOption.BooleanDefault(false)
    @ConfigOption.Comment("If enabled, tools/weapons/armor are destroyed when broken, just like vanilla.")
    public static boolean gearBreaksPermanently;

    public static IAOETool.MatchMode aoeToolMatchMode = IAOETool.MatchMode.MODERATE;
    public static IAOETool.MatchMode aoeToolOreMode = IAOETool.MatchMode.STRICT;

    public static EntityMatchList sinewAnimals = new EntityMatchList(true, false,
            "minecraft:cow", "minecraft:sheep", "minecraft:pig");
    private static final String SINEW_ANIMALS_COMMENT = "These entities can drop sinew. It is not restricted to animals.";

    public static ItemMatchList blockPlacerTools = new ItemMatchList(true, false,
            SilentGear.RESOURCE_PREFIX + "pickaxe", SilentGear.RESOURCE_PREFIX + "shovel", SilentGear.RESOURCE_PREFIX + "axe");
    private static final String BLOCK_PLACER_TOOLS_COMMENT = "These items will be able to place blocks by using them (right-click-to-place)";

    public static ItemMatchList itemsThatToolsCanUse = new ItemMatchList(true, false,
            "danknull:dank_null", "xreliquary:sojourner_staff", "torchbandolier:torch_bandolier");
    private static final String ITEMS_THAT_TOOLS_CAN_USE_COMMENT = "Items that block-placing tools can \"use\" by simulating a right-click.";

    /*
     * Nerfed gear
     */

    public static Set<String> nerfedGear;
    private static final String NERFED_GEAR_COMMENT = "These items will have reduced durability to discourage use, but they can still be crafted and used as normal. Items from other mods can be added to the list, but I cannot guarantee their durability will actually change.";

    @ConfigOption(name = "Durability Multiplier", category = CAT_NERFED_GEAR)
    @ConfigOption.RangeFloat(value = 0.5f, min = 0f, max = 1f)
    @ConfigOption.Comment("The durability of items in the nerfed gear list will be multiplied by this value.")
    public static float nerfedGearMulti;

    /*
     * Tools
     */

    private static List<ConfigOptionEquipment> equipmentConfigs = new ArrayList<>();
    public static ConfigOptionEquipment sword = forEquipment(ModItems.sword);
    public static ConfigOptionEquipment dagger = forEquipment(ModItems.dagger);
    public static ConfigOptionEquipment katana = forEquipment(ModItems.katana);
    public static ConfigOptionEquipment machete = forEquipment(ModItems.machete);
    public static ConfigOptionEquipment pickaxe = forEquipment(ModItems.pickaxe);
    public static ConfigOptionEquipment shovel = forEquipment(ModItems.shovel);
    public static ConfigOptionEquipment axe = forEquipment(ModItems.axe);
    public static ConfigOptionEquipment hammer = forEquipment(ModItems.hammer);
    public static ConfigOptionEquipment excavator = forEquipment(ModItems.excavator);
    public static ConfigOptionEquipment mattock = forEquipment(ModItems.mattock);
    public static ConfigOptionEquipment sickle = forEquipment(ModItems.sickle);
    public static ConfigOptionEquipment bow = forEquipment(ModItems.bow);
    public static ConfigOptionEquipment helmet = forEquipment(ModItems.helmet);
    public static ConfigOptionEquipment chestplate = forEquipment(ModItems.chestplate);
    public static ConfigOptionEquipment leggings = forEquipment(ModItems.leggings);
    public static ConfigOptionEquipment boots = forEquipment(ModItems.boots);

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
    public I18nHelper i18n() {
        return SilentGear.i18n;
    }

    @Override
    public LogHelper log() {
        return SilentGear.log;
    }

    @Override
    public void load() {
        try {
            super.load();

            /*
             * Items
             */

            // Sinew
            sinewAnimals.loadConfig(config, "Animals That Drop Sinew", CAT_SINEW, SINEW_ANIMALS_COMMENT);
            // Block placer tools
            blockPlacerTools.loadConfig(config, "Items That Place Blocks", CAT_ITEMS, BLOCK_PLACER_TOOLS_COMMENT);
            itemsThatToolsCanUse.loadConfig(config, "Items That Block Placer Tools Can Use", CAT_ITEMS, ITEMS_THAT_TOOLS_CAN_USE_COMMENT);

            aoeToolMatchMode = loadEnum("AOE Tool Match Mode", CAT_GEAR, IAOETool.MatchMode.class, IAOETool.MatchMode.MODERATE,
                    "Block matching mode for hammers and excavators. LOOSE will break any blocks the tool can harvest" +
                            " together (bit OP with blocks like obsidian), MODERATE will break blocks of similar" +
                            " harvest levels, and STRICT will only mine one block type.");
            aoeToolOreMode = loadEnum("AOE Tool Ore Match Mode", CAT_GEAR, IAOETool.MatchMode.class, IAOETool.MatchMode.STRICT,
                    "Ore matching mode for hammers and excavators, overrides standard match mode if both blocks are" +
                            " ores. LOOSE will break anything, MODERATE will break the same harvest level or lower," +
                            " STRICT will break only the same block type.");

            /*
             * Nerfed gear
             */

            config.setCategoryComment(CAT_NERFED_GEAR, "Settings for nerfing gear from vanilla or other mods.");
            config.setCategoryRequiresMcRestart(CAT_NERFED_GEAR, true);

            String[] nerfedItems = config.getStringList("Nerfed Gear List", CAT_NERFED_GEAR, getDefaultNerfedGear(), NERFED_GEAR_COMMENT);
            nerfedGear = ImmutableSet.copyOf(nerfedItems);

            /*
             * Tools
             */

            for (ConfigOptionEquipment option : equipmentConfigs)
                option.loadValue(config);

            // Grab last build number for potential changes?
            int currentBuild = SilentGear.instance.getBuildNum();
            int lastBuild = config.get("last_version", "last_build", currentBuild).getInt(currentBuild);
            // TODO
        } catch (Exception ex) {
            SilentGear.log.fatal("Could not load configuration file! This could end badly...");
            SilentGear.log.catching(ex);
        }
    }

    @Override
    public void save() {
        super.save();

        int buildNum = SilentGear.instance.getBuildNum();
        config.get("last_version", "last_build", buildNum).setValue(buildNum);
    }

    private static ConfigOptionEquipment forEquipment(ICoreItem item) {
        ConfigOptionEquipment option = new ConfigOptionEquipment(item);
        equipmentConfigs.add(option);
        return option;
    }

    private static String[] getDefaultNerfedGear() {
        Set<String> toolTypes = ImmutableSet.of("pickaxe", "shovel", "axe", "sword");
        Set<String> toolMaterials = ImmutableSet.of("wooden", "stone", "iron", "golden", "diamond");
        List<String> items = toolTypes.stream()
                .flatMap(type -> toolMaterials.stream()
                        .map(material -> "minecraft:" + material + "_" + type))
                .collect(Collectors.toList());

        Set<String> armorTypes = ImmutableSet.of("helmet", "chestplate", "leggings", "boots");
        Set<String> armorMaterials = ImmutableSet.of("leather", "chainmail", "iron", "diamond", "golden");
        items.addAll(armorTypes.stream()
                .flatMap(type -> armorMaterials.stream()
                        .map(material -> "minecraft:" + material + "_" + type))
                .collect(Collectors.toList()));

        return items.toArray(new String[0]);
    }

    public File getDirectory() {
        return directory;
    }
}
