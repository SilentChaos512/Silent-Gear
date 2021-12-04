package net.silentchaos512.gear.gear.material.modifier;

import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.api.material.IMaterialInstance;
import net.silentchaos512.gear.api.material.modifier.IMaterialModifier;
import net.silentchaos512.gear.api.material.modifier.IMaterialModifierType;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.stats.ChargedProperties;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.util.Const;

import javax.annotation.Nullable;
import java.util.function.BiFunction;

public abstract class ChargedMaterialModifier implements IMaterialModifier {
    protected final IMaterialInstance material;
    protected final int level;

    protected ChargedMaterialModifier(IMaterialInstance material, int level) {
        this.material = material;
        this.level = level;
    }

    public ChargedProperties getChargedProperties() {
        return new ChargedProperties(level, material.getStat(PartType.MAIN, ItemStats.CHARGEABILITY));
    }

    public static class Type<T extends ChargedMaterialModifier> implements IMaterialModifierType<T> {
        private final BiFunction<IMaterialInstance, Integer, T> factory;
        private final String nbtTagName;

        public Type(BiFunction<IMaterialInstance, Integer, T> factory, String nbtTagName) {
            this.factory = factory;
            this.nbtTagName = nbtTagName;
        }

        public int checkLevel(ItemStack stack) {
            return stack.getOrCreateTag().getShort(nbtTagName);
        }

        public T create(IMaterialInstance material, int level) {
            return factory.apply(material, level);
        }

        @Override
        public void removeModifier(ItemStack stack) {
            stack.getOrCreateTag().remove(nbtTagName);
            stack.getOrCreateTag().remove(Const.NBT_IS_FOIL);
        }

        boolean causesFoilEffect() {
            return true;
        }

        @Nullable
        @Override
        public IMaterialModifier read(IMaterialInstance material) {
            int level = material.getItem().getOrCreateTag().getShort(nbtTagName);

            if (level == 0) {
                return null;
            }

            return factory.apply(material, level);
        }

        @Override
        public void write(T modifier, ItemStack stack) {
            stack.getOrCreateTag().putShort(nbtTagName, (short) modifier.level);
            if (causesFoilEffect()) {
                stack.getOrCreateTag().putBoolean(Const.NBT_IS_FOIL, true);
            }
        }
    }
}
