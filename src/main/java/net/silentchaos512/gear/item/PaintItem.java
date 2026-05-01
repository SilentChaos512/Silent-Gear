package net.silentchaos512.gear.item;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.setup.SgDataComponents;

public class PaintItem extends Item implements ItemWithSubItems {
    public PaintItem(Properties properties) {
        super(properties);
    }

    @Override
    public void addSubItems(CreativeModeTab.Output output) {
        for (DyeColor dyeColor : DyeColor.values()) {
            var stack = new ItemStack(this);
            stack.set(SgDataComponents.PAINT_COLOR, dyeColor.getTextureDiffuseColor());
            output.accept(stack);
        }
    }
}
