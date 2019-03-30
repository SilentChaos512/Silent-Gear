package net.silentchaos512.gear.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.init.ModItems;
import net.silentchaos512.gear.item.CraftingItems;

public class FlaxPlant extends BlockCrops {
    private final boolean wild;

    public FlaxPlant(boolean wild) {
        super(Properties.create(Material.PLANTS)
                .hardnessAndResistance(0)
                .doesNotBlockMovement()
                .sound(SoundType.PLANT)
        );
        this.wild = wild;
    }

    public IBlockState getMaturePlant() {
        return withAge(getMaxAge());
    }

    @Override
    protected boolean isValidGround(IBlockState state, IBlockReader worldIn, BlockPos pos) {
        if (wild) {
            Block block = state.getBlock();
            return block == Blocks.GRASS_BLOCK || block == Blocks.DIRT || block == Blocks.COARSE_DIRT || block == Blocks.PODZOL;
        }
        return super.isValidGround(state, worldIn, pos);
    }

    @Override
    protected IItemProvider getCropsItem() {
        return CraftingItems.FLAX_FIBER;
    }

    @Override
    protected IItemProvider getSeedsItem() {
        return ModItems.flaxseeds;
    }

    @Override
    public EnumPlantType getPlantType(IBlockReader world, BlockPos pos) {
        return EnumPlantType.Crop;
    }

    @Override
    public void getDrops(IBlockState state, NonNullList<ItemStack> drops, World world, BlockPos pos, int fortune) {
        // Always get one seed back
        drops.add(new ItemStack(getSeedsItem()));

        int age = getAge(state);
        if (age >= 7) {
            // Seeds
            for (int i = 0; i < 1 + fortune; ++i) {
                if (SilentGear.random.nextInt(15) <= age) {
                    drops.add(new ItemStack(getSeedsItem()));
                }
            }
            // Fibers
            if (!this.wild) {
                int fiberCount = 2 + fortune + SilentGear.random.nextInt(3);
                for (int i = 0; i < fiberCount; ++i) {
                    drops.add(new ItemStack(getCropsItem()));
                }
            }
        }
    }
}
