package net.silentchaos512.gear.api.part;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.silentchaos512.gear.config.Config;
import net.silentchaos512.utils.EnumUtils;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

public enum MaterialGrade {
    NONE(0), E(1), D(2), C(3), B(4), A(5), S(10), SS(15), SSS(25), MAX(30);

    private static final String NBT_KEY = "SGear_Grade";

    public final int bonusPercent;

    MaterialGrade(int bonusPercent) {
        this.bonusPercent = bonusPercent;
    }

    /**
     * Gets the highest possible grade (MAX). This should be preferred over explicitly referencing
     * the last value, so that users can mod the mod with custom grades easily.
     *
     * @return Returns the highest grade (MAX)
     */
    public static MaterialGrade getMax() {
        return values()[values().length - 1];
    }

    public static MaterialGrade fromStack(ItemStack stack) {
        if (!stack.isEmpty() && stack.hasTag() && stack.getOrCreateTag().contains(NBT_KEY)) {
            String str = stack.getOrCreateTag().getString(NBT_KEY);
            return fromString(str);
        }
        return NONE;
    }

    public static MaterialGrade fromString(String str) {
        if (!str.isEmpty()) {
            for (MaterialGrade grade : values()) {
                if (grade.name().equalsIgnoreCase(str)) {
                    return grade;
                }
            }
        }
        return NONE;
    }

    /**
     * Select a random grade, with median based on the catalyst tier. Standard median is C at
     * catalyst tier 1, each catalyst tier adds one to the median.
     *
     * @param random       A {@link Random} object to use
     * @param catalystTier The catalyst tier (or zero if there is no catalyst)
     * @return A random grade that is not NONE
     */
    public static MaterialGrade selectWithCatalyst(Random random, @Nonnegative int catalystTier) {
        int ordinal = Config.Common.graderMedianGrade.get().ordinal() + catalystTier - 1;
        MaterialGrade median = EnumUtils.byOrdinal(ordinal, getMax());
        return selectRandom(random, median, Config.Common.graderStandardDeviation.get(), getMax());
    }

    /**
     * Select a random grade with the given parameters. Grades are normally distributed. That means
     * the median is most common, and each grade above/below is rarer.
     *
     * @param random   A {@link Random} object to use
     * @param median   The median grade. This is the most common, in the center of the bell curve.
     * @param stdDev   The standard deviation. Larger values make a flatter distribution.
     * @param maxGrade The highest grade that can be selected
     * @return A random grade that is not NONE
     */
    public static MaterialGrade selectRandom(Random random, MaterialGrade median, double stdDev, MaterialGrade maxGrade) {
        int val = (int) Math.round(stdDev * random.nextGaussian() + median.ordinal());
        val = MathHelper.clamp(val, 1, maxGrade.ordinal());
        return values()[val];
    }

    public void setGradeOnStack(@Nonnull ItemStack stack) {
        if (!stack.isEmpty()) {
            if (this != NONE) {
                stack.getOrCreateTag().putString(NBT_KEY, name());
            } else {
                stack.getOrCreateTag().remove(NBT_KEY);
            }
        }
    }

    public IFormattableTextComponent getDisplayName() {
        return new TranslationTextComponent("stat.silentgear.grade." + name());
    }

    @Deprecated
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

    public static class Range {
        public static final Range OPEN = new Range(MaterialGrade.NONE, MaterialGrade.getMax());

        private final MaterialGrade min;
        private final MaterialGrade max;

        public Range(MaterialGrade min, MaterialGrade max) {
            this.min = min;
            this.max = max;

            if (this.min.ordinal() > this.max.ordinal()) {
                throw new IllegalArgumentException("min grade is greater than max grade");
            }
        }

        public boolean test(MaterialGrade grade) {
            int o = grade.ordinal();
            return o >= min.ordinal() && o <= max.ordinal();
        }

        public static Range deserialize(JsonElement json) {
            if (json.isJsonPrimitive()) {
                MaterialGrade grade = MaterialGrade.fromString(json.getAsString());
                return grade != NONE ? new Range(grade, grade) : OPEN;
            }
            JsonObject jsonObject = json.getAsJsonObject();
            String min = JSONUtils.getString(jsonObject, "min", "NONE");
            String max = JSONUtils.getString(jsonObject, "max", "MAX");
            return new Range(MaterialGrade.fromString(min), MaterialGrade.fromString(max));
        }
    }
}
