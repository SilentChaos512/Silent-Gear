package net.silentchaos512.gear.block;

import net.minecraft.block.BlockFlower;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class Flower extends BlockFlower {
    public Flower() {
        super(Properties.create(Material.PLANTS)
                .hardnessAndResistance(0)
                .doesNotBlockMovement()
                .sound(SoundType.PLANT)
        );
    }
}
