package net.silentchaos512.gear.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.client.KeyTracker;
import net.silentchaos512.gear.network.KeyPressOnItemPacket;
import net.silentchaos512.gear.util.TextUtil;
import net.silentchaos512.utils.Color;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

public class ModKitItem extends Item implements ICycleItem {
    private static final String NBT_SELECTED = "SelectedType";

    public ModKitItem(Properties properties) {
        super(properties);
    }

    public static PartType getSelectedType(ItemStack stack) {
        String key = stack.getOrCreateTag().getString(NBT_SELECTED);
        return PartType.getNonNull(new ResourceLocation(key));
    }

    public static void setSelectedType(ItemStack stack, PartType type) {
        stack.getOrCreateTag().putString(NBT_SELECTED, type.getName().toString());
    }

    @Override
    public void onCycleKeyPress(ItemStack stack, KeyPressOnItemPacket.Type direction) {
        PartType selected = getSelectedType(stack);
        List<PartType> types = getRemovableTypes();
        if (types.isEmpty()) return;

        if (selected == PartType.NONE) {
            if (direction == KeyPressOnItemPacket.Type.CYCLE_BACK) {
                setSelectedType(stack, types.get(types.size() - 1));
            } else if (direction == KeyPressOnItemPacket.Type.CYCLE_NEXT) {
                setSelectedType(stack, types.get(0));
            }
        } else {
            int index = types.indexOf(selected) + direction.direction;
            if (index < 0) index = types.size() - 1;
            if (index >= types.size()) index = 0;

            setSelectedType(stack, types.get(index));
        }
    }

    private static List<PartType> getRemovableTypes() {
        return PartType.getValues().stream()
                .filter(PartType::isRemovable)
                .collect(Collectors.toList());
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        PartType selected = getSelectedType(stack);
        tooltip.add(TextUtil.withColor(TextUtil.translate("item", "mod_kit.selected"), Color.SKYBLUE)
                .append(selected.getDisplayName(0).mergeStyle(TextFormatting.GRAY)));

        tooltip.add(TextUtil.translate("item", "mod_kit.keyHint",
                TextUtil.withColor(TextUtil.keyBinding(KeyTracker.CYCLE_BACK), Color.AQUAMARINE),
                TextUtil.withColor(TextUtil.keyBinding(KeyTracker.CYCLE_NEXT), Color.AQUAMARINE)));

        if (flagIn.isAdvanced()) {
            StringTextComponent text = new StringTextComponent("Removable types: " + getRemovableTypes().size());
            tooltip.add(TextUtil.withColor(text, TextFormatting.DARK_GRAY));
        }
    }

    @Override
    public ItemStack getContainerItem(ItemStack itemStack) {
        return itemStack.copy();
    }

    @Override
    public boolean hasContainerItem(ItemStack stack) {
        return true;
    }
}
