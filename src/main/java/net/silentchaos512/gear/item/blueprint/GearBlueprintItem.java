package net.silentchaos512.gear.item.blueprint;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;

import javax.annotation.Nullable;
import java.util.List;

public class GearBlueprintItem extends AbstractBlueprintItem {
    private final GearType gearType;
    private ResourceLocation itemTag;

    public GearBlueprintItem(GearType gearType, boolean singleUse, Properties properties) {
        super(properties, singleUse);
        this.gearType = gearType;
    }

    @Override
    public ResourceLocation getItemTag() {
        if (itemTag == null) {
            ResourceLocation id = this.getRegistryName();
            if (id != null) {
                itemTag = new ResourceLocation(id.getNamespace(), "blueprints/" + gearType.getName());
            }
        }
        return itemTag != null ? itemTag : SilentGear.getId("invalid");
    }

    @Override
    protected ITextComponent getCraftedName(ItemStack stack) {
        ResourceLocation id = this.getRegistryName();
        if (id == null) {
            return new StringTextComponent("ERROR");
        }
        return new TranslationTextComponent(Util.makeTranslationKey("item", new ResourceLocation(id.getNamespace(), this.gearType.getName())));
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> list, ITooltipFlag flag) {
        String itemClass = this.gearType.getName();

        // Flavor text
        if (!gearType.matches("armor")) {
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
    }
}
