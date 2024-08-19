package net.silentchaos512.gear.core.component;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record TraitAddedEnchantments(
        List<Holder<Enchantment>> enchantments // FIXME: map of trait data resource to enchantments?
) {
    public static final Codec<TraitAddedEnchantments> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.list(BuiltInRegistries.ENCHANTMENT.holderByNameCodec()).fieldOf("enchantments").forGetter(d -> d.enchantments)
            ).apply(instance, TraitAddedEnchantments::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, TraitAddedEnchantments> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.holderRegistry(Registries.ENCHANTMENT).apply(ByteBufCodecs.list()), d -> d.enchantments,
            TraitAddedEnchantments::new
    );

    public TraitAddedEnchantments(List<Holder<Enchantment>> enchantments) {
        this.enchantments = ImmutableList.copyOf(enchantments);
    }

    public static TraitAddedEnchantments empty() {
        return new TraitAddedEnchantments(Collections.emptyList());
    }

    public static class Mutable {
        private final List<Holder<Enchantment>> list = new ArrayList<>();

        public Mutable(TraitAddedEnchantments original) {
            this.list.addAll(original.enchantments);
        }

        public TraitAddedEnchantments toImmutable() {
            return new TraitAddedEnchantments(this.list);
        }
    }
}
