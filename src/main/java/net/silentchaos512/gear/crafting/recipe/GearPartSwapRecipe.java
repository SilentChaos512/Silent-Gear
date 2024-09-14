package net.silentchaos512.gear.crafting.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.CommonHooks;
import net.silentchaos512.gear.api.item.GearItem;
import net.silentchaos512.gear.api.part.PartList;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.Config;
import net.silentchaos512.gear.gear.part.PartInstance;
import net.silentchaos512.gear.item.MainPartItem;
import net.silentchaos512.gear.setup.SgRecipes;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.lib.collection.StackList;

import java.util.*;

public class GearPartSwapRecipe extends CustomRecipe {
    public GearPartSwapRecipe(CraftingBookCategory bookCategory) {
        super(bookCategory);
    }

    @Override
    public boolean matches(CraftingInput inv, Level worldIn) {
        StackList list = StackList.from(inv);
        ItemStack gear = list.uniqueOfType(GearItem.class);
        if (gear.isEmpty()) return false;

        GearItem item = (GearItem) gear.getItem();
        Collection<ItemStack> others = list.allMatches(stack -> !(stack.getItem() instanceof GearItem));
        if (others.isEmpty()) return false;

        Map<PartType, Integer> typeCounts = new HashMap<>();

        for (ItemStack stack : others) {
            PartInstance part = PartInstance.from(stack);
            if (part == null) return false;

            // Only required part types, and no duplicates
            PartType type = part.getType();
            if (!item.supportsPart(gear, part) || typeCounts.getOrDefault(type, 0) >= type.maxPerItem()) {
                return false;
            }
            typeCounts.merge(type, 1, Integer::sum);
        }
        return true;
    }

    @Override
    public ItemStack assemble(CraftingInput inv, HolderLookup.Provider registryAccess) {
        StackList list = StackList.from(inv);
        ItemStack gear = list.uniqueOfType(GearItem.class);
        if (gear.isEmpty()) return ItemStack.EMPTY;

        Collection<ItemStack> others = list.allMatches(stack -> !(stack.getItem() instanceof GearItem));
        if (others.isEmpty()) return ItemStack.EMPTY;

        ItemStack result = gear.copy();
        PartList originalParts = GearData.getConstruction(gear).parts();
        List<PartInstance> parts = new ArrayList<>(originalParts);
        PartList newParts = PartList.of();

        for (ItemStack stack : others) {
            PartInstance part = PartInstance.from(stack);
            if (part == null) return ItemStack.EMPTY;

            PartType type = part.getType();
            List<PartInstance> partsOfType = new ArrayList<>(originalParts.getPartsOfType(type));
            int maxPerItem = type.maxPerItem();

            // Remove old part of type (if over limit), then add replacement
            if (partsOfType.size() >= maxPerItem) {
                PartInstance oldPart = partsOfType.getFirst();
                partsOfType.remove(oldPart);
                parts.remove(oldPart);
                oldPart.onRemoveFromGear(result);
            }

            parts.add(part);
            newParts.add(part);
        }

        GearData.writeConstructionParts(result, parts);
        GearData.recalculateGearData(result, CommonHooks.getCraftingPlayer());
        newParts.forEach(p -> p.onAddToGear(result));

        return result;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput inv) {
        NonNullList<ItemStack> list = NonNullList.withSize(inv.size(), ItemStack.EMPTY);
        ItemStack gear = StackList.from(inv).uniqueMatch(s -> s.getItem() instanceof GearItem);
        PartList oldParts = GearData.getConstruction(gear).parts();
        Map<PartType, Integer> removedCount = new HashMap<>();

        for (int i = 0; i < list.size(); ++i) {
            ItemStack stack = inv.getItem(i);

            if (stack.getItem() instanceof GearItem) {
                list.set(i, ItemStack.EMPTY);
            } else {
                PartInstance newPart = PartInstance.from(stack);
                if (newPart != null && !Config.Common.destroySwappedParts.get()) {
                    PartType type = newPart.getType();
                    List<PartInstance> partsOfType = oldParts.getPartsOfType(type);

                    if (partsOfType.size() >= type.maxPerItem()) {
                        int index = removedCount.getOrDefault(type, 0);
                        if (index < partsOfType.size()) {
                            // Return old part
                            PartInstance oldPart = partsOfType.get(index);
                            oldPart.onRemoveFromGear(gear);
                            ItemStack oldPartItem = oldPart.getItem();
                            // Store gear damage on main part item
                            if (oldPartItem.getItem() instanceof MainPartItem) {
                                oldPartItem.setDamageValue(gear.getDamageValue());
                            }
                            list.set(i, oldPartItem);
                            removedCount.merge(type, 1, Integer::sum);
                        }
                    } else {
                        list.set(i, ItemStack.EMPTY);
                    }
                }
            }
        }

        return list;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SgRecipes.SWAP_GEAR_PART.get();
    }
}
