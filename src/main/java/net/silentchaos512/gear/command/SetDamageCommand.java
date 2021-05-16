package net.silentchaos512.gear.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.gear.util.TextUtil;

public final class SetDamageCommand {
    private SetDamageCommand() {}

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("set_damage")
                .requires(source -> source.hasPermissionLevel(2))
                .then(
                        Commands.argument("amount", IntegerArgumentType.integer())
                                .executes(context ->
                                        run(context, IntegerArgumentType.getInteger(context, "amount"))
                                )
                )
                .then(
                        Commands.literal("max")
                                .executes(context ->
                                        run(context, -1)
                                )
                )
        );
    }

    private static int run(CommandContext<CommandSource> context, int amount) throws CommandSyntaxException {
        ServerPlayerEntity playerMP = context.getSource().asPlayer();
        ItemStack stack = playerMP.getHeldItemMainhand();

        if (stack.isDamageable()) {
            // amount of -1 indicates "max" value
            int correctedAmount = amount < 0 ? getMaxDamage(stack) : amount;
            int clamped = MathHelper.clamp(correctedAmount, 0, getMaxDamage(stack));

            stack.setDamage(clamped);

            if (stack.getItem() instanceof ICoreItem) {
                GearData.recalculateStats(stack, playerMP);
            }

            return 1;
        } else {
            ITextComponent msg = TextUtil.translate("command", "set_damage.notDamageable", stack.getDisplayName());
            context.getSource().sendErrorMessage(msg);
            return 0;
        }
    }

    private static int getMaxDamage(ItemStack stack) {
        if (GearHelper.isGear(stack)) {
            return stack.getMaxDamage() - 1;
        }
        return stack.getMaxDamage();
    }
}
