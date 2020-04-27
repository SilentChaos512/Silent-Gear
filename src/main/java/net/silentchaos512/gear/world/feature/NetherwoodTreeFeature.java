package net.silentchaos512.gear.world.feature;

import com.mojang.datafixers.Dynamic;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.IWorldGenerationBaseReader;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.feature.AcaciaFeature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.silentchaos512.gear.init.ModTags;

import java.util.Optional;
import java.util.function.Function;

public class NetherwoodTreeFeature extends AcaciaFeature {
    public NetherwoodTreeFeature(Function<Dynamic<?>, ? extends TreeFeatureConfig> p_i225798_1_) {
        super(p_i225798_1_);
    }

    @Override
    public Optional<BlockPos> func_227212_a_(IWorldGenerationReader p_227212_1_, int p_227212_2_, int p_227212_3_, int p_227212_4_, BlockPos p_227212_5_, TreeFeatureConfig treeFeatureConfigIn) {
        BlockPos blockpos = p_227212_5_;

        if (blockpos.getY() >= 1 && blockpos.getY() + p_227212_2_ + 1 <= p_227212_1_.getMaxHeight()) {
            for(int i1 = 0; i1 <= p_227212_2_ + 1; ++i1) {
                int j1 = treeFeatureConfigIn.foliagePlacer.func_225570_a_(p_227212_3_, p_227212_2_, p_227212_4_, i1);
                BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

                for(int k = -j1; k <= j1; ++k) {
                    int l = -j1;

                    while(l <= j1) {
                        if (i1 + blockpos.getY() >= 0 && i1 + blockpos.getY() < p_227212_1_.getMaxHeight()) {
                            blockpos$mutable.setPos(k + blockpos.getX(), i1 + blockpos.getY(), l + blockpos.getZ());
                            if (func_214587_a(p_227212_1_, blockpos$mutable) && (treeFeatureConfigIn.ignoreVines || !func_227222_d_(p_227212_1_, blockpos$mutable))) {
                                ++l;
                                continue;
                            }

                            return Optional.empty();
                        }

                        return Optional.empty();
                    }
                }
            }

            return isNetherwoodSoil(p_227212_1_, blockpos.down()) && blockpos.getY() < p_227212_1_.getMaxHeight() - p_227212_2_ - 1 ? Optional.of(blockpos) : Optional.empty();
        } else {
            return Optional.empty();
        }
    }

    private static boolean isNetherwoodSoil(IWorldGenerationBaseReader reader, BlockPos pos) {
        return reader.hasBlockState(pos, state -> state.isIn(ModTags.Blocks.NETHERWOOD_SOIL));
    }
}
