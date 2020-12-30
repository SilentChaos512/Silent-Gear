package net.silentchaos512.gear.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.part.PartDataList;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.api.stats.StatInstance;
import net.silentchaos512.gear.api.stats.StatModifierMap;
import net.silentchaos512.gear.gear.part.PartData;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.gear.util.TextUtil;
import net.silentchaos512.lib.util.PlayerUtils;

import java.util.Collection;

public final class StatsCommand {
    private StatsCommand() {
    }

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("sgear_stats")
                .requires(source -> source.hasPermissionLevel(2))
                .then(Commands.literal("info")
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(ctx -> runInfo(ctx, EntityArgument.getPlayer(ctx, "player")))
                        )
                        .executes(ctx -> runInfo(ctx, ctx.getSource().asPlayer()))
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

    private static int runInfo(CommandContext<CommandSource> context, ServerPlayerEntity player) {
        ItemStack stack = player.getHeldItemMainhand();

        if (!GearHelper.isGear(stack)) {
            context.getSource().sendErrorMessage(TextUtil.translate("command", "invalidItemType", stack.getDisplayName()));
            return 0;
        }

        context.getSource().sendFeedback(
                TextUtil.translate("command", "stats.info.header", player.getName(), stack.getDisplayName())
                        .mergeStyle(TextFormatting.BOLD),
                true
        );

        ICoreItem item = (ICoreItem) stack.getItem();
        PartDataList parts = GearData.getConstructionParts(stack);
        StatModifierMap stats = GearData.getStatModifiers(stack, item, parts);

        for (ItemStat stat : ItemStats.allStatsOrderedExcluding((item).getExcludedStats(stack))) {
            Collection<StatInstance> mods = stats.get(stat, item.getGearType());
            if (!mods.isEmpty()) {
                ITextComponent name = TextUtil.withColor(stat.getDisplayName(), stat.getNameColor());
                ITextComponent modsText = StatModifierMap.formatText(mods, stat, 5, true);
                ITextComponent valueText = TextUtil.withColor(
                        StatInstance.of(stat.compute(0f, true, item.getGearType(), mods))
                                .getFormattedText(stat, 5, false),
                        TextFormatting.YELLOW);

                context.getSource().sendFeedback(
                        TextUtil.translate("command", "stats.info.format", name, modsText, valueText),
                        true
                );

                for (PartData part : parts) {
                    Collection<StatInstance> partMods = part.getStatModifiers(stack, stat);
                    if (!partMods.isEmpty()) {
                        ITextComponent partName = part.getDisplayName(stack);
                        ITextComponent partModsText = StatModifierMap.formatText(partMods, stat, 5, true);

                        context.getSource().sendFeedback(
                                TextUtil.translate("command", "stats.info.formatPart", partName, partModsText),
                                true
                        );
                    }
                }
            }
        }

        return 1;
    }

    private static int runLockStats(CommandContext<CommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity playerMP = context.getSource().asPlayer();
        ItemStack stack = playerMP.getHeldItemMainhand();
        if (GearHelper.isGear(stack)) {
            boolean locked = !GearData.hasLockedStats(stack);
            GearData.setLockedStats(stack, locked);
            String translationKey = "command.silentgear.lock_stats." + (locked ? "locked" : "unlocked");
            context.getSource().sendFeedback(new TranslationTextComponent(translationKey, stack.getDisplayName()), true);
        } else {
            context.getSource().sendErrorMessage(new TranslationTextComponent("command.silentgear.lock_stats.invalid"));
        }
        return 1;
    }

    private static int runRecalculate(CommandContext<CommandSource> context, Collection<ServerPlayerEntity> players) {
        for (ServerPlayerEntity player : players) {
            for (ItemStack stack : PlayerUtils.getNonEmptyStacks(player)) {
                if (GearHelper.isGear(stack)) {
                    GearData.recalculateStats(stack, player);
                }
            }
            context.getSource().sendFeedback(new TranslationTextComponent("command.silentgear.recalculate", player.getScoreboardName()), true);
        }
        return 1;
    }
}
