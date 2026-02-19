package net.silentchaos512.gear.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
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
import net.silentchaos512.gear.api.part.MaterialGrade;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.util.TextUtil;

public final class GradeCommand {
    private static final SuggestionProvider<CommandSourceStack> GRADE_SUGGESTIONS = (ctx, builder) ->
            SharedSuggestionProvider.suggest(
                    java.util.Arrays.stream(MaterialGrade.values()).map(Enum::name),
                    builder
            );

    private GradeCommand() {}

    /**
     * Creates the subcommand for use with the unified /sgear command.
     */
    public static LiteralArgumentBuilder<CommandSourceStack> createSubcommand() {
        return Commands.literal("grade")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .executes(ctx -> showHelp(ctx.getSource()))
                .then(Commands.literal("help")
                        .executes(ctx -> showHelp(ctx.getSource())))
                .then(buildListArgument())
                .then(buildSetArgument());
    }

    private static int showHelp(CommandSourceStack source) {
        source.sendSuccess(() -> TextUtil.translate("command", "help.grade.title")
                .withStyle(ChatFormatting.GOLD), false);
        source.sendSuccess(() -> Component.literal("  /sgear grade list").withStyle(ChatFormatting.YELLOW)
                .append(Component.literal(SGearCommand.HELP_INDENT).withStyle(ChatFormatting.GRAY))
                .append(TextUtil.translate("command", "help.grade.list").withStyle(ChatFormatting.GRAY)), false);
        source.sendSuccess(() -> Component.literal("  /sgear grade set <grade>").withStyle(ChatFormatting.YELLOW)
                .append(Component.literal(SGearCommand.HELP_INDENT).withStyle(ChatFormatting.GRAY))
                .append(TextUtil.translate("command", "help.grade.set").withStyle(ChatFormatting.GRAY)), false);
        return 1;
    }

    private static LiteralArgumentBuilder<CommandSourceStack> buildListArgument() {
        return Commands.literal("list")
                .executes(context -> runList(context.getSource()));
    }

    private static int runList(CommandSourceStack source) {
        var grades = MaterialGrade.values();

        // Header
        source.sendSuccess(() -> TextUtil.translate("command", "grade.list.header")
                .withStyle(ChatFormatting.GOLD), false);

        // List
        String listStr = java.util.Arrays.stream(grades)
                .map(Enum::name)
                .collect(java.util.stream.Collectors.joining(", "));
        source.sendSuccess(() -> Component.literal(listStr), false);

        // Total
        source.sendSuccess(() -> TextUtil.translate("command", "grade.list.total", grades.length)
                .withStyle(ChatFormatting.GRAY), false);

        return 1;
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("sgear_grade")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(buildListArgument())
                .then(buildSetArgument())
        );
    }

    private static LiteralArgumentBuilder<CommandSourceStack> buildSetArgument() {
        return Commands.literal("set")
                .then(Commands.argument("grade", StringArgumentType.string())
                        .suggests(GRADE_SUGGESTIONS)
                        .executes(context ->
                                runSet(context, StringArgumentType.getString(context, "grade"))
                        )
                );
    }

    private static int runSet(CommandContext<CommandSourceStack> context, String gradeStr) throws CommandSyntaxException {
        MaterialGrade grade = MaterialGrade.fromString(gradeStr);
        ServerPlayer player = context.getSource().getPlayerOrException();
        ItemStack stack = player.getMainHandItem();
        MaterialInstance material = MaterialInstance.from(stack);

        if (material != null) {
            grade.setGradeOnStack(stack);
            if (grade == MaterialGrade.NONE) {
                context.getSource().sendSuccess(() -> TextUtil.translate("command", "grade.remove.success", stack.getHoverName()), false);
            } else {
                context.getSource().sendSuccess(() -> TextUtil.translate("command", "grade.set.success", grade.name(), stack.getHoverName()), false);
            }
            return 1;
        } else {
            context.getSource().sendFailure(TextUtil.translate("command", "errors.notMaterial", stack.getHoverName()));
            return 0;
        }
    }
}
