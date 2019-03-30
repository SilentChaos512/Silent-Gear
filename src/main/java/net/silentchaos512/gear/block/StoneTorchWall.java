package net.silentchaos512.gear.block;

import net.minecraft.block.BlockTorchWall;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class StoneTorchWall extends BlockTorchWall {
    public StoneTorchWall() {
        super(Properties.create(Material.CIRCUITS)
                .doesNotBlockMovement()
                .hardnessAndResistance(0)
                .lightValue(14)
                .sound(SoundType.STONE)
        );
    }
}
