package net.silentchaos512.gear.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.NonNullList;
import net.silentchaos512.gear.api.parts.PartType;
import net.silentchaos512.utils.Lazy;

import javax.annotation.Nullable;

/**
 * Was going to use this where parts cannot be display, but found a solution. I'll just leave this
 * in case it is needed in the future.
 */
public enum PartIcons implements IItemProvider {
    BINDING(PartType.BINDING),
    BOWSTRING(PartType.BOWSTRING),
    GRIP(PartType.GRIP),
    MAIN(PartType.MAIN),
    ROD(PartType.ROD),
    TIP(PartType.TIP);

    private final Lazy<Item> item;
    private final PartType type;

    PartIcons(PartType type) {
        this.type = type;
        this.item = Lazy.of(IconItem::new);
    }

    @Nullable
    public static PartIcons getIconForType(PartType type) {
        for (PartIcons icon : values()) {
            if (icon.type.equals(type)) {
                return icon;
            }
        }
        return null;
    }

    @Override
    public Item asItem() {
        return this.item.get();
    }

    public static class IconItem extends Item {
        public IconItem() {
            super(new Properties());
        }

        @Override
        public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
            // Hide from everywhere
        }
    }
}
