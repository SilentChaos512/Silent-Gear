package net.silentchaos512.gear.item.blueprint;

import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.item.ICoreTool;
import net.silentchaos512.gear.api.lib.PartDataList;
import net.silentchaos512.gear.block.craftingstation.GuiCraftingStation;
import net.silentchaos512.gear.init.ModItems;
import net.silentchaos512.gear.item.ToolHead;
import net.silentchaos512.lib.client.key.KeyTrackerSL;
import net.silentchaos512.lib.item.ItemSL;
import net.silentchaos512.lib.registry.RecipeMaker;
import net.silentchaos512.lib.util.LocalizationHelper;
import net.silentchaos512.lib.util.StackHelper;

import java.util.*;
import java.util.function.Function;

public class Blueprint extends ItemSL implements IBlueprint {

    private static final String NBT_GEAR_CLASS = "GearClass";
    private static final String NAME = "blueprint";

    private static Map<String, Function<PartDataList, ItemStack>> crafting = new HashMap<>();
    private static Map<String, IBlueprint.Output> outputs = new HashMap<>();

    public final boolean singleUse;

    public Blueprint(String name, boolean singleUse) {
        super(1, SilentGear.MOD_ID, name);

        this.singleUse = singleUse;
        if (!singleUse)
            setContainerItem(this);
    }

    public static void initSubtypes() {
        ModItems.toolClasses.forEach((key, item) -> addBlueprintType(key, ModItems.toolHead,
                partDataList -> ModItems.toolHead.getStack(key, partDataList)));
        ModItems.armorClasses.forEach((key, item) -> addBlueprintType(key, item.getItem(),
                partDataList -> item.construct(item.getItem(), partDataList)));
    }

    private static void addBlueprintType(String key, Item item, Function<PartDataList, ItemStack> function) {
        crafting.put(key, function);
        ICoreItem gear = item instanceof ICoreItem ? (ICoreItem) item : ModItems.toolClasses.get(key);
        outputs.put(key, new IBlueprint.Output(key, item, gear));
    }

    @Override
    public ItemStack getCraftingResult(ItemStack blueprint, Collection<ItemStack> parts) {
        return crafting.get(getOutputItemType(blueprint)).apply(PartDataList.from(parts));
    }

    @Override
    public Output getOutputInfo(ItemStack blueprint) {
        return outputs.get(getOutputItemType(blueprint));
    }

    public String getOutputItemType(ItemStack blueprint) {
        return StackHelper.getTagCompound(blueprint, true).getString(NBT_GEAR_CLASS);
    }

    public ItemStack getStack(String toolClass) {

        ItemStack result = new ItemStack(this);
        NBTTagCompound tags = StackHelper.getTagCompound(result, true);
        tags.setString(NBT_GEAR_CLASS, toolClass);
        return result;
    }

    @Override
    public void addRecipes(RecipeMaker recipes) {
        Object paper = !singleUse ? ModItems.crafting.blueprintPaper : new ItemStack(Blocks.WOODEN_SLAB, 1, OreDictionary.WILDCARD_VALUE);
        String stick = "stickWood";
        List<String> added = new ArrayList<>();

        addRecipe(recipes, added, "sword", "p", "p", "s", 'p', paper, 's', stick);
        addRecipe(recipes, added, "bow", " ps", "p s", " ps", 'p', paper, 's', stick);
        addRecipe(recipes, added, "pickaxe", "ppp", " s ", " s ", 'p', paper, 's', stick);
        addRecipe(recipes, added, "shovel", "p", "s", "s", 'p', paper, 's', stick);
        addRecipe(recipes, added, "axe", "pp", "ps", " s", 'p', paper, 's', stick);
        addRecipe(recipes, added, "hammer", "ppp", "ppp", "psp", 'p', paper, 's', stick);
        addRecipe(recipes, added, "mattock", "pp ", "psp", " s ", 'p', paper, 's', stick);

        addRecipe(recipes, added, "helmet", "ppp", "psp", 'p', paper, 's', stick);
        addRecipe(recipes, added, "chestplate", "psp", "ppp", "ppp", 'p', paper, 's', stick);
        addRecipe(recipes, added, "leggings", "ppp", "psp", "p p", 'p', paper, 's', stick);
        addRecipe(recipes, added, "boots", "psp", "p p", 'p', paper, 's', stick);

        // Report missing recipes!
        for (String str : ModItems.toolClasses.keySet())
            if (!added.contains(str))
                SilentGear.log.debug("Missing blueprint recipe: " + str);
        for (String str : ModItems.armorClasses.keySet())
            if (!added.contains(str))
                SilentGear.log.debug("Missing blueprint recipe: " + str);
    }

    private void addRecipe(RecipeMaker recipes, List<String> added, String itemType, Object... inputs) {
        added.add(itemType);
        recipes.addShapedOre("blueprint_" + itemType, getStack(itemType), inputs);
    }

    @Override
    public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag flag) {

        LocalizationHelper loc = SilentGear.localization;
        String itemClass = getOutputItemType(stack);

        // Output item class
        IBlueprint.Output output = outputs.get(itemClass);
        if (output == null) {
            list.add(TextFormatting.RED + loc.getItemSubText(NAME, "invalid"));
            list.add(TextFormatting.RED + "class=" + itemClass);
            return;
        }

        String key = output.item.getUnlocalizedName() + "." + (output.item instanceof ToolHead ? itemClass : "name");
        list.add(TextFormatting.AQUA + loc.getLocalizedString(key));
        if (flag.isAdvanced()) {
            list.add(TextFormatting.DARK_GRAY + itemClass);
        }

        // Material required for crafting
        if (output.gear != null) {
            int amount = output.gear.getConfig().getHeadCount();
            list.add(loc.getItemSubText(NAME, "materialAmount", amount));
        }

        // Is mixed material allowed in this GUI?
        if (Minecraft.getMinecraft().currentScreen instanceof GuiCraftingStation) {
            list.add(TextFormatting.GREEN + loc.getItemSubText(NAME, "canMix"));
        } else {
            list.add(TextFormatting.RED + loc.getItemSubText(NAME, "noMixing"));
        }

        // Item recipe
        if (!(output.gear instanceof ICoreTool)) {
            return;
        }
        list.add("");
        if (KeyTrackerSL.isAltDown()) {
            String locToolName = loc.getItemSubText(itemClass, "name");
            list.add(TextFormatting.YELLOW + loc.getItemSubText(NAME, "itemRecipe1", locToolName));
            String toolHeadName = loc.getItemSubText("tool_head", itemClass);
            list.add("  " + loc.getItemSubText(NAME, "itemRecipe2", 1, toolHeadName));

            int rodCount = output.gear.getConfig().getRodCount();
            if (rodCount > 0) {
                String partName = loc.getLocalizedString("part", "type.rod");
                list.add("  " + loc.getItemSubText(NAME, "itemRecipe2", rodCount, partName));
            }
            int bowstringCount = output.gear.getConfig().getBowstringCount();
            if (bowstringCount > 0) {
                String partName = loc.getLocalizedString("part", "type.bowstring");
                list.add("  " + loc.getItemSubText(NAME, "itemRecipe2", bowstringCount, partName));
            }
        } else {
            list.add(TextFormatting.YELLOW + loc.getItemSubText(NAME, "altForRecipe"));
        }
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list) {

        if (!this.isInCreativeTab(tab))
            return;
        ModItems.toolClasses.forEach((key, item) -> list.add(getStack(key)));
        ModItems.armorClasses.forEach((key, item) -> list.add(getStack(key)));
    }
}
