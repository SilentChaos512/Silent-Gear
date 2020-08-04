package net.silentchaos512.gear.item.gear;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ToolType;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.util.GearHelper;

import java.util.Collections;
import java.util.Set;

public class CorePaxel extends CorePickaxe {
    private static final ImmutableSet<ToolType> TOOL_CLASSES = ImmutableSet.of(
            ToolType.PICKAXE,
            ToolType.SHOVEL,
            ToolType.AXE
    );
    private static final Set<Material> EXTRA_EFFECTIVE_MATERIALS = ImmutableSet.of(
            Material.MISCELLANEOUS,
            Material.GLASS,
            Material.PISTON,
            Material.REDSTONE_LIGHT,
            Material.BAMBOO,
            Material.LEAVES,
            Material.PLANTS,
            Material.TALL_PLANTS,
            Material.WOOD
    );

    public CorePaxel() {
        super(EXTRA_EFFECTIVE_MATERIALS);
    }

    @Override
    public GearType getGearType() {
        return GearType.PAXEL;
    }

    @Override
    public Set<ToolType> getToolTypes(ItemStack stack) {
        return !GearHelper.isBroken(stack) ? TOOL_CLASSES : Collections.emptySet();
    }
}
