package net.silentchaos512.gear.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.ChatFormatting;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.api.item.GearItem;
import net.silentchaos512.gear.util.TextUtil;
import net.silentchaos512.gear.api.part.GearPart;
import net.silentchaos512.gear.api.part.PartList;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.property.GearPropertyMap;
import net.silentchaos512.gear.api.util.PartGearKey;
import net.silentchaos512.gear.api.util.PropertyKey;
import net.silentchaos512.gear.client.tooltip.FormatColorScheme;
import net.silentchaos512.gear.gear.part.PartInstance;
import net.silentchaos512.gear.setup.SgRegistries;
import net.silentchaos512.gear.setup.gear.GearTypes;
import net.silentchaos512.gear.util.GearData;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class PartsCommand {
    private static final SuggestionProvider<CommandSourceStack> partIdSuggestions = (ctx, builder) ->
            SharedSuggestionProvider.suggestResource(SgRegistries.PART.keySet(), builder);
    private static final SuggestionProvider<CommandSourceStack> partInGearSuggestions = (ctx, builder) -> {
        PartList parts = GearData.getConstruction(getGear(ctx)).parts();
        return SharedSuggestionProvider.suggestResource(
                parts.stream().map(PartInstance::getId),
                builder
        );
    };
    private static final Pattern FORMAT_CODES = Pattern.compile("\u00a7[0-9a-z]");

    private PartsCommand() {
    }

    /**
     * Creates the subcommand for use with the unified /sgear command.
     */
    public static ArgumentBuilder<CommandSourceStack, ?> createSubcommand() {
        return Commands.literal("parts")
                .executes(ctx -> showHelp(ctx.getSource()))
                .then(Commands.literal("help")
                        .executes(ctx -> showHelp(ctx.getSource())))
                .then(buildListArgument())
                .then(buildDescribeArgument())
                .then(buildDumpArgument());
    }

    private static int showHelp(CommandSourceStack source) {
        source.sendSuccess(() -> TextUtil.translate("command", "help.parts.title")
                .withStyle(ChatFormatting.GOLD), false);
        source.sendSuccess(() -> Component.literal("  /sgear parts list").withStyle(ChatFormatting.YELLOW)
                .append(Component.literal(SGearCommand.HELP_INDENT).withStyle(ChatFormatting.GRAY))
                .append(TextUtil.translate("command", "help.parts.list").withStyle(ChatFormatting.GRAY)), false);
        source.sendSuccess(() -> Component.literal("  /sgear parts describe <partID>").withStyle(ChatFormatting.YELLOW)
                .append(Component.literal(SGearCommand.HELP_INDENT).withStyle(ChatFormatting.GRAY))
                .append(TextUtil.translate("command", "help.parts.describe").withStyle(ChatFormatting.GRAY)), false);
        source.sendSuccess(() -> Component.literal("  /sgear parts dump").withStyle(ChatFormatting.YELLOW)
                .append(Component.literal(SGearCommand.HELP_INDENT).withStyle(ChatFormatting.GRAY))
                .append(TextUtil.translate("command", "help.parts.dump").withStyle(ChatFormatting.GRAY)), false);
        return 1;
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("sgear_parts")
                .then(buildListArgument())
                .then(buildDescribeArgument())
                .then(buildDumpArgument())
        );
    }

    private static LiteralArgumentBuilder<CommandSourceStack> buildListArgument() {
        return Commands.literal("list")
                .executes(PartsCommand::runList);
    }

    private static LiteralArgumentBuilder<CommandSourceStack> buildDescribeArgument() {
        return Commands.literal("describe")
                .then(Commands.argument("partID", ResourceLocationArgument.id())
                        .suggests(partIdSuggestions)
                        .executes(context -> runDescribe(context, ResourceLocationArgument.getId(context, "partID")))
                );
    }

    private static LiteralArgumentBuilder<CommandSourceStack> buildDumpArgument() {
        return Commands.literal("dump")
                .executes(PartsCommand::runDump);
    }

    private static int runList(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        var parts = SgRegistries.PART.keySet();

        // Header
        source.sendSuccess(() -> TextUtil.translate("command", "parts.list.header")
                .withStyle(ChatFormatting.GOLD), true);

        // List
        String listStr = parts.stream()
                .map(ResourceLocation::toString)
                .collect(Collectors.joining(", "));
        source.sendSuccess(() -> Component.literal(listStr), true);

        // Total
        source.sendSuccess(() -> TextUtil.translate("command", "parts.list.total", parts.size())
                .withStyle(ChatFormatting.GRAY), true);

        return 1;
    }

    private static int runDescribe(CommandContext<CommandSourceStack> context, ResourceLocation partId) {
        GearPart part = SgRegistries.PART.get(partId);
        if (part == null) {
            context.getSource().sendFailure(TextUtil.translate("command", "parts.partNotFound"));
            return 0;
        }

        PartInstance partData = PartInstance.of(part);
        CommandSourceStack source = context.getSource();

        // Display name as title
        source.sendSuccess(() -> part.getDisplayName(partData, part.getType())
                .copy().withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD), true);

        // Table separator
        source.sendSuccess(() -> Component.literal("─────────────────────────────────")
                .withStyle(ChatFormatting.DARK_GRAY), true);

        // ID row
        source.sendSuccess(() -> tableRow("parts.describe.id", partId.toString()), true);

        // Part Type row
        source.sendSuccess(() -> tableRow("parts.describe.type", part.getType().getDisplayName().getString()), true);

        // Gear Type row
        source.sendSuccess(() -> tableRow("parts.describe.gearType", part.getGearType().getDisplayName().getString()), true);

        // Visible row
        String visible = part.isVisible()
                ? TextUtil.translate("command", "parts.describe.visible.yes").getString()
                : TextUtil.translate("command", "parts.describe.visible.no").getString();
        source.sendSuccess(() -> tableRow("parts.describe.visible", visible), true);

        // Materials
        var materials = part.getMaterials(partData);
        if (!materials.isEmpty()) {
            String matsStr = materials.stream()
                    .map(m -> m.getDisplayName(part.getType(), ItemStack.EMPTY).getString())
                    .collect(Collectors.joining(", "));
            source.sendSuccess(() -> tableRow("parts.describe.materials", matsStr), true);
        }

        // Traits
        var traits = partData.getTraits(PartGearKey.of(GearTypes.ALL.get(), part.getType()));
        if (!traits.isEmpty()) {
            source.sendSuccess(() -> Component.literal("─────────────────────────────────")
                    .withStyle(ChatFormatting.DARK_GRAY), true);
            source.sendSuccess(() -> TextUtil.translate("command", "parts.describe.traitsHeader")
                    .withStyle(ChatFormatting.AQUA, ChatFormatting.UNDERLINE), true);

            for (var trait : traits) {
                source.sendSuccess(() -> tableRowDirect("  " + trait.getDisplayName().getString(), "Lv " + trait.getLevel()), true);
            }
        }

        return 1;
    }

    private static Component tableRow(String labelKey, String value) {
        return TextUtil.translate("command", labelKey)
                .withStyle(ChatFormatting.AQUA)
                .append(Component.literal(value)
                        .withStyle(ChatFormatting.WHITE));
    }

    private static Component tableRowDirect(String label, String value) {
        return Component.literal(label + ": ")
                .withStyle(ChatFormatting.AQUA)
                .append(Component.literal(value)
                        .withStyle(ChatFormatting.WHITE));
    }

    private static int runDump(CommandContext<CommandSourceStack> context) {
        String fileName = "part_export.tsv";
        String dirPath = "output/silentgear";
        File output = new File(dirPath, fileName);
        File directory = output.getParentFile();
        if (!directory.exists() && !directory.mkdirs()) {
            context.getSource().sendFailure(Component.literal("Could not create directory: " + output.getParent()));
            return 0;
        }

        try (Writer writer = new OutputStreamWriter(new FileOutputStream(output), StandardCharsets.UTF_8)) {
            StringBuilder builder = new StringBuilder("Name\tID\tType\t");
            SgRegistries.GEAR_PROPERTY.forEach(prop -> builder.append(prop.getDisplayName().getString()).append("\t"));
            writer.write(builder.toString());

            for (GearPart part : SgRegistries.PART) {
                writer.write(partToTsvLine(part) + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            context.getSource().sendSuccess(() -> Component.literal("Wrote to " + output.getAbsolutePath()), true);
        }

        return 1;
    }

    private static String partToTsvLine(GearPart part) {
        StringBuilder builder = new StringBuilder();
        PartInstance partData = PartInstance.of(part);
        appendTsv(builder, part.getDisplayName(partData, part.getType()).getString());
        appendTsv(builder, SgRegistries.PART.getKey(part).toString());
        appendTsv(builder, SgRegistries.PART_TYPE.getKey(part.getType()));

        // Properties
        for (var property : SgRegistries.GEAR_PROPERTY) {
            var mods = part.getPropertyModifiers(PartInstance.of(part), part.getType(), PropertyKey.of(property, GearTypes.ALL.get()));
            var formattedText = GearPropertyMap.formatTextUnchecked(mods, property, FormatColorScheme.NO_COLORS);
            appendTsv(builder, FORMAT_CODES.matcher(formattedText.getString()).replaceAll(""));
        }

        return builder.toString();
    }

    private static void appendTsv(StringBuilder builder, Object value) {
        builder.append(value).append("\t");
    }

    private static ItemStack getGear(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        if (ctx.getSource().getEntity() instanceof ServerPlayer) {
            ItemStack gear = ctx.getSource().getPlayerOrException().getMainHandItem();
            if (gear.getItem() instanceof GearItem) {
                return gear;
            }
        } else {
            ctx.getSource().sendFailure(text("sourceMustBePlayer"));
        }
        return ItemStack.EMPTY;
    }

    private static Component text(String key, Object... args) {
        return Component.translatable("command.silentgear.parts." + key, args);
    }
}
