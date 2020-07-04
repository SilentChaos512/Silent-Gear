package net.silentchaos512.gear.init;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.BlockNamedItem;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.parts.PartType;
import net.silentchaos512.gear.item.*;
import net.silentchaos512.gear.item.blueprint.GearBlueprintItem;
import net.silentchaos512.gear.item.blueprint.PartBlueprintItem;
import net.silentchaos512.gear.item.gear.*;
import net.silentchaos512.lib.registry.ItemRegistryObject;
import net.silentchaos512.lib.util.TimeUtils;

import java.util.function.Supplier;

@SuppressWarnings({"unused", "OverlyCoupledClass"})
public final class ModItems {
    //region Blocks
    // TODO
    //endregion

    public static final ItemRegistryObject<BlueprintPackageItem> BLUEPRINT_PACKAGE = register("blueprint_package", () ->
            new BlueprintPackageItem(SilentGear.getId("starter_blueprints")));

    //region Blueprints and templates
    // Blueprints
    public static final ItemRegistryObject<PartBlueprintItem> ROD_BLUEPRINT = registerPartBlueprint(PartType.ROD, false);
    public static final ItemRegistryObject<PartBlueprintItem> TIP_BLUEPRINT = registerPartBlueprint(PartType.TIP, false);
    public static final ItemRegistryObject<PartBlueprintItem> GRIP_BLUEPRINT = registerPartBlueprint(PartType.GRIP, false);
    public static final ItemRegistryObject<PartBlueprintItem> BINDING_BLUEPRINT = registerPartBlueprint(PartType.BINDING, false);
    public static final ItemRegistryObject<PartBlueprintItem> BOWSTRING_BLUEPRINT = registerPartBlueprint(PartType.BOWSTRING, false);
    public static final ItemRegistryObject<GearBlueprintItem> SWORD_BLUEPRINT = registerGearBlueprint(GearType.SWORD, false);
    public static final ItemRegistryObject<GearBlueprintItem> DAGGER_BLUEPRINT = registerGearBlueprint(GearType.DAGGER, false);
    public static final ItemRegistryObject<GearBlueprintItem> KATANA_BLUEPRINT = registerGearBlueprint(GearType.KATANA, false);
    public static final ItemRegistryObject<GearBlueprintItem> MACHETE_BLUEPRINT = registerGearBlueprint(GearType.MACHETE, false);
    public static final ItemRegistryObject<GearBlueprintItem> SPEAR_BLUEPRINT = registerGearBlueprint(GearType.SPEAR, false);
    public static final ItemRegistryObject<GearBlueprintItem> PICKAXE_BLUEPRINT = registerGearBlueprint(GearType.PICKAXE, false);
    public static final ItemRegistryObject<GearBlueprintItem> SHOVEL_BLUEPRINT = registerGearBlueprint(GearType.SHOVEL, false);
    public static final ItemRegistryObject<GearBlueprintItem> AXE_BLUEPRINT = registerGearBlueprint(GearType.AXE, false);
    public static final ItemRegistryObject<GearBlueprintItem> PAXEL_BLUEPRINT = registerGearBlueprint(GearType.PAXEL, false);
    public static final ItemRegistryObject<GearBlueprintItem> HAMMER_BLUEPRINT = registerGearBlueprint(GearType.HAMMER, false);
    public static final ItemRegistryObject<GearBlueprintItem> EXCAVATOR_BLUEPRINT = registerGearBlueprint(GearType.EXCAVATOR, false);
    public static final ItemRegistryObject<GearBlueprintItem> LUMBER_AXE_BLUEPRINT = registerGearBlueprint(GearType.LUMBER_AXE, false);
    public static final ItemRegistryObject<GearBlueprintItem> MATTOCK_BLUEPRINT = registerGearBlueprint(GearType.MATTOCK, false);
    public static final ItemRegistryObject<GearBlueprintItem> SICKLE_BLUEPRINT = registerGearBlueprint(GearType.SICKLE, false);
    public static final ItemRegistryObject<GearBlueprintItem> SHEARS_BLUEPRINT = registerGearBlueprint(GearType.SHEARS, false);
    public static final ItemRegistryObject<GearBlueprintItem> BOW_BLUEPRINT = registerGearBlueprint(GearType.BOW, false);
    public static final ItemRegistryObject<GearBlueprintItem> CROSSBOW_BLUEPRINT = registerGearBlueprint(GearType.CROSSBOW, false);
    public static final ItemRegistryObject<GearBlueprintItem> SLINGSHOT_BLUEPRINT = registerGearBlueprint(GearType.SLINGSHOT, false);
    public static final ItemRegistryObject<GearBlueprintItem> SHIELD_BLUEPRINT = registerGearBlueprint(GearType.SHIELD, false);
    public static final ItemRegistryObject<GearBlueprintItem> HELMET_BLUEPRINT = registerGearBlueprint(GearType.HELMET, false);
    public static final ItemRegistryObject<GearBlueprintItem> CHESTPLATE_BLUEPRINT = registerGearBlueprint(GearType.CHESTPLATE, false);
    public static final ItemRegistryObject<GearBlueprintItem> LEGGINGS_BLUEPRINT = registerGearBlueprint(GearType.LEGGINGS, false);
    public static final ItemRegistryObject<GearBlueprintItem> BOOTS_BLUEPRINT = registerGearBlueprint(GearType.BOOTS, false);
    // Templates
    public static final ItemRegistryObject<PartBlueprintItem> ROD_TEMPLATE = registerPartBlueprint(PartType.ROD, true);
    public static final ItemRegistryObject<PartBlueprintItem> TIP_TEMPLATE = registerPartBlueprint(PartType.TIP, true);
    public static final ItemRegistryObject<PartBlueprintItem> GRIP_TEMPLATE = registerPartBlueprint(PartType.GRIP, true);
    public static final ItemRegistryObject<PartBlueprintItem> BINDING_TEMPLATE = registerPartBlueprint(PartType.BINDING, true);
    public static final ItemRegistryObject<PartBlueprintItem> BOWSTRING_TEMPLATE = registerPartBlueprint(PartType.BOWSTRING, true);
    public static final ItemRegistryObject<GearBlueprintItem> SWORD_TEMPLATE = registerGearBlueprint(GearType.SWORD, true);
    public static final ItemRegistryObject<GearBlueprintItem> DAGGER_TEMPLATE = registerGearBlueprint(GearType.DAGGER, true);
    public static final ItemRegistryObject<GearBlueprintItem> KATANA_TEMPLATE = registerGearBlueprint(GearType.KATANA, true);
    public static final ItemRegistryObject<GearBlueprintItem> MACHETE_TEMPLATE = registerGearBlueprint(GearType.MACHETE, true);
    public static final ItemRegistryObject<GearBlueprintItem> SPEAR_TEMPLATE = registerGearBlueprint(GearType.SPEAR, true);
    public static final ItemRegistryObject<GearBlueprintItem> PICKAXE_TEMPLATE = registerGearBlueprint(GearType.PICKAXE, true);
    public static final ItemRegistryObject<GearBlueprintItem> SHOVEL_TEMPLATE = registerGearBlueprint(GearType.SHOVEL, true);
    public static final ItemRegistryObject<GearBlueprintItem> AXE_TEMPLATE = registerGearBlueprint(GearType.AXE, true);
    public static final ItemRegistryObject<GearBlueprintItem> PAXEL_TEMPLATE = registerGearBlueprint(GearType.PAXEL, true);
    public static final ItemRegistryObject<GearBlueprintItem> HAMMER_TEMPLATE = registerGearBlueprint(GearType.HAMMER, true);
    public static final ItemRegistryObject<GearBlueprintItem> EXCAVATOR_TEMPLATE = registerGearBlueprint(GearType.EXCAVATOR, true);
    public static final ItemRegistryObject<GearBlueprintItem> LUMBER_AXE_TEMPLATE = registerGearBlueprint(GearType.LUMBER_AXE, true);
    public static final ItemRegistryObject<GearBlueprintItem> MATTOCK_TEMPLATE = registerGearBlueprint(GearType.MATTOCK, true);
    public static final ItemRegistryObject<GearBlueprintItem> SICKLE_TEMPLATE = registerGearBlueprint(GearType.SICKLE, true);
    public static final ItemRegistryObject<GearBlueprintItem> SHEARS_TEMPLATE = registerGearBlueprint(GearType.SHEARS, true);
    public static final ItemRegistryObject<GearBlueprintItem> BOW_TEMPLATE = registerGearBlueprint(GearType.BOW, true);
    public static final ItemRegistryObject<GearBlueprintItem> CROSSBOW_TEMPLATE = registerGearBlueprint(GearType.CROSSBOW, true);
    public static final ItemRegistryObject<GearBlueprintItem> SLINGSHOT_TEMPLATE = registerGearBlueprint(GearType.SLINGSHOT, true);
    public static final ItemRegistryObject<GearBlueprintItem> SHIELD_TEMPLATE = registerGearBlueprint(GearType.SHIELD, true);
    public static final ItemRegistryObject<GearBlueprintItem> HELMET_TEMPLATE = registerGearBlueprint(GearType.HELMET, true);
    public static final ItemRegistryObject<GearBlueprintItem> CHESTPLATE_TEMPLATE = registerGearBlueprint(GearType.CHESTPLATE, true);
    public static final ItemRegistryObject<GearBlueprintItem> LEGGINGS_TEMPLATE = registerGearBlueprint(GearType.LEGGINGS, true);
    public static final ItemRegistryObject<GearBlueprintItem> BOOTS_TEMPLATE = registerGearBlueprint(GearType.BOOTS, true);
    //endregion

    // Compound Parts
    public static final ItemRegistryObject<CompoundPartItem> ROD = register("rod", () ->
            new CompoundPartItem(PartType.ROD, baseProps()));
    public static final ItemRegistryObject<CompoundPartItem> LONG_ROD = register("long_rod", () ->
            new CompoundPartItem(PartType.ROD, baseProps()));
    public static final ItemRegistryObject<CompoundPartItem> TIP = register("tip", () ->
            new CompoundPartItem(PartType.TIP, 1, baseProps()));
    public static final ItemRegistryObject<CompoundPartItem> GRIP = register("grip", () ->
            new CompoundPartItem(PartType.GRIP, baseProps()));
    public static final ItemRegistryObject<CompoundPartItem> BINDING = register("binding", () ->
            new CompoundPartItem(PartType.BINDING, baseProps()));

    // Repair Kits
    public static final ItemRegistryObject<Item> CRUDE_REPAIR_KIT = register("crude_repair_kit", () ->
            new RepairKitItem(1000, baseProps()));
    public static final ItemRegistryObject<Item> STURDY_REPAIR_KIT = register("sturdy_repair_kit", () ->
            new RepairKitItem(10_000, baseProps()));
    public static final ItemRegistryObject<Item> CRIMSON_REPAIR_KIT = register("crimson_repair_kit", () ->
            new RepairKitItem(100_000, baseProps()));

    static {
        CraftingItems.register(Registration.ITEMS);
    }

    public static final ItemRegistryObject<CustomTippedUpgrade> CUSTOM_TIPPED_UPGRADE = register("custom_tipped_upgrade", CustomTippedUpgrade::new);
    public static final ItemRegistryObject<Item> PEBBLE = register("pebble", () -> new SlingshotAmmoItem(baseProps()));

    public static final ItemRegistryObject<BlockNamedItem> FLAXSEEDS = register("flaxseeds", () ->
            new BlockNamedItem(ModBlocks.FLAX_PLANT.get(), baseProps()));
    public static final ItemRegistryObject<Item> NETHER_BANANA = register("nether_banana", () ->
            new Item(baseProps()
                    .food(new Food.Builder().hunger(5).saturation(0.4f).build())));
    public static final ItemRegistryObject<Item> GOLDEN_NETHER_BANANA = register("golden_nether_banana", () ->
            new Item(baseProps()
                    .food(new Food.Builder().hunger(10).saturation(1.0f)
                            .setAlwaysEdible()
                            .effect(() -> new EffectInstance(Effects.FIRE_RESISTANCE, TimeUtils.ticksFromMinutes(10)), 1f)
                            .effect(() -> new EffectInstance(Effects.RESISTANCE, TimeUtils.ticksFromMinutes(5)), 1f)
                            .effect(() -> new EffectInstance(Effects.REGENERATION, TimeUtils.ticksFromSeconds(10)), 1f)
                            .build())));

    public static final ItemRegistryObject<CoreSword> SWORD = register("sword", () -> new CoreSword());
    public static final ItemRegistryObject<CoreDagger> DAGGER = register("dagger", () -> new CoreDagger());
    public static final ItemRegistryObject<CoreKatana> KATANA = register("katana", () -> new CoreKatana());
    public static final ItemRegistryObject<CoreMachete> MACHETE = register("machete", () -> new CoreMachete());
    public static final ItemRegistryObject<CoreSpear> SPEAR = register("spear", () -> new CoreSpear());
    public static final ItemRegistryObject<CorePickaxe> PICKAXE = register("pickaxe", () -> new CorePickaxe());
    public static final ItemRegistryObject<CoreShovel> SHOVEL = register("shovel", () -> new CoreShovel());
    public static final ItemRegistryObject<CoreAxe> AXE = register("axe", () -> new CoreAxe());
    public static final ItemRegistryObject<CorePaxel> PAXEL = register("paxel", () -> new CorePaxel());
    public static final ItemRegistryObject<CoreHammer> HAMMER = register("hammer", () -> new CoreHammer());
    public static final ItemRegistryObject<CoreExcavator> EXCAVATOR = register("excavator", () -> new CoreExcavator());
    public static final ItemRegistryObject<CoreLumberAxe> LUMBER_AXE = register("lumber_axe", () -> new CoreLumberAxe());
    public static final ItemRegistryObject<CoreMattock> MATTOCK = register("mattock", () -> new CoreMattock());
    public static final ItemRegistryObject<CoreSickle> SICKLE = register("sickle", () -> new CoreSickle());
    public static final ItemRegistryObject<CoreShears> SHEARS = register("shears", () -> new CoreShears());
    public static final ItemRegistryObject<CoreBow> BOW = register("bow", () -> new CoreBow());
    public static final ItemRegistryObject<CoreCrossbow> CROSSBOW = register("crossbow", () -> new CoreCrossbow());
    public static final ItemRegistryObject<CoreSlingshot> SLINGSHOT = register("slingshot", () -> new CoreSlingshot());
    public static final ItemRegistryObject<CoreShield> SHIELD = register("shield", () -> new CoreShield());

    public static final ItemRegistryObject<CoreArmor> HELMET = register("helmet", () -> new CoreArmor(EquipmentSlotType.HEAD));
    public static final ItemRegistryObject<CoreArmor> CHESTPLATE = register("chestplate", () -> new CoreArmor(EquipmentSlotType.CHEST));
    public static final ItemRegistryObject<CoreArmor> LEGGINGS = register("leggings", () -> new CoreArmor(EquipmentSlotType.LEGS));
    public static final ItemRegistryObject<CoreArmor> BOOTS = register("boots", () -> new CoreArmor(EquipmentSlotType.FEET));

    private ModItems() {}

    static void register() {}

    static Item.Properties baseProps() {
        return new Item.Properties().group(SilentGear.ITEM_GROUP);
    }

    private static <T extends Item> ItemRegistryObject<T> register(String name, Supplier<T> item) {
        return new ItemRegistryObject<>(Registration.ITEMS.register(name, item));
    }

    private static ItemRegistryObject<GearBlueprintItem> registerGearBlueprint(GearType gearType, boolean singleUse) {
        // TODO: Flip the name to be geartype_blueprint
        String name = (singleUse ? "template" : "blueprint") + "_" + gearType.getName();
        return register(name, () -> new GearBlueprintItem(gearType, singleUse, baseProps()));
    }

    private static ItemRegistryObject<PartBlueprintItem> registerPartBlueprint(PartType partType, boolean singleUse) {
        // TODO: Flip the name to be parttype_blueprint
        String name = (singleUse ? "template" : "blueprint") + "_" + partType.getName().getPath();
        return register(name, () -> new PartBlueprintItem(partType, singleUse, baseProps()));
    }
}
