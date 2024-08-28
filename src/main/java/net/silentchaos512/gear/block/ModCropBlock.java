package net.silentchaos512.gear.block;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.CropBlock;

import java.util.function.Supplier;

public class ModCropBlock extends CropBlock {
    private final Supplier<Item> seedItem;

    public ModCropBlock(Supplier<Item> seedItem, Properties builder) {
        super(builder);
        this.seedItem = seedItem;
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return seedItem.get();
    }
}
