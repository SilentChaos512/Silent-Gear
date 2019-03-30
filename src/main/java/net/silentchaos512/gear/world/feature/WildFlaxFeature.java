package net.silentchaos512.gear.world.feature;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.AbstractFlowersFeature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.silentchaos512.gear.block.FlaxPlant;
import net.silentchaos512.gear.init.ModBlocks;

import java.util.Random;

public class WildFlaxFeature extends AbstractFlowersFeature {
    private final int tryCount;
    private final int maxCount;

    public WildFlaxFeature(int tryCount, int maxCount) {
        this.tryCount = tryCount;
        this.maxCount = maxCount;
    }

    @Override
    public boolean func_212245_a(IWorld world, IChunkGenerator<? extends IChunkGenSettings> chunkGenerator, Random random, BlockPos pos, NoFeatureConfig config) {
        IBlockState plant = this.getRandomFlower(random, pos);
        int placedCount = 0;

        // Same as super, but different number of iterations and a placement count cap
        for(int j = 0; j < this.tryCount && placedCount < this.maxCount; ++j) {
            BlockPos pos1 = pos.add(
                    random.nextInt(8) - random.nextInt(8),
                    random.nextInt(4) - random.nextInt(4),
                    random.nextInt(8) - random.nextInt(8)
            );
            if (world.isAirBlock(pos1) && pos1.getY() < 255 && plant.isValidPosition(world, pos1)) {
                world.setBlockState(pos1, plant, 2);
                ++placedCount;
            }
        }

        return placedCount > 0;
    }

    @Override
    public IBlockState getRandomFlower(Random p_202355_1_, BlockPos p_202355_2_) {
        return ((FlaxPlant) ModBlocks.WILD_FLAX_PLANT.asBlock()).getMaturePlant();
    }
}
