package net.silentchaos512.gear.data;

import net.minecraft.data.BlockFamily;
import net.silentchaos512.gear.setup.SgBlocks;

import java.util.function.Supplier;

public class SgBlockFamilies {
    public static final Supplier<BlockFamily> NETHERWOOD = () ->
            new BlockFamily.Builder(SgBlocks.NETHERWOOD_PLANKS.get())
                    .fence(SgBlocks.NETHERWOOD_FENCE.get())
                    .fenceGate(SgBlocks.NETHERWOOD_FENCE_GATE.get())
                    .slab(SgBlocks.NETHERWOOD_SLAB.get())
                    .stairs(SgBlocks.NETHERWOOD_STAIRS.get())
                    .door(SgBlocks.NETHERWOOD_DOOR.get())
                    .trapdoor(SgBlocks.NETHERWOOD_TRAPDOOR.get())
                    .recipeUnlockedBy("has_planks")
                    .getFamily();
}
