package net.silentchaos512.gear.setup;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.config.Config;
import net.silentchaos512.gear.item.*;
import net.silentchaos512.gear.item.blueprint.GearBlueprintItem;
import net.silentchaos512.gear.item.blueprint.PartBlueprintItem;
import net.silentchaos512.gear.item.blueprint.book.BlueprintBookItem;
import net.silentchaos512.gear.item.gear.*;
import net.silentchaos512.lib.util.TimeUtils;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@SuppressWarnings({"unused", "OverlyCoupledClass"})
public final class SgItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SilentGear.MOD_ID);

    public static final DeferredItem<GuideBookItem> GUIDE_BOOK = register("guide_book", () ->
            new GuideBookItem(unstackableProps()));

    public static final DeferredItem<BlueprintPackageItem> BLUEPRINT_PACKAGE = register("blueprint_package", () ->
            new BlueprintPackageItem(SilentGear.getId("starter_blueprints")));

    public static final DeferredItem<Item> MOD_KIT = register("mod_kit", () ->
            new ModKitItem(unstackableProps().rarity(Rarity.UNCOMMON)));

    // Repair Kits
    public static final DeferredItem<Item> VERY_CRUDE_REPAIR_KIT = register("very_crude_repair_kit", () -> new RepairKitItem(
            Config.Common.repairKitVeryCrudeCapacity::get,
            Config.Common.repairKitVeryCrudeEfficiency::get,
            unstackableProps().rarity(Rarity.COMMON)));
    public static final DeferredItem<Item> CRUDE_REPAIR_KIT = register("crude_repair_kit", () -> new RepairKitItem(
            Config.Common.repairKitCrudeCapacity::get,
            Config.Common.repairKitCrudeEfficiency::get,
            unstackableProps().rarity(Rarity.COMMON)));
    public static final DeferredItem<Item> STURDY_REPAIR_KIT = register("sturdy_repair_kit", () -> new RepairKitItem(
            Config.Common.repairKitSturdyCapacity::get,
            Config.Common.repairKitSturdyEfficiency::get,
            unstackableProps().rarity(Rarity.UNCOMMON)));
    public static final DeferredItem<Item> CRIMSON_REPAIR_KIT = register("crimson_repair_kit", () -> new RepairKitItem(
            Config.Common.repairKitCrimsonCapacity::get,
            Config.Common.repairKitCrimsonEfficiency::get,
            unstackableProps().rarity(Rarity.RARE)));
    public static final DeferredItem<Item> AZURE_REPAIR_KIT = register("azure_repair_kit", () -> new RepairKitItem(
            Config.Common.repairKitAzureCapacity::get,
            Config.Common.repairKitAzureEfficiency::get,
            unstackableProps().rarity(Rarity.EPIC)));

    public static final DeferredItem<Item> COATING_SMITHING_TEMPLATE = register("coating_smithing_template", () ->
            new Item(baseProps()));

    //region Blueprints and templates
    public static final DeferredItem<BlueprintBookItem> BLUEPRINT_BOOK = register("blueprint_book", () ->
            new BlueprintBookItem(unstackableProps().rarity(Rarity.UNCOMMON)));
    // Blueprints
    public static final DeferredItem<PartBlueprintItem> JEWELER_TOOLS = register("jeweler_tools", () ->
            new JewelerKitItem(PartType.ADORNMENT, false, unstackableProps()));
    public static final DeferredItem<PartBlueprintItem> ROD_BLUEPRINT = registerPartBlueprint(PartType.ROD, false);
    public static final DeferredItem<PartBlueprintItem> TIP_BLUEPRINT = registerPartBlueprint(PartType.TIP, false);
    public static final DeferredItem<PartBlueprintItem> COATING_BLUEPRINT = registerPartBlueprint(PartType.COATING, false);
    public static final DeferredItem<PartBlueprintItem> GRIP_BLUEPRINT = registerPartBlueprint(PartType.GRIP, false);
    public static final DeferredItem<PartBlueprintItem> BINDING_BLUEPRINT = registerPartBlueprint(PartType.BINDING, false);
    public static final DeferredItem<PartBlueprintItem> LINING_BLUEPRINT = registerPartBlueprint(PartType.LINING, false);
    public static final DeferredItem<PartBlueprintItem> CORD_BLUEPRINT = registerPartBlueprint(PartType.CORD, false);
    public static final DeferredItem<PartBlueprintItem> FLETCHING_BLUEPRINT = registerPartBlueprint(PartType.FLETCHING, false);
    public static final DeferredItem<GearBlueprintItem> SWORD_BLUEPRINT = registerGearBlueprint(GearType.SWORD, false);
    public static final DeferredItem<GearBlueprintItem> KATANA_BLUEPRINT = registerGearBlueprint(GearType.KATANA, false);
    public static final DeferredItem<GearBlueprintItem> MACHETE_BLUEPRINT = registerGearBlueprint(GearType.MACHETE, false);
    public static final DeferredItem<GearBlueprintItem> SPEAR_BLUEPRINT = registerGearBlueprint(GearType.SPEAR, false);
    public static final DeferredItem<GearBlueprintItem> TRIDENT_BLUEPRINT = registerGearBlueprint(GearType.TRIDENT, false);
    public static final DeferredItem<GearBlueprintItem> KNIFE_BLUEPRINT = registerGearBlueprint(GearType.KNIFE, false);
    public static final DeferredItem<GearBlueprintItem> DAGGER_BLUEPRINT = registerGearBlueprint(GearType.DAGGER, false);
    public static final DeferredItem<GearBlueprintItem> PICKAXE_BLUEPRINT = registerGearBlueprint(GearType.PICKAXE, false);
    public static final DeferredItem<GearBlueprintItem> SHOVEL_BLUEPRINT = registerGearBlueprint(GearType.SHOVEL, false);
    public static final DeferredItem<GearBlueprintItem> AXE_BLUEPRINT = registerGearBlueprint(GearType.AXE, false);
    public static final DeferredItem<GearBlueprintItem> PAXEL_BLUEPRINT = registerGearBlueprint(GearType.PAXEL, false);
    public static final DeferredItem<GearBlueprintItem> HAMMER_BLUEPRINT = registerGearBlueprint(GearType.HAMMER, false);
    public static final DeferredItem<GearBlueprintItem> EXCAVATOR_BLUEPRINT = registerGearBlueprint(GearType.EXCAVATOR, false);
    public static final DeferredItem<GearBlueprintItem> SAW_BLUEPRINT = registerGearBlueprint(GearType.SAW, false);
    public static final DeferredItem<GearBlueprintItem> HOE_BLUEPRINT = registerGearBlueprint(GearType.HOE, false);
    public static final DeferredItem<GearBlueprintItem> MATTOCK_BLUEPRINT = registerGearBlueprint(GearType.MATTOCK, false);
    public static final DeferredItem<GearBlueprintItem> PROSPECTOR_HAMMER_BLUEPRINT = registerGearBlueprint(GearType.PROSPECTOR_HAMMER, false);
    public static final DeferredItem<GearBlueprintItem> SICKLE_BLUEPRINT = registerGearBlueprint(GearType.SICKLE, false);
    public static final DeferredItem<GearBlueprintItem> SHEARS_BLUEPRINT = registerGearBlueprint(GearType.SHEARS, false);
    public static final DeferredItem<GearBlueprintItem> FISHING_ROD_BLUEPRINT = registerGearBlueprint(GearType.FISHING_ROD, false);
    public static final DeferredItem<GearBlueprintItem> BOW_BLUEPRINT = registerGearBlueprint(GearType.BOW, false);
    public static final DeferredItem<GearBlueprintItem> CROSSBOW_BLUEPRINT = registerGearBlueprint(GearType.CROSSBOW, false);
    public static final DeferredItem<GearBlueprintItem> SLINGSHOT_BLUEPRINT = registerGearBlueprint(GearType.SLINGSHOT, false);
    public static final DeferredItem<GearBlueprintItem> SHIELD_BLUEPRINT = registerGearBlueprint(GearType.SHIELD, false);
    public static final DeferredItem<GearBlueprintItem> HELMET_BLUEPRINT = registerGearBlueprint(GearType.HELMET, false);
    public static final DeferredItem<GearBlueprintItem> CHESTPLATE_BLUEPRINT = registerGearBlueprint(GearType.CHESTPLATE, false);
    public static final DeferredItem<GearBlueprintItem> LEGGINGS_BLUEPRINT = registerGearBlueprint(GearType.LEGGINGS, false);
    public static final DeferredItem<GearBlueprintItem> BOOTS_BLUEPRINT = registerGearBlueprint(GearType.BOOTS, false);
    public static final DeferredItem<GearBlueprintItem> ELYTRA_BLUEPRINT = registerGearBlueprint(GearType.ELYTRA, false);
    public static final DeferredItem<GearBlueprintItem> ARROW_BLUEPRINT = registerGearBlueprint(GearType.ARROW, false);
    public static final DeferredItem<GearBlueprintItem> RING_BLUEPRINT = registerGearBlueprint(GearType.RING, false);
    public static final DeferredItem<GearBlueprintItem> BRACELET_BLUEPRINT = registerGearBlueprint(GearType.BRACELET, false);
    // Templates
    public static final DeferredItem<PartBlueprintItem> ROD_TEMPLATE = registerPartBlueprint(PartType.ROD, true);
    public static final DeferredItem<PartBlueprintItem> TIP_TEMPLATE = registerPartBlueprint(PartType.TIP, true);
    public static final DeferredItem<PartBlueprintItem> COATING_TEMPLATE = registerPartBlueprint(PartType.COATING, true);
    public static final DeferredItem<PartBlueprintItem> GRIP_TEMPLATE = registerPartBlueprint(PartType.GRIP, true);
    public static final DeferredItem<PartBlueprintItem> BINDING_TEMPLATE = registerPartBlueprint(PartType.BINDING, true);
    public static final DeferredItem<PartBlueprintItem> LINING_TEMPLATE = registerPartBlueprint(PartType.LINING, true);
    public static final DeferredItem<PartBlueprintItem> CORD_TEMPLATE = registerPartBlueprint(PartType.CORD, true);
    public static final DeferredItem<PartBlueprintItem> FLETCHING_TEMPLATE = registerPartBlueprint(PartType.FLETCHING, true);
    public static final DeferredItem<GearBlueprintItem> SWORD_TEMPLATE = registerGearBlueprint(GearType.SWORD, true);
    public static final DeferredItem<GearBlueprintItem> KATANA_TEMPLATE = registerGearBlueprint(GearType.KATANA, true);
    public static final DeferredItem<GearBlueprintItem> MACHETE_TEMPLATE = registerGearBlueprint(GearType.MACHETE, true);
    public static final DeferredItem<GearBlueprintItem> SPEAR_TEMPLATE = registerGearBlueprint(GearType.SPEAR, true);
    public static final DeferredItem<GearBlueprintItem> TRIDENT_TEMPLATE = registerGearBlueprint(GearType.TRIDENT, true);
    public static final DeferredItem<GearBlueprintItem> KNIFE_TEMPLATE = registerGearBlueprint(GearType.KNIFE, true);
    public static final DeferredItem<GearBlueprintItem> DAGGER_TEMPLATE = registerGearBlueprint(GearType.DAGGER, true);
    public static final DeferredItem<GearBlueprintItem> PICKAXE_TEMPLATE = registerGearBlueprint(GearType.PICKAXE, true);
    public static final DeferredItem<GearBlueprintItem> SHOVEL_TEMPLATE = registerGearBlueprint(GearType.SHOVEL, true);
    public static final DeferredItem<GearBlueprintItem> AXE_TEMPLATE = registerGearBlueprint(GearType.AXE, true);
    public static final DeferredItem<GearBlueprintItem> PAXEL_TEMPLATE = registerGearBlueprint(GearType.PAXEL, true);
    public static final DeferredItem<GearBlueprintItem> HAMMER_TEMPLATE = registerGearBlueprint(GearType.HAMMER, true);
    public static final DeferredItem<GearBlueprintItem> EXCAVATOR_TEMPLATE = registerGearBlueprint(GearType.EXCAVATOR, true);
    public static final DeferredItem<GearBlueprintItem> SAW_TEMPLATE = registerGearBlueprint(GearType.SAW, true);
    public static final DeferredItem<GearBlueprintItem> HOE_TEMPLATE = registerGearBlueprint(GearType.HOE, true);
    public static final DeferredItem<GearBlueprintItem> MATTOCK_TEMPLATE = registerGearBlueprint(GearType.MATTOCK, true);
    public static final DeferredItem<GearBlueprintItem> PROSPECTOR_HAMMER_TEMPLATE = registerGearBlueprint(GearType.PROSPECTOR_HAMMER, true);
    public static final DeferredItem<GearBlueprintItem> SICKLE_TEMPLATE = registerGearBlueprint(GearType.SICKLE, true);
    public static final DeferredItem<GearBlueprintItem> SHEARS_TEMPLATE = registerGearBlueprint(GearType.SHEARS, true);
    public static final DeferredItem<GearBlueprintItem> FISHING_ROD_TEMPLATE = registerGearBlueprint(GearType.FISHING_ROD, true);
    public static final DeferredItem<GearBlueprintItem> BOW_TEMPLATE = registerGearBlueprint(GearType.BOW, true);
    public static final DeferredItem<GearBlueprintItem> CROSSBOW_TEMPLATE = registerGearBlueprint(GearType.CROSSBOW, true);
    public static final DeferredItem<GearBlueprintItem> SLINGSHOT_TEMPLATE = registerGearBlueprint(GearType.SLINGSHOT, true);
    public static final DeferredItem<GearBlueprintItem> SHIELD_TEMPLATE = registerGearBlueprint(GearType.SHIELD, true);
    public static final DeferredItem<GearBlueprintItem> HELMET_TEMPLATE = registerGearBlueprint(GearType.HELMET, true);
    public static final DeferredItem<GearBlueprintItem> CHESTPLATE_TEMPLATE = registerGearBlueprint(GearType.CHESTPLATE, true);
    public static final DeferredItem<GearBlueprintItem> LEGGINGS_TEMPLATE = registerGearBlueprint(GearType.LEGGINGS, true);
    public static final DeferredItem<GearBlueprintItem> BOOTS_TEMPLATE = registerGearBlueprint(GearType.BOOTS, true);
    public static final DeferredItem<GearBlueprintItem> ELYTRA_TEMPLATE = registerGearBlueprint(GearType.ELYTRA, true);
    public static final DeferredItem<GearBlueprintItem> ARROW_TEMPLATE = registerGearBlueprint(GearType.ARROW, true);
    public static final DeferredItem<GearBlueprintItem> RING_TEMPLATE = registerGearBlueprint(GearType.RING, true);
    public static final DeferredItem<GearBlueprintItem> BRACELET_TEMPLATE = registerGearBlueprint(GearType.BRACELET, true);
    //endregion

    //region Compound Parts and Tool Heads
    // Tool Heads
    public static final DeferredItem<MainPartItem> SWORD_BLADE = registerCompoundPart("sword_blade", () ->
            new MainPartItem(GearType.SWORD, unstackableProps()));
    public static final DeferredItem<MainPartItem> KATANA_BLADE = registerCompoundPart("katana_blade", () ->
            new MainPartItem(GearType.KATANA, unstackableProps()));
    public static final DeferredItem<MainPartItem> MACHETE_BLADE = registerCompoundPart("machete_blade", () ->
            new MainPartItem(GearType.MACHETE, unstackableProps()));
    public static final DeferredItem<MainPartItem> SPEAR_TIP = registerCompoundPart("spear_tip", () ->
            new MainPartItem(GearType.SPEAR, unstackableProps()));
    public static final DeferredItem<MainPartItem> TRIDENT_PRONGS = registerCompoundPart("trident_prongs", () ->
            new MainPartItem(GearType.TRIDENT, unstackableProps()));
    public static final DeferredItem<MainPartItem> KNIFE_BLADE = registerCompoundPart("knife_blade", () ->
            new MainPartItem(GearType.KNIFE, unstackableProps()));
    public static final DeferredItem<MainPartItem> DAGGER_BLADE = registerCompoundPart("dagger_blade", () ->
            new MainPartItem(GearType.DAGGER, unstackableProps()));
    public static final DeferredItem<MainPartItem> PICKAXE_HEAD = registerCompoundPart("pickaxe_head", () ->
            new MainPartItem(GearType.PICKAXE, unstackableProps()));
    public static final DeferredItem<MainPartItem> SHOVEL_HEAD = registerCompoundPart("shovel_head", () ->
            new MainPartItem(GearType.SHOVEL, unstackableProps()));
    public static final DeferredItem<MainPartItem> AXE_HEAD = registerCompoundPart("axe_head", () ->
            new MainPartItem(GearType.AXE, unstackableProps()));
    public static final DeferredItem<MainPartItem> PAXEL_HEAD = registerCompoundPart("paxel_head", () ->
            new MainPartItem(GearType.PAXEL, unstackableProps()));
    public static final DeferredItem<MainPartItem> HAMMER_HEAD = registerCompoundPart("hammer_head", () ->
            new MainPartItem(GearType.HAMMER, unstackableProps()));
    public static final DeferredItem<MainPartItem> EXCAVATOR_HEAD = registerCompoundPart("excavator_head", () ->
            new MainPartItem(GearType.EXCAVATOR, unstackableProps()));
    public static final DeferredItem<MainPartItem> SAW_BLADE = registerCompoundPart("saw_blade", () ->
            new MainPartItem(GearType.SAW, unstackableProps()));
    public static final DeferredItem<MainPartItem> HOE_HEAD = registerCompoundPart("hoe_head", () ->
            new MainPartItem(GearType.HOE, unstackableProps()));
    public static final DeferredItem<MainPartItem> MATTOCK_HEAD = registerCompoundPart("mattock_head", () ->
            new MainPartItem(GearType.MATTOCK, unstackableProps()));
    public static final DeferredItem<MainPartItem> PROSPECTOR_HAMMER_HEAD = registerCompoundPart("prospector_hammer_head", () ->
            new MainPartItem(GearType.PROSPECTOR_HAMMER, unstackableProps()));
    public static final DeferredItem<MainPartItem> SICKLE_BLADE = registerCompoundPart("sickle_blade", () ->
            new MainPartItem(GearType.SICKLE, unstackableProps()));
    public static final DeferredItem<MainPartItem> SHEARS_BLADES = registerCompoundPart("shears_blades", () ->
            new MainPartItem(GearType.SHEARS, unstackableProps()));
    public static final DeferredItem<MainPartItem> FISHING_REEL_AND_HOOK = registerCompoundPart("fishing_reel_and_hook", () ->
            new MainPartItem(GearType.FISHING_ROD, unstackableProps()));
    public static final DeferredItem<MainPartItem> BOW_LIMBS = registerCompoundPart("bow_limbs", () ->
            new MainPartItem(GearType.BOW, unstackableProps()));
    public static final DeferredItem<MainPartItem> CROSSBOW_LIMBS = registerCompoundPart("crossbow_limbs", () ->
            new MainPartItem(GearType.CROSSBOW, unstackableProps()));
    public static final DeferredItem<MainPartItem> SLINGSHOT_LIMBS = registerCompoundPart("slingshot_limbs", () ->
            new MainPartItem(GearType.SLINGSHOT, unstackableProps()));
    public static final DeferredItem<MainPartItem> SHIELD_PLATE = registerCompoundPart("shield_plate", () ->
            new MainPartItem(GearType.SHIELD, new Item.Properties().stacksTo(1)));
    public static final DeferredItem<MainPartItem> HELMET_PLATES = registerCompoundPart("helmet_plates", () ->
            new MainPartItem(GearType.HELMET, unstackableProps()));
    public static final DeferredItem<MainPartItem> CHESTPLATE_PLATES = registerCompoundPart("chestplate_plates", () ->
            new MainPartItem(GearType.CHESTPLATE, unstackableProps()));
    public static final DeferredItem<MainPartItem> LEGGING_PLATES = registerCompoundPart("legging_plates", () ->
            new MainPartItem(GearType.LEGGINGS, unstackableProps()));
    public static final DeferredItem<MainPartItem> BOOT_PLATES = registerCompoundPart("boot_plates", () ->
            new MainPartItem(GearType.BOOTS, unstackableProps()));
    public static final DeferredItem<MainPartItem> ELYTRA_WINGS = registerCompoundPart("elytra_wings", () ->
            new MainPartItem(GearType.ELYTRA, unstackableProps()));
    public static final DeferredItem<MainPartItem> ARROW_HEADS = registerCompoundPart("arrow_heads", () ->
            new MainPartItem(GearType.ARROW, unstackableProps()));
    public static final DeferredItem<MainPartItem> RING_SHANK = registerCompoundPart("ring_shank", () ->
            new MainPartItem(GearType.RING, unstackableProps()));
    public static final DeferredItem<MainPartItem> BRACELET_BAND = registerCompoundPart("bracelet_band", () ->
            new MainPartItem(GearType.BRACELET, unstackableProps()));
    // Compound Parts
    public static final DeferredItem<CompoundPartItem> ROD = registerCompoundPart("rod", () ->
            new CompoundPartItem(PartType.ROD, baseProps()));
    public static final DeferredItem<CompoundPartItem> TIP = registerCompoundPart("tip", () ->
            new CompoundPartItem(PartType.TIP, baseProps()));
    public static final DeferredItem<CompoundPartItem> COATING = registerCompoundPart("coating", () ->
            new CompoundPartItem(PartType.COATING, baseProps()));
    public static final DeferredItem<CompoundPartItem> GRIP = registerCompoundPart("grip", () ->
            new CompoundPartItem(PartType.GRIP, baseProps()));
    public static final DeferredItem<CompoundPartItem> BINDING = registerCompoundPart("binding", () ->
            new CompoundPartItem(PartType.BINDING, baseProps()));
    public static final DeferredItem<CompoundPartItem> LINING = registerCompoundPart("lining", () ->
            new CompoundPartItem(PartType.LINING, baseProps()));
    public static final DeferredItem<CompoundPartItem> CORD = registerCompoundPart("cord", () ->
            new CompoundPartItem(PartType.CORD, baseProps()));
    public static final DeferredItem<CompoundPartItem> FLETCHING = registerCompoundPart("fletching", () ->
            new CompoundPartItem(PartType.FLETCHING, baseProps()));
    public static final DeferredItem<CompoundPartItem> ADORNMENT = registerCompoundPart("adornment", () ->
            new CompoundPartItem(PartType.ADORNMENT, baseProps()));
    //endregion

    // Compound materials
    public static final DeferredItem<CompoundMaterialItem> ALLOY_INGOT = register("alloy_ingot", () ->
            new CompoundMaterialItem(baseProps()));
    public static final DeferredItem<CompoundMaterialItem> HYBRID_GEM = register("hybrid_gem", () ->
            new CompoundMaterialItem(baseProps()));
    public static final DeferredItem<CompoundMaterialItem> MIXED_FABRIC = register("mixed_fabric", () ->
            new CompoundMaterialItem(baseProps()));

    // Custom materials
    public static final DeferredItem<CustomMaterialItem> CUSTOM_INGOT = register("custom_ingot", () ->
            new CustomMaterialItem(baseProps()));
    public static final DeferredItem<CustomMaterialItem> CUSTOM_GEM = register("custom_gem", () ->
            new CustomMaterialItem(baseProps()));

    public static final DeferredItem<CraftedMaterialItem> SHEET_METAL = register("sheet_metal", () ->
            new CraftedMaterialItem(baseProps()));

    static {
        CraftingItems.register(ITEMS);
    }

    public static final DeferredItem<FragmentItem> FRAGMENT = register("fragment", () -> new FragmentItem(baseProps()));

    public static final DeferredItem<Item> PEBBLE = register("pebble", () -> new SlingshotAmmoItem(baseProps()));

    public static final DeferredItem<ItemNameBlockItem> FLAX_SEEDS = register("flax_seeds", () ->
            new SeedItem(SgBlocks.FLAX_PLANT.get(), baseProps()));
    public static final DeferredItem<ItemNameBlockItem> FLUFFY_SEEDS = register("fluffy_seeds", () ->
            new SeedItem(SgBlocks.FLUFFY_PLANT.get(), baseProps()));

    public static final DeferredItem<Item> NETHER_BANANA = register("nether_banana", () ->
            new Item(baseProps()
                    .food(new FoodProperties.Builder().nutrition(5).saturationMod(0.4f).build())));
    public static final DeferredItem<Item> GOLDEN_NETHER_BANANA = register("golden_nether_banana", () ->
            new Item(baseProps()
                    .food(new FoodProperties.Builder().nutrition(10).saturationMod(1.0f)
                            .alwaysEat()
                            .effect(() -> new MobEffectInstance(MobEffects.FIRE_RESISTANCE, TimeUtils.ticksFromMinutes(10)), 1f)
                            .effect(() -> new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, TimeUtils.ticksFromMinutes(5)), 1f)
                            .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, TimeUtils.ticksFromSeconds(10)), 1f)
                            .build())));
    public static final DeferredItem<Item> NETHERWOOD_CHARCOAL = register("netherwood_charcoal", () ->
            new Item(baseProps()) {
                @Override
                public int getBurnTime(ItemStack itemStack, @Nullable RecipeType<?> recipeType) {
                    return Config.Common.netherwoodCharcoalBurnTime.get();
                }
            });

    public static final DeferredItem<GearSwordItem> SWORD = register("sword", () -> new GearSwordItem(GearType.SWORD));
    public static final DeferredItem<GearSwordItem> KATANA = register("katana", () -> new GearSwordItem(GearType.KATANA));
    public static final DeferredItem<GearMacheteItem> MACHETE = register("machete", () -> new GearMacheteItem(GearType.MACHETE));
    public static final DeferredItem<GearSwordItem> SPEAR = register("spear", () -> new GearSwordItem(GearType.SPEAR));
    public static final DeferredItem<GearTridentItem> TRIDENT = register("trident", () -> new GearTridentItem());
    public static final DeferredItem<GearDaggerItem> KNIFE = register("knife", () -> new GearDaggerItem(GearType.KNIFE));
    public static final DeferredItem<GearDaggerItem> DAGGER = register("dagger", () -> new GearDaggerItem(GearType.DAGGER));
    public static final DeferredItem<GearPickaxeItem> PICKAXE = register("pickaxe", () -> new GearPickaxeItem(GearType.PICKAXE));
    public static final DeferredItem<GearShovelItem> SHOVEL = register("shovel", () -> new GearShovelItem(GearType.SHOVEL));
    public static final DeferredItem<GearAxeItem> AXE = register("axe", () -> new GearAxeItem(GearType.AXE));
    public static final DeferredItem<GearPaxelItem> PAXEL = register("paxel", () -> new GearPaxelItem(GearType.PAXEL));
    public static final DeferredItem<GearHammerItem> HAMMER = register("hammer", () -> new GearHammerItem(GearType.HAMMER));
    public static final DeferredItem<GearExcavatorItem> EXCAVATOR = register("excavator", () -> new GearExcavatorItem(GearType.EXCAVATOR));
    public static final DeferredItem<GearSawItem> SAW = register("saw", () -> new GearSawItem(GearType.SAW));
    public static final DeferredItem<GearProspectorHammerItem> PROSPECTOR_HAMMER = register("prospector_hammer", () -> new GearProspectorHammerItem(GearType.PROSPECTOR_HAMMER));
    public static final DeferredItem<GearHoeItem> HOE = register("hoe", () -> new GearHoeItem(GearType.HOE));
    public static final DeferredItem<GearMattockItem> MATTOCK = register("mattock", () -> new GearMattockItem());
    public static final DeferredItem<GearSickleItem> SICKLE = register("sickle", () -> new GearSickleItem(GearType.SICKLE));
    public static final DeferredItem<GearShearsItem> SHEARS = register("shears", () -> new GearShearsItem());
    public static final DeferredItem<GearFishingRodItem> FISHING_ROD = register("fishing_rod", GearFishingRodItem::new);
    public static final DeferredItem<GearBowItem> BOW = register("bow", () -> new GearBowItem());
    public static final DeferredItem<GearCrossbowItem> CROSSBOW = register("crossbow", () -> new GearCrossbowItem());
    public static final DeferredItem<GearSlingshotItem> SLINGSHOT = register("slingshot", () -> new GearSlingshotItem());
    public static final DeferredItem<GearShieldItem> SHIELD = register("shield", () -> new GearShieldItem());
    public static final DeferredItem<GearArrowItem> ARROW = register("arrow", () -> new GearArrowItem(unstackableProps()));

    public static final DeferredItem<GearArmorItem> HELMET = register("helmet", () -> new GearArmorItem(ArmorItem.Type.HELMET));
    public static final DeferredItem<GearArmorItem> CHESTPLATE = register("chestplate", () -> new GearArmorItem(ArmorItem.Type.CHESTPLATE));
    public static final DeferredItem<GearArmorItem> LEGGINGS = register("leggings", () -> new GearArmorItem(ArmorItem.Type.LEGGINGS));
    public static final DeferredItem<GearArmorItem> BOOTS = register("boots", () -> new GearArmorItem(ArmorItem.Type.BOOTS));

    public static final DeferredItem<GearElytraItem> ELYTRA = register("elytra", () -> new GearElytraItem(unstackableProps()));

    public static final DeferredItem<GearCurioItem> RING = register("ring", () ->
            new GearCurioItem(GearType.RING, "ring", unstackableProps()));
    public static final DeferredItem<GearCurioItem> BRACELET = register("bracelet", () ->
            new GearCurioItem(GearType.BRACELET, "bracelet", unstackableProps()));

    private SgItems() {}

    private static Item.Properties baseProps() {
        return new Item.Properties();
    }

    private static Item.Properties unstackableProps() {
        return baseProps().stacksTo(1);
    }

    private static <T extends Item> DeferredItem<T> register(String name, Supplier<T> item) {
        return ITEMS.register(name, item);
    }

    private static <T extends CompoundPartItem> DeferredItem<T> registerCompoundPart(String name, Supplier<T> item) {
        return register(name, item);
    }

    private static DeferredItem<GearBlueprintItem> registerGearBlueprint(GearType gearType, boolean singleUse) {
        String name = gearType.getName() + "_" + (singleUse ? "template" : "blueprint");
        return register(name, () -> new GearBlueprintItem(gearType, singleUse, baseProps()));
    }

    private static DeferredItem<PartBlueprintItem> registerPartBlueprint(PartType partType, boolean singleUse) {
        String name = partType.getName().getPath() + "_" + (singleUse ? "template" : "blueprint");
        return register(name, () -> new PartBlueprintItem(partType, singleUse, baseProps()));
    }

    @SuppressWarnings("unchecked")
    public static <T> Collection<T> getItems(Class<T> clazz) {
        return ITEMS.getEntries().stream()
                .map(DeferredHolder::get)
                .filter(clazz::isInstance)
                .map(item -> (T) item)
                .collect(Collectors.toList());
    }

    public static Collection<Item> getItems(Predicate<Item> predicate) {
        return ITEMS.getEntries().stream()
                .map(DeferredHolder::get)
                .filter(predicate)
                .collect(Collectors.toList());
    }
}
