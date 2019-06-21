package net.silentchaos512.gear.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.ResourceLocationArgument;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.silentchaos512.gear.api.traits.ITrait;
import net.silentchaos512.gear.traits.TraitManager;

import java.util.stream.Collectors;

public final class SGearTraitsCommand {
    private static final SuggestionProvider<CommandSource> TRAIT_ID_SUGGESTIONS = (ctx, builder) ->
            ISuggestionProvider.func_212476_a(TraitManager.getValues().stream().map(ITrait::getId), builder);

    private SGearTraitsCommand() {}

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("sgear_traits")
                .then(Commands.literal("describe")
                        .then(Commands.argument("traitID", ResourceLocationArgument.resourceLocation())
                                .suggests(TRAIT_ID_SUGGESTIONS)
                                .executes(context -> runDescribe(context, ResourceLocationArgument.getResourceLocation(context, "traitID")))
                        )
                )
                .then(Commands.literal("list")
                        .executes(SGearTraitsCommand::runList)
                )
        );
    }

    private static int runDescribe(CommandContext<CommandSource> context, ResourceLocation traitId) {
        ITrait trait = TraitManager.get(traitId);
        if (trait == null) {
            context.getSource().sendErrorMessage(new TranslationTextComponent("command.silentgear.traits.traitNotFound", traitId));
            return 0;
        }

        context.getSource().sendFeedback(trait.getDisplayName(1), true);
        context.getSource().sendFeedback(trait.getDescription(1), true);
        context.getSource().sendFeedback(new TranslationTextComponent("command.silentgear.traits.maxLevel", trait.getMaxLevel()), true);
        context.getSource().sendFeedback(new StringTextComponent("Object: " + trait), true);
        context.getSource().sendFeedback(new StringTextComponent("Serializer: " + trait.getSerializer()), true);

        return 1;
    }

    private static int runList(CommandContext<CommandSource> context) {
        String listStr = TraitManager.getValues().stream()
                .map(trait -> trait.getId().toString())
                .collect(Collectors.joining(", "));
        context.getSource().sendFeedback(new StringTextComponent(listStr), true);
        context.getSource().sendFeedback(new StringTextComponent("Total: " + TraitManager.getValues().size()), true);

        return 1;
    }
}
