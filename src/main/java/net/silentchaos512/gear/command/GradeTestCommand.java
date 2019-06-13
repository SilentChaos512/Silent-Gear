package net.silentchaos512.gear.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.silentchaos512.gear.api.parts.MaterialGrade;

import java.util.EnumMap;
import java.util.Map;
import java.util.Random;

public final class GradeTestCommand {
    private GradeTestCommand() {}

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("sgear_grade_test")
                .requires(source -> source.hasPermissionLevel(2))
                .then(Commands.argument("median", new MaterialGrade.Argument())
                        .executes(
                                GradeTestCommand::run
                        )
                )
        );
    }

    private static int run(CommandContext<CommandSource> context) {
        Random random = new Random();
        MaterialGrade median = MaterialGrade.Argument.getGrade(context, "median");
        Map<MaterialGrade, Integer> map = new EnumMap<>(MaterialGrade.class);
        for (int i = 0; i < 10_000; ++i) {
            MaterialGrade grade = MaterialGrade.selectRandom(random, median, 1.5, MaterialGrade.SSS);
            map.put(grade, map.getOrDefault(grade, 0) + 1);
        }

        for (MaterialGrade grade : MaterialGrade.values()) {
            if (grade != MaterialGrade.NONE) {
                int amount = map.getOrDefault(grade, 0);
                ITextComponent text = new StringTextComponent(grade + ": " + amount);
                context.getSource().sendFeedback(text, true);
            }
        }

        return 1;
    }
}
