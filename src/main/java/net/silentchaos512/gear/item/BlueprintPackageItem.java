package net.silentchaos512.gear.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.storage.loot.LootTable;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.lib.item.LootContainerItem;

import java.util.function.Consumer;

public class BlueprintPackageItem extends LootContainerItem {
    public static final ResourceKey<LootTable> DEFAULT_LOOT_TABLE = ResourceKey.create(Registries.LOOT_TABLE, SilentGear.getId("starter_blueprints"));

    public BlueprintPackageItem(Item.Properties properties) {
        super(true, properties);
    }

    public ItemStack getDefaultStack() {
        return getStack(DEFAULT_LOOT_TABLE);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        tooltipAdder.accept(Component.translatable("item.silentgear.blueprint_package.desc1").withStyle(ChatFormatting.ITALIC));
        tooltipAdder.accept(Component.translatable("item.silentgear.blueprint_package.desc2").withStyle(ChatFormatting.ITALIC));
        super.appendHoverText(stack, context, tooltipDisplay, tooltipAdder, flag);
    }
}
