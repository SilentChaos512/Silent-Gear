package net.silentchaos512.gear.block.compounder;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import net.silentchaos512.gear.api.material.IMaterialCategory;
import net.silentchaos512.gear.item.CompoundMaterialItem;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.function.Supplier;

public class CompounderBlock extends Block {
    private final Supplier<TileEntityType<? extends CompounderTileEntity>> tileEntityType;
    private final Supplier<ContainerType<? extends CompounderContainer>> containerType;
    private final Supplier<CompoundMaterialItem> outputItem;
    private final int inputSlotCount;
    private final Collection<IMaterialCategory> categories;

    public CompounderBlock(Supplier<TileEntityType<? extends CompounderTileEntity>> tileEntityType,
                           Supplier<ContainerType<? extends CompounderContainer>> containerType,
                           Supplier<CompoundMaterialItem> outputItem,
                           int inputSlotCount,
                           Collection<IMaterialCategory> categories,
                           Properties properties) {
        super(properties);
        this.tileEntityType = tileEntityType;
        this.containerType = containerType;
        this.outputItem = outputItem;
        this.inputSlotCount = inputSlotCount;
        this.categories = ImmutableSet.copyOf(categories);
    }

    public Collection<IMaterialCategory> getCategories() {
        return categories;
    }

    @SuppressWarnings("deprecation")
    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (tileEntity instanceof CompounderTileEntity && player instanceof ServerPlayerEntity) {
//            player.openContainer((INamedContainerProvider) tileEntity);
            CompounderTileEntity te = (CompounderTileEntity) tileEntity;
            NetworkHooks.openGui((ServerPlayerEntity) player, te, te::encodeExtraData);
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.PASS;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new CompounderTileEntity(this.tileEntityType.get(), this.containerType.get(), this.outputItem, this.inputSlotCount, this.categories);
    }
}
