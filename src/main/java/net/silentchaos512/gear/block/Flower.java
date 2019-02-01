package net.silentchaos512.gear.block;

import net.minecraft.block.BlockBush;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class Flower extends BlockBush {
    public Flower() {
        super(Builder.create(Material.PLANTS)
                .sound(SoundType.PLANT)
        );
    }
}
