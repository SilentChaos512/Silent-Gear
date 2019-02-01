package net.silentchaos512.gear.block;

import net.minecraft.block.BlockCrops;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.init.ModItems;
import net.silentchaos512.gear.item.CraftingItems;

public class FlaxPlant extends BlockCrops {
    public FlaxPlant() {
        super(Builder.create(Material.PLANTS)
                .sound(SoundType.PLANT)
        );
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
    public void getDrops(IBlockState state, NonNullList<ItemStack> drops, World world, BlockPos pos, int fortune) {
        // Always get one seed back
        drops.add(new ItemStack(getSeedsItem()));

        int age = getAge(state);
        if (age >= 7) {
            // Seeds
            for (int i = 0; i < 1 + fortune; ++i)
                if (SilentGear.random.nextInt(15) <= age)
                    drops.add(new ItemStack(getSeedsItem()));
            // Fibers
            int fiberCount = 2 + fortune + SilentGear.random.nextInt(3);
            for (int i = 0; i < fiberCount; ++i)
                drops.add(new ItemStack(getCropsItem()));
        }
    }
}
