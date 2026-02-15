package net.silentchaos512.gear.setup;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.level.block.Block;
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
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

@SuppressWarnings({"unused", "OverlyCoupledClass"})
public final class SgItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SilentGear.MOD_ID);

    public static final DeferredItem<MaterialBookItem> MATERIAL_BOOK = register("material_book", () ->
            new MaterialBookItem(unstackableProps()));
    public static final DeferredItem<BlueprintPackageItem> BLUEPRINT_PACKAGE = register(
            "blueprint_package",
            BlueprintPackageItem::new
    );

    public static final DeferredItem<Item> MOD_KIT = register(
            "mod_kit",
            ModKitItem::new,
            properties -> properties.stacksTo(1).rarity(Rarity.UNCOMMON)
    );

    // Repair Kits
    public static final DeferredItem<Item> VERY_CRUDE_REPAIR_KIT = register(
            "very_crude_repair_kit",
            p -> new RepairKitItem(
                    Config.Common.repairKitVeryCrudeCapacity::get,
                    Config.Common.repairKitVeryCrudeEfficiency::get,
                    p
            ),
            properties -> properties.stacksTo(1).rarity(Rarity.COMMON)
    );
    public static final DeferredItem<Item> CRUDE_REPAIR_KIT = register(
            "crude_repair_kit",
            p -> new RepairKitItem(
                    Config.Common.repairKitCrudeCapacity::get,
                    Config.Common.repairKitCrudeEfficiency::get,
                    p
            ),
            properties -> properties.stacksTo(1).rarity(Rarity.COMMON)
    );
    public static final DeferredItem<Item> STURDY_REPAIR_KIT = register(
            "sturdy_repair_kit",
            p -> new RepairKitItem(
                    Config.Common.repairKitSturdyCapacity::get,
                    Config.Common.repairKitSturdyEfficiency::get,
                    p
            ),
            properties -> properties.stacksTo(1).rarity(Rarity.UNCOMMON)
    );
    public static final DeferredItem<Item> CRIMSON_REPAIR_KIT = register(
            "crimson_repair_kit",
            p -> new RepairKitItem(
                    Config.Common.repairKitCrimsonCapacity::get,
                    Config.Common.repairKitCrimsonEfficiency::get,
                    p
            ),
            properties -> properties.stacksTo(1).rarity(Rarity.RARE)
    );
    public static final DeferredItem<Item> AZURE_REPAIR_KIT = register(
            "azure_repair_kit",
            p -> new RepairKitItem(
                    Config.Common.repairKitAzureCapacity::get,
                    Config.Common.repairKitAzureEfficiency::get,
                    p
            ),
            properties -> properties.stacksTo(1).rarity(Rarity.EPIC)
    );

    public static final DeferredItem<Item> COATING_SMITHING_TEMPLATE = register(
            "coating_smithing_template",
            Item::new
    );

    public static final DeferredItem<Item> CRUDE_KNIFE = register(
            "crude_knife",
            Item::new,
            properties -> properties.durability(32)
    );
    public static final DeferredItem<Item> CRUDE_HAMMER = register(
            "crude_hammer",
            Item::new,
            properties -> properties.durability(32)
    );

    //region Blueprints and templates
    public static final DeferredItem<BlueprintBookItem> BLUEPRINT_BOOK = register(
            "blueprint_book",
            BlueprintBookItem::new,
            properties -> properties.stacksTo(1).rarity(Rarity.UNCOMMON)
    );
    // Blueprints
    public static final DeferredItem<PartBlueprintItem> JEWELER_TOOLS = register(
            "jeweler_tools",
            p -> new JewelerKitItem(PartTypes.SETTING, BlueprintType.BLUEPRINT, p),
            unstackableProps()
    );
    public static final DeferredItem<PartBlueprintItem> ROD_BLUEPRINT = registerPartBlueprint(PartTypes.ROD, false);
    public static final DeferredItem<PartBlueprintItem> TIP_BLUEPRINT = registerPartBlueprint(PartTypes.TIP, false);
    @Deprecated
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
    @Deprecated
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
    public static final DeferredItem<CompoundPartItem> ROD = registerCompoundPart("rod", PartTypes.ROD);
    public static final DeferredItem<CompoundPartItem> TIP = registerCompoundPart("tip", PartTypes.TIP);
    @Deprecated
    public static final DeferredItem<CompoundPartItem> COATING = registerCompoundPart("coating", PartTypes.COATING);
    public static final DeferredItem<CompoundPartItem> GRIP = registerCompoundPart("grip", PartTypes.GRIP);
    public static final DeferredItem<CompoundPartItem> BINDING = registerCompoundPart("binding", PartTypes.BINDING);
    public static final DeferredItem<CompoundPartItem> LINING = registerCompoundPart("lining", PartTypes.LINING);
    public static final DeferredItem<CompoundPartItem> CORD = registerCompoundPart("cord", PartTypes.CORD);
    public static final DeferredItem<CompoundPartItem> FLETCHING = registerCompoundPart("fletching", PartTypes.FLETCHING);
    public static final DeferredItem<CompoundPartItem> SETTING = registerCompoundPart("setting", PartTypes.SETTING);
    //endregion

    // Compound materials
    public static final DeferredItem<CompoundMaterialItem> ALLOY_INGOT = register("alloy_ingot", CompoundMaterialItem::new);
    public static final DeferredItem<CompoundMaterialItem> CRUDE_ALLOY = register("crude_alloy", CompoundMaterialItem::new);
    public static final DeferredItem<CompoundMaterialItem> HYBRID_GEM = register("hybrid_gem", CompoundMaterialItem::new);
    public static final DeferredItem<CompoundMaterialItem> MIXED_FABRIC = register("mixed_fabric", CompoundMaterialItem::new);
    public static final DeferredItem<CompoundMaterialItem> SUPER_ALLOY = register("super_alloy", CompoundMaterialItem::new);

    // Custom materials
    public static final DeferredItem<CustomMaterialItem> CUSTOM_INGOT = register("custom_ingot", CustomMaterialItem::new);
    public static final DeferredItem<CustomMaterialItem> CUSTOM_GEM = register("custom_gem", CustomMaterialItem::new);

    public static final DeferredItem<ProcessedMaterialItem> SHEET_METAL = register("sheet_metal", ProcessedMaterialItem::new);

    static {
        CraftingItems.register(ITEMS);
    }

    public static final DeferredItem<SlingshotAmmoItem> PEBBLE = register("pebble", SlingshotAmmoItem::new);

    public static final DeferredItem<BlockItem> FLAX_SEEDS = registerSeed("flax_seeds", () -> SgBlocks.FLAX_PLANT.get());
    public static final DeferredItem<BlockItem> FLUFFY_SEEDS = registerSeed("fluffy_seeds", () -> SgBlocks.FLUFFY_PLANT.get());

    public static final DeferredItem<Item> NETHER_BANANA = register(
            "nether_banana",
            props -> new Item(
                    props
                            .food(
                                    new FoodProperties.Builder().nutrition(5).saturationModifier(0.4f).build()
                            )
            )
    );
    public static final DeferredItem<Item> GOLDEN_NETHER_BANANA = register(
            "golden_nether_banana",
            props -> new Item(
                    props
                            .food(
                                    new FoodProperties.Builder().nutrition(10).saturationModifier(1.0f)
                                            .alwaysEdible()
                                            .build(),
                                    Consumables.defaultFood()
                                            .onConsume(
                                                    new ApplyStatusEffectsConsumeEffect(
                                                            List.of(
                                                                    new MobEffectInstance(MobEffects.FIRE_RESISTANCE, TimeUtils.ticksFromMinutes(10)),
                                                                    new MobEffectInstance(MobEffects.RESISTANCE, TimeUtils.ticksFromMinutes(5)),
                                                                    new MobEffectInstance(MobEffects.REGENERATION, TimeUtils.ticksFromSeconds(10))
                                                            )
                                                    )
                                            )
                                            .build()
                            )
            )
    );
    public static final DeferredItem<Item> NETHERWOOD_CHARCOAL = register("netherwood_charcoal", Item::new);

    static {
        GearItemSets.registerGearItems();
    }

    private SgItems() {
    }

    private static UnaryOperator<Item.Properties> baseProps() {
        return UnaryOperator.identity();
    }

    public static UnaryOperator<Item.Properties> unstackableProps() {
        return properties -> properties.stacksTo(1);
    }

    static <T extends Item> DeferredItem<T> register(String name, Function<Item.Properties, T> item) {
        return ITEMS.registerItem(name, item);
    }

    static <T extends Item> DeferredItem<T> register(String name, Function<Item.Properties, T> item, UnaryOperator<Item.Properties> properties) {
        return ITEMS.registerItem(name, item, properties);
    }

    private static DeferredItem<BlockItem> registerSeed(String name, Supplier<? extends Block> plantBlock) {
        return ITEMS.registerSimpleBlockItem(name, plantBlock);
    }

    private static DeferredItem<CompoundPartItem> registerCompoundPart(String name, DeferredHolder<PartType, PartType> partType) {
        return register(name, props -> new CompoundPartItem(partType, props), baseProps());
    }

    @Deprecated // Make a part item set like gear items have?
    private static DeferredItem<PartBlueprintItem> registerPartBlueprint(DeferredHolder<PartType, PartType> partType, boolean singleUse) {
        String name = partType.getId().getPath() + "_" + (singleUse ? "template" : "blueprint");
        var blueprintType = singleUse ? BlueprintType.TEMPLATE : BlueprintType.BLUEPRINT;
        return register(name, props -> new PartBlueprintItem(partType, blueprintType, props), baseProps());
    }

    private static DeferredItem<CompoundMaterialItem> registerCompoundMaterial(String name) {
        return register(name, CompoundMaterialItem::new, baseProps());
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
