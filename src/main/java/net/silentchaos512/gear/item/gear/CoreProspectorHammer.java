package net.silentchaos512.gear.item.gear;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fmllegacy.network.NetworkDirection;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.config.Config;
import net.silentchaos512.gear.init.ModTags;
import net.silentchaos512.gear.network.Network;
import net.silentchaos512.gear.network.ProspectingResultPacket;
import net.silentchaos512.gear.util.GearHelper;

import java.util.HashSet;
import java.util.Set;

public class CoreProspectorHammer extends CorePickaxe {
    @Override
    public GearType getGearType() {
        return GearType.PROSPECTOR_HAMMER;
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
        Network.channel.sendTo(new ProspectingResultPacket(matches), ((ServerPlayer) player).connection.connection, NetworkDirection.PLAY_TO_CLIENT);

        GearHelper.attemptDamage(context.getItemInHand(), 2, player, context.getHand());
        player.getCooldowns().addCooldown(this, 20);

        return InteractionResult.SUCCESS;
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
                    if (state.is(ModTags.Blocks.PROSPECTOR_HAMMER_TARGETS)) {
                        matches.add(state);
                    }
                }
            }
        }

        return matches;
    }
}
