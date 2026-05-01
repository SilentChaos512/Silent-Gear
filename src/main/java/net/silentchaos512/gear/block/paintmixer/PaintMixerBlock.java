package net.silentchaos512.gear.block.paintmixer;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.silentchaos512.gear.block.ModContainerBlock;
import net.silentchaos512.gear.setup.SgBlockEntities;
import org.jetbrains.annotations.Nullable;

public class PaintMixerBlock extends ModContainerBlock<PaintMixerBlockEntity> {
    public static final MapCodec<PaintMixerBlock> CODEC = simpleCodec(PaintMixerBlock::new);

    public PaintMixerBlock(Properties properties) {
        super(PaintMixerBlockEntity::new, properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide
                ? null
                : createTickerHelper(blockEntityType, SgBlockEntities.PAINT_MIXER.get(), PaintMixerBlockEntity::tick);
    }
}
