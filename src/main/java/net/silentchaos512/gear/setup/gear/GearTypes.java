package net.silentchaos512.gear.setup.gear;

import net.neoforged.neoforge.common.ToolActions;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.setup.SgRegistries;

import java.util.function.Supplier;

public class GearTypes {
    public static final DeferredRegister<GearType> GEAR_TYPES = DeferredRegister.create(SgRegistries.GEAR_TYPES, SilentGear.MOD_ID);

    public static final Supplier<GearType> NONE = GEAR_TYPES.register("none", () -> GearType.Builder.of()
            .build()
    );
    public static final Supplier<GearType> ALL = GEAR_TYPES.register("all", () -> GearType.Builder.of()
            .build()
    );

    // Common parents
    public static final Supplier<GearType> TOOL = GEAR_TYPES.register("tool", () -> GearType.Builder.of(ALL)
            .build()
    );
    public static final Supplier<GearType> WEAPON = GEAR_TYPES.register("weapon", () -> GearType.Builder.of(TOOL)
            .build()
    );
    public static final Supplier<GearType> ARMOR = GEAR_TYPES.register("armor", () -> GearType.Builder.of(ALL)
            .durabilityStat(GearProperties.ARMOR_DURABILITY)
            .build()
    );
    public static final Supplier<GearType> HARVEST_TOOL = GEAR_TYPES.register("harvest_tool", () -> GearType.Builder.of(TOOL)
            .build()
    );
    public static final Supplier<GearType> MELEE_WEAPON = GEAR_TYPES.register("melee_weapon", () -> GearType.Builder.of(WEAPON)
            .build()
    );
    public static final Supplier<GearType> RANGED_WEAPON = GEAR_TYPES.register("ranged_weapon", () -> GearType.Builder.of(WEAPON)
            .build()
    );
    public static final Supplier<GearType> CURIO = GEAR_TYPES.register("curio", () -> GearType.Builder.of(ALL)
            .build()
    );
    public static final Supplier<GearType> PROJECTILE = GEAR_TYPES.register("projectile", () -> GearType.Builder.of(ALL)
            .build()
    );

    // Standard harvest tools
    public static final Supplier<GearType> PICKAXE = GEAR_TYPES.register("pickaxe", () -> GearType.Builder.of(HARVEST_TOOL)
            .toolActions(ToolActions.DEFAULT_PICKAXE_ACTIONS)
            .build()
    );
    public static final Supplier<GearType> SHOVEL = GEAR_TYPES.register("shovel", () -> GearType.Builder.of(HARVEST_TOOL)
            .toolActions(ToolActions.DEFAULT_SHOVEL_ACTIONS)
            .build()
    );
    public static final Supplier<GearType> AXE = GEAR_TYPES.register("axe", () -> GearType.Builder.of(HARVEST_TOOL)
            .toolActions(ToolActions.DEFAULT_AXE_ACTIONS)
            .build()
    );
    public static final Supplier<GearType> HOE = GEAR_TYPES.register("hoe", () -> GearType.Builder.of(HARVEST_TOOL)
            .toolActions(ToolActions.DEFAULT_HOE_ACTIONS)
            .build()
    );
    public static final Supplier<GearType> SHEARS = GEAR_TYPES.register("shears", () -> GearType.Builder.of(HARVEST_TOOL)
            .toolActions(ToolActions.DEFAULT_SHEARS_ACTIONS)
            .build()
    );
    // Big harvest tool
    public static final Supplier<GearType> HAMMER = GEAR_TYPES.register("hammer", () -> GearType.Builder.of(PICKAXE)
            .toolActions(ToolActions.PICKAXE_DIG)
            .build()
    );
    public static final Supplier<GearType> EXCAVATOR = GEAR_TYPES.register("excavator", () -> GearType.Builder.of(SHOVEL)
            .toolActions(ToolActions.SHOVEL_DIG)
            .build()
    );
    public static final Supplier<GearType> SAW = GEAR_TYPES.register("saw", () -> GearType.Builder.of(AXE)
            .toolActions(ToolActions.AXE_DIG)
            .build()
    );
    public static final Supplier<GearType> SICKLE = GEAR_TYPES.register("sickle", () -> GearType.Builder.of(HARVEST_TOOL)
            .toolActions(ToolActions.HOE_DIG)
            .build()
    );
    // Specialty harvest tools
    public static final Supplier<GearType> MATTOCK = GEAR_TYPES.register("mattock", () -> GearType.Builder.of(HARVEST_TOOL)
            .toolActions(
                    ToolActions.SHOVEL_DIG,
                    ToolActions.AXE_DIG,
                    ToolActions.HOE_DIG,
                    ToolActions.HOE_TILL
            )
            .build()
    );
    public static final Supplier<GearType> PAXEL = GEAR_TYPES.register("paxel", () -> GearType.Builder.of(HARVEST_TOOL)
            .toolActions(
                    ToolActions.AXE_DIG,
                    ToolActions.AXE_SCRAPE,
                    ToolActions.AXE_STRIP,
                    ToolActions.AXE_WAX_OFF,
                    ToolActions.PICKAXE_DIG,
                    ToolActions.SHOVEL_DIG
            )
            .build()
    );
    public static final Supplier<GearType> PROSPECTOR_HAMMER = GEAR_TYPES.register("prospector_hammer", () -> GearType.Builder.of(PICKAXE)
            .toolActions(ToolActions.DEFAULT_PICKAXE_ACTIONS)
            .build()
    );

    // Melee weapons
    public static final Supplier<GearType> SWORD = GEAR_TYPES.register("sword", () -> GearType.Builder.of(MELEE_WEAPON)
            .toolActions(ToolActions.DEFAULT_SWORD_ACTIONS)
            .build()
    );
    public static final Supplier<GearType> KATANA = GEAR_TYPES.register("katana", () -> GearType.Builder.of(MELEE_WEAPON)
            .toolActions(ToolActions.DEFAULT_SWORD_ACTIONS)
            .build()
    );
    public static final Supplier<GearType> MACHETE = GEAR_TYPES.register("machete", () -> GearType.Builder.of(MELEE_WEAPON)
            .toolActions(
                    ToolActions.SWORD_DIG,
                    ToolActions.SWORD_SWEEP,
                    ToolActions.AXE_DIG
            )
            .build()
    );
    public static final Supplier<GearType> SPEAR = GEAR_TYPES.register("spear", () -> GearType.Builder.of(MELEE_WEAPON)
            .toolActions(ToolActions.SWORD_DIG)
            .build()
    );
    public static final Supplier<GearType> TRIDENT = GEAR_TYPES.register("trident", () -> GearType.Builder.of(MELEE_WEAPON)
            .toolActions(ToolActions.DEFAULT_TRIDENT_ACTIONS)
            .build()
    );
    public static final Supplier<GearType> DAGGER = GEAR_TYPES.register("dagger", () -> GearType.Builder.of(MELEE_WEAPON)
            .toolActions(ToolActions.SWORD_DIG)
            .build()
    );
    public static final Supplier<GearType> KNIFE = GEAR_TYPES.register("knife", () -> GearType.Builder.of(MELEE_WEAPON)
            .toolActions(ToolActions.SWORD_DIG)
            .build()
    );

    // Ranged weapons
    public static final Supplier<GearType> BOW = GEAR_TYPES.register("bow", () -> GearType.Builder.of(RANGED_WEAPON)
            .build()
    );
    public static final Supplier<GearType> CROSSBOW = GEAR_TYPES.register("crossbow", () -> GearType.Builder.of(RANGED_WEAPON)
            .build()
    );
    public static final Supplier<GearType> SLINGSHOT = GEAR_TYPES.register("slingshot", () -> GearType.Builder.of(RANGED_WEAPON)
            .build()
    );

    // Other tools
    public static final Supplier<GearType> FISHING_ROD = GEAR_TYPES.register("fishing_rod", () -> GearType.Builder.of(TOOL)
            .toolActions(ToolActions.DEFAULT_FISHING_ROD_ACTIONS)
            .build()
    );
    public static final Supplier<GearType> SHIELD = GEAR_TYPES.register("shield", () -> GearType.Builder.of(TOOL)
            .toolActions(ToolActions.DEFAULT_SHIELD_ACTIONS)
            .build()
    );

    // Armor
    public static final Supplier<GearType> HELMET = GEAR_TYPES.register("helmet", () -> GearType.Builder.of(ARMOR)
            .durabilityStat(GearProperties.ARMOR_DURABILITY)
            .armorDurabilityMultiplier(11)
            .build()
    );
    public static final Supplier<GearType> CHESTPLATE = GEAR_TYPES.register("chestplate", () -> GearType.Builder.of(ARMOR)
            .durabilityStat(GearProperties.ARMOR_DURABILITY)
            .armorDurabilityMultiplier(16)
            .build()
    );
    public static final Supplier<GearType> LEGGINGS = GEAR_TYPES.register("leggings", () -> GearType.Builder.of(ARMOR)
            .durabilityStat(GearProperties.ARMOR_DURABILITY)
            .armorDurabilityMultiplier(15)
            .build()
    );
    public static final Supplier<GearType> BOOTS = GEAR_TYPES.register("boots", () -> GearType.Builder.of(ARMOR)
            .durabilityStat(GearProperties.ARMOR_DURABILITY)
            .armorDurabilityMultiplier(13)
            .build()
    );
    public static final Supplier<GearType> ELYTRA = GEAR_TYPES.register("elytra", () -> GearType.Builder.of(ARMOR)
            .durabilityStat(GearProperties.ARMOR_DURABILITY)
            .armorDurabilityMultiplier(25)
            .build()
    );

    // Projectiles
    public static final Supplier<GearType> ARROW = GEAR_TYPES.register("arrow", () -> GearType.Builder.of(PROJECTILE)
            .build()
    );

    // Curios
    public static final Supplier<GearType> BRACELET = GEAR_TYPES.register("bracelet", () -> GearType.Builder.of(CURIO)
            .build()
    );
    public static final Supplier<GearType> NECKLACE = GEAR_TYPES.register("necklace", () -> GearType.Builder.of(ALL)
            .build()
    );
    public static final Supplier<GearType> RING = GEAR_TYPES.register("ring", () -> GearType.Builder.of(CURIO)
            .build()
    );

    // Curios
}
