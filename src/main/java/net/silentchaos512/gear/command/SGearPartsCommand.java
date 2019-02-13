package net.silentchaos512.gear.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.parts.IGearPart;
import net.silentchaos512.gear.api.parts.PartDataList;
import net.silentchaos512.gear.parts.PartData;
import net.silentchaos512.gear.parts.PartManager;
import net.silentchaos512.gear.util.GearData;

import javax.annotation.Nullable;
import java.util.Collection;

public class SGearPartsCommand {
    enum Subs {
        ADD {
            @Override
            void build(LiteralArgumentBuilder<CommandSource> builder) {
                builder.then(Commands.literal("add"
                ).then(Commands.argument("partID", StringArgumentType.greedyString())
                ).executes(this::run));
            }

            private int run(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
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
        },
        REMOVE {
            @Override
            void build(LiteralArgumentBuilder<CommandSource> builder) {
                builder.then(Commands.literal("remove"
                ).then(Commands.argument("partID", StringArgumentType.string())
                ).executes(this::run));
            }

            private int run(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
                IGearPart part = getPart(ctx);
                if (part == null) return 0;
                ItemStack gear = getGear(ctx);
                if (gear.isEmpty()) return 0;

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
                    ctx.getSource().sendFeedback(new TextComponentTranslation("command.silentgear.parts.remove.success"), true);
                    return 1;
                } else {
                    ctx.getSource().sendErrorMessage(new TextComponentTranslation("command.silentgear.parts.gearDoesNotContainPart"));
                    return 0;
                }
            }
        };

        abstract void build(LiteralArgumentBuilder<CommandSource> builder);
    }

    @Nullable
    static IGearPart getPart(CommandContext<CommandSource> ctx) {
//        ResourceLocation id = ResourceLocationArgument.getResourceLocation(ctx, "partID");
        ResourceLocation id = new ResourceLocation(StringArgumentType.getString(ctx, "partID"));
        IGearPart part = PartManager.get(id);
        if (part == null) {
            ctx.getSource().sendErrorMessage(new TextComponentTranslation("command.silentgear.partNotFound", id));
        }
        return part;
    }

    static ItemStack getGear(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
        if (ctx.getSource().getEntity() instanceof EntityPlayerMP) {
            ItemStack gear = ctx.getSource().asPlayer().getHeldItemMainhand();
            if (gear.getItem() instanceof ICoreItem) {
                return gear;
            }
        } else {
            ctx.getSource().sendErrorMessage(new TextComponentTranslation("command.silentgear.sourceMustBePlayer"));
        }
        return ItemStack.EMPTY;
    }

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        LiteralArgumentBuilder<CommandSource> builder = Commands.literal("sgear_parts")
                .requires(source -> source.hasPermissionLevel(2));

        for (Subs subcommand : Subs.values()) {
            subcommand.build(builder);
        }

        dispatcher.register(builder);
    }
}
