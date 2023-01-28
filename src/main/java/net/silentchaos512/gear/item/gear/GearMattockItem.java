package net.silentchaos512.gear.item.gear;

import com.google.common.collect.ImmutableSet;
import net.minecraft.world.level.material.Material;
import net.silentchaos512.gear.api.item.GearType;

import java.util.Set;

public class GearMattockItem extends GearHoeItem {
    private static final Set<Material> EFFECTIVE_MATERIALS = ImmutableSet.of(
            Material.LEAVES,
            Material.PLANT,
            Material.REPLACEABLE_PLANT,
            Material.GRASS,
            Material.DIRT,
            Material.CLAY,
            Material.SAND,
            Material.TOP_SNOW,
            Material.VEGETABLE,
            Material.WOOD
    );

    public GearMattockItem() {
        super(GearType.MATTOCK, EFFECTIVE_MATERIALS);
    }
}
