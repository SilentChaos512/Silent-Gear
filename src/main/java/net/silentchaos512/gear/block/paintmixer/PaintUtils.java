package net.silentchaos512.gear.block.paintmixer;

import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.client.util.ColorUtils;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.setup.SgDataComponents;
import net.silentchaos512.gear.setup.gear.GearTypes;
import net.silentchaos512.gear.setup.gear.PartTypes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.OptionalInt;

public class PaintUtils {
    public static OptionalInt getPaintColor(ItemStack stack) {
        if (stack.has(SgDataComponents.PAINT_COLOR)) {
            return OptionalInt.of(Objects.requireNonNull(stack.get(SgDataComponents.PAINT_COLOR)));
        } else if (stack.getItem() instanceof DyeItem dyeItem) {
            return OptionalInt.of(dyeItem.getDyeColor().getTextureDiffuseColor());
        } else {
            MaterialInstance material = MaterialInstance.from(stack);
            if (material != null) {
                return OptionalInt.of(material.getColor(GearTypes.ALL.get(), PartTypes.MAIN.get()));
            }
        }
        return OptionalInt.empty();
    }

    public static OptionalInt getBlendedColor(PaintMixerBlockEntity container) {
        Collection<Integer> colors = new ArrayList<>();
        for (int i = 0; i < PaintMixerBlockEntity.INPUT_SLOT_COUNT; ++i) {
            var stack = container.getItem(i);
            if (!stack.isEmpty()) {
                var color = getPaintColor(stack);
                if (color.isPresent()) {
                    colors.add(color.getAsInt());
                } else {
                    return OptionalInt.empty();
                }
            }
        }
        return OptionalInt.of(ColorUtils.getBlendedColor(colors));
    }
}
