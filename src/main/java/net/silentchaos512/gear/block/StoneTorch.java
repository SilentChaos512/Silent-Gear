package net.silentchaos512.gear.block;

import net.minecraft.block.SoundType;
import net.minecraft.block.TorchBlock;
import net.minecraft.block.material.Material;

public class StoneTorch extends TorchBlock {
    public StoneTorch() {
        super(Properties.create(Material.MISCELLANEOUS)
                .doesNotBlockMovement()
                .hardnessAndResistance(0)
                .lightValue(14)
                .sound(SoundType.STONE)
        );
    }
}
