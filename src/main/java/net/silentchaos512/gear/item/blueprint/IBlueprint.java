package net.silentchaos512.gear.item.blueprint;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.silentchaos512.gear.api.item.ICoreItem;

import java.util.Collection;

public interface IBlueprint {
    ItemStack getCraftingResult(ItemStack blueprint, Collection<ItemStack> parts);

    Output getOutputInfo(ItemStack blueprint);

    class Output {
        public final String key;
        public final Item item;
        public final ICoreItem gear;
        public final int cost;

        public Output(String key, Item item, ICoreItem gear) {
            this.key = key;
            this.item = item;
            this.gear = gear;
            this.cost = gear.getConfig().getHeadCount();
        }
    }
}
