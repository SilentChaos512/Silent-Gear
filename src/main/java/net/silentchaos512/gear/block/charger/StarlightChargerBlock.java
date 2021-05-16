package net.silentchaos512.gear.block.charger;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.silentchaos512.gear.block.ModContainerBlock;

import java.util.function.BiFunction;

public class StarlightChargerBlock extends ModContainerBlock<ChargerTileEntity> {
    private static final VoxelShape SHAPE;

    static {
        VoxelShape base1 = makeCuboidShape(3, 0, 3, 13, 2, 13);
        VoxelShape base2 = VoxelShapes.or(base1, makeCuboidShape(4, 2, 4, 12, 4, 12));
        VoxelShape base3 = VoxelShapes.or(base2, makeCuboidShape(5, 4, 5, 11, 10, 11));
        VoxelShape base4 = VoxelShapes.or(base3, makeCuboidShape(1, 11, 1, 15, 12, 15));
        VoxelShape top = makeCuboidShape(0, 12, 0, 16, 16, 16);
        SHAPE = VoxelShapes.or(base4, top);
    }

    public StarlightChargerBlock(BiFunction<BlockState, IBlockReader, ? extends ChargerTileEntity> tileFactory, Properties properties) {
        super(tileFactory, properties);
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }
}
