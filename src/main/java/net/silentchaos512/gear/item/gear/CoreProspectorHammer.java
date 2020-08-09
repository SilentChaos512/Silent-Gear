package net.silentchaos512.gear.item.gear;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.config.Config;
import net.silentchaos512.gear.init.ModTags;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.gear.util.TextUtil;

import java.util.HashSet;
import java.util.Set;

public class CoreProspectorHammer extends CorePickaxe {
    @Override
    public GearType getGearType() {
        return GearType.PROSPECTOR_HAMMER;
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        int range = Config.Common.prospectorHammerRange.get();
        PlayerEntity player = context.getPlayer();
        Direction face = context.getFace();
        if (range <= 0 || player == null || face.getAxis() == Direction.Axis.Y) {
            return GearHelper.onItemUse(context);
        }

        if (context.getWorld().isRemote) {
            return ActionResultType.SUCCESS;
        }

        Set<BlockState> matches = new HashSet<>();

        Direction direction = face.getOpposite();
        for (int i = 0; i < range; ++i) {
            for (int j = -1; j <= 1; ++j) {
                for (int k = -1; k <= 1; ++k) {
                    BlockPos pos = context.getPos()
                            .offset(direction, i)
                            .offset(direction.rotateYCCW(), j)
                            .offset(Direction.DOWN, k);

                    BlockState state = context.getWorld().getBlockState(pos);
                    if (state.isIn(ModTags.Blocks.PROSPECTOR_HAMMER_TARGETS)) {
                        matches.add(state);
                    }
                }
            }
        }

        // List the ores found in chat, if any
        ITextComponent text = matches.stream()
                .map(state -> state.getBlock().getTranslatedName())
                .reduce((t1, t2) -> t1.func_240702_b_(", ").func_230529_a_(t2))
                .orElseGet(() -> TextUtil.translate("item", "prospector_hammer.no_finds"));
        player.sendMessage(!matches.isEmpty() ? TextUtil.translate("item", "prospector_hammer.finds", text) : text, Util.DUMMY_UUID);

        GearHelper.attemptDamage(context.getItem(), 2, player, context.getHand());
        player.getCooldownTracker().setCooldown(this, 20);

        return ActionResultType.SUCCESS;
    }
}
