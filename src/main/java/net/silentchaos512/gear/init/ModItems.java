package net.silentchaos512.gear.init;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockNamedItem;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.ICoreArmor;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.item.ICoreTool;
import net.silentchaos512.gear.api.parts.PartType;
import net.silentchaos512.gear.item.*;
import net.silentchaos512.gear.item.blueprint.GearBlueprintItem;
import net.silentchaos512.gear.item.blueprint.PartBlueprintItem;
import net.silentchaos512.gear.item.gear.*;
import net.silentchaos512.lib.util.TimeUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ModItems {
    public static final Map<ResourceLocation, ICoreTool> toolClasses = new LinkedHashMap<>();
    public static final Map<ResourceLocation, ICoreArmor> armorClasses = new LinkedHashMap<>();
    public static final Map<ResourceLocation, ICoreItem> gearClasses = new LinkedHashMap<>();
    public static final List<GearBlueprintItem> blueprints = new ArrayList<>();
    static final Map<String, BlockItem> blocksToRegister = new LinkedHashMap<>();

    public static BlueprintPackageItem blueprintPackage;
    public static CustomTippedUpgrade customTippedUpgrade = new CustomTippedUpgrade();

    public static BlockNamedItem flaxseeds;
    public static Item netherBanana;
    public static Item goldenNetherBanana;
    public static Item pebble;

    public static CoreSword sword = new CoreSword();
    public static CoreDagger dagger = new CoreDagger();
    public static CoreKatana katana = new CoreKatana();
    public static CoreMachete machete = new CoreMachete();
    public static CoreSpear spear = new CoreSpear();
    public static CorePickaxe pickaxe = new CorePickaxe();
    public static CoreShovel shovel = new CoreShovel();
    public static CoreAxe axe = new CoreAxe();
    public static CorePaxel paxel = new CorePaxel();
    public static CoreHammer hammer = new CoreHammer();
    public static CoreExcavator excavator = new CoreExcavator();
    public static CoreLumberAxe lumberAxe = new CoreLumberAxe();
    public static CoreMattock mattock = new CoreMattock();
    public static CoreSickle sickle = new CoreSickle();
    public static CoreShears shears = new CoreShears();
    public static CoreBow bow = new CoreBow();
    public static CoreCrossbow crossbow = new CoreCrossbow();
    public static CoreSlingshot slingshot = new CoreSlingshot();
    public static CoreShield shield = new CoreShield();

    public static CoreArmor helmet = new CoreArmor(EquipmentSlotType.HEAD);
    public static CoreArmor chestplate = new CoreArmor(EquipmentSlotType.CHEST);
    public static CoreArmor leggings = new CoreArmor(EquipmentSlotType.LEGS);
    public static CoreArmor boots = new CoreArmor(EquipmentSlotType.FEET);

    static {
        // Seems colors events can fire before items are initialized in some case?
        // So we need to construct the items right now...
        initializeGear();
    }

    private ModItems() {}

    public static void registerAll(RegistryEvent.Register<Item> event) {
        blocksToRegister.forEach(ModItems::register);

        // Initializes, but does not register gear classes, fills maps
        initializeGear();

        blueprintPackage = register("blueprint_package", new BlueprintPackageItem(SilentGear.getId("starter_blueprints")));

        // Blueprints/templates
        registerBlueprints("blueprint", false);
        registerBlueprints("template", true);

        for (CraftingItems item : CraftingItems.values()) {
            register(item.getName(), item.asItem());
        }

        register("custom_tipped_upgrade", customTippedUpgrade);
        register("rod", new PartItem(SilentGear.getId("rod"), PartType.ROD, new Item.Properties().group(SilentGear.ITEM_GROUP)));
        register("long_rod", new PartItem(SilentGear.getId("long_rod"), PartType.ROD, new Item.Properties().group(SilentGear.ITEM_GROUP)));

        flaxseeds = register("flaxseeds", new BlockNamedItem(ModBlocks.FLAX_PLANT.asBlock(), getBaseProperties()));
        netherBanana = register("nether_banana", new Item(getBaseProperties()
                .food(new Food.Builder().hunger(5).saturation(0.4f).build())));
        goldenNetherBanana = register("golden_nether_banana", new Item(getBaseProperties()
                .food(new Food.Builder()
                        .hunger(10)
                        .saturation(1.0f)
                        .setAlwaysEdible()
                        .effect(() -> new EffectInstance(Effects.FIRE_RESISTANCE, TimeUtils.ticksFromMinutes(10)), 1f)
                        .effect(() -> new EffectInstance(Effects.RESISTANCE, TimeUtils.ticksFromMinutes(5)), 1f)
                        .effect(() -> new EffectInstance(Effects.REGENERATION, TimeUtils.ticksFromSeconds(10)), 1f)
                        .build())));

        pebble = register("pebble", new SlingshotAmmoItem());

        // Register gear classes
        gearClasses.forEach((key, item) -> register(key, item.asItem()));

//        for (PartIcons icon : PartIcons.values()) {
//            register("dummy_icon_" + icon.name().toLowerCase(Locale.ROOT), icon.asItem());
//        }

//        if (SilentGear.isDevBuild()) {
//            register("test_item", new TestItem());
//        }
    }

    private static Item.Properties getBaseProperties() {
        return new Item.Properties().group(SilentGear.ITEM_GROUP);
    }

    private static void initializeGear() {
        // Build gear maps now because blueprints need them
        toolClasses.put(SilentGear.getId("sword"), sword);
        toolClasses.put(SilentGear.getId("dagger"), dagger);
        toolClasses.put(SilentGear.getId("katana"), katana);
        toolClasses.put(SilentGear.getId("machete"), machete);
        toolClasses.put(SilentGear.getId("spear"), spear);
        toolClasses.put(SilentGear.getId("pickaxe"), pickaxe);
        toolClasses.put(SilentGear.getId("shovel"), shovel);
        toolClasses.put(SilentGear.getId("axe"), axe);
        toolClasses.put(SilentGear.getId("paxel"), paxel);
        toolClasses.put(SilentGear.getId("hammer"), hammer);
        toolClasses.put(SilentGear.getId("excavator"), excavator);
        toolClasses.put(SilentGear.getId("lumber_axe"), lumberAxe);
        toolClasses.put(SilentGear.getId("mattock"), mattock);
        toolClasses.put(SilentGear.getId("sickle"), sickle);
        toolClasses.put(SilentGear.getId("shears"), shears);
        toolClasses.put(SilentGear.getId("bow"), bow);
        toolClasses.put(SilentGear.getId("crossbow"), crossbow);
//        toolClasses.put(SilentGear.getId("slingshot"), slingshot);

        armorClasses.put(SilentGear.getId("helmet"), helmet);
        armorClasses.put(SilentGear.getId("chestplate"), chestplate);
        armorClasses.put(SilentGear.getId("leggings"), leggings);
        armorClasses.put(SilentGear.getId("boots"), boots);

        gearClasses.put(SilentGear.getId("shield"), shield);
        gearClasses.putAll(toolClasses);
        gearClasses.putAll(armorClasses);
    }

    private static <T extends Item> T register(String name, T item) {
        return register(SilentGear.getId(name), item);
    }

    private static <T extends Item> T register(ResourceLocation id, T item) {
        item.setRegistryName(id);
        ForgeRegistries.ITEMS.register(item);
        return item;
    }

    private static void registerBlueprints(String name, boolean singleUse) {
        // TODO: Change item ID's from "blueprint_item" to "item_blueprint"
        gearClasses.forEach((key, item) -> {
            GearBlueprintItem blueprint = new GearBlueprintItem(singleUse, () -> item);
            blueprints.add(blueprint);
            register(new ResourceLocation(key.getNamespace(), name + "_" + key.getPath()), blueprint);
        });

        // Part blueprints
        register(name + "_rod", new PartBlueprintItem(singleUse, PartType.ROD));
        register(name + "_bowstring", new PartBlueprintItem(singleUse, PartType.BOWSTRING));
        register(name + "_grip", new PartBlueprintItem(singleUse, PartType.GRIP));
    }
}
