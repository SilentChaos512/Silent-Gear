package net.silentchaos512.gear.block.compounder;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import net.silentchaos512.gear.api.material.IMaterialCategory;

import javax.annotation.Nullable;
import java.util.Collection;

public class CompounderBlock extends Block {
    private final CompounderInfo info;

    public CompounderBlock(CompounderInfo info, Properties properties) {
        super(properties);
        this.info = info;
    }

    public Collection<IMaterialCategory> getCategories() {
        return this.info.getCategories();
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
        return new CompounderTileEntity(this.info);
    }
}
