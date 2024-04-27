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
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.material.IMaterial;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.api.stats.StatInstance;
import net.silentchaos512.gear.api.stats.StatModifierMap;
import net.silentchaos512.gear.api.util.StatGearKey;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.gear.material.MaterialManager;
import net.silentchaos512.gear.network.payload.server.CommandOutputPayload;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class MaterialsCommand {
    private static final SuggestionProvider<CommandSourceStack> MATERIAL_ID_SUGGESTIONS = (ctx, builder) ->
            SharedSuggestionProvider.suggestResource(MaterialManager.getValues().stream().map(IMaterial::getId), builder);

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
        String listStr = MaterialManager.getValues().stream()
                .map(mat -> mat.getId().toString())
                .collect(Collectors.joining(", "));
        context.getSource().sendSuccess(() -> Component.literal(listStr), true);

        return 1;
    }

    private static int runDump(CommandContext<CommandSourceStack> context, boolean includeChildren) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        SilentGear.LOGGER.info("Send material dump packet to client {}", player.getScoreboardName());
        CommandOutputPayload message = CommandOutputPayload.materials(includeChildren);
        PacketDistributor.PLAYER.with(player).send(message);
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
            StringBuilder builder = new StringBuilder("Pack\tName\tType\tID\tParent\tTraits\tTier\t");
            ItemStats.allStatsOrdered().forEach(s -> builder.append(s.getDisplayName().getString()).append("\t"));
            writer.write(builder + "\n");

            List<PartType> partTypes = new ArrayList<>(PartType.getValues());
            partTypes.sort((o1, o2) -> Comparator.comparing(o -> ((PartType) o).getDisplayName(0).getString()).compare(o1, o2));
            for (PartType partType : partTypes) {
                for (IMaterial material : MaterialManager.getValues()) {
                    if (includeChildren || getParentId(material).isEmpty()) {
                        MaterialInstance inst = MaterialInstance.of(material);
                        if (material.allowedInPart(inst, partType)) {
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
        int tier = material.getTier(partType);
//        appendTsv(builder, partType.getDisplayName(tier).getFormattedText());
        appendTsv(builder, partType.getDisplayName(0).getString());
        appendTsv(builder, material.getId().toString());
        appendTsv(builder, getParentId(material.get()));

        // Traits
        appendTsv(builder, material.getTraits(partType).stream()
                .map(t -> t.getTrait().getDisplayName(t.getLevel()).getString())
                .collect(Collectors.joining(", ")));

        appendTsv(builder, tier);

        // Stats
        for (ItemStat stat : ItemStats.allStatsOrdered()) {
            Collection<StatInstance> statModifiers = material.getStatModifiers(partType, StatGearKey.of(stat, GearType.ALL));
            appendTsv(builder, FORMAT_CODES.matcher(StatModifierMap.formatText(statModifiers, stat, 5).getString()).replaceAll(""));
        }

        return builder.toString();
    }

    private static String getParentId(IMaterial material) {
        IMaterial parent = material.getParent();
        if (parent != null) {
            return parent.getId().toString();
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
