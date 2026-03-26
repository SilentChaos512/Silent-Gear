package net.silentchaos512.gear.crafting.recipe;

import net.minecraft.world.item.ItemStack;
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

import java.util.ArrayList;
import java.util.List;

public class FillRepairKitRecipe extends CustomRecipe {
    public static final FillRepairKitRecipe INSTANCE = new FillRepairKitRecipe();

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
    public ItemStack assemble(CraftingInput input) {
        ItemStack repairKit = ItemStack.EMPTY;
        RepairKitItem repairKitItem = null;
        List<ItemStack> materials = new ArrayList<>();

        for (ItemStack stack : input.items()) {
            if (stack.getItem() instanceof RepairKitItem item) {
                repairKit = stack.copy();
                repairKitItem = item;
            } else if (isRepairMaterial(stack)) {
                materials.add(stack);
            }
        }
        if (repairKit.isEmpty() || repairKitItem == null || materials.isEmpty()) {
            return ItemStack.EMPTY;
        }

        // Apply materials to repair kit copy
        for (ItemStack materialStack : materials) {
            if (!repairKitItem.addMaterial(repairKit, materialStack)) {
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
        if (!material.isValid()) return false;

        float durability = material.getProperty(PartTypes.MAIN, PropertyKey.of(GearProperties.DURABILITY, GearTypes.ALL));
        float armorDurability = material.getProperty(PartTypes.MAIN, PropertyKey.of(GearProperties.ARMOR_DURABILITY, GearTypes.ALL));
        Material mat = material.get();
        return mat.isAllowedInPart(material, PartTypes.MAIN.get())
                && (durability > 0 || armorDurability > 0);
    }

    @Override
    public RecipeSerializer<? extends CustomRecipe> getSerializer() {
        return SgRecipes.FILL_REPAIR_KIT.get();
    }
}
