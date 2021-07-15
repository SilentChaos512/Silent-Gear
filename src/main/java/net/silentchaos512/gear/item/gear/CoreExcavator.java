package net.silentchaos512.gear.item.gear;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.util.IAoeTool;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CoreExcavator extends CoreShovel implements IAoeTool {
    @Override
    public GearType getGearType() {
        return GearType.EXCAVATOR;
    }

    @Nonnull
    @Override
    public ToolType getAoeToolType() {
        return ToolType.SHOVEL;
    }

    @Nullable
    @Override
    public RayTraceResult rayTraceBlocks(World world, PlayerEntity player) {
        return getPlayerPOVHitResult(world, player, RayTraceContext.FluidMode.NONE);
    }

    @Override
    public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, PlayerEntity player) {
        return IAoeTool.BreakHandler.onBlockStartBreak(itemstack, pos, player);
    }
}
