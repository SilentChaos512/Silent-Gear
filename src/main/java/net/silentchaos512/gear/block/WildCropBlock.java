package net.silentchaos512.gear.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.BushBlock;

public class WildCropBlock extends BushBlock {
    public static final MapCodec<BushBlock> CODEC = simpleCodec(WildCropBlock::new);

    public WildCropBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public MapCodec<BushBlock> codec() {
        return CODEC;
    }
}
