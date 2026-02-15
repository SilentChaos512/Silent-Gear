package net.silentchaos512.gear.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.util.Mth;
import net.minecraft.network.chat.Component;
import net.silentchaos512.gear.api.item.GearItem;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.gear.util.TextUtil;

import java.util.ArrayList;
import java.util.List;

public final class SetDamageCommand {
    private static final SuggestionProvider<CommandSourceStack> DAMAGE_SUGGESTIONS = (ctx, builder) -> {
        List<String> suggestions = new ArrayList<>();

        try {
            ServerPlayer player = ctx.getSource().getPlayerOrException();
            ItemStack stack = player.getMainHandItem();
            if (stack.isDamageableItem()) {
                int currentDamage = stack.getDamageValue();
                int maxDamage = getMaxDamage(stack);

                suggestions.add("0");

                if (maxDamage > 0) {
                    int quarter = (int) Math.ceil(maxDamage * 0.25);
                    int half = (int) Math.ceil(maxDamage * 0.5);
                    int threeQuarters = (int) Math.ceil(maxDamage * 0.75);

                    if (quarter > 0 && quarter != currentDamage) {
                        suggestions.add(String.valueOf(quarter));
                    }
                    if (half > 0 && half != currentDamage && half != quarter) {
                        suggestions.add(String.valueOf(half));
                    }
                    if (threeQuarters > 0 && threeQuarters != currentDamage && threeQuarters != half) {
                        suggestions.add(String.valueOf(threeQuarters));
                    }
                    if (maxDamage != currentDamage && maxDamage != threeQuarters) {
                        suggestions.add(String.valueOf(maxDamage));
                    }
                }

                if (currentDamage > 0 && !suggestions.contains(String.valueOf(currentDamage))) {
                    suggestions.add(String.valueOf(currentDamage));
                }
            }
        } catch (CommandSyntaxException ignored) {
            // Not a player, no suggestions
        }

        return SharedSuggestionProvider.suggest(suggestions, builder);
    };

    private SetDamageCommand() {}

    /**
     * Creates the subcommand for use with the unified /sgear command.
     */
    public static LiteralArgumentBuilder<CommandSourceStack> createSubcommand() {
        return Commands.literal("damage")
                .requires(source -> source.hasPermission(2))
                .executes(ctx -> showHelp(ctx.getSource()))
                .then(Commands.literal("help")
                        .executes(ctx -> showHelp(ctx.getSource())))
                .then(buildAmountArgument())
                .then(buildMaxArgument());
    }

    private static int showHelp(CommandSourceStack source) {
        source.sendSuccess(() -> TextUtil.translate("command", "help.damage.title")
                .withStyle(ChatFormatting.GOLD), false);
        source.sendSuccess(() -> Component.literal("  /sgear damage <amount>").withStyle(ChatFormatting.YELLOW)
                .append(Component.literal(SGearCommand.HELP_INDENT).withStyle(ChatFormatting.GRAY))
                .append(TextUtil.translate("command", "help.damage.amount").withStyle(ChatFormatting.GRAY)), false);
        source.sendSuccess(() -> Component.literal("  /sgear damage max").withStyle(ChatFormatting.YELLOW)
                .append(Component.literal(SGearCommand.HELP_INDENT).withStyle(ChatFormatting.GRAY))
                .append(TextUtil.translate("command", "help.damage.max").withStyle(ChatFormatting.GRAY)), false);
        return 1;
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("set_damage")
                .requires(source -> source.hasPermission(2))
                .then(buildAmountArgument())
                .then(buildMaxArgument())
        );
    }

    private static ArgumentBuilder<CommandSourceStack, ?> buildAmountArgument() {
        return Commands.argument("amount", IntegerArgumentType.integer(0))
                .suggests(DAMAGE_SUGGESTIONS)
                .executes(context ->
                        run(context, IntegerArgumentType.getInteger(context, "amount"))
                );
    }

    private static LiteralArgumentBuilder<CommandSourceStack> buildMaxArgument() {
        return Commands.literal("max")
                .executes(context -> run(context, -1));
    }

    private static int run(CommandContext<CommandSourceStack> context, int amount) throws CommandSyntaxException {
        ServerPlayer playerMP = context.getSource().getPlayerOrException();
        ItemStack stack = playerMP.getMainHandItem();

        if (stack.isDamageableItem()) {
            int maxDamage = getMaxDamage(stack);
            // amount of -1 indicates "max" value
            boolean isMax = amount < 0;
            int correctedAmount = isMax ? maxDamage : amount;
            int clamped = Mth.clamp(correctedAmount, 0, maxDamage);

            stack.setDamageValue(clamped);

            if (stack.getItem() instanceof GearItem) {
                GearData.recalculateGearData(stack, playerMP);
            }

            int remainingDurability = stack.getMaxDamage() - clamped;
            int maxDurability = stack.getMaxDamage();

            if (isMax) {
                context.getSource().sendSuccess(() -> TextUtil.translate("command", "set_damage.max.success",
                        stack.getHoverName(), remainingDurability, maxDurability), false);
            } else {
                context.getSource().sendSuccess(() -> TextUtil.translate("command", "set_damage.success",
                        clamped, stack.getHoverName(), remainingDurability, maxDurability), false);
            }

            return 1;
        } else {
            context.getSource().sendFailure(TextUtil.translate("command", "errors.notDamageable", stack.getHoverName()));
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
