package net.silentchaos512.gear.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.api.item.GearItem;
import net.silentchaos512.gear.api.part.PartList;
import net.silentchaos512.gear.api.property.GearProperty;
import net.silentchaos512.gear.api.property.GearPropertyMap;
import net.silentchaos512.gear.api.property.GearPropertyValue;
import net.silentchaos512.gear.api.util.PropertyKey;
import net.silentchaos512.gear.client.tooltip.FormatColorScheme;
import net.silentchaos512.gear.gear.part.PartInstance;
import net.silentchaos512.gear.setup.SgRegistries;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.gear.util.TextUtil;

import java.util.Collection;

public final class PropertiesCommand {
    private PropertiesCommand() {
    }

    /**
     * Creates the subcommand for use with the unified /sgear command.
     */
    public static ArgumentBuilder<CommandSourceStack, ?> createSubcommand() {
        return Commands.literal("properties")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .executes(ctx -> showHelp(ctx.getSource()))
                .then(Commands.literal("help")
                        .executes(ctx -> showHelp(ctx.getSource())))
                .then(buildInfoArgument())
                .then(buildRecalculateArgument());
    }

    private static int showHelp(CommandSourceStack source) {
        source.sendSuccess(() -> TextUtil.translate("command", "help.properties.title")
                .withStyle(ChatFormatting.GOLD), false);
        source.sendSuccess(() -> Component.literal("  /sgear properties info [player]").withStyle(ChatFormatting.YELLOW)
                .append(Component.literal(SGearCommand.HELP_INDENT).withStyle(ChatFormatting.GRAY))
                .append(TextUtil.translate("command", "help.properties.info").withStyle(ChatFormatting.GRAY)), false);
        source.sendSuccess(() -> Component.literal("  /sgear properties recalculate <players>").withStyle(ChatFormatting.YELLOW)
                .append(Component.literal(SGearCommand.HELP_INDENT).withStyle(ChatFormatting.GRAY))
                .append(TextUtil.translate("command", "help.properties.recalculate").withStyle(ChatFormatting.GRAY)), false);
        return 1;
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("sgear_properties")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(buildInfoArgument())
                .then(buildRecalculateArgument())
        );
    }

    private static LiteralArgumentBuilder<CommandSourceStack> buildInfoArgument() {
        return Commands.literal("info")
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(ctx -> runInfo(ctx, EntityArgument.getPlayer(ctx, "player")))
                )
                .executes(ctx -> runInfo(ctx, ctx.getSource().getPlayerOrException()));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> buildRecalculateArgument() {
        return Commands.literal("recalculate")
                .then(Commands.argument("players", EntityArgument.players())
                        .executes(ctx -> runRecalculate(ctx, EntityArgument.getPlayers(ctx, "players")))
                );
    }

    private static int runInfo(CommandContext<CommandSourceStack> context, ServerPlayer player) {
        ItemStack stack = player.getMainHandItem();

        if (!GearHelper.isGear(stack)) {
            context.getSource().sendFailure(TextUtil.translate("command", "invalidItemType", stack.getHoverName()));
            return 0;
        }

        context.getSource().sendSuccess(
                () -> TextUtil.translate("command", "stats.info.header", player.getName(), stack.getHoverName())
                        .withStyle(ChatFormatting.BOLD),
                true
        );

        GearItem item = (GearItem) stack.getItem();
        PartList parts = GearData.getConstruction(stack).parts();
        GearPropertyMap properties = parts.getPropertyModifiersFromParts(item.getGearType());

        for (var property : SgRegistries.GEAR_PROPERTY) {
            var key = PropertyKey.of(property, item.getGearType());
            Collection<GearPropertyValue<?>> mods = properties.get(key);

            if (!mods.isEmpty()) {
                Component name = TextUtil.withColor(property.getDisplayName(), property.getGroup().getColor());
                Component modsText = GearPropertyMap.formatTextUnchecked(mods, property, FormatColorScheme.NO_COLORS);
                Component valueText = TextUtil.withColor(
                        property.formatModifiersWithColorUnchecked(mods, GearProperty.FormatContext.GEAR, FormatColorScheme.NO_COLORS),
                        ChatFormatting.YELLOW
                );

                context.getSource().sendSuccess(
                        () -> TextUtil.translate("command", "stats.info.format", name, modsText, valueText),
                        true
                );

                for (PartInstance part : parts) {
                    var partMods = part.getPropertyModifiers(part.getType(), key);
                    if (!partMods.isEmpty()) {
                        Component partName = part.getDisplayName(part.getType());
                        //noinspection unchecked
                        Component partModsText = GearPropertyMap.formatTextUnchecked(partMods, property, FormatColorScheme.NO_COLORS);

                        context.getSource().sendSuccess(
                                () -> TextUtil.translate("command", "stats.info.formatPart", partName, partModsText),
                                true
                        );
                    }
                }
            }
        }

        return 1;
    }

    private static int runRecalculate(CommandContext<CommandSourceStack> context, Collection<ServerPlayer> players) {
        for (ServerPlayer player : players) {
            for (ItemStack stack : player.getInventory()) {
                if (GearHelper.isGear(stack)) {
                    GearData.recalculateGearData(stack, player);
                }
            }
            context.getSource().sendSuccess(() -> Component.translatable("command.silentgear.recalculate", player.getScoreboardName()), true);
        }
        return 1;
    }
}
