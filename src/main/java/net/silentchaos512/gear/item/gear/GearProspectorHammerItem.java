package net.silentchaos512.gear.item.gear;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ToolAction;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.config.Config;
import net.silentchaos512.gear.setup.SgTags;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.gear.util.TextUtil;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class GearProspectorHammerItem extends GearPickaxeItem {
    public GearProspectorHammerItem(GearType gearType) {
        super(gearType);
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
        // TODO: Add a prospecting action?
        return getGearType().canPerformAction(toolAction);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        int range = Config.Common.prospectorHammerRange.get();
        Player player = context.getPlayer();
        Direction face = context.getClickedFace();
        if (range <= 0 || player == null || face.getAxis() == Direction.Axis.Y) {
            return GearHelper.onItemUse(context);
        }

        if (context.getLevel().isClientSide || !(player instanceof ServerPlayer)) {
            return InteractionResult.SUCCESS;
        }

        Set<BlockState> matches = getTargetedBlocks(context, range, face);

        // List the ores found in chat, if any
        player.sendSystemMessage(listFoundBlocks(matches));

        GearHelper.attemptDamage(context.getItemInHand(), 2, player, context.getHand());
        player.getCooldowns().addCooldown(this, 20);

        return InteractionResult.SUCCESS;
    }

    private static Component listFoundBlocks(Collection<BlockState> blocksFound) {
        return blocksFound.stream()
                .map(state -> state.getBlock().getName())
                .reduce((t1, t2) -> t1.append(", ").append(t2))
                .orElseGet(() -> TextUtil.translate("item", "prospector_hammer.no_finds"));
    }

    public Set<BlockState> getTargetedBlocks(UseOnContext context, int range, Direction face) {
        Set<BlockState> matches = new HashSet<>();

        Direction direction = face.getOpposite();
        for (int i = 0; i < range; ++i) {
            for (int j = -1; j <= 1; ++j) {
                for (int k = -1; k <= 1; ++k) {
                    BlockPos pos = context.getClickedPos()
                            .relative(direction, i)
                            .relative(direction.getCounterClockWise(), j)
                            .relative(Direction.DOWN, k);

                    BlockState state = context.getLevel().getBlockState(pos);
                    if (state.is(SgTags.Blocks.PROSPECTOR_HAMMER_TARGETS)) {
                        matches.add(state);
                    }
                }
            }
        }

        return matches;
    }
}
