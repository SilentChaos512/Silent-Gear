package net.silentchaos512.gear.block.trees;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.silentchaos512.gear.SilentGear;

public class NetherwoodTree {
    /**
     * The tree feature, defined in {@code data/silentgear/worldgen/feature/netherwood.json}
     */
    public static final ResourceKey<Feature> KEY = ResourceKey.create(Registries.FEATURE, SilentGear.getId("netherwood"));

    public static final TreeGrower GROWER = new TreeGrower("netherwood", WeightedList.of(KEY), WeightedList.of(), WeightedList.of(), KEY);
}
