package net.silentchaos512.gear.item.gear;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.StatInstance;
import net.silentchaos512.gear.util.IAOETool;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class CoreHammer extends CorePickaxe implements IAOETool {
    public CoreHammer() {
        super(false);
    }

    @Override
    public GearType getGearType() {
        return GearType.HAMMER;
    }

    @Nonnull
    @Override
    public ToolType getAOEToolClass() {
        return ToolType.PICKAXE;
    }

    @Override
    public Optional<StatInstance> getBaseStatModifier(ItemStat stat) {
        if (stat == ItemStats.MELEE_DAMAGE)
            return Optional.of(StatInstance.makeBaseMod(4));
        if (stat == ItemStats.ATTACK_SPEED)
            return Optional.of(StatInstance.makeBaseMod(-3.2f));
        if (stat == ItemStats.REPAIR_EFFICIENCY)
            return Optional.of(StatInstance.makeBaseMod(0.5f));
        return Optional.empty();
    }

    @Override
    public Optional<StatInstance> getStatModifier(ItemStat stat) {
        if (stat == ItemStats.DURABILITY)
            return Optional.of(StatInstance.makeGearMod(1.0f));
        if (stat == ItemStats.ENCHANTABILITY)
            return Optional.of(StatInstance.makeGearMod(-0.5f));
        if (stat == ItemStats.HARVEST_SPEED)
            return Optional.of(StatInstance.makeGearMod(-0.5f));
        return Optional.empty();
    }

    @Nullable
    @Override
    public RayTraceResult rayTraceBlocks(World world, PlayerEntity player) {
        return rayTrace(world, player, RayTraceContext.FluidMode.NONE);
    }

    @Override
    public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, PlayerEntity player) {
        return IAOETool.BreakHandler.onBlockStartBreak(itemstack, pos, player);
    }
}
