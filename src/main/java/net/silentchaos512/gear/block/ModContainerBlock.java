package net.silentchaos512.gear.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

public abstract class ModContainerBlock<T extends BlockEntity> extends BaseEntityBlock {
    private final BlockEntityType.BlockEntitySupplier<? extends T> tileFactory;

    public ModContainerBlock(BlockEntityType.BlockEntitySupplier<? extends T> tileFactory, Properties properties) {
        super(properties);
        this.tileFactory = tileFactory;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return tileFactory.create(pos, state);
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide()) {
            BlockEntity tile = level.getBlockEntity(pos);
            if (tile instanceof INamedContainerExtraData te && player instanceof ServerPlayer) {
                player.openMenu(te, te::encodeExtraData);
            } else if (tile instanceof MenuProvider menuProvider) {
                player.openMenu(menuProvider);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
}
