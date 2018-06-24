package net.silentchaos512.gear.api.lib;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.lib.util.StackHelper;

import javax.annotation.Nonnull;
import java.util.Random;

public enum MaterialGrade {

    NONE(0), E(2), D(4), C(6), B(8), A(12), S(16), SS(24), SSS(32);

    public static final String NBT_KEY = "ToolCore_Grade";

    public final int bonusPercent;

    private MaterialGrade(int bonusPercent) {

        this.bonusPercent = bonusPercent;
    }

    public static MaterialGrade fromStack(ItemStack stack) {

        if (StackHelper.isValid(stack) && stack.hasTagCompound() && stack.getTagCompound().hasKey(NBT_KEY)) {
            String str = stack.getTagCompound().getString(NBT_KEY);
            return fromString(str);
        }
        return MaterialGrade.NONE;
    }

    @Nonnull
    public static MaterialGrade fromString(String str) {

        if (str != null && !str.isEmpty()) {
            for (MaterialGrade grade : values()) {
                if (grade.name().equalsIgnoreCase(str)) {
                    return grade;
                }
            }
        }
        return MaterialGrade.NONE;
    }

    @Nonnull
    public static MaterialGrade selectRandom(Random random) {

        // If I understand the math here, 95% of the time, we should get grades between E and SS,
        // inclusive. SSS should be about 2.5% chance, I think. E picks up the 2.5% on the low end.
        int val = (int) (1.5 * random.nextGaussian() + 4);
        val = MathHelper.clamp(val, 1, 8);
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

    public String getLocalizedName() {

        return SilentGear.localization.getLocalizedString("stat", "grade." + name());
    }
}
