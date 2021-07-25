package net.silentchaos512.gear.init;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.silentchaos512.gear.command.*;

public final class ModCommands {
    private ModCommands() {throw new IllegalAccessError("Utility class");}

    public static void registerAll(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        SetDamageCommand.register(dispatcher);
        MaterialsCommand.register(dispatcher);
        PartsCommand.register(dispatcher);
        StatsCommand.register(dispatcher);
        TraitsCommand.register(dispatcher);
        RandomGearCommand.register(dispatcher);
    }
}
