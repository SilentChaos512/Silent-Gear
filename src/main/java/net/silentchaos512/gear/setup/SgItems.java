package net.silentchaos512.gear.setup;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.silentchaos512.gear.Config;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.item.*;
import net.silentchaos512.gear.item.blueprint.BlueprintType;
import net.silentchaos512.gear.item.blueprint.PartBlueprintItem;
import net.silentchaos512.gear.item.blueprint.book.BlueprintBookItem;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.lib.util.TimeUtils;

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

    public static final DeferredItem<Item> CRUDE_KNIFE = register("crude_knife", () ->
            new Item(baseProps().durability(32)));
    public static final DeferredItem<Item> CRUDE_HAMMER = register("crude_hammer", () ->
            new Item(baseProps().durability(32)));

    //region Blueprints and templates
    public static final DeferredItem<BlueprintBookItem> BLUEPRINT_BOOK = register("blueprint_book", () ->
            new BlueprintBookItem(unstackableProps().rarity(Rarity.UNCOMMON)));
    // Blueprints
    public static final DeferredItem<PartBlueprintItem> JEWELER_TOOLS = register("jeweler_tools", () ->
            new JewelerKitItem(PartTypes.SETTING, BlueprintType.BLUEPRINT, unstackableProps()));
    public static final DeferredItem<PartBlueprintItem> ROD_BLUEPRINT = registerPartBlueprint(PartTypes.ROD, false);
    public static final DeferredItem<PartBlueprintItem> TIP_BLUEPRINT = registerPartBlueprint(PartTypes.TIP, false);
    public static final DeferredItem<PartBlueprintItem> COATING_BLUEPRINT = registerPartBlueprint(PartTypes.COATING, false);
    public static final DeferredItem<PartBlueprintItem> GRIP_BLUEPRINT = registerPartBlueprint(PartTypes.GRIP, false);
    public static final DeferredItem<PartBlueprintItem> BINDING_BLUEPRINT = registerPartBlueprint(PartTypes.BINDING, false);
    public static final DeferredItem<PartBlueprintItem> LINING_BLUEPRINT = registerPartBlueprint(PartTypes.LINING, false);
    public static final DeferredItem<PartBlueprintItem> CORD_BLUEPRINT = registerPartBlueprint(PartTypes.CORD, false);
    public static final DeferredItem<PartBlueprintItem> FLETCHING_BLUEPRINT = registerPartBlueprint(PartTypes.FLETCHING, false);

    static {
        GearItemSets.registerBlueprintItems();
    }

    // Templates
    public static final DeferredItem<PartBlueprintItem> ROD_TEMPLATE = registerPartBlueprint(PartTypes.ROD, true);
    public static final DeferredItem<PartBlueprintItem> TIP_TEMPLATE = registerPartBlueprint(PartTypes.TIP, true);
    public static final DeferredItem<PartBlueprintItem> COATING_TEMPLATE = registerPartBlueprint(PartTypes.COATING, true);
    public static final DeferredItem<PartBlueprintItem> GRIP_TEMPLATE = registerPartBlueprint(PartTypes.GRIP, true);
    public static final DeferredItem<PartBlueprintItem> BINDING_TEMPLATE = registerPartBlueprint(PartTypes.BINDING, true);
    public static final DeferredItem<PartBlueprintItem> LINING_TEMPLATE = registerPartBlueprint(PartTypes.LINING, true);
    public static final DeferredItem<PartBlueprintItem> CORD_TEMPLATE = registerPartBlueprint(PartTypes.CORD, true);
    public static final DeferredItem<PartBlueprintItem> FLETCHING_TEMPLATE = registerPartBlueprint(PartTypes.FLETCHING, true);

    static {
        GearItemSets.registerTemplateItems();
    }
    //endregion

    //region Parts
    // Mains (tool heads, etc.)
    static {
        GearItemSets.registerMainPartItems();
    }

    // Compound Parts
    public static final DeferredItem<CompoundPartItem> ROD = registerCompoundPart("rod", () ->
            new CompoundPartItem(PartTypes.ROD, baseProps()));
    public static final DeferredItem<CompoundPartItem> TIP = registerCompoundPart("tip", () ->
            new CompoundPartItem(PartTypes.TIP, baseProps()));
    public static final DeferredItem<CompoundPartItem> COATING = registerCompoundPart("coating", () ->
            new CompoundPartItem(PartTypes.COATING, baseProps()));
    public static final DeferredItem<CompoundPartItem> GRIP = registerCompoundPart("grip", () ->
            new CompoundPartItem(PartTypes.GRIP, baseProps()));
    public static final DeferredItem<CompoundPartItem> BINDING = registerCompoundPart("binding", () ->
            new CompoundPartItem(PartTypes.BINDING, baseProps()));
    public static final DeferredItem<CompoundPartItem> LINING = registerCompoundPart("lining", () ->
            new CompoundPartItem(PartTypes.LINING, baseProps()));
    public static final DeferredItem<CompoundPartItem> CORD = registerCompoundPart("cord", () ->
            new CompoundPartItem(PartTypes.CORD, baseProps()));
    public static final DeferredItem<CompoundPartItem> FLETCHING = registerCompoundPart("fletching", () ->
            new CompoundPartItem(PartTypes.FLETCHING, baseProps()));
    public static final DeferredItem<CompoundPartItem> SETTING = registerCompoundPart("setting", () ->
            new CompoundPartItem(PartTypes.SETTING, baseProps()));
    //endregion

    // Compound materials
    public static final DeferredItem<CompoundMaterialItem> ALLOY_INGOT = register("alloy_ingot", () ->
            new CompoundMaterialItem(baseProps()));
    public static final DeferredItem<CompoundMaterialItem> HYBRID_GEM = register("hybrid_gem", () ->
            new CompoundMaterialItem(baseProps()));
    public static final DeferredItem<CompoundMaterialItem> MIXED_FABRIC = register("mixed_fabric", () ->
            new CompoundMaterialItem(baseProps()));
    public static final DeferredItem<CompoundMaterialItem> SUPER_ALLOY = register("super_alloy", () ->
            new CompoundMaterialItem(baseProps()));

    // Custom materials
    public static final DeferredItem<CustomMaterialItem> CUSTOM_INGOT = register("custom_ingot", () ->
            new CustomMaterialItem(baseProps()));
    public static final DeferredItem<CustomMaterialItem> CUSTOM_GEM = register("custom_gem", () ->
            new CustomMaterialItem(baseProps()));

    public static final DeferredItem<ProcessedMaterialItem> SHEET_METAL = register("sheet_metal", () ->
            new ProcessedMaterialItem(baseProps()));

    static {
        CraftingItems.register(ITEMS);
    }

    public static final DeferredItem<SlingshotAmmoItem> PEBBLE = register("pebble", () -> new SlingshotAmmoItem(baseProps()));

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
            new Item(baseProps()));

    static {
        GearItemSets.registerGearItems();
    }

    private SgItems() {
    }

    private static Item.Properties baseProps() {
        return new Item.Properties();
    }

    public static Item.Properties unstackableProps() {
        return baseProps().stacksTo(1);
    }

    private static <T extends Item> DeferredItem<T> register(String name, Supplier<T> item) {
        return ITEMS.register(name, item);
    }

    private static <T extends CompoundPartItem> DeferredItem<T> registerCompoundPart(String name, Supplier<T> item) {
        return register(name, item);
    }

    @Deprecated // Make a part item set like gear items have?
    private static DeferredItem<PartBlueprintItem> registerPartBlueprint(DeferredHolder<PartType, PartType> partType, boolean singleUse) {
        String name = partType.getId().getPath() + "_" + (singleUse ? "template" : "blueprint");
        return register(name, () -> new PartBlueprintItem(partType, singleUse ? BlueprintType.TEMPLATE : BlueprintType.BLUEPRINT, baseProps()));
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
