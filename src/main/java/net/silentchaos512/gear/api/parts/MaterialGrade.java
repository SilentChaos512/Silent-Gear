/*
 * Silent Gear -- MaterialGrade
 * Copyright (C) 2018 SilentChaos512
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 3
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.silentchaos512.gear.api.parts;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

public enum MaterialGrade {
    NONE(0), E(-8), D(-4), C(0), B(5), A(10), S(20), SS(30), SSS(40);

    private static final String NBT_KEY = "SGear_Grade";

    public final int bonusPercent;

    MaterialGrade(int bonusPercent) {
        this.bonusPercent = bonusPercent;
    }

    public static MaterialGrade fromStack(ItemStack stack) {
        if (!stack.isEmpty() && stack.hasTag() && stack.getOrCreateTag().contains(NBT_KEY)) {
            String str = stack.getOrCreateTag().getString(NBT_KEY);
            return fromString(str);
        }
        return MaterialGrade.NONE;
    }

    public static MaterialGrade fromString(String str) {
        if (!str.isEmpty()) {
            for (MaterialGrade grade : values()) {
                if (grade.name().equalsIgnoreCase(str)) {
                    return grade;
                }
            }
        }
        return MaterialGrade.NONE;
    }

    public static MaterialGrade selectRandom(Random random) {
        return selectRandom(random, MaterialGrade.SSS);
    }

    /**
     * Select a random grade with default settings (median B and 1.5 standard deviation)
     *
     * @param random   A random object to use.
     * @param maxGrade The highest grade that can be selected
     * @return A MaterialGrade that is not NONE
     */
    public static MaterialGrade selectRandom(Random random, MaterialGrade maxGrade) {
        // If I understand the math here, 95% of the time, we should get grades between E and SS,
        // inclusive. SSS should be about 2.5% chance, I think. E picks up the 2.5% on the low end.
        return selectRandom(random, MaterialGrade.B, 1.5, maxGrade);
    }

    /**
     * Select a random grade with the given parameters. Grades are normally distributed. That means
     * the median is most common, and each grade above/below is rarer.
     *
     * @param random   A random object to use.
     * @param median   The median grade. This is the most common, in the center of the bell curve.
     * @param stdDev   The standard deviation. Larger values make a flatter distribution.
     * @param maxGrade The highest grade that can be selected
     * @return A MaterialGrade that is not NONE
     */
    public static MaterialGrade selectRandom(Random random, MaterialGrade median, double stdDev, MaterialGrade maxGrade) {
        int val = (int) (stdDev * random.nextGaussian() + median.ordinal());
        val = MathHelper.clamp(val, 1, maxGrade.ordinal());
        return values()[val];
    }

    public void setGradeOnStack(@Nonnull ItemStack stack) {
        if (!stack.isEmpty()) {
            stack.getOrCreateTag().putString(NBT_KEY, name());
        }
    }

    public ITextComponent getDisplayName() {
        TextComponentTranslation text = new TextComponentTranslation("stat.silentgear.grade." + name());
        return new TextComponentTranslation("part.silentgear.gradeOnPart", text);
    }

    public static class Argument implements ArgumentType<MaterialGrade> {
        @Override
        public MaterialGrade parse(StringReader reader) {
            return MaterialGrade.fromString(reader.readUnquotedString());
        }

        @Override
        public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
            return ISuggestionProvider.suggest(Arrays.stream(values()).map(MaterialGrade::name), builder);
        }

        public static MaterialGrade getGrade(CommandContext<CommandSource> context, String name) {
            return context.getArgument(name, MaterialGrade.class);
        }
    }
}
