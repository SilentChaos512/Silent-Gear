package net.silentchaos512.gear.item.blueprint;

import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.tags.Tag;
import net.minecraft.tags.ItemTags;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
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

import net.minecraft.world.item.Item.Properties;

public class GearBlueprintItem extends AbstractBlueprintItem {
    private final GearType gearType;
    private Tag.Named<Item> itemTag;

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
    public Tag.Named<Item> getItemTag() {
        if (itemTag == null) {
            ResourceLocation id = this.getRegistryName();
            if (id != null) {
                itemTag = ItemTags.bind(new ResourceLocation(id.getNamespace(), "blueprints/" + gearType.getName()).toString());
            }
        }
        return itemTag;
    }

    @Override
    protected Component getCraftedName(ItemStack stack) {
        ResourceLocation id = this.getRegistryName();
        if (id == null) {
            return new TextComponent("ERROR");
        }
        return new TranslatableComponent(Util.makeDescriptionId("item", new ResourceLocation(id.getNamespace(), this.gearType.getName())));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> list, TooltipFlag flag) {
        String itemClass = this.gearType.getName();

        // Flavor text
        if (!gearType.isArmor()) {
            String key = "item." + NameUtils.fromItem(stack).getNamespace() + ".blueprint." + itemClass + ".desc";
            list.add(new TranslatableComponent(key).withStyle(ChatFormatting.ITALIC));
        }

        // Single use or multiple uses? Or disabled?
        if (isDisabled()) {
            list.add(new TranslatableComponent("item.silentgear.blueprint.disabled").withStyle(ChatFormatting.DARK_RED));
        } else if (this.singleUse) {
            list.add(new TranslatableComponent("item.silentgear.blueprint.singleUse").withStyle(ChatFormatting.RED));
        } else {
            list.add(new TranslatableComponent("item.silentgear.blueprint.multiUse").withStyle(ChatFormatting.GREEN));
        }

        addInformationSupportedPartTypes(list);
    }

    private void addInformationSupportedPartTypes(Collection<Component> list) {
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

                List<Component> lines = builder.build();
                if (!lines.isEmpty()) {
                    list.add(TextUtil.withColor(TextUtil.misc("supportedPartTypes"), Color.GOLD));
                    list.addAll(lines);
                }
            }
        } else {
            list.add(TextUtil.withColor(TextUtil.misc("supportedPartTypes"), Color.GOLD)
                    .append(" ")
                    .append(TextUtil.withColor(TextUtil.keyBinding(KeyTracker.DISPLAY_STATS), ChatFormatting.GRAY)));
        }
    }
}
