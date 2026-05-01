package net.silentchaos512.gear.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.TooltipDisplay;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.client.KeyTracker;
import net.silentchaos512.gear.setup.SgDataComponents;
import net.silentchaos512.gear.setup.SgRegistries;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.gear.util.TextUtil;
import net.silentchaos512.lib.util.Color;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class ModKitItem extends Item implements ICycleItem {
    public ModKitItem(Properties properties) {
        super(properties);
    }

    public static PartType getSelectedType(ItemStack stack) {
        return stack.getOrDefault(SgDataComponents.PART_TYPE, PartTypes.MAIN.get());
    }

    private static void setSelectedType(ItemStack stack, PartType type) {
        stack.set(SgDataComponents.PART_TYPE, type);
    }

    @Override
    public void onCycleKeyPress(ItemStack stack, ICycleItem.Direction direction) {
        PartType selected = getSelectedType(stack);
        List<PartType> types = SgRegistries.PART_TYPE.stream().toList();
        if (types.isEmpty()) return;

        int index = types.indexOf(selected) + direction.scale;
        // Wrap around
        if (index < 0) index = types.size() - 1;
        if (index >= types.size()) index = 0;

        setSelectedType(stack, types.get(index));
    }

    @SuppressWarnings("deprecation")
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        PartType selected = getSelectedType(stack);
        var selectedName = selected.getDisplayName().withStyle(ChatFormatting.YELLOW);
        tooltipAdder.accept(TextUtil.withColor(TextUtil.translate("item", "mod_kit.selected", selectedName), Color.SKYBLUE));

        tooltipAdder.accept(TextUtil.translate("item", "mod_kit.keyHint",
                TextUtil.withColor(TextUtil.keyBinding(KeyTracker.CYCLE_BACK), Color.AQUAMARINE),
                TextUtil.withColor(TextUtil.keyBinding(KeyTracker.CYCLE_NEXT), Color.AQUAMARINE)));

        // Indicate if painting/removing parts is possible
        var paintText = TextUtil.translate("item", "mod_kit.paint").withStyle(ChatFormatting.GREEN);
        var removeText = TextUtil.translate("item", "mod_kit.remove").withStyle(ChatFormatting.RED);
        if (selected.canPaint() && selected.isRemovable()) {
            var text = TextUtil.translate("item", "mod_kit.can_paint_and_remove", paintText, removeText);
            tooltipAdder.accept(text);
        } else if (selected.canPaint()) {
            tooltipAdder.accept(TextUtil.translate("item", "mod_kit.can_paint_or_remove", paintText));
        } else if (selected.isRemovable()) {
            tooltipAdder.accept(TextUtil.translate("item", "mod_kit.can_paint_or_remove", removeText));
        } else {
            tooltipAdder.accept(TextUtil.translate("item", "mod_kit.no_actions").withStyle(ChatFormatting.RED));
        }
    }

    @Override
    public @Nullable ItemStackTemplate getCraftingRemainder(ItemInstance instance) {
        return new ItemStackTemplate(instance.typeHolder().value());
    }
}
