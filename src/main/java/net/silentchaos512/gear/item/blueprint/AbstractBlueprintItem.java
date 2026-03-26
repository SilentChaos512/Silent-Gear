package net.silentchaos512.gear.item.blueprint;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.TooltipDisplay;
import net.silentchaos512.gear.Config;
import org.jspecify.annotations.Nullable;

import java.util.Locale;
import java.util.function.Consumer;

public abstract class AbstractBlueprintItem extends Item implements IBlueprint {
    final BlueprintType type;

    AbstractBlueprintItem(Properties properties, BlueprintType type) {
        super(properties);
        this.type = type;
        if (this.type == BlueprintType.BLUEPRINT) {
            properties.component(DataComponents.RARITY, Rarity.UNCOMMON);
        }
    }

    public abstract TagKey<Item> getItemTag();

    protected abstract Component getCraftedName(ItemStack stack);

    public boolean isSingleUse() {
        return this.type == BlueprintType.TEMPLATE;
    }

    @Override
    public @Nullable ItemStackTemplate getCraftingRemainder(ItemInstance instance) {
        if (this.type == BlueprintType.BLUEPRINT) {
            return new ItemStackTemplate(instance.typeHolder().value(), 1);
        }
        return null;
    }

    boolean isDisabled() {
        BlueprintType.ConfigOption config = Config.Common.blueprintTypes.get();
        return this.type == BlueprintType.TEMPLATE && !config.allowTemplate()
                || this.type == BlueprintType.BLUEPRINT && !config.allowBlueprint();
    }

    @Override
    public Component getName(ItemStack stack) {
        String key = "item.silentgear." + this.type.name().toLowerCase(Locale.ROOT);
        return Component.translatable(key, this.getCraftedName(stack));
    }

    public boolean hasStandardModel() {
        return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
    }
}
