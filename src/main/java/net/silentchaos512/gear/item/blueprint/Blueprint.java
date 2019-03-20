package net.silentchaos512.gear.item.blueprint;

import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.item.ICoreTool;
import net.silentchaos512.gear.block.craftingstation.GuiCraftingStation;
import net.silentchaos512.gear.config.Config;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class Blueprint extends Item implements IBlueprint {
    private static final int COLOR_OUTLINE_BLUEPRINT = 0xF1F1B9;
    private static final int COLOR_OUTLINE_TEMPLATE = 0xBAF2E2;

    private final boolean singleUse;
    @Nonnull
    private final ICoreItem gearItem;

    public Blueprint(boolean singleUse, @Nonnull ICoreItem gearItem) {
        super(new Properties().group(SilentGear.ITEM_GROUP));
        this.singleUse = singleUse;
        this.gearItem = gearItem;
    }

    @Override
    public ItemStack getContainerItem(ItemStack itemStack) {
        return isSingleUse(itemStack) ? ItemStack.EMPTY : new ItemStack(this);
    }

    @Override
    public boolean hasContainerItem(ItemStack stack) {
        return !isSingleUse(stack);
    }

    @Override
    public boolean isSingleUse(ItemStack blueprint) {
        return singleUse;
    }

    public boolean isDisabled() {
        BlueprintType config = Config.GENERAL.blueprintTypes.get();
        return singleUse && !config.allowTemplate()
                || !singleUse && !config.allowBlueprint();
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> list, ITooltipFlag flag) {
        String itemClass = this.gearItem.getGearType().getName();

        // Output item class
        list.add(gearItem.asItem().getName().applyTextStyle(TextFormatting.AQUA));
        // Flavor text
        if (this.gearItem instanceof ICoreTool) {
            list.add(new TextComponentTranslation("item.silentgear.blueprint." + itemClass + ".desc")
                    .applyTextStyle(TextFormatting.ITALIC));
        }

        // Single use or multiple uses? Or disabled?
        if (isDisabled()) {
            list.add(new TextComponentTranslation("item.silentgear.blueprint.disabled")
                    .applyTextStyle(TextFormatting.DARK_RED));
        } else if (this.singleUse) {
            list.add(new TextComponentTranslation("item.silentgear.blueprint.singleUse")
                    .applyTextStyle(TextFormatting.RED));
        } else {
            list.add(new TextComponentTranslation("item.silentgear.blueprint.multiUse")
                    .applyTextStyle(TextFormatting.GREEN));
        }

        // Is mixed material allowed in this GUI?
        if (Minecraft.getInstance().currentScreen instanceof GuiCraftingStation) {
            list.add(new TextComponentTranslation("item.silentgear.blueprint.canMix")
                    .applyTextStyle(TextFormatting.GREEN));
        } else {
            list.add(new TextComponentTranslation("item.silentgear.blueprint.noMixing")
                    .applyTextStyle(TextFormatting.RED));
        }
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if (!isDisabled()) super.fillItemGroup(group, items);
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        return super.getTranslationKey(stack).replaceFirst("_[a-z]+$", "");
    }
}
