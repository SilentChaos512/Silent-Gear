package net.silentchaos512.gear.init;

import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.GuideBookToolMod;
import net.silentchaos512.gear.api.item.ICoreArmor;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.item.ICoreTool;
import net.silentchaos512.gear.item.*;
import net.silentchaos512.gear.item.blueprint.Blueprint;
import net.silentchaos512.gear.item.blueprint.BlueprintBook;
import net.silentchaos512.gear.item.gear.*;
import net.silentchaos512.lib.item.ItemGuideBookSL;
import net.silentchaos512.lib.item.ItemSL;
import net.silentchaos512.lib.registry.IRegistrationHandler;
import net.silentchaos512.lib.registry.IRegistryObject;
import net.silentchaos512.lib.registry.RecipeMaker;
import net.silentchaos512.lib.registry.SRegistry;

import java.util.LinkedHashMap;
import java.util.Map;

public class ModItems implements IRegistrationHandler<Item> {

    public static final ModItems INSTANCE = new ModItems();
    public static Map<String, ICoreItem> toolClasses = new LinkedHashMap<>();
    public static Map<String, ICoreArmor> armorClasses = new LinkedHashMap<>();

    public static ItemGuideBookSL guideBook = new ItemGuideBookSL(new GuideBookToolMod());

    public static Blueprint blueprint = new Blueprint("blueprint", false);
    public static Blueprint template = new Blueprint("template", true);
    public static BlueprintBook blueprintBook = new BlueprintBook();
    public static TipUpgrade tipUpgrade = new TipUpgrade();
    public static CraftingItem crafting = new CraftingItem();
    public static Dye dye = new Dye();
    public static Flaxseeds flaxseeds = new Flaxseeds();
    public static ItemSL flaxFiber = new ItemSL(1, SilentGear.MOD_ID, "flax_fiber");

    public static ToolHead toolHead = new ToolHead();

    public static CoreSword sword = new CoreSword();
    public static CorePickaxe pickaxe = new CorePickaxe();
    public static CoreShovel shovel = new CoreShovel();
    public static CoreAxe axe = new CoreAxe();
    public static CoreHammer hammer = new CoreHammer();
    public static CoreMattock mattock = new CoreMattock();
    public static CoreSickle sickle = new CoreSickle();
    public static CoreBow bow = new CoreBow();

    public static CoreArmor helmet = new CoreArmor(EntityEquipmentSlot.HEAD, "helmet");
    public static CoreArmor chestplate = new CoreArmor(EntityEquipmentSlot.CHEST, "chestplate");
    public static CoreArmor leggings = new CoreArmor(EntityEquipmentSlot.LEGS, "leggings");
    public static CoreArmor boots = new CoreArmor(EntityEquipmentSlot.FEET, "boots");

    @Override
    public void registerAll(SRegistry reg) {
        reg.registerItem(guideBook);

        reg.registerItem(blueprint);
        reg.registerItem(template);
        reg.registerItem(blueprintBook);
        reg.registerItem(tipUpgrade);
        reg.registerItem(crafting);
        reg.registerItem(dye);
        reg.registerItem(flaxseeds);
        reg.registerItem(flaxFiber);

        reg.registerItem(toolHead);

        registerTool(reg, sword);
        registerTool(reg, pickaxe);
        registerTool(reg, shovel);
        registerTool(reg, axe);
        registerTool(reg, hammer);
        registerTool(reg, mattock);
        registerTool(reg, sickle);
        registerTool(reg, bow);

        registerArmor(reg, helmet);
        registerArmor(reg, chestplate);
        registerArmor(reg, leggings);
        registerArmor(reg, boots);

        // Extra initializations
        Blueprint.initSubtypes();

        // Extra recipes
        RecipeMaker recipes = SilentGear.registry.recipes;
        recipes.addShapeless("guide_book", new ItemStack(guideBook), Items.BOOK, ModItems.crafting.blueprintPaper);
    }

    private <T extends Item & IRegistryObject & ICoreTool> T registerTool(SRegistry reg, T item) {
        reg.registerItem(item);
        toolClasses.put(item.getName(), item);
        return item;
    }

    private <T extends Item & IRegistryObject & ICoreArmor> T registerArmor(SRegistry reg, T item) {
        reg.registerItem(item);
        armorClasses.put(item.getName(), item);
        return item;
    }
}
