package net.silentchaos512.gear.item.blueprint;

import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.ModList;
import net.silentchaos512.gear.config.Config;
import net.silentchaos512.gear.util.TextUtil;

import javax.annotation.Nullable;
import java.util.List;

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

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flags) {
        if (!isDisabled()) {
            if (Config.Client.showJeiHints.get()) {
                // JEI hints
                if (ModList.get().isLoaded("jei")) {
                    // JEI is installed! Provide a tip for noobs.
                    tooltip.add(TextUtil.translate("item", "blueprint.jeiTip").withStyle(ChatFormatting.AQUA));
                } else {
                    // JEI is not installed? Educate the ultra noobs.
                    tooltip.add(TextUtil.misc("jeiNotInstalled").withStyle(ChatFormatting.DARK_RED));
                }
            }

            // Legacy material mixing allowed?
            if (Config.Common.allowLegacyMaterialMixing.get()) {
                tooltip.add(TextUtil.translate("item", "blueprint.mixing.enabled").withStyle(ChatFormatting.GREEN));
            } else {
                tooltip.add(TextUtil.translate("item", "blueprint.mixing.disabled").withStyle(ChatFormatting.RED));
            }
        }
    }
}
