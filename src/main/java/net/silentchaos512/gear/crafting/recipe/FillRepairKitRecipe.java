package net.silentchaos512.gear.crafting.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.silentchaos512.gear.api.material.Material;
import net.silentchaos512.gear.api.util.PropertyKey;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.item.RepairKitItem;
import net.silentchaos512.gear.setup.SgRecipes;
import net.silentchaos512.gear.setup.gear.GearProperties;
import net.silentchaos512.gear.setup.gear.GearTypes;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.lib.collection.StackList;

public class FillRepairKitRecipe extends CustomRecipe {
    public FillRepairKitRecipe(CraftingBookCategory bookCategory) {
        super(bookCategory);
    }

    @Override
    public boolean matches(CraftingInput inv, Level worldIn) {
        // Need 1 repair kit and 1+ mats
        boolean kitFound = false;
        int matsFound = 0;

        for (int i = 0; i < inv.size(); ++i) {
            ItemStack stack = inv.getItem(i);
            if (!stack.isEmpty()) {
                if (stack.getItem() instanceof RepairKitItem) {
                    if (kitFound) {
                        return false;
                    }
                    kitFound = true;
                } else if (isRepairMaterial(stack)) {
                    ++matsFound;
                } else {
                    return false;
                }
            }
        }

        return kitFound && matsFound > 0;
    }

    @Override
    public ItemStack assemble(CraftingInput inv, HolderLookup.Provider registryAccess) {
        StackList list = StackList.from(inv);
        ItemStack repairKit = list.uniqueOfType(RepairKitItem.class).copy();
        repairKit.setCount(1);
        RepairKitItem repairKitItem = (RepairKitItem) repairKit.getItem();

        for (ItemStack mat : list.allMatches(FillRepairKitRecipe::isRepairMaterial)) {
            if (!repairKitItem.addMaterial(repairKit, mat)) {
                // Repair kit is too full to accept more materials
                return ItemStack.EMPTY;
            }
        }

        return repairKit;
    }

    private static boolean isRepairMaterial(ItemStack stack) {
        MaterialInstance material = MaterialInstance.from(stack);
        return material != null && isRepairMaterial(material);
    }

    private static boolean isRepairMaterial(MaterialInstance material) {
        float durability = material.getProperty(PartTypes.MAIN, PropertyKey.of(GearProperties.DURABILITY, GearTypes.ALL));
        float armorDurability = material.getProperty(PartTypes.MAIN, PropertyKey.of(GearProperties.ARMOR_DURABILITY, GearTypes.ALL));
        Material mat = material.get();
        return mat != null && mat.isAllowedInPart(material, PartTypes.MAIN.get())
                && (durability > 0 || armorDurability > 0);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SgRecipes.FILL_REPAIR_KIT.get();
    }
}
