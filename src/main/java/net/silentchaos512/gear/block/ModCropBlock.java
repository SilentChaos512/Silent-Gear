package net.silentchaos512.gear.block;

import net.minecraft.block.CropsBlock;
import net.minecraft.item.Item;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.PlantType;

import java.util.function.Supplier;

public class ModCropBlock extends CropsBlock {
    private final Supplier<Item> seedItem;

    public ModCropBlock(Supplier<Item> seedItem, Properties builder) {
        super(builder);
        this.seedItem = seedItem;
    }

    @Override
    protected IItemProvider getSeedsItem() {
        return seedItem.get();
    }

    @Override
    public PlantType getPlantType(IBlockReader world, BlockPos pos) {
        return PlantType.CROP;
    }
}
