package net.silentchaos512.gear.item;

import net.minecraft.world.item.ItemStack;

public interface ICycleItem {
    enum Direction {
        BACK(-1),
        NEXT(1),
        NEITHER(0);

        public final int scale;

        Direction(int scale) {
            this.scale = scale;
        }
    }

    void onCycleKeyPress(ItemStack stack, Direction direction);
}
