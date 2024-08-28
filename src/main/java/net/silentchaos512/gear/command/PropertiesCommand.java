package net.silentchaos512.gear.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.api.item.GearItem;
import net.silentchaos512.gear.api.part.PartList;
import net.silentchaos512.gear.api.property.GearPropertyMap;
import net.silentchaos512.gear.api.property.GearPropertyValue;
import net.silentchaos512.gear.api.util.PropertyKey;
import net.silentchaos512.gear.gear.part.PartInstance;
import net.silentchaos512.gear.setup.SgRegistries;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.gear.util.TextUtil;
import net.silentchaos512.lib.util.PlayerUtils;

import java.util.Collection;

public final class PropertiesCommand {
    private PropertiesCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("sgear_properties")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("info")
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(ctx -> runInfo(ctx, EntityArgument.getPlayer(ctx, "player")))
                        )
                        .executes(ctx -> runInfo(ctx, ctx.getSource().getPlayerOrException()))
                )
                .then(Commands.literal("recalculate")
                        .then(Commands.argument("players", EntityArgument.players())
                                .executes(ctx -> runRecalculate(ctx, EntityArgument.getPlayers(ctx, "players")))
                        )
                )
        );
    }

    private static int runInfo(CommandContext<CommandSourceStack> context, ServerPlayer player) {
        ItemStack stack = player.getMainHandItem();

        if (!GearHelper.isGear(stack)) {
            context.getSource().sendFailure(TextUtil.translate("command", "invalidItemType", stack.getHoverName()));
            return 0;
        }

        context.getSource().sendSuccess(
                () -> TextUtil.translate("command", "stats.info.header", player.getName(), stack.getHoverName())
                        .withStyle(ChatFormatting.BOLD),
                true
        );

        GearItem item = (GearItem) stack.getItem();
        PartList parts = GearData.getConstruction(stack).parts();
        GearPropertyMap properties = parts.getPropertyModifiersFromParts(item.getGearType());

        for (var property : SgRegistries.GEAR_PROPERTY) {
            var key = PropertyKey.of(property, item.getGearType());
            Collection<GearPropertyValue<?>> mods = properties.get(key);

            if (!mods.isEmpty()) {
                Component name = TextUtil.withColor(property.getDisplayName(), property.getGroup().getColor());
                Component modsText = GearPropertyMap.formatTextUnchecked(mods, property, true);
                Component valueText = TextUtil.withColor(
                        property.formatModifiersWithColorUnchecked(mods, true),
                        ChatFormatting.YELLOW
                );

                context.getSource().sendSuccess(
                        () -> TextUtil.translate("command", "stats.info.format", name, modsText, valueText),
                        true
                );

                for (PartInstance part : parts) {
                    var partMods = part.getPropertyModifiers(part.getType(), key);
                    if (!partMods.isEmpty()) {
                        Component partName = part.getDisplayName(part.getType());
                        //noinspection unchecked
                        Component partModsText = GearPropertyMap.formatTextUnchecked(partMods, property, true);

                        context.getSource().sendSuccess(
                                () -> TextUtil.translate("command", "stats.info.formatPart", partName, partModsText),
                                true
                        );
                    }
                }
            }
        }

        return 1;
    }

    private static int runRecalculate(CommandContext<CommandSourceStack> context, Collection<ServerPlayer> players) {
        for (ServerPlayer player : players) {
            for (ItemStack stack : PlayerUtils.getNonEmptyStacks(player)) {
                if (GearHelper.isGear(stack)) {
                    GearData.recalculateGearData(stack, player);
                }
            }
            context.getSource().sendSuccess(() -> Component.translatable("command.silentgear.recalculate", player.getScoreboardName()), true);
        }
        return 1;
    }
}
