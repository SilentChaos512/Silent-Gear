package net.silentchaos512.gear.gear.material.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.api.material.modifier.IMaterialModifier;
import net.silentchaos512.gear.api.material.modifier.IMaterialModifierType;
import net.silentchaos512.gear.api.util.ChargedProperties;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.setup.gear.GearProperties;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.gear.util.Const;

import java.util.function.Function;
import java.util.function.Supplier;

public abstract class ChargedMaterialModifier implements IMaterialModifier {
    protected final int level;

    protected ChargedMaterialModifier(int level) {
        this.level = level;
    }

    public ChargedProperties getChargedProperties(MaterialInstance material) {
        var chargingValue = material.getProperty(PartTypes.MAIN.get(), GearProperties.CHARGING_VALUE.get());
        return new ChargedProperties(level, chargingValue);
    }

    public static class Type<T extends ChargedMaterialModifier> implements IMaterialModifierType<T> {
        private final Function<Integer, T> factory;
        private final Supplier<DataComponentType<Integer>> dataComponentType;
        private final MapCodec<T> codec;
        private final StreamCodec<RegistryFriendlyByteBuf, T> streamCodec;

        public Type(Function<Integer, T> factory, Supplier<DataComponentType<Integer>> dataComponentType) {
            this.factory = factory;
            this.dataComponentType = dataComponentType;
            this.codec = RecordCodecBuilder.mapCodec(
                    instance -> instance.group(
                            Codec.INT.fieldOf("level").forGetter(m -> m.level)
                    ).apply(instance, factory)
            );
            this.streamCodec = StreamCodec.composite(
                    ByteBufCodecs.VAR_INT, m -> m.level,
                    this.factory
            );
        }

        public int checkLevel(ItemStack stack) {
            var i = stack.get(this.dataComponentType.get());
            return i != null ? i : 0;
        }

        public T create(int level) {
            return factory.apply(level);
        }

        @Override
        public ResourceLocation getId() {
            return Const.STARCHARGED;
        }

        @Override
        public void addModifier(T mod, ItemStack stack) {
            stack.set(this.dataComponentType.get(), mod.level);
            if (this.causesFoilEffect()) {
                stack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
            }
        }

        @Override
        public void removeModifier(ItemStack stack) {
            if (!stack.isEmpty()) {
                stack.remove(this.dataComponentType.get());
                if (this.causesFoilEffect()) {
                    stack.remove(DataComponents.ENCHANTMENT_GLINT_OVERRIDE);
                }
            }
        }

        boolean causesFoilEffect() {
            return true;
        }

        @Override
        public MapCodec<T> codec() {
            return this.codec;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, T> streamCodec() {
            return this.streamCodec;
        }
    }
}
