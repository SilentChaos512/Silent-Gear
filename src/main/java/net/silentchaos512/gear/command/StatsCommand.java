package net.silentchaos512.gear.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.part.PartDataList;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.api.stats.StatInstance;
import net.silentchaos512.gear.api.stats.StatModifierMap;
import net.silentchaos512.gear.api.util.StatGearKey;
import net.silentchaos512.gear.gear.part.PartData;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.gear.util.TextUtil;
import net.silentchaos512.lib.util.PlayerUtils;

import java.util.Collection;

public final class StatsCommand {
    private StatsCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("sgear_stats")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("info")
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(ctx -> runInfo(ctx, EntityArgument.getPlayer(ctx, "player")))
                        )
                        .executes(ctx -> runInfo(ctx, ctx.getSource().getPlayerOrException()))
                )
                .then(Commands.literal("lock")
                        .executes(StatsCommand::runLockStats)
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
                TextUtil.translate("command", "stats.info.header", player.getName(), stack.getHoverName())
                        .withStyle(ChatFormatting.BOLD),
                true
        );

        ICoreItem item = (ICoreItem) stack.getItem();
        PartDataList parts = GearData.getConstructionParts(stack);
        StatModifierMap stats = GearData.getStatModifiers(stack, item, parts);

        for (ItemStat stat : ItemStats.allStatsOrderedExcluding((item).getExcludedStats(stack))) {
            StatGearKey key = StatGearKey.of(stat, item.getGearType());
            Collection<StatInstance> mods = stats.get(key);

            if (!mods.isEmpty()) {
                Component name = TextUtil.withColor(stat.getDisplayName(), stat.getNameColor());
                Component modsText = StatModifierMap.formatText(mods, stat, 5, true);
                float statValue = stat.compute(0f, true, item.getGearType(), mods);
                Component valueText = TextUtil.withColor(
                        StatInstance.of(statValue, StatInstance.Operation.AVG, key)
                                .getFormattedText(stat, 5, false),
                        ChatFormatting.YELLOW);

                context.getSource().sendSuccess(
                        TextUtil.translate("command", "stats.info.format", name, modsText, valueText),
                        true
                );

                for (PartData part : parts) {
                    Collection<StatInstance> partMods = part.getStatModifiers(key, stack);
                    if (!partMods.isEmpty()) {
                        Component partName = part.getDisplayName(stack);
                        Component partModsText = StatModifierMap.formatText(partMods, stat, 5, true);

                        context.getSource().sendSuccess(
                                TextUtil.translate("command", "stats.info.formatPart", partName, partModsText),
                                true
                        );
                    }
                }
            }
        }

        return 1;
    }

    private static int runLockStats(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer playerMP = context.getSource().getPlayerOrException();
        ItemStack stack = playerMP.getMainHandItem();
        if (GearHelper.isGear(stack)) {
            boolean locked = !GearData.hasLockedStats(stack);
            GearData.setLockedStats(stack, locked);
            String translationKey = "command.silentgear.lock_stats." + (locked ? "locked" : "unlocked");
            context.getSource().sendSuccess(Component.translatable(translationKey, stack.getHoverName()), true);
        } else {
            context.getSource().sendFailure(Component.translatable("command.silentgear.lock_stats.invalid"));
        }
        return 1;
    }

    private static int runRecalculate(CommandContext<CommandSourceStack> context, Collection<ServerPlayer> players) {
        for (ServerPlayer player : players) {
            for (ItemStack stack : PlayerUtils.getNonEmptyStacks(player)) {
                if (GearHelper.isGear(stack)) {
                    GearData.recalculateStats(stack, player);
                }
            }
            context.getSource().sendSuccess(Component.translatable("command.silentgear.recalculate", player.getScoreboardName()), true);
        }
        return 1;
    }
}
