package net.silentchaos512.gear.init;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.silentchaos512.gear.GuideBookToolMod;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.ICoreArmor;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.item.ICoreTool;
import net.silentchaos512.gear.api.parts.PartDataList;
import net.silentchaos512.gear.item.*;
import net.silentchaos512.gear.item.blueprint.Blueprint;
import net.silentchaos512.gear.item.blueprint.BlueprintBook;
import net.silentchaos512.gear.item.gear.*;
import net.silentchaos512.lib.item.IEnumItems;
import net.silentchaos512.lib.item.ItemGuideBookSL;
import net.silentchaos512.lib.registry.IRegistrationHandler;
import net.silentchaos512.lib.registry.SRegistry;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public class ModItems implements IRegistrationHandler<Item> {

    public static final ModItems INSTANCE = new ModItems();
    public static final Map<String, ICoreTool> toolClasses = new LinkedHashMap<>();
    public static final Map<String, ICoreArmor> armorClasses = new LinkedHashMap<>();
    public static final Map<String, ICoreItem> gearClasses = new LinkedHashMap<>();

    public static ItemGuideBookSL guideBook = new ItemGuideBookSL(new GuideBookToolMod());

    public static BlueprintBook blueprintBook = new BlueprintBook();
    public static Flaxseeds flaxseeds = new Flaxseeds();

    public static ToolHead toolHead = new ToolHead();

    public static CoreSword sword = new CoreSword();
    public static CoreDagger dagger = new CoreDagger();
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
        // Build gear maps now because blueprints need them
        toolClasses.put("sword", sword);
        toolClasses.put("dagger", dagger);
        toolClasses.put("pickaxe", pickaxe);
        toolClasses.put("shovel", shovel);
        toolClasses.put("axe", axe);
        toolClasses.put("hammer", hammer);
        toolClasses.put("mattock", mattock);
        toolClasses.put("sickle", sickle);
        toolClasses.put("bow", bow);

        armorClasses.put("helmet", helmet);
        armorClasses.put("chestplate", chestplate);
        armorClasses.put("leggings", leggings);
        armorClasses.put("boots", boots);

        gearClasses.putAll(toolClasses);
        gearClasses.putAll(armorClasses);

        reg.registerItem(guideBook, "guide_book");

        // Blueprints/templates
        registerBlueprints(reg, "blueprint", false);
        registerBlueprints(reg, "template", true);
        reg.registerItem(blueprintBook);

        IEnumItems.registerItems(TipUpgrade.Type.values(), reg);
        IEnumItems.registerItems(CraftingItems.values(), reg);
        reg.registerItem(flaxseeds, "flaxseeds");

        reg.registerItem(toolHead, "tool_head");

        // Tools/armor
        toolClasses.forEach((key, item) -> reg.registerItem(item.getItem(), key));
        armorClasses.forEach((key, item) -> reg.registerItem(item.getItem(), key));

        if (SilentGear.instance.getBuildNum() == 0) {
            reg.registerItem(new TestItem(), "test_item");
        }

        registerOreDictEntries();
    }

    private void registerOreDictEntries() {
        OreDictionary.registerOre("dyeBlack", CraftingItems.BLACK_DYE.getItem());
        OreDictionary.registerOre("dyeBlue", CraftingItems.BLUE_DYE.getItem());
        OreDictionary.registerOre("nuggetDiamond", CraftingItems.DIAMOND_SHARD.getItem());
        OreDictionary.registerOre("nuggetEmerald", CraftingItems.EMERALD_SHARD.getItem());
        OreDictionary.registerOre("stickIron", CraftingItems.IRON_ROD.getItem());
        OreDictionary.registerOre("stickStone", CraftingItems.STONE_ROD.getItem());
    }

    private void registerBlueprints(SRegistry reg, String name, boolean singleUse) {
        toolClasses.forEach((key, item) -> {
            Function<PartDataList, ItemStack> funcToolHead = partList -> toolHead.getStack(key, partList);
            reg.registerItem(new Blueprint(singleUse, item, funcToolHead), name + "_" + key);
        });
        armorClasses.forEach((key, item) -> {
            Function<PartDataList, ItemStack> funcArmor = partList -> item.construct(item.getItem(), partList);
            reg.registerItem(new Blueprint(singleUse, item, funcArmor), name + "_" + key);
        });
    }
}
