package net.silentchaos512.gear.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.item.ICoreTool;
import net.silentchaos512.gear.api.parts.ItemPartData;
import net.silentchaos512.gear.api.parts.PartDataList;
import net.silentchaos512.gear.block.craftingstation.ContainerCraftingStation;
import net.silentchaos512.gear.block.craftingstation.TileCraftingStation;
import net.silentchaos512.gear.init.ModBlocks;
import net.silentchaos512.gear.init.ModItems;
import net.silentchaos512.gear.init.ModMaterials;
import net.silentchaos512.gear.item.MiscUpgrades;
import net.silentchaos512.gear.item.TipUpgrades;
import net.silentchaos512.gear.item.ToolRods;
import net.silentchaos512.gear.item.gear.CoreArmor;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.lib.registry.RecipeMaker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JEIPlugin
public class JeiPlugin implements IModPlugin {

    @Override
    public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry) {
        subtypeRegistry.registerSubtypeInterpreter(ModItems.toolHead, s -> ModItems.toolHead.getSubtypeKey(s));

//        ModItems.gearClasses.forEach(
//                (key, item) -> subtypeRegistry.registerSubtypeInterpreter(item.getItem(),
//                        stack -> {
//                            ItemPartData part = GearData.getPrimaryPart(stack);
//                            return part == null ? key : key + "|" + part.getPart().getRegistryName().toString();
//                        }));
    }

    @Override
    public void register(IModRegistry registry) {
        RecipeMaker recipeMaker = SilentGear.registry.getRecipeMaker();

        registry.addRecipeCatalyst(new ItemStack(ModBlocks.craftingStation), VanillaRecipeCategoryUid.CRAFTING);
        registry.getRecipeTransferRegistry().addRecipeTransferHandler(ContainerCraftingStation.class, VanillaRecipeCategoryUid.CRAFTING,
                1, 9, 10, 36 + TileCraftingStation.SIDE_INVENTORY_SLOT_COUNT);

        // Add "example recipes". We can't allow these to be crafted, but it's helpful to have them
        // show in JEI. Some people can't read...
        registry.addRecipes(ModItems.toolHead.getExampleRecipes(), VanillaRecipeCategoryUid.CRAFTING);
        for (ICoreItem item : ModItems.toolClasses.values())
            registry.addRecipes(GearHelper.getExampleRecipes(item), VanillaRecipeCategoryUid.CRAFTING);
        for (ICoreItem item : ModItems.armorClasses.values())
            if (item instanceof CoreArmor)
                registry.addRecipes(((CoreArmor) item).getExampleRecipes(), VanillaRecipeCategoryUid.CRAFTING);

        // Examples of applying upgrades, yay!
        List<IRecipe> builtRecipes = new ArrayList<>();
        for (ICoreItem item : ModItems.gearClasses.values()) {
            ItemStack gear = getSampleStack(item);
            // Tipped upgrades
            if (item instanceof ICoreTool) {
                for (TipUpgrades tip : TipUpgrades.values()) {
                    if (tip.getPart().isValidFor(item)) {
                        ItemStack gearWithTips = gear.copy();
                        GearData.addUpgradePart(gearWithTips, ItemPartData.instance(tip.getPart()));
                        GearData.recalculateStats(gearWithTips);
                        builtRecipes.add(recipeMaker.makeShapeless(gearWithTips, gear, tip.getItem()));
                    }
                }
            }
            // Misc upgrades
            for (MiscUpgrades upgrade : MiscUpgrades.values()) {
                if (upgrade.getPart().isValidFor(item)) {
                    ItemStack gearWithUpgrade = gear.copy();
                    GearData.addUpgradePart(gearWithUpgrade, ItemPartData.instance(upgrade.getPart()));
                    GearData.recalculateStats(gearWithUpgrade);
                    builtRecipes.add(recipeMaker.makeShapeless(gearWithUpgrade, gear, upgrade.getItem()));
                }
            }
        }
        registry.addRecipes(builtRecipes, VanillaRecipeCategoryUid.CRAFTING);
    }

    private Map<ResourceLocation, ItemStack> sampleStacks = new HashMap<>();

    private ItemStack getSampleStack(ICoreItem gearItem) {
        ResourceLocation name = gearItem.getItem().getRegistryName();
        if (!sampleStacks.containsKey(name)) {
            PartDataList parts = PartDataList.of();
            for (int i = 0; i < gearItem.getConfig().getHeadCount(); ++i)
                parts.addPart(ModMaterials.mainIron);
            if (gearItem.getConfig().getRodCount() > 0)
                parts.addPart(ToolRods.WOOD.getPart());
            if (gearItem.getConfig().getBowstringCount() > 0)
                parts.addPart(ModMaterials.bowstringString);

            ItemStack stack = gearItem.construct(gearItem.getItem(), parts);

            // Add text to indicate this is a sample, using vanilla lore tag
            NBTTagList tagList = new NBTTagList();
            tagList.appendTag(new NBTTagString(SilentGear.i18n.translate("jei", "tooltip.sample1")));
            tagList.appendTag(new NBTTagString(SilentGear.i18n.translate("jei", "tooltip.sample2")));
            stack.getOrCreateSubCompound("display").setTag("Lore", tagList);
            sampleStacks.put(name, stack);
        }
        return sampleStacks.get(name);
    }
}
