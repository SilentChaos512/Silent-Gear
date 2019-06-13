package net.silentchaos512.gear.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.OreBlock;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;

public class CrimsonIronOre extends OreBlock {
    public CrimsonIronOre() {
        super(Properties.create(Material.ROCK)
                .hardnessAndResistance(4, 10)
        );
    }

    @Override
    public int getHarvestLevel(BlockState state) {
        return 2;
    }

    @Nullable
    @Override
    public ToolType getHarvestTool(BlockState state) {
        return ToolType.PICKAXE;
    }
}
