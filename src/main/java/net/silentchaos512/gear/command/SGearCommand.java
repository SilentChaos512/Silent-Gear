package net.silentchaos512.gear.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.silentchaos512.gear.util.TextUtil;

/**
 * Unified /sgear command that provides CLI-style access to all Silent Gear commands.
 * Provides a help system that shows available subcommands filtered by permission level.
 */
public final class SGearCommand {
    /** Indentation used in help text for command descriptions */
    public static final String HELP_INDENT = "\n      ";

    private SGearCommand() {}

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("sgear")
                .executes(ctx -> showHelp(ctx.getSource()))
                .then(Commands.literal("help")
                        .executes(ctx -> showHelp(ctx.getSource())))
                .then(GradeCommand.createSubcommand())
                .then(StarchargeCommand.createSubcommand())
                .then(SetDamageCommand.createSubcommand())
                .then(MaterialsCommand.createSubcommand())
                .then(PartsCommand.createSubcommand())
                .then(PropertiesCommand.createSubcommand())
                .then(TraitsCommand.createSubcommand())
                .then(RandomGearCommand.createSubcommand());

        dispatcher.register(builder);
    }

    private static int showHelp(CommandSourceStack source) {
        source.sendSuccess(() -> TextUtil.translate("command", "help.title")
                .withStyle(ChatFormatting.GOLD), false);

        // Operator commands (level 2)
        if (source.hasPermission(2)) {
            source.sendSuccess(() -> TextUtil.translate("command", "help.operatorSection")
                    .withStyle(ChatFormatting.DARK_AQUA), false);
            source.sendSuccess(() -> helpLine("grade set <grade>", "help.grade"), false);
            source.sendSuccess(() -> helpLine("starcharged set <level>", "help.starcharged"), false);
            source.sendSuccess(() -> helpLine("damage <amount | max>", "help.damage"), false);
            source.sendSuccess(() -> helpLine("properties <info | recalculate>", "help.properties"), false);
            source.sendSuccess(() -> helpLine("random <players> <item> [tier]", "help.random"), false);
        }

        // Public commands
        source.sendSuccess(() -> TextUtil.translate("command", "help.publicSection")
                .withStyle(ChatFormatting.DARK_GREEN), false);
        source.sendSuccess(() -> helpLine("mats <list | describe | dump>", "help.mats"), false);
        source.sendSuccess(() -> helpLine("parts <list | describe | dump>", "help.parts"), false);
        source.sendSuccess(() -> helpLine("traits <list | describe | dump_md>", "help.traits"), false);

        source.sendSuccess(() -> TextUtil.translate("command", "help.footer")
                .withStyle(ChatFormatting.GRAY), false);

        return 1;
    }

    private static Component helpLine(String command, String descriptionKey) {
        return Component.literal("  /sgear " + command)
                .withStyle(ChatFormatting.YELLOW)
                .append(Component.literal(HELP_INDENT).withStyle(ChatFormatting.GRAY))
                .append(TextUtil.translate("command", descriptionKey)
                        .withStyle(ChatFormatting.GRAY));
    }
}
