package net.silentchaos512.gear.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.util.Mth;
import net.minecraft.network.chat.Component;
import net.silentchaos512.gear.api.item.GearItem;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.gear.util.TextUtil;

public final class SetDamageCommand {
    private SetDamageCommand() {}

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("set_damage")
                .requires(source -> source.hasPermission(2))
                .then(
                        Commands.argument("amount", IntegerArgumentType.integer())
                                .executes(context ->
                                        run(context, IntegerArgumentType.getInteger(context, "amount"))
                                )
                )
                .then(
                        Commands.literal("max")
                                .executes(context ->
                                        run(context, -1)
                                )
                )
        );
    }

    private static int run(CommandContext<CommandSourceStack> context, int amount) throws CommandSyntaxException {
        ServerPlayer playerMP = context.getSource().getPlayerOrException();
        ItemStack stack = playerMP.getMainHandItem();

        if (stack.isDamageableItem()) {
            // amount of -1 indicates "max" value
            int correctedAmount = amount < 0 ? getMaxDamage(stack) : amount;
            int clamped = Mth.clamp(correctedAmount, 0, getMaxDamage(stack));

            stack.setDamageValue(clamped);

            if (stack.getItem() instanceof GearItem) {
                GearData.recalculateGearData(stack, playerMP);
            }

            return 1;
        } else {
            Component msg = TextUtil.translate("command", "set_damage.notDamageable", stack.getHoverName());
            context.getSource().sendFailure(msg);
            return 0;
        }
    }

    private static int getMaxDamage(ItemStack stack) {
        if (GearHelper.isGear(stack)) {
            return stack.getMaxDamage() - 1;
        }
        return stack.getMaxDamage();
    }
}
