package net.silentchaos512.gear.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.util.TextUtil;
import net.silentchaos512.gear.api.material.Material;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.property.GearPropertyMap;
import net.silentchaos512.gear.api.util.PartGearKey;
import net.silentchaos512.gear.api.util.PropertyKey;
import net.silentchaos512.gear.client.tooltip.FormatColorScheme;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.network.payload.server.CommandOutputPayload;
import net.silentchaos512.gear.setup.SgRegistries;
import net.silentchaos512.gear.setup.gear.GearTypes;
import net.silentchaos512.gear.setup.gear.PartTypes;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class MaterialsCommand {
    private static final SuggestionProvider<CommandSourceStack> MATERIAL_ID_SUGGESTIONS = (ctx, builder) ->
            SharedSuggestionProvider.suggestResource(SgRegistries.MATERIAL.keySet(), builder);

    private static final Pattern FORMAT_CODES = Pattern.compile("\u00a7[0-9a-z]");

    private MaterialsCommand() {
    }

    /**
     * Creates the subcommand for use with the unified /sgear command.
     */
    public static ArgumentBuilder<CommandSourceStack, ?> createSubcommand() {
        return Commands.literal("mats")
                .executes(ctx -> showHelp(ctx.getSource()))
                .then(Commands.literal("help")
                        .executes(ctx -> showHelp(ctx.getSource())))
                .then(buildListArgument())
                .then(buildDescribeArgument())
                .then(buildDumpArgument());
    }

    private static int showHelp(CommandSourceStack source) {
        source.sendSuccess(() -> TextUtil.translate("command", "help.mats.title")
                .withStyle(ChatFormatting.GOLD), false);
        source.sendSuccess(() -> Component.literal("  /sgear mats list").withStyle(ChatFormatting.YELLOW)
                .append(Component.literal(SGearCommand.HELP_INDENT).withStyle(ChatFormatting.GRAY))
                .append(TextUtil.translate("command", "help.mats.list").withStyle(ChatFormatting.GRAY)), false);
        source.sendSuccess(() -> Component.literal("  /sgear mats describe <materialID>").withStyle(ChatFormatting.YELLOW)
                .append(Component.literal(SGearCommand.HELP_INDENT).withStyle(ChatFormatting.GRAY))
                .append(TextUtil.translate("command", "help.mats.describe").withStyle(ChatFormatting.GRAY)), false);
        source.sendSuccess(() -> Component.literal("  /sgear mats dump [includeChildren]").withStyle(ChatFormatting.YELLOW)
                .append(Component.literal(SGearCommand.HELP_INDENT).withStyle(ChatFormatting.GRAY))
                .append(TextUtil.translate("command", "help.mats.dump").withStyle(ChatFormatting.GRAY)), false);
        return 1;
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("sgear_mats")
                .then(buildListArgument())
                .then(buildDescribeArgument())
                .then(buildDumpArgument())
        );
    }

    private static LiteralArgumentBuilder<CommandSourceStack> buildListArgument() {
        return Commands.literal("list")
                .executes(MaterialsCommand::runList);
    }

    private static LiteralArgumentBuilder<CommandSourceStack> buildDescribeArgument() {
        return Commands.literal("describe")
                .then(Commands.argument("materialID", ResourceLocationArgument.id())
                        .suggests(MATERIAL_ID_SUGGESTIONS)
                        .executes(context -> runDescribe(context, ResourceLocationArgument.getId(context, "materialID")))
                );
    }

    private static LiteralArgumentBuilder<CommandSourceStack> buildDumpArgument() {
        return Commands.literal("dump")
                .then(Commands.argument("includeChildren", BoolArgumentType.bool())
                        .executes(context -> runDump(context, context.getArgument("includeChildren", Boolean.class)))
                )
                .executes(context -> runDump(context, true));
    }

    private static int runList(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        var materials = SgRegistries.MATERIAL.keySet();

        // Header
        source.sendSuccess(() -> TextUtil.translate("command", "mats.list.header")
                .withStyle(ChatFormatting.GOLD), true);

        // List
        String listStr = materials.stream()
                .map(ResourceLocation::toString)
                .collect(Collectors.joining(", "));
        source.sendSuccess(() -> Component.literal(listStr), true);

        // Total
        source.sendSuccess(() -> TextUtil.translate("command", "mats.list.total", materials.size())
                .withStyle(ChatFormatting.GRAY), true);

        return 1;
    }

    private static int runDescribe(CommandContext<CommandSourceStack> context, ResourceLocation materialId) {
        Material material = SgRegistries.MATERIAL.get(materialId);
        if (material == null) {
            context.getSource().sendFailure(TextUtil.translate("command", "mats.materialNotFound"));
            return 0;
        }

        MaterialInstance instance = MaterialInstance.of(material);
        CommandSourceStack source = context.getSource();

        // Display name as title
        source.sendSuccess(() -> instance.getDisplayName(PartTypes.MAIN.get(), ItemStack.EMPTY)
                .copy().withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD), true);

        // Table separator
        source.sendSuccess(() -> Component.literal("─────────────────────────────────")
                .withStyle(ChatFormatting.DARK_GRAY), true);

        // ID row
        source.sendSuccess(() -> tableRow("mats.describe.id", materialId.toString()), true);

        // Parent row (if exists)
        Material parent = material.getParent();
        if (parent != null) {
            ResourceLocation parentId = SgRegistries.MATERIAL.getKey(parent);
            source.sendSuccess(() -> tableRow("mats.describe.parent", parentId.toString()), true);
        }

        // Type row
        String type = material.isSimple()
                ? TextUtil.translate("command", "mats.describe.type.simple").getString()
                : TextUtil.translate("command", "mats.describe.type.compound").getString();
        source.sendSuccess(() -> tableRow("mats.describe.type", type), true);

        // Categories row
        String categories = instance.getCategories().stream()
                .map(cat -> cat.getDisplayName().getString())
                .collect(Collectors.joining(", "));
        if (!categories.isEmpty()) {
            source.sendSuccess(() -> tableRow("mats.describe.categories", categories), true);
        }

        // Part Types row
        String partTypes = instance.getPartTypes().stream()
                .map(pt -> pt.getDisplayName().getString())
                .collect(Collectors.joining(", "));
        if (!partTypes.isEmpty()) {
            source.sendSuccess(() -> tableRow("mats.describe.partTypes", partTypes), true);
        }

        // Traits section
        boolean hasTraits = instance.getPartTypes().stream()
                .anyMatch(pt -> !instance.getTraits(PartGearKey.of(GearTypes.ALL.get(), pt)).isEmpty());

        if (hasTraits) {
            source.sendSuccess(() -> Component.literal("─────────────────────────────────")
                    .withStyle(ChatFormatting.DARK_GRAY), true);
            source.sendSuccess(() -> TextUtil.translate("command", "mats.describe.traitsHeader")
                    .withStyle(ChatFormatting.AQUA, ChatFormatting.UNDERLINE), true);

            for (PartType partType : instance.getPartTypes()) {
                var traits = instance.getTraits(PartGearKey.of(GearTypes.ALL.get(), partType));
                if (!traits.isEmpty()) {
                    String traitStr = traits.stream()
                            .map(t -> t.getDisplayName().getString())
                            .collect(Collectors.joining(", "));
                    source.sendSuccess(() -> tableRowDirect("  " + partType.getDisplayName().getString(), traitStr), true);
                }
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

    private static int runDump(CommandContext<CommandSourceStack> context, boolean includeChildren) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        SilentGear.LOGGER.info("Send material dump packet to client {}", player.getScoreboardName());
        CommandOutputPayload message = CommandOutputPayload.materials(includeChildren);
        PacketDistributor.sendToPlayer(player, message);
        return 1;
    }

    public static void runDumpClient(boolean includeChildren) {
        Player player = SilentGear.PROXY.getClientPlayer();
        if (player == null) {
            SilentGear.LOGGER.error("MaterialsCommand#runDumpClient: player is null?");
            return;
        }

        String fileName = "material_export.tsv";
        String dirPath = "output/silentgear";
        File output = new File(dirPath, fileName);
        File directory = output.getParentFile();
        if (!directory.exists() && !directory.mkdirs()) {
            player.displayClientMessage(Component.literal("Could not create directory: " + output.getParent()), false);
            return;
        }

        try (Writer writer = new OutputStreamWriter(new FileOutputStream(output), StandardCharsets.UTF_8)) {
            StringBuilder builder = new StringBuilder("Pack\tName\tType\tID\tParent\tCategories\t");
            SgRegistries.GEAR_PROPERTY.forEach(prop -> builder.append(prop.getDisplayName().getString()).append("\t"));
            writer.write(builder + "\n");

            List<PartType> partTypes = new ArrayList<>(SgRegistries.PART_TYPE.stream().toList());
            partTypes.sort((o1, o2) -> Comparator.comparing(o -> ((PartType) o).getDisplayName().getString()).compare(o1, o2));
            for (PartType partType : partTypes) {
                for (Material material : SgRegistries.MATERIAL.getValues(includeChildren)) {
                    MaterialInstance inst = MaterialInstance.of(material);
                    if (material.isAllowedInPart(inst, partType)) {
                        writer.write(makeTsvLine(inst, partType) + "\n");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Component fileNameText = (Component.literal(output.getAbsolutePath())).withStyle(ChatFormatting.UNDERLINE).withStyle(style ->
                    style.withClickEvent(new ClickEvent.OpenFile(output.getAbsolutePath())));
            player.displayClientMessage(Component.literal("Wrote materials info to ").append(fileNameText), false);
        }
    }

    private static String makeTsvLine(MaterialInstance material, PartType partType) {
        StringBuilder builder = new StringBuilder();
        appendTsv(builder, SgRegistries.MATERIAL.getPackName(material.get()));
        appendTsv(builder, material.getDisplayName(partType).getString());
        appendTsv(builder, partType.getDisplayName().getString());
        appendTsv(builder, material.getId().toString());
        appendTsv(builder, getParentName(material.get(), partType));
        appendTsv(builder, material.getCategories().stream().map(cat -> cat.getDisplayName().getString()).collect(Collectors.joining(", ")));

        // Properties
        for (var property : SgRegistries.GEAR_PROPERTY) {
            var mods = material.getPropertyModifiers(partType, PropertyKey.of(property, GearTypes.ALL.get()));
            var formattedText = GearPropertyMap.formatTextUnchecked(mods, property, FormatColorScheme.NO_COLORS);
            appendTsv(builder, FORMAT_CODES.matcher(formattedText.getString()).replaceAll(""));
        }

        return builder.toString();
    }

    private static String getParentName(Material material, PartType partType) {
        Material parent = material.getParent();
        if (parent != null) {
            MaterialInstance parentInstance = MaterialInstance.of(parent);
            return parentInstance.getDisplayName(partType).getString();
        }
        return "";
    }

    private static void appendTsv(StringBuilder builder, Object value) {
        builder.append(value).append("\t");
    }
}
