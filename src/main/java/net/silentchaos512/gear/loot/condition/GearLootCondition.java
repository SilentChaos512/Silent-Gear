package net.silentchaos512.gear.loot.condition;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;

abstract class GearLootCondition implements ILootCondition {
    static ItemStack getItemUsed(LootContext context) {
        // Attempt to get the tool from context first, then the killer player if unavailable
        ItemStack tool = context.get(LootParameters.TOOL);
        if (tool != null && !tool.isEmpty()) {
            return tool;
        }

        Entity entity = context.get(LootParameters.KILLER_ENTITY);
        if (entity instanceof PlayerEntity) {
            return ((PlayerEntity) entity).getHeldItemMainhand();
        }

        return ItemStack.EMPTY;
    }
}
