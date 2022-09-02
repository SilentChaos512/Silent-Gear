package net.silentchaos512.gear.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.part.IGearPart;
import net.silentchaos512.gear.api.part.PartDataList;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.api.stats.StatInstance;
import net.silentchaos512.gear.api.stats.StatModifierMap;
import net.silentchaos512.gear.api.util.StatGearKey;
import net.silentchaos512.gear.gear.part.PartData;
import net.silentchaos512.gear.gear.part.PartManager;
import net.silentchaos512.gear.util.GearData;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class PartsCommand {
    private static final SuggestionProvider<CommandSourceStack> partIdSuggestions = (ctx, builder) ->
            SharedSuggestionProvider.suggestResource(PartManager.getValues().stream().map(IGearPart::getId), builder);
    private static final SuggestionProvider<CommandSourceStack> partInGearSuggestions = (ctx, builder) -> {
        PartDataList parts = GearData.getConstructionParts(getGear(ctx));
        return SharedSuggestionProvider.suggestResource(parts.getUniqueParts(false).stream().map(part ->
                part.get().getId()), builder);
    };
    private static final Pattern FORMAT_CODES = Pattern.compile("\u00a7[0-9a-z]");

    private PartsCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("sgear_parts");

        // List
        builder.then(Commands.literal("list")
                .executes(
                        PartsCommand::runList
                )
        );
        // Dump to TSV
        builder.then(Commands.literal("dump")
                .executes(
                        PartsCommand::runDump
                )
        );

        dispatcher.register(builder);
    }

    private static int runList(CommandContext<CommandSourceStack> context) {
        String listStr = PartManager.getValues().stream()
                .map(part -> part.getId().toString())
                .collect(Collectors.joining(", "));
        context.getSource().sendSuccess(Component.literal(listStr), true);

        for (PartType type : PartType.getValues()) {
            int count = PartManager.getPartsOfType(type).size();
            String str = String.format("%s: %d", type.getName(), count);
            context.getSource().sendSuccess(Component.literal(str), true);
        }

        return 1;
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
            StringBuilder builder = new StringBuilder("Name\tID\tType\tTier\t");
            ItemStats.allStatsOrdered().forEach(s -> builder.append(s.getDisplayName().getString()).append("\t"));
            builder.append("Traits");
            writer.write(builder.toString());

            for (IGearPart part : PartManager.getValues()) {
                writer.write(partToTsvLine(part) + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            context.getSource().sendSuccess(Component.literal("Wrote to " + output.getAbsolutePath()), true);
        }

        return 1;
    }

    private static String partToTsvLine(IGearPart part) {
        StringBuilder builder = new StringBuilder();
        PartData partData = PartData.of(part);
        appendTsv(builder, part.getDisplayName(partData, ItemStack.EMPTY).getString());
        appendTsv(builder, part.getId().toString());
        appendTsv(builder, part.getType().getName());
        appendTsv(builder, partData.getTier());

        // Stats
        for (ItemStat stat : ItemStats.allStatsOrdered()) {
            Collection<StatInstance> statModifiers = partData.getStatModifiers(StatGearKey.of(stat, GearType.ALL), ItemStack.EMPTY);
            appendTsv(builder, FORMAT_CODES.matcher(StatModifierMap.formatText(statModifiers, stat, 5).getString()).replaceAll(""));
        }

        // Traits
        appendTsv(builder, partData.getTraits().stream()
                .map(t -> t.getTrait().getDisplayName(t.getLevel()).getString())
                .collect(Collectors.joining(", ")));

        return builder.toString();
    }

    private static void appendTsv(StringBuilder builder, Object value) {
        builder.append(value).append("\t");
    }

    private static ItemStack getGear(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        if (ctx.getSource().getEntity() instanceof ServerPlayer) {
            ItemStack gear = ctx.getSource().getPlayerOrException().getMainHandItem();
            if (gear.getItem() instanceof ICoreItem) {
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
