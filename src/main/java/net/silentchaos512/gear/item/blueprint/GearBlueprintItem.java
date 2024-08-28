package net.silentchaos512.gear.item.blueprint;

import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.part.GearPart;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.client.KeyTracker;
import net.silentchaos512.gear.client.util.TextListBuilder;
import net.silentchaos512.gear.gear.part.PartInstance;
import net.silentchaos512.gear.setup.SgRegistries;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.gear.util.TextUtil;
import net.silentchaos512.lib.util.Color;
import net.silentchaos512.lib.util.MathUtils;
import net.silentchaos512.lib.util.NameUtils;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class GearBlueprintItem extends AbstractBlueprintItem {
    private final GearType gearType;
    private TagKey<Item> itemTag;

    public GearBlueprintItem(GearType gearType, BlueprintType type, Properties properties) {
        super(properties, type);
        this.gearType = gearType;
    }

    @Override
    public PartType getPartType(ItemStack stack) {
        return PartTypes.MAIN.get();
    }

    public GearType getGearType() {
        return gearType;
    }

    @Override
    public GearType getGearType(ItemStack stack) {
        return gearType;
    }

    @Override
    public TagKey<Item> getItemTag() {
        if (itemTag == null) {
            ResourceLocation id = NameUtils.fromItem(this);
            String itemClass = Objects.requireNonNull(SgRegistries.GEAR_TYPE.getKey(this.gearType)).getPath();
            itemTag = ItemTags.create(ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "blueprints/" + itemClass));
        }
        return itemTag;
    }

    @Override
    protected Component getCraftedName(ItemStack stack) {
        ResourceLocation id = NameUtils.fromItem(this);
        String itemClass = Objects.requireNonNull(SgRegistries.GEAR_TYPE.getKey(this.gearType)).getPath();
        return Component.translatable(Util.makeDescriptionId("item", ResourceLocation.fromNamespaceAndPath(id.getNamespace(), itemClass)));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext tooltipContext, List<Component> tooltip, TooltipFlag flags) {
        String itemClass = Objects.requireNonNull(SgRegistries.GEAR_TYPE.getKey(this.gearType)).getPath();

        // Flavor text
        if (!gearType.isArmor()) {
            String key = "item." + NameUtils.fromItem(stack).getNamespace() + ".blueprint." + itemClass + ".desc";
            tooltip.add(Component.translatable(key).withStyle(ChatFormatting.ITALIC));
        }

        // Armor durability text
        if (!MathUtils.floatsEqual(gearType.armorDurabilityMultiplier(), 1f)) {
            tooltip.add(TextUtil.translate("item", "blueprint.armorDurability", gearType.armorDurabilityMultiplier())
                    .withStyle(ChatFormatting.ITALIC));
        }

        super.appendHoverText(stack, tooltipContext, tooltip, flags);

        // Single use or multiple uses? Or disabled?
        if (isDisabled()) {
            tooltip.add(Component.translatable("item.silentgear.blueprint.disabled").withStyle(ChatFormatting.DARK_RED));
        } else if (this.isSingleUse()) {
            tooltip.add(Component.translatable("item.silentgear.blueprint.singleUse").withStyle(ChatFormatting.RED));
        } else {
            tooltip.add(Component.translatable("item.silentgear.blueprint.multiUse").withStyle(ChatFormatting.GREEN));
        }

        appendSupportedTypesText(tooltip);
    }

    private void appendSupportedTypesText(Collection<Component> list) {
        if (KeyTracker.isDisplayStatsDown()) {
            ICoreItem item = GearType.getItem(this.gearType);

            if (item != null) {
                TextListBuilder builder = new TextListBuilder();
                ItemStack gear = new ItemStack(item);

                for (PartType type : SgRegistries.PART_TYPE) {
                    if (type != PartTypes.MAIN.get()) {
                        List<GearPart> partsOfType = SgRegistries.PART.getPartsOfType(type);

                        if (!partsOfType.isEmpty() && item.supportsPart(gear, PartInstance.of(partsOfType.getFirst()))) {
                            builder.add(type.getDisplayName());
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
