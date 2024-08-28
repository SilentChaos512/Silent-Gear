package net.silentchaos512.gear.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.material.Material;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.property.GearPropertyMap;
import net.silentchaos512.gear.api.util.PropertyKey;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.network.payload.server.CommandOutputPayload;
import net.silentchaos512.gear.setup.SgRegistries;
import net.silentchaos512.gear.setup.gear.GearTypes;

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

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("sgear_mats");

        // List
        builder.then(Commands.literal("list")
                .executes(
                        MaterialsCommand::runList
                )
        );
        // Dump to CSV
        builder.then(Commands.literal("dump")
                .then(Commands.argument("includeChildren", BoolArgumentType.bool())
                        .executes(context -> runDump(context, context.getArgument("includeChildren", Boolean.class)))
                )
                .executes(context -> runDump(context, true))
        );

        dispatcher.register(builder);
    }

    private static int runList(CommandContext<CommandSourceStack> context) {
        String listStr = SgRegistries.MATERIAL.keySet().stream()
                .map(ResourceLocation::toString)
                .collect(Collectors.joining(", "));
        context.getSource().sendSuccess(() -> Component.literal(listStr), true);

        return 1;
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
            player.sendSystemMessage(Component.literal("Could not create directory: " + output.getParent()));
            return;
        }

        try (Writer writer = new OutputStreamWriter(new FileOutputStream(output), StandardCharsets.UTF_8)) {
            StringBuilder builder = new StringBuilder("Pack\tName\tType\tID\tParent\t");
            SgRegistries.GEAR_PROPERTY.forEach(prop -> builder.append(prop.getDisplayName().getString()).append("\t"));
            writer.write(builder + "\n");

            List<PartType> partTypes = new ArrayList<>(SgRegistries.PART_TYPE.stream().toList());
            partTypes.sort((o1, o2) -> Comparator.comparing(o -> ((PartType) o).getDisplayName().getString()).compare(o1, o2));
            for (PartType partType : partTypes) {
                for (Material material : SgRegistries.MATERIAL) {
                    if (includeChildren || getParentId(material).isEmpty()) {
                        MaterialInstance inst = MaterialInstance.of(material);
                        if (material.isAllowedInPart(inst, partType)) {
                            writer.write(makeTsvLine(inst, partType) + "\n");
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Component fileNameText = (Component.literal(output.getAbsolutePath())).withStyle(ChatFormatting.UNDERLINE).withStyle(style ->
                    style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, output.getAbsolutePath())));
            player.sendSystemMessage(Component.literal("Wrote materials info to ").append(fileNameText));
        }
    }

    private static String makeTsvLine(MaterialInstance material, PartType partType) {
        StringBuilder builder = new StringBuilder();
        appendTsv(builder, material.get().getPackName());
        appendTsv(builder, material.getDisplayName(partType).getString());
        appendTsv(builder, partType.getDisplayName().getString());
        appendTsv(builder, material.getId().toString());
        appendTsv(builder, getParentId(material.get()));

        // Properties
        for (var property : SgRegistries.GEAR_PROPERTY) {
            var mods = material.getPropertyModifiers(partType, PropertyKey.of(property, GearTypes.ALL.get()));
            var formattedText = GearPropertyMap.formatTextUnchecked(mods, property, false);
            appendTsv(builder, FORMAT_CODES.matcher(formattedText.getString()).replaceAll(""));
        }

        return builder.toString();
    }

    private static String getParentId(Material material) {
        Material parent = material.getParent();
        if (parent != null) {
            return SgRegistries.MATERIAL.getKey(material).toString();
        }
        return "";
    }

    private static void appendTsv(StringBuilder builder, Object value) {
        builder.append(value).append("\t");
    }

    private static Component text(String key, Object... args) {
        return Component.translatable("command.silentgear.parts." + key, args);
    }
}
