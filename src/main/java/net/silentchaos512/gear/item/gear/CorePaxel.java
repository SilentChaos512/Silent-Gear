package net.silentchaos512.gear.item.gear;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ToolType;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.api.stats.StatInstance;
import net.silentchaos512.gear.util.GearHelper;

import java.util.Collections;
import java.util.Optional;
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
    public Optional<StatInstance> getBaseStatModifier(ItemStat stat) {
        if (stat == ItemStats.MELEE_DAMAGE)
            return Optional.of(StatInstance.makeBaseMod(2));
        if (stat == ItemStats.ATTACK_SPEED)
            return Optional.of(StatInstance.makeBaseMod(-3.0f));
        if (stat == ItemStats.REPAIR_EFFICIENCY)
            return Optional.of(StatInstance.makeBaseMod(0.8f));
        return Optional.empty();
    }

    @Override
    public Optional<StatInstance> getStatModifier(ItemStat stat) {
        if (stat == ItemStats.DURABILITY)
            return Optional.of(StatInstance.makeGearMod(0.25f));
        if (stat == ItemStats.ENCHANTABILITY)
            return Optional.of(StatInstance.makeGearMod(-0.3f));
        if (stat == ItemStats.HARVEST_SPEED)
            return Optional.of(StatInstance.makeGearMod(-0.2f));
        return Optional.empty();
    }

    @Override
    public Set<ToolType> getToolTypes(ItemStack stack) {
        return !GearHelper.isBroken(stack) ? TOOL_CLASSES : Collections.emptySet();
    }
}
