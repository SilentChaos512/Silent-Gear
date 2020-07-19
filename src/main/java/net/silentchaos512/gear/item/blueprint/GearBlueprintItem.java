package net.silentchaos512.gear.item.blueprint;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.lib.util.NameUtils;

import javax.annotation.Nullable;
import java.util.List;

public class GearBlueprintItem extends AbstractBlueprintItem {
    private final GearType gearType;
    private ITag.INamedTag<Item> itemTag;

    public GearBlueprintItem(GearType gearType, boolean singleUse, Properties properties) {
        super(properties, singleUse);
        this.gearType = gearType;
    }

    public GearType getGearType() {
        return gearType;
    }

    @Override
    public ITag.INamedTag<Item> getItemTag() {
        if (itemTag == null) {
            ResourceLocation id = this.getRegistryName();
            if (id != null) {
                itemTag = ItemTags.makeWrapperTag(new ResourceLocation(id.getNamespace(), "blueprints/" + gearType.getName()).toString());
            }
        }
        return itemTag;
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
            String key = "item." + NameUtils.fromItem(stack).getNamespace() + ".blueprint." + itemClass + ".desc";
            list.add(new TranslationTextComponent(key).func_240699_a_(TextFormatting.ITALIC));
        }

        // Single use or multiple uses? Or disabled?
        if (isDisabled()) {
            list.add(new TranslationTextComponent("item.silentgear.blueprint.disabled").func_240699_a_(TextFormatting.DARK_RED));
        } else if (this.singleUse) {
            list.add(new TranslationTextComponent("item.silentgear.blueprint.singleUse").func_240699_a_(TextFormatting.RED));
        } else {
            list.add(new TranslationTextComponent("item.silentgear.blueprint.multiUse").func_240699_a_(TextFormatting.GREEN));
        }
    }
}
