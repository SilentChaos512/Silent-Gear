package net.silentchaos512.gear.world;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.config.Config;
import net.silentchaos512.gear.init.ModBlocks;
import net.silentchaos512.gear.world.feature.NetherwoodTree;
import net.silentchaos512.lib.config.ConfigOptionOreGen;
import net.silentchaos512.lib.util.MathUtils;
import net.silentchaos512.lib.world.WorldGeneratorSL;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Random;

public class ModWorldGenerator extends WorldGeneratorSL {
    private static final int NETHER_TREE_SPREAD = 6;
    public static final int NETHER_TREE_MIN_Y = 32;
    public static final int NETHER_TREE_MAX_Y = 96;

    public ModWorldGenerator() {
        super(false, SilentGear.MOD_ID);
    }

    @Override
    protected void generateSurface(World world, Random random, int posX, int posZ) {
        generateFlowers(world, random, posX, posZ);
    }

    @Override
    protected void generateNether(World world, Random random, int posX, int posZ) {
        generateOres(world, random, posX, posZ, ModBlocks.crimsonIronOre, Config.crimsonIronOreGen);
        generateNetherTrees(world, random, posX, posZ);
    }

    private void generateFlowers(World world, Random random, int posX, int posZ) {
        // Blacklisted from dimension?
        if (world.provider != null && ArrayUtils.contains(Config.flowerDimensionBlacklist, world.provider.getDimension()))
            return;

        if (random.nextFloat() > Config.flowerClusterChance)
            return;

        int sx = posX + 8 + random.nextInt(16);
        int sy = -1;
        int sz = posZ + 8 + random.nextInt(16);

        for (int i = 0; i < Config.flowerClusterSize; ++i) {
            int x = sx + random.nextInt(7) - 3;
            int y = sy < 0 ? random.nextInt(120) + 50 : sy;
            sy = y + 5;
            int z = sz + random.nextInt(9) - 4;
            BlockPos pos = new BlockPos(x, y, z);

            IBlockState state = ModBlocks.flower.getDefaultState();

            // Find top-most valid block
            for (; y > 50; --y) {
                if (world.isAirBlock(pos) && pos.getY() < 255 && ModBlocks.flower.canBlockStay(world, pos, state)) {
                    world.setBlockState(pos, state, 2);
                    break;
                }
                pos = pos.down();
            }
        }
    }

    private void generateNetherTrees(World world, Random random, int posX, int posZ) {
        // TODO: Configs for chance and min/max count?
        if (MathUtils.tryPercentage(random, Config.netherwoodClusterChance)) {
            final int count = MathUtils.nextIntInclusive(random, Config.netherwoodClusterMinSize, Config.netherwoodClusterMaxSize);

            int sx = posX + 8 + random.nextInt(16);
            int sz = posZ + 8 + random.nextInt(16);

            for (int i = 0; i < count; ++i) {
                int x = sx + MathUtils.nextIntInclusive(random, -NETHER_TREE_SPREAD, NETHER_TREE_SPREAD);
                int z = sz + MathUtils.nextIntInclusive(random, -NETHER_TREE_SPREAD, NETHER_TREE_SPREAD);
                int y = MathUtils.nextIntInclusive(random, NETHER_TREE_MIN_Y + 10, NETHER_TREE_MAX_Y);

                BlockPos pos = new BlockPos(x, y, z);
                IBlockState state = ModBlocks.netherwoodSapling.getDefaultState();

                for (; y > NETHER_TREE_MIN_Y; --y, pos = pos.down()) {
                    if (world.isAirBlock(pos) && pos.getY() < 255 && ModBlocks.netherwoodSapling.canBlockStay(world, pos, state)) {
                        world.setBlockState(pos, state);
                        new NetherwoodTree(ModBlocks.netherwoodLog.getDefaultState(), ModBlocks.netherwoodLeaves.getDefaultState())
                                .generate(world, random, pos.up());
                        break;
                    }
                }
            }
        }
    }

    private void generateOres(World world, Random random, int posX, int posZ, Block block, ConfigOptionOreGen config) {
        final int dimension = world.provider.getDimension();

        if (config.isEnabled() && config.canSpawnInDimension(dimension)) {
            final int veinCount = config.getVeinCount(random);
            final int veinSize = config.veinSize;

            for (int i = 0; i < veinCount; ++i) {
                BlockPos pos = config.getRandomPos(random, posX, posZ);
                IBlockState state = block.getDefaultState();
                new WorldGenMinable(state, veinSize, s -> s != null && s.getBlock() == Blocks.NETHERRACK).generate(world, random, pos);
            }
        }
    }
}
