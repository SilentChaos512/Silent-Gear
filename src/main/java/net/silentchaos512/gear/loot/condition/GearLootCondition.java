package net.silentchaos512.gear.loot.condition;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

abstract class GearLootCondition implements LootItemCondition {
    static ItemStack getItemUsed(LootContext context) {
        // Attempt to get the tool from context first, then the killer player if unavailable
        ItemStack tool = context.getParamOrNull(LootContextParams.TOOL);
        if (tool != null && !tool.isEmpty()) {
            return tool;
        }

        Entity entity = context.getParamOrNull(LootContextParams.KILLER_ENTITY);
        if (entity instanceof Player) {
            return ((Player) entity).getMainHandItem();
        }

        return ItemStack.EMPTY;
    }
}
