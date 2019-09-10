package net.silentchaos512.gear.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.CropsBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.PlantType;
import net.silentchaos512.gear.init.ModItems;

public class FlaxPlant extends CropsBlock {
    private final boolean wild;

    public FlaxPlant(boolean wild) {
        super(Properties.create(Material.PLANTS)
                .hardnessAndResistance(0)
                .doesNotBlockMovement()
                .tickRandomly()
                .sound(SoundType.CROP)
        );
        this.wild = wild;
    }

    public BlockState getMaturePlant() {
        return withAge(getMaxAge());
    }

    @Override
    protected boolean isValidGround(BlockState state, IBlockReader worldIn, BlockPos pos) {
        if (wild) {
            return state.isIn(BlockTags.DIRT_LIKE);
        }
        return super.isValidGround(state, worldIn, pos);
    }

    @Override
    protected IItemProvider getSeedsItem() {
        return ModItems.flaxseeds;
    }

    @Override
    public PlantType getPlantType(IBlockReader world, BlockPos pos) {
        return PlantType.Crop;
    }
}
