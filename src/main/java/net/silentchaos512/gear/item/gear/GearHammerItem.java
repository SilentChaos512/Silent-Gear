package net.silentchaos512.gear.item.gear;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.common.ItemAbility;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.util.IAoeTool;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class GearHammerItem extends GearPickaxeItem implements IAoeTool {
    public GearHammerItem(Supplier<GearType> gearType) {
        super(gearType);
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ItemAbility itemAbility) {
        return getGearType().canPerformAction(itemAbility);
    }

    @Nullable
    @Override
    public HitResult rayTraceBlocks(Level world, Player player) {
        return getPlayerPOVHitResult(world, player, ClipContext.Fluid.NONE);
    }
}
