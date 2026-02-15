package net.silentchaos512.gear.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.setup.SgTags;
import net.silentchaos512.gear.setup.gear.MaterialModifiers;
import net.silentchaos512.gear.util.TextUtil;

public final class StarchargeCommand {
    private static final SuggestionProvider<CommandSourceStack> LEVEL_SUGGESTIONS = (ctx, builder) -> {
        int maxLevel = SgTags.Items.STARLIGHT_CHARGER_TIERS.size();
        return SharedSuggestionProvider.suggest(
                java.util.stream.IntStream.rangeClosed(0, maxLevel).mapToObj(String::valueOf),
                builder
        );
    };

    private StarchargeCommand() {}

    /**
     * Creates the subcommand for use with the unified /sgear command.
     */
    public static LiteralArgumentBuilder<CommandSourceStack> createSubcommand() {
        return Commands.literal("starcharged")
                .requires(source -> source.hasPermission(2))
                .executes(ctx -> showHelp(ctx.getSource()))
                .then(Commands.literal("help")
                        .executes(ctx -> showHelp(ctx.getSource())))
                .then(buildListArgument())
                .then(buildSetArgument());
    }

    private static int showHelp(CommandSourceStack source) {
        int maxLevel = SgTags.Items.STARLIGHT_CHARGER_TIERS.size();
        source.sendSuccess(() -> TextUtil.translate("command", "help.starcharged.title")
                .withStyle(ChatFormatting.GOLD), false);
        source.sendSuccess(() -> Component.literal("  /sgear starcharged list").withStyle(ChatFormatting.YELLOW)
                .append(Component.literal(SGearCommand.HELP_INDENT).withStyle(ChatFormatting.GRAY))
                .append(TextUtil.translate("command", "help.starcharged.list").withStyle(ChatFormatting.GRAY)), false);
        source.sendSuccess(() -> Component.literal("  /sgear starcharged set <level>").withStyle(ChatFormatting.YELLOW)
                .append(Component.literal(SGearCommand.HELP_INDENT).withStyle(ChatFormatting.GRAY))
                .append(TextUtil.translate("command", "help.starcharged.set", maxLevel).withStyle(ChatFormatting.GRAY)), false);
        return 1;
    }

    private static LiteralArgumentBuilder<CommandSourceStack> buildListArgument() {
        return Commands.literal("list")
                .executes(context -> runList(context.getSource()));
    }

    private static int runList(CommandSourceStack source) {
        int maxLevel = SgTags.Items.STARLIGHT_CHARGER_TIERS.size();
        int count = maxLevel + 1; // 0 through maxLevel

        // Header
        source.sendSuccess(() -> TextUtil.translate("command", "starcharged.list.header")
                .withStyle(ChatFormatting.GOLD), false);

        // List
        String listStr = java.util.stream.IntStream.rangeClosed(0, maxLevel)
                .mapToObj(String::valueOf)
                .collect(java.util.stream.Collectors.joining(", "));
        source.sendSuccess(() -> Component.literal(listStr), false);

        // Total
        source.sendSuccess(() -> TextUtil.translate("command", "starcharged.list.total", count)
                .withStyle(ChatFormatting.GRAY), false);

        return 1;
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("sgear_starcharged")
                .requires(source -> source.hasPermission(2))
                .then(buildListArgument())
                .then(buildSetArgument())
        );
    }

    private static LiteralArgumentBuilder<CommandSourceStack> buildSetArgument() {
        int maxLevel = SgTags.Items.STARLIGHT_CHARGER_TIERS.size();
        return Commands.literal("set")
                .then(Commands.argument("level", IntegerArgumentType.integer(0, maxLevel))
                        .suggests(LEVEL_SUGGESTIONS)
                        .executes(context ->
                                runSet(context, IntegerArgumentType.getInteger(context, "level"))
                        )
                );
    }

    private static int runSet(CommandContext<CommandSourceStack> context, int level) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        ItemStack stack = player.getMainHandItem();
        MaterialInstance material = MaterialInstance.from(stack);

        if (material != null) {
            var modifierType = MaterialModifiers.STARCHARGED.get();
            if (level == 0) {
                modifierType.removeModifier(stack);
                context.getSource().sendSuccess(() -> TextUtil.translate("command", "starcharged.remove.success", stack.getHoverName()), false);
            } else {
                var modifier = modifierType.create(level);
                modifierType.addModifier(modifier, stack);
                context.getSource().sendSuccess(() -> TextUtil.translate("command", "starcharged.set.success", level, stack.getHoverName()), false);
            }
            return 1;
        } else {
            context.getSource().sendFailure(TextUtil.translate("command", "errors.notMaterial", stack.getHoverName()));
            return 0;
        }
    }
}
