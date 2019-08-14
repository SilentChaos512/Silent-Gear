package net.silentchaos512.gear.config;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.silentchaos512.gear.api.parts.MaterialGrade;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.init.NerfedGear;
import net.silentchaos512.gear.item.blueprint.BlueprintType;
import net.silentchaos512.gear.util.IAOETool;
import net.silentchaos512.utils.config.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Config {
    private static final ConfigSpecWrapper WRAPPER_CLIENT = ConfigSpecWrapper.create(
            FMLPaths.CONFIGDIR.get().resolve("silentgear-client.toml"));
    private static final ConfigSpecWrapper WRAPPER = ConfigSpecWrapper.create(
            FMLPaths.CONFIGDIR.get().resolve("silentgear-common.toml"));

    public static final Client CLIENT = new Client(WRAPPER_CLIENT);
    public static final General GENERAL = new General(WRAPPER);

    public static class General {
        // Blueprints
        public final EnumValue<BlueprintType> blueprintTypes;
        public final BooleanValue spawnWithStarterBlueprints;
        // Block placers
        final ConfigValue<List<? extends String>> placerTools;
        final ConfigValue<List<? extends String>> placeableItems;
        // Nerfed gear
        public final BooleanValue nerfedItemsEnabled;
        public final DoubleValue nerfedItemDurabilityMulti;
        public final DoubleValue nerfedItemHarvestSpeedMulti;
        final ConfigValue<List<? extends String>> nerfedItems;
        // Sinew
        public final DoubleValue sinewDropRate;
        final ConfigValue<List<? extends String>> sinewAnimals;
        // Gear
        public final EnumValue<IAOETool.MatchMode> matchModeStandard;
        public final EnumValue<IAOETool.MatchMode> matchModeOres;
        public final BooleanValue gearBreaksPermanently;
        public final DoubleValue repairFactorAnvil;
        public final DoubleValue repairFactorQuick;
        public final BooleanValue upgradesInAnvilOnly;
        private final Map<ItemStat, DoubleValue> statMultipliers = new HashMap<>();
        // Grading
        public final EnumValue<MaterialGrade> randomGradeMean;
        public final EnumValue<MaterialGrade> randomGradeMax;
        public final DoubleValue randomGradeStd;
        public final BooleanValue randomGradeSameOnAllParts;
        // Salvager
        public final DoubleValue salvagerMinLossRate;
        public final DoubleValue salvagerMaxLossRate;
        // Compatibility
        public final BooleanValue mineAndSlashSupport;
        // Debug
        public final BooleanValue extraPartAndTraitLogging;

        General(ConfigSpecWrapper wrapper) {
            wrapper.comment("item.blueprint", "Blueprint and template settings");

            blueprintTypes = wrapper
                    .builder("item.blueprint.typesAllowed")
                    .comment("Allowed blueprint types. Valid values are: BOTH, BLUEPRINT, and TEMPLATE")
                    .defineEnum(BlueprintType.BOTH);

            spawnWithStarterBlueprints = wrapper
                    .builder("item.blueprint.spawnWithStarterBlueprints")
                    .comment("When joining a new world, should players be given a blueprint package?",
                            "The blueprint package gives some blueprints when used (right-click).",
                            "To change what is given, override the starter_blueprints loot table.")
                    .define(true);

            wrapper.comment("item.blockPlacers",
                    "Silent Gear allows some items to be used to place blocks.",
                    "You can change which items place blocks and what other items they can activate.");

            placerTools = wrapper
                    .builder("item.blockPlacers.placerTools")
                    .comment("These items are able to place blocks. The player must be sneaking.")
                    .defineList(
                            ImmutableList.of(
                                    "silentgear:axe",
                                    "silentgear:pickaxe",
                                    "silentgear:shovel"
                            ),
                            Config::isResourceLocation);
            placeableItems = wrapper
                    .builder("item.blockPlacers.placeableItems")
                    .comment("These items can be used by placer tools. The player must be sneaking.",
                            "Note that some items may not work with this feature.")
                    .defineList(
                            ImmutableList.of(
                                    "danknull:dank_null",
                                    "torchbandolier:torch_bandolier",
                                    "xreliquary:sojourner_staff"
                            ),
                            Config::isResourceLocation);

            wrapper.comment("item.nerfedItems",
                    "Settings for nerfed items.",
                    "You can give items reduced durability to encourage use of Silent Gear tools.",
                    "Changes require a restart!");

            nerfedItemsEnabled = wrapper
                    .builder("item.nerfedItems.enabled")
                    .comment("Enable this feature. If false, the other settings in this category are ignored.")
                    .define(false);
            nerfedItemDurabilityMulti = wrapper
                    .builder("item.nerfedItems.durabilityMultiplier")
                    .comment("Multiplies max durability by this value. If the result would be zero, a value of 1 is assigned.")
                    .defineInRange(0.05, 0, 1);
            nerfedItemHarvestSpeedMulti = wrapper
                    .builder("item.nerfedItems.harvestSpeedMultiplier")
                    .comment("Multiplies harvest speed by this value.")
                    .defineInRange(0.5, 0, 1);
            nerfedItems = wrapper
                    .builder("item.nerfedItems.items")
                    .comment("These items will have reduced durability")
                    .defineList(NerfedGear.DEFAULT_ITEMS, Config::isResourceLocation);

            wrapper.comment("item.sinew", "Settings for sinew drops");

            sinewDropRate = wrapper
                    .builder("item.sinew.dropRate")
                    .comment("Drop rate of sinew (chance out of 1)")
                    .defineInRange(0.2, 0, 1);
            sinewAnimals = wrapper
                    .builder("item.sinew.dropsFrom")
                    .comment("These entities can drop sinew when killed.")
                    .defineList(
                            ImmutableList.of(
                                    "minecraft:cow",
                                    "minecraft:pig",
                                    "minecraft:sheep"
                            ),
                            Config::isResourceLocation);

            wrapper.comment("item.gear", "Settings for gear (tools, weapons, and armor)");

            wrapper.comment("item.gear.aoeTools",
                    "Settings for AOE tools (hammer, excavator)",
                    "Match modes determine what blocks are considered similar enough to be mined together.",
                    "LOOSE: Break anything (you probably do not want this)",
                    "MODERATE: Break anything with the same harvest level",
                    "STRICT: Break only the exact same block");

            matchModeStandard = wrapper
                    .builder("item.gear.aoeTools.matchMode.standard")
                    .comment("Match mode for most blocks")
                    .defineEnum(IAOETool.MatchMode.MODERATE);
            matchModeOres = wrapper
                    .builder("item.gear.aoeTools.matchMode.ores")
                    .comment("Match mode for ore blocks (anything in the forge:ores block tag)")
                    .defineEnum(IAOETool.MatchMode.STRICT);

            gearBreaksPermanently = wrapper
                    .builder("item.gear.breaksPermanently")
                    .comment("If true, gear breaks permanently, like vanilla tools and armor")
                    .define(false);

            repairFactorAnvil = wrapper
                    .builder("item.gear.repairs.anvilEffectiveness")
                    .comment("Effectiveness of gear repairs done in an anvil. Set to 0 to disable anvil repairs.")
                    .defineInRange(0.5, 0, 1);
            repairFactorQuick = wrapper
                    .builder("item.gear.repairs.quickEffectiveness")
                    .comment("Effectiveness of quick gear repairs (crafting grid). Set to 0 to disable quick repairs.")
                    .defineInRange(0.35, 0, 1);

            upgradesInAnvilOnly = wrapper
                    .builder("item.gear.upgrades.applyInAnvilOnly")
                    .comment("If true, upgrade parts may only be applied in an anvil.")
                    .define(false);

            wrapper.comment("item.gear.statMultipliers",
                    "Multipliers for stats on all gear. This allows the stats on all items to be increased or decreased",
                    "without overriding every single file.");

            ItemStat.ALL_STATS.forEach((name, stat) -> {
                DoubleValue config = wrapper
                        .builder("item.gear.statMultipliers." + name)
                        .defineInRange(1, 0, Double.MAX_VALUE);
                statMultipliers.put(stat, config);
            });

            // Grading
            wrapper.comment("item.grading.random",
                    "Settings for random grading of ungraded materials. This affects gear items crafted with ungraded parts, not parts graded in the part analyzer.",
                    "Grading follows a normal distribution, which means that values closer to the mean (average) are more common.");
            randomGradeMean = wrapper
                    .builder("item.grading.random.mean")
                    .comment("The mean (average) grade assigned to ungraded parts.")
                    .defineEnum(MaterialGrade.C);
            randomGradeMax = wrapper
                    .builder("item.grading.random.max")
                    .comment("The maximum grade that ungraded parts can receive.")
                    .defineEnum(MaterialGrade.S);
            randomGradeStd = wrapper
                    .builder("item.grading.random.standardDeviation")
                    .comment("The standard deviation (how 'spread out' the curve is) for random grading. Must be non-negative.",
                            "Setting to zero would disable all randomness and grade all parts at the mean.")
                    .defineInRange(1.5, 0, 10);
            randomGradeSameOnAllParts = wrapper
                    .builder("item.grading.random.sameOnAllParts")
                    .comment("Apply the same grade to all parts on the tool. This makes stats more random.")
                    .define(true);

            wrapper.comment("salvager", "Settings for the salvager");

            salvagerMinLossRate = wrapper
                    .builder("salvager.partLossRate.min")
                    .comment("Minimum rate of part loss when salvaging items. 0 = no loss, 1 = complete loss.",
                            "Rate depends on remaining durability.")
                    .defineInRange(0.0, 0, 1);
            salvagerMaxLossRate = wrapper
                    .builder("salvager.partLossRate.max")
                    .comment("Maximum rate of part loss when salvaging items. 0 = no loss, 1 = complete loss.",
                            "Rate depends on remaining durability.")
                    .defineInRange(0.5, 0, 1);

            mineAndSlashSupport = wrapper
                    .builder("compat.mineAndSlash.enabled")
                    .comment("Enable compatibility with the Mine and Slash mod, if installed")
                    .define(true);

            extraPartAndTraitLogging = wrapper
                    .builder("debug.logging.extraPartAndTraitInfo")
                    .comment("Log additional information related to loading and synchronizing gear parts and traits.",
                            "This might help track down more obscure issues.")
                    .define(false);
        }

        public float getStatWithMultiplier(ItemStat stat, float value) {
            if (statMultipliers.containsKey(stat))
                return statMultipliers.get(stat).get().floatValue() * value;
            return value;
        }

        @SuppressWarnings("TypeMayBeWeakened")
        public boolean isNerfedItem(Item item) {
            return nerfedItemsEnabled.get() && isThingInList(item, nerfedItems);
        }

        public boolean isPlacerTool(ItemStack stack) {
            return isThingInList(stack.getItem(), placerTools);
        }

        public boolean isPlaceableItem(ItemStack stack) {
            return isThingInList(stack.getItem(), placeableItems);
        }

        public boolean isSinewAnimal(LivingEntity entity) {
            return isThingInList(entity.getType(), sinewAnimals);
        }

        private static boolean isThingInList(IForgeRegistryEntry<?> thing, ConfigValue<List<? extends String>> list) {
            ResourceLocation name = thing.getRegistryName();
            for (String str : list.get()) {
                ResourceLocation fromList = ResourceLocation.tryCreate(str);
                if (fromList != null && fromList.equals(name)) {
                    return true;
                }
            }
            return false;
        }
    }

    private static boolean isResourceLocation(Object o) {
        return o instanceof String && ResourceLocation.tryCreate((String) o) != null;
    }

    public static class Client {
        public final BooleanValue allowEnchantedEffect;
        public final BooleanValue useLiteModels;

        Client(ConfigSpecWrapper wrapper) {
            allowEnchantedEffect = wrapper
                    .builder("gear.allowEnchantedEffect")
                    .comment("Allow gear items to have the 'enchanted glow' effect. Set to 'false' to disable the effect.",
                            "The way vanilla handles the effect is bugged, and it is recommended to disable this until custom models are possible again.")
                    .define(false);
            useLiteModels = wrapper
                    .builder("gear.useLiteModels")
                    .comment("Use 'lite' gear models. These should be easier on some systems, but do not allow unique textures for different materials.",
                            "Currently, this option has no effect, as the normal model system is not working yet (lite models are used)")
                    .define(false);
        }
    }

    private Config() {}

    public static void init() {
        WRAPPER_CLIENT.validate();
        WRAPPER_CLIENT.validate();
        WRAPPER.validate();
        WRAPPER.validate();
    }

    // TODO: Old stuff below, needs to be fixed

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
