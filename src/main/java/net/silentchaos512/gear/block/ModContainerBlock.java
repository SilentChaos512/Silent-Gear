package net.silentchaos512.gear.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.function.BiFunction;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class ModContainerBlock<T extends BlockEntity & INamedContainerExtraData> extends Block {
    private final BiFunction<BlockState, BlockGetter, ? extends T> tileFactory;

    public ModContainerBlock(BiFunction<BlockState, BlockGetter, ? extends T> tileFactory, Properties properties) {
        super(properties);
        this.tileFactory = tileFactory;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public BlockEntity createTileEntity(BlockState state, BlockGetter world) {
        return tileFactory.apply(state, world);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity tile = worldIn.getBlockEntity(pos);
            if (tile instanceof Container) {
                Containers.dropContents(worldIn, pos, (Container) tile);
                worldIn.updateNeighbourForOutputSignal(pos, this);
            }
            super.onRemove(state, worldIn, pos, newState, isMoving);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!worldIn.isClientSide) {
            BlockEntity tile = worldIn.getBlockEntity(pos);
            if (tile instanceof INamedContainerExtraData && player instanceof ServerPlayer) {
                INamedContainerExtraData te = (INamedContainerExtraData) tile;
                NetworkHooks.openGui((ServerPlayer) player, te, te::encodeExtraData);
            }
        }
        return InteractionResult.SUCCESS;
    }
}
