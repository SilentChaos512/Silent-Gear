package net.silentchaos512.gear.item.gear;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.world.BlockEvent;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.api.stats.StatInstance;

import java.util.Optional;

public class CoreLumberAxe extends CoreAxe {
    @Override
    public Optional<StatInstance> getBaseStatModifier(ItemStat stat) {
        if (stat == ItemStats.MELEE_DAMAGE)
            return Optional.of(StatInstance.makeBaseMod(6));
        if (stat == ItemStats.ATTACK_SPEED)
            return Optional.of(StatInstance.makeBaseMod(-3.3f));
        if (stat == ItemStats.REPAIR_EFFICIENCY)
            return Optional.of(StatInstance.makeBaseMod(0.5f));
        return Optional.empty();
    }

    @Override
    public Optional<StatInstance> getStatModifier(ItemStat stat) {
        if (stat == ItemStats.DURABILITY)
            return Optional.of(StatInstance.makeGearMod(1.0f));
        if (stat == ItemStats.ENCHANTABILITY)
            return Optional.of(StatInstance.makeGearMod(-0.5f));
        if (stat == ItemStats.HARVEST_SPEED)
            return Optional.of(StatInstance.makeGearMod(-0.75f));
        return Optional.empty();
    }

    @Override
    public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, PlayerEntity player) {
        World world = player.world;
        BlockState state = world.getBlockState(pos);

        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        if (state.isIn(BlockTags.LOGS)) {
            if (detectTree(world, x, y, z, state.getBlock())) {
                // Don't allow in creative mode.
                if (player.abilities.isCreativeMode) {
                    return false;
                }

                TreeBreakResult result = new TreeBreakResult();
                breakTree(result, world, x, y, z, x, y, z, stack, state, player);
                return true;
            }
        }

        return false;
    }

    private static boolean detectTree(IBlockReader world, int x, int y, int z, Block wood) {
        int height = y;
        boolean foundTop = false;
        do {
            ++height;
            Block block = world.getBlockState(new BlockPos(x, height, z)).getBlock();
            if (block != wood) {
                --height;
                foundTop = true;
            }
        } while (!foundTop);

        int numLeaves = 0;
        if (height - y < 50) {
            for (int xPos = x - 1; xPos <= x + 1; xPos++) {
                for (int yPos = height - 1; yPos <= height + 1; yPos++) {
                    for (int zPos = z - 1; zPos <= z + 1; zPos++) {
                        BlockPos pos = new BlockPos(xPos, yPos, zPos);
                        BlockState leaves = world.getBlockState(pos);
                        if (leaves.isIn(BlockTags.LEAVES)) {
                            ++numLeaves;
                        }
                    }
                }
            }
        }

        return numLeaves > 3;
    }

    private void breakTree(TreeBreakResult result, World world, int x, int y, int z, int xStart, int yStart, int zStart, ItemStack tool, BlockState state, PlayerEntity player) {
        Block block = state.getBlock();
        BlockPos pos = new BlockPos(x, y, z);

        for (int xPos = x - 1; xPos <= x + 1; ++xPos) {
            for (int yPos = y; yPos <= y + 1; ++yPos) {
                for (int zPos = z - 1; zPos <= z + 1; ++zPos) {
                    BlockPos localPos = new BlockPos(xPos, yPos, zPos);
                    BlockState localState = world.getBlockState(localPos);
                    Block localBlock = localState.getBlock();
                    if (block == localBlock) {
                        int harvestLevel = localBlock.getHarvestLevel(localState);
                        float localHardness = localState.getBlockHardness(world, localPos);

                        if (harvestLevel <= getHarvestLevel(tool, ToolType.AXE, player, localState) && localHardness >= 0) {

                            // Block break event
                            BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(world, localPos, localState, player);
                            // event.setCanceled(cancel);
                            MinecraftForge.EVENT_BUS.post(event);
                            boolean cancel = event.isCanceled();

                            int xDist = xPos - xStart;
                            int yDist = yPos - yStart;
                            int zDist = zPos - zStart;

                            if (9 * xDist * xDist + yDist * yDist + 9 * zDist * zDist < 2500) {
                                if (cancel) {
                                    breakTree(result, world, xPos, yPos, zPos, xStart, yStart, zStart, tool, state, player);
                                } else {
                                    if (!player.abilities.isCreativeMode) {
                                        localBlock.harvestBlock(world, player, pos, state, world.getTileEntity(pos), tool);
                                        onBlockDestroyed(tool, world, localState, localPos, player);
                                        ++result.blocksBroken;
                                    }

                                    world.removeBlock(localPos, false);
                                    if (!world.isRemote) {
                                        breakTree(result, world, xPos, yPos, zPos, xStart, yStart, zStart, tool, state, player);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static class TreeBreakResult {
        int blocksBroken;
    }
}
