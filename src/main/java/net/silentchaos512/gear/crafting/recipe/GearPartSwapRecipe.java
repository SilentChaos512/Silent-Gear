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
import net.silentchaos512.gear.api.parts.IGearPart;
import net.silentchaos512.gear.api.parts.IPartData;
import net.silentchaos512.gear.api.parts.PartDataList;
import net.silentchaos512.gear.api.parts.PartType;
import net.silentchaos512.gear.parts.PartData;
import net.silentchaos512.gear.parts.PartManager;
import net.silentchaos512.gear.parts.type.CompoundPart;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.lib.collection.StackList;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

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

        Collection<PartType> typesFound = new HashSet<>();

        for (ItemStack stack : others) {
            PartData part = PartData.fromStackFast(stack);
            if (part == null) return false;

            // Only required part types (no mains), and no duplicates
            PartType type = part.getType();
            if (isLegacyMain(part) || !item.supportsPartOfType(type) || typesFound.contains(type)) {
                return false;
            }
            typesFound.add(type);
        }
        return true;
    }

    private boolean isLegacyMain(IPartData part) {
        return part.getType() == PartType.MAIN && !(part.getPart() instanceof CompoundPart);
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

            // Remove old part of type, replace
            PartType type = part.getType();
            Collection<PartData> toRemove = parts.stream().filter(p -> p.getType() == type).collect(Collectors.toList());
            parts.removeAll(toRemove);
            toRemove.forEach(p -> p.onRemoveFromGear(result));
            parts.add(part);
            part.onAddToGear(result);
        }

        GearData.writeConstructionParts(result, parts);
        GearData.recalculateStats(result, null);
        return result;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInventory inv) {
        NonNullList<ItemStack> list = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
        StackList stackList = StackList.from(inv);
        ItemStack gear = stackList.uniqueMatch(s -> s.getItem() instanceof ICoreItem);
        PartDataList oldParts = GearData.getConstructionParts(gear);

        for (int i = 0; i < list.size(); ++i) {
            ItemStack stack = inv.getStackInSlot(i);

            if (stack.getItem() instanceof ICoreItem) {
                list.set(i, ItemStack.EMPTY);
            } else {
                IGearPart part = PartManager.from(stack);
                if (part != null) {
                    List<PartData> partsOfType = oldParts.getPartsOfType(part.getType());
                    if (!partsOfType.isEmpty()) {
                        PartData partData = partsOfType.get(0);
                        partData.onRemoveFromGear(gear);
                        list.set(i, partData.getCraftingItem());
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
