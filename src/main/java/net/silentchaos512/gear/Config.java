package net.silentchaos512.gear;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.silentchaos512.gear.api.part.MaterialGrade;
import net.silentchaos512.gear.api.property.GearProperty;
import net.silentchaos512.gear.api.property.GearPropertyValue;
import net.silentchaos512.gear.item.blueprint.BlueprintType;
import net.silentchaos512.gear.setup.NerfedGear;
import net.silentchaos512.gear.util.IAoeTool;
import net.silentchaos512.lib.util.NameUtils;

import javax.annotation.Nullable;
import java.util.List;

@EventBusSubscriber(modid = SilentGear.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class Config {
    public static final class Common {
        static final ModConfigSpec SPEC;
        // Blueprints
        public static final ModConfigSpec.EnumValue<BlueprintType.ConfigOption> blueprintTypes;
        public static final ModConfigSpec.BooleanValue spawnWithStarterBlueprints;
        // Nerfed gear
        public static final ModConfigSpec.BooleanValue nerfedItemsEnabled;
        public static final ModConfigSpec.DoubleValue nerfedItemDurabilityMulti;
        public static final ModConfigSpec.DoubleValue nerfedItemHarvestSpeedMulti;
        static final ModConfigSpec.ConfigValue<List<? extends String>> nerfedItems;
        // Gear
        public static final ModConfigSpec.BooleanValue allowConversionRecipes;
        public static final ModConfigSpec.BooleanValue allowEnchanting;
        public static final ModConfigSpec.BooleanValue forceRemoveEnchantments;
        public static final ModConfigSpec.BooleanValue sendGearBrokenMessage;
        public static final ModConfigSpec.EnumValue<IAoeTool.MatchMode> matchModeStandard;
        public static final ModConfigSpec.EnumValue<IAoeTool.MatchMode> matchModeOres;
        public static final ModConfigSpec.IntValue damageFactorLevels;
        public static final ModConfigSpec.BooleanValue gearBreaksPermanently;
        public static final ModConfigSpec.EnumValue<MaterialGrade> graderMedianGrade;
        public static final ModConfigSpec.DoubleValue graderStandardDeviation;
        public static final ModConfigSpec.BooleanValue graderCanGradeParts;
        public static final ModConfigSpec.IntValue prospectorHammerRange;
        public static final ModConfigSpec.DoubleValue repairFactorAnvil;
        public static final ModConfigSpec.IntValue repairKitVeryCrudeCapacity;
        public static final ModConfigSpec.IntValue repairKitCrudeCapacity;
        public static final ModConfigSpec.IntValue repairKitSturdyCapacity;
        public static final ModConfigSpec.IntValue repairKitCrimsonCapacity;
        public static final ModConfigSpec.IntValue repairKitAzureCapacity;
        public static final ModConfigSpec.DoubleValue repairKitVeryCrudeEfficiency;
        public static final ModConfigSpec.DoubleValue repairKitCrudeEfficiency;
        public static final ModConfigSpec.DoubleValue repairKitSturdyEfficiency;
        public static final ModConfigSpec.DoubleValue repairKitCrimsonEfficiency;
        public static final ModConfigSpec.DoubleValue repairKitAzureEfficiency;
        public static final ModConfigSpec.DoubleValue missingRepairKitEfficiency;
        public static final ModConfigSpec.IntValue sawRecursionDepth;
        public static final ModConfigSpec.BooleanValue upgradesInAnvilOnly;
        public static final ModConfigSpec.BooleanValue destroySwappedParts;
        // Traits
        public static final ModConfigSpec.BooleanValue magnetPullDisabledOnCrouch;
        // Salvager
        public static final ModConfigSpec.DoubleValue salvagerMinLossRate;
        public static final ModConfigSpec.DoubleValue salvagerMaxLossRate;
        public static final ModConfigSpec.BooleanValue salvagerBreakDownPartsWithGear;
        // Starlight Charger
        public static final ModConfigSpec.IntValue starlightChargerChargeRate;
        public static final ModConfigSpec.IntValue starlightChargerMaxCharge;
        public static final ModConfigSpec.BooleanValue starlightChargerCanChargeParts;
        // Debug
        public static final ModConfigSpec.BooleanValue propertiesDebugLogging;
        // Other
        public static final ModConfigSpec.BooleanValue showWipText;

        static {
            ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

            {
                builder.push("item");

                {
                    builder.comment("Blueprint and template settings");
                    builder.push("blueprint");
                    blueprintTypes = builder
                            .comment("Allowed blueprint types. Valid values are: BOTH, BLUEPRINT, and TEMPLATE")
                            .defineEnum("types_allowed", BlueprintType.ConfigOption.BOTH);
                    spawnWithStarterBlueprints = builder
                            .comment("When joining a new world, should players be given a blueprint package?",
                                    "The blueprint package gives some blueprints when used (right-click).",
                                    "To change what is given, override the starter_blueprints loot table.")
                            .define("spawn_with_starter_blueprints", true);
                    builder.pop();
                }
                {
                    builder.comment("Repair kit configs");
                    builder.push("repair_kits");

                    {
                        builder.comment("Capacity is the number of materials that can be stored (all types combined)",
                                "Setting to zero would make the repair kit unusable.");
                        builder.push("capacity");
                        repairKitVeryCrudeCapacity = builder.defineInRange("very_crude", 8, 0, Integer.MAX_VALUE);
                        repairKitCrudeCapacity = builder.defineInRange("crude", 16, 0, Integer.MAX_VALUE);
                        repairKitSturdyCapacity = builder.defineInRange("sturdy", 32, 0, Integer.MAX_VALUE);
                        repairKitCrimsonCapacity = builder.defineInRange("crimson", 48, 0, Integer.MAX_VALUE);
                        repairKitAzureCapacity = builder.defineInRange("azure", 64, 0, Integer.MAX_VALUE);
                        builder.pop();
                    }
                    {
                        builder.comment("Efficiency is the percentage of the repair value used. Higher values mean less materials used.",
                                "Setting to zero would make the repair kit unusable.");
                        builder.push("efficiency");
                        repairKitVeryCrudeEfficiency = builder.defineInRange("very_crude", 0.3f, 0f, 10f);
                        repairKitCrudeEfficiency = builder.defineInRange("crude", 0.35f, 0f, 10f);
                        repairKitSturdyEfficiency = builder.defineInRange("sturdy", 0.4f, 0f, 10f);
                        repairKitCrimsonEfficiency = builder.defineInRange("crimson", 0.45f, 0f, 10f);
                        repairKitAzureEfficiency = builder.defineInRange("azure", 0.5f, 0f, 10f);
                        missingRepairKitEfficiency = builder
                                .comment("Repair efficiency with loose materials if no repair kit is used.",
                                        "Setting a value greater than zero makes repair kits optional.")
                                .defineInRange("missing", 0.0f, 0f, 10f);
                        builder.pop();
                    }

                    builder.pop();
                }

                builder.pop();
            }
            {
                builder.comment("Settings for nerfed items.",
                        "You can give items reduced durability to encourage use of Silent Gear tools.",
                        "Changes require a restart!");
                builder.push("nerfed_items");
                nerfedItemsEnabled = builder
                        .comment("Enable this feature. If false, the other settings in this group are ignored.")
                        .define("enabled", false);
                nerfedItemDurabilityMulti = builder
                        .comment("Multiplies max durability by this value. If the result would be zero, a value of 1 is assigned.")
                        .defineInRange("durability_multiplier", 0.05, 0, 1);
                nerfedItemHarvestSpeedMulti = builder
                        .comment("Multiplies harvest speed by this value.")
                        .defineInRange("harvest_speed_multiplier", 0.5, 0, 1);
                nerfedItems = builder
                        .comment("These items will have reduced durability")
                        .defineList("items", NerfedGear.DEFAULT_ITEMS, Config::isResourceLocation);
                builder.pop();
            }
            {
                builder.comment("Settings for gear (tools, weapons, and armor)");
                builder.push("gear");

                allowConversionRecipes = builder
                        .comment("If set to false all conversion recipes (type 'silentgear:conversion') will be disabled",
                                "An example of a conversion recipe is placing a vanilla stone pickaxe into a crafting grid to make a Silent Gear stone pickaxe",
                                "Note: This also affects conversion recipes added by data packs and other mods")
                        .define("allow_conversion_recipes", true);

                sendGearBrokenMessage = builder
                        .comment("Displays a message in chat, notifying the player that an item broke and hinting that it can be repaired")
                        .define("send_broken_message", true);

                damageFactorLevels = builder
                        .comment("How frequently gear will recalculate properties as damaged",
                                "Higher numbers will cause more recalculations, allowing traits to update property values more often")
                        .defineInRange("damage_factor_levels", 10, 1, Integer.MAX_VALUE);

                gearBreaksPermanently = builder
                        .comment("If true, gear breaks permanently, like vanilla tools and armor")
                        .define("breaks_permanently", false);

                {
                    builder.push("enchanting");
                    allowEnchanting = builder
                            .comment("Allow gear items to be enchanted by normal means (enchanting table, etc.)",
                                    "There may still be other ways to obtain enchantments on gear items, depending on what other mods are installed.",
                                    "Enchantments will not be removed from gear items that have them.")
                            .define("allow_enchanting", true);
                    forceRemoveEnchantments = builder
                            .comment("Forcibly remove all enchantments from gear items. Enchantments added by traits will not be removed.",
                                    "Enchantments will be removed during property recalculations, so items not in a player's inventory will not be affected.")
                            .define("force_remove_enchantments", false);
                    builder.pop();
                }
                {
                    builder.push("prospector_hammer");
                    prospectorHammerRange = builder
                            .comment("The range in blocks the prospector hammer will search for blocks of interest")
                            .defineInRange("range", 16, 0, 64);
                    builder.pop();
                }
                {
                    builder.push("saw");
                    sawRecursionDepth = builder
                            .comment("Caps how far the saw can look for blocks when chopping down trees. Try decreasing this if you get stack overflow exceptions.",
                                    "Increasing this value is allowed, but not recommended unless you know what you are doing.")
                            .defineInRange("recursion_depth", 200, 0, Integer.MAX_VALUE);
                    builder.pop();
                }
                {
                    builder.comment("Settings for AOE tools (hammer, excavator)",
                            "Match modes determine what blocks are considered similar enough to be mined together.",
                            "LOOSE: Break anything (you probably do not want this)",
                            "MODERATE: Break anything with the same harvest level",
                            "STRICT: Break only the exact same block");
                    builder.push("aoe_tool");
                    matchModeStandard = builder
                            .comment("Match mode for most blocks")
                            .defineEnum("match_mode.standard", IAoeTool.MatchMode.MODERATE);
                    matchModeOres = builder
                            .comment("Match mode for ore blocks (anything in the forge:ores block tag)")
                            .defineEnum("match_mode.ores", IAoeTool.MatchMode.STRICT);
                    builder.pop();
                }
                {
                    builder.push("repairs");
                    repairFactorAnvil = builder
                            .comment("Effectiveness of gear repairs done in an anvil. Set to 0 to disable anvil repairs.")
                            .defineInRange("anvil_effectiveness", 0.5, 0, 1);

                    builder.pop();
                }
                {
                    builder.push("upgrades");
                    upgradesInAnvilOnly = builder
                            .comment("If true, upgrade parts may only be applied in an anvil.")
                            .define("apply_in_anvil_only", false);
                    destroySwappedParts = builder
                            .comment("If true, parts that are replaced (swapped out) of a gear item are not returned to the player and are instead destroyed.")
                            .comment("This applies to the recipe where placing a gear item and a part into a crafting grid will swap out the part.")
                            .define("destroy_swapped_parts", false);
                    builder.pop();
                }
                {
                    /*builder.comment("Multipliers for stats on all gear. This allows the stats on all items to be increased or decreased",
                            "without overriding every single file.");
                    builder.push("stat_multipliers");*/

                    // FIXME: Does not work, called too early
                    // TODO: Maybe use a data map?
                    /*ItemStats.getRegistry().getValues().forEach(stat -> {
                        ResourceLocation name = Objects.requireNonNull(stat.getRegistryName());
                        String key = name.getNamespace() + "." + name.getPath();
                        ForgeConfigSpec.DoubleValue config = builder
                                .defineInRange(key, 1, 0, Double.MAX_VALUE);
                        statMultipliers.put(stat, config);
                    });*/
//                    builder.pop();
                }
                builder.pop();
            }

            magnetPullDisabledOnCrouch = builder
                    .comment("Disable magnetic pull effects when the player is crouching/sneaking.")
                    .define("trait.item_magnet.disable_on_crouch", true);

            {
                builder.comment("Settings for the material grader");
                builder.push("material_grader");
                graderMedianGrade = builder
                        .comment("The median (most common, average) grade that a material grader with tier 1 catalyst will produce.",
                                "Higher tier catalysts will increase the median by one grade per tier past 1 (if 1 = C, 2 = B, etc.)")
                        .defineEnum("median_grade", MaterialGrade.C);
                graderStandardDeviation = builder
                        .comment("The standard deviation of grades the material grader will produce.",
                                "Grades are normally distributed, with the median grade being at the center of the bell curve.",
                                "Larger numbers will make both higher and lower grades more common.",
                                "Extremely large values may completely break the curve, yielding mostly the lowest and highest grades.")
                        .defineInRange("standard_deviation", 1.5, 0.0, 100.0);
                graderCanGradeParts = builder
                        .comment("If enabled, allows the material grader to grade the individual materials inside a gear part")
                        .define("can_grade_parts", false);
                builder.pop();
            }

            {
                builder.comment("Settings for the salvager");
                builder.push("salvager");
                salvagerMinLossRate = builder
                        .comment("Minimum rate of part loss when salvaging items. 0 = no loss, 1 = complete loss.",
                                "Rate depends on remaining durability.")
                        .defineInRange("part_loss_rate.min", 0.0, 0, 1);
                salvagerMaxLossRate = builder
                        .comment("Maximum rate of part loss when salvaging items. 0 = no loss, 1 = complete loss.",
                                "Rate depends on remaining durability.")
                        .defineInRange("part_loss_rate.max", 0.5, 0, 1);
                salvagerBreakDownPartsWithGear = builder
                        .comment("If enabled, the salvager will automatically break down any parts returned by a gear item into their component materials.",
                                "Otherwise, the parts are returned and can be optionally salvaged individually.")
                        .define("break_down_parts_with_gear", false);
                builder.pop();
            }

            {
                builder.comment("Settings for the starlight charger");
                builder.push("starlight_charger");
                starlightChargerChargeRate = builder
                        .comment("The rate at which the starlight charger gathers energy during the night")
                        .defineInRange("charge_rate", 100, 0, Integer.MAX_VALUE);
                starlightChargerMaxCharge = builder
                        .comment("The maximum amount of energy the starlight charger can store")
                        .defineInRange("max_charge", 1_000_000, 0, Integer.MAX_VALUE);
                starlightChargerCanChargeParts = builder
                        .comment("If enabled, allows the starlight charger to charge the individual materials inside a gear part")
                        .define("can_charge_parts", false);
                builder.pop();
            }

            propertiesDebugLogging = builder
                    .comment("Log property calculations in the log file every time gear properties are recalculated")
                    .define("debug.logging.properties", true);

            // Other random stuff
            showWipText = builder
                    .comment("Shows a \"WIP\" (work in progress) label in the tooltip of certain unfinished, but usable blocks and items")
                    .comment("Set to false to remove the text from tooltips")
                    .define("other.show_wip_text", true);

            SPEC = builder.build();
        }

        private Common() {
        }

        public static boolean isLoaded() {
            return SPEC.isLoaded();
        }

        @Nullable
        public static GearPropertyValue<?> getPropertyBonusMultiplier(GearProperty<?, ?> property) {
            // TODO: Make a special "_global_properties.json" file for materials to handle stat multipliers
            return null;
        }

        @SuppressWarnings("TypeMayBeWeakened")
        public static boolean isNerfedItem(Item item) {
            return nerfedItemsEnabled.get() && isThingInList(NameUtils.fromItem(item), nerfedItems);
        }

        private static boolean isThingInList(ResourceLocation name, ModConfigSpec.ConfigValue<List<? extends String>> list) {
            for (String str : list.get()) {
                ResourceLocation fromList = ResourceLocation.tryParse(str);
                if (fromList != null && fromList.equals(name)) {
                    return true;
                }
            }
            return false;
        }
    }

    private static boolean isResourceLocation(Object o) {
        return o instanceof String && ResourceLocation.tryParse((String) o) != null;
    }

    public static final class Client {
        static final ModConfigSpec SPEC;

        public static final ModConfigSpec.BooleanValue allowEnchantedEffect;
        //Tooltip
        public static final ModConfigSpec.BooleanValue showMaterialTooltips;
        public static final ModConfigSpec.BooleanValue showPartTooltips;
        public static final ModConfigSpec.BooleanValue vanillaStyleTooltips;

        static {
            ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

            allowEnchantedEffect = builder
                    .comment("Allow gear items to have the 'enchanted glow' effect. Set to 'false' to disable the effect.",
                            "The way vanilla handles the effect is bugged, and it is recommended to disable this until it can be fixed",
                            "The bug is not harmful and some like the way the overpowered effect looks")
                    .define("gear.allow_enchanted_effect", false);

            showMaterialTooltips = builder
                    .comment("Show SGear Material tooltips on items that can be used as materials.")
                    .define("tooltip.show_material_tooltips", true);

            showPartTooltips = builder
                    .comment("Show tooltips on parts and items that can be used as parts.")
                    .define("tooltip.show_part_tooltips", true);

            vanillaStyleTooltips = builder
                    .comment("Tooltips are replaced with a simpler variant similar to vanilla and contains about as much information.")
                    .define("tooltip.vanilla_style_tooltips", false);

            SPEC = builder.build();
        }

        private Client() {
        }
    }

    private Config() {
    }

    @SubscribeEvent
    public static void onLoad(final ModConfigEvent event) {
    }
}
