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
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.part.IGearPart;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.client.KeyTracker;
import net.silentchaos512.gear.client.util.TextListBuilder;
import net.silentchaos512.gear.gear.part.PartData;
import net.silentchaos512.gear.gear.part.PartManager;
import net.silentchaos512.gear.util.TextUtil;
import net.silentchaos512.lib.util.NameUtils;
import net.silentchaos512.utils.Color;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class GearBlueprintItem extends AbstractBlueprintItem {
    private final GearType gearType;
    private ITag.INamedTag<Item> itemTag;

    public GearBlueprintItem(GearType gearType, boolean singleUse, Properties properties) {
        super(properties, singleUse);
        this.gearType = gearType;
    }

    @Override
    public PartType getPartType(ItemStack stack) {
        return PartType.MAIN;
    }

    public GearType getGearType() {
        return gearType;
    }

    @Override
    public GearType getGearType(ItemStack stack) {
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
        if (!gearType.isArmor()) {
            String key = "item." + NameUtils.fromItem(stack).getNamespace() + ".blueprint." + itemClass + ".desc";
            list.add(new TranslationTextComponent(key).mergeStyle(TextFormatting.ITALIC));
        }

        // Single use or multiple uses? Or disabled?
        if (isDisabled()) {
            list.add(new TranslationTextComponent("item.silentgear.blueprint.disabled").mergeStyle(TextFormatting.DARK_RED));
        } else if (this.singleUse) {
            list.add(new TranslationTextComponent("item.silentgear.blueprint.singleUse").mergeStyle(TextFormatting.RED));
        } else {
            list.add(new TranslationTextComponent("item.silentgear.blueprint.multiUse").mergeStyle(TextFormatting.GREEN));
        }

        addInformationSupportedPartTypes(list);
    }

    private void addInformationSupportedPartTypes(Collection<ITextComponent> list) {
        if (KeyTracker.isDisplayStatsDown()) {
            Optional<ICoreItem> itemOptional = this.gearType.getItem();

            if (itemOptional.isPresent()) {
                TextListBuilder builder = new TextListBuilder();
                ICoreItem item = itemOptional.get();
                ItemStack gear = new ItemStack(item);

                for (PartType type : PartType.getValues()) {
                    if (type != PartType.MAIN) {
                        List<IGearPart> partsOfType = PartManager.getPartsOfType(type);

                        if (!partsOfType.isEmpty() && item.supportsPart(gear, PartData.of(partsOfType.get(0)))) {
                            builder.add(type.getDisplayName(0));
                        }
                    }
                }

                List<ITextComponent> lines = builder.build();
                if (!lines.isEmpty()) {
                    list.add(TextUtil.withColor(TextUtil.misc("supportedPartTypes"), Color.GOLD));
                    list.addAll(lines);
                }
            }
        } else {
            list.add(TextUtil.withColor(TextUtil.misc("supportedPartTypes"), Color.GOLD)
                    .appendString(" ")
                    .append(TextUtil.withColor(TextUtil.keyBinding(KeyTracker.DISPLAY_STATS), TextFormatting.GRAY)));
        }
    }
}
