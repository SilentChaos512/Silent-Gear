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
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.parts.IGearPart;
import net.silentchaos512.gear.api.parts.MaterialGrade;
import net.silentchaos512.gear.api.parts.PartDataList;
import net.silentchaos512.gear.api.parts.PartType;
import net.silentchaos512.gear.parts.PartData;
import net.silentchaos512.gear.parts.PartManager;
import net.silentchaos512.gear.util.GearData;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.stream.Collectors;

public final class SGearPartsCommand {
    private static final SuggestionProvider<CommandSource> partIdSuggestions = (ctx, builder) ->
            ISuggestionProvider.func_212476_a(PartManager.getValues().stream().map(IGearPart::getId), builder);
    private static final SuggestionProvider<CommandSource> partInGearSuggestions = (ctx, builder) -> {
        PartDataList parts = GearData.getConstructionParts(getGear(ctx));
        return ISuggestionProvider.func_212476_a(parts.getUniqueParts(false).stream().map(part ->
                part.getPart().getId()), builder);
    };

    private SGearPartsCommand() {}

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        LiteralArgumentBuilder<CommandSource> builder = Commands.literal("sgear_parts")
                .requires(source -> source.hasPermissionLevel(2));

        // Add
        builder.then(Commands.literal("add")
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

        dispatcher.register(builder);
    }

    private static int runAdd(CommandContext<CommandSource> ctx, MaterialGrade grade) throws CommandSyntaxException {
        IGearPart part = getPart(ctx);
        if (part == null) return 0;
        ItemStack gear = getGear(ctx);
        if (gear.isEmpty()) return 0;

        Collection<PartData> partList = GearData.getConstructionParts(gear);
        partList.add(PartData.of(part, grade));
        GearData.writeConstructionParts(gear, partList);
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
