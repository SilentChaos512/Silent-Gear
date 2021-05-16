package net.silentchaos512.gear.item.gear;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.world.BlockEvent;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.config.Config;

import javax.annotation.Nullable;

public class CoreSaw extends CoreAxe {
    @Override
    public GearType getGearType() {
        return GearType.SAW;
    }

    @Override
    public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, PlayerEntity player) {
        World world = player.world;
        if (!world.isRemote) {
            BlockState state = world.getBlockState(pos);

            if (isLog(state) && detectTree(world, pos.getX(), pos.getY(), pos.getZ(), state.getBlock())) {
                // Don't allow in creative mode.
                if (player.abilities.isCreativeMode) {
                    return false;
                }

                TreeBreakResult result = new TreeBreakResult(stack, player);
                breakTree(result, world, pos, pos, 0);
                SilentGear.LOGGER.debug("{} chopped down a tree with {} blocks using {}. Max recursion depth: {}",
                        player.getScoreboardName(),
                        result.blocksBroken,
                        stack.getDisplayName().getString(),
                        result.maxDepth);
                return true;
            }
        }

        return false;
    }

    private static boolean detectTree(IBlockReader world, int x, int y, int z, Block wood) {
        BlockPos.Mutable pos = new BlockPos.Mutable(x, y, z);
        int height = y;
        boolean foundTop = false;
        do {
            ++height;
            if (!checkForLogs(world, pos.offset(Direction.UP, height - y))) {
                --height;
                foundTop = true;
            }
        } while (!foundTop);

        int foliageCount = 0;
        if (height - y < 50) {
            for (int xPos = x - 1; xPos <= x + 1; xPos++) {
                for (int yPos = height - 1; yPos <= height + 1; yPos++) {
                    for (int zPos = z - 1; zPos <= z + 1; zPos++) {
                        BlockState leaves = world.getBlockState(pos.setPos(xPos, yPos, zPos));
                        if (isFoliage(leaves)) {
                            ++foliageCount;
                        }
                    }
                }
            }
        }

        return foliageCount > 3;
    }

    private static boolean checkForLogs(IBlockReader world, BlockPos pos) {
        return isLog(world.getBlockState(pos))
                || isLog(world.getBlockState(pos.offset(Direction.NORTH)))
                || isLog(world.getBlockState(pos.offset(Direction.SOUTH)))
                || isLog(world.getBlockState(pos.offset(Direction.EAST)))
                || isLog(world.getBlockState(pos.offset(Direction.WEST)))
                || isLog(world.getBlockState(pos.offset(Direction.NORTH).offset(Direction.EAST)))
                || isLog(world.getBlockState(pos.offset(Direction.NORTH).offset(Direction.WEST)))
                || isLog(world.getBlockState(pos.offset(Direction.SOUTH).offset(Direction.EAST)))
                || isLog(world.getBlockState(pos.offset(Direction.SOUTH).offset(Direction.WEST)));
    }

    private static boolean isLog(BlockState state) {
        return isLog(state, null);
    }

    private static boolean isLog(BlockState state, @Nullable TreeBreakResult result) {
        if (result != null && result.firstLog != null) {
            return state.getBlock() == result.firstLog;
        }
        return state.isIn(BlockTags.LOGS);
    }

    private static boolean isFoliage(BlockState state) {
        return isFoliage(state, null);
    }

    private static boolean isFoliage(BlockState state, @Nullable TreeBreakResult result) {
        if (state.getBlock() == Blocks.SHROOMLIGHT) {
            return true;
        }

        // Ignore player-placed leaves
        if (state.hasProperty(LeavesBlock.PERSISTENT) && state.get(LeavesBlock.PERSISTENT)) {
            return false;
        }

        if (result != null && result.firstFoliage != null) {
            return state.getBlock() == result.firstFoliage;
        }
        return state.isIn(BlockTags.LEAVES) || state.isIn(BlockTags.WART_BLOCKS);
    }

    private void breakTree(TreeBreakResult result, World world, BlockPos pos, BlockPos startPos, int recursionDepth) {
        result.maxDepth = recursionDepth;

        if (recursionDepth > Config.Common.sawRecursionDepth.get()) {
            return;
        }

        for (int xPos = pos.getX() - 1; xPos <= pos.getX() + 1; ++xPos) {
            for (int yPos = pos.getY() - 1; yPos <= pos.getY() + 1; ++yPos) { // starts 1 down for hanging foliage
                for (int zPos = pos.getZ() - 1; zPos <= pos.getZ() + 1; ++zPos) {
                    BlockPos localPos = new BlockPos(xPos, yPos, zPos);
                    BlockState localState = world.getBlockState(localPos);
                    Block localBlock = localState.getBlock();

                    boolean isLog = isLog(localState, result);
                    boolean isFoliage = isFoliage(localState, result);

                    if (isLog || isFoliage) {
                        // Remember what logs/leaves this tree has, so we don't chop down multiple trees
                        if (isLog && result.firstLog == null) {
                            result.firstLog = localBlock;
                        }
                        if (isFoliage && result.firstFoliage == null) {
                            result.firstFoliage = localBlock;
                        }

                        int harvestLevel = localBlock.getHarvestLevel(localState);
                        float localHardness = localState.getBlockHardness(world, localPos);

                        if (harvestLevel <= getHarvestLevel(result.tool, ToolType.AXE, result.player, localState) && localHardness >= 0) {
                            // Block break event
                            BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(world, localPos, localState, result.player);
                            MinecraftForge.EVENT_BUS.post(event);
                            boolean cancel = event.isCanceled();

                            int xDist = xPos - startPos.getX();
                            int yDist = yPos - startPos.getY();
                            int zDist = zPos - startPos.getZ();

                            if (9 * xDist * xDist + yDist * yDist + 9 * zDist * zDist < 1000) {
                                if (cancel) {
                                    breakTree(result, world, localPos, startPos, recursionDepth + 1);
                                } else {
                                    if (!result.player.abilities.isCreativeMode) {
                                        localBlock.harvestBlock(world, result.player, pos, localState, world.getTileEntity(pos), result.tool);
                                        onBlockDestroyed(result.tool, world, localState, localPos, result.player);
                                        ++result.blocksBroken;
                                    }

                                    world.removeBlock(localPos, false);
                                    breakTree(result, world, localPos, startPos, recursionDepth + 1);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static final class TreeBreakResult {
        final ItemStack tool;
        final PlayerEntity player;

        int blocksBroken;
        int maxDepth;
        Block firstLog;
        Block firstFoliage;

        private TreeBreakResult(ItemStack tool, PlayerEntity player) {
            this.tool = tool;
            this.player = player;
        }
    }
}
