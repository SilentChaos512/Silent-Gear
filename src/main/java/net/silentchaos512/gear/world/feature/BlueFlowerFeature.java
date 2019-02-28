package net.silentchaos512.gear.world.feature;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.AbstractFlowersFeature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.silentchaos512.gear.init.ModBlocks;

import java.util.Random;

public class BlueFlowerFeature extends AbstractFlowersFeature {
    private static final int TRY_COUNT = 32;

    @Override
    public boolean func_212245_a(IWorld p_212245_1_, IChunkGenerator<? extends IChunkGenSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, NoFeatureConfig p_212245_5_) {
        IBlockState iblockstate = this.getRandomFlower(p_212245_3_, p_212245_4_);
        int i = 0;

        // Same as super, but fewer iterations
        for(int j = 0; j < TRY_COUNT; ++j) {
            BlockPos blockpos = p_212245_4_.add(p_212245_3_.nextInt(8) - p_212245_3_.nextInt(8), p_212245_3_.nextInt(4) - p_212245_3_.nextInt(4), p_212245_3_.nextInt(8) - p_212245_3_.nextInt(8));
            if (p_212245_1_.isAirBlock(blockpos) && blockpos.getY() < 255 && iblockstate.isValidPosition(p_212245_1_, blockpos)) {
                p_212245_1_.setBlockState(blockpos, iblockstate, 2);
                ++i;
            }
        }

        return i > 0;
    }

    @Override
    public IBlockState getRandomFlower(Random p_202355_1_, BlockPos p_202355_2_) {
        return ModBlocks.FLOWER.asBlockState();
    }
}
