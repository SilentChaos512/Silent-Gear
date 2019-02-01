package net.silentchaos512.gear.item;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemSeeds;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.EnumPlantType;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.init.ModBlocks;

public class Flaxseeds extends ItemSeeds {
    public Flaxseeds() {
        super(ModBlocks.FLAX_PLANT.asBlock(), new Builder().group(SilentGear.ITEM_GROUP));
    }

    @Override
    public IBlockState getPlant(IBlockReader world, BlockPos pos) {
        return ModBlocks.FLAX_PLANT.asBlockState();
    }

    @Override
    public EnumPlantType getPlantType(IBlockReader world, BlockPos pos) {
        return EnumPlantType.Plains;
    }
}
