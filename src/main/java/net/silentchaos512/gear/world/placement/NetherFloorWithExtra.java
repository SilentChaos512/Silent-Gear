package net.silentchaos512.gear.world.placement;

import com.mojang.datafixers.Dynamic;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.placement.Placement;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class NetherFloorWithExtra extends Placement<NetherFloorWithExtraConfig> {
    public static NetherFloorWithExtra INSTANCE = new NetherFloorWithExtra(NetherFloorWithExtraConfig::deserialize);

    private static final int TRACE_DEPTH = 32;

    public NetherFloorWithExtra(Function<Dynamic<?>, ? extends NetherFloorWithExtraConfig> p_i51371_1_) {
        super(p_i51371_1_);
    }

    @Override
    public Stream<BlockPos> getPositions(IWorld world, ChunkGenerator<? extends GenerationSettings> chunkGenerator, Random random, NetherFloorWithExtraConfig config, BlockPos pos) {
        int count = config.count;
        if (random.nextFloat() < config.extraChance) {
            count += config.extraCount;
        }

        final int height = random.nextInt(config.minHeight) + config.maxHeight;

        return IntStream.range(0, count).mapToObj((p_215051_3_) -> {
            BlockPos pos1 = new BlockPos(
                    pos.getX() + random.nextInt(16),
                    height,
                    pos.getZ() + random.nextInt(16));
            return findFloor(world, pos1);
        }).filter(Objects::nonNull);
    }

    @Nullable
    private static BlockPos findFloor(IWorldReader world, BlockPos start) {
        for (int i = 0; i < TRACE_DEPTH; ++i) {
            BlockPos pos = start.down(i);
            if (pos.getY() < 0) {
                break;
            }
            if (world.isAirBlock(pos) && world.getBlockState(pos.down(1)).getBlock() == Blocks.NETHERRACK) {
                return pos;
            }
        }
        return null;
    }
}
