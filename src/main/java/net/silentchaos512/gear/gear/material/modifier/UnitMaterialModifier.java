package net.silentchaos512.gear.gear.material.modifier;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.api.material.modifier.IMaterialModifier;
import net.silentchaos512.gear.api.material.modifier.IMaterialModifierType;

import java.util.Optional;
import java.util.function.Supplier;

public abstract class UnitMaterialModifier implements IMaterialModifier {
    @Override
    public MutableComponent modifyMaterialName(MutableComponent name) {
        // No changes by default
        return name;
    }

    public static class Type<M extends UnitMaterialModifier> implements IMaterialModifierType<M> {
        private final ResourceLocation id;
        private final M instance;
        private final Supplier<DataComponentType<Unit>> dataComponent;
        private final MapCodec<M> codec;
        private final StreamCodec<RegistryFriendlyByteBuf, M> streamCodec;

        public Type(ResourceLocation id, M instance, Supplier<DataComponentType<Unit>> dataComponent, MapCodec<M> codec, StreamCodec<RegistryFriendlyByteBuf, M> streamCodec) {
            this.id = id;
            this.instance = instance;
            this.dataComponent = dataComponent;
            this.codec = codec;
            this.streamCodec = streamCodec;
        }

        @Override
        public ResourceLocation getId() {
            return this.id;
        }

        @Override
        public Optional<M> readModifier(ItemStack stack) {
            return stack.has(this.dataComponent) ? Optional.of(this.instance) : Optional.empty();
        }

        @Override
        public void addModifier(M mod, ItemStack stack) {
            stack.set(this.dataComponent, Unit.INSTANCE);
        }

        @Override
        public void removeModifier(ItemStack stack) {
            stack.remove(this.dataComponent);
        }

        @Override
        public MapCodec<M> codec() {
            return this.codec;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, M> streamCodec() {
            return this.streamCodec;
        }
    }
}
