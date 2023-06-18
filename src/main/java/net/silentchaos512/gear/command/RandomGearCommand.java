package net.silentchaos512.gear.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.util.GearGenerator;
import net.silentchaos512.lib.util.NameUtils;
import net.silentchaos512.lib.util.PlayerUtils;

import java.util.Collection;

public final class RandomGearCommand {
    private static final SuggestionProvider<CommandSourceStack> itemIdSuggestions = (context, builder) ->
            SharedSuggestionProvider.suggestResource(ForgeRegistries.ITEMS.getValues().stream().filter(item -> item instanceof ICoreItem).map(NameUtils::fromItem), builder);

    private RandomGearCommand() {}

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("sgear_random_gear")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("players", EntityArgument.players())
                        .then(Commands.argument("item", ResourceLocationArgument.id())
                                .suggests(itemIdSuggestions)
                                .executes(context -> run(
                                        context,
                                        EntityArgument.getPlayers(context, "players"),
                                        ResourceLocationArgument.getId(context, "item"),
                                        3
                                ))
                                .then(Commands.argument("tier", IntegerArgumentType.integer())
                                        .executes(context -> run(
                                                context,
                                                EntityArgument.getPlayers(context, "players"),
                                                ResourceLocationArgument.getId(context, "item"),
                                                IntegerArgumentType.getInteger(context, "tier")
                                        ))
                                )
                        )
                )
        );
    }

    private static int run(CommandContext<CommandSourceStack> context, Collection<ServerPlayer> players, ResourceLocation itemId, int tier) throws CommandSyntaxException {
        Item item = ForgeRegistries.ITEMS.getValue(itemId);
        if (!(item instanceof ICoreItem)) {
            context.getSource().sendFailure(Component.translatable("command.silentgear.randomGear.invalidItem"));
            return 0;
        }

        for (ServerPlayer player : players) {
            ItemStack stack = GearGenerator.create((ICoreItem) item, tier);
            if (!stack.isEmpty()) {
                context.getSource().sendSuccess(() -> Component.translatable("commands.give.success.single", 1, stack.getDisplayName(), player.getDisplayName()), true);
                PlayerUtils.giveItem(player, stack.copy());
            }
        }

        return 1;
    }
}
