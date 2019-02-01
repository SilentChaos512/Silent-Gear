package net.silentchaos512.gear.init;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.LazyLoadBase;
import net.silentchaos512.lib.block.IBlockProvider;

import java.util.Locale;
import java.util.function.Supplier;

public enum Test implements IBlockProvider, IStringSerializable {
    ;

    private final LazyLoadBase<Block> block;

    Test(Supplier<Block> blockSupplier) {
        this.block = new LazyLoadBase<>(blockSupplier);
    }

    @Override
    public String getName() {
        return name().toLowerCase(Locale.ROOT);
    }

    @Override
    public Block asBlock() {
        return block.getValue();
    }

    @Override
    public Item asItem() {
        return asBlock().asItem();
    }
}
