package net.silentchaos512.gear.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.silentchaos512.gear.setup.SgTags;

public class FluffyBlock extends Block {
    static {
        NeoForge.EVENT_BUS.addListener(FluffyBlock::onGetBreakSpeed);
    }

    private final DyeColor dyeColor;

    public FluffyBlock(DyeColor color, BlockBehaviour.Properties properties) {
        super(properties);
        this.dyeColor = color;
    }

    public DyeColor getDyeColor() {
        return dyeColor;
    }

    @Override
    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, double fallDistance) {
        if (fallDistance < 2 || level.isClientSide()) return;

        // Count the number of fluffy blocks that are stacked up.
        int stackedBlocks = 0;
        while (stackedBlocks < 10 && level.getBlockState(pos).is(SgTags.Blocks.FLUFFY_BLOCKS)) {
            pos = pos.below();
            ++stackedBlocks;
        }

        // Reduce fall distance per stacked block
        double newDistance = fallDistance - Math.min(8 * stackedBlocks, fallDistance);
        entity.fallDistance = 0.0;
        entity.causeFallDamage(newDistance, 1f, level.damageSources().fall());
    }

    private static void onGetBreakSpeed(PlayerEvent.BreakSpeed event) {
        // Increase harvest speed when player is using shears
        if (event.getState().is(SgTags.Blocks.FLUFFY_BLOCKS)) {
            ItemStack mainHand = event.getEntity().getItemInHand(InteractionHand.MAIN_HAND);

            if (!mainHand.isEmpty() && mainHand.getItem() instanceof ShearsItem) {
                float efficiency = (float) event.getEntity().getAttributeValue(Attributes.MINING_EFFICIENCY);
                float speed = event.getNewSpeed() * 4 + efficiency;
                event.setNewSpeed(speed);
            }
        }
    }
}
