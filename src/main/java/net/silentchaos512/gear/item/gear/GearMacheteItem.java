package net.silentchaos512.gear.item.gear;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.setup.SgItems;
import net.silentchaos512.gear.util.GearHelper;

public class GearMacheteItem extends GearSwordItem {
    private static final int BREAK_RANGE = 2;

    public GearMacheteItem(GearType gearType) {
        super(gearType);
    }

    @Override
    public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, Player player) {
        // Allow clearing vegetation, just like sickles but with a smaller range
        if (!player.isCrouching())
            return SgItems.SICKLE.get().onSickleStartBreak(itemstack, pos, player, BREAK_RANGE);
        return super.onBlockStartBreak(itemstack, pos, player);
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        return GearHelper.isCorrectToolForDrops(stack, state, BlockTags.MINEABLE_WITH_AXE);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        float axeSpeed = GearHelper.getDestroySpeed(stack, state);
        float speed = Math.max(axeSpeed, super.getDestroySpeed(stack, state));
        // Slower on materials normally harvested with axes
        if (GearHelper.isCorrectToolForDrops(stack, state, BlockTags.MINEABLE_WITH_AXE))
            return speed * 0.4f;
        return speed;
    }
}
