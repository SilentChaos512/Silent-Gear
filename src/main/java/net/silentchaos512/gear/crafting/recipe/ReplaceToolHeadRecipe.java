package net.silentchaos512.gear.crafting.recipe;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.ICoreTool;
import net.silentchaos512.gear.api.parts.PartDataList;
import net.silentchaos512.gear.api.parts.PartType;
import net.silentchaos512.gear.parts.PartData;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.lib.collection.StackList;

import java.util.List;

public class ReplaceToolHeadRecipe extends SpecialRecipe {
    public static final ResourceLocation NAME = SilentGear.getId("replace_tool_head");
    public static final IRecipeSerializer<ReplaceToolHeadRecipe> SERIALIZER = new SpecialRecipeSerializer<>(ReplaceToolHeadRecipe::new);

    public ReplaceToolHeadRecipe(ResourceLocation idIn) {
        super(idIn);
    }

    @Override
    public boolean matches(CraftingInventory inv, World worldIn) {
        ItemStack tool1 = ItemStack.EMPTY;
        ItemStack tool2 = ItemStack.EMPTY;

        for (int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack stack = inv.getStackInSlot(i);
            if (!stack.isEmpty()) {
                // Need two tools
                if (stack.getItem() instanceof ICoreTool) {
                    if (tool1.isEmpty())
                        tool1 = stack;
                    else if (tool2.isEmpty())
                        tool2 = stack;
                    else
                        return false;
                } else {
                    return false;
                }
            }
        }

        return !tool1.isEmpty() && !tool2.isEmpty()
                && tool1.getItem() == tool2.getItem()
                && (isToolAndToolHead(tool1, tool2) || isToolAndToolHead(tool2, tool1));

    }

    private static boolean isToolAndToolHead(ItemStack tool, ItemStack toolHead) {
        PartDataList toolParts = GearData.getConstructionParts(tool);
        if (toolParts.isEmpty() || isToolHead(toolParts)) return false;

        PartDataList toolHeadParts = GearData.getConstructionParts(toolHead);
        return !toolHeadParts.isEmpty() && isToolHead(toolHeadParts);
    }

    private static boolean isToolHead(PartDataList parts) {
        return parts.stream().allMatch(p -> {
            PartType type = p.getType();
            return type == PartType.MAIN || type == PartType.TIP || type == PartType.HIGHLIGHT;
        });
    }

    @Override
    public ItemStack getCraftingResult(CraftingInventory inv) {
        ItemStack tool = ItemStack.EMPTY;
        ItemStack toolHead = ItemStack.EMPTY;

        for (int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack stack = inv.getStackInSlot(i);
            if (!stack.isEmpty()) {
                if (stack.getItem() instanceof ICoreTool) {
                    PartDataList parts = GearData.getConstructionParts(stack);
                    if (isToolHead(parts))
                        toolHead = stack;
                    else
                        tool = stack;
                }
            }
        }

        // Move the tool head's parts to the tool
        PartDataList toolParts = GearData.getConstructionParts(tool);
        PartDataList headParts = GearData.getConstructionParts(toolHead);
        List<PartData> oldHead = toolParts.getMains();
        List<PartData> newHead = headParts.getMains();
        toolParts.removeAll(oldHead);
        toolParts.addAll(newHead);

        ItemStack result = tool.copy();
        GearData.writeConstructionParts(result, toolParts);
        GearData.recalculateStats(result, ForgeHooks.getCraftingPlayer());
        result.setDamage(toolHead.getDamage());
        return result;
    }

    @Override
    public boolean canFit(int width, int height) {
        return true;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInventory inv) {
        NonNullList<ItemStack> list = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);

        for(int i = 0; i < list.size(); ++i) {
            ItemStack stack = inv.getStackInSlot(i);
            PartDataList parts = GearData.getConstructionParts(stack);

            if (isToolHead(parts)) {
                // Remove the swapped-in head
                list.set(i, ItemStack.EMPTY);
            } else {
                // Create a "new" tool head with the main parts of this tool
                ItemStack toolHead = StackList.from(inv).uniqueMatch(s -> isToolHead(GearData.getConstructionParts(s)));
                ItemStack copy = toolHead.copy();
                parts.removeIf(p -> p.getType() != PartType.MAIN);
                GearData.writeConstructionParts(copy, parts);
                GearData.recalculateStats(copy, ForgeHooks.getCraftingPlayer());
                copy.setDamage(stack.getDamage());
                list.set(i, copy);
            }
        }

        return list;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }
}
