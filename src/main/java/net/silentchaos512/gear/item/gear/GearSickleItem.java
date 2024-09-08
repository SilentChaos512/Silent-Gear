package net.silentchaos512.gear.item.gear;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.silentchaos512.gear.api.item.BreakEventHandler;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.setup.SgTags;
import net.silentchaos512.gear.util.GearHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class GearSickleItem extends GearDiggerItem implements BreakEventHandler {
    private static final int DURABILITY_USAGE = 3;
    private static final int BREAK_RANGE = 4;
    private static final int HARVEST_RANGE = 2;
    private static final Map<Block, BlockState> HARVEST_STATES = new HashMap<>();

    static {
        putHarvestState(Blocks.SWEET_BERRY_BUSH, Blocks.SWEET_BERRY_BUSH.defaultBlockState().setValue(SweetBerryBushBlock.AGE, 1));
    }

    /**
     * When a block is harvested by right-clicking with a sickle, the block is normally reset to the
     * default state. This can be used to override that with a different state.
     *
     * @param block The block to match
     * @param state The state to set after harvesting
     */
    public static void putHarvestState(Block block, BlockState state) {
        HARVEST_STATES.put(block, state);
    }

    public GearSickleItem(Supplier<GearType> gearType) {
        super(gearType, SgTags.Blocks.MINEABLE_WITH_SICKLE, GearHelper.getBaseItemProperties());
    }

    //region Sickle harvesting

    private static boolean canRightClickHarvestBlock(BlockState state) {
        Block block = state.getBlock();
        return block instanceof BonemealableBlock;
    }

    private static boolean tryHarvest(ServerLevel world, BlockPos pos, BlockState state, Player player, ItemStack sickle) {
        Block block = state.getBlock();
        BonemealableBlock growable = (BonemealableBlock) block;

        if (!growable.isValidBonemealTarget(world, pos, state)) {
            // Fully grown crop
            NonNullList<ItemStack> drops = NonNullList.create();
            drops.addAll(Block.getDrops(state, world, pos, null, player, sickle));

            // Spawn drops in world, remove first seed
            boolean foundSeed = false;
            for (ItemStack drop : drops) {
                Item item = drop.getItem();
                if (!foundSeed && item instanceof BlockItem && ((BlockItem) item).getBlock() == block) {
                    foundSeed = true;
                } else {
                    Block.popResource(world, pos, drop);
                }
            }

            // Reset state
            world.setBlock(pos, HARVEST_STATES.getOrDefault(block, block.defaultBlockState()), 2);
            return true;
        }

        return false;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        // Sickles can right-click to harvest an area of plants
        ItemStack sickle = context.getItemInHand();
        if (GearHelper.isBroken(sickle) || !(context.getLevel() instanceof ServerLevel serverLevel)) {
            return InteractionResult.PASS;
        }

        BlockPos pos = context.getClickedPos();
        BlockState state = serverLevel.getBlockState(pos);
        if (!canRightClickHarvestBlock(state)) return InteractionResult.PASS;

        Player player = context.getPlayer();
        if (player == null) return InteractionResult.PASS;

        int harvestCount = 0;
        final int radius = HARVEST_RANGE;

        for (int z = pos.getZ() - radius; z <= pos.getZ() + radius; ++z) {
            for (int x = pos.getX() - radius; x <= pos.getX() + radius; ++x) {
                BlockPos target = new BlockPos(x, pos.getY(), z);
                state = serverLevel.getBlockState(target);

                if (canRightClickHarvestBlock(state) && tryHarvest(serverLevel, target, state, player, sickle)) {
                    ++harvestCount;
                }
            }
        }

        if (harvestCount > 0) {
            var damageToTool = Math.min(DURABILITY_USAGE, harvestCount);
            GearHelper.attemptDamage(sickle, damageToTool, player, context.getHand());
            player.causeFoodExhaustion(0.02f);
            return InteractionResult.SUCCESS;
        }

        return GearHelper.onItemUse(context);
    }

    @Override
    public void onBlockBreakEvent(ItemStack stack, Player player, Level level, BlockPos pos, BlockState state) {
        breakPlantsInRange(stack, pos, player, BREAK_RANGE);
    }

    void breakPlantsInRange(ItemStack sickle, BlockPos pos, Player player, final int range) {
        if (GearHelper.isBroken(sickle)) return;

        Level world = player.level();
        BlockState state = world.getBlockState(pos);

        if (!state.is(getToolBlockSet())) return;

        int blocksBroken = 1;

        final int x = pos.getX();
        final int y = pos.getY();
        final int z = pos.getZ();

        for (int xPos = x - range; xPos <= x + range; ++xPos) {
            for (int zPos = z - range; zPos <= z + range; ++zPos) {
                BlockPos target = new BlockPos(xPos, y, zPos);
                if (!(xPos == x && zPos == z) && world.getBlockState(target) == state && breakExtraBlock(sickle, world, target, player)) {
                    ++blocksBroken;
                }
            }
        }
    }

    private static boolean breakExtraBlock(ItemStack sickle, Level level, BlockPos pos, Player player) {
        if (level.isEmptyBlock(pos) || !(player instanceof ServerPlayer serverPlayer)) return false;

        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();

        if (serverPlayer.getAbilities().instabuild) {
            block.playerWillDestroy(level, pos, state, player);
            if (block.onDestroyedByPlayer(state, level, pos, serverPlayer, false, state.getFluidState())) {
                block.destroy(level, pos, state);
            }
            if (!level.isClientSide) {
                serverPlayer.connection.send(new ClientboundBlockUpdatePacket(level, pos));
            }
            return true;
        }

        if (!level.isClientSide && level instanceof ServerLevel) {
            block.playerWillDestroy(level, pos, state, serverPlayer);

            if (block.onDestroyedByPlayer(state, level, pos, serverPlayer, true, state.getFluidState())) {
                block.destroy(level, pos, state);
                var blockEntity = level.getBlockEntity(pos);
                block.playerDestroy(level, player, pos, state, blockEntity, sickle);
                block.popExperience((ServerLevel) level, pos, state.getExpDrop(level, pos, blockEntity, player, sickle));
            }

            serverPlayer.connection.send(new ClientboundBlockUpdatePacket(level, pos));
        } else {
            level.levelEvent(2001, pos, Block.getId(state));
            if (block.onDestroyedByPlayer(state, level, pos, serverPlayer, true, state.getFluidState())) {
                block.destroy(level, pos, state);
            }

            sickle.mineBlock(level, state, pos, serverPlayer);
        }

        return true;
    }

    @Override
    public int getDamageOnBlockBreak(ItemStack gear, Level world, BlockState state, BlockPos pos) {
        return DURABILITY_USAGE;
    }

    //endregion
}
