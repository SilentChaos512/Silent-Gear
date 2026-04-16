package net.silentchaos512.gear.crafting.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.silentchaos512.gear.block.paintmixer.PaintUtils;
import net.silentchaos512.gear.gear.part.PartInstance;
import net.silentchaos512.gear.setup.SgRecipes;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.lib.util.ColorBlendAlgorithm;
import net.silentchaos512.lib.util.ColorUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;

public class QuickPaintRecipe extends CustomRecipe {
    public QuickPaintRecipe(CraftingBookCategory category) {
        super(category);
    }

    private boolean isPaintable(ItemStack stack) {
        if (GearHelper.isGear(stack)) {
            return true;
        }
        var part = PartInstance.from(stack);
        return part != null && part.getType().canPaint();
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        int paintCount = 0;
        int paintableCount = 0;

        for (int i = 0; i < input.size(); ++i) {
            var stack = input.getItem(i);

            if (isPaintable(stack)) {
                ++paintableCount;
            } else if (PaintUtils.getPaintOrDyeColor(stack).isPresent()) {
                ++paintCount;
            }
        }

        return paintCount > 0 && paintableCount == 1;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        ItemStack paintable = ItemStack.EMPTY;
        List<Integer> paintColors = new ArrayList<>();

        for (int i = 0; i < input.size(); ++i) {
            var stack = input.getItem(i);

            if (isPaintable(stack)) {
                paintable = stack;
            } else {
                OptionalInt color = PaintUtils.getPaintOrDyeColor(stack);
                if (color.isPresent()) {
                    paintColors.add(color.getAsInt());
                } else {
                    return ItemStack.EMPTY;
                }
            }
        }

        if (paintable.isEmpty() || paintColors.isEmpty()) {
            return ItemStack.EMPTY;
        }

        int newPaintColor = ColorUtils.blend(ColorBlendAlgorithm.MIXBOX, paintColors);
        return PaintUtils.paint(paintable, newPaintColor);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SgRecipes.QUICK_PAINT.get();
    }
}
