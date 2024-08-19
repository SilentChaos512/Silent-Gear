package net.silentchaos512.gear.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.client.KeyTracker;
import net.silentchaos512.gear.setup.SgDataComponents;
import net.silentchaos512.gear.setup.SgRegistries;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.gear.util.TextUtil;
import net.silentchaos512.lib.util.Color;

import java.util.ArrayList;
import java.util.List;

public class ModKitItem extends Item implements ICycleItem {
    public ModKitItem(Properties properties) {
        super(properties);
    }

    public static PartType getSelectedType(ItemStack stack) {
        var type = stack.get(SgDataComponents.PART_TYPE);
        return type != null ? type : PartTypes.NONE.get();
    }

    private static void setSelectedType(ItemStack stack, PartType type) {
        stack.set(SgDataComponents.PART_TYPE, type);
    }

    @Override
    public void onCycleKeyPress(ItemStack stack, ICycleItem.Direction direction) {
        PartType selected = getSelectedType(stack);
        List<PartType> types = getRemovableTypes();
        if (types.isEmpty()) return;

        if (selected == PartTypes.NONE.get()) {
            if (direction == ICycleItem.Direction.BACK) {
                setSelectedType(stack, types.getLast());
            } else if (direction == ICycleItem.Direction.NEXT) {
                setSelectedType(stack, types.getFirst());
            }
        } else {
            int index = types.indexOf(selected) + direction.scale;
            // Wrap around
            if (index < 0) index = types.size() - 1;
            if (index >= types.size()) index = 0;

            setSelectedType(stack, types.get(index));
        }
    }

    private static List<PartType> getRemovableTypes() {
        List<PartType> list = new ArrayList<>();
        for (PartType partType : SgRegistries.PART_TYPE) {
            if (partType.isRemovable()) {
                list.add(partType);
            }
        }
        return list;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        PartType selected = getSelectedType(stack);
        tooltip.add(TextUtil.withColor(TextUtil.translate("item", "mod_kit.selected"), Color.SKYBLUE)
                .append(selected.getDisplayName().withStyle(ChatFormatting.GRAY)));

        tooltip.add(TextUtil.translate("item", "mod_kit.keyHint",
                TextUtil.withColor(TextUtil.keyBinding(KeyTracker.CYCLE_BACK), Color.AQUAMARINE),
                TextUtil.withColor(TextUtil.keyBinding(KeyTracker.CYCLE_NEXT), Color.AQUAMARINE)));

        if (flagIn.isAdvanced()) {
            MutableComponent text = Component.literal("Removable types: " + getRemovableTypes().size());
            tooltip.add(TextUtil.withColor(text, ChatFormatting.DARK_GRAY));
        }
    }

    @Override
    public ItemStack getCraftingRemainingItem(ItemStack itemStack) {
        return itemStack.copy();
    }

    @Override
    public boolean hasCraftingRemainingItem(ItemStack stack) {
        return true;
    }
}
