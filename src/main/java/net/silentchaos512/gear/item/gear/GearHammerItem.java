package net.silentchaos512.gear.item.gear;

import com.google.common.collect.ImmutableSet;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.ToolAction;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.util.IAoeTool;

import javax.annotation.Nullable;
import java.util.Set;

public class GearHammerItem extends GearPickaxeItem implements IAoeTool {
    private static final Set<Material> HAMMER_EXTRA_MATERIALS = ImmutableSet.of(
            Material.STONE,
            Material.METAL,
            Material.HEAVY_METAL,
            Material.ICE,
            Material.ICE_SOLID,
            Material.GLASS
    );

    public GearHammerItem(GearType gearType) {
        super(gearType, HAMMER_EXTRA_MATERIALS);
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
        return getGearType().canPerformAction(toolAction);
    }

    @Nullable
    @Override
    public HitResult rayTraceBlocks(Level world, Player player) {
        return getPlayerPOVHitResult(world, player, ClipContext.Fluid.NONE);
    }

    @Override
    public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, Player player) {
        return IAoeTool.BreakHandler.onBlockStartBreak(itemstack, pos, player);
    }
}
