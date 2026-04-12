package net.silentchaos512.gear.util;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.component.WrittenBookContent;
import net.minecraft.world.item.crafting.Ingredient;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.stream.Stream;

// TODO: Maybe move this to Silent Lib?
public class ItemHelper {
    @SuppressWarnings("deprecation")
    public static @Nullable ItemStackTemplate getAnyMatchingItem(Ingredient ingredient) {
        var optionalItem = ingredient.items().findAny();
        return optionalItem.map(ItemStackTemplate::new).orElse(null);
    }

    public static @Nullable Component getCustomName(ItemInstance instance) {
        Component customName = instance.get(DataComponents.CUSTOM_NAME);
        if (customName != null) {
            return customName;
        } else {
            WrittenBookContent content = instance.get(DataComponents.WRITTEN_BOOK_CONTENT);
            if (content != null) {
                String title = content.title().raw();
                if (!StringUtil.isBlank(title)) {
                    return Component.literal(title);
                }
            }

            return null;
        }
    }

    /**
     * A temporary workaround for compound materials to get their display names
     * @param instance
     * @return
     */
    public static Component getHoverName(@Nullable ItemInstance instance) {
        if (instance == null) return Component.empty();
        Component customName = getCustomName(instance);
        return customName != null ? customName : instance.getOrDefault(DataComponents.ITEM_NAME, CommonComponents.EMPTY);
    }

    public static int hashItemAndComponents(@Nullable ItemStackTemplate template) {
        if (template != null) {
            int result = 31 + template.item().hashCode();
            return 31 * result + template.components().hashCode();
        } else {
            return 0;
        }
    }

    public static boolean equals(@Nullable ItemInstance a, @Nullable ItemInstance b) {
        if (a == b) return true;
        if (a == null) return false;
        return a.equals(b);
    }

    public static ItemStack toStack(@Nullable ItemInstance instance) {
        if (instance == null) {
            return ItemStack.EMPTY;
        }
        if (instance instanceof ItemStackTemplate template) {
            return template.create();
        }
        return (ItemStack) instance;
    }

    public static @Nullable ItemStackTemplate toTemplate(@Nullable ItemInstance instance) {
        if (instance == null) {
            return null;
        }
        if (instance instanceof ItemStack stack) {
            if (stack.isEmpty()) {
                return null;
            }
            return ItemStackTemplate.fromNonEmptyStack(stack);
        }
        return (ItemStackTemplate) instance;
    }
}
