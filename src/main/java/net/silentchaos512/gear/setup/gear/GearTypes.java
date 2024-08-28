package net.silentchaos512.gear.setup.gear;

import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.property.GearPropertyGroups;
import net.silentchaos512.gear.setup.SgRegistries;

public class GearTypes {
    public static final DeferredRegister<GearType> REGISTRAR = DeferredRegister.create(SgRegistries.GEAR_TYPE, SilentGear.MOD_ID);

    public static final DeferredHolder<GearType, GearType> NONE = REGISTRAR.register("none",
            () -> GearType.Builder.of()
                    .build()
    );
    public static final DeferredHolder<GearType, GearType> ALL = REGISTRAR.register("all",
            () -> GearType.Builder.of()
                    .relevantPropertyGroups(
                            GearPropertyGroups.TRAITS,
                            GearPropertyGroups.GENERAL
                    )
                    .build()
    );

    // Common parents
    public static final DeferredHolder<GearType, GearType> TOOL = REGISTRAR.register("tool",
            () -> GearType.Builder.of(ALL)
                    .relevantPropertyGroups(
                            GearPropertyGroups.TRAITS,
                            GearPropertyGroups.GENERAL,
                            GearPropertyGroups.HARVEST,
                            GearPropertyGroups.ATTACK
                    )
                    .build()
    );
    public static final DeferredHolder<GearType, GearType> WEAPON = REGISTRAR.register("weapon",
            () -> GearType.Builder.of(TOOL)
                    .build()
    );
    public static final DeferredHolder<GearType, GearType> ARMOR = REGISTRAR.register("armor",
            () -> GearType.Builder.of(ALL)
                    .durabilityStat(GearProperties.ARMOR_DURABILITY)
                    .relevantPropertyGroups(
                            GearPropertyGroups.TRAITS,
                            GearPropertyGroups.GENERAL,
                            GearPropertyGroups.ARMOR
                    )
                    .build()
    );
    public static final DeferredHolder<GearType, GearType> HARVEST_TOOL = REGISTRAR.register("harvest_tool",
            () -> GearType.Builder.of(TOOL)
                    .build()
    );
    public static final DeferredHolder<GearType, GearType> MELEE_WEAPON = REGISTRAR.register("melee_weapon",
            () -> GearType.Builder.of(WEAPON)
                    .build()
    );
    public static final DeferredHolder<GearType, GearType> RANGED_WEAPON = REGISTRAR.register("ranged_weapon",
            () -> GearType.Builder.of(WEAPON)
                    .relevantPropertyGroups(
                            GearPropertyGroups.TRAITS,
                            GearPropertyGroups.GENERAL,
                            GearPropertyGroups.PROJECTILE
                    )
                    .build()
    );
    public static final DeferredHolder<GearType, GearType> CURIO = REGISTRAR.register("curio",
            () -> GearType.Builder.of(ALL)
                    .relevantPropertyGroups(
                            GearPropertyGroups.TRAITS
                    )
                    .build()
    );
    public static final DeferredHolder<GearType, GearType> PROJECTILE = REGISTRAR.register("projectile",
            () -> GearType.Builder.of(ALL)
                    .relevantPropertyGroups(
                            GearPropertyGroups.TRAITS,
                            GearPropertyGroups.PROJECTILE
                    )
                    .build()
    );

    // Standard harvest tools
    public static final DeferredHolder<GearType, GearType> PICKAXE = REGISTRAR.register("pickaxe",
            () -> GearType.Builder.of(HARVEST_TOOL)
                    .toolActions(ItemAbilities.DEFAULT_PICKAXE_ACTIONS)
                    .build()
    );
    public static final DeferredHolder<GearType, GearType> SHOVEL = REGISTRAR.register("shovel",
            () -> GearType.Builder.of(HARVEST_TOOL)
                    .toolActions(ItemAbilities.DEFAULT_SHOVEL_ACTIONS)
                    .build()
    );
    public static final DeferredHolder<GearType, GearType> AXE = REGISTRAR.register("axe",
            () -> GearType.Builder.of(HARVEST_TOOL)
                    .toolActions(ItemAbilities.DEFAULT_AXE_ACTIONS)
                    .build()
    );
    public static final DeferredHolder<GearType, GearType> HOE = REGISTRAR.register("hoe",
            () -> GearType.Builder.of(HARVEST_TOOL)
                    .toolActions(ItemAbilities.DEFAULT_HOE_ACTIONS)
                    .build()
    );
    public static final DeferredHolder<GearType, GearType> SHEARS = REGISTRAR.register("shears",
            () -> GearType.Builder.of(HARVEST_TOOL)
                    .toolActions(ItemAbilities.DEFAULT_SHEARS_ACTIONS)
                    .build()
    );
    // Big harvest tool
    public static final DeferredHolder<GearType, GearType> HAMMER = REGISTRAR.register("hammer",
            () -> GearType.Builder.of(PICKAXE)
                    .toolActions(ItemAbilities.PICKAXE_DIG)
                    .build()
    );
    public static final DeferredHolder<GearType, GearType> EXCAVATOR = REGISTRAR.register("excavator",
            () -> GearType.Builder.of(SHOVEL)
                    .toolActions(ItemAbilities.SHOVEL_DIG)
                    .build()
    );
    public static final DeferredHolder<GearType, GearType> SAW = REGISTRAR.register("saw",
            () -> GearType.Builder.of(AXE)
                    .toolActions(ItemAbilities.AXE_DIG)
                    .build()
    );
    public static final DeferredHolder<GearType, GearType> SICKLE = REGISTRAR.register("sickle",
            () -> GearType.Builder.of(HARVEST_TOOL)
                    .toolActions(ItemAbilities.HOE_DIG)
                    .build()
    );
    // Specialty harvest tools
    public static final DeferredHolder<GearType, GearType> MATTOCK = REGISTRAR.register("mattock",
            () -> GearType.Builder.of(HARVEST_TOOL)
                    .toolActions(
                            ItemAbilities.SHOVEL_DIG,
                            ItemAbilities.AXE_DIG,
                            ItemAbilities.HOE_DIG,
                            ItemAbilities.HOE_TILL
                    )
                    .build()
    );
    public static final DeferredHolder<GearType, GearType> PAXEL = REGISTRAR.register("paxel",
            () -> GearType.Builder.of(HARVEST_TOOL)
                    .toolActions(
                            ItemAbilities.AXE_DIG,
                            ItemAbilities.AXE_SCRAPE,
                            ItemAbilities.AXE_STRIP,
                            ItemAbilities.AXE_WAX_OFF,
                            ItemAbilities.PICKAXE_DIG,
                            ItemAbilities.SHOVEL_DIG
                    )
                    .build()
    );
    public static final DeferredHolder<GearType, GearType> PROSPECTOR_HAMMER = REGISTRAR.register("prospector_hammer",
            () -> GearType.Builder.of(PICKAXE)
                    .toolActions(ItemAbilities.DEFAULT_PICKAXE_ACTIONS)
                    .build()
    );

    // Melee weapons
    public static final DeferredHolder<GearType, GearType> SWORD = REGISTRAR.register("sword",
            () -> GearType.Builder.of(MELEE_WEAPON)
                    .toolActions(ItemAbilities.DEFAULT_SWORD_ACTIONS)
                    .build()
    );
    public static final DeferredHolder<GearType, GearType> KATANA = REGISTRAR.register("katana",
            () -> GearType.Builder.of(MELEE_WEAPON)
                    .toolActions(ItemAbilities.DEFAULT_SWORD_ACTIONS)
                    .build()
    );
    public static final DeferredHolder<GearType, GearType> MACHETE = REGISTRAR.register("machete",
            () -> GearType.Builder.of(MELEE_WEAPON)
                    .toolActions(
                            ItemAbilities.SWORD_DIG,
                            ItemAbilities.SWORD_SWEEP,
                            ItemAbilities.AXE_DIG
                    )
                    .build()
    );
    public static final DeferredHolder<GearType, GearType> SPEAR = REGISTRAR.register("spear",
            () -> GearType.Builder.of(MELEE_WEAPON)
                    .toolActions(ItemAbilities.SWORD_DIG)
                    .build()
    );
    public static final DeferredHolder<GearType, GearType> TRIDENT = REGISTRAR.register("trident",
            () -> GearType.Builder.of(MELEE_WEAPON)
                    .toolActions(ItemAbilities.DEFAULT_TRIDENT_ACTIONS)
                    .build()
    );
    public static final DeferredHolder<GearType, GearType> MACE = REGISTRAR.register("mace",
            () -> GearType.Builder.of(MELEE_WEAPON)
                    .build()
    );
    public static final DeferredHolder<GearType, GearType> DAGGER = REGISTRAR.register("dagger",
            () -> GearType.Builder.of(MELEE_WEAPON)
                    .toolActions(ItemAbilities.SWORD_DIG)
                    .build()
    );
    public static final DeferredHolder<GearType, GearType> KNIFE = REGISTRAR.register("knife",
            () -> GearType.Builder.of(MELEE_WEAPON)
                    .toolActions(ItemAbilities.SWORD_DIG)
                    .build()
    );

    // Ranged weapons
    public static final DeferredHolder<GearType, GearType> BOW = REGISTRAR.register("bow",
            () -> GearType.Builder.of(RANGED_WEAPON)
                    .build()
    );
    public static final DeferredHolder<GearType, GearType> CROSSBOW = REGISTRAR.register("crossbow",
            () -> GearType.Builder.of(RANGED_WEAPON)
                    .build()
    );
    public static final DeferredHolder<GearType, GearType> SLINGSHOT = REGISTRAR.register("slingshot",
            () -> GearType.Builder.of(RANGED_WEAPON)
                    .build()
    );

    // Other tools
    public static final DeferredHolder<GearType, GearType> FISHING_ROD = REGISTRAR.register("fishing_rod",
            () -> GearType.Builder.of(TOOL)
                    .toolActions(ItemAbilities.DEFAULT_FISHING_ROD_ACTIONS)
                    .relevantPropertyGroups(
                            GearPropertyGroups.TRAITS,
                            GearPropertyGroups.GENERAL
                    )
                    .build()
    );
    public static final DeferredHolder<GearType, GearType> SHIELD = REGISTRAR.register("shield",
            () -> GearType.Builder.of(TOOL)
                    .toolActions(ItemAbilities.DEFAULT_SHIELD_ACTIONS)
                    .relevantPropertyGroups(
                            GearPropertyGroups.TRAITS,
                            GearPropertyGroups.GENERAL
                    )
                    .build()
    );

    // Armor
    public static final DeferredHolder<GearType, GearType> HELMET = REGISTRAR.register("helmet",
            () -> GearType.Builder.of(ARMOR)
                    .durabilityStat(GearProperties.ARMOR_DURABILITY)
                    .armorDurabilityMultiplier(11)
                    .build()
    );
    public static final DeferredHolder<GearType, GearType> CHESTPLATE = REGISTRAR.register("chestplate",
            () -> GearType.Builder.of(ARMOR)
                    .durabilityStat(GearProperties.ARMOR_DURABILITY)
                    .armorDurabilityMultiplier(16)
                    .build()
    );
    public static final DeferredHolder<GearType, GearType> LEGGINGS = REGISTRAR.register("leggings",
            () -> GearType.Builder.of(ARMOR)
                    .durabilityStat(GearProperties.ARMOR_DURABILITY)
                    .armorDurabilityMultiplier(15)
                    .build()
    );
    public static final DeferredHolder<GearType, GearType> BOOTS = REGISTRAR.register("boots",
            () -> GearType.Builder.of(ARMOR)
                    .durabilityStat(GearProperties.ARMOR_DURABILITY)
                    .armorDurabilityMultiplier(13)
                    .build()
    );
    public static final DeferredHolder<GearType, GearType> ELYTRA = REGISTRAR.register("elytra",
            () -> GearType.Builder.of(ARMOR)
                    .durabilityStat(GearProperties.ARMOR_DURABILITY)
                    .armorDurabilityMultiplier(25)
                    .build()
    );

    // Projectiles
    public static final DeferredHolder<GearType, GearType> ARROW = REGISTRAR.register("arrow",
            () -> GearType.Builder.of(PROJECTILE)
                    .build()
    );

    // Curios
    public static final DeferredHolder<GearType, GearType> BRACELET = REGISTRAR.register("bracelet",
            () -> GearType.Builder.of(CURIO)
                    .build()
    );
    public static final DeferredHolder<GearType, GearType> NECKLACE = REGISTRAR.register("necklace",
            () -> GearType.Builder.of(CURIO)
                    .build()
    );
    public static final DeferredHolder<GearType, GearType> RING = REGISTRAR.register("ring",
            () -> GearType.Builder.of(CURIO)
                    .build()
    );

    // Curios
}
