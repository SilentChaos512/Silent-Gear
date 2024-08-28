package net.silentchaos512.gear.item.gear;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.silentchaos512.gear.api.item.BreakEventHandler;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.setup.GearItemSets;
import net.silentchaos512.gear.util.GearHelper;

import java.util.function.Supplier;

public class GearMacheteItem extends GearSwordItem implements BreakEventHandler {
    private static final int BREAK_RANGE = 2;

    public GearMacheteItem(Supplier<GearType> gearType) {
        super(gearType);
    }

    @Override
    public void onBlockBreakEvent(ItemStack stack, Player player, Level level, BlockPos pos, BlockState state) {
        // Allow clearing vegetation, just like sickles but with a smaller range
        if (!player.isCrouching()) {
            GearItemSets.SICKLE.gearItem().breakPlantsInRange(stack, pos, player, BREAK_RANGE);
        }
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
