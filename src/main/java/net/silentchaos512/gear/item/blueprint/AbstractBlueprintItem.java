package net.silentchaos512.gear.item.blueprint;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.tags.Tag;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.silentchaos512.gear.config.Config;

public abstract class AbstractBlueprintItem extends Item {
    final boolean singleUse;

    AbstractBlueprintItem(Properties properties, boolean singleUse) {
        super(properties);
        this.singleUse = singleUse;
    }

    public abstract Tag<Item> getItemTag();

    protected abstract ITextComponent getCraftedName(ItemStack stack);

    public boolean isSingleUse() {
        return this.singleUse;
    }

    @Override
    public ItemStack getContainerItem(ItemStack itemStack) {
        return itemStack.copy();
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
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if (!this.isDisabled()) {
            super.fillItemGroup(group, items);
        }
    }

    @Override
    public ITextComponent getDisplayName(ItemStack stack) {
        String key = "item.silentgear." + (this.singleUse ? "template" : "blueprint");
        return new TranslationTextComponent(key, this.getCraftedName(stack));
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return this.singleUse ? Rarity.COMMON : Rarity.UNCOMMON;
    }
}
