package net.silentchaos512.gear.item.gear;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkDirection;
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
    public ActionResultType useOn(ItemUseContext context) {
        int range = Config.Common.prospectorHammerRange.get();
        PlayerEntity player = context.getPlayer();
        Direction face = context.getClickedFace();
        if (range <= 0 || player == null || face.getAxis() == Direction.Axis.Y) {
            return GearHelper.onItemUse(context);
        }

        if (context.getLevel().isClientSide || !(player instanceof ServerPlayerEntity)) {
            return ActionResultType.SUCCESS;
        }

        Set<BlockState> matches = getTargetedBlocks(context, range, face);

        // List the ores found in chat, if any
        Network.channel.sendTo(new ProspectingResultPacket(matches), ((ServerPlayerEntity) player).connection.connection, NetworkDirection.PLAY_TO_CLIENT);

        GearHelper.attemptDamage(context.getItemInHand(), 2, player, context.getHand());
        player.getCooldowns().addCooldown(this, 20);

        return ActionResultType.SUCCESS;
    }

    public Set<BlockState> getTargetedBlocks(ItemUseContext context, int range, Direction face) {
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
