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
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.parts.IGearPart;
import net.silentchaos512.gear.api.parts.PartDataList;
import net.silentchaos512.gear.parts.PartData;
import net.silentchaos512.gear.parts.PartManager;
import net.silentchaos512.gear.util.GearData;

import javax.annotation.Nullable;
import java.util.Collection;

public final class SGearPartsCommand {
    private static final SuggestionProvider<CommandSource> partIdSuggestions = (ctx, builder) ->
            ISuggestionProvider.func_212476_a(PartManager.getValues().stream().map(IGearPart::getName), builder);
    private static final SuggestionProvider<CommandSource>  partInGearSuggestions = (ctx, builder) -> {
        PartDataList parts = GearData.getConstructionParts(getGear(ctx));
        return ISuggestionProvider.func_212476_a(parts.getUniqueParts(false).stream().map(part ->
                part.getPart().getName()), builder);
    };

    private SGearPartsCommand() {}

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        LiteralArgumentBuilder<CommandSource> builder = Commands.literal("sgear_parts")
                .requires(source -> source.hasPermissionLevel(2));

        // Add
        builder.then(
                Commands.literal("add").then(
                        Commands.argument("partID", ResourceLocationArgument.resourceLocation()).executes(
                                SGearPartsCommand::runAdd
                        ).suggests(partIdSuggestions)
                )
        );
        // Remove
        builder.then(
                Commands.literal("remove").then(
                        Commands.argument("partID", ResourceLocationArgument.resourceLocation()).executes(
                                SGearPartsCommand::runRemoveById
                        ).suggests(partInGearSuggestions)
                ).then(
                        Commands.argument("partIndex", IntegerArgumentType.integer()).executes(
                                SGearPartsCommand::runRemoveByIndex
                        )
                )
        );

        dispatcher.register(builder);
    }

    private static int runAdd(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
        IGearPart part = getPart(ctx);
        if (part == null) return 0;
        ItemStack gear = getGear(ctx);
        if (gear.isEmpty()) return 0;

        Collection<PartData> partList = GearData.getConstructionParts(gear);
        partList.add(PartData.of(part));
        GearData.writeConstructionParts(gear, partList);
        GearData.recalculateStats(ctx.getSource().asPlayer(), gear);
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
        ctx.getSource().sendFeedback(text("remove.success"), true);
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

    private static ItemStack getGear(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
        if (ctx.getSource().getEntity() instanceof EntityPlayerMP) {
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
        return new TextComponentTranslation("command.silentgear.parts." + key, args);
    }
}
