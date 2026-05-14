package net.silentchaos512.gear.block.paintmixer;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.CommonHooks;
import net.silentchaos512.gear.api.util.DataResource;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.gear.part.PartInstance;
import net.silentchaos512.gear.setup.SgDataComponents;
import net.silentchaos512.gear.setup.SgTags;
import net.silentchaos512.gear.setup.gear.GearTypes;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.lib.util.ColorBlendAlgorithm;
import net.silentchaos512.lib.util.ColorUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.OptionalInt;

public class PaintUtils {
    public static OptionalInt getPaintMixColor(ItemStack stack) {
        if (stack.has(SgDataComponents.PAINT_COLOR)) {
            return OptionalInt.of(Objects.requireNonNull(stack.get(SgDataComponents.PAINT_COLOR)));
        } else if (stack.has(DataComponents.DYE)) {
            var dyeColor = Objects.requireNonNull(stack.get(DataComponents.DYE));
            return OptionalInt.of(dyeColor.getTextureDiffuseColor());
        } else {
            MaterialInstance material = MaterialInstance.from(stack);
            if (material != null) {
                return OptionalInt.of(material.getColor(GearTypes.ALL.get(), PartTypes.MAIN.get()));
            }
        }
        return OptionalInt.empty();
    }

    public static OptionalInt getPaintOrDyeColor(ItemStack stack) {
        if (stack.has(SgDataComponents.PAINT_COLOR)) {
            return OptionalInt.of(Objects.requireNonNull(stack.get(SgDataComponents.PAINT_COLOR)));
        } else if (stack.has(DataComponents.DYE)) {
            var dyeColor = Objects.requireNonNull(stack.get(DataComponents.DYE));
            return OptionalInt.of(dyeColor.getTextureDiffuseColor());
        }
        return OptionalInt.empty();
    }

    public static OptionalInt getPaintColor(ItemStack stack) {
        if (stack.has(SgDataComponents.PAINT_COLOR)) {
            return OptionalInt.of(Objects.requireNonNull(stack.get(SgDataComponents.PAINT_COLOR)));
        }
        return OptionalInt.empty();
    }

    public static boolean isPaintMixerInput(ItemStack stack) {
        return isPaintMixerColorInput(stack) || stack.is(SgTags.Items.PAINT_FILLER);
    }

    public static boolean isPaintMixerColorInput(ItemStack stack) {
        return getPaintMixColor(stack).isPresent();
    }

    public static OptionalInt getBlendedColor(PaintMixerBlockEntity container) {
        Collection<Integer> colors = new ArrayList<>();
        for (int i = 0; i < PaintMixerBlockEntity.INPUT_SLOT_COUNT; ++i) {
            var stack = container.getItem(i);
            if (!stack.isEmpty()) {
                var color = getPaintMixColor(stack);
                if (color.isPresent()) {
                    colors.add(color.getAsInt());
                } else if (!stack.is(SgTags.Items.PAINT_FILLER)) {
                    return OptionalInt.empty();
                }
            }
        }
        return OptionalInt.of(ColorUtils.blend(ColorBlendAlgorithm.MIXBOX, colors));
    }

    public static PartInstance paint(PartInstance part, int color) {
        var newItem = part.copyItem();
        newItem.set(SgDataComponents.PAINT_COLOR, color);
        return PartInstance.of(DataResource.part(part.getId()), newItem);
    }

    public static ItemStack paint(ItemStack stack, int color) {
        if (GearHelper.isGear(stack)) {
            var coatingOrMainPart = GearData.getCoatingOrMainPart(stack);
            if (coatingOrMainPart == null) {
                return ItemStack.EMPTY;
            }
            var paintedPart = paint(coatingOrMainPart, color);
            var result = stack.copy();
            GearData.addOrReplacePart(result, paintedPart);
            GearData.recalculateGearData(result, CommonHooks.getCraftingPlayer());
            return result;
        }

        var part = PartInstance.from(stack);
        if (part != null) {
            return paint(part, color).copyItem();
        }
        return ItemStack.EMPTY;
    }
}
