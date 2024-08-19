package net.silentchaos512.gear.crafting.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.CommonHooks;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.part.PartList;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.config.Config;
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
    public boolean matches(CraftingContainer inv, Level worldIn) {
        StackList list = StackList.from(inv);
        ItemStack gear = list.uniqueOfType(ICoreItem.class);
        if (gear.isEmpty()) return false;

        ICoreItem item = (ICoreItem) gear.getItem();
        Collection<ItemStack> others = list.allMatches(stack -> !(stack.getItem() instanceof ICoreItem));
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
    public ItemStack assemble(CraftingContainer inv, HolderLookup.Provider registryAccess) {
        StackList list = StackList.from(inv);
        ItemStack gear = list.uniqueOfType(ICoreItem.class);
        if (gear.isEmpty()) return ItemStack.EMPTY;

        Collection<ItemStack> others = list.allMatches(stack -> !(stack.getItem() instanceof ICoreItem));
        if (others.isEmpty()) return ItemStack.EMPTY;

        ItemStack result = gear.copy();
        PartList parts = GearData.getConstruction(gear).parts();
        PartList newParts = PartList.of();

        for (ItemStack stack : others) {
            PartInstance part = PartInstance.from(stack);
            if (part == null) return ItemStack.EMPTY;

            PartType type = part.getType();
            List<PartData> partsOfType = new ArrayList<>(parts.getPartsOfType(type));
            int maxPerItem = type.maxPerItem();

            // Remove old part of type (if over limit), then add replacement
            if (partsOfType.size() >= maxPerItem) {
                PartData oldPart = partsOfType.getFirst();
                partsOfType.remove(oldPart);
                parts.remove(oldPart);
                oldPart.onRemoveFromGear(result);
            }

            parts.add(part);
            newParts.add(part);
        }

        GearData.writeConstructionParts(result, parts);
        GearData.recalculateStats(result, CommonHooks.getCraftingPlayer());
        newParts.forEach(p -> p.onAddToGear(result));

        return result;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer inv) {
        NonNullList<ItemStack> list = NonNullList.withSize(inv.getContainerSize(), ItemStack.EMPTY);
        ItemStack gear = StackList.from(inv).uniqueMatch(s -> s.getItem() instanceof ICoreItem);
        PartList oldParts = GearData.getConstruction(gear).parts();
        Map<PartType, Integer> removedCount = new HashMap<>();

        for (int i = 0; i < list.size(); ++i) {
            ItemStack stack = inv.getItem(i);

            if (stack.getItem() instanceof ICoreItem) {
                list.set(i, ItemStack.EMPTY);
            } else {
                PartInstance newPart = PartInstance.from(stack);
                if (newPart != null && !Config.Common.destroySwappedParts.get()) {
                    PartType type = newPart.getType();
                    List<PartData> partsOfType = oldParts.getPartsOfType(type);

                    if (partsOfType.size() >= type.maxPerItem()) {
                        int index = removedCount.getOrDefault(type, 0);
                        if (index < partsOfType.size()) {
                            // Return old part
                            PartData oldPart = partsOfType.get(index);
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
