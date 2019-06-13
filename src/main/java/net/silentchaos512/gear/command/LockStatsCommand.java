package net.silentchaos512.gear.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TranslationTextComponent;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;

public final class LockStatsCommand {
    private LockStatsCommand() {}

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("sgear_lock_stats")
                .requires(source -> source.hasPermissionLevel(2))
                .executes(
                        LockStatsCommand::run
                )
        );
    }

    private static int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity playerMP = context.getSource().asPlayer();
        ItemStack stack = playerMP.getHeldItemMainhand();
        if (GearHelper.isGear(stack)) {
            boolean locked = !GearData.hasLockedStats(stack);
            GearData.setLockedStats(stack, locked);
            String translationKey = "command.silentgear.lock_stats." + (locked ? "locked" : "unlocked");
            context.getSource().sendFeedback(new TranslationTextComponent(translationKey, stack.getDisplayName()), true);
        } else {
            context.getSource().sendErrorMessage(new TranslationTextComponent("command.silentgear.lock_stats.invalid"));
        }
        return 1;
    }
}
