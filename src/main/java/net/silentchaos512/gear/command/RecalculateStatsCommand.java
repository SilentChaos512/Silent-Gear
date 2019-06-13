package net.silentchaos512.gear.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TranslationTextComponent;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.lib.util.PlayerUtils;

import java.util.Collection;

public final class RecalculateStatsCommand {
    private RecalculateStatsCommand() {}

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("sgear_recalculate")
                .requires(source -> source.hasPermissionLevel(2))
                .then(Commands.argument("targets", EntityArgument.players())
                        .executes(context ->
                                run(context, EntityArgument.getPlayers(context, "targets"))
                        )
                )
        );
    }

    private static int run(CommandContext<CommandSource> context, Collection<ServerPlayerEntity> players) {
        for (ServerPlayerEntity player : players) {
            for (ItemStack stack : PlayerUtils.getNonEmptyStacks(player)) {
                if (GearHelper.isGear(stack)) {
                    GearData.recalculateStats(stack, player);
                }
            }
            context.getSource().sendFeedback(new TranslationTextComponent("command.silentgear.recalculate", player.getScoreboardName()), true);
        }
        return 1;
    }
}
