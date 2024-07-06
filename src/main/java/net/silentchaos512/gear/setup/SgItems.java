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
import net.silentchaos512.gear.setup.gear.GearTypes;
import net.silentchaos512.gear.setup.gear.PartTypes;
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
            new JewelerKitItem(PartTypes.SETTING.get(), false, unstackableProps()));
    public static final DeferredItem<PartBlueprintItem> ROD_BLUEPRINT = registerPartBlueprint(PartTypes.ROD.get(), false);
    public static final DeferredItem<PartBlueprintItem> TIP_BLUEPRINT = registerPartBlueprint(PartTypes.TIP.get(), false);
    public static final DeferredItem<PartBlueprintItem> COATING_BLUEPRINT = registerPartBlueprint(PartTypes.COATING.get(), false);
    public static final DeferredItem<PartBlueprintItem> GRIP_BLUEPRINT = registerPartBlueprint(PartTypes.GRIP.get(), false);
    public static final DeferredItem<PartBlueprintItem> BINDING_BLUEPRINT = registerPartBlueprint(PartTypes.BINDING.get(), false);
    public static final DeferredItem<PartBlueprintItem> LINING_BLUEPRINT = registerPartBlueprint(PartTypes.LINING.get(), false);
    public static final DeferredItem<PartBlueprintItem> CORD_BLUEPRINT = registerPartBlueprint(PartTypes.CORD.get(), false);
    public static final DeferredItem<PartBlueprintItem> FLETCHING_BLUEPRINT = registerPartBlueprint(PartTypes.FLETCHING.get(), false);
    public static final DeferredItem<GearBlueprintItem> SWORD_BLUEPRINT = registerGearBlueprint(GearTypes.SWORD.get(), false);
    public static final DeferredItem<GearBlueprintItem> KATANA_BLUEPRINT = registerGearBlueprint(GearTypes.KATANA.get(), false);
    public static final DeferredItem<GearBlueprintItem> MACHETE_BLUEPRINT = registerGearBlueprint(GearTypes.MACHETE.get(), false);
    public static final DeferredItem<GearBlueprintItem> SPEAR_BLUEPRINT = registerGearBlueprint(GearTypes.SPEAR.get(), false);
    public static final DeferredItem<GearBlueprintItem> TRIDENT_BLUEPRINT = registerGearBlueprint(GearTypes.TRIDENT.get(), false);
    public static final DeferredItem<GearBlueprintItem> KNIFE_BLUEPRINT = registerGearBlueprint(GearTypes.KNIFE.get(), false);
    public static final DeferredItem<GearBlueprintItem> DAGGER_BLUEPRINT = registerGearBlueprint(GearTypes.DAGGER.get(), false);
    public static final DeferredItem<GearBlueprintItem> PICKAXE_BLUEPRINT = registerGearBlueprint(GearTypes.PICKAXE.get(), false);
    public static final DeferredItem<GearBlueprintItem> SHOVEL_BLUEPRINT = registerGearBlueprint(GearTypes.SHOVEL.get(), false);
    public static final DeferredItem<GearBlueprintItem> AXE_BLUEPRINT = registerGearBlueprint(GearTypes.AXE.get(), false);
    public static final DeferredItem<GearBlueprintItem> PAXEL_BLUEPRINT = registerGearBlueprint(GearTypes.PAXEL.get(), false);
    public static final DeferredItem<GearBlueprintItem> HAMMER_BLUEPRINT = registerGearBlueprint(GearTypes.HAMMER.get(), false);
    public static final DeferredItem<GearBlueprintItem> EXCAVATOR_BLUEPRINT = registerGearBlueprint(GearTypes.EXCAVATOR.get(), false);
    public static final DeferredItem<GearBlueprintItem> SAW_BLUEPRINT = registerGearBlueprint(GearTypes.SAW.get(), false);
    public static final DeferredItem<GearBlueprintItem> HOE_BLUEPRINT = registerGearBlueprint(GearTypes.HOE.get(), false);
    public static final DeferredItem<GearBlueprintItem> MATTOCK_BLUEPRINT = registerGearBlueprint(GearTypes.MATTOCK.get(), false);
    public static final DeferredItem<GearBlueprintItem> PROSPECTOR_HAMMER_BLUEPRINT = registerGearBlueprint(GearTypes.PROSPECTOR_HAMMER.get(), false);
    public static final DeferredItem<GearBlueprintItem> SICKLE_BLUEPRINT = registerGearBlueprint(GearTypes.SICKLE.get(), false);
    public static final DeferredItem<GearBlueprintItem> SHEARS_BLUEPRINT = registerGearBlueprint(GearTypes.SHEARS.get(), false);
    public static final DeferredItem<GearBlueprintItem> FISHING_ROD_BLUEPRINT = registerGearBlueprint(GearTypes.FISHING_ROD.get(), false);
    public static final DeferredItem<GearBlueprintItem> BOW_BLUEPRINT = registerGearBlueprint(GearTypes.BOW.get(), false);
    public static final DeferredItem<GearBlueprintItem> CROSSBOW_BLUEPRINT = registerGearBlueprint(GearTypes.CROSSBOW.get(), false);
    public static final DeferredItem<GearBlueprintItem> SLINGSHOT_BLUEPRINT = registerGearBlueprint(GearTypes.SLINGSHOT.get(), false);
    public static final DeferredItem<GearBlueprintItem> SHIELD_BLUEPRINT = registerGearBlueprint(GearTypes.SHIELD.get(), false);
    public static final DeferredItem<GearBlueprintItem> HELMET_BLUEPRINT = registerGearBlueprint(GearTypes.HELMET.get(), false);
    public static final DeferredItem<GearBlueprintItem> CHESTPLATE_BLUEPRINT = registerGearBlueprint(GearTypes.CHESTPLATE.get(), false);
    public static final DeferredItem<GearBlueprintItem> LEGGINGS_BLUEPRINT = registerGearBlueprint(GearTypes.LEGGINGS.get(), false);
    public static final DeferredItem<GearBlueprintItem> BOOTS_BLUEPRINT = registerGearBlueprint(GearTypes.BOOTS.get(), false);
    public static final DeferredItem<GearBlueprintItem> ELYTRA_BLUEPRINT = registerGearBlueprint(GearTypes.ELYTRA.get(), false);
    public static final DeferredItem<GearBlueprintItem> ARROW_BLUEPRINT = registerGearBlueprint(GearTypes.ARROW.get(), false);
    public static final DeferredItem<GearBlueprintItem> RING_BLUEPRINT = registerGearBlueprint(GearTypes.RING.get(), false);
    public static final DeferredItem<GearBlueprintItem> BRACELET_BLUEPRINT = registerGearBlueprint(GearTypes.BRACELET.get(), false);
    // Templates
    public static final DeferredItem<PartBlueprintItem> ROD_TEMPLATE = registerPartBlueprint(PartTypes.ROD.get(), true);
    public static final DeferredItem<PartBlueprintItem> TIP_TEMPLATE = registerPartBlueprint(PartTypes.TIP.get(), true);
    public static final DeferredItem<PartBlueprintItem> COATING_TEMPLATE = registerPartBlueprint(PartTypes.COATING.get(), true);
    public static final DeferredItem<PartBlueprintItem> GRIP_TEMPLATE = registerPartBlueprint(PartTypes.GRIP.get(), true);
    public static final DeferredItem<PartBlueprintItem> BINDING_TEMPLATE = registerPartBlueprint(PartTypes.BINDING.get(), true);
    public static final DeferredItem<PartBlueprintItem> LINING_TEMPLATE = registerPartBlueprint(PartTypes.LINING.get(), true);
    public static final DeferredItem<PartBlueprintItem> CORD_TEMPLATE = registerPartBlueprint(PartTypes.CORD.get(), true);
    public static final DeferredItem<PartBlueprintItem> FLETCHING_TEMPLATE = registerPartBlueprint(PartTypes.FLETCHING.get(), true);
    public static final DeferredItem<GearBlueprintItem> SWORD_TEMPLATE = registerGearBlueprint(GearTypes.SWORD.get(), true);
    public static final DeferredItem<GearBlueprintItem> KATANA_TEMPLATE = registerGearBlueprint(GearTypes.KATANA.get(), true);
    public static final DeferredItem<GearBlueprintItem> MACHETE_TEMPLATE = registerGearBlueprint(GearTypes.MACHETE.get(), true);
    public static final DeferredItem<GearBlueprintItem> SPEAR_TEMPLATE = registerGearBlueprint(GearTypes.SPEAR.get(), true);
    public static final DeferredItem<GearBlueprintItem> TRIDENT_TEMPLATE = registerGearBlueprint(GearTypes.TRIDENT.get(), true);
    public static final DeferredItem<GearBlueprintItem> KNIFE_TEMPLATE = registerGearBlueprint(GearTypes.KNIFE.get(), true);
    public static final DeferredItem<GearBlueprintItem> DAGGER_TEMPLATE = registerGearBlueprint(GearTypes.DAGGER.get(), true);
    public static final DeferredItem<GearBlueprintItem> PICKAXE_TEMPLATE = registerGearBlueprint(GearTypes.PICKAXE.get(), true);
    public static final DeferredItem<GearBlueprintItem> SHOVEL_TEMPLATE = registerGearBlueprint(GearTypes.SHOVEL.get(), true);
    public static final DeferredItem<GearBlueprintItem> AXE_TEMPLATE = registerGearBlueprint(GearTypes.AXE.get(), true);
    public static final DeferredItem<GearBlueprintItem> PAXEL_TEMPLATE = registerGearBlueprint(GearTypes.PAXEL.get(), true);
    public static final DeferredItem<GearBlueprintItem> HAMMER_TEMPLATE = registerGearBlueprint(GearTypes.HAMMER.get(), true);
    public static final DeferredItem<GearBlueprintItem> EXCAVATOR_TEMPLATE = registerGearBlueprint(GearTypes.EXCAVATOR.get(), true);
    public static final DeferredItem<GearBlueprintItem> SAW_TEMPLATE = registerGearBlueprint(GearTypes.SAW.get(), true);
    public static final DeferredItem<GearBlueprintItem> HOE_TEMPLATE = registerGearBlueprint(GearTypes.HOE.get(), true);
    public static final DeferredItem<GearBlueprintItem> MATTOCK_TEMPLATE = registerGearBlueprint(GearTypes.MATTOCK.get(), true);
    public static final DeferredItem<GearBlueprintItem> PROSPECTOR_HAMMER_TEMPLATE = registerGearBlueprint(GearTypes.PROSPECTOR_HAMMER.get(), true);
    public static final DeferredItem<GearBlueprintItem> SICKLE_TEMPLATE = registerGearBlueprint(GearTypes.SICKLE.get(), true);
    public static final DeferredItem<GearBlueprintItem> SHEARS_TEMPLATE = registerGearBlueprint(GearTypes.SHEARS.get(), true);
    public static final DeferredItem<GearBlueprintItem> FISHING_ROD_TEMPLATE = registerGearBlueprint(GearTypes.FISHING_ROD.get(), true);
    public static final DeferredItem<GearBlueprintItem> BOW_TEMPLATE = registerGearBlueprint(GearTypes.BOW.get(), true);
    public static final DeferredItem<GearBlueprintItem> CROSSBOW_TEMPLATE = registerGearBlueprint(GearTypes.CROSSBOW.get(), true);
    public static final DeferredItem<GearBlueprintItem> SLINGSHOT_TEMPLATE = registerGearBlueprint(GearTypes.SLINGSHOT.get(), true);
    public static final DeferredItem<GearBlueprintItem> SHIELD_TEMPLATE = registerGearBlueprint(GearTypes.SHIELD.get(), true);
    public static final DeferredItem<GearBlueprintItem> HELMET_TEMPLATE = registerGearBlueprint(GearTypes.HELMET.get(), true);
    public static final DeferredItem<GearBlueprintItem> CHESTPLATE_TEMPLATE = registerGearBlueprint(GearTypes.CHESTPLATE.get(), true);
    public static final DeferredItem<GearBlueprintItem> LEGGINGS_TEMPLATE = registerGearBlueprint(GearTypes.LEGGINGS.get(), true);
    public static final DeferredItem<GearBlueprintItem> BOOTS_TEMPLATE = registerGearBlueprint(GearTypes.BOOTS.get(), true);
    public static final DeferredItem<GearBlueprintItem> ELYTRA_TEMPLATE = registerGearBlueprint(GearTypes.ELYTRA.get(), true);
    public static final DeferredItem<GearBlueprintItem> ARROW_TEMPLATE = registerGearBlueprint(GearTypes.ARROW.get(), true);
    public static final DeferredItem<GearBlueprintItem> RING_TEMPLATE = registerGearBlueprint(GearTypes.RING.get(), true);
    public static final DeferredItem<GearBlueprintItem> BRACELET_TEMPLATE = registerGearBlueprint(GearTypes.BRACELET.get(), true);
    //endregion

    //region Compound Parts and Tool Heads
    // Tool Heads
    public static final DeferredItem<MainPartItem> SWORD_BLADE = registerCompoundPart("sword_blade", () ->
            new MainPartItem(GearTypes.SWORD.get(), unstackableProps()));
    public static final DeferredItem<MainPartItem> KATANA_BLADE = registerCompoundPart("katana_blade", () ->
            new MainPartItem(GearTypes.KATANA.get(), unstackableProps()));
    public static final DeferredItem<MainPartItem> MACHETE_BLADE = registerCompoundPart("machete_blade", () ->
            new MainPartItem(GearTypes.MACHETE.get(), unstackableProps()));
    public static final DeferredItem<MainPartItem> SPEAR_TIP = registerCompoundPart("spear_tip", () ->
            new MainPartItem(GearTypes.SPEAR.get(), unstackableProps()));
    public static final DeferredItem<MainPartItem> TRIDENT_PRONGS = registerCompoundPart("trident_prongs", () ->
            new MainPartItem(GearTypes.TRIDENT.get(), unstackableProps()));
    public static final DeferredItem<MainPartItem> KNIFE_BLADE = registerCompoundPart("knife_blade", () ->
            new MainPartItem(GearTypes.KNIFE.get(), unstackableProps()));
    public static final DeferredItem<MainPartItem> DAGGER_BLADE = registerCompoundPart("dagger_blade", () ->
            new MainPartItem(GearTypes.DAGGER.get(), unstackableProps()));
    public static final DeferredItem<MainPartItem> PICKAXE_HEAD = registerCompoundPart("pickaxe_head", () ->
            new MainPartItem(GearTypes.PICKAXE.get(), unstackableProps()));
    public static final DeferredItem<MainPartItem> SHOVEL_HEAD = registerCompoundPart("shovel_head", () ->
            new MainPartItem(GearTypes.SHOVEL.get(), unstackableProps()));
    public static final DeferredItem<MainPartItem> AXE_HEAD = registerCompoundPart("axe_head", () ->
            new MainPartItem(GearTypes.AXE.get(), unstackableProps()));
    public static final DeferredItem<MainPartItem> PAXEL_HEAD = registerCompoundPart("paxel_head", () ->
            new MainPartItem(GearTypes.PAXEL.get(), unstackableProps()));
    public static final DeferredItem<MainPartItem> HAMMER_HEAD = registerCompoundPart("hammer_head", () ->
            new MainPartItem(GearTypes.HAMMER.get(), unstackableProps()));
    public static final DeferredItem<MainPartItem> EXCAVATOR_HEAD = registerCompoundPart("excavator_head", () ->
            new MainPartItem(GearTypes.EXCAVATOR.get(), unstackableProps()));
    public static final DeferredItem<MainPartItem> SAW_BLADE = registerCompoundPart("saw_blade", () ->
            new MainPartItem(GearTypes.SAW.get(), unstackableProps()));
    public static final DeferredItem<MainPartItem> HOE_HEAD = registerCompoundPart("hoe_head", () ->
            new MainPartItem(GearTypes.HOE.get(), unstackableProps()));
    public static final DeferredItem<MainPartItem> MATTOCK_HEAD = registerCompoundPart("mattock_head", () ->
            new MainPartItem(GearTypes.MATTOCK.get(), unstackableProps()));
    public static final DeferredItem<MainPartItem> PROSPECTOR_HAMMER_HEAD = registerCompoundPart("prospector_hammer_head", () ->
            new MainPartItem(GearTypes.PROSPECTOR_HAMMER.get(), unstackableProps()));
    public static final DeferredItem<MainPartItem> SICKLE_BLADE = registerCompoundPart("sickle_blade", () ->
            new MainPartItem(GearTypes.SICKLE.get(), unstackableProps()));
    public static final DeferredItem<MainPartItem> SHEARS_BLADES = registerCompoundPart("shears_blades", () ->
            new MainPartItem(GearTypes.SHEARS.get(), unstackableProps()));
    public static final DeferredItem<MainPartItem> FISHING_REEL_AND_HOOK = registerCompoundPart("fishing_reel_and_hook", () ->
            new MainPartItem(GearTypes.FISHING_ROD.get(), unstackableProps()));
    public static final DeferredItem<MainPartItem> BOW_LIMBS = registerCompoundPart("bow_limbs", () ->
            new MainPartItem(GearTypes.BOW.get(), unstackableProps()));
    public static final DeferredItem<MainPartItem> CROSSBOW_LIMBS = registerCompoundPart("crossbow_limbs", () ->
            new MainPartItem(GearTypes.CROSSBOW.get(), unstackableProps()));
    public static final DeferredItem<MainPartItem> SLINGSHOT_LIMBS = registerCompoundPart("slingshot_limbs", () ->
            new MainPartItem(GearTypes.SLINGSHOT.get(), unstackableProps()));
    public static final DeferredItem<MainPartItem> SHIELD_PLATE = registerCompoundPart("shield_plate", () ->
            new MainPartItem(GearTypes.SHIELD.get(), new Item.Properties().stacksTo(1)));
    public static final DeferredItem<MainPartItem> HELMET_PLATES = registerCompoundPart("helmet_plates", () ->
            new MainPartItem(GearTypes.HELMET.get(), unstackableProps()));
    public static final DeferredItem<MainPartItem> CHESTPLATE_PLATES = registerCompoundPart("chestplate_plates", () ->
            new MainPartItem(GearTypes.CHESTPLATE.get(), unstackableProps()));
    public static final DeferredItem<MainPartItem> LEGGING_PLATES = registerCompoundPart("legging_plates", () ->
            new MainPartItem(GearTypes.LEGGINGS.get(), unstackableProps()));
    public static final DeferredItem<MainPartItem> BOOT_PLATES = registerCompoundPart("boot_plates", () ->
            new MainPartItem(GearTypes.BOOTS.get(), unstackableProps()));
    public static final DeferredItem<MainPartItem> ELYTRA_WINGS = registerCompoundPart("elytra_wings", () ->
            new MainPartItem(GearTypes.ELYTRA.get(), unstackableProps()));
    public static final DeferredItem<MainPartItem> ARROW_HEADS = registerCompoundPart("arrow_heads", () ->
            new MainPartItem(GearTypes.ARROW.get(), unstackableProps()));
    public static final DeferredItem<MainPartItem> RING_SHANK = registerCompoundPart("ring_shank", () ->
            new MainPartItem(GearTypes.RING.get(), unstackableProps()));
    public static final DeferredItem<MainPartItem> BRACELET_BAND = registerCompoundPart("bracelet_band", () ->
            new MainPartItem(GearTypes.BRACELET.get(), unstackableProps()));
    // Compound Parts
    public static final DeferredItem<CompoundPartItem> ROD = registerCompoundPart("rod", () ->
            new CompoundPartItem(PartTypes.ROD.get(), baseProps()));
    public static final DeferredItem<CompoundPartItem> TIP = registerCompoundPart("tip", () ->
            new CompoundPartItem(PartTypes.TIP.get(), baseProps()));
    public static final DeferredItem<CompoundPartItem> COATING = registerCompoundPart("coating", () ->
            new CompoundPartItem(PartTypes.COATING.get(), baseProps()));
    public static final DeferredItem<CompoundPartItem> GRIP = registerCompoundPart("grip", () ->
            new CompoundPartItem(PartTypes.GRIP.get(), baseProps()));
    public static final DeferredItem<CompoundPartItem> BINDING = registerCompoundPart("binding", () ->
            new CompoundPartItem(PartTypes.BINDING.get(), baseProps()));
    public static final DeferredItem<CompoundPartItem> LINING = registerCompoundPart("lining", () ->
            new CompoundPartItem(PartTypes.LINING.get(), baseProps()));
    public static final DeferredItem<CompoundPartItem> CORD = registerCompoundPart("cord", () ->
            new CompoundPartItem(PartTypes.CORD.get(), baseProps()));
    public static final DeferredItem<CompoundPartItem> FLETCHING = registerCompoundPart("fletching", () ->
            new CompoundPartItem(PartTypes.FLETCHING.get(), baseProps()));
    public static final DeferredItem<CompoundPartItem> SETTING = registerCompoundPart("setting", () ->
            new CompoundPartItem(PartTypes.SETTING.get(), baseProps()));
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
                    .food(new FoodProperties.Builder().nutrition(5).saturationModifier(0.4f).build())));
    public static final DeferredItem<Item> GOLDEN_NETHER_BANANA = register("golden_nether_banana", () ->
            new Item(baseProps()
                    .food(new FoodProperties.Builder().nutrition(10).saturationModifier(1.0f)
                            .alwaysEdible()
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

    public static final DeferredItem<GearSwordItem> SWORD = register("sword", () -> new GearSwordItem(GearTypes.SWORD.get()));
    public static final DeferredItem<GearSwordItem> KATANA = register("katana", () -> new GearSwordItem(GearTypes.KATANA.get()));
    public static final DeferredItem<GearMacheteItem> MACHETE = register("machete", () -> new GearMacheteItem(GearTypes.MACHETE.get()));
    public static final DeferredItem<GearSwordItem> SPEAR = register("spear", () -> new GearSwordItem(GearTypes.SPEAR.get()));
    public static final DeferredItem<GearTridentItem> TRIDENT = register("trident", () -> new GearTridentItem());
    public static final DeferredItem<GearDaggerItem> KNIFE = register("knife", () -> new GearDaggerItem(GearTypes.KNIFE.get()));
    public static final DeferredItem<GearDaggerItem> DAGGER = register("dagger", () -> new GearDaggerItem(GearTypes.DAGGER.get()));
    public static final DeferredItem<GearPickaxeItem> PICKAXE = register("pickaxe", () -> new GearPickaxeItem(GearTypes.PICKAXE.get()));
    public static final DeferredItem<GearShovelItem> SHOVEL = register("shovel", () -> new GearShovelItem(GearTypes.SHOVEL.get()));
    public static final DeferredItem<GearAxeItem> AXE = register("axe", () -> new GearAxeItem(GearTypes.AXE.get()));
    public static final DeferredItem<GearPaxelItem> PAXEL = register("paxel", () -> new GearPaxelItem(GearTypes.PAXEL.get()));
    public static final DeferredItem<GearHammerItem> HAMMER = register("hammer", () -> new GearHammerItem(GearTypes.HAMMER.get()));
    public static final DeferredItem<GearExcavatorItem> EXCAVATOR = register("excavator", () -> new GearExcavatorItem(GearTypes.EXCAVATOR.get()));
    public static final DeferredItem<GearSawItem> SAW = register("saw", () -> new GearSawItem(GearTypes.SAW.get()));
    public static final DeferredItem<GearProspectorHammerItem> PROSPECTOR_HAMMER = register("prospector_hammer", () -> new GearProspectorHammerItem(GearTypes.PROSPECTOR_HAMMER.get()));
    public static final DeferredItem<GearHoeItem> HOE = register("hoe", () -> new GearHoeItem(GearTypes.HOE.get()));
    public static final DeferredItem<GearMattockItem> MATTOCK = register("mattock", () -> new GearMattockItem());
    public static final DeferredItem<GearSickleItem> SICKLE = register("sickle", () -> new GearSickleItem(GearTypes.SICKLE.get()));
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
            new GearCurioItem(GearTypes.RING.get(), "ring", unstackableProps()));
    public static final DeferredItem<GearCurioItem> BRACELET = register("bracelet", () ->
            new GearCurioItem(GearTypes.BRACELET.get(), "bracelet", unstackableProps()));

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
        String name = SgRegistries.GEAR_TYPES.getKey(gearType).getPath() + "_" + (singleUse ? "template" : "blueprint");
        return register(name, () -> new GearBlueprintItem(gearType, singleUse, baseProps()));
    }

    private static DeferredItem<PartBlueprintItem> registerPartBlueprint(PartType partType, boolean singleUse) {
        String name = SgRegistries.PART_TYPES.getKey(partType).getPath() + "_" + (singleUse ? "template" : "blueprint");
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
