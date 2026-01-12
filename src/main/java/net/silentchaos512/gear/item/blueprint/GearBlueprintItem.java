package net.silentchaos512.gear.item.blueprint;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Util;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.silentchaos512.gear.api.item.GearItem;
import net.silentchaos512.gear.api.item.GearType;
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

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class GearBlueprintItem extends AbstractBlueprintItem {
    private final Supplier<GearType> gearType;
    @Nullable private TagKey<Item> itemTag;

    public GearBlueprintItem(Supplier<GearType> gearType, BlueprintType type, Properties properties) {
        super(properties, type);
        this.gearType = gearType;
    }

    @Override
    public PartType getPartType(ItemStack stack) {
        return PartTypes.MAIN.get();
    }

    public GearType gearType() {
        return gearType.get();
    }

    @Override
    public GearType getGearType(ItemStack stack) {
        return gearType.get();
    }

    @Override
    public TagKey<Item> getItemTag() {
        if (itemTag == null) {
            Identifier id = NameUtils.fromItem(this);
            String itemClass = Objects.requireNonNull(SgRegistries.GEAR_TYPE.getKey(gearType())).getPath();
            itemTag = ItemTags.create(Identifier.fromNamespaceAndPath(id.getNamespace(), "blueprints/" + itemClass));
        }
        return itemTag;
    }

    @Override
    protected Component getCraftedName(ItemStack stack) {
        Identifier id = NameUtils.fromItem(this);
        String itemClass = Objects.requireNonNull(SgRegistries.GEAR_TYPE.getKey(gearType())).getPath();
        return Component.translatable(Util.makeDescriptionId("item", Identifier.fromNamespaceAndPath(id.getNamespace(), itemClass)));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        String itemClass = Objects.requireNonNull(SgRegistries.GEAR_TYPE.getKey(gearType())).getPath();

        // Flavor text
        if (!gearType().isArmor()) {
            String key = "item." + NameUtils.fromItem(stack).getNamespace() + ".blueprint." + itemClass + ".desc";
            tooltipAdder.accept(Component.translatable(key).withStyle(ChatFormatting.ITALIC));
        }

        // Armor durability text
        if (!MathUtils.floatsEqual(gearType().armorDurabilityMultiplier(), 1f)) {
            tooltipAdder.accept(TextUtil.translate("item", "blueprint.armorDurability", gearType().armorDurabilityMultiplier())
                    .withStyle(ChatFormatting.ITALIC));
        }

        super.appendHoverText(stack, context, tooltipDisplay, tooltipAdder, flag);

        // Single use or multiple uses? Or disabled?
        if (isDisabled()) {
            tooltipAdder.accept(Component.translatable("item.silentgear.blueprint.disabled").withStyle(ChatFormatting.DARK_RED));
        } else if (this.isSingleUse()) {
            tooltipAdder.accept(Component.translatable("item.silentgear.blueprint.singleUse").withStyle(ChatFormatting.RED));
        } else {
            tooltipAdder.accept(Component.translatable("item.silentgear.blueprint.multiUse").withStyle(ChatFormatting.GREEN));
        }

        appendSupportedTypesText(tooltipAdder);
    }

    private void appendSupportedTypesText(Consumer<Component> tooltipAdder) {
        if (KeyTracker.isDisplayPropertiesDown()) {
            GearItem item = GearType.getItem(gearType());

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
                    tooltipAdder.accept(TextUtil.withColor(TextUtil.misc("supportedPartTypes"), Color.GOLD));
                    lines.forEach(tooltipAdder);
                }
            }
        } else {
            tooltipAdder.accept(TextUtil.withColor(TextUtil.misc("supportedPartTypes"), Color.GOLD)
                    .append(" ")
                    .append(TextUtil.withColor(TextUtil.keyBinding(KeyTracker.DISPLAY_PROPERTIES), ChatFormatting.GRAY)));
        }
    }
}
