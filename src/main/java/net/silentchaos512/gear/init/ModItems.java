package net.silentchaos512.gear.init;

import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.silentchaos512.gear.GuideBookToolMod;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.ICoreArmor;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.item.ICoreTool;
import net.silentchaos512.gear.item.*;
import net.silentchaos512.gear.item.blueprint.Blueprint;
import net.silentchaos512.gear.item.gear.*;
import net.silentchaos512.lib.item.IEnumItems;
import net.silentchaos512.lib.item.ItemGuideBookSL;
import net.silentchaos512.lib.registry.SRegistry;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ModItems {
    public static final Map<String, ICoreTool> toolClasses = new LinkedHashMap<>();
    public static final Map<String, ICoreArmor> armorClasses = new LinkedHashMap<>();
    public static final Map<String, ICoreItem> gearClasses = new LinkedHashMap<>();
    public static final List<Blueprint> blueprints = new ArrayList<>();

    public static ItemGuideBookSL guideBook = new ItemGuideBookSL(new GuideBookToolMod());
    public static BlueprintPackage blueprintPackage = new BlueprintPackage(new ResourceLocation(SilentGear.MOD_ID, "starter_blueprints"));

    public static Flaxseeds flaxseeds = new Flaxseeds();

    public static ToolHead toolHead = new ToolHead();

    public static CoreSword sword = new CoreSword();
    public static CoreDagger dagger = new CoreDagger();
    public static CoreKatana katana = new CoreKatana();
    public static CoreMachete machete = new CoreMachete();
    public static CorePickaxe pickaxe = new CorePickaxe();
    public static CoreShovel shovel = new CoreShovel();
    public static CoreAxe axe = new CoreAxe();
    public static CoreHammer hammer = new CoreHammer();
    public static CoreExcavator excavator = new CoreExcavator();
    public static CoreMattock mattock = new CoreMattock();
    public static CoreSickle sickle = new CoreSickle();
    public static CoreBow bow = new CoreBow();

    public static CoreArmor helmet = new CoreArmor(EntityEquipmentSlot.HEAD, "helmet");
    public static CoreArmor chestplate = new CoreArmor(EntityEquipmentSlot.CHEST, "chestplate");
    public static CoreArmor leggings = new CoreArmor(EntityEquipmentSlot.LEGS, "leggings");
    public static CoreArmor boots = new CoreArmor(EntityEquipmentSlot.FEET, "boots");

    public static void registerAll(SRegistry reg) {
        guideBook.giveBookOnFirstLogin = false;

        // Build gear maps now because blueprints need them
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

        IEnumItems.RegistrationHelper enumItems = new IEnumItems.RegistrationHelper(reg);

        reg.registerItem(guideBook, "guide_book");
        reg.registerItem(blueprintPackage, "blueprint_package");

        // Blueprints/templates
        registerBlueprints(reg, "blueprint", false);
        registerBlueprints(reg, "template", true);
//        reg.registerItem(blueprintBook);

        enumItems.registerItems(TipUpgrades.values());
        enumItems.registerItems(MiscUpgrades.values());
        enumItems.registerItems(CraftingItems.values());
        reg.registerItem(flaxseeds, "flaxseeds");

        reg.registerItem(toolHead, "tool_head");

        // Tools/armor
        toolClasses.forEach((key, item) -> reg.registerItem(item.getItem(), key));
        armorClasses.forEach((key, item) -> reg.registerItem(item.getItem(), key));

        if (SilentGear.instance.getBuildNum() == 0) {
            reg.registerItem(new TestItem(), "test_item");
        }

        registerOreDictEntries();
        addSmeltingRecipes();
    }

    private static void registerOreDictEntries() {
        OreDictionary.registerOre("flint", Items.FLINT);
        CraftingItems.registerOreDict();
    }

    private static void addSmeltingRecipes() {
        GameRegistry.addSmelting(CraftingItems.SINEW.getStack(), CraftingItems.DRIED_SINEW.getStack(), 0.1f);
    }

    private static void registerBlueprints(SRegistry reg, String name, boolean singleUse) {
        toolClasses.forEach((key, item) -> {
            Blueprint blueprint = new Blueprint(singleUse, item,
                    partList -> toolHead.getStack(key, partList));
            blueprints.add(blueprint);
            reg.registerItem(blueprint, name + "_" + key);
        });
        armorClasses.forEach((key, item) -> {
            Blueprint blueprint = new Blueprint(singleUse, item,
                    partList -> item.construct(item.getItem(), partList));
            blueprints.add(blueprint);
            reg.registerItem(blueprint, name + "_" + key);
        });

        // Part blueprints (just rods right now, are any others needed?)
        Item item = new Item();
        if (!singleUse) {
            item.setContainerItem(item);
        }
        reg.registerItem(item, name + "_rod");
        OreDictionary.registerOre("blueprintRod", item);
    }
}
