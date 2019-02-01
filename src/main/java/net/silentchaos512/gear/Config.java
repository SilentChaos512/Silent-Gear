package net.silentchaos512.gear;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.config.ConfigOptionEquipment;
import net.silentchaos512.gear.init.ModItems;
import net.silentchaos512.gear.item.blueprint.BlueprintType;
import net.silentchaos512.gear.util.IAOETool;
import net.silentchaos512.lib.util.EnumUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static net.minecraftforge.fml.Logging.CORE;
import static net.minecraftforge.fml.loading.LogMarkers.FORGEMOD;

public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final General GENERAL = new General(BUILDER);

    public static class General {
        // Blueprints
        public final Supplier<BlueprintType> blueprintTypes;
        public final BooleanValue spawnWithStarterBlueprints;
        // Block placers
        final ConfigValue<List<? extends String>> placerTools;
        final ConfigValue<List<? extends String>> placeableItems;
        // Sinew
        public final DoubleValue sinewDropRate;
        final ConfigValue<List<? extends String>> sinewAnimals;
        // Gear
        public final Supplier<IAOETool.MatchMode> matchModeStandard;
        public final Supplier<IAOETool.MatchMode> matchModeOres;
        public final BooleanValue gearBreaksPermanently;
        public final DoubleValue repairFactorAnvil;
        public final DoubleValue repairFactorQuick;
        public final BooleanValue upgradesInAnvilOnly;
        // Salvager
        public final DoubleValue salvagerMinLossRate;
        public final DoubleValue salvagerMaxLossRate;

        General(ForgeConfigSpec.Builder builder) {
            builder.comment("Settings related to items");
            builder.push("items");

            builder.comment("Blueprint and template settings");
            builder.push("blueprints");

            builder.comment("Allowed blueprint types. Valid values are: BOTH, BLUEPRINT, and TEMPLATE");
            blueprintTypes = EnumUtils.defineEnumFix(builder, "typesAllowed", BlueprintType.BOTH);

            spawnWithStarterBlueprints = builder
                    .comment("When joining a new world, should players be given a blueprint package?",
                            "The blueprint package gives some blueprints when used (right-click).",
                            "To change what is given, override the starter_blueprints loot table.")
                    .define("spawnWithStarterBlueprints", true);

            builder.pop(); // blueprints

            builder.comment("Silent Gear allows some items to be used to place blocks.",
                    "You can change which items place blocks and what other items they can activate.");
            builder.push("blockPlacers");

            placerTools = builder
                    .comment("These items are able to place blocks. The player must be sneaking.")
                    .defineList("placerTools",
                            ImmutableList.of(
                                    "silentgear:axe",
                                    "silentgear:pickaxe",
                                    "silentgear:shovel"
                            ),
                            o -> o instanceof String && ResourceLocation.makeResourceLocation((String) o) != null);
            placeableItems = builder
                    .comment("These items can be used by placer tools. The player must be sneaking.",
                            "Note that some items may not work with this feature.")
                    .defineList("placeableItems",
                            ImmutableList.of(
                                    "danknull:dank_null",
                                    "torchbandolier:torch_bandolier",
                                    "xreliquary:sojourner_staff"
                            ),
                            o -> o instanceof String && ResourceLocation.makeResourceLocation((String) o) != null);

            builder.pop(); // blockPlacers

            builder.comment("Settings for sinew drops");
            builder.push("sinew");

            sinewDropRate = builder
                    .comment("Drop rate of sinew (chance out of 1)")
                    .defineInRange("dropRate", 0.2, 0, 1);
            sinewAnimals = builder
                    .comment("These entities can drop sinew when killed.")
                    .defineList("dropsFrom",
                            ImmutableList.of(
                                    "minecraft:cow",
                                    "minecraft:pig",
                                    "minecraft:sheep"
                            ),
                            o -> o instanceof String && ResourceLocation.makeResourceLocation((String) o) != null);

            builder.pop(); // sinew

            builder.comment("Settings for gear (tools, weapons, and armor)");
            builder.push("gear");

            builder.comment("Settings for AOE tools (hammer, excavator)",
                    "Match modes determine what blocks are considered similar enough to be mined together.",
                    "LOOSE: Break anything (you probably do not want this)",
                    "MODERATE: Break anything with the same harvest level",
                    "STRICT: Break only the exact same block");
            builder.push("aoeTools");

            builder.comment("Match mode for most blocks");
            matchModeStandard = EnumUtils.defineEnumFix(builder, "matchModeStandard", IAOETool.MatchMode.MODERATE);

            builder.comment("Match mode for ore blocks (anything in the forge:ores block tag)");
            matchModeOres = EnumUtils.defineEnumFix(builder, "matchModeOres", IAOETool.MatchMode.STRICT);

            builder.pop(); // aoeTools

            gearBreaksPermanently = builder
                    .comment("If true, gear breaks permanently, like vanilla tools and armor")
                    .define("breaksPermanently", false);
            repairFactorAnvil = builder
                    .comment("Effectiveness of gear repairs done in an anvil. Set to 0 to disable anvil repairs.")
                    .defineInRange("repairFactorAnvil", 0.5, 0, 1);
            repairFactorQuick = builder
                    .comment("Effectiveness of quick gear repairs (crafting grid). Set to 0 to disable quick repairs.")
                    .defineInRange("repairFactorQuick", 0.35, 0, 1);
            upgradesInAnvilOnly = builder
                    .comment("If true, upgrade parts may only be applied in an anvil.")
                    .define("upgradesInAnvilOnly", false);

            builder.pop(); //gear

            builder.pop(); // items

            builder.comment("Settings for the salvager");
            builder.push("salvager");

            salvagerMinLossRate = builder
                    .comment("Minimum rate of part loss when salvaging items. 0 = no loss, 1 = complete loss.",
                            "Rate depends on remaining durability.")
                    .defineInRange("minLossRate", 0.0, 0, 1);
            salvagerMaxLossRate = builder
                    .comment("Maximum rate of part loss when salvaging items. 0 = no loss, 1 = complete loss.",
                            "Rate depends on remaining durability.")
                    .defineInRange("maxLossRate", 0.5, 0, 1);

            builder.pop(); // salvager
        }

        public boolean isPlacerTool(ItemStack stack) {
            return isThingInList(stack.getItem(), placerTools);
        }

        public boolean isPlaceableItem(ItemStack stack) {
            return isThingInList(stack.getItem(), placeableItems);
        }

        public boolean isSinewAnimal(EntityLivingBase entity) {
            return isThingInList(entity.getType(), sinewAnimals);
        }

        private static boolean isThingInList(IForgeRegistryEntry<?> thing, ConfigValue<List<? extends String>> list) {
            ResourceLocation name = thing.getRegistryName();
            for (String str : list.get()) {
                ResourceLocation fromList = ResourceLocation.makeResourceLocation(str);
                if (fromList != null && fromList.equals(name)) {
                    return true;
                }
            }
            return false;
        }
    }

    static final ForgeConfigSpec spec = BUILDER.build();

    @SubscribeEvent
    public static void onLoad(final ModConfig.Loading configEvent) {
        SilentGear.LOGGER.debug(FORGEMOD, "Loaded config file {}", configEvent.getConfig().getFileName());
    }

    @SubscribeEvent
    public static void onFileChange(final ModConfig.ConfigReloading configEvent) {
        SilentGear.LOGGER.fatal(CORE, "Config just got changed on the file system!");
    }

    // TODO: Old stuff below, needs to be fixed

    private static List<ConfigOptionEquipment> equipmentConfigs = new ArrayList<>();
    public static ConfigOptionEquipment sword = forEquipment("sword", () -> ModItems.sword);
    public static ConfigOptionEquipment dagger = forEquipment("dagger", () -> ModItems.dagger);
    public static ConfigOptionEquipment katana = forEquipment("katana", () -> ModItems.katana);
    public static ConfigOptionEquipment machete = forEquipment("machete", () -> ModItems.machete);
    public static ConfigOptionEquipment pickaxe = forEquipment("pickaxe", () -> ModItems.pickaxe);
    public static ConfigOptionEquipment shovel = forEquipment("shovel", () -> ModItems.shovel);
    public static ConfigOptionEquipment axe = forEquipment("axe", () -> ModItems.axe);
    public static ConfigOptionEquipment hammer = forEquipment("hammer", () -> ModItems.hammer);
    public static ConfigOptionEquipment excavator = forEquipment("excavator", () -> ModItems.excavator);
    public static ConfigOptionEquipment mattock = forEquipment("mattock", () -> ModItems.mattock);
    public static ConfigOptionEquipment sickle = forEquipment("sickle", () -> ModItems.sickle);
    public static ConfigOptionEquipment bow = forEquipment("bow", () -> ModItems.bow);
    public static ConfigOptionEquipment helmet = forEquipment("helmet", () -> ModItems.helmet);
    public static ConfigOptionEquipment chestplate = forEquipment("chestplate", () -> ModItems.chestplate);
    public static ConfigOptionEquipment leggings = forEquipment("leggings", () -> ModItems.leggings);
    public static ConfigOptionEquipment boots = forEquipment("boots", () -> ModItems.boots);

    @Deprecated
    private static ConfigOptionEquipment forEquipment(String name, Supplier<? extends ICoreItem> item) {
        ConfigOptionEquipment option = new ConfigOptionEquipment(name, item);
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
}
