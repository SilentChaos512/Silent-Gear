package net.silentchaos512.gear.init;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.command.*;

public final class ModCommands {
    private ModCommands() {throw new IllegalAccessError("Utility class");}

    public static void registerAll(CommandDispatcher<CommandSource> dispatcher) {
        LockStatsCommand.register(dispatcher);
        RecalculateStatsCommand.register(dispatcher);
        SGearPartsCommand.register(dispatcher);
        SGearTraitsCommand.register(dispatcher);
        RandomGearCommand.register(dispatcher);

        if (SilentGear.isDevBuild()) {
            GradeTestCommand.register(dispatcher);
        }
    }
}
