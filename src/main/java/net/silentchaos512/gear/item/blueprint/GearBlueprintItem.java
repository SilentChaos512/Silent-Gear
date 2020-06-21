package net.silentchaos512.gear.item.blueprint;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.item.ICoreTool;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

public class GearBlueprintItem extends AbstractBlueprintItem {
    private final Supplier<ICoreItem> gearItem;
    private ResourceLocation itemTag;

    @Deprecated
    public GearBlueprintItem(boolean singleUse, ICoreItem gearItem) {
        this(singleUse, () -> gearItem);
    }

    public GearBlueprintItem(boolean singleUse, Supplier<ICoreItem> gearItem) {
        this(singleUse, gearItem, new Properties().group(SilentGear.ITEM_GROUP));
    }

    public GearBlueprintItem(boolean singleUse, Supplier<ICoreItem> gearItem, Properties properties) {
        super(properties, singleUse);
        this.gearItem = gearItem;
    }

    @Override
    public ResourceLocation getItemTag() {
        if (itemTag == null) {
            ResourceLocation id = this.gearItem.get().asItem().getRegistryName();
            if (id != null) {
                itemTag = new ResourceLocation(id.getNamespace(), "blueprints/" + id.getPath());
            }
        }
        return itemTag != null ? itemTag : SilentGear.getId("invalid");
    }

    @Override
    protected ITextComponent getCraftedName(ItemStack stack) {
        return new TranslationTextComponent(this.gearItem.get().asItem().getTranslationKey());
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> list, ITooltipFlag flag) {
        String itemClass = this.gearItem.get().getGearType().getName();

        // Flavor text
        if (this.gearItem instanceof ICoreTool) {
            list.add(new TranslationTextComponent("item.silentgear.blueprint." + itemClass + ".desc").applyTextStyle(TextFormatting.ITALIC));
        }

        // Single use or multiple uses? Or disabled?
        if (isDisabled()) {
            list.add(new TranslationTextComponent("item.silentgear.blueprint.disabled").applyTextStyle(TextFormatting.DARK_RED));
        } else if (this.singleUse) {
            list.add(new TranslationTextComponent("item.silentgear.blueprint.singleUse").applyTextStyle(TextFormatting.RED));
        } else {
            list.add(new TranslationTextComponent("item.silentgear.blueprint.multiUse").applyTextStyle(TextFormatting.GREEN));
        }

        // Is mixed material allowed in this GUI?
/*        if (Minecraft.getInstance().currentScreen instanceof CraftingStationScreen) {
            list.add(new TranslationTextComponent("item.silentgear.blueprint.canMix")
                    .applyTextStyle(TextFormatting.GREEN));
        } else {
            list.add(new TranslationTextComponent("item.silentgear.blueprint.noMixing")
                    .applyTextStyle(TextFormatting.RED));
        }*/
    }
}
