package net.silentchaos512.gear.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.ResourceLocationArgument;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistries;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.init.ModItems;
import net.silentchaos512.gear.util.GearGenerator;
import net.silentchaos512.lib.util.PlayerUtils;

public final class RandomGearCommand {
    private static final SuggestionProvider<CommandSource> itemIdSuggestions = (context, builder) ->
            ISuggestionProvider.func_212476_a(ModItems.gearClasses.keySet().stream().map(SilentGear::getId), builder);

    private RandomGearCommand() {}

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("sgear_random_gear")
                .requires(source -> source.hasPermissionLevel(2))
                .then(Commands.argument("item", ResourceLocationArgument.resourceLocation())
                        .suggests(itemIdSuggestions)
                        .executes(context -> run(
                                context,
                                ResourceLocationArgument.getResourceLocation(context, "item"),
                                3
                        ))
                        .then(Commands.argument("tier", IntegerArgumentType.integer())
                                .executes(context -> run(
                                        context,
                                        ResourceLocationArgument.getResourceLocation(context, "item"),
                                        IntegerArgumentType.getInteger(context, "tier")
                                ))
                        )
                )
        );
    }

    private static int run(CommandContext<CommandSource> context, ResourceLocation itemId, int tier) throws CommandSyntaxException {
        Item item = ForgeRegistries.ITEMS.getValue(itemId);
        if (!(item instanceof ICoreItem)) {
            context.getSource().sendErrorMessage(new TranslationTextComponent("command.silentgear.randomGear.invalidItem"));
            return 0;
        }

        ItemStack stack = GearGenerator.create((ICoreItem) item, tier);
        if (!stack.isEmpty()) {
            PlayerUtils.giveItem(context.getSource().asPlayer(), stack);
            return 1;
        }

        return 0;
    }
}
