package net.silentchaos512.gear.block.charger;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.silentchaos512.gear.block.ModContainerBlock;
import net.silentchaos512.gear.setup.SgBlockEntities;

import javax.annotation.Nullable;

public class StarlightChargerBlock extends ModContainerBlock<ChargerTileEntity> {
    private static final VoxelShape SHAPE;

    static {
        VoxelShape base1 = box(3, 0, 3, 13, 2, 13);
        VoxelShape base2 = Shapes.or(base1, box(4, 2, 4, 12, 4, 12));
        VoxelShape base3 = Shapes.or(base2, box(5, 4, 5, 11, 10, 11));
        VoxelShape base4 = Shapes.or(base3, box(1, 11, 1, 15, 12, 15));
        VoxelShape top = box(0, 12, 0, 16, 16, 16);
        SHAPE = Shapes.or(base4, top);
    }

    public StarlightChargerBlock(BlockEntityType.BlockEntitySupplier<ChargerTileEntity> tileFactory, Properties properties) {
        super(tileFactory, properties);
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? null : createTickerHelper(blockEntityType, SgBlockEntities.STARLIGHT_CHARGER.get(), ChargerTileEntity::tick);
    }
}
