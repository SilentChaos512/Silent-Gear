package net.silentchaos512.gear.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.parts.PartDataList;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.api.stats.StatInstance;
import net.silentchaos512.gear.api.stats.StatModifierMap;
import net.silentchaos512.gear.gear.part.PartData;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.gear.util.TextUtil;

import java.util.Collection;

public final class SGearStatsCommand {
    private SGearStatsCommand() {}

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("sgear_stats")
                .requires(source -> source.hasPermissionLevel(2))
                .then(Commands.literal("info")
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(ctx -> run(ctx, EntityArgument.getPlayer(ctx, "player")))
                        )
                        .executes(ctx -> run(ctx, ctx.getSource().asPlayer()))
                )
        );
    }

    private static int run(CommandContext<CommandSource> context, ServerPlayerEntity player) {
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
            Collection<StatInstance> mods = stats.get(stat);
            if (!mods.isEmpty()) {
                ITextComponent name = TextUtil.withColor(stat.getDisplayName(), stat.getNameColor());
                ITextComponent modsText = StatModifierMap.formatText(mods, stat, 5, true);
                ITextComponent valueText = TextUtil.withColor(
                        StatInstance.of(stat.compute(0f, true, mods))
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
}
