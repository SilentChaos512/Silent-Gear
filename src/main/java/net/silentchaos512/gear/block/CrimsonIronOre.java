package net.silentchaos512.gear.block;

import net.minecraft.block.BlockOre;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;

public class CrimsonIronOre extends BlockOre {
    public CrimsonIronOre() {
        super(Builder.create(Material.ROCK)
                .hardnessAndResistance(4, 10)
        );
    }

    @Override
    public int getHarvestLevel(IBlockState state) {
        return 2;
    }
}
