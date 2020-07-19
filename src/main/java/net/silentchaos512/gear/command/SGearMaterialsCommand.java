package net.silentchaos512.gear.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.silentchaos512.gear.api.material.IMaterial;
import net.silentchaos512.gear.api.parts.PartType;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.api.stats.StatInstance;
import net.silentchaos512.gear.api.stats.StatModifierMap;
import net.silentchaos512.gear.gear.material.MaterialManager;
import net.silentchaos512.gear.gear.material.PartMaterial;
import net.silentchaos512.utils.Color;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class SGearMaterialsCommand {
    private static final SuggestionProvider<CommandSource> MATERIAL_ID_SUGGESTIONS = (ctx, builder) ->
            ISuggestionProvider.func_212476_a(MaterialManager.getValues().stream().map(IMaterial::getId), builder);

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
                .then(Commands.argument("includeChildren", BoolArgumentType.bool())
                        .executes(context -> runDump(context, context.getArgument("includeChildren", Boolean.class)))
                )
                .executes(context -> runDump(context, true))
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

    private static int runDump(CommandContext<CommandSource> context, boolean includeChildren) {
        String fileName = "material_export.tsv";
        String dirPath = "output/silentgear";
        File output = new File(dirPath, fileName);
        File directory = output.getParentFile();
        if (!directory.exists() && !directory.mkdirs()) {
            context.getSource().sendErrorMessage(new StringTextComponent("Could not create directory: " + output.getParent()));
            return 0;
        }

        try (Writer writer = new OutputStreamWriter(new FileOutputStream(output), StandardCharsets.UTF_8)) {
            StringBuilder builder = new StringBuilder("Pack\tName\tType\tID\tParent\tTier\t");
            ItemStats.allStatsOrdered().forEach(s -> builder.append(s.getDisplayName().getString()).append("\t"));
            builder.append("Traits\tTexture\tColor\n");
            writer.write(builder.toString());

            List<PartType> partTypes = new ArrayList<>(PartType.getValues());
            partTypes.sort((o1, o2) -> Comparator.comparing(o -> ((PartType) o).getName()).compare(o1, o2));
            for (PartType partType : partTypes) {
                for (IMaterial material : MaterialManager.getValues()) {
                    if (includeChildren || getParentId(material).isEmpty()) {
                        if (material.allowedInPart(partType)) {
                            writer.write(makeTsvLine(material, partType) + "\n");
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            ITextComponent fileNameText = (new StringTextComponent(output.getAbsolutePath())).func_240699_a_(TextFormatting.UNDERLINE).func_240700_a_(style ->
                    style.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, output.getAbsolutePath())));
            context.getSource().sendFeedback(new StringTextComponent("Wrote materials info to ").func_230529_a_(fileNameText), true);
        }

        return 1;
    }

    private static String makeTsvLine(IMaterial material, PartType partType) {
        StringBuilder builder = new StringBuilder();
        appendTsv(builder, material.getPackName());
        appendTsv(builder, material.getDisplayName(partType, ItemStack.EMPTY).getString());
        int tier = material.getTier(partType);
//        appendTsv(builder, partType.getDisplayName(tier).getFormattedText());
        appendTsv(builder, partType.getName().toString());
        appendTsv(builder, material.getId().toString());
        appendTsv(builder, getParentId(material));
        appendTsv(builder, tier);

        // Stats
        for (ItemStat stat : ItemStats.allStatsOrdered()) {
            Collection<StatInstance> statModifiers = material.getStatModifiers(stat, partType);
            appendTsv(builder, FORMAT_CODES.matcher(StatModifierMap.formatText(statModifiers, stat, 5).getString()).replaceAll(""));
        }

        // Traits
        appendTsv(builder, material.getTraits(partType).stream()
                .map(t -> t.getTrait().getDisplayName(t.getLevel()).getString())
                .collect(Collectors.joining(", ")));

        // Display
        appendTsv(builder, material.getTexture(partType, ItemStack.EMPTY));
        appendTsv(builder, Color.format(material.getPrimaryColor(ItemStack.EMPTY, partType) & 0xFFFFFF));

        return builder.toString();
    }

    private static String getParentId(IMaterial material) {
        if (material instanceof PartMaterial) {
            IMaterial parent = ((PartMaterial) material).getParent();
            if (parent != null) {
                return parent.getId().toString();
            }
        }
        return "";
    }

    private static void appendTsv(StringBuilder builder, Object value) {
        builder.append(value).append("\t");
    }

    private static ITextComponent text(String key, Object... args) {
        return new TranslationTextComponent("command.silentgear.parts." + key, args);
    }
}
