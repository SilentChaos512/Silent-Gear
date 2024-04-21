package net.silentchaos512.gear.item.blueprint;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.fml.ModList;
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

    public abstract TagKey<Item> getItemTag();

    protected abstract Component getCraftedName(ItemStack stack);

    public boolean isSingleUse() {
        return this.singleUse;
    }

    @Override
    public ItemStack getCraftingRemainingItem(ItemStack itemStack) {
        ItemStack copy = itemStack.copy();
        copy.setCount(1);
        return copy;
    }

    @Override
    public boolean hasCraftingRemainingItem(ItemStack stack) {
        return !this.singleUse;
    }

    boolean isDisabled() {
        BlueprintType config = Config.Common.blueprintTypes.get();
        return this.singleUse && !config.allowTemplate()
                || !this.singleUse && !config.allowBlueprint();
    }

    @Override
    public Component getName(ItemStack stack) {
        String key = "item.silentgear." + (this.singleUse ? "template" : "blueprint");
        return Component.translatable(key, this.getCraftedName(stack));
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
