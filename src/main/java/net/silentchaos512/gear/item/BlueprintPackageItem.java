package net.silentchaos512.gear.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.storage.loot.LootTable;
import net.silentchaos512.lib.item.LootContainerItem;

import java.util.List;

public class BlueprintPackageItem extends LootContainerItem {
    public BlueprintPackageItem(ResourceLocation defaultLootTable) {
        super(defaultLootTable, new Properties());
    }

    public ResourceKey<LootTable> getDefaultLootTable() {
        return getLootTable(getStack());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(Component.translatable("item.silentgear.blueprint_package.desc1").withStyle(ChatFormatting.ITALIC));
        tooltip.add(Component.translatable("item.silentgear.blueprint_package.desc2").withStyle(ChatFormatting.ITALIC));
        super.appendHoverText(stack, context, tooltip, flagIn);
    }
}
