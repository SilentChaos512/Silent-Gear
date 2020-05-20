package net.silentchaos512.gear.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.ResourceLocationArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.NetworkDirection;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.parts.*;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.api.stats.StatInstance;
import net.silentchaos512.gear.api.stats.StatModifierMap;
import net.silentchaos512.gear.network.Network;
import net.silentchaos512.gear.network.ShowPartsScreenPacket;
import net.silentchaos512.gear.parts.PartData;
import net.silentchaos512.gear.parts.PartManager;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.utils.Color;

import javax.annotation.Nullable;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class SGearPartsCommand {
    private static final SuggestionProvider<CommandSource> partIdSuggestions = (ctx, builder) ->
            ISuggestionProvider.func_212476_a(PartManager.getValues().stream().map(IGearPart::getId), builder);
    private static final SuggestionProvider<CommandSource> partInGearSuggestions = (ctx, builder) -> {
        PartDataList parts = GearData.getConstructionParts(getGear(ctx));
        return ISuggestionProvider.func_212476_a(parts.getUniqueParts(false).stream().map(part ->
                part.getPart().getId()), builder);
    };
    private static final Pattern FORMAT_CODES = Pattern.compile("\u00a7[0-9a-z]");

    private SGearPartsCommand() {
    }

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        LiteralArgumentBuilder<CommandSource> builder = Commands.literal("sgear_parts");

        // Add
        builder.then(Commands.literal("add")
                .requires(source -> source.hasPermissionLevel(2))
                .then(Commands.argument("partID", ResourceLocationArgument.resourceLocation())
                        .suggests(partIdSuggestions)
                        .then(Commands.argument("grade", new MaterialGrade.Argument())
                                .executes(ctx ->
                                        runAdd(ctx, getPartGrade(ctx))
                                )
                        ).executes(ctx ->
                                runAdd(ctx, MaterialGrade.NONE)
                        )
                )
        );
        // Remove
        builder.then(Commands.literal("remove")
                .requires(source -> source.hasPermissionLevel(2))
                .then(Commands.argument("partID", ResourceLocationArgument.resourceLocation())
                        .suggests(partInGearSuggestions)
                        .executes(
                                SGearPartsCommand::runRemoveById
                        )
                )
                .then(Commands.argument("partIndex", IntegerArgumentType.integer())
                        .executes(
                                SGearPartsCommand::runRemoveByIndex
                        )
                )
        );
        // List
        builder.then(Commands.literal("list")
                .executes(
                        SGearPartsCommand::runList
                )
        );
        // Dump to CSV
        builder.then(Commands.literal("dump")
                .executes(
                        SGearPartsCommand::runDumpCsv
                )
        );
        // Show GUI
        builder.then(Commands.literal("show_gui")
                .executes(context -> {
                    ServerPlayerEntity playerMP = context.getSource().asPlayer();
                    Network.channel.sendTo(new ShowPartsScreenPacket(), playerMP.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
                    return 1;
                })
        );

        dispatcher.register(builder);
    }

    private static int runAdd(CommandContext<CommandSource> ctx, MaterialGrade grade) throws CommandSyntaxException {
        IGearPart part = getPart(ctx);
        if (part == null) return 0;
        ItemStack gear = getGear(ctx);
        if (gear.isEmpty()) return 0;

        GearData.addPart(gear, PartData.of(part));
        GearData.recalculateStats(gear, ctx.getSource().asPlayer());
        return 1;
    }

    private static int runRemoveById(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
        ItemStack gear = getGear(ctx);
        if (gear.isEmpty()) return 0;
        IGearPart part = getPart(ctx);
        if (part == null) return 0;

        PartDataList partList = GearData.getConstructionParts(gear);
        boolean removed = false;
        for (int i = 0; !removed && i < partList.size(); ++i) {
            PartData data = partList.get(i);
            if (data.getPart() == part) {
                partList.remove(data);
                removed = true;
            }
        }

        if (removed) {
            GearData.writeConstructionParts(gear, partList);
            GearData.recalculateStats(gear, ctx.getSource().asPlayer());
            ctx.getSource().sendFeedback(text("remove.success"), true);
            return 1;
        } else {
            ctx.getSource().sendErrorMessage(text("gearDoesNotContainPart"));
            return 0;
        }
    }

    private static int runRemoveByIndex(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
        ItemStack gear = getGear(ctx);
        if (gear.isEmpty()) return 0;
        PartDataList partList = GearData.getConstructionParts(gear);

        int index = IntegerArgumentType.getInteger(ctx, "partIndex");
        if (index < 0 || index >= partList.size()) {
            ctx.getSource().sendErrorMessage(text("indexOutOfBounds"));
            return 0;
        }

        partList.remove(index);
        GearData.writeConstructionParts(gear, partList);
        GearData.recalculateStats(gear, ctx.getSource().asPlayer());
        ctx.getSource().sendFeedback(text("remove.success"), true);
        return 1;
    }

    private static int runList(CommandContext<CommandSource> context) {
        String listStr = PartManager.getValues().stream()
                .map(part -> part.getId().toString())
                .collect(Collectors.joining(", "));
        context.getSource().sendFeedback(new StringTextComponent(listStr), true);

        for (PartType type : PartType.getValues()) {
            int count = PartManager.getPartsOfType(type).size();
            String str = String.format("%s: %d", type.getName(), count);
            context.getSource().sendFeedback(new StringTextComponent(str), true);
        }

        return 1;
    }

    private static int runDumpCsv(CommandContext<CommandSource> context) {
        String fileName = "part_export.tsv";
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
            builder.append("Traits\tLite Texture\tNormal Color\tBroken Color\tFallback Color\tArmor Color");
            writer.write(builder.toString());

            for (IGearPart part : PartManager.getValues()) {
                writer.write(partToTsvLine(part) + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            context.getSource().sendFeedback(new StringTextComponent("Wrote to " + output.getAbsolutePath()), true);
        }

        return 1;
    }

    private static String partToTsvLine(IGearPart part) {
        StringBuilder builder = new StringBuilder();
        PartData partData = PartData.of(part);
        appendTsv(builder, part.getDisplayName(partData, ItemStack.EMPTY).getString());
        appendTsv(builder, part.getId().toString());
        appendTsv(builder, part.getType().getName());
        appendTsv(builder, part.getTier());

        // Stats
        for (ItemStat stat : ItemStats.allStatsOrdered()) {
            Collection<StatInstance> statModifiers = part.getStatModifiers(stat, partData);
            appendTsv(builder, FORMAT_CODES.matcher(StatModifierMap.formatText(statModifiers, stat, 5).getString()).replaceAll(""));
        }

        // Traits
        appendTsv(builder, part.getTraits(partData).stream()
                .map(t -> t.getTrait().getDisplayName(t.getLevel()).getFormattedText())
                .collect(Collectors.joining(", ")));

        // Display
        IPartDisplay display = part.getDisplayProperties(partData, ItemStack.EMPTY, 0);
        appendTsv(builder, display.getLiteTexture());
        appendTsv(builder, Color.format(display.getNormalColor() & 0xFFFFFF));
        appendTsv(builder, Color.format(display.getBrokenColor() & 0xFFFFFF));
        appendTsv(builder, Color.format(display.getFallbackColor() & 0xFFFFFF));
        appendTsv(builder, Color.format(display.getArmorColor() & 0xFFFFFF));

        return builder.toString();
    }

    private static void appendTsv(StringBuilder builder, Object value) {
        builder.append(value).append("\t");
    }

    @Nullable
    private static IGearPart getPart(CommandContext<CommandSource> ctx) {
        ResourceLocation id = ResourceLocationArgument.getResourceLocation(ctx, "partID");
        IGearPart part = PartManager.get(id);
        if (part == null) {
            ctx.getSource().sendErrorMessage(text("partNotFound", id));
        }
        return part;
    }

    private static MaterialGrade getPartGrade(CommandContext<CommandSource> ctx) {
        return MaterialGrade.Argument.getGrade(ctx, "grade");
    }

    private static ItemStack getGear(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
        if (ctx.getSource().getEntity() instanceof ServerPlayerEntity) {
            ItemStack gear = ctx.getSource().asPlayer().getHeldItemMainhand();
            if (gear.getItem() instanceof ICoreItem) {
                return gear;
            }
        } else {
            ctx.getSource().sendErrorMessage(text("sourceMustBePlayer"));
        }
        return ItemStack.EMPTY;
    }

    private static ITextComponent text(String key, Object... args) {
        return new TranslationTextComponent("command.silentgear.parts." + key, args);
    }
}
