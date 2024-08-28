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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.part.GearPart;
import net.silentchaos512.gear.api.part.PartList;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.property.GearPropertyMap;
import net.silentchaos512.gear.api.util.PropertyKey;
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
        String listStr = SgRegistries.PART.keySet().stream()
                .map(ResourceLocation::toString)
                .collect(Collectors.joining(", "));
        context.getSource().sendSuccess(() -> Component.literal(listStr), true);

        for (PartType type : SgRegistries.PART_TYPE) {
            int count = SgRegistries.PART.getPartsOfType(type).size();
            String str = String.format("%s: %d", SgRegistries.PART_TYPE.getKey(type), count);
            context.getSource().sendSuccess(() -> Component.literal(str), true);
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
            var formattedText = GearPropertyMap.formatTextUnchecked(mods, property, false);
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
