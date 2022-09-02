package net.silentchaos512.gear.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

public final class ModUtils {
    private ModUtils() {throw new IllegalStateException("Utility class");}

    public static ResourceLocation getBlockId(Block block) {
        return Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(block));
    }
}
