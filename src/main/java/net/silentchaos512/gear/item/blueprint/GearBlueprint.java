package net.silentchaos512.gear.item.blueprint;

import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.item.ICoreTool;
import net.silentchaos512.gear.block.craftingstation.GuiCraftingStation;

import javax.annotation.Nullable;
import java.util.List;

public class GearBlueprint extends AbstractBlueprint {
    private final ICoreItem gearItem;

    public GearBlueprint(boolean singleUse, ICoreItem gearItem) {
        super(new Properties().group(SilentGear.ITEM_GROUP), singleUse);
        this.gearItem = gearItem;
    }

    @Override
    protected ITextComponent getCraftedName(ItemStack stack) {
        return new TextComponentTranslation(this.gearItem.asItem().getTranslationKey());
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> list, ITooltipFlag flag) {
        String itemClass = this.gearItem.getGearType().getName();

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
}
