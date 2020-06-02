package net.silentchaos512.gear.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.silentchaos512.gear.api.material.IPartMaterial;
import net.silentchaos512.gear.api.parts.PartType;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.api.stats.StatInstance;
import net.silentchaos512.gear.api.stats.StatModifierMap;
import net.silentchaos512.gear.gear.material.MaterialManager;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class SGearMaterialsCommand {
    private static final SuggestionProvider<CommandSource> MATERIAL_ID_SUGGESTIONS = (ctx, builder) ->
            ISuggestionProvider.func_212476_a(MaterialManager.getValues().stream().map(IPartMaterial::getId), builder);

    private static final Pattern FORMAT_CODES = Pattern.compile("\u00a7[0-9a-z]");

    private SGearMaterialsCommand() {
    }

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        LiteralArgumentBuilder<CommandSource> builder = Commands.literal("sgear_mats");

        // List
        builder.then(Commands.literal("list")
                .executes(
                        SGearMaterialsCommand::runList
                )
        );
        // Dump to CSV
        builder.then(Commands.literal("dump")
                .executes(
                        SGearMaterialsCommand::runDump
                )
        );

        dispatcher.register(builder);
    }

    private static int runList(CommandContext<CommandSource> context) {
        String listStr = MaterialManager.getValues().stream()
                .map(mat -> mat.getId().toString())
                .collect(Collectors.joining(", "));
        context.getSource().sendFeedback(new StringTextComponent(listStr), true);

        return 1;
    }

    private static int runDump(CommandContext<CommandSource> context) {
        String fileName = "material_export.tsv";
        String dirPath = "output/silentgear";
        File output = new File(dirPath, fileName);
        File directory = output.getParentFile();
        if (!directory.exists() && !directory.mkdirs()) {
            context.getSource().sendErrorMessage(new StringTextComponent("Could not create directory: " + output.getParent()));
            return 0;
        }

        try (Writer writer = new OutputStreamWriter(new FileOutputStream(output), StandardCharsets.UTF_8)) {
            StringBuilder builder = new StringBuilder("Name\tID\tType\tTier\t");
            ItemStats.allStatsOrdered().forEach(s -> builder.append(s.getDisplayName().getFormattedText()).append("\t"));
            builder.append("Traits\tTexture\tColor");
            writer.write(builder.toString());

            for (IPartMaterial material : MaterialManager.getValues()) {
                for (PartType partType : PartType.getValues()) {
                    if (material.allowedInPart(partType)) {
                        writer.write(makeTsvLine(material, partType) + "\n");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            context.getSource().sendFeedback(new StringTextComponent("Wrote to " + output.getAbsolutePath()), true);
        }

        return 1;
    }

    private static String makeTsvLine(IPartMaterial material, PartType partType) {
        StringBuilder builder = new StringBuilder();
        appendTsv(builder, material.getDisplayName(ItemStack.EMPTY, partType).getString());
        appendTsv(builder, material.getId().toString());
        appendTsv(builder, partType.getName());
        appendTsv(builder, material.getTier(partType));

        // Stats
        for (ItemStat stat : ItemStats.allStatsOrdered()) {
            Collection<StatInstance> statModifiers = material.getStatModifiers(stat, partType);
            appendTsv(builder, FORMAT_CODES.matcher(StatModifierMap.formatText(statModifiers, stat, 5).getString()).replaceAll(""));
        }

        // Traits
        appendTsv(builder, material.getTraits(partType).stream()
                .map(t -> t.getTrait().getDisplayName(t.getLevel()).getFormattedText())
                .collect(Collectors.joining(", ")));

        // Display
        appendTsv(builder, material.getTexture(ItemStack.EMPTY, partType));
        appendTsv(builder, material.getColor(ItemStack.EMPTY, partType));

        return builder.toString();
    }

    private static void appendTsv(StringBuilder builder, Object value) {
        builder.append(value).append("\t");
    }

    private static ITextComponent text(String key, Object... args) {
        return new TranslationTextComponent("command.silentgear.parts." + key, args);
    }
}
