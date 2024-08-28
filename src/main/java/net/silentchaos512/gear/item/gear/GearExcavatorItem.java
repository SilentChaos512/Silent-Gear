package net.silentchaos512.gear.item.gear;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.util.IAoeTool;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class GearExcavatorItem extends GearShovelItem implements IAoeTool {
    public GearExcavatorItem(Supplier<GearType> gearType) {
        super(gearType);
    }

    @Nullable
    @Override
    public HitResult rayTraceBlocks(Level world, Player player) {
        return getPlayerPOVHitResult(world, player, ClipContext.Fluid.NONE);
    }
}
