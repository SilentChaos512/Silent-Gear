package net.silentchaos512.gear.item.gear;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.silentchaos512.gear.Config;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.BreakEventHandler;
import net.silentchaos512.gear.api.item.GearType;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class GearSawItem extends GearAxeItem implements BreakEventHandler {
    public GearSawItem(Supplier<GearType> gearType) {
        super(gearType);
    }

    @Override
    public void onBlockBreakEvent(ItemStack stack, Player player, Level level, BlockPos pos, BlockState state) {
        if (!level.isClientSide) {
            if (isLog(state) && detectTree(level, pos.getX(), pos.getY(), pos.getZ(), state.getBlock())) {
                // Don't allow in creative mode.
                if (player.getAbilities().instabuild) {
                    return;
                }

                TreeBreakResult result = new TreeBreakResult(stack, player);
                breakTree(result, level, pos, pos, 0);
                SilentGear.LOGGER.debug("{} chopped down a tree with {} blocks using {}. Max recursion depth: {}",
                        player.getScoreboardName(),
                        result.blocksBroken,
                        stack.getHoverName().getString(),
                        result.maxDepth
                );
            }
        }
    }

    private static boolean detectTree(BlockGetter world, int x, int y, int z, Block wood) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(x, y, z);
        int height = y;
        boolean foundTop = false;
        do {
            ++height;
            if (!checkForLogs(world, pos.relative(Direction.UP, height - y))) {
                --height;
                foundTop = true;
            }
        } while (!foundTop);

        int foliageCount = 0;
        if (height - y < 50) {
            for (int xPos = x - 1; xPos <= x + 1; xPos++) {
                for (int yPos = height - 1; yPos <= height + 1; yPos++) {
                    for (int zPos = z - 1; zPos <= z + 1; zPos++) {
                        BlockState leaves = world.getBlockState(pos.set(xPos, yPos, zPos));
                        if (isFoliage(leaves)) {
                            ++foliageCount;
                        }
                    }
                }
            }
        }

        return foliageCount > 3;
    }

    private static boolean checkForLogs(BlockGetter world, BlockPos pos) {
        return isLog(world.getBlockState(pos))
                || isLog(world.getBlockState(pos.relative(Direction.NORTH)))
                || isLog(world.getBlockState(pos.relative(Direction.SOUTH)))
                || isLog(world.getBlockState(pos.relative(Direction.EAST)))
                || isLog(world.getBlockState(pos.relative(Direction.WEST)))
                || isLog(world.getBlockState(pos.relative(Direction.NORTH).relative(Direction.EAST)))
                || isLog(world.getBlockState(pos.relative(Direction.NORTH).relative(Direction.WEST)))
                || isLog(world.getBlockState(pos.relative(Direction.SOUTH).relative(Direction.EAST)))
                || isLog(world.getBlockState(pos.relative(Direction.SOUTH).relative(Direction.WEST)));
    }

    private static boolean isLog(BlockState state) {
        return isLog(state, null);
    }

    private static boolean isLog(BlockState state, @Nullable TreeBreakResult result) {
        if (result != null && result.firstLog != null) {
            return state.getBlock() == result.firstLog;
        }
        // TODO: Add a tag to allow more things to be recognized as trees, like crimson/warped fungus
        return state.is(BlockTags.LOGS);
    }

    private static boolean isFoliage(BlockState state) {
        return isFoliage(state, null);
    }

    private static boolean isFoliage(BlockState state, @Nullable TreeBreakResult result) {
        // TODO: Add a tag to allow more things to be recognized as foliage?
        if (state.getBlock() == Blocks.SHROOMLIGHT) {
            return true;
        }

        // Ignore player-placed leaves
        if (state.hasProperty(LeavesBlock.PERSISTENT) && state.getValue(LeavesBlock.PERSISTENT)) {
            return false;
        }

        if (result != null && result.firstFoliage != null) {
            return state.getBlock() == result.firstFoliage;
        }
        return state.is(BlockTags.LEAVES) || state.is(BlockTags.WART_BLOCKS);
    }

    private void breakTree(TreeBreakResult result, Level world, BlockPos pos, BlockPos startPos, int recursionDepth) {
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

                        float localHardness = localState.getDestroySpeed(world, localPos);

                        if (isCorrectToolForDrops(result.tool, localState) && localHardness >= 0) {
                            int xDist = xPos - startPos.getX();
                            int yDist = yPos - startPos.getY();
                            int zDist = zPos - startPos.getZ();

                            if (9 * xDist * xDist + yDist * yDist + 9 * zDist * zDist < 1000) {
                                if (!result.player.getAbilities().instabuild) {
                                    localBlock.playerDestroy(world, result.player, pos, localState, world.getBlockEntity(pos), result.tool);
                                    mineBlock(result.tool, world, localState, localPos, result.player);
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

    private static final class TreeBreakResult {
        final ItemStack tool;
        final Player player;

        int blocksBroken;
        int maxDepth;
        Block firstLog;
        Block firstFoliage;

        private TreeBreakResult(ItemStack tool, Player player) {
            this.tool = tool;
            this.player = player;
        }
    }
}
