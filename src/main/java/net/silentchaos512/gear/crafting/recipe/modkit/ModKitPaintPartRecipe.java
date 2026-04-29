package net.silentchaos512.gear.crafting.recipe.modkit;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.CommonHooks;
import net.silentchaos512.gear.api.part.PartList;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.block.paintmixer.PaintUtils;
import net.silentchaos512.gear.item.ModKitItem;
import net.silentchaos512.gear.setup.SgRecipes;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.lib.util.ColorBlendAlgorithm;
import net.silentchaos512.lib.util.ColorUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;

public class ModKitPaintPartRecipe extends ModKitRecipe {
    public ModKitPaintPartRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean isActionSupported(PartType partType) {
        return partType.canPaint();
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        ItemStack gearItem = ItemStack.EMPTY;
        boolean foundModKit = false;
        PartType partType = PartTypes.NONE.get();
        int paintAndDyeCount = 0;

        for (int i = 0; i < input.size(); ++i) {
            var stack = input.getItem(i);
            if (stack.isEmpty()) continue;

            if (PaintUtils.getPaintOrDyeColor(stack).isPresent()) {
                ++paintAndDyeCount;
            } else if (isModKit(stack)) {
                if (foundModKit) {
                    // multiple mod kits
                    return false;
                }
                partType = ModKitItem.getSelectedType(stack);
                if (!isActionSupported(partType)) {
                    // selected part type does not allow paint
                    return false;
                }
                foundModKit = true;
            } else if (GearHelper.isGear(stack)) {
                if (!gearItem.isEmpty()) {
                    // multiple gear items
                    return false;
                }
                gearItem = stack;
            }
        }

        return paintAndDyeCount > 0 && foundModKit && !gearItem.isEmpty() && GearData.hasPartOfType(gearItem, partType);
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        ItemStack gearItem = ItemStack.EMPTY;
        ItemStack modKit = ItemStack.EMPTY;
        List<Integer> paintColors = new ArrayList<>();

        for (int i = 0; i < input.size(); ++i) {
            var stack = input.getItem(i);
            if (stack.isEmpty()) continue;

            if (GearHelper.isGear(stack)) {
                gearItem = stack;
            } else if (isModKit(stack)) {
                modKit = stack;
            } else {
                OptionalInt color = PaintUtils.getPaintOrDyeColor(stack);
                if (color.isPresent()) {
                    paintColors.add(color.getAsInt());
                } else {
                    return ItemStack.EMPTY;
                }
            }
        }

        if (gearItem.isEmpty() || modKit.isEmpty() || paintColors.isEmpty()) {
            return ItemStack.EMPTY;
        }

        PartType partType = ModKitItem.getSelectedType(modKit);
        int newPaintColor = ColorUtils.blend(ColorBlendAlgorithm.MIXBOX, paintColors);
        var construction = GearData.getConstruction(gearItem);
        var newPartList = PartList.of();
        for (var part : construction.parts()) {
            if (part.getType().equals(partType)) {
                newPartList.add(PaintUtils.paint(part, newPaintColor));
            } else {
                newPartList.add(part);
            }
        }

        var result = gearItem.copy();
        GearData.writeConstructionParts(result, newPartList);
        GearData.recalculateGearData(result, CommonHooks.getCraftingPlayer());
        return result;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SgRecipes.MOD_KIT_PAINT_PART.get();
    }
}
