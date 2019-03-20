package net.silentchaos512.gear.init;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.ICoreArmor;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.item.ICoreTool;
import net.silentchaos512.gear.item.*;
import net.silentchaos512.gear.item.blueprint.Blueprint;
import net.silentchaos512.gear.item.blueprint.book.BlueprintBook;
import net.silentchaos512.gear.item.gear.*;

import java.util.*;

public final class ModItems {
    public static final Map<String, ICoreTool> toolClasses = new LinkedHashMap<>();
    public static final Map<String, ICoreArmor> armorClasses = new LinkedHashMap<>();
    public static final Map<String, ICoreItem> gearClasses = new LinkedHashMap<>();
    public static final List<Blueprint> blueprints = new ArrayList<>();
    static final Map<String, ItemBlock> blocksToRegister = new LinkedHashMap<>();

    public static BlueprintPackage blueprintPackage;
    public static BlueprintBook blueprintBook;

    public static Flaxseeds flaxseeds;
    public static NetherBanana netherBanana;

    public static CoreSword sword;
    public static CoreDagger dagger;
    public static CoreKatana katana;
    public static CoreMachete machete;
    public static CorePickaxe pickaxe;
    public static CoreShovel shovel;
    public static CoreAxe axe;
    public static CoreHammer hammer;
    public static CoreExcavator excavator;
    public static CoreMattock mattock;
    public static CoreSickle sickle;
    public static CoreBow bow;

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

        blueprintPackage = register("blueprint_package", new BlueprintPackage(
                new ResourceLocation(SilentGear.MOD_ID, "starter_blueprints")));

        // Blueprints/templates
        registerBlueprints("blueprint", false);
        registerBlueprints("template", true);
        blueprintBook = register("blueprint_book", new BlueprintBook());

        for (CraftingItems item : CraftingItems.values()) {
            register(item.getName(), item.asItem());
        }

        flaxseeds = register("flaxseeds", new Flaxseeds());
        netherBanana = register("nether_banana", new NetherBanana());

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

    private static void initializeGear() {
        // Build gear maps now because blueprints need them
        sword = new CoreSword();
        dagger = new CoreDagger();
        katana = new CoreKatana();
        machete = new CoreMachete();
        pickaxe = new CorePickaxe();
        shovel = new CoreShovel();
        axe = new CoreAxe();
        hammer = new CoreHammer();
        excavator = new CoreExcavator();
        mattock = new CoreMattock();
        sickle = new CoreSickle();
        bow = new CoreBow();

        helmet = new CoreArmor(EntityEquipmentSlot.HEAD, "helmet");
        chestplate = new CoreArmor(EntityEquipmentSlot.CHEST, "chestplate");
        leggings = new CoreArmor(EntityEquipmentSlot.LEGS, "leggings");
        boots = new CoreArmor(EntityEquipmentSlot.FEET, "boots");

        toolClasses.put("sword", sword);
        toolClasses.put("dagger", dagger);
        toolClasses.put("katana", katana);
        toolClasses.put("machete", machete);
        toolClasses.put("pickaxe", pickaxe);
        toolClasses.put("shovel", shovel);
        toolClasses.put("axe", axe);
        toolClasses.put("hammer", hammer);
        toolClasses.put("excavator", excavator);
        toolClasses.put("mattock", mattock);
        toolClasses.put("sickle", sickle);
        toolClasses.put("bow", bow);

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
            Blueprint blueprint = new Blueprint(singleUse, item);
            blueprints.add(blueprint);
            register(name + "_" + key, blueprint);
        });

        // Part blueprints
        Item.Properties properties = new Item.Properties().group(SilentGear.ITEM_GROUP);
        register(name + "_rod", new Item(properties));
        register(name + "_bowstring", new Item(properties));
    }
}
