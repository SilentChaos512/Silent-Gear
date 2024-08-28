package net.silentchaos512.gear.api.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface BreakEventHandler {
    void onBlockBreakEvent(ItemStack stack, Player player, Level level, BlockPos pos, BlockState state);
}
