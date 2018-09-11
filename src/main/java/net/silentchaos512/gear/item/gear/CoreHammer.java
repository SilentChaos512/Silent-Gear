package net.silentchaos512.gear.item.gear;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.silentchaos512.gear.config.Config;
import net.silentchaos512.gear.config.ConfigOptionEquipment;
import net.silentchaos512.gear.util.IAOETool;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CoreHammer extends CorePickaxe implements IAOETool {
    @Nonnull
    @Override
    public ConfigOptionEquipment getConfig() {
        return Config.hammer;
    }

    @Override
    public String getGearClass() {
        return "hammer";
    }

    @Nonnull
    @Override
    public String getAOEToolClass() {
        return "pickaxe";
    }

    @Nullable
    @Override
    public RayTraceResult rayTraceBlocks(World world, EntityPlayer player) {
        return this.rayTrace(world, player, false);
    }

    @Override
    public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, EntityPlayer player) {
        return IAOETool.BreakHandler.onBlockStartBreak(itemstack, pos, player);
    }
}
