package net.silentchaos512.gear.block.craftingstation;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockCraftingStation extends BlockContainer {
    private static final DirectionProperty FACING = DirectionProperty.create("facing", EnumFacing.Plane.HORIZONTAL);

    public BlockCraftingStation() {
        super(Properties.create(Material.WOOD)
                .hardnessAndResistance(3, 10)
                .sound(SoundType.WOOD)
        );
        this.setDefaultState(this.getDefaultState().with(FACING, EnumFacing.SOUTH));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new TileCraftingStation();
    }

    @Override
    public void onReplaced(IBlockState state, World worldIn, BlockPos pos, IBlockState newState, boolean isMoving) {
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (tileEntity instanceof TileCraftingStation) {
            TileCraftingStation tileCraftingStation = (TileCraftingStation) tileEntity;
            InventoryHelper.dropInventoryItems(worldIn, pos, tileCraftingStation.getInternalStorage());
        }
        super.onReplaced(state, worldIn, pos, newState, isMoving);
    }

    @Override
    public boolean onBlockActivated(IBlockState state, World world, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        return !player.isSneaking() && (world.isRemote || this.openGui(player, world, pos));
    }

    private boolean openGui(EntityPlayer player, World world, BlockPos pos) {
        // FIXME
//        player.openGui(SilentGear.instance, 0, world, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }

    @Nullable
    @Override
    public IBlockState getStateForPlacement(BlockItemUseContext context) {
        return getDefaultState().with(FACING, context.getPlacementHorizontalFacing());
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);
        EnumFacing side = placer.getHorizontalFacing().getOpposite();
        world.setBlockState(pos, state.with(FACING, side), 2);
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean doesSideBlockRendering(IBlockState state, IWorldReader world, BlockPos pos, EnumFacing face) {
        return face == EnumFacing.DOWN || face == EnumFacing.UP;
    }
}
