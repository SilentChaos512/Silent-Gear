package net.silentchaos512.gear.item.blueprint;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.tags.Tag;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.silentchaos512.gear.config.Config;

import net.minecraft.world.item.Item.Properties;

public abstract class AbstractBlueprintItem extends Item implements IBlueprint {
    final boolean singleUse;

    AbstractBlueprintItem(Properties properties, boolean singleUse) {
        super(properties);
        this.singleUse = singleUse;
    }

    public abstract Tag.Named<Item> getItemTag();

    protected abstract Component getCraftedName(ItemStack stack);

    public boolean isSingleUse() {
        return this.singleUse;
    }

    @Override
    public ItemStack getContainerItem(ItemStack itemStack) {
        ItemStack copy = itemStack.copy();
        copy.setCount(1);
        return copy;
    }

    @Override
    public boolean hasContainerItem(ItemStack stack) {
        return !this.singleUse;
    }

    boolean isDisabled() {
        BlueprintType config = Config.Common.blueprintTypes.get();
        return this.singleUse && !config.allowTemplate()
                || !this.singleUse && !config.allowBlueprint();
    }

    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
        if (!this.isDisabled()) {
            super.fillItemCategory(group, items);
        }
    }

    @Override
    public Component getName(ItemStack stack) {
        String key = "item.silentgear." + (this.singleUse ? "template" : "blueprint");
        return new TranslatableComponent(key, this.getCraftedName(stack));
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return this.singleUse ? Rarity.COMMON : Rarity.UNCOMMON;
    }

    public boolean hasStandardModel() {
        return true;
    }
}
