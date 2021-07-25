package net.silentchaos512.gear.world.feature;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.shapes.BitSetDiscreteVoxelShape;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.LevelSimulatedRW;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraftforge.common.Tags;

import java.util.*;

public class NetherwoodTreeFeature extends Feature<TreeConfiguration> {
    public NetherwoodTreeFeature(Codec<TreeConfiguration> codec) {
        super(codec);
    }

    public static boolean isFree(LevelSimulatedReader p_236410_0_, BlockPos p_236410_1_) {
        return isReplaceableAt(p_236410_0_, p_236410_1_) || p_236410_0_.isStateAtPosition(p_236410_1_, (p_236417_0_) -> {
            return p_236417_0_.is(BlockTags.LOGS);
        });
    }

    private static boolean isVine(LevelSimulatedReader p_236414_0_, BlockPos p_236414_1_) {
        return p_236414_0_.isStateAtPosition(p_236414_1_, (p_236415_0_) -> {
            return p_236415_0_.is(Blocks.VINE);
        });
    }

    private static boolean isWaterAt(LevelSimulatedReader p_236416_0_, BlockPos p_236416_1_) {
        return p_236416_0_.isStateAtPosition(p_236416_1_, (p_236413_0_) -> {
            return p_236413_0_.is(Blocks.WATER);
        });
    }

    public static boolean isAirOrLeavesAt(LevelSimulatedReader p_236412_0_, BlockPos p_236412_1_) {
        return p_236412_0_.isStateAtPosition(p_236412_1_, (p_236411_0_) -> {
            return p_236411_0_.isAir() || p_236411_0_.is(BlockTags.LEAVES);
        });
    }

    private static boolean isDirtOrFarmlandAt(LevelSimulatedReader world, BlockPos pos) {
        return world.isStateAtPosition(pos, state -> {
            Block block = state.getBlock();
            return state.is(Tags.Blocks.DIRT) || state.is(Tags.Blocks.NETHERRACK) || block == Blocks.FARMLAND;
        });
    }

    private static boolean isTallPlantAt(LevelSimulatedReader p_236419_0_, BlockPos p_236419_1_) {
        return p_236419_0_.isStateAtPosition(p_236419_1_, (p_236406_0_) -> {
            Material material = p_236406_0_.getMaterial();
            return material == Material.REPLACEABLE_PLANT;
        });
    }

    public static void setBlockKnownShape(LevelWriter p_236408_0_, BlockPos p_236408_1_, BlockState p_236408_2_) {
        p_236408_0_.setBlock(p_236408_1_, p_236408_2_, 19);
    }

    public static boolean isReplaceableAt(LevelSimulatedReader p_236404_0_, BlockPos p_236404_1_) {
        return isAirOrLeavesAt(p_236404_0_, p_236404_1_) || isTallPlantAt(p_236404_0_, p_236404_1_) || isWaterAt(p_236404_0_, p_236404_1_);
    }

    /**
     * Called when placing the tree feature.
     */
    private boolean place(LevelSimulatedRW generationReader, Random rand, BlockPos positionIn, Set<BlockPos> p_225557_4_, Set<BlockPos> p_225557_5_, BoundingBox boundingBoxIn, TreeConfiguration configIn) {
        int i = configIn.trunkPlacer.getTreeHeight(rand);
        int j = configIn.foliagePlacer.foliageHeight(rand, i, configIn);
        int k = i - j;
        int l = configIn.foliagePlacer.foliageRadius(rand, k);
        BlockPos blockpos;
        if (!configIn.fromSapling) {
            int i1 = generationReader.getHeightmapPos(Heightmap.Types.OCEAN_FLOOR, positionIn).getY();
            int j1 = generationReader.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, positionIn).getY();
            if (j1 - i1 > configIn.maxWaterDepth) {
                return false;
            }

            int k1;
            if (configIn.heightmap == Heightmap.Types.OCEAN_FLOOR) {
                k1 = i1;
            } else if (configIn.heightmap == Heightmap.Types.WORLD_SURFACE) {
                k1 = j1;
            } else {
                k1 = generationReader.getHeightmapPos(configIn.heightmap, positionIn).getY();
            }

            blockpos = new BlockPos(positionIn.getX(), k1, positionIn.getZ());
        } else {
            blockpos = positionIn;
        }

        for (int y = 96; y >= 32; --y) {
            BlockPos pos1 = new BlockPos(blockpos.getX(), y, blockpos.getZ());
            if (pos1.getY() >= 1 && pos1.getY() + i + 1 <= 256) {
                if (isDirtOrFarmlandAt(generationReader, pos1.below(y))) {
                    OptionalInt optionalint = configIn.minimumSize.minClippedHeight();
                    int l1 = this.getMaxFreeTreeHeight(generationReader, i, pos1, configIn);
                    if (l1 >= i || optionalint.isPresent() && l1 >= optionalint.getAsInt()) {
                        List<FoliagePlacer.FoliageAttachment> list = configIn.trunkPlacer.placeTrunk(generationReader, rand, l1, pos1, p_225557_4_, boundingBoxIn, configIn);
                        list.forEach((p_236407_8_) -> {
                            configIn.foliagePlacer.createFoliage(generationReader, rand, configIn, l1, p_236407_8_, j, l, p_225557_5_, boundingBoxIn);
                        });
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private int getMaxFreeTreeHeight(LevelSimulatedReader worldIn, int p_241521_2_, BlockPos pos, TreeConfiguration config) {
        BlockPos.MutableBlockPos blockpos$mutable = new BlockPos.MutableBlockPos();

        for(int i = 0; i <= p_241521_2_ + 1; ++i) {
            int j = config.minimumSize.getSizeAtHeight(p_241521_2_, i);

            for(int k = -j; k <= j; ++k) {
                for(int l = -j; l <= j; ++l) {
                    blockpos$mutable.setWithOffset(pos, k, i, l);
                    if (!isFree(worldIn, blockpos$mutable) || !config.ignoreVines && isVine(worldIn, blockpos$mutable)) {
                        return i - 2;
                    }
                }
            }
        }

        return p_241521_2_;
    }

    protected void setBlock(LevelWriter world, BlockPos pos, BlockState state) {
        setBlockKnownShape(world, pos, state);
    }

    @Override
    public boolean place(WorldGenLevel p_241855_1_, ChunkGenerator p_241855_2_, Random p_241855_3_, BlockPos p_241855_4_, TreeConfiguration p_241855_5_) {
        Set<BlockPos> set = Sets.newHashSet();
        Set<BlockPos> set1 = Sets.newHashSet();
        Set<BlockPos> set2 = Sets.newHashSet();
        BoundingBox mutableboundingbox = BoundingBox.getUnknownBox();
        boolean flag = this.place(p_241855_1_, p_241855_3_, p_241855_4_, set, set1, mutableboundingbox, p_241855_5_);
        if (mutableboundingbox.x0 <= mutableboundingbox.x1 && flag && !set.isEmpty()) {
            if (!p_241855_5_.decorators.isEmpty()) {
                List<BlockPos> list = Lists.newArrayList(set);
                List<BlockPos> list1 = Lists.newArrayList(set1);
                list.sort(Comparator.comparingInt(Vec3i::getY));
                list1.sort(Comparator.comparingInt(Vec3i::getY));
                p_241855_5_.decorators.forEach((p_236405_6_) -> {
                    p_236405_6_.place(p_241855_1_, p_241855_3_, list, list1, set2, mutableboundingbox);
                });
            }

            DiscreteVoxelShape voxelshapepart = this.updateLeaves(p_241855_1_, mutableboundingbox, set, set2);
            StructureTemplate.updateShapeAtEdge(p_241855_1_, 3, voxelshapepart, mutableboundingbox.x0, mutableboundingbox.y0, mutableboundingbox.z0);
            return true;
        } else {
            return false;
        }
    }

    private DiscreteVoxelShape updateLeaves(LevelAccessor p_236403_1_, BoundingBox p_236403_2_, Set<BlockPos> p_236403_3_, Set<BlockPos> p_236403_4_) {
        List<Set<BlockPos>> list = Lists.newArrayList();
        DiscreteVoxelShape voxelshapepart = new BitSetDiscreteVoxelShape(p_236403_2_.getXSpan(), p_236403_2_.getYSpan(), p_236403_2_.getZSpan());
        int i = 6;

        for(int j = 0; j < 6; ++j) {
            list.add(Sets.newHashSet());
        }

        BlockPos.MutableBlockPos blockpos$mutable = new BlockPos.MutableBlockPos();

        for(BlockPos blockpos : Lists.newArrayList(p_236403_4_)) {
            if (p_236403_2_.isInside(blockpos)) {
                voxelshapepart.setFull(blockpos.getX() - p_236403_2_.x0, blockpos.getY() - p_236403_2_.y0, blockpos.getZ() - p_236403_2_.z0, true, true);
            }
        }

        for(BlockPos blockpos1 : Lists.newArrayList(p_236403_3_)) {
            if (p_236403_2_.isInside(blockpos1)) {
                voxelshapepart.setFull(blockpos1.getX() - p_236403_2_.x0, blockpos1.getY() - p_236403_2_.y0, blockpos1.getZ() - p_236403_2_.z0, true, true);
            }

            for(Direction direction : Direction.values()) {
                blockpos$mutable.setWithOffset(blockpos1, direction);
                if (!p_236403_3_.contains(blockpos$mutable)) {
                    BlockState blockstate = p_236403_1_.getBlockState(blockpos$mutable);
                    if (blockstate.hasProperty(BlockStateProperties.DISTANCE)) {
                        list.get(0).add(blockpos$mutable.immutable());
                        setBlockKnownShape(p_236403_1_, blockpos$mutable, blockstate.setValue(BlockStateProperties.DISTANCE, 1));
                        if (p_236403_2_.isInside(blockpos$mutable)) {
                            voxelshapepart.setFull(blockpos$mutable.getX() - p_236403_2_.x0, blockpos$mutable.getY() - p_236403_2_.y0, blockpos$mutable.getZ() - p_236403_2_.z0, true, true);
                        }
                    }
                }
            }
        }

        for(int l = 1; l < 6; ++l) {
            Set<BlockPos> set = list.get(l - 1);
            Set<BlockPos> set1 = list.get(l);

            for(BlockPos blockpos2 : set) {
                if (p_236403_2_.isInside(blockpos2)) {
                    voxelshapepart.setFull(blockpos2.getX() - p_236403_2_.x0, blockpos2.getY() - p_236403_2_.y0, blockpos2.getZ() - p_236403_2_.z0, true, true);
                }

                for(Direction direction1 : Direction.values()) {
                    blockpos$mutable.setWithOffset(blockpos2, direction1);
                    if (!set.contains(blockpos$mutable) && !set1.contains(blockpos$mutable)) {
                        BlockState blockstate1 = p_236403_1_.getBlockState(blockpos$mutable);
                        if (blockstate1.hasProperty(BlockStateProperties.DISTANCE)) {
                            int k = blockstate1.getValue(BlockStateProperties.DISTANCE);
                            if (k > l + 1) {
                                BlockState blockstate2 = blockstate1.setValue(BlockStateProperties.DISTANCE, l + 1);
                                setBlockKnownShape(p_236403_1_, blockpos$mutable, blockstate2);
                                if (p_236403_2_.isInside(blockpos$mutable)) {
                                    voxelshapepart.setFull(blockpos$mutable.getX() - p_236403_2_.x0, blockpos$mutable.getY() - p_236403_2_.y0, blockpos$mutable.getZ() - p_236403_2_.z0, true, true);
                                }

                                set1.add(blockpos$mutable.immutable());
                            }
                        }
                    }
                }
            }
        }

        return voxelshapepart;
    }
}
