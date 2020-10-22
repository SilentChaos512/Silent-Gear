package net.silentchaos512.gear.crafting.recipe;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.part.IGearPart;
import net.silentchaos512.gear.api.part.PartDataList;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.gear.part.PartData;
import net.silentchaos512.gear.gear.part.PartManager;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.lib.collection.StackList;

import java.util.*;

public class GearPartSwapRecipe extends SpecialRecipe {
    public static final ResourceLocation NAME = new ResourceLocation(SilentGear.MOD_ID, "swap_gear_part");
    public static final SpecialRecipeSerializer<GearPartSwapRecipe> SERIALIZER = new SpecialRecipeSerializer<>(GearPartSwapRecipe::new);

    public GearPartSwapRecipe(ResourceLocation idIn) {
        super(idIn);
    }

    @Override
    public boolean matches(CraftingInventory inv, World worldIn) {
        StackList list = StackList.from(inv);
        ItemStack gear = list.uniqueOfType(ICoreItem.class);
        if (gear.isEmpty()) return false;

        ICoreItem item = (ICoreItem) gear.getItem();
        Collection<ItemStack> others = list.allMatches(stack -> !(stack.getItem() instanceof ICoreItem));
        if (others.isEmpty()) return false;

        Map<PartType, Integer> typeCounts = new HashMap<>();

        for (ItemStack stack : others) {
            PartData part = PartData.from(stack);
            if (part == null) return false;

            // Only required part types, and no duplicates
            PartType type = part.getType();
            if (!item.supportsPart(gear, part) || typeCounts.getOrDefault(type, 0) >= type.getMaxPerItem(item.getGearType())) {
                return false;
            }
            typeCounts.merge(type, 1, Integer::sum);
        }
        return true;
    }

    @Override
    public ItemStack getCraftingResult(CraftingInventory inv) {
        StackList list = StackList.from(inv);
        ItemStack gear = list.uniqueOfType(ICoreItem.class);
        if (gear.isEmpty()) return ItemStack.EMPTY;

        Collection<ItemStack> others = list.allMatches(stack -> !(stack.getItem() instanceof ICoreItem));
        if (others.isEmpty()) return ItemStack.EMPTY;

        ItemStack result = gear.copy();
        PartDataList parts = GearData.getConstructionParts(result);

        for (ItemStack stack : others) {
            PartData part = PartData.from(stack);
            if (part == null) return ItemStack.EMPTY;

            PartType type = part.getType();
            List<PartData> partsOfType = new ArrayList<>(parts.getPartsOfType(type));
            int maxPerItem = type.getMaxPerItem(GearHelper.getType(result));

            // Remove old part of type (if over limit), then add replacement
            if (partsOfType.size() >= maxPerItem) {
                PartData oldPart = partsOfType.get(0);
                partsOfType.remove(oldPart);
                parts.remove(oldPart);
                oldPart.onRemoveFromGear(result);
            }

            parts.add(part);
            part.onAddToGear(result);
        }

        GearData.writeConstructionParts(result, parts);
        GearData.removeExcessParts(result);
        GearData.recalculateStats(result, null);
        return result;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInventory inv) {
        NonNullList<ItemStack> list = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
        ItemStack gear = StackList.from(inv).uniqueMatch(s -> s.getItem() instanceof ICoreItem);
        PartDataList oldParts = GearData.getConstructionParts(gear);
        Map<PartType, Integer> removedCount = new HashMap<>();

        for (int i = 0; i < list.size(); ++i) {
            ItemStack stack = inv.getStackInSlot(i);

            if (stack.getItem() instanceof ICoreItem) {
                list.set(i, ItemStack.EMPTY);
            } else {
                IGearPart newPart = PartManager.from(stack);
                if (newPart != null) {
                    PartType type = newPart.getType();
                    List<PartData> partsOfType = oldParts.getPartsOfType(type);

                    if (partsOfType.size() >= type.getMaxPerItem(GearHelper.getType(gear))) {
                        int index = removedCount.getOrDefault(type, 0);
                        if (index < partsOfType.size()) {
                            PartData oldPart = partsOfType.get(index);
                            oldPart.onRemoveFromGear(gear);
                            list.set(i, oldPart.getCraftingItem());
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
    public boolean canFit(int width, int height) {
        return true;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }
}
