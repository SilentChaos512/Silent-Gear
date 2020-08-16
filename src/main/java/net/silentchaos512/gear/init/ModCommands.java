package net.silentchaos512.gear.init;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.silentchaos512.gear.command.*;

public final class ModCommands {
    private ModCommands() {throw new IllegalAccessError("Utility class");}

    public static void registerAll(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSource> dispatcher = event.getDispatcher();
        LockStatsCommand.register(dispatcher);
        RecalculateStatsCommand.register(dispatcher);
        SetDamageCommand.register(dispatcher);
        SGearMaterialsCommand.register(dispatcher);
        SGearPartsCommand.register(dispatcher);
        SGearStatsCommand.register(dispatcher);
        SGearTraitsCommand.register(dispatcher);
        RandomGearCommand.register(dispatcher);
    }
}
