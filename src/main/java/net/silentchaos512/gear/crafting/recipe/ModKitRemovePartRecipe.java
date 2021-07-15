package net.silentchaos512.gear.crafting.recipe;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.gear.part.PartData;
import net.silentchaos512.gear.init.ModRecipes;
import net.silentchaos512.gear.item.ModKitItem;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.lib.collection.StackList;

public class ModKitRemovePartRecipe extends SpecialRecipe {
    public ModKitRemovePartRecipe(ResourceLocation idIn) {
        super(idIn);
    }

    @Override
    public boolean matches(CraftingInventory inv, World worldIn) {
        ItemStack gear = ItemStack.EMPTY;
        boolean foundModKit = false;
        PartType type = PartType.NONE;

        for (int i = 0; i < inv.getContainerSize(); ++i) {
            ItemStack stack = inv.getItem(i);
            if (!stack.isEmpty()) {
                //noinspection ChainOfInstanceofChecks
                if (gear.isEmpty() && stack.getItem() instanceof ICoreItem) {
                    gear = stack;
                } else if (!foundModKit && stack.getItem() instanceof ModKitItem) {
                    type = ModKitItem.getSelectedType(stack);
                    if (type == PartType.NONE) {
                        return false;
                    }
                    foundModKit = true;
                } else {
                    return false;
                }
            }
        }

        return !gear.isEmpty() && foundModKit && GearData.hasPartOfType(gear, type);
    }

    @Override
    public ItemStack assemble(CraftingInventory inv) {
        StackList list = StackList.from(inv);
        ItemStack gear = list.uniqueOfType(ICoreItem.class);
        ItemStack modKit = list.uniqueOfType(ModKitItem.class);
        if (gear.isEmpty() || modKit.isEmpty()) return ItemStack.EMPTY;

        ItemStack result = gear.copy();
        PartType type = ModKitItem.getSelectedType(modKit);

        if (GearData.removeFirstPartOfType(result, type)) {
            GearData.recalculateStats(result, ForgeHooks.getCraftingPlayer());
        }
        return result;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInventory inv) {
        NonNullList<ItemStack> list = NonNullList.withSize(inv.getContainerSize(), ItemStack.EMPTY);
        ItemStack gear = StackList.from(inv).uniqueOfType(ICoreItem.class);
        ItemStack modKit = StackList.from(inv).uniqueOfType(ModKitItem.class);
        PartType type = ModKitItem.getSelectedType(modKit);
        PartData part = GearData.getPartOfType(gear, type);

        for (int i = 0; i < list.size(); ++i) {
            ItemStack stack = inv.getItem(i);

            if (stack.getItem() instanceof ICoreItem) {
                list.set(i, part != null ? part.getItem() : ItemStack.EMPTY);
            } else if (stack.hasContainerItem()) {
                list.set(i, stack.getContainerItem());
            }
        }

        return list;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ModRecipes.MOD_KIT_REMOVE_PART.get();
    }
}
