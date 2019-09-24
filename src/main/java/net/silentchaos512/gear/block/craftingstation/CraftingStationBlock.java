package net.silentchaos512.gear.block.craftingstation;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class CraftingStationBlock extends ContainerBlock {
    private static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public CraftingStationBlock() {
        super(Properties.create(Material.WOOD)
                .hardnessAndResistance(3, 10)
                .sound(SoundType.WOOD)
        );
        this.setDefaultState(this.getDefaultState().with(FACING, Direction.SOUTH));
    }

    public static Direction getFacing(BlockState state) {
        return state.has(FACING) ? state.get(FACING) : Direction.SOUTH;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new CraftingStationTileEntity();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            TileEntity tileEntity = worldIn.getTileEntity(pos);
            if (tileEntity instanceof CraftingStationTileEntity) {
                CraftingStationTileEntity tileCraftingStation = (CraftingStationTileEntity) tileEntity;
                InventoryHelper.dropInventoryItems(worldIn, pos, tileCraftingStation.getInternalStorage());
            }
        }
        super.onReplaced(state, worldIn, pos, newState, isMoving);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        return !player.isSneaking() && (worldIn.isRemote || openGui(player, worldIn, pos));
    }

    private static boolean openGui(PlayerEntity player, World worldIn, BlockPos pos) {
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (tileEntity instanceof CraftingStationTileEntity) {
            player.openContainer((INamedContainerProvider) tileEntity);
            //player.addStat(...);
        }
        return true;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return getDefaultState().with(FACING, context.getPlacementHorizontalFacing());
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);
        Direction side = placer.getHorizontalFacing().getOpposite();
        world.setBlockState(pos, state.with(FACING, side), 2);
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
}
