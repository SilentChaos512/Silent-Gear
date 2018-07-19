package net.silentchaos512.gear.api.lib;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.lib.util.StackHelper;

import javax.annotation.Nonnull;
import java.util.Random;

public enum MaterialGrade {

    NONE(0), E(-8), D(-4), C(0), B(5), A(10), S(20), SS(30), SSS(40);

    public static final String NBT_KEY = SilentGear.MOD_ID + "_grade";

    public final int bonusPercent;

    MaterialGrade(int bonusPercent) {
        this.bonusPercent = bonusPercent;
    }

    public static MaterialGrade fromStack(ItemStack stack) {
        if (StackHelper.isValid(stack) && stack.hasTagCompound() && stack.getTagCompound().hasKey(NBT_KEY)) {
            String str = stack.getTagCompound().getString(NBT_KEY);
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
        if (StackHelper.isValid(stack)) {
            if (!stack.hasTagCompound()) {
                stack.setTagCompound(new NBTTagCompound());
            }
            stack.getTagCompound().setString(NBT_KEY, name());
        }
    }

    public String getTranslatedName() {
        return SilentGear.localization.getLocalizedString("stat", "grade." + name());
    }
}
