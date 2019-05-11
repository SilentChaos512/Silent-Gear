package net.silentchaos512.gear.item.blueprint;

import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.silentchaos512.gear.config.Config;

public abstract class AbstractBlueprint extends Item implements IBlueprint {
    final boolean singleUse;

    AbstractBlueprint(Properties properties, boolean singleUse) {
        super(properties);
        this.singleUse = singleUse;
    }

    protected abstract ITextComponent getCraftedName(ItemStack stack);

    @Override
    public ItemStack getContainerItem(ItemStack itemStack) {
        return itemStack.copy();
    }

    @Override
    public boolean hasContainerItem(ItemStack stack) {
        return !isSingleUse(stack);
    }

    @Override
    public boolean isSingleUse(ItemStack blueprint) {
        return this.singleUse;
    }

    protected boolean isDisabled() {
        BlueprintType config = Config.GENERAL.blueprintTypes.get();
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
        return new TextComponentTranslation(key, this.getCraftedName(stack));
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return this.isSingleUse(stack) ? EnumRarity.COMMON : EnumRarity.UNCOMMON;
    }
}
