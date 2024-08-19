package net.silentchaos512.gear.setup;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.silentchaos512.gear.command.*;

public final class SgCommands {
    private SgCommands() {throw new IllegalAccessError("Utility class");}

    public static void registerAll(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        GradeCommand.register(dispatcher);
        SetDamageCommand.register(dispatcher);
        MaterialsCommand.register(dispatcher);
        PartsCommand.register(dispatcher);
        PropertiesCommand.register(dispatcher);
        TraitsCommand.register(dispatcher);
        RandomGearCommand.register(dispatcher);
    }
}
