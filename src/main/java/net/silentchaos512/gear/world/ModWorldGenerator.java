package net.silentchaos512.gear.world;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.init.ModBlocks;
import net.silentchaos512.lib.world.WorldGeneratorSL;

public class ModWorldGenerator extends WorldGeneratorSL {

  public ModWorldGenerator() {

    super(false, SilentGear.MOD_ID);
  }

  @Override
  protected void generateSurface(World world, Random random, int posX, int posZ) {

    generateFlowers(world, random, posX, posZ);
  }

  private void generateFlowers(World world, Random random, int posX, int posZ) {

    // TODO: Configs!

    if (random.nextFloat() > 0.025f)
      return;

    int i, x, y, z, meta;
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
}
