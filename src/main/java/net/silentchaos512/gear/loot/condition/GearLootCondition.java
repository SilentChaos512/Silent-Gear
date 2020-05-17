package net.silentchaos512.gear.loot.condition;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.conditions.ILootCondition;

abstract class GearLootCondition implements ILootCondition {
    static ItemStack getItemUsed(LootContext context) {
        ItemStack tool = context.get(LootParameters.TOOL);
        if (tool != null && !tool.isEmpty())
            return tool;
        Entity entity = context.get(LootParameters.KILLER_ENTITY);
        if (entity instanceof PlayerEntity)
            return ((PlayerEntity) entity).getHeldItemMainhand();
        return ItemStack.EMPTY;
    }
}
