package net.silentchaos512.gear.block.alloymaker;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.silentchaos512.gear.api.material.IMaterialCategory;
import net.silentchaos512.gear.block.IDroppableInventory;
import net.silentchaos512.gear.block.ModContainerBlock;
import net.silentchaos512.gear.crafting.recipe.alloy.AlloyRecipe;
import net.silentchaos512.gear.util.TextUtil;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AlloyMakerBlock<R extends AlloyRecipe> extends ModContainerBlock<AlloyMakerBlockEntity<R>> {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    private static final VoxelShape SHAPE = Block.box(1, 0, 1, 15, 27, 15);

    private final AlloyMakerInfo<R> info;
    private final MapCodec<AlloyMakerBlock<R>> codec;

    public AlloyMakerBlock(AlloyMakerInfo<R> info, Properties properties) {
        super((pos, state) -> new AlloyMakerBlockEntity<>(info, pos, state), properties);
        this.info = info;
        this.codec = simpleCodec(p -> new AlloyMakerBlock<>(info, p));
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(LIT, false));
    }

    public Collection<IMaterialCategory> getCategories() {
        return this.info.getCategories();
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level worldIn, BlockPos pos, Player player, BlockHitResult hit) {
        if (worldIn.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        this.interactWith(worldIn, pos, player);
        return InteractionResult.CONSUME;
    }

    protected void interactWith(Level worldIn, BlockPos pos, Player player) {
        BlockEntity tileEntity = worldIn.getBlockEntity(pos);
        if (tileEntity instanceof AlloyMakerBlockEntity<?> compoundMaker && player instanceof ServerPlayer) {
            player.openMenu(compoundMaker, compoundMaker::encodeExtraData);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext tooltipContext, List<Component> tooltip, TooltipFlag flagIn) {
        Set<Component> catNameSet = this.info.getCategories().stream().map(IMaterialCategory::getDisplayName).collect(Collectors.toSet());
        Component catStr = TextUtil.separatedList(catNameSet);
        if (catStr != null) {
            tooltip.add(TextUtil.translate("block", "compounder.desc", catStr));
        }
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity tileEntity = worldIn.getBlockEntity(pos);
            if (tileEntity instanceof IDroppableInventory droppableInventory) {
                Containers.dropContents(worldIn, pos, droppableInventory.getItemsToDrop());
                worldIn.updateNeighbourForOutputSignal(pos, this);
            }
            super.onRemove(state, worldIn, pos, newState, isMoving);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, LIT);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? null : createTickerHelper(blockEntityType, info.getBlockEntityType(), info.getServerBlockEntityTicker());
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return codec;
    }
}
