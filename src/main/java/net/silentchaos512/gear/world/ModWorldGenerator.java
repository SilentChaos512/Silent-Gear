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
import net.silentchaos512.lib.config.ConfigOptionOreGen;
import net.silentchaos512.lib.world.WorldGeneratorSL;

import java.util.Random;

public class ModWorldGenerator extends WorldGeneratorSL {
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
    }

    private void generateFlowers(World world, Random random, int posX, int posZ) {
        // TODO: Configs!

        if (random.nextFloat() > 0.025f)
            return;

        int i, x, y, z;
        IBlockState state;
        BlockPos pos;

        int sx = posX + 8 + random.nextInt(16);
        int sy = -1;
        int sz = posZ + 8 + random.nextInt(16);

        for (i = 0; i < 10; ++i) {
            x = sx + random.nextInt(7) - 3;
            y = sy < 0 ? random.nextInt(120) + 50 : sy;
            sy = y + 5;
            z = sz + random.nextInt(9) - 4;
            pos = new BlockPos(x, y, z);

            state = ModBlocks.flower.getDefaultState();

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
