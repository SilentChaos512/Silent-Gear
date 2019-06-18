package net.silentchaos512.gear.init;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockNamedItem;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.ICoreArmor;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.item.ICoreTool;
import net.silentchaos512.gear.api.parts.PartType;
import net.silentchaos512.gear.item.BlueprintPackageItem;
import net.silentchaos512.gear.item.CraftingItems;
import net.silentchaos512.gear.item.blueprint.GearBlueprintItem;
import net.silentchaos512.gear.item.blueprint.PartBlueprintItem;
import net.silentchaos512.gear.item.gear.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ModItems {
    public static final Map<String, ICoreTool> toolClasses = new LinkedHashMap<>();
    public static final Map<String, ICoreArmor> armorClasses = new LinkedHashMap<>();
    public static final Map<String, ICoreItem> gearClasses = new LinkedHashMap<>();
    public static final List<GearBlueprintItem> blueprints = new ArrayList<>();
    static final Map<String, BlockItem> blocksToRegister = new LinkedHashMap<>();

    public static BlueprintPackageItem blueprintPackage;

    public static BlockNamedItem flaxseeds;
    public static Item netherBanana;
    public static Item pebble;

    public static CoreSword sword;
    public static CoreDagger dagger;
    public static CoreKatana katana;
    public static CoreMachete machete;
    public static CoreSpear spear;
    public static CorePickaxe pickaxe;
    public static CoreShovel shovel;
    public static CoreAxe axe;
    public static CoreHammer hammer;
    public static CoreExcavator excavator;
    public static CoreMattock mattock;
    public static CoreSickle sickle;
    public static CoreBow bow;
    public static CoreSlingshot slingshot;

    public static CoreArmor helmet;
    public static CoreArmor chestplate;
    public static CoreArmor leggings;
    public static CoreArmor boots;

    private ModItems() {}

    public static void registerAll(RegistryEvent.Register<Item> event) {
        if (!event.getName().equals(ForgeRegistries.ITEMS.getRegistryName())) return;

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

        flaxseeds = register("flaxseeds", new BlockNamedItem(ModBlocks.FLAX_PLANT.asBlock(), getBaseProperties()));
        netherBanana = register("nether_banana", new Item(getBaseProperties()
                .food(new Food.Builder().hunger(5).saturation(0.4f).build())));

//        pebble = register("pebble", new SlingshotAmmoItem());

        // Register gear classes
        toolClasses.forEach((key, item) -> register(key, item.asItem()));
        armorClasses.forEach((key, item) -> register(key, item.asItem()));

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
        sword = new CoreSword();
        dagger = new CoreDagger();
        katana = new CoreKatana();
        machete = new CoreMachete();
        spear = new CoreSpear();
        pickaxe = new CorePickaxe();
        shovel = new CoreShovel();
        axe = new CoreAxe();
        hammer = new CoreHammer();
        excavator = new CoreExcavator();
        mattock = new CoreMattock();
        sickle = new CoreSickle();
        bow = new CoreBow();
        slingshot = new CoreSlingshot();

        helmet = new CoreArmor(EquipmentSlotType.HEAD, "helmet");
        chestplate = new CoreArmor(EquipmentSlotType.CHEST, "chestplate");
        leggings = new CoreArmor(EquipmentSlotType.LEGS, "leggings");
        boots = new CoreArmor(EquipmentSlotType.FEET, "boots");

        toolClasses.put("sword", sword);
        toolClasses.put("dagger", dagger);
        toolClasses.put("katana", katana);
        toolClasses.put("machete", machete);
        toolClasses.put("spear", spear);
        toolClasses.put("pickaxe", pickaxe);
        toolClasses.put("shovel", shovel);
        toolClasses.put("axe", axe);
        toolClasses.put("hammer", hammer);
        toolClasses.put("excavator", excavator);
        toolClasses.put("mattock", mattock);
        toolClasses.put("sickle", sickle);
        toolClasses.put("bow", bow);
//        toolClasses.put("slingshot", slingshot);

        armorClasses.put("helmet", helmet);
        armorClasses.put("chestplate", chestplate);
        armorClasses.put("leggings", leggings);
        armorClasses.put("boots", boots);

        gearClasses.putAll(toolClasses);
        gearClasses.putAll(armorClasses);
    }

    private static <T extends Item> T register(String name, T item) {
        ResourceLocation id = new ResourceLocation(SilentGear.MOD_ID, name);
        item.setRegistryName(id);
        ForgeRegistries.ITEMS.register(item);
        return item;
    }

    private static void registerBlueprints(String name, boolean singleUse) {
        gearClasses.forEach((key, item) -> {
            GearBlueprintItem blueprint = new GearBlueprintItem(singleUse, item);
            blueprints.add(blueprint);
            register(name + "_" + key, blueprint);
        });

        // Part blueprints
        register(name + "_rod", new PartBlueprintItem(singleUse, PartType.ROD));
        register(name + "_bowstring", new PartBlueprintItem(singleUse, PartType.BOWSTRING));
    }
}
